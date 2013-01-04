
package com.buzonfiscal.ns.xsd.bf.timbradocfd;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.buzonfiscal.ns.xsd.bf.timbradocfd package. 
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

    private final static QName _RequestTimbradoCFD_QNAME = new QName("http://www.buzonfiscal.com/ns/xsd/bf/TimbradoCFD", "RequestTimbradoCFD");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.buzonfiscal.ns.xsd.bf.timbradocfd
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RequestTimbradoCFDType }
     * 
     */
    public RequestTimbradoCFDType createRequestTimbradoCFDType() {
        return new RequestTimbradoCFDType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestTimbradoCFDType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.buzonfiscal.com/ns/xsd/bf/TimbradoCFD", name = "RequestTimbradoCFD")
    public JAXBElement<RequestTimbradoCFDType> createRequestTimbradoCFD(RequestTimbradoCFDType value) {
        return new JAXBElement<RequestTimbradoCFDType>(_RequestTimbradoCFD_QNAME, RequestTimbradoCFDType.class, null, value);
    }

}
