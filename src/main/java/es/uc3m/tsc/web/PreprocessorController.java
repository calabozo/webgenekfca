package es.uc3m.tsc.web;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.uc3m.tsc.gene.DataMatrix;
import es.uc3m.tsc.gene.Preprocessor;
import es.uc3m.tsc.gene.PreprocessorEnum;
import es.uc3m.tsc.general.Constants.GeneExpType;
import es.uc3m.tsc.kfca.explore.KFCAResults;
import es.uc3m.tsc.math.ArrayUtils;
import es.uc3m.tsc.math.MatrixInfo;
import es.uc3m.tsc.threads.ThreadGeneric;
import es.uc3m.tsc.threads.ThreadProcessor;
import es.uc3m.tsc.threads.ThreadSavePreprocessorInBackground;

@RequestMapping("/preprocessors")
@Controller
@RooWebScaffold(path = "preprocessors", formBackingObject = Preprocessor.class)
public class PreprocessorController {
	
	 DecimalFormat df;
	 public PreprocessorController(){
		 df= new DecimalFormat("#.##");
	 }
	
    void populateEditForm(Model uiModel, Preprocessor preprocessor) {
    	List<PreprocessorEnum> preprocessorenums=Arrays.asList(PreprocessorEnum.values());
    	List<String>           preprocessolabels=new ArrayList<String>(preprocessorenums.size());
    	List<MatrixInfo> histograms=new ArrayList<MatrixInfo>(preprocessorenums.size());
        Iterator<PreprocessorEnum> iter=preprocessorenums.iterator(); 
        
        while(iter.hasNext()){        	
        	PreprocessorEnum pe=iter.next();
        	preprocessolabels.add(pe.getFormula());        	
        	histograms.add(preprocessor.getDataMatrix().getHistogram(pe));
        }	    	
        ArrayList<DataMatrix> l=new ArrayList<DataMatrix>(1);
        l.add(preprocessor.getDataMatrix());
        uiModel.addAttribute("preprocessor", preprocessor);
        uiModel.addAttribute("datamatrixes", l);
        uiModel.addAttribute("datamatrixId", preprocessor.getDataMatrix().getId());
        uiModel.addAttribute("preprocessorenums", preprocessorenums);
        uiModel.addAttribute("preprocessorlabels", preprocessolabels);
        uiModel.addAttribute("histogram", histograms);
        
        
        List<GeneExpType> gExpTypes;
        if (preprocessor.getGeneExpressionType()==GeneExpType.GEXP_UNKNOWN){
        	gExpTypes=new ArrayList<GeneExpType>();
        	gExpTypes.add(GeneExpType.GEXP_UNKNOWN);
        }else{
        	gExpTypes=Arrays.asList(GeneExpType.values());
        }
        uiModel.addAttribute("geneexptypes", gExpTypes);
        
        
    }
    
    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel, @RequestParam("matrixId") Long matrixId) {
    	DataMatrix dataMatrix=DataMatrix.findDataMatrix(matrixId);
    	Preprocessor p=dataMatrix.getNewPreprocessor();    	
        populateEditForm(uiModel, p);
        return "preprocessors/create";
    }
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid Preprocessor preprocessor, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, preprocessor);
            return "preprocessors/create";
        }
        uiModel.asMap().clear();
        Long preprocessorThreadId=preprocessor.savePreprocessorThread();
                
        uiModel.addAttribute("preprocessor", preprocessor);
        uiModel.addAttribute("preprocessorThreadId", preprocessorThreadId); 
    	return "preprocessors/processing";   
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid Preprocessor preprocessor, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
    	Preprocessor originalPreprocessor=Preprocessor.findPreprocessor(preprocessor.getId());
        preprocessor.setDataMatrix(originalPreprocessor.getDataMatrix());   
        
    	if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, preprocessor);
            return "preprocessors/update";
        }
        uiModel.asMap().clear();
        preprocessor.merge();
        return "redirect:/preprocessors/" + encodeUrlPathSegment(preprocessor.getId().toString(), httpServletRequest);
    }
     
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel,HttpServletRequest httpServletRequest) {
    	Preprocessor preprocessor=Preprocessor.findPreprocessor(id);
    	if (preprocessor.hasRunningProcess()){
    		return "redirect:/preprocessors/" + encodeUrlPathSegment(preprocessor.getId().toString(), httpServletRequest);
    	}else{
    		populateEditForm(uiModel, preprocessor);        
    		return "preprocessors/update";
    	}
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Preprocessor preprocessor = Preprocessor.findPreprocessor(id);
       	DataMatrix dataMatrix=DataMatrix.findDataMatrix(preprocessor.getDataMatrix().getId());
       	dataMatrix.removePreprocessor(id);

       	uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/datamatrixes/"+dataMatrix.getId();
    }
    
    @RequestMapping(value = "/calc/{id}", produces = "text/html")
    public String calcPreprocessor(@PathVariable("id") Long id, @RequestParam(value = "force", required = false) Boolean forceStart, Model uiModel){
    	Preprocessor preprocessor=Preprocessor.findPreprocessor(id);
    	ThreadProcessor tp;
    	if (forceStart!=null && forceStart==true){
    		tp=preprocessor.forceStartProcessor();
    	}else{
    		tp=preprocessor.startProcessor();
    	}
    	uiModel.addAttribute("preprocessor", preprocessor);
        uiModel.addAttribute("processor", tp);    	
    	return "preprocessors/show_calc";    	    	
    }
	
    @RequestMapping(value = "/stop/{id}", produces = "text/html")
    public @ResponseBody String stopPreprocessor(@PathVariable("id") Long id){
    	Preprocessor preprocessor=Preprocessor.findPreprocessor(id);
    	ThreadProcessor tp=preprocessor.getProcessor();
    	if (tp!=null) tp.shouldStop();
    	return "";
    }
    
    @RequestMapping(value = "/result/{id}", produces = "text/html")
    public String resultPreprocessor(@PathVariable("id") Long id){
    	Preprocessor preprocessor=Preprocessor.findPreprocessor(id);
    	KFCAResults kfcaResult=preprocessor.getKfcaResults();    	
    	if (kfcaResult!=null){
    		return "redirect:/kfcaresultses/"+kfcaResult.getId();
    	}else{
    		return null;
    	}    	
    }
    @RequestMapping(value = "/analysis/{id}", produces = "text/html")
    public String analysisPreprocessor(@PathVariable("id") Long id){
    	Preprocessor preprocessor=Preprocessor.findPreprocessor(id);
    	KFCAResults kfcaResult=preprocessor.getKfcaResults();    	
    	if (kfcaResult!=null){
    		return "redirect:/analysis/"+kfcaResult.getId();
    	}else{
    		return null;
    	}    	
    }
    
    @RequestMapping(value = "/savestatus/{id}", method=RequestMethod.GET)
    public @ResponseBody String statusSavePreprocessor(@PathVariable("id") Long id){
    	ThreadSavePreprocessorInBackground tp=ThreadSavePreprocessorInBackground.getRunningThreadThreadSavePreprocessorInBackground(id);    	
    	return this.status(tp);
    }
    
    @RequestMapping(value = "/explorationstatus/{id}", method=RequestMethod.GET)
    public @ResponseBody String explorationStatusPreprocessor(@PathVariable("id") Long id){
    	ThreadProcessor tp=ThreadProcessor.getRunningThreadProcessor(id);    	
    	return this.status(tp);
    }
    
    private String status(ThreadGeneric tp){
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
        	}
        	ratio="\"ratio\":"+tp.getRatioDone()*100;    	
        	timeToFinish="\"es\":"+df.format((double)(tp.getEstimatedMilis()/1000)/60);
        	
        	waitingStop = "\"waitingstop\": "+tp.getWaitingStop();        
        	
        	return "{"+status+","+ratio+","+timeToFinish+","+waitingStop+"}";
        }else{
        	return "{"+"\"status\": \"done\""+"}";
        }
    }
       
    /*
	 * Download the raw datamatrix
	 */
    @RequestMapping(value = "/download/{id}", method=RequestMethod.GET)
    public ResponseEntity<byte[]> matrixMinPlus(@PathVariable("id") Long id){		
    	Preprocessor preprocessor=Preprocessor.findPreprocessor(id);
		InputStream in;
		try {
			in = ArrayUtils.getPipedMatrix(preprocessor.getGroupMatrix(), preprocessor.getDataMatrix().getRowNames(), preprocessor.getGroupName());		
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(new MediaType("text","csv"));
			headers.add("Content-Disposition", "attachment; filename=\""+preprocessor.getName().replace(' ', '_')+".csv\"");
			return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
    
}
