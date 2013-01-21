package varviewer.client;

import java.util.ArrayList;
import java.util.List;

import varviewer.client.filters.FilterListener;
import varviewer.client.services.VarRequestService;
import varviewer.client.services.VarRequestServiceAsync;
import varviewer.shared.IntervalList;
import varviewer.shared.Variant;
import varviewer.shared.VariantFilter;
import varviewer.shared.VariantRequest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The VarListManager is responsible for sensibly handling requests for variants from the server.
 * It maintains information about the current sample, intervals and filters, and issues
 * requests for updated lists as necessary. This thing also maintains a list of VarListListeners
 * to be notified of var-list-updating events  
 * @author brendan
 *
 */
public class VarListManager implements FilterListener {

	private VariantRequest req = new VariantRequest();
	private List<Variant> currentList = new ArrayList<Variant>();
	private boolean reloadRequired = false;
	
	private List<VarListListener> listeners = new ArrayList<VarListListener>();
	
	
	private static VarListManager manager;
	
	public static VarListManager getManager() {
		if (manager == null)
			manager = new VarListManager();
		return manager;
	}

	private VarListManager() {
		//private constructor to enforce singleton status
	}
	
	public void setSample(String sampleID) {
		req.clearSamples();
		req.addSample(sampleID);
		reloadRequired = true;
	}
	
	public void setIntervals(IntervalList intervals) {
		req.setIntervals(intervals);
		reloadRequired = true;
	}
	
	@Override
	public void filtersUpdated(List<VariantFilter> newFilters) {
		setFilters(newFilters);
	}
	
	/**
	 * Clear current filter settings and 
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
		varRequestService.queryVariant(req, new AsyncCallback<List<Variant>>() {

			@Override
		public void onFailure(Throwable caught) {
				Window.alert("Error retrieving variants : " + caught.toString());
			}

			@Override
			public void onSuccess(List<Variant> result) {
				currentList = result;
				fireVarListChanged();
			}
			  
		  });
	}
	
	
	VarRequestServiceAsync varRequestService = (VarRequestServiceAsync) GWT.create(VarRequestService.class);

}
