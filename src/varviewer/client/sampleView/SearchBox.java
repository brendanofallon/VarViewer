package varviewer.client.sampleView;

import varviewer.client.HighlightButton;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A semi-configurable text box that can be used to filter the samples
 * @author brendanofallon
 *
 */
public class SearchBox extends HorizontalPanel {

	private TextBox textBox = new TextBox();
	private HighlightButton clearButton;
	private SamplesView sampleView;
	private boolean first = true;
	
	public SearchBox(SamplesView parentView) {
		this.sampleView = parentView;
		initComponents();
	}

	private void initComponents() {
		textBox.setStylePrimaryName("searchbox");
		textBox.setText("Filter...");
		textBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				handleTextChange();
			}
		});
		textBox.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				handleClick();
			}
		});
		this.add(textBox);
		
		Image clearImage = new Image("images/clearIcon16.png");
		clearButton = new HighlightButton(clearImage);
		this.add(clearButton);
		clearButton.setWidth("16px");
		clearButton.setHeight("16px");
		clearButton.getElement().getStyle().setMarginTop(3.0, Unit.PX);
		clearButton.getElement().getStyle().setMarginLeft(3.0, Unit.PX);
		clearButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				textBox.setText("");
				handleTextChange();
			}
		});
	}

	/**
	 * Clear text from the box if this is the first click
	 */
	protected void handleClick() {
		if (first) {
			textBox.setText("");
			first = false;
		}
	}

	protected void handleTextChange() {
		sampleView.setFilterText(textBox.getText());
	}
}
