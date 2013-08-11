package varviewer.server.bcrabl;

import varviewer.shared.SampleInfo;
import varviewer.shared.bcrabl.BCRABLReport;

public interface ReportHandler {

	BCRABLReport getReportForSample(SampleInfo info);
	
}
