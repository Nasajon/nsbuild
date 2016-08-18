package br.com.nasajon.nsjbuild;

import java.util.HashSet;
import java.util.Set;

public class Compilador implements ControleCompilacao {
	
	private Grafo grafoDependencias;
	private int maxThreadsAtivas;
	private Integer threadsAtivas;
	private Set<No> nosCompilados;
	private BuildMode buildMode;
	private String batchName;
	
	public Compilador(Grafo grafoDependencias, int maxThreadsAtivas, BuildMode buildMode, String batchName) {
		
		super();
		
		this.threadsAtivas = 0;
		this.maxThreadsAtivas = maxThreadsAtivas;
		this.grafoDependencias = grafoDependencias;
		this.nosCompilados = new HashSet<No>();
		this.buildMode = buildMode;
		this.batchName = batchName;
	}

//	public void compilaProjetoComDependenciasBuscaLargura(String idProjeto) throws InterruptedException {
//
//		Grafo grafoMinimo = BuscaLargura.buscaLarguraDependeciasRetornaGrafo(idProjeto, grafoDependencias);
//		
//		// Compilando enquanto houver no por compilar. A cada um compilado, atualiza as folhas...
//		No n = null;
//		while((n = grafoMinimo.getFolha()) != null) {
//
//			// Compilando as folhas:
//			while(this.threadsAtivas > maxThreadsAtivas) {
//				Thread.sleep(2000);
//			}
//
//			new ThreadCompilacao(n, this).start();
//			synchronized (this.threadsAtivas) {
//				this.threadsAtivas++;
//			}
//
//			grafoMinimo.removeNo(n.getId());
//		}
//	}

	public void compilaProjetoComDependencias(String idProjeto) throws GrafoCiclicoException, InterruptedException {
		
		BuscaProfundidade.buscaProfundidade(idProjeto, grafoDependencias, this);
	}
	
//	public void compilaProjetoComDependenciasOld(String idProjeto) {
//		Queue<No> listaNosPorCompilar = BuscaLargura.buscaLarguraDependecias(idProjeto, grafoDependencias);
//		Set<No> nosPorCompilar = new HashSet<No>(listaNosPorCompilar);
//		
//		Set<No> nosCompilados = new HashSet<No>();
//		Queue<No> folhas = new LinkedList<No>();
//		Queue<No> folhasRecemCompiladas = new LinkedList<No>();
//		
//		// Descobrindo as folhas:
//		for (No n: nosPorCompilar) {
//			if(n.getSaidas().size() == 0) {
//				folhas.add(n);
//			}
//		}
//		
//		// Compilando enquanto houver no por compilar. A cada um compilado, atualiza as folhas...
//		while(nosPorCompilar.size() > 0) {
//			folhasRecemCompiladas.clear();
//			
//			// Compilando as folhas:
//			while(folhas.size() > 0) {
//				No n = folhas.poll();
//				
//				n.compilar();
//				
//				folhasRecemCompiladas.offer(n);
//				nosCompilados.add(n);
//				
//				nosPorCompilar.remove(n);
//			}
//			
//			// Descobrindo as novas folhas, a partir das que foram compiladas:
//			while(folhasRecemCompiladas.size() > 0) {
//				No n = folhasRecemCompiladas.poll();
//				
//				for(No pai: n.getEntradas()) {
//					if (!nosPorCompilar.contains(pai)) {
//						continue;
//					}
//					
//					boolean encontrouDependenciaPorCompilar = false;
//					
//					for(No dependencia: n.getSaidas()) {
//						if (dependencia.equals(n)) {
//							continue;
//						}
//						
//						if (nosPorCompilar.contains(dependencia)) {
//							encontrouDependenciaPorCompilar = true;
//							break;
//						}
//					}
//					
//					if (!encontrouDependenciaPorCompilar) {
//						folhas.offer(pai);
//					}
//				}
//			}
//		}
//	}

	public Grafo getGrafoDependencias() {
		return grafoDependencias;
	}

	public void setGrafoDependencias(Grafo grafoDependencias) {
		this.grafoDependencias = grafoDependencias;
	}

	@Override
	public boolean compilar(No no) throws InterruptedException {
		
		while(this.threadsAtivas >= maxThreadsAtivas || !this.nosCompilados.containsAll(no.getSaidas())) {
			Thread.sleep(2000);
		}
		
		new ThreadCompilacao(no, this).start();
		synchronized (this.threadsAtivas) {
			this.threadsAtivas++;
		}
		
		return true;
	}

	@Override
	public void notifyThreadFinished(No no){
		
		this.nosCompilados.add(no);
		
		synchronized (this.threadsAtivas) {
			this.threadsAtivas--;
		}
	}

	@Override
	public void notifyThreadError(No no, String msgErro) {
		System.out.println(msgErro);
	}

	@Override
	public String getBuildMode() {
		return buildMode.toString();
	}

	@Override
	public String getBatchName() {
		return batchName;
	}
}
