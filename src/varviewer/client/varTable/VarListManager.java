package varviewer.client.varTable;

import java.util.ArrayList;
import java.util.List;

import varviewer.client.VarListListener;
import varviewer.client.services.VarRequestService;
import varviewer.client.services.VarRequestServiceAsync;
import varviewer.shared.IntervalList;
import varviewer.shared.varFilters.PedigreeFilter;
import varviewer.shared.variant.Variant;
import varviewer.shared.variant.VariantFilter;
import varviewer.shared.variant.VariantRequest;
import varviewer.shared.variant.VariantRequestResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The VarListManager is responsible for sensibly handling requests for variants from the server.
 * It maintains information about a single "current" sample, intervals and filters, and issues
 * requests for updated lists as necessary. This thing also maintains a list of VarListListeners
 * to be notified of var-list-updating events.  
 * @author brendan
 *
 */
public class VarListManager {

	private VariantRequest req = new VariantRequest();
	private List<Variant> currentList = new ArrayList<Variant>();
	private List<PedigreeFilter> pedigreeFilters = new ArrayList<PedigreeFilter>(); //Active pedigree filters, these are managed separately from normal filters
	private boolean reloadRequired = false;
	
	private List<VarListListener> listeners = new ArrayList<VarListListener>();
	
	public void setSample(String sampleID) {
		req.clearSamples();
		req.addSample(sampleID);
		pedigreeFilters.clear();
		reloadRequired = true;
	}
	
	public void setAnnotations(List<String> annotationKeys) {
		req.setAnnotations(annotationKeys);
		reloadRequired = true;
	}
	
	/**
	 * Obtain the list of current sample names. If the list has not been updated via a reload() call
	 * the sample names may not reflect the names of the variants contained herein.
	 * @return
	 */
	public List<String> getSampleNames() {
		return req.getSampleIDs();
	}
	
	public void setIntervals(IntervalList intervals) {
		req.setIntervals(intervals);
		reloadRequired = true;
	}
	
	/**
	 * Clear current filter settings and add all given filters to the filter list
	 * @param filters
	 */
	public void setFilters(List<VariantFilter> filters) {
		req.clearFilters();
		for(VariantFilter filter : filters) {
			req.addFilter(filter);
		}		
		reloadRequired = true;
	}
	
	
	/**
	 * Add all given filters to the filter list. 
	 * @param filters
	 */
	public void setPedigreeFilters(List<PedigreeFilter> pedFilters) {
		pedigreeFilters.clear();
		pedigreeFilters.addAll(pedFilters);
		reloadRequired = true;
	}
	
	
	
	/**
	 * Returns true if settings have been changed since last variant reload. This is 
	 * initially false
	 * @return
	 */
	public boolean getReloadRequired() {
		return reloadRequired;
	}
	
	public void addListener(VarListListener l) {
		listeners.add(l);
	}
	
	public boolean removeListener(VarListListener l) {
		return listeners.remove(l);
	}
	
	protected void fireVarListChanged() {
		for(VarListListener l : listeners) {
			l.variantListUpdated(currentList);
		}
	}
	
	protected void fireVarUpdateBeginning() {
		for(VarListListener l : listeners) {
			l.variantListUpdateBeginning();
		}
	}
	
	protected void fireVarUpdateError() {
		for(VarListListener l : listeners) {
			l.variantListUpdateError();
		}
	}
	
	/**
	 * Reload variant list only if settings have changed since last reload
	 */
	public void reloadIfRequired() {
		if (reloadRequired) {
			reloadVariants();
		}
	}

	public void reloadVariants() {
		reloadRequired = false;
		fireVarUpdateBeginning();
		
		
		//We actually send a copy of the usual variant request to which we append 
		//all PedigreeFilters
		VariantRequest newReq = req.clone();
		for(PedigreeFilter pedFilter : pedigreeFilters) {
			newReq.addFilter(pedFilter);
		}
		varRequestService.queryVariant(newReq, new AsyncCallback<VariantRequestResult>() {

			@Override
		public void onFailure(Throwable caught) {
				caught.printStackTrace();
				fireVarUpdateError();
				Window.alert("Error retrieving variants : " + caught.toString() + " Cause: " + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(VariantRequestResult result) {
				currentList = result.getVars();
				if (currentList != null) 
					fireVarListChanged();
				else {
					fireVarUpdateError();
				}
			}
			  
		  });
	}
	
	VarRequestServiceAsync varRequestService = (VarRequestServiceAsync) GWT.create(VarRequestService.class);

}
