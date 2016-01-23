package es.uc3m.tsc.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/general")
@Controller
public class GeneralController {
	

    @RequestMapping(value = "/changetype/{type}", produces = "text/html")
    public String changeType(@PathVariable("type") String type, Model uiModel,HttpServletResponse response){
    	Cookie c=new Cookie("mode", type);  
    	c.setPath("/");
    	response.addCookie(c);
    	return "redirect:/";
    }
        
}
