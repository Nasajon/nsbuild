package br.com.nasajon.nsjbuild.model;

import java.io.File;

import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;
import br.com.nasajon.nsjbuild.modelXML.repositorydependencies.RepositoryDependencies;
import br.com.nasajon.nsjbuild.modelXML.repositorydependencies.RepositoryDependencies.Dependency;

public class RepositoryDependencyWrapper {

	private RepositoryDependencies repositoryDependencies;
	private ParametrosNsjbuild parametros;

	public RepositoryDependencyWrapper(RepositoryDependencies repositoryDependencies) {
		this.repositoryDependencies = repositoryDependencies;
	}

	public RepositoryDependencies getRepositoryDependencies() {
		return repositoryDependencies;
	}

	public void setParametrosNsjbuild(ParametrosNsjbuild parametrosNsjbuild) {
		this.parametros = parametrosNsjbuild;
	}

	public boolean isRepositoryExists(Dependency dependency) {
		File dir = new File(getOutputDir(dependency));
		return dir.exists();
	}

	public String getOutputDir(Dependency dependency) {
		return parametros.getErpPath() + "\\_externals\\" + dependency.getValue();
	}
}