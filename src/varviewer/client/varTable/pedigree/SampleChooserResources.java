package varviewer.client.varTable.pedigree;

import com.google.gwt.user.cellview.client.CellTable;

public interface SampleChooserResources extends CellTable.Resources {


	 /**
	    * The styles applied to the table.
	  */
	  interface TableStyle extends CellTable.Style {
	  }

	  @Override
	  @Source({ CellTable.Style.DEFAULT_CSS, "samplechooser.css" })
	  TableStyle cellTableStyle();
		
}
