package varviewer.shared.variant;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleAnnotationIndex implements AnnotationIndex, Serializable {

	private Map<String, Integer> keyMap = new HashMap<String, Integer>();
	private Map<String, Boolean> numericMap = new HashMap<String, Boolean>();
	
	public SimpleAnnotationIndex() {
		//required no-arg constructor
	}
	
	public SimpleAnnotationIndex(String[] allKeys, Collection<String> numericKeys) {
		for(int i=0; i<allKeys.length; i++) {
			keyMap.put(allKeys[i], i);
			if (numericKeys.contains(allKeys[i])) {
				numericMap.put(allKeys[i], true);
			}
			else {
				numericMap.put(allKeys[i], false);
			}
		}
	}
	
	@Override
	public int getIndexForKey(String key) {
		Integer val = keyMap.get(key);
		if (val == null) {
			//Not necessarily an error, not every set of variants will have every annotation
			return -1;
		}
		return val;
	}

	@Override
	public boolean isNumericForKey(String key) {
		return numericMap.get(key);
	}

	@Override
	public int size() {
		return keyMap.size();
	}

}
