/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.common.obj.ResourceProject;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.SessionAttributes;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/home/")
public class HomeController {
    private static final Logger log  = Logger.getLogger(HomeController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
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
        
        //String userId = String.valueOf(userdata.getUserId());
        //System.out.println("id_de_usuario: "+userId);
        
        //String codificado = Base64Coder.encodeString(userId);
        //System.out.println("codificado: "+codificado);
        
        //String decodificado = Base64Coder.decodeString(codificado);
        //System.out.println("decodificado: "+decodificado);
        
        
        return x;
    }
    

    
    
    
    
    
    

    
    
    

}
