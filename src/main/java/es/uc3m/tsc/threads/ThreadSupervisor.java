package es.uc3m.tsc.threads;

import org.apache.log4j.Logger;

import es.uc3m.tsc.general.Constants;


public class ThreadSupervisor extends Thread{

	protected Logger logger;
	boolean forceStop;
	
	public void forceStop(){
		forceStop=true;
		this.interrupt();
	}
	
	public ThreadSupervisor(){
		forceStop=false;
		this.logger = Logger.getLogger("es.uc3m.tsc.thread");
		logger.info("Init Thread SUPERVISOR ");				
	}
	
	public synchronized void run(){
		ThreadExecutor th=ThreadExecutor.getInstance();
		try{
			while(!forceStop){
				th.checkThreadsHealth();
				wait(Constants.periodToRunThreadSupervisor);
			}
		}catch(Exception e){
			logger.error("Exit from Thread supervisor: " + e.toString());
		}
	}
	
	

}
