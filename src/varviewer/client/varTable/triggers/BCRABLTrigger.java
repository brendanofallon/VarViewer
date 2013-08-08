package varviewer.client.varTable.triggers;

import java.util.ArrayList;
import java.util.List;

import varviewer.client.varTable.ColumnStore;
import varviewer.client.varTable.VarAnnotation;
import varviewer.client.varTable.VariantDisplay;
import varviewer.shared.SampleInfo;

/**
 * This trigger changes the filtering and visible annotations to something relevant for BCR-ABL tests
 * @author brendan
 *
 */
public class BCRABLTrigger implements SampleInfoTrigger {

	@Override
	public boolean handleSampleTrigger(SampleInfo info, VariantDisplay display) {
		
		if (info.getAnalysisType().contains("BCR-ABL")) {
			
			display.getFiltersPanel().turnOffAllFilters();
						
			List<VarAnnotation<?>> annos = new ArrayList<VarAnnotation<?>>();
			annos.add( ColumnStore.getStore().getColumnForID("cdot"));
			annos.add( ColumnStore.getStore().getColumnForID("pdot"));
			annos.add( ColumnStore.getStore().getColumnForID("InVitro"));
			annos.add( ColumnStore.getStore().getColumnForID("Known"));
			annos.add( ColumnStore.getStore().getColumnForID("igv.link"));
			
			display.getColumnModel().setColumns(annos);
			
			return true;
		}
		
		return false;
	}

}
