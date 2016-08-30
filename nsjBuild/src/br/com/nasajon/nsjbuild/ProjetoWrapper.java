package br.com.nasajon.nsjbuild;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.nasajon.nsjbuild.modelXML.Projeto;

public class ProjetoWrapper {
	private Projeto projeto;
	private File arquivoXML;
	private Calendar ultimaCompilacao;
	
	public ProjetoWrapper() {
		super();
		
		this.ultimaCompilacao = null;
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
}
