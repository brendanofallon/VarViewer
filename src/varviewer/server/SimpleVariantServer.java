package varviewer.server;

import java.util.ArrayList;
import java.util.List;

import varviewer.shared.IntervalList;
import varviewer.shared.Variant;
import varviewer.shared.VariantRequest;

/**
 * A very simple variant source for testing / debugging
 * @author brendan
 *
 */
public class SimpleVariantServer extends AbstractVariantServer {

	List<Variant> allVariants = new ArrayList<Variant>();
	
	public SimpleVariantServer() {
		allVariants.add(new Variant("1", 1, "A", "T"));
		allVariants.add(new Variant("1", 10, "G", "T"));
		allVariants.add(new Variant("1", 117, "-", "T"));
		allVariants.add(new Variant("1", 234, "G", "T"));
		allVariants.add(new Variant("1", 589, "C", "-"));
		allVariants.add(new Variant("1", 1092, "AAGC", "TAA"));
		allVariants.add(new Variant("2", 10, "GGCGGC", "A"));
		allVariants.add(new Variant("2", 11, "G", "-"));
		allVariants.add(new Variant("2", 12, "A", "N"));
		allVariants.add(new Variant("2", 105, "C", "A"));
		allVariants.add(new Variant("2", 1006, "A", "A"));
		allVariants.add(new Variant("2", 1042, "G", "T"));
		
	}
	
	@Override
	public List<Variant> getVariants(VariantRequest req) {
		 //Look through all variants, if any are in the intervals requested add them
		
		List<Variant> varsToReturn = new ArrayList<Variant>();
		IntervalList intervals = req.getIntervals();
		for(Variant var : allVariants) {
//			if (var.getChrom().equals("1")) {
//				varsToReturn.add(var);
//				System.out.println("Adding variant : " + var);
//			}
			if (intervals.contains(var.getChrom(), var.getPos())) {
				varsToReturn.add(var);
				System.out.println("Adding variant : " + var);
			}
		}
		return varsToReturn;
	}

}
