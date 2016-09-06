package br.com.nasajon.nsjbuild.exception;

public class ProjectFileNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public ProjectFileNotFoundException(String projeto, String path) {
		super("ERRO: Projeto '" + projeto + "' - Path de projeto inválido: '" + path + "'");
	}
}
