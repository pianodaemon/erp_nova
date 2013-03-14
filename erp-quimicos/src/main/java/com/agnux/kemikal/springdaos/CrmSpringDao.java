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
                    + "crm_tipos_seguimiento_visita.titulo AS tipo_seg, "
                    + "crm_registro_visitas.fecha,"
                    + "crm_registro_visitas.hora "
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
                    row.put("fecha",String.valueOf(rs.getDate("fecha")));
                    row.put("hora",String.valueOf(rs.getTime("hora")));
                    
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

        System.out.println("Trae Oportunidades"+sql_to_query);
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


//Termina Metodos Catalogo de Prospectos(CRM)
	@Override
    public ArrayList<HashMap<String, Object>> getPaises() {
        //String sql_to_query = "SELECT DISTINCT cve_pais ,pais_ent FROM municipios;";
        String sql_to_query = "SELECT DISTINCT id as cve_pais, titulo as pais_ent FROM gral_pais;";

        ArrayList<HashMap<String, Object>> pais = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cve_pais",rs.getString("cve_pais"));
                    row.put("pais_ent",rs.getString("pais_ent"));
                    return row;
                }
            }
        );
        return pais;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getEntidadesForThisPais(String id_pais) {
        //String sql_to_query = "SELECT DISTINCT cve_ent ,nom_ent FROM municipios where cve_pais='"+id_pais+"' order by nom_ent;";
        String sql_to_query = "SELECT id as cve_ent, titulo as nom_ent FROM gral_edo WHERE pais_id="+id_pais+" order by nom_ent;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cve_ent",rs.getString("cve_ent"));
                    row.put("nom_ent",rs.getString("nom_ent"));
                    return row;
                }
            }
        );
        return hm;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getLocalidadesForThisEntidad(String id_pais, String id_entidad) {
        //String sql_to_query = "SELECT DISTINCT cve_mun ,nom_mun FROM municipios where cve_ent='"+id_entidad+"' and cve_pais='"+id_pais+"' order by nom_mun;";
        String sql_to_query = "SELECT id as cve_mun, titulo as nom_mun FROM gral_mun WHERE estado_id="+id_entidad+" and pais_id="+id_pais+" order by nom_mun;";

        //System.out.println("Ejecutando query loc_for_this_entidad: "+sql_to_query);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cve_mun",rs.getString("cve_mun"));
                    row.put("nom_mun",rs.getString("nom_mun"));
                    return row;
                }
            }
        );
        return hm;
    }





    @Override
    public ArrayList<HashMap<String, Object>> getProspecto_Datos(Integer id) {

        String sql_query = ""
                + "SELECT "

                    +"crm_prospectos.id as id_prospecto, "
                    +"crm_prospectos.razon_social , "
                    +"crm_prospectos.numero_control, "
                    +"crm_prospectos.estatus, "
                    +"crm_prospectos.crm_etapas_prospecto_id, "
                    +"crm_prospectos.tipo_prospecto_id, "
                    +"crm_tipo_prospecto.tipo_prospecto, "
                    +"crm_prospectos.rfc, "
                    //+"crm_prospectos.curp, "
                    +"crm_prospectos.razon_social, "
                    //+"cxc_clie.clave_comercial, "
                    +"crm_prospectos.calle, "
                    +"crm_prospectos.numero, "
                    +"crm_prospectos.entre_calles, "
                    +"crm_prospectos.numero_exterior, "
                    +"crm_prospectos.colonia, "
                    +"crm_prospectos.cp, "
                    +"crm_prospectos.pais_id, "
                    +"crm_prospectos.estado_id, "
                    +"crm_prospectos.municipio_id, "
                    +"crm_prospectos.localidad_alternativa, "
                    +"crm_prospectos.telefono1, "
                    +"crm_prospectos.extension1, "
                    +"crm_prospectos.fax, "
                    +"crm_prospectos.telefono2, "
                    +"crm_prospectos.extension2, "
                    +"crm_prospectos.email, "
                    +"crm_prospectos.contacto ,"

                +"crm_prospectos.clasificacion_id, "
                +"crm_prospectos.tipo_industria_id, "
                +"crm_prospectos.observaciones "



            +"FROM crm_prospectos "
            +" JOIN crm_tipo_prospecto on crm_tipo_prospecto.id=  crm_prospectos.tipo_prospecto_id    "
            +"WHERE  crm_prospectos.borrado_logico=false AND crm_prospectos.id = ?";

        System.out.println("Ejecutando getProspecto_Datos:"+ sql_query);
        System.out.println("IdProspecto "+id);

        ArrayList<HashMap<String, Object>> prospecto = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_prospecto",rs.getInt("id_prospecto"));
                    row.put("prospecto",rs.getString("razon_social"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("estatus",rs.getString("estatus"));
                    row.put("etapas_deventa_id",rs.getString("crm_etapas_prospecto_id"));
                    row.put("tipo_prospecto_id",rs.getString("tipo_prospecto_id"));
                    row.put("tipo_prospecto",rs.getString("tipo_prospecto"));
                    row.put("observaciones",rs.getString("observaciones"));


                    row.put("rfc",rs.getString("rfc"));

                    row.put("razon_social",rs.getString("razon_social"));

                    row.put("calle",rs.getString("calle"));
                    row.put("numero",rs.getString("numero"));
                    row.put("entre_calles",rs.getString("entre_calles"));
                    row.put("numero_exterior",rs.getString("numero_exterior"));
                    row.put("colonia",rs.getString("colonia"));
                    row.put("cp",rs.getString("cp"));
                    row.put("pais_id",rs.getString("pais_id"));
                    row.put("estado_id",rs.getString("estado_id"));
                    row.put("municipio_id",rs.getString("municipio_id"));
                    row.put("localidad_alternativa",rs.getString("localidad_alternativa"));
                    row.put("telefono1",rs.getString("telefono1"));
                    row.put("extension1",rs.getString("extension1"));
                    row.put("fax",rs.getString("fax"));
                    row.put("telefono2",rs.getString("telefono2"));
                    row.put("extension2",rs.getString("extension2"));
                    row.put("email",rs.getString("email"));

                    row.put("contacto",rs.getString("contacto"));
                    row.put("clasificacion_id",rs.getString("clasificacion_id"));
                    row.put("tipo_industria_id",rs.getString("tipo_industria_id"));
                    row.put("observaciones",rs.getString("observaciones"));


                    return row;
                }
            }
        );
        return prospecto;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getProspectos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT "
				+"crm_prospectos.id, "
				+"crm_prospectos.numero_control, "
				+"crm_prospectos.razon_social, "
				+"crm_prospectos.rfc, "
				//+"cxc_clie_clases.titulo AS tipo_cliente, "
                                +"(CASE WHEN crm_prospectos.telefono1='' OR crm_prospectos.telefono1 IS NULL THEN crm_prospectos.telefono2 ELSE crm_prospectos.telefono1 END) AS tel "
			+"FROM crm_prospectos "
			//+"LEFT JOIN cxc_clie_clases ON cxc_clie_clases.id = cxc_clie.clienttipo_id "
                        +"JOIN ("+sql_busqueda+") as subt on subt.id=crm_prospectos.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("rfc",rs.getString("rfc"));
                    //row.put("tipo_cliente",rs.getString("tipo_cliente"));
                    row.put("tel",rs.getString("tel"));
                    return row;
                }
            }
        );
        return hm;
    }

    @Override
    public ArrayList<HashMap<String, Object>> gettipo_prospecto(String id_prospecto) {
        //String sql_to_query = "SELECT DISTINCT cve_ent ,nom_ent FROM municipios where cve_pais='"+id_pais+"' order by nom_ent;";
        String sql_to_query = "select id,tipo_prospecto from crm_tipo_prospecto WHERE crm_tipo_prospecto.id="+id_prospecto+" order by tipo_prospecto;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("tipo_prospecto",rs.getString("tipo_prospecto"));
                    return row;
                }
            }
        );
        return hm;
    }


    @Override
    public ArrayList<HashMap<String, Object>> getTipo_Prospecto() {
        //String sql_to_query = "SELECT DISTINCT cve_pais ,pais_ent FROM municipios;";
        String sql_to_query = "select id,tipo_prospecto from crm_tipo_prospecto order by tipo_prospecto;";

        ArrayList<HashMap<String, Object>> Tipo_Prospecto = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("tipo_prospecto",rs.getString("tipo_prospecto"));
                    return row;
                }
            }
        );
        return Tipo_Prospecto;
    }


    @Override

        public ArrayList<HashMap<String, Object>> getEtapas_prospecto() {
        //String sql_to_query = "SELECT DISTINCT cve_pais ,pais_ent FROM municipios;";
        String sql_to_query = "select id, descripcion from crm_etapas_prospecto order by id;";

        ArrayList<HashMap<String, Object>> Etapa_ventas = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("etapa",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return Etapa_ventas;
    }


    @Override
    public ArrayList<HashMap<String, Object>> getClasificacion_prospecto() {
        //String sql_to_query = "SELECT DISTINCT cve_pais ,pais_ent FROM municipios;";
        String sql_to_query = "select id, clasificacion ,clasificacion_abr from crm_clasificacion_prospecto order by clasificacion_abr;";

        ArrayList<HashMap<String, Object>> clasifficacion_prosp = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("clasificacion",rs.getString("clasificacion"));
                    row.put("clasificacion_abr",rs.getString("clasificacion_abr"));
                    return row;
                }
            }
        );
        return clasifficacion_prosp;
    }


     @Override
    public ArrayList<HashMap<String, Object>> getTipo_industria() {
        //String sql_to_query = "SELECT DISTINCT cve_pais ,pais_ent FROM municipios;";
        String sql_to_query = "select id, tipo_industria from crm_tipo_industria order by tipo_industria;";

        ArrayList<HashMap<String, Object>> Tipo_industria = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("tipo_industria",rs.getString("tipo_industria"));

                    return row;
                }
            }
        );
        return Tipo_industria;
    }
    //Termina Metodos Catalogo de Prospectos(CRM)

    //+++++++++++++++++++++++++++++++++++Metodos Registro de Metas+++++++++++++++++++++++++++++++++
    @Override
    public ArrayList<HashMap<String, String>> getCrmRegistoMetas_Datos(Integer id) {
        String sql_to_query = ""
                +"SELECT "
                    + "crm_metas.id,  "
                    +"crm_metas.folio,"
                    +"crm_metas.gral_empleado_id, "
                    +"crm_metas.ano, "
                    + "crm_metas.mes, "


                        +"crm_metas.cantidad_visitas, "
                        +"crm_metas.cantidad_llamadas, "
                        +"crm_metas.cantidad_prospectos, "
                        +"crm_metas.cantidad_cotizaciones, "
                        +"crm_metas.cantidad_oportunidades, "
                        +"crm_metas.monto_cotizaciones, "
                        +"crm_metas.monto_oportunidades, "
                        +"crm_metas.ventas_prospectos, "
                        +"crm_metas.cantidad_cotizaciones2 as cant_cotizaciones, "
                        +"crm_metas.cantidad_oportunidades2 as cant_oportunidades, "
                        +"crm_metas.monto_cotizaciones2 as montos_cotizaciones, "
                        +"crm_metas.monto_oportunidades2 as montos_oportunidades, "
                        +"crm_metas.ventas_clientes, "
                        +"crm_metas.ventas_oportunidades_clientes, "
                        +"crm_metas.gral_empleado_id "
                +"FROM crm_metas "
                +"WHERE crm_metas.id=? ";
        System.out.println("Datos de la META ___"+sql_to_query);
        ArrayList<HashMap<String, String>> datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("ano",String.valueOf(rs.getInt("ano")));
                    row.put("mes",String.valueOf(rs.getInt("mes")));
                    row.put("cantidad_visitas",String.valueOf(rs.getInt("cantidad_visitas")));
                    row.put("cantidad_llamadas",String.valueOf(rs.getInt("cantidad_llamadas")));
                    row.put("cantidad_prospectos",String.valueOf(rs.getInt("cantidad_prospectos")));
                    row.put("cantidad_cotizaciones",String.valueOf(rs.getInt("cantidad_cotizaciones")));
                    row.put("cantidad_oportunidades",String.valueOf(rs.getInt("cantidad_oportunidades")));
                    row.put("empleado_id",String.valueOf(rs.getInt("gral_empleado_id")));
                    row.put("empleado_id",String.valueOf(rs.getInt("gral_empleado_id")));
                    row.put("monto_cotizaciones",StringHelper.roundDouble(rs.getDouble("monto_cotizaciones"),2));
                    row.put("monto_oportunidades",StringHelper.roundDouble(rs.getDouble("monto_oportunidades"),2));
                    row.put("ventas_prospectos",String.valueOf(rs.getInt("ventas_prospectos")));
                    row.put("cant_cotizaciones",String.valueOf(rs.getInt("cant_cotizaciones")));
                    row.put("cant_oportunidades",String.valueOf(rs.getInt("cant_oportunidades")));
                    row.put("montos_cotizaciones",StringHelper.roundDouble(rs.getDouble("montos_cotizaciones"),2));
                    row.put("montos_oportunidades",StringHelper.roundDouble(rs.getDouble("montos_oportunidades"),2));
                    row.put("ventas_clientes",String.valueOf(rs.getInt("ventas_clientes")));
                    row.put("ventas_oportunidades_clientes",String.valueOf(rs.getInt("ventas_oportunidades_clientes")));

                    return row;
                }
            }
        );
        return datos;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getRegistroMetas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT crm_metas.id, "
                                +" crm_metas.folio, "
                                +"gral_empleados.nombre_pila ||' '|| apellido_paterno ||' '|| apellido_materno as agente,"
                                + "(case when mes =1 then 'Enero' "
                                        +"when mes =2 then 'Febrero' "
                                        +"when mes =3 then 'Marzo' "
                                        +"when mes =4 then 'Abril' "
                                        +"when mes =5 then 'Mayo' "
                                        +"when mes =6 then 'Junio' "
                                        +"when mes =7 then 'Julio' "
                                        +"when mes =8 then 'Agosto' "
                                        +"when mes =9 then 'Septiembre' "
                                        +"when mes =10 then 'Octubre' "
                                        +"when mes =11 then 'Noviembre' "
                                        +"when mes =12 then 'Diciembre' "
                                    +"else 'Fin del Mundo' end ) as mes,  "
                                    +" crm_metas.ano "
                                +"FROM "
                                +"crm_metas "
                                + "join gral_empleados on gral_empleados.id = crm_metas.gral_empleado_id "
                                + "join ("+sql_busqueda+") AS sbt ON sbt.id = crm_metas.id "
                                + "order by "+orderBy+" "+asc+" limit ? OFFSET ? ";
        System.out.println("Esto es lo que me trae:"+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("agente",rs.getString("agente"));
                    row.put("mes",rs.getString("mes"));
                    row.put("ano",String.valueOf(rs.getInt("ano")));


                    return row;
                }
            }
        );
        return hm;
    }
     //+++++++++++++++++++++++++++++++++++FIN  de Registro de Metas+++++++++++++++++++++++++++++++++

     ///------------------------------------------------Aplicativo de Registro de Llamadas-------------------------------------------
    @Override
    public ArrayList<HashMap<String, String>> getMotivos_Llamadas(Integer id_empresa) {
        String sql_to_query = "SELECT id,descripcion FROM crm_motivos_llamada WHERE gral_emp_id="+id_empresa+" AND borrado_logico=false ORDER BY descripcion;";

        System.out.println("Query motivos llamadas::::....."+sql_to_query);
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
    public ArrayList<HashMap<String, String>> getCalificacion_Llamadas(Integer id_empresa) {
        String sql_to_query = "select id,titulo from crm_calificaciones_llamadas where gral_emp_id="+id_empresa+" AND borrado_logico=false ORDER BY id;";

        System.out.println("Query Calificaciones::::....."+sql_to_query);

        ArrayList<HashMap<String, String>> dato_datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));

                    return row;
                }
            }
        );
        return dato_datos;
    }

    @Override
    public ArrayList<HashMap<String, String>> getRegistroLlamadas_Seguimiento(Integer id_empresa) {
       String sql_to_query = "select id,titulo from crm_tipos_seguimiento_llamadas  where gral_emp_id="+id_empresa+" AND borrado_logico=false ORDER BY id; ";

       System.out.println("Query Seguimientos::::....."+sql_to_query);
       ArrayList<HashMap<String, String>> dato_datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));

                    return row;
                }
            }

        );
        return dato_datos;

    }

    @Override
    public ArrayList<HashMap<String, String>> getCrmRegistroLlamadas_Datos(Integer id) {
        String sql_to_query = ""
                + "SELECT "
                    +"crm_registro_llamadas.id , "
                    +"crm_registro_llamadas.folio, "
                    +"crm_registro_llamadas.fecha, "
                    +"(CASE WHEN EXTRACT(HOUR FROM crm_registro_llamadas.hora)=0 AND EXTRACT(MINUTE FROM crm_registro_llamadas.hora)=0 THEN '00:00' ELSE (lpad(EXTRACT(HOUR FROM crm_registro_llamadas.hora)::character varying, 2, '0')||':'||lpad(EXTRACT(MINUTE FROM crm_registro_llamadas.hora)::character varying, 2, '0'))END) AS hora, "
                    +"(CASE WHEN EXTRACT(HOUR FROM crm_registro_llamadas.duracion)=0 AND EXTRACT(MINUTE FROM crm_registro_llamadas.duracion)=0 THEN '00:00' ELSE (lpad(EXTRACT(HOUR FROM crm_registro_llamadas.duracion)::character varying, 2, '0')||':'||lpad(EXTRACT(MINUTE FROM crm_registro_llamadas.duracion)::character varying, 2, '0'))END) AS duracion, "
                    +"crm_registro_llamadas.gral_empleado_id, "
                    +"crm_registro_llamadas.crm_contacto_id, "
                    +"crm_contactos.nombre||' '||(CASE WHEN crm_contactos.apellido_paterno IS NULL THEN '' ELSE crm_contactos.apellido_paterno END) ||' '||(CASE WHEN crm_contactos.apellido_paterno IS NULL THEN '' ELSE crm_contactos.apellido_paterno END) AS  nombre_contacto, "
                    +"crm_registro_llamadas.crm_motivos_llamda_id, "
                    +"crm_registro_llamadas.crm_calificacion_llamada_id, "
                    +"crm_registro_llamadas.crm_tipos_seguimiento_llamada_id, "
                    +"crm_registro_llamadas.deteccion_oportunidad, "
                    +"crm_registro_llamadas.llamada_planeada, "
                    +"crm_registro_llamadas.llamada_completada,"
                    + "crm_registro_llamadas.tipo_llamada, "
                    +"crm_registro_llamadas.resultado, "
                    +"crm_registro_llamadas.observaciones, "
                    +"(CASE WHEN crm_registro_llamadas.fecha_sig_llamada::character varying='2999-12-31' THEN '' ELSE crm_registro_llamadas.fecha_sig_llamada::character varying END) AS fecha_sig_llamada, "
                    +"(CASE WHEN EXTRACT(HOUR FROM crm_registro_llamadas.hora_sig_llamada)=0 AND EXTRACT(MINUTE FROM crm_registro_llamadas.hora_sig_llamada)=0 THEN '00:00' ELSE (lpad(EXTRACT(HOUR FROM crm_registro_llamadas.hora_sig_llamada)::character varying, 2, '0')||':'||lpad(EXTRACT(MINUTE FROM crm_registro_llamadas.hora_sig_llamada)::character varying, 2, '0'))END) AS hora_sig_llamada, "
                    +"crm_registro_llamadas.comentarios_sig_llamada "
                +"FROM crm_registro_llamadas "
                +"LEFT JOIN crm_contactos ON crm_contactos.id=crm_registro_llamadas.crm_contacto_id "
                +"WHERE crm_registro_llamadas.id=? ";
        System.out.println("Datos de Llamadas"+sql_to_query);
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
                    row.put("crm_motivos_llamda_id",String.valueOf(rs.getInt("crm_motivos_llamda_id")));
                    row.put("calificacion_id",String.valueOf(rs.getInt("crm_calificacion_llamada_id")));
                    row.put("seguimiento_id",String.valueOf(rs.getInt("crm_tipos_seguimiento_llamada_id")));
                    row.put("deteccion_oportunidad",String.valueOf(rs.getInt("deteccion_oportunidad")));

                    row.put("resultado",rs.getString("resultado").toUpperCase());
                    row.put("observaciones",rs.getString("observaciones").toUpperCase());
                    row.put("fecha_sig_llamada",rs.getString("fecha_sig_llamada"));
                    row.put("hora_sig_llamada",rs.getString("hora_sig_llamada"));
                    row.put("comentarios_sig_llamada",rs.getString("comentarios_sig_llamada").toUpperCase());
                    row.put("llamada_planeada",String.valueOf(rs.getInt("llamada_planeada")));
                    row.put("llamada_completada",String.valueOf(rs.getInt("llamada_completada")));
                    row.put("tipo_llamada",String.valueOf(rs.getInt("tipo_llamada")));
                    return row;
                }
            }
        );
        return datos;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getRegistroLlamadas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = " "
                + "SELECT "
                   +" crm_registro_llamadas.id, "
                    +"crm_registro_llamadas.folio, "
                    +"cxc_agen.nombre AS agente, "
                    +"crm_motivos_llamada.descripcion AS motivo, "
                    +"crm_calificaciones_visita.titulo AS calif, "
                    +"crm_tipos_seguimiento_visita.titulo AS tipo_seg, "
                    +"crm_registro_llamadas.fecha, "
                    +"crm_registro_llamadas.hora "
                +"FROM crm_registro_llamadas "
                +"JOIN cxc_agen ON cxc_agen.id=crm_registro_llamadas.gral_empleado_id "
                +"LEFT JOIN crm_motivos_llamada ON crm_motivos_llamada.id=crm_registro_llamadas.crm_motivos_llamda_id "
                +"LEFT JOIN crm_calificaciones_visita ON crm_calificaciones_visita.id=crm_registro_llamadas.crm_calificacion_llamada_id "
                +"LEFT JOIN crm_tipos_seguimiento_visita ON crm_tipos_seguimiento_visita.id=crm_registro_llamadas.crm_tipos_seguimiento_llamada_id "
                +"join ("+sql_busqueda+") AS sbt ON sbt.id = crm_registro_llamadas.id "

                + "order by "+orderBy+" "+asc+" limit ? OFFSET ? ";
        System.out.println("Devuelve la consulta"+sql_to_query);
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
                    row.put("fecha",String.valueOf(rs.getDate("fecha")));
                    row.put("hora",String.valueOf(rs.getTime("hora")));
                    
                    return row;
                }
            }
        );
        return hm;
    }

    //----------------------------------------------Termina aplicativo de registro de llamadas----------------------------------------

    //----------------------------------------------catalogo de Registro de Casos-------------------------------------------------------------
    @Override
    public ArrayList<HashMap<String, Object>> getCrmRegistroCasos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT "
                    +" crm_registro_casos.id ,  "
                    +" crm_registro_casos.folio ,  "
                    +" (case when crm_registro_casos.tipo = 1 then 'Clientes' else 'Prospectos' end ) as tipo,  "

                    +" (CASE WHEN crm_registro_casos.tipo = 1 THEN   "
                    +" cxc_clie.razon_social else crm_prospectos.razon_social END) as razon_social,  "
                    +" to_char(crm_registro_casos.fecha_cierre,'yyyy-mm-dd') AS fecha_cierre,  "
                    +" crm_registro_casos.estatus ,  "
                    +" crm_registro_casos.prioridad ,  "
                    +" crm_registro_casos.tipo_caso ,  "
                    +" crm_registro_casos.descripcion ,  "
                    +" crm_registro_casos.resolucion ,  "
                    +" crm_registro_casos.observacion_agente  "
                    
                + "FROM crm_registro_casos "
                +" LEFT JOIN crm_registro_casos_prospectos on crm_registro_casos_prospectos.id_crm_registro_casos=crm_registro_casos.id  "
	        +" LEFT JOIN crm_registro_casos_clie on  crm_registro_casos_clie.id_crm_registro_casos=crm_registro_casos.id  "
                +" LEFT JOIN cxc_clie on cxc_clie.id=crm_registro_casos_clie.id_cliente  "
                +" LEFT JOIN crm_prospectos on crm_prospectos.id=crm_registro_casos_prospectos.id_prospecto  "
                +" LEFT JOIN cxc_agen ON cxc_agen.id=crm_registro_casos.gral_empleado_id  "
                + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = crm_registro_casos.id "
                +"where crm_registro_casos.borrado_logico=false  "
                + " order by "+orderBy+" "+asc+" limit ? OFFSET ? ";
        System.out.println("Busqueda GetPage GRIDD PRINCIPAL: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("buscado_por",rs.getString("tipo"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("fecha_cierre",rs.getString("fecha_cierre"));
                    row.put("estatus",String.valueOf(rs.getInt("estatus")));
                    row.put("prioridad",String.valueOf(rs.getInt("prioridad")));
                    row.put("tipo_caso",String.valueOf(rs.getInt("tipo_caso")));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("resolucion",rs.getString("resolucion"));
                    row.put("observacion_agente",rs.getString("observacion_agente"));

                    return row;
                }
            }
        );
        return hm;
    }





    @Override
    public ArrayList<HashMap<String, String>> getCrmRegistroCasos_Datos(Integer id) {


        String sql_to_query = "SELECT  "
	+" crm_registro_casos.id ,  "
	+" crm_registro_casos.folio ,  "
	+" crm_registro_casos.tipo,  "
	+" (CASE WHEN crm_registro_casos.tipo = 1 THEN   "
	+" crm_registro_casos_clie.id_cliente else crm_registro_casos_prospectos.id_prospecto END) as id_cliente_prospecto,  "
	+" (CASE WHEN crm_registro_casos.tipo = 1 THEN   "
	+" cxc_clie.razon_social else crm_prospectos.razon_social END) as razon_social,  "
	+" crm_registro_casos.gral_empleado_id as agente_id ,  "
	+" to_char(crm_registro_casos.fecha_cierre,'yyyy-mm-dd') AS fecha_cierre,  "
	+" crm_registro_casos.estatus ,  "
	+" crm_registro_casos.prioridad ,  "
	+" crm_registro_casos.tipo_caso ,  "
	+" crm_registro_casos.descripcion ,  "
	+" crm_registro_casos.resolucion ,  "
	+" crm_registro_casos.observacion_agente  "
	+" FROM crm_registro_casos   "
	+" LEFT JOIN crm_registro_casos_prospectos on crm_registro_casos_prospectos.id_crm_registro_casos=crm_registro_casos.id  "
	+" LEFT JOIN crm_registro_casos_clie on  crm_registro_casos_clie.id_crm_registro_casos=crm_registro_casos.id  "
	+" LEFT JOIN cxc_clie on cxc_clie.id=crm_registro_casos_clie.id_cliente  "
	+" LEFT JOIN crm_prospectos on crm_prospectos.id=crm_registro_casos_prospectos.id_prospecto  "
	+" LEFT JOIN cxc_agen ON cxc_agen.id=crm_registro_casos.gral_empleado_id  "
	+" WHERE crm_registro_casos.borrado_logico=false   "
        +" AND crm_registro_casos.id=?";
        ArrayList<HashMap<String, String>> datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("buscado_por",String.valueOf(rs.getInt("tipo")));
                    row.put("id_cliente_prospecto",String.valueOf(rs.getInt("id_cliente_prospecto")));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("agente_id",rs.getString("agente_id"));
                    row.put("fecha",rs.getString("fecha_cierre"));

                    row.put("estatus",String.valueOf(rs.getInt("estatus")));
                    row.put("prioridad",String.valueOf(rs.getInt("prioridad")));
                    row.put("tipo_caso",String.valueOf(rs.getInt("tipo_caso")));


                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("resolucion",rs.getString("resolucion"));
                    row.put("observacion_agente",rs.getString("observacion_agente"));
                    return row;
                }
            }
        );
        return datos;
    }

    /*Buscador de contactos*/
    @Override
    public ArrayList<HashMap<String, String>> getBuscadorCliente_Prospecto(String Razon_social,  String rfc, Integer identificador, Integer id_empresa) {
        String tabla="";
        String empresa="";
        String cadena_where="";
       Integer valor=0;


        System.out.println("Filtros:: " +Razon_social+"___"+rfc);
        if(identificador == 1){
              tabla="cxc_clie";
              empresa=" empresa_id";
         }
        if(identificador == 2){
             tabla="crm_prospectos";
             empresa="gral_emp_id";
         }

        if(!Razon_social.equals("%%")){
         cadena_where = "  and     "+tabla+".razon_social ilike '"+Razon_social+"' " ;
         valor=1;
        }

        if(!rfc.equals("%%")){
            if (valor == 0){
                cadena_where = " and "+tabla+".rfc ilike '"+rfc+"'  " ;
            } else{
                cadena_where = cadena_where+" and "+tabla+".rfc ilike '"+rfc+"' " ;
            }

        }
        String sql_to_query = "select "+tabla+".id,"
                +" "+tabla+".numero_control,"
                +" "+tabla+".rfc,"
                +" "+tabla+".razon_social"
                +" from "+tabla+" where borrado_logico=false "
                +" "+cadena_where
                +"  AND "+empresa+"="+id_empresa+" ";

        System.out.println("Ejecutando query" +sql_to_query);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);

        ArrayList<HashMap<String, String>> hm_datos_cliente_prospecto = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("numero_control",String.valueOf(rs.getString("numero_control")));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));

                    return row;
                }
            }
        );
        return hm_datos_cliente_prospecto;
    }
    //----------------------------------------------Termina aplicativo de registro de Casos----------------------------------------

