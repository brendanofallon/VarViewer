package varviewer.server.variant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import varviewer.shared.variant.AnnotationIndex;
import varviewer.shared.variant.Variant;
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
	
	public ConcurrentVariantReader() {
		//for happy bean-ness
	}
	
	@Override
	public VariantCollection toVariantCollection() {
		VariantCollection vars = new VariantCollection();
				
		try {
			BufferedReader reader = new BufferedReader( new FileReader(varFile));
			String line = reader.readLine();
			initializeHeader(line); //Header must be initialized before we create the StringConverter

			StringProducer prod = new StringProducer(reader);
			StringConverter converter = new StringConverter(vars, getAnnotationIndex(), numericFlags);

			ConcurrentBuffer<String[]> processor = new ConcurrentBuffer<String[]>(prod, converter, null);

			processor.start();

			vars.sortAllContigs();
			vars.setAnnoIndex(getAnnotationIndex());
		}
		catch (IOException ex) {
			Logger.getLogger(getClass()).error("IO error reading variant file " + varFile.getAbsolutePath() + " exception: " + ex.getMessage());
		}
		return vars;
	}


	static class StringConverter implements Consumer<String[]> {

		int itemsProcessed = 0;
		final VariantCollection vars;
		final AnnotationIndex index;
		final boolean[] numericFlags;
		
		public StringConverter(VariantCollection vars, AnnotationIndex index, boolean[] numericFlags) {
			this.vars = vars;
			this.index = index;
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
			Variant var = AbstractVariantReader.variantFromString(str, index, numericFlags);
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
		
	}



}
