package es.uc3m.tsc.gene;


public enum DataTypeEnum {

    ATH1121501(0),HGU133PLUS2(1),HGU133A(2),PRIMEVIEW(3),TEST(99),GENERIC(-1),
    SEQUNKNOW(128),SEQECOLI(129),SEQSCEREVISAE(130),SEQDMELANOGASTER(131),SEQHOMOSAPIENS(132);
    final private int id;
    private DataTypeEnum(int id){
    	this.id=id;
    }
    
    public String getSpecieName(){    	    	
		switch (this){
		case ATH1121501:
			return "arabidopsis thaliana";
		case HGU133PLUS2:
		case HGU133A:
		case PRIMEVIEW:
			return "Homo Sapiens";
		case SEQUNKNOW:
			return "unknown";
		case SEQECOLI:
			return "Escherichia Coli";
		case SEQSCEREVISAE:
			return "saccharomyces cerevisiae";
		case SEQDMELANOGASTER:
			return "Drosophila Melanogaster";
		case SEQHOMOSAPIENS:
			return "Homo Sapiens";
		default:
			return "";			
		}    	
    }
    
    public boolean isMicroarray(){
    	return this.id<128 || this.id==GENERIC.id;
    }
    public boolean isRNASeq(){
    	return !this.isMicroarray();
    }
    
    public String getLabelName(){ 
    	if (this.isMicroarray()){
    		return "Microarray, "+this.getMicroArrayName();
    	}else{
    		return "RNASeq, "+this.getSpecieName();
    	}
    }
    
    public String getMicroArrayName(){    	    	
		switch (this){
		case ATH1121501:
			return "ATH1-12501";		
		case HGU133PLUS2:
			return "HG-U133 Plus";
		case HGU133A:
			return "HG-U133A";
		case PRIMEVIEW:
			return "PrimeView";
		case SEQECOLI:
		case SEQSCEREVISAE:
		case SEQDMELANOGASTER:
		case SEQHOMOSAPIENS:
			return "RNASeq";
		default:
			return "";			
		}    	
    }
    
    public String getCDF(){    	    	
		switch (this){
		case ATH1121501:
			return "ATH1-121501.CDF";	
		case HGU133PLUS2:
			//http://www.affymetrix.com/support/technical/byproduct.affx?product=hg-u133-plus
			return "HG-U133_Plus_2.CDF";			
		case HGU133A:
			//http://www.affymetrix.com/support/technical/byproduct.affx?product=ht_hg-u133_set
			return "HG-U133A.CDF";
		case PRIMEVIEW:
			//http://www.affymetrix.com/estore/browse/products.jsp?productId=prod530005#1_3
			return "PrimeView.cdf";
		default:
			return null;			
		}    	
    }
    public int getId(){
    	return this.id;
    }
    
    public static DataTypeEnum getType(Integer id) {
        
        if (id == null) {
            return null;
        }

        for (DataTypeEnum position : DataTypeEnum.values()) {
            if (id.equals(position.getId())) {
                return position;
            }
        }
        throw new IllegalArgumentException("No matching type for id " + id);
    }

}
