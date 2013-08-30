package varviewer.client.varTable.cisTrans;

import java.util.Iterator;
import java.util.Set;

import varviewer.client.services.CisTransService;
import varviewer.client.services.CisTransServiceAsync;
import varviewer.client.varTable.VariantDisplay;
import varviewer.shared.bcrabl.CisTransRequest;
import varviewer.shared.bcrabl.CisTransResult;
import varviewer.shared.variant.Variant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CisTransPopup extends PopupPanel {

	private FlowPanel mainPanel = new FlowPanel();
	private VerticalPanel centerPanel = new VerticalPanel();
	private HorizontalPanel bottomPanel = new HorizontalPanel();
	private VariantDisplay varDisplay;
	 
	public CisTransPopup(VariantDisplay display) {
		super(false);
		this.varDisplay = display;
		initComponents();
	}

	public void refreshResults() {
		
		
		Set<Variant> selectedVars = varDisplay.getVarTable().getSelectedVariants();
		if (selectedVars.size() != 2) {
			Window.alert("Please select exactly two variants");
			return;
		}
		
		Iterator<Variant> vit = selectedVars.iterator();
		Variant varA = vit.next();
		Variant varB = vit.next();
		
		CisTransRequest req = new CisTransRequest();
		req.setVarA(varA);
		req.setVarB(varB);
		req.setSample(varDisplay.getSample());
		
		
		centerPanel.clear(); //Clear previous results, otherwise may be confusing...
		HTML waitLabel = new HTML("<h4>Computing cis/trans probabilities, please wait...</h4>");
		centerPanel.add(waitLabel);
		
		cisTransService.computeCisTrans(req, new AsyncCallback<CisTransResult>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failure! " + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(CisTransResult result) {
				displayResult(result);
			}
			
		});
	}
	
	protected void displayResult(CisTransResult result) {
		centerPanel.clear();
		centerPanel.add( makePanel("Total informative reads: ", "" + result.getCoverage() + " reads"));
		centerPanel.add( makePanel("Both refs: ", format(result.getBothRefs()) + "%"));
		centerPanel.add( makePanel("Alt 1 only: ", format(result.getAlt1Only()) + "%") );
		centerPanel.add( makePanel("Alt 2 only: ", format(result.getAlt2Only()) + "%") );
		centerPanel.add( makePanel("Both alts: ", format(result.getBothAlts()) + "%") );
		Panel panel = makePanel("Fraction in trans: ", format(result.getTransFrac()) + "%" );
		panel.getElement().getStyle().setMarginTop(10.0, Unit.PX);
		centerPanel.add( panel );
		centerPanel.add( makePanel("Fraction in cis: ", format(result.getCisFrac()) + "%") );
	}
	
	private static String format(Double x) {
		if (x == null || Double.isNaN(x)) {
			return "NA";
		}
		
		String str = "" + x;
		if (str.length() > 5) {
			str = str.substring(0, 5);
		}
		return str;
	}

	private HorizontalPanel makePanel(String label1, String label2) {
		HorizontalPanel panel = new HorizontalPanel();
		Label labA = new Label(label1);
		Label labB = new Label(label2);
		labA.setStylePrimaryName("bcrabl-itemA");
		labB.setStylePrimaryName("bcrabl-itemB");
		
		panel.add(labA);
		panel.add(labB);
		panel.setStylePrimaryName("bcrabl-interiorpanel");
		return panel;
	}
	
	
	private void initComponents() {
		this.add(mainPanel);
		this.setStylePrimaryName("genericpopup");
		Label header = new Label("Cis / trans inference");
		header.setStylePrimaryName("pedpopuptitle");
		mainPanel.add(header);
		
		mainPanel.add(centerPanel);
		centerPanel.setStylePrimaryName("bcrabl-centerpanel");
			

		Button doneButton = new Button("Done");
		doneButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				done();
			}
		});
		bottomPanel.getElement().getStyle().setMarginTop(10, Unit.PX);
		bottomPanel.add(doneButton);
		doneButton.setStylePrimaryName("centered");
		mainPanel.add(bottomPanel);
	}

	protected void done() {
		hidePopup();
	}

	protected void hidePopup() {
		this.hide();
	}
	
	CisTransServiceAsync cisTransService = (CisTransServiceAsync) GWT.create(CisTransService.class);

}
