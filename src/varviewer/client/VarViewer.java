package varviewer.client;

import varviewer.client.sampleView.SamplesView;
import varviewer.client.varTable.VariantDisplay;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Main entry point for application. This contains the main root UI element (mainPanel), and 
 * can display either a VarDisplay or a SampleView element in it. 
 */
public class VarViewer implements EntryPoint {
	
	  public void onModuleLoad() {
		initComponents();
	  }
	  
	  private void initComponents() {
		  mainPanel = new FlowPanel();
		  showSampleViewer();
		  RootPanel.get().add(mainPanel);		  
	  }
	  
	  /**
	   * Remove all widgets from main panel and add the varDisplay widget to it
	   */
	  public void showVariantDisplay() {
		  if (mainPanel.getWidgetCount() > 0
				  && mainPanel.getWidget(0) == varDisplay) {
			  //Variant Display is already showing
			  return; 
		  }
		  else {
			  while(mainPanel.getWidgetCount()>0) {
				  mainPanel.remove(0);
			  }
			  mainPanel.add(varDisplay);
		  }
	  }
	  
	  public void showSampleViewer() {
		  if (mainPanel.getWidgetCount() > 0
				  && mainPanel.getWidget(0) == sampleView) {
			  //SampleView is already showing
			  return; 
		  }
		  else {
			  //Remove all current widgets
			  while(mainPanel.getWidgetCount()>0) {
				  mainPanel.remove(0);
			  }
			  mainPanel.add(sampleView);
			  sampleView.refreshSampleList();
		  }
	  }


	  FlowPanel mainPanel; //Root container for all UI elements
	  VariantDisplay varDisplay = new VariantDisplay(); //Displays variant table and related panels
	  SamplesView sampleView = new SamplesView(this); //UI element that displays sample list	  
	  VarListManager varManager = VarListManager.getManager(); //client-side store of current variant data

}

