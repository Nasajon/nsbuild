package br.com.nasajon.nsjbuild.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import br.com.nasajon.nsjbuild.exception.GrafoCiclicoException;
import br.com.nasajon.nsjbuild.model.BuildMode;
import br.com.nasajon.nsjbuild.model.BuildTarget;
import br.com.nasajon.nsjbuild.model.Grafo;
import br.com.nasajon.nsjbuild.model.No;

public class Compilador implements ControleCompilacao {
	
	private int maxThreadsAtivas;
	private Integer threadsAtivas;
	private Set<No> nosCompilados;
	private BuildMode buildMode;
	private String batchName;
	private boolean isAborted;
	private BuildTarget buildTarget;
	private Integer qtdProjetosCompilar;
	private Integer qtdProjetosIniciados;
	
	public Compilador(int maxThreadsAtivas, BuildMode buildMode, String batchName, BuildTarget buildTarget) {
		
		super();
		
		this.threadsAtivas = 0;
		this.maxThreadsAtivas = maxThreadsAtivas;
		this.nosCompilados = new HashSet<No>();
		this.buildMode = buildMode;
		this.batchName = batchName;
		this.isAborted = false;
		this.buildTarget = buildTarget;
	}

	public Queue<No> compilaProjetoComDependencias(Grafo grafoDependencias, String idProjeto) throws GrafoCiclicoException, InterruptedException {
		this.qtdProjetosIniciados = 0;
		
		return BuscaProfundidade.buscaProfundidade(idProjeto, grafoDependencias, this, false);
	}

	public Queue<No> simularCompilacaoProjetoComDependencias(Grafo grafoDependencias, String idProjeto) throws GrafoCiclicoException, InterruptedException {
		this.qtdProjetosIniciados = 0;
		
		return BuscaProfundidade.buscaProfundidade(idProjeto, grafoDependencias, this, true);
	}

	public Queue<No> compileAll(Grafo grafoDependencias) throws GrafoCiclicoException, InterruptedException {
		this.qtdProjetosIniciados = 0;
		
		Queue<No> retorno = new LinkedList<No>();
		
		List<No> listaNaoCompilados = new ArrayList<No>();
		
		for (String idNo : grafoDependencias.getNos().keySet()) {
			No no = grafoDependencias.getNo(idNo);
			
			if (!no.isMarcado()) {
				listaNaoCompilados.add(no);
			}
		}
		
		for (No n : listaNaoCompilados) {
			if (!n.isCompilacaoChamada()) {
				Queue<No> iteracao = BuscaProfundidade.buscaProfundidade(n.getId(), grafoDependencias, this, false);
				
				retorno.addAll(iteracao);
			}
		}
		
		return retorno;
	}

	public Queue<No> simulateCompileAll(Grafo grafoDependencias) throws GrafoCiclicoException, InterruptedException {
		this.qtdProjetosIniciados = 0;
		
		Queue<No> retorno = new LinkedList<No>();
		
		List<No> listaNaoCompilados = new ArrayList<No>();
		
		for (String idNo : grafoDependencias.getNos().keySet()) {
			No no = grafoDependencias.getNo(idNo);
			
			if (!no.isMarcado()) {
				listaNaoCompilados.add(no);
			}
		}
		
		for (No n : listaNaoCompilados) {
			if (!n.isCompilacaoChamada()) {
				Queue<No> iteracao = BuscaProfundidade.buscaProfundidade(n.getId(), grafoDependencias, this, true);
				
				retorno.addAll(iteracao);
			}
		}
		
		return retorno;
	}

	@Override
	public boolean compilar(No no) throws InterruptedException {
		
		while(this.threadsAtivas >= maxThreadsAtivas || no.existsSaidaNaoCompilada()) {
			if (this.isAborted) {
				return false;
			}
			
			System.out.println("dormir");
			Thread.sleep(2000);
		}
		
		synchronized (this.threadsAtivas) {
			this.threadsAtivas++;
		}
		System.out.println("iniciando thread");
		new ThreadCompilacao(no, this).start();
		
		return true;
	}

	@Override
	public void notifyThreadFinished(No no){
		
		synchronized (this.nosCompilados) {
			this.nosCompilados.add(no);
		}
		
		synchronized (this.threadsAtivas) {
			this.threadsAtivas--;
		}
	}

	@Override
	public void notifyThreadError(No no, String msgErro) {
		this.isAborted = true;
		System.out.println("");
		System.out.println("");
		System.out.println("");
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
	
	public boolean existsThreadAtiva() {
		return this.threadsAtivas > 0;
	}

	public boolean isAborted() {
		return isAborted;
	}

	public BuildTarget getBuildTarget() { 
		return this.buildTarget;
	}

	@Override
	public Integer getQtdProjetosCompilar() {
		return qtdProjetosCompilar;
	}

	public void setQtdProjetosCompilar(Integer qtdProjetosCompilar) {
		this.qtdProjetosCompilar = qtdProjetosCompilar;
	}

	@Override
	public Integer incrementarQtdIniciados() {
		synchronized (this.qtdProjetosIniciados) {
			return ++this.qtdProjetosIniciados;
		}
	}
	
	public Integer getTotalCompilados() {
		return this.nosCompilados.size();
	}
}
