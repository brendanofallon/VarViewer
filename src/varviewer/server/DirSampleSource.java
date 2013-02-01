package varviewer.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import varviewer.server.variant.AbstractVariantReader;
import varviewer.server.variant.TabixCSVReader;
import varviewer.server.variant.UncompressedCSVReader;
import varviewer.server.variant.VariantCollection;
import varviewer.shared.SampleInfo;

/**
 * A SampleSource that reads its samples from a single directory. 
 * @author brendan
 *
 */
public class DirSampleSource implements SampleSource {

	public static final String SAMPLE_MANIFEST_FILENAME = "sampleManifest.txt";
	
	private File rootDir = null;
	private Map<String, SampleInfoFile> samples = new HashMap<String, SampleInfoFile>();
	
	public DirSampleSource() {
		String rootPath = VVProps.getProperty("sample.dir");
		if (rootPath != null)
			rootDir = new File(rootPath);
	
		initialize();
	}
	
	/**
	 * Set the root directory to the given file and read all samples from it. 
	 * @param rootDir
	 */
	public void initialize(File rootDir) {
		this.rootDir = rootDir;
		if (! rootDir.exists()) {
			Logger.getLogger(getClass()).error("Sample info directory "  + rootDir.getAbsolutePath() + " does not exist");
			throw new IllegalArgumentException("Sample info directory " + rootDir.getAbsolutePath() + " does not exist");
		}
		if (! rootDir.isDirectory()) {
			Logger.getLogger(getClass()).error("Sample info directory "  + rootDir.getAbsolutePath() + " is not a directory");
			throw new IllegalArgumentException("Sample info directory " + rootDir.getAbsolutePath() + " is not a directory");
		}
		initialize();
	}
	
