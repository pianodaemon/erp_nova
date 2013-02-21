/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import com.agnux.kemikal.reportes.PdfInvControlCosto;
import com.agnux.kemikal.reportes.PdfOrdenSalida;
import com.itextpdf.text.DocumentException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * 20/febrero/2012
 * 
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/invactualizaprecios/")
public class InvActualizadorPreciosController {
    private static final Logger log  = Logger.getLogger(InvActualizadorPreciosController.class.getName());
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
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvActualizadorPreciosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        //infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("codigo", "C&oacute;digo:90");
        infoConstruccionTabla.put("descripcion","Descripci&oacute;n:200");
        infoConstruccionTabla.put("unidad", "Unidad:80");
        infoConstruccionTabla.put("presentacion", "Presentaci&oacute;n:100");
        infoConstruccionTabla.put("precio_minimo", "Precio M&iacute;nimo:110");
        infoConstruccionTabla.put("moneda", "Moneda:80");
        
        ModelAndView x = new ModelAndView("invactualizaprecios/startup", "title", "Actualizador de Precios a partir del Precio M&iacute;nimo");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("sucursal", user.getSucursal());
        
        String userId = String.valueOf(user.getUserId());
        
        //codificar id de usuario
        String codificado = Base64Coder.encodeString(userId);
       
