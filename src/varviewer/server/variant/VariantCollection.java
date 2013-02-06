package varviewer.server.variant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import varviewer.shared.AllIntervals;
import varviewer.shared.IntervalList;
import varviewer.shared.Variant;

/**
 * An in-memory collection of variants
 * @author brendan
 *
 */
public class VariantCollection {

	protected Map<String, List<Variant>>  vars = new HashMap<String, List<Variant>>();
	
	/**
	 * Build a new variant pool from the given list of variants
	 * @param varList
	 */
	public VariantCollection(List<Variant> varList) {
		for(Variant v : varList) {
			List<Variant> contig = vars.get(v.getChrom());
			if (contig == null) {
				contig = new ArrayList<Variant>(2048);
				vars.put(v.getChrom(), contig);
			}
			contig.add(v);
		}
	}
	
	public VariantCollection(VCFReader reader) throws IOException {
		do {
			Variant rec = reader.toVariant();
			if (rec == null) {
				System.err.println("Warning, could not import variant from line: " + reader.getCurrentLine() );
			}
			else {
				this.addRecordNoSort(rec);
			}
		} while (reader.advanceLine());
		sortAllContigs();
	}
	
	/**
	 * Sort all variant records in each contig
	 */
	public void sortAllContigs() {
		for(String contig : getContigs()) {
			List<Variant> records = getVariantsForContig(contig);
			Collections.sort(records);
		}
	}
	
	/**
	 * Add a new record to the pool but do not sort the contig it was added to. This is 
	 * faster if you're adding lots of variants (from a VCFFile, for instance), but
	 * requires that all contigs are sorted 
	 * @param rec
	 */
	public void addRecordNoSort(Variant rec) {
		List<Variant> contigVars = vars.get( rec.getChrom() ); 
		if (contigVars == null) {
			contigVars = new ArrayList<Variant>(2048);
			vars.put(rec.getChrom(), contigVars);
		}
		contigVars.add(rec);
	}
	
	public int getContigCount() {
		return vars.size();
	}

	public Collection<String> getContigs() {
		return vars.keySet();
	}
	
	/**
	 * Find and return the variant at the given position, returns null if there is
	 * no such variant
	 * @param contig
	 * @param pos
	 * @return
	 */
	public Variant getVariant(String contig, int pos) {
		List<Variant> cVars = vars.get(contig);
		if (cVars == null)
			return null;
		
		qRec.setPos(pos);
		
		int index = Collections.binarySearch(cVars, qRec, posComparator);
		if (index < 0) {
			return null;
		}
		
		return cVars.get(index);		
		
	}
	
	/**
	 * Returns a reference to the list of variants in the given contig, modifications
	 * to the list will modify this collection
	 * @param contig
	 * @return
	 */
	public List<Variant> getVariantsForContig(String contig) {
		List<Variant> varList = vars.get(contig);
		if (varList != null)
			return varList;
		else 
			return new ArrayList<Variant>();
	}
	
	/**
	 * Return all variants in this collection that are in the regions
	 * defined by the IntervalList. Returns an empty list if no variants
	 * match the regions
	 * @param intervals
	 * @return
	 */
	public List<Variant> getVariantsInIntervals(IntervalList intervals) {
		
		if (intervals instanceof AllIntervals) {
			return asList();
		}

		List<Variant> varsToReturn = new ArrayList<Variant>(128);
		for(String contig : intervals.getContigs())  {
			for(Variant var : getVariantsForContig(contig)) {
				if (intervals.contains(var.getChrom(), var.getPos())) {
					varsToReturn.add(var);
				}
			}

		}
		
		return varsToReturn;
	}

	/**
	 * Returns the number of variants in this collection
	 * @return
	 */
	public int size() {
		int size = 0;
		for(String contig : getContigs()) {
			size += getVariantsForContig(contig).size();
		}
		return size;
	}
	
	/**
	 * Push all variants into a single list
	 * @return
	 */
	public List<Variant> asList() {
		List<Variant> allVars = new ArrayList<Variant>( size() );
		for(String contig : getContigs()) {
			allVars.addAll( getVariantsForContig(contig));
		}
		return allVars;
	}
	
	public static class PositionComparator implements Comparator<Variant> {

		@Override
		public int compare(Variant o1, Variant o2) {
			if (o1 == o2) {
				return 0;
			}

			return o1.getPos() - o2.getPos();
		}
	}
	
	private Variant qRec = new Variant(); //Just used for binary searches
	private PositionComparator posComparator = new PositionComparator();
}
