/**
 *
 * @author Noé Martínez
 * gpmarsan@gmail.com
 * 06/diciembre/2012
 * 
 * Ésta clase genera el xml sin Timbre Fiscal
 * 
 */

package com.agnux.cfdi.timbre;

import java.io.StringWriter;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang.StringEscapeUtils;

public class CfdiXmlBuilder {
	private DocumentBuilderFactory dbf;
	private Document doc;
	private DocumentBuilder db;
	private DOMImplementation domImpl;
        
	public void init() throws Exception{
            this.setDbf(DocumentBuilderFactory.newInstance());
            this.setDb(this.getDbf().newDocumentBuilder());
            this.setDomImpl(this.getDb().getDOMImplementation());
	}
        
	public void construyeNodoFactura(String tipoDeComprobante,String condicionesDePago,String formaDePago,String fecha,String subTotal,String total, String moneda, String tipo_cambio, String no_certificado_emisor,String certificado, String metodoDePago, String LugarExpedicion, String numTarjeta) {
            //Document tmp = this.getDomImpl().createDocument("http://www.sat.gob.mx/cfd/2", "Comprobante", null);
            Document tmp = this.getDomImpl().createDocument("", "cfdi:Comprobante", null);
            tmp.getDocumentElement().setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
            tmp.getDocumentElement().setAttribute("xsi:schemaLocation","http://www.sat.gob.mx/cfd/3 http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv32.xsd http://www.sat.gob.mx/detallista http://www.sat.gob.mx/sitio_internet/cfd/detallista/detallista.xsd http://www.sat.gob.mx/ecc http://www.sat.gob.mx/sitio_internet/cfd/ecc/ecc.xsd http://www.sat.gob.mx/terceros http://www.sat.gob.mx/sitio_internet/cfd/terceros/terceros11.xsd http://www.sat.gob.mx/implocal http://www.sat.gob.mx/sitio_internet/cfd/implocal/implocal.xsd http://www.sat.gob.mx/ecb http://www.sat.gob.mx/sitio_internet/cfd/ecb/ecb.xsd http://www.buzonfiscal.com/ns/addenda/bf/2 http://www.buzonfiscal.com/schema/xsd/Addenda_BF_v2.2.xsd http://www.sat.gob.mx/TimbreFiscalDigital http://www.sat.gob.mx/sitio_internet/TimbreFiscalDigital/TimbreFiscalDigital.xsd http://www.sat.gob.mx/iedu http://www.sat.gob.mx/sitio_internet/cfd/iedu/iedu.xsd http://www.sat.gob.mx/ventavehiculos http://www.sat.gob.mx/sitio_internet/cfd/ventavehiculos/ventavehiculos.xsd http://http://www.sat.gob.mx/TuristaPasajeroExtranjero http://www.sat.gob.mx/sitio_internet/cfd/TuristaPasajeroExtranjero/TuristaPasajeroExtranjero.xsd http://www.sat.gob.mx/pfic http://www.sat.gob.mx/sitio_internet/cfd/pfic/pfic.xsd http://www.sat.gob.mx/leyendasFiscales http://www.sat.gob.mx/sitio_internet/cfd/leyendasFiscales/leyendasFisc.xsd http://www.sat.gob.mx/donat http://www.sat.gob.mx/sitio_internet/cfd/donat/donat11.xsd http://www.sat.gob.mx/divisas http://www.sat.gob.mx/sitio_internet/cfd/divisas/Divisas.xsd http://www.buzonfiscal.com/ns/addenda/bf/3 http://www.buzonfiscal.com/schema/xsd/Addenda_BF_v3.xsd");
            tmp.getDocumentElement().setAttribute("xmlns:ecb","http://www.sat.gob.mx/ecb");
            tmp.getDocumentElement().setAttribute("xmlns:implocal","http://www.sat.gob.mx/implocal");
            tmp.getDocumentElement().setAttribute("xmlns:bfa2","http://www.buzonfiscal.com/ns/addenda/bf/2");
            tmp.getDocumentElement().setAttribute("xmlns:detallista","http://www.sat.gob.mx/detallista");
            tmp.getDocumentElement().setAttribute("xmlns:leyendasFisc","http://www.sat.gob.mx/leyendasFiscales");
            tmp.getDocumentElement().setAttribute("xmlns:tfd","http://www.sat.gob.mx/TimbreFiscalDigital");
            tmp.getDocumentElement().setAttribute("xmlns:pfic","http://www.sat.gob.mx/pfic");
            tmp.getDocumentElement().setAttribute("xmlns:ventavehiculos","http://www.sat.gob.mx/ventavehiculos");
            tmp.getDocumentElement().setAttribute("xmlns:iedu","http://www.sat.gob.mx/iedu");
            tmp.getDocumentElement().setAttribute("xmlns:donat","http://www.sat.gob.mx/donat");
            tmp.getDocumentElement().setAttribute("xmlns:terceros","http://www.sat.gob.mx/terceros");
            tmp.getDocumentElement().setAttribute("xmlns:divisas","http://www.sat.gob.mx/divisas");
            tmp.getDocumentElement().setAttribute("xmlns:tpe","http://www.sat.gob.mx/TuristaPasajeroExtranjero");
            tmp.getDocumentElement().setAttribute("xmlns:cfdi","http://www.sat.gob.mx/cfd/3");
            tmp.getDocumentElement().setAttribute("xmlns:ecc","http://www.sat.gob.mx/ecc");
            tmp.getDocumentElement().setAttribute("version","3.2");
            tmp.getDocumentElement().setAttribute("certificado",certificado);
            tmp.getDocumentElement().setAttribute("noCertificado",no_certificado_emisor);
            tmp.getDocumentElement().setAttribute("sello","@SELLO_DIGITAL");
            tmp.getDocumentElement().setAttribute("tipoDeComprobante",tipoDeComprobante);
            tmp.getDocumentElement().setAttribute("serie","@SERIE");
            tmp.getDocumentElement().setAttribute("folio","@FOLIO");
            tmp.getDocumentElement().setAttribute("LugarExpedicion",StringEscapeUtils.escapeHtml(LugarExpedicion));
            tmp.getDocumentElement().setAttribute("fecha",fecha);
            //tmp.getDocumentElement().setAttribute("formaDePago","PAGO EN UNA SOLA EXHIBICION");
            tmp.getDocumentElement().setAttribute("formaDePago",StringEscapeUtils.escapeHtml(formaDePago));
            tmp.getDocumentElement().setAttribute("metodoDePago",StringEscapeUtils.escapeHtml(metodoDePago));
            tmp.getDocumentElement().setAttribute("subTotal",subTotal);
            tmp.getDocumentElement().setAttribute("total",total);
            tmp.getDocumentElement().setAttribute("Moneda",moneda);
            tmp.getDocumentElement().setAttribute("TipoCambio",tipo_cambio);
            
            if(!numTarjeta.equals("")){
                tmp.getDocumentElement().setAttribute("NumCtaPago",numTarjeta);
            }
            
            this.setDoc(tmp);
	}
        
