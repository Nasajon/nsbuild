package br.com.nasajon.nsjbuild.util;

public class GrafoCiclicoException extends Exception {

	private static final long serialVersionUID = 1L;

	public GrafoCiclicoException(String noOrigem, String noDestino) {
		super("Erro: Referência circular encontrada. Origem: '" + noOrigem + "' Destino: '" + noDestino + "'");
	}
}
