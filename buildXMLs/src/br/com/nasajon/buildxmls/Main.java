package br.com.nasajon.buildxmls;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import br.com.nasajon.nsjbuild.modelXML.Projeto;
import br.com.nasajon.nsjbuild.modelXML.Projeto.Dependencias;

public class Main {

	public static void main(String[] args) throws IOException, DatatypeConfigurationException {
		//		List<String> listaDependencias = InterpretadorUnit.extrairDependencias(new File("C:\\@work\\erp\\source\\desktop_new\\commonfeature\\atendimentos\\commonfeature.atendimentos.Browser.pas"));
		//		
		//		for(String s: listaDependencias) {
		//			System.out.println(s);
		//		}
		//		
		//		Set<String> includes = InterpretadorDproj.extrairIncludes(new File("C:\\@work\\erp\\source\\desktop_new\\commonfeature\\atendimentos\\package\\atendimento.dproj"));
		//		for(String s: includes) {
		//			System.out.println(s);
		//		}

		long inicio = System.currentTimeMillis();

		LocalizadorProjetos localizadorProjetos = new LocalizadorProjetos();

		List<ProjetoSearch> listaProjetos = localizadorProjetos.buscarProjetosSubFolders(new File("C:\\@work\\erp\\source\\desktop_new"));

		//		for(Projeto p: listaProjetos) {
		//			System.out.println(p.getArquivoDproj().getAbsolutePath());
		//		}

		try {
			new GeradorXML().resolverDependencias(listaProjetos);
		} catch (ReplicacaoUnitException e) {
			e.printStackTrace();
		}

		// Gerando os XMLS
		for (ProjetoSearch ps : listaProjetos) {

			//Gerando o bean do xml:
			Projeto p = new Projeto();

			p.setNome(ps.getNome().toLowerCase());

			String path = ps.getArquivoDproj().getAbsolutePath();
			path = path.substring(13);

			p.setPath(path);

			Dependencias dp = new Dependencias();

			for(ProjetoSearch ps2 : ps.getDependencias()) {
				dp.getDependencia().add(ps2.getNome().toLowerCase());
			}

			p.setDependencias(dp);
			
			p.setAutor("[Identidade Nasajon]");
			p.setDataCriacao("[01/01/1900]");
			p.setResumo("[Indicar resumo para documentação do projeto]");
			
			// Escrevendo o xml:
			try {

				File file = new File("xmls/" + p.getNome() + ".nsproj.xml");
				JAXBContext jaxbContext = JAXBContext.newInstance(Projeto.class);
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

				// output pretty printed
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.nasajon.com.br/nsjbuild nsjBuildProject.xsd ");

				jaxbMarshaller.marshal(p, file);

			} catch (JAXBException e) {
				e.printStackTrace();
			}			
		}

		long fim = System.currentTimeMillis();
		Double intervaloMinutos = ((fim - inicio)/1000.0)/60.0;

		System.out.println("Demorou " + intervaloMinutos + " (minutos)");
	}

}
