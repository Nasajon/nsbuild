package br.com.nasajon.nsjbuild;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
	public No addNo(String id, String path, File arquivoXML) {
		No no = nos.get(id);
		if(no != null) {
			return no;
		}
		
		no = new No(id, path,arquivoXML);
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
			copia.addNo(n.getId(), n.getPath(), n.getArquivoXML());
		}
		
		for (No n: this.nos.values()) {
			for (No saida: n.getSaidas()) {
				copia.addAresta(n.getId(), saida.getId());
			}
		}
		
		return copia;
	}
}
