/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.cfd.v2.BeanFacturador;
import com.agnux.cfdi.BeanCancelaCfdi;
import com.agnux.cfdi.BeanFacturadorCfdi;
import com.agnux.cfdi.BeanFromCfdiXml;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.FacturasInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.pdfCfd;
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
 * @author Noe Martinez
 * gpmarsan@gmail.com
 * 25/julio/2012
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/notascredito/")
public class NotasCreditoController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(NotasCreditoController.class.getName());
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoFacturas")
    private FacturasInterfaceDao facdao;
    
    @Autowired
    @Qualifier("beanFacturador")
    BeanFacturador bf;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("beanFacturadorCfdi")
    BeanFacturadorCfdi bfcfdi;
    
    @Autowired
    @Qualifier("beanCancelaCfdi")
    BeanCancelaCfdi bcancelafdi;

    public BeanCancelaCfdi getBcancelafdi() {
        return bcancelafdi;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    public FacturasInterfaceDao getFacdao() {
        return facdao;
    }
    
    public BeanFacturador getBf() {
        return bf;
    }
    
    public BeanFacturadorCfdi getBfcfdi() {
        return bfcfdi;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", NotasCreditoController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio", "Folio:80");
        infoConstruccionTabla.put("cliente", "Cliente:300");
        infoConstruccionTabla.put("total", "Monto:90");
        infoConstruccionTabla.put("fecha_expedicion","Fecha Expedici&oacute;n:110");
        infoConstruccionTabla.put("moneda", "Moneda:60");
        infoConstruccionTabla.put("factura", "Factura:80");
        infoConstruccionTabla.put("estado", "Estado:90");
        
        ModelAndView x = new ModelAndView("notascredito/startup", "title", "Notas de Cr&eacute;dito");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("sucursal", user.getSucursal());
        
        String userId = String.valueOf(user.getUserId());
        
        //System.out.println("id_de_usuario: "+userId);
        
        String codificado = Base64Coder.encodeString(userId);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    
    
    
    @RequestMapping(value="/getAllNotasCredito.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllNotasCreditoJson(
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
        
        //aplicativo Notas de Credito
        Integer app_selected = 70;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String cliente = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cliente")))+"%";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+cliente+"___"+fecha_inicial+"___"+fecha_final;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getFacdao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getFacdao().getNotasCredito_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_clientes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> get_buscador_clientesJson(
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
        
        jsonretorno.put("clientes", this.getFacdao().getBuscadorClientes(cadena,filtro,id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getNotaCredito.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getNotaCreditoJson(
            @RequestParam(value="id_nota_credito", required=true) Integer id_nota_credito,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getNotaCreditoJson de {0}", NotasCreditoController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> datosNota = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> valorIva = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> monedas = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> tipoCambioActual = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> tc = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> vendedores = new ArrayList<HashMap<String, Object>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( id_nota_credito != 0  ){
            datosNota = this.getFacdao().getNotasCredito_Datos(id_nota_credito);
        }
        
        valorIva= this.getFacdao().getValoriva(id_sucursal);
        tc.put("tipo_cambio", StringHelper.roundDouble(this.getFacdao().getTipoCambioActual(), 4)) ;
        tipoCambioActual.add(0,tc);
        
        monedas = this.getFacdao().getFactura_Monedas();
        vendedores = this.getFacdao().getFactura_Agentes(id_empresa, id_sucursal);
        
        jsonretorno.put("datosNota", datosNota);
        jsonretorno.put("iva", valorIva);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Tc", tipoCambioActual);
        jsonretorno.put("Vendedores", vendedores);
        
        return jsonretorno;
    }
    
    
    
    
    
    //Buscador de Remisiones del Cliente
    @RequestMapping(method = RequestMethod.POST, value="/getFacturasCliente.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getFacturasClienteJson(
            @RequestParam(value="id_cliente", required=true) Integer id_cliente,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        jsonretorno.put("Facturas", this.getFacdao().getNotasCredito_FacturasCliente(id_cliente));
        
        return jsonretorno;
    }
    
    
    










    
    
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer id_nota_credito,
            @RequestParam(value="id_cliente", required=true) String id_cliente,
            @RequestParam(value="id_impuesto", required=true) String id_impuesto,
            @RequestParam(value="valorimpuesto", required=true) String valor_impuesto,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="select_moneda", required=true) String select_moneda,
            @RequestParam(value="select_vendedor", required=true) String select_vendedor,
            @RequestParam(value="concepto", required=true) String concepto,
            @RequestParam(value="tasa_ret_immex", required=true) String tasa_ret_immex,
            @RequestParam(value="tipo_cambio", required=true) String tipo_cambio,
            @RequestParam(value="importe", required=true) String importe,
            @RequestParam(value="impuesto", required=true) String impuesto,
            @RequestParam(value="retencion", required=true) String retencion,
            @RequestParam(value="total", required=true) String total,
            @RequestParam(value="factura", required=true) String factura,
            @RequestParam(value="generar", required=true) String generar,
            @RequestParam(value="fac_saldado", required=true) String fac_saldado,
            @ModelAttribute("user") UserSessionData user
            ) {
            
            System.out.println("Guardar del Nota de Credito");
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            String tipo_facturacion="";
            
            HashMap<String, String> userDat = new HashMap<String, String>();
            HashMap<String,String> datos_emisor = new HashMap<String,String>();
            
            //variables para xml de Nota de Credito CFD y CFDI
            HashMap<String,String> dataCliente = new HashMap<String,String>();
            ArrayList<LinkedHashMap<String,String>> listaConceptos = new ArrayList<LinkedHashMap<String,String>>();
            ArrayList<LinkedHashMap<String,String>> impTrasladados = new ArrayList<LinkedHashMap<String,String>>();
            ArrayList<LinkedHashMap<String,String>> impRetenidos = new ArrayList<LinkedHashMap<String,String>>();
            LinkedHashMap<String,String> datosExtras = new LinkedHashMap<String,String>();
            
            //variables para Nota de Credito en CFDI
            //LinkedHashMap<String,String> datosExtrasCfdi = new LinkedHashMap<String,String>();
            //ArrayList<LinkedHashMap<String,String>> listaConceptosCfdi = new ArrayList<LinkedHashMap<String,String>>();
            //ArrayList<LinkedHashMap<String,String>> impTrasladadosCfdi = new ArrayList<LinkedHashMap<String,String>>();
            //ArrayList<LinkedHashMap<String,String>> impRetenidosCfdi = new ArrayList<LinkedHashMap<String,String>>();
            ArrayList<String> leyendas = new ArrayList<String>();
            
            //variables para PDF de Nota de Credito CFD
            ArrayList<HashMap<String, String>> listaConceptosPdf = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> datosExtrasPdf= new HashMap<String, String>();
            
            
            Integer app_selected = 70;//aplicativo notas de credito
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            String extra_data_array = "'sin datos'";
            String actualizo = "0";
            String serieFolio="";
            String rfcEmisor="";
            userDat = this.getHomeDao().getUserById(id_usuario);
            Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
            Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
            
            if( id_nota_credito==0 ){
                command_selected = "new";
            }else{
                command_selected = "edit";
            }
            
            
            String data_string = 
                    app_selected+"___"+
                    command_selected+"___"+
                    id_usuario+"___"+
                    id_nota_credito+"___"+
                    id_cliente+"___"+
                    id_impuesto+"___"+
                    valor_impuesto+"___"+
                    observaciones.toUpperCase()+"___"+
                    select_moneda+"___"+
                    select_vendedor+"___"+
                    concepto.toUpperCase()+"___"+
                    tipo_cambio+"___"+
                    importe+"___"+
                    impuesto+"___"+
                    retencion+"___"+
                    total+"___"+
                    factura;
            //System.out.println("data_string: "+data_string);
            
            succes = this.getFacdao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            
            if( String.valueOf(succes.get("success")).equals("true") ){
                actualizo = this.getFacdao().selectFunctionForFacAdmProcesos(data_string, extra_data_array);
                jsonretorno.put("actualizo",String.valueOf(actualizo));
            }
            
            
            
            System.out.println("Actualizo::: "+actualizo);
            
            if(generar.equals("true")){
                
                if(actualizo.equals("1")){
                    System.out.println("::::::::::::Iniciando Generacion de NOTA DE CREDITO:::::::::::::::::..");
                    String proposito = "NOTA_CREDITO";
                    
                    //obtener tipo de facturacion
                    tipo_facturacion = this.getFacdao().getTipoFacturacion();
                    
                    System.out.println("tipo_facturacion:::"+tipo_facturacion);
                    
                    //tipo facturacion CFD
                    if(tipo_facturacion.equals("cfd")){
                        System.out.println("::::::::::::Tipo CFD:::::::::::::::::..");
                        dataCliente = this.getFacdao().getNotaCreditoCfd_Cfdi_Datos(id_nota_credito);
                        listaConceptos = this.getFacdao().getNotaCreditoCfd_ListaConceptosXml(id_nota_credito);
                        impRetenidos = this.getFacdao().getNotaCreditoCfd_ImpuestosRetenidosXml();
                        impTrasladados = this.getFacdao().getNotaCreditoCfd_ImpuestosTrasladadosXml(id_sucursal);
                        
                        command_selected = "genera_nota_credito_cfd";
                        extra_data_array = "'sin datos'";
                        datosExtras = this.getFacdao().getNotaCreditoCfd_DatosExtrasXml(id_nota_credito,tipo_cambio,String.valueOf(id_usuario),select_moneda,id_empresa,app_selected, command_selected, extra_data_array, fac_saldado);
                        
                        
                        //xml factura
                        this.getBf().init(dataCliente, listaConceptos,impRetenidos,impTrasladados , proposito,datosExtras, id_empresa, id_sucursal);
                        this.getBf().start();
                        
                        //obtiene serie_folio de la Nota de Credito que se acaba de guardar
                        serieFolio = this.getFacdao().getSerieFolioNotaCredito(id_nota_credito);
                        
                        String cadena_original=this.getBf().getCadenaOriginal();
                        //System.out.println("cadena_original:"+cadena_original);
                        
                        String sello_digital = this.getBf().getSelloDigital();
                        //System.out.println("sello_digital:"+sello_digital);
                        
                        //conceptos para el pdfcfd
                        listaConceptosPdf = this.getFacdao().getNotaCreditoCfd_ListaConceptosPdf(serieFolio);
                        
                        //datos para el pdf
                        datosExtrasPdf = this.getFacdao().getNotaCreditoCfd_DatosExtrasPdf( serieFolio, proposito, cadena_original,sello_digital, id_sucursal);
                        
                        //pdf factura
                        pdfCfd pdfFactura = new pdfCfd(this.getGralDao(), dataCliente, listaConceptosPdf, datosExtrasPdf, id_empresa, id_sucursal);
                        //pdfFactura.viewPDF();
                        
                        jsonretorno.put("folio",serieFolio);
                        
                    }
                    
                    
                    
                    //tipo facturacion CFDI
                    if(tipo_facturacion.equals("cfdi")){
                        System.out.println("::::::::::::Tipo CFDI:::::::::::::::::..");
                        
                        data_string="";
                        extra_data_array = "'sin datos'";
                        command_selected="genera_nota_credito_cfdi";
                        
                        String Serie=this.getGralDao().getSerieNotaCredito(id_empresa);
                        String Folio=this.getGralDao().getFolioNotaCredito(id_empresa);
                        rfcEmisor = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
                        
                        //lista de conceptos para la Nota de Credito cfdi
                        listaConceptos = this.getFacdao().getNotaCreditoCfdi_ListaConceptos(id_nota_credito);
                        dataCliente = this.getFacdao().getNotaCreditoCfd_Cfdi_Datos(id_nota_credito);
                        
                        //obtiene datos extras para el cfdi
                        datosExtras = this.getFacdao().getNotaCreditoCfdi_DatosExtras(id_nota_credito, Serie, Folio);
                        impTrasladados = this.getFacdao().getNotaCreditoCfdi_ImpuestosTrasladados(id_nota_credito);
                        impRetenidos = this.getFacdao().getNotaCreditoCfdi_ImpuestosRetenidos(id_nota_credito);
                        //leyendas = this.getFacdao().getLeyendasEspecialesCfdi(id_empresa);
                        
                        //generar archivo de texto para cfdi
                        this.getBfcfdi().init(dataCliente, listaConceptos,impRetenidos,impTrasladados, leyendas, proposito,datosExtras, id_empresa, id_sucursal);
                        this.getBfcfdi().start();
                        
                        
                        
                        //aqui se debe actializar el registro
                        data_string = 
                                app_selected+"___"+
                                command_selected+"___"+
                                id_usuario+"___"+
                                id_nota_credito+"___"+
                                Serie+Folio+"___"+
                                fac_saldado;
                       
                        
                        actualizo = this.getFacdao().selectFunctionForFacAdmProcesos(data_string, extra_data_array);
                        
                        //actualiza el folio de la Nota de Credito
                        this.getGralDao().actualizarFolioNotaCredito(id_empresa);
                        
                        jsonretorno.put("folio",Serie+Folio);
                    }//termina tipo CFDI
                    
                    
                    
                    System.out.println("Folio: "+ String.valueOf(jsonretorno.get("folio")));
                    
                }else{
                    if(actualizo.equals("0")){
                        jsonretorno.put("actualizo",String.valueOf(actualizo));
                    }
                }

            
            }//termina if genarar
            
            
            
            
            
            
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    
    
    

    //obtiene los tipos de cancelacion
    @RequestMapping(method = RequestMethod.POST, value="/getVerificaArchivoGenerado.json")
    public @ResponseBody HashMap<String,String> getVerificaArchivoGeneradoJson(
            @RequestParam(value="serie_folio", required=true) String serie_folio,
            @RequestParam(value="ext", required=true) String extension,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getVerificaArchivoGeneradoJson de {0}", NotasCreditoController.class.getName());
        HashMap<String, String> jsonretorno = new HashMap<String,String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        String existe ="false";
        String dirSalidas = "";
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //obtener tipo de facturacion
        String tipo_facturacion = this.getFacdao().getTipoFacturacion();
        
        if(tipo_facturacion.equals("cfd")){
            dirSalidas = this.getGralDao().getCfdEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        }else{
            dirSalidas = this.getGralDao().getCfdiSolicitudesDir() + "out/";
        }
        
        String fileout = dirSalidas +"/"+ serie_folio +"."+extension;
        
        System.out.println("Ruta: " + fileout);
        File file = new File(fileout);
        if (file.exists()){
            existe="true";
        }
        
        jsonretorno.put("descargar", existe);
        
        return jsonretorno;
    }
    
    
    
    
    //cancelacion de Notas de Credito
    @RequestMapping(method = RequestMethod.POST, value="/cancelarNotaCredito.json")
    public @ResponseBody HashMap<String, String> cancelarNotaCreditoJson(
            @RequestParam(value="id_nota", required=true) Integer id_nota,
            @RequestParam(value="motivo", required=true) String motivo_cancelacion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        Integer id_usuario=0;//aqui va el id del usuario
        
        //decodificar id de usuario
        id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        //aplicativo notas de credito
        Integer app_selected = 70;
        String command_selected = "cancelacion";
        String extra_data_array = "'sin datos'";
        String succcess = "false";
        String serie_folio="";
        String tipo_facturacion="";
        
        //obtener tipo de facturacion
        tipo_facturacion = this.getFacdao().getTipoFacturacion();
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_nota+"___"+motivo_cancelacion.toUpperCase()+"___"+tipo_facturacion;
        
        succcess = this.getFacdao().selectFunctionForFacAdmProcesos(data_string, extra_data_array);
        
        System.out.println("tipo_facturacion:::"+tipo_facturacion);
        
        //tipo facturacion CFDI. Generar txt para buzon fiscal
        if(tipo_facturacion.equals("cfdi")){
            
            if(succcess.split(":")[1].equals("true")){
                
                HashMap<String, String> data = new HashMap<String, String>();
                serie_folio = succcess.split(":")[0];
                
                String directorioSolicitudesCfdiOut=this.getGralDao().getCfdiSolicitudesDir() + "out/"+serie_folio+".xml";
                BeanFromCfdiXml pop = new BeanFromCfdiXml(directorioSolicitudesCfdiOut);
                
                data.put("uuid", pop.getUuid());
                data.put("emisor_rfc", pop.getEmisor_rfc());
                data.put("receptor_rfc", pop.getReceptor_rfc());
                
                //generar archivo de texto para cfdi
                this.getBcancelafdi().init(data, serie_folio);
                this.getBcancelafdi().start();
                
                System.out.println("serie_folio:"+serie_folio + "    Cancelado:"+succcess.split(":")[1]);
            }
        }
        
        jsonretorno.put("success", succcess);
        //System.out.println("Success:: "+ succcess);
        return jsonretorno;
    }
    
    
    
    
    
    //Descarga pdf de la Nota de Credito generado anteriormente
    @RequestMapping(value = "/getDescargarPdfNotaCredito/{id_nota_credito}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getDescargaPdfFacturaJson(
            @PathVariable("id_nota_credito") Integer id_nota_credito, 
            @PathVariable("iu") String id_user,
            HttpServletRequest request, HttpServletResponse response, Model model)
            throws ServletException, IOException, URISyntaxException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        String dirSalidas = "";
        String nombre_archivo = "";
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        //obtener tipo de facturacion
        String tipo_facturacion = this.getFacdao().getTipoFacturacion();
        
        if(tipo_facturacion.equals("cfd")){
            dirSalidas = this.getGralDao().getCfdEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        }else{
            dirSalidas = this.getGralDao().getCfdiSolicitudesDir() + "out/";
        }
        
        nombre_archivo = this.getFacdao().getSerieFolioNotaCredito(id_nota_credito);
        
        
        String fileout = dirSalidas + "/" + nombre_archivo +".pdf";
        
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
    
    
    
    
    
    //Descarga xml de la Nota de Credito
    @RequestMapping(value = "/getDescargarXmlNotaCredito/{id_nota_credito}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getDescargaXmlFacturaJson(
            @PathVariable("id_nota_credito") Integer id_nota_credito, 
            @PathVariable("iu") String id_user,
            HttpServletRequest request, 
            HttpServletResponse response, 
            Model model) throws ServletException, IOException, URISyntaxException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        String dirSalidas = "";
        String nombre_archivo = "";
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        
        //obtener tipo de facturacion
        String tipo_facturacion = this.getFacdao().getTipoFacturacion();
        
        if(tipo_facturacion.equals("cfd")){
            dirSalidas = this.getGralDao().getCfdEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        }else{
            dirSalidas = this.getGralDao().getCfdiSolicitudesDir() + "out/";
        }
        
        nombre_archivo = this.getFacdao().getSerieFolioNotaCredito(id_nota_credito);
        
        //ruta completa del archivo a descargar
        String fileout = dirSalidas + "/" + nombre_archivo +".xml";
        
        
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        
        if (file.exists()){
            int size = (int) file.length(); // Tamaño del archivo
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            response.setBufferSize(size);
            response.setContentLength(size);
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition","attachment; filename=\"" + file.getCanonicalPath() +"\"");
            FileCopyUtils.copy(bis, response.getOutputStream());  	
            response.flushBuffer();
            
        }
     
        return null;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
}
