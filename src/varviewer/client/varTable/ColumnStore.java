package varviewer.client.varTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import varviewer.client.IGVInterface;
import varviewer.shared.variant.Variant;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Image;

/**
 * Maintains a list of all available Columns that can potentially be used in a ColumnModel
 * and VarTable. 
 * This is a singleton.
 * @author brendan
 *
 */
public class ColumnStore {

	
	private static List<VarAnnotation<?>> cols = new ArrayList<VarAnnotation<?>>();
	
	private static ColumnStore store;
	
	public static ColumnStore getStore() {
		if (store == null) {
			store = new ColumnStore();
		}
		
		return store;
	}
	
	/**
	 * Private constructor, get access to the store statically through ColumnStore.getStore()
	 */
	private ColumnStore() {
		initialize();
		store = this;
	}
	
	/**
	 * Obtain the column associated with the given key
	 * @param key
	 * @return
	 */
	public VarAnnotation<?> getColumnForID(String key) {
		for(VarAnnotation<?> col : cols) {
			if (col.id.equals(key)) {
				return col;
			}
		}
		return null;
	}
	
	/**
	 * Obtain a reference to a list of all potential columns
	 * @return
	 */
	public List<VarAnnotation<?>> getAllColumns() {
		return cols;
	}
	
	private void addColumn(VarAnnotation<?> col) {
		cols.add(col);
	}
	
	private static String format(Double x) {
		if (x==null) {
			return "-";
		}
		String str = x.toString();
		
		if (x<10.0) {
			return str.substring(0, Math.min(str.length(), 5));
		}
		return str;
	}
	
	/**
	 * Creates all possible columns and stores them in a list here....
	 */
	private void initialize() {
		
		
		addColumn(new VarAnnotation<String>("gene", "Gene", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotationStr("gene");
				return val != null ? val : "-";
			}
		}, 1.0));

		VarAnnotation<String> chrAnno = new VarAnnotation<String>("contig", "Chr", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				return var.getChrom();
			}
		}, 0.5); 
		
		chrAnno.setComparator(new Comparator<Variant>() {

			@Override
			public int compare(Variant o1, Variant o2) {
				return o1.getChrom().compareTo(o2.getChrom());
			}
			
		});
		addColumn(chrAnno);

		VarAnnotation<SafeHtml> clinvarAnnotation = new VarAnnotation<SafeHtml>("ClinVar", "ClinVar", new Column<Variant, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(Variant var) {
				String clnsig = var.getAnnotationStr("clinvar.clnsig");
				
				SafeHtmlBuilder bldr = new SafeHtmlBuilder();
				
				if (clnsig == null || clnsig.equals("null")) {
					bldr.appendHtmlConstant("<span style=\"color: gray;\"><b>?</b></span>");
					return bldr.toSafeHtml();
				} 				
				
				String[] sigs = clnsig.split("|");
				Integer[] iSigs = new Integer[sigs.length];
				int maxSig = -1;
				int maxSigIndex = -1;
				for(int i=0; i<sigs.length; i++) {
					try {
						iSigs[i] = Integer.parseInt(sigs[i]);
						if (iSigs[i]>maxSig) {
							maxSig = iSigs[i];
							maxSigIndex = i;
						}
					}
					catch(NumberFormatException nfe) {
						//ignore it.. 
					}
				}
				
				
				//Find disease name...
				String diseaseName = var.getAnnotationStr("clinvar.clndbn");
				if (diseaseName != null && !diseaseName.equals("null")) {
					String[] diseases = diseaseName.split("\\|");
					if (maxSigIndex < diseases.length) {
						diseaseName = diseases[maxSigIndex];
					}
				}

				
				if (diseaseName == null || diseaseName.equals("null")) {
					diseaseName = "?";
				}
				
				diseaseName = diseaseName.replace("_", " ");
				if (maxSig == 5) {
					bldr.appendHtmlConstant("<div><div style=\"color: red;\"><b>Pathogenic</b></div> <div>" + diseaseName + "</div></div>");
				} else if (maxSig == 4) {
					bldr.appendHtmlConstant("<div><div style=\"color: orange;\"><b>Likely Pathogenic</b></div> <div>" + diseaseName + "</div></div>");
				} else if (maxSig ==  3) {
					bldr.appendHtmlConstant("<span style=\"color: blue;\"><b>Likely Benign</b></span>");
				} else if (maxSig == 2) {
					bldr.appendHtmlConstant("<span style=\"color: green;\"><b>Benign</b></span>");
				} else if (maxSig == 255 || maxSig==1 || maxSig==6 || maxSig == 0) {
					bldr.appendHtmlConstant("<span style=\"color: gray;\">VUS</span>");
				}
				
				return bldr.toSafeHtml();
			}
		}, 2.0);
		addColumn(clinvarAnnotation);
		
		VarAnnotation<String> posAnnotation = new VarAnnotation<String>("pos", "Start", new TextColumn<Variant>() {
			@Override
			public String getValue(Variant var) {
				return "" + var.getPos();
			}
		}, 1.0, new PositionComparator());
		
		
		//BCR-ABL specific
		VarAnnotation<String> inVitroAnnotation = new VarAnnotation<String>("InVitro", "In Vitro (BCR-ABL)", new TextColumn<Variant>() {
			@Override
			public String getValue(Variant var) {
				return var.getAnnotationStr("InVitro");
			}
		}, 1.0);
		addColumn(inVitroAnnotation);
		
		VarAnnotation<String> bcrAblKnownAnnotation = new VarAnnotation<String>("Known", "Known (BCR-ABL)", new TextColumn<Variant>() {
			@Override
			public String getValue(Variant var) {
				return var.getAnnotationStr("Known");
			}
		}, 1.0);
		addColumn(bcrAblKnownAnnotation);
		
		
		posAnnotation.setComparator(new Comparator<Variant>() {

			@Override
			public int compare(Variant v0, Variant v1) {
				if (v0.getChrom().equals(v1.getChrom())) {
					return v1.getPos() - v0.getPos();
				}
				else {
					return v0.getChrom().compareTo(v1.getChrom());
				}
			}
			
		});
		addColumn(posAnnotation);
		
