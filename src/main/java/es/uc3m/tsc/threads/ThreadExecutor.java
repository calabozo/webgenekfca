package es.uc3m.tsc.threads;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import es.uc3m.tsc.general.Constants;

public class ThreadExecutor {
	public enum ExecutorType{
		DATAMATRIX,PREPROCESSOR,EXPLORATION,CLUSTERHS,CLUSTERPVALUE
	}
	private static ThreadExecutor t=null;
	
	private ExecutorService exe;	
	private HashMap<ExecutorType,HashMap<Long,ThreadGeneric>> threads;
	private Logger logger;
	ThreadSupervisor ths;
	private int numThreadsAlive;
	
	private ThreadExecutor(int numThreads){
		this.exe=Executors.newFixedThreadPool(numThreads);		
		this.threads=new HashMap<ExecutorType,HashMap<Long,ThreadGeneric>>();
		this.logger = Logger.getLogger("es.uc3m.tsc.thread");
		
		ExecutorType[] enums=ExecutorType.class.getEnumConstants();		
		for (ExecutorType m:enums){
			this.threads.put(m, new HashMap<Long,ThreadGeneric>());
		}
		
		ths=new ThreadSupervisor();
		exe.execute(ths);
	}
	
	public synchronized static ThreadExecutor getInstance(){
		if (t==null){
			t=new ThreadExecutor(4);
		}
		return t;		
	}
	
	public void addThread(ExecutorType type,Long pId,ThreadGeneric t){
		ThreadGeneric oldT=this.removeThread(type, pId);
		if (oldT!=null){
			oldT.shouldStop();
			try {
				oldT.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(e);
			}
		}
		
		threads.get(type).put(pId, t);
		exe.execute(t);
	}
	public ThreadGeneric getThread(ExecutorType type,Long pId){
		return threads.get(type).get(pId);
	}
	public ThreadGeneric removeThread(ExecutorType type,Long pId){
		return threads.get(type).remove(pId);
	}
	
	public Long getFirstAvailableId(ExecutorType type){
		HashMap<Long,ThreadGeneric> t=threads.get(type);
		Set<Long> s=t.keySet();
		
		Long id=0L;
		while(s.contains(id)){
			id++;
		}
		return id;
	}
	
	public void checkThreadsHealth(){
		int numAlive=0;
		Collection<HashMap<Long,ThreadGeneric>> colth=threads.values();
		for (HashMap<Long,ThreadGeneric> hashTh:colth){
			//Makes a copy to safely remove
			List<ThreadGeneric> listTh=new ArrayList<ThreadGeneric>(hashTh.values());			
			for(ThreadGeneric th:listTh){
				numAlive++;
				if (th.autoRemove==false){
					long now=new Date().getTime();
					long diff=now-th.lastRefresh.getTime();
					if (diff>Constants.maximumMilisecondsForThreadRefresh){
						if (th.getStopDate()!=null || th.getState()==Thread.State.TERMINATED ){
							this.removeThread(th.type,th.id);
						}else{
							logger.info("Atempting to finish thread id:"+th.id+" becausee it has passed more than "+diff+"ms");
							th.shouldStop();
							
						}
					}
				}				
			}						
		}
		if (numAlive>0){
			logger.info("There are "+numAlive+" threads running.");
		}
		this.numThreadsAlive = numAlive;
	}

	public int getNumThreadsAlive() {
		return this.numThreadsAlive;
	}

	public void stopAll(){
		logger.warn("Stoping all threads");
		
		ths.forceStop();
		Collection<HashMap<Long,ThreadGeneric>> colth=threads.values();
		for (HashMap<Long,ThreadGeneric> hashTh:colth){
			//Makes a copy to safely remove
			List<ThreadGeneric> listTh=new ArrayList<ThreadGeneric>(hashTh.values());			
			for(ThreadGeneric th:listTh){
				this.removeThread(th.type,th.id);
				th.shouldStop();
				logger.warn("Stoping thread:" + th.id + " type:" + th.type);
			}
		}
	}
	
	@Override
	public void finalize(){
		this.stopAll();
	}
}