        //decodificar id de usuario
        //String decodificado = Base64Coder.decodeString(codificado);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    
    //Metodo para el grid y el Paginado
    @RequestMapping(value="/getAllPminProductos.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllCostosJson(
           @RequestParam(value="orderby", required=true) String orderby,
           @RequestParam(value="desc", required=true) String desc,
           @RequestParam(value="items_por_pag", required=true) int items_por_pag,
           @RequestParam(value="pag_start", required=true) int pag_start,
           @RequestParam(value="display_pag", required=true) String display_pag,
           @RequestParam(value="input_json", required=true) String input_json,
           @RequestParam(value="cadena_busqueda", required=true) String cadena_busqueda,
           @RequestParam(value="iu", required=true) String id_user_cod,
           Model modcel
       ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String,String> has_busqueda = StringHelper.convert2hash(StringHelper.ascii2string(cadena_busqueda));
        
        //aplicativo Actualizador lista de Precios a partir de Precio Minimo
        Integer app_selected = 126;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String tipo_producto = StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo_producto")));
        String familia = StringHelper.isNullString(String.valueOf(has_busqueda.get("familia")));
        String subfamilia = StringHelper.isNullString(String.valueOf(has_busqueda.get("subfamilia")));
        String marca = StringHelper.isNullString(String.valueOf(has_busqueda.get("marca")));
        String presentacion = StringHelper.isNullString(String.valueOf(has_busqueda.get("presentacion")));
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        String producto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("producto")))+"%";
        
        
        String tipo_costo="1";//calculo a partir del ultimo costo
        String importacion="0";
        String directo="0";
        String pminimo="0";
        String simulacion="false";
        String tipo_cambio="0";
        
        //esta parte no es igual a todos los aplicativos porque se cambia el procedimiento de busqueda,
        //Se utiliza el mismo procedimiento que se utiliza en el plugin de Control de Costos.
        //se tomo la decision de utilizar el mismo proc porque se hace varios calculos y asi evitamos volver a construir codigo para el grid
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        String data_string = 
                app_selected+"___"+
                id_usuario+"___"+
                tipo_producto+"___"+
                marca+"___"+
                familia+"___"+
                subfamilia+"___"+
                codigo+"___"+
                producto+"___"+
                presentacion;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getInvDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data",this.getInvDao().getInvActualizaPrecio_PaginaGrid(data_string, offset, items_por_pag, orderby, desc) );
        
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
        ArrayList<HashMap<String, String>> marcas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> familias = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> subfamilias = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> presentaciones = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        String id_tipo_producto = "1";//Tipo de Producto TERMINADO(Para que se traiga por default las Familias de Productos Terminados)
        
        tiposProducto = this.getInvDao().getProducto_Tipos();
        marcas = this.getInvDao().getProducto_Marcas(id_empresa);
        familias = this.getInvDao().getInvProdSubFamiliasByTipoProd(id_empresa, id_tipo_producto);
        
        String familia_id = "0";
        if(familias.size()>0){
            familia_id = familias.get(0).get("id");
        }
        
        subfamilias = this.getInvDao().getProducto_Subfamilias(id_empresa,familia_id );
        
        //Se le pasa como parametro el cero para que devuelva todas las presentaciones 
        presentaciones = this.getInvDao().getProducto_Presentaciones(0);
        
        jsonretorno.put("Anios", this.getInvDao().getInvControlCostos_Anios());
        jsonretorno.put("Marcas", marcas);
        jsonretorno.put("Familias", familias);
        jsonretorno.put("SubFamilias", subfamilias);
        jsonretorno.put("ProdTipos", tiposProducto);
        jsonretorno.put("Presentaciones", presentaciones);
        return jsonretorno;
    }
    
    
    //obtiene los Familias del producto seleccionado
    @RequestMapping(method = RequestMethod.POST, value="/getSubFamiliasByFamProd.json")
    //public @ResponseBody HashMap<java.lang.String,java.lang.Object> getProveedorJson(
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getSubFamiliasByFamProdJson(
            @RequestParam(value="fam", required=true) String familia_id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getSubFamiliasByFamProdJson de {0}", InvActualizadorPreciosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> subfamilias = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        subfamilias = this.getInvDao().getProducto_Subfamilias(id_empresa,familia_id );
        
        jsonretorno.put("SubFamilias", subfamilias);
        
        return jsonretorno;
    }
    
    //obtiene los Familias del producto seleccionado
    @RequestMapping(method = RequestMethod.POST, value="/getFamiliasByTipoProd.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getFamiliasByTipoProdJson(
            @RequestParam(value="tipo_prod", required=true) String tipo_prod,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getFamiliasByTipoProdJson de {0}", InvActualizadorPreciosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> familias = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        familias = this.getInvDao().getInvProdSubFamiliasByTipoProd(id_empresa, tipo_prod);
        
        jsonretorno.put("Familias", familias);
        
        return jsonretorno;
    }
    
    
    //crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) String id,
            @RequestParam(value="tipo_producto", required=true) String tipo_producto,
            @RequestParam(value="familia", required=true) String familia,
            @RequestParam(value="subfamilia", required=true) String subfamilia,
            @RequestParam(value="presentacion", required=true) String presentacion,
            @RequestParam(value="codigo", required=true) String codigo,
            @RequestParam(value="producto", required=true) String producto,
            @RequestParam(value="marca", required=true) String marca,
            @RequestParam(value="lista1", required=false) String lista1,
            @RequestParam(value="lista2", required=false) String lista2,
            @RequestParam(value="lista3", required=false) String lista3,
            @RequestParam(value="lista4", required=false) String lista4,
            @RequestParam(value="lista5", required=false) String lista5,
            @RequestParam(value="lista6", required=false) String lista6,
            @RequestParam(value="lista7", required=false) String lista7,
            @RequestParam(value="lista8", required=false) String lista8,
            @RequestParam(value="lista9", required=false) String lista9,
            @RequestParam(value="lista10", required=false) String lista10,
            @RequestParam(value="descto1", required=false) String descto1,
            @RequestParam(value="descto2", required=false) String descto2,
            @RequestParam(value="descto3", required=false) String descto3,
            @RequestParam(value="descto4", required=false) String descto4,
            @RequestParam(value="descto5", required=false) String descto5,
            @RequestParam(value="descto6", required=false) String descto6,
            @RequestParam(value="descto7", required=false) String descto7,
            @RequestParam(value="descto8", required=false) String descto8,
            @RequestParam(value="descto9", required=false) String descto9,
            @RequestParam(value="descto10", required=false) String descto10,
            @RequestParam(value="check_aplicar_descto", required=false) String aplicar_descto,
            Model model,@ModelAttribute("user") UserSessionData user
        ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 126;
        String command_selected = "update";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        lista1 = StringHelper.removerComas(lista1);
        lista2 = StringHelper.removerComas(lista2);
        lista3 = StringHelper.removerComas(lista3);
        lista4 = StringHelper.removerComas(lista4);
        lista5 = StringHelper.removerComas(lista5);
        lista6 = StringHelper.removerComas(lista6);
        lista7 = StringHelper.removerComas(lista7);
        lista8 = StringHelper.removerComas(lista8);
        lista9 = StringHelper.removerComas(lista9);
        lista10 = StringHelper.removerComas(lista10);
        descto1 = StringHelper.removerComas(descto1);
        descto2 = StringHelper.removerComas(descto2);
        descto3 = StringHelper.removerComas(descto3);
        descto4 = StringHelper.removerComas(descto4);
        descto5 = StringHelper.removerComas(descto5);
        descto6 = StringHelper.removerComas(descto6);
        descto7 = StringHelper.removerComas(descto7);
        descto8 = StringHelper.removerComas(descto8);
        descto9 = StringHelper.removerComas(descto9);
        descto10 = StringHelper.removerComas(descto10);
        
        aplicar_descto = StringHelper.verificarCheckBox(aplicar_descto);
        
        if( id.equals("0") ){
            command_selected = "update";
        }else{
            //command_selected = "edit";
        }
        
        String data_string = 
                app_selected+"___"+
                command_selected+"___"+
                id_usuario+"___"+
                tipo_producto+"___"+
                familia+"___"+
                subfamilia+"___"+
                presentacion+"___%"+
                codigo+"%___%"+
                producto+"%___"+
                aplicar_descto+"___"+
                lista1+"___"+
                lista2+"___"+
                lista3+"___"+
                lista4+"___"+
                lista5+"___"+
                lista6+"___"+
                lista7+"___"+
                lista8+"___"+
                lista9+"___"+
                lista10+"___"+
                descto1+"___"+
                descto2+"___"+
                descto3+"___"+
                descto4+"___"+
                descto5+"___"+
                descto6+"___"+
                descto7+"___"+
                descto8+"___"+
                descto9+"___"+
                descto10+"___"+
                marca;
        
        succes = this.getInvDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getInvDao().selectFunctionForApp_MovimientosInventario(data_string, extra_data_array);
        }
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }

    
    
    
    
    
    
}
