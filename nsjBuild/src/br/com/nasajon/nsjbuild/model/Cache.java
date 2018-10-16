package br.com.nasajon.nsjbuild.model;

import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;

public class Cache {
	private static String CACHE_ENV_NAME = "nsbuild_cache";

	public static String resolveCachePath(ParametrosNsjbuild parametros) {
		if (parametros == null) {
			return "";
		}

		String envValue = System.getenv(CACHE_ENV_NAME);
		String cachePath = parametros.getCachePath();

		if (cachePath == null || cachePath.isEmpty()) {
			return "cache";
		}

		if (envValue != null) {
			cachePath = cachePath.replace("%" + CACHE_ENV_NAME + "%", envValue);
		}

		return cachePath;
	}
}