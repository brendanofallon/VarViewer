package varviewer.shared.variant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class Variant implements Comparable<Variant>, Serializable {

	String chrom = "Z";
	int pos = -1;
	String ref = "X";
	String alt = "X";
	
	//Stores mapping of annotation keys (like "pop.freq") to their indices in the annotations list
	AnnotationIndex annoIndex = null;
	List<Annotation> annotations = new ArrayList<Annotation>(16);
	
	public Variant() {
		//blank on purpose, must have a no-arg constructor for serialization
	}
	
	public Variant(String chrom, int pos, String ref, String alt) {
		this.chrom = chrom;
		this.pos = pos;
		this.ref = ref;
		this.alt = alt;
	}
	
//	public void addAnnotation(String key, String value) {
//		
//		addAnnotation(key, new Annotation(value));
//	}
//	
//	public void addAnnotation(String key, Double value) {
//		addAnnotation(key, new Annotation(value));
//	}
	
	public void addAnnotation(int index, Annotation anno) {
		while(annotations.size() <= index) {
			annotations.add(null);
		}
		annotations.set(index, anno);
	}
	
	public void setAnnotationIndex(AnnotationIndex index) {
		this.annoIndex = index;
	}
	
	public void setAnnotations(List<Annotation> annos) {
		annotations.clear();
		annotations.addAll(annos);
	}
	
	public Annotation getAnnotation(int index) {
		return index > -1 ? annotations.get(index) : null;
	}
	
	public Annotation getAnnotation(String key) {
		return getAnnotation( annoIndex.getIndexForKey(key));
	}
	
	public String getAnnotationStr(int index) {
		Annotation anno = getAnnotation(index);
		return anno == null ? null : anno.toString();
	}
	
	public Double getAnnotationDouble(int index) {
		Annotation anno = getAnnotation(index);
		return anno == null ? null : anno.getDoubleValue();
	}
	
	public String getAnnotationStr(String key) {
		return getAnnotationStr( annoIndex.getIndexForKey(key));
	}
	
	public Double getAnnotationDouble(String key) {
		return getAnnotationDouble( annoIndex.getIndexForKey(key));
	}

	
	public String getChrom() {
		return chrom;
	}

	public void setChrom(String chrom) {
		this.chrom = chrom;
	}

	public int getPos() {
		return pos;
	}
	
	/**
	 * Set the position of this variant. Warning! If this is in a VariantCollection you must re-sort
	 * all variants after you set the position!
	 * @param pos
	 */
	public void setPos(int pos) {
		this.pos = pos;
	}


	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}
	
	public String toString() {
		return chrom + ":" + pos + "\t" + ref + "\t" + alt;
	}

	@Override
	public int compareTo(Variant o) {
		if (o.getChrom().equals( getChrom())) {
			return this.getPos() - o.getPos();
		}
		else {
			return this.getChrom().compareTo( o.getChrom() );
		}
	}
	
	
}
