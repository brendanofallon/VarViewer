package varviewer.server;

import java.io.File;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import varviewer.client.services.SampleListService;
import varviewer.shared.SampleInfo;

/**
 * Grabs the list of available samples and returns them in a list of SampleInfos
 * @author brendanofallon
 *
 */
public class SampleListServiceImpl extends RemoteServiceServlet implements SampleListService {

	DirSampleSource sampleDir = null;
	@Override
	public List<SampleInfo> getSampleList() {
		if (sampleDir == null) {
			String sampleDirPath = VVProps.getProperty("sample.dir");
			if (sampleDirPath == null)
				throw new IllegalStateException("No sample dir specified, put sample.dir into properties file");
			File dirFile = new File(sampleDirPath);
			if (! dirFile.exists())
				throw new IllegalStateException("Sample directory " + dirFile.getAbsolutePath() + " does not exist");
			sampleDir = new DirSampleSource();
			sampleDir.initialize( dirFile );
		}
		
		
		sampleDir.initialize();
		return sampleDir.getSampleInfos();
	}

}
