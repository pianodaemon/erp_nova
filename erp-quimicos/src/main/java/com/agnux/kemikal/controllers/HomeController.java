/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.common.obj.ResourceProject;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/home/")
public class HomeController {
    private static final Logger log  = Logger.getLogger(HomeController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public void setHomeDao(HomeInterfaceDao HomeDao) {
        this.HomeDao = HomeDao;
    }
    
    public ResourceProject getResource() {
        return resource;
    }
    
    public void setResource(ResourceProject resource) {
        this.resource = resource;
    }
    
    
    
    /*Variables para guardar valores DOF*/
    String title = "";
    String description = "";
    String valueDate = "";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }
    
    
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", HomeController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        String username = "";
        UserSessionData userdata = null;
        
        ModelAndView x = new ModelAndView("home/startup", "title", "Home ERP");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        x = x.addObject("url", resource.getUrl(request));
        
        
        //ModelAndView mav = new ModelAndView("user");
        
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        /*
         * 
         * EN ESTE BLOQUE DEBERAN DE IR LAS CONSULTAS AL DAO PARA OBTENER
         * USER ID, EMPRESA ID, EMPLEADO ID
         */ 
        HashMap<String, String> succes = new HashMap<String, String>();
        
        succes = this.getHomeDao().getUserByName(username);
        userdata = new UserSessionData(String.valueOf(succes.get("username")), Integer.parseInt(succes.get("id")), Integer.parseInt(succes.get("empresa_id")), String.valueOf(succes.get("empresa")),Integer.parseInt(succes.get("sucursal_id")), String.valueOf(succes.get("sucursal")));
        x.addObject("user", userdata);
       
       /* 
        if(user == null){
            
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                username = ((UserDetails)principal).getUsername();
            } else {
                username = principal.toString();
            }
            
            
         

            HashMap<String, String> succes = new HashMap<String, String>();
            
            succes = this.getHomeDao().getUserByName(username);
            user = new UserSessionData(String.valueOf(succes.get("username")), Integer.parseInt(succes.get("id")),Integer.parseInt(succes.get("empleado_id")),0);
            
            //UserSessionData(String login_name, int userId , int empleadoId, int empresaId) {
            session.setAttribute("user", user);
            
            user = (UserSessionData) session.getAttribute("user");
            System.out.println("userId: "+ user.getUserId());
            System.out.println("empleadoId: "+ user.getEmpleadoId());
            System.out.println("empresaId: "+ user.getEmpresaId());
            System.out.println("userName: "+ user.getUserName());
            
            
            x = x.addObject("username", user.getUserName());
            
        }
        */
       
        x = x.addObject("username", userdata.getUserName());
        x = x.addObject("empresa", userdata.getRazonSocialEmpresa());
        x = x.addObject("sucursal", userdata.getSucursal());
        
        /*Para actualizar el tipo de cambio*/
        this.tipoCambioServiceMethod(Integer.parseInt(succes.get("id")));
        
        //String userId = String.valueOf(userdata.getUserId());
        //System.out.println("id_de_usuario: "+userId);
        
        //String codificado = Base64Coder.encodeString(userId);
        //System.out.println("codificado: "+codificado);
        
        //String decodificado = Base64Coder.decodeString(codificado);
        //System.out.println("decodificado: "+decodificado);
        
        
        return x;
    }
    
    public void tipoCambioServiceMethod(Integer user_id){
        Date date = new Date();
        
        getXmlV2("http://dof.gob.mx/indicadores.xml");
        
        //121--Proceso Actualizador Tipo de cambion
        String data_string = "121___new___"+user_id+"___0___"+this.getDescription()+"___dolar%";
        String extra_data_array = "'sin datos'";
        
        //System.out.println("antes de jdbc data_string:"+data_string+"     extra_data_array:"+extra_data_array);
        String retorno = this.jobTiposMoneda(data_string, extra_data_array);
        /*
        if(retorno.equals("1")){
            System.out.println("despues de jdbc data_string:"+data_string+"     extra_data_array:"+extra_data_array);
        }else{
            System.out.println("despues de jdbc data_string:"+data_string+"     extra_data_array:"+extra_data_array);
        }
        */
    }
    
    public void getXmlV2(String urlString){
        try {
            
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(conn.getInputStream());
            
            NodeList ndl = doc.getElementsByTagName("item");
            
            for(int i=0; i < ndl.getLength(); i++ ){
                NodeList nodl = (NodeList) ndl.item(i);
                for(int j=0; j < nodl.getLength(); j++ ){
                    Node nditem = nodl.item(j);
                    if (nditem.getNodeName().equals("title") ){
                        setTitle(nditem.getTextContent());
                    }
                    if(title.equals("DOLAR")){
                        if (nditem.getNodeName().equals("description") ){
                            setDescription(nditem.getTextContent());
                        }
                        if (nditem.getNodeName().equals("valueDate") ){
                            setValueDate(nditem.getTextContent());
                        }
                    }
                }
            }
           
            //System.out.println("title:"+title+" description:"+description+" valueDate:"+valueDate);
            /*
            TransformerFactory factory1 = TransformerFactory.newInstance();
            Transformer xform = factory1.newTransformer();
            
            // that’s the default xform; use a stylesheet to get a real one
            xform.transform(new DOMSource(doc), new StreamResult(System.out));
            */
            
        }
        catch (ParserConfigurationException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }        catch (SAXException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }        catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String jobTiposMoneda(String data_string, String extra_data_array){
        
        String actualizo = "0";
        if(!this.getDescription().equals(null) && !this.getDescription().equals("")){
            actualizo = this.getGralDao().selectFunctionForThisApp(data_string, extra_data_array);
        }
        //actualizo = this.getGralDao().selectFunctionForThisApp(data_string, extra_data_array);
        
        return actualizo;
    }
    
    

    
    
    

}
