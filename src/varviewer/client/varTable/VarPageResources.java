package varviewer.client.varTable;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

interface VarPageResources extends ClientBundle {

	@Source("export-icon.png")
	ImageResource testImage();
	
	@Source("OMIM-icon.png")
	ImageResource omimImage();
	
	@Source("HGMD-icon.png")
	ImageResource hgmdImage();
	
	@Source("HGMDHit-icon.png")
	ImageResource hgmdHitImage();
	
	
}
