package br.com.nasajon.nsjbuild;

import java.util.HashSet;
import java.util.Set;

public class No {
	private String id;
	private String path;
	private Set<No> entradas;
	private Set<No> saidas;
	private boolean marcado;
	private boolean visitado;

	public No(String id, String path) {
		super();
		
		this.id = id;
		this.setPath(path);
		entradas = new HashSet<No>();
		saidas = new HashSet<No>();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Set<No> getEntradas() {
		return entradas;
	}
	public Set<No> getSaidas() {
		return saidas;
	}
	
	public void addSaida(No saida) {
		this.saidas.add(saida);
		
		if (!saida.getEntradas().contains(this)) {
			saida.addEntrada(this);
		}
	}
	public void addEntrada(No entrada) {
		this.entradas.add(entrada);
		
		if (!entrada.getSaidas().contains(this)) {
			entrada.addSaida(this);
		}
	}
	
	public boolean isMarcado() {
		return marcado;
	}
	public void setMarcado(boolean marcado) {
		this.marcado = marcado;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			No noObj = (No) obj;
			return id.equals(noObj.getId());
		} catch(ClassCastException e) {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return this.id;
	}

	public boolean isVisitado() {
		return visitado;
	}

	public void setVisitado(boolean visitado) {
		this.visitado = visitado;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
