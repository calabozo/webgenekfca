package es.uc3m.tsc.util;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")	
public class SystemInfoTest {
	
	
	@Resource
    private SystemInfo systemInfo;
	
	@Test	
	public void testDatabaseSize(){
		double dbSize=systemInfo.getDatabaseSize();
		System.out.println("Database Size:"+dbSize+" bytes");
		Assert.assertEquals(true, dbSize>0);
		
	}

}
