package varviewer.client.varTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import varviewer.shared.variant.Variant;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

/**
 * A fairly thin wrapper for a CellTable<Variant>, this just displays a list of variants.
 * @author brendan
 *
 */
public class VarPage extends CellTable<Variant> {

	private ListDataProvider<Variant> varData = new ListDataProvider<Variant>();
	private List<VariantSelectionListener> varListeners = new ArrayList<VariantSelectionListener>(); 
	private List<VarAnnotation<?>> displayedAnnotations = new ArrayList<VarAnnotation<?>>();
	
	
	public VarPage(Resources resources) {
		super(VarTable.VISIBLE_ROWS, resources);
		varData.addDataDisplay(this);
		this.setWidth("100%", true);		

		this.addCellPreviewHandler(new CellPreviewEvent.Handler<Variant>() {

			@Override
			public void onCellPreview(CellPreviewEvent<Variant> event) {
				//Many types of events may come through here, we only respond to click events
				NativeEvent nEvt = event.getNativeEvent();
				boolean isClick = "click".equals(nEvt.getType());
				if (isClick) {
					Variant var = event.getValue();
					if (var != null) {
						fireVariantSelection(var);
					}
				}
			}
			
		});
		
		
		//Potential confusion here: We use a SelectionModel to handle the CheckBoxes, which are always column 0,
		//but clicks on rows separately trigger the gene details box to be updated.
		final MultiSelectionModel<Variant> selectionModel = new MultiSelectionModel<Variant>();
		this.setSelectionModel(selectionModel, DefaultSelectionEventManager.<Variant> createCheckboxManager(0));
		
		Column<Variant, Boolean> checkColumn = new Column<Variant, Boolean>(
			    new CheckboxCell(true, false)) {
			  @Override
			  public Boolean getValue(Variant var) {
			    return selectionModel.isSelected(var);
			  }
			  
			};
		
		this.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
	
		this.setColumnWidth(checkColumn, 40, Unit.PX);
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
		//Server-side errors sometimes result in a null list of variants, which causes a 
		//null pointer exception here if we don't catch it
		if (vars != null) {
			varData.setList(vars);		
			initializeSorters();
		}
	}
	
	/**
	 * Return all currently checked (selected) Variants. Set may be empty.  
	 * @return
	 */
	public Set<Variant> getSelectedVariants() {
		return ((MultiSelectionModel<Variant>) this.getSelectionModel()).getSelectedSet();
	}
	
	/**
	 * Create and set the sorter for all column types
	 */
	private void initializeSorters() {
		
		//We must add all column sorting handlers *after* the variants have been supplied
		ListHandler<Variant> columnSortHandler = new ListHandler<Variant>(varData.getList());
		for(VarAnnotation<?> varAnno : displayedAnnotations) {
			if (varAnno.getComparator() != null)
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
	public void addColumn(VarAnnotation<?> varAnno) {
		this.addColumn(varAnno.col, varAnno.userText);
		displayedAnnotations.add(varAnno);
	}
	
	/**
	 * Remove all displayed columns. The table will not draw any data after this call. 
	 */
	public void clearColumns() {
		for(VarAnnotation<?> varAnno : displayedAnnotations) {
			this.removeColumn( varAnno.col );
		}
		displayedAnnotations.clear();
		clearSelectedVariants();
	}
	
	public void clearSelectedVariants() {
		((MultiSelectionModel)this.getSelectionModel()).clear();
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
