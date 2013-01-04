
package com.buzonfiscal.ns.xsd.bf.timbradocfd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.buzonfiscal.ns.xsd.bf.requesttimbracfdi.DocumentoType;
import com.buzonfiscal.ns.xsd.bf.requesttimbracfdi.InfoAdicionalType;
import com.buzonfiscal.ns.xsd.bf.requesttimbracfdi.InfoBasicaType;


/**
 * <p>Java class for RequestTimbradoCFDType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestTimbradoCFDType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.buzonfiscal.com/ns/xsd/bf/RequestTimbraCFDI}Documento" minOccurs="0"/>
 *         &lt;element ref="{http://www.buzonfiscal.com/ns/xsd/bf/RequestTimbraCFDI}InfoBasica"/>
 *         &lt;element name="InfoAdicional" type="{http://www.buzonfiscal.com/ns/xsd/bf/RequestTimbraCFDI}InfoAdicionalType" maxOccurs="10" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.buzonfiscal.com/ns/xsd/bf/RequestTimbraCFDI}RefID"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestTimbradoCFDType", propOrder = {
    "documento",
    "infoBasica",
    "infoAdicional"
})
public class RequestTimbradoCFDType {

    @XmlElement(name = "Documento", namespace = "http://www.buzonfiscal.com/ns/xsd/bf/RequestTimbraCFDI")
    protected DocumentoType documento;
    @XmlElement(name = "InfoBasica", namespace = "http://www.buzonfiscal.com/ns/xsd/bf/RequestTimbraCFDI", required = true)
    protected InfoBasicaType infoBasica;
    @XmlElement(name = "InfoAdicional")
    protected List<InfoAdicionalType> infoAdicional;
    @XmlAttribute(name = "RefID", namespace = "http://www.buzonfiscal.com/ns/xsd/bf/RequestTimbraCFDI")
    protected String refID;

    /**
     * Gets the value of the documento property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentoType }
     *     
     */
    public DocumentoType getDocumento() {
        return documento;
    }

    /**
     * Sets the value of the documento property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentoType }
     *     
     */
    public void setDocumento(DocumentoType value) {
        this.documento = value;
    }

    /**
     * Gets the value of the infoBasica property.
     * 
     * @return
     *     possible object is
     *     {@link InfoBasicaType }
     *     
     */
    public InfoBasicaType getInfoBasica() {
        return infoBasica;
    }

    /**
     * Sets the value of the infoBasica property.
     * 
     * @param value
     *     allowed object is
     *     {@link InfoBasicaType }
     *     
     */
    public void setInfoBasica(InfoBasicaType value) {
        this.infoBasica = value;
    }

    /**
     * Gets the value of the infoAdicional property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the infoAdicional property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInfoAdicional().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InfoAdicionalType }
     * 
     * 
     */
    public List<InfoAdicionalType> getInfoAdicional() {
        if (infoAdicional == null) {
            infoAdicional = new ArrayList<InfoAdicionalType>();
        }
        return this.infoAdicional;
    }

    /**
     * Gets the value of the refID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefID() {
        return refID;
    }

    /**
     * Sets the value of the refID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefID(String value) {
        this.refID = value;
    }

}
