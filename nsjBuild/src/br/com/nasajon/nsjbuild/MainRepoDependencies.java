package br.com.nasajon.nsjbuild;

import java.io.FileNotFoundException;
import java.io.File;
import javax.xml.bind.JAXBException;

import br.com.nasajon.nsjbuild.model.RepositoryDependencyWrapper;
import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;
import br.com.nasajon.nsjbuild.modelXML.repositorydependencies.RepositoryDependencies;
import br.com.nasajon.nsjbuild.modelXML.repositorydependencies.RepositoryDependencies.Dependency;
import br.com.nasajon.nsjbuild.util.Shell;
import br.com.nasajon.nsjbuild.util.XMLHandler;

public class MainRepoDependencies {

	private ParametrosNsjbuild parametros;
	private RepositoryDependencyWrapper dependencyWrapper;

	public boolean execute(ParametrosNsjbuild parametros) {
		this.parametros = parametros;

		try {
			System.out.println("");			
			System.out.println("Clonando/Atualizando a(s) depend�ncias(s). Aguarde...");
			
			dependencyWrapper = loadXMLDependencies(".");

			RepositoryDependencies dependencies = dependencyWrapper.getRepositoryDependencies();
			
			//TO-DO: Tornar essa rotina recursiva.
			for (Dependency dep : dependencies.getDependency()) {
				//this.parametros.setErpPath(this.dependencyWrapper.getOutputDir(dep));
				
				if (dependencyWrapper.isRepositoryExists(dep)) {
					updateRepo(dep);
				} else {
					cloneRepo(dep);
				}
				
				RepositoryDependencyWrapper wrapper = loadXMLDependencies(this.dependencyWrapper.getOutputDir(dep) + "\\build");
				
				String erpPath = this.parametros.getErpPath();				
				this.parametros.setErpPath(this.dependencyWrapper.getOutputDir(dep));
				
				for (Dependency dep1 : wrapper.getRepositoryDependencies().getDependency()) {
					if (wrapper.isRepositoryExists(dep1)) {
						updateRepo(dep1);
					} else {
						cloneRepo(dep1);
					}
				}
				
				this.parametros.setErpPath(erpPath);
			}

			System.out.println("Depend�ncias atualizadas com sucesso.");

			return true;
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return false;
		}
	}

	private void updateRepo(Dependency dependency) throws Exception {
		String outputDir = dependencyWrapper.getOutputDir(dependency);
		
		System.out.println("----------------------ATEN��O-----------------------");
		System.out.println("TODAS as altera��es no branch atual ser�o desfeitas.");
		System.out.println("----------------------------------------------------");
		System.out.println("");
		
		String cmd = "reset --hard";
		cmd = gitBasicCommand(dependency, cmd);
		
		Shell.runCmd(cmd, String.format("Erro ao executar o reset do reposit�rio: %s no diret�rio %s", dependency.getRepository(), outputDir));
		
		System.out.println("Alterando o reposit�rio "+dependency.getRepository()+" para o branch " + dependency.getVersion());
		
		cmd = String.format("fetch origin %s", dependency.getVersion());
		cmd = gitBasicCommand(dependency, cmd);
		
		Shell.runCmd(cmd, String.format("Erro ao executar o fetch do reposit�rio: %s no diret�rio %s", dependency.getRepository(), outputDir));
		
		cmd = String.format("checkout %s", dependency.getVersion());
		cmd = gitBasicCommand(dependency, cmd);
		
		Shell.runCmd(cmd, String.format("Erro ao executar o checkout do reposit�rio: %s no diret�rio %s", dependency.getRepository(), outputDir));
		
		cmd = String.format("pull origin %s", dependency.getVersion());
		cmd = gitBasicCommand(dependency, cmd);
		
		Shell.runCmd(cmd, String.format("Erro ao executar o pull do reposit�rio: %s no diret�rio %s", dependency.getRepository(), outputDir));
	}

	private void cloneRepo(Dependency dependency) throws Exception {
		String outputDir = dependencyWrapper.getOutputDir(dependency);
		
		System.out.println("Clonando reposit�rio " + dependency.getRepository() + " em " + outputDir);

		String cmd = String.format("git clone %s \"%s\"", dependency.getRepository(), outputDir);
		
		Shell.runCmd(cmd, String.format("Erro ao executar o clone do reposit�rio: %s no diret�rio %s", dependency.getRepository(), outputDir));
		
		if (!dependency.getVersion().equals("master")) {
			cmd = String.format("checkout %s", dependency.getVersion());
			cmd = gitBasicCommand(dependency, cmd);
			
			Shell.runCmd(cmd, String.format("Erro ao executar o checkout do reposit�rio: %s no diret�rio %s", dependency.getRepository(), outputDir));
			
			cmd = String.format("pull origin %s", dependency.getVersion());
			cmd = gitBasicCommand(dependency, cmd);
			
			Shell.runCmd(cmd, String.format("Erro ao executar o pull do reposit�rio: %s no diret�rio %s", dependency.getRepository(), outputDir));
		}
	}

	private RepositoryDependencyWrapper loadXMLDependencies(String xmlPath) throws Exception {
		File file = new File(xmlPath + "\\dependencies.xml");
		if (!file.exists()) {
			throw new FileNotFoundException("Arquivo dependencies.xml n�o encontrado no diret�rio " + xmlPath + ".");
		}

		XMLHandler handler = new XMLHandler();

		try {
			RepositoryDependencyWrapper wrapper = handler.carregaXMLRepositoryDependencies(file);
			wrapper.setParametrosNsjbuild(this.parametros);

			return wrapper;
		} catch (JAXBException e) {
			System.out.println("");
			System.out.println("");

			throw new Exception("Erro ao ler XML de depend�ncias de reposit�rios: " + xmlPath);
		}
	}
	
	private String gitBasicCommand(Dependency dependency, String command) {
		String outputDir = dependencyWrapper.getOutputDir(dependency);
		
		return String.format("git --git-dir=\"%s\\.git\" --work-tree=\"%s\" %s", outputDir, outputDir, command);
	}

}