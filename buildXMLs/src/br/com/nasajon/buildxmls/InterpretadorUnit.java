package br.com.nasajon.buildxmls;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterpretadorUnit {
	
	public static final String ER_USES = "(uses$)|(uses )";

	public static List<Unit> extrairDependencias(File unit) throws IOException {
		List<Unit> listaUses = new ArrayList<Unit>();
		
		try(
				FileReader fr = new FileReader(unit);
				BufferedReader br = new BufferedReader(fr);
			) {
			
			String linha;
			String strUses = null;
			int qtdUses = 0;
			
			Pattern pattern = Pattern.compile(ER_USES);
			
		    while ((linha = br.readLine()) != null) {
		    	linha = linha.toLowerCase();
		    	
		    	if (strUses == null) {
		    		Matcher matcher = pattern.matcher(linha);
		    		
//			    	int posUses = linha.indexOf(USES);
			    	if(matcher.find()) {
			    		int posUses = matcher.end();
			    		
			    		linha = linha.substring(posUses);
			    		
			    		if (linha.contains("}")) {
			    			continue;
			    		}
			    		
			    		qtdUses ++;
			    		strUses = linha;
			    	}
		    	}
		    	
		    	if (strUses != null) {
		    		int posFim = linha.indexOf(';');
		    		
		    		if (posFim > -1) {
		    			linha = linha.substring(0, posFim);
		    			if (!strUses.contains(linha)) {
		    				strUses += " " + linha;
		    			} else {
		    				int posPontoVirgula = strUses.indexOf(';');
		    				if (posPontoVirgula > -1) {
		    					strUses = strUses.substring(0, posPontoVirgula);
		    				}
		    			}
		    			
		    			String[] split = strUses.split(",");
		    			for (String s: split) {
		    				Unit u = new Unit();
		    				u.setNome(s.trim());
		    				
		    				listaUses.add(u);
		    			}
		    			
		    			strUses = null;
		    			
		    			if (qtdUses >= 2) {
		    				break;
		    			}
		    		} else {
		    			int posComment = linha.indexOf("//");
		    			
		    			if (posComment > -1) {
			    			linha = linha.substring(0, posComment);
		    			}
		    			
		    			strUses += " " + linha;
		    		}
		    	}
		    }
		}
		
		return listaUses;
	}
}
