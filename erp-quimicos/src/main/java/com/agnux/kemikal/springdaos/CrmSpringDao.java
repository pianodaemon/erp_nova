/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.springdaos;

import com.agnux.common.helpers.StringHelper;
import java.sql.ResultSet;
import com.agnux.kemikal.interfacedaos.CrmInterfaceDao;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author luis Carrillo
 */
public class CrmSpringDao implements CrmInterfaceDao{
    private JdbcTemplate jdbcTemplate;
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
     //metodos  de uso general
    @Override
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array) {
        String sql_to_query = "select erp_fn_validaciones_por_aplicativo from erp_fn_validaciones_por_aplicativo('"+data+"',"+idApp+",array["+extra_data_array+"]);";
        //System.out.println("Validacion:"+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
            sql_to_query, 
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("success",rs.getString("erp_fn_validaciones_por_aplicativo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public String selectFunctionForThisApp(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from gral_adm_catalogos('"+campos_data+"',array["+extra_data_array+"]);";
        
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        valor_retorno = update.get("gral_adm_catalogos").toString();
        return valor_retorno;
    }
    
    
    @Override
    public String selectFunctionForCrmAdmProcesos(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from crm_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";
        
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        valor_retorno = update.get("crm_adm_procesos").toString();
        return valor_retorno;
    }
    
    
    @Override
    public int countAll(String data_string) {
        String sql_busqueda = "select id from gral_bus_catalogos('"+data_string+"') as foo (id integer)";
        String sql_to_query = "select count(id)::int as total from ("+sql_busqueda+") as subt";
        
        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        return rowCount;
    }
    
    
    
/*Buscador de contactos*/
    @Override
    public ArrayList<HashMap<String, String>> getBuscadorContactos(String nombre, String apellidop, String apellidom, String tipo_contacto, Integer id_empresa) {
        
        String sql_tmp1 = "select id, nombre||' '||apellido_paterno||' '||apellido_materno as contacto, "
                + "(CASE WHEN tipo_contacto=1 THEN 'Cliente' ELSE 'Prospecto' END) as tipo, tipo_contacto "
                + "from crm_contactos where borrado_logico=false AND "
                + "tipo_contacto="+tipo_contacto+" AND nombre ilike '%"+nombre+"%' AND apellido_paterno ilike '%"+apellidop+"%' AND apellido_materno ilike '%"+apellidom+"%' "
                + "AND gral_emp_id="+id_empresa+" ";
        
        //1->cliente
        //2->prospecto
        String sql_to_query = "";
        if(tipo_contacto.equals("1")){
            sql_to_query = "select cont_tmp.*, cxc_clie.razon_social,cxc_clie.rfc from (";
            sql_to_query += sql_tmp1;
            sql_to_query += ") as cont_tmp JOIN crm_contacto_cli on crm_contacto_cli.crm_contactos_id=cont_tmp.id "
                    + "JOIN "
                    + "cxc_clie on cxc_clie.id=crm_contacto_cli.cxc_clie_id";
        }else{
             sql_to_query = "select cont_tmp.*, crm_prospectos.razon_social,crm_prospectos.rfc from ( ";
            sql_to_query += sql_tmp1;
            sql_to_query += " ) as cont_tmp JOIN "
                    + "crm_contacto_pro on crm_contacto_pro.crm_contactos_id=cont_tmp.id "
                    + "JOIN "
                    + "crm_prospectos on crm_prospectos.id=crm_contacto_pro.crm_prospectos_id";
        }
        
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        ArrayList<HashMap<String, String>> hm_datos_contacto = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("tipo_contacto",String.valueOf(rs.getInt("tipo_contacto")));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("contacto",rs.getString("contacto"));
                    row.put("tipo",rs.getString("tipo"));
                    return row;
                }
            }
        );
        return hm_datos_contacto;  
    }

    
    
    //CRM Motivos de Visitas
    //------------------------------------------Aplicativo de Motivos de Visitas----------------------------------------
  
    @Override
    public ArrayList<HashMap<String, String>> getMotivoVisita_Datos(Integer id) {
        
        String sql_to_query = "SELECT id,folio_mv,descripcion FROM crm_motivos_visita WHERE id="+id;
        ArrayList<HashMap<String, String>> dato_datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio_mv",rs.getString("folio_mv"));
                    row.put("descripcion",rs.getString("descripcion")); 
                    
                    return row;
                }
            }
        );
        return dato_datos;
         
    }

    @Override
    public ArrayList<HashMap<String, Object>> getMotivosVisita_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc, Integer id_empresa) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT crm_motivos_visita.id, crm_motivos_visita.descripcion "                              
                                +"FROM crm_motivos_visita "                        
                                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = crm_motivos_visita.id "
                                +"WHERE crm_motivos_visita.borrado_logico=false "
                                +"and crm_motivos_visita.gral_emp_id= " +id_empresa
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ? ";
        
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion"));                    
                    return row;
                }
            }
        );
        return hm; 
    }
    //--------------------------------------------------- catalogo de formas de contacto-----------------------------------------------
    @Override
    public ArrayList<HashMap<String, Object>> getFormasContacto_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc, Integer id_empresa) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT crm_formas_contacto.id, crm_formas_contacto.descripcion "                              
                                +"FROM crm_formas_contacto "                        
                                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = crm_formas_contacto.id "
                                +"WHERE crm_formas_contacto.borrado_logico=false "
                                +"and crm_formas_contacto.gral_emp_id= " +id_empresa
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ? ";
        
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion"));                    
                    return row;
                }
            }
        );
        return hm; 
    }

    @Override
    public ArrayList<HashMap<String, String>> getFormasContacto_Datos(Integer id) {
       String sql_to_query = "SELECT id,folio_fc,descripcion FROM crm_formas_contacto WHERE id="+id;
        ArrayList<HashMap<String, String>> dato_datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio_fc",rs.getString("folio_fc"));
                    row.put("descripcion",rs.getString("descripcion")); 
                    
                    return row;
                }
            }
        );
        return dato_datos;
    }

    //---------------------------------------------fin de catologo de formas de contacto--------------------------------------------------------

    //----------------------------------------------catalogo de motivos de llamada-------------------------------------------------------------
    @Override
    public ArrayList<HashMap<String, String>> getMotivosLlamada_Datos(Integer id) {
        String sql_to_query = "SELECT id,folio_mll,descripcion FROM crm_motivos_llamada WHERE id="+id;
        ArrayList<HashMap<String, String>> dato_datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio_mll",rs.getString("folio_mll"));
                    row.put("descripcion",rs.getString("descripcion")); 
                    
                    return row;
                }
            }
        );
        return dato_datos;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getMotivosLlamada_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc, Integer id_empresa) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT crm_motivos_llamada.id, crm_motivos_llamada.descripcion "                              
                                +"FROM crm_motivos_llamada "                        
                                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = crm_motivos_llamada.id "
                                +"WHERE crm_motivos_llamada.borrado_logico=false "
                                +"and crm_motivos_llamada.gral_emp_id= " +id_empresa
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ? ";
        
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion"));                    
                    return row;
                }
            }
        );
        return hm; 
    }
   //----------------------------------------------fin de catalogo de motivos de llamada--------------------------------------------------
    
    
    //----------------------------------------------catalogo de Registro de Visitas-------------------------------------------------------------
    @Override
    public ArrayList<HashMap<String, Object>> getCrmRegistroVisitas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
                + "SELECT "
                    + "crm_registro_visitas.id,"
                    + "crm_registro_visitas.folio, "
                    + "cxc_agen.nombre AS agente, "
                    + "crm_motivos_visita.descripcion AS motivo, "
                    + "crm_calificaciones_visita.titulo AS calif, "
                    + "crm_tipos_seguimiento_visita.titulo AS tipo_seg "
                + "FROM crm_registro_visitas "
                + "JOIN cxc_agen ON cxc_agen.id=crm_registro_visitas.gral_empleado_id "
                + "LEFT JOIN crm_motivos_visita ON crm_motivos_visita.id=crm_registro_visitas.crm_motivos_visita_id "
                + "LEFT JOIN crm_calificaciones_visita ON crm_calificaciones_visita.id=crm_registro_visitas.crm_calificacion_visita_id "
                + "LEFT JOIN crm_tipos_seguimiento_visita ON crm_tipos_seguimiento_visita.id=crm_registro_visitas.crm_tipos_seguimiento_visita_id "
                + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = crm_registro_visitas.id "
                + "order by "+orderBy+" "+asc+" limit ? OFFSET ? ";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("agente",rs.getString("agente"));
                    row.put("motivo",rs.getString("motivo"));
                    row.put("calif",rs.getString("calif"));
                    row.put("tipo_seg",rs.getString("tipo_seg"));
                    
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    

    
    @Override
    public ArrayList<HashMap<String, String>> getCrmRegistroVisitas_Datos(Integer id) {
        String sql_to_query = ""
                + "SELECT "
                    + "crm_registro_visitas.id ,"
                    + "crm_registro_visitas.folio,"
                    + "crm_registro_visitas.fecha,"
                    + "(CASE WHEN EXTRACT(HOUR FROM crm_registro_visitas.hora)=0 AND EXTRACT(MINUTE FROM crm_registro_visitas.hora)=0 THEN '00:00' ELSE (lpad(EXTRACT(HOUR FROM crm_registro_visitas.hora)::character varying, 2, '0')||':'||lpad(EXTRACT(MINUTE FROM crm_registro_visitas.hora)::character varying, 2, '0'))END) AS hora, "
                    + "(CASE WHEN EXTRACT(HOUR FROM crm_registro_visitas.duracion)=0 AND EXTRACT(MINUTE FROM crm_registro_visitas.duracion)=0 THEN '00:00' ELSE (lpad(EXTRACT(HOUR FROM crm_registro_visitas.duracion)::character varying, 2, '0')||':'||lpad(EXTRACT(MINUTE FROM crm_registro_visitas.duracion)::character varying, 2, '0'))END) AS duracion, "
                    + "crm_registro_visitas.gral_empleado_id,"
                    + "crm_registro_visitas.crm_contacto_id,"
                    + "crm_contactos.nombre||' '||(CASE WHEN crm_contactos.apellido_paterno IS NULL THEN '' ELSE crm_contactos.apellido_paterno END) ||' '||(CASE WHEN crm_contactos.apellido_paterno IS NULL THEN '' ELSE crm_contactos.apellido_paterno END) AS  nombre_contacto, "
                    + "crm_registro_visitas.crm_motivos_visita_id,"
                    + "crm_registro_visitas.crm_calificacion_visita_id,"
                    + "crm_registro_visitas.crm_tipos_seguimiento_visita_id,"
                    + "crm_registro_visitas.deteccion_oportunidad,"
                    + "crm_registro_visitas.recursos_utilizados,"
                    + "crm_registro_visitas.resultado,"
                    + "crm_registro_visitas.observaciones,"
                    + "(CASE WHEN crm_registro_visitas.fecha_sig_visita::character varying='2999-12-31' THEN '' ELSE crm_registro_visitas.fecha_sig_visita::character varying END) AS fecha_sig_visita,"
                    + "(CASE WHEN EXTRACT(HOUR FROM crm_registro_visitas.hora_sig_visita)=0 AND EXTRACT(MINUTE FROM crm_registro_visitas.hora_sig_visita)=0 THEN '00:00' ELSE (lpad(EXTRACT(HOUR FROM crm_registro_visitas.hora_sig_visita)::character varying, 2, '0')||':'||lpad(EXTRACT(MINUTE FROM crm_registro_visitas.hora_sig_visita)::character varying, 2, '0'))END) AS hora_sig_visita, "
                    + "crm_registro_visitas.comentarios_sig_visita "
                + "FROM crm_registro_visitas "
                + "LEFT JOIN crm_contactos ON crm_contactos.id=crm_registro_visitas.crm_contacto_id "
                + "WHERE crm_registro_visitas.id=?";
        ArrayList<HashMap<String, String>> datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("hora",rs.getString("hora"));
                    row.put("duracion",rs.getString("duracion"));
                    row.put("empleado_id",String.valueOf(rs.getInt("gral_empleado_id")));
                    row.put("contacto_id",String.valueOf(rs.getInt("crm_contacto_id")));
                    row.put("nombre_contacto",rs.getString("nombre_contacto"));
                    row.put("motivo_id",String.valueOf(rs.getInt("crm_motivos_visita_id")));
                    row.put("calificacion_id",String.valueOf(rs.getInt("crm_calificacion_visita_id")));
                    row.put("seguimiento_id",String.valueOf(rs.getInt("crm_tipos_seguimiento_visita_id")));
                    row.put("deteccion_oportunidad",String.valueOf(rs.getInt("deteccion_oportunidad")));
                    row.put("recursos_utilizados",rs.getString("recursos_utilizados"));
                    row.put("resultado",rs.getString("resultado"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("fecha_sig_visita",rs.getString("fecha_sig_visita"));
                    row.put("hora_sig_visita",rs.getString("hora_sig_visita"));
                    row.put("comentarios_sig_visita",rs.getString("comentarios_sig_visita"));
                    return row;
                }
            }
        );
        return datos;
    }
    
    
    
    
    //obtiene todos los agentes de la empresa
    @Override
    public ArrayList<HashMap<String, String>> getAgentes(Integer id_empresa) {
        String sql_to_query = "SELECT cxc_agen.id,  "
                                        +"cxc_agen.nombre AS nombre_agente "
                                +"FROM cxc_agen "
                                +"JOIN gral_usr_suc ON gral_usr_suc.gral_usr_id=cxc_agen.gral_usr_id "
                                +"JOIN gral_suc ON gral_suc.id=gral_usr_suc.gral_suc_id "
                                +"WHERE gral_suc.empresa_id="+id_empresa+" ORDER BY cxc_agen.id;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id")  );
                    row.put("nombre_agente",rs.getString("nombre_agente"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public HashMap<String, String> getUserRol(Integer id_user) {
        HashMap<String, String> data = new HashMap<String, String>();
        
        //verificar si el usuario tiene  rol de ADMINISTTRADOR
        //si exis es mayor que cero, el usuario si es ADMINISTRADOR
        String sql_to_query = "SELECT count(gral_usr_id) AS exis_rol_admin FROM gral_usr_rol WHERE gral_usr_id="+id_user+" AND gral_rol_id=1;";
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        data.put("exis_rol_admin",map.get("exis_rol_admin").toString().toUpperCase());
        
        return data;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCrmRegistroVisitas_Motivos(Integer id_empresa) {
        String sql_to_query = "SELECT id, descripcion FROM crm_motivos_visita WHERE gral_emp_id="+id_empresa+" AND borrado_logico=false ORDER BY descripcion;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id")  );
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCrmRegistroVisitas_Calificaciones(Integer id_empresa) {
        String sql_to_query = "SELECT id, titulo FROM crm_calificaciones_visita WHERE gral_emp_id="+id_empresa+" AND borrado_logico=false ORDER BY id;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id")  );
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCrmRegistroVisitas_Seguimientos(Integer id_empresa) {
        String sql_to_query = "SELECT id, titulo FROM crm_tipos_seguimiento_visita WHERE gral_emp_id="+id_empresa+" AND borrado_logico=false ORDER BY id;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id")  );
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
   //----------------------------------------------fin de Catalogo de Registro de Visitas--------------------------------------------------
    
    
