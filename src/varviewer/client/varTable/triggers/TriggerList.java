package varviewer.client.varTable.triggers;

import java.util.ArrayList;
import java.util.List;

public class TriggerList {

	/**
	 * Static access to all triggers
	 * @return
	 */
	public static List<SampleInfoTrigger> getAllTriggers() {
		List<SampleInfoTrigger> triggers = new ArrayList<SampleInfoTrigger>();
		
		
		triggers.add(new BCRABLTrigger());
		
		return triggers;
	}
	
}
