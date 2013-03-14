package varviewer.server;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import varviewer.client.services.VarRequestService;
import varviewer.server.annotation.AnnotationKeyIndex;
import varviewer.server.annotation.AnnotationProvider;
import varviewer.server.annotation.TestAnnotationProvider;
import varviewer.server.variant.VariantCollection;
import varviewer.shared.Variant;
import varviewer.shared.VariantRequest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the variant request service. The 'variantSource' is basically a caching
 * wrapper for variants from a DirSampleSource, which reads variants from a file but
 * doesn't store them in memory. 
 * @author brendan
 *
 */
public class VarRequestServiceImpl extends RemoteServiceServlet implements VarRequestService {

	SampleSource variantSource = null;
	AnnotationProvider annoSource = null;

	@Override
	public List<Variant> queryVariant(VariantRequest req)
			throws IllegalArgumentException {

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
			samplesSource.initialize(new File(sampleDir));
			Logger.getLogger(getClass()).info("Initializing new sampleSource from file " + sampleDir);
			variantSource = new CachingSampleSource(samplesSource); //Caches variants
		}
		
		if (annoSource == null) {
			annoSource = new TestAnnotationProvider();
		}

		//First: Obtain the "raw" list of variants, unfiltered and (mostly) unannotated
		VariantCollection vars = variantSource.getVariantsForSample(req.getSampleIDs().get(0));

		//Second: Annotate the variants according to the annotations requested
		if (req.getAnnotations().size() > 0) {
			AnnotationKeyIndex[] index = annoSource.getKeyIndices(req.getAnnotations());
			for(String contig : vars.getContigs()) {
				for(Variant var : vars.getVariantsForContig(contig)) {
					annoSource.annotateVariant(var, index);
				}
			}
		}
	
		//Third : Apply filters to the variants and return only those passing all filters
		FilterExecutor filterExec = new SimpleFilterExecutor();
		List<Variant> passingVars = filterExec.filterAll(vars, req.getFilters());
		
		return passingVars;
	}

}
