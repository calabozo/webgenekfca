package es.uc3m.tsc.kfca.explore;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.uc3m.tsc.file.ProcessUploadedFiles;
import es.uc3m.tsc.gene.DataMatrix;
import es.uc3m.tsc.gene.DataTypeEnum;
import es.uc3m.tsc.gene.Preprocessor;
import es.uc3m.tsc.gene.PreprocessorEnum;
import es.uc3m.tsc.gene.ProcessCELFilesTest;
import es.uc3m.tsc.general.Constants.GeneExpType;
import es.uc3m.tsc.genetools.GeneInfo;
import es.uc3m.tsc.genetools.GoDomainEnum;
import es.uc3m.tsc.math.MatrixInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/META-INF/spring/applicationContext*.xml")
public class KFCAResultGOFCA {

	@Resource
	GeneInfo geneInfo;

	@Test
	public void testGeneExpression() throws FileNotFoundException, UnsupportedEncodingException {
		DataMatrix dm=new DataMatrix();
		double[][] data= { 
				{ 1, 1, 0.1, 0.1 }, 
				{ 1, 2, 2, 3 }, 
				{ 1, 3, 0.4, 0.1 }, 
				{ 1, 15, 3, 2 }, 
				{ 2, 0.4, -5, 30 }, 
				{ 2, 9, -5, 0.3 }, 
				{ 2, 1, -5, -2 }, 
				{ 2, 5, -5, -1 },
				{ 2, 8, -5, 0.1 }, 
				{ 3, 50, -6, 0.1 },
				{ 4, 1, 1, -1 }, 
				{ 5, 2, 1, -1 } };
		dm.setRawData(data);

		String[] colNames= { "A", "B", "C", "D" };
		dm.setColNames(colNames);
		String[] rowNames= { "prob1", "prob2", "prob3", "prob4", "prob5", "prob6", "prob7", "prob8", "prob9", "prob10", "prob11", "prob12" };
		dm.setRowNames(rowNames);
		dm.setMicroArrayType(DataTypeEnum.TEST);

		Preprocessor p=new Preprocessor();
		p.setDataMatrix(dm);
		p.setName(dm.getName());
		p.setAlgorithm(1);

		PreprocessorEnum penum=PreprocessorEnum.LOGPREPROUNIT;

		MatrixInfo mi=dm.getHistogram(penum);

		p.setPreprocessorType(penum);
		p.setGeneExpressionType(GeneExpType.PROBESET);
		p.setMaxPhiToExplore(mi.getNumElements());
		p.execute();

		KFCAExplore ke=new KFCAExploreSimple64bits(p);
		ke.geneInfo=geneInfo;
		ke.execute();
		KFCAResults results=ke.getResults();

		String s=results.createFCAGeneOntology(1.0, 3, false, GoDomainEnum.BP);
		String sExp="GO:BP2,GO:BP1,GO:BP11\nGEN1,0,1,1\nGEN2,1,1,0\nGEN3,0,0,1\n";
		Assert.assertEquals(s,sExp);
		s=results.createFCAGeneOntology(1.0, 3, false, GoDomainEnum.CC);
		Assert.assertEquals(s,"");

		s=results.createFCAGeneOntology(1.0, 3, false, GoDomainEnum.MF);
		Assert.assertEquals(s,"");

	}

	@Test
	public void testExploreArabidopsis() throws FileNotFoundException, UnsupportedEncodingException {

		String celIDFiles="1236";
		String[] celFiles= { "GSM237280.CEL", "GSM237281.CEL", "GSM237282.CEL", "GSM237283.CEL", "GSM237292.CEL", "GSM237293.CEL", "GSM237294.CEL", "GSM237295.CEL" };
		// String
		// fileName=System.getProperty("user.dir")+"/src/test/res/plier-mm-sketch.summary.txt";

		DataMatrix dm=new DataMatrix();
		dm.setMicroArrayType(DataTypeEnum.ATH1121501);
		dm.setCelIDFiles(celIDFiles);

		ProcessUploadedFiles processCELFiles=dm.getProcessFiles();
		ProcessCELFilesTest.createSummary(processCELFiles, celFiles, celIDFiles);

		dm.setColNames(celFiles);
		dm.loadDataFromFileName();

		processCELFiles.removeFiles(celIDFiles);

		Preprocessor p=new Preprocessor();
		p.setDataMatrix(dm);
		p.setName(dm.getName());
		p.setAlgorithm(1);

		p.setPreprocessorType(PreprocessorEnum.LOGPREPROARITMEAN);
		p.setMaxPhiToExplore(10);
		p.execute();

		KFCAExplore ke=new KFCAExploreSimple64bits(p);
		ke.geneInfo=geneInfo;
		ke.execute();
		KFCAResults results=ke.getResults();

		String s=results.createFCAGeneOntology(0, 3, false, GoDomainEnum.BP);
		System.out.println(s);
		PrintWriter writer=new PrintWriter("/tmp/gobp.csv", "US-ASCII");
		writer.print(s);
		writer.close();

		s=results.createFCAGeneOntology(0, 3, false, GoDomainEnum.MF);
		System.out.println(s);
		writer=new PrintWriter("/tmp/gomf.csv", "US-ASCII");
		writer.print(s);
		writer.close();

		s=results.createFCAGeneOntology(0, 3, false, GoDomainEnum.CC);
		System.out.println(s);
		writer=new PrintWriter("/tmp/gocc.csv", "US-ASCII");
		writer.print(s);
		writer.close();
	}

}
