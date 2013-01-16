package varviewer.client.filters;

import java.util.ArrayList;
import java.util.List;

import varviewer.client.HighlightButton;
import varviewer.client.VarListManager;
import varviewer.shared.VariantFilter;
import varviewer.shared.varFilters.ExonFuncFilter;
import varviewer.shared.varFilters.MaxFreqFilter;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
		
		HorizontalPanel topPanel = new HorizontalPanel();
		topPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		this.add(topPanel);
		
		Image addImage = new Image("images/add-icon.png");
		SimplePanel spacer1 = new SimplePanel();
		spacer1.setWidth("80px");
		topPanel.add(spacer1);
		HighlightButton addFilterButton = new HighlightButton("Add new filter", addImage);
		addFilterButton.getElement().getStyle().setPadding(2.0, Unit.PX);
		
		topPanel.add(addFilterButton);
		
		VariantFilter freqFilter = new MaxFreqFilter("pop.freq", 0.10);
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
	}

}
