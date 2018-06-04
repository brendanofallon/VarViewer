package varviewer.server.sampleSource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
		//attachChildSamples(root, rootDir);
		attachChildrenConcurrently(root, rootDir);
	}
	
	private void attachChildrenConcurrently(SampleTreeNode parentNode, File parentDir)  {
		
		ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
		final File[] subdirs = parentDir.listFiles();
		for(int i=0; i<subdirs.length; i++) {

			if (subdirs[i].isDirectory()) {
				Attacher attachJob = new Attacher(parentNode, subdirs[i]);
				pool.submit(attachJob);
			}
		}
		
		pool.shutdown();
		try {
			pool.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private void attachSample(SampleTreeNode parentNode, File subdir) {
		if (subdir.isDirectory()) {
			SampleInfoFile sampleInfo = null;
			try {
				sampleInfo = createInfoForFile(subdir);
			}
			catch(Exception ex) {
				System.err.println("Error reading sample info for file : " + subdir + " " + ex.getLocalizedMessage());
				Logger.getLogger(this.getClass()).warn("Error reading sample info for file : " + subdir + " " + ex.getLocalizedMessage());
			}

			if (sampleInfo != null) {
				if (sampleInfo.info != prohibitedInfo) {
					String key = sampleInfo.info.getAbsolutePath();
					if (samples.containsKey(key)) {
						SampleInfoFile existing = samples.get(key);
						File existingFile = existing.source;
						File newConflictingFile = sampleInfo.source;
						
						//Same keys, different files, this shouldn't happen
						if (! existingFile.equals(newConflictingFile)) {
							throw new IllegalStateException("Conflicting sample keys, sample with id " + sampleInfo.info.getSampleID() + " has key " + sampleInfo.info.getAbsolutePath() + " is associated with " + samples.get(key).info.getSampleID() + " (key text: " + existing.info.getAbsolutePath() + " dir: " + existingFile + ")");
						}
					}
					
					samples.put(key, sampleInfo);
					SampleTreeNode sampleNode = new SampleTreeNode(sampleInfo.info);
					parentNode.addChild(sampleNode);
				}
			}
			else {
				//No sample manifest or info in this directory, so assume that it contains more directories to list
				 
				if (subdir.listFiles().length>0) {
					SampleTreeNode dirNode = new SampleTreeNode(subdir.getName(), new ArrayList<SampleTreeNode>());
					attachChildSamples(dirNode, subdir);
					//Don't include empty directories
					if (dirNode.getChildren().size()>0)
						parentNode.addChild(dirNode);
				}
			}
		}
	}

	
	/**
	 * Recursive function to scan directory structure and attempt to parse 
	 * @param parentNode
	 * @param parentDir
	 */
	private void attachChildSamples(SampleTreeNode parentNode, File parentDir) {
		File[] subdirs = parentDir.listFiles();
		for(int i=0; i<subdirs.length; i++) {
			attachSample(parentNode, subdirs[i]);
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
	public boolean containsSample(SampleInfo sample) {
		String sampleKey = sample.getAbsolutePath();
		for(String qKey : samples.keySet()) {
			if (qKey.equals(sampleKey)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<SampleInfo> getAllSamples() {
		List<SampleInfo> sampleInfos = new ArrayList<SampleInfo>();
		for(SampleInfoFile infoFile : samples.values()) {
			sampleInfos.add(infoFile.info);
		}
		return sampleInfos;
	}

	@Override
	public SampleInfo getInfoForSample(String path) {
		SampleInfoFile infoFile = samples.get(path);
		if (infoFile == null)
			return null;
		else 
			return infoFile.info;
	}

	public File getBAMFileForSample(SampleInfo sample) {
		if (! containsSample(sample)) {
			throw new IllegalArgumentException("No sample with sampleID " + sample.getSampleID());
		}
		
		
		SampleInfo info = samples.get(sample.getAbsolutePath()).info;
		File sampleDir = samples.get(sample.getAbsolutePath()).source;
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
	public VariantCollection getVariantsForSample(SampleInfo sample) {
		if ( containsSample(sample)) {
			File sampleDir = samples.get(sample.getAbsolutePath()).source;
			String varsPath =  sample.getAnnotatedVarsFile();
			if (varsPath == null || varsPath.length()==0) {
				return null;
			}
			
			File varsFile = null;
			if (varsPath.startsWith("/"))
				varsFile = new File(varsPath);
			else 
				varsFile = new File(sampleDir + "/" + varsPath);
			
			if (!varsFile.exists() || (! varsFile.isFile())) {
				Logger.getLogger(getClass()).warn("For sample " + sample.getSampleID() + " annotated vars file " + varsFile.getAbsolutePath() + " does not exist" );
				return null;
			}
			
			try {
				if (variantReader == null) {
					throw new IllegalStateException("No variant reader specified, cannot load variants");
				}
				
				variantReader.setSource(varsFile.getAbsolutePath());
				
				return variantReader.toVariantCollection();
			} catch (IOException e) {
				Logger.getLogger(getClass()).warn("IO error reading variants for " + sample.getSampleID() + " from " + varsFile.getAbsolutePath() + " Message: " + e.getMessage() );
				e.printStackTrace();
				return null;
			}
			
		}
		else {
			Logger.getLogger(getClass()).warn("Request for variants from sample " + sample.getSampleID() + " but there's no sample with that ID" );
			StringBuilder msg = new StringBuilder();
			for(String key : samples.keySet()) {
				msg.append(samples.get(key).info + ", ");
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
	public HasVariants getHasVariantsForSample(SampleInfo sample) {
		return getVariantsForSample(sample);
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
	
	class Attacher implements Runnable {
		File subdir;
		SampleTreeNode parent;
		
		Attacher(SampleTreeNode parent, File subdir) {
			this.subdir = subdir;
			this.parent = parent;
		}

		@Override
		public void run() {
			attachSample(parent, subdir); //recursively calls attachChildSamples
		}
		
	}

	static final SampleInfo prohibitedInfo = new SampleInfo();
}
