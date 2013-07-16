package varviewer.client.varTable.filters;

import java.util.ArrayList;
import java.util.List;

import varviewer.client.HighlightButton;
import varviewer.client.serviceUI.SampleViewUI;
import varviewer.shared.varFilters.DeleteriousFilter;
import varviewer.shared.varFilters.ExonFuncFilter;
import varviewer.shared.varFilters.GeneFilter;
import varviewer.shared.varFilters.HGMDOmimFilter;
import varviewer.shared.varFilters.MaxFreqFilter;
import varviewer.shared.varFilters.QualityDepthFilter;
import varviewer.shared.variant.VariantFilter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The filters panel displays a succession of VariantFilters, whose visual representation
 * is a FilterBox. This object manages the list of currently active filters, and fires
 * events to FilterListeners when the list of filters has been changed. 
 * @author brendan
 *
 */
public class FiltersPanel extends FlowPanel {
	
	private HighlightButton expandButton;
	private HorizontalPanel topPanel = new HorizontalPanel();
	private VerticalPanel filterContainer = new VerticalPanel();
	private List<FilterBox> activeFilters = new ArrayList<FilterBox>();
	private List<FilterListener> listeners = new ArrayList<FilterListener>();
	private SampleViewUI mainView;
	
	public FiltersPanel(SampleViewUI display) {
		this.mainView = display;
		initComponents();
	}
	
	/**
	 * Obtain a list of the current filters 
	 * @return
	 */
	public List<VariantFilter> getFilters() {
		List<VariantFilter> filters = new ArrayList<VariantFilter>();
		for(FilterBox box : activeFilters) {
			if (box.isEnabled())
				filters.add(box.getFilter());
		}
		
		return filters;
	}
	
	/**
	 * Obtain a user-readable description of all currently active filters. 
	 * @return
	 */
	public String getFilterUserText() {
		StringBuilder str = new StringBuilder("Variant filters in use: \n");
		for(VariantFilter filter : getFilters()) {
			String desc = filter.getUserDescription();
			str.append(desc + "\n");
		}
		return str.toString();
	}
	
	public void addFilter(String name, VariantFilter filter) {
		FilterBox filterBox = new FilterBox(this, name, filter);
		addFilter(filterBox);
	}

	public void addFilter(FilterBox filter) {
		activeFilters.add(filter);
		filterContainer.add(filter);
		fireFiltersChanged();
	}
	
	public void removeFilter(FilterBox filterBox) {
		boolean ok = Window.confirm("Remove filter?");
		if (ok) {
			filterContainer.remove(filterBox);
			activeFilters.remove(filterBox);
		}
		fireFiltersChanged();
	}
	
	/**
	 * Add a FilterListener to the list of objects to be notified when the filter list 
	 * is updated
	 * @param fl
	 */
	public void addListener(FilterListener fl) {
		listeners.add(fl);
	}
	
	/**
	 * Removes a FilterListener from the list of objects to be notified when the filter list 
	 * is updated
	 * @param fl
	 */
	public boolean removeListener(FilterListener fl) {
		return listeners.remove(fl);
	}

	/**
	 * Notify all listeners that the filters have been changed
	 */
	void fireFiltersChanged() {
		List<VariantFilter> filters = getFilters();
		for(FilterListener fl : listeners) {
			fl.filtersUpdated(filters);
		}
	}
	
	protected void collapse() {
		this.remove(topPanel);
		this.remove(filterContainer);
		this.add(expandButton);
		this.setStylePrimaryName("filterspanel-collapsed");
		if (mainView.getVariantDisplay() != null)
			mainView.getVariantDisplay().setFilterPanelWidth(36.0);
	}
	
	protected void expand() {
		this.remove(expandButton);
		this.add(topPanel);
		this.add(filterContainer);
		this.setStylePrimaryName("filterspanel");
		if (mainView.getVariantDisplay() != null)
			mainView.getVariantDisplay().setFilterPanelWidth(240.0);
	}
	
	/**
	 * Create UI components
	 */
	private void initComponents() {
		this.setStylePrimaryName("filterspanel");
		
		Image expandImage = new Image("images/expand32.png");
		expandButton = new HighlightButton(expandImage, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				expand();
			}
			
		});
		expandButton.setTitle("Show filters");
		expandButton.setWidth("34px");
		expandButton.setHeight("34px");
		
		Image backImage = new Image("images/backButton.png");
		HighlightButton backButton = new HighlightButton(backImage, "Back to sample list", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				mainView.showSampleChooser();
			}
		});
		
		topPanel.add(backButton);
		
		Image collapseImage = new Image("images/collapse32.png");
		HighlightButton collapseButton = new HighlightButton(collapseImage, "Collapse filters", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				collapse();				
			}
			
		});
		topPanel.add(collapseButton);
		this.add(topPanel);
		
	
		backButton.setWidth("130px");
		SimplePanel spacer1 = new SimplePanel();
		spacer1.setHeight("20px");
		filterContainer.add(spacer1);
		
		this.add(filterContainer);
		
		MaxFreqFilter freqFilter = new MaxFreqFilter();
		freqFilter.setMaxFreq(0.10);
		freqFilter.setArupMax(0.20);
		FilterBox freqFilterBox = new FilterBox(this, "Pop. frequency", freqFilter);
		PopFreqConfig freqConfig = new PopFreqConfig(freqFilterBox);
		freqFilterBox.setConfigTool(freqConfig);
		addFilter(freqFilterBox);
		
		ExonFuncFilter effectFilter = new ExonFuncFilter();
		FilterBox exonFilterBox = new FilterBox(this, "Exon effect", effectFilter);
		ExonFuncFilterConfig exonConfig = new ExonFuncFilterConfig(exonFilterBox);
		exonFilterBox.setConfigTool(exonConfig);
		addFilter(exonFilterBox);
		
		QualityDepthFilter qdFilter = new QualityDepthFilter();
		FilterBox qdFilterBox = new FilterBox(this, "Quality & Depth", qdFilter);
		QualDepthFilterConfig qualDepthConfig = new QualDepthFilterConfig(qdFilterBox);
		qdFilterBox.setConfigTool(qualDepthConfig);
		addFilter(qdFilterBox);

		
		DeleteriousFilter delFilter = new DeleteriousFilter();
		FilterBox deleteriousFilterBox = new FilterBox(this, "Deleterious Score", delFilter);
		deleteriousFilterBox.setInteriorText("No del. prediction filters set");
		DelFilterConfig delConfig = new DelFilterConfig(deleteriousFilterBox);
		deleteriousFilterBox.setConfigTool(delConfig);
		addFilter(deleteriousFilterBox);
		
		GeneFilter geneFilter = new GeneFilter();
		FilterBox geneFilterBox = new FilterBox(this, "Genes & Regions", geneFilter);
		geneFilterBox.setInteriorText("No gene filters set");
		GeneFilterConfig geneConfig = new GeneFilterConfig(geneFilterBox);
		geneFilterBox.setConfigTool(geneConfig);
		addFilter(geneFilterBox);
		
		HGMDOmimFilter diseaseFilter = new HGMDOmimFilter();
		FilterBox diseaseFilterBox = new FilterBox(this, "HGMD & OMIM", diseaseFilter);
		diseaseFilterBox.setInteriorText("No disease filters set");
		HGMDOmimFilterConfig disConfig = new HGMDOmimFilterConfig(diseaseFilterBox);
		diseaseFilterBox.setConfigTool(disConfig);
		addFilter(diseaseFilterBox);
	}

	protected void toggleFiltersDisabled() {
		
	}

}
