package varviewer.server.textFetch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import varviewer.shared.TextFetchResult;

/**
 * An implementation of a text-fetcher that looks for a file with a name matching the given
 * id, then attempts to read and return text from it
 * @author brendan
 *
 */
public class FileTextFetcher implements TextFetcherHandler {

	@Override
	public TextFetchResult fetchText(String id) {
		File textFile = new File(id);
		if (! textFile.exists()) {
			Logger.getLogger(getClass()).warn("File " + textFile.getAbsolutePath() + " not found, cannot fetch text from it");
			return null;
		}
		
		try {
			List<String> text = readTextFromFile(textFile);
			TextFetchResult res = new TextFetchResult();
			res.setLinesOfText(text);
			return res;
		} catch (IOException e) {
			Logger.getLogger(getClass()).warn("IO error reading file " + textFile.getAbsolutePath() + " : " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private List<String> readTextFromFile(File textFile) throws IOException {
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(textFile));
		String line = reader.readLine();
		while(line != null) {
			lines.add(line);
			line = reader.readLine();
		}
		reader.close();
		return lines;
	}

	
	
}
