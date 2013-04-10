/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.springdaos;

import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.interfacedaos.EnvInterfaceDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
/**
 *
 * @author Noe Martinez
 * gpmarsan@gmail.com
 * 09/abril/2012
 */
public class EnvSpringDao implements EnvInterfaceDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    //metodos  de uso general
    @Override
    public int countAll(String data_string) {
        String sql_busqueda = "select id from env_bus_aplicativos('"+data_string+"') as foo (id integer)";
        String sql_to_query = "select count(id)::int as total from ("+sql_busqueda+") as subt";

        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        return rowCount;
    }
    
    
    @Override
    public HashMap<String, String> selectFunctionValidateAplicativo(String data, String extra_data_array) {
        String sql_to_query = "select env_validaciones from env_validaciones('"+data+"',array["+extra_data_array+"]);";
        //System.out.println("Validacion:"+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);

        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
            sql_to_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("success",rs.getString("env_validaciones"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public String selectFunctionForThisApp(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from env_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";

        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        valor_retorno = update.get("env_adm_procesos").toString();
        return valor_retorno;
    }
    
    
    

    @Override
    public ArrayList<HashMap<String, String>> getBuscadorProductos(String sku, String tipo, String descripcion, Integer id_empresa) {
        String where = "";
	if(!sku.equals("")){
		where=" AND inv_prod.sku ilike '%"+sku+"%'";
	}
	if(!tipo.equals("0")){
		where +=" AND inv_prod.tipo_de_producto_id="+tipo;
	}
	if(!descripcion.equals("")){
		where +=" AND inv_prod.descripcion ilike '%"+descripcion+"%'";
	}

        String sql_to_query = ""
                + "SELECT "
				+"inv_prod.id,"
				+"inv_prod.sku,"
                                +"inv_prod.descripcion, "
                                + "inv_prod.unidad_id, "
                                + "inv_prod_unidades.titulo AS unidad, "
				+"inv_prod_tipos.titulo AS tipo,"
                                + "inv_prod_unidades.decimales "
		+"FROM inv_prod "
                + "LEFT JOIN inv_prod_tipos ON inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
                + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "WHERE inv_prod.empresa_id="+id_empresa+" "
                + "AND inv_prod.borrado_logico=false "+where+" "
                + "ORDER BY inv_prod.descripcion;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);

        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("decimales",String.valueOf(rs.getInt("decimales")));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }
    
    
    //busca datos de un producto en especifico a partir del codigo
    @Override
    public ArrayList<HashMap<String, String>> getDataProductBySku(String codigo, Integer id_empresa) {
        String sql_to_query = ""
                + "SELECT "
                    +"inv_prod.id,"
                    +"inv_prod.sku,"
                    +"inv_prod.descripcion, "
                    + "inv_prod.unidad_id, "
                    + "inv_prod_unidades.titulo AS unidad, "
                    +"inv_prod_tipos.titulo AS tipo,"
                    + "inv_prod_unidades.decimales "
		+"FROM inv_prod "
                + "LEFT JOIN inv_prod_tipos ON inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
                + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "WHERE inv_prod.empresa_id="+id_empresa+" "
                + "AND inv_prod.borrado_logico=false "
                + "AND inv_prod.sku='"+codigo.toUpperCase()+"' "
                + "LIMIT 1;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("decimales",String.valueOf(rs.getInt("decimales")));
                    return row;
                }
            }
        );
        return hm;
    }

    
    
    //obtiene tipos de productos
    @Override
    public ArrayList<HashMap<String, String>> getProductoTipos() {
	String sql_query = "SELECT DISTINCT id,titulo FROM inv_prod_tipos WHERE borrado_logico=false order by titulo ASC;";
        ArrayList<HashMap<String, String>> hm_tp = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );

        return hm_tp;
    }
    
    
    
    //obtiene las presentaciones de un producto
    //cuando id=0, obtiene todas las presentaciones
    //cuando id es diferente de 0, obtiene las presentaciones no asignadas a un producto en especifico
    @Override
    public ArrayList<HashMap<String, String>> getProductoPresentaciones(Integer id_producto, Integer id_empresa) {
        String sql_query="";
        if(id_producto != 0){
            sql_query = "SELECT id,titulo FROM inv_prod_presentaciones WHERE id NOT IN (SELECT presentacion_id FROM  inv_prod_pres_x_prod WHERE producto_id = "+id_producto+") order by titulo;";
        }else{
            sql_query = "SELECT id,titulo FROM inv_prod_presentaciones WHERE borrado_logico=FALSE order by titulo;";
        }
        
        ArrayList<HashMap<String, String>> hm_pres= (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_pres;
    }
    
    
    //obtiene las presentaciones Asignadas a un producto en especifico
    @Override
    public ArrayList<HashMap<String, String>> getProductoPresentacionesON(Integer id_producto) {
        String sql_query = "SELECT id,titulo FROM inv_prod_presentaciones WHERE id IN (SELECT presentacion_id FROM  inv_prod_pres_x_prod WHERE producto_id = "+id_producto+") order by titulo;";
        
        ArrayList<HashMap<String, String>> hm_pres_on = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_pres_on;
    }
    
    
    
    
    //###### METODOS PARA CONFIGURACION DE ENVASADO ###############
    
    //Este metodo es para obtener datos del Grid y Paginado
    @Override
    public ArrayList<HashMap<String, Object>> getEnvConf_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from env_bus_aplicativos(?) as foo (id integer)";
	String sql_to_query = ""
                + "SELECT DISTINCT "
                    + "env_conf.id,"
                    + "inv_prod.sku AS codigo,"
                    + "inv_prod.descripcion,"
                    + "inv_prod_unidades.titulo AS unidad,"
                    + "inv_prod_presentaciones.titulo AS presentacion "
                + "FROM env_conf "
                + "JOIN inv_prod ON inv_prod.id=env_conf.inv_prod_id "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=env_conf.inv_prod_presentacion_id "
                + "JOIN ("+sql_busqueda+") as subt on subt.id=env_conf.id "
                + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("data_string: "+data_string);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("presentacion",rs.getString("presentacion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getEnvConf_Datos(Integer id) {
        String sql_to_query = ""
                + "SELECT "
                    + "env_conf.id,"
                    + "env_conf.inv_prod_id,"
                    + "env_conf.inv_prod_presentacion_id,"
                    + "inv_prod.sku AS codigo,"
                    + "inv_prod.descripcion,"
                    + "inv_prod_unidades.titulo AS unidad "
                + "FROM env_conf "
                + "JOIN inv_prod ON inv_prod.id=env_conf.inv_prod_id "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "WHERE env_conf.id=?;";
        
        //System.out.println("sql_to_query: "+sql_to_query);
        //System.out.println("id: "+id);
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("producto_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("presentacion_id",String.valueOf(rs.getInt("inv_prod_presentacion_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //###### TERMINA METODOS PARA CONFIGURACION DE ENVASADO ############################################
    
    
    


    
    
}
