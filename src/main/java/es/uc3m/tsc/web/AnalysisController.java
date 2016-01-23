package es.uc3m.tsc.web;

import java.text.DecimalFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.uc3m.tsc.gene.Preprocessor;
import es.uc3m.tsc.kfca.explore.KFCAResults;
import es.uc3m.tsc.threads.ThreadCalcHomSep;


@RequestMapping("/analysis")
@Controller
public class AnalysisController {
	private final static String STRUE="true";	

	DecimalFormat df;
	public AnalysisController(){
		df= new DecimalFormat("#.##");
	}
	
	private int getConceptId(String cols){
		String[] sColsArray=cols.split(",");
		int conceptId=0;
		for (int i=0;i<sColsArray.length;i++){
			if (STRUE.equals(sColsArray[i])){
				//conceptId+=Math.pow(2, i);
				conceptId+=1<<i;
			}
		}
		return conceptId;
	}
	
    @RequestMapping(method = RequestMethod.POST, value = "{id}")
    public void post(@PathVariable Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    }


    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
    	KFCAResults kfcaresults = KFCAResults.findKFCAResults(id);
        uiModel.addAttribute("kfcaresults_", kfcaresults);
        uiModel.addAttribute("itemId", id);
        uiModel.addAttribute("preprocessor",Preprocessor.findPreprocessor(kfcaresults.getPreprocessorId()));
        
        return "analysis/index";
    }
	
    @RequestMapping(value = "get_info/{id}", produces = "text/plain")
    public @ResponseBody  String show(@PathVariable("id") Long id, String cols, boolean max_min_plus, Integer sorted_by, Integer max_genes, boolean json_csv_format ) {
    	if (cols!=null){
    		if (max_genes==null) max_genes=-1;
    		int conceptId=this.getConceptId(cols);
    		KFCAResults kfaresult=KFCAResults.findKFCAResults(id);    		
    		return kfaresult.getGeneAnalysis(conceptId, max_min_plus, sorted_by, max_genes,json_csv_format);
    	}else{
    		return "{\"response\":\"ERROR\"}";	
    	}    	    	       	       
    }
    
    @RequestMapping(value = "/get_homsep/{id}", method=RequestMethod.GET)
    public @ResponseBody String statusPreprocessor(@PathVariable("id") Long id){
    	ThreadCalcHomSep tp=ThreadCalcHomSep.getRunningThreadCalcHomSep(id);
    	String ratio;
    	String timeToFinish;
    	String status;
    	String waitingStop;    	
        if (tp!=null){
        	if (tp.getStartDate()==null){
        		status="\"status\": \"waiting\"";
        	}else if (tp.getStopDate()==null){
        		status="\"status\": \"running\"";
        	}else{
        		status="\"status\": \"done\"";      
        		status+=",\"hs\":"+tp.getResult();
        	}
        	ratio="\"ratio\":"+tp.getRatioDone()*100;    	
        	timeToFinish="\"es\":"+df.format((double)(tp.getEstimatedMilis()/1000)/60);
        	
        	waitingStop = "\"waitingstop\": "+tp.getWaitingStop();        
        	
        	return "{"+status+","+ratio+","+timeToFinish+","+waitingStop+"}";
        }else{
        	return "{"+"\"status\": \"done\""+"}";
        }
    }
}
