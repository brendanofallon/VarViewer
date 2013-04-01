package varviewer.client.varTable;

import java.util.Comparator;

import varviewer.shared.variant.Variant;

public class PositionComparator implements Comparator<Variant> {

	@Override
	public int compare(Variant v0, Variant v1) {
		if (v0.getChrom().equals( v1.getChrom()))
			return v1.getPos() - v0.getPos();
		else {
			return v0.getChrom().compareTo( v1.getChrom() );
		}
	}

}
