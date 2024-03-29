package br.com.nasajon.nsjbuild.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.nasajon.nsjbuild.controller.AvaliadorEstadoCompilacao;
import br.com.nasajon.nsjbuild.controller.BuscaLargura;
import br.com.nasajon.nsjbuild.delphi.AvaliadorEstadoCompilacaoDelphi;
import br.com.nasajon.nsjbuild.exception.DependenciaInvalidaException;
import br.com.nasajon.nsjbuild.exception.FreeCacheException;
import br.com.nasajon.nsjbuild.exception.ProjectFileNotFoundException;
import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;

public class Grafo {

	private Map<String, No> nos;
	private Set<No> folhas;

	public Grafo() {
		super();

		nos = new HashMap<String, No>();
		folhas = new HashSet<No>();
	}

	public Map<String, No> getNos() {
		return nos;
	}
	public Set<No> getFolhas() {
		return folhas;
	}
	public No getFolha() {
		if(this.folhas.size() == 0) {
			return null;
		}

		return this.folhas.iterator().next();
	}

	public No getNo(String idNo) {
		return this.nos.get(idNo);
	}
	public No addNo(String id, String path, ProjetoWrapper projeto) {
		No no = nos.get(id);
		if(no != null) {
			return no;
		}

		no = new No(id, path, projeto);
		this.nos.put(id, no);
		return no;
	}
	public boolean removeNo(String idNo) {
		No no = this.nos.get(idNo);
		if(no == null) {
			return false;
		}

		Iterator<No> it = no.getEntradas().iterator();
		while(it.hasNext()) {
			No n2 = it.next();

			it.remove();
			n2.getSaidas().remove(no);

			// Atualizando as folhas:
			if (n2.getSaidas().size() == 0) {
				this.folhas.add(n2);
			}
		}

		it = no.getSaidas().iterator();
		while(it.hasNext()) {
			No n2 = it.next();

			it.remove();
			n2.getEntradas().remove(no);

			// Atualizando as folhas:
			if (n2.getSaidas().size() == 0) {
				this.folhas.add(n2);
			}
		}

		this.folhas.remove(no);

		return true;
	}

	public boolean addAresta(String idOrigem, String idDestino) {
		No origem = nos.get(idOrigem);
		No destino = nos.get(idDestino);

		if (origem == null || destino == null) {
			return false;
		}

		origem.addSaida(destino);

		// Atualizando as folhas:
		this.folhas.remove(origem);
		if (destino.getSaidas().size() == 0) {
			folhas.add(destino);
		}

		return true;
	}

	public Grafo getCloneGrafo() {
		Grafo copiaGrafo = new Grafo();

		for (No n: this.nos.values()) {
			copiaGrafo.getNos().put(n.getId(), n.getCloneSemRelacionamentos());
		}

		for (No n: this.nos.values()) {
			for (No saida: n.getSaidas()) {
				copiaGrafo.addAresta(n.getId(), saida.getId());
			}
		}

		return copiaGrafo;
	}

	public static Grafo montaGrafo(
			ParametrosNsjbuild parametros,
			List<ProjetoWrapper> listaProjetos,
			boolean isBuildForce,
			boolean isBuildAlterados
			) throws FileNotFoundException, FreeCacheException, IOException, DependenciaInvalidaException, ProjectFileNotFoundException {

		AvaliadorEstadoCompilacao avaliador = new AvaliadorEstadoCompilacaoDelphi(parametros);

		// Montando o GRAFO - Primeira passada - N�s:
		Grafo g = new Grafo();
		for (ProjetoWrapper p : listaProjetos) {
			//String pathDproj = parametros.getErpPath() + p.getProjeto().getPath();
			String pathDproj = p.getProjectFullName();

			if (!(new File(pathDproj)).exists()) {
				throw new ProjectFileNotFoundException(p.getProjeto().getNome(), pathDproj);
			}

			No n = g.addNo(p.getProjeto().getNome(), pathDproj, p);

			Boolean isProjetoCompilado = false;
			if (!isBuildForce) {
				isProjetoCompilado = avaliador.isProjetoCompilado(p, isBuildAlterados);
			}
			n.setMarcado(isProjetoCompilado);
			n.setVisitado(false);
		}

		// Montando o GRAFO - Segunda passada - Arestas:
		for (ProjetoWrapper p : listaProjetos) {
			for (String d : p.getProjeto().getDependencias().getDependencia()) {
				if (!g.addAresta(p.getProjeto().getNome(), d)) {
					throw new DependenciaInvalidaException(p.getProjeto().getNome(), d);
				}
			}
		}

		// Montando o GRAFO - Terceira passada - Marcando n�s pendentes de compila��o (por depend�ncia com os n�o compilados):
		if (!isBuildForce) {
			Set<String> raizes = new HashSet<String>();
			for (No n : g.getNos().values()) {
				if (!n.isMarcado()) {
					raizes.add(n.getId());
				}
			}

			for (String idNo : raizes) {
				No n = g.getNo(idNo);

				if (!n.isVisitado()) {
					BuscaLargura.desmarcaNosQueUtilizamAtual(idNo, g, parametros.isInline());
				}
			}
		}

		// Montando o GRAFO - Quarta passada - Marcando todos os n�s como n�o visitados (para n�o atrapalha a busca em profundidade):
		if (!isBuildForce) {
			for (No n : g.getNos().values()) {
				n.setVisitado(false);
			}
		}

		return g;
	}
}
