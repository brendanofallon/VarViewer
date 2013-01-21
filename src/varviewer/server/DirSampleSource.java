package varviewer.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import varviewer.server.variant.AnnotatedCSVReader;
import varviewer.server.variant.VariantCollection;
import varviewer.shared.SampleInfo;
import varviewer.shared.Variant;

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
			throw new IllegalArgumentException("Sample info directory " + rootDir.getAbsolutePath() + " does not exist");
		}
		if (! rootDir.isDirectory()) {
			throw new IllegalArgumentException("Sample info directory " + rootDir.getAbsolutePath() + " is not a directory");
		}
		initialize();
	}
	
	public void initialize() {
		if (rootDir == null) {
			throw new IllegalArgumentException("Sample info directory has not been initialized");
		}
		
		File[] subdirs = rootDir.listFiles();
		for(int i=0; i<subdirs.length; i++) {
			if (subdirs[i].isDirectory()) {
				SampleInfoFile sampleInfo = createInfoForFile(subdirs[i]);
				if (sampleInfo != null) {
					samples.put(sampleInfo.info.getSampleID(), sampleInfo);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		
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
			if (key.equals("current.time")) {
				String dateStr = pairs.get(key);
				try {
					Date analysisDate = dateFormatter.parse(dateStr);
					info.setAnalysisDate(analysisDate);
				}
				catch(Exception ex) {
					
				}
			}
		}
		
		//Now attempt to find vcf, annotated csv, and .bam files...
		File vcfFile = findVCF( new File(subFile.getAbsolutePath() + "/var/") );
		if (vcfFile != null)
			info.setVcfFile(vcfFile.getAbsolutePath());
		
		File csvFile = findCSV( new File(subFile.getAbsolutePath() + "/var/") );
		if (csvFile != null)
			info.setAnnotatedVarsFile(csvFile.getAbsolutePath());
		
		File bamFile = findBAM( new File(subFile.getAbsolutePath() + "/bam/") );
		if (bamFile != null) {
			info.setBamFile(bamFile.getAbsolutePath());
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
			SampleInfo info = samples.get(sampleID).info;
			String annoVarsPath =  info.getAnnotatedVarsFile();
			if (annoVarsPath == null || annoVarsPath.length()==0) {
				return null;
			}
			
			File varsFile = new File(annoVarsPath);
			if (!varsFile.exists()) {
				return null;
			}
			
			AnnotatedCSVReader reader = new AnnotatedCSVReader(annoVarsPath);
			try {
				return reader.toVariantCollection();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
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
	
	public static void main(String[] args) {
		System.out.println(new Date());
	}
}
