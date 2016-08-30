package br.com.nasajon.nsjbuild;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;

public class AvaliadorEstadoCompilacao {
	private ParametrosNsjbuild parametrosBuild;
	
	public AvaliadorEstadoCompilacao(ParametrosNsjbuild parametrosBuild) {
		super();
		
		this.parametrosBuild = parametrosBuild;
	}
	
	/**
	 * 
	 * @param projeto
	 * @param isBuildAlterados Se o desejado é compilar apenas os projetos alterados, então consideram-se como compilados (isto é, não precisam ser avaliados) os projetos sobre os quais não se tem informações (a ideia é que o programador terá chamado o nsjbuild anteriormente para compilar o que ele precisava para o trabalho, e com o parâmetro 'alterados', o build compila só o que foi alterado (desde a última compilação) e os projetos que dependem dos mesmos - é chamado um build 'all' no final). 
	 * @return
	 * @throws IOException
	 */
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
