/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.kemikal.interfacedaos.CotizacionesInterfaceDao;
import com.agnux.kemikal.reportes.pdfCotizacion;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.ParametrosGeneralesInterfaceDao;
import com.agnux.kemikal.interfacedaos.PocInterfaceDao;
import com.agnux.kemikal.reportes.PDFCotizacionDescripcion;
import com.itextpdf.text.DocumentException;
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
import org.springframework.web.bind.annotation.RequestParam;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.servlet.ServletOutputStream;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;


/**
 *
 * @author Noe Martinez
 * 11/febrero/2013
 * gpmarsan@gmail.com
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/cotizaciones/")
public class CotizacionesController {
    
    private static final Logger log  = Logger.getLogger(CotizacionesController.class.getName());
    ResourceProject resource = new ResourceProject();

    //dao de procesos comerciales
    @Autowired
    @Qualifier("daoPoc")
    private PocInterfaceDao PocDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public PocInterfaceDao getPocDao() {
        return PocDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CotizacionesController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("folio", "Folio:90");
        
        if(user.getIncluyeCrm().equals("true")){
            infoConstruccionTabla.put("tipo", "Tipo:100");
            infoConstruccionTabla.put("cliente", "Cliente/Prospecto:300");
        }else{
            infoConstruccionTabla.put("cliente", "Cliente:300");
        }
        
        infoConstruccionTabla.put("fecha","Fecha:110");
        
        ModelAndView x = new ModelAndView("cotizaciones/startup", "title", "Cotizaciones");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("sucursal", user.getSucursal());
        x = x.addObject("crm", user.getIncluyeCrm());
        
        String userId = String.valueOf(user.getUserId());
        
        String codificado = Base64Coder.encodeString(userId);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    
    
    
    @RequestMapping(value="/getCotizaciones.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getCotizacionesJson(
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
        
        //aplicativo Cotizaciones
        Integer app_selected = 12;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String cliente = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cliente")))+"%";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        String tipo = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo")))+"";
        String incluye_crm = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("incluye_crm")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+cliente+"___"+fecha_inicial+"___"+fecha_final+"___"+tipo+"___"+incluye_crm;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getPocDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getPocDao().getCotizacion_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
        
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getCotizacion.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getCotizacionJson(
            @RequestParam(value="id_cotizacion", required=true) String id_cotizacion,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getCotizacionJson de {0}", CotizacionesController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datosCotizacion = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> DatosCliPros = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosGrid = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> valorIva = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        HashMap<String, String> tc = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> tipoCambioActual = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        String dirImgProd="";
        
        extra.put("mod_crm", userDat.get("incluye_crm"));
        arrayExtra.add(0,extra);
        
        if( (id_cotizacion.equals("0"))==false  ){
            datosCotizacion = this.getPocDao().getCotizacion_Datos(Integer.parseInt(id_cotizacion));
            if(datosCotizacion.get(0).get("tipo").equals("1")){
                DatosCliPros = this.getPocDao().getCotizacion_DatosCliente(Integer.parseInt(id_cotizacion));
            }else{
                DatosCliPros = this.getPocDao().getCotizacion_DatosProspecto(Integer.parseInt(id_cotizacion));
            }
            datosGrid = this.getPocDao().getCotizacion_DatosGrid(Integer.parseInt(id_cotizacion));
        }
        
        valorIva= this.getPocDao().getValoriva(id_sucursal);
        monedas = this.getPocDao().getMonedas();
        tc.put("tipo_cambio", StringHelper.roundDouble(this.getPocDao().getTipoCambioActual(), 4));
        tipoCambioActual.add(0,tc);
        
        jsonretorno.put("datosCotizacion", datosCotizacion);
        jsonretorno.put("DatosCP", DatosCliPros);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("iva", valorIva);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Extras", arrayExtra);
        jsonretorno.put("Tc", tipoCambioActual);
        
        return jsonretorno;
    }
    
    
    
    //Buscador de Clientes ó Prospectos segun el parametro Tipo de busqueda
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorClienteProspecto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorClienteProspectoJson(
            @RequestParam(value="tipo", required=true) String tipo,
            @RequestParam(value="cadena", required=true) String cadena,
            @RequestParam(value="filtro", required=true) Integer filtro,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if(tipo.equals("1")){
            //buscar clientes
            datos = this.getPocDao().getBuscadorClientes(cadena,filtro,id_empresa,id_sucursal);
        }else{
            //buscar Prospectos
            datos = this.getPocDao().getBuscadorProspectos(cadena,filtro,id_empresa,id_sucursal);
        }
        
        jsonretorno.put("Resultado", datos);
        
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
    
    
    //Buscador de productos
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_productos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> get_buscador_productosJson(
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
            @RequestParam(value="lista_precio",required=true) String lista_precio,
            @RequestParam(value="tipo",required=true) String tipo,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        jsonretorno.put("Presentaciones", this.getPocDao().getPresentacionesProducto(sku, lista_precio, id_empresa));
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene la moneda de la lista de precios del cliente
    @RequestMapping(method = RequestMethod.POST,value="/getMonedaLista.json")
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
    
    
    
    
    //descargtar imagen
    @RequestMapping(method = RequestMethod.GET, value="/imgDownloadImg/{name_img}/{id}/{iu}/out.json")
    public @ResponseBody HashMap<String, String> imgDownloadImgJson(
        @PathVariable("name_img") String name_img,
        @PathVariable("id") String id,
        @PathVariable("iu") String id_user,
        HttpServletResponse response, 
        Model model) throws IOException {
        ServletOutputStream out;
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        String rfc_empresa=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        String varDir = "";
        if(id.equals("0")){
            varDir = this.getGralDao().getJvmTmpDir();
        }else{
            varDir = this.getGralDao().getProdImgDir()+rfc_empresa;
        }
        
        File file = new File(varDir+"/"+name_img);
        
        byte[] fichero = FileHelper.BytesFromFile(file);
        response.setContentType ("application/png");
        response.setHeader ( "Content-disposition", "inline; filename=" + name_img );
        response.setHeader ( "Cache-Control", "max-age=30" );
        response.setHeader ( "Pragma", "No-cache" );
        response.setDateHeader ("Expires", 0);
        response.setContentLength (fichero.length);
        out = response.getOutputStream ();
        out.write (fichero, 0, fichero.length);
        out.flush ();
        out.close ();
        
        return null;
    }
    
    
    /*
    id_cotizacion
    id_cliente
    select_tipo_cotizacion
    check_descripcion_larga
    observaciones
    total_tr
    
    iddetalle
    eliminado
    idproducto
    id_presentacion
    monedagrid
    cantidad
    precio
     */
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="id_cotizacion", required=true) Integer identificador,
            @RequestParam(value="select_tipo_cotizacion", required=true) String select_tipo_cotizacion,
            @RequestParam(value="id_cliente", required=true) String id_cliente,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="check_descripcion_larga", required=false) String check_descripcion_larga,
            @RequestParam(value="tc", required=true) String tc,
            @RequestParam(value="moneda", required=true) String moneda_id,
            @RequestParam(value="total_tr", required=true) String total_tr,
            @RequestParam(value="iddetalle", required=true) String[] iddetalle,
            @RequestParam(value="eliminado", required=true) String[] eliminado,
            @RequestParam(value="idproducto", required=true) String[] idproducto,
            @RequestParam(value="id_presentacion", required=true) String[] id_presentacion,
            @RequestParam(value="cantidad", required=true) String[] cantidad,
            @RequestParam(value="precio", required=true) String[] precio,
            @RequestParam(value="monedagrid", required=true) String[] monedagrid,
            @RequestParam(value="notr", required=true) String[] notr,
            @ModelAttribute("user") UserSessionData user,
            Model model
        ) {
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            String arreglo[];
            arreglo = new String[eliminado.length];
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            Integer app_selected = 12;
            String command_selected = "new";
            String actualizo = "0";
            
            if( identificador==0 ){
                command_selected = "new";
            }else{
                command_selected = "edit";
            }
            
            for(int i=0; i<eliminado.length; i++) { 
                //Imprimir el contenido de cada celda 
                arreglo[i]= "'"+eliminado[i] +"___" + iddetalle[i] +"___" + idproducto[i] +"___" + id_presentacion[i] +"___" + cantidad[i] +"___" + precio[i] +"___" + monedagrid[i]+"___"+notr[i]+"'";
                //System.out.println("arreglo["+i+"] = "+arreglo[i]);
            }
            
            //serializar el arreglo
            String extra_data_array = StringUtils.join(arreglo, ",");
            
            check_descripcion_larga = StringHelper.verificarCheckBox(check_descripcion_larga);
            
            String data_string = 
                    app_selected + "___"+ 
                    command_selected + "___"+ 
                    id_usuario + "___"+ 
                    identificador + "___"+ 
                    select_tipo_cotizacion + "___"+ 
                    id_cliente + "___"+ 
                    check_descripcion_larga + "___"+ 
                    observaciones.toUpperCase() + "___"+ 
                    tc+"___"+
                    moneda_id;
            
            succes = this.getPocDao().selectFunctionValidateAaplicativo(data_string, app_selected, extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            
            if( String.valueOf(succes.get("success")).equals("true")  ){
                actualizo = this.getPocDao().selectFunctionForThisApp(data_string, extra_data_array);
            }
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    //cambiar a borrado logico un registro
    @RequestMapping(method = RequestMethod.POST, value="/logicDelete.json")
    public @ResponseBody HashMap<String, String> logicDeleteJson(
            @RequestParam(value="id_cotizacion", required=true) String id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 12;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        System.out.println("Ejecutando borrado logico de una Cotizacion");
        jsonretorno.put("success",String.valueOf( this.getPocDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    

    
    
    
    
    
    @RequestMapping(value = "/getGeneraPdfCotizacion/{id_cotizacion}/{incluye_img}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfCotizacionJson(
                @PathVariable("id_cotizacion") Integer id_cotizacion,
                @PathVariable("incluye_img") String incluye_img,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> HeaderFooter = new HashMap<String, String>();
        HashMap<String, String> datos = new HashMap<String, String>();
        HashMap<String, String> datosEmisor = new HashMap<String, String>();
        HashMap<String, String> datosReceptor = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datosCotizacion = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosCliPros = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> lista_productos = new ArrayList<HashMap<String, String>>();
        
        System.out.println("Generando PDF de Cotizacion");
        
        Integer app_selected = 12; //aplicativo Cotizaciones
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String rfc_empresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        //directorio de imagenes de productos
        String dirImgProd = this.getGralDao().getProdImgDir()+rfc_empresa+"/";
        
        //ruta del la imagen del Logotipo
        String rutaLogoEmpresa = this.getGralDao().getImagesDir()+rfc_empresa+"_logo.png";
        
        
        String file_name = "COT_"+rfc_empresa+".pdf";
        
        //ruta de archivo de salida
        String fileout = dir_tmp + file_name;
        
        datosEmisor.put("emp_razon_social", razon_social_empresa);
        datosEmisor.put("emp_rfc", this.getGralDao().getRfcEmpresaEmisora(id_empresa));
        datosEmisor.put("emp_calle", this.getGralDao().getCalleDomicilioFiscalEmpresaEmisora(id_empresa));
        datosEmisor.put("emp_no_exterior", this.getGralDao().getNoExteriorDomicilioFiscalEmpresaEmisora(id_empresa));
        datosEmisor.put("emp_colonia", this.getGralDao().getColoniaDomicilioFiscalEmpresaEmisora(id_empresa));
        datosEmisor.put("emp_pais", this.getGralDao().getPaisDomicilioFiscalEmpresaEmisora(id_empresa));
        datosEmisor.put("emp_estado", this.getGralDao().getEstadoDomicilioFiscalEmpresaEmisora(id_empresa));
        datosEmisor.put("emp_municipio", this.getGralDao().getMunicipioDomicilioFiscalEmpresaEmisora(id_empresa));
        datosEmisor.put("emp_cp", this.getGralDao().getCpDomicilioFiscalEmpresaEmisora(id_empresa));
        
        HeaderFooter.put("titulo_reporte", "COTIZACIÓN");
        HeaderFooter.put("periodo", "");
        HeaderFooter.put("empresa", "");
        HeaderFooter.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        HeaderFooter.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        
        datosCotizacion = this.getPocDao().getCotizacion_Datos(id_cotizacion);
        lista_productos = this.getPocDao().getCotizacion_DatosGrid(id_cotizacion);
        datos.put("ruta_logo", rutaLogoEmpresa);
        datos.put("file_out", fileout);
        datos.put("dirImagenes", dirImgProd);
        datos.put("tipo", datosCotizacion.get(0).get("tipo"));
        datos.put("folio", datosCotizacion.get(0).get("folio"));
        datos.put("fecha", datosCotizacion.get(0).get("fecha"));
        datos.put("tipoCambio", datosCotizacion.get(0).get("tipo_cambio"));
        datos.put("observaciones", datosCotizacion.get(0).get("observaciones"));
        datos.put("img_desc", incluye_img);
        datos.put("nombre_usuario", datosCotizacion.get(0).get("nombre_usuario"));
        datos.put("puesto_usuario", datosCotizacion.get(0).get("puesto_usuario"));
        
        if(datosCotizacion.get(0).get("tipo").equals("1")){
            datosCliPros = this.getPocDao().getCotizacion_DatosCliente(id_cotizacion);
        }else{
            datosCliPros = this.getPocDao().getCotizacion_DatosProspecto(id_cotizacion);
        }
        
        datosReceptor.put("clieCalle", datosCliPros.get(0).get("calle"));
        datosReceptor.put("clieNumero", datosCliPros.get(0).get("numero"));
        datosReceptor.put("clieColonia", datosCliPros.get(0).get("colonia"));
        datosReceptor.put("clieMunicipio", datosCliPros.get(0).get("municipio"));
        datosReceptor.put("clieEstado", datosCliPros.get(0).get("estado"));
        datosReceptor.put("cliePais", datosCliPros.get(0).get("pais"));
        datosReceptor.put("clieCp", datosCliPros.get(0).get("cp"));
        datosReceptor.put("clieTel", datosCliPros.get(0).get("telefono"));
        datosReceptor.put("clieRfc", datosCliPros.get(0).get("rfc"));
        datosReceptor.put("clieContacto", datosCliPros.get(0).get("contacto"));
        datosReceptor.put("clieRazonSocial", datosCliPros.get(0).get("razon_social"));
        
        pdfCotizacion pdf = new pdfCotizacion(HeaderFooter, datosEmisor, datos,datosReceptor,lista_productos);
        pdf.ViewPDF();
        
        
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