package varviewer.client.varTable;

import java.util.ArrayList;
import java.util.List;

import varviewer.client.services.GeneDetailService;
import varviewer.client.services.GeneDetailServiceAsync;
import varviewer.shared.GeneInfo;
import varviewer.shared.Variant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DetailsPanel extends ScrollPanel implements VariantSelectionListener {

	private String currentGene = null;
	private DetailPanelHeader header = null;
	private VerticalPanel mainPanel = new VerticalPanel();
	private HorizontalPanel summaryOmimPanel = new HorizontalPanel();
	private HTML omimDiseases = new HTML("<b>OMIM Disease: </b>None");
	private HTML hgmd = new HTML("<b>HGMD Variants: </b>None");
	private HTML omimInheritance = new HTML("<b>Inheritance pattern: </b>None");
	private HTML omimPhenotypes = new HTML("<b>Phenotypes: </b>None");
	private HTML summary = new HTML("<b>Summary: </b>None");
	
	public DetailsPanel() {
		this.setStylePrimaryName("detailspanel");
		this.add(mainPanel);
		summaryOmimPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		header = new DetailPanelHeader(this);
		mainPanel.add(header);
		
		summaryOmimPanel.setWidth("100%");
		FlowPanel leftSide = new FlowPanel();
		leftSide.setStylePrimaryName("detailstext");
		
		FlowPanel rightSide = new FlowPanel();
		
		leftSide.setWidth("450px");
		rightSide.setWidth("450px");
		
		rightSide.setStylePrimaryName("detailstext");
		summaryOmimPanel.add(leftSide);
		summaryOmimPanel.add(rightSide);
		leftSide.add(summary);
		leftSide.add(hgmd);
		
		rightSide.add(omimDiseases);
		rightSide.add(omimInheritance);
		rightSide.add(omimPhenotypes);
		
		mainPanel.add(summaryOmimPanel);
	}

	@Override
	public void variantSelected(Variant selectedVar) {
		if (selectedVar == null) {
			selectGene(null);
		}
		
		String geneName = selectedVar.getAnnotationStr("gene");
		selectGene(geneName);		
	}

	private void selectGene(String geneName) {
		if (currentGene != geneName) {
			currentGene = geneName;
			
			geneDetailService.getDetails(currentGene, new AsyncCallback<GeneInfo>() {

				@Override
				public void onFailure(Throwable caught) {
					//Window.alert("Unable to find gene details for gene : " + currentGene + " Reason: " + caught.getMessage());
				}

				@Override
				public void onSuccess(GeneInfo result) {
					displayGeneInfo(result);
				}
				
			});
		}
	}

	protected void displayGeneInfo(GeneInfo result) {
		String fullName = result.getFullName();
		if (fullName != null) {
			header.updateLabel(getCurrentGene() + " : " + result.getFullName());
		}
		else {
			header.updateLabel(getCurrentGene());
		}
		
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
		
		
		if (result.getOmimPhenos() != null && result.getOmimPhenos().length > 0 && result.getOmimPhenos()[0].length()>3) {
			StringBuilder phenoStr = new StringBuilder("<b>Phenotypes:</b><ul>");
			String[] allPhenos = result.getOmimPhenos();
			//Uniqify phenos
			List<String> phenoSet = new ArrayList<String>();
			for(int i=0; i<allPhenos.length; i++) {
				if (! phenoSet.contains(allPhenos[i]) && (! allPhenos[i].startsWith("[")))
					phenoSet.add(allPhenos[i]);
			}
			
			
			for(int i=0; i<phenoSet.size(); i++) {
				phenoStr.append("<li>" + phenoSet.get(i) +"</li>");
			}
			phenoStr.append("</ul>");
			omimPhenotypes.setHTML(phenoStr.toString());
		}
		else {
			omimPhenotypes.setHTML("<b>Phenotypes: </b> None found");
		}
		
		if (result.getOmimInheritance() != null && result.getOmimInheritance().length > 0 && result.getOmimInheritance()[0].length()>3) {
			StringBuilder inheritStr = new StringBuilder("<b>Inheritance:  </b>");
			inheritStr.append( result.getOmimInheritance()[0] );
			for(int i=1; i<result.getOmimInheritance().length; i++) {
				inheritStr.append(", " + result.getOmimInheritance()[i] );	
			}
			
			omimInheritance.setHTML(inheritStr.toString());
		}
		else {
			omimInheritance.setHTML("<b>Inheritance:</b> None found");
		}
		
		
		String omimStr = "None found";
		if (result.getDbNSFPDisease() != null && result.getDbNSFPDisease().length()>3) {
			omimStr = result.getDbNSFPDisease();
		}
		
		omimDiseases.setHTML("<p><b>OMIM Diseases:</b> " + omimStr +"</p>" );
		
		
		String summaryStr = "None found";
		if (result.getSummary() != null) {
			summaryStr = result.getSummary();
		}
		if (summaryStr.equals("null")) {
			summaryStr = "None found";
		}
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
