package varviewer.client.varTable;

import java.util.Comparator;

import varviewer.shared.Variant;

import com.google.gwt.user.cellview.client.Column;

/**
 * A single piece of information about a variant that has a unique id, a user-friendly 
 * text label, and a TextColumn that knows how to render the info in a page
 * @author brendan
 *
 */
public class VarAnnotation<T> {
	
	final String id;
	final String userText;
	final Column<Variant, T> col;
	private double relativeWidth = 2.0; //Column width scaling factor
	private Comparator<Variant> sortComparator = null;
		
	public VarAnnotation(String id, String userText, Column<Variant, T> col, double relativeWidth) {
		this(id, userText, col);
		this.relativeWidth = relativeWidth;
		sortComparator = new DefaultAnnotationComparator(id);
	}
	
	public VarAnnotation(String id, String userText, Column<Variant, T> col, double relativeWidth, Comparator<Variant> comparator) {
		this(id, userText, col);
		this.relativeWidth = relativeWidth;
		if (comparator == null) {
			col.setSortable(false);
		}
		
		sortComparator = comparator;
	}
	
	public VarAnnotation(String id, String userText, Column<Variant, T> col) {
		this.id = id;
		this.userText = userText;
		this.col = col;
		col.setSortable(true);
		sortComparator = new DefaultAnnotationComparator(id);
	}

	public Comparator<Variant> getComparator() {
		return sortComparator;
	}
	
	public double getRelativeWidth() {
		return relativeWidth;
	}

	public void setRelativeWidth(double relativeWidth) {
		this.relativeWidth = relativeWidth;
	}

	public void setComparator(Comparator<Variant> comparator) {
		this.sortComparator = comparator;		
	}
	
}
