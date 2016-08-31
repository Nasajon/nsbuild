package br.com.nasajon.nsjbuild;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;

public class Main {
	private static final String PAR_HELP = "/? -? --help -help /help";
	
	private static final String PAR_BUILD_CLEAN = "clean";
	private static final String PAR_BUILD_CLEAN_CACHE = "clean_cache";
	private static final String PAR_BUILD_ALL = "all";
	private static final String PAR_BUILD_ALTERADOS = "alterados";
	private static final String PAR_BUILD_FORCE = "force";

	public static void main(String[] args) {

		long inicio = System.currentTimeMillis();
		
		if (args.length < 1) {
			System.out.println("");
			System.out.println("");
			System.out.println("Por favor, indique o objetivo do build (primeiro parâmetro). Exemplo de uso:");
			imprimirFormaUso();
			
			System.exit(1);
			return;
		}
		
		// Pegando o prâmetro do projeto (objetivo do build):
		String parProjeto = args[0];
		
		// Verificando se é uma chamada ao help:
		if (PAR_HELP.contains(parProjeto)) {
			imprimirFormaUso();
			return;
		}
		
		// Verificando se não é uma chamada ao clean (para limpar a cache):
		if (parProjeto.equals(PAR_BUILD_CLEAN_CACHE)) {
			if (!limparCache()) {
				System.exit(1);
			}
			
			return;
		}
		
		// Carregando parâmetros de configuração do build:
		ParametrosNsjbuild parametros = carregaParametrosBuild();
		
		// Verificando se não é uma chamada ao clean (para limpar a cache):
		if (parProjeto.equals(PAR_BUILD_CLEAN)) {
			if (!limparCache()) {
				System.exit(1);
			}
			
			if (!callCleanBatch(parametros)) {
				System.exit(1);
			}
			
			return;
		}
		
		// Testando se foltou o build mode
		if (args.length < 2) {
			System.out.println("");
			System.out.println("");
			System.out.println("Por favor, indique o objetivo do build (primeiro parâmetro), e o modo de build. Exemplo de uso:");
			imprimirFormaUso();

			System.exit(1);
			return;
		}
		
		// Resolvendo o build mode:
		BuildMode bm = resolveBuildMode(args);
		
		if (bm == null) {
			System.out.println("");
			System.out.println("");
			System.out.println("Modo de build inválido. Por favor digite '" + BuildMode.debug.toString() + "' ou '" + BuildMode.release.toString() + "'. Exemplo de uso:");
			imprimirFormaUso();

			System.exit(1);
			return;
		}
		
		List<ProjetoWrapper> listaProjetos = carregaListaDeProjetos(parametros);
		if (listaProjetos == null) {
			System.out.println("");
			System.out.println("");
			System.out.println("Não foi possível carregar os XMLs de descrição dos projetos.");

			System.exit(1);
			return;
		}
		
		// Verificando se o projeto passado existe:
		boolean isBuildAlterados = false;
		
		if (!parProjeto.equals(PAR_BUILD_ALL) && !parProjeto.equals(PAR_BUILD_ALTERADOS)) {
			boolean achou = false;
			for (ProjetoWrapper p: listaProjetos) {
				if (p.getProjeto().getNome().equals(parProjeto)) {
					achou = true;
					break;
				}
			}
			
			if (!achou) {
				System.out.println("");
				System.out.println("");
				System.out.println("Projeto não encontrado: " + parProjeto);

				System.exit(1);
				return;
			}
		} else if (parProjeto.equals(PAR_BUILD_ALTERADOS)) {
			isBuildAlterados = true;
		}
		
		// Verificando se foi passado o parâmetro de build force:
		boolean isBuildForce = false;
		BuildTarget buildTarget = BuildTarget.build;
		
		if (args.length > 2) {
			String terceiroParametro = args[2]; 
			String quartoParametro = "";
			if (args.length > 3) {
				quartoParametro = args[3];
			}
					
			if (!terceiroParametro.equals(PAR_BUILD_FORCE) && !BuildTarget.isBuildTarget(terceiroParametro)) {
				System.out.println("");
				System.out.println("");
				System.out.println("Parâmetro de inválido. Exemplo de uso:");
				imprimirFormaUso();

				System.exit(1);
				return;
			} else if (!quartoParametro.equals("") && !quartoParametro.equals(PAR_BUILD_FORCE) && !BuildTarget.isBuildTarget(quartoParametro)) {
				System.out.println("");
				System.out.println("");
				System.out.println("Parâmetro de inválido. Exemplo de uso:");
				imprimirFormaUso();

				System.exit(1);
				return;
			} else {
				if (terceiroParametro.equals(PAR_BUILD_FORCE) || quartoParametro.equals(PAR_BUILD_FORCE)) {
					isBuildForce = true;
				}
				
				if (BuildTarget.isBuildTarget(terceiroParametro)) {
					buildTarget = BuildTarget.valueOf(terceiroParametro);
				} else if (BuildTarget.isBuildTarget(quartoParametro)) {
					buildTarget = BuildTarget.valueOf(quartoParametro);
				}
			}
		}
		
		// Setando o inline como ON se o target for "Build", pois neste caso independente do INLINE, as
		// dependencias sempre exigem de recompilação.
		if (buildTarget == BuildTarget.build) {
			parametros.setInline(true);
		}
		
		try {
			long antesGrafo = System.currentTimeMillis();
			System.out.println("Montando grafo...");
			Grafo g = Grafo.montaGrafo(parametros, listaProjetos, isBuildForce, isBuildAlterados);
			Double intervaloGrafo = ((System.currentTimeMillis() - antesGrafo)/1000.0)/60.0;
			System.out.println("Grafo completo. Tempo: " + intervaloGrafo + " minutos.");
			
			Compilador compilador = new Compilador(g, parametros.getMaxProcessos().intValue(), bm, parametros.getBatchName(), buildTarget);
			
			if (!callPreBuildBatch(parametros)) {
				System.exit(1);
				return;
			}
			
			if (!parProjeto.equals(PAR_BUILD_ALL) && !isBuildAlterados) {
				compilador.compilaProjetoComDependencias(parProjeto);
			} else {
				compilador.compileAll();
			}
			
			while (!compilador.isAborted() && compilador.existsThreadAtiva()) {
				Thread.sleep(2000);
			}
			
			// Imprimindo mensagem de finalização:
			long fim = System.currentTimeMillis();
			Double intervaloMinutos = ((fim - inicio)/1000.0)/60.0;
			
			if (!compilador.isAborted()) {
				System.out.println("BUILD FINALIZADO COM SUCESSO. Demorou " + intervaloMinutos + " (minutos)");
				return;
			} else {
				System.out.println("Build com falhas. Demorou " + intervaloMinutos + " (minutos)");
				System.exit(1);
				return;
			}
		} catch (GrafoCiclicoException e) {
			System.out.println("");
			System.out.println("");
			System.out.println(e.getMessage());
			System.exit(1);
			return;
		} catch (InterruptedException e) {
			System.out.println("");
			System.out.println("");
			System.out.println("Erro de interrupção de thread durante a compilação:");
			e.printStackTrace();
			System.exit(1);
			return;
		} catch (IOException e) {
			System.out.println("");
			System.out.println("");
			System.out.println("Erro de IO ao checar status de compilação dos projetos:");
			e.printStackTrace();
			System.exit(1);
			return;
		}
	}

