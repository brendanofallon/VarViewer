package varviewer.server.annotation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import varviewer.shared.Annotation;
import varviewer.shared.Variant;

public class TestAnnotationProvider implements AnnotationProvider {

	List<String> annos = Arrays.asList(new String[]{"some.anno", "another.anno"});
	
	public TestAnnotationProvider() {
		//Some test data
		annoMap.put(1, new Annotation[]{null, new Annotation(5.0)});
		annoMap.put(2, new Annotation[]{new Annotation("Something2"), new Annotation(17.0)});
		annoMap.put(6, new Annotation[]{new Annotation("Something6"), new Annotation(32.0)});
		annoMap.put(8, new Annotation[]{new Annotation("Something8"), new Annotation(14.2)});
		annoMap.put(11, new Annotation[]{new Annotation("Something11"), null});
	}
	
	@Override
	public Collection<String> getAnnotationsProvided() {
		return annos;
	}

	@Override
	public void annotateVariant(Variant var, AnnotationKeyIndex[] annotationKeys) {
		Annotation[] annos = getAnnosForPosition(var.getChrom(), var.getPos());
		if (annos == null) {
			return;
		}
		for(int i=0; i<annotationKeys.length; i++) {
			var.addAnnotation(annotationKeys[i].getKey(), annos[annotationKeys[i].getIndex()]);
		}
	}

	private Annotation[] getAnnosForPosition(String chrom, int pos) {
		return annoMap.get(pos);
	}

	@Override
	public AnnotationKeyIndex[] getKeyIndices(List<String> annotations) {
		AnnotationKeyIndex[] indices = new AnnotationKeyIndex[annotations.size()];
		for(int i=0; i<annotations.size(); i++) {
			indices[i] = new TestKeyIndex(annotations.get(i), annos.indexOf(annotations.get(i)));
		}
		return indices;
	}

	class TestKeyIndex implements AnnotationKeyIndex {

		final String key;
		final int index;
		
		TestKeyIndex(String key, int index) {
			this.key = key;
			this.index = index;
		}
		
		@Override
		public String getKey() {
			return key;
		}

		@Override
		public int getIndex() {
			return index;
		}
		
	}
	
	Map<Integer, Annotation[]> annoMap = new HashMap<Integer, Annotation[]>();
	
	public static void main(String[] args) {
		AnnotationProvider ap = new TestAnnotationProvider();
		AnnotationKeyIndex[] kIdx = ap.getKeyIndices(Arrays.asList(new String[]{"some.anno", "another.anno"}));
		Variant var = new Variant("1", 1, "N", "N");
		Variant var2 = new Variant("1", 2, "N", "N");
		Variant var3 = new Variant("1", 3, "N", "N");
		Variant var4 = new Variant("1", 11, "N", "N");
		ap.annotateVariant(var, kIdx);
		ap.annotateVariant(var2, kIdx);
		ap.annotateVariant(var3, kIdx);
		ap.annotateVariant(var4, kIdx);
		
		System.out.println("Annotation " + kIdx[0].getKey() + " : " + var.getAnnotation(kIdx[0].getKey()) + "\t\t another: " + var.getAnnotationStr("another.anno"));
		System.out.println("Annotation " + kIdx[0].getKey() + " : " + var2.getAnnotation(kIdx[0].getKey()) + "\t\t another: " + var2.getAnnotationStr("another.anno"));
		System.out.println("Annotation " + kIdx[0].getKey() + " : " + var3.getAnnotation(kIdx[0].getKey()) + "\t\t another: " + var3.getAnnotationStr("another.anno"));
		System.out.println("Annotation " + kIdx[0].getKey() + " : " + var4.getAnnotation(kIdx[0].getKey()) + "\t\t another: " + var4.getAnnotationStr("another.anno"));
		
	}
}
