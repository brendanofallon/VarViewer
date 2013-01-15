package varviewer.client;

import java.util.List;

import varviewer.client.filters.FiltersPanel;
import varviewer.client.varTable.VarTable;
import varviewer.shared.VariantFilter;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class VarViewer implements EntryPoint {
	

	  public void onModuleLoad() {
		initComponents();
		loadVariants();
	  }
	  
	  private void initComponents() {
		  FlowPanel mainPanel = new FlowPanel();
		  
		  HorizontalPanel filtersAndTablePanel = new HorizontalPanel();
		  filtersPanel = new FiltersPanel();
		  filtersAndTablePanel.add(filtersPanel);
		  filtersAndTablePanel.setStylePrimaryName("filterandtablepanel");
		  
		  varTable = new VarTable();
		  
		  filtersAndTablePanel.add(varTable);
		  mainPanel.add(filtersAndTablePanel);
		  
		  DetailsPanel detailsPanel = new DetailsPanel();
		  //details panel listens to variant selection events generated by the varTable
		  varTable.addVariantSelectionListener(detailsPanel); 
		  varManager.addListener(varTable);
		  filtersPanel.addListener(varManager);
		  mainPanel.add(detailsPanel);

		  RootPanel.get().add(mainPanel);		  
	  }

	  private void loadVariants() {
		  varManager.setSample("HHT11");
		  List<VariantFilter> filters = filtersPanel.getFilters();
		  varManager.setFilters(filters);
		  
		  varManager.reloadIfRequired();
	  }

	  FiltersPanel filtersPanel;
	  VarListManager varManager = VarListManager.getManager();
	  VarTable varTable = null;
}

