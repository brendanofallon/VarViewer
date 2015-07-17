package varviewer.client.varTable.filters;

import varviewer.shared.varFilters.MaxFreqFilter;
import varviewer.shared.variant.VariantFilter;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PopFreqConfig extends FilterConfig {

	private TextBox freqBox = new TextBox();
	private TextBox arupBox = new TextBox();
	private TextBox exomesFreqBox = new TextBox();
	private TextBox exomesHomFreqBox = new TextBox();
	private TextBox exacHomCountBox = new TextBox();
	
	//private TextBox varBinBox = new TextBox();
	
	private MaxFreqFilter mfFilter;
	
	public PopFreqConfig(FilterBox parent) {
		super(parent);
		
		VariantFilter filter = parent.getFilter();
		if (filter instanceof MaxFreqFilter) {
			this.mfFilter = (MaxFreqFilter)filter;
		}
		else {
			throw new IllegalArgumentException("Incorrect filter type given to PopFreqConfig tool");
		}
		
		
		VerticalPanel panel = new VerticalPanel();
		HorizontalPanel freqPanel = new HorizontalPanel();
		Label lab = new Label("Exclude all variants with minor allele frequency (MAF) > ");
		lab.setStylePrimaryName("interiortext");
		lab.getElement().getStyle().setMarginRight(10, Unit.PX);
		freqPanel.add(lab);
		
		freqBox.setText("" + mfFilter.getMaxFreq());
		freqBox.setWidth("50px");
		freqPanel.add(freqBox);
		panel.add(freqPanel);
		
		//spacer
		SimplePanel spacer = new SimplePanel();
		spacer.setHeight("10px");
		panel.add(spacer);
		
		HorizontalPanel arupPanel = new HorizontalPanel();
		Label lab3 = new Label("Exclude variants with ARUP frequency greater than");
		lab3.setStylePrimaryName("interiortext");
		lab3.getElement().getStyle().setMarginRight(10, Unit.PX);
		arupPanel.add(lab3);
		arupBox.setText("" + mfFilter.getArupMax());
		arupBox.setWidth("50px");
		arupPanel.add(arupBox);
		panel.add(arupPanel);
		
		SimplePanel spacer2 = new SimplePanel();
		spacer2.setHeight("10px");
		panel.add(spacer2);
		
		HorizontalPanel exomesPanel = new HorizontalPanel();
		Label lab4 = new Label("Exclude ESP Exomes frequency greater than");
		lab4.setStylePrimaryName("interiortext");
		lab4.getElement().getStyle().setMarginRight(10, Unit.PX);
		exomesPanel.add(lab4);
		exomesFreqBox.setText("" + mfFilter.getExomesMax());
		exomesFreqBox.setWidth("50px");
		exomesPanel.add(exomesFreqBox);
		panel.add(exomesPanel);
		
		SimplePanel spacer3 = new SimplePanel();
		spacer3.setHeight("10px");
		panel.add(spacer3);
		
		HorizontalPanel exomesHomPanel = new HorizontalPanel();
		Label lab5 = new Label("Exclude ESP Exomes HOMOZYGOUS frequency greater than");
		lab5.setStylePrimaryName("interiortext");
		lab5.getElement().getStyle().setMarginRight(10, Unit.PX);
		exomesHomPanel.add(lab5);
		exomesHomFreqBox.setText("" + mfFilter.getExomesHomMax());
		exomesHomFreqBox.setWidth("50px");
		exomesHomPanel.add(exomesHomFreqBox);
		panel.add(exomesHomPanel);
		
		
		SimplePanel spacer4 = new SimplePanel();
		spacer4.setHeight("10px");
		panel.add(spacer4);
		
		HorizontalPanel exACHomPanel = new HorizontalPanel();
		Label lab6 = new Label("Exclude ExAC homozygous count greater than");
		lab6.setStylePrimaryName("interiortext");
		lab6.getElement().getStyle().setMarginRight(10, Unit.PX);
		exACHomPanel.add(lab6);
		exacHomCountBox.setText("" + mfFilter.getExacHomCountMax());
		exacHomCountBox.setWidth("50px");
		exACHomPanel.add(exacHomCountBox);
		panel.add(exACHomPanel);
		
//		Label lab4 = new Label("Exclude with VarBin greater than (1-4)");
//		panel.add(lab4);
//		varBinBox.setText("3");
//		varBinBox.setWidth("50px");
//		panel.add(varBinBox);
		
		interiorPanel.add(panel);
		interiorPanel.setWidth("250px");
		interiorPanel.setHeight("300px");
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
			
			
			Double arupFreq = Double.parseDouble( arupBox.getText() );
			if (arupFreq < 0.0 || arupFreq > 1.0) {
				Window.alert("Please enter a number between 0 and 1.0");
				return false;
			}
			//Integer varBinMin = Integer.parseInt( varBinBox.getText() );
			
			Double exomesFreq = Double.parseDouble( exomesFreqBox.getText() );
			if (exomesFreq < 0.0 || exomesFreq > 100.0) {
				Window.alert("Please enter a number between 0 and 100 for exomes frequency");
				return false;
			}
			
			Double exomesHomFreq = Double.parseDouble( exomesHomFreqBox.getText() );
			if (exomesHomFreq < 0.0 || exomesHomFreq > 1.0) {
				Window.alert("Please enter a number between 0 and 1.0 for exomes homozygous frequency");
				return false;
			}
			
			Double exacHomCount = Double.parseDouble(exacHomCountBox.getText());
			if (exomesHomFreq < 0.0) {
				Window.alert("Please enter a number greater than 0 for the ExAC homozygous count");
				return false;
			}
			
			
			this.mfFilter.setArupMax(arupFreq);
			this.mfFilter.setExomesMax(exomesFreq);
			this.mfFilter.setExomesHomMax(exomesHomFreq);
			this.mfFilter.setMaxFreq(freq);
			this.mfFilter.setExACHomCount(exacHomCount);
			//this.filter.setVarBinMin(varBinMin);
			
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
		parentBox.setInteriorText("Pop. freq. > " + this.mfFilter.getMaxFreq() + ", ARUP freq. > " + mfFilter.getArupMax() /* + ", VarBin > " + filter.getVarBinMin() */);
	}

}
