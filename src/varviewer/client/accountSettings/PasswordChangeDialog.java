package varviewer.client.accountSettings;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Popup allows users to enter a new password for their account
 * @author brendan
 *
 */
public class PasswordChangeDialog extends PopupPanel {

	final protected DockLayoutPanel interiorPanel = new DockLayoutPanel(Unit.PX);
	private Button okButton;
	private Button cancelButton;
	private PasswordTextBox newBox;
	private PasswordTextBox verifyBox;
	private Grid grid;
	private HorizontalPanel okPanel;
	private HorizontalPanel warnPanel;
	
	
	public PasswordChangeDialog() {
		initComponents();
	}

	private void initComponents() {
		this.add(interiorPanel);
		this.setStylePrimaryName("passworddialog");
		HTML header = new HTML("<center>Change password</center>");
		header.setStylePrimaryName("textlabel14");
		interiorPanel.addNorth(header, 22);
		
		HorizontalPanel bottomPanel = new HorizontalPanel();
		okButton= new Button("Change password");
		okButton.setEnabled(false);
		okButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				submit();
			}
			
		});
		cancelButton = new Button("Cancel");
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				cancel();
			}
		});
		SimplePanel spacer = new SimplePanel();
		spacer.setWidth("130px");
		bottomPanel.add(cancelButton);
		bottomPanel.add(spacer);
		bottomPanel.add(okButton);
		interiorPanel.addSouth(bottomPanel, 32);
		
		grid = new Grid(5,2);
		grid.setWidget(0, 0, new Label("Current password:"));
		grid.setWidget(2, 0, new Label("New password:"));
		grid.setWidget(3, 0, new Label("Verify new password:"));
		grid.getCellFormatter().setHorizontalAlignment(0, 0, HorizontalAlignmentConstant.endOf(Direction.LTR));
		grid.getCellFormatter().setHorizontalAlignment(2, 0, HorizontalAlignmentConstant.endOf(Direction.LTR));
		grid.getCellFormatter().setHorizontalAlignment(3, 0, HorizontalAlignmentConstant.endOf(Direction.LTR));		
		PasswordTextBox existingBox = new PasswordTextBox();
		
		newBox = new PasswordTextBox();
		newBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				if (verifyBox.getText().length()>0)
					checkPasswordsMatch();
			}
			
		});
		verifyBox = new PasswordTextBox();

		verifyBox.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				checkPasswordsMatch();
			}
		});
		
		grid.setWidget(0, 1, existingBox);
		grid.setWidget(2, 1, newBox);
		grid.setWidget(3, 1, verifyBox);
		
		interiorPanel.add(grid);
		
		okPanel = new HorizontalPanel();
		warnPanel = new HorizontalPanel();
		
		okPanel.add(new Image("images/check32.png"));
		warnPanel.add(new Image("images/warning32.png"));
		Label warnLabel = new Label("Passwords don't match");
		warnLabel.setStylePrimaryName("textlabel12");
		warnPanel.add(warnLabel);
		
		this.setSize("320px", "250px");
		this.setPopupPosition(200, 100);
	}

	protected void checkPasswordsMatch() {
		if (newBox.getText().equals(verifyBox.getText())) {
			okButton.setEnabled(true);
			grid.setWidget(4, 0, okPanel);
		}
		else {
			okButton.setEnabled(false);
			grid.setWidget(4, 0, warnPanel);
		}
	}

	protected void submit() {
		this.hide();
	}

	protected void cancel() {
		this.hide();
	}
	
	
	
}
