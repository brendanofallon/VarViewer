package varviewer.client.varTable;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import varviewer.shared.variant.Variant;

public class ColumnModel
{
  List<String> keys = new ArrayList();
  Map<String, VarAnnotation> colMap = new HashMap();
  List<ColumnModelListener> listeners = new ArrayList();
  
  public ColumnModel()
  {
    addColumn(ColumnStore.getStore().getColumnForID("gene"));
    addColumn(ColumnStore.getStore().getColumnForID("exon.function"));
    addColumn(ColumnStore.getStore().getColumnForID("variant.type"));
    addColumn(ColumnStore.getStore().getColumnForID("zygosity"));
    addColumn(ColumnStore.getStore().getColumnForID("cdot"));
    addColumn(ColumnStore.getStore().getColumnForID("pdot"));
    addColumn(ColumnStore.getStore().getColumnForID("pop.freq.maf"));
    addColumn(ColumnStore.getStore().getColumnForID("disease.pics"));
    addColumn(ColumnStore.getStore().getColumnForID("disease.2.pics"));
    addColumn(ColumnStore.getStore().getColumnForID("rsnum"));
    addColumn(ColumnStore.getStore().getColumnForID("igv.link"));
  }
  
  public void setColumns(List<VarAnnotation<?>> annos)
  {
    this.colMap.clear();
    this.keys.clear();
    for (VarAnnotation<?> anno : annos) {
      addColumn(anno, true);
    }
    fireColumnChange();
  }
  
  public void addColumn(VarAnnotation<?> varAnno, boolean silent)
  {
    this.keys.add(varAnno.id);
    this.colMap.put(varAnno.id, varAnno);
    if (!silent) {
      fireColumnChange();
    }
  }
  
  public void addColumn(VarAnnotation<?> varAnno)
  {
    addColumn(varAnno, false);
  }
  
  public void removeColumn(String id)
  {
    this.keys.remove(id);
    this.colMap.remove(id);
    fireColumnChange();
  }
  
  public void removeColumnsByClass(Class<?> clz)
  {
    List<String> keysToRemove = new ArrayList();
    for (String key : this.colMap.keySet())
    {
      VarAnnotation<?> varAnno = (VarAnnotation)this.colMap.get(key);
      if (varAnno.getClass().equals(clz)) {
        keysToRemove.add(key);
      }
    }
    for (String key : keysToRemove)
    {
      this.colMap.remove(key);
      this.keys.remove(key);
    }
    if (keysToRemove.size() > 0) {
      fireColumnChange();
    }
  }
  
  public List<String> getKeys()
  {
    return this.keys;
  }
  
  public String writeHeader()
  {
    StringBuilder str = new StringBuilder();
    for (String key : getKeys())
    {
      String txt = getUserTextForKey(key);
      str.append(txt + ",");
    }
    return str.toString();
  }
  
  public String writeVariant(Variant var)
  {
    StringBuilder str = new StringBuilder();
    for (String key : getKeys())
    {
      Object val = getColumnForKey(key).getValue(var);
      String valStr = "?";
      if (val != null)
      {
        valStr = val.toString();
        if ((val instanceof SafeHtml))
        {
          SafeHtml html = (SafeHtml)val;
          valStr = html.asString().replaceAll("\\<.*?>", "");
        }
        if ((val instanceof ImageResource))
        {
          ImageResource imgRes = (ImageResource)val;
          valStr = "(image)";
          if (imgRes.equals(this.resources.refImage())) {
            valStr = "ref";
          }
          if (imgRes.equals(this.resources.hetImage())) {
            valStr = "het";
          }
          if (imgRes.equals(this.resources.homImage())) {
            valStr = "hom";
          }
        }
      }
      str.append(valStr + ",");
    }
    return str.toString();
  }
  
  public Column<Variant, ?> getColumnForKey(String key)
  {
    return ((VarAnnotation)this.colMap.get(key)).col;
  }
  
  public boolean containsColumn(String key)
  {
    return this.colMap.containsKey(key);
  }
  
  public String getUserTextForKey(String key)
  {
    return ((VarAnnotation)this.colMap.get(key)).userText;
  }
  
  public VarAnnotation<?> getVarAnnoForKey(String key)
  {
    return (VarAnnotation)this.colMap.get(key);
  }
  
  public void addListener(ColumnModelListener l)
  {
    this.listeners.add(l);
  }
  
  public void removeListener(ColumnModelListener l)
  {
    this.listeners.remove(l);
  }
  
  private void fireColumnChange()
  {
    for (ColumnModelListener l : this.listeners) {
      l.columnStateChanged(this);
    }
  }
  
  VarPageResources resources = (VarPageResources)GWT.create(VarPageResources.class);
}
