package br.com.nasajon.nsjbuild;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

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
		long inicio = System.currentTimeMillis();
		
		try {
			System.out.println(this.no.getId() + " - INICIANDO" );
			Process p = Runtime.getRuntime().exec(this.controleCompilacao.getBatchName() + " " + this.controleCompilacao.getBuildMode() + " " + this.no.getPath() + " " + this.no.getId() + " " + this.controleCompilacao.getBuildTarget().toCallString());
			
			if(p.waitFor() != 0) {
				this.controleCompilacao.notifyThreadError(this.no, "ERRO DE COMPILAÇÃO NO PROJETO: " + this.no.getId());
				this.no.getProjeto().setUltimaCompilacao(null);
				
				InputStream error = p.getInputStream();
				for (int i = 0; i < error.available(); i++) {
					System.out.print("" + (char)error.read());
				}
				return;
			} else {
				this.no.getProjeto().setUltimaCompilacao(new Date());
				this.no.setMarcado(true);
			}
		} catch (IOException e) {
			this.controleCompilacao.notifyThreadError(this.no, e.getMessage());
			e.printStackTrace();
			return;
		} catch (InterruptedException e) {
			this.controleCompilacao.notifyThreadError(this.no, e.getMessage());
			e.printStackTrace();
			return;
		}

		Double intervalo = ((System.currentTimeMillis() - inicio)/1000.0)/60.0;
		System.out.println(this.no.getId() + " - FINALIZADO: " + intervalo + " minutos.");
		this.controleCompilacao.notifyThreadFinished(this.no);
	}
	
}