	public void configurarNodoEmisor(String razon_social_emisor,String rfc_emisor,String pais,String estado,String municipio, String colonia,String calle,String no_exterior,String codigo_postal, String regimenfiscal){
            Document tmp = this.getDoc();
            Element element = tmp.createElement("cfdi:Emisor");
            element.setAttribute("nombre", razon_social_emisor );
            element.setAttribute("rfc", rfc_emisor );
            
            Element extra = this.getDoc().createElement("cfdi:DomicilioFiscal");
            extra.setAttribute("codigoPostal",codigo_postal);
            extra.setAttribute("pais",pais);
            extra.setAttribute("estado",estado);
            extra.setAttribute("municipio",municipio);
            extra.setAttribute("calle",calle);
            extra.setAttribute("colonia",colonia);
            extra.setAttribute("noExterior",no_exterior);
            element.appendChild(extra);
            
            Element extra2 = this.getDoc().createElement("cfdi:RegimenFiscal");
            extra2.setAttribute("Regimen",regimenfiscal);
            element.appendChild(extra2);
            
            tmp.getDocumentElement().appendChild(element);
            this.setDoc(tmp);
	}
        
	public void configurarNodoReceptor(String nombre,String rfc,String pais, String noExterior, String calle, String colonia, String municipio, String estado, String codigoPostal ){
            Document tmp = this.getDoc();
            Element element = tmp.createElement("cfdi:Receptor");
            element.setAttribute("nombre", StringEscapeUtils.escapeHtml(nombre) );
            element.setAttribute("rfc", rfc );
            
            Element extra = this.getDoc().createElement("cfdi:Domicilio");
            extra.setAttribute("codigoPostal",codigoPostal);
            extra.setAttribute("estado",StringEscapeUtils.escapeHtml(estado));
            extra.setAttribute("municipio",StringEscapeUtils.escapeHtml(municipio));
            extra.setAttribute("colonia",StringEscapeUtils.escapeHtml(colonia));
            extra.setAttribute("noExterior",noExterior);
            extra.setAttribute("pais",StringEscapeUtils.escapeHtml(pais));
            extra.setAttribute("calle",StringEscapeUtils.escapeHtml(calle));
            element.appendChild(extra);
            
            tmp.getDocumentElement().appendChild(element);
            this.setDoc(tmp);
	}
        
        
	public void configurarNodoConceptos(ArrayList<LinkedHashMap<String,String>> lista_de_conceptos) throws Exception {
		Document tmp = this.getDoc();
		Element element = tmp.createElement("Conceptos");
		if (lista_de_conceptos.size() > 0){
			for( LinkedHashMap<String,String> i : lista_de_conceptos ){
				Element c = this.getDoc().createElement("Concepto");
				@SuppressWarnings("rawtypes")
				Iterator it = i.entrySet().iterator();
				while (it.hasNext()) {
					@SuppressWarnings("rawtypes")
					Map.Entry elemento_hash = (Map.Entry)it.next();
					String llave = (String)elemento_hash.getKey();
					String valor = (String)elemento_hash.getValue();
                                        
                                        if (llave.equals("noIdentificacion")){ c.setAttribute(llave , valor); }
                                        if (llave.equals("descripcion")){ c.setAttribute(llave , StringEscapeUtils.escapeHtml(valor)); }
                                        if (llave.equals("unidad")){ c.setAttribute(llave , valor); }
                                        if (llave.equals("cantidad")){ c.setAttribute(llave , valor);  }
                                        if (llave.equals("valorUnitario")){ c.setAttribute(llave , valor); }
                                        if (llave.equals("importe")){ c.setAttribute(llave , valor); }
                                        
					if (llave.equals("infoAduana")){
						Element extra = this.getDoc().createElement("InformacionAduanera");
						extra.setAttribute("numero", valor.split("|")[0]);
						extra.setAttribute("fecha", valor.split("|")[1]);
						extra.setAttribute("aduana", valor.split("|")[2]);
						c.appendChild(extra);
					}
				}
				element.appendChild(c);
			}
			tmp.getDocumentElement().appendChild(element);
		}
		this.setDoc(tmp);
	}
        
