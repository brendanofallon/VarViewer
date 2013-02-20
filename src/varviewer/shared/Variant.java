package varviewer.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Variant implements Comparable<Variant>, Serializable {

	String chrom = "Z";
	int pos = -1;
	String ref = "X";
	String alt = "X";
	
	Map<String, Annotation> annotations = new HashMap<String, Annotation>();
	
	public Variant() {
		//blank on purpose, must have a no-arg constructor for serialization
	}
	
	public Variant(String chrom, int pos, String ref, String alt) {
		this.chrom = chrom;
		this.pos = pos;
		this.ref = ref;
		this.alt = alt;
	}
	
	public void addAnnotation(String key, String value) {
		annotations.put(key, new Annotation(value));
	}
	
	public void addAnnotation(String key, Double value) {
		annotations.put(key, new Annotation(value));
	}
	
	public Annotation getAnnotation(String key) {
		return annotations.get(key);
	}
	
	public String getAnnotationStr(String key) {
		Annotation anno = getAnnotation(key);
		return anno == null ? null : anno.toString();
	}
	
	public Double getAnnotationDouble(String key) {
		Annotation anno = getAnnotation(key);
		return anno == null ? null : anno.doubleVal;
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
