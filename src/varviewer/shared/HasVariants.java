package varviewer.shared;

import varviewer.shared.variant.Variant;

public interface HasVariants {

	/**
	 * Return variant at given chromosome and position, if one exists
	 * @param contig
	 * @param pos
	 * @return
	 */
	public Variant getVariant(String contig, int pos);
	
}
