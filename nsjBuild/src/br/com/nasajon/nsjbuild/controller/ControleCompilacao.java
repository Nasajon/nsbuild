package br.com.nasajon.nsjbuild.controller;

import br.com.nasajon.nsjbuild.model.BuildTarget;
import br.com.nasajon.nsjbuild.model.No;

public interface ControleCompilacao {
	
	public boolean compilar(No no) throws InterruptedException;
	
	public void notifyThreadFinished(No no);
	public void notifyThreadError(No no, String msgErro);
	
	public String getBuildMode();
	public String getBatchName();
	
	public boolean isAborted(); 
	public BuildTarget getBuildTarget(); 

	public Integer getQtdProjetosCompilar();
	public Integer incrementarQtdIniciados();
}