//		addColumn(new VarAnnotation<String>("zygosity", "Zygosity", new TextColumn<Variant>() {
//
//			@Override
//			public String getValue(Variant var) {
//				String val = var.getAnnotation("zygosity");
//			
//				if (val == null || val.length()<2)
//					return "?";
//					
//				return val;
//			}
//		}, 1.0, false));

		
		addColumn(new VarAnnotation<ImageResource>("bad.region", "NextSeq Flagged Region", new Column<Variant, ImageResource>(new ImageResourceCell()) {

			@Override
			public ImageResource getValue(Variant var) {
				String bad = var.getAnnotationStr("bad.region");
								
				if (bad != null && bad.equals("true"))
					return resources.rainbowImage();
				
				return null;
			}
			
		}, 0.5));

		
		addColumn(new VarAnnotation<ImageResource>("zygosity", "Zygosity", new Column<Variant, ImageResource>(new ImageResourceCell()) {

			@Override
			public ImageResource getValue(Variant var) {
				String zyg = var.getAnnotationStr("zygosity");
								
				if (zyg == null || zyg.equals("ref"))
					return resources.refImage();
				if (zyg.equals("het"))
					return resources.hetImage();
				if (zyg.equals("hom"))
					return resources.homImage();
				
				return resources.refImage();
			}
			
		}, 0.6));


		addColumn(new VarAnnotation<String>("exon.function", "Exon effect", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotationStr("exon.function");
				if (val == null || val.equals("-")) {
					val = var.getAnnotationStr("variant.type");
				}
				return val != null ? val : "-";
			}
		}, 2.0));

		addColumn(new VarAnnotation<String>("nm.number", "NM Number", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotationStr("nm.number");
				return val != null ? val : "-";
			}
		}, 2.0));

		addColumn(new VarAnnotation<String>("cdot", "c.dot", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotationStr("cdot");
				String usingPreferred = var.getAnnotationStr("non.preferred.transcript");
				String append = "";
				if (usingPreferred != null && usingPreferred.toLowerCase().equals("false")) {
					append = "*";
				}
				return val != null ? val+append : "-" +append;
			}
		}, 2.0));

		addColumn(new VarAnnotation<String>("pdot", "p.dot", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotationStr("pdot");
				return val != null ? val : "-";
			}
		}, 2.0));
		
	    addColumn(new VarAnnotation("variant.type", "variant.type", new TextColumn<Variant>()
	    {
	      public String getValue(Variant var)
	      {
	        String val = var.getAnnotationStr("variant.type");
	        return val != null ? val : "-";
	      }
	    }, 2.0D));
	    
		addColumn(new VarAnnotation<String>("ref", "Ref.", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				return var.getRef();
			}
		}, 1.0));
		
		
		addColumn(new VarAnnotation<SafeHtml>("omim.inheritance", "OMIM Inheritance",new Column<Variant, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(Variant var) {
				String inheritance = var.getAnnotationStr("omim.inheritance");
				SafeHtmlBuilder bldr = new SafeHtmlBuilder();
							
				if (inheritance == null || inheritance.equals("null") || inheritance.equals("UNKNOWN")) {
					bldr.appendHtmlConstant("<span style=\"color: gray;\"><b>?</b></span>");
				}
				if (inheritance.toLowerCase().equals("recessive")) {
					bldr.appendHtmlConstant("<span style=\"color: blue;\"><b>AR</b></span>");
				}
				if (inheritance.toLowerCase().equals("dominant")) {
					bldr.appendHtmlConstant("<span style=\"color: red;\"><b>AD</b></span>");
				}
				if (inheritance.toLowerCase().equals("xlinked")) {
					bldr.appendHtmlConstant("<span style=\"color: magenta;\"><b>1</b></span>");
				}
				if (inheritance.equals("BOTH")) {
					bldr.appendHtmlConstant("<span style=\"color: blue;\"><b>AR</b></span>, ");
					bldr.appendHtmlConstant("<span style=\"color: red;\"><b>AD</b></span>");
				}
				return bldr.toSafeHtml();
			}
		}, 1.0));
		
		addColumn(new VarAnnotation<String>("alt", "Alt.", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				return var.getAlt();
			}
		}, 1.0));

		VarAnnotation<SafeHtml> scSnvRFCol =new VarAnnotation<SafeHtml>("scSNV.rf", "Splicing Impact (RF)", new Column<Variant, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(Variant var) {
				Double val = var.getAnnotationDouble("scSNV.rf_score");
				SafeHtmlBuilder bldr = new SafeHtmlBuilder();
				
				if (val==null) {
					bldr.appendHtmlConstant("<span style=\"color: gray;\">-</span>");	
				} else if (val < 0.5) {
					bldr.appendHtmlConstant("<span style=\"color: green;\">" + format(val) + "</span>");
				} else if (val < 0.7) {
					bldr.appendHtmlConstant("<span style=\"color: blue;\">" + format(val) + "</span>");
				} else {
					bldr.appendHtmlConstant("<span style=\"color: red;\"><b>" + format(val) + "</b></span>");	
				}
								
				return bldr.toSafeHtml();
			}
		}, 1.0) ;
		scSnvRFCol.setComparator(new Comparator<Variant>() {

			@Override
			public int compare(Variant arg0, Variant arg1) {
				Double val0 = arg0.getAnnotationDouble("scSNV.rf_score");
				Double val1 = arg1.getAnnotationDouble("scSNV.rf_score");
				if (val0==null)
					val0 = 0.0;
				if (val1 == null)
					val1 = 0.0;
				return val0.compareTo(val1);
			}
		});
		addColumn(scSnvRFCol);
		
		VarAnnotation<SafeHtml> scSnvADACol = new VarAnnotation<SafeHtml>("scSNV.ada", "Splicing Impact (ADA)", new Column<Variant, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(Variant var) {
				Double val = var.getAnnotationDouble("scSNV.ada_score");
				SafeHtmlBuilder bldr = new SafeHtmlBuilder();
				if (val==null) {
					bldr.appendHtmlConstant("<span style=\"color: gray;\">-</span>");	
				} else if (val < 0.5) {
					bldr.appendHtmlConstant("<span style=\"color: green;\">" + format(val) + "</span>");
				} else if (val < 0.7) {
					bldr.appendHtmlConstant("<span style=\"color: blue;\">" + format(val) + "</span>");
				} else {
					bldr.appendHtmlConstant("<span style=\"color: red;\"><b>" + format(val) + "</b></span>");	
				}
								
				return bldr.toSafeHtml();
			}
		}, 1.0);
		scSnvADACol.setComparator(new Comparator<Variant>() {

			@Override
			public int compare(Variant arg0, Variant arg1) {
				Double val0 = arg0.getAnnotationDouble("scSNV.ada_score");
				Double val1 = arg1.getAnnotationDouble("scSNV.ada_score");
				if (val0==null)
					val0 = 0.0;
				if (val1 == null)
					val1 = 0.0;
				return val0.compareTo(val1);
			}
		});
		addColumn(scSnvADACol);
		
		addColumn(new VarAnnotation<String>("quality", "Quality", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double val = var.getAnnotationDouble("quality");
				return val != null ? val.toString() : "-";
			}
		}, 1.0));

		addColumn(new VarAnnotation<String>("depth", "Depth", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double val = var.getAnnotationDouble("depth");
				return val != null ? val.toString() : "-";
			}
		}, 1.0));
		
		addColumn(new VarAnnotation<String>("var.freq", "Alt. Freq", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double tot = var.getAnnotationDouble("depth");
				Double alt = var.getAnnotationDouble("var.depth");
				if (tot != null && alt != null && tot > 0) {
					
						double freq = alt / tot;
						String freqStr = "" + freq;
						if (freqStr.length() > 5) {
							freqStr = freqStr.substring(0, 4);
						}
						return freqStr;
					
					
				}
				return "-";
			}
		}, 1.0));
		
		
		
