package varviewer.client.varTable;

import java.util.ArrayList;
import java.util.List;

import varviewer.client.services.ExportService;
import varviewer.client.services.ExportServiceAsync;
import varviewer.shared.Variant;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable.Resources;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Wraps a VarPage with a few buttons and extra widgets, allows for some easy scrolling / paging
 * @author brendan
 *
 */
public class VarTable extends FlowPanel implements ColumnModelListener, ProvidesResize, RequiresResize {
	
	public static final int VISIBLE_ROWS = 20;
	
	//For custom styling of the VarPage CellTable, create the resources 
	VarPage varPage = new VarPage( (Resources)GWT.create(VarTableResources.class) );
	
	VarTableHeader header = null;
	ColumnModel colModel = new ColumnModel();
	SearchBoxVariantFilter searchBoxFilter = new SearchBoxVariantFilter();
	List<Variant> fullVariantList = null; //Stores all variants passed in from VarListManager, but does not reflect filtering from the SearchBoxFilter
	
	public VarTable() {
		this.setStylePrimaryName("vartable");
		initComponents();
	}
	
	public void setVariants(List<Variant> variants) {
		fullVariantList = variants;
		List<Variant> searchBoxPassingVars = new ArrayList<Variant>();
		if (searchBoxFilter.getFilterCount()>0) {
			for(Variant var : variants) {
				if ( searchBoxFilter.variantPasses(var)) {
					searchBoxPassingVars.add(var);
				}
			}
		}
		else {
			searchBoxPassingVars = fullVariantList;
		}
		varPage.setVariants(searchBoxPassingVars);
		varPage.setRowCount(searchBoxPassingVars.size(), true);
		varPage.setVisibleRange(0, VISIBLE_ROWS);
	}

	public void handleSearchBoxTextChange(String text) {
		if (text.trim().length()==0) {
			searchBoxFilter.clearFilters();
		}
		else {
			searchBoxFilter.setTerms(text);
		}
		
		setVariants(fullVariantList);
	}
	
	protected VarPage getVarPage() {
		return varPage;
	}
	
	/**
	 * Download currently displayed data as a .csv. This method is called when the user
	 * clicks the 'Export' button, which lives in the TableHeader. It uses the exportService
	 * to write the data to a file on the server, then downloads the file
	 */
	protected void exportData() {
		List<String> varStrs = new ArrayList<String>();
		for(Variant var : varPage.getVariantList()) {
			varStrs.add( colModel.writeVariant(var));
		}
		
		exportService.doExport(varStrs, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error exporting file : " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				Window.open("http://" + Location.getHost() + result, "_self", "");
			}
		});
	}
	
	/**
	 * Add a new object that will be notified when the selected variant changes
	 * @param varListener
	 */
	public void addVariantSelectionListener(VariantSelectionListener varListener) {
		varPage.addVariantSelectionListener(varListener);
	}
	
	/**
	 * Create and initialize a few UI components
	 */
	private void initComponents() {
		header = new VarTableHeader(this);
		
		this.add(header);
		this.add(varPage);
		
		//Initialize column model
		columnStateChanged(colModel);
		colModel.addListener(this);
	}
	
	/**
	 * Reference to the header object of this table, allowing access to the label
	 * @return
	 */
	VarTableHeader getHeader() {
		return header;
	}
	
	@Override
	public void columnStateChanged(ColumnModel model) {
		varPage.setColumns(colModel);
		
		//Set column widths
		double widthTotal = 0;
		for(String key : colModel.getKeys()) {
			widthTotal = colModel.getVarAnnoForKey(key).getRelativeWidth();
		}
		
		for(String key: colModel.getKeys()) {
			varPage.setColumnWidth(colModel.getColumnForKey(key), colModel.getVarAnnoForKey(key).getRelativeWidth() / widthTotal * 100.0, Unit.PCT);
		}
		
	}
	
	@Override
	public void onResize() { 
		for (Widget child : getChildren()) { 
			if (child instanceof RequiresResize) { 
				((RequiresResize) child).onResize(); 
			}
		} 
	}
	
	
	ExportServiceAsync exportService = (ExportServiceAsync) GWT.create(ExportService.class);

	
	
}
