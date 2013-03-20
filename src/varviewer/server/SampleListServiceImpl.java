package varviewer.server;

import java.io.File;

import org.apache.log4j.Logger;

import varviewer.client.services.SampleListService;
import varviewer.shared.SampleListResult;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Grabs the list of available samples and returns them in a list of SampleInfos
 * @author brendanofallon
 *
 */
public class SampleListServiceImpl extends RemoteServiceServlet implements SampleListService {

	DirSampleSource sampleDir = null;
	
	@Override
	public SampleListResult getSampleList() {
		if (sampleDir == null) {
			String sampleDirPath = VVProps.getProperty("sample.dir");
			if (sampleDirPath == null) {
				Logger.getLogger(getClass()).error("Sample dir path not specified in properties file");
				throw new IllegalStateException("No sample dir specified, put sample.dir into properties file");
			}
			File dirFile = new File(sampleDirPath);
			if (! dirFile.exists()) {
				Logger.getLogger(getClass()).error("Sample dir path " + sampleDirPath + " does not exist");
				throw new IllegalStateException("Sample directory " + dirFile.getAbsolutePath() + " does not exist");
			}
			sampleDir = new DirSampleSource();
			Logger.getLogger(getClass()).info("Initializing sample source directory on path: " + dirFile.getAbsolutePath());
			sampleDir.setRootDir(dirFile);
		}
		
		//re-initialize every time
		sampleDir.initialize();
		return new SampleListResult(sampleDir.getSampleTreeRoot());
	}
	

}
