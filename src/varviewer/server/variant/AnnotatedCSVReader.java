package varviewer.server.variant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.broad.tribble.readers.TabixReader;

import varviewer.server.AbstractVariantServer;
import varviewer.shared.Interval;
import varviewer.shared.IntervalList;
import varviewer.shared.Variant;
import varviewer.shared.VariantRequest;

public class AnnotatedCSVReader extends AbstractVariantServer {

	private TabixReader reader = null;
	private File varFile = null;
	private String[] headerToks = null;
	
	public AnnotatedCSVReader(String path) throws IllegalArgumentException {
		this.varFile = new File(path);
		if (!varFile.exists()) {
			throw new IllegalArgumentException("File " + varFile.getAbsolutePath() + " does not exist");
		}
		if (!varFile.canRead()) {
			throw new IllegalArgumentException("File " + varFile.getAbsolutePath() + " exists but is not readable");
		}
	}
	
	/**
	 * Create the Tabix file reader. This should only happen once
	 */
	private void initializeReader() {
		try {
			reader = new TabixReader(varFile.getAbsolutePath());
			String header = reader.readLine();
			header = header.substring(1); //Trim off leading #
			headerToks = header.split("\t");
			for(int i=0; i<headerToks.length; i++) {
				headerToks[i] = headerToks[i].trim();
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Error opening TGP data at path " + varFile.getAbsolutePath() + " error : " + e.getMessage());
		}
	}	
	
	
	@Override
	public List<Variant> getVariants(VariantRequest req) {
		if (reader == null) {
			initializeReader();
		}

		List<Variant> vars = new ArrayList<Variant>();
		try {
			IntervalList intervals = req.getIntervals();
			for(String contig : intervals.getContigs()) {
				for(Interval interval : intervals.getIntervalsInContig(contig)) {
					vars.addAll( variantsInInterval(contig, interval) );
				}
			}
		}
		catch(IOException ex) {
			ex.printStackTrace();
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
			String qStr = contig + ":" + interval.getFirstPos() + "-" + interval.getLastPos();
			TabixReader.Iterator iter = reader.query(qStr);

			if(iter != null) {
				String val = iter.next();
				while(val != null) {
					Variant var = variantFromString(val);
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

	/**
	 * Parse, create and return a variant from the given string. All items in columns five
	 * and greater are treated as annotations with key determined by the header 
	 * @param str
	 * @return
	 */
	private Variant variantFromString(String str) {
		String[] toks = str.split("\t");
		if (toks.length != headerToks.length) {
			return null;
		}
		String contig = toks[0];
		Integer start = Integer.parseInt(toks[1]);
		String ref = toks[3];
		String alt = toks[4];
		Variant var = new Variant(contig, start, ref, alt);
		for(int i=5; i<toks.length; i++) {
			var.addAnnotation(headerToks[i], toks[i]);
			//System.out.println("Adding annotation '" + headerToks[i] + "' = " + toks[i]);
		}
		return var;
	}




	
}
