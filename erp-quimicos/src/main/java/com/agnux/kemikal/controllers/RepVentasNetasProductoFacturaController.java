/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;


import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxcInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.PdfRepVentasNetasProductoFactura;
import com.agnux.kemikal.reportes.PdfReporteVentasNetasProductoFacturados;
import com.agnux.kemikal.reportes.PdfReporteVentasNetasSumatoriaxClientes;
import com.agnux.kemikal.reportes.PdfReporteVentasNetasSumatoriaxProducto;
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
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/repventasnetasproductofactura/")
public class RepVentasNetasProductoFacturaController {
    private static final Logger log  = Logger.getLogger(RepPronosCobranzaController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoCxc")
    private CxcInterfaceDao cxcDao;

    public CxcInterfaceDao getCxcDao() {
        return cxcDao;
    }

    public void setCxcDao(CxcInterfaceDao cxcDao) {
        this.cxcDao = cxcDao;
    }
    
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
        
       
        log.log(Level.INFO, "Ejecutando starUp de {0}", RepVentasNetasProductoFacturaController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        
        ModelAndView x = new ModelAndView("repventasnetasproductofactura/startup", "title", "Ventas Netas por producto desglosado por factura");
        
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
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("Clientes", this.getCxcDao().getBuscadorClientes(cadena,filtro,id_empresa, id_sucursal));
        
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
        arrayTiposProducto=this.getCxcDao().getProductoTipos();
        jsonretorno.put("prodTipos", arrayTiposProducto);
        
        return jsonretorno;
    }
    
    
    
    //cargando filtros
    @RequestMapping(method = RequestMethod.POST, value="/get_cargando_filtros.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> get_cargando_filtrosJson(
            @RequestParam(value="linea", required=true) Integer id_linea,
            @RequestParam(value="marca", required=true) Integer id_marca,
            @RequestParam(value="familia", required=true) Integer id__familia,
            @RequestParam(value="subfamilia", required=true) Integer id_subfamilia,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        ArrayList<HashMap<String, String>> arraylineas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayMarcas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayFamilia = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arraySubfamilia = new ArrayList<HashMap<String, String>>();
        
        if(id__familia != 0){
          arraySubfamilia=this.getCxcDao().getSubfamilias(id__familia);  
            
        }
        
        
        arraylineas=this.getCxcDao().getLineas();
        arrayMarcas=this.getCxcDao().getMarcas();
        arrayFamilia=this.getCxcDao().getFamilias();
        
        
        
        jsonretorno.put("lineas", arraylineas);
        jsonretorno.put("marcas", arrayMarcas);
        jsonretorno.put("familias", arrayFamilia);
        jsonretorno.put("subfamilias", arraySubfamilia);
        
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
        
        jsonretorno.put("productos", this.getCxcDao().getBuscadorProductos(sku,tipo,descripcion, id_empresa));
        return jsonretorno;
    }
    //fin del buscador de productos
    
    
    
    
    
    //obtiene datos para el sqlquery
    @RequestMapping(value="/getVentasNetasProductoFactura/out.json", method = RequestMethod.POST)
    public @ResponseBody ArrayList<HashMap<String, String>> getVentasNetasProductoFactura(
            @RequestParam(value="tipo_reporte", required=true) Integer tipo_reporte,
            @RequestParam(value="tipo_costo", required=true) Integer tipo_costo,
            @RequestParam(value="cliente", required=true) String cliente,
            @RequestParam(value="producto", required=true) String producto,
            @RequestParam(value="fecha_inicial", required=true) String fecha_inicial, 
            @RequestParam(value="fecha_final", required=true) String fecha_final,
            @RequestParam(value="linea", required=true) Integer id_linea,
            @RequestParam(value="marca", required=true) Integer id_marca,
            @RequestParam(value="familia", required=true) Integer id_familia,
            @RequestParam(value="subfamilia", required=true) Integer id_subfamilia,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            /*cliente	
            familia	0
            fecha_final	2013-01-01
            fecha_inicial	2012-12-01
            iu	MQ==
            linea	0
            marca	0
            producto	
            subfamilia	0
            tipo_reporte	1*/
            ) {
        log.log(Level.INFO, "Ejecutando getVentasNetasProductoFactura de {0}", RepVentasNetasProductoFacturaController.class.getName());
        //HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        ArrayList<HashMap<String, String>> z = this.getCxcDao().getVentasNetasProductoFactura(tipo_reporte, cliente, producto, fecha_inicial, fecha_final, id_empresa,id_linea, id_marca, id_familia, id_subfamilia,tipo_costo);
        
        
        return z;    
    
    }
 
    
    
    
    //reporte de ventas netas por Cliente/producto desglosado por factura
     @RequestMapping(value = "/getrepventasnetasproductofactura/{cadena}/out.json", method = RequestMethod.GET ) 
     public ModelAndView PdfVentasNetasProductoFactura(
                @PathVariable("cadena") String cadena,
                HttpServletRequest request,
                HttpServletResponse response, 
                Model model)
     throws ServletException, IOException, URISyntaxException, DocumentException {
         //               1               2             3                4                   5              6           7            8              9                 10
         //cadena = tipo_reporte+"___"+cliente+"___"+producto+"___"+fecha_inicial+"___"+fecha_final+"___"+linea+"___"+marca+"___"+familia+"___"+subfamilia+"___"+tipo_costo+"___"+usuario
    
     String arreglo[];
     arreglo = cadena.split("___");
         
         
     HashMap<String, String> userDat = new HashMap<String, String>();
        
        System.out.println("Generando reporte de Ventas Netas por producto factura");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(arreglo[5]));
        System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        //String ruta_imagen = this.getPgdao().getImagesDir() +"logo_"+ company_name +".png";
        
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        String file_name = "";
        if(Integer.parseInt(arreglo[0]) == 1){
            file_name = "VentasNetas_x_cliente.pdf";
        }
        if(Integer.parseInt(arreglo[0]) == 4){
            file_name = "VentasNetas_sumarizado_x_cliente.pdf";
        }
        if(Integer.parseInt(arreglo[0]) == 2){
            file_name = "VentasNetas_x_producto.pdf";
        }
        
         if(Integer.parseInt(arreglo[0]) == 3){
            file_name = "VentasNetas_sumarizado_x_producto.pdf";
        }
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        //String fileout = "C:\\Users\\micompu\\Desktop\\mi reporte de clientes sumarizados.pdf";
        ArrayList<HashMap<String, String>> lista_ventasporproducto = new ArrayList<HashMap<String, String>>();
        
        //obtiene los informacion de ventas  del periodo indicado
        
        lista_ventasporproducto = this.getCxcDao().getVentasNetasProductoFactura(Integer.parseInt(arreglo[0]), arreglo[1],arreglo[2], arreglo[3], arreglo[4], id_empresa,Integer.parseInt(arreglo[6]),Integer.parseInt(arreglo[7]),Integer.parseInt(arreglo[8]),Integer.parseInt(arreglo[9]),Integer.parseInt(arreglo[10]) );
        //[0]tipo_reporte+"___"+[1]cliente+"___"+[2]producto+"___"+[3]fecha_inicial+"___"+[4]fecha_final+"___"+[5]usuario
        if(Integer.parseInt(arreglo[0])==1){
            //instancia a la clase que construye el pdf  del reporte ventas netas por cliente
            PdfRepVentasNetasProductoFactura x = new PdfRepVentasNetasProductoFactura(lista_ventasporproducto,arreglo[3],arreglo[4],razon_social_empresa,fileout);
        }
        
        if(Integer.parseInt(arreglo[0])==4){
            //instancia a la clase que construye el pdf ventas netas Sumarizado por Cliente
            PdfReporteVentasNetasSumatoriaxClientes x = new PdfReporteVentasNetasSumatoriaxClientes(lista_ventasporproducto,arreglo[2],arreglo[3],arreglo[4],razon_social_empresa,fileout);
        }
        
        if(Integer.parseInt(arreglo[0])==2){
            //instancia a la clase que construye el pdf ventas netas por Producto
            PdfReporteVentasNetasProductoFacturados x = new PdfReporteVentasNetasProductoFacturados(lista_ventasporproducto,arreglo[3],arreglo[4],razon_social_empresa,fileout);
        }
        if(Integer.parseInt(arreglo[0])==3){
            //instancia a la clase que construye el pdf ventas netas Sumarizado por Producto
            PdfReporteVentasNetasSumatoriaxProducto x = new PdfReporteVentasNetasSumatoriaxProducto(lista_ventasporproducto,arreglo[1],arreglo[3],arreglo[4],razon_social_empresa,fileout);
        }
        
        
        
        //System.out.println("Recuperando archivo: " + fileout);
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