//		addColumn(new VarAnnotation<SafeHtml>("varbin.bin", "VarBin", new Column<Variant, SafeHtml>(new SafeHtmlCell()) {
//
//			@Override
//			public SafeHtml getValue(Variant var) {
//				SafeHtmlBuilder bldr = new SafeHtmlBuilder();
//				Double val = var.getAnnotationDouble("varbin.bin");
//				
//				if (val == null) {
//					bldr.appendEscaped("-");
//				}
//				else {
//					if (val.equals(1)) {
//						bldr.appendHtmlConstant("<span style=\"color: #003300;\"><b>1</b></span>");	
//					}
//					if (val.equals(2)) {
//						bldr.appendHtmlConstant("<span style=\"color: #996600;\"><b>2</b></span>");	
//					}
//					if (val.equals(3)) {
//						bldr.appendHtmlConstant("<span style=\"color: #990000;\"><b>3</b></span>");	
//					}
//					if (val.equals(4)) {
//						bldr.appendHtmlConstant("<span style=\"color: #FF0000;\"><b>4</b></span>");	
//					}
//					
//				}
//				return bldr.toSafeHtml();
//			}
//			
//		}, 1.0));

		VarAnnotation<SafeHtml> exacHetHomCol =new VarAnnotation<SafeHtml>("exac.hethom", "ExAC 63K Het / Hom", new Column<Variant, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(Variant var) {
				SafeHtmlBuilder bldr = new SafeHtmlBuilder();
				
				Double homVal = var.getAnnotationDouble("exac63K.overall.hom.freq");
				Double hetVal = var.getAnnotationDouble("exac63K.overall.het.freq");
				
				if (homVal == null) {
					homVal = 0.0;
				}
				if (hetVal == null) {
					hetVal = 0.0;
				}
				
				//Truncating... 
				homVal = ((int)(homVal * 10000.0))/10000.0;
				hetVal = ((int)(hetVal * 10000.0))/10000.0;
				
				String linkTarget = var.getChrom() + "-" + var.getPos() + "-" + var.getRef() + "-" + var.getAlt();
				
				bldr.appendHtmlConstant("<a href=\"http://exac.broadinstitute.org/variant/" + linkTarget + "\" target=\"_blank\"> Het: " + hetVal + " Hom: " + homVal + "</a>" );
				
				return bldr.toSafeHtml();
			}
		}, 1.5);
		addColumn(exacHetHomCol);
		
		VarAnnotation<String> exacFreqCol =new VarAnnotation<String>("exac.freq", "ExAC 63K Exomes", new TextColumn<Variant>() {
			@Override
			public String getValue(Variant var) {
				Double val = var.getAnnotationDouble("exac63K.overall.allele.freq");
				return val != null ? format(val) : "0";
			}
			
		}, 1.0);
		exacFreqCol.setComparator(new Comparator<Variant>() {

			@Override
			public int compare(Variant arg0, Variant arg1) {
				Double val0 = arg0.getAnnotationDouble("exac63K.overall.allele.freq");
				Double val1 = arg1.getAnnotationDouble("exac63K.overall.allele.freq");
				if (val0==null)
					val0 = 0.0;
				if (val1 == null)
					val1 = 0.0;
				return val0.compareTo(val1);
			}
		});
		addColumn(exacFreqCol);
		
		VarAnnotation<String> exacHomCountCol = new VarAnnotation<String>("ExAC Hom Count", "ExAC Hom Count", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double count = var.getAnnotationDouble("exac63K.overall.hom.count");
				if (count==null) {
					return "";
				}
				return "" + count.intValue();
			}
		}, 1.0);
		addColumn(exacHomCountCol);
		exacHomCountCol.setComparator(new Comparator<Variant>() {

			@Override
			public int compare(Variant arg0, Variant arg1) {
				Double val0 = arg0.getAnnotationDouble("exac63K.overall.hom.count");
				Double val1 = arg1.getAnnotationDouble("exac63K.overall.hom.count");
				if (val0==null)
					val0 = 0.0;
				if (val1 == null)
					val1 = 0.0;
				return val0.compareTo(val1);
			}
		});
		
		VarAnnotation<String> uk10KFreqCol =new VarAnnotation<String>("uk10k.frequency", "UK10K Frequency", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double val = var.getAnnotationDouble("uk10k.frequency");
				return val != null ? format(val) : "0";
			}
		}, 1.0);
		uk10KFreqCol.setComparator(new Comparator<Variant>() {

			@Override
			public int compare(Variant arg0, Variant arg1) {
				Double val0 = arg0.getAnnotationDouble("uk10k.frequency");
				Double val1 = arg1.getAnnotationDouble("uk10k.frequency");
				if (val0==null)
					val0 = 0.0;
				if (val1 == null)
					val1 = 0.0;
				return val0.compareTo(val1);
			}
		});
		addColumn(uk10KFreqCol);
		
		
		
		VarAnnotation<String> popFreqCol =new VarAnnotation<String>("pop.freq", "Pop. Freq.", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double val = var.getAnnotationDouble("pop.freq");
				return val != null ?format(val) : "0";
			}
		}, 1.0);
		
		
		//Null population frequency values should be converted to zero
		popFreqCol.setComparator(new Comparator<Variant>() {

			@Override
			public int compare(Variant arg0, Variant arg1) {
				Double val0 = arg0.getAnnotationDouble("pop.freq");
				Double val1 = arg1.getAnnotationDouble("pop.freq");
				if (val0==null)
					val0 = 0.0;
				if (val1 == null)
					val1 = 0.0;
				return val0.compareTo(val1);
			}
		});
		addColumn(popFreqCol);
		
		
		VarAnnotation<String> popFreqMAFCol =new VarAnnotation<String>("pop.freq.maf", "Pop. Freq. MAF", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double val = var.getAnnotationDouble("pop.freq");
				if (val == null)
					val = 0.0;
				String retVal = "" + Math.min(val,  1.0-val);
				if (retVal.length()>6) {
					retVal = retVal.substring(0, 6);
				}
				return retVal; 
			}
		}, 1.0);
		
		
		VarAnnotation<String> espFreqCol =new VarAnnotation<String>("exomes5400.frequency", "6500 Exomes", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double val = var.getAnnotationDouble("exomes5400.frequency");
				if (val == null) {
					val = var.getAnnotationDouble("exomes6500.frequency");
				}
				return val != null ? format(val) : "0";
			}
		});
		addColumn(espFreqCol);
		

		VarAnnotation<String> espGenotypesCol =new VarAnnotation<String>("exomes5400.genotypes", "6500 Exomes Details", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double eaHet = var.getAnnotationDouble("exomes5400.EA.het");
				Double eaHomAlt = var.getAnnotationDouble("exomes5400.EA.homalt");
				Double aaHet = var.getAnnotationDouble("exomes5400.AA.het");
				Double aaHomAlt = var.getAnnotationDouble("exomes5400.AA.homalt");
				String eaHetStr =  eaHet != null ? (100.0*eaHet)+"" : "0.0";
				String eaHomAltStr =  eaHomAlt != null ? (100.0*eaHomAlt) + "" : "0.0";
				String aaHetStr =  aaHet != null ? (100.0*aaHet)+"" : "0.0";
				String aaHomAltStr =  aaHomAlt != null ? (100.0*aaHomAlt)+"" : "0.0";
				if (eaHetStr.length()>4) {
					eaHetStr = eaHetStr.substring(0, 4);
				}
				if (eaHomAltStr.length()>4) {
					eaHomAltStr = eaHomAltStr.substring(0, 4);
				}
				if (aaHetStr.length()>4) {
					aaHetStr = aaHetStr.substring(0, 4);
				}
				if (aaHomAltStr.length()>4) {
					aaHomAltStr = aaHomAltStr.substring(0, 4);
				}
				
				return "Het/Homs EA: " + eaHetStr + "/" + eaHomAltStr + " AA: " + aaHetStr + "/" + aaHomAltStr; 
			}
		}, 2.0);
		addColumn(espGenotypesCol);
		
		VarAnnotation<String> espHomozygousCol =new VarAnnotation<String>("exome6500.hom.frequency", "6500 Exomes Homozygous freq.", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double homFreq = var.getAnnotationDouble("exomes6500.homalt.frequency");
				
				if (homFreq == null) {
					homFreq = 0.0;
				}
				String homFreqStr = homFreq.toString();
				if (homFreqStr.length()>5) {
					homFreqStr = homFreqStr.substring(0, 5);
				}
				
				return homFreqStr; 
			}
		}, 1.0);
		addColumn(espHomozygousCol);
		
		VarAnnotation<String> espEADetailsCol =new VarAnnotation<String>("exome6500.EA.details", "6500 Exomes Eur. Am. Genotypes (Het / Hom)", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double eaHet = var.getAnnotationDouble("exomes6500.EA.het");
				Double eaHomAlt = var.getAnnotationDouble("exomes6500.EA.homalt");
				
				String eaHetStr =  eaHet != null ? (100.0*eaHet)+"" : "0.0";
				String eaHomAltStr =  eaHomAlt != null ? (100.0*eaHomAlt) + "" : "0.0";
				
				if (eaHetStr.length()>4) {
					eaHetStr = eaHetStr.substring(0, 4);
				}
				if (eaHomAltStr.length()>4) {
					eaHomAltStr = eaHomAltStr.substring(0, 4);
				}
				
				
				return eaHetStr + " / " + eaHomAltStr; 
			}
		}, 1.0);
		addColumn(espEADetailsCol);
		
		VarAnnotation<String> espAADetailsCol =new VarAnnotation<String>("exome6500.AA.details", "6500 Exomes Afr. Am. Genotypes (Het / Hom)", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double aaHet = var.getAnnotationDouble("exomes6500.AA.het");
				Double aaHomAlt = var.getAnnotationDouble("exomes6500.AA.homalt");
				
				String aaHetStr =  aaHet != null ? (100.0*aaHet)+"" : "0.0";
				String aaHomAltStr =  aaHomAlt != null ? (100.0*aaHomAlt) + "" : "0.0";
				
				if (aaHetStr.length()>4) {
					aaHetStr = aaHetStr.substring(0, 4);
				}
				if (aaHomAltStr.length()>4) {
					aaHomAltStr = aaHomAltStr.substring(0, 4);
				}
				
				
				return  aaHetStr + " / " + aaHomAltStr; 
			}
		}, 1.0);
		addColumn(espAADetailsCol);
		
		//Null population frequency values should be converted to zero
		popFreqMAFCol.setComparator(new Comparator<Variant>() {

			@Override
			public int compare(Variant arg0, Variant arg1) {
				Double val0 = arg0.getAnnotationDouble("pop.freq");
				Double val1 = arg1.getAnnotationDouble("pop.freq");
				if (val0==null)
					val0 = 0.0;
				if (val1 == null)
					val1 = 0.0;
				val0 = Math.min(val0,  1.0-val0);
				val1 = Math.min(val1,  1.0-val1);
				return val0.compareTo(val1);
			}
		});
		addColumn(popFreqMAFCol);
		
		
		addColumn(new VarAnnotation<String>("arup.freq", "ARUP count", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotationStr("ARUP.freq");
				if (val.equals("-"))
					val = "0";
				return val != null ? val : "0";
			}
		}, 2.0));

		addColumn(new VarAnnotation<String>("ARUP.overall.freq", "ARUP Freq.", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double val = var.getAnnotationDouble("ARUP.overall.freq");
				if (val == null) {
					return "NA";
				}
				
				val *= 100.0;
				String retStr = "" + val;
				
				if (retStr.length()>3)
					retStr = retStr.substring(0,4);
				return retStr + "%";
			}
		}, 1.0));
		
		addColumn(new VarAnnotation<String>("ARUP.freq.details", "ARUP details", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotationStr("ARUP.freq.details");
				if (val == null) {
					return "NA";
				}
				return val;
			}
		}, 2.0));
		
		addColumn(new VarAnnotation<String>("sift.score", "SIFT score", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double val = var.getAnnotationDouble("sift.score");
				return val != null ? format(val) : "NA";
			}
		}, 1.0));
		
		addColumn(new VarAnnotation<String>("mt.score", "MutationTaster score", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double val = var.getAnnotationDouble("mt.score");
				return val != null ? format(val) : "NA";
			}
		}, 1.0));
		
		addColumn(new VarAnnotation<String>("gerp.score", "GERP++ score", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double val = var.getAnnotationDouble("gerp.score");
				return val != null ? format(val) : "NA";
			}
		}, 1.0));
		
		addColumn(new VarAnnotation<SafeHtml>("rsnum", "dbSNP #", new Column<Variant, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(Variant var) {
				SafeHtmlBuilder bldr = new SafeHtmlBuilder();
				String val = var.getAnnotationStr("rsnum");
				if (val == null || val.length() < 2) {
					bldr.appendEscaped("-");
				}
				else {
					bldr.appendHtmlConstant("<a href=\"http://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=" + val.replace("rs",  "") + "\" target=\"_blank\">" + val + "</a>" );
				}
				return bldr.toSafeHtml();
			}
			
		}, 1.0));
		
		addColumn(new VarAnnotation<String>("pp.score", "PolyPhen-2 score", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				Double val = var.getAnnotationDouble("pp.score");
				return val != null ? format(val) : "NA";
			}
		}, 1.0));
		
		addColumn(new VarAnnotation<String>("omim.num", "OMIM #", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotationStr("omim.disease.ids");
				return val != null ? val : "0";
			}
		}, 2.0));
		
		
		addColumn(new VarAnnotation<ImageResource>("disease.2.pics", "HGMD Exact Variant", new Column<Variant, ImageResource>(new ImageResourceCell()) {

			@Override
			public ImageResource getValue(Variant var) {
				String hgmdExact = var.getAnnotationStr("hgmd.exact.hit");
												
				ImageResource img = null;
				if (hgmdExact == "true" || hgmdExact == "True" || hgmdExact == "TRUE") {
						img = resources.hgmdHitImage();
					}

				return img;
			}
			
		}, 1.0, null));
		
		
		addColumn(new VarAnnotation<String>("hgmd.class", "HGMD Class", new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotationStr("hgmd.class");
				return val != null ? val : "0";
			}
		}, 2.0));
		
		addColumn(new VarAnnotation<ImageResource>("disease.pics", "HGMD Pos & OMIM", new Column<Variant, ImageResource>(new ImageResourceCell()) {

			@Override
			public ImageResource getValue(Variant var) {
				String hgmdExact = var.getAnnotationStr("hgmd.hit");
				String hgmdGeneMatch = var.getAnnotationStr("hgmd.info");
				String omimGeneMatch = var.getAnnotationStr("omim.disease");
				
				boolean hasHGMDExact = hgmdExact != null && hgmdExact.length() > 3;
				boolean hasHGMDGene = hgmdGeneMatch != null && hgmdGeneMatch.length() > 3;
				boolean hasOmim = omimGeneMatch != null && omimGeneMatch.length() > 3;
				
				//System.out.println(var.getAnnotationStr("gene") + " : " + var.getAnnotationStr("cdot") + "hgmdExact: " +hgmdExact + " hgmdGene: " + hgmdGeneMatch + " omim: " + omimGeneMatch);
				
				ImageResource img = null;
				if (hasHGMDExact) {
					if (hasOmim) {
						//Has all 3
						img = resources.hgmdHitHgmdOmimImage();
					}
					else {
						//No omim, but has hgmd exact and gene match
						img = resources.hgmdHitHgmdImage();
					}
				}
				else {
					//No exact hit
					if (hasHGMDGene) {
						if (hasOmim) {
							img = resources.hgmdOmimImage();
						}
						else {
							//No OMIM, just hgmd gene match
							img = resources.hgmdOnlyImage();
						}
					}
					else {
						if (hasOmim) {
							img = resources.omimOnlyImage();
						}
						else {
							//nothing, img is null
						}
					}
				}

				return img;
			}
			
		}, 1.0, null));
	
