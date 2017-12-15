package br.com.nasajon.sqldoc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

	private static Map<String, List<DocFuncao>> mapaPacotes = new HashMap<String, List<DocFuncao>>();
	static String diretorioOrigem = "C:\\@work\\bancos\\desktop\\Scripts\\functions";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		File f = new File(diretorioOrigem);
		File dirSaida = new File(f.getAbsolutePath() + File.separator + "documentacao");

		if (f.isDirectory()) {
			tratarDiretorio(f, dirSaida);
		} else {
			if (!f.getAbsolutePath().toLowerCase().endsWith(".sql")) {
				throw new RuntimeException(
						"Este aplicativo só deve ser utilizado para documentar arquivos de função com extensão .SQL.");
			}

			tratarArquivo(f, dirSaida);
		}
	}

	private static void tratarDiretorio(File f, File dirSaida) throws FileNotFoundException, IOException {
		for (File ff : f.listFiles()) {
			if (ff.getAbsolutePath().toLowerCase().endsWith(".sql")) {
				tratarArquivo(ff, dirSaida);
			}
		}
	}

	private static void tratarArquivo(File f, File dirSaida) throws FileNotFoundException, IOException {

		InterpretadorDocumentacao id = new InterpretadorDocumentacao();

		DocFuncao df = id.interpretar(f);

		String visibilidade = "public";
		if (df.getVisibilidade() == Visibilidade.Privada) {
			visibilidade = "private";
		}

		String pathRelativoDoc = df.getPacote() + File.separator + visibilidade + File.separator + df.getNome()
				+ ".html";

		File saida = new File(dirSaida.getAbsolutePath() + File.separator + pathRelativoDoc);
		saida.getParentFile().mkdirs();

		df.setPathRelativo(pathRelativoDoc);

		try (FileOutputStream fos = new FileOutputStream(saida);
				OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
				BufferedWriter bw = new BufferedWriter(osw);) {
			bw.write(df.buildHtml());
		}

		List<DocFuncao> listaFuncoes = mapaPacotes.get(df.getPacote());
		if (listaFuncoes == null) {
			listaFuncoes = new ArrayList<DocFuncao>();
			mapaPacotes.put(df.getPacote(), listaFuncoes);
		}

		listaFuncoes.add(df);
	}

}
