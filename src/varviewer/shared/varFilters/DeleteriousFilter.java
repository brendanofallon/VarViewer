package varviewer.shared.varFilters;

import java.io.Serializable;

import varviewer.shared.Variant;
import varviewer.shared.VariantFilter;

public class DeleteriousFilter implements VariantFilter, Serializable {

	double siftMax = 1.0;
	double polyphenMin = 0.0;
	double mutationTasterMin = 0.0;
	double gerpMin = -10.0;
	double phyloPMin = -10.0;
	double combinedMin = 0.0;
	
	boolean siftEnabled = true;
	boolean polyphenEnabled = true;
	boolean mutationTasterEnabled = true;
	boolean gerpEnabled = true;
	boolean phyloPEnabled = true;
	boolean combinedEnabled = true;
	
	
	
	public boolean isSiftEnabled() {
		return siftEnabled;
	}

	public void setSiftEnabled(boolean siftEnabled) {
		this.siftEnabled = siftEnabled;
	}

	public boolean isPolyphenEnabled() {
		return polyphenEnabled;
	}

	public void setPolyphenEnabled(boolean polyphenEnabled) {
		this.polyphenEnabled = polyphenEnabled;
	}

	public boolean isMutationTasterEnabled() {
		return mutationTasterEnabled;
	}

	public void setMutationTasterEnabled(boolean mutationTasterEnabled) {
		this.mutationTasterEnabled = mutationTasterEnabled;
	}

	public boolean isGerpEnabled() {
		return gerpEnabled;
	}

	public void setGerpEnabled(boolean gerpEnabled) {
		this.gerpEnabled = gerpEnabled;
	}

	public boolean isPhyloPEnabled() {
		return phyloPEnabled;
	}

	public void setPhyloPEnabled(boolean phyloPEnabled) {
		this.phyloPEnabled = phyloPEnabled;
	}

	public boolean isCombinedEnabled() {
		return combinedEnabled;
	}

	public void setCombinedEnabled(boolean combinedEnabled) {
		this.combinedEnabled = combinedEnabled;
	}

	public double getSiftMax() {
		return siftMax;
	}

	public void setSiftMax(double siftMax) {
		this.siftMax = siftMax;
	}

	public double getPolyphenMin() {
		return polyphenMin;
	}

	public void setPolyphenMin(double polyphenMin) {
		this.polyphenMin = polyphenMin;
	}

	public double getMutationTasterMin() {
		return mutationTasterMin;
	}

	public void setMutationTasterMin(double mutationTasterMin) {
		this.mutationTasterMin = mutationTasterMin;
	}

	public double getGerpMin() {
		return gerpMin;
	}

	public void setGerpMin(double gerpMin) {
		this.gerpMin = gerpMin;
	}

	public double getPhyloPMin() {
		return phyloPMin;
	}

	public void setPhyloPMin(double phyloPMin) {
		this.phyloPMin = phyloPMin;
	}

	public double getCombinedMin() {
		return combinedMin;
	}

	public void setCombinedMin(double combinedMin) {
		this.combinedMin = combinedMin;
	}

	@Override
	public boolean variantPasses(Variant var) {
	
		//SIFT score
		boolean passes = true;
		if (siftEnabled) {
			passes = checkLess(var, "sift.score", siftMax);
			if (!passes) {
				return false;
			}
		}
		
		if (polyphenEnabled) {
			passes = checkMore(var, "pp.score", polyphenMin);
			if (!passes) {
				return false;
			}
		}
		
		if (mutationTasterEnabled) {
			passes = checkMore(var, "mt.score", mutationTasterMin);
			if (!passes) {
				return false;
			}
		}
		
		if (gerpEnabled) {
			passes = checkMore(var, "gerp.score", gerpMin);
			if (!passes) {
				return false;
			}
		}
		
		if (phyloPEnabled) {
			passes = checkMore(var, "phylop.score", phyloPMin);
			if (!passes) {
				return false;
			}
		}
		
		if (combinedEnabled) {
			passes = checkMore(var, "svm.effect.prediction", combinedMin);
			if (!passes) {
				return false;
			}
		}
		
		return true;
	}
	
	private static boolean checkLess(Variant var, String key, double max) {
		String score = var.getAnnotation(key);
		if (score != null && (! score.equals("-"))) {
			try {
				Double val = Double.parseDouble(score);
				if (val > max) {
					return false;
				}
			}
			catch (NumberFormatException nfe) {
				//ignore for now
			}
		}
		//Default is to return true
		return true;
	}
	
	private static boolean checkMore(Variant var, String key, double max) {
		String score = var.getAnnotation(key);
		if (score != null && (! score.equals("-"))) {
			try {
				Double val = Double.parseDouble(score);
				if (val < max) {
					return false;
				}
			}
			catch (NumberFormatException nfe) {
				//ignore for now
			}
		}
		//Default is to return true
		return true;
	}
	
}
