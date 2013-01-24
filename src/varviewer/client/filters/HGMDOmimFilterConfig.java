package varviewer.client.filters;

import varviewer.shared.VariantFilter;
import varviewer.shared.varFilters.HGMDOmimFilter;

import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

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
			
		}
		if (hgmdExactBox.getValue()) {
			filter.setExcludeNonExactHits(true);
			filter.setExcludeNonGeneHits(false);
			
		}
		if (geneMatchBox.getValue()) {
			filter.setExcludeNonExactHits(false);
			filter.setExcludeNonGeneHits(true);
		}
		
		updateInteriorLabelText();
		return true;
	}

	@Override
	public void updateInteriorLabelText() {
		boolean exact = filter.isExcludeNonExactHits();
		boolean gene = filter.isExcludeNonGeneHits();
		if ((!exact) && (!gene)) {
			parentBox.setInteriorText("No disease filters set");
		}
		if (exact && (!gene)) {
			parentBox.setInteriorText("Exact variant matches only");
		}
		if ((!exact) && gene) {
			parentBox.setInteriorText("HGMD & OMIM gene hits only");
		}
	}
}
