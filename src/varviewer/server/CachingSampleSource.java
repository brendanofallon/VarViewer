package varviewer.server;

import java.io.IOException;
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

	
	//TODO : This should be able to store a handful of samples ... maybe we can cache 3-5 samples? 
	private String currentSampleID = null; //
	private VariantCollection currentVariants = null;
	private SampleSource source;
	
	public CachingSampleSource(SampleSource sampleSource) {
		this.source = sampleSource;
	}
	
	@Override
	public VariantCollection getVariantsForSample(String sampleID) {
		if (currentVariants == null || (!currentSampleID.equals(sampleID))) {
			Logger.getLogger(CachingSampleSource.class).info("No variant list currently loaded, reading new list from file");
			loadVariants(sampleID);
		}
		
		if (currentVariants == null) {
			Logger.getLogger(CachingSampleSource.class).warn("Could not find or load variants for sample " + sampleID);
		}
		return currentVariants;
	}
	
	private void loadVariants(String id) {
		try {
			//Force re-loading of sample info
			source.initialize();
		} catch (IOException e) {
			Logger.getLogger(CachingSampleSource.class).warn("IOError re-loading variants: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		currentVariants = source.getVariantsForSample(id);
		currentSampleID = id;
		return;
	}
	
//	public List<Variant> getVariants(VariantRequest req) {
//		StringBuilder msg = new StringBuilder();
//		for(String id : req.getSampleIDs()) {
//			msg.append(id + ", ");
//		}
//		
//		Logger.getLogger(CachingSampleSource.class).info("Processing request for variants for sample(s) " + msg);
//		
//		//No variants loaded, automatically attempt to load
//		if (currentVariants == null) {
//			Logger.getLogger(CachingSampleSource.class).info("No variant list currently loaded, reading new list from file");
//			loadVariants(req.getSampleIDs());
//		}
//		
//		//Check to see if current sample ID matches the loaded sample, if so filter and return the variants 
//		if (currentSampleID != null && req.getSampleIDs().contains(currentSampleID)) {
//			Logger.getLogger(CachingSampleSource.class).info("Returning variants for sample(s): " + msg);
//			List<Variant> vars = currentVariants.getVariantsInIntervals(req.getIntervals());
//			if (req.getFilters() != null && req.getFilters().size()>0) {
//				vars = applyFilters(vars, req.getFilters());
//			}
//			return vars;
//		}
//		
//		//Current variants is not null but sample IDs don't match, so try to load new variants
//		loadVariants(req.getSampleIDs());
//		
//		//if variant load failed (bad sample id) then current variants may still be null
//		if (currentVariants != null) {
//			Logger.getLogger(CachingSampleSource.class).info("Returning variants for sample(s): " + msg);
//			List<Variant> vars = currentVariants.getVariantsInIntervals(req.getIntervals());
//			if (req.getFilters() != null && req.getFilters().size()>0) {
//				vars = applyFilters(vars, req.getFilters());
//			}
//			return vars;
//		}
//		
//		
//		Logger.getLogger(CachingSampleSource.class).warn("Could not find or load variants for sample(s): " + msg);
//		return null;		
//	}


	
	/**
	 * Returns a new list of variants containing only those variants that pass ALL 
	 * filters in the list
	 * @param vars
	 * @param filters
	 * @return
	 */
//	private List<Variant> applyFilters(List<Variant> vars, List<VariantFilter> filters) {
//		List<Variant> passingVars = new ArrayList<Variant>(1024);
//		
//		//Kind of a hack here... pedigree-based filters need to be 'initialized' with a SampleSource
//		//before they work, right now we do this here. 
//		
//		for(VariantFilter filter : filters) {
//			if (filter instanceof PedigreeFilter) {
//				PedigreeFilter pedFilter = (PedigreeFilter)filter;
//				pedFilter.setVariantSource(source);
//			}
//		}
//		
//		for(Variant var : vars) {
//			boolean passes = true;
//			for(VariantFilter filter : filters) {
//				if (! filter.variantPasses(var)) {
//					passes = false;
//					break;
//				}
//			}
//			if (passes)
//				passingVars.add(var);
//		}
//		
//		//Similar hack here, PedigreeFilters also apply an annotation, but they need to be told
//		//to do so to a given list of variants. We do this here so they don't waste time annotating
//		//variants that will be filtered out, but this functionality should be encapsulated somewhere
//		//else at some point
//		for(VariantFilter filter : filters) {
//			if (filter instanceof PedigreeFilter) {
//				PedigreeFilter pedFilter = (PedigreeFilter)filter;
//				pedFilter.applyAnnotations(passingVars);
//			}
//		}
//		
//		Logger.getLogger(CachingSampleSource.class).info(passingVars.size() + " of " + vars.size() + " total vars passed filters");
//		return passingVars;
//	}


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


	

}
