package br.com.nasajon.nsjbuild.util;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import br.com.nasajon.nsjbuild.model.ProjetoWrapper;
import br.com.nasajon.nsjbuild.modelXML.Projeto;
import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;

public class XMLHandler {

	public ParametrosNsjbuild carregaXMLParametros(File arquivoXML) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(ParametrosNsjbuild.class);
		
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		ParametrosNsjbuild parametros = (ParametrosNsjbuild) jaxbUnmarshaller.unmarshal(arquivoXML);
		
		return parametros;
	}
	
	public ProjetoWrapper carregaXMLProjeto(File arquivoXML) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Projeto.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Projeto p = (Projeto) jaxbUnmarshaller.unmarshal(arquivoXML);
		ProjetoWrapper pw = new ProjetoWrapper();
		pw.setProjeto(p);
		pw.setArquivoXML(arquivoXML);
		
		return pw;
	}
	
//	public void atualizaUltimaCompilacaoXML(File arquivoXML, boolean sucesso) throws JAXBException, DatatypeConfigurationException {
//		Projeto p = this.carregaXMLProjeto(arquivoXML).getProjeto();
//		
//		this.atualizaUltimaCompilacaoXML(arquivoXML, p, sucesso);
//	}
//	
//	public void atualizaUltimaCompilacaoXML(File arquivoXML, Projeto projeto, boolean sucesso) throws JAXBException, DatatypeConfigurationException {
//		JAXBContext jaxbContext = JAXBContext.newInstance(Projeto.class);
//
//		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//		
//		// Setando a data de útlima compilação:
//		if (sucesso) {
//			GregorianCalendar calUltimaCompilacao = (GregorianCalendar) GregorianCalendar.getInstance();
//			calUltimaCompilacao.setTime(new Date());
//			XMLGregorianCalendar xmlCalUltimaCompilacao = DatatypeFactory.newInstance().newXMLGregorianCalendar(calUltimaCompilacao);
//			projeto.setUltimaCompilacao(xmlCalUltimaCompilacao);
//		} else {
//			projeto.setUltimaCompilacao(null);
//		}
//		
//		// output pretty printed
//		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//		jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.nasajon.com.br/nsjbuild nsjBuildProject.xsd ");
//
//		jaxbMarshaller.marshal(projeto, arquivoXML);
//
//	}
}
