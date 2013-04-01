package varviewer.client.varTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import varviewer.shared.variant.Variant;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

/**
 * The list of columns / annotations that should be displayed in the VarTable. Each column
 * is associated with a VarAnnotation that contains a user-readable label and the actual
 * TextColumn used to render the data.  
 * @author brendan
 *
 */
public class ColumnModel {
	
	//Ordered list of annotation keys and their respective columns
	List<String> keys = new ArrayList<String>();
	Map<String, VarAnnotation> colMap = new HashMap<String, VarAnnotation>();
	List<ColumnModelListener> listeners = new ArrayList<ColumnModelListener>();
	
	public ColumnModel() {
//		addColumn( ColumnStore.getStore().getColumnForID("contig"));
//		addColumn( ColumnStore.getStore().getColumnForID("pos"));
//		addColumn( ColumnStore.getStore().getColumnForID("some.annotation"));
//		addColumn( ColumnStore.getStore().getColumnForID("another.annotation"));
//		addColumn( ColumnStore.getStore().getColumnForID("quality"));
		addColumn( ColumnStore.getStore().getColumnForID("gene"));
		addColumn( ColumnStore.getStore().getColumnForID("exon.function"));
		addColumn( ColumnStore.getStore().getColumnForID("zygosity"));
		addColumn( ColumnStore.getStore().getColumnForID("cdot"));
		addColumn( ColumnStore.getStore().getColumnForID("pdot"));
		addColumn( ColumnStore.getStore().getColumnForID("pop.freq"));
		addColumn( ColumnStore.getStore().getColumnForID("disease.pics"));
		addColumn( ColumnStore.getStore().getColumnForID("rsnum"));
		addColumn( ColumnStore.getStore().getColumnForID("igv.link"));
	}
	
	public void addColumn(VarAnnotation varAnno) {
		keys.add(varAnno.id);
		colMap.put(varAnno.id, varAnno);
		fireColumnChange();
	}
	
	public void removeColumn(String id) {
		keys.remove(id);
		colMap.remove(id);
		fireColumnChange();
	}
	
	public void removeColumnsByClass(Class<?> clz) {
		List<String> keysToRemove = new ArrayList<String>();
		for(String key : colMap.keySet()) {
			VarAnnotation<?> varAnno = colMap.get(key);
			if (varAnno.getClass().equals(clz)) {
				keysToRemove.add(key);
			}
		}
		
		for(String key: keysToRemove) {
			colMap.remove(key);
			keys.remove(key);
		}
		
		if (keysToRemove.size()>0)
			fireColumnChange();
	}
	
	/**
	 * Returns the list used to store all current annotation keys
	 * @return
	 */
	public List<String> getKeys() {
		return keys;
	}
	
	/**
	 * Obtain a String with the user text for each column separated by commas, suitable as a header
	 * for the variants written by writeVarian(var)  
	 * @return
	 */
	public String writeHeader() {
		StringBuilder str = new StringBuilder();
		for(String key : getKeys()) {
			String txt = this.getUserTextForKey(key);
			str.append(txt + ",");
		}
		
		return str.toString();
	}
	
	/**
	 * Creates a string representing the given Variant using the currently active columns
	 * in this model
	 * @param var
	 * @return
	 */
	public String writeVariant(Variant var) {
		StringBuilder str = new StringBuilder();
		for(String key : getKeys()) {
			Object val = getColumnForKey(key).getValue(var);
			String valStr = "?";
			if (val != null) {
				valStr = val.toString();
				
				if (val instanceof SafeHtml) {
					SafeHtml html = (SafeHtml) val;
					valStr = html.asString(). replaceAll("\\<.*?>","");
				}
				
				if (val instanceof ImageResource) {
					ImageResource imgRes = (ImageResource)val;
					valStr = "(image)";
					if (imgRes.equals(resources.refImage())) {
						valStr = "ref";
					}
					if (imgRes.equals(resources.hetImage())) {
						valStr = "het";
					}
					if (imgRes.equals(resources.homImage())) {
						valStr = "hom";
					}
				}
			}
			
			str.append( valStr + ",");
		}
		return str.toString();
	}
	
	/**
	 * Get the column used to render the given annotation key, or null if there's
	 * no such column
	 * @param key
	 * @return
	 */
	public Column<Variant, ?> getColumnForKey(String key) {
		return colMap.get(key).col;
	}

	/**
	 * True if this column model contains a column with the given id
	 * @param key
	 * @return
	 */
	public boolean containsColumn(String key) {
		return colMap.containsKey(key);
	}
	
	/**
	 * A user-friendly String describing this annotation
	 * @param key
	 * @return
	 */
	public String getUserTextForKey(String key) {
		return colMap.get(key).userText;
	}
	
	/**
	 * Return the VarAnno object associated with the key
	 * @param key
	 * @return
	 */
	public VarAnnotation<?> getVarAnnoForKey(String key) {
		return colMap.get(key);
	}

	/**
	 * Add a listener that will be notified when this model changes
	 * @param l
	 */
	public void addListener(ColumnModelListener l) {
		listeners.add(l);
	}
	
	public void removeListener(ColumnModelListener l) {
		listeners.remove(l);
	}
	
	private void fireColumnChange() {
		for(ColumnModelListener l : listeners) {
			l.columnStateChanged(this);
		}
	}
	
	VarPageResources resources = (VarPageResources) GWT.create(VarPageResources.class);

}
