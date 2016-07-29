package br.com.nasajon.nsjbuild;

import java.io.IOException;

public class ThreadCompilacao extends Thread {
	
	private No no;
	private ControleCompilacao controleCompilacao;
	
	public ThreadCompilacao(No no, ControleCompilacao controleCompilacao) {
		
		super();
		
		this.no = no;
		this.controleCompilacao = controleCompilacao;
	}

	@Override
	public void run() {
		
		try {
			Process p = Runtime.getRuntime().exec("cmd /c start /wait nsjBuild.bat " + this.controleCompilacao.getBuildMode() + " " + this.no.getPath());
			p.waitFor();
		} catch (IOException e) {
			this.controleCompilacao.notifyThreadError(this.no, e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			this.controleCompilacao.notifyThreadError(this.no, e.getMessage());
			e.printStackTrace();
		}
		
		System.out.println(this.no.getId());
		this.controleCompilacao.notifyThreadFinished(this.no);
	}
}
