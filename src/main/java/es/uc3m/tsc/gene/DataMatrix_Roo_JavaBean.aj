// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package es.uc3m.tsc.gene;

import es.uc3m.tsc.file.ProcessUploadedFiles;
import es.uc3m.tsc.gene.DataMatrix;
import es.uc3m.tsc.gene.DataTypeEnum;
import es.uc3m.tsc.gene.Preprocessor;
import java.util.Set;

privileged aspect DataMatrix_Roo_JavaBean {
    
    public Set<Preprocessor> DataMatrix.getPreprocessor() {
        return this.preprocessor;
    }
    
    public void DataMatrix.setPreprocessor(Set<Preprocessor> preprocessor) {
        this.preprocessor = preprocessor;
    }
    
    public String DataMatrix.getName() {
        return this.name;
    }
    
    public void DataMatrix.setName(String name) {
        this.name = name;
    }
    
    public String DataMatrix.getDescription() {
        return this.description;
    }
    
    public void DataMatrix.setDescription(String description) {
        this.description = description;
    }
    
    public String[] DataMatrix.getColNames() {
        return this.colNames;
    }
    
    public void DataMatrix.setColNames(String[] colNames) {
        this.colNames = colNames;
    }
    
    public String[] DataMatrix.getRowNames() {
        return this.rowNames;
    }
    
    public void DataMatrix.setRowNames(String[] rowNames) {
        this.rowNames = rowNames;
    }
    
    public double[][] DataMatrix.getRawData() {
        return this.rawData;
    }
    
    public void DataMatrix.setRawData(double[][] rawData) {
        this.rawData = rawData;
    }
    
    public ProcessUploadedFiles DataMatrix.getProcessFiles() {
        return this.processFiles;
    }
    
    public void DataMatrix.setProcessFiles(ProcessUploadedFiles processFiles) {
        this.processFiles = processFiles;
    }
    
    public String DataMatrix.getCelIDFiles() {
        return this.celIDFiles;
    }
    
    public void DataMatrix.setCelIDFiles(String celIDFiles) {
        this.celIDFiles = celIDFiles;
    }
    
    public DataTypeEnum DataMatrix.getMicroArrayType() {
        return this.microArrayType;
    }
    
    public void DataMatrix.setMicroArrayType(DataTypeEnum microArrayType) {
        this.microArrayType = microArrayType;
    }
    
}
