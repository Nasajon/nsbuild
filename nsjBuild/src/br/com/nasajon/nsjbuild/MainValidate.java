package br.com.nasajon.nsjbuild;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import br.com.nasajon.nsjbuild.controller.Compilador;
import br.com.nasajon.nsjbuild.delphi.AnalisadorDependenciasEntreProjetos;
import br.com.nasajon.nsjbuild.exception.DependenciaInvalidaException;
import br.com.nasajon.nsjbuild.exception.GrafoCiclicoException;
import br.com.nasajon.nsjbuild.exception.ProjectFileNotFoundException;
import br.com.nasajon.nsjbuild.exception.ReplicacaoUnitException;
import br.com.nasajon.nsjbuild.model.BuildMode;
import br.com.nasajon.nsjbuild.model.BuildTarget;
import br.com.nasajon.nsjbuild.model.Grafo;
import br.com.nasajon.nsjbuild.model.No;
import br.com.nasajon.nsjbuild.model.ProjetoWrapper;
import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;
import br.com.nasajon.nsjbuild.util.XMLHandler;

public class MainValidate {
	
	private static final String PAR_MOSTRAR_UNITS_REPLICADAS = "units"; 
	private static final String PATH_ARQUIVO_LOG = "logs" + File.separator + "validate.log"; 

	public boolean execute(String[] args, ParametrosNsjbuild parametros) {

		// Carregando a lista de projetos:
		List<ProjetoWrapper> listaProjetos = XMLHandler.carregaListaDeProjetos(parametros);
		if (listaProjetos == null) {
			System.out.println("");
			System.out.println("");
			System.out.println("Não foi possível carregar os XMLs de descrição dos projetos.");

			return false;
		}
		
		Boolean mostrarUnitsReplicadas = false;
		if (args.length > 1) {
			mostrarUnitsReplicadas = args[1].equals(PAR_MOSTRAR_UNITS_REPLICADAS);
		}

		StringBuilder sbSaida = new StringBuilder();
		
		try {
			long antesGrafo = System.currentTimeMillis();
			System.out.println("Montando grafo...");
			Grafo grafo = Grafo.montaGrafo(parametros, listaProjetos, true, false);
			Double intervaloGrafo = ((System.currentTimeMillis() - antesGrafo)/1000.0)/60.0;
			System.out.println("Grafo completo. Tempo: " + String.format("%.4f", intervaloGrafo) + " minutos.");
			
			Compilador compilador = new Compilador(parametros.getMaxProcessos().intValue(), BuildMode.debug, parametros.getBatchName(), BuildTarget.build);
			
			// Rodando a busca para colocar os projetos em ordem:
			Queue<No> simulacaoCompilacao = compilador.simulateCompileAll(grafo);
			
			List<ProjetoWrapper> listaProjetosEmOrdem = new ArrayList<>();
			for (No n: simulacaoCompilacao) {
				listaProjetosEmOrdem.add(n.getProjeto());
			}
			
			// Analisando as dependências entre os projetos:
			AnalisadorDependenciasEntreProjetos analisador = new AnalisadorDependenciasEntreProjetos(parametros, mostrarUnitsReplicadas);
			
			analisador.resolverDependencias(listaProjetosEmOrdem, sbSaida);
			
			// Imprimindo as inconsistências encontradas:
			for (ProjetoWrapper projeto: listaProjetosEmOrdem) {
				List<String> projetosFaltando = new ArrayList<String>();
				List<String> projetosSobrando = new ArrayList<String>();
				
				// Criando estruturas auxiliares:
				Set<String> dependenciasCalculadas = new HashSet<String>();
				for (ProjetoWrapper dependencia: projeto.getDependenciasCalculadas()) {
					dependenciasCalculadas.add(dependencia.getProjeto().getNome());
				}

				Set<String> dependenciasIndicadas = new HashSet<String>();
				for (String dependencia: projeto.getProjeto().getDependencias().getDependencia()) {
					dependenciasIndicadas.add(dependencia);
				}
				
				// Resolvendo faltas e sobras:
				for (String dependencia : dependenciasIndicadas) {
					if (!dependenciasCalculadas.contains(dependencia)) {
						projetosSobrando.add(dependencia);
					}
				}

				for (String dependencia : dependenciasCalculadas) {
					if (!dependenciasIndicadas.contains(dependencia)) {
						projetosFaltando.add(dependencia);
					}
				}
				
				// Imprimindo faltas e sobras:
				if (projetosSobrando.size() > 0 || projetosFaltando.size() > 0) {
					sbSaida.append("\r\n");
					sbSaida.append("PROJETO '" + projeto.getProjeto().getNome() + "':\r\n");
				}
					
				if (projetosSobrando.size() > 0) {
					sbSaida.append("Lista de dependencias desnecessárias no XML:\r\n");
					
					for (String s : projetosSobrando) {
						sbSaida.append(s + "\r\n");
					}
					
					sbSaida.append("\r\n");
				}
				
				if (projetosFaltando.size() > 0) {
					sbSaida.append("Lista de dependencias necessárias não encontradas no XML:\r\n");

					for (String s : projetosFaltando) {
						sbSaida.append(s + "\r\n");
					}
				}
			}
			
		} catch (GrafoCiclicoException e) {
			System.out.println("");
			System.out.println("");
			System.out.println(e.getMessage());
			return false;
		} catch (InterruptedException e) {
			System.out.println("");
			System.out.println("");
			System.out.println("Erro de interrupção de thread durante a compilação:");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.out.println("");
			System.out.println("");
			System.out.println("Erro de IO ao checar status de compilação dos projetos:");
			e.printStackTrace();
			return false;
		} catch (ReplicacaoUnitException e) {
			// Este tipo de excessão está inativada, por enquanto!
			System.out.println("");
			System.out.println("");
			System.out.println(e.getMessage());
			return false;
		} catch (DependenciaInvalidaException e) {
			System.out.println("");
			System.out.println("");
			System.out.println(e.getMessage());
			return false;
		} catch (ProjectFileNotFoundException e) {
			System.out.println("");
			System.out.println("");
			System.out.println(e.getMessage());
			return false;
		}
		
		// Imprimindo saída em tela:
		System.out.println(sbSaida);
		System.out.println("Este log de validação também estará disponível no arquivo: " + PATH_ARQUIVO_LOG);
		
		// Imprimindo saida em arquivo:
		if (sbSaida.length() > 0) {
			File arquivoLog = new File(PATH_ARQUIVO_LOG);
			
			if (arquivoLog.exists()) {
				if (!arquivoLog.delete()) {
					System.out.println("ERRO ao excluir arquivo de log (para imprimir novo log): " + PATH_ARQUIVO_LOG);
				}
			}
			
			try (
					FileWriter fw = new FileWriter(arquivoLog)
			) {
				fw.write(sbSaida.toString());
			} catch (IOException e) {
				System.out.println("Erro ao persistir saída em arquivo:");
				e.printStackTrace();
				return false;
			}
		}
		
		return true;		
	}
}
