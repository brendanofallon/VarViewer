package varviewer.client.varTable;

import java.util.Comparator;

import varviewer.shared.Variant;

public class NumericAnnotationComparator implements Comparator<Variant> {
	public final String key;
	
	public NumericAnnotationComparator(String key) {
		this.key = key;
	}

	@Override
	public int compare(Variant v0, Variant v1) {
		if (v0 == v1) {
			return 0;
		}
		
		String a0 = v0.getAnnotation(key);
		String a1 = v1.getAnnotation(key);
		
		if (a0 == null && a1 != null) {
			return 1;
		}
		if (a1 == null && a0 != null) {
			return -1;
		}
		
		Double d0 = 0.0;
		Double d1 = 0.0;
		try {
			d0 = Double.parseDouble(a0);
		}
		catch (NumberFormatException ex) {
			//blank on purpose
		}
		
		try {
			d1 = Double.parseDouble(a1);
			
		}
		catch (NumberFormatException ex) {
			// blank on purpose
		}
		return d0.compareTo(d1);
	}
}
