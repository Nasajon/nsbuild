package br.com.nasajon.nsjbuild;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterpretadorDproj {
	
	public static final String ER_INCLUDE = "(<dccreference include=\")(.*)(\")";

	public static Set<Unit> extrairIncludes(File dproj) throws IOException {
		Set<Unit> listaUses = new HashSet<Unit>();
		
		try(
				FileReader fr = new FileReader(dproj);
				BufferedReader br = new BufferedReader(fr);
			) {
			
			String linha;
			
			Pattern pattern = Pattern.compile(ER_INCLUDE);
			
		    while ((linha = br.readLine()) != null) {
		    	linha = linha.toLowerCase();
	    		Matcher matcher = pattern.matcher(linha);
	    		
		    	if(matcher.find()) {
		    		String path = matcher.group(2);
		    		String nome = path;
		    		
		    		int pos = nome.lastIndexOf(File.separator);
		    		if (pos > -1) {
		    			nome = nome.substring(pos+1);
		    		}

		    		if (nome.endsWith(".pas")) {
		    			nome = nome.substring(0, nome.length()-4);
		    		}
		    		
		    		Unit unit = new Unit();
		    		unit.setNome(nome);
		    		unit.setPath(path);
		    		
		    		listaUses.add(unit);
		    		
//		    		if (unit.getNome().toLowerCase().equals("commonfeature.usuarios.DTO".toLowerCase())) {
//		    			System.out.println(dproj.getAbsolutePath());
//		    		}
		    	}
		    }
		}
		
		return listaUses;
	}

}
