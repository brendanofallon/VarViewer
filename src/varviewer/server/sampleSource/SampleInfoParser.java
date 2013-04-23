package varviewer.server.sampleSource;

import varviewer.shared.SampleInfo;

public interface SampleInfoParser {

	public SampleInfo getInfoForURL(String path) throws SampleParseException;
	
}
