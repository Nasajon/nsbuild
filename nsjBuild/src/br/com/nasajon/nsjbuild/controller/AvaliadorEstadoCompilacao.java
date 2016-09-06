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
	 * @param isBuildAlterados Se o desejado � compilar apenas os projetos alterados, ent�o consideram-se como compilados (isto �, n�o precisam ser avaliados) os projetos sobre os quais n�o se tem informa��es (a ideia � que o programador ter� chamado o nsjbuild anteriormente para compilar o que ele precisava para o trabalho, e com o par�metro 'alterados', o build compila s� o que foi alterado (desde a �ltima compila��o) e os projetos que dependem dos mesmos - � chamado um build 'all' no final). 
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
