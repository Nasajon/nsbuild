package br.com.nasajon.nsjbuild.delphi;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.nasajon.nsjbuild.exception.ReplicacaoUnitException;
import br.com.nasajon.nsjbuild.model.ProjetoWrapper;
import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;

public class AnalisadorDependenciasEntreProjetos {
	private Map<String, ProjetoWrapper> mapaProjetosPorUnit;
	private String filaCompilacao = "";	
	private boolean mostrarReplicacoesUnits;

	public AnalisadorDependenciasEntreProjetos(ParametrosNsjbuild parametros, boolean mostrarReplicacoesUnits) {
		super();
		
		this.mapaProjetosPorUnit = new HashMap<String, ProjetoWrapper>();
		this.mostrarReplicacoesUnits = mostrarReplicacoesUnits;
	}

	public void resolverDependencias(List<ProjetoWrapper> listaProjetos, StringBuilder sbSaida) throws ReplicacaoUnitException, IOException {

		this.criaFilaCompilacao(listaProjetos, sbSaida);

		// Passada 0 - Fazendo parser de units por projeto:
		Iterator<ProjetoWrapper> it = listaProjetos.iterator();
		while(it.hasNext()) {
			ProjetoWrapper projeto = it.next();

			File arquivoDproj = new File(projeto.getProjectFullName());

			Set<Unit> units = InterpretadorDproj.extrairIncludes(arquivoDproj);

			projeto.setUnits(units);
		}

		// Passada 1 - Criando mapa de projetos indexado pelas units:
		it = listaProjetos.iterator();
		while(it.hasNext()) {
			ProjetoWrapper projeto = it.next();

			if (!filaCompilacao.contains(";" + projeto.getProjeto().getNome().toLowerCase() + ";")) {
				it.remove();
				continue;
			}

			for (Unit unit : projeto.getUnits()) {
				this.addRelacionamentoUnitProjeto(unit.getNome(), projeto, sbSaida);
			}
		}

		// Passada 2 - Resolvendo a lista de dependências entre projetos:
		it = listaProjetos.iterator();
		while(it.hasNext()) {
			ProjetoWrapper projeto = it.next();

			File arquivoDproj = new File(projeto.getProjectFullName());
			File dirDproj = arquivoDproj.getParentFile();

			for (Unit unit : projeto.getUnits()) {
				String pathUnit = dirDproj.getAbsolutePath() + File.separator + unit.getPath();
				if (pathUnit.toUpperCase().endsWith(".DCP")) {
					continue;
				}

				File fUnit = new File(pathUnit);
				if (!fUnit.exists()) {
					continue;
				}

				List<Unit> dependenciasUnit = InterpretadorUnit.extrairDependencias(fUnit);

				for (Unit dependencia: dependenciasUnit) {
					if (!projeto.getUnits().contains(dependencia)) {
						ProjetoWrapper projetoDependente = this.mapaProjetosPorUnit.get(dependencia.getNome());

						if (projetoDependente == null) {
							// Ignora units sem projeto encontrado!!! Pode se tratar de units em BPLs ou padrões do Celphi como SyUtils
						} else {
							if (!projeto.getDependenciasCalculadas().contains(projetoDependente)) {
								projeto.getDependenciasCalculadas().add(projetoDependente);
							}
						}
					}
				}
			}
		}
	}

	private void addRelacionamentoUnitProjeto(String unit, ProjetoWrapper projeto, StringBuilder sbSaida) throws ReplicacaoUnitException {

		ProjetoWrapper p = mapaProjetosPorUnit.get(unit);

		if (p != null) {
			int posFila1 = filaCompilacao.indexOf(";" + p.getProjeto().getNome().toLowerCase() + ";");
			int posFila2 = filaCompilacao.indexOf(";" + projeto.getProjeto().getNome().toLowerCase() + ";");

			if (mostrarReplicacoesUnits) {
				sbSaida.append("ATENCAO: Unit de nome '" + unit + "' replicada nos projetos: '" + projeto.getProjeto().getNome() + "' e '" + p.getProjeto().getNome() + "'.\r\n");
			}

			if (posFila2 < posFila1) {
				mapaProjetosPorUnit.put(unit, projeto);
			} else {
				return;
			}
		}

		mapaProjetosPorUnit.put(unit, projeto);
	}

	private void criaFilaCompilacao(List<ProjetoWrapper> listaProjetos, StringBuilder sbSaida) {

		Iterator<ProjetoWrapper> it = listaProjetos.iterator();
		while(it.hasNext()) {
			ProjetoWrapper projeto = it.next();

			if (filaCompilacao.contains(";" + projeto.getProjeto().getNome().toLowerCase() + ";")) {
				sbSaida.append("ATENCAO: Replicação de nome de projeto: " + projeto.getProjeto().getNome() + "\r\n");
			} else {
				filaCompilacao += ";" + projeto.getProjeto().getNome().toLowerCase();
			}
		}

		filaCompilacao = filaCompilacao + ";";

		sbSaida.append("Fila de compilação:\r\n");
		sbSaida.append(filaCompilacao + "\r\n");
	}
}
