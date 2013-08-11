package varviewer.client.bcrablReporter;

import varviewer.shared.SampleInfo;
import varviewer.shared.bcrabl.BCRABLReport;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BCRABLReportServiceAsync {

	void generateBCRABLReport(SampleInfo info, AsyncCallback<BCRABLReport> reportCallback);
	
}
