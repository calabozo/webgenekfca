package es.uc3m.tsc.threads;

import java.util.Date;
import java.util.Set;

import org.hibernate.Hibernate;

import es.uc3m.tsc.gene.DataMatrix;
import es.uc3m.tsc.gene.Preprocessor;

public class ThreadSavePreprocessorInBackground extends ThreadGeneric{
	protected Preprocessor preprocessor;
	
	public static ThreadSavePreprocessorInBackground getRunningThreadThreadSavePreprocessorInBackground(Long id){
		return (ThreadSavePreprocessorInBackground)ThreadExecutor.getInstance().getThread(ThreadExecutor.ExecutorType.PREPROCESSOR,id);
	}
	
	public ThreadSavePreprocessorInBackground(Preprocessor p){
		super(ThreadExecutor.ExecutorType.PREPROCESSOR, p.getId());
		this.preprocessor=p;
		preprocessor.persist();
		DataMatrix dm=preprocessor.getDataMatrix();
		dm.addPreprocessor(p);
		dm.merge();
		
        this.preprocessor=Preprocessor.reloadCachePreprocessor(preprocessor.getId());
		this.id=this.preprocessor.getId();
		//this.preprocessor.thProcessor=this;
	}
	public void execute(){
		
		this.preprocessor.execute();
		//this.preprocessor.merge();
	}
	public void saveResults(){       
        this.stopThread=new Date();		
		preprocessor.startProcessor();                
	}
	
	@Deprecated
	public void setRatioDone(double r){
		this.ratioDone=r;
	}
	
	@Override
	protected void exceptionCaptured(Exception e) {
		// TODO Auto-generated method stub		
	}
}