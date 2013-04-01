package varviewer.server.variant;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import varviewer.server.CachingSampleSource;
import varviewer.server.DirSampleSource;
import varviewer.server.FilterExecutor;
import varviewer.server.SampleSource;
import varviewer.server.SimpleFilterExecutor;
import varviewer.server.VVProps;
import varviewer.server.VariantRequestHandler;
import varviewer.server.annotation.AnnotationKeyIndex;
import varviewer.server.annotation.AnnotationProvider;
import varviewer.server.annotation.TestAnnotationProvider;
import varviewer.shared.variant.Variant;
import varviewer.shared.variant.VariantRequest;
import varviewer.shared.variant.VariantRequestResult;

public class TestVarRequestHandler implements VariantRequestHandler {

	AnnotationProvider annoSource = null;
	SampleSource variantSource = null;
	
	@Override
	public VariantRequestResult queryVariant(VariantRequest req) {
		Date begin = new Date();
		
		//If variantSource has not been initialized, try to create one by reading "sample.dir" from
		//the properties and creating a 'DirSampleSource' to provide a list of samples
		if (variantSource == null) {
			String sampleDir = VVProps.getProperty("sample.dir");
			if (sampleDir == null) {
				Logger.getLogger(getClass()).fatal("Could not read sample.dir from properties file, the object is null");
				throw new IllegalStateException("No sample dir specified, cannot retrieve variants");
			}
			File sampleDirFile = new File(sampleDir);
			if (! sampleDirFile.exists()) {
				Logger.getLogger(getClass()).fatal("Could not read sample.dir from properties file, the file " + sampleDir + " does not exist");
				throw new IllegalStateException("Sample directory " + sampleDirFile.getAbsolutePath() + " does not exist, cannot read variants");
			}
			
			
			DirSampleSource samplesSource = new DirSampleSource();
			samplesSource.setRootDir(new File(sampleDir));
			Logger.getLogger(getClass()).info("Initializing new sampleSource from file " + sampleDir);
			variantSource = new CachingSampleSource(samplesSource); //Caches variants
		}
		
		Date init = new Date();
		System.out.println("Time to initialize: " + (init.getTime()-begin.getTime())/1000.0);
		
		if (annoSource == null) {
			annoSource = new TestAnnotationProvider();
		}
		

		//First: Obtain the "raw" list of variants, unfiltered and (mostly) unannotated
		VariantCollection vars = variantSource.getVariantsForSample(req.getSampleIDs().get(0));

		Date readVars = new Date();
		System.out.println("Time to read: " + (readVars.getTime()-init.getTime())/1000.0);
		
		//Second: Annotate the variants according to the annotations requested
		if (req.getAnnotations().size() > 0) {
			AnnotationKeyIndex[] index = annoSource.getKeyIndices(req.getAnnotations());
			for(String contig : vars.getContigs()) {
				for(Variant var : vars.getVariantsForContig(contig)) {
					annoSource.annotateVariant(var, index);
				}
			}
		}
	
		Date annotate = new Date();
		System.out.println("Time to annotate: " + (annotate.getTime()-readVars.getTime())/1000.0);
		
		//Third : Apply filters to the variants and return only those passing all filters
		FilterExecutor filterExec = new SimpleFilterExecutor();
		List<Variant> passingVars = filterExec.filterAll(vars, req.getFilters());
		
		Date filter = new Date();
		System.out.println("Time to filter: " + (filter.getTime()-annotate.getTime())/1000.0);
		
		VariantRequestResult result = new VariantRequestResult();
		result.setSampleID(req.getSampleIDs().get(0));
		result.setVars(passingVars);
		return result;
	}

	public AnnotationProvider getAnnoSource() {
		return annoSource;
	}

	public void setAnnoSource(AnnotationProvider annoSource) {
		this.annoSource = annoSource;
	}

	public SampleSource getVariantSource() {
		return variantSource;
	}

	public void setVariantSource(SampleSource variantSource) {
		this.variantSource = variantSource;
	}
	
	

}
