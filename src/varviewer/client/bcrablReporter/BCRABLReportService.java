package varviewer.client.bcrablReporter;

import varviewer.shared.SampleInfo;
import varviewer.shared.bcrabl.BCRABLReport;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("bcrablreport")
public interface BCRABLReportService extends RemoteService {

	BCRABLReport generateBCRABLReport(SampleInfo info);
	
}
