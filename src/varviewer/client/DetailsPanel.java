package varviewer.client;

import varviewer.client.varTable.VariantSelectionListener;
import varviewer.shared.GeneInfo;
import varviewer.shared.Variant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;

public class DetailsPanel extends FlowPanel implements VariantSelectionListener {

	private String currentGene = null;
	private DetailPanelHeader header = null;
	private ScrollPanel scrollPanel;
	HTML omimDiseases = new HTML("<b>OMIM Disease:</b>");
	HTML hgmd = new HTML("<b>HGMD Variants:</b>");
	HTML summary = new HTML("<b>Summary:</b>");
	
	public DetailsPanel() {
		this.setStylePrimaryName("detailspanel");
		
		header = new DetailPanelHeader(this);
		this.add(header);
		
		FlowPanel insidePanel= new FlowPanel();
		insidePanel.add(summary);
		insidePanel.add(omimDiseases);
		insidePanel.add(hgmd);
		
		scrollPanel = new ScrollPanel(insidePanel);
		scrollPanel.setAlwaysShowScrollBars(false);
		scrollPanel.setWidth("100%");
		scrollPanel.setHeight("173px");
		this.add(scrollPanel);
		
	}

	@Override
	public void variantSelected(Variant selectedVar) {
		if (selectedVar == null) {
			selectGene(null);
		}
		
		String geneName = selectedVar.getAnnotation("gene");
		selectGene(geneName);		
	}

	private void selectGene(String geneName) {
		if (currentGene != geneName) {
			currentGene = geneName;
			
			geneDetailService.getDetails(currentGene, new AsyncCallback<GeneInfo>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Unable to find gene details for gene : " + currentGene);
				}

				@Override
				public void onSuccess(GeneInfo result) {
					displayGeneInfo(result);
				}
				
			});
		}
	}

	protected void displayGeneInfo(GeneInfo result) {
		header.updateLabel();
		
		String hgmdStr = "None found";
		if (result.getHgmdVars() != null && result.getHgmdVars().length > 0 && result.getHgmdVars()[0].length()>3) {
			hgmdStr = "<li>" + result.getHgmdVars()[0] + "</li>";
			for(int i=1; i<result.getHgmdVars().length; i++) {
				String[] bits = result.getHgmdVars()[i].split(",");
				hgmdStr = hgmdStr + " <li>" + bits[1] + " : " + bits[0] + ", " + bits[2] + "</li>";
			}
			hgmd.setHTML("<b>HGMD Hits:</b><ul id=\"hgmdlist\">" + hgmdStr + "</ul>");
		}
		else {
			hgmd.setHTML("<b>HGMD Hits:</b> None found");
		}
		
		
		String omimStr = "None found";
		if (result.getDbNSFPDisease() != null) {
			omimStr = result.getDbNSFPDisease();
		}
		
		omimDiseases.setHTML("<p><b>OMIM Diseases:</b> " + omimStr +"</p>" );
		String summaryStr = "None";
		if (result.getSummary() != null)
			summaryStr = result.getSummary();
		
		summary.setHTML("<p><b>Summary:</b> " + summaryStr + "</p>");
	}

	/**
	 * Get the gene name of the currently displayed gene
	 * @return
	 */
	public String getCurrentGene() {
		return currentGene;
	}
	
	GeneDetailServiceAsync geneDetailService = (GeneDetailServiceAsync) GWT.create(GeneDetailService.class);

}
