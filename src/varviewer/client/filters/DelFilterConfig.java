package varviewer.client.filters;

import varviewer.shared.varFilters.DeleteriousFilter;
import varviewer.shared.variant.VariantFilter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class DelFilterConfig extends FilterConfig {
	
	private DeleteriousFilter filter;
	TextValueBox siftPanel = new TextValueBox("Max. SIFT Score (0-1):", 0.9, false);	
	TextValueBox ppPanel = new TextValueBox("Min. Polyphen Score (0-1):", 0.1, false);
	TextValueBox mtPanel = new TextValueBox("Min. Mut. Taster Score (0-1):", 0.1, false);
	TextValueBox gerpPanel = new TextValueBox("Min. GERP++ Score (-10-10)", 0.0, false);
	TextValueBox phyloPPanel = new TextValueBox("Min. PhyloP (-10-10)", 0.0, false);

	public DelFilterConfig(FilterBox parent) {
		super(parent);
		
		VariantFilter filter = parent.getFilter();
		if (filter instanceof DeleteriousFilter) {
			this.filter = (DeleteriousFilter)filter;
		}
		else {
			throw new IllegalArgumentException("Incorrect filter type given to DelFilterConfig tool");
		}
		
		
		FlowPanel panel = new FlowPanel();
		
		panel.add(siftPanel);
		panel.add(ppPanel);
		panel.add(mtPanel);
		panel.add(gerpPanel);
		panel.add(phyloPPanel);
		
		interiorPanel.add(panel);
		interiorPanel.setWidth("250px");
		interiorPanel.setHeight("240px");
		updateInteriorLabelText();
	}

	
	class TextValueBox extends HorizontalPanel {
		
		final TextBox textBox = new TextBox();
		final CheckBox checkBox = new CheckBox();
		
		public TextValueBox(String text, Double initVal, boolean enabled) {
			textBox.setText("" + initVal);
			checkBox.setValue(enabled);
			Label lab = new Label(text);
			lab.setStylePrimaryName("interiortext");
			this.add(lab);
			textBox.setWidth("40px");
			textBox.setStylePrimaryName("configtextbox");
			this.add(textBox);
			this.add(checkBox);
			if( checkBox.getValue()) {
				textBox.setEnabled(true);
			}
			else {
				textBox.setEnabled(false);
			}
			
			checkBox.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (checkBox.getValue()) {
						textBox.setEnabled(true);
					}
					else {
						textBox.setEnabled(false);
					}
					
				}
			});
		}
		
		public boolean getEnabled() {
			return checkBox.getValue();
		}
		
		public Double getDoubleValue() {
			if (! getEnabled()) {
				return null;
			}
			else {
				try {
					return Double.parseDouble(textBox.getText());
				}
				catch (NumberFormatException nfe) {
					//? 
				}
			}
			return null;
		}
	}
	
	
	@Override
	protected boolean validateAndUpdateFilter() {
		try {			
			
			if (this.siftPanel.getEnabled()) {
				this.filter.setSiftEnabled(true);
				this.filter.setSiftMax(siftPanel.getDoubleValue());
			}
			else {
				this.filter.setSiftEnabled(false);
			}
			
			if (ppPanel.getEnabled()) {
				filter.setPolyphenEnabled(true);
				filter.setPolyphenMin(ppPanel.getDoubleValue());
			}
			else {
				filter.setPolyphenEnabled(false);
			}
			
			if (mtPanel.getEnabled()) {
				filter.setMutationTasterEnabled(true);
				filter.setMutationTasterMin(mtPanel.getDoubleValue());
			}
			else {
				filter.setMutationTasterEnabled(false);
			}
			
			if (gerpPanel.getEnabled()) {
				filter.setGerpEnabled(true);
				filter.setGerpMin(gerpPanel.getDoubleValue());
			}
			else {
				filter.setGerpEnabled(false);
			}
			
			if (phyloPPanel.getEnabled()) {
				filter.setPhyloPEnabled(true);
				filter.setPhyloPMin(phyloPPanel.getDoubleValue());
			}
			else {
				filter.setPhyloPEnabled(false);
			}
			
			
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
		int sum = 0;
		if (siftPanel.getEnabled()) sum++;
		if (ppPanel.getEnabled()) sum++;
		if (mtPanel.getEnabled()) sum++;
		if (gerpPanel.getEnabled()) sum++;
		if (phyloPPanel.getEnabled()) sum++;
		
		if (sum == 0)
			parentBox.setInteriorText("No filters set");
		if (sum == 1) {
			parentBox.setInteriorText("Filtering on one score");
		}
		if (sum > 1) {
			parentBox.setInteriorText("Filtering on " + sum + " scores");
		}
	}
}
