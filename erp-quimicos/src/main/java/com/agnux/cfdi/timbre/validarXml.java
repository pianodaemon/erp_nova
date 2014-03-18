/*
 * Ésta clase es para validar xml contra un esquema xsd.
 * Recibe la ruta completa del fichero xml y la ruta completa del fichero xsd.
 */

package com.agnux.cfdi.timbre;
import java.io.File;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Noé Martínez
 * gpmarsan@gmail.com
 * 12/12/2012
 */

public class validarXml {
    private String path_file_xml;
    private String path_file_schema;
    
    public validarXml(String path_file_xml, String path_file_schema) {
        this.setPath_file_schema(path_file_schema);
        this.setPath_file_xml(path_file_xml);
    }
    
    public String validar() {
        String retorno="true";
        try{
            // crear y configurar la factory de parsers de documentos XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);  // activar soporte para namespaces
            
            // cargar el documento XML
            DocumentBuilder parser = dbf.newDocumentBuilder();
            Document doc = parser.parse(new File(this.getPath_file_xml()));
            
            // crear una SchemaFactory preparada para interpretar esquemas XML W3C
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            
            // cargar el esquema XSD
            Schema schema = sf.newSchema(new File(this.getPath_file_schema()));
            
            // crear el objeto validator, que será el responsable de validar el XML
            Validator validator = schema.newValidator();
            
            // validar el documento XML
            validator.validate(new DOMSource(doc));
            
            // si se llega a este punto, el documento es válido
            System.out.println("DOCUMENTO VÁLIDO");
            
        }catch (SAXException e){
            
           if( e.getMessage().contains("'nomina:Nomina'")){
               //Si el error contiene el elemento 'nomina:Nomina', entonces lo dejamos pasar
               retorno="true";
           }else{
                //Esta excepción indica fallo de validación
                retorno = "DOCUMENTO INVÁLIDO: "+ e.getMessage();
           }
            //e.printStackTrace();
        }catch (ParserConfigurationException e){
            // errores en la configuración del parser
            retorno="Errores en la configuracion del parser.";
            return retorno;
            //e.printStackTrace();
        }catch (IOException e){
            // errores de lectura
            //e.printStackTrace();
            retorno="Errores de lectura.";
            return retorno;
        }
        
        return retorno;
    }
    
    public String getPath_file_schema() {
        return path_file_schema;
    }

    public void setPath_file_schema(String path_file_schema) {
        this.path_file_schema = path_file_schema;
    }

    public String getPath_file_xml() {
        return path_file_xml;
    }

    public void setPath_file_xml(String path_file_xml) {
        this.path_file_xml = path_file_xml;
    }
}
