package varviewer.client.varTable.filters;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import varviewer.shared.varFilters.ExonFuncFilter;
import varviewer.shared.variant.VariantFilter;

public class ExonFuncFilterConfig
  extends FilterConfig
{
  private Map<String, CheckBox> exonTypes = new HashMap();
  private ExonFuncFilter filter;
  
  public ExonFuncFilterConfig(FilterBox parent)
  {
    super(parent);
    
    VariantFilter fil = parent.getFilter();
    if ((fil instanceof ExonFuncFilter)) {
      this.filter = ((ExonFuncFilter)fil);
    } else {
      throw new IllegalArgumentException("Incorrect filter type in ExonFuncFilter");
    }
    VerticalPanel panel = new VerticalPanel();
    Label lab = new Label("Exclude variants by type:");
    panel.add(lab);
    panel.add(addType("Noncoding RNA", true));
    panel.add(addType("Non-frameshifting indels", false));
    panel.add(addType("Frameshifting indels", false));
    panel.add(addType("Stop gains & losses", false));
    panel.add(addType("Synonymous", true));
    panel.add(addType("Missense", false));
    
    Label lab2 = new Label("Exclude variants by region:");
    panel.add(lab2);
    panel.add(addType("Exonic", false));
    panel.add(addType("Exonic;splicing", false));
    panel.add(addType("Splicing (no additional annotation)", false));
    panel.add(addType("Intronic (and not splicing)", true));
    panel.add(addType("Intergenic", true));
    panel.add(addType("UTR", true));
    
    panel.getElement().addClassName("exonFuncTable");
    for (int i = 0; i < panel.getWidgetCount(); i++) {
      panel.setCellHeight(panel.getWidget(i), "20px");
    }
    updateInteriorLabelText();
    this.interiorPanel.add(panel);
    panel.setHeight("350px");
    this.interiorPanel.setWidth("300px");
    this.interiorPanel.setHeight("400px");
  }
  
  private CheckBox addType(String userText, boolean checked)
  {
    CheckBox box = new CheckBox(userText);
    box.setValue(Boolean.valueOf(checked));
    this.exonTypes.put(userText, box);
    return box;
  }
  
  public void updateInteriorLabelText()
  {
    int count = 0;
    List<String> types = new ArrayList();
    for (String key : this.exonTypes.keySet())
    {
      CheckBox box = (CheckBox)this.exonTypes.get(key);
      if (box.getValue().booleanValue())
      {
        count++;
        types.add(key);
      }
    }
    if (count == 0)
    {
      this.parentBox.setInteriorText("Excluding NO variant types");
      return;
    }
    if (count > 4)
    {
      this.parentBox.setInteriorText("Excluding " + count + " variant types");
      return;
    }
    String str = "";
    for (int i = 0; i < types.size() - 1; i++) {
      str = str + (String)types.get(i) + ", ";
    }
    str = str + " and " + (String)types.get(types.size() - 1);
    this.parentBox.setInteriorText("Excluding " + str + " variants");
  }
  
  protected boolean validateAndUpdateFilter()
  {
    for (String key : this.exonTypes.keySet())
    {
      CheckBox box = (CheckBox)this.exonTypes.get(key);
      if (key.equals("Intergenic")) {
        this.filter.setExcludeIntergenic(box.getValue().booleanValue());
      }
      if (key.equals("Intronic (and not splicing)")) {
        this.filter.setExcludeIntronic(box.getValue().booleanValue());
      }
      if (key.equals("UTR")) {
        this.filter.setExcludeUTR(box.getValue().booleanValue());
      }
      if (key.equals("Synonymous")) {
        this.filter.setExcludeSynonymous(box.getValue().booleanValue());
      }
      if (key.equals("Non-frameshifting indels")) {
        this.filter.setExcludeNonFrameshift(box.getValue().booleanValue());
      }
      if (key.equals("Frameshifting indels")) {
        this.filter.setExcludeFrameshift(box.getValue().booleanValue());
      }
      if (key.equals("Splicing (no additional annotation)")) {
        this.filter.setExcludeSplicing(box.getValue().booleanValue());
      }
      if (key.equals("Exonic;splicing")) {
        this.filter.setExcludeExonicSplicing(box.getValue().booleanValue());
      }
      if (key.equals("Exonic")) {
        this.filter.setExcludeExonic(box.getValue().booleanValue());
      }
      if (key.equals("Missense")) {
        this.filter.setExcludeNonsynonymous(box.getValue().booleanValue());
      }
      if (key.equals("Noncoding RNA")) {
        this.filter.setExcludeNCRNA(box.getValue().booleanValue());
      }
      if (key.equals("Stop gains & losses")) {
        this.filter.setExcludeStopGainsLosses(box.getValue().booleanValue());
      }
    }
    updateInteriorLabelText();
    return true;
  }
}
