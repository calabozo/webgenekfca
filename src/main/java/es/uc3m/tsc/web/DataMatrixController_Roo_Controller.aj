// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package es.uc3m.tsc.web;

import es.uc3m.tsc.gene.DataMatrix;
import es.uc3m.tsc.web.DataMatrixController;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect DataMatrixController_Roo_Controller {
    
    @RequestMapping(params = "form", produces = "text/html")
    public String DataMatrixController.createForm(Model uiModel) {
        populateEditForm(uiModel, new DataMatrix());
        return "datamatrixes/create";
    }
    
    @RequestMapping(produces = "text/html")
    public String DataMatrixController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("datamatrixes", DataMatrix.findDataMatrixEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) DataMatrix.countDataMatrixes() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("datamatrixes", DataMatrix.findAllDataMatrixes(sortFieldName, sortOrder));
        }
        return "datamatrixes/list";
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String DataMatrixController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, DataMatrix.findDataMatrix(id));
        return "datamatrixes/update";
    }
    
    String DataMatrixController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}