	private static BuildMode resolveBuildMode(String[] args) {
		BuildMode bm = null;
		
		if (args[1].equals(BuildMode.debug.toString())) {
			bm = BuildMode.debug;
		}
		if (args[1].equals(BuildMode.release.toString())) {
			bm = BuildMode.release;
		}
		return bm;
	}

	private static List<ProjetoWrapper> carregaListaDeProjetos(ParametrosNsjbuild parametros) {
		File raiz = new File(parametros.getXmlsProjectsPath());
		FilenameFilter textFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".nsproj.xml")) {
					return true;
				} else {
					return false;
				}
			}
		};

		List<ProjetoWrapper> listaProjetos = new ArrayList<ProjetoWrapper>();
		XMLHandler xmlHandler = new XMLHandler();
		
		for (File f : raiz.listFiles(textFilter)){
			try {
				ProjetoWrapper pw = xmlHandler.carregaXMLProjeto(f);
				
				listaProjetos.add(pw);
			} catch (JAXBException e) {
				System.out.println("Erro ao ler XML de projeto: " + f.getAbsolutePath());
				e.printStackTrace();
				return null;
			}
		}
		
		return listaProjetos;
	}

	private static ParametrosNsjbuild carregaParametrosBuild() {
		ParametrosNsjbuild parametros = new ParametrosNsjbuild();
		parametros.setErpPath("c:\\@work\\erp");
		parametros.setMaxProcessos(new BigInteger("2"));
		parametros.setXmlsProjectsPath(new File("xmls").getAbsolutePath());
		parametros.setBatchName("internal_build.bat");
		parametros.setBatchPrebuild("prebuild.bat");
		parametros.setBatchClean("clean.bat");
		parametros.setInline(true);
		
		File fileParametros = new File("nsjBuildParameters.xml");
		if (fileParametros.exists()) {
			XMLHandler xmlHandler = new XMLHandler();
			
			try {
				parametros = xmlHandler.carregaXMLParametros(fileParametros);
			} catch (JAXBException e) {
				System.out.println("");
				System.out.println("");
				System.out.println("Erro ao ler XML de configuração do nsjBuild:");
				e.printStackTrace();
			}
		}
		
		// Adicionando o separador de arquivo no final do path do ERP (se necessário):
		if (!parametros.getErpPath().endsWith("\\") && !parametros.getErpPath().endsWith("/")) {
			parametros.setErpPath(parametros.getErpPath() + File.separator);
		}
		
		return parametros;
	}
	
	private static boolean limparCache() {
		File dirCache = new File("cache");
		
		if (dirCache.exists() && dirCache.isDirectory()) {
			for (File f: dirCache.listFiles()) {
				if(!f.delete()) {
					System.out.println("Erro ao limpar cache. Erro ao apagar arquivo: " + f.getAbsolutePath());

					return false;
				}
			}
		}
		
		return true;
	}
	
	private static boolean callCleanBatch(ParametrosNsjbuild parametros) {

		try {
			Process p = Runtime.getRuntime().exec(parametros.getBatchClean());
			
			if(p.waitFor() != 0) {
				System.out.println("");
				System.out.println("");
				System.out.println("");
				System.out.println("");

				System.out.println("Erro ao executar batch de 'clean' dos projetos:");

				InputStream error = p.getInputStream();
				for (int i = 0; i < error.available(); i++) {
					System.out.print("" + (char)error.read());
				}
				System.out.println("");
				
				error = p.getErrorStream();
				for (int i = 0; i < error.available(); i++) {
					System.out.print("" + (char)error.read());
				}
				
				return false;
			} else {
				System.out.println("Batch de 'clean' executado com sucesso.");
				
				return true;
			}
		} catch (Exception e) {
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");

			System.out.println("Erro ao executar batch de 'clean' dos projetos:");
			e.printStackTrace();
			
			return false;
		}
	}

	private static boolean callPreBuildBatch(ParametrosNsjbuild parametros) {

		System.out.println("Chamando o batch de 'pre-build'...");
		
		try {
			Process p = Runtime.getRuntime().exec(parametros.getBatchPrebuild());
			
			if(p.waitFor() != 0) {
				System.out.println("");
				System.out.println("");
				System.out.println("");
				System.out.println("");
				
				System.out.println("Erro ao executar batch de 'pre-build':");
				
				InputStream error = p.getInputStream();
				for (int i = 0; i < error.available(); i++) {
					System.out.print("" + (char)error.read());
				}
				System.out.println("");
				
				error = p.getErrorStream();
				for (int i = 0; i < error.available(); i++) {
					System.out.print("" + (char)error.read());
				}
				
				return false;
			} else {
				System.out.println("Batch de 'pre-build' executado com sucesso.");
				
				return true;
			}
		} catch (Exception e) {
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			
			System.out.println("Erro ao executar batch de 'pre-build':");
			e.printStackTrace();
			
			return false;
		}
	}
	
	private static void imprimirFormaUso() {
		System.out.println("");
		System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------");
		System.out.println("nsjBuild <nome do projeto/all/alterados/clean/clean_cache> [debug/release] [force] [" + BuildTarget.toSeparatedString("/") + "]");
		System.out.println("");
		System.out.println("");
		System.out.println("Conceitos importantes:");
		System.out.println("all - Chama o msbuild para todos os projetos nunca compilados ou modificados.");
		System.out.println("");
		System.out.println("alterados - Chama o msbuild somente para os projetos modificados desde a última compilação (e para os projetos que dependem dos mesmos).");
		System.out.println("");
		System.out.println("clean - Apaga todas as DCUs e limpa a cache de projetos compilados (isto é apaga os arquivos de controle para a data da última compilação).");
		System.out.println("");
		System.out.println("clean_cache - Limpa a cache de projetos compilados (isto é apaga os arquivos de controle para a data da última compilação).");
		System.out.println("");
		System.out.println("debug/release - Modo de build (só não é obrigatório numa chamada ao 'clean' ou 'clean_cache').");
		System.out.println("");
		System.out.println("force - Chama o msbuild para todos os projetos na árvore de compilação desejada (não importando se já algum projeto já tenha sido compilado anteriormente).");
		System.out.println("");
		System.out.println("compile/build - Chama o build passando o target desejado (o padrão é 'compile', para evitar recompilação desnecessária a nível das units).");
		System.out.println("");
		System.out.println("");
		System.out.println("Obs.: Para garantir a recompilação de todos os projetos (antigo build.bat na opção 0), é preciso usar os comandos:");
		System.out.println("nsjbuild clean");
		System.out.println("nsjbuild all force build");
	}
}
