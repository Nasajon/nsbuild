//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.8-b130911.1802 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2016.08.30 às 04:40:42 PM BRT 
//


package br.com.nasajon.nsjbuild.modelXML.buildParameters;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de anonymous complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="erp_path" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="xmls_projects_path" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="max_processos" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="batch_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="inline" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="batch_prebuild" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="batch_clean" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "erpPath",
    "xmlsProjectsPath",
    "maxProcessos",
    "batchName",
    "inline",
    "batchPrebuild",
    "batchClean"
})
@XmlRootElement(name = "parametros_nsjbuild")
public class ParametrosNsjbuild {

    @XmlElement(name = "erp_path", required = true)
    protected String erpPath;
    @XmlElement(name = "xmls_projects_path", required = true)
    protected String xmlsProjectsPath;
    @XmlElement(name = "max_processos", required = true)
    protected BigInteger maxProcessos;
    @XmlElement(name = "batch_name", required = true)
    protected String batchName;
    protected boolean inline;
    @XmlElement(name = "batch_prebuild", required = true)
    protected String batchPrebuild;
    @XmlElement(name = "batch_clean", required = true)
    protected String batchClean;

    /**
     * Obtém o valor da propriedade erpPath.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErpPath() {
        return erpPath;
    }

    /**
     * Define o valor da propriedade erpPath.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErpPath(String value) {
        this.erpPath = value;
    }

    /**
     * Obtém o valor da propriedade xmlsProjectsPath.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlsProjectsPath() {
        return xmlsProjectsPath;
    }

    /**
     * Define o valor da propriedade xmlsProjectsPath.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlsProjectsPath(String value) {
        this.xmlsProjectsPath = value;
    }

    /**
     * Obtém o valor da propriedade maxProcessos.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxProcessos() {
        return maxProcessos;
    }

    /**
     * Define o valor da propriedade maxProcessos.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxProcessos(BigInteger value) {
        this.maxProcessos = value;
    }

    /**
     * Obtém o valor da propriedade batchName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatchName() {
        return batchName;
    }

    /**
     * Define o valor da propriedade batchName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchName(String value) {
        this.batchName = value;
    }

    /**
     * Obtém o valor da propriedade inline.
     * 
     */
    public boolean isInline() {
        return inline;
    }

    /**
     * Define o valor da propriedade inline.
     * 
     */
    public void setInline(boolean value) {
        this.inline = value;
    }

    /**
     * Obtém o valor da propriedade batchPrebuild.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatchPrebuild() {
        return batchPrebuild;
    }

    /**
     * Define o valor da propriedade batchPrebuild.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchPrebuild(String value) {
        this.batchPrebuild = value;
    }

    /**
     * Obtém o valor da propriedade batchClean.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatchClean() {
        return batchClean;
    }

    /**
     * Define o valor da propriedade batchClean.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchClean(String value) {
        this.batchClean = value;
    }

}
