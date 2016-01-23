package es.uc3m.tsc.threads;


import es.uc3m.tsc.gene.DataMatrix;

public class ThreadLoadData extends ThreadGeneric{
	protected DataMatrix dataMatrix;
	protected StringBuilder aptOutput;
	
	public static ThreadLoadData getRunningThreadLoadData(Long id){
		return (ThreadLoadData)ThreadExecutor.getInstance().getThread(ThreadExecutor.ExecutorType.DATAMATRIX,id);
	}
	
	public ThreadLoadData(DataMatrix dataMatrix){
		super(ThreadExecutor.ExecutorType.DATAMATRIX, dataMatrix.getId());
		this.dataMatrix = dataMatrix;
		this.aptOutput = null;
	}
	
	public void execute() {
		aptOutput = dataMatrix.loadDataFromFileName();
		if (dataMatrix.getRawData() != null)
			dataMatrix.merge();
		else //There was an error loading data, remove the data
			dataMatrix.remove();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void saveResults() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void exceptionCaptured(Exception e) {
		dataMatrix.remove();		
	}
	
	public String getStrOutput() {
		if (this.aptOutput == null) return null;
		return this.aptOutput.toString();
	}
	
	
}