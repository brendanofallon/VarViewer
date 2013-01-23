package varviewer.client.filters;

import java.util.ArrayList;
import java.util.List;

import varviewer.client.HighlightButton;
import varviewer.client.VarListManager;
import varviewer.client.VarViewer;
import varviewer.shared.VariantFilter;
import varviewer.shared.varFilters.ExonFuncFilter;
import varviewer.shared.varFilters.GeneFilter;
import varviewer.shared.varFilters.HGMDOmimFilter;
import varviewer.shared.varFilters.MaxFreqFilter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * The filters panel displays a succession of VariantFilters, whose visual representation
 * is a FilterBox. This object manages the list of currently active filters, and fires
 * events to FilterListeners when the list of filters has been changed. 
 * @author brendan
 *
 */
public class FiltersPanel extends FlowPanel {
	
	private List<FilterBox> activeFilters = new ArrayList<FilterBox>();
	private List<FilterListener> listeners = new ArrayList<FilterListener>();
	
	public FiltersPanel() {
		initComponents();
	}
	
	/**
	 * Obtain a list of the current filters 
	 * @return
	 */
	public List<VariantFilter> getFilters() {
		List<VariantFilter> filters = new ArrayList<VariantFilter>();
		for(FilterBox box : activeFilters) {
			filters.add(box.getFilter());
		}
		
		return filters;
	}
	
	public void addFilter(String name, VariantFilter filter) {
		FilterBox filterBox = new FilterBox(this, name, filter);
		addFilter(filterBox);
	}

	public void addFilter(FilterBox filter) {
		activeFilters.add(filter);
		this.add(filter);
		fireFiltersChanged();
	}
	
	public void removeFilter(FilterBox filterBox) {
		boolean ok = Window.confirm("Remove filter?");
		if (ok) {
			this.remove(filterBox);
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
		VarListManager.getManager().reloadIfRequired();
	}
	
	/**
	 * Create UI components
	 */
	private void initComponents() {
		this.setStylePrimaryName("filterspanel");
		
		
		Image backImage = new Image("images/backButton.png");
		HighlightButton backButton = new HighlightButton(backImage, "Back to sample list", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				VarViewer.getViewer().showSampleViewer();
			}
		});
		
		this.add(backButton);
		backButton.setWidth("100px");
		SimplePanel spacer1 = new SimplePanel();
		spacer1.setHeight("20px");
		this.add(spacer1);
		
		MaxFreqFilter freqFilter = new MaxFreqFilter();
		freqFilter.setMaxFreq(0.10);
		freqFilter.setArupMax(30);
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

}
