package br.com.nasajon.nsjbuild;

public class GrafoCiclicoException extends Exception {

	private static final long serialVersionUID = 1L;

	public GrafoCiclicoException(String noOrigem, String noDestino) {
		super("Erro: Refer�ncia circular encontrada. Origem: '" + noOrigem + "' Destino: '" + noDestino + "'");
	}
}
