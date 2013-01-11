package varviewer.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ExportServiceAsync {
	void doExport(List<String> lins, AsyncCallback<String> dlPath);
}
