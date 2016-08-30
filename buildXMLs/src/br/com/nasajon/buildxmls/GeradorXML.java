package br.com.nasajon.buildxmls;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GeradorXML {
	private Map<String, ProjetoSearch> mapaProjetosPorUnit;
	private final String FILA_COMPILACAO_COPIADA = ";commonutils_static;nsXEUtil;nsEmail;nsProtecao;nsDicionarioDeDados;nsEditorConsultas;nsFuncoesControles;nsReport;workflow;nsORM;nsTestFramework;nsTest;nsTelemetria;nsBackup;nsCertificadoDigital;nsImagemCrop;nsInicioRapido;nsProxy;nsLocalizar;nsLog;v2;nsMalaDireta;nsMenuFavoritos;nsTraducao;nsAlertas;nsBroker;nsjMDAAPI;nsjAPI;nsWS;nsLayoutControl;TLB;NsLicenciamento;Resources;nsdashboard;ImpNFeWeb;tiposfollowups;docEngine;PesssoaClassificacao;formapagamento;banco;contafinanceira;Configuracao;centrodecustofinanceiro;configuracoesnumeracoesdnf;grade;usuario;classificacaofinanceira;PerfisUsuario;limitedecreditohistorico;faixadecredito;FiguraTributaria;perfilimportacao;ncm;cest;categoriadeproduto;EntidadesEmpresariais;projetofinanceiro;nsjGrupoDeUsuario;nsjUsuarios;docfis;PrioridadesAtendimentos;PerfilUsuarioSistema;historicospadrao;PromocoesLeads;etiqueta_base;ItemEstabelecimento;scrittamateriais;IPIEnquadramento;PerfilTributario;LocalDeEstoqueEndereco;operacoes;elementosdecontrolebase;objetosservicosbase;item;nsjReportEditor;limitedecredito;documentositensescopo;relatorios;nsIntegradorGED;motorista;veiculo;series;rpsbase;ordensdeservicobase;modelosnaturezas;naturezascontratos;itensfaturamentobase;contrato;consultadocengine;bempatrimonial;rateioPadrao;CicloFaturamento;IntegracaoFinancasPersona;documentorateado;LocalDeEstoque_Operacao;nsGestorLayoutShell;nsdocument;nsPageMaster;documentTemplate;catalogodeofertas;atributos;tiposDeServicos;ItensFaturamento;servicosTecnicos;atualizador_classificacaofinanceira;financeiro;preTitulos;gruposdecomponentes;componentesbase;ofertasbase;dashboardinstancia;ObjetosServicos;NumeroSerie;elementosDeControle;followup;tiposordensservicos;tiposmanutencoes;OperacaoOrdemServico;BI;contacontabil;tabelaDePreco;notasdemercadorias;sociosparticipacoes;socios;bloqueios;arquivoLayout;workflowCommonfeature;midiaorigem;unidades;componentes;segmentoDeAtuacao;qualificacoespj;obra;loterps;produto;composicao;ofertas;documentositens;controlesaldosprodutos;inventario;cfop;geradordedocumento;geradorRequisicao;portfoliobens;rps;exportadordedocumentos;etiqueta;producao;requisicaoalmoxarifado;atendimento;servicosconvenio115_03;cfopexclusao_fatura;templatesemails;moeda;propostaoperacao;Rateios;propostabase;pedidodefaturamento;vouchers;nsjContabilizacao;persona;proposta;dashboardclientes;clientes;tiposProjetos;nsImportador;scp;movimento;codigoReceita;nfs;template_os;representante;contasfornecedores;participantes;ExclusaoEmpresas;projetos;cenarioorcamentario;PeriodosBrasil;nsjEmissorNFSe;danfe;DespesasMedicas;documentosServiceDocument;previsaofinanceira;contaspadroes;nsjAutoUpdate;Nasajon;nsPg;scrittamateriais;ConvTool;nsjPersona;nsjECF;nsjScritta;nsjContabil;nsjContabilizacao;nsjMANAD;nsjDIPJ;nsjDIRF;nsjConversor;categoriadeservico;nsjFinancas;nsjServicos;nsjMDFe;nsjEstoque;nsjCompras;nsjCRM;nsjAgente;nsjAdmin;NsRegService;";
//	private final String FILA_COMPILACAO_COPIADA = ";commonutils_static;nsXEUtil;nsEmail;nsProtecao;nsDicionarioDeDados;nsEditorConsultas;nsFuncoesControles;nsReport;workflow;nsORM;nsTestFramework;nsTest;nsTelemetria;nsBackup;nsCertificadoDigital;nsImagemCrop;nsInicioRapido;nsProxy;nsLocalizar;nsLog;v2;nsMalaDireta;nsMenuFavoritos;nsTraducao;nsAlertas;nsBroker;nsjMDAAPI;nsjAPI;nsWS;nsLayoutControl;TLB;NsLicenciamento;Resources;nsdashboard;ImpNFeWeb;tiposfollowups;docEngine;PesssoaClassificacao;formapagamento;banco;contafinanceira;Configuracao;centrodecustofinanceiro;configuracoesnumeracoesdnf;grade;usuario;classificacaofinanceira;PerfisUsuario;limitedecreditohistorico;faixadecredito;FiguraTributaria;perfilimportacao;ncm;cest;categoriadeproduto;EntidadesEmpresariais;projetofinanceiro;nsjGrupoDeUsuario;nsjUsuarios;docfis;PrioridadesAtendimentos;historicospadrao;PromocoesLeads;etiqueta_base;ItemEstabelecimento;scrittamateriais;IPIEnquadramento;PerfilTributario;LocalDeEstoqueEndereco;operacoes;elementosdecontrolebase;objetosservicosbase;item;nsjReportEditor;limitedecredito;documentositensescopo;relatorios;nsIntegradorGED;motorista;veiculo;series;rpsbase;ordensdeservicobase;itensfaturamentobase;contrato;consultadocengine;bempatrimonial;rateioPadrao;CicloFaturamento;IntegracaoFinancasPersona;documentorateado;LocalDeEstoque_Operacao;nsGestorLayoutShell;nsdocument;nsPageMaster;documentTemplate;catalogodeofertas;atributos;tiposDeServicos;ItensFaturamento;servicosTecnicos;preTitulos;atualizador_classificacaofinanceira;gruposdecomponentes;componentesbase;ofertasbase;dashboardinstancia;ObjetosServicos;NumeroSerie;elementosDeControle;followup;tiposordensservicos;tiposmanutencoes;OperacaoOrdemServico;BI;contacontabil;tabelaDePreco;notasdemercadorias;sociosparticipacoes;socios;bloqueios;arquivoLayout;workflowCommonfeature;midiaorigem;unidades;componentes;segmentoDeAtuacao;qualificacoespj;obra;loterps;produto;composicao;ofertas;documentositens;controlesaldosprodutos;inventario;cfop;financeiro;geradordedocumento;geradorRequisicao;portfoliobens;rps;exportadordedocumentos;etiqueta;producao;requisicaoalmoxarifado;atendimento;servicosconvenio115_03;cfopexclusao_fatura;templatesemails;moeda;modelosnaturezas;naturezascontratos;propostaoperacao;propostabase;pedidodefaturamento;vouchers;proposta;nsjContabilizacao;persona;dashboardclientes;clientes;Rateios;tiposProjetos;nsImportador;scp;movimento;codigoReceita;nfs;template_os;representante;contasfornecedores;participantes;ExclusaoEmpresas;projetos;cenarioorcamentario;PeriodosBrasil;nsjEmissorNFSe;danfe;DespesasMedicas;documentosServiceDocument;previsaofinanceira;contaspadroes;nsjPersona;nsjECF;nsjScritta;nsjContabil;nsjContabilizacao;nsjMANAD;nsjDIPJ;nsjDIRF;nsjConversor;nsjFinancas;nsjServicos;nsjMDFe;nsjEstoque;nsjCompras;nsjCRM;nsjAgente;nsjAdmin;NsRegService;";
//	private final String FILA_COMPILACAO_COPIADA = ";commonutils_static;nsXEUtil;nsEmail;nsProtecao;nsDicionarioDeDados;nsEditorConsultas;nsFuncoesControles;nsReport;workflow;nsORM;nsTestFramework;nsTest;nsTelemetria;nsBackup;nsCertificadoDigital;nsImagemCrop;nsInicioRapido;nsProxy;nsLocalizar;nsLog;v2;nsMalaDireta;nsMenuFavoritos;nsTraducao;nsAlertas;nsBroker;nsjMDAAPI;nsjAPI;nsWS;nsLayoutControl;TLB;NsLicenciamento;Resources;nsdashboard;ImpNFeWeb;tiposfollowups;docEngine;PesssoaClassificacao;formapagamento;banco;contafinanceira;Configuracao;centrodecustofinanceiro;configuracoesnumeracoesdnf;grade;usuario;classificacaofinanceira;PerfisUsuario;limitedecreditohistorico;faixadecredito;FiguraTributaria;perfilimportacao;ncm;cest;categoriadeproduto;EntidadesEmpresariais;projetofinanceiro;docfis;PrioridadesAtendimentos;historicospadrao;PromocoesLeads;etiqueta_base;ItemEstabelecimento;scrittamateriais;IPIEnquadramento;PerfilTributario;LocalDeEstoqueEndereco;operacoes;elementosdecontrolebase;objetosservicosbase;item;nsjReportEditor;limitedecredito;documentositensescopo;relatorios;nsIntegradorGED;motorista;veiculo;series;rpsbase;ordensdeservicobase;itensfaturamentobase;contrato;consultadocengine;bempatrimonial;rateioPadrao;CicloFaturamento;IntegracaoFinancasPersona;documentorateado;LocalDeEstoque_Operacao;nsGestorLayoutShell;nsdocument;nsPageMaster;documentTemplate;catalogodeofertas;atributos;tiposDeServicos;ItensFaturamento;servicosTecnicos;preTitulos;atualizador_classificacaofinanceira;gruposdecomponentes;componentesbase;ofertasbase;dashboardinstancia;ObjetosServicos;NumeroSerie;elementosDeControle;followup;tiposordensservicos;tiposmanutencoes;OperacaoOrdemServico;BI;contacontabil;tabelaDePreco;notasdemercadorias;sociosparticipacoes;socios;bloqueios;arquivoLayout;workflowCommonfeature;midiaorigem;unidades;componentes;segmentoDeAtuacao;qualificacoespj;obra;loterps;produto;composicao;ofertas;documentositens;controlesaldosprodutos;inventario;cfop;financeiro;geradordedocumento;geradorRequisicao;portfoliobens;rps;exportadordedocumentos;etiqueta;producao;requisicaoalmoxarifado;atendimento;servicosconvenio115_03;cfopexclusao_fatura;templatesemails;moeda;modelosnaturezas;naturezascontratos;propostaoperacao;propostabase;pedidodefaturamento;vouchers;proposta;nsjContabilizacao;persona;dashboardclientes;clientes;Rateios;tiposProjetos;nsImportador;scp;movimento;codigoReceita;nfs;template_os;representante;contasfornecedores;participantes;ExclusaoEmpresas;projetos;cenarioorcamentario;PeriodosBrasil;nsjEmissorNFSe;danfe;DespesasMedicas;documentosServiceDocument;previsaofinanceira;contaspadroes;nsjPersona;nsjECF;nsjScritta;nsjContabil;nsjContabilizacao;nsjMANAD;nsjDIPJ;nsjDIRF;nsjConversor;nsjFinancas;nsjServicos;nsjMDFe;nsjEstoque;nsjCompras;nsjCRM;nsjAgente;nsjAdmin;NsRegService;";
	private final String FILA_COMPILACAO = FILA_COMPILACAO_COPIADA.toLowerCase();
	
	public GeradorXML() {
		super();
		
		this.mapaProjetosPorUnit = new HashMap<String, ProjetoSearch>(); 
	}
	
	private void addRelacionamentoUnitProjeto(String unit, ProjetoSearch projeto) throws ReplicacaoUnitException {
		
		ProjetoSearch p = mapaProjetosPorUnit.get(unit);

		if (p != null) {
			int posFila1 = FILA_COMPILACAO.indexOf(";" + p.getNome().toLowerCase() + ";");
			int posFila2 = FILA_COMPILACAO.indexOf(";" + projeto.getNome().toLowerCase() + ";");
			
//			if (posFila1 == -1 || posFila2 == -1) {
//				System.out.println("Erro unit replicada: " + unit + "     Projeto 1: " + p.getNome() + "     Projeto 2: " + projeto.getNome());
//			}
			
			if (posFila2 < posFila1) {
				mapaProjetosPorUnit.put(unit, projeto);
			} else {
				return;
			}
			
			//throw new ReplicacaoUnitException(unit);
		}
		
		mapaProjetosPorUnit.put(unit, projeto);
	}
	
	public void resolverDependencias(List<ProjetoSearch> listaProjetos) throws ReplicacaoUnitException, IOException {
		Set<String> nomeProjetos = new HashSet<String>();
		
		// Passada 1 - Levantando os projetos pelas units:
		Iterator<ProjetoSearch> it = listaProjetos.iterator();
		while(it.hasNext()) {
			ProjetoSearch projeto = it.next();
						
			if (!FILA_COMPILACAO.contains(";" + projeto.getNome().toLowerCase() + ";")) {
				it.remove();
				continue;
			}
			
			if (nomeProjetos.contains(projeto.getNome().toLowerCase())) {
				System.out.println(projeto.getNome() + " - " + projeto.getArquivoDproj().getAbsolutePath());
			} else {
				nomeProjetos.add(projeto.getNome().toLowerCase());
			}
			
			for (Unit unit : projeto.getUnits()) {
				this.addRelacionamentoUnitProjeto(unit.getNome(), projeto);
			}
		}
		
		// Passada 2 - Resolvendo a lista de dependências entre projetos:
		it = listaProjetos.iterator();
		while(it.hasNext()) {
			ProjetoSearch projeto = it.next();
			
			for (Unit unit : projeto.getUnits()) {
				
				String path = projeto.getArquivoDproj().getParentFile().getAbsolutePath() + File.separator + unit.getPath();
				if (path.toUpperCase().endsWith(".DCP")) {
					continue;
				}
				
				File fUnit = new File(path);
				if (!fUnit.exists()) {
					continue;
				}
				
				List<Unit> dependenciasUnit = InterpretadorUnit.extrairDependencias(fUnit);
				
				for (Unit dependencia: dependenciasUnit) {
					if (!projeto.getUnits().contains(dependencia)) {
						ProjetoSearch projetoDependente = this.mapaProjetosPorUnit.get(dependencia.getNome());
//						if (projeto.getNome().equals("producao")) {
//							if (projetoDependente != null && projetoDependente.getNome().contains("nsj")) {
//								System.out.println("dependencia - " + dependencia.getNome());
//								System.out.println("projetoDependente - " + projetoDependente.getNome());
//							}
//						}
						
						if (projetoDependente == null) {
							//System.out.println("Não foi encontrado o projeto para unit: " + dependencia + " PATH: " + path);
						} else {
							if (!projeto.getDependencias().contains(projetoDependente)) {
								projeto.getDependencias().add(projetoDependente);
							}
						}
					}
				}
			}
		}
	}
	
//	private int countMatches(String strPadrao, String strBusca) {
//		
//		int pos = -1;
//		int count = 0;
//		while((pos = strBusca.lastIndexOf(strPadrao)) > -1) {
//			count++;
//			strBusca = strBusca.substring(0, pos);
//		}
//		
//		return count;
//	}
}
