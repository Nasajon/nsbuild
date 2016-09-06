//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.8-b130911.1802 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2016.09.06 às 05:54:29 PM BRT 
//


package br.com.nasajon.nsjbuild.modelXML;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="autor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dataCriacao" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="resumo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="path" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dependencias" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="dependencia" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="1000"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ignore" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
    "nome",
    "autor",
    "dataCriacao",
    "resumo",
    "path",
    "dependencias",
    "ignore"
})
@XmlRootElement(name = "projeto")
public class Projeto {

    @XmlElement(required = true)
    protected String nome;
    @XmlElement(required = true)
    protected String autor;
    @XmlElement(required = true)
    protected String dataCriacao;
    @XmlElement(required = true)
    protected String resumo;
    @XmlElement(required = true)
    protected String path;
    protected Projeto.Dependencias dependencias;
    protected Boolean ignore;

    /**
     * Obtém o valor da propriedade nome.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o valor da propriedade nome.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNome(String value) {
        this.nome = value;
    }

    /**
     * Obtém o valor da propriedade autor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAutor() {
        return autor;
    }

    /**
     * Define o valor da propriedade autor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAutor(String value) {
        this.autor = value;
    }

    /**
     * Obtém o valor da propriedade dataCriacao.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataCriacao() {
        return dataCriacao;
    }

    /**
     * Define o valor da propriedade dataCriacao.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataCriacao(String value) {
        this.dataCriacao = value;
    }

    /**
     * Obtém o valor da propriedade resumo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResumo() {
        return resumo;
    }

    /**
     * Define o valor da propriedade resumo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResumo(String value) {
        this.resumo = value;
    }

    /**
     * Obtém o valor da propriedade path.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Define o valor da propriedade path.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

    /**
     * Obtém o valor da propriedade dependencias.
     * 
     * @return
     *     possible object is
     *     {@link Projeto.Dependencias }
     *     
     */
    public Projeto.Dependencias getDependencias() {
        return dependencias;
    }

    /**
     * Define o valor da propriedade dependencias.
     * 
     * @param value
     *     allowed object is
     *     {@link Projeto.Dependencias }
     *     
     */
    public void setDependencias(Projeto.Dependencias value) {
        this.dependencias = value;
    }

    /**
     * Obtém o valor da propriedade ignore.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIgnore() {
        return ignore;
    }

    /**
     * Define o valor da propriedade ignore.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIgnore(Boolean value) {
        this.ignore = value;
    }


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
     *         &lt;element name="dependencia" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="1000"/>
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
        "dependencia"
    })
    public static class Dependencias {

        @XmlElement(required = true)
        protected List<String> dependencia;

        /**
         * Gets the value of the dependencia property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the dependencia property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDependencia().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getDependencia() {
            if (dependencia == null) {
                dependencia = new ArrayList<String>();
            }
            return this.dependencia;
        }

    }

}
