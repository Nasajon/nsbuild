package br.com.nasajon.nsjbuild;

import java.io.File;

import br.com.nasajon.nsjbuild.modelXML.Projeto;

public class ProjetoWrapper {
	private Projeto projeto;
	private File arquivoXML;

	public File getArquivoXML() {
		return arquivoXML;
	}
	public void setArquivoXML(File arquivoXML) {
		this.arquivoXML = arquivoXML;
	}
	public Projeto getProjeto() {
		return projeto;
	}
	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}
}
