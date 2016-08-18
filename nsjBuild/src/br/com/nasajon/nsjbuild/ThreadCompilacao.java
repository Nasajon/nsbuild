package br.com.nasajon.nsjbuild;

import java.io.IOException;
import java.io.InputStream;

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
//			Process p = Runtime.getRuntime().exec("cmd /c start /b /wait nsjBuild.bat " + this.controleCompilacao.getBuildMode() + " " + this.no.getPath());
			Process p = Runtime.getRuntime().exec("cmd /c " + this.controleCompilacao.getBatchName() + " " + this.controleCompilacao.getBuildMode() + " " + this.no.getPath());
			p.waitFor();
			
			if(p.getErrorStream().available() > 0) {
				InputStream error = p.getInputStream();
				for (int i = 0; i < error.available(); i++) {
					System.out.print("" + (char)error.read());
				}
			}
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
