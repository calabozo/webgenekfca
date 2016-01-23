package es.uc3m.tsc.general;

public class Constants {
	public static final int ColTypeMask=0x1;
	public static final int GeneExpTypeMask=0x1E; //11110
	
	private static int getMaskedValue(int id, int tmpMask){
		int ret=id;
		while ((tmpMask&0x1)==0){
			ret=ret<<1;
			tmpMask=tmpMask>>1;
		}
		return ret;
	}
	
	private static int getUnMaskedValue(int id, int tmpMask){
		int ret=id&tmpMask;
		while ((tmpMask&0x1)==0){
			ret=ret>>1;
			tmpMask=tmpMask>>1;
		}
		return ret;
	}

	public enum ColType{
		FULL_EXPLORATION(0),PARTIAL_EXPLORATION(1);
		int id;
		private ColType(int id){
			this.id=id;
		}
		public int getValue(){
			return this.id;
		}
		public int getMaskedValue(){
			return Constants.getMaskedValue(this.id, ColTypeMask);			
		}
	}
	
	public enum GeneExpType{
		PROBESET(0),GEXP_AVERAGE(1),GEXP_SUMMARY(2),GEXP_RANDOM(3),
		GEXP_MEAN(4),GEXP_VARIANCE(5),GEXP_CORRELATION(6),GEXP_ENTROPY(7),GEXP_UNKNOWN(99);
		int id;
		private GeneExpType(int id){
			this.id=id;
		}
		public int getValue(){
			return this.id;
		}
		public int getMaskedValue(){
			return Constants.getMaskedValue(this.id, GeneExpTypeMask);			
		}
		public static GeneExpType getEnum(int type){
			GeneExpType myExp=GeneExpType.PROBESET;
			GeneExpType[] enums=myExp.getDeclaringClass().getEnumConstants();
			type=getUnMaskedValue(type,GeneExpTypeMask);
			for (GeneExpType m:enums){
				if (m.getValue()==type){
					return m;
				}
			}
			return null;
		}
		public String getGeneExpName(){    	    	
			switch (this){			
			case GEXP_AVERAGE:
				return "Average";
			case GEXP_SUMMARY:
				return "Summary";
			case GEXP_RANDOM:
				return "Random";
			case GEXP_MEAN:
				return "Mean";
			case GEXP_VARIANCE:
				return "Variance";
			case GEXP_CORRELATION:
				return "Correlation";
			case GEXP_ENTROPY:
				return "Entropy";
			case GEXP_UNKNOWN:
				return "Gene expresion value";
			case PROBESET:
			default:
				return "Raw probeset value";			
			}    	
	    }
		
		public String getGeneExpDescription(){    	    	
			switch (this){			
			case GEXP_UNKNOWN:
				return "The Gene Expression was calculated before uploading the data to this application.";
			case GEXP_AVERAGE:
				return "There are multiple probesets corresponding to a gene. These probesets are averaged to represent the gene expression.";
			case GEXP_SUMMARY:
				return "Summary";
			case GEXP_RANDOM:
				return "There are multiple probesets corresponding to a gene. From these probesets one is randomly selected.";
			case GEXP_MEAN:
				return "There are multiple probesets corresponding to a gene. Only one probeset is selected, which is the one that has the maximum mean value among all the experiments.";
			case GEXP_VARIANCE:
				return "There are multiple probesets corresponding to a gene. The selected probeset is the one which has tha biggest variance.";
			case GEXP_CORRELATION:
				return "There are multiple probesets corresponding to a gene. The correlation matrix is calculated among all the different probesets, then it is summarized. The probeset with the highest cumulative correlation is selected.";
			case GEXP_ENTROPY:
				return "There are multiple probesets corresponding to a gene. The entropy of each probeset is calculated and the probeset with the highest entropy is selected.";
			case PROBESET:
			default:
				return "Raw probeset value, many probesets can correspond to a same gene.";
			}    	
	    }
	}
	
	public static final int maximumCols=9;  //Threshold to reduce number concepts 
	public static final int maximumColsExploration=12; //Threshold to use fast approximate sweep
	public static final int maximumNumberConcepts=64; //Maximum number of concepts to plot if maximumCols is exceeded
	public static final int maximumPValuesToCalculate=200; //Maximum values to return from a Cluster pValue
	public static final int numPhisToCalculateHomogeneitySeparation=20;
	public static final long maximumMilisecondsForThreadRefresh=10000; //Maximum number of milisecons between refresh from javascript.
	public static final long periodToRunThreadSupervisor=3000; //Time to wait in ms between runs of ThreadSupervisor
}
