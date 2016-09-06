package br.com.nasajon.nsjbuild.util;

import java.io.File;
import java.io.IOException;

public class FreeCacheException extends IOException {
	
	private static final long serialVersionUID = 1L;

	public FreeCacheException(File arquivoCache) {
		super ("Erro ao apagar ao excluir arquivo de chache do projeto: " + arquivoCache.getAbsolutePath());
	}
}
