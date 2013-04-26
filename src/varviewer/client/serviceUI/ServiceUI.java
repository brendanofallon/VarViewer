package varviewer.client.serviceUI;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public interface ServiceUI {

	public void initialize();
	
	public Widget getWidget();
	
	public void close();
	
	public Image getIcon();
	
}
