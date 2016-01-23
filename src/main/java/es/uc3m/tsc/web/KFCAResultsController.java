package es.uc3m.tsc.web;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.uc3m.tsc.gene.Preprocessor;
import es.uc3m.tsc.genetools.GoDomainEnum;
import es.uc3m.tsc.kfca.explore.KFCAResults;
import es.uc3m.tsc.threads.ThreadCalcPValue;

@RequestMapping("/kfcaresultses")
@Controller
@RooWebScaffold(path = "kfcaresultses", formBackingObject = KFCAResults.class)
public class KFCAResultsController {
	Logger logger;
	DecimalFormat df;
				
	public KFCAResultsController(){
		logger = Logger.getLogger("es.uc3m.tsc.web");
		df= new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
	}

	@RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        KFCAResults kfcaresults = KFCAResults.findKFCAResults(id);
        uiModel.addAttribute("kfcaresults_", kfcaresults);
        uiModel.addAttribute("itemId", id);
        uiModel.addAttribute("preprocessor",Preprocessor.findPreprocessor(kfcaresults.getPreprocessorId()));
        return "kfcaresultses/show";
    }
	
	/*
	 * Download matrix with intensity for a given concept 
	 * Callend from kfcaresultsets/show.jspx
	 */
	@RequestMapping(value = "/matrix_concept/{id}", method=RequestMethod.GET)
    public ResponseEntity<byte[]> matrixConcept(@PathVariable("id") Long id,@RequestParam("max") Boolean max_min_plus, @RequestParam("phi") Double phi, @RequestParam("attrid") Integer attrid){		
		KFCAResults kfcaResults=KFCAResults.findKFCAResults(id);		
		InputStream in;
		String filename="concept"+attrid;
		try {
			in = kfcaResults.getPipedMatrixConcept(max_min_plus,phi,attrid);		
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(new MediaType("text","csv"));
			headers.add("Content-Disposition", "attachment; filename=\""+filename+".csv\"");
			return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}
	
	/*
	 * Download maxplus boolean matrix from kfcaresultsets/show.jspx
	 */
	@RequestMapping(value = "/matrix_maxplus/{id}/{phi:.+}", method=RequestMethod.GET)
    public ResponseEntity<byte[]> matrixMaxPlus(@PathVariable("id") Long id,@PathVariable("phi") Double phi){		
		KFCAResults kfcaResults=KFCAResults.findKFCAResults(id);		
		InputStream in;
		try {
			in = kfcaResults.getPipedMatrixMaxPlus(phi);		
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(new MediaType("text","csv"));
			headers.add("Content-Disposition", "attachment; filename=\"maxplus_"+phi+".csv\"");
			return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}
    /*
	 * Download minplus boolean matrix from kfcaresultsets/show.jspx
	 */
    @RequestMapping(value = "/matrix_minplus/{id}/{phi:.+}", method=RequestMethod.GET)
    public ResponseEntity<byte[]> matrixMinPlus(@PathVariable("id") Long id,@PathVariable("phi") Double phi){		
		KFCAResults kfcaResults=KFCAResults.findKFCAResults(id);		
		InputStream in;
		try {
			in = kfcaResults.getPipedMatrixMinPlus(phi);		
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(new MediaType("text","csv"));
			headers.add("Content-Disposition", "attachment; filename=\"minplus_"+phi+".csv\"");
			return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}
    
    /*
     * Download JSON maxplus with concept info necessary to draw the Hasse diagram
     * Called from kfcaresultsets/show.jspx
     */
	@RequestMapping(value = "/concepts_maxplus/{id}/{phi:.+}", method=RequestMethod.GET)
    public @ResponseBody String conceptsMaxPlus(@PathVariable Long id,@PathVariable Double phi){
		//Double phi=new Double(sPhi);
		KFCAResults kfcaResults=KFCAResults.findKFCAResults(id);
		return kfcaResults.toJSONMaxPlusIntents(phi);						
	}		
	/*
     * Download JSON maxplus with info for just one concept given by attrid
     * Called from kfcaresultsets/show.jspx
     */
	@RequestMapping(value = "/concepts_maxplus/{id}/{phi}/{attrid}", method=RequestMethod.GET)
    public @ResponseBody String objectsMaxPlus(@PathVariable("id") Long id,@PathVariable("phi") Double phi,@PathVariable("attrid") Integer attrid){		
		KFCAResults kfcaResults=KFCAResults.findKFCAResults(id);
		return kfcaResults.toJSONMaxPlusSortConcept(phi,attrid);						
	}
	@RequestMapping(value = "/concept_range_maxplus/{id}/{objectName}", method=RequestMethod.GET)
    public @ResponseBody String objectsRangePhiMaxPlus(@PathVariable("id") Long id,@PathVariable("objectName") String objectName){		
		KFCAResults kfcaResults=KFCAResults.findKFCAResults(id);
		return kfcaResults.toJSONMaxMinPlusRangeInfo(objectName,true);
	}
	
	/*
     * Download JSON minplus with concept info necessary to draw the Hasse diagram
     * Called from kfcaresultsets/show.jspx
     */
	@RequestMapping(value = "/concepts_minplus/{id}/{varphi:.+}", method=RequestMethod.GET)
    public @ResponseBody String conceptsMinPlus(@PathVariable("id") Long id,@PathVariable("varphi") Double varphi){
		KFCAResults kfcaResults=KFCAResults.findKFCAResults(id);
		return kfcaResults.toJSONMinPlusIntents(varphi);
	}
	/*
     * Download JSON minplus with info for just one concept given by attrid
     * Called from kfcaresultsets/show.jspx
     */
	@RequestMapping(value = "/concepts_minplus/{id}/{varphi}/{attrid}", method=RequestMethod.GET)
    public @ResponseBody String objectsMinPlus(@PathVariable("id") Long id,@PathVariable("varphi") Double varphi,@PathVariable("attrid") Integer attrid){		
		KFCAResults kfcaResults=KFCAResults.findKFCAResults(id);
		//return kfcaResults.toJSONMinPlusConcept(varphi,attrid);						
		return kfcaResults.toJSONMinPlusSortConcept(varphi,attrid);
	}
	@RequestMapping(value = "/concept_range_minplus/{id}/{objectName}", method=RequestMethod.GET)
    public @ResponseBody String objectsRangeVarphiMinPlus(@PathVariable("id") Long id,@PathVariable("objectName") String objectName){		
		KFCAResults kfcaResults=KFCAResults.findKFCAResults(id);
		return kfcaResults.toJSONMaxMinPlusRangeInfo(objectName,false);
	}
	
	/*
     * Download JSON pValue of GO elements with info for just one concept given by attrid
     * Called from kfcaresultsets/show.jspx when a user chooses
     */
	@RequestMapping(value = "/gopvalues_minplus/{id}/{varphi}/{attrid}", method=RequestMethod.GET)
    public @ResponseBody String pValueMinPlus(@PathVariable("id") Long id,@PathVariable("varphi") Double varphi,@PathVariable("attrid") Integer attrid){		
		KFCAResults kfcaResults=KFCAResults.findKFCAResults(id);			
		return kfcaResults.threadMaxMinPlusPValueConcept(varphi,attrid, false);
		//return kfcaResults.toJSONMaxMinPlusPValueConcept(varphi,attrid, false);
	}
	@RequestMapping(value = "/gopvalues_maxplus/{id}/{varphi}/{attrid}", method=RequestMethod.GET)
	public @ResponseBody String pValueMaxPlus(@PathVariable("id") Long id,@PathVariable("varphi") Double varphi,@PathVariable("attrid") Integer attrid){		
		KFCAResults kfcaResults=KFCAResults.findKFCAResults(id);
		return kfcaResults.threadMaxMinPlusPValueConcept(varphi,attrid, true);
		//return kfcaResults.toJSONMaxMinPlusPValueConcept(varphi,attrid, true);
	}
	@RequestMapping(value = "/gopvalues_status/{id}", method=RequestMethod.GET)
	public @ResponseBody String pValueMaxPlus(@PathVariable("id") Long id){		
		ThreadCalcPValue tp=ThreadCalcPValue.getRunningThreadCalcPValue(id);
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
        		status+=",\"data\":"+tp.getResult();
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
	 * Download CSV of GO elements and probeset or gene info
	 */
	@RequestMapping(value="/csv_go_minplus_fca/{id}/{varphi}/{attrid}/{ontology}", method=RequestMethod.GET)
	public @ResponseBody String csvGoMinPlus(@PathVariable("id") Long id, @PathVariable("varphi") Double varphi, @PathVariable("attrid") Integer attrid, @PathVariable("ontology") String ontology) {
		KFCAResults kfcaResults=KFCAResults.findKFCAResults(id);
		return kfcaResults.createFCAGeneOntology(varphi, attrid, false, GoDomainEnum.valueOf(ontology.toUpperCase()));
	}

	@RequestMapping(value="/csv_go_maxplus_fca/{id}/{varphi}/{attrid}/{ontology}", method=RequestMethod.GET)
	public @ResponseBody String csvGoMaxPlus(@PathVariable("id") Long id, @PathVariable("varphi") Double varphi, @PathVariable("attrid") Integer attrid, @PathVariable("ontology") String ontology) {
		KFCAResults kfcaResults=KFCAResults.findKFCAResults(id);
		return kfcaResults.createFCAGeneOntology(varphi, attrid, true, GoDomainEnum.valueOf(ontology.toUpperCase()));
	}
}

