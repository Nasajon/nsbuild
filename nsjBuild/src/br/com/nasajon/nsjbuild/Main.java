package br.com.nasajon.nsjbuild;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import br.com.nasajon.nsjbuild.modelXML.Projeto;
import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;

public class Main {

	public static void main(String[] args) {

		long inicio = System.currentTimeMillis();
		
		if (args.length < 2) {
			System.out.println("Por favor, indique o projeto a ser compilado. Exemplo de uso:");
			System.out.println("nsjBuild nsjestoque <debug/release>");
			return;
		}
		
		BuildMode bm = null;
		bm = resolveBuildMode(args, bm);
		
		if (bm == null) {
			System.out.println("Modo de build inválido. Por favor digite 'debug' ou 'release'. Exemplo de uso:");
			System.out.println("nsjBuild nsjestoque <debug/release>");
		}
		
		ParametrosNsjbuild parametros = carregaParametrosBuild();

		List<Projeto> listaProjetos = carregaListaDeProjetos(parametros);
		if (listaProjetos == null) {
			return;
		}
		
		// Verificando se o projeto passado existe:
		boolean achou = false;
		for (Projeto p: listaProjetos) {
			if (p.getNome().equals(args[0])) {
				achou = true;
				break;
			}
		}
		
		if (!achou) {
			System.out.println("Projeto não encontrado: " + args[0]);
			return;
		}
		
		Grafo g = montaGrafo(parametros, listaProjetos);

		try {
			new Compilador(g, parametros.getMaxProcessos().intValue(), bm, parametros.getBatchName()).compilaProjetoComDependencias(args[0]);
		} catch (GrafoCiclicoException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			System.out.println("Erro de interrupção de thread durante a compilação:");
			e.printStackTrace();
			return;
		}
		
		long fim = System.currentTimeMillis();
		Double intervaloMinutos = ((fim - inicio)/1000.0)/60.0;

		System.out.println("Demorou " + intervaloMinutos + " (minutos)");
	}

	private static BuildMode resolveBuildMode(String[] args, BuildMode bm) {
		if (args[1].equals(BuildMode.debug.toString())) {
			bm = BuildMode.debug;
		}
		if (args[1].equals(BuildMode.release.toString())) {
			bm = BuildMode.release;
		}
		return bm;
	}

	private static Grafo montaGrafo(ParametrosNsjbuild parametros, List<Projeto> listaProjetos) {
		// Montando o GRAFO - Primeira passada - Nós:
		Grafo g = new Grafo();
		for (Projeto p : listaProjetos) {
			g.addNo(p.getNome(), parametros.getErpPath() + p.getPath());
		}

		// Montando o GRAFO - Segunda passada - Arestas:
		for (Projeto p : listaProjetos) {
			for (String d : p.getDependencias().getDependencia()) {
				g.addAresta(p.getNome(), d);
			}
		}
		return g;
	}

	private static List<Projeto> carregaListaDeProjetos(ParametrosNsjbuild parametros) {
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

		List<Projeto> listaProjetos = new ArrayList<Projeto>();
		
		for (File f : raiz.listFiles(textFilter)){
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(Projeto.class);

				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				Projeto p = (Projeto) jaxbUnmarshaller.unmarshal(f);
				
				listaProjetos.add(p);
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
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(ParametrosNsjbuild.class);
	
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				parametros = (ParametrosNsjbuild) jaxbUnmarshaller.unmarshal(fileParametros);
				
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
