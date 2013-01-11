package varviewer.server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import varviewer.client.ExportService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The export service writes some text to a file (in a tmp dir on the server) then returns
 *  the path to that file so it can be downloaded from the client
 * @author brendan
 *
 */
public class ExportServiceImpl extends RemoteServiceServlet implements ExportService {

	@Override
	public String doExport(List<String> lines) {
		
		String dir = "exportdata";
		
		String filename = "export-" + ("" + System.currentTimeMillis()).substring(5) + ".csv";
		String fullPath = dir + "/" + filename;
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(fullPath));

			for(String line : lines) {
				writer.write(line + "\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("Error writing tmp file for export: " + e.getMessage());
		}
		return "/" + fullPath;
	}

}
