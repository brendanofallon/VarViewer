package varviewer.client.varTable;

import java.util.ArrayList;
import java.util.List;

import varviewer.shared.Variant;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * A fairly thin wrapper for a CellTable<Variant>, this just displays a list of variants.
 *  Multiple VarPages make up a single VarTable
 * @author brendan
 *
 */
public class VarPage extends CellTable<Variant> {

	private ListDataProvider<Variant> varData = new ListDataProvider<Variant>();
	private List<VariantSelectionListener> varListeners = new ArrayList<VariantSelectionListener>(); 
	private List<VarAnnotation> displayedAnnotations = new ArrayList<VarAnnotation>();
	
	
	public VarPage(Resources resources) {
		super(VarTable.VISIBLE_ROWS, resources);
		varData.addDataDisplay(this);
		this.setWidth("100%", true);		
		final SingleSelectionModel<Variant> selectionModel = new SingleSelectionModel<Variant>();
		this.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				handleSelectionChange( (Variant)selectionModel.getSelectedObject() );
			}
		});
		
	}

	/**
	 * Called when the user clicks the table somewhere. We usually pop open the gene details
	 * panel when this happens. 
	 */
	protected void handleSelectionChange(Variant selectedVariant) {
		fireVariantSelection(selectedVariant);
	}

	/**
	 * Cause this varPage to show the given variants. Future modifications to the list
	 * will not be reflected in this table until setVariants is called again.  
	 * @param vars
	 */
	public void setVariants(List<Variant> vars) {
		varData.setList(vars);		
		initializeSorters();
	}
	
	/**
	 * Create and set the sorter for all column types
	 */
	private void initializeSorters() {
		
		//We must add all column sorting handlers *after* the variants have been supplied
		ListHandler<Variant> columnSortHandler = new ListHandler<Variant>(varData.getList());
		for(VarAnnotation varAnno : displayedAnnotations) {
			columnSortHandler.setComparator(varAnno.col, varAnno.getComparator());
		}
		this.addColumnSortHandler(columnSortHandler);
	}
	
	/**
	 * Returns an reference to the actual list of variants used to store all of the data.
	 * Don't modify it. 
	 * @return
	 */
	public List<Variant> getVariantList() {
		return varData.getList();
	}
	
	/**
	 * Add a new column to be displayed. Probably not a great idea to add the same column
	 * multiple times. 
	 * @param varAnno
	 */
	public void addColumn(VarAnnotation varAnno) {
		this.addColumn(varAnno.col, varAnno.userText);
		displayedAnnotations.add(varAnno);
	}
	
	/**
	 * Remove all displayed columns. The table will not draw any data after this call. 
	 */
	public void clearColumns() {
		for(VarAnnotation varAnno : displayedAnnotations) {
			this.removeColumn( varAnno.col );
		}
		displayedAnnotations.clear();
	}
	
	public void setColumns(ColumnModel model) {
		clearColumns();
		for(String key : model.getKeys()) {
			addColumn(model.getVarAnnoForKey(key));
		}
		
		initializeSorters();
	}
	
	
	/**
	 * Register a new listener that will be notified when a new variant is selected
	 * @param listener
	 */
	public void addVariantSelectionListener(VariantSelectionListener listener) {
		varListeners.add(listener);
	}
	
	public void removeVariantSelectionListener(VariantSelectionListener listener) {
		varListeners.remove(listener);
	}
	
	/**
	 * Calls variantSelected() on all variant selection listeners
	 * @param selectedVar
	 */
	protected void fireVariantSelection(Variant selectedVar) {
		for(VariantSelectionListener l : varListeners) {
			l.variantSelected(selectedVar);
		}
	}


}
