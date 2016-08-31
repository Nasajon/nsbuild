package br.com.nasajon.nsjbuild;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	public Grafo getCopiaGrafo() {
		Grafo copia = new Grafo();

		for (No n: this.nos.values()) {
			copia.addNo(n.getId(), n.getPath(), n.getProjeto());
		}

		for (No n: this.nos.values()) {
			for (No saida: n.getSaidas()) {
				copia.addAresta(n.getId(), saida.getId());
			}
		}

		return copia;
	}

	public static Grafo montaGrafo(
			ParametrosNsjbuild parametros,
			List<ProjetoWrapper> listaProjetos,
			boolean isBuildForce,
			boolean isBuildAlterados
			) throws FileNotFoundException, FreeCacheException, IOException {

		AvaliadorEstadoCompilacao avaliador = new AvaliadorEstadoCompilacao(parametros);

		// Montando o GRAFO - Primeira passada - Nós:
		Grafo g = new Grafo();
		for (ProjetoWrapper p : listaProjetos) {
			No n = g.addNo(p.getProjeto().getNome(), parametros.getErpPath() + p.getProjeto().getPath(), p);

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
				g.addAresta(p.getProjeto().getNome(), d);
			}
		}

		// Montando o GRAFO - Terceira passada - Marcando nós pendentes de compilação (por dependência com os não compilados):
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

		// Montando o GRAFO - Quarta passada - Marcando todos os nós como não visitados (para não atrapalha a busca em profundidade):
		if (!isBuildForce) {
			for (No n : g.getNos().values()) {
				n.setVisitado(false);
			}
		}
		
		return g;
	}
}
