package br.com.nasajon.nsjbuild.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import br.com.nasajon.nsjbuild.model.ProjetoWrapper;
import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;

public abstract class AvaliadorEstadoCompilacao {
	protected ParametrosNsjbuild parametrosBuild;
	
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
	public abstract boolean isProjetoCompilado(ProjetoWrapper projeto, boolean isBuildAlterados) throws IOException;
	
	protected boolean contemAlteracao(File f, Calendar calReferencia) {
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
