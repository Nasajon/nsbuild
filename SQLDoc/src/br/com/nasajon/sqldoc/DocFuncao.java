package br.com.nasajon.sqldoc;

import java.util.ArrayList;
import java.util.List;

public class DocFuncao {
	private String nome;
	private Visibilidade visibilidade;
	private String pacote;
	private String descricao;
	private List<DocParametro> parametros;
	private DocRetorno retorno;
	private List<DocExcecao> excecoes;
	private String pathRelativo;
	
	public DocFuncao() {
		super();
		this.parametros = new ArrayList<DocParametro>();
		this.excecoes = new ArrayList<DocExcecao>();
		
		this.pacote = "SemPacote";
		this.visibilidade = Visibilidade.Publica;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Visibilidade getVisibilidade() {
		return visibilidade;
	}
	public void setVisibilidade(Visibilidade visibilidade) {
		this.visibilidade = visibilidade;
	}
	public String getPacote() {
		return pacote;
	}
	public void setPacote(String pacote) {
		this.pacote = pacote;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public List<DocParametro> getParametros() {
		return parametros;
	}
	public void setParametros(List<DocParametro> parametros) {
		this.parametros = parametros;
	}
	public DocRetorno getRetorno() {
		return retorno;
	}
	public void setRetorno(DocRetorno retorno) {
		this.retorno = retorno;
	}
	public List<DocExcecao> getExcecoes() {
		return excecoes;
	}
	public void setExcecoes(List<DocExcecao> excecoes) {
		this.excecoes = excecoes;
	}
	public String getPathRelativo() {
		return pathRelativo;
	}
	public void setPathRelativo(String pathRelativo) {
		this.pathRelativo = pathRelativo;
	}

	public String buildHtml() {
		
		StringBuilder sb = new StringBuilder();
		
		// Cabeçalho:
		sb.append("<html>\r\n");
		sb.append("<head>\r\n");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\"/>\r\n");
		sb.append("</head>\r\n");
		sb.append("<body>\r\n");
		
		// Título da página:
		sb.append("<h1>");
		
		if (this.visibilidade == Visibilidade.Publica) {
			sb.append("<font color=\"green\">");
		} else {
			sb.append("<font color=\"red\">");
		}
		
		sb.append(this.pacote);
		sb.append(" - ");
		sb.append(this.nome);

		sb.append("</font>");
		sb.append("</h1>\r\n");
		
		// Sessão de Decrição da função:
		if (this.descricao != null) {
			sb.append("<h2> Descrição: </h2>\r\n");
			sb.append(this.descricao + "\r\n");
		}
		
		// Sessão de Parâmetros:
		if ((this.parametros != null) && (this.parametros.size() > 0)) {
			sb.append("<h2> Parâmetros: </h2>\r\n");
			sb.append("<ul>\r\n");
			
			for (DocParametro p : this.parametros) {
				sb.append("<li><b>");
				sb.append(p.getTitulo());
				sb.append(":</b> ");
				sb.append(p.getDescricao());
				sb.append("</li>\r\n");
			}
			
			sb.append("</ul>\r\n");
		}
		
		// Sessão de Retorno:
		if (this.retorno != null) {
			sb.append("<h2> Retorno: </h2>\r\n");
			sb.append("<b>");
			sb.append(this.retorno.getTitulo());
			sb.append(":</b> ");
			sb.append(this.retorno.getDescricao() + "\r\n");
		}
		
		// Sessão de Exceções:
		if ((this.excecoes != null) && (this.excecoes.size() > 0)) {
			sb.append("<h2> Exceções: </h2>\r\n");
			sb.append("<ul>\r\n");
			
			for (DocExcecao e : this.excecoes) {
				sb.append("<li><b>");
				sb.append(e.getTitulo());
				sb.append(":</b> ");
				sb.append(e.getDescricao());
				sb.append("</li>\r\n");
			}
			
			sb.append("</ul>\r\n");
		}

		// Finalizando o arquivo:
		sb.append("</body>\r\n");
		sb.append("</html>");
		
		return sb.toString();
	}
}
