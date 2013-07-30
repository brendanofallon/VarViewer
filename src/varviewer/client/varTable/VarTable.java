package varviewer.client.varTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import varviewer.client.services.ExportService;
import varviewer.client.services.ExportServiceAsync;
import varviewer.client.varTable.cisTrans.CisTransPopup;
import varviewer.client.varTable.pedigree.PedigreePopup;
import varviewer.client.varTable.pedigree.PedigreeVarAnnotation;
import varviewer.shared.variant.Variant;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable.Resources;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Wraps a VarPage with a nice header with some buttons and a pager. Also contains implementations
 * for a few of the buttons found in the header (export, search field updates, etc.)
 * @author brendan
 *
 */
public class VarTable extends FlowPanel implements ColumnModelListener, ProvidesResize, RequiresResize {
	
	public static final int VISIBLE_ROWS = 20;
	
	//For custom styling of the VarPage CellTable, create the resources 
	VarPage varPage = new VarPage( (Resources)GWT.create(VarTableResources.class) );
	
	VarTableHeader header = null;
	ColumnModel colModel = null;
	SearchBoxVariantFilter searchBoxFilter = new SearchBoxVariantFilter();
	List<Variant> fullVariantList = null; //Stores all variants passed in from VarListManager, but does not reflect filtering from the SearchBoxFilter
	List<PedigreeVarAnnotation> pedAnnotations = new ArrayList<PedigreeVarAnnotation>(); //Stores pedigree 
	VariantDisplay displayParent;
	
	public VarTable(VariantDisplay display, ColumnModel colModel) {
		displayParent = display;
		this.colModel = colModel;
		this.setStylePrimaryName("vartable");
		initComponents();
	}
	
	public void setVariants(List<Variant> variants) {
		this.clearSelectedVariants();
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

	/**
	 * Return the number of variants with their checkboxes checked.  
	 * @return
	 */
	public int getSelectedVariantCount() {
		return varPage.getSelectedVariants().size();
	}
	
	/**
	 * Return all variants that have their checkboxes checked. 
	 * @return
	 */
	public Set<Variant> getSelectedVariants() {
		return varPage.getSelectedVariants();
	}
	
	public void clearSelectedVariants() {
		varPage.clearSelectedVariants();
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
		//TODO : This should be moved to it's own class (Exporter?) soon
		//Prepare a small header
		List<String> varStrs = new ArrayList<String>();
		
		varStrs.add("Variant list for sample " + header.getSampleLabel());
		
		varStrs.add( displayParent.getFilterUserText() + "\n");
		
		varStrs.add( colModel.writeHeader() );
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
				Window.open("http://" + Location.getHost() + result, "_blank", "");
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
		PedigreePopup pedPopup = new PedigreePopup(displayParent);
		ColPickerPopup colPopup = new ColPickerPopup(colModel);
		CisTransPopup ctPopup = new CisTransPopup(displayParent);
		header = new VarTableHeader(this, colModel, pedPopup, ctPopup, colPopup);
		
		this.add(header);
		
		ScrollPanel pageScrollPanel = new ScrollPanel(varPage);
		pageScrollPanel.setWidth("100%");
		pageScrollPanel.setHeight("100%");
		this.add(pageScrollPanel);
		
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
	
	public void clearPedAnnotations() {
		pedAnnotations.clear();
	}
	
	@Override
	public void onResize() { 
		for (Widget child : getChildren()) { 
			if (child instanceof RequiresResize) { 
				((RequiresResize) child).onResize(); 
			}
		} 
	}
	
	public ColumnModel getColumnModel() {
		return colModel;
	}
	
	ExportServiceAsync exportService = (ExportServiceAsync) GWT.create(ExportService.class);

	

}
