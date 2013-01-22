package varviewer.client.filters;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import varviewer.shared.VariantFilter;
import varviewer.shared.varFilters.HGMDOmimFilter;

public class HGMDOmimFilterConfig extends FilterConfig {

	private HGMDOmimFilter filter = null;
	private final RadioButton noFilterBox;
	private final RadioButton hgmdExactBox;
	private final RadioButton geneMatchBox;
	
	public HGMDOmimFilterConfig(FilterBox parent) {
		super(parent);
		
		VariantFilter fil = parent.getFilter();
		if (fil instanceof HGMDOmimFilter) {
			this.filter = (HGMDOmimFilter)fil;
		}
		else {
			throw new IllegalArgumentException("Incorrect filter type in HGMDOmimFilterConfig");
		}
		
		VerticalPanel panel = new VerticalPanel();
		
		noFilterBox = new RadioButton("rbuttons", "Do not filter");
		panel.add(noFilterBox);
				
		hgmdExactBox = new RadioButton("rbuttons", "Show exact (variant) matches only");
		panel.add(hgmdExactBox);
		
		geneMatchBox = new RadioButton("rbuttons", "Show HGMD & OMIM gene matches");
		panel.add(geneMatchBox);
		
		//Initial state is no filtering
		noFilterBox.setValue(true);
		
		interiorPanel.add(panel);
		interiorPanel.setWidth("250px");
		interiorPanel.setHeight("200px");
	}

	@Override
	protected boolean validateAndUpdateFilter() {
		if (noFilterBox.getValue()) {
			filter.setExcludeNonExactHits(false);
			filter.setExcludeNonGeneHits(false);
			parentBox.setInteriorText("No disease filters set");
		}
		if (hgmdExactBox.getValue()) {
			filter.setExcludeNonExactHits(true);
			filter.setExcludeNonGeneHits(false);
			parentBox.setInteriorText("Exact variant hits only");
		}
		if (geneMatchBox.getValue()) {
			filter.setExcludeNonExactHits(false);
			filter.setExcludeNonGeneHits(true);
			parentBox.setInteriorText("HGMD & OMIM gene hits only");
		}
		return true;
	}

}
