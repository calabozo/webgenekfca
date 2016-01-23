package es.uc3m.tsc.kfca.explore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
public class KFCAObjectExplored implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	String objectId;
		
	double[] maxplusPhi; //Phi value which corresponds with a concept	
	long[]	maxplusConceptId; //Maxplus concept identifier			
	double[] minplusVarphi; //VarPhi value which corresponds with a concept	
	long[]	minplusConceptId; //Minplus concept identifier
		
	List<Double> lmaxplusPhi; //Phi value which corresponds with a concept	
	List<Long>	lmaxplusConceptId; //Maxplus concept identifier		
	List<Double> lminplusVarphi; //VarPhi value which corresponds with a concept	
	List<Long>	lminplusConceptId; //Minplus concept identifier
	
	
	public KFCAObjectExplored(){
	 //	
	}
	
	public KFCAObjectExplored(String objectId){		
		this.objectId=objectId;
		this.lmaxplusPhi=new ArrayList<Double>();
		this.lminplusVarphi=new ArrayList<Double>();		
		this.lmaxplusConceptId=new ArrayList<Long>();
		this.lminplusConceptId=new ArrayList<Long>();	
		
		this.maxplusPhi=new double[0];		
		this.minplusVarphi=new double[0];
		this.maxplusConceptId=new long[0];
		this.minplusConceptId=new long[0];
	}
	
	public void addMaxPlusConcept(Double phi,Long conceptId){
		this.lmaxplusConceptId.add(conceptId);
		this.lmaxplusPhi.add(phi);
	}
	public void addMinPlusConcept(Double varphi,Long conceptId){
		this.lminplusConceptId.add(conceptId);
		this.lminplusVarphi.add(varphi);
	}
	
	public void optimizeArray(){
		int i=0;
		this.maxplusPhi=new double[this.lmaxplusPhi.size()];
		Iterator<Double> it=lmaxplusPhi.iterator();
		while(it.hasNext()){
			this.maxplusPhi[i]=it.next();
			i++;
		}				
		this.minplusVarphi=new double[this.lminplusVarphi.size()];
		i=0;
		it=lminplusVarphi.iterator();
		while(it.hasNext()){
			this.minplusVarphi[i]=it.next();
			i++;
		}
		
		this.maxplusConceptId=new long[this.lmaxplusConceptId.size()];
		i=0;
		Iterator<Long> iti=lmaxplusConceptId.iterator();
		while(iti.hasNext()){
			this.maxplusConceptId[i]=iti.next();
			i++;
		}
		this.minplusConceptId=new long[this.lminplusConceptId.size()];
		i=0;
		iti=lminplusConceptId.iterator();
		while(iti.hasNext()){
			this.minplusConceptId[i]=iti.next();
			i++;
		}
		lmaxplusPhi=null;	
		lmaxplusConceptId=null; //Maxplus concept identifier		
		lminplusVarphi=null; //VarPhi value which corresponds with a concept	
		lminplusConceptId=null; //Minplus concept identifier
		
	}
	
	public long getMaxConceptId(double phi){
						
		if (phi<maxplusPhi[0]){
			return 0;
		}		
		int mx=this.maxplusPhi.length-1;		
		for (int i=0;i<mx;i++){
			if (phi<maxplusPhi[i+1]){
				return this.maxplusConceptId[i];
			}
		}
		return this.maxplusConceptId[this.maxplusPhi.length-1];
	}
	
	/*
	 * Range in the interval [phi0,phi1) in which the object belongs to conceptId 
	 */
	public double[] getMaxMarginPhi(int conceptId){
		
		for (int id=0;id<maxplusConceptId.length;id++){
			if (conceptId==maxplusConceptId[id]){
				double[] phirange=new double[2];
				phirange[0]=maxplusPhi[id];
				phirange[1]=maxplusPhi[id+1];	
				return phirange;
			}
		}				
		return null;
	}
	
	
	public long getMinConceptId(double varphi){
		
		if (varphi>minplusVarphi[0]){
			return 0;
		}		
		int mx=this.minplusVarphi.length-1;		
		for (int i=0;i<mx;i++){
			if (varphi>minplusVarphi[i+1]){
				return this.minplusConceptId[i];
			}
		}
		return this.minplusConceptId[this.minplusVarphi.length-1];
	}
	
	/*
	 * Range in the interval (phi0,phi1] in which the object belongs to conceptId 
	 */
	public double[] getMinMarginVarphi(int conceptId){
		
		for (int id=0;id<minplusConceptId.length;id++){
			if (conceptId==minplusConceptId[id]){
				double[] phirange=new double[2];
				phirange[1]=minplusVarphi[id];
				if (id+1<minplusVarphi.length)
					phirange[0]=minplusVarphi[id+1];
				else
					phirange[0]=0;
				return phirange;
			}
		}				
		return null;
						
	}
}
