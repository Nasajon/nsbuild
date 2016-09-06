package br.com.nasajon.nsjbuild.delphi;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Set;

import br.com.nasajon.nsjbuild.controller.AvaliadorEstadoCompilacao;
import br.com.nasajon.nsjbuild.model.ProjetoWrapper;
import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;

public class AvaliadorEstadoCompilacaoDelphi extends AvaliadorEstadoCompilacao {
	
	public AvaliadorEstadoCompilacaoDelphi(ParametrosNsjbuild parametrosBuild) {
		super(parametrosBuild);
	}
	
	@Override
	public boolean isProjetoCompilado(ProjetoWrapper projeto, boolean isBuildAlterados) throws IOException {
		if (projeto.getUltimaCompilacao() == null) {
			if (!isBuildAlterados) {
				return false;
			} else {
				return true;
			}
		}
		
		Calendar cal = projeto.getUltimaCompilacao(); 
		
		File arquivoDproj = new File(parametrosBuild.getErpPath() + projeto.getProjeto().getPath());
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
}
