package varviewer.client.filters;

import java.util.ArrayList;
import java.util.List;

import varviewer.shared.VariantFilter;
import varviewer.shared.varFilters.ExonFuncFilter;
import varviewer.shared.varFilters.GeneFilter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

public class GeneFilterConfig extends FilterConfig {
	
	private TextArea genesBox = new TextArea();
	private boolean first = true;
	private GeneFilter filter = null;
	
	public GeneFilterConfig(FilterBox parent) {
		super(parent);
		
		VariantFilter fil = parent.getFilter();
		if (fil instanceof GeneFilter) {
			this.filter = (GeneFilter)fil;
		}
		else {
			throw new IllegalArgumentException("Incorrect filter type in GeneFilterConfig");
		}
		
		FlowPanel panel = new FlowPanel();
		
		Label lab = new Label("Enter gene names to include");
		panel.add(lab);
		genesBox.setCharacterWidth(40);
		genesBox.setVisibleLines(6);
		genesBox.setText("e.g. FBN1, BRAF, KRAS");
		genesBox.setStylePrimaryName("textboxexample");
		genesBox.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (first) {
					genesBox.setText("");
					genesBox.setStylePrimaryName("textbox");
					first = false;
				}
			}
		});
		panel.add(genesBox);
		
		interiorPanel.add(panel);
		interiorPanel.setWidth("250px");
		interiorPanel.setHeight("200px");
	}

	@Override
	protected boolean validateAndUpdateFilter() {
		String[] names = genesBox.getText().split(",");
		List<String> trimmedNames = new ArrayList<String>();
		for(int i=0; i<names.length; i++) {
			String tName = names[i].trim().toUpperCase();
			if (tName.length()>1)
				trimmedNames.add(tName);
		}
		filter.setGeneNames(trimmedNames);
		if (trimmedNames.size()==0) {
			parentBox.setInteriorText("No gene filters set");
		}
		else {
			if (trimmedNames.size()==1) {
				parentBox.setInteriorText("Showing results for " + trimmedNames.get(0));
			}
			else {
				parentBox.setInteriorText("Showing results for " + trimmedNames.size() + " genes");
			}
		}
		return true;
	}

}
