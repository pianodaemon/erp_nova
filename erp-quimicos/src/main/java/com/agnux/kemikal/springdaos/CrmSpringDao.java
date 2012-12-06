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
    public int countAll(String data_string) {
        String sql_busqueda = "select id from gral_bus_catalogos('"+data_string+"') as foo (id integer)";
        String sql_to_query = "select count(id)::int as total from ("+sql_busqueda+") as subt";
        
        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        return rowCount;
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
}
