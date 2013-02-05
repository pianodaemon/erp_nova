/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.kemikal.interfacedaos.CotizacionesInterfaceDao;
import com.agnux.kemikal.reportes.pdfCotizacion;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.ParametrosGeneralesInterfaceDao;
import com.agnux.kemikal.reportes.PDFCotizacionDescripcion;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;


/**
 *
 * @author pianodaemon
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/cotizaciones/")
public class CotizacionesController {
    
    private static final Logger log  = Logger.getLogger(CotizacionesController.class.getName());
    ResourceProject resource = new ResourceProject();
    @Autowired
    @Qualifier("daoCotizacion")
    private CotizacionesInterfaceDao cotdao;
    
    @Autowired
    @Qualifier("daoParametros")
    private ParametrosGeneralesInterfaceDao pgdao;

    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public ParametrosGeneralesInterfaceDao getPgdao() {
        return pgdao;
    }
    
    public CotizacionesInterfaceDao getCotdao() {
        return cotdao;
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
        infoConstruccionTabla.put("cliente", "Cliente:300");
        infoConstruccionTabla.put("situacion", "Estado:100");
        infoConstruccionTabla.put("fecha","Fecha creacion:110");
        
        ModelAndView x = new ModelAndView("cotizaciones/startup", "title", "Cotizaciones");
        
        
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
        
        
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+cliente+"___"+fecha_inicial+"___"+fecha_final;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCotdao().countAll(folio,cliente,fecha_inicial,fecha_final);
        
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCotdao().getPage(folio,cliente,fecha_inicial,fecha_final, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        
        return jsonretorno;
        
    }
    
    
   
    @RequestMapping(method = RequestMethod.POST, value="/getCotizacion.json")
    //public @ResponseBody HashMap<java.lang.String,java.lang.Object> getProveedorJson(
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getCotizacionJson(
            @RequestParam(value="id_cotizacion", required=true) String id_cotizacion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getProveedorJson de {0}", CotizacionesController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> datosCotizacion = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> datosGrid = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> valorIva = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> monedas = new ArrayList<HashMap<String, Object>>();
        
        if( (id_cotizacion.equals("0"))==false  ){
            datosCotizacion = this.getCotdao().getCotizacion(Integer.parseInt(id_cotizacion));
            datosGrid = this.getCotdao().getDatosGrid(Integer.parseInt(id_cotizacion));
        }
        
        valorIva= this.getCotdao().getValoriva();
        monedas = this.getCotdao().getMonedas();
        
        jsonretorno.put("datosCotizacion", datosCotizacion);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("iva", valorIva);
        jsonretorno.put("Monedas", monedas);
        
        return jsonretorno;
    }
    
    
    
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_clientes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> get_buscador_clientesJson(
            @RequestParam(value="cadena", required=true) String cadena,
            @RequestParam(value="filtro", required=true) Integer filtro,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("clientes", this.getCotdao().get_buscador_clientes(cadena,filtro,id_empresa,id_sucursal));
        
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
        arrayTiposProducto=this.getCotdao().getProductoTipos();
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
        
        jsonretorno.put("productos", this.getCotdao().getBuscadorProductos(sku,tipo,descripcion,id_empresa));
        
        return jsonretorno;
    }
    
    
    //Buscador de presentaciones de producto
    @RequestMapping(method = RequestMethod.POST, value="/get_presentaciones_producto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> get_presentaciones_productoJson(
            @RequestParam(value="sku", required=true) String sku,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        jsonretorno.put("Presentaciones", this.getCotdao().get_presentaciones_producto(sku));
        
        return jsonretorno;
    }
    
    
    
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="id_cotizacion", required=true) String id_cotizacion,
            @RequestParam(value="id_cliente", required=true) String id_cliente,
            @RequestParam(value="moneda", required=true) String moneda,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="total_tr", required=true) String total_tr,
            @RequestParam(value="eliminado", required=true) String[] eliminado,
            @RequestParam(value="iddetalle", required=true) String[] iddetalle,
            @RequestParam(value="idproducto", required=true) String[] idproducto,
            @RequestParam(value="id_presentacion", required=true) String[] id_presentacion,
            @RequestParam(value="id_impuesto", required=true) String id_impuesto,
            @RequestParam(value="cantidad", required=true) String[] cantidad,
            @RequestParam(value="costo", required=true) String[] costo,
            @RequestParam(value="monedagrid", required=true) String[] monedagrid,
            Model model
            ) {
            
            String arreglo[];
            arreglo = new String[eliminado.length];
            Integer id_usuario=0;//variable para el id del usuario
            
            for(int i=0; i<eliminado.length; i++) { 
                //Imprimir el contenido de cada celda 
                arreglo[i]= "'"+eliminado[i] +"___" + iddetalle[i] +"___" + idproducto[i] +"___" + id_presentacion[i] +"___" + id_impuesto +"___" + cantidad[i] +"___" + costo[i] +"___" + monedagrid[i]+"'";
            }
            
            
            //serializar el arreglo
            String string_array = StringUtils.join(arreglo, ",");
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            
            HashMap<String, String> succes = new HashMap<String, String>();
            
            String data_string = id_cliente+"___"+observaciones.toUpperCase()+"___"+id_usuario;
            
            
            succes = this.getCotdao().selectFunctionValidateAaplicativo(data_string,12,string_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            int actualizo = 0;
            
            if( String.valueOf(succes.get("success")).equals("true") && id_cotizacion.equals("0") ){
                actualizo = this.getCotdao().selectFunctionForThisApp(Integer.parseInt(id_cotizacion), data_string, "new",string_array);
            }else{
                if(String.valueOf(succes.get("success")).equals("true")){
                    actualizo = this.getCotdao().selectFunctionForThisApp(Integer.parseInt(id_cotizacion), data_string, "edit",string_array);
                }
            }
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    //cambiar a borrado logico un registro
    @RequestMapping(method = RequestMethod.POST, value="/logicDelete.json")
    public @ResponseBody HashMap<String, String> logicDeleteJson(
            @RequestParam(value="id_cotizacion", required=true) String id_cotizacion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        //$id,"", "delete"
        String string_array ="'sin datos'";
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        jsonretorno.put("success",String.valueOf(this.getCotdao().selectFunctionForThisApp(Integer.parseInt(id_cotizacion), "", "delete",string_array)));
        return jsonretorno;
    }
    
    
    
    
    
    /*
     * Agregar modificaciones en header.vm para datepicker
     * modificar color del fondo del datepicker en css
     * index.js
     * controller
     * pdfcotizacion
     * cotizacionespringdao
     */
    
    
    
    
    //Genera pdf de la cotizacion
    @RequestMapping(value = "/get_genera_pdf_cotizacion/{id_cotizacion}/{seleccionado}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView get_genera_pdf_cotizacionJson(
            @PathVariable("id_cotizacion") String id_cotizacion, 
            @PathVariable("seleccionado") String seleccionado,
            @PathVariable("iu") String id_user,
            HttpServletRequest request, HttpServletResponse response, Model model)
            throws ServletException, IOException, URISyntaxException {
        
       
        System.out.println(seleccionado);
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> datos_empresa = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        /*String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";*/
        
        String rfc_empresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        String ruta_imagen = this.getGralDao().getImagesDir()+rfc_empresa+"_logo.png";
        
        datos_empresa.put("emp_razon_social", razon_social_empresa);
        datos_empresa.put("emp_rfc", this.getGralDao().getRfcEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_calle", this.getGralDao().getCalleDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_no_exterior", this.getGralDao().getNoExteriorDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_colonia", this.getGralDao().getColoniaDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_pais", this.getGralDao().getPaisDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_estado", this.getGralDao().getEstadoDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_municipio", this.getGralDao().getMunicipioDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_cp", this.getGralDao().getCpDomicilioFiscalEmpresaEmisora(id_empresa));
        
        
        this.getCotdao().getDatosEmpresaPdf(datos_empresa);
        //String razonSocial = this.getCdao().getEmp_RazonSocial();
        //System.out.println("Esta es la razon social: "+razonSocial);
        
        
       
        
        System.out.println("Ruta absoluta de imagen: "+ruta_imagen);
        
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        //String path_to_file = new OsVars().getTmpDir();
        //System.out.println("Path file:"+ path_to_file);
        //Random rnmd5 = new Random();
        //genera numero aleatorio y se convierte a cadena
        //String ValorRand = String.format("%s", rnmd5.nextInt(999999990));
        //String ramdom_file_md5 = generaMD5.MD5(ValorRand);
        //String fileout = path_to_file + ramdom_file_md5 +".pdf";
        
         String file_name = "FAC_COM_"+rfc_empresa+".pdf";
        
       // String file_name = company_name+"_cotizacion.pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        if(seleccionado.equals("1")){
            //instancia a la clase que construye el pdf de la cotizacion
            String dir_imagen_producto = this.getGralDao().getImagesDir()+rfc_empresa+"/";
            
            PDFCotizacionDescripcion x = new PDFCotizacionDescripcion( this.getCotdao(), fileout, ruta_imagen,seleccionado ,dir_imagen_producto);
            this.getCotdao().getDatosCotizacionDescripcionPdf(Integer.parseInt(id_cotizacion)); 
            
            System.out.println("mandando la ruta desde el controller"+dir_imagen_producto);
        }else{
            
            pdfCotizacion y= new pdfCotizacion( this.getCotdao(),fileout,ruta_imagen);
            this.getCotdao().getDatosCotizacionPdf(Integer.parseInt(id_cotizacion)); 
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
        
        return null;
        
    } 
    
    
}