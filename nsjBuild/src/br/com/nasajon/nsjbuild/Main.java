package br.com.nasajon.nsjbuild;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import br.com.nasajon.nsjbuild.modelXML.Projeto;

public class Main {

	public static void main(String[] args) {

		long inicio = System.currentTimeMillis();

		File raiz = new File("xmls");
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
				e.printStackTrace();
			}
		}
		
		// Montando o GRAFO - Primeira passada - Nós:
		Grafo g = new Grafo();
		for (Projeto p : listaProjetos) {
			g.addNo(p.getNome(), "C:\\@work\\erp\\" + p.getPath());
		}

		// Montando o GRAFO - Segunda passada - Arestas:
		for (Projeto p : listaProjetos) {
			for (String d : p.getDependencias().getDependencia()) {
				g.addAresta(p.getNome(), d);
			}
		}

		try {
			new Compilador(g, 2).compilaProjetoComDependencias("NSORM");
		} catch (GrafoCiclicoException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		long fim = System.currentTimeMillis();
		Double intervaloMinutos = ((fim - inicio)/1000.0)/60.0;

		System.out.println("Demorou " + intervaloMinutos + " (minutos)");
	}
}
