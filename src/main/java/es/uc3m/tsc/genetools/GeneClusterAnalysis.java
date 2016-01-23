package es.uc3m.tsc.genetools;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import es.uc3m.tsc.gene.DataTypeEnum;
import es.uc3m.tsc.general.Constants;
import es.uc3m.tsc.kfca.explore.KFCAResults;
import es.uc3m.tsc.math.ClusterAnalysis;
import es.uc3m.tsc.threads.ThreadCalcPValue;

public class GeneClusterAnalysis extends ClusterAnalysis {
		

	public GeneClusterAnalysis(KFCAResults kfcaResults) {
		super(kfcaResults);
		// TODO Auto-generated constructor stub
	}

	public GOpValue[] getGOpValue(List<String> genes){
		return this.getGOpValue(genes,null);
	}
	public GOpValue[] getGOpValue(List<String> genes, ThreadCalcPValue th){
		HashMap<String,GOpValue> GOHash=new HashMap<String,GOpValue>();
		String goID;
		GOpValue gopValue;		
		GeneInfo geneInfo=this.kfcaResults.getGeneInfo();
		DataTypeEnum microArrayType=this.kfcaResults.getMicroArrayType();
		GeneDescription genDescription;
		List<GODescription> goList;
		boolean isGeneExpression=this.kfcaResults.getIsGeneExpression();
		double ratioDenominator=(4.0*(double)genes.size());
		double k=0;
		for (String gen:genes){
			if (th!=null){
				if (th.getShouldStop()) return null;
				th.setRatioDone(k++/ratioDenominator);
			}
			
			if (isGeneExpression){
				genDescription=geneInfo.getGenesFromGeneName(microArrayType, gen);
				if (genDescription!=null) goList=genDescription.getAllGO();
				else continue;
			}else{
				//genDescription=geneInfo.getGeneDescription(microArrayType, gen);
				//goList=genDescription.getAllGO();
				goList=geneInfo.findGeneOntologyFromProbeSetID(microArrayType, gen);	
			}
			
			HashSet<String> usedGOIDs=new HashSet<String>();
			for (GODescription go: goList){
				goID=go.getGoID();
				gopValue=GOHash.get(goID);
				if (gopValue==null){
					int f;
					if (isGeneExpression){
						f=geneInfo.getNumGenesForGeneOntology(microArrayType, goID);
					}else{
						f=geneInfo.getNumProbeSetsForGeneOntology(microArrayType, goID);
					}
					gopValue=new GOpValue(goID,go.getDescription(),f,go.getGoDomain());
					GOHash.put(goID, gopValue);					
				}
				if (!usedGOIDs.contains(goID)){					
					gopValue.addGene(gen);
					usedGOIDs.add(goID);
				}
			}			
		}
		
		GOpValue[] goTerms = new GOpValue[GOHash.size()];
		int genomeSize;
		if (isGeneExpression){
			genomeSize=geneInfo.getNumGenes(microArrayType).intValue();
		}else{
			genomeSize=geneInfo.getNumProbeSets(microArrayType).intValue();			
		}
		int clusterSize=genes.size();
		int i=0;
		k=goTerms.length;
		ratioDenominator=(4.0*(double)goTerms.length);
		for (GOpValue g: GOHash.values()){
			if (th!=null){
				if (th.getShouldStop()) return null;
				k+=3;
				th.setRatioDone(k++/ratioDenominator);				
			}
			//g.setPValue(g.getNumTotal()/(g.getNumAppear()+1)); //FAke past approximation to pValue
			g.calcpValue(clusterSize, genomeSize);
			goTerms[i++]=g;
		}
		
		GopValueComparator hashComparator=new GopValueComparator();		
		Arrays.sort(goTerms,hashComparator);
		
		if (goTerms.length>Constants.maximumPValuesToCalculate){
			goTerms=Arrays.copyOf(goTerms,Constants.maximumPValuesToCalculate);
		}
		/*
		for (GOpValue g:goTerms){
			g.calcpValue(clusterSize, genomeSize);
		}
		
		//return Arrays.copyOf(goTerms, 100);

		 */
		return goTerms;
	}
	
	public static class  GopValueComparator implements Comparator<GOpValue>{
		
	    @Override
	    public int compare(GOpValue s1,GOpValue s2) {	    	
	        double r=s1.pValue-s2.pValue;
	        if (r>0) return 1;
	        if (r<0) return -1;
	        return 0;
 	    }
	}
}
