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
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.PocInterfaceDao;
import com.agnux.kemikal.reportes.PdfPocPedidoFormato1;
import com.agnux.kemikal.reportes.PdfPocPedidoFormato2;
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
 * 21/junio/2012
 */

@Controller
@SessionAttributes({"user"})
@RequestMapping("/pocpedidos/")
public class PocPedidosController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(PocPedidosController.class.getName());
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    //dao de procesos comerciales
    @Autowired
    @Qualifier("daoPoc")
    private PocInterfaceDao PocDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    public PocInterfaceDao getPocDao() {
        return PocDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", PocPedidosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio", "Folio:70");
        infoConstruccionTabla.put("cliente", "Cliente:320");
        infoConstruccionTabla.put("total", "Monto:100");
        infoConstruccionTabla.put("denominacion", "Moneda:70");
        infoConstruccionTabla.put("estado", "Estado:100");
        infoConstruccionTabla.put("fecha_creacion","Fecha creacion:110");
        
        ModelAndView x = new ModelAndView("pocpedidos/startup", "title", "Pedidos de Clientes");
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
    
    @RequestMapping(value="/getAllPedidos.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllPedidosJson(
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
        
        //aplicativo pedidos de clientes
        Integer app_selected = 64;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String cliente = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cliente")))+"%";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+cliente+"___"+fecha_inicial+"___"+fecha_final;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getPocDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getPocDao().getPocPedidos_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }

    //Trae los datos del pedido
    @RequestMapping(method = RequestMethod.POST, value="/getPedido.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getPedidoJson(
            @RequestParam(value="id_pedido", required=true) String id_pedido,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getPedidoJson de {0}", PocPedidosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> datosPedido = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosGrid = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> valorIva = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tipoCambioActual = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> vendedores = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> condiciones = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> metodos_pago = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> almacenes = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        HashMap<String, String> tc = new HashMap<String, String>();
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        extra.put("mod_produccion", userDat.get("incluye_produccion"));//esta variable indica si la empresa incluye modulo de produccion
        
        arrayExtra.add(0,extra);
        
        if( !id_pedido.equals("0")  ){
            datosPedido = this.getPocDao().getPocPedido_Datos(Integer.parseInt(id_pedido));
            datosGrid = this.getPocDao().getPocPedido_DatosGrid(Integer.parseInt(id_pedido));
        }
        
        valorIva= this.getPocDao().getValoriva(id_sucursal);
        tc.put("tipo_cambio", StringHelper.roundDouble(this.getPocDao().getTipoCambioActual(), 4));
        tipoCambioActual.add(0,tc);
        
        monedas = this.getPocDao().getMonedas();
        vendedores = this.getPocDao().getAgentes(id_empresa, id_sucursal);
        condiciones = this.getPocDao().getCondicionesDePago();
        metodos_pago = this.getPocDao().getMetodosPago();
        almacenes = this.getPocDao().getPocPedido_Almacenes(id_sucursal);
        
        jsonretorno.put("datosPedido", datosPedido);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("iva", valorIva);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Tc", tipoCambioActual);
        jsonretorno.put("Vendedores", vendedores);
        jsonretorno.put("Condiciones", condiciones);
        jsonretorno.put("MetodosPago", metodos_pago);
        jsonretorno.put("Extras", arrayExtra);
        jsonretorno.put("Almacenes", almacenes);
        return jsonretorno;
    }
    
    
    
    
    
    
    
    
    
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorClientes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorClientesJson(
            @RequestParam(value="cadena", required=true) String cadena,
            @RequestParam(value="filtro", required=true) Integer filtro,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        
        jsonretorno.put("clientes", this.getPocDao().getBuscadorClientes(cadena,filtro,id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/getDataByNoClient.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDataByNoClientJson(
            @RequestParam(value="no_control", required=true) String no_control,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        
        jsonretorno.put("Cliente", this.getPocDao().getDatosClienteByNoCliente(no_control, id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    
    //obtiene las Direcciones Fiscales del Cliente
    //esta busqueda solo es cuando el cliente tiene mas de una Dirección Fiscal
    @RequestMapping(method = RequestMethod.POST,value="/getDireccionesFiscalesCliente.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String,String>>>getDireccionesFiscalesClienteJson(
            @RequestParam(value="id_cliente",required=true )Integer id_cliente,
            Model model
        ){
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        ArrayList<HashMap<String, String>> direcciones_fiscales = new ArrayList<HashMap<String, String>>();
        direcciones_fiscales=this.getPocDao().getPocPedido_DireccionesFiscalesCliente(id_cliente);
        jsonretorno.put("DirFiscal", direcciones_fiscales);
        
        return jsonretorno;
    }
    
    //obtiene la moneda de la lista de precios por el cliente
    @RequestMapping(method = RequestMethod.POST,value="/getMonedaListaCliente.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String,String>>>getMonedaListaClienteJson(
            @RequestParam(value="lista_precio",required=true )Integer lista_precio,
            Model model
            ){
            HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        ArrayList<HashMap<String, String>> arraylistaprecio = new ArrayList<HashMap<String, String>>();
        arraylistaprecio=this.getPocDao().getListaPrecio(lista_precio);
        jsonretorno.put("listaprecio", arraylistaprecio);
        
        return jsonretorno;
    }
    
    
    //obtiene los tipos de productos para el buscador de productos
    @RequestMapping(method = RequestMethod.POST, value="/getProductoTipos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProductoTiposJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        ArrayList<HashMap<String, String>> arrayTiposProducto = new ArrayList<HashMap<String, String>>();
        arrayTiposProducto=this.getPocDao().getProductoTipos();
        jsonretorno.put("prodTipos", arrayTiposProducto);
        
        return jsonretorno;
    }
    
    
    
    
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorProductos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorProductosJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="tipo", required=true) String tipo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        jsonretorno.put("productos", this.getPocDao().getBuscadorProductos(sku,tipo,descripcion,id_empresa));
        
        return jsonretorno;
    }
    
    
    //Buscador de presentaciones de producto
    @RequestMapping(method = RequestMethod.POST, value="/getPresentacionesProducto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getPresentacionesProductoJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="lista_precios",required=true) String lista_precio,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        jsonretorno.put("Presentaciones", this.getPocDao().getPresentacionesProducto(sku,lista_precio,id_empresa));
        
        return jsonretorno;
    }
    
    
    
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="id_pedido", required=true) Integer id_pedido,
            @RequestParam(value="id_cliente", required=true) String id_cliente,
            @RequestParam(value="select_moneda", required=true) String select_moneda,
            @RequestParam(value="tasa_ret_immex", required=true) String tasa_ret_immex,
            @RequestParam(value="tipo_cambio", required=true) String tipo_cambio,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="vendedor", required=true) String id_agente,
            @RequestParam(value="select_condiciones", required=true) String select_condiciones,
            @RequestParam(value="orden_compra", required=true) String orden_compra,
            @RequestParam(value="fecha_compromiso", required=true) String fecha_compromiso,
            @RequestParam(value="lugar_entrega", required=true) String lugar_entrega,
            @RequestParam(value="transporte", required=true) String transporte,
            @RequestParam(value="accion_proceso", required=true) String accion_proceso,
            @RequestParam(value="select_metodo_pago", required=true) Integer select_metodo_pago,
            @RequestParam(value="no_cuenta", required=false) String no_cuenta,
            @RequestParam(value="check_ruta", required=false) String check_ruta,
            @RequestParam(value="select_almacen", required=false) String select_almacen,
            @RequestParam(value="id_df", required=true) String id_df,
            @RequestParam(value="check_enviar_obser", required=false) String check_enviar_obser,
            @RequestParam(value="eliminado", required=false) String[] eliminado,
            @RequestParam(value="iddetalle", required=false) String[] iddetalle,
            @RequestParam(value="idproducto", required=false) String[] idproducto,
            @RequestParam(value="id_presentacion", required=false) String[] id_presentacion,
            @RequestParam(value="id_imp_prod", required=false) String[] id_impuesto,
            @RequestParam(value="valor_imp", required=false) String[] valor_imp,
            @RequestParam(value="cantidad", required=false) String[] cantidad,
            @RequestParam(value="costo", required=false) String[] costo,
            @RequestParam(value="noTr", required=false) String[] noTr,
            @RequestParam(value="seleccionado", required=false) String[] seleccionado,
            @ModelAttribute("user") UserSessionData user
        ) {
            
            System.out.println("Guardar del Pedido");
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            
            Integer app_selected = 64;
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            
            String arreglo[];
            arreglo = new String[eliminado.length];
            
            for(int i=0; i<eliminado.length; i++) { 
                arreglo[i]= "'"+eliminado[i] +"___" + iddetalle[i] +"___" + idproducto[i] +"___" + id_presentacion[i] +"___" + id_impuesto[i] +"___" + cantidad[i] +"___" + costo[i] + "___"+valor_imp[i] + "___"+noTr[i] + "___"+seleccionado[i] +"'";
                //System.out.println(arreglo[i]);
            }
            
            //serializar el arreglo
            String extra_data_array = StringUtils.join(arreglo, ",");
            
            if( id_pedido==0 ){
                command_selected = "new";
            }else{
                if(accion_proceso.equals("cancelar")){
                    command_selected = accion_proceso;
                }else{
                    command_selected = "edit";
                }
            }
            
            
            if (no_cuenta==null){
                no_cuenta="";
            }
            
            check_ruta = StringHelper.verificarCheckBox(check_ruta);
            check_enviar_obser = StringHelper.verificarCheckBox(check_enviar_obser);
            if (id_df.equals("0")){
                id_df="1";//si viene cero, le asignamos uno para indicar que debe tomar la direccion de la tabla cxc_clie.
            }
            
            String data_string = 
                    app_selected+"___"+
                    command_selected+"___"+
                    id_usuario+"___"+
                    id_pedido+"___"+
                    id_cliente+"___"+
                    select_moneda+"___"+
                    observaciones.toUpperCase()+"___"+
                    tipo_cambio+"___"+
                    id_agente+"___"+
                    select_condiciones+"___"+
                    orden_compra+"___"+
                    fecha_compromiso+"___"+
                    lugar_entrega.toUpperCase()+"___"+
                    transporte.toUpperCase()+"___"+
                    tasa_ret_immex+"___"+
                    select_metodo_pago+"___"+
                    no_cuenta+"___"+
                    check_ruta+"___"+
                    select_almacen+"___"+
                    id_df+"___"+
                    check_enviar_obser;
            //System.out.println("data_string: "+data_string);
            
            succes = this.getPocDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            String actualizo = "0";
            
            if( String.valueOf(succes.get("success")).equals("true") ){
                actualizo = this.getPocDao().selectFunctionForThisApp(data_string, extra_data_array);
                jsonretorno.put("actualizo",String.valueOf(actualizo));
            }
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    
    //Genera pdf de PEDIDOS
    @RequestMapping(value = "/get_genera_pdf_pedido/{id_pedido}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView get_genera_pdf_pedidoJson(
                @PathVariable("id_pedido") Integer id_pedido,
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model
    )throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> parametros = new HashMap<String, String>();
        HashMap<String, String> datosEncabezadoPie= new HashMap<String, String>();
        HashMap<String, String> datospedido_pdf = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> conceptos_pedido = new ArrayList<HashMap<String, String>>();
        
        System.out.println("Generando PDF de Pedido");
        
        Integer app_selected = 64; //aplicativo Pedidos de clientes
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        String rfc_empresa=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        datosEncabezadoPie.put("nombre_empresa_emisora", razon_social_empresa);
        datosEncabezadoPie.put("titulo_reporte", this.getGralDao().getTituloReporte(id_empresa, app_selected));
        datosEncabezadoPie.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        datosEncabezadoPie.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        String ruta_imagen = this.getGralDao().getImagesDir()+rfc_empresa+"_logo.png";
        
        File file_dir_tmp = new File(dir_tmp);
        
        //aqui se obtienen los parametros de la facturacion, nos intersa el tipo de formato para el pdf del pedido
        parametros = this.getPocDao().getPocPedido_Parametros(id_empresa, id_sucursal);
        
        datospedido_pdf = this.getPocDao().getDatosPDF(id_pedido);
        conceptos_pedido = this.getPocDao().getPocPedido_DatosGrid(id_pedido);
        
        String municipio = this.getGralDao().getMunicipioSucursalEmisora(id_sucursal);
        String Estado = this.getGralDao().getEstadoSucursalEmisora(id_sucursal);
        
        String calle = this.getGralDao().getCalleDomicilioFiscalEmpresaEmisora(id_empresa);
        String numero= this.getGralDao().getNoExteriorDomicilioFiscalEmpresaEmisora(id_empresa);
        String colonia= this.getGralDao().getColoniaDomicilioFiscalEmpresaEmisora(id_empresa);
        String pais= this.getGralDao().getPaisDomicilioFiscalEmpresaEmisora(id_empresa);
        String cp= this.getGralDao().getCpDomicilioFiscalEmpresaEmisora(id_empresa);
        String rfc=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String municipio_sucursal = this.getGralDao().getMunicipioSucursalEmisora(id_sucursal);
        String estado_sucursal = this.getGralDao().getEstadoSucursalEmisora(id_sucursal);
        
        
        datospedido_pdf.put("emisor_calle", calle);
        datospedido_pdf.put("emisor_numero", numero);
        datospedido_pdf.put("emisor_colonia", colonia);
        datospedido_pdf.put("emisor_expedidoen_municipio", municipio);
        datospedido_pdf.put("emisor_expedidoen_Estado", Estado);
        datospedido_pdf.put("emisor_pais", pais);
        datospedido_pdf.put("emisor_cp", cp);
        datospedido_pdf.put("emisor_rfc", rfc);
        datospedido_pdf.put("municipio_sucursal", municipio_sucursal);
        datospedido_pdf.put("estado_sucursal", estado_sucursal);
        
        //genera nombre del archivo
        String file_name = "PED_"+ rfc +"_"+ datospedido_pdf.get("folio") +".pdf";
        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        
        if (parametros.get("formato_pedido").equals("1")){
            //instancia a la clase que construye el pdf de la del reporte de estado de cuentas del cliente
            PdfPocPedidoFormato1 x = new PdfPocPedidoFormato1(datosEncabezadoPie,datospedido_pdf,conceptos_pedido,razon_social_empresa,fileout,ruta_imagen);
        }else{
            //instancia a la clase que construye el pdf de la del reporte de estado de cuentas del cliente
            PdfPocPedidoFormato2 x = new PdfPocPedidoFormato2(datosEncabezadoPie,datospedido_pdf,conceptos_pedido,razon_social_empresa,fileout,ruta_imagen);
        }
        
        

        
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
        
        FileHelper.delete(fileout);
        
        return null;
        
    }
   
}
