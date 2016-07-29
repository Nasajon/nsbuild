package br.com.nasajon.nsjbuild;

public interface ControleCompilacao {
	
	public boolean compilar(No no) throws InterruptedException;
	
	public void notifyThreadFinished(No no);
	public void notifyThreadError(No no, String msgErro);
	
	public String getBuildMode();
}
