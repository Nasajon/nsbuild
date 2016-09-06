package br.com.nasajon.nsjbuild.exception;

public class DependenciaInvalidaException extends Exception {

	private static final long serialVersionUID = 1L;

	public DependenciaInvalidaException(String projeto, String dependencia) {
		super("ERRO: Projeto '" + projeto + "' - Dependência inválida localizada: '" + dependencia + "'");
	}
}
