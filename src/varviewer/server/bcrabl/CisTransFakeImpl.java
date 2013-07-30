package varviewer.server.bcrabl;

import varviewer.shared.bcrabl.CisTransRequest;
import varviewer.shared.bcrabl.CisTransResult;

public class CisTransFakeImpl implements CisTransHandler {

	@Override
	public CisTransResult computeCisTransResult(CisTransRequest req) {
		CisTransResult result = new CisTransResult();
		result.setMessage("Hello from the Server!");
		return result;
	}

}
