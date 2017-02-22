package varviewer.shared.varFilters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import varviewer.shared.variant.AnnotationIndex;
import varviewer.shared.variant.Variant;
import varviewer.shared.variant.VariantFilter;

public class ExonFuncFilter
  implements VariantFilter, Serializable
{
  private boolean excludeIntergenic = true;
  private boolean excludeIntronic = true;
  private boolean excludeSynonymousAll = true;
  private boolean excludeNonsynonymous = false;
  private boolean excludeNonFrameshift = false;
  private boolean excludeStopGainsLosses = false;
  private boolean excludeFrameshift = false;
  private boolean excludeSplicing = false;
  private boolean excludeExonic = false;
  private boolean excludeExonicSplicing = false;
  private boolean excludeUTR = true;
  private boolean excludeNCRNA = true;
  private AnnotationIndex index = null;
  private int varTypeIndex = -1;
  private int exonFuncIndex = -1;
  private int geneIndex = -1;
  private boolean missingDataPasses = true;
  
  public void setAnnotationIndex(AnnotationIndex index)
  {
    this.index = index;
    if (index != null)
    {
      this.varTypeIndex = index.getIndexForKey("variant.type");
      this.exonFuncIndex = index.getIndexForKey("exon.function");
      this.geneIndex = index.getIndexForKey("gene");
    }
  }
  
  public boolean variantPasses(Variant var)
  {
    String varType = var.getAnnotationStr(this.varTypeIndex);
    String exonFunc = var.getAnnotationStr(this.exonFuncIndex);
    if (varType == null) {
      return this.missingDataPasses;
    }
    boolean isSnpEff = varType.toUpperCase().equals(varType);
    
    varType = varType.toLowerCase();
    if ((this.excludeIntergenic) && (
      (varType.contains("intergen")) || 
      (varType.contains("upstream")) || 
      (varType.contains("downstream")) || (
      (var.getAnnotationStr(this.geneIndex) != null) && (var.getAnnotationStr(this.geneIndex).length() < 2)))) {
      return false;
    }
    if ((this.excludeIntronic) && (varType.contains("intron"))) {
      return false;
    }
    if ((this.excludeUTR) && (varType.contains("utr"))) {
      return false;
    }
    if ((this.excludeNCRNA) && (varType.contains("ncrna"))) {
      return false;
    }
    if ((this.excludeExonic) && (varType.equals("exonic"))) {
      return false;
    }
    if ((this.excludeExonicSplicing) && (varType.startsWith("exonic;splicing"))) {
      return false;
    }
    if ((this.excludeSplicing) && (varType.startsWith("splicing"))) {
      return false;
    }
    if ((isSnpEff) || (exonFunc.equals("-"))) {
      exonFunc = varType.toLowerCase();
    }
    if (exonFunc == null) {
      return this.missingDataPasses;
    }
    if ((this.excludeNonFrameshift) && (exonFunc.contains("nonframeshift"))) {
      return false;
    }
    if ((this.excludeStopGainsLosses) && (exonFunc.contains("stop"))) {
      return false;
    }
    if ((this.excludeFrameshift) && ((exonFunc.startsWith("frameshift")) || (exonFunc.startsWith("frame_shift")))) {
      return false;
    }
    if ((this.excludeSynonymousAll) && (exonFunc.startsWith("synonymous"))) {
      return false;
    }
    if ((this.excludeNonsynonymous) && ((exonFunc.contains("missense")) || (exonFunc.contains("nonsynonymous")) || (exonFunc.contains("non_synonymous")))) {
      return false;
    }
    return true;
  }
  
  public boolean isExcludeIntergenic()
  {
    return this.excludeIntergenic;
  }
  
  public void setExcludeIntergenic(boolean excludeIntergenic)
  {
    this.excludeIntergenic = excludeIntergenic;
  }
  
  public boolean isExcludeIntronic()
  {
    return this.excludeIntronic;
  }
  
  public void setExcludeIntronic(boolean excludeIntronic)
  {
    this.excludeIntronic = excludeIntronic;
  }
  
  public boolean isExcludeSynonymous()
  {
    return this.excludeSynonymousAll;
  }
  
  public void setExcludeSynonymous(boolean excludeSynonymousAll)
  {
    this.excludeSynonymousAll = excludeSynonymousAll;
  }
  
  public boolean isExcludeNonsynonymous()
  {
    return this.excludeNonsynonymous;
  }
  
  public void setExcludeNonsynonymous(boolean excludeNonsynonymous)
  {
    this.excludeNonsynonymous = excludeNonsynonymous;
  }
  
  public boolean isExcludeNonFrameshift()
  {
    return this.excludeNonFrameshift;
  }
  
  public void setExcludeNonFrameshift(boolean excludeNonFrameshift)
  {
    this.excludeNonFrameshift = excludeNonFrameshift;
  }
  
  public boolean isExcludeFrameshift()
  {
    return this.excludeFrameshift;
  }
  
  public void setExcludeFrameshift(boolean excludeFrameshift)
  {
    this.excludeFrameshift = excludeFrameshift;
  }
  
  public boolean isExcludeSplicing()
  {
    return this.excludeSplicing;
  }
  
  public void setExcludeSplicing(boolean excludeSplicing)
  {
    this.excludeSplicing = excludeSplicing;
  }
  
  public boolean isExcludeExonicSplicing()
  {
    return this.excludeExonicSplicing;
  }
  
  public void setExcludeExonicSplicing(boolean excludeExonicSplicing)
  {
    this.excludeExonicSplicing = excludeExonicSplicing;
  }
  
  public boolean isExcludeExonic()
  {
    return this.excludeExonic;
  }
  
  public void setExcludeExonic(boolean excludeExonic)
  {
    this.excludeExonic = excludeExonic;
  }
  
  public boolean isExcludeStopGainsLosses()
  {
    return this.excludeStopGainsLosses;
  }
  
  public void setExcludeStopGainsLosses(boolean excludeStopGainsLosses)
  {
    this.excludeStopGainsLosses = excludeStopGainsLosses;
  }
  
  public boolean isExcludeUTR()
  {
    return this.excludeUTR;
  }
  
  public void setExcludeUTR(boolean excludeUTR)
  {
    this.excludeUTR = excludeUTR;
  }
  
  public boolean isMissingDataPasses()
  {
    return this.missingDataPasses;
  }
  
  public void setMissingDataPasses(boolean missingDataPasses)
  {
    this.missingDataPasses = missingDataPasses;
  }
  
  public boolean isExcludeNCRNA()
  {
    return this.excludeNCRNA;
  }
  
  public void setExcludeNCRNA(boolean excludeNCRNA)
  {
    this.excludeNCRNA = excludeNCRNA;
  }
  
  public String getUserDescription()
  {
    StringBuilder str = new StringBuilder("Variants of the following types were excluded: ");
    List<String> strs = new ArrayList();
    if (isExcludeIntergenic()) {
      strs.add("intergenic");
    }
    if (isExcludeIntronic()) {
      strs.add("intronic");
    }
    if (isExcludeNCRNA()) {
      strs.add("non-coding RNA");
    }
    if (isExcludeNonFrameshift()) {
      strs.add("non-frameshifting");
    }
    if (isExcludeFrameshift()) {
      strs.add("frameshifting");
    }
    if (isExcludeSplicing()) {
      strs.add("splicing");
    }
    if (isExcludeStopGainsLosses()) {
      strs.add("stop gains and losses");
    }
    if (isExcludeSynonymous()) {
      strs.add("synonymous SNPs (all regions)");
    }
    if (isExcludeNonsynonymous()) {
      strs.add("nonsynonymous SNPs");
    }
    if (isExcludeUTR()) {
      strs.add("UTR");
    }
    if (isExcludeExonicSplicing()) {
      strs.add("exonic;splicing");
    }
    if (isExcludeExonic()) {
      strs.add("exonic");
    }
    if (strs.size() == 0) {
      return "No filtering based on variant type was performed.";
    }
    str.append((String)strs.get(0));
    for (int i = 0; i < strs.size(); i++) {
      str.append(", " + (String)strs.get(i));
    }
    return str.toString();
  }
}
