package varviewer.client.varTable.filters;

import varviewer.shared.varFilters.MaxFreqFilter;
import varviewer.shared.variant.VariantFilter;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class PopFreqConfig extends FilterConfig {

	private TextBox freqBox = new TextBox();
	private TextBox arupBox = new TextBox();
	private TextBox varBinBox = new TextBox();
	private MaxFreqFilter filter;
	
	public PopFreqConfig(FilterBox parent) {
		super(parent);
		
		VariantFilter filter = parent.getFilter();
		if (filter instanceof MaxFreqFilter) {
			this.filter = (MaxFreqFilter)filter;
		}
		else {
			throw new IllegalArgumentException("Incorrect filter type given to PopFreqConfig tool");
		}
		
		
		FlowPanel panel = new FlowPanel();
		Label lab = new Label("Exclude all variants with population frequency greater than ");
		panel.add(lab);
		
		freqBox.setText("0.10");
		freqBox.setWidth("50px");
		panel.add(freqBox);
		
		HTML lab2 = new HTML("(<em> range 0.0-1.0 </em>)");
		panel.add(lab2);
		
		Label lab3 = new Label("Exclude variants seen at ARUP more than");
		panel.add(lab3);
		arupBox.setText("50");
		arupBox.setWidth("50px");
		panel.add(arupBox);
		
		Label lab4 = new Label("Exclude with VarBin greater than (1-4)");
		panel.add(lab4);
		varBinBox.setText("3");
		varBinBox.setWidth("50px");
		panel.add(varBinBox);
		
		interiorPanel.add(panel);
		interiorPanel.setWidth("250px");
		interiorPanel.setHeight("240px");
		updateInteriorLabelText();
	}

	@Override
	protected boolean validateAndUpdateFilter() {
		try {
			Double freq = Double.parseDouble( freqBox.getText() );
			if (freq < 0.0 || freq > 1.0) {
				Window.alert("Please enter a number between 0 and 1.0");
				return false;
			}
			
			
			Integer arupTot = Integer.parseInt( arupBox.getText() );
			Integer varBinMin = Integer.parseInt( varBinBox.getText() );
			
			
			this.filter.setArupMax(arupTot);
			this.filter.setMaxFreq(freq);
			this.filter.setVarBinMin(varBinMin);
			
			
			
			updateInteriorLabelText();
			return true;
		}
		catch (NumberFormatException nfe) {
			Window.alert("Please enter a number between 0 and 1.0");
			return false;
		}
		
	}

	@Override
	public void updateInteriorLabelText() {
		parentBox.setInteriorText("Exclude pop. freq. > " + this.filter.getMaxFreq() + ", ARUP > " + filter.getArupMax() + ", VarBin > " + filter.getVarBinMin());
	}

}
