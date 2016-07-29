package br.com.nasajon.nsjbuild;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class BuscaProfundidade {

	public static Queue<No> buscaProfundidade(String idRaiz, Grafo grafo, ControleCompilacao controleCompilacao) throws GrafoCiclicoException, InterruptedException {

		No raiz = grafo.getNo(idRaiz);
		if (raiz == null) {
			return null;
		}
		
		Queue<No> retorno = new LinkedList<No>();

		buscaRecursiva(raiz, controleCompilacao, retorno);

		return retorno;
	}
	
	private static void buscaRecursiva(No noIteracao, ControleCompilacao controleCompilacao, Queue<No> retorno) throws GrafoCiclicoException, InterruptedException {

		noIteracao.setVisitado(true);

		for(No n: noIteracao.getSaidas()) {
			if (!n.isVisitado()) {
				buscaRecursiva(n, controleCompilacao, retorno);
			} else {
				if (!n.isMarcado()) {
					throw new GrafoCiclicoException(noIteracao.getId(), n.getId());
				}
			}
		}
		
		controleCompilacao.compilar(noIteracao);
		noIteracao.setMarcado(true);
		
		retorno.offer(noIteracao);
	}
	
	public static Queue<No> buscaProfundidade2(String idRaiz, Grafo grafo, ControleCompilacao controleCompilacao) throws GrafoCiclicoException, InterruptedException {

		No raiz = grafo.getNo(idRaiz);
		if (raiz == null) {
			return null;
		}
		
		Queue<No> retorno = new LinkedList<No>();
		Stack<No> pilha = new Stack<No>();
		boolean encontrouFilhoNaoMarcado;

		No noIteracao = raiz;
		pilha.push(noIteracao);

		while(pilha.size() > 0) {
			encontrouFilhoNaoMarcado = false;
			
			noIteracao = pilha.peek();
			noIteracao.setVisitado(true);

			for(No n: noIteracao.getSaidas()) {
				if (!n.isMarcado()) {
					if (n.isVisitado()) {
						throw new GrafoCiclicoException(noIteracao.getId(), n.getId());
					}
					
					pilha.push(n);
					encontrouFilhoNaoMarcado = true;
				}
			}
			
			if (!encontrouFilhoNaoMarcado) {
				pilha.pop();
				if (!noIteracao.isMarcado()) {
					controleCompilacao.compilar(noIteracao);
					noIteracao.setMarcado(true);
				}
				
				retorno.offer(noIteracao);
			}
		}

		return retorno;
	}
}
