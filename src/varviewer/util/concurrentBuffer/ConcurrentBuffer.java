package varviewer.util.concurrentBuffer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import varviewer.server.variant.AbstractVariantReader;
import varviewer.server.variant.ConcurrentVariantReader;
import varviewer.server.variant.VariantCollection;


public class ConcurrentBuffer<T> {

	//Buffer won't ever contain more than this many items
	final int MAX_BUFFER_SIZE = 16384;

	//The main storage for items
	protected Queue<T> buffer = new ConcurrentLinkedQueue<T>();
	
	protected ProducerTask producer = null;
	protected ConsumerTask consumer = null;
	protected Thread producerThread = null;
	protected Thread consumerThread = null;
	protected ConcurrentBufferListener listener = null;
	
	public ConcurrentBuffer(Producer<T> prod, Consumer<T> cons, ConcurrentBufferListener listener) {
		this.producer = new ProducerTask(prod);
		this.consumer = new ConsumerTask(cons);
		this.listener = listener;
	}
	
	public void start() {
		producerThread = new Thread(producer);
		consumerThread = new Thread(consumer);
		
		producerThread.start();
		consumerThread.start();
		
		waitForCompletion();
	}
	
	private void waitForCompletion() {
		
		while(producerThread.isAlive()) {
			System.out.println("Waiting for producer to die. buffer size=" + buffer.size());
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		System.out.println("Producer is dead, signalling consumer to stop...");
		consumer.stopWhenBufferIsEmpty();
		
		while(consumerThread.isAlive()) {
			System.out.println("Waiting for consumer to die");
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Consumer is dead...");
		
		if (listener != null ) {
			listener.processHasFinished();
		}
	}

	class ProducerTask implements Runnable {

		final Producer<T> producer;
		
		ProducerTask(Producer<T> prod) {
			this.producer = prod;
		}
		
		@Override
		public void run() {
			
			while(! producer.isFinishedProducing()) {
				T item = producer.nextItem();
				if (item != null) {
					buffer.add(item);
				}
				
				if (buffer.size() > MAX_BUFFER_SIZE) {
					try {
						Thread.sleep(500);
						System.out.println("Producer is waiting for consumer to catch up....");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
		
		public boolean isDone() {
			return producer.isFinishedProducing();
		}
	}
	
	class ConsumerTask implements Runnable {

		final Consumer<T> consumer;
		boolean run = true;
		boolean stopIfEmpty = false;
		
		ConsumerTask(Consumer<T> cons) {
			this.consumer = cons;
		}
		
		@Override
		public void run() {
			
			while(run) {
				T item = buffer.poll();
				if (item != null) {
					consumer.processItem(item);
				}
				else {
					if (stopIfEmpty) {
						break;
					}
				}
			}
			
		}
		
		public void stopWhenBufferIsEmpty() {
			this.stopIfEmpty = true;
		}
		
	}
	
	static class StringProducer implements Producer<String> {

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
		public String nextItem() {
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
			}
			else {
				linesRead++;
			}
			return line;
		}
		
		public int getLinesRead() {
			return linesRead;
		}
		
	};
	
	
	static class StringProcessor implements Consumer<String> {

		int itemsProcessed = 0;
		@Override
		public void done() {
			//This never gets called, I think
		}

		public int getItemsProcessed() {
			return itemsProcessed;
		}
		
		@Override
		public void processItem(String item) {
			String[] toks = item.split("\t");
			itemsProcessed++;
			System.out.print("Item #" + itemsProcessed + " : ");
//			for(int i=0; i<toks.length; i++) {
//				System.out.print(" " + toks[i]);
//			}
			System.out.println();
			
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		
		final Date begin = new Date();
		
		AbstractVariantReader varReader = new ConcurrentVariantReader(new File("testinput.csv"));
		VariantCollection vars = varReader.toVariantCollection();
		
		Date end = new Date();
		double elapsedTime = (end.getTime() - begin.getTime())/1000.0;
		System.out.println("Read in " + vars.size() + " vars in " + elapsedTime + " seconds");
		
	}
	
	
}
