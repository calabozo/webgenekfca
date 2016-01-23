package es.uc3m.tsc.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class SystemInfo {

	@PersistenceContext
    transient EntityManager entityManager;
	
	/*
	 * Returns: Database size in MB
	 */
	public long getDatabaseSize(){
		long lRet=-1;
		try{
			Query q=entityManager.createNativeQuery("select DATABASE()");
			String dbName=(String)q.getSingleResult();
			String strQuery="SELECT sum( data_length + index_length )  FROM information_schema.TABLES where table_schema=?";
			q=entityManager.createNativeQuery(strQuery);
			q.setParameter(1, dbName);
			BigDecimal dbSize=(BigDecimal)q.getSingleResult();
			lRet=dbSize.longValue();			
		}catch(Exception e){};
		
		return lRet;
	}
	
	public double getMemoryUsed(){		
		MemoryUsage mu=ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		return mu.getUsed()/mu.getMax();
	}
	
	public String getOS(){
		OperatingSystemMXBean os=ManagementFactory.getOperatingSystemMXBean();
		return os.getName()+" "+os.getVersion()+"  "+os.getAvailableProcessors()+"cores";
	}
	
	public long getUptime(){
		//Returns the uptime of the Java virtual machine in milliseconds.
		return ManagementFactory.getRuntimeMXBean().getUptime();
	}
}
