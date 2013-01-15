package varviewer.client.varTable;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A popup menu that allows the user to choose which columns to display. Choices
 * a reflected in a ColumnModel, which fires events which are listened to by the VarTable
 * @author brendan
 *
 */
public class ColPickerPopup extends PopupPanel {

	private Map<String, CheckBox> colItems = new HashMap<String, CheckBox>();
	private VerticalPanel innerPanel = new VerticalPanel();
	private ColumnModel model;
	
	public ColPickerPopup(ColumnModel colModel) {
		super(true);
		
		this.model = colModel;
		
		ColumnStore colStore = ColumnStore.getStore();
		for(VarAnnotation varAnno : colStore.getAllColumns()) {
			addColumn(varAnno);
		}
		this.add(innerPanel);
		innerPanel.setStylePrimaryName("colpopup");
		
		//Set state of checkboxes...
		for(String key : colModel.getKeys()) {
			CheckBox box = colItems.get(key);
			if (box != null) {
				box.setValue(true);
			}
		}
	}
	
	
	private void addColumn(final VarAnnotation col) {
		final CheckBox box = new CheckBox(col.userText);
		box.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				toggleState(col.id);
			}	
		});
		colItems.put(col.id, box);
		innerPanel.add(box);
	}


	protected void toggleState(String id) {
		CheckBox box = colItems.get(id);
		if (model.containsColumn(id)) {
			box.setValue(false);
			model.removeColumn(id);
		}
		else {
			box.setValue(true);
			model.addColumn( ColumnStore.getStore().getColumnForID(id) );
			
		}
	}
	
	
}
