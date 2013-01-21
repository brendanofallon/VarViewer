package varviewer.server;

import java.io.File;
import java.util.List;

import varviewer.client.services.VarRequestService;
import varviewer.shared.Variant;
import varviewer.shared.VariantRequest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the variant request service. All requests are currently honored
 * by a 'SimpleVariantServer' which is used for testing
 * @author brendan
 *
 */
public class VarRequestServiceImpl extends RemoteServiceServlet implements VarRequestService {

	AbstractVariantServer variantSource = null;

	@Override
	public List<Variant> queryVariant(VariantRequest req)
			throws IllegalArgumentException {

		//If variantSource has not been initialized, try to create one by reading "sample.dir" from
		//the properties and creating a 'DirSampleSource' to provide a list of samples
		if (variantSource == null) {
			String sampleDir = VVProps.getProperty("sample.dir");
			if (sampleDir == null) {
				throw new IllegalStateException("No sample dir specified, cannot retrieve variants");
			}
			File sampleDirFile = new File(sampleDir);
			if (! sampleDirFile.exists()) {
				throw new IllegalStateException("Sample directory " + sampleDirFile.getAbsolutePath() + " does not exist, cannot read variants");
			}
			
			DirSampleSource samplesSource = new DirSampleSource();
			samplesSource.initialize(new File(sampleDir));
			variantSource = new CachingVariantServer(samplesSource);
		}
		
		return variantSource.getVariants(req);
	}

}
