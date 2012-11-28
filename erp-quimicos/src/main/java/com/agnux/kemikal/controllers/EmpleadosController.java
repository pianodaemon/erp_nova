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
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author luis Carrillo
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/empleados/")
public class EmpleadosController {
    private static final Logger log  = Logger.getLogger(EmpleadosController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    public void setGralDao(GralInterfaceDao gralDao) {
        this.gralDao = gralDao;
    }
    
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", EmpleadosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("clave", "No.Empleado:100");
        infoConstruccionTabla.put("nombre_empleado", "Nombre Empleado:250");
        infoConstruccionTabla.put("curp", "CURP:100");
        infoConstruccionTabla.put("titulo","Puesto:150");
        
        
        ModelAndView x = new ModelAndView("empleados/startup", "title", "Cat&aacute;logo de Empleados");
        
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
    
    
    
    
    @RequestMapping(value="/getEmpleados.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getEmpleadosJson(
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
        
        //aplicativo de empleados
        Integer app_selected = 4;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String cad_busqueda = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cadena_busqueda")))+"%";
        String filtro_por = StringHelper.isNullString(String.valueOf(has_busqueda.get("filtro_por")));
        
        String data_string = app_selected+"___"+id_usuario+"___"+cad_busqueda+"___"+filtro_por;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getGralDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getGralDao().getEmpleados_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/get_empleado.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> get_empleadoJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando get_empleadosJson de {0}", EmpleadosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        ArrayList<HashMap<String, Object>> paises = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> entidades = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> localidades = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> datoscliente = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> escolaridad = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> genero = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> civil = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> religion = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String,Object>>tipo_sangre=new ArrayList<HashMap<String,Object>>();
        ArrayList<HashMap<String,Object>>puesto=new ArrayList<HashMap<String,Object>>();
        ArrayList<HashMap<String,Object>>sucursal=new ArrayList<HashMap<String,Object>>();
        ArrayList<HashMap<String,Object>>categoria= new ArrayList<HashMap<String,Object>>();
        ArrayList<HashMap<String,Object>>roles=new ArrayList<HashMap<String,Object>>();
        ArrayList<HashMap<String,Object>>rols_edit=new ArrayList<HashMap<String,Object>>();
        ArrayList<HashMap<String,Object>>region=new ArrayList<HashMap<String,Object>>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        if( id != 0  ){
            datoscliente = this.getGralDao().getEmpleados_Datos(id);
            
            entidades = this.getGralDao().getEntidadesForThisPais(datoscliente.get(0).get("gral_pais_id").toString());
            localidades = this.getGralDao().getLocalidadesForThisEntidad(datoscliente.get(0).get("gral_pais_id").toString(), datoscliente.get(0).get("gral_edo_id").toString());
            categoria=this.getGralDao().getPuestoForCategoria(datoscliente.get(0).get("gral_puesto_id").toString());
            rols_edit=this.getGralDao().getRolsEdit(Integer.parseInt(datoscliente.get(0).get("id_usuario").toString()));
            
        }
        
        //valorIva= this.getCdao().getValoriva();
        
        //monedas = this.getGralDao().getMonedas();
        
        paises = this.getGralDao().getPaises();
        escolaridad=this.getGralDao().getEscolaridad(id_empresa);
        genero=this.getGralDao().getGeneroSexual();
        civil=this.getGralDao().getEdoCivil();
        religion=this.getGralDao().getReligion(id_usuario);
        tipo_sangre=this.getGralDao().getTiposangre(id_empresa);
        puesto=this.getGralDao().getPuesto(id_empresa);
        sucursal=this.getGralDao().getSucursal(id_empresa);
        roles=this.getGralDao().getRoles();
        region=this.getGralDao().getRegion();
        
        jsonretorno.put("Empleados", datoscliente);
        jsonretorno.put("Paises", paises);
        jsonretorno.put("Entidades", entidades);
        jsonretorno.put("Localidades", localidades);
        jsonretorno.put("Escolaridad",escolaridad);
        jsonretorno.put("Genero", genero);
        jsonretorno.put("EdoCivil", civil);
        jsonretorno.put("Religion", religion);
        jsonretorno.put("Sangre",tipo_sangre);
        jsonretorno.put("Puesto",puesto);
        jsonretorno.put("Categoria",categoria);
        jsonretorno.put("Roles",roles);
        jsonretorno.put("Sucursal",sucursal);
        jsonretorno.put("RolsEdit",rols_edit);
        jsonretorno.put("Region",region);
        
        
        return jsonretorno;
    }
    
  
 //obtiene  las localidades de una entidad
    @RequestMapping(method = RequestMethod.POST, value="/getLocalidades.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getLocalidadesJson(
            @RequestParam(value="id_pais", required=true) String id_pais,
            @RequestParam(value="id_entidad", required=true) String id_entidad,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        
        jsonretorno.put("Localidades", this.getGralDao().getLocalidadesForThisEntidad(id_pais, id_entidad));
        
        return jsonretorno;
    }
    
    
    
    //obtiene el las entidades de un pais
    @RequestMapping(method = RequestMethod.POST, value="/getEntidades.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getEntidadesJson(
            @RequestParam(value="id_pais", required=true) String id_pais,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        
        jsonretorno.put("Entidades", this.getGralDao().getEntidadesForThisPais(id_pais));
        
        return jsonretorno;
    }
    
   //obtiene el las categorias de un puesto
    @RequestMapping(method = RequestMethod.POST, value="/getCategorias.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getCategoriasJson(
            @RequestParam(value="id_puesto", required=true) String id_puesto,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        
        jsonretorno.put("Categoria", this.getGralDao().getPuestoForCategoria(id_puesto));
        
        return jsonretorno;
    }
    
   //obtiene los roles de empleados
    @RequestMapping(method=RequestMethod.POST,value="/getRoles.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String,Object>>>getRolesJson(
            @RequestParam(value="id",required=true)String id_rols,
           // @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        HashMap<String,ArrayList<HashMap<String,Object>>>jsonretorno= new HashMap<String,ArrayList<HashMap<String,Object>>>();
        //HashMap<String, String> userDat = new HashMap<String, String>();
       // Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        jsonretorno.put("Roles",this.getGralDao().getRoles());
        
        return jsonretorno;
    }
    
    
    
    //crear y editar un cliente
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador_empleado", required=true) Integer id_empleado,
            //@RequestParam(value="empleado_id", required=true) Integer empleado_id,
            @RequestParam(value="nombre", required=true) String nombre,
            
            @RequestParam(value="appaterno", required=true) String appaterno,
            @RequestParam(value="apmaterno", required=true) String apmaterno,
            @RequestParam(value="imss", required=true) String imss,
            @RequestParam(value="infonavit", required=true) String infonavit,
            @RequestParam(value="curp", required=true) String curp,
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="f_nacimiento", required=true) String f_nacimiento,
            @RequestParam(value="f_ingreso", required=true) String f_ingreso,
            @RequestParam(value="escolaridad", required=true) String escolaridad,
            @RequestParam(value="sexo", required=true) String sexo,
            @RequestParam(value="edocivil", required=true) String edocivil,
            @RequestParam(value="religion", required=true) String religion,
            @RequestParam(value="telefono", required=true) String telefono,
            @RequestParam(value="movil", required=true) String movil,
            @RequestParam(value="correo_personal", required=true) String correo_personal,
            @RequestParam(value="pais", required=true) String pais,
            @RequestParam(value="estado", required=true) String estado,
            @RequestParam(value="municipio", required=true) String municipio,
            @RequestParam(value="calle", required=true) String calle,
            @RequestParam(value="numero", required=true) String numero,
            @RequestParam(value="colonia", required=true) String colonia,
            @RequestParam(value="cp", required=true) String cp,
            @RequestParam(value="contacto", required=true) String contacto,
            @RequestParam(value="telcontacto", required=true) String telcontacto,
            @RequestParam(value="tipo_sangre", required=true) String tipo_sangre,
            @RequestParam(value="enfermedades", required=true) String enfermedades,
            @RequestParam(value="alergias", required=true) String alergias,
            @RequestParam(value="puesto", required=true) String puesto,
            @RequestParam(value="sucursal", required=true) String sucursal,
            @RequestParam(value="categoria", required=true) String categoria,
            @RequestParam(value="comentarios", required=true) String comentarios,
            @RequestParam(value="email_usr", required=true) String email_usr,
            @RequestParam(value="password",required=true)String password,
            @RequestParam(value="verifica_pass",required=true)String verifica_pass,
            @RequestParam(value="permite",required=true)String verifica_acceso,
            @RequestParam(value="seleccionado",required=false)String[] seleccionado,
            @RequestParam(value="id_rol",required=true)String[] id_rols,
            @RequestParam(value="comision",required=true)String comision_agen,
            @RequestParam(value="comision2",required=true)String comision_agen2,
            @RequestParam(value="comision3",required=true)String comision_agen3,
            @RequestParam(value="comision4",required=true)String comision_agen4,
            @RequestParam(value="dias_comision",required=true)String dias_comision_agen,
            @RequestParam(value="dias_comision2",required=true)String dias_comision_agen2,
            @RequestParam(value="dias_comision3",required=true)String dias_comision_agen3,
            @RequestParam(value="region",required=true)String region_agen,
            Model model,@ModelAttribute("user") UserSessionData user
            ) {
            System.out.print("Verifica si entra la deshabilitacion"+verifica_acceso);
            System.out.print("Verificando si se envian"+nombre+appaterno);
            Integer app_selected = 4;
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            String arreglo[];
            String extra_data_array ="";
            String actualizo = "0";
            int contador=0;
            
            for (int i=0; i<seleccionado.length; i++){
                if(seleccionado[i].equals("1")){
                     contador++;
                }
            }
            
            arreglo = new String[contador];
            int contador2=0;
            for(int i=0;i<seleccionado.length;i++){
                if(seleccionado[i].equals("1")){
                    arreglo[contador2]="'"+id_rols[i]+"'";
                    
                    contador2++;
                } 
               
            }
            //serializar el arreglo
            extra_data_array = StringUtils.join(arreglo, ",");
            

            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            
            HashMap<String, String> succes = new HashMap<String, String>();
            
            if( id_empleado == 0 ){
                command_selected = "new";
            }else{
                command_selected = "edit";
            }
            
            System.out.print("Esta imprimiento esto :"+" "+extra_data_array);
            
            String data_string = 
            app_selected //1
            +"___"+command_selected//2
            +"___"+id_usuario//3
            +"___"+id_empleado//4
            +"___"+nombre.toUpperCase()//5
            +"___"+appaterno.toUpperCase()//6
            +"___"+apmaterno.toUpperCase()//7
            +"___"+imss.toUpperCase()//8
            +"___"+infonavit.toUpperCase()//9
            +"___"+curp.toUpperCase()//10
            +"___"+rfc.toUpperCase()//11
            +"___"+f_nacimiento//12
            +"___"+f_ingreso//13
            +"___"+escolaridad//14
            +"___"+sexo//15
            +"___"+edocivil//16
            +"___"+religion//17
            +"___"+telefono//18
            +"___"+movil//19
            +"___"+correo_personal//20
            +"___"+pais//21
            +"___"+estado//22
            +"___"+municipio//23
            +"___"+calle.toUpperCase()//24
            +"___"+numero.toUpperCase()//25
            +"___"+colonia.toUpperCase()//26
            +"___"+cp//27
            +"___"+contacto.toUpperCase()//28
            +"___"+telcontacto//29
            +"___"+tipo_sangre//30
            +"___"+enfermedades.toUpperCase()//31
            +"___"+alergias.toUpperCase()//32
            +"___"+puesto//33
            +"___"+sucursal//34
            +"___"+categoria//35
            +"___"+comentarios.toUpperCase()//36
            +"___"+email_usr//37
            +"___"+password//38
            +"___"+verifica_pass//39
            +"___"+verifica_acceso//40
            +"___"+comision_agen//41
            +"___"+comision_agen2//42
            +"___"+comision_agen3//43
            +"___"+comision_agen4//44
            +"___"+dias_comision_agen//45
            +"___"+dias_comision_agen2//46
            +"___"+dias_comision_agen3//47
            +"___"+region_agen;//48
            
            
           
            
            
            System.out.println("data_string: "+data_string);
            
            succes = this.getGralDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            if( String.valueOf(succes.get("success")).equals("true") ){
                actualizo = this.getGralDao().selectFunctionForThisApp(data_string, extra_data_array);
            }
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    
    //cambiar a borrado logico un registro
    @RequestMapping(method = RequestMethod.POST, value="/logicDelete.json")
    public @ResponseBody HashMap<String, String> logicDeleteJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        System.out.println("Borrado logico de cliente");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 4;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        System.out.println("Ejecutando borrado logico de cliente");
        jsonretorno.put("success",String.valueOf( this.getGralDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
}
