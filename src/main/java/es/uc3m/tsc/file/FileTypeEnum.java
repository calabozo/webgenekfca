package es.uc3m.tsc.file;

public enum FileTypeEnum {
	FILETYPE_ERROR, //Error, ha saltado excepción.
	FILETYPE_NOVALID, //Archivo no valido
	FILETYPE_CEL, //Fichero CEL de microarrays
	FILETYPE_TABLE, //Tabla de expresión genética
	FILETYPE_COUNT; //Fichero de conteo de genes para RNASeq
}
