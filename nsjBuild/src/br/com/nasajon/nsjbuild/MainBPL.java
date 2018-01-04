package br.com.nasajon.nsjbuild;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import br.com.nasajon.nsjbuild.delphi.InterpretadorDproj;
import br.com.nasajon.nsjbuild.delphi.Unit;
import br.com.nasajon.nsjbuild.model.ProjetoWrapper;
import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;

public class MainBPL {	

	private static final String TAG_PACKAGE_NAME = "#{package_name}";
	private static final String TAG_PROJECT_GUID = "#{project_guid}";
	private static final String TAG_SEARCH_PATH = "#{search_path}";
	private static final String TAG_CONTAINS = "#{contains}";
	private static final String TAG_DCC_REFERENCE = "#{dcc_reference}";
	//private static final String TAG_DCU_OUTPUT_DIR = "#{dcu_output_dir}";

	public boolean execute(ParametrosNsjbuild parametros,
			String paramProjeto, List<ProjetoWrapper> listaProjetos) {		

		if (listaProjetos == null) {
			System.out.println("");
			System.out.println("");
			System.out.println("Não foi possível carregar os XMLs de descrição dos projetos.");

			return false;
		}

		try {
			if (paramProjeto.isEmpty()) {
				for (ProjetoWrapper p : listaProjetos) {
					makeBPLFileProj(p);
				}
			} else {
				ProjetoWrapper projeto = null;
				for (ProjetoWrapper p : listaProjetos) {
					if (p.getProjeto().getNome().equals(paramProjeto)) {
						projeto = p;
						break;
					}
				}

				if (projeto != null) {
					makeBPLFileProj(projeto);
				} else {
					System.out.println("");
					System.out.println("");
					System.out.println("Projeto não encontrado: " + paramProjeto);

					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	private void makeBPLFileProj(ProjetoWrapper projetoWrapper) throws FileNotFoundException {
		System.out.println("Criando pacote BPL para o projeto " + projetoWrapper.getProjeto().getNome() + ".");
		//System.out.println("path: " + projetoWrapper.getProjectPath(parametros));

		//System.out.println("package: " + projetoWrapper.getPackageName());

		makeDpkFile(projetoWrapper);
		makeDprojFile(projetoWrapper);
	}

	private void makeDprojFile(ProjetoWrapper projetoWrapper) throws FileNotFoundException {
		File arquivo = new File(projetoWrapper.getProjectPath() + projetoWrapper.getPackageName() + ".dproj");

		if (arquivo.exists()) {
			System.out.println("Arquivo " + arquivo.getPath() + " já existe, ignorando.");
			return;
		}

		File fileTemplate = new File("templates\\bpl_dproj.template");
		String template;

		try {
			template = this.getFileContent(fileTemplate);
		} catch (FileNotFoundException e) {
			System.out.println("Arquivo de template de dproj da BPL não encontrado.");
			throw e;
		} catch (IOException e) {
			System.out.println("Erro ao ler o arquivo de template de dproj da BPL.");
			e.printStackTrace();
			return;
		}

		String searchPath = "";
		String dccReference = "";

		try {
			File arq = new File(projetoWrapper.getProjectPath() + projetoWrapper.getProjeto().getNome() + ".dproj");

			searchPath = InterpretadorDproj.getSearchPath(arq);
			dccReference = InterpretadorDproj.getDCCReferences(arq);

		} catch (FileNotFoundException e) {
			System.out.println("Não foi possível extrair o Search Path. Arquivo de dproj não encontrado do projeto "+ projetoWrapper.getProjeto().getNome() + ".");
			throw e;
		} catch (IOException e1) {
			System.out.println("Erro ao extrair o Search Path do projeto "+ projetoWrapper.getProjeto().getNome() + ".");
			e1.printStackTrace();
			return;
		}

		String result = template.replace(TAG_PACKAGE_NAME, projetoWrapper.getPackageName());
		result = result.replace(TAG_PROJECT_GUID, "{" + UUID.randomUUID().toString() + "}");
		result = result.replace(TAG_SEARCH_PATH, searchPath);
		result = result.replace(TAG_DCC_REFERENCE, dccReference);

		try
		{
			this.writeFileContent(arquivo, result);
		} catch (IOException e) {
			System.out.println("Erro ao criar o arquivo dproj da BPL do projeto "+ projetoWrapper.getPackageName() +".");
			e.printStackTrace();
			return;
		}
	}

	private String makeContainsSection(Set<Unit> includes) {
		if (includes == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		if (includes.size() > 0) {
			sb.append("contains "  + System.lineSeparator());
		}

		for (Unit u : includes) {
			String line = String.format("  %s in '%s',", u.getNome(), u.getPath());

			sb.append(line + System.lineSeparator());
		}

		if (includes.size() > 0) {
			String result = sb.toString().trim();
			result = result.substring(0, result.length() - 1);

			return result + ";";
		}

		return sb.toString();
	}

	private void makeDpkFile(ProjetoWrapper projetoWrapper) throws FileNotFoundException {
		File arquivo = new File(projetoWrapper.getProjectPath() + projetoWrapper.getPackageName() + ".dpk");

		if (arquivo.exists()) {
			System.out.println("Arquivo " + arquivo.getPath() + " já existe, ignorando.");
			return;
		}

		File fileTemplate = new File("templates\\dpk.template");
		String template;

		try {
			template = this.getFileContent(fileTemplate);
		} catch (FileNotFoundException e) {
			System.out.println("Arquivo de template de DPK não encontrado.");
			throw e;
		} catch (IOException e) {
			System.out.println("Erro ao ler o arquivo de template de DPK.");
			e.printStackTrace();
			return;
		}

		Set<Unit> includes;

		try {
			File arq = new File(projetoWrapper.getProjectPath() + projetoWrapper.getProjeto().getNome() + ".dproj");
			includes = InterpretadorDproj.extrairIncludes(arq);

		} catch (FileNotFoundException e) {
			System.out.println("Não foi possível extrair os includes. Arquivo de dproj não encontrado do projeto "+ projetoWrapper.getProjeto().getNome() +".");
			throw e;
		} catch (IOException e1) {
			System.out.println("Erro ao ler o arquivo de dproj não encontrado do projeto "+ projetoWrapper.getProjeto().getNome() +".");
			e1.printStackTrace();
			return;
		}

		String contains = makeContainsSection(includes);

		String result = template.replace(TAG_PACKAGE_NAME, projetoWrapper.getPackageName());
		result = result.replace(TAG_CONTAINS, contains);

		try
		{
			this.writeFileContent(arquivo, result);
		} catch (IOException e) {
			System.out.println("Erro ao criar o arquivo DPK do projeto "+ projetoWrapper.getProjeto().getNome() +".");
			e.printStackTrace();
			return;
		}
	}

	private String getFileContent(File file) throws FileNotFoundException, IOException {
		StringBuilder lines = new StringBuilder();

		try (FileReader fr = new FileReader(file);
			 BufferedReader br = new BufferedReader(fr);
			)
		{
			String line;

			while ((line = br.readLine()) != null) {
				lines.append(line + System.lineSeparator());
			}
		}

		return lines.toString();
	}

	private void writeFileContent(File file, String content) throws IOException {
		try (FileWriter fw = new FileWriter(file);
			 BufferedWriter bw = new BufferedWriter(fw);
			)
		{
			bw.append(content);
		}
	}
}