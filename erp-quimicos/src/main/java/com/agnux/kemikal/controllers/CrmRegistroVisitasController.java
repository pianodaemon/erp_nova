/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CrmInterfaceDao;
import com.agnux.kemikal.interfacedaos.CxcInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 *
 * @author Noé Martínez
 * gpmarsan@gmail.com
 * 14/01/2013
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/crmregistrovisitas/")
public class CrmRegistroVisitasController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CrmRegistroVisitasController.class.getName());
    
    @Autowired
    @Qualifier("daoCrm")
    private CrmInterfaceDao CrmlDao;
    
    @Autowired
    @Qualifier("daoHome")   //permite controlar usuarios que entren
    private HomeInterfaceDao HomeDao;
    
    public CrmInterfaceDao getCrmDao() {
        return CrmlDao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CrmRegistroVisitasController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("folio", "Folio:100");
        infoConstruccionTabla.put("agente", "Nombre del Agente:250");
        infoConstruccionTabla.put("motivo", "Motivo:150");
        infoConstruccionTabla.put("calif", "Calificaci&oacute;n:120");
        infoConstruccionTabla.put("tipo_seg", "Tipo de Seguimiento:150");
        
        ModelAndView x = new ModelAndView("crmregistrovisitas/startup", "title", "Registro de Visitas");
        
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
    
    
    
    //obtener todos los registros de Visitas del Agente
    @RequestMapping(value="/getAllRegistroVisitas.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllRegistroVisitasJson(
           @RequestParam(value="orderby", required=true) String orderby,
           @RequestParam(value="desc", required=true) String desc,
           @RequestParam(value="items_por_pag", required=true) int items_por_pag,
           @RequestParam(value="pag_start", required=true) int pag_start,
           @RequestParam(value="display_pag", required=true) String display_pag,
           @RequestParam(value="input_json", required=true) String input_json,
           @RequestParam(value="cadena_busqueda", required=true) String cadena_busqueda,
           @RequestParam(value="iu", required=true) String id_user_cod,
       Model modcel) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String,String> has_busqueda = StringHelper.convert2hash(StringHelper.ascii2string(cadena_busqueda));
        
        //Aplicativo de Registro de Visitas
        Integer app_selected = 115;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String tipo_visita = String.valueOf(has_busqueda.get("tipo_visita"));
        String contacto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("contacto")))+"%";
        String agente = String.valueOf(has_busqueda.get("agente"));
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+tipo_visita+"___"+contacto+"___"+agente+"___"+fecha_inicial+"___"+fecha_final;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCrmDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCrmDao().getCrmRegistroVisitas_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getRegistroVisita.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getRegistroVisitaJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getclientsdfJson de {0}", ClientsDirFiscalesController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> agentes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> motivos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> calificaciones = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> seguimientos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_agente = Integer.parseInt(userDat.get("empleado_id"));
        
        if( id != 0  ){
            datos = this.getCrmDao().getCrmRegistroVisitas_Datos(id);
        }
        
        agentes = this.getCrmDao().getAgentes(id_empresa);
        motivos = this.getCrmDao().getCrmRegistroVisitas_Motivos(id_empresa);
        calificaciones = this.getCrmDao().getCrmRegistroVisitas_Calificaciones(id_empresa);
        seguimientos = this.getCrmDao().getCrmRegistroVisitas_Seguimientos(id_empresa);
        
        extra = this.getCrmDao().getUserRol(id_usuario);
        extra.put("id_agente", String.valueOf(id_agente));
        arrayExtra.add(0,extra);
        
        jsonretorno.put("Datos", datos);
        jsonretorno.put("Extra", arrayExtra);
        jsonretorno.put("Agentes", agentes);
        jsonretorno.put("Motivos", motivos);
        jsonretorno.put("Calificaciones", calificaciones);
        jsonretorno.put("Seguimientos", seguimientos);
        
        return jsonretorno;
    }
    
    
    
    
    /*
    public void genera_pdf(Integer id_empresa) {
        HashMap<String,String> dataFacturaCliente = new HashMap<String,String>();
        Integer id_prefactura=0;
        Integer id_emp=0;
        Integer id_suc=0;
        //obtiene serie_folio de la factura que se acaba de guardar
        String serieFolio = "P38";
        
        String cadena_original=this.getBfCfdiTf().getCadenaOriginal();
        
        String sello_digital = this.getBfCfdiTf().getSelloDigital();
        //System.out.println("sello_digital:"+sello_digital);
        
        //este es el timbre fiscal, se debe extraer del xml que nos devuelve el web service del timbrado
        String sello_digital_sat = this.getBfCfdiTf().getSelloDigitalSat();
        
        //conceptos para el pdfcfd
        listaConceptosPdfCfd = this.getFacdao().getListaConceptosPdfCfd(serieFolio);
        
        //datos para el pdf
        datosExtrasPdfCfd = this.getFacdao().getDatosExtrasPdfCfd( serieFolio, proposito, cadena_original,sello_digital, id_sucursal);
        datosExtrasPdfCfd.put("tipo_facturacion", tipo_facturacion);
        datosExtrasPdfCfd.put("sello_sat", sello_digital_sat);
        
        //pdf factura
        pdfCfd pdfFactura = new pdfCfd(this.getGralDao(), dataFacturaCliente, listaConceptosPdfCfd, datosExtrasPdfCfd, id_empresa, id_sucursal);
                      
    }
    */
    
    
    
    
}
