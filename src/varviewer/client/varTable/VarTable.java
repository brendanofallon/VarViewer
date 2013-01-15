package varviewer.client.varTable;

import java.util.ArrayList;
import java.util.List;

import varviewer.client.ExportService;
import varviewer.client.ExportServiceAsync;
import varviewer.client.VarListListener;
import varviewer.shared.Variant;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable.Resources;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Wraps a VarPage with a few buttons and extra widgets, allows for some easy scrolling / paging
 * @author brendan
 *
 */
public class VarTable extends FlowPanel implements ColumnModelListener, VarListListener {
	
	public static final int VISIBLE_ROWS = 25;
	
	//For custom styling of the VarPage CellTable, create the resources 
	VarPage varPage = new VarPage( (Resources)GWT.create(VarTableResources.class) );
	VarTableHeader header = null;
	ColumnModel colModel = new ColumnModel();
	
	public VarTable() {
		this.setStylePrimaryName("vartable");
		initComponents();
	}
	
	public void setVariants(List<Variant> variants) {
		varPage.setVariants(variants);
		varPage.setRowCount(variants.size(), true);
		varPage.setVisibleRange(0, VISIBLE_ROWS);
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
				Window.open(result, "_self", "");
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

	@Override
	public void variantListUpdated(List<Variant> newVars) {
		setVariants(newVars);
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
	
	ExportServiceAsync exportService = (ExportServiceAsync) GWT.create(ExportService.class);

	

	
}
