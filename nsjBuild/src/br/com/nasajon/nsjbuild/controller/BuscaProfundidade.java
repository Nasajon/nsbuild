package br.com.nasajon.nsjbuild.controller;

import java.util.LinkedList;
import java.util.Queue;

import br.com.nasajon.nsjbuild.exception.GrafoCiclicoException;
import br.com.nasajon.nsjbuild.model.Grafo;
import br.com.nasajon.nsjbuild.model.No;

public class BuscaProfundidade {

	public static Queue<No> buscaProfundidade(String idRaiz, Grafo grafo, ControleCompilacao controleCompilacao, boolean apenasContagem) throws GrafoCiclicoException, InterruptedException {

		No raiz = grafo.getNo(idRaiz);
		if (raiz == null) {
			return null;
		}
		
		Queue<No> retorno = new LinkedList<No>();

		buscaRecursiva(raiz, controleCompilacao, retorno, apenasContagem);

		return retorno;
	}
	
	private static void buscaRecursiva(No noIteracao, ControleCompilacao controleCompilacao, Queue<No> retorno, boolean apenasContagem) throws GrafoCiclicoException, InterruptedException {

		if (controleCompilacao.isAborted()) {
			return;
		}

		noIteracao.setVisitado(true);

		for(No n: noIteracao.getSaidas()) {
			if (controleCompilacao.isAborted()) {
				return;
			}
			
//			if (!n.isVisitado()) {
//				buscaRecursiva(n, controleCompilacao, retorno);
//			} else {
//				if (!n.isCompilacaoChamada()) {
//					throw new GrafoCiclicoException(noIteracao.getId(), n.getId());
//				}
//			}
			if (n.isVisitado() && !n.isCompilacaoChamada()) {
				throw new GrafoCiclicoException(noIteracao.getId(), n.getId());
			} else {
				if (!n.isVisitado() && !n.isMarcado()) {
					buscaRecursiva(n, controleCompilacao, retorno, apenasContagem);
				}
			}
		}
		
		if (!apenasContagem) {
			controleCompilacao.compilar(noIteracao);
		}
		noIteracao.setCompilacaoChamada(true);
		
		retorno.offer(noIteracao);
	}
	
//	public static Queue<No> buscaProfundidade2(String idRaiz, Grafo grafo, ControleCompilacao controleCompilacao) throws GrafoCiclicoException, InterruptedException {
//
//		No raiz = grafo.getNo(idRaiz);
//		if (raiz == null) {
//			return null;
//		}
//		
//		Queue<No> retorno = new LinkedList<No>();
//		Stack<No> pilha = new Stack<No>();
//		boolean encontrouFilhoNaoMarcado;
//
//		No noIteracao = raiz;
//		pilha.push(noIteracao);
//
//		while(pilha.size() > 0) {
//			encontrouFilhoNaoMarcado = false;
//			
//			noIteracao = pilha.peek();
//			noIteracao.setVisitado(true);
//
//			for(No n: noIteracao.getSaidas()) {
//				if (!n.isMarcado()) {
//					if (n.isVisitado()) {
//						throw new GrafoCiclicoException(noIteracao.getId(), n.getId());
//					}
//					
//					pilha.push(n);
//					encontrouFilhoNaoMarcado = true;
//				}
//			}
//			
//			if (!encontrouFilhoNaoMarcado) {
//				pilha.pop();
//				if (!noIteracao.isMarcado()) {
//					controleCompilacao.compilar(noIteracao);
//					noIteracao.setMarcado(true);
//				}
//				
//				retorno.offer(noIteracao);
//			}
//		}
//
//		return retorno;
//	}
}
