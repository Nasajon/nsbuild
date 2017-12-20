package br.com.nasajon.nsjbuild.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import br.com.nasajon.nsjbuild.delphi.Unit;
import br.com.nasajon.nsjbuild.exception.FreeCacheException;
import br.com.nasajon.nsjbuild.modelXML.Projeto;
import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;

public class ProjetoWrapper {
	private Projeto projeto;
	private File arquivoXML;
	private Calendar ultimaCompilacao;
	private Set<Unit> units;
	private Set<ProjetoWrapper> dependenciasCalculadas;

	public ProjetoWrapper() {
		super();

		this.ultimaCompilacao = null;
		this.dependenciasCalculadas = new HashSet<ProjetoWrapper>();
		this.units = new HashSet<Unit>();
	}

	public File getArquivoXML() {
		return arquivoXML;
	}
	public void setArquivoXML(File arquivoXML) {
		this.arquivoXML = arquivoXML;
	}
	public Projeto getProjeto() {
		return projeto;
	}
	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public Calendar getUltimaCompilacao() {
		if (this.ultimaCompilacao != null) {
			return this.ultimaCompilacao;
		}

		File projetoCache = this.getCacheFile();

		if (projetoCache.exists()) {
			this.ultimaCompilacao = GregorianCalendar.getInstance();
			this.ultimaCompilacao.setTime(new Date(projetoCache.lastModified()));

			return this.ultimaCompilacao;
		} else {
			return null;
		}
	}

	/**
	 * Atualiza a data e hora de última compilação do projeto (no diretório de cache).
	 *
	 * @param date Data e hora da última compilação; se for passado null, o arquivo de cache do projeto será excluído, registrando necessidade de compilar novamente o projeto.
	 */
	public void setUltimaCompilacao(Date date) throws FileNotFoundException, IOException, FreeCacheException {
		File projetoCache = this.getCacheFile();

		if (date != null) {
			if (this.ultimaCompilacao == null) {
				this.ultimaCompilacao = GregorianCalendar.getInstance();
			}
			this.ultimaCompilacao.setTime(date);

	        if (!projetoCache.exists()) {
	        	new FileOutputStream(projetoCache).close();
	        }

			projetoCache.setLastModified(date.getTime());
		} else {
	        if (projetoCache.exists()) {
	        	if (!projetoCache.delete()) {
	        		throw new FreeCacheException(projetoCache);
	        	}
	        }

	        this.ultimaCompilacao = null;
		}
	}

	private File getCacheFile() {
		File projetoCache = new File("cache" + File.separator + projeto.getNome() + ".cache");

		projetoCache.getParentFile().mkdirs();

		return projetoCache;
	}

	public Set<Unit> getUnits() {
		return units;
	}

	public void setUnits(Set<Unit> units) {
		this.units = units;
	}

	public Set<ProjetoWrapper> getDependenciasCalculadas() {
		return dependenciasCalculadas;
	}

	public void setDependenciasCalculadas(Set<ProjetoWrapper> dependenciasCalculadas) {
		this.dependenciasCalculadas = dependenciasCalculadas;
	}

	public String getPackageName() {
		return getProjeto().getNome()+"BPL";
	}

	public String getProjectPath(ParametrosNsjbuild parametros) {
		File arq = new File(parametros.getErpPath() + File.separator + getProjeto().getPath());

		return arq.getParent() + File.separator;
	}
}
