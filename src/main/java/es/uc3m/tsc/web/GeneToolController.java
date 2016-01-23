package es.uc3m.tsc.web;
import java.util.Map;
import org.apache.log4j.Logger;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.uc3m.tsc.gene.DataTypeEnum;
import es.uc3m.tsc.genetools.GeneDescription;
import es.uc3m.tsc.genetools.GeneInfo;


@RequestMapping("/genetool/**")
@Controller
public class GeneToolController {
	private Logger logger;
	
	@Resource 
	private GeneInfo geneInfo;
	
	public GeneToolController(){
		logger = Logger.getLogger("es.uc3m.tsc.web");
	}

	@RequestMapping(value = "/geneinfo/{microArrayType}/{geneid1}/{geneid2}", method=RequestMethod.GET)
    public @ResponseBody String geneInfo(@PathVariable DataTypeEnum microArrayType, @PathVariable String geneid1, @PathVariable String geneid2){
		return geneInfo(microArrayType, geneid1+" /// "+geneid2);
	}
	
	@RequestMapping(value = "/geneinfo/{microArrayType}/{geneid}", method=RequestMethod.GET)
    public @ResponseBody String geneInfo(@PathVariable DataTypeEnum microArrayType, @PathVariable String geneid){
		
		
		if (geneid.endsWith("_at")){//probeset
			return geneInfo.getGeneDescription(microArrayType,geneid).toJson();
		}else{
			GeneDescription gd=geneInfo.getGenesFromGeneName(microArrayType,geneid);
			if (gd!=null){
				return gd.toJson();
			}
		}
		return "";
	}
}
