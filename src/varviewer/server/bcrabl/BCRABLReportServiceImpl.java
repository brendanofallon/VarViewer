package varviewer.server.bcrabl;

import org.springframework.context.ApplicationContext;

import varviewer.client.bcrablReporter.BCRABLReportService;
import varviewer.server.appContext.SpringContext;
import varviewer.shared.SampleInfo;
import varviewer.shared.bcrabl.BCRABLReport;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class BCRABLReportServiceImpl extends RemoteServiceServlet implements BCRABLReportService {

	ReportHandler reportHandler = null;
	
	@Override
	public BCRABLReport generateBCRABLReport(SampleInfo info) {
	
		if (reportHandler == null) {
			ApplicationContext context = SpringContext.getContext(); 
			reportHandler = (ReportHandler) context.getBean("bcrablReportHandler");	
		}	
		
		try {
			BCRABLReport report = reportHandler.getReportForSample(info);
			return report;
		}
		catch (Exception ex) {
			BCRABLReport report = new BCRABLReport();
			report.setMessage("Error processing sample: " + ex.getMessage());
			return report;
		}
	
	}

}
