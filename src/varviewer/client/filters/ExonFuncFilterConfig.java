package varviewer.client.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import varviewer.shared.VariantFilter;
import varviewer.shared.varFilters.ExonFuncFilter;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * VariantFilter config popup box allowing users to select what types of variants to include
 * and or exclude
 * @author brendan
 *
 */
public class ExonFuncFilterConfig extends FilterConfig {

	private Map<String, CheckBox> exonTypes = new HashMap<String, CheckBox>();
	private ExonFuncFilter filter;
	
	public ExonFuncFilterConfig(FilterBox parent) {
		super(parent);
		
		VariantFilter fil = parent.getFilter();
		if (fil instanceof ExonFuncFilter) {
			this.filter = (ExonFuncFilter)fil;
		}
		else {
			throw new IllegalArgumentException("Incorrect filter type in ExonFuncFilter");
		}
		
		VerticalPanel panel = new VerticalPanel();
		Label lab = new Label("Exclude variants by type:");
		panel.add(lab);
		
		
		panel.add( addType("Intergenic", true) );
		panel.add( addType("Intronic (and not splicing)", true) );
		panel.add( addType("UTR", true) );
		panel.add( addType("Synonymous", true) );
		panel.add( addType("Noncoding RNA", true) );
		panel.add( addType("Non-frameshifting indels", false) );
		panel.add( addType("Frameshifting indels",  false) );
		panel.add( addType("Splicing", false) );
		panel.add( addType("Missense", false) );
		
		updateInteriorLabelText(); //Set interior text in parental filterbox
		interiorPanel.add(panel);
		interiorPanel.setWidth("250px");
		interiorPanel.setHeight("250px");
	}

	private CheckBox addType(String userText, boolean checked) {
		CheckBox box = new CheckBox(userText);
		box.setValue(checked);
		exonTypes.put(userText, box);
		return box;
	}
	
	public void updateInteriorLabelText() {
		int count = 0;
		List<String> types = new ArrayList<String>();
		for(String key : exonTypes.keySet()) {
			CheckBox box = exonTypes.get(key);
			if (box.getValue()) {
				count++;
				types.add(key);
			}
		}
		
		if (count == 0) {
			parentBox.setInteriorText("Excluding NO variant types");
			return;
		}
		if (count > 4) {
			parentBox.setInteriorText("Excluding " + count + " variant types");
			return;
		}
		
		String str = "";
		for(int i=0; i<types.size()-1; i++) {
			str = str + types.get(i) + ", ";
		}
		str = str + " and " + types.get( types.size()-1);
		parentBox.setInteriorText("Excluding " + str + " variants");
	}
	
	@Override
	protected boolean validateAndUpdateFilter() {
		for(String key : exonTypes.keySet()) {
			CheckBox box = exonTypes.get(key);
			
			if (key.equals("Intergenic"))
				this.filter.setExcludeIntergenic( box.getValue() ); 
				
			if (key.equals("Intronic (and not splicing)")) 
				this.filter.setExcludeIntronic( box.getValue() ); 
				
			if (key.equals("UTR"))
				this.filter.setExcludeUTR( box.getValue() ); 
				
			if (key.equals( "Synonymous")) 
				this.filter.setExcludeSynonymous( box.getValue() ); 
				
			if (key.equals( "Non-frameshifting indels")) 
				this.filter.setExcludeNonFrameshift( box.getValue() ); 
				
			if (key.equals( "Splicing")) 
				this.filter.setExcludeSplicing( box.getValue() ); 
				
			if (key.equals( "Missense")) 
				this.filter.setExcludeNonsynonymous( box.getValue() ); 
				
			if (key.equals( "Noncoding RNA")) 
				this.filter.setExcludeNCRNA( box.getValue() ); 
				
		}
		
		updateInteriorLabelText();
		return true;
	}

}
