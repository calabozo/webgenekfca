package es.uc3m.tsc.threads;

import es.uc3m.tsc.gene.Preprocessor;

public class ThreadProcessor extends ThreadGeneric {
	
	protected Preprocessor preprocessor;
		
	public static ThreadProcessor getRunningThreadProcessor(Long id){
		return (ThreadProcessor)ThreadExecutor.getInstance().getThread(ThreadExecutor.ExecutorType.EXPLORATION,id);
	}
	
	public ThreadProcessor(Preprocessor p){
		super(ThreadExecutor.ExecutorType.EXPLORATION, p.getId());
		this.preprocessor=p;
	}
		
	protected void execute(){
		double maxIters=10;
		for (double i=0;i<maxIters && !this.shouldStop;i++){
			this.ratioDone=i/maxIters;
			logger.info("Processsing: "+ this.ratioDone);
			try {
				sleep(3000);
			} catch (InterruptedException e) {
				break;
			}
		}
		if (!this.shouldStop) this.ratioDone=1;
	}
	
	public double getRatioDone(){
		return this.ratioDone;
	}
	
		
	protected void saveResults(){
		logger.info("Nothing to save");
	}

	@Override
	protected void exceptionCaptured(Exception e) {
		this.preprocessor.setResultException(e);		
	}
	
}