        public void configurarImpuestos(ArrayList<LinkedHashMap<String,String>> lista_de_retenidos,ArrayList<LinkedHashMap<String,String>> lista_de_traslados){
	//public void configurarImpuestos(String trans, String ret, String impuesto, String tasa){
		Document tmp = this.getDoc();
		Element root = tmp.createElement("Impuestos");
		root.setAttribute("totalImpuestosRetenidos", "@SUMIMPUESTOS_RETENIDOS");
		root.setAttribute("totalImpuestosTrasladados", "@SUMIMPUESTOS_TRASLADADOS" );
                
		Element child_1 = tmp.createElement("Retenciones");
                if (lista_de_retenidos.size() > 0){
                    for( LinkedHashMap<String,String> i : lista_de_retenidos ){
                        Element child_1_1 = tmp.createElement("Retencion");
                        
                        @SuppressWarnings("rawtypes")
			Iterator it = i.entrySet().iterator();
			while (it.hasNext()) {
                            @SuppressWarnings("rawtypes")
                            Map.Entry elemento_hash = (Map.Entry)it.next();
                            String llave = (String)elemento_hash.getKey();
                            String valor = (String)elemento_hash.getValue();
                            if (llave.equals("importe")){ child_1_1.setAttribute(llave , valor);  }
                            if (llave.equals("impuesto")){ child_1_1.setAttribute(llave , valor); }
                        }
                        
                        child_1.appendChild(child_1_1);
                    }
                }
                root.appendChild(child_1);
                
                
		Element child_2 = tmp.createElement("Traslados");
                if (lista_de_traslados.size() > 0){
                    for( LinkedHashMap<String,String> i : lista_de_traslados ){
                        Element child_2_1 = tmp.createElement("Traslado");
                        
                        @SuppressWarnings("rawtypes")
                        Iterator it = i.entrySet().iterator();
			while (it.hasNext()) {
                            Map.Entry elemento_hash = (Map.Entry)it.next();
                            String llave = (String)elemento_hash.getKey();
                            String valor = (String)elemento_hash.getValue();
                            if (llave.equals("importe")){ child_2_1.setAttribute(llave , valor);  }
                            if (llave.equals("impuesto")){ child_2_1.setAttribute(llave , valor); }
                            if (llave.equals("tasa")){ child_2_1.setAttribute(llave , valor); }
                        }
                        
                        child_2.appendChild(child_2_1);
                    }
                }
		root.appendChild(child_2);
                
		tmp.getDocumentElement().appendChild(root);
		this.setDoc(tmp);
	}
        
        
	public DocumentBuilder getDb() {
		return db;
	}
        
	public void setDb(DocumentBuilder db) {
		this.db = db;
	}
        
	public DocumentBuilderFactory getDbf() {
		return dbf;
	}
        
	public void setDbf(DocumentBuilderFactory dbf) {
		this.dbf = dbf;
	}
        
	public Document getDoc() {
		return doc;
	}
        
	public void setDoc(Document doc) {
		this.doc = doc;
	}
        
	public DOMImplementation getDomImpl() {
		return domImpl;
	}
        
	public void setDomImpl(DOMImplementation domImpl) {
		this.domImpl = domImpl;
	}
        
        public String getOutXmlString() throws TransformerConfigurationException, TransformerException{
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            //initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(this.getDoc());
            transformer.transform(source, result);
            
            return result.getWriter().toString().replace("standalone=\"no\"", " ");
        }
}
