package varviewer.client.varTable.filters;

import varviewer.client.HighlightButton;
import varviewer.shared.variant.VariantFilter;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class FilterBox extends DockLayoutPanel {
	
	private HighlightButton configButton;
	//private HighlightButton removeButton;
	private String name;
	private FilterConfig configTool = null;
	private VariantFilter filter;
	private FiltersPanel parentPanel;
	protected Label interiorText = new Label();
	private CheckBox enabledBox = new CheckBox();
	private Label nameLabel;
	
	public FilterBox(FiltersPanel parentPanel, String name, VariantFilter filter) {
		super(Style.Unit.PX);
		this.setStylePrimaryName("filterbox");
		this.name = name;
		this.filter = filter;
		this.parentPanel = parentPanel;
		initComponents();
	}

	/**
	 *	A reference to the FiltersPanel this FilterBox is housed in 
	 * @return
	 */
	FiltersPanel getFiltersPanel() {
		return parentPanel;
	}
	
	/**
	 * Set the configuration tool used by this filter
	 * @param configTool
	 */
	public void setConfigTool(FilterConfig configTool) {
		this.configTool = configTool;
	}

	private void initComponents() {
		
		DockLayoutPanel topPanel = new DockLayoutPanel(Style.Unit.PX);
		topPanel.setStylePrimaryName("filterboxheader");
		nameLabel = new Label(name);
		nameLabel.setStylePrimaryName("textlabel");
		topPanel.addWest(nameLabel, 120.0);
		
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		
		Image configImage = new Image("images/wrench-icon.png");
		configButton = new HighlightButton(configImage, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				popConfigTool();
			}
		});
		configButton.setWidth("18px");
		configButton.setHeight("18px");
		topPanel.addEast(buttonsPanel, 45.0);
		buttonsPanel.add(configButton);
		buttonsPanel.add(enabledBox);
		enabledBox.setValue(true);
		enabledBox.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				toggleEnabled();
			}
		});
		
//		Image removeImage = new Image("images/remove-icon.png");
//		removeButton = new HighlightButton(removeImage, new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				removeMe();
//			}
//		});
//		removeButton.setWidth("17px");
//		removeButton.setHeight("17px");
//		buttonsPanel.add(removeButton);
		
		this.addNorth(topPanel, 26);
		
		interiorText.setStylePrimaryName("filterbox-interior");
		this.add(interiorText);
	}
	
	/**
	 * Programmatic turn off check box for this filter. 
	 */
	public void turnOffFilter() {
		if ( isEnabled() ) {
			enabledBox.setValue(false);
		}
	}
	
	protected void toggleEnabled() {
		parentPanel.fireFiltersChanged();
		if (! isEnabled()) {
			setInteriorText("");
			nameLabel.setStylePrimaryName("textlabel-disabled");
		}
		else {
			nameLabel.setStylePrimaryName("textlabel");
			if (configTool != null) {
				configTool.updateInteriorLabelText();
			}
		}
	}

	/**
	 * True if the filter is 'enabled', which means it will actually filter things. Disabled filters dont do any filtering. 
	 */
	public boolean isEnabled() {
		return enabledBox.getValue();
	}

	protected void setInteriorText(String text) {
		this.interiorText.setText(text);
	}

	protected void removeMe() {
		parentPanel.removeFilter(this);
		
		//Hide config tool if it's showing
		if (configTool != null)
			configTool.hide();
	}

	protected void popConfigTool() {
		if (configTool != null) {
			configTool.showRelativeTo(configButton);
		}
	}

	public VariantFilter getFilter() {
		return filter;
	}

	/**
	 * The user-friendly name of this filter ("Exon effect", "Pop. frequency", etc).
	 * @return
	 */
	public String getName() {
		return name;
	}

}
