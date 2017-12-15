package br.com.nasajon.sqldoc;

import java.io.File;
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
	private List<DocType> types;
	private String pathRelativo;

	public DocFuncao() {
		super();
		this.parametros = new ArrayList<DocParametro>();
		this.excecoes = new ArrayList<DocExcecao>();
		this.types = new ArrayList<DocType>();
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
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<DocParametro> getParametros() {
		return parametros;
	}

	public List<DocType> getTypes() {
		return types;
	}

	public void setTypes(List<DocType> types) {
		this.types = types;
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

	public void gerarArquivosType(String nomeArquivo) {
		String path = Main.diretorioOrigem + File.separator + "documentacao";
		GeradorArquivosType gat = new GeradorArquivosType(path + File.separator + this.getPacote() + File.separator
				+ (visibilidade == Visibilidade.Publica ? "public" : "private") + File.separator + nomeArquivo);
		gat.getConteudo();
		gat.geraArquivoJSONHTML(nomeArquivo);
	}

	public String buildHtml() {

		StringBuilder sb = new StringBuilder();
		// margin-top:5%;padding-left: 1%;padding-right:
		// 5%;width:85.7%;left:4.2%;position: absolute;background-color:#2F2F2F
		// Cabeçalho:
		sb.append("<html>\r\n");
		sb.append("<head>\r\n");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\"/>\r\n");
		sb.append("</head>\r\n");
		sb.append("<style type=\"text/css\">" + " body {	height: 100%;	margin: 0;	padding-left: 2.6%;"
				+ "padding-right: 2.5%;" + "		background:#DCDCDC} "
				+ "{	width: 85.7%;	border:  solid #666666;	" + "margin: 0 auto;	margin-left: 1%;	"
				+ "left: 50%;	z-index: 2;	height: 100%;	position: absolute;}" + "</style>");
		sb.append("<body>\r\n");

		// Título da página:
		sb.append("<div "
				+ "style=\"margin-top:5%;padding-left: 1%;padding-right: 5%;width:85.7%;"
				+ "left:4.2%;position: absolute;background-color:#2F2F2F\">");

		sb.append("<h1>");
		sb.append("<br>");

		if (this.visibilidade == Visibilidade.Publica) {
			sb.append("<font color=\"white\">");
		} else {
			sb.append("<font color=\"red\">");
		}

		sb.append(this.pacote);
		sb.append(" - ");
		sb.append(this.nome);

		sb.append("</font>");
		sb.append("</h1>\r\n");

		sb.append("<img style=\"padding-left:80.0%;width: auto;height: auto;"
				+ "margin-top:-6.1%;margin-right:0.0%;margin-bottom:2.5%;\" " + " src = 'logo-rodape.png'>");

		sb.append("</div>");
		sb.append("<br>");
		// Sessão de Descrição da função:
		sb.append(
				"<div style=\"background-color: white; min-height:100%;display: block; height: auto; margin-left: 1.5%;"
						+ " margin-right: 1.5%; z-index: 0; border: solid lightgray; padding-left: 4%; padding-right: 18%; padding-top: 15%;\">");

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

		// Sessão de types
		if ((this.types != null) && (this.types.size() > 0)) {
			sb.append("<h2> Types: </h2>\r\n");
			sb.append("<ul>\r\n");

			for (DocType dt : this.types) {
				if (!dt.getTitulo().isEmpty()) {
					gerarArquivosType(dt.getTitulo());
					sb.append("<li><b>");
					sb.append("<a target=\"_blank\" href=\"" + dt.getTitulo() + ".html" + "\">");
					sb.append(dt.getTitulo());
					sb.append("</a>");
					sb.append("</b>");

					sb.append("</li>\r\n");
				}
			}

			sb.append("</ul>\r\n");
		}

		sb.append("</div>");

		// Rodapé
		sb.append("<div "
				+ "style=\"margin-top:0%;padding-left: 1%;padding-right: 5%;width:85.7%;left:4.2%;position: absolute;color:white;background-color:black;\">");
		sb.append("<p style=\"padding-left:10%;padding-top:4%;\"><b>©2015&nbsp;Nasajon Sistemas&nbsp;</b>");
		sb.append("<br>");

		sb.append("<b style=\"display:inline;\"><label>Av. Rio Branco, 45 - 18º Centro - Rio de Janeiro - RJ</label>");
		sb.append("<img style=\"padding-left:75%;margin-top:-4.8%;padding-top:1%\"  src = 'logo-rodape.png'>");
		sb.append("<br>");

		sb.append("</b>");

		sb.append("<b>Telefone: 0800 021 7070</b>");
		sb.append("<br>");
		sb.append("</p>");

		sb.append("<br>");
		sb.append("</div>");

		// Finalizando o arquivo:
		sb.append("</body>\r\n");
		sb.append("</html>");

		return sb.toString();
	}
}
