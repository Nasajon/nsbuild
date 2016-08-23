package br.com.nasajon.nsjbuild;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import br.com.nasajon.nsjbuild.modelXML.Projeto;
import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;

public class AvaliadorEstadoCompilacao {
	private ParametrosNsjbuild parametrosBuild;
	
	public AvaliadorEstadoCompilacao(ParametrosNsjbuild parametrosBuild) {
		super();
		
		this.parametrosBuild = parametrosBuild;
	}
	
	public boolean isProjetoCompilado(Projeto projeto) throws IOException {
		if (projeto.getUltimaCompilacao() == null) {
			return false;
		}
		
		Calendar cal = projeto.getUltimaCompilacao().toGregorianCalendar(); 
		
		File arquivoDproj = new File(parametrosBuild.getErpPath() + projeto.getPath());
		if (this.contemAlteracao(arquivoDproj, cal)) {
			return false;
		}

		String pathDpr = arquivoDproj.getAbsolutePath();
		if (pathDpr.toLowerCase().endsWith(".dproj")) {
			int pos = pathDpr.toLowerCase().lastIndexOf(".dproj");
			pathDpr = pathDpr.substring(0, pos) + ".dpr";
			File fileDpr = new File(pathDpr);
			
			if (this.contemAlteracao(fileDpr, cal)) {
				return false;
			}
		}
		
		Set<Unit> units = InterpretadorDproj.extrairIncludes(arquivoDproj);
		
		for (Unit u : units) {
			String pathUnit = arquivoDproj.getParentFile().getAbsolutePath();
			if (!pathUnit.endsWith("/") && !pathUnit.endsWith("\\")) {
				pathUnit += File.separator;
			}
			pathUnit += u.getPath();

			File fUnit = new File(pathUnit);
			if (this.contemAlteracao(fUnit, cal)) {
				return false;
			}

			String pathDfm = pathUnit;
			if (pathDfm.toLowerCase().endsWith(".pas")) {
				int pos = pathDfm.toLowerCase().lastIndexOf(".pas");
				pathDfm = pathDfm.substring(0, pos) + ".dfm";
				File fDfm = new File(pathDfm);
				
				if (this.contemAlteracao(fDfm, cal)) {
					return false;
				}
			}			
		}
		
		return true;
	}
	
	private boolean contemAlteracao(File f, Calendar calReferencia) {
		if (!f.exists()) {
			return false;
		}
		
		Long ultimaAlteracao = f.lastModified();
		Calendar calUnit = GregorianCalendar.getInstance();
		calUnit.setTime(new Date(ultimaAlteracao));
		
		if (calUnit.after(calReferencia)) {
			return true;
		} else {
			return false;
		}
	}
}
