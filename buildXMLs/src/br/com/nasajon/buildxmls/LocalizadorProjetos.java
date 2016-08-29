package br.com.nasajon.buildxmls;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LocalizadorProjetos {
	
	public List<ProjetoSearch> buscarProjetosSubFolders(File raiz) throws IOException {
		List<ProjetoSearch> projetosEncontrados = new ArrayList<ProjetoSearch>();
		
//		if (raiz.isDirectory() && (raiz.getName().equalsIgnoreCase("libraries") || raiz.getName().equalsIgnoreCase("components"))) {
//			return projetosEncontrados;
//		}
		
		File[] filhos = raiz.listFiles();
		
		for(File filho: filhos) {
			if (filho.isDirectory()) {
				List<ProjetoSearch> projetosFilhos = buscarProjetosSubFolders(filho);
				projetosEncontrados.addAll(projetosFilhos);
				continue;
			}
			
			if (filho.getName().endsWith(".dproj")) {
				ProjetoSearch p = new ProjetoSearch();
				p.setArquivoDproj(filho);
				p.setNome(filho.getName().substring(0, filho.getName().length()-6));
				Set<Unit> units = InterpretadorDproj.extrairIncludes(filho); 
				p.setUnits(units);
				
				projetosEncontrados.add(p);
			}
		}
		
		return projetosEncontrados;
	}
}
