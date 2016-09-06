package br.com.nasajon.nsjbuild.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import br.com.nasajon.nsjbuild.exception.FreeCacheException;
import br.com.nasajon.nsjbuild.model.Grafo;
import br.com.nasajon.nsjbuild.model.No;

public class BuscaLargura {

	public static void desmarcaNosQueUtilizamAtual(String idRaiz, Grafo grafo, boolean inline) throws FileNotFoundException, FreeCacheException, IOException {

		No raiz = grafo.getNo(idRaiz);
		if (raiz == null) {
			return;
		}
		
		Queue<No> retorno = new LinkedList<No>();
		Queue<No> fila = new LinkedList<No>();

		No noIteracao = raiz;
		noIteracao.setVisitado(true);
		noIteracao.setMarcado(false);
		fila.offer(noIteracao);

		while(fila.size() > 0) {
			noIteracao = fila.poll();
			retorno.offer(noIteracao);

			for(No n: noIteracao.getEntradas()) {
				if (!n.isVisitado()) {
					n.setVisitado(true);
					n.setMarcado(false);
					n.getProjeto().setUltimaCompilacao(null);
					fila.offer(n);
				}
			}
			
			if (!inline) {
				return;
			}
		}

		return;
	}
}
