package es.uc3m.tsc.web;

import java.sql.Blob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.roo.addon.web.mvc.controller.converter.RooConversionService;

import es.uc3m.tsc.gene.DataMatrix;
import es.uc3m.tsc.gene.DataTypeEnum;
import es.uc3m.tsc.gene.Preprocessor;
import es.uc3m.tsc.kfca.explore.KFCAObjectExplored;

/**
 * A central place to register application converters and formatters. 
 */
@RooConversionService
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

	@Override
	protected void installFormatters(FormatterRegistry registry) {
		super.installFormatters(registry);
		// Register application converters and formatters
	}
	
    public Converter<DataMatrix, String> getDataMatrixToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<es.uc3m.tsc.gene.DataMatrix, java.lang.String>() {
            public String convert(DataMatrix dataMatrix) {
                return dataMatrix.toString();
            }
        };
    }
    
    public Converter<Preprocessor, String> getPreprocessorToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<es.uc3m.tsc.gene.Preprocessor, java.lang.String>() {
            public String convert(Preprocessor preprocessor) {
                return new StringBuilder().append(preprocessor.getName()).toString();
            }
        };
    }
    
    public Converter<DataTypeEnum, String> getDataTypeEnumToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<DataTypeEnum, java.lang.String>() {
            public String convert(DataTypeEnum dataType) {
            	return dataType.getLabelName();
            }
        };
    }
}
