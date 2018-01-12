package br.com.nasajon.sqldoc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class GeradorArquivosType {
	String caminhoArquivo;
	String conteudo;

	String json;
	List<String> campos;

	public GeradorArquivosType(String caminho) {
		this.json = "";
		this.conteudo = new String();
		this.caminhoArquivo = caminho;
	}

	public void preencheJSON() {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(caminhoArquivo + ".txt"));
			String linha = "";
			boolean gravaLinha = false;
			while (br.ready()) {
				linha = br.readLine();
				if (linha.contains("{")) {
					gravaLinha = true;
				}
				if (linha.contains("@")) {
					break;
				}
				if (gravaLinha && linha != null)
					json += linha + "<br>";

			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void preencheCampos() {
		BufferedReader br;
		campos = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(caminhoArquivo + ".txt"));
			String linha = "";
			boolean gravaLinha = false;
			while (br.ready() && !linha.contains("@Campo")) {
				linha = br.readLine();
			}
			String novaLinha = "";
			while (br.ready()) {
				if (linha.contains("@"))
					gravaLinha = true;
				if (gravaLinha) {
					novaLinha = "\n" + br.readLine();
					while (!novaLinha.contains("@") && br.ready()) {
						linha += novaLinha;
						novaLinha = "\n" + br.readLine();
					}
					campos.add(linha.replaceAll("@", ""));
				}
				linha = novaLinha;
				novaLinha = "";
				gravaLinha = false;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getConteudo() {
		if (!conteudo.isEmpty())
			return conteudo;
		preencheJSON();
		preencheCampos();
		conteudo = json + "\n" + campos.toString();
		return conteudo;
	}

	public String getJson() {
		return "<code style = \"background-color:#DCDCDC; display:block;padding-left:4.0%;\">" + json + "</code>";
	}

	public List<String> getCampos() {
		return campos;
	}

	public void geraArquivoJSONHTML(String nomeJSON) {
		try {
			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(caminhoArquivo + ".html"), "UTF-8"));
			bw.write(geraConteudoJSONHTML(nomeJSON));
			bw.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public String geraConteudoJSONHTML(String nomeJSON) {
		StringBuilder sb = new StringBuilder();
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

		sb.append("<font color=\"white\">");

		sb.append("Type " + nomeJSON);

		sb.append("</font>");
		sb.append("</h1>\r\n");

		sb.append("<img style=\"padding-left:80%;width: auto;height: auto;"
				+ "margin-top:-6.1%;margin-right:0.0%;margin-bottom:2.5%;\" " + " src = 'logo-rodape.png'>");

		sb.append("</div>");
		sb.append("<br>");
		sb.append(
				"<div style=\"background-color: white; min-height:100%;display: block; height: auto; margin-left: 1.5%;"
						+ " margin-right: 1.5%; z-index: 0; border: solid lightgray; padding-left: 4%; padding-right: 18%; padding-top: 15%;\">");

		// Sessão JSON
		sb.append("<h2> Function: </h2>\r\n");
		sb.append("<ul>\r\n");

		sb.append("<li>");
		sb.append(getJson().replaceAll("\n", ",<br>"));
		sb.append("</li>\r\n");

		sb.append("</ul>\r\n");

		// Sessão Campos
		if ((this.campos != null) && (this.campos.size() > 0)) {
			sb.append("<h2> Detalhes: </h2>\r\n");
			sb.append("<ul>\r\n");

			for (String p : this.campos) {
				if (p.contains("Campo")) {
					sb.append("<br> ");
					sb.append("<li>");
					sb.append(p);
					sb.append("<br> ");
					sb.append("</li>\r\n");
				} else {
					sb.append(p);
					sb.append("<br>\r\n");
				}
			}
			sb.append("</ul>\r\n");
		}
		sb.append("</div>");
		// rodape

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

	public static void main(String[] args) {
		GeradorArquivosType gat = new GeradorArquivosType(
				"C:\\@work\\bancos\\desktop\\Scripts\\functions\\documentacao\\GrafoFormula\\public\\ns.pessoaNovo");
		gat.getConteudo();
		gat.geraArquivoJSONHTML("ns.pessoaNovo");
		System.out.println(gat.getJson().toString());
	}
}
