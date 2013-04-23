package varviewer.server.sampleSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import varviewer.shared.SampleInfo;

/**
 * Stateless, examines a 'reviewdir' structure of directories and files and attempts to build a sample
 * info object and return it.
 * 
 * @author brendan
 *
 */
public class DefaultSampleInfoParser implements SampleInfoParser {

	public static final String SAMPLE_MANIFEST_FILENAME = "sampleManifest.txt";
	
	@Override
	public SampleInfo getInfoForURL(String path) throws SampleParseException {
		File dirFile = new File(path);
		if (! dirFile.exists()) {
			throw new SampleParseException("Sample directory " + path + " does not exist");
		}
		if (! dirFile.canRead()) {
			throw new SampleParseException("Sample directory " + path + " exists, cannot be read");
		}
		
		//Look for a file named sampleManifest.txt
		File[] subfiles = dirFile.listFiles();
		for(int i=0; i<subfiles.length; i++) {
			File subFile = subfiles[i];
			if (subFile.getName().equals(SAMPLE_MANIFEST_FILENAME)) {
				Map<String, String> manifestProperties = parseManifest(subFile);
				SampleInfo info = parseSampleInfo(dirFile, manifestProperties);
				return info;	
			}
		}
		
		Logger.getLogger(getClass()).warn("No sample manifest file found in directory : " + path);
		return null;
	}

	protected Map<String, String> parseManifest(File manifestFile) {
		Map<String, String> properties = new HashMap<String, String>();
		
		try {
			BufferedReader reader= new BufferedReader(new FileReader(manifestFile));
			String line = reader.readLine();
			while(line != null) {
				String[] toks = line.split("=");
				if (toks.length==2) {
					properties.put(toks[0], toks[1]);
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			Logger.getLogger(getClass()).warn("IO error reading sample information from file: " + manifestFile.getAbsolutePath() + " cause: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return properties;
	}
	
	protected SampleInfo parseSampleInfo(File sampleRoot, Map<String, String> properties) {

		SampleInfo info = new SampleInfo();
		
		for(String key: properties.keySet()) {
			if (key.equals("sample.name")) {
				info.setSampleID(properties.get(key));
			}
			if (key.equals("analysis.type")) {
				info.setAnalysisType(properties.get(key));
			}
			if (key.equals("submitter")) {
				info.setSubmitter(properties.get(key));
			}
			if (key.equals("annotated.vars")) {
				info.setAnnotatedVarsFile(properties.get(key));
			}
			if (key.equals("bam.file")) {
				info.setBamFile(properties.get(key));
			}
			if (key.equals("bam.link")) {
				info.setBamLink(properties.get(key));
			}
			if (key.equals("vcf.link")) {
				info.setVcfLink(properties.get(key));
			}
			if (key.equals("qc.link")) {
				info.setQCLink(properties.get(key));
			}
			if (key.equals("current.time")) {
				String dateStr = properties.get(key);
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
		
		//Now attempt to find vcf, annotated csv, and .bam files...
		File vcfFile = findVCF( new File(sampleRoot.getAbsolutePath() + "/var/") );
		if (vcfFile != null) {
			info.setVcfFile(vcfFile.getAbsolutePath());
		}
		else {
			Logger.getLogger(getClass()).warn("No VCF file for sample " + info.getSampleID() );
		}
		
		File csvFile = findCSV( new File(sampleRoot.getAbsolutePath() + "/var/") );
		if (csvFile != null) {
			info.setAnnotatedVarsFile(csvFile.getAbsolutePath());
		}
		else {
			Logger.getLogger(getClass()).warn("No annotated csv file for sample " + info.getSampleID() );
		}
		
		File bamFile = findBAM( new File(sampleRoot.getAbsolutePath() + "/bam/") );
		if (bamFile != null) {
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

}
