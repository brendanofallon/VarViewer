package varviewer.server.sampleSource;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import varviewer.client.services.SampleListService;
import varviewer.shared.SampleListResult;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Grabs the list of available samples and returns them in a list of SampleInfos
 * @author brendanofallon
 *
 */
public class SampleListServiceImpl extends RemoteServiceServlet implements SampleListService {

	SampleSource sampleDir = null;
	
	@Override
	public SampleListResult getSampleList() {
		if (sampleDir == null) {
			ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
			sampleDir = (SampleSource) context.getBean("sampleSource");
		}
		
		//re-initialize every time to check for changes
		try {
			sampleDir.initialize();
		} catch (IOException e) {
			Logger.getLogger(getClass()).error("Error initializing sample info : " + e.getLocalizedMessage());
		}
		return new SampleListResult(sampleDir.getSampleTreeRoot());
	}
	

}
