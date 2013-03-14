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
import com.agnux.kemikal.interfacedaos.ComInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.PdfReporteComOrdenDeCompra;
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
 * @author Valentin Santos
 * valentin.vale@gmail.com
 * 27/Agosto/2012
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/comordencompra/")
public class CompOrdenCompraController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CompOrdenCompraController.class.getName());
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    //dao de procesos comerciales
    @Autowired
    @Qualifier("daoCom")
    private ComInterfaceDao ComDao;

    public ResourceProject getResource() {
        return resource;
    }
    
    public void setResource(ResourceProject resource) {
        this.resource = resource;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public ComInterfaceDao getComDao() {
        return ComDao;
    }
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CompOrdenCompraController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio", "Folio:80");
        infoConstruccionTabla.put("proveedor", "Proveedor:300");
        infoConstruccionTabla.put("total", "Monto:100");
        infoConstruccionTabla.put("denominacion", "Moneda:70");
        infoConstruccionTabla.put("momento_creacion", "Fecha:80");
        infoConstruccionTabla.put("estado", "Estado:150");
        
        
        ModelAndView x = new ModelAndView("comordencompra/startup", "title", "Orden de compra");
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
    
    
    
    
    
    
    @RequestMapping(value="/getAllOrdenCompra.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllOrdenCompraJson(
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
        
        //aplicativo Orden de Compra
        Integer app_selected = 90;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String proveedor = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("proveedor")))+"%";
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        String producto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("producto")))+"%";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+proveedor+"___"+fecha_inicial+"___"+fecha_final+"___"+codigo+"___"+producto;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getComDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
       
          
        jsonretorno.put("Data", this.getComDao().getComOrdenCompra_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
                //.getComOrdenCompra_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getOrden_Compra.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getOrden_CompraJson(
            @RequestParam(value="id_orden_compra", required=true) String id_orden_compra,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getOrden_CompraJson de {0}", CompOrdenCompraController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> datosOrdenCompra = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosGrid = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> valorIva = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tipoCambioActual = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> tc = new HashMap<String, String>();
        //ArrayList<HashMap<String, String>> vendedores = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> condiciones = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> via_envarque = new ArrayList<HashMap<String, String>>();
        //ArrayList<HashMap<String, String>> metodos_pago = new ArrayList<HashMap<String, String>>();
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( (id_orden_compra.equals("0"))==false  ){
            datosOrdenCompra = this.getComDao().getComOrdenCompra_Datos(Integer.parseInt(id_orden_compra));
            datosGrid = this.getComDao().getComOrdenCompra_DatosGrid(Integer.parseInt(id_orden_compra));
        }
        
        valorIva= this.getComDao().getValoriva(id_sucursal);
        tc.put("tipo_cambio", StringHelper.roundDouble(this.getComDao().getTipoCambioActual(), 4)) ;
        tipoCambioActual.add(0,tc);
        
        monedas = this.getComDao().getMonedas();
        //vendedores = this.getComDao().getAgentes(id_empresa, id_sucursal);
        condiciones = this.getComDao().getCondicionesDePago();
        via_envarque = this.getComDao().getViaEnvarque();
        //metodos_pago = this.getComDao().getMetodosPago();
        
        jsonretorno.put("via_embarque", via_envarque);
        
        jsonretorno.put("datosOrdenCompra", datosOrdenCompra);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("iva", valorIva);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Tc", tipoCambioActual);
        //jsonretorno.put("Vendedores", vendedores);
        jsonretorno.put("Condiciones", condiciones);
        //jsonretorno.put("MetodosPago", metodos_pago);
        
        return jsonretorno;
    }
    
    
    
    
    
    
   //bucador de proveedores
    //obtiene los proveedores para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getBuscaProveedores.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscaProveedoresJson(
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="no_proveedor", required=true) String no_proveedor,
            @RequestParam(value="nombre", required=true) String nombre,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getBuscaProveedoresJson de {0}", CompOrdenCompraController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> proveedores = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        proveedores = this.getComDao().getBuscadorProveedores(rfc, no_proveedor, nombre,id_empresa);
        
        jsonretorno.put("Proveedores", proveedores);
        
        return jsonretorno;
    }
    
    
    
    
    //Obtener datos del Proveedor a partir del Numero de Control
    @RequestMapping(method = RequestMethod.POST, value="/getDataByNoProv.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDataByNoProvJson(
            @RequestParam(value="no_proveedor", required=true) String no_proveedor,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("Proveedor", this.getComDao().getDatosProveedorByNoProv(no_proveedor, id_empresa));
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
        arrayTiposProducto=this.getComDao().getProductoTipos();
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
        
        jsonretorno.put("productos", this.getComDao().getBuscadorProductos(sku,tipo,descripcion,id_empresa));
        
        return jsonretorno;
    }
    
    
    //Buscador de presentaciones de producto
    @RequestMapping(method = RequestMethod.POST, value="/getPresentacionesProducto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getPresentacionesProductoJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        jsonretorno.put("Presentaciones", this.getComDao().getPresentacionesProducto(sku,id_empresa));
        
        return jsonretorno;
    }
    
    
    
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="id_orden_compra", required=true) Integer id_orden_compra,
            @RequestParam(value="id_proveedor", required=true) String id_proveedor,
            @RequestParam(value="select_moneda", required=true) String select_moneda,
            @RequestParam(value="tipo_cambio", required=true) String tipo_cambio,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="cancelado", required=true) String cancelado,
            @RequestParam(value="folio", required=true) String folio,
            @RequestParam(value="grupo", required=true) String grupo,
            @RequestParam(value="select_condiciones", required=true) String select_condiciones,  
            @RequestParam(value="consigandoA", required=true) String consigandoA,
            @RequestParam(value="via_envarque", required=true) String tipo_envarque_id,
            @RequestParam(value="subtotal", required=true) String subtotal,
            @RequestParam(value="impuesto", required=true) String impuesto,
            @RequestParam(value="total", required=true) String total,
            @RequestParam(value="accion_proceso", required=true) String accion_proceso,
            @RequestParam(value="eliminado", required=false) String[] eliminado,
            @RequestParam(value="iddetalle", required=false) String[] iddetalle,
            @RequestParam(value="idproducto", required=false) String[] idproducto,
            @RequestParam(value="id_presentacion", required=false) String[] id_presentacion,
            @RequestParam(value="id_imp_prod", required=false) String[] id_impuesto,
            @RequestParam(value="valor_imp", required=false) String[] valor_imp,
            @RequestParam(value="cantidad", required=false) String[] cantidad,
            @RequestParam(value="costo", required=false) String[] costo,
            @ModelAttribute("user") UserSessionData user
        ) {
            
            System.out.println("Guardar la Orden de Compra");
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            
            Integer app_selected = 90;
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            
            String arreglo[];
            arreglo = new String[eliminado.length];
            
            for(int i=0; i<eliminado.length; i++) {
                arreglo[i]= "'"+eliminado[i] +"___" + iddetalle[i] +"___" + idproducto[i] +"___" + id_presentacion[i] +"___" + id_impuesto[i] +"___" + cantidad[i] +"___" + costo[i] + "___"+valor_imp[i] +"'";
                //System.out.println(arreglo[i]);
            }
            
            //serializar el arreglo
            String extra_data_array = StringUtils.join(arreglo, ",");
            
            if( id_orden_compra==0 ){
                command_selected = "new";
            }else{
                if(accion_proceso.equals("cancelar")){
                    command_selected = accion_proceso;
                }else{
                    command_selected = "edit";
                }
            }
            
            
            String data_string = 
                    app_selected+"___"+
                    command_selected+"___"+
                    id_usuario+"___"+
                    id_orden_compra+"___"+
                    id_proveedor+"___"+  
                    observaciones.toUpperCase()+"___"+
                    select_moneda+"___"+
                    tipo_cambio+"___"+
                    grupo +"___"+
                    select_condiciones+"___"+
                    consigandoA+"___"+
                    tipo_envarque_id;
            
            
            System.out.println("data_string: "+data_string);
            
            succes = this.getComDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            String actualizo = "0";
            
            if( String.valueOf(succes.get("success")).equals("true") ){
                actualizo = this.getComDao().selectFunctionForThisApp(data_string, extra_data_array);
                jsonretorno.put("actualizo",String.valueOf(actualizo));
            }
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    
        //Genera pdf de ORDEN COMPRA
        @RequestMapping(value = "/get_genera_pdf_orden_compra/{id_orden_compra}/{iu}/out.json", method = RequestMethod.GET ) 
        public ModelAndView get_genera_pdf_orden_compraJson(
                @PathVariable("id_orden_compra") Integer id_ordenCompra,
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> datosEncabezadoPie= new HashMap<String, String>();
        HashMap<String, String> datosOrdenCompra = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> conceptosOrdenCompra = new ArrayList<HashMap<String, String>>();
        System.out.println("Generando PDF de Orden Compra");
        
        Integer app_selected = 90; //aplicativo Orden de compra
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        String rfc_empresa=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        datosEncabezadoPie.put("nombre_empresa_emisora", razon_social_empresa);
        datosEncabezadoPie.put("titulo_reporte", this.getGralDao().getTituloReporte(id_empresa, app_selected));
        datosEncabezadoPie.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        datosEncabezadoPie.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        String titulo_reporte2 = this.getGralDao().getTituloReporte(id_empresa, app_selected);
        System.out.println(titulo_reporte2);
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        String ruta_imagen = this.getGralDao().getImagesDir()+rfc_empresa+"_logo.png";
        File file_dir_tmp = new File(dir_tmp);
        
        datosOrdenCompra = this.getComDao().getDatosPDFOrdenCompra(id_ordenCompra);
        conceptosOrdenCompra = this.getComDao().getconceptosOrdenCompra(id_ordenCompra);        
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
        String telefono_empresa = this.getGralDao().getTelefonoEmpresaEmisora(id_empresa);
        //direccion de la empresa
        String direccion_empresa = calle +" #"+ numero +" "+ colonia +"";
        String mun_edo = municipio +", "+ Estado;
        datosOrdenCompra.put("telefono_empresa", telefono_empresa); 
        datosOrdenCompra.put("direccion_empresa", direccion_empresa);        
        datosOrdenCompra.put("emisor_calle", calle);
        datosOrdenCompra.put("emisor_numero", numero);
        datosOrdenCompra.put("emisor_colonia", colonia);
        datosOrdenCompra.put("emisor_expedidoen_municipio", municipio);
        datosOrdenCompra.put("emisor_expedidoen_Estado", Estado);
        datosOrdenCompra.put("emisor_pais", pais);
        datosOrdenCompra.put("emisor_cp", cp);
        datosOrdenCompra.put("emisor_rfc", rfc);
        datosOrdenCompra.put("municipio_sucursal", municipio_sucursal);
        datosOrdenCompra.put("estado_sucursal", estado_sucursal);
        datosOrdenCompra.put("mun_edo", mun_edo);
        
        //genera nombre del archivo
        String file_name = "ORDENCOM_"+ rfc +"_"+ datosOrdenCompra.get("folio") +".pdf";        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;        
        //instancia a la clase que construye el pdf de la del reporte de estado de cuentas del cliente  
        PdfReporteComOrdenDeCompra x = new PdfReporteComOrdenDeCompra(datosEncabezadoPie,datosOrdenCompra,conceptosOrdenCompra,razon_social_empresa,fileout,ruta_imagen);
        
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
