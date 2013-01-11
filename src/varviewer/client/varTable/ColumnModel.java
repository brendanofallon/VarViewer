package varviewer.client.varTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import varviewer.shared.Variant;

import com.google.gwt.user.cellview.client.TextColumn;

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
	
	public ColumnModel() {
		
		//A few default annotations...
		addColumn(new VarAnnotation("gene", "Gene", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotation("gene");
				return val != null ? val : "-";
			}
		}, 3.0, false));
		
//		addColumn(new VarAnnotation("contig", "Chr", new TextColumn<Variant>() {
//
//			@Override
//			public String getValue(Variant var) {
//				return var.getChrom();
//			}
//		}, 1.0));
//		
//		addColumn(new VarAnnotation("pos", "Start", new TextColumn<Variant>() {
//
//			@Override
//			public String getValue(Variant var) {
//				return "" + var.getPos();
//			}
//		}));
		

		addColumn(new VarAnnotation("exon.function", "Exon effect", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotation("exon.function");
				if (val == null || val.equals("-")) {
					val = var.getAnnotation("variant.type");
				}
				return val != null ? val : "-";
			}
		}, 3.0, false));
		
		addColumn(new VarAnnotation("nm.number", "NM Number", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotation("nm.number");
				return val != null ? val : "-";
			}
		}, 3.0, false));
		
		addColumn(new VarAnnotation("cdot", "c.dot", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotation("cdot");
				return val != null ? val : "-";
			}
		}, 2.0, false));
		
		addColumn(new VarAnnotation("pdot", "p.dot", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotation("pdot");
				return val != null ? val : "-";
			}
		}, 2.0, false));
		
//		addColumn(new VarAnnotation("quality", "Quality", new TextColumn<Variant>() {
//
//			@Override
//			public String getValue(Variant var) {
//				String val = var.getAnnotation("quality");
//				return val != null ? val : "-";
//			}
//		}, 2.0));
		
		addColumn(new VarAnnotation("depth", "Depth", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotation("depth");
				return val != null ? val : "-";
			}
		}, 1.0, true));
		
		addColumn(new VarAnnotation("pop.freq", "Pop. Freq.", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotation("pop.freq");
				if (val.equals("-"))
					val = "0";
				return val != null ? val : "0";
			}
		}, 1.0, true));
		
		
	}
	
	public void addColumn(VarAnnotation varAnno) {
		keys.add(varAnno.id);
		colMap.put(varAnno.id, varAnno);
	}
	
	/**
	 * Returns the list used to store all current annotation keys
	 * @return
	 */
	public List<String> getKeys() {
		return keys;
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
			str.append( getColumnForKey(key).getValue(var) + "\t");
		}
		return str.toString();
	}
	
	/**
	 * Get the column used to render the given annotation key, or null if there's
	 * no such column
	 * @param key
	 * @return
	 */
	public TextColumn<Variant> getColumnForKey(String key) {
		return colMap.get(key).col;
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
	public VarAnnotation getVarAnnoForKey(String key) {
		return colMap.get(key);
	}
}
