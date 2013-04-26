/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;
import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.EnvInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
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
import org.apache.commons.lang.StringUtils;
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
 *
 * @author Noe Martinez
 * gpmarsan@gmail.com
 * 09/abril/2013
 */

@Controller
@SessionAttributes({"user"})
@RequestMapping("/envproceso/")
public class EnvProcesoController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(EnvProcesoController.class.getName());
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao homeDao;
    
    //dao para Modulo de Envasado
    @Autowired
    @Qualifier("daoEnv")
    private EnvInterfaceDao envDao;

    public EnvInterfaceDao getEnvDao() {
        return envDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return homeDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
        )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", EnvProcesoController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("codigo", "C&oacute;digo:120");
        infoConstruccionTabla.put("descripcion", "Descripci&oacute;n:300");
        infoConstruccionTabla.put("unidad", "Unidad:150");
        infoConstruccionTabla.put("presentacion", "Presentaci&oacute;n:160");
        
        ModelAndView x = new ModelAndView("envproceso/startup", "title", "Envasado");
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
    
    
    /*De aqui poara adelante no me sirve para este catalogo*/
    @RequestMapping(value="/getAllEnvProceso.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllEnvProcesoJson(
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
        
        //Configuracion de Envasado(ENV)
        Integer app_selected = 137;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        String descripcion = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("descripcion")))+"%";
        String estatus = StringHelper.isNullString(String.valueOf(has_busqueda.get("estatus")));
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+codigo+"___"+descripcion+"___"+estatus;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getEnvDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getEnvDao().getEnvConf_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene datos Buscador principal
    @RequestMapping(method = RequestMethod.POST, value="/getDatosBuscadorPrincipal.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDatosBuscadorPrincipalJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> tiposProducto = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> estatus = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        tiposProducto = this.getEnvDao().getProductoTipos();
        
        estatus = this.getEnvDao().getEstatus();
        
        jsonretorno.put("ProdTipos", tiposProducto);
        jsonretorno.put("Estatus", estatus);
        
        return jsonretorno;
    }
    
    
    //Obtiene datos de Una COnfiguracion
    @RequestMapping(method = RequestMethod.POST, value="/getEnvProceso.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getConfJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getConfJson de {0}", EnvProcesoController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datosEnvConf = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> presentaciones = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> almacenes = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( id != 0  ){
            datosEnvConf = this.getEnvDao().getEnvConf_Datos(id);
            presentaciones=this.getEnvDao().getProductoPresentacionesON(Integer.parseInt(datosEnvConf.get(0).get("producto_id")));
        }
        
        jsonretorno.put("Datos", datosEnvConf);
        jsonretorno.put("Presentaciones", presentaciones);
        jsonretorno.put("Almacenes", this.getEnvDao().getAlmacenes(id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorOrdenProduccion.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorOrdenProduccionJson(
            @RequestParam(value="folio", required=true) String folio,
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getProductosJson de {0}", EnvProcesoController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> ordenes = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        ordenes = this.getEnvDao().getBuscadorOrdenProduccion(folio, sku, descripcion, id_empresa);
        
        jsonretorno.put("Ordenes", ordenes);
        
        return jsonretorno;
    }
    
    
    //obtiene los Datos de Un producto en especifico a partir del Codigo(SKU)
    @RequestMapping(method = RequestMethod.POST, value="/gatDatosProducto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> gatDatosProductoJson(
            @RequestParam(value="codigo", required=true) String codigo,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando gatDatosProductoJson de {0}", EnvProcesoController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> producto = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> presentaciones = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        producto = this.getEnvDao().getDataProductBySku(codigo, id_empresa);
        
        if(producto.size()>0){
            presentaciones=this.getEnvDao().getProductoPresentacionesON(Integer.parseInt(producto.get(0).get("id")));
        }
        
        jsonretorno.put("Producto", producto);
        jsonretorno.put("Presentaciones", presentaciones);
        
        return jsonretorno;
    }
    
    
    //obtiene las presentaciones de un producto en especifico a partir de Id de Producto
    @RequestMapping(method = RequestMethod.POST, value="/getPresentacionesProducto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getPresentacionesProductoJson(
            @RequestParam(value="id_prod", required=true) Integer id_prod,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> presentaciones = new ArrayList<HashMap<String, String>>();
        
        presentaciones=this.getEnvDao().getProductoPresentacionesON(id_prod);
        jsonretorno.put("Presentaciones", presentaciones);
        
        return jsonretorno;
    }
    
    //obtiene las presentaciones que han sido confioguradas de un producto en especifico a partir de Id de Producto
    @RequestMapping(method = RequestMethod.POST, value="/getPresentacionesConfProducto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getPresentacionesConfProductoJson(
            @RequestParam(value="id_prod", required=true) Integer id_prod,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> presentaciones = new ArrayList<HashMap<String, String>>();
        
        presentaciones=this.getEnvDao().getEnvasesPorProducto(id_prod);
        jsonretorno.put("PresentacionEnvases", presentaciones);
        
        return jsonretorno;
    }
    
    
    //obtiene datos para el autopletar operario
    @RequestMapping(value="/getAutocompleteOperarios.json", method = RequestMethod.POST)
    public @ResponseBody ArrayList<HashMap<String, String>> getProOrdenOperariosJson(
           @RequestParam(value="cadena", required=true) String cadena,
           @RequestParam(value="iu", required=true) String id_user_cod,
           Model modcel) {
        
        ArrayList<HashMap<String, String>> arrayOperarios = new ArrayList<HashMap<String, String>>();
        //HashMap<String, String> cadenaLineas = new HashMap<String, String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //decodificar id de usuario
        //Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        cadena = "%"+cadena+"%";
        arrayOperarios=this.getEnvDao().getProOrdenOperariosDisponibles(cadena, id_empresa);
        
        return arrayOperarios;
    }
    
    
    //obtiene datos para el autopletar equipo
    @RequestMapping(value="/getAutocompleteEquipo.json", method = RequestMethod.POST)
    public @ResponseBody ArrayList<HashMap<String, String>> getProOrdenEquiposJson(
           @RequestParam(value="cadena", required=true) String cadena,
           @RequestParam(value="iu", required=true) String id_user_cod,
           Model modcel) {
        
        ArrayList<HashMap<String, String>> arrayEquipo = new ArrayList<HashMap<String, String>>();
        //HashMap<String, String> cadenaLineas = new HashMap<String, String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //decodificar id de usuario
        //Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        cadena = "%"+cadena+"%";
        arrayEquipo=this.getEnvDao().getProOrdenEquipoDisponible(cadena, id_empresa);
        
        return arrayEquipo;
    }
    
    
    
    //obtiene las presentaciones que han sido confioguradas de un producto en especifico a partir de Id de Producto
    @RequestMapping(method = RequestMethod.POST, value="/getExistPresentacion.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getExistPresentacionJson(
           @RequestParam(value="alm_id", required=true) Integer alm_id,
           @RequestParam(value="prod_id", required=true) Integer prod_id,
           @RequestParam(value="pres_id", required=true) Integer pres_id,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> existPres = new ArrayList<HashMap<String, String>>();
        
        existPres=this.getEnvDao().getEnv_ExistenciasConf( prod_id, pres_id, alm_id);
        jsonretorno.put("existPres", existPres);
        
        return jsonretorno;
    }
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="producto_id", required=true) String producto_id,
            @RequestParam(value="produccion_id", required=true) String produccion_id,
            @RequestParam(value="folio", required=true) String folio,
            @RequestParam(value="fecha", required=true) String fecha,
            @RequestParam(value="hora", required=true) String hora,
            @RequestParam(value="select_estatus", required=true) String select_estatus,
            @RequestParam(value="select_alm_orden_orig", required=true) String select_alm_orden_orig,
            @RequestParam(value="select_pres_orden_orig", required=true) String select_pres_orden_orig,
            @RequestParam(value="equipo", required=true) String equipo,
            @RequestParam(value="operador", required=true) String operador,
            @RequestParam(value="merma", required=true) String merma,
            @RequestParam(value="exis_pres", required=true) String exis_pres,
            
            @RequestParam(value="eliminado", required=true) String[] eliminado,
            @RequestParam(value="iddetalle", required=false) String[] iddetalle, //is de el registro que que ocupa en la tabla de el detalle
            @RequestParam(value="noTr", required=false) String[] noTr, //numero de posicion de el tr en el grid
            @RequestParam(value="idprod", required=true) String[] idprod,
            @RequestParam(value="select_aml_origen", required=true) String[] select_aml_origen,
            @RequestParam(value="select_pres_dest", required=false) String[] select_pres_dest,
            @RequestParam(value="cantpres", required=false) String[] cantpres,
            @RequestParam(value="cantuni", required=false) String[] cantuni,
            @RequestParam(value="select_aml_dest", required=false) String[] select_aml_dest,
            @RequestParam(value="idconf", required=false) String[] idconf,
            
            @ModelAttribute("user") UserSessionData user
        ) {
            
            System.out.println("Guardar del Pedido");
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            
            Integer app_selected = 137;
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            
            String arreglo[];
            arreglo = new String[noTr.length];
            
            for(int i=0; i<noTr.length; i++) { 
                arreglo[i] = "'"+eliminado[i] +"___"+ noTr[i] +"___" + iddetalle[i] +"___" + idprod[i] +"___" + select_aml_origen[i];
                arreglo[i] += "___" + select_pres_dest[i]+"___" + cantpres[i]+"___" + cantuni[i]+"___" + select_aml_dest[i]+"___" + idconf[i]+"'";
                System.out.println(arreglo[i]);
            }//idconf
            
            //serializar el arreglo
            String extra_data_array = StringUtils.join(arreglo, ",");
            
            if( identificador==0 ){
                command_selected = "new";
            }else{
                command_selected = "edit";
            }
            
            String data_string = 
                    app_selected+"___"+
                    command_selected+"___"+
                    id_usuario+"___"+
                    identificador+"___"+
                    producto_id+"___"+
                    produccion_id+"___"+
                    fecha+"___"+
                    hora+"___"+
                    select_alm_orden_orig+"___"+
                    select_pres_orden_orig+"___"+
                    equipo+"___"+
                    operador+"___"+
                    merma+"___"+
                    exis_pres+"___"+
                    select_estatus;
            //System.out.println("data_string: "+data_string);
            
            succes = this.getEnvDao().selectFunctionValidateAplicativo(data_string,extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            String actualizo = "0";
            
            if( String.valueOf(succes.get("success")).equals("true") ){
                actualizo = this.getEnvDao().selectFunctionForThisApp(data_string, extra_data_array);
                jsonretorno.put("actualizo",String.valueOf(actualizo));
            }
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
}
