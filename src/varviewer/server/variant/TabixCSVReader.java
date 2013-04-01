package varviewer.server.variant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.broad.tribble.readers.TabixReader;

import varviewer.shared.Interval;
import varviewer.shared.variant.Variant;

public class TabixCSVReader extends AbstractVariantReader {
	
	public TabixCSVReader(String path) throws IOException {
		super(new File(path));
	}
	
	public VariantCollection toVariantCollection() {
		VariantCollection vars = new VariantCollection();
		try {
			TabixReader reader = new TabixReader(varFile.getAbsolutePath());
			String line = reader.readLine();
			initializeHeader(line);
			line = reader.readLine();
			while(line != null) {
				Variant var = variantFromString(line.split("\t"), getAnnotationIndex(), numericFlags);
				if (var != null)
					vars.addRecordNoSort(var);
				line = reader.readLine();
			}
			reader.close();
			vars.sortAllContigs();
			vars.setAnnoIndex(getAnnotationIndex());
		}
		catch (IOException ex) {
			Logger.getLogger(getClass()).error("IO error reading variant file " + varFile.getAbsolutePath() + " exception: " + ex.getMessage());
		}
		return vars;
	}
	
	/**
	 * Create a list of Variants with properties matching those read from the file in the given interval
	 * @param contig
	 * @param interval
	 * @return
	 * @throws IOException
	 */
	private List<Variant> variantsInInterval(String contig, Interval interval) throws IOException {
		List<Variant> vars = new ArrayList<Variant>();
		try {
			TabixReader reader = new TabixReader(varFile.getAbsolutePath());
			String qStr = contig + ":" + interval.getFirstPos() + "-" + interval.getLastPos();
			TabixReader.Iterator iter = reader.query(qStr);

			if(iter != null) {
				String val = iter.next();
				while(val != null) {
					Variant var = variantFromString(val.split("\t"), getAnnotationIndex(), numericFlags);
					vars.add(var);
					val = iter.next();
				}

			}
		}
		catch (RuntimeException rex) {
			//Bad contigs will cause an array out-of-bounds exception to be thrown by
			//the tabix reader. There's not much we can do about this since the methods
			//are private... right now we just ignore it and skip this variant
		}
		return vars;
	}

	





	
}
