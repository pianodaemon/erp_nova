/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;
import com.agnux.cfd.v2.ArchivoInformeMensual;
import com.agnux.cfd.v2.Base64Coder;
import com.agnux.cfd.v2.BeanFacturador;
import com.agnux.cfdi.BeanFacturadorCfdi;
import com.agnux.cfdi.adendas.AdendaCliente;
import com.agnux.cfdi.timbre.BeanFacturadorCfdiTimbre;
import com.agnux.common.helpers.FileHelper;
import com.agnux.kemikal.interfacedaos.PrefacturasInterfaceDao;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.FacturasInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.pdfCfd_CfdiTimbrado;
import com.agnux.kemikal.reportes.pdfCfd_CfdiTimbradoFormato2;
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
import javax.servlet.http.HttpSession;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 *
 * @author Noe Martinez
 * gpmarsan@gmail.com
 * 17/febrero/2014
 * 
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/facnomina/")
public class FacNominaController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(FacNominaController.class.getName());
    
    @Autowired
    @Qualifier("daoFacturas")
    private FacturasInterfaceDao facdao;
        
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public FacturasInterfaceDao getFacdao() {
        return facdao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
        )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", FacNominaController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("Periodo", "periodo:320");
        infoConstruccionTabla.put("total", "Monto:100");
        infoConstruccionTabla.put("estado", "Estado:100");
        infoConstruccionTabla.put("fecha_creacion","Fecha creacion:110");
        
        ModelAndView x = new ModelAndView("facnomina/startup", "title", "Nomina");
        
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
    
    
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getNomina.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getNominaJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        log.log(Level.INFO, "Ejecutando getPrefacturaJson de {0}", PrefacturasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> datos = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> datosGrid = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> monedas = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> metodos_pago = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> periodicidad_pago = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> parametros = new ArrayList<HashMap<String, Object>>();
        
        ArrayList<HashMap<String,Object>>puestos=new ArrayList<HashMap<String,Object>>();
        ArrayList<HashMap<String,Object>>departamentos=new ArrayList<HashMap<String,Object>>();
        ArrayList<HashMap<String,Object>> regimen_contratacion=new ArrayList<HashMap<String,Object>>();
        ArrayList<HashMap<String,Object>> tipo_contrato=new ArrayList<HashMap<String,Object>>();
        ArrayList<HashMap<String,Object>> tipo_jornada=new ArrayList<HashMap<String,Object>>();
        ArrayList<HashMap<String,Object>> riesgo_puesto=new ArrayList<HashMap<String,Object>>();
        ArrayList<HashMap<String,Object>> bancos=new ArrayList<HashMap<String,Object>>();
        ArrayList<HashMap<String,Object>> percepciones=new ArrayList<HashMap<String,Object>>();
        ArrayList<HashMap<String,Object>> deducciones=new ArrayList<HashMap<String,Object>>();
        
        ArrayList<HashMap<String, Object>> arrayExtra = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> extra = new HashMap<String, Object>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        
        
        
        
        ArrayList<HashMap<String, Object>> datosAdenda = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> valorIva = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> vendedores = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> condiciones = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> almacenes = new ArrayList<HashMap<String, Object>>();
        
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        
        if( identificador!=0  ){
            datos = this.getFacdao().getFacNomina_Datos(identificador);
            datosGrid = this.getFacdao().getFacNomina_Grid(identificador);
        }else{
            //Aquí solo entra cuando es nuevo
            //Obtiene los datos del Emisor y los almacena en el HashMap estra
            extra = this.getFacdao().getFacNomina_DatosEmisor(id_empresa);
            
            /*
            Es necesario conocer el id que tomará el registro antes guardar, 
            para eso se utiliza el metodo getIdSeqFacNomina para apartar el siguiente id de la secuencia,
            esto porque la nomina se de cada empleado se guardará uno por uno cada vez que se editen los datos y al guardar ya tenemos que conocer el id de la tabla header(fac_nomina).
            */
            //Agregar el nuevo ID para la tabla fac_nomina
            extra.put("identificador", this.getFacdao().getIdSeqFacNomina());
            
            arrayExtra.add(extra);
            
            //Obtiene parametros
            parametros = this.getFacdao().getFacNomina_Parametros(id_empresa, id_sucursal);
        }
        
        
        
        jsonretorno.put("Datos", datos);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("Monedas", this.getFacdao().getFactura_Monedas());
        jsonretorno.put("MetodosPago", this.getFacdao().getMetodosPago());
        jsonretorno.put("Periodicidad", this.getFacdao().getFacNomina_PeriodicidadPago(id_empresa));
        jsonretorno.put("Puestos", this.getFacdao().getFacNomina_Puestos(id_empresa));
        jsonretorno.put("Deptos", this.getFacdao().getFacNomina_Departamentos(id_empresa));
        jsonretorno.put("RegimenContrato", this.getFacdao().getFacNomina_RegimenContratacion());
        jsonretorno.put("TipoContrato",this.getFacdao().getFacNomina_TiposContrato());
        jsonretorno.put("TipoJornada",this.getFacdao().getFacNomina_TiposJornada());
        jsonretorno.put("Riesgos",this.getFacdao().getFacNomina_RiesgosPuesto());
        jsonretorno.put("Bancos",this.getFacdao().getFacNomina_Bancos(id_empresa));
        jsonretorno.put("ImpuestoRet",this.getFacdao().getFacNomina_ISR(id_empresa));
        jsonretorno.put("TiposHrsExtra",this.getFacdao().getFacNomina_TiposHoraExtra());
        jsonretorno.put("TiposIncapacidad",this.getFacdao().getFacNomina_TiposIncapacidad());
        
        jsonretorno.put("Percepciones",this.getFacdao().getFacNomina_Percepciones(0, id_empresa));
        jsonretorno.put("Deducciones",this.getFacdao().getFacNomina_Deducciones(0, id_empresa));
        
        jsonretorno.put("Par", parametros);
        jsonretorno.put("Extra", arrayExtra);
        
        
        
        jsonretorno.put("datosAdenda", datosAdenda);
        jsonretorno.put("iva", valorIva);
        jsonretorno.put("Vendedores", vendedores);
        jsonretorno.put("Condiciones", condiciones);
        jsonretorno.put("Almacenes", almacenes);
        
        return jsonretorno;
    }
    
    
    
    
    //Obtiene todos los empleados que se les paga en el periodo indicado
    @RequestMapping(method = RequestMethod.POST, value="/getEmpleados.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getEmpleadosJson(
            @RequestParam(value="id", required=true) Integer periodicidad_id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("Empleados", this.getFacdao().getFacNomina_Empleados(id_empresa, periodicidad_id));
        
        return jsonretorno;
    }
    
    
    //Obtiene todos los periodos de un Tipo de Periodicidad
    @RequestMapping(method = RequestMethod.POST, value="/getPeriodosPorTipoPeridicidad.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getPeriodosPorTipoPeridicidadJson(
            @RequestParam(value="id", required=true) Integer periodicidad_id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("Periodos", this.getFacdao().getFacNomina_PeriodosPorTipo(periodicidad_id, id_empresa));
        
        return jsonretorno;
    }
    
    
    
    //Obtiene datos de la Nomina de un Empleado
    @RequestMapping(method = RequestMethod.POST, value="/getDataNominaEmpleado.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDataNominaEmpleadoJson(
            @RequestParam(value="id_reg", required=true) Integer id_reg,
            @RequestParam(value="id_empleado", required=true) Integer id_empleado,
            @RequestParam(value="id_periodo", required=true) Integer id_periodo,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if(id_reg!=0){
            //Editar. Obtener datos de tabla de Nomina
            //jsonretorno.put("Data", this.getFacdao().getFacNomina_DataNomina(id_reg, id_empleado));
            
        }else{
            //Nuevo. Obtener datos de tabla de empleados
            jsonretorno.put("Data", this.getFacdao().getFacNomina_DataEmpleado(id_empleado));
            jsonretorno.put("Periodo", this.getFacdao().getFacNomina_DataPeriodo(id_periodo, id_empresa));
            //Obtener las percepciones configuradas en el catalogo de empleados
            jsonretorno.put("PercepEmpleado", this.getFacdao().getFacNomina_Percepciones(id_empleado, id_empresa));
            jsonretorno.put("DeducEmpleado", this.getFacdao().getFacNomina_Deducciones(id_empleado, id_empresa));
        }
        
        
        
        return jsonretorno;
    }
    
    
    
    
    //Edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="accion", required=true) String accion,
            @RequestParam(value="comp_tipo", required=true) String comp_tipo,
            @RequestParam(value="comp_forma_pago", required=true) String comp_forma_pago,
            @RequestParam(value="comp_tc", required=true) String comp_tc,
            @RequestParam(value="comp_no_cuenta", required=true) String comp_no_cuenta,
            @RequestParam(value="fecha_pago", required=true) String fecha_pago,
            @RequestParam(value="select_comp_metodo_pago", required=true) String select_comp_metodo_pago,
            @RequestParam(value="select_comp_moneda", required=true) String select_comp_moneda,
            @RequestParam(value="select_comp_periodicidad", required=true) String select_comp_periodicidad,
            @RequestParam(value="select_no_periodo", required=true) String select_no_periodo,
            
            
            @RequestParam(value="orden_compra", required=true) String orden_compra,
            @RequestParam(value="refacturar", required=true) String refacturar,
            @RequestParam(value="select_metodo_pago", required=true) String id_metodo_pago,
            @RequestParam(value="no_cuenta", required=false) String no_cuenta,
            @RequestParam(value="folio_pedido", required=false) String folio_pedido,
            @RequestParam(value="tasa_ret_immex", required=false) String tasa_ret_immex,
            @RequestParam(value="select_almacen", required=false) String select_almacen,
            
            
            @RequestParam(value="eliminado", required=false) String[] eliminado,
            @RequestParam(value="iddetalle", required=false) String[] iddetalle,
            @RequestParam(value="idproducto", required=false) String[] idproducto,
            @RequestParam(value="idUnidad", required=false) String[] idUnidad,
            @RequestParam(value="id_presentacion", required=false) String[] id_presentacion,
            @RequestParam(value="id_imp_prod", required=false) String[] id_impuesto,
            @RequestParam(value="valor_imp", required=false) String[] valor_imp,
            @RequestParam(value="cantidad", required=false) String[] cantidad,
            @RequestParam(value="costo_promedio", required=false) String[] costo_promedio,
            @RequestParam(value="costo", required=false) String[] costo,
            @RequestParam(value="idIeps", required=false) String[] idIeps,
            @RequestParam(value="tasaIeps", required=false) String[] tasaIeps,
            
            @RequestParam(value="id_remision", required=false) String[] id_remision,
            @RequestParam(value="id_df", required=false) String id_df,
            @ModelAttribute("user") UserSessionData user
        ) throws Exception {
        
        //System.out.println(TimeHelper.getFechaActualYMDH()+": INICIO------------------------------------");
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        HashMap<String, String> parametros = new HashMap<String, String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        HashMap<String,String> dataFacturaCliente = new HashMap<String,String>();
        ArrayList<LinkedHashMap<String,String>> conceptos = new ArrayList<LinkedHashMap<String,String>>();
        ArrayList<LinkedHashMap<String,String>> impTrasladados = new ArrayList<LinkedHashMap<String,String>>();
        ArrayList<LinkedHashMap<String,String>> impRetenidos = new ArrayList<LinkedHashMap<String,String>>();
        LinkedHashMap<String,String> datosExtrasXmlFactura = new LinkedHashMap<String,String>();
        LinkedHashMap<String,Object> dataAdenda = new LinkedHashMap<String,Object>();
        ArrayList<HashMap<String, String>> ieps = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> iva = new ArrayList<HashMap<String, String>>();
        LinkedHashMap<String,String> datosExtrasCfdi = new LinkedHashMap<String,String>();
        ArrayList<LinkedHashMap<String,String>> listaConceptosCfdi = new ArrayList<LinkedHashMap<String,String>>();
        ArrayList<LinkedHashMap<String,String>> impTrasladadosCfdi = new ArrayList<LinkedHashMap<String,String>>();
        ArrayList<LinkedHashMap<String,String>> impRetenidosCfdi = new ArrayList<LinkedHashMap<String,String>>();
        ArrayList<String> leyendas = new ArrayList<String>();
        
        HashMap<String,String> datos_emisor = new HashMap<String,String>();
        ArrayList<HashMap<String, String>> listaConceptosPdfCfd = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datosExtrasPdfCfd= new HashMap<String, String>();
        
        String valorRespuesta="false";
        String msjRespuesta="";
        Integer app_selected = 13;
        String command_selected = "";
        String actualizo = "0";
        String retorno="";
        String tipo_facturacion="";
        String folio="";
        String serieFolio="";
        String rfcEmisor="";
        Integer id_factura=0;
        //Variable para el id  del usuario
        Integer id_usuario= user.getUserId();
        
        //Variable que indica si terminó bien el proceso de agregar la Adenda, por default es verdadero, si ocurre algu problema en el proceso se le asigna un false.
        boolean procesoAdendaCorrecto=true;
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        String arreglo[];
        arreglo = new String[eliminado.length];
        
        for(int i=0; i<eliminado.length; i++) {
            arreglo[i]= "'"+eliminado[i] +"___" + iddetalle[i] +"___" + idproducto[i] +"___" + id_presentacion[i] +"___" + id_impuesto[i] +"___" + cantidad[i] +"___" + StringHelper.removerComas(costo[i]) + "___"+valor_imp[i]+"___" + id_remision[i]+"___"+costo_promedio[i]+"___"+idUnidad[i] + "___" + idIeps[i] + "___" + tasaIeps[i] +"'";
            //arreglo[i]= "'"+eliminado[i] +"___" + iddetalle[i] +"___" + idproducto[i] +"___" + id_presentacion[i] +"___" + id_impuesto +"___" + cantidad[i] +"___" + costo[i]+"'";
            //System.out.println(arreglo[i]);
        }
        
        //serializar el arreglo
        String extra_data_array = StringUtils.join(arreglo, ",");
        
        command_selected = accion;
        
        if (no_cuenta==null){
            no_cuenta="";
        }
        
        if(id_df.equals("0")){
            id_df="1";
        }
        
        
        //System.out.println("data_string: "+data_string);
        //String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_prefactura+"___"+id_cliente+"___"+id_moneda+"___"+observaciones.toUpperCase()+"___"+tipo_cambio_vista+"___"+id_vendedor+"___"+id_condiciones+"___"+orden_compra.toUpperCase()+"___"+refacturar+"___"+id_metodo_pago+"___"+no_cuenta+"___"+select_tipo_documento+"___"+folio_pedido+"___"+select_almacen+"___"+id_moneda_original+"___"+id_df+"___"+campo_adenda1.toUpperCase()+"___"+campo_adenda2.toUpperCase()+"___"+campo_adenda3+"___"+campo_adenda4.toUpperCase()+"___"+campo_adenda5.toUpperCase()+"___"+campo_adenda6.toUpperCase()+"___"+campo_adenda7.toUpperCase()+"___"+campo_adenda8.toUpperCase();
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario;
        //System.out.println("data_string: "+data_string);
        
        //System.out.println(TimeHelper.getFechaActualYMDH()+"::::Inicia Validacion de la Prefactura::::::::::::::::::");
        succes = this.getFacdao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, TimeHelper.getFechaActualYMDH()+"Despues de validacion {0}", String.valueOf(succes.get("success")));
        
        //System.out.println(TimeHelper.getFechaActualYMDH()+": Inicia actualizacion de datos de la prefactura");
        if( String.valueOf(succes.get("success")).equals("true")){
            retorno = this.getFacdao().selectFunctionForFacAdmProcesos(data_string, extra_data_array);
            
            //retorna un 1, si se  actualizo correctamente
            actualizo=retorno.split(":")[0];
            
            jsonretorno.put("actualizo",String.valueOf(actualizo));
        }
        
        
        //System.out.println(TimeHelper.getFechaActualYMDH()+"::Termina Actualizacion de la Prefactura:: "+actualizo);
        /*
        if(actualizo.equals("1")){
            
            if ( !accion.equals("new") ){
                //select_tipo_documento 1=Factura, 3=Factura de Remision
                if(select_tipo_documento==1 || select_tipo_documento==3){
                    System.out.println(TimeHelper.getFechaActualYMDH()+"::::::::::::Iniciando Facturacion:::::::::::::::::..");
                    String proposito = "FACTURA";
                    
                    //obtener tipo de facturacion
                    tipo_facturacion = this.getFacdao().getTipoFacturacion(id_empresa);
                    tipo_facturacion = String.valueOf(tipo_facturacion);
                    
                    //Obtener el numero del PAC para el Timbrado de la Factura
                    String noPac = this.getFacdao().getNoPacFacturacion(id_empresa);
                    
                    //Obtener el Ambiente de Facturacion PRUEBAS ó PRODUCCION, solo aplica para Facturacion por Timbre FIscal(cfditf)
                    String ambienteFac = this.getFacdao().getAmbienteFacturacion(id_empresa);
                    
                    //System.out.println(TimeHelper.getFechaActualYMDH()+"::::::Tipo::"+tipo_facturacion+" | noPac::"+noPac+" | Ambiente::"+ambienteFac);
                    
                    //aqui se obtienen los parametros de la facturacion, nos intersa el tipo de formato para el pdf de la factura
                    parametros = this.getFacdao().getFac_Parametros(id_empresa, id_sucursal);

                    
                    //**********************************************************
                    //tipo facturacion CFDITF(CFDI TIMBRE FISCAL)
                    //**********************************************************
                    if(tipo_facturacion.equals("cfditf")){
                        
                        //Pac 0=Sin PAC, 1=Diverza, 2=ServiSim
                        if(!noPac.equals("0")){
                            //Solo se permite generar Factura para Timbrado con Diverza y ServiSim
                            //System.out.println(TimeHelper.getFechaActualYMDH()+":::::::::::Obteniendo datos para CFDI:::::::::::::::::..");
                            command_selected = "facturar_cfditf";
                            extra_data_array = "'sin datos'";
                            
                            //Obtener los valores del IEPS e IVAque se estan utilizando
                            ieps = this.getFacdao().getIeps(id_empresa);
                            iva = this.getFacdao().getIvas();
                            
                            
                            conceptos = this.getFacdao().getListaConceptosXmlCfdiTf(id_prefactura);
                            impRetenidos = this.getFacdao().getImpuestosRetenidosFacturaXml();
                            impTrasladados = this.getFacdao().getImpuestosTrasladadosFacturaXml(id_sucursal, conceptos, ieps, iva);
                            dataFacturaCliente = this.getFacdao().getDataFacturaXml(id_prefactura);
                            leyendas = this.getFacdao().getLeyendasEspecialesCfdi(id_empresa);
                            
                            //estos son requeridos para cfditf
                            datosExtrasXmlFactura.put("prefactura_id", String.valueOf(id_prefactura));
                            datosExtrasXmlFactura.put("tipo_documento", String.valueOf(select_tipo_documento));
                            datosExtrasXmlFactura.put("moneda_id", id_moneda);
                            datosExtrasXmlFactura.put("usuario_id", String.valueOf(id_usuario));
                            datosExtrasXmlFactura.put("empresa_id", String.valueOf(id_empresa));
                            datosExtrasXmlFactura.put("sucursal_id", String.valueOf(id_sucursal));
                            datosExtrasXmlFactura.put("refacturar", refacturar);
                            datosExtrasXmlFactura.put("app_selected", String.valueOf(app_selected));
                            datosExtrasXmlFactura.put("command_selected", command_selected);
                            datosExtrasXmlFactura.put("extra_data_array", extra_data_array);
                            datosExtrasXmlFactura.put("noPac", noPac);
                            datosExtrasXmlFactura.put("ambienteFac", ambienteFac);
                            
                            
                            //System.out.println(TimeHelper.getFechaActualYMDH()+":::::::::::Inicia BeanFacturador:::::::::::::::::..");
                            //genera xml factura
                            this.getBfCfdiTf().init(dataFacturaCliente, conceptos, impRetenidos, impTrasladados, proposito, datosExtrasXmlFactura, id_empresa, id_sucursal);
                            String timbrado_correcto = this.getBfCfdiTf().start();
                            //System.out.println(TimeHelper.getFechaActualYMDH()+":::::::::::Termina BeanFacturador:::::::::::::::::..");
                            String cadRes[] = timbrado_correcto.split("___");
                            
                            //aqui se checa si el xml fue validado correctamente
                            //si fue correcto debe traer un valor "true", de otra manera trae un error y ppor lo tanto no se genera el pdf
                            if(cadRes[0].equals("true")){
                                //obtiene serie_folio de la factura que se acaba de guardar
                                serieFolio = this.getFacdao().getSerieFolioFacturaByIdPrefactura(id_prefactura, id_empresa);
                                
                                String cadena_original=this.getBfCfdiTf().getCadenaOriginalTimbre();
                                //System.out.println("cadena_original:"+cadena_original);
                                
                                String sello_digital = this.getBfCfdiTf().getSelloDigital();
                                //System.out.println("sello_digital:"+sello_digital);
                                
                                //este es el timbre fiscal, se debe extraer del xml que nos devuelve el web service del timbrado
                                String sello_digital_sat = this.getBfCfdiTf().getSelloDigitalSat();
                                
                                //este es el folio fiscal del la factura timbrada, se obtiene   del xml
                                String uuid = this.getBfCfdiTf().getUuid();
                                String fechaTimbre = this.getBfCfdiTf().getFechaTimbrado();
                                String noCertSAT = this.getBfCfdiTf().getNoCertificadoSAT();
                                
                                //System.out.println(TimeHelper.getFechaActualYMDH()+":::::::::::Inicia construccion de PDF:::::::::::::::::..");
                                
                                //conceptos para el pdfcfd
                                listaConceptosPdfCfd = this.getFacdao().getListaConceptosPdfCfd(serieFolio);
                                
                                //datos para el pdf
                                datosExtrasPdfCfd = this.getFacdao().getDatosExtrasPdfCfd( serieFolio, proposito, cadena_original,sello_digital, id_sucursal);
                                datosExtrasPdfCfd.put("tipo_facturacion", tipo_facturacion);
                                datosExtrasPdfCfd.put("sello_sat", sello_digital_sat);
                                datosExtrasPdfCfd.put("uuid", uuid);
                                datosExtrasPdfCfd.put("fechaTimbre", fechaTimbre);
                                datosExtrasPdfCfd.put("noCertificadoSAT", noCertSAT);
                                
                                //pdf factura
                                if (parametros.get("formato_factura").equals("2")){
                                    pdfCfd_CfdiTimbradoFormato2 pdfFactura = new pdfCfd_CfdiTimbradoFormato2(this.getGralDao(), dataFacturaCliente, listaConceptosPdfCfd, leyendas, datosExtrasPdfCfd, id_empresa, id_sucursal);
                                    pdfFactura.ViewPDF();
                                }else{
                                    pdfCfd_CfdiTimbrado pdfFactura = new pdfCfd_CfdiTimbrado(this.getGralDao(), dataFacturaCliente, listaConceptosPdfCfd, datosExtrasPdfCfd, id_empresa, id_sucursal);
                                }
                                //System.out.println(TimeHelper.getFechaActualYMDH()+":::::::::::Termina construccion de PDF:::::::::::::::::..");
                                
                                
                                
                                jsonretorno.put("folio",serieFolio);
                                valorRespuesta="true";
                                //msjRespuesta=cadRes[1];
                                msjRespuesta = "Se gener&oacute; la Factura: "+serieFolio;
                                if (!procesoAdendaCorrecto){
                                    msjRespuesta = msjRespuesta + ", pero no fue posible agregar la Adenda.\nContacte a Soporte.";
                                }
                            }else{
                                valorRespuesta="false";
                                msjRespuesta=cadRes[1];
                            }
                        }else{
                            valorRespuesta="false";
                            msjRespuesta="No se puede Timbrar la Factura con el PAC actual.\nVerifique la configuraci&oacute;n del tipo de Facturaci&oacute;n y del PAC.";
                        }
                    }
                    
                }else{
                    valorRespuesta="true";
                    msjRespuesta="Se gener&oacute; la Remisi&oacute;n con Folio: "+jsonretorno.get("folio");
                }
                
            }else{
                if (accion.equals("new") ){
                    valorRespuesta="true";
                    msjRespuesta="El registro se gener&oacute; con &eacute;xito, puede proceder a Facturar.";
                }
            }//termina if accion diferente de new
            
            System.out.println("Folio: "+ String.valueOf(jsonretorno.get("folio")));
            
        }else{
            if(actualizo.equals("0")){
                jsonretorno.put("actualizo",String.valueOf(actualizo));
            }
        }
        */
        jsonretorno.put("success",succes.get("success"));
        jsonretorno.put("valor",valorRespuesta);
        jsonretorno.put("msj",msjRespuesta);
        
        System.out.println("Validacion: "+ String.valueOf(jsonretorno.get("success")));
        //System.out.println("Actualizo: "+String.valueOf(jsonretorno.get("actualizo")));
        System.out.println("valorRespuesta: "+String.valueOf(valorRespuesta));
        System.out.println("msjRespuesta: "+String.valueOf(msjRespuesta));
        
        //System.out.println(TimeHelper.getFechaActualYMDH()+": FIN------------------------------------");
        
        return jsonretorno;
    }
    
    
    
    
    
    
}
