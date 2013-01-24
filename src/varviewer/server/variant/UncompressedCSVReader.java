package varviewer.server.variant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import varviewer.shared.Variant;

public class UncompressedCSVReader extends AbstractVariantReader {
	
	public UncompressedCSVReader(String path) throws IOException {
		super(new File(path));
	}

	/**
	 * Returns all variants in a VariantCollection
	 * @return
	 * @throws IOException 
	 */
	public VariantCollection toVariantCollection() throws IOException {
		List<Variant> vars = new ArrayList<Variant>(1024);
		BufferedReader reader = new BufferedReader( new FileReader(varFile));
		String line = reader.readLine();
		initializeHeader(line);
		line = reader.readLine(); //read next line, don't try to parse a variant from the header
		while(line != null) {
			Variant var = variantFromString(line);
			if (var != null)
				vars.add(var);
			line = reader.readLine();
		}
		reader.close();
		if (vars.size()>0)
			Logger.getLogger(getClass()).info("Read in " + vars.size() + " variants from " + varFile);
		else {
			Logger.getLogger(getClass()).warn("Read in " + vars.size() + " variants from " + varFile);
		}
		return new VariantCollection(vars);
	}
	

}