//		ButtonImageCell commentButton = new ButtonImageCell(new Image("images/comment-icon.png"));
//		VarAnnotation<String> commentVarAnno =new VarAnnotation<String>("comment", "Notes", new Column<Variant, String>(commentButton) {
//
//			@Override
//			public String getValue(Variant var) {
//				//Somehow get comment info from Variant - is it an annotation?
//				
//				return "huh?";
//			}
//			
//		}, 0.6);
//		
//		commentVarAnno.col.setFieldUpdater(new FieldUpdater<Variant, String>() {
//
//			@Override
//			public void update(int index, Variant var, String value) {
//				//TODO Show comment popup? 
//			}
//			
//		});
//		addColumn(commentVarAnno);
		
		
		IGVCell igvCell = new IGVCell();
		
		addColumn(new VarAnnotation<String>("igv.link", "IGV", new Column<Variant, String>(igvCell) {

			@Override
			public String getValue(Variant var) {
				String locus = "chr" + var.getChrom() + ":" + var.getPos();
				return locus;
			}
			
		}, 1.0, null));
			
	}
	
	public class ButtonImageCell extends ButtonCell{

		final Image image;
		
		public ButtonImageCell(Image image) {
			this.image = image;
		}
		
	    @Override
	    public void render(com.google.gwt.cell.client.Cell.Context context, 
	            String value, SafeHtmlBuilder sb) {
	        SafeHtml html = SafeHtmlUtils.fromTrustedString(image.toString());
	        sb.append(html);
	    }
	}
	
	static class IGVCell extends ButtonCell {
		
		public void render(Cell.Context context, String value, SafeHtmlBuilder sb) {
			sb.appendHtmlConstant("<a href=\"" + IGVInterface.baseURL + "goto?locus=" + value + "\" target=\"_self\"><img src=\"images/linkIcon.png\"/></a>" );
		}
	}
	
	static final VarPageResources resources = (VarPageResources) GWT.create(VarPageResources.class);
	
	
}
