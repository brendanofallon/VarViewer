package varviewer.server.variant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import varviewer.shared.variant.Variant;

public class UncompressedCSVReader extends AbstractVariantReader {
	
	public UncompressedCSVReader(String path) throws IOException {
		super(new File(path));
	}

	/**
	 * Returns all variants in a VariantCollection
	 * @return
	 * @throws IOException 
	 */
	public VariantCollection toVariantCollection() {
		VariantCollection vars = new VariantCollection();
		try {
			BufferedReader reader = new BufferedReader( new FileReader(varFile));
			String line = reader.readLine();
			initializeHeader(line);
			line = reader.readLine(); //read next line, don't try to parse a variant from the header
			while(line != null) {
				Variant var = variantFromString(line.split("\t"), getAnnotationIndex(), numericFlags);
				if (var != null)
					vars.addRecordNoSort(var);
				line = reader.readLine();
			}

			vars.sortAllContigs();
			vars.setAnnoIndex(getAnnotationIndex());
			reader.close();
		}
		catch (IOException ex) {
			Logger.getLogger(getClass()).error("IO error reading variant file " + varFile.getAbsolutePath() + " exception: " + ex.getMessage());
		}
		
		
		return vars;
	}
	

}
