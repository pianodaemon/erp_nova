
package com.buzonfiscal.ns.xsd.bf.requesttimbracfdi;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.buzonfiscal.ns.xsd.bf.requesttimbracfdi package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _InfoAdicional_QNAME = new QName("http://www.buzonfiscal.com/ns/xsd/bf/RequestTimbraCFDI", "InfoAdicional");
    private final static QName _Documento_QNAME = new QName("http://www.buzonfiscal.com/ns/xsd/bf/RequestTimbraCFDI", "Documento");
    private final static QName _InfoBasica_QNAME = new QName("http://www.buzonfiscal.com/ns/xsd/bf/RequestTimbraCFDI", "InfoBasica");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.buzonfiscal.ns.xsd.bf.requesttimbracfdi
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DocumentoType }
     * 
     */
    public DocumentoType createDocumentoType() {
        return new DocumentoType();
    }

    /**
     * Create an instance of {@link InfoBasicaType }
     * 
     */
    public InfoBasicaType createInfoBasicaType() {
        return new InfoBasicaType();
    }

    /**
     * Create an instance of {@link InfoAdicionalType }
     * 
     */
    public InfoAdicionalType createInfoAdicionalType() {
        return new InfoAdicionalType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoAdicionalType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.buzonfiscal.com/ns/xsd/bf/RequestTimbraCFDI", name = "InfoAdicional")
    public JAXBElement<InfoAdicionalType> createInfoAdicional(InfoAdicionalType value) {
        return new JAXBElement<InfoAdicionalType>(_InfoAdicional_QNAME, InfoAdicionalType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocumentoType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.buzonfiscal.com/ns/xsd/bf/RequestTimbraCFDI", name = "Documento")
    public JAXBElement<DocumentoType> createDocumento(DocumentoType value) {
        return new JAXBElement<DocumentoType>(_Documento_QNAME, DocumentoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoBasicaType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.buzonfiscal.com/ns/xsd/bf/RequestTimbraCFDI", name = "InfoBasica")
    public JAXBElement<InfoBasicaType> createInfoBasica(InfoBasicaType value) {
        return new JAXBElement<InfoBasicaType>(_InfoBasica_QNAME, InfoBasicaType.class, null, value);
    }

}
