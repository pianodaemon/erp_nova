
package com.buzonfiscal.ns.xsd.bf.requesttimbracfdi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InfoAdicionalType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InfoAdicionalType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="Atributo" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Valor" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfoAdicionalType")
public class InfoAdicionalType {

    @XmlAttribute(name = "Atributo", required = true)
    protected String atributo;
    @XmlAttribute(name = "Valor", required = true)
    protected String valor;

    /**
     * Gets the value of the atributo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtributo() {
        return atributo;
    }

    /**
     * Sets the value of the atributo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtributo(String value) {
        this.atributo = value;
    }

    /**
     * Gets the value of the valor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValor() {
        return valor;
    }

    /**
     * Sets the value of the valor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValor(String value) {
        this.valor = value;
    }

}
