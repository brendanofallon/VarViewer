package varviewer.client.varTable;

import java.util.ArrayList;
import java.util.List;

import varviewer.shared.Variant;

import com.google.gwt.user.cellview.client.TextColumn;

/**
 * Maintains a list of all available Columns that can potentially be used in a ColumnModel
 * and VarTable. 
 * This is a singleton.
 * @author brendan
 *
 */
public class ColumnStore {

	private static List<VarAnnotation> cols = new ArrayList<VarAnnotation>();
	
	private static ColumnStore store;
	
	public static ColumnStore getStore() {
		if (store == null) {
			store = new ColumnStore();
		}
		
		return store;
	}
	
	/**
	 * Private constructor, get access to the store statically through ColumnStore.getStore()
	 */
	private ColumnStore() {
		initialize();
		store = this;
	}
	
	/**
	 * Obtain the column associated with the given key
	 * @param key
	 * @return
	 */
	public VarAnnotation getColumnForID(String key) {
		for(VarAnnotation col : cols) {
			if (col.id.equals(key)) {
				return col;
			}
		}
		return null;
	}
	
	/**
	 * Obtain a reference to a list of all potential columns
	 * @return
	 */
	public List<VarAnnotation> getAllColumns() {
		return cols;
	}
	
	private void addColumn(VarAnnotation col) {
		cols.add(col);
	}
	
	/**
	 * Creates all possible columns and stores them in a list here....
	 */
	private void initialize() {
		addColumn(new VarAnnotation("gene", "Gene", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotation("gene");
				return val != null ? val : "-";
			}
		}, 3.0, false));

		addColumn(new VarAnnotation("contig", "Chr", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				return var.getChrom();
			}
		}, 1.0, false));

		addColumn(new VarAnnotation("pos", "Start", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				return "" + var.getPos();
			}
		}, 1.0, true));


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

		addColumn(new VarAnnotation("quality", "Quality", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotation("quality");
				return val != null ? val : "-";
			}
		}, 1.0, true));

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
}
