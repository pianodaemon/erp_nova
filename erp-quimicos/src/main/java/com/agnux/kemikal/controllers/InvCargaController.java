/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import com.agnux.kemikal.reportes.InvListaProductosXls;
import com.itextpdf.text.DocumentException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Noe Martinez
 * gpmarsan@gmail.com
 * 08/julio/2014
 * 
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/invcarga/")
public class InvCargaController {
    private static final Logger log  = Logger.getLogger(InvCargaController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoInv")
    private InvInterfaceDao invDao;
    
    public InvInterfaceDao getInvDao() {
        return invDao;
    }
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvCargaController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        
        ModelAndView x = new ModelAndView("invcarga/startup", "title", "Carga de inventario f&iacute;sico");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("sucursal", user.getSucursal());
        
        String userId = String.valueOf(user.getUserId());
        
        String codificado = Base64Coder.encodeString(userId);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getCargar.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getCargarJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ){
        
        log.log(Level.INFO, "Ejecutando getCargarJson de {0}", InvCargaController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> almacenes = new ArrayList<HashMap<String, String>>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        almacenes = this.getInvDao().getAlmacenes(id_empresa);
        
        
        jsonretorno.put("Alms", almacenes);
        
        return jsonretorno;
    }
    
    
    
    
    
    
    //Genera Reporte de Dicas Promedio de entrega de OC
    @RequestMapping(value = "/getFormato/{cadena}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getReporteJson(
                @PathVariable("cadena") String almacen,
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request,
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        String tituloReporte="Descargar fomato en excel";
        System.out.println(tituloReporte);
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String rfcEmpresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
       
        //obtener el directorio temporal
        //String dir_tmp = System.getProperty("java.io.tmpdir");
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        File file_dir_tmp = new File(dir_tmp);
        //System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        String file_name = rfcEmpresa+"_Productos"+TimeHelper.getFechaActualYMDHMS()+".xls";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        ArrayList<HashMap<String, String>> lista_productos = new ArrayList<HashMap<String, String>>();
        
        
        String periodo="";
        int app_selected=178;
        
        String data_string = app_selected+"___"+id_usuario+"___"+id_empresa+"___"+almacen;
        
        lista_productos = this.getInvDao().selectFunctionForInvReporte(app_selected,data_string);
        
        InvListaProductosXls excel = new InvListaProductosXls(fileout,lista_productos);
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/xls");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());  	
        response.flushBuffer();
        
        FileHelper.delete(fileout);
        
        return null;
    }
    
    
    
    
    
    
    //para subir el archivo a la carpeta temporal de java
    @RequestMapping(method = RequestMethod.POST, value="/fileUpload.json")
    public @ResponseBody String fileUploadJson(
            @RequestParam(value="file", required=true) MultipartFile upload,
            Model model, Exception exception
        ) {
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        String retorno="";
            if (!upload.isEmpty()) {
                try {
                    byte[] bytes = upload.getBytes();

                    System.out.println("FileHelper: "+this.getGralDao().getTmpDir()+upload.getOriginalFilename());
                    String urlSave = FileHelper.saveByteFile(bytes, this.getGralDao().getTmpDir()+upload.getOriginalFilename());

                    String ul_img = upload.getOriginalFilename();

                    //System.out.println("getTmpDir:"+this.getGralDao().getTmpDir());

                    System.err.println("urlSave: "+urlSave);


                    jsonretorno.put("url",ul_img);
                    jsonretorno.put("success","true");
                    retorno="true";
                } catch (IOException ex) {
                    System.out.println("errorMessage2: "+ex.getMessage());
                    retorno="false";
                }

            } else {
                log.log(Level.INFO, "Test upload {0}", "uploadFailure");
                jsonretorno.put("url","no");
                jsonretorno.put("success","false");
                retorno="false";
            }
        
        return retorno;
    
    }
    
    
}
