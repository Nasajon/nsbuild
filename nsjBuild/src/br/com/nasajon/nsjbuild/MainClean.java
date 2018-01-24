package br.com.nasajon.nsjbuild;

import java.io.File;
import java.io.InputStream;

import br.com.nasajon.nsjbuild.model.Cache;
import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;

public class MainClean {

	public boolean execute(ParametrosNsjbuild parametros) {
		if (!this.limparCache(parametros)) {
			return false;
		}
		
		if (!this.callCleanBatch(parametros)) {
			return false;		
		}
		
		return true;
	}
	
	private boolean limparCache(ParametrosNsjbuild parametros) {
		String cachePath = Cache.resolveCachePath(parametros);
		
		System.out.println("Path: " + cachePath);
		
		File dirCache = new File(cachePath);
		
		if (dirCache.exists() && dirCache.isDirectory()) {
			for (File f: dirCache.listFiles()) {
				if (f.getName().endsWith(".gitignore")) {
					continue;
				}
				
				if(!f.delete()) {
					System.out.println("Erro ao limpar cache. Erro ao apagar arquivo: " + f.getAbsolutePath());

					return false;
				}
			}
		}
		
		return true;
	}
	
	private boolean callCleanBatch(ParametrosNsjbuild parametros) {

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
}
