package varviewer.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import varviewer.client.services.ExportService;

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
		
		
		String dir = VVProps.getProperty("export.dir");
		if (dir == null)
			dir = "exportdata";
		
		String filename = "export-" + ("" + System.currentTimeMillis()).substring(5) + ".csv";
		String fullPath = dir + "/" + filename;
		
		File tmpFile = new File(fullPath);
		
		Logger.getLogger(getClass()).info("Writing export data to: " + tmpFile.getAbsolutePath());
		
		BufferedWriter writer;
		try {
			int lineCount = 0;
			tmpFile.createNewFile();
			writer = new BufferedWriter(new FileWriter(tmpFile));

			for(String line : lines) {
				writer.write(line + "\n");
				lineCount++;
			}
			writer.close();
			Logger.getLogger(getClass()).info("Wrote " + lineCount + " lines of export data to " + tmpFile.getAbsolutePath());
			
		} catch (IOException e) {
			e.printStackTrace();
			Logger.getLogger(getClass()).warn("Error writing export data to file: " + e.getMessage());
			//throw new IllegalStateException("Error writing tmp file for export: " + e.getMessage());
		}
		
		return "/VarViewer/exportdata/" + filename;
	}

}
