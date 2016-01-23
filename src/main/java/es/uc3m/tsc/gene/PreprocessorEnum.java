package es.uc3m.tsc.gene;


public enum PreprocessorEnum {
	LOGPREPROUNIT(0),LOGPREPROARITMEAN(1),LOGPREPROGEOMEAN(2),LOGPREPROGEOMAX(3),LOGPREPROMEANVAR(4),LOGPREPROMEANVARCOLROW(5),
	PREPROUNIT(100),PREPROARITMEAN(101),PREPROGEOMEAN(102),PREPROGEOMAX(103),PREPROMEANVAR(104),PREPROMEANVARCOLROW(105);
	final private int id;
    private PreprocessorEnum(int id){
    	this.id=id;
    }
    
    public String getMathJSFormula(){
    	switch (this){
    	case PREPROARITMEAN:
    	case PREPROGEOMAX:
    	case PREPROGEOMEAN:
    	case PREPROMEANVAR:
    	case PREPROUNIT:
    	case LOGPREPROARITMEAN:
    	case LOGPREPROGEOMAX:
    	case LOGPREPROGEOMEAN:
    	case LOGPREPROMEANVAR:
    	case LOGPREPROUNIT:
    		return "$$"+this.getFormula()+"$$";
    	case LOGPREPROMEANVARCOLROW:
    	case PREPROMEANVARCOLROW:
    		return this.getFormula();
    	default:
    		return "NOT SUPPORTED";
    	}
    }
    
    public String getFormula(){    	    	
		switch (this){
		//case PREPRONULL:
		//	return "None";
		case PREPROARITMEAN:
			return "xn_{ij}=x_{ij}/\\frac {1} {n}\\sum _{j=1}^{n}x_{ij}";					
		case PREPROGEOMEAN:
			return "xn_{ij}=x_{ij}/\\sqrt [n] {\\prod _{j=1}^{n}x_{ij}}";
		case PREPROGEOMAX:
			return "xn_{ij}=x_{ij}/max_{j=1}^{n}(x_{ij})";			
		case PREPROUNIT:
			return "xn_{ij}=x_{ij}";						
		case PREPROMEANVAR:
			return "xn_{ij}=(x_{i,j}-\\overline{x_{i·}})/\\sum _{j=1}^{n}x_{ij}·\\sqrt {m}";
		case PREPROMEANVARCOLROW:
			return "Mean 0 and var 1 in rows and columns";
		case LOGPREPROARITMEAN:
			return "xn_{ij}=log(x_{ij}/\\frac {1} {n}\\sum _{j=1}^{n}x_{ij})";					
		case LOGPREPROGEOMEAN:
			return "xn_{ij}=log(x_{ij}/\\sqrt [n] {\\prod _{j=1}^{n}x_{ij}})";
		case LOGPREPROGEOMAX:
			return "xn_{ij}=log(x_{ij}/max_{j=1}^{n}(x_{ij}))";			
		case LOGPREPROUNIT:
			return "xn_{ij}=log(x_{ij})";						
		case LOGPREPROMEANVAR:
			return "xn_{ij}=(log(x_{i,j})-\\overline{log(x_{i·})})/\\sum _{j=1}^{n}log(x_{ij})·\\sqrt {m}";
		case LOGPREPROMEANVARCOLROW:
			return "Mean 0 and var 1 in rows and columns of log(x_{ij})";
		default:
			return "";			
		}    	
    }
    
    public int getId(){
    	return this.id;
    }
    
}
