package varviewer.client.varTable;

import java.util.ArrayList;
import java.util.List;

import varviewer.client.VarListListener;
import varviewer.client.filters.FilterListener;
import varviewer.client.filters.FiltersPanel;
import varviewer.client.varTable.pedigree.PedigreeVarAnnotation;
import varviewer.shared.Variant;
import varviewer.shared.VariantFilter;

import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * A simple container for a filters panel, VarTable, and Details panel..
 * @author brendanofallon
 *
 */
public class VariantDisplay extends SplitLayoutPanel implements VarListListener, ColumnModelListener, FilterListener {

	public VariantDisplay() {
		super(4); //Splitters slightly smaller than usual
		initComponents();
	}
	
	/**
	 * Set the bam link text that will be displayed in the BAM link button in the header of the vartable
	 * @param linkText
	 */
	public void setBamLink(String linkText) {
		varTable.getHeader().setBAMLink(linkText);
	}
	
	/**
	 * Return a list of the currently used filters
	 * @return
	 */
	public List<VariantFilter> getActiveFilters() {
		return filtersPanel.getFilters();
	}

	public String getFilterUserText() {
		return filtersPanel.getFilterUserText();
	}
	
	private void initComponents() {
		//TODO : These should be obtained from a factory or something, not instantiated here
		filtersPanel = new FiltersPanel();
		detailsPanel = new DetailsPanel();
		
		this.setStylePrimaryName("variantdisplay");
		this.addWest(filtersPanel, 240);

		varTable = new VarTable(this, colModel);

		
		//details panel listens to variant selection events generated by the varTable
		varTable.addVariantSelectionListener(detailsPanel); 
		varManager.addListener(this);
		colModel.addListener(this);
		filtersPanel.addListener(this);
		
		this.addSouth(detailsPanel, 300.0);
		this.add(varTable);
	}

	@Override
	public void variantListUpdated(List<Variant> newVars) {
		varTable.setVariants(newVars);
		
		//Create a string with sample names for the new label text
		StringBuilder str = new StringBuilder();
		List<String> samples = varManager.getSampleNames();
		if (samples == null || samples.size()==0) {
			str.append("Unknown sample");
		}
		else {
			str.append("Sample : " + samples.get(0));
			for(int i=1; i<samples.size(); i++) {
				str.append(", " + samples.get(i));
			}
		}
		setSampleLabelText(str.toString());
	}
	
	@Override
	public void variantListUpdateBeginning() {
		varTable.setVariants(new ArrayList<Variant>());
		setSampleLabelText("Loading data");
	}

	@Override
	public void variantListUpdateError() {
		setSampleLabelText("Error loading sample");
	}
	
	public void setSample(String sampleID) {
		clearPedAnnotations();
		varManager.setSample( sampleID );
		varManager.setFilters( getActiveFilters() );
		varManager.setAnnotations( colModel.getKeys() );
		varManager.reloadIfRequired();
	}
	
	
	/**
	 * Set the text of the sample label in the header above the variant table
	 * @param text
	 */
	public void setSampleLabelText(String text) {
		varTable.getHeader().setSampleName(text);
	}
	
	/**
	 * Remove annotations (columns) displaying pedigree info
	 */
	public void clearPedAnnotations() {
		varTable.getColumnModel().removeColumnsByClass(PedigreeVarAnnotation.class);
	}
	
	public VarListManager getVarListManager() {
		return varManager;
	}
	
	public VarTable getVarTable() {
		return varTable;
	}
	
	@Override
	public void columnStateChanged(ColumnModel model) {
		//If we want to switch to on-the-fly annotations then we'll need to ask for new
		//annotations everytime they change. In that case uncomment the next two lines and
		//the varManager will reload with the new annotations. 
		//List<String> annotationKeys = model.getKeys();
		//varManager.setAnnotations(annotationKeys);
		varManager.reloadIfRequired();
	}

	@Override
	public void filtersUpdated(List<VariantFilter> newFilters) {
		varManager.setFilters(newFilters);
		varManager.reloadIfRequired();
	}
	
	VarListManager varManager = new VarListManager();
	ColumnModel colModel = new ColumnModel();
	FiltersPanel filtersPanel;
	DetailsPanel detailsPanel;
	VarTable varTable = null;

}
