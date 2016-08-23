package br.com.nasajon.nsjbuild;

import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

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
//			Process p = Runtime.getRuntime().exec("cmd /c " + this.controleCompilacao.getBatchName() + " " + this.controleCompilacao.getBuildMode() + " " + this.no.getPath() + " " + this.no.getId());
			Process p = Runtime.getRuntime().exec(this.controleCompilacao.getBatchName() + " " + this.controleCompilacao.getBuildMode() + " " + this.no.getPath() + " " + this.no.getId());
			
			XMLHandler xmlHandler = new XMLHandler();
			
			if(p.waitFor() != 0) {
//				System.out.println("ERRO de compilação no projeto: " + this.no.getId());
				this.controleCompilacao.notifyThreadError(this.no, "ERRO de compilação no projeto: " + this.no.getId());
				xmlHandler.atualizaUltimaCompilacaoXML(this.no.getArquivoXML(), false);
				return;
//				InputStream error = p.getInputStream();
//				for (int i = 0; i < error.available(); i++) {
//					System.out.print("" + (char)error.read());
//				}
			} else {
				xmlHandler.atualizaUltimaCompilacaoXML(this.no.getArquivoXML(), true);
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
		} catch (JAXBException e) {
			this.controleCompilacao.notifyThreadError(this.no, e.getMessage());
			e.printStackTrace();
			return;
		} catch (DatatypeConfigurationException e) {
			this.controleCompilacao.notifyThreadError(this.no, e.getMessage());
			e.printStackTrace();
			return;
		}

		System.out.println(this.no.getId());
		this.controleCompilacao.notifyThreadFinished(this.no);
	}
	
}
