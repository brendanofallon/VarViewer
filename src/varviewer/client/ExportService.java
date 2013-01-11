package varviewer.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("export")
public interface ExportService extends RemoteService {
	String doExport(List<String> lines);
}
