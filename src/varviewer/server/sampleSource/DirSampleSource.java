package varviewer.server.sampleSource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import varviewer.server.VVProps;
import varviewer.server.variant.VariantCollection;
import varviewer.server.variant.VariantReader;
import varviewer.shared.HasVariants;
import varviewer.shared.SampleInfo;
import varviewer.shared.SampleTreeNode;

/**
 * A SampleSource that reads its samples from a single directory. 
 * @author brendan
 *
 */
public class DirSampleSource implements SampleSource {

	private File rootDir = null;
	private Map<String, SampleInfoFile> samples = new HashMap<String, SampleInfoFile>();
	private SampleTreeNode root = null; //null until initialized
	private VariantReader variantReader = null;
	
	private SampleInfoParser infoParser = new DefaultSampleInfoParser();
	
	public DirSampleSource() {
		String rootPath = VVProps.getProperty("sample.dir");
		if (rootPath != null)
			rootDir = new File(rootPath);
	
		initialize();
	}
	
	public void setInfoParser(SampleInfoParser parser) {
		this.infoParser = parser;
	}
	
	public VariantReader getVariantReader() {
		return variantReader;
	}


	public void setVariantReader(VariantReader variantReader) {
		this.variantReader = variantReader;
	}



	public File getRootDir() {
		return rootDir;
	}

	public void setRootDir(File rootDir) {
		this.rootDir = rootDir;
		initialize();
	}


	
	public void initialize() {
		if (rootDir == null) {
			throw new IllegalArgumentException("Sample info directory has not been initialized");
		}
		
		Logger.getLogger(getClass()).info("Initializing sample info directory from path: " + rootDir.getAbsolutePath());
		samples.clear();
		root = new SampleTreeNode("root", new ArrayList<SampleTreeNode>());
		attachChildSamples(root, rootDir);
	}

	/**
	 * Recursive function to scan directory structure and attempt to parse 
	 * @param parentNode
	 * @param parentDir
	 */
	private void attachChildSamples(SampleTreeNode parentNode, File parentDir) {
		File[] subdirs = parentDir.listFiles();
		for(int i=0; i<subdirs.length; i++) {
			if (subdirs[i].isDirectory()) {
				SampleInfoFile sampleInfo = createInfoForFile(subdirs[i]);

				if (sampleInfo != null) {
					if (sampleInfo.info != prohibitedInfo) {
						samples.put(sampleInfo.info.getSampleID(), sampleInfo);
						SampleTreeNode sampleNode = new SampleTreeNode(sampleInfo.info);
						parentNode.addChild(sampleNode);
					}
				}
				else {
					//No sample manifest or info in this directory, so assume that it contains more directories to list
					 
					if (subdirs[i].listFiles().length>0) {
						SampleTreeNode dirNode = new SampleTreeNode(subdirs[i].getName(), new ArrayList<SampleTreeNode>());
						attachChildSamples(dirNode, subdirs[i]);
						//Don't include empty directories
						if (dirNode.getChildren().size()>0)
							parentNode.addChild(dirNode);
					}
				}
			}
			else {
				//this file is not a directory, so ignore it
			}
		}
	}
	
	/**
	 * Attempt to parse a SampleInfoFile from the given directory. 
	 * @param file
	 * @return
	 */
	private SampleInfoFile createInfoForFile(File file) {
		try {
			SampleInfo info = infoParser.getInfoForURL(file.getAbsolutePath());
			if (info != null) {
				SampleInfoFile infoFile = new SampleInfoFile();
				infoFile.info = info;
				infoFile.source = file;
				return infoFile;
			}
			return null;
		} catch (SampleParseException e) {
			Logger.getLogger(getClass()).warn("Parsing error reading sample directory: " + file.getAbsolutePath());
		}
		
		return null;
	}

	
	
	@Override
	public boolean containsSample(String sampleID) {
		return samples.containsKey(sampleID);
	}

	@Override
	public List<SampleInfo> getAllSamples() {
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

	public File getBAMFileForSample(String sampleID) {
		if (! containsSample(sampleID)) {
			throw new IllegalArgumentException("No sample with sampleID " + sampleID);
		}
		
		
		SampleInfo info = samples.get(sampleID).info;
		File sampleDir = samples.get(sampleID).source;
		String bamPath = info.getBamFile();
		File bamFile = null;
		if (bamPath.startsWith("/")) {
			bamFile = new File(bamPath);
		}
		else {
			bamFile = new File(sampleDir + "/" + bamPath);
		}
		
		return bamFile;
	}
	
	@Override
	public VariantCollection getVariantsForSample(String sampleID) {
		if ( containsSample(sampleID)) {
			File sampleDir = samples.get(sampleID).source;
			SampleInfo info = samples.get(sampleID).info;
			String varsPath =  info.getAnnotatedVarsFile();
			if (varsPath == null || varsPath.length()==0) {
				return null;
			}
			
			File varsFile = null;
			if (varsPath.startsWith("/"))
				varsFile = new File(varsPath);
			else 
				varsFile = new File(sampleDir + "/" + varsPath);
			
			if (!varsFile.exists() || (! varsFile.isFile())) {
				Logger.getLogger(getClass()).warn("For sample " + sampleID + " annotated vars file " + varsFile.getAbsolutePath() + " does not exist" );
				return null;
			}
			
			try {
				if (variantReader == null) {
					throw new IllegalStateException("No variant reader specified, cannot load variants");
				}
				
				variantReader.setSource(varsFile.getAbsolutePath());
				
				return variantReader.toVariantCollection();
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
	
	@Override
	public SampleTreeNode getSampleTreeRoot() {
		return root;
	}
	
	@Override
	public HasVariants getHasVariantsForSample(String sampleID) {
		return getVariantsForSample(sampleID);
	}
	
	/**
	 * Small wrapper for sample info and file path, used in this class only
	 * @author brendan
	 *
	 */
	static class SampleInfoFile {
		File source;
		SampleInfo info;
	}

	static final SampleInfo prohibitedInfo = new SampleInfo();
}
