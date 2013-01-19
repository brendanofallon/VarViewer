package varviewer.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ExportServiceAsync {
	void doExport(List<String> lins, AsyncCallback<String> dlPath);
}
