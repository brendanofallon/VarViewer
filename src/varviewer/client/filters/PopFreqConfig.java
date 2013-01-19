package varviewer.client.filters;

import varviewer.shared.VariantFilter;
import varviewer.shared.varFilters.MaxFreqFilter;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class PopFreqConfig extends FilterConfig {

	private TextBox freqBox = new TextBox();
	private MaxFreqFilter filter;
	
	public PopFreqConfig(FilterBox parent) {
		super(parent);
		
		FlowPanel panel = new FlowPanel();
		Label lab = new Label("Exclude all variants with population frequency greater than ");
		panel.add(lab);
		
		VariantFilter filter = parent.getFilter();
		if (filter instanceof MaxFreqFilter) {
			this.filter = (MaxFreqFilter)filter;
		}
		else {
			throw new IllegalArgumentException("Incorrect filter type given to PopFreqConfig tool");
		}
		parentBox.setInteriorText("Exclude variants with freq.  > " + this.filter.getMaxValue() );
		freqBox.setText("0.10");
		freqBox.setWidth("50px");
		panel.add(freqBox);
		
		HTML lab2 = new HTML("(<em> range 0.0-1.0 </em>)");
		panel.add(lab2);
		
		interiorPanel.add(panel);
		interiorPanel.setWidth("250px");
		interiorPanel.setHeight("200px");
	}

	@Override
	protected boolean validateAndUpdateFilter() {
		try {
			Double freq = Double.parseDouble( freqBox.getText() );
			if (freq < 0.0 || freq > 1.0) {
				Window.alert("Please enter a number between 0 and 1.0");
				return false;
			}
			this.filter.setMaxValue(freq);
			parentBox.setInteriorText("Exclude variants with freq. > " + this.filter.getMaxValue());
			return true;
		}
		catch (NumberFormatException nfe) {
			Window.alert("Please enter a number between 0 and 1.0");
			return false;
		}
		
	}

}
