package varviewer.server.geneDetails;

import varviewer.shared.GeneInfo;

public interface GeneDetailHandler {

	/**
	 * Obtain a gene info object containing various pieces of descriptive information
	 * regarding the gene with the given id
	 * @param geneID
	 * @return
	 */
	public GeneInfo getInfoForGene(String geneID);
}
