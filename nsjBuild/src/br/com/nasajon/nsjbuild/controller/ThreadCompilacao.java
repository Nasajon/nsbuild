package br.com.nasajon.nsjbuild.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import br.com.nasajon.nsjbuild.model.No;

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
			Integer qtdProjetosCompilados = this.controleCompilacao.incrementarQtdIniciados();
			Integer qtdProjetosCompilar  = this.controleCompilacao.getQtdProjetosCompilar();
			
			if (qtdProjetosCompilar != null) {
				System.out.println(this.no.getId() + " - INICIANDO... Projeto " + qtdProjetosCompilados + " de " + qtdProjetosCompilar);
			} else {
				System.out.println(this.no.getId() + " - INICIANDO...");
			}
			
			Process p = Runtime.getRuntime().exec(this.controleCompilacao.getBatchName() + " " + this.controleCompilacao.getBuildMode() + " " + this.no.getPath() + " " + this.no.getId() + " " + this.controleCompilacao.getBuildTarget().toCallString());
			
			if(p.waitFor() != 0) {
				String caminhoLog = "\"logs" + File.separator + this.no.getId() + ".log\"";
				Process p2 = Runtime.getRuntime().exec("cmd /C start notepad " + caminhoLog);
				if (p2.waitFor() != 0) {
					System.out.println("Erro ao abrir arquivo de log: " + caminhoLog);
				}

				this.controleCompilacao.notifyThreadError(this.no, "ERRO DE COMPILAÇÃO NO PROJETO: " + this.no.getId());
				this.no.getProjeto().setUltimaCompilacao(null);
				
				String logFilePath = this.no.getProjeto().getParametrosNsjBuild().getErpPath() + "\\build\\logs\\" + this.no.getId() + ".log"; 
				
				File log = new File(logFilePath);
				
				if (log.exists()) {
					String content = new String(Files.readAllBytes(Paths.get(log.getAbsolutePath())));
					
					System.out.println(content);	
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
		System.out.println(this.no.getId() + " - FINALIZADO: " + String.format("%.4f", intervalo) + " minutos.");
		this.controleCompilacao.notifyThreadFinished(this.no);
	}
	
}
