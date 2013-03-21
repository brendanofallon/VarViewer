package varviewer.server;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import varviewer.server.variant.VariantCollection;
import varviewer.shared.HasVariants;
import varviewer.shared.SampleInfo;
import varviewer.shared.SampleTreeNode;

/**
 * A VariantServer that maintains the most recently requested variants in memory
 * for faster response times. 
 * @author brendan
 *
 */
public class CachingSampleSource implements SampleSource {

	int samplesToCache = 4;
	private SampleSource source;
	
	private List<CachedSample> cache = new LinkedList<CachedSample>();
	
	public CachingSampleSource(SampleSource sampleSource) {
		this.source = sampleSource;
	}
	
	@Override
	public VariantCollection getVariantsForSample(String sampleID) {
		CachedSample sample = getCachedSampleForID(sampleID);
		
		if (sample != null) {
			//Sweet, cache hit
			bumpToFront(sampleID);
			return sample.vars;
		}
		
		Logger.getLogger(CachingSampleSource.class).info("Cache miss for sample " + sampleID + ", loading new set of variants");
		loadVariants(sampleID);
		
		try {
			//Force re-loading of sample info
			source.initialize();
			addToCache(sampleID, source.getVariantsForSample(sampleID));
		} catch (IOException e) {
			Logger.getLogger(CachingSampleSource.class).warn("IOError re-loading variants: " + e.getMessage());
			e.printStackTrace();
		}
		
		sample = getCachedSampleForID(sampleID);
		if (sample != null) {
			return sample.vars;
		}
		else {
			return null;
		}
	}
	
	private void loadVariants(String id) {
		
		
		
	}



	@Override
	public HasVariants getHasVariantsForSample(String sampleID) {
		return source.getHasVariantsForSample(sampleID);
	}


	@Override
	public void initialize() throws IOException {
		source.initialize();
	}


	@Override
	public boolean containsSample(String sampleID) {
		return source.containsSample(sampleID);
	}


	@Override
	public List<SampleInfo> getAllSamples() {
		return source.getAllSamples();
	}


	@Override
	public SampleTreeNode getSampleTreeRoot() {
		return source.getSampleTreeRoot();
	}


	@Override
	public SampleInfo getInfoForSample(String sampleID) {
		return source.getInfoForSample(sampleID);
	}

	/**
	 * Returns true if a sample with the given id is current in the cache
	 * @param sampleID
	 * @return
	 */
	private boolean isInCache(String sampleID) {
		return getCachedSampleForID(sampleID) != null;
	}
	
	private CachedSample getCachedSampleForID(String id) {
		for(CachedSample sample : cache) {
			if (sample.sampleID.equals(id))
				return sample;
		}
		return null;
	}
	
	/**
	 * 
	 * @param sampleID
	 */
	private void bumpToFront(String sampleID) {
		CachedSample cs = getCachedSampleForID(sampleID);
		if (cs != null) {
			cache.remove(cs);
			cache.add(cs);
		}
	}
	
	private void addToCache(String sampleID, VariantCollection vars) {
		CachedSample cs = new CachedSample(sampleID, vars);
		cache.add(cs);
		if (cache.size() > samplesToCache) {
			CachedSample removed = cache.remove(0);
			Logger.getLogger(CachingSampleSource.class).info("Added sample " + sampleID + " to cache, bumped " + removed.sampleID + " from cache since it was full");
		}
	}
	
	class CachedSample {
		final String sampleID;
		final VariantCollection vars;
		
		public CachedSample(String id, VariantCollection vars) {
			this.sampleID = id;
			this.vars = vars;
		}
	}

}
