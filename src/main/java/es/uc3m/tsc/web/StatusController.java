package es.uc3m.tsc.web;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uc3m.tsc.file.ProcessUploadedFiles;
import es.uc3m.tsc.gene.DataMatrix;
import es.uc3m.tsc.gene.DataTypeEnum;
import es.uc3m.tsc.genetools.GeneInfo;
import es.uc3m.tsc.threads.ThreadExecutor;
import es.uc3m.tsc.util.SystemInfo;

@RequestMapping("/status/")
@Controller
public class StatusController {
	
	@Resource
    private SystemInfo systemInfo;
	
	@Resource
    private ProcessUploadedFiles processCELFiles;
	
	@Resource 
	private GeneInfo geneInfo;
	
    @RequestMapping(method = RequestMethod.POST, value = "{id}")
    public void post(@PathVariable Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    }

	@RequestMapping(value = { "/", " * " }, produces = "text/html")
    public String index(Model uiModel) {
    	long numMatrix=-1;
    	String strDBStatus;
    	try{
    		numMatrix=DataMatrix.countDataMatrixes();
    		strDBStatus="OK";
    	}catch(Exception e){
    		strDBStatus="ERROR: "+e.toString();
    	}
    	
    	uiModel.addAttribute("numMatrix", numMatrix);
    	uiModel.addAttribute("memLoad", systemInfo.getMemoryUsed()*100);
    	uiModel.addAttribute("osSystem", systemInfo.getOS());
		uiModel.addAttribute("uptime", ((double) systemInfo.getUptime() / 1000 / 60) / 60 / 24);
    	uiModel.addAttribute("dbSizeMB", systemInfo.getDatabaseSize()/1024/1024);
		uiModel.addAttribute("numThreads", ThreadExecutor.getInstance().getNumThreadsAlive());

    	uiModel.addAttribute("apttoolsDir", processCELFiles.getAptToolsDir());    	
    	uiModel.addAttribute("apttoolsExists", processCELFiles.getAptToolsExist());
    	uiModel.addAttribute("tmpDir", processCELFiles.getFilesPath());    	
    	uiModel.addAttribute("tmpDirExists", processCELFiles.getFilesPathExist());
		uiModel.addAttribute("tmpDirSize", processCELFiles.getFilesPathSize() / 1024 / 1024);

    	uiModel.addAttribute("cdfFilesDir", processCELFiles.getCDFDir());
    	uiModel.addAttribute("cdfFilesName", processCELFiles.getCDFNames());
    	
    	ArrayList<Boolean> fileNameExists=new ArrayList<Boolean>();
    	ArrayList<String> fileName=new ArrayList<String>();

    	for (DataTypeEnum array : DataTypeEnum.values()){
			if (!"".equals(array.getMicroArrayName())) {
				long numProbeSets = geneInfo.getNumProbeSets(array);
				fileNameExists.add(new Boolean(numProbeSets > 0));
				fileName.add(array.getMicroArrayName() + " -- " + numProbeSets + " probesets");
			}
    	}
    	/*
    	Iterator<GeneInfo> iter=microArrays.values().iterator();
    	while(iter.hasNext()){
    		GeneInfo g=iter.next();
    		fileNameExists.add(new Boolean(new File(g.getFileName()).exists()));
    		fileName.add(g.getFileName());
    	}
    	*/
    	uiModel.addAttribute("geneInfoFileExists",  fileNameExists);
    	uiModel.addAttribute("geneInfoFile", fileName);
    	    	
    	uiModel.addAttribute("dbstatus", strDBStatus);
    	
    	
    	uiModel.addAttribute("baseDir", new File(".").getAbsolutePath());
		uiModel.addAttribute("userDir", System.getProperty("user.dir"));
    	
		ArrayList<String> logDirs=new ArrayList<String>();
		Enumeration e = Logger.getRootLogger().getAllAppenders();
		while ( e.hasMoreElements() ){
		      Appender app = (Appender)e.nextElement();
		      if ( app instanceof FileAppender ){
				logDirs.add(((FileAppender) app).getFile());
		      }
		    }
		uiModel.addAttribute("logDirs", logDirs);
		
        return "status/index";
    }

	@RequestMapping(value = "/clean", produces = "text/html")
	public String cleanTmpDir(Model uiModel) {
		processCELFiles.cleanDirectory();
		return "redirect:/status/";
	}

}
