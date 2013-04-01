package varviewer.client.varTable;

import java.util.Comparator;

import varviewer.shared.variant.Variant;

import com.google.gwt.user.cellview.client.Column;

/**
 * Model for describing a single piece of information about a variant. This does not 
 * actually hold the variant data (that's in an annotation object that is stored in each Variant)
 * This mostly just augments a Column<Variant, ?> with additional data including user-friendly text header,
 * sorter, preferred width, etc. 
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
