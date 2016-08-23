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

	public static void main(String[] args) {

		long inicio = System.currentTimeMillis();
		
		if (args.length < 2) {
			System.out.println("Por favor, indique o projeto a ser compilado. Exemplo de uso:");
			System.out.println("nsjBuild nsjestoque <debug/release> [force]");
			return;
		}
		
		BuildMode bm = resolveBuildMode(args);
		
		if (bm == null) {
			System.out.println("Modo de build inválido. Por favor digite 'debug' ou 'release'. Exemplo de uso:");
			System.out.println("nsjBuild nsjestoque <debug/release> [force]");
			return;
		}
		
		ParametrosNsjbuild parametros = carregaParametrosBuild();

		List<ProjetoWrapper> listaProjetos = carregaListaDeProjetos(parametros);
		if (listaProjetos == null) {
			return;
		}
		
		// Verificando se o projeto passado existe:
		boolean achou = false;
		for (ProjetoWrapper p: listaProjetos) {
			if (p.getProjeto().getNome().equals(args[0])) {
				achou = true;
				break;
			}
		}
		
		if (!achou) {
			System.out.println("Projeto não encontrado: " + args[0]);
			return;
		}
		
		// Varificando se foi passado o parêmtro de build all:
		boolean buildForce = false;
		if (args.length > 2) {
			if (!args[2].equals("force")) {
				System.out.println("Parâmetro de indicação para build force inválido (build ignorando as marcações de projetos já compilados). Exemplo de uso:");
				System.out.println("nsjBuild nsjestoque <debug/release> [force]");
				return;
			} else {
				buildForce = true;
			}
		}
		
		try {
			long antesGrafo = System.currentTimeMillis();
			System.out.println("Montando grafo...");
			Grafo g = montaGrafo(parametros, listaProjetos, buildForce);
			Double intervaloGrafo = ((System.currentTimeMillis() - antesGrafo)/1000.0)/60.0;
			System.out.println("Grafo completo. Tempo: " + intervaloGrafo + " minutos.");
			
			Compilador compilador = new Compilador(g, parametros.getMaxProcessos().intValue(), bm, parametros.getBatchName());
			compilador.compilaProjetoComDependencias(args[0]);
			
			while (!compilador.isAborted() &&  compilador.existsThreadAtiva()) {
				Thread.sleep(2000);
			}
		} catch (GrafoCiclicoException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			System.out.println("Erro de interrupção de thread durante a compilação:");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			System.out.println("Erro de IO ao checar status de compilação dos projetos:");
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

	private static Grafo montaGrafo(ParametrosNsjbuild parametros, List<ProjetoWrapper> listaProjetos, Boolean buildForce) throws IOException, JAXBException, DatatypeConfigurationException {
		AvaliadorEstadoCompilacao avaliador = new AvaliadorEstadoCompilacao(parametros);
		
		// Montando o GRAFO - Primeira passada - Nós:
		Grafo g = new Grafo();
		for (ProjetoWrapper p : listaProjetos) {
			No n = g.addNo(p.getProjeto().getNome(), parametros.getErpPath() + p.getProjeto().getPath(), p.getArquivoXML());
			
			Boolean isProjetoCompilado = false;
			if (!buildForce) {
				isProjetoCompilado = avaliador.isProjetoCompilado(p.getProjeto()); 
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

		// Montando o GRAFO - Terceira passada - Marcando nós pendentes de compilação (por dependência com os não compilados):
		if (!buildForce) {
			Set<String> raizes = new HashSet<String>();
			for (No n : g.getNos().values()) {
				if (!n.isMarcado()) {
					raizes.add(n.getId());
				}
			}
			
			for (String idNo : raizes) {
				BuscaLargura.desmarcaNosQueUtilizamAtual(idNo, g);
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
		
		File fileParametros = new File("nsjBuildParameters.xml");
		if (fileParametros.exists()) {
			XMLHandler xmlHandler = new XMLHandler();
			
			try {
				parametros = xmlHandler.carregaXMLParametros(fileParametros);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		
		// Adicionando o separador de arquivo no final do path do ERP (se necessário):
		if (!parametros.getErpPath().endsWith("\\") && !parametros.getErpPath().endsWith("/")) {
			parametros.setErpPath(parametros.getErpPath() + File.separator);
		}
		
		return parametros;
	}
}
