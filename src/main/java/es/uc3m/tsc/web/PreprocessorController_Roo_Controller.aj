// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package es.uc3m.tsc.web;

import es.uc3m.tsc.gene.Preprocessor;
import es.uc3m.tsc.web.PreprocessorController;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect PreprocessorController_Roo_Controller {
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String PreprocessorController.show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("preprocessor", Preprocessor.findPreprocessor(id));
        uiModel.addAttribute("itemId", id);
        return "preprocessors/show";
    }
    
    @RequestMapping(produces = "text/html")
    public String PreprocessorController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("preprocessors", Preprocessor.findPreprocessorEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) Preprocessor.countPreprocessors() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("preprocessors", Preprocessor.findAllPreprocessors(sortFieldName, sortOrder));
        }
        addDateTimeFormatPatterns(uiModel);
        return "preprocessors/list";
    }
    
    void PreprocessorController.addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("preprocessor_creationdate_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
    }
    
    String PreprocessorController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
