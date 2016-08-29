package br.com.nasajon.buildxmls;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ProjetoSearch {
	
	private String nome;
	private File arquivoDproj;
	private Set<Unit> units;
	private Set<ProjetoSearch> dependencias;
	
	public ProjetoSearch() {
		super();
		
		this.dependencias = new HashSet<ProjetoSearch>();
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public File getArquivoDproj() {
		return arquivoDproj;
	}
	public void setArquivoDproj(File arquivoDproj) {
		this.arquivoDproj = arquivoDproj;
	}
	public Set<Unit> getUnits() {
		return units;
	}
	public void setUnits(Set<Unit> units) {
		this.units = units;
	}
	public Set<ProjetoSearch> getDependencias() {
		return dependencias;
	}
	public void setDependencias(Set<ProjetoSearch> dependencias) {
		this.dependencias = dependencias;
	}

	@Override
	public int hashCode() {
		return this.nome.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		try {
			ProjetoSearch p2 = (ProjetoSearch) obj;
			
			return nome.equals(p2.getNome());
		} catch(ClassCastException e) {
			return false;
		}
	}

	@Override
	public String toString() {
		return this.nome.toString();
	}
	
	
}
