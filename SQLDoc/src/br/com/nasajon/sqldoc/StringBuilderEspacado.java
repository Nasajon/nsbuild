package br.com.nasajon.sqldoc;

public class StringBuilderEspacado {
	
	private StringBuilder sb;
	
	public StringBuilderEspacado() {
		super();
		
		this.sb = new StringBuilder();
	}
	
	public void append(String s) {
		s = s.replaceAll("<", "&lt;");
		s = s.replaceAll(">", "&gt;");
		
		if(sb.length() > 0) {
			sb.append(" " + s);
		} else {
			sb.append(s);
		}
	}
	
	public void clear() {
		this.sb = new StringBuilder();
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
