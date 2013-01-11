package varviewer.client.varTable;

import com.google.gwt.user.cellview.client.CellTable;

/**
 * Inject our own css into to override default cell table style
 * @author brendan
 *
 */
public interface VarTableResources extends CellTable.Resources {

	 /**
     * The styles applied to the table.
     */
  interface TableStyle extends CellTable.Style {
  }

  @Override
  @Source({ CellTable.Style.DEFAULT_CSS, "vartable.css" })
  TableStyle cellTableStyle();
	
}