@Override
    public ArrayList<HashMap<String, String>> getBuscadorRegistros(Integer id, Integer agente, Integer tipo_seleccion,Integer status,Integer etapa, String fecha_inicial, String fecha_final, Integer id_empresa) {


            String query ="select * from crm_consultas("+id+","+agente+","+tipo_seleccion+","+status+","+etapa+",'"+fecha_inicial+"','"+fecha_final+"',"+id_empresa+")as foo("
                                                        + "cantidad_llamadas integer, "
                                                        + "llamadas_totales integer, "
                                                        + "llamadas_entrantes integer, "
                                                        + "llamadas_salientes integer, "
                                                        + "llamadas_planeadas integer, "
                                                        + "llamadas_con_exito integer, "
                                                        + "llamadas_con_cita integer, "
                                                        + "llamadas_con_seguimiento integer, "
                                                        + "porcentaje_llamadas double precision, "
                                                        + "efectividad double precision, "
                                                        + "avance double precision, "
                                                        + "gestion double precision, "
                                                        + "planeacion double precision)";

            System.out.println("Resultados de la FUNCION__1____"+" "+query);


        //String sql_to_query="";
        ArrayList<HashMap<String, String>> hm_datos_contacto = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("cantidad_llamadas",String.valueOf(rs.getInt("cantidad_llamadas")));
                    row.put("llamadas_totales",String.valueOf(rs.getInt("llamadas_totales")));
                    row.put("llamadas_entrantes",String.valueOf(rs.getInt("llamadas_entrantes")));
                    row.put("llamadas_salientes",String.valueOf(rs.getInt("llamadas_salientes")));
                    row.put("llamadas_planeadas",String.valueOf(rs.getInt("llamadas_planeadas")));
                    row.put("llamadas_con_exito",String.valueOf(rs.getInt("llamadas_con_exito")));
                    row.put("llamadas_con_cita",String.valueOf(rs.getInt("llamadas_con_cita")));
                    row.put("llamadas_con_seguimiento",String.valueOf(rs.getInt("llamadas_con_seguimiento")));
                    row.put("porcentaje_llamadas",StringHelper.roundDouble(rs.getDouble("porcentaje_llamadas"),2));
                    row.put("efectividad",StringHelper.roundDouble(rs.getDouble("efectividad"),2));
                    row.put("avance",StringHelper.roundDouble(rs.getDouble("avance"),2));
                    row.put("gestion",StringHelper.roundDouble(rs.getDouble("gestion"),2));
                    row.put("planeacion",StringHelper.roundDouble(rs.getDouble("planeacion"),2));


                    return row;
                }
            }
        );
        return hm_datos_contacto;
    }

    @Override
    public ArrayList<HashMap<String, String>> getBuscadorVisitas(Integer id, Integer agente, Integer tipo_seleccion, Integer status, Integer etapa, String fecha_inicial, String fecha_final, Integer id_empresa) {

            String query ="select * from crm_consultas("+id+","+agente+","+tipo_seleccion+","+status+","+etapa+",'"+fecha_inicial+"','"+fecha_final+"',"+id_empresa+")as foo("
                                                        + "cantidad_visitas integer, "
                                                        + "visitas_totales integer, "
                                                        + "visitas_con_exito integer, "
                                                        + "visitas_con_cita integer, "
                                                        + "visitas_con_seguimiento integer, "
                                                        + "porcentaje_visitas double precision, "
                                                        + "efectividad double precision, "
                                                        + "avance double precision, "
                                                        + "gestion double precision) ";


            System.out.println("Resultados de la FUNCION___2____"+" "+query);
      ArrayList<HashMap<String,String>>hm_registro_visitas =(ArrayList<HashMap<String,String>>)this.jdbcTemplate.query(
            query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("cantidad_visitas",String.valueOf(rs.getInt("cantidad_visitas")));
                    row.put("visitas_totales",String.valueOf(rs.getInt("visitas_totales")));
                    row.put("visitas_con_exito",String.valueOf(rs.getInt("visitas_con_exito")));
                    row.put("visitas_con_cita",String.valueOf(rs.getInt("visitas_con_cita")));
                    row.put("visitas_con_seguimiento",String.valueOf(rs.getInt("visitas_con_seguimiento")));
                    row.put("porcentaje_visitas",StringHelper.roundDouble(rs.getDouble("porcentaje_visitas"),2));
                    row.put("efectividad",StringHelper.roundDouble(rs.getDouble("efectividad"),2));
                    row.put("avance",StringHelper.roundDouble(rs.getDouble("avance"),2));
                    row.put("gestion",StringHelper.roundDouble(rs.getDouble("gestion"),2));



                    return row;
                }
            }
        );
        return hm_registro_visitas;
    }



    @Override
    public ArrayList<HashMap<String, String>> getBuscadorCasos(Integer id, Integer agente, Integer tipo_seleccion, Integer status, Integer etapa, String fecha_inicial, String fecha_final, Integer id_empresa) {
        String query ="select * from crm_consultas("+id+","+agente+","+tipo_seleccion+","+status+","+etapa+",'"+fecha_inicial+"','"+
                fecha_final+"',"+id_empresa+")as foo(casos_totales integer, casos_facturacion double precision ,"
                + "casos_producto double precision,casos_garantia double precision,casos_distribucion double precision ,"
                + "casos_danos double precision,casos_devoluciones double precision, casos_cobranza double precision,"
                + "casos_varios double precision)";


        System.out.println("Resultados de la FUNCION___3____"+" "+query);
      ArrayList<HashMap<String,String>>hm_registro_casos =(ArrayList<HashMap<String,String>>)this.jdbcTemplate.query(
            query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("casos_totales",String.valueOf(rs.getInt("casos_totales")));
                    row.put("casos_facturacion",StringHelper.roundDouble(rs.getDouble("casos_facturacion"),2));
                    row.put("casos_producto",StringHelper.roundDouble(rs.getDouble("casos_producto"),2));
                    row.put("casos_garantia",StringHelper.roundDouble(rs.getDouble("casos_garantia"),2));
                    row.put("casos_distribucion",StringHelper.roundDouble(rs.getDouble("casos_distribucion"),2));
                    row.put("casos_danos",StringHelper.roundDouble(rs.getDouble("casos_danos"),2));
                    row.put("casos_devoluciones",StringHelper.roundDouble(rs.getDouble("casos_devoluciones"),2));
                    row.put("casos_cobranza",StringHelper.roundDouble(rs.getDouble("casos_cobranza"),2));
                    row.put("casos_varios",StringHelper.roundDouble(rs.getDouble("casos_varios"),2));

                    return row;
                }
            }
        );
        return hm_registro_casos;
    }





    @Override
    public ArrayList<HashMap<String, String>> getBuscadorOportunidades(Integer id, Integer agente, Integer tipo_seleccion, Integer status, Integer etapa, String fecha_inicial, String fecha_final, Integer id_empresa) {
         String query ="select * from crm_consultas("+id+","+agente+","+tipo_seleccion+","+status+","+etapa+",'"+fecha_inicial+"','"+fecha_final+"',"+id_empresa+")as foo(metas_oport integer,total_metas_oport integer,"
                 + "monto_metas_oport double precision,total_montos_oport double precision, metas_cumplidas double precision,"
                 + "montos_cumplidos double precision, oport_inicial double precision, oport_seguimiento double precision,"
                 + "oport_visitas double precision, oport_cotizacion double precision, oport_negociacion double precision"
                 + ",oport_cierre double precision, oport_ganados double precision, oport_perdidos double precision)";


            System.out.println("Resultados de la FUNCION___4____"+" "+query);
      ArrayList<HashMap<String,String>>hm_registro_oportunidades =(ArrayList<HashMap<String,String>>)this.jdbcTemplate.query(
            query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("metas_oport",String.valueOf(rs.getInt("metas_oport")));
                    row.put("total_metas_oport",String.valueOf(rs.getInt("total_metas_oport")));
                    row.put("monto_metas_oport",StringHelper.roundDouble(rs.getDouble("monto_metas_oport"),2));
                    row.put("total_montos_oport",StringHelper.roundDouble(rs.getDouble("total_montos_oport"),2));
                    row.put("metas_cumplidas",StringHelper.roundDouble(rs.getDouble("metas_cumplidas"),2));
                    row.put("montos_cumplidos",StringHelper.roundDouble(rs.getDouble("montos_cumplidos"),2));
                    row.put("oport_inicial",StringHelper.roundDouble(rs.getDouble("oport_inicial"),2));
                    row.put("oport_seguimiento",StringHelper.roundDouble(rs.getDouble("oport_seguimiento"),2));
                    row.put("oport_visitas",StringHelper.roundDouble(rs.getDouble("oport_visitas"),2));
                    row.put("oport_cotizacion",StringHelper.roundDouble(rs.getDouble("oport_cotizacion"),2));
                    row.put("oport_negociacion",StringHelper.roundDouble(rs.getDouble("oport_negociacion"),2));
                    row.put("oport_cierre",StringHelper.roundDouble(rs.getDouble("oport_cierre"),2));
                    row.put("oport_ganados",StringHelper.roundDouble(rs.getDouble("oport_ganados"),2));
                    row.put("oport_perdidos",StringHelper.roundDouble(rs.getDouble("oport_perdidos"),2));

                    return row;
                }
            }
        );
        return hm_registro_oportunidades;
    }

    @Override
    public ArrayList<HashMap<String, String>> getBuscadorVarios(Integer id, Integer agente, Integer tipo_seleccion, Integer status, Integer etapa, String fecha_inicial, String fecha_final, Integer id_empresa) {
        String query ="select * from crm_consultas("+id+","+agente+","+tipo_seleccion+","+status+","+etapa+",'"+fecha_inicial+"','"+fecha_final+"',"+id_empresa+")as foo("
                                                        +"prospectos_meta integer, "
                                                        + "total_contactos integer, "
                                                        + "porcentaje_cumplido double precision, "
                                                        + "contactos integer )";


            System.out.println("Resultados de la FUNCION___5____"+" "+query);
      ArrayList<HashMap<String,String>>hm_registro_varios =(ArrayList<HashMap<String,String>>)this.jdbcTemplate.query(
            query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("prospectos_meta",String.valueOf(rs.getInt("prospectos_meta")));
                    row.put("total_contactos",String.valueOf(rs.getInt("total_contactos")));
                    row.put("porcentaje_cumplido",StringHelper.roundDouble(rs.getDouble("porcentaje_cumplido"),1));
                    row.put("contactos",String.valueOf(rs.getInt("contactos")));




                    return row;
                }
            }
        );
        return hm_registro_varios;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getContactos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc, Integer id_empresa) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

        String sql_to_query = "SELECT crm_contactos.id, crm_contactos.nombre||' '||crm_contactos.apellido_paterno||' '||"
                + "crm_contactos.apellido_materno as contacto "
                + ",(CASE WHEN tipo_contacto=1 THEN 'Cliente' ELSE 'Prospecto' END) as tipo_contacto "
                +"FROM crm_contactos "
                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = crm_contactos.id "
                +"WHERE crm_contactos.borrado_logico=false "
                +"and crm_contactos.gral_emp_id= " +id_empresa
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ? ";


        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("tipo_contacto",rs.getString("tipo_contacto"));
                    row.put("contacto",rs.getString("contacto"));

                    return row;
                }
            }
        );
        return hm;
    }

    @Override
    public ArrayList<HashMap<String, String>> getContacto_Datos(Integer id) {

        String sql_to_query = "SELECT crm_contactos.*, ("
                + "CASE WHEN tipo_contacto=1 THEN  "
                + "(select tmp_crmcli.cxc_clie_id||'___'||cxc_clie.rfc||'___'||cxc_clie.razon_social as cliente from "
                + "(select * from crm_contacto_cli where crm_contactos_id=crm_contactos.id) as tmp_crmcli "
                + "join cxc_clie on cxc_clie.id=tmp_crmcli.cxc_clie_id limit 1) "
                + "ELSE "
                + "(select tmp_crmcli.crm_prospectos_id||'___'||crm_prospectos.rfc||'___'||crm_prospectos.razon_social as cliente "
                + "from (select * from crm_contacto_pro where crm_contactos_id=crm_contactos.id) as tmp_crmcli join "
                + "crm_prospectos on crm_prospectos.id=tmp_crmcli.crm_prospectos_id limit 1 ) "
                + "END) cliente FROM crm_contactos WHERE id="+id;

        ArrayList<HashMap<String, String>> dato_datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("apellido_paterno",rs.getString("apellido_paterno"));
                    row.put("apellido_materno",rs.getString("apellido_materno"));
                    row.put("telefono1",rs.getString("telefono1"));
                    row.put("telefono2",rs.getString("telefono2"));
                    row.put("fax",rs.getString("fax"));
                    row.put("telefono_directo",rs.getString("telefono_directo"));
                    row.put("email",rs.getString("email"));
                    row.put("email2",rs.getString("email2"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("cliente",rs.getString("cliente"));

                    row.put("gral_empleado_id",String.valueOf(rs.getInt("gral_empleado_id")));
                    row.put("tipo_contacto",String.valueOf(rs.getInt("tipo_contacto")));
                    row.put("gral_emp_id",String.valueOf(rs.getInt("gral_emp_id")));
                    row.put("gral_suc_id",String.valueOf(rs.getInt("gral_suc_id")));

                    return row;
                }
            }
        );
        return dato_datos;
    }

    //CRM  Reportes
    @Override
    public ArrayList<HashMap<String, String>> getVisitas(String fecha_inicial, String fecha_final,Integer id_empresa) {
        String sql_to_query = "select gral_empleados.clave,"
+" crm_registro_visitas.gral_empleado_id ,  "
+" gral_empleados.nombre_pila||'  '||gral_empleados.apellido_paterno||'  '||gral_empleados.apellido_materno as nombre_empleado,  "
+" crm_registro_visitas.fecha as fecha_visita,  "
+" crm_motivos_visita.folio_mv,  "
+" crm_motivos_visita.descripcion as motivo_visita,  "
+" crm_contactos.nombre||'  '||crm_contactos.apellido_paterno||'  '||crm_contactos.apellido_materno as nombre_contacto,  "
+" crm_calificaciones_visita.titulo as calificacion_visita,  "
+" crm_tipos_seguimiento_visita.titulo as tipo_seguimiento_visita,  "
+" (case when crm_registro_visitas.deteccion_oportunidad=1 then 'SI' else 'NO' end ) as existe_oportunidad,  "
+" crm_registro_visitas.resultado  "
+" from crm_registro_visitas  "
+" join gral_empleados on gral_empleados.id=crm_registro_visitas.gral_empleado_id  "
+" join crm_motivos_visita on crm_motivos_visita.id=crm_registro_visitas.crm_motivos_visita_id  "
+" join crm_contactos on crm_contactos.id=crm_registro_visitas.crm_contacto_id  "
+" join crm_calificaciones_visita on crm_calificaciones_visita.id=crm_registro_visitas.crm_calificacion_visita_id  "
+" join crm_tipos_seguimiento_visita on crm_tipos_seguimiento_visita.id=crm_registro_visitas.crm_tipos_seguimiento_visita_id  "
+" WHERE to_char(crm_registro_visitas.momento_creacion,'yyyymmdd')::integer between to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::integer AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::integer "
+" AND crm_registro_visitas.gral_emp_id="+id_empresa+" ORDER BY gral_empleados.nombre_pila desc;";
System.out.println("DATOS del registro de Visitas:  "+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("clave",rs.getString("clave")  );
                    row.put("gral_empleado_id",rs.getString("gral_empleado_id")  );
                    row.put("nombre_empleado",rs.getString("nombre_empleado")  );
                    row.put("fecha_visita",rs.getString("fecha_visita")  );
                    row.put("folio_mv",rs.getString("folio_mv")  );
                    row.put("motivo_visita",rs.getString("motivo_visita")  );
                    row.put("nombre_contacto",rs.getString("nombre_contacto")  );
                    row.put("calificacion_visita",rs.getString("calificacion_visita")  );
                    row.put("tipo_seguimiento_visita",rs.getString("tipo_seguimiento_visita")  );
                    row.put("existe_oportunidad",rs.getString("existe_oportunidad")  );
                    row.put("resultado",rs.getString("resultado")  );

                    return row;
                }
            }
        );
        return hm;
    }
    //fin del reporte de visitas

}

