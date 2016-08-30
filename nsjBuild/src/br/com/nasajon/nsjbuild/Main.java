package br.com.nasajon.nsjbuild;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;

public class Main {
	private static final String PAR_HELP = "/? -? --help -help /help";
	
	private static final String PAR_BUILD_CLEAN_CACHE = "clean_cache";
	private static final String PAR_BUILD_ALL = "all";
	private static final String PAR_BUILD_ALTERADOS = "alterados";
	private static final String PAR_BUILD_FORCE = "force";

	public static void main(String[] args) {

		long inicio = System.currentTimeMillis();
		
		if (args.length < 1) {
			System.out.println("Por favor, indique o objetivo do build (primeiro par�metro). Exemplo de uso:");
			imprimirFormaUso();
			return;
		}
		
		// Pegando o pr�metro do projeto (objetivo do build):
		String parProjeto = args[0];
		
		// Verificando se � uma chamada ao help:
		if (PAR_HELP.contains(parProjeto)) {
			imprimirFormaUso();
			return;
		}
		
		// Verificando se n�o � uma chamada ao clean (para limpar a cache):
		if (parProjeto.equals(PAR_BUILD_CLEAN_CACHE)) {
			limparCache();
			return;
		}
		
		// Testando se foltou o build mode
		if (args.length < 2) {
			System.out.println("Por favor, indique o objetivo do build (primeiro par�metro), e o modo de build. Exemplo de uso:");
			imprimirFormaUso();
			return;
		}
		
		// Resolvendo o build mode:
		BuildMode bm = resolveBuildMode(args);
		
		if (bm == null) {
			System.out.println("Modo de build inv�lido. Por favor digite '" + BuildMode.debug.toString() + "' ou '" + BuildMode.release.toString() + "'. Exemplo de uso:");
			imprimirFormaUso();
			return;
		}
		
		ParametrosNsjbuild parametros = carregaParametrosBuild();

		List<ProjetoWrapper> listaProjetos = carregaListaDeProjetos(parametros);
		if (listaProjetos == null) {
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
				System.out.println("Projeto n�o encontrado: " + parProjeto);
				return;
			}
		} else if (parProjeto.equals(PAR_BUILD_ALTERADOS)) {
			isBuildAlterados = true;
		}
		
		// Verificando se foi passado o par�metro de build force:
		boolean isBuildForce = false;
		BuildTarget buildTarget = BuildTarget.build;
		
