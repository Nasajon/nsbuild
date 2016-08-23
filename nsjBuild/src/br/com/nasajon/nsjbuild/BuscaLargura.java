package br.com.nasajon.nsjbuild;

import java.util.LinkedList;
import java.util.Queue;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

public class BuscaLargura {

//	public static Grafo buscaLarguraDependeciasRetornaGrafo(String idRaiz, Grafo grafo) {
//		Queue<No> listaNos = BuscaLargura.buscaLarguraDependecias(idRaiz, grafo);
//		
//		Grafo retorno = grafo.getCopiaGrafo();
//		
//		for (No n: retorno.getNos().values()) {
//			if (!listaNos.contains(n)) {
//				retorno.removeNo(n.getId());
//			}
//		}
//		
//		return retorno;
//	}
	
//	public static Queue<No> buscaLarguraDependecias(String idRaiz, Grafo grafo) {
//
//		No raiz = grafo.getNo(idRaiz);
//		if (raiz == null) {
//			return null;
//		}
//		
//		Queue<No> retorno = new LinkedList<No>();
//		Queue<No> fila = new LinkedList<No>();
//
//		No noIteracao = raiz;
//		noIteracao.setMarcado(true);
//		fila.offer(noIteracao);
//
//		while(fila.size() > 0) {
//			noIteracao = fila.poll();
//			retorno.offer(noIteracao);
//
//			for(No n: noIteracao.getSaidas()) {
//				if (!n.isMarcado()) {
//					n.setMarcado(true);
//					fila.offer(n);
//				}
//			}
//		}
//
//		return retorno;
//	}

	public static void desmarcaNosQueUtilizamAtual(String idRaiz, Grafo grafo) throws JAXBException, DatatypeConfigurationException {

		No raiz = grafo.getNo(idRaiz);
		if (raiz == null) {
			return;
		}
		
		XMLHandler xmlHandler = new XMLHandler(); 
		
		Queue<No> retorno = new LinkedList<No>();
		Queue<No> fila = new LinkedList<No>();

		No noIteracao = raiz;
		noIteracao.setMarcado(false);
		fila.offer(noIteracao);

		while(fila.size() > 0) {
			noIteracao = fila.poll();
			retorno.offer(noIteracao);

			for(No n: noIteracao.getEntradas()) {
				if (n.isMarcado()) {
					n.setMarcado(false);
					xmlHandler.atualizaUltimaCompilacaoXML(n.getArquivoXML(), false);
					fila.offer(n);
				}
			}
		}

		return;
	}
}
