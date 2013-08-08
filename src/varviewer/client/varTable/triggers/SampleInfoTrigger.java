package varviewer.client.varTable.triggers;

import varviewer.client.varTable.VariantDisplay;
import varviewer.shared.SampleInfo;

public interface SampleInfoTrigger {

	/**
	 * Sometimes loading a certain sample type causes changes in the filtering or annotation columns
	 * These 'triggers' are fired every time a VariantDisplay loads a new sample
	 * @param info
	 * @param display
	 * @return
	 */
	public boolean handleSampleTrigger(SampleInfo info, VariantDisplay display);
}
