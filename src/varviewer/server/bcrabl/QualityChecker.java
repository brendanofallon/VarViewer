package varviewer.server.bcrabl;

import java.io.File;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import varviewer.shared.SampleInfo;

public class QualityChecker {

	
	public QualityCheckResult verifyQuality(SampleInfo info, File inputBAMFile) {
		
		SAMFileReader inputBAM = new SAMFileReader(inputBAMFile);
		SAMRecordIterator sit = inputBAM.query("ABL1", 0, 1500, false);
		MappedRead mapped;
		char baseAtRef1, baseAtRef2;
		double bothAlts=0, bothRefs=0, alt1Only=0, alt2Only=0, misc=0,
				readCov=0;
		SAMRecord samRecord = sit.next();


		long totalReads = 0;
		long goodReads = 0;


		while(samRecord != null) {
			totalReads++;
			if (samRecord.getMappingQuality() > 20) {
				goodReads++;
			}

			if (sit.hasNext()) {
				samRecord = sit.next();
			}
			else {
				samRecord = null;
			}

		}
		inputBAM.close();


		double fractionGood = goodReads / totalReads;
		boolean passed = false;
		String message = null;
		if (fractionGood > 0.50 && totalReads > 5000) {
			passed = true;
			message = "Total reads: " + totalReads + " Mapped Reads: " + goodReads;
		}
		else {
			passed = false;
			message = "Total reads: " + totalReads + " Mapped Reads: " + goodReads + " Fraction of unmapped reads:" + fractionGood;
		}

		QualityCheckResult result = new QualityCheckResult(passed, message);
		return result;
	}
}