		if (args.length > 2) {
			String terceiroParametro = args[2]; 
			String quartoParametro = "";
			if (args.length > 3) {
				quartoParametro = args[3];
			}
					
			if (!terceiroParametro.equals(PAR_BUILD_FORCE) && !BuildTarget.isBuildTarget(terceiroParametro)) {
				System.out.println("Par�metro de inv�lido. Exemplo de uso:");
				imprimirFormaUso();
				return;
			} else if (!quartoParametro.equals("") && !quartoParametro.equals(PAR_BUILD_FORCE) && !BuildTarget.isBuildTarget(quartoParametro)) {
				System.out.println("Par�metro de inv�lido. Exemplo de uso:");
				imprimirFormaUso();
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
		
		try {
			long antesGrafo = System.currentTimeMillis();
			System.out.println("Montando grafo...");
			Grafo g = montaGrafo(parametros, listaProjetos, isBuildForce, isBuildAlterados);
			Double intervaloGrafo = ((System.currentTimeMillis() - antesGrafo)/1000.0)/60.0;
			System.out.println("Grafo completo. Tempo: " + intervaloGrafo + " minutos.");
			
			Compilador compilador = new Compilador(g, parametros.getMaxProcessos().intValue(), bm, parametros.getBatchName(), buildTarget);
			
			if (!parProjeto.equals(PAR_BUILD_ALL) && !isBuildAlterados) {
				compilador.compilaProjetoComDependencias(parProjeto);
			} else {
				compilador.compileAll();
			}
			
			while (!compilador.isAborted() &&  compilador.existsThreadAtiva()) {
				Thread.sleep(2000);
			}
		} catch (GrafoCiclicoException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			System.out.println("Erro de interrup��o de thread durante a compila��o:");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			System.out.println("Erro de IO ao checar status de compila��o dos projetos:");
			e.printStackTrace();
			return;
		} catch (JAXBException e) {
			System.out.println("Erro ao atualizar XMLs que precisam ser recompilados:");
			e.printStackTrace();
			return;
		} catch (DatatypeConfigurationException e) {
			System.out.println("Erro ao atualizar XMLs que precisam ser recompilados:");
			e.printStackTrace();
			return;
		}
		
		long fim = System.currentTimeMillis();
		Double intervaloMinutos = ((fim - inicio)/1000.0)/60.0;

		System.out.println("Demorou " + intervaloMinutos + " (minutos)");
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

	private static Grafo montaGrafo(
		ParametrosNsjbuild parametros,
		List<ProjetoWrapper> listaProjetos,
		boolean isBuildForce,
		boolean isBuildAlterados
	) throws IOException, JAXBException, DatatypeConfigurationException {
			
		AvaliadorEstadoCompilacao avaliador = new AvaliadorEstadoCompilacao(parametros);
		
		// Montando o GRAFO - Primeira passada - N�s:
		Grafo g = new Grafo();
		for (ProjetoWrapper p : listaProjetos) {
			No n = g.addNo(p.getProjeto().getNome(), parametros.getErpPath() + p.getProjeto().getPath(), p);
			
			Boolean isProjetoCompilado = false;
			if (!isBuildForce) {
				isProjetoCompilado = avaliador.isProjetoCompilado(p, isBuildAlterados); 
			}
			n.setMarcado(isProjetoCompilado);
			n.setVisitado(false);
		}

		// Montando o GRAFO - Segunda passada - Arestas:
		for (ProjetoWrapper p : listaProjetos) {
			for (String d : p.getProjeto().getDependencias().getDependencia()) {
				g.addAresta(p.getProjeto().getNome(), d);
			}
		}

		// Montando o GRAFO - Terceira passada - Marcando n�s pendentes de compila��o (por depend�ncia com os n�o compilados):
		if (!isBuildForce) {
			Set<String> raizes = new HashSet<String>();
			for (No n : g.getNos().values()) {
				if (!n.isMarcado()) {
					raizes.add(n.getId());
				}
			}
			
			for (String idNo : raizes) {
				BuscaLargura.desmarcaNosQueUtilizamAtual(idNo, g, parametros.isInline());
			}
		}
		
		return g;
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
		parametros.setInline(false);
		
		File fileParametros = new File("nsjBuildParameters.xml");
		if (fileParametros.exists()) {
			XMLHandler xmlHandler = new XMLHandler();
			
			try {
				parametros = xmlHandler.carregaXMLParametros(fileParametros);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		
		// Adicionando o separador de arquivo no final do path do ERP (se necess�rio):
		if (!parametros.getErpPath().endsWith("\\") && !parametros.getErpPath().endsWith("/")) {
			parametros.setErpPath(parametros.getErpPath() + File.separator);
		}
		
		return parametros;
	}
	
	private static void limparCache() {
		File dirCache = new File("cache");
		
		if (dirCache.exists() && dirCache.isDirectory()) {
			for (File f: dirCache.listFiles()) {
				if(!f.delete()) {
					System.out.println("Erro ao limpar cache. Erro ao apagar arquivo: " + f.getAbsolutePath());
				}
			}
		}
	}
	
	private static void imprimirFormaUso() {
		System.out.println("nsjBuild <nome do projeto/all/alterados/clean> [debug/release] [" + PAR_BUILD_FORCE + "] [" + BuildTarget.toSeparatedString("/") + "]");
		System.out.println("");
		System.out.println("");
		System.out.println("Conceitos importantes:");
		System.out.println("all - Chama o msbuild para todos os projetos nunca compilados ou modificados.");
		System.out.println("");
		System.out.println("alterados - Chama o msbuild somente para os projetos modificados desde a �ltima compil��o (e para os projetos que dependem dos mesmos).");
		System.out.println("");
		System.out.println("clean_cache - Limpa a cache de projetos compilados (isto � apaga os arquivos de controle para a data da �ltima compila��o).");
		System.out.println("");
		System.out.println("debug/release - Modo de build (s� n�o � obrigat�rio numa chamada ao 'clean').");
		System.out.println("");
		System.out.println("force - Chama o msbuild para todos os projetos na �rvore de compila��o desejada (n�o importando se j� algum projeto j� tenha sido compilado anteriormente).");
		System.out.println("");
		System.out.println("compile/build - Chama o build passando o target desejado (o padr�o � 'compile', para evitar recompila��o desnecess�ria a n�vel das units).");
		System.out.println("");
		System.out.println("");
		System.out.println("Obs.: Para garantir a recompila��o de todos os projetos (antigo build.bat na op��o 0), � preciso usar o comando:");
		System.out.println("nsjbuild all force build");
	}
}