//----------------------------------------------inicio de catalogo de crm_oportunidades--------------------------------------------------
    @Override
    public ArrayList<HashMap<String, Object>> getOportunidades_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc, Integer id_empresa) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
        String sql_to_query = "select oport.*,crm_tipos_oportunidad.descripcion as accesor_tipo_oportunidad,crm_etapas_venta.descripcion as accesos_etapa,"
                + "crm_contactos.nombre||' '||crm_contactos.apellido_paterno||' '||crm_contactos.apellido_materno as accesor_contacto, "
                + "gral_empleados.nombre_pila||' '||gral_empleados.apellido_paterno||' '||gral_empleados.apellido_materno as accesor_empleado from ("
                + "select crm_oportunidades.id, fecha_oportunidad, monto, fecha_cotizar, fecha_cierre, estatus, cierre_oportunidad, crm_tipos_oportunidad_id, "
                + "crm_contactos_id, crm_etapas_venta_id, gral_empleados_id from crm_oportunidades "
                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = crm_oportunidades.id "
                //+ " where gral_emp_id=1 and borrado_logico=false "
                + ") as oport "
                + "join crm_contactos on crm_contactos.id=oport.crm_contactos_id "
                + "join gral_empleados on gral_empleados.id=oport.gral_empleados_id "
                + "join crm_etapas_venta on crm_etapas_venta.id=oport.crm_etapas_venta_id "
                + "join crm_tipos_oportunidad on crm_tipos_oportunidad.id=oport.crm_tipos_oportunidad_id "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ? ";
                
        
        //System.out.println("sql_to_query: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    
                    row.put("fecha_oportunidad",rs.getString("fecha_oportunidad"));
                    row.put("fecha_cotizar",rs.getString("fecha_cotizar")); 
                    row.put("fecha_cierre",rs.getString("fecha_cierre")); 
                    row.put("monto",StringHelper.roundDouble(String.valueOf(rs.getDouble("monto")), 2));
                    row.put("estatus",String.valueOf(rs.getBoolean("estatus")).equals("true") ? "Vigente" : "Cancelado" );
                    row.put("cierre_oportunidad",String.valueOf(rs.getInt("cierre_oportunidad")));
                    row.put("accesor_tipo_oportunidad",rs.getString("accesor_tipo_oportunidad"));
                    row.put("accesor_empleado",rs.getString("accesor_empleado"));
                    row.put("accesor_contacto",rs.getString("accesor_contacto"));
                    row.put("accesos_etapa",rs.getString("accesos_etapa"));
                    
                    return row;
                }
            }
        );
        return hm; 
    }

    
    
    @Override
    public ArrayList<HashMap<String, String>> getOportunidad_Datos(Integer id) {
        String sql_to_query = "select tmp_op.*, crm_contactos.nombre||' '||crm_contactos.apellido_paterno||' '||crm_contactos.apellido_materno as contacto, "
                + "(CASE WHEN crm_contactos.tipo_contacto=1 THEN ( "
                + "select cxc_clie.razon_social||' RFC:'||cxc_clie.rfc from "
                + "(select cxc_clie_id from crm_contacto_cli where crm_contactos_id=tmp_op.crm_contactos_id) as con_cli_tmp "
                + "JOIN "
                + "cxc_clie on cxc_clie.id=con_cli_tmp.cxc_clie_id limit 1 "
                + ") ELSE ( "
                + "select crm_prospectos.razon_social||' RFC:'||crm_prospectos.rfc from "
                + "(select crm_prospectos_id from crm_contacto_pro where crm_contactos_id=tmp_op.crm_contactos_id) as con_pro_tmp "
                + "JOIN "
                + "crm_prospectos on crm_prospectos.id=con_pro_tmp.crm_prospectos_id limit 1 "
                + ") END) as prospecto "
                + " from ( "
                + "SELECT * FROM crm_oportunidades  WHERE id="+id+" "
                + ") tmp_op "
                + "join crm_contactos ON crm_contactos.id=tmp_op.crm_contactos_id ";
        
        ArrayList<HashMap<String, String>> dato_datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("crm_tipos_oportunidad_id",String.valueOf(rs.getInt("crm_tipos_oportunidad_id")));
                    row.put("crm_contactos_id",String.valueOf(rs.getInt("crm_contactos_id")));
                    row.put("crm_etapas_venta_id",String.valueOf(rs.getInt("crm_etapas_venta_id")));
                    row.put("gral_empleados_id",String.valueOf(rs.getInt("gral_empleados_id")));
                    row.put("fecha_oportunidad",rs.getString("fecha_oportunidad"));
                    row.put("fecha_cotizar",rs.getString("fecha_cotizar")); 
                    row.put("fecha_cierre",rs.getString("fecha_cierre")); 
                    row.put("monto",StringHelper.roundDouble(String.valueOf(rs.getDouble("monto")), 2));
                    row.put("estatus",String.valueOf(rs.getBoolean("estatus")));
                    row.put("cierre_oportunidad",String.valueOf(rs.getInt("cierre_oportunidad")));
                    row.put("prospecto",rs.getString("prospecto"));
                    row.put("contacto",rs.getString("contacto"));
                    
                    return row;
                }
            }
        );
        return dato_datos;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getTiposOportunidad() {
       String sql_to_query = "SELECT id,descripcion FROM crm_tipos_oportunidad WHERE borrado_logico=false";
        ArrayList<HashMap<String, String>> dato_datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion")); 
                    
                    return row;
                }
            }
        );
        return dato_datos;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getEtapasVenta() {
       String sql_to_query = "SELECT id,descripcion FROM crm_etapas_venta WHERE borrado_logico=false";
        ArrayList<HashMap<String, String>> dato_datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion")); 
                    
                    return row;
                }
            }
        );
        return dato_datos;
    }
    
    //----------------------------------------------fin de catalogo de crm_oportunidades--------------------------------------------------
    
    
}
