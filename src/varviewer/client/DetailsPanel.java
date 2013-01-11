package varviewer.client;

import varviewer.client.varTable.VariantSelectionListener;
import varviewer.shared.GeneInfo;
import varviewer.shared.Variant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class DetailsPanel extends FlowPanel implements VariantSelectionListener {

	private String currentGene = null;
	private DetailPanelHeader header = null;
	HTML omimDiseases = new HTML("<b>OMIM Disease:</b>");
	HTML hgmd = new HTML("<b>HGMD Variants:</b>");
	HTML summary = new HTML("<b>Summary:</b>");
	
	public DetailsPanel() {
		this.setStylePrimaryName("detailspanel");
		
		header = new DetailPanelHeader(this);
		this.add(header);
		
		this.add(hgmd);
		this.add(omimDiseases);
		this.add(summary);
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
		if (result.getHgmdDiseases() != null && result.getHgmdDiseases().length > 0) {
			hgmdStr = result.getHgmdDiseases()[0];
			for(int i=1; i<result.getHgmdDiseases().length; i++) {
				hgmdStr = hgmdStr + "; " + result.getHgmdDiseases()[i];
			}
		}
		hgmd.setHTML("<p><b>HGMD Variants:</b> " + hgmdStr + "</p>");
		
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
