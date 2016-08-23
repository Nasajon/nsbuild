package br.com.nasajon.nsjbuild;

public class Unit {
	
	private String nome;
	private String path;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public boolean equals(Object arg0) {
		try {
			Unit u2 = (Unit) arg0;
			
			return nome.equals(u2.getNome());
		} catch(ClassCastException e) {
			return false;
		}
	}
	@Override
	public int hashCode() {
		return nome.hashCode();
	}
	@Override
	public String toString() {
		return nome.toString();
	}
	
}