	public void initialize() {
		if (rootDir == null) {
			throw new IllegalArgumentException("Sample info directory has not been initialized");
		}
		
		Logger.getLogger(getClass()).info("Initializing sample info directory from path: " + rootDir.getAbsolutePath());
		samples.clear();
		File[] subdirs = rootDir.listFiles();
		for(int i=0; i<subdirs.length; i++) {
			if (subdirs[i].isDirectory()) {
				SampleInfoFile sampleInfo = createInfoForFile(subdirs[i]);
				if (sampleInfo != null) {
					Logger.getLogger(getClass()).info("Loading sample info from file " + subdirs[i].getAbsolutePath() + ", found sample id: " + sampleInfo.info.getSampleID());
					samples.put(sampleInfo.info.getSampleID(), sampleInfo);
				}
				else {
					Logger.getLogger(getClass()).warn("Could not parse sample info from directory " + subdirs[i].getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Attempt to parse a SampleInfoFile from the given directory. 
	 * @param file
	 * @return
	 */
	private SampleInfoFile createInfoForFile(File file) {
		SampleInfoFile infoFile = null;
		//Look for a file named sampleManifest.txt
		File[] subfiles = file.listFiles();
		for(int i=0; i<subfiles.length; i++) {
			File subFile = subfiles[i];
			if (subFile.getName().equals(SAMPLE_MANIFEST_FILENAME)) {
				SampleInfo info = parseSampleInfo(subFile);
				infoFile = new SampleInfoFile();
				infoFile.info = info;
				infoFile.source = file;
				return infoFile;
			}
		}
		Logger.getLogger(getClass()).warn("No sample manifest file found in directory : " + file.getAbsolutePath());
		return null;
	}

	private SampleInfo parseSampleInfo(File subFile) {
		Map<String, String> pairs = new HashMap<String, String>();
		
		try {
			BufferedReader reader= new BufferedReader(new FileReader(subFile));
			String line = reader.readLine();
			while(line != null) {
				String[] toks = line.split("=");
				if (toks.length==2) {
					pairs.put(toks[0], toks[1]);
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			Logger.getLogger(getClass()).warn("IO error reading sample informatoin from file: " + subFile.getAbsolutePath() + " cause: " + e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		}
		
				
		SampleInfo info = new SampleInfo();
		for(String key: pairs.keySet()) {
			if (key.equals("sample.name")) {
				info.setSampleID(pairs.get(key));
			}
			if (key.equals("analysis.type")) {
				info.setAnalysisType(pairs.get(key));
			}
			if (key.equals("submitter")) {
				info.setSubmitter(pairs.get(key));
			}
			if (key.equals("annotated.vars")) {
				info.setAnnotatedVarsFile(pairs.get(key));
			}
			if (key.equals("bam.file")) {
				info.setBamFile(pairs.get(key));
			}
			if (key.equals("bam.link")) {
				info.setBamLink(pairs.get(key));
			}
			if (key.equals("vcf.link")) {
				info.setVcfLink(pairs.get(key));
			}
			if (key.equals("qc.link")) {
				info.setQCLink(pairs.get(key));
			}
			if (key.equals("current.time")) {
				String dateStr = pairs.get(key);
				try {
					Long time = Long.parseLong(dateStr);
					Date analysisDate = new Date(time);
					info.setAnalysisDate(analysisDate);
				}
				catch(Exception ex) {
					//System.out.println("Could not parse date from string: " + dateStr + " reason: " + ex.getMessage());
				}
			}
			
		}
		
		File sampleRoot = subFile.getParentFile();
		//Now attempt to find vcf, annotated csv, and .bam files...
		File vcfFile = findVCF( new File(sampleRoot.getAbsolutePath() + "/var/") );
		if (vcfFile != null) {
			Logger.getLogger(getClass()).info("Found VCF file for sample " + info.getSampleID() + ": " + vcfFile.getAbsolutePath());
			info.setVcfFile(vcfFile.getAbsolutePath());
		}
		else {
			Logger.getLogger(getClass()).warn("No VCF file for sample " + info.getSampleID() );
		}
		
		File csvFile = findCSV( new File(sampleRoot.getAbsolutePath() + "/var/") );
		if (csvFile != null) {
			Logger.getLogger(getClass()).info("Found annotated vars file for sample " + info.getSampleID() + ": " + csvFile.getAbsolutePath());
			info.setAnnotatedVarsFile(csvFile.getAbsolutePath());
		}
		else {
			Logger.getLogger(getClass()).warn("No annotated csv file for sample " + info.getSampleID() );
		}
		
		File bamFile = findBAM( new File(sampleRoot.getAbsolutePath() + "/bam/") );
		if (bamFile != null) {
			Logger.getLogger(getClass()).info("Found BAM file for sample " + info.getSampleID() + ": " + bamFile.getAbsolutePath());
			info.setBamFile(bamFile.getAbsolutePath());
		}
		else {
			Logger.getLogger(getClass()).warn("No BAM file for sample " + info.getSampleID() );
		}

		
		return info;
	}

	/**
	 * Return the File re
	 * @param file
	 * @return
	 */
	private File findVCF(File file) {
		if (file.exists() && file.isDirectory()) {
			File[] subfiles = file.listFiles();
			for(int i=0; i<subfiles.length; i++) {
				File subfile = subfiles[i];
				if (subfile.getName().endsWith(".vcf") || subfile.getName().endsWith(".vcf.gz")) {
					return subfile;
				}
			}
		}
		else {
			Logger.getLogger(getClass()).warn("VCF base directory " + file.getAbsolutePath() + " does not exist, cannot read vcf files from this path");
		}
		return null;
	}

	private File findCSV(File file) {
		if (file.exists() && file.isDirectory()) {
			File[] subfiles = file.listFiles();
			for(int i=0; i<subfiles.length; i++) {
				File subfile = subfiles[i];
				if (subfile.getName().endsWith(".csv") || subfile.getName().endsWith(".csv.gz")) {
					return subfile;
				}
			}
		}
		else {
			Logger.getLogger(getClass()).warn("CSV base directory " + file.getAbsolutePath() + " does not exist, cannot read csv files from this path");
		}
		return null;
	}
	
	private File findBAM(File file) {
		if (file.exists() && file.isDirectory()) {
			File[] subfiles = file.listFiles();
			for(int i=0; i<subfiles.length; i++) {
				File subfile = subfiles[i];
				if (subfile.getName().endsWith(".bam")) {
					return subfile;
				}
			}
		}
		else {
			Logger.getLogger(getClass()).warn("BAM base directory " + file.getAbsolutePath() + " does not exist, cannot read BAM files from this path");
		}
		return null;
	}
	
	@Override
	public boolean containsSample(String sampleID) {
		return samples.containsKey(sampleID);
	}

	@Override
	public List<SampleInfo> getSampleInfos() {
		List<SampleInfo> infos = new ArrayList<SampleInfo>();
		for(String id : samples.keySet()) {
			SampleInfoFile infoFile = samples.get(id);
			infos.add(infoFile.info);
		}
		return infos;
	}

	@Override
	public SampleInfo getInfoForSample(String sampleID) {
		SampleInfoFile infoFile = samples.get(sampleID);
		if (infoFile == null)
			return null;
		else 
			return infoFile.info;
	}

	@Override
	public VariantCollection getVariantsForSample(String sampleID) {
		if ( containsSample(sampleID)) {
			File sampleDir = samples.get(sampleID).source;
			SampleInfo info = samples.get(sampleID).info;
			String annoVarsPath =  info.getAnnotatedVarsFile();
			if (annoVarsPath == null || annoVarsPath.length()==0) {
				return null;
			}
			
			File varsFile = null;
			if (annoVarsPath.startsWith("/"))
				varsFile = new File(annoVarsPath);
			else 
				varsFile = new File(sampleDir + "/" + annoVarsPath);
			
			if (!varsFile.exists()) {
				Logger.getLogger(getClass()).warn("IO error reading variants for " + sampleID + " annotated vars file " + varsFile.getAbsolutePath() + " does not exist" );
				return null;
			}
			
			
			try {
				AbstractVariantReader reader = null;
				if (varsFile.getName().endsWith(".gz")) {
					reader = new TabixCSVReader(varsFile.getAbsolutePath());
				}
				else {
					reader = new UncompressedCSVReader(varsFile.getAbsolutePath());
				}
				
				return reader.toVariantCollection();
			} catch (IOException e) {
				Logger.getLogger(getClass()).warn("IO error reading variants for " + sampleID + " from " + varsFile.getAbsolutePath() + " Message: " + e.getMessage() );
				e.printStackTrace();
				return null;
			}
			
		}
		else {
			Logger.getLogger(getClass()).warn("Request for variants from sample " + sampleID + " but there's no sample with that ID" );
			StringBuilder msg = new StringBuilder();
			for(String samp: samples.keySet()) {
				msg.append(samp + ", ");
			}
			Logger.getLogger(getClass()).warn("Current sample ids are: " + msg);
		}
		return null;
	}
	
	
	/**
	 * Small wrapper for sample info and file path, used in this class only
	 * @author brendan
	 *
	 */
	private class SampleInfoFile {
		File source;
		SampleInfo info;
	}

}
