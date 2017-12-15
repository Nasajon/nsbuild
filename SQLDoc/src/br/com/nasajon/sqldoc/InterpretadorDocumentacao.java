package br.com.nasajon.sqldoc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class InterpretadorDocumentacao {

	private boolean inBlocoDocumentacao;
	private boolean encontrouNomeFuncao;
	private DocFuncao docFuncao;
	private EstadoDocumentacao estadoAtual;

	private StringBuilderEspacado descricao;

	private StringBuilderEspacado tituloParametro;
	private StringBuilderEspacado descricaoParametro;

	private StringBuilderEspacado tituloRetorno;
	private StringBuilderEspacado descricaoRetorno;

	private StringBuilderEspacado tituloExcessao;
	private StringBuilderEspacado descricaoExcessao;

	private StringBuilderEspacado tituloType;
	private StringBuilderEspacado descricaoType;

	private boolean aguardandoDefinicao;

	public InterpretadorDocumentacao() {
		super();
	}

	public DocFuncao interpretar(File f) throws FileNotFoundException, IOException {
		// Iniciando parâmetros:
		inBlocoDocumentacao = false;
		docFuncao = new DocFuncao();
		estadoAtual = EstadoDocumentacao.Inicio;

		descricao = new StringBuilderEspacado();

		tituloParametro = new StringBuilderEspacado();
		descricaoParametro = new StringBuilderEspacado();

		tituloRetorno = new StringBuilderEspacado();
		descricaoRetorno = new StringBuilderEspacado();

		tituloExcessao = new StringBuilderEspacado();
		descricaoExcessao = new StringBuilderEspacado();

		tituloType = new StringBuilderEspacado();
		descricaoType = new StringBuilderEspacado();

		this.aguardandoDefinicao = false;

		// Iterando o arquivo:
		try (IteratorWords it = new IteratorWords(f)) {
			String word = null;
			while ((word = it.nextWord()) != null) {
				if (this.encontrouNomeFuncao) {
					break;
				}

				if (this.inBlocoDocumentacao) {
					interpretarBlocoDocumentacao(it, word);
				} else {
					interpretarForaDocumentacao(it, word);
				}
			}
		}

		if (encontrouNomeFuncao) {
			return docFuncao;
		} else {
			return null;
		}
	}

	private void interpretarBlocoDocumentacao(IteratorWords it, String word) throws IOException {
		String wordLowerCase = word.toLowerCase();

		if (wordLowerCase.equals("*/")) {
			this.finalizaBloco();
			this.inBlocoDocumentacao = false;
			this.estadoAtual = EstadoDocumentacao.Default;
			return;
		}

		if (estadoAtual == EstadoDocumentacao.Inicio) {
			switch (wordLowerCase) {
			case "@private":
				docFuncao.setVisibilidade(Visibilidade.Privada);
				break;
			case "@public":
				docFuncao.setVisibilidade(Visibilidade.Publica);
				break;
			case "@package":
				String nomePackage = it.nextWord();
				if (nomePackage == null) {
					return;
				}

				docFuncao.setPacote(nomePackage);
				break;
			case "@param":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Parametros;
				this.aguardandoDefinicao = true;
				break;
			case "@return":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Retorno;
				this.aguardandoDefinicao = true;
				break;
			case "@throws":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Excecoes;
				this.aguardandoDefinicao = true;
				break;
			case "@type":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Types;
				this.aguardandoDefinicao = true;
				break;
			default:
				this.estadoAtual = EstadoDocumentacao.Descricao;
				descricao.append(word);
				break;
			}
		} else if (estadoAtual == EstadoDocumentacao.Descricao) {
			switch (wordLowerCase) {
			case "@param":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Parametros;
				this.aguardandoDefinicao = true;
				break;
			case "@return":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Retorno;
				this.aguardandoDefinicao = true;
				break;
			case "@throws":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Excecoes;
				this.aguardandoDefinicao = true;
				break;
			case "@type":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Types;
				this.aguardandoDefinicao = true;
				break;
			default:
				descricao.append(word);
				break;
			}
		} else if (estadoAtual == EstadoDocumentacao.Parametros) {
			switch (wordLowerCase) {
			case ":=":
				this.aguardandoDefinicao = false;
				break;
			case "@param":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Parametros;
				this.aguardandoDefinicao = true;
				break;
			case "@return":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Retorno;
				this.aguardandoDefinicao = true;
				break;
			case "@throws":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Excecoes;
				this.aguardandoDefinicao = true;
				break;
			case "@type":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Types;
				this.aguardandoDefinicao = true;
				break;
			default:
				if (this.aguardandoDefinicao) {
					tituloParametro.append(word);
				} else {
					descricaoParametro.append(word);
				}
				break;
			}
		} else if (estadoAtual == EstadoDocumentacao.Retorno) {
			switch (wordLowerCase) {
			case ":=":
				this.aguardandoDefinicao = false;
				break;
			case "@param":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Parametros;
				this.aguardandoDefinicao = true;
				break;
			case "@return":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Retorno;
				this.aguardandoDefinicao = true;
				break;
			case "@throws":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Excecoes;
				this.aguardandoDefinicao = true;
				break;
			case "@type":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Types;
				this.aguardandoDefinicao = true;
				break;
			default:
				if (this.aguardandoDefinicao) {
					tituloRetorno.append(word);
				} else {
					descricaoRetorno.append(word);
				}
				break;
			}
		} else if (estadoAtual == EstadoDocumentacao.Excecoes) {
			switch (wordLowerCase) {
			case ":=":
				this.aguardandoDefinicao = false;
				break;
			case "@param":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Parametros;
				this.aguardandoDefinicao = true;
				break;
			case "@return":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Retorno;
				this.aguardandoDefinicao = true;
				break;
			case "@throws":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Excecoes;
				this.aguardandoDefinicao = true;
				break;
			case "@type":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Types;
				this.aguardandoDefinicao = true;
				break;
			default:
				if (this.aguardandoDefinicao) {
					tituloExcessao.append(word);
				} else {
					descricaoExcessao.append(word);

				}
				break;
			}
		} else {
			switch (wordLowerCase) {
			case ":=":
				this.aguardandoDefinicao = false;
				break;
			case "@param":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Parametros;
				this.aguardandoDefinicao = true;
				break;
			case "@return":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Retorno;
				this.aguardandoDefinicao = true;
				break;
			case "@throws":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Excecoes;
				this.aguardandoDefinicao = true;
				break;
			case "@type":
				this.finalizaBloco();
				this.estadoAtual = EstadoDocumentacao.Types;
				this.aguardandoDefinicao = true;
				break;
			default:
				if (this.aguardandoDefinicao) {
					tituloType.append(word);
				} else {
					descricaoType.append(word);

				}
				break;
			}
		}
	}

	private void finalizaBloco() {

		if (this.estadoAtual == EstadoDocumentacao.Descricao) {
			this.docFuncao.setDescricao(this.descricao.toString());
			this.descricao = null;
			this.aguardandoDefinicao = false;

		} else if (this.estadoAtual == EstadoDocumentacao.Parametros) {
			DocParametro dp = new DocParametro();
			dp.setTitulo(this.tituloParametro.toString());
			this.tituloParametro.clear();
			dp.setDescricao(this.descricaoParametro.toString());
			this.descricaoParametro.clear();

			this.docFuncao.getParametros().add(dp);
			this.aguardandoDefinicao = false;

		} else if (this.estadoAtual == EstadoDocumentacao.Retorno) {
			DocRetorno dr = new DocRetorno();
			dr.setTitulo(this.tituloRetorno.toString());
			this.tituloRetorno = null;
			dr.setDescricao(this.descricaoRetorno.toString());
			this.descricaoRetorno = null;

			this.docFuncao.setRetorno(dr);
			this.aguardandoDefinicao = false;

		} else if (this.estadoAtual == EstadoDocumentacao.Excecoes) {
			DocExcecao de = new DocExcecao();
			de.setTitulo(this.tituloExcessao.toString());
			this.tituloExcessao.clear();
			de.setDescricao(this.descricaoExcessao.toString());
			this.descricaoExcessao.clear();

			this.docFuncao.getExcecoes().add(de);
			this.aguardandoDefinicao = false;
		} else if (this.estadoAtual == EstadoDocumentacao.Types) {
			DocType dt = new DocType();
			dt.setTitulo(this.tituloType.toString());
			this.tituloType.clear();

			dt.setDescricao(this.descricaoType.toString());
			this.descricaoType.clear();

			this.docFuncao.getTypes().add(dt);
			this.aguardandoDefinicao = false;
		}
	}

	private void interpretarForaDocumentacao(IteratorWords it, String word) throws IOException {
		String wordLowerCase = word.toLowerCase();

		switch (wordLowerCase) {
		case "/**":
			this.inBlocoDocumentacao = true;
			break;
		case "function":
			String nomeFuncao = it.nextWord();
			if (nomeFuncao == null) {
				return;
			}

			int pos = nomeFuncao.indexOf("(");
			if (pos > -1) {
				nomeFuncao = nomeFuncao.substring(0, pos);
			}

			this.encontrouNomeFuncao = true;
			docFuncao.setNome(nomeFuncao);
			break;
		}
	}
}
