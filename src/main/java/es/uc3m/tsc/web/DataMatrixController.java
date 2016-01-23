package es.uc3m.tsc.web;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.uc3m.tsc.file.FileTypeEnum;
import es.uc3m.tsc.file.ProcessUploadedFiles;
import es.uc3m.tsc.file.ProcessUploadedFiles.FileDimensions;
import es.uc3m.tsc.gene.DataMatrix;
import es.uc3m.tsc.gene.DataTypeEnum;
import es.uc3m.tsc.gene.Preprocessor;
import es.uc3m.tsc.gene.PreprocessorEnum;
import es.uc3m.tsc.math.ArrayUtils;
import es.uc3m.tsc.threads.ThreadLoadData;

@RequestMapping(value={"/datamatrixes","datamatrices"})
@Controller
@RooWebScaffold(path = "datamatrixes", formBackingObject = DataMatrix.class)
public class DataMatrixController {
	
	@Resource
    private ProcessUploadedFiles processCELFiles;
	Logger logger;
	
	
	public DataMatrixController(){
		logger = Logger.getLogger("es.uc3m.tsc.web");
	}
	
	void populateEditForm(Model uiModel, DataMatrix dataMatrix) {
		List<DataTypeEnum> inmutableMicroarrayEnums=Arrays.asList(DataTypeEnum.values());
		List<DataTypeEnum> microarrayEnums=new ArrayList<DataTypeEnum>(inmutableMicroarrayEnums.size());
		Iterator<DataTypeEnum> it=inmutableMicroarrayEnums.iterator();
		while(it.hasNext()){
			DataTypeEnum m=it.next();
			if (m!=DataTypeEnum.GENERIC && m!=DataTypeEnum.TEST) microarrayEnums.add(m);
		}
		dataMatrix.setMicroArrayType(DataTypeEnum.GENERIC);

        uiModel.addAttribute("dataMatrix", dataMatrix);
        uiModel.addAttribute("microarrayenums", microarrayEnums);
        uiModel.addAttribute("preprocessors", Preprocessor.findAllPreprocessors());
    }
    
	
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid DataMatrix dataMatrix, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, dataMatrix);
            return "datamatrixes/create";
        }
        /*
        dataMatrix.loadDataFromFileName();
        uiModel.asMap().clear();
        dataMatrix.persist();
        return "redirect:/datamatrixes/" + encodeUrlPathSegment(dataMatrix.getId().toString(), httpServletRequest);
        */      
        dataMatrix.persist();
        dataMatrix.launchThreadLoadDataFromFileName();
        uiModel.addAttribute("datamatrix", dataMatrix);        
        return "datamatrixes/processing";
    }
	
    @RequestMapping(value = "/checkapt/{id}", method=RequestMethod.GET)
    public @ResponseBody String checkApt(@PathVariable("id") Long id){
		ThreadLoadData th=ThreadLoadData.getRunningThreadLoadData(id);
		String strOutput="";
		if (th != null) {
			strOutput="<br/>" + th.getStrOutput().replace("\n", "<br/>");
		}
		return "{\"status\":" + DataMatrix.isCompletedDataMatrix(id) + ",\"info\":\"" + strOutput + "\"}";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid DataMatrix dataMatrix, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, dataMatrix);
            return "datamatrixes/update";
        }
        uiModel.asMap().clear();
        DataMatrix originalDataMatrix=DataMatrix.findDataMatrix(dataMatrix.getId());                
        originalDataMatrix.merge();
        return "redirect:/datamatrixes/" + encodeUrlPathSegment(dataMatrix.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/addPreprocessor/{id}", produces = "text/html")
    public String addPreprocessor(@PathVariable("id") Long id, Model uiModel){
    	return "redirect:/preprocessors/?form&matrixId="+id;
    	/*
    	DataMatrix dataMatrix=DataMatrix.findDataMatrix(id);
    	long idPreprocessor=dataMatrix.addPreprocessor();
    	return "redirect:/preprocessors/"+idPreprocessor+"?form";    	
    	*/
    }
    
    @RequestMapping(value = "/fileuploader/{fileid}", produces = "text/html",method = RequestMethod.POST)
    public @ResponseBody String fileuploader(@PathVariable("fileid") String id, HttpServletRequest request,HttpServletResponse response,@CookieValue(value = "mode" , required = false) String cookieMode) throws IOException{    	    	                       
    	String filename = request.getHeader("X-File-Name");    	
    	String out;
    	FileDimensions fileDimensions=processCELFiles.new FileDimensions();
    	
    	InputStream is = request.getInputStream();
    	FileTypeEnum fType=processCELFiles.saveFile(id, filename, is,fileDimensions);    		
    	
    	switch (fType){    	
    	case FILETYPE_TABLE:
    		out="{success: true, filetype:\"TABLE\", cols: "+fileDimensions.cols+", rows:"+fileDimensions.rows+"}";
    		break;
    	
    	case FILETYPE_CEL:
    		if (!"kfca".equals(cookieMode)){
    			out="{success: true, filetype:\"CEL\"}";
    			break;
    		}
    	case FILETYPE_COUNT:
    		out="{success: true, filetype:\"COUNT\", rows:"+fileDimensions.rows+"}";
    		break;    	
    		
    	case FILETYPE_ERROR:
    	case FILETYPE_NOVALID:
    	default:
    		out="{success: false}";
    		response.sendError(500);
    		break;
    	}
    	
    	
    	return out;
    }
    
    @RequestMapping(value = "/fileuploader/{fileid}", produces = "text/html",method = RequestMethod.DELETE)
    public @ResponseBody String filedelete(@PathVariable("fileid") String id, HttpServletRequest request, HttpServletResponse response) throws IOException{    	    	                       
    	String filename = request.getHeader("X-File-Name");
    	String out;
    	boolean bOut=processCELFiles.removeSingleFile(id, filename);
    	
    	if (bOut){
    		out="{success: true}";
    	}else{    		
    		out="{success: false}";
    		response.sendError(500);
    	}
    	
    	return out;
    }
    
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        DataMatrix dataMatrix = DataMatrix.findDataMatrix(id);
        dataMatrix.removeAllPreprocessors();
        dataMatrix.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/datamatrixes";
    }
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
    	List<PreprocessorEnum> preprocessorenums=Arrays.asList(PreprocessorEnum.values());
        uiModel.addAttribute("datamatrix", DataMatrix.findDataMatrix(id));
        uiModel.addAttribute("itemId", id);
        uiModel.addAttribute("preprocessorenums", preprocessorenums);
        return "datamatrixes/show";
    }
    
    /*
	 * Download the image background of gene expression
	 */
    @RequestMapping(value = "/image/{id}", method=RequestMethod.GET)
    public ResponseEntity<byte[]> matrixMinPlus(@PathVariable("id") Long id,@RequestParam(value="width", required = false) Integer width, @RequestParam(value="height", required = false) Integer height){
    	DataMatrix dataMatrix = DataMatrix.findDataMatrix(id);		
		try {
			if (width==null) width=100;
			if (height==null) height=100;
			
			BufferedImage img=ArrayUtils.generateImage(dataMatrix.getRawData(),width,height);
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			ImageIO.write(img, "jpg", baos );
			byte[] imageInByte=baos.toByteArray();
		
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(new MediaType("image","jpeg"));			
			return new ResponseEntity<byte[]>(imageInByte, headers, HttpStatus.CREATED);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}
 
    
    /*
	 * Download the raw datamatrix
	 */
    @RequestMapping(value = "/download/{id}", method=RequestMethod.GET)
    public ResponseEntity<byte[]> matrixMinPlus(@PathVariable("id") Long id){		
    	DataMatrix dataMatrix = DataMatrix.findDataMatrix(id);
		InputStream in;
		try {
			in = ArrayUtils.getPipedMatrix(dataMatrix.getRawData(), dataMatrix.getRowNames(), dataMatrix.getColNames());		
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(new MediaType("text","csv"));
			headers.add("Content-Disposition", "attachment; filename=\""+dataMatrix.getName().replace(' ', '_')+".csv\"");
			return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}
    
}
