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
        
        HashMap<String, Object> extra = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> arrayExtra = new ArrayList<HashMap<String, Object>>();
        
        
        ArrayList<HashMap<String, Object>> datosAdenda = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> valorIva = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> vendedores = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> condiciones = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> almacenes = new ArrayList<HashMap<String, Object>>();
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
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
        
        monedas = this.getFacdao().getFactura_Monedas();
        metodos_pago = this.getFacdao().getMetodosPago();
        periodicidad_pago = this.getFacdao().getFacNomina_PeriodicidadPago();
        
        
        /*
        valorIva= this.getFacdao().getValoriva(id_sucursal);
        extra.put("tipo_cambio", StringHelper.roundDouble(this.getFacdao().getTipoCambioActual(), 4)) ;
        extra.put("controlExiPres", userDat.get("control_exi_pres"));
        extra.put("validaPresPedido", parametros.get(0).get("validaPresPedido"));
        extra.put("adenda", String.valueOf(incluirAdenda));
        arrayExtras.add(0,extra);
        
        
        monedas = this.getFacdao().getMonedas();
        vendedores = this.getFacdao().getVendedores(id_empresa, id_sucursal);
        condiciones = this.getFacdao().getCondiciones();
        almacenes = this.getFacdao().getAlmacenes(id_empresa);
         */
        
        jsonretorno.put("Datos", datos);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("MetodosPago", metodos_pago);
        jsonretorno.put("Periodicidad", periodicidad_pago);
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
    
    
    
}
