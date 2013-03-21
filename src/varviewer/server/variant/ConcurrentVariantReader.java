package varviewer.server.variant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import varviewer.shared.Variant;
import varviewer.util.concurrentBuffer.ConcurrentBuffer;
import varviewer.util.concurrentBuffer.Consumer;
import varviewer.util.concurrentBuffer.Producer;

/**
 * Uses a ConcurrentBuffer to speed up reading of big variant files. A Producer (StringProducer)
 * reads lines from a file and puts them in a buffer, while a Consumer (StringConverter) reads
 * from the buffer and converts the strings to variants. 
 * @author brendan
 *
 */
public class ConcurrentVariantReader extends AbstractVariantReader {

	public ConcurrentVariantReader(File source) throws IOException {
		super(source);
	}

	@Override
	public VariantCollection toVariantCollection() throws IOException {
		VariantCollection vars = new VariantCollection();
		
		BufferedReader reader = new BufferedReader( new FileReader(varFile));
		String line = reader.readLine();
		initializeHeader(line); //Header must be initialized before we create the StringConverter
		
		StringProducer prod = new StringProducer(reader);
		StringConverter converter = new StringConverter(vars, headerToks, numericFlags);
		
		ConcurrentBuffer<String[]> processor = new ConcurrentBuffer<String[]>(prod, converter, null);
		
		processor.start();
		
		vars.sortAllContigs();
		return vars;
	}


	static class StringConverter implements Consumer<String[]> {

		int itemsProcessed = 0;
		final VariantCollection vars;
		final String[] header;
		final boolean[] numericFlags;
		
		public StringConverter(VariantCollection vars, String[] header, boolean[] numericFlags) {
			this.vars = vars;
			this.header = header;
			this.numericFlags = numericFlags;
		}
		
		@Override
		public void done() {
			//This never gets called, I think
		}

		public int getItemsProcessed() {
			return itemsProcessed;
		}
		
		@Override
		public void processItem(String[] str) {
			Variant var = AbstractVariantReader.variantFromString(str, header, numericFlags);
			vars.addRecordNoSort(var);
		}
		
	}
	
	/**
	 * Reads lines from a source file
	 * @author brendan
	 *
	 */
	static class StringProducer implements Producer<String[] > {

		BufferedReader reader;
		
		private int linesRead = 0;
		private boolean done = false;
		
		public StringProducer(BufferedReader reader) {
			this.reader = reader;
		}
		
		@Override
		public boolean isFinishedProducing() {
			return done;
		}

		@Override
		public String[] nextItem() {
			String line;
			try {
				line = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				done = true;
				return null;
			}
			
			if (line == null) {
				done = true;
				return null;
			}
			else {
				linesRead++;
			}
			return line.split("\t");
		}
		
		public int getLinesRead() {
			return linesRead;
		}
		
	};

}
