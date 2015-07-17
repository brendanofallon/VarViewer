package varviewer.client.varTable.filters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import varviewer.shared.varFilters.HGMDOmimFilter;
import varviewer.shared.variant.VariantFilter;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HGMDOmimFilterConfig extends FilterConfig {

	private HGMDOmimFilter filter = null;
	private Map<String, CheckBox> filterTypes = new HashMap<String, CheckBox>();

	
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

		
		
		panel.add( addType("ClinVar Pathogenic", true) );
		panel.add( addType("ClinVar Likely Pathogenic", true) );
		panel.add( addType("ClinVar VUS & Other", true) );
		panel.add( addType("ClinVar Likely Benign", true) );
		panel.add( addType("ClinVar Benign", true) );
		panel.add( addType("HGMD Exact Match", true) );
		panel.add( addType("HGMD & OMIM Gene Match", true) );

		
		interiorPanel.add(panel);
		interiorPanel.setHeight("250px");
		interiorPanel.setWidth("250px");
		
	}
	
	private CheckBox addType(String userText, boolean checked) {
		CheckBox box = new CheckBox(userText);
		box.setValue(checked);
		filterTypes.put(userText, box);
		return box;
	}

	@Override
	protected boolean validateAndUpdateFilter() {
		for(String key : filterTypes.keySet()) {
			CheckBox box = filterTypes.get(key);
			
			if (key.equals("ClinVar Pathogenic")) {
				filter.setIncludeClinvarPathogenic( box.getValue() );
			}
			if (key.equals("ClinVar Likely Pathogenic")) {
				filter.setIncludeClinvarLikelyPathogenic( box.getValue() );
			}
			if (key.equals("ClinVar VUS & Other")) {
				filter.setIncludeClinvarVUS( box.getValue() );
			}
			if (key.equals("ClinVar Likely Benign")) {
				filter.setIncludeClinvarLikelyBenign( box.getValue() );
			}
			if (key.equals("ClinVar Benign")) {
				filter.setIncludeClinvarBenign( box.getValue() );
			}
			
			if (key.equals("HGMD Exact Match")) {
				filter.setExcludeNonExactHits( !box.getValue() );
			}
			
			if (key.equals("HGMD & OMIM Gene Match")) {
				filter.setExcludeNonGeneHits( !box.getValue() );
			}
			
		}
		updateInteriorLabelText();
		return true;
	}

	@Override
	public void updateInteriorLabelText() {
//		List<String> includes = new ArrayList<String>();
//		if (filter.isIncludeClinvarPathogenic()) {
//			includes.add("Clinvar pathogenic");
//		}
//		if (filter.isIncludeClinvarLikelyPathogenic()) {
//			includes.add("Clinvar likely pathogenic");
//		}
//		if (filter.isIncludeClinvarVUS()) {
//			includes.add("Clinvar VUS");
//		}
//		if (filter.isIncludeClinvarLikelyBenign()) {
//			includes.add("Clinvar likely benign");
//		}
//		if (filter.isIncludeClinvarBenign()) {
//			includes.add("Clinvar benign");
//		}
//		
//		boolean exact = filter.isExcludeNonExactHits();
//		boolean gene = filter.isExcludeNonGeneHits();
//		if (exact) {
//			includes.add("HGMD exact hits");
//		}
//		if (gene) {
//			includes.add("HGMD & OMIM gene hits");
//		}
//		
//		if (includes.size()==0) {
//			parentBox.setInteriorText("No disease filters set");
//			return;
//		}
//		
//		parentBox.setInteriorText("Including " + join(includes));
	}
	
	private static String join(List<String> strs) {
		String result = "";
		if (strs.size()==0) {
			return result;
		}
		for(String str : strs) {
			result = result + str + ", ";
		}
		return result.substring(0, result.length()-2);
	}
}
