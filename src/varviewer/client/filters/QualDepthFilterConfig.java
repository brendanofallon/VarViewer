package varviewer.client.filters;

import varviewer.shared.varFilters.QualityDepthFilter;
import varviewer.shared.variant.VariantFilter;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class QualDepthFilterConfig extends FilterConfig {

	private QualityDepthFilter filter;
	private TextBox depthBox = new TextBox();
	private TextBox varFreqBox = new TextBox();
	private TextBox qualBox = new TextBox();
	
	public QualDepthFilterConfig(FilterBox parent) {
		super(parent);
		
		VariantFilter vf = parent.getFilter();
		if (! (vf instanceof QualityDepthFilter)) {
			throw new IllegalArgumentException("Wrong filter type to qual depth filter config");
		}
		this.filter = (QualityDepthFilter)vf;
		
		VerticalPanel panel = new VerticalPanel();
		SimplePanel spacer = new SimplePanel();
		spacer.setHeight("20px");
		panel.add(spacer);
		HorizontalPanel qualPanel = new HorizontalPanel();
		Label labA = new Label("Minimum quality");
		labA.setStylePrimaryName("interiortext");
		qualPanel.add(labA);
		qualBox.setStylePrimaryName("configtextbox");
		qualBox.setText("" + filter.getMinQuality());
		qualPanel.add(qualBox);
		panel.add(qualPanel);
		
		
		HorizontalPanel depthPanel = new HorizontalPanel();
		Label labB = new Label("Minimum depth");
		labB.setStylePrimaryName("interiortext");
		depthPanel.add(labB);
		depthBox.setStylePrimaryName("configtextbox");
		depthBox.setText("" + filter.getMinDepth());
		depthPanel.add(depthBox);
		panel.add(depthPanel);
		
		HorizontalPanel varFreqPanel = new HorizontalPanel();
		Label labC = new Label("Min. variant fraction");
		labC.setStylePrimaryName("interiortext");
		varFreqPanel.add(labC);
		
		varFreqBox.setStylePrimaryName("configtextbox");
		varFreqBox.setWidth("40px");
		varFreqBox.setText("" + filter.getMinVarFreq());
		varFreqPanel.add(varFreqBox);
		panel.add(varFreqPanel);
		
		
		updateInteriorLabelText(); //Set interior text in parental filterbox
		interiorPanel.add(panel);
		interiorPanel.setWidth("250px");
		interiorPanel.setHeight("200px");
		
	}

	public void updateInteriorLabelText() {
		parentBox.setInteriorText("Quality: " + filter.getMinQuality() + "  Depth: " + filter.getMinDepth() + "  Var. freq: " + filter.getMinVarFreq());
	}

	@Override
	protected boolean validateAndUpdateFilter() {
		try {
			Double qual = Double.parseDouble( qualBox.getText() );
			filter.setMinQuality(qual);
		}
		catch (NumberFormatException nfe) {
			Window.alert("Please enter a valid number for minimum quality");
			updateInteriorLabelText();
			return false;
		}
		
		try {
			double depth = Double.parseDouble( depthBox.getText() );
			filter.setMinDepth((int)depth);
		}
		catch (NumberFormatException nfe) {
			Window.alert("Please enter a valid number for minimum depth");
			updateInteriorLabelText();
			return false;
		}
		
		try {
			Double frac = Double.parseDouble( varFreqBox.getText() );
			filter.setMinVarFreq(frac);
		}
		catch (NumberFormatException nfe) {
			Window.alert("Please enter a valid number for minimum var. freq.");
			updateInteriorLabelText();
			return false;
		}
		
		updateInteriorLabelText();
		return true;
	}


}
