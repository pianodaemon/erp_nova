/*
 * Clase para Crear Adenda para Quimiproductos.
 * Aqui se reciben los datos necesarios, se crea el xml y se incrusta en el archivo xml antes del timrado.
 * 
 */
package com.agnux.cfdi.adendas;

import com.agnux.common.helpers.FileHelper;
import com.agnux.common.obj.AgnuxXmlObject;
import java.io.File;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Noe Martinez
 * gpmarsan@gmail.com
 * 22/octubre/2013
 * 
 */
public class AdendaFemsaQuimiproductos extends Adenda{
    
    @Override
    public void createAdenda(Integer noAdenda, LinkedHashMap<String, Object> dataAdenda, String dirXml, String fileNameXml) {
        String pathXmlCfdi = dirXml+"/"+fileNameXml;
        
        if (noAdenda==1){
            try {
                System.out.println(this.getClass().toString());
                
                //Objeto para contruir xml de la Adenda
                XmlBuilder xmlBuilder = new XmlBuilder(dataAdenda);
                //String outXmlString = xmlBuilder.getOutXmlString();
                //System.out.println("Adenda: "+outXmlString);
                
                //Objeto para agregar la Adenda al XML del CDFI
                AgregarAdendaXmlCfdi addAdenda = new AgregarAdendaXmlCfdi(pathXmlCfdi, xmlBuilder.getDoc());
                //Ejecutar metodo que agrega elemento al xml del CFDI
                addAdenda.agregarAdenda();
                //Obtener la cadena XML del CFDI con Adenda
                String xmlString = addAdenda.getXmlOutString();
                
                
                System.out.println("pathXmlCfdi: "+pathXmlCfdi+"\n\n");
                
                System.out.println("xmlString: "+xmlString);
                
                
                //Crear fichero
                File xml_cfdi= new File(pathXmlCfdi);
                
                //Verificar si ya existe
                if(xml_cfdi.exists()){
                    //Si ya existe un fichero con el mismo nombre hay que eliminarlo
                    FileHelper.delete(pathXmlCfdi);
                }
                
                //Crear el nuevo fichero del XML
                boolean fichero_xml_con_adenda_ok = FileHelper.createFileWithText(dirXml, fileNameXml, xmlString);
                
                if(fichero_xml_con_adenda_ok){
                    System.out.println("Fichero xml con adenda se creó con éxito.");
                }else{
                    System.out.println("Fichero xml con adenda NO se creó.");
                }
            } catch (Exception ex) {
                System.out.println("Error al agregar Adenda al CFDI.");
                Logger.getLogger(AdendaFemsaQuimiproductos.class.getName()).log(Level.SEVERE, null, ex);
            }

            
        }else{
            next.createAdenda(noAdenda, dataAdenda, dirXml, fileNameXml);
        }
    }
    
    
    private class XmlBuilder extends AgnuxXmlObject{
        public XmlBuilder(LinkedHashMap<String, Object> data){
            super();
            this.construyeNodoAdenda(data);
        }
        
        private void construyeNodoAdenda(LinkedHashMap<String, Object> data){
            String elemento = new String();
            Document tmp = this.getDb().newDocument();
            
            Element root = tmp.createElement("cfdi:Adenda");
            Element child_1 = tmp.createElement("Documento");
            Element child_1_1 = tmp.createElement("FacturaFemsa");
            
            elemento = "noVersAdd";
            Element noVersAdd = tmp.createElement(elemento);
            if(!data.get(elemento).equals("")){
                noVersAdd.setTextContent(elemento);
            }
            child_1_1.appendChild(noVersAdd);
            
            elemento = "claseDoc";
            Element claseDoc = tmp.createElement(elemento);
            if(!data.get(elemento).equals("")){
                claseDoc.setTextContent(elemento);
            }
            child_1_1.appendChild(claseDoc);
            
            
            elemento = "noSociedad";
            Element noSociedad = tmp.createElement(elemento);
            if(!data.get(elemento).equals("")){
                noSociedad.setTextContent(elemento);
            }
            child_1_1.appendChild(noSociedad);
            
            elemento = "noProveedor";
            Element noProveedor = tmp.createElement(elemento);
            if(!data.get(elemento).equals("")){
                noProveedor.setTextContent(elemento);
            }
            child_1_1.appendChild(noProveedor);
            
            elemento = "noPedido";
            Element noPedido = tmp.createElement(elemento);
            if(!data.get(elemento).equals("")){
                noPedido.setTextContent(elemento);
            }
            child_1_1.appendChild(noPedido);
            
            elemento = "moneda";
            Element moneda = tmp.createElement(elemento);
            if(!data.get(elemento).equals("")){
                moneda.setTextContent(elemento);
            }
            child_1_1.appendChild(moneda);
            
            elemento = "noEntrada";
            Element noEntrada = tmp.createElement(elemento);
            if(!data.get(elemento).equals("")){
                noEntrada.setTextContent(elemento);
            }
            child_1_1.appendChild(noEntrada);
            
            elemento = "noRemision";
            Element noRemision = tmp.createElement(elemento);
            if(!data.get(elemento).equals("")){
                noRemision.setTextContent(elemento);
            }
            child_1_1.appendChild(noRemision);
            
            elemento = "noSocio";
            Element noSocio = tmp.createElement(elemento);
            if(!data.get(elemento).equals("")){
                noSocio.setTextContent(elemento);
            }
            child_1_1.appendChild(noSocio);
            
            elemento = "centro";
            Element centro = tmp.createElement(elemento);
            if(!data.get(elemento).equals("")){
                centro.setTextContent(elemento);
            }
            child_1_1.appendChild(centro);
            
            elemento = "iniPerLiq";
            Element iniPerLiq = tmp.createElement(elemento);
            if(!data.get(elemento).equals("")){
                iniPerLiq.setTextContent(elemento);
            }
            child_1_1.appendChild(iniPerLiq);
            
            elemento = "finPerLiq";
            Element finPerLiq = tmp.createElement(elemento);
            if(!data.get(elemento).equals("")){
                finPerLiq.setTextContent(elemento);
            }
            child_1_1.appendChild(finPerLiq);
            
            elemento = "retencion1";
            Element retencion1 = tmp.createElement(elemento);
            if(!data.get(elemento).equals("")){
                retencion1.setTextContent(elemento);
            }
            child_1_1.appendChild(retencion1);
            
            elemento = "retencion2";
            Element retencion2 = tmp.createElement(elemento);
            if(!data.get(elemento).equals("")){
                retencion2.setTextContent(elemento);
            }
            child_1_1.appendChild(retencion2);
            
            elemento = "email";
            Element email = tmp.createElement(elemento);
            if(!data.get(elemento).equals("")){
                email.setTextContent(elemento);
            }
            child_1_1.appendChild(email);
            
            
            child_1.appendChild(child_1_1);
            root.appendChild(child_1);
            tmp.appendChild(root);
            
            this.setDoc(tmp);
        }
        
        
        
        
        
        public String getOutXmlString() throws TransformerConfigurationException, TransformerException{
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            
            //initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(this.getDoc());
            transformer.transform(source, result);
            
            return result.getWriter().toString().replace("standalone=\"no\"", " ");
        }
    }

    
    
    
}
