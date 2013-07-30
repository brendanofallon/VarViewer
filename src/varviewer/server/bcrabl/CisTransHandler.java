package varviewer.server.bcrabl;

import varviewer.shared.bcrabl.CisTransRequest;
import varviewer.shared.bcrabl.CisTransResult;

//Interface for things that can implement bcr-able style cis/trans computations
public interface CisTransHandler {

	public CisTransResult computeCisTransResult(CisTransRequest req);
}
