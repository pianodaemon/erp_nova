/*
package com.agnux.kemikal.controllers;
public class CrmPdfReporteVisitasController {

}*/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CrmInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.Pdf_CRM_registroVisitas;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * /**
 *
 * @author vale8490
 * lunes 4 de marzo del 2013
 * Este controller es para generar el reporte de visitas
 */

@Controller
@SessionAttributes({"user"})
@RequestMapping("/crmreportevisitas/")

public class CrmPdfReporteVisitasController {
    private static final Logger log  = Logger.getLogger(CrmPdfReporteVisitasController.class.getName());
    ResourceProject resource = new ResourceProject();

    @Autowired
    @Qualifier("daoCrm")
    private CrmInterfaceDao CrmDao;

    public CrmInterfaceDao getCrmDao() {
        return CrmDao;
    }

    public void setCrmDao(CrmInterfaceDao CrmDao) {
        this.CrmDao = CrmDao;
    }


    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;

    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    private Integer agente_id;



    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }


    public GralInterfaceDao getGralDao() {
        return gralDao;
    }



    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {

        log.log(Level.INFO, "Ejecutando starUp de {0}", CrmPdfReporteVisitasController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();


        ModelAndView x = new ModelAndView("crmreportevisitas/startup", "title", "Reporte Visitas");

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

    @RequestMapping(method = RequestMethod.POST, value="/getVisitas.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getRegistroMetasJson(
            @RequestParam(value="fecha_inicial", required=true) String fecha_inicial,
            @RequestParam(value="fecha_final", required=true) String fecha_final,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {

        log.log(Level.INFO, "Ejecutando getRegistroMetasJson de {0}", CrmPdfReporteVisitasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();

        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();



        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

        datos = this.getCrmDao().getVisitas(fecha_inicial, fecha_final,id_empresa);



        jsonretorno.put("Datos", datos);



        return jsonretorno;
    }




     //PDF reporte
     @RequestMapping(value = "/Crear_PDF_reg_Visitas/{cadena}/{iu}/out.json", method = RequestMethod.GET )
     public ModelAndView Crear_PDF_reg_Visitas(
        @PathVariable("cadena") String cadena,
        @PathVariable("iu") String id_user,
        HttpServletRequest request,
        HttpServletResponse response,
        Model model)
     throws ServletException, IOException, URISyntaxException, DocumentException {

        String[] filtros = cadena.split("___");

        String fecha_inicial = filtros[0];
        String fecha_final = filtros[1];


        HashMap<String, String> userDat = new HashMap<String, String>();
        System.out.println("Generando Reporte de Visitas");
        String dir_tmp = this.getGralDao().getTmpDir();
        File file_dir_tmp = new File(dir_tmp);
        String file_name = "RepVistas del "+fecha_inicial+"al"+fecha_final+".pdf";

        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;

        ArrayList<HashMap<String, String>> reg_visitas = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datosEncabezadoPie = new HashMap<String, String>();
        HashMap<String, String> datos= new HashMap<String, String>();

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //String rfc_empresa=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        Integer app_selected=128;
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);



        String titulo_reporte ="Reporte de Visitas";
        datosEncabezadoPie.put("nombre_empresa_emisora", razon_social_empresa);
        datosEncabezadoPie.put("titulo_reporte", titulo_reporte);

        datosEncabezadoPie.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        datosEncabezadoPie.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        //datosEncabezadoPie.put("codigo1", "");
        //datosEncabezadoPie.put("codigo2","");
        datos.put("fecha_inicial",fecha_inicial);
        datos.put("fecha_final",fecha_final);

        //obtiene los depositos del periodo indicado
//        String codigo="";
//        String descripcion="";
//        Integer idcliente = Integer.parseInt(cliente);
       //(HashMap<String, String> datosEncabezadoPie, String fileout, ArrayList<HashMap<String, String>> lista_CobranzaDiaria, HashMap<String, String> datos)
        reg_visitas =this.getCrmDao().getVisitas(fecha_inicial,fecha_final, id_empresa);
                //this.getPedidDao().getReportePedidos(arreglo[0],arreglo[1], arreglo[2], arreglo[3], arreglo[4],id_empresa);
       // pedidos = this.getPedidDao().getReportePedidos(opcion,agente, cliente, fecha_inicial, fecha_final,id_empresa);
        //getPedDaoDao().getCobranzaDiaria( arreglo[1], arreglo[2],idcliente,  id_empresa);

        //instancia a la clase que construye el pdf de Cobranza Diaria
        Pdf_CRM_registroVisitas x = new Pdf_CRM_registroVisitas(datosEncabezadoPie, fileout,reg_visitas,datos);

        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getCanonicalPath() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());
        response.flushBuffer();

        return null;

    }

}
