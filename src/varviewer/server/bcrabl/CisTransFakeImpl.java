package varviewer.server.bcrabl;

import java.io.File;

import varviewer.server.appContext.SpringContext;
import varviewer.server.sampleSource.SampleSource;
import varviewer.shared.bcrabl.CisTransRequest;
import varviewer.shared.bcrabl.CisTransResult;

public class CisTransFakeImpl implements CisTransHandler {

	@Override
	public CisTransResult computeCisTransResult(CisTransRequest req) {
		CisTransResult result = new CisTransResult();

		//Get SampleSource from ApplicationContext so we can examine BAM file
		SampleSource sampleSource = (SampleSource) SpringContext.getContext().getBean("sampleSource");
		
		File bamFile = sampleSource.getBAMFileForSample(req.getSample());
		result.setMessage("Hello! Got sampleID " + req.getSample().getSampleID() + " bam path: " + bamFile + " variant A: " + req.getVarA().getPos() + " var B : " + req.getVarB().getPos());
		
		result.setBothRefs(0.5);
		result.setBothAlts(0.51213);
		result.setAlt1Only(0.135);
		result.setAlt2Only(0.1235);
		result.setTransFrac(0.5289);
		result.setCisFrac(0.123897);
		
		return result;
	}

	@Override
	public boolean closeEnoughToCompute(CisTransRequest req) {
		return true;
	}

}
