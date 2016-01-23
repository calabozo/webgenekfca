package es.uc3m.tsc.threads;

import java.util.Date;

import org.apache.log4j.Logger;


public abstract class ThreadGeneric extends Thread{

	protected double ratioDone;
	private Date initThread;
	private Date startThread;
	protected Date stopThread;	
	protected Logger logger;
	protected boolean shouldStop; //When true the execute() method should stop	
	protected Long id;
	protected ThreadExecutor.ExecutorType type;
	protected boolean autoRemove;
	protected Date lastRefresh;
	
	public ThreadGeneric(ThreadExecutor.ExecutorType type, Long id){
		this.logger = Logger.getLogger("es.uc3m.tsc.thread");
		this.initThread=new Date();
		this.lastRefresh=new Date();
		this.ratioDone=0;
		this.shouldStop=false;
		this.id=id;
		this.type=type;	
		this.autoRemove=true;		
		logger.info("Init Thread Generic at "+ this.initThread);				
	}
	
	public void run(){
		this.startThread=new Date();		
		logger.info("Start Thread Generic at "+ this.startThread+" with id:"+this.id);
		try{
			this.execute();
			if (this.shouldStop){
				logger.info("Thread id:"+this.id+" forced to stop");
				this.stopThread=new Date();
			}else{							
				this.saveResults();
				this.stopThread=new Date();
				long elapsedSeconds=(this.stopThread.getTime()-this.startThread.getTime())/1000;
				logger.info("Finish Thread Generic at "+ this.stopThread+" elapsed time:"+elapsedSeconds+"s");
			}
		}catch(Exception e){
			this.stopThread=new Date();
			logger.error("Exception "+e.toString());
			e.printStackTrace();
			logger.error(e);
			this.exceptionCaptured(e);
		}		
		if (autoRemove || this.shouldStop)	ThreadExecutor.getInstance().removeThread(this.type,this.id);
	}
	
	public Long addToExecutor(){
		ThreadExecutor th=ThreadExecutor.getInstance();
		if (this.id<0){ //Negative id is provisional, just find a proper one.
			//TODO: Avoid run race.
			//There can be a run race situation where two different instances get the same new id.
			//But this will never happend if the current load this web has.
			this.id=th.getFirstAvailableId(this.type);
		}
		th.addThread(this.type, this.id, this);
		return this.id;
	}
	
	protected abstract void execute();
	protected abstract void saveResults();
	protected abstract void exceptionCaptured(Exception e);
	
	public double getRatioDone(){
		this.lastRefresh=new Date();
		return this.ratioDone;
	}
	
	public long getEstimatedMilis(){
		if (this.stopThread==null && this.startThread!=null && !this.shouldStop){
			long s=this.startThread.getTime();
			long now=new Date().getTime();
			double difMilis=(now-s);
			
			if (difMilis>1000 && this.ratioDone>0.01)
				return (long)((difMilis/this.ratioDone)-difMilis);
			else
				return -1;
		}else{
			return 0;
		}
	}
	
	public Date getInitDate(){
		return this.initThread;
	}
	public Date getStartDate(){
		return this.startThread;
	}
	public Date getStopDate(){
		return this.stopThread;
	}
	public void shouldStop(){
		logger.info("Atempting to finish thread id:"+this.id+" of class "+this.getClass().getName());
		this.shouldStop=true;
	}
	public boolean getWaitingStop(){
		return this.shouldStop;
	}	
	
	public Long getIdThread(){
		return this.id;
	}
	

}
