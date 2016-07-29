package br.com.nasajon.nsjbuild;

import java.util.LinkedList;
import java.util.Queue;

public class BuscaLargura {

	public static Grafo buscaLarguraDependeciasRetornaGrafo(String idRaiz, Grafo grafo) {
		Queue<No> listaNos = BuscaLargura.buscaLarguraDependecias(idRaiz, grafo);
		
		Grafo retorno = grafo.getCopiaGrafo();
		
		for (No n: retorno.getNos().values()) {
			if (!listaNos.contains(n)) {
				retorno.removeNo(n.getId());
			}
		}
		
		return retorno;
	}
	
	public static Queue<No> buscaLarguraDependecias(String idRaiz, Grafo grafo) {

		No raiz = grafo.getNo(idRaiz);
		if (raiz == null) {
			return null;
		}
		
		Queue<No> retorno = new LinkedList<No>();
		Queue<No> fila = new LinkedList<No>();

		No noIteracao = raiz;
		noIteracao.setMarcado(true);
		fila.offer(noIteracao);

		while(fila.size() > 0) {
			noIteracao = fila.poll();
			retorno.offer(noIteracao);

			for(No n: noIteracao.getSaidas()) {
				if (!n.isMarcado()) {
					n.setMarcado(true);
					fila.offer(n);
				}
			}
		}

		return retorno;
	}
}
