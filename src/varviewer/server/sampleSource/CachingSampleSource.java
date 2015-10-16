package varviewer.server.sampleSource;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

import varviewer.server.variant.VariantCollection;
import varviewer.shared.HasVariants;
import varviewer.shared.SampleInfo;
import varviewer.shared.SampleTreeNode;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * A VariantServer that just wraps the most recently requested variants in memory
 * for faster response times. 
 * @author brendan
 *
 */
public class CachingSampleSource implements SampleSource {
	
	private LoadingCache<String, CachedSample> cache;
	
	private final int msPerHour = 3600000;
	private SampleSource source;
	private SampleTreeNode sampleRoot = null;
	private Date lastRootUpdate = null;
	private Timer sampleUpdater;
		
	private int cacheSize = 50;
	private double reloadFreqInHours = 1.0;
	
	public CachingSampleSource(SampleSource sampleSource) {
		this.source = sampleSource;
		
		cache = CacheBuilder.newBuilder()
				.maximumSize(cacheSize)
				.build( new CacheLoader<String, CachedSample>() {

					@Override
					public CachedSample load(String path) throws Exception {
						
						// source.initialize(); //Previous implementation had this, but its really expensive and maybe doesn't do anything
						SampleInfo info = source.getInfoForSample(path);
						if (info == null) {
							throw new Exception("Invalid sample path: " + path);
						}
						VariantCollection vars = source.getVariantsForSample(info);
						
						CachedSample cs = new CachedSample(info,vars);
						return cs;
					}
					
				});
		
		//This timer fires periodicially (and in the background) to update the sample tree (the list of all samples)
		//Previous behavior was to do it on every load, but with a lot of samples that gets too slow
		//So now we do it in the background once an hour or so
		sampleUpdater = new Timer();
		sampleUpdater.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				updateSampleTree();
			}
			
		}, 100, (long)Math.round(reloadFreqInHours*msPerHour));
	}
	
	@Override
	public VariantCollection getVariantsForSample(SampleInfo info) {
		CachedSample sample;
		try {
			Logger.getLogger(getClass()).info("Getting sample: " + info.getAbsolutePath() + " Cache size " + cache.size() );
			sample = cache.get(info.getAbsolutePath());
			return sample.vars;
		} catch (ExecutionException e) {
			Logger.getLogger(getClass()).error("Error retrieving " + info.getAbsolutePath() + ": " + e.getMessage() );
			e.printStackTrace();
		}
		
		return null;
	}

	public int getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	public double getReloadFreqInHours() {
		return reloadFreqInHours;
	}

	public void setReloadFreqInHours(double reloadFreqInHours) {
		this.reloadFreqInHours = reloadFreqInHours;
	}

	@Override
	public HasVariants getHasVariantsForSample(SampleInfo info) {
		return getVariantsForSample(info);
	}

	@Override
	public File getBAMFileForSample(SampleInfo info) {
		return source.getBAMFileForSample(info);
	}
	
	@Override
	public void initialize() throws IOException {
		source.initialize();
	}


	@Override
	public boolean containsSample(SampleInfo info) {
		return source.containsSample(info);
	}


	@Override
	public List<SampleInfo> getAllSamples() {
		return source.getAllSamples();
	}


	@Override
	public SampleTreeNode getSampleTreeRoot() {
		//Check to see if we need to update the sample tree
		if (needUpdateForSampleTree()) {
			updateSampleTree();
		}
		return sampleRoot; 
	}
	
	private boolean needUpdateForSampleTree() {
		if (sampleRoot == null) {
			return true;
		}
		if (lastRootUpdate == null) {
			return true;
		}
		
		
		if ( (lastRootUpdate.getTime() - (new Date()).getTime()) > 1*msPerHour) {
			return true;
		}
		
		return false;
		
	}
	
	private void updateSampleTree() {
		Logger.getLogger(CachingSampleSource.class).info("Updating sample tree root");
		try {
			source.initialize();
			this.sampleRoot = source.getSampleTreeRoot();
			this.lastRootUpdate = new Date();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public SampleInfo getInfoForSample(String path) {
		return source.getInfoForSample(path);
	}


	
	class CachedSample {
		final SampleInfo info;
		final VariantCollection vars;
		
		public CachedSample(SampleInfo info, VariantCollection vars) {
			this.info = info;
			this.vars = vars;
		}
	}

	

}
