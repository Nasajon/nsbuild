package br.com.nasajon.nsjbuild;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Compilador implements ControleCompilacao {
	
	private Grafo grafoDependencias;
	private int maxThreadsAtivas;
	private Integer threadsAtivas;
	private Set<No> nosCompilados;
	private BuildMode buildMode;
	private String batchName;
	private boolean isAborted;
	private BuildTarget buildTarget;
	
	public Compilador(Grafo grafoDependencias, int maxThreadsAtivas, BuildMode buildMode, String batchName, BuildTarget buildTarget) {
		
		super();
		
		this.threadsAtivas = 0;
		this.maxThreadsAtivas = maxThreadsAtivas;
		this.grafoDependencias = grafoDependencias;
		this.nosCompilados = new HashSet<No>();
		this.buildMode = buildMode;
		this.batchName = batchName;
		this.isAborted = false;
		this.buildTarget = buildTarget;
	}

	public void compilaProjetoComDependencias(String idProjeto) throws GrafoCiclicoException, InterruptedException {
		
		BuscaProfundidade.buscaProfundidade(idProjeto, grafoDependencias, this);
	}

	public void compileAll() throws GrafoCiclicoException, InterruptedException {
		
		List<No> listaNaoCompilados = new ArrayList<No>();
		
		for (String idNo : grafoDependencias.getNos().keySet()) {
			No no = grafoDependencias.getNo(idNo);
			
			if (!no.isMarcado()) {
				listaNaoCompilados.add(no);
			}
		}
		
		for (No n : listaNaoCompilados) {
			if (!n.isMarcado()) {
				BuscaProfundidade.buscaProfundidade(n.getId(), grafoDependencias, this);
			}
		}
	}

	public Grafo getGrafoDependencias() {
		return grafoDependencias;
	}

	public void setGrafoDependencias(Grafo grafoDependencias) {
		this.grafoDependencias = grafoDependencias;
	}

	@Override
	public boolean compilar(No no) throws InterruptedException {
		
		while(this.threadsAtivas >= maxThreadsAtivas || no.existsSaidaNaoCompilada()) {
			if (this.isAborted) {
				return false;
			}
			
			Thread.sleep(2000);
		}
		
		synchronized (this.threadsAtivas) {
			this.threadsAtivas++;
		}
		new ThreadCompilacao(no, this).start();
		
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
}
