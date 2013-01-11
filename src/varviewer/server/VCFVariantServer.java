package varviewer.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import varviewer.server.variant.VCFReader;
import varviewer.server.variant.VariantCollection;
import varviewer.shared.Interval;
import varviewer.shared.IntervalList;
import varviewer.shared.Variant;
import varviewer.shared.VariantRequest;

/**
 * Obtains variants from a VCF file, also mostly for debugging purposes.
 * @author brendan
 *
 */
public class VCFVariantServer extends AbstractVariantServer {

	VariantCollection pool = null;
	
	public VCFVariantServer() {
		try {
			pool = new VariantCollection(new VCFReader(new File("/home/brendan/workspace/VarViewer/test.vcf")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public List<Variant> getVariants(VariantRequest req) {
		List<Variant> vars = new ArrayList<Variant>();
		if (pool != null) {
			IntervalList intervals = req.getIntervals();
			for(String contig : intervals.getContigs()) {
				List<Variant> contigVars = pool.getVariantsForContig(contig);
				for(Interval interval : intervals.getIntervalsInContig(contig)) {
					vars.addAll( varsInInterval(contigVars, interval) );
				}
			}
		}
		return vars;
	}
	
	/**
	 * Returns all vars in the list whose positions are contained in the given single interval.
	 * List is assumed to be SORTED! 
	 * @param vars
	 * @param interval
	 * @return
	 */
	private List<Variant> varsInInterval(List<Variant> vars, Interval interval) {
		List<Variant> matches = new ArrayList<Variant>();
		
		//Dumb way, make this faster someday when it matters
		for(Variant var : vars) {
			if (var.getPos() >= interval.getFirstPos() && var.getPos() < interval.getLastPos()) {
				matches.add(var);
			}
			if (var.getPos() >= interval.getLastPos()) {
				break;
			}
		}
		
		return matches;
	}

}
