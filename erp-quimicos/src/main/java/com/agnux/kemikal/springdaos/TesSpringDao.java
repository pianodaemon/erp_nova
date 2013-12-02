/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.springdaos;
import com.agnux.kemikal.interfacedaos.TesInterfaceDao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Paco Mora
 */
public class TesSpringDao implements TesInterfaceDao{
    private JdbcTemplate jdbcTemplate;
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array) {
        String sql_to_query = "select erp_fn_validaciones_por_aplicativo from erp_fn_validaciones_por_aplicativo('"+data+"',"+idApp+",array["+extra_data_array+"]);";
        System.out.println("Validacion:"+sql_to_query);
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
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        System.out.println("Ejacutando Guardar:"+sql_to_query);
        //int update = this.getJdbcTemplate().queryForInt(sql_to_query);
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
    
    
    //Metodos para catalogo TesMovTipos
    @Override
    public ArrayList<HashMap<String, Object>> getTesMovTiposGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT tes_mov_tipos.id,tes_mov_tipos.titulo,tes_mov_tipos.descripcion,tes_mov_tipos.tipo, "
                + "tes_mov_tipos.grupo ,tes_mov_tipos.consecutivo ,tes_mov_tipos.conciliacion "
                            + "FROM tes_mov_tipos "
                            + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = tes_mov_tipos.id "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        System.out.println("getInvSeccionesGrid: "+sql_to_query+"    "+data_string);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("grupo",(rs.getInt("grupo") == 1 ? "Dep&oacute;sito" : (rs.getInt("id") == 2 ? "Cheque" : "Otros")) );
                    row.put("tipo",String.valueOf(rs.getBoolean("tipo")) == "true" ? "Abono" : "Cargo" );
                    row.put("conciliacion",String.valueOf(rs.getBoolean("conciliacion")) == "true" ? "Si" : "No" );
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    //obtiene datos de TesMovTipos actual
    @Override
    public ArrayList<HashMap<String, String>> getTesMovTipos_Datos(Integer id) {
        String sql_query = "SELECT id, titulo, descripcion,tipo, grupo, consecutivo, conciliacion FROM tes_mov_tipos WHERE id = ? and borrado_logico=false";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("grupo",(rs.getInt("grupo") == 1 ? "Dep&oacute;sito" : (rs.getInt("id") == 2 ? "Cheque" : "Otros")) );
                    row.put("tipo",String.valueOf(rs.getBoolean("tipo")) );
                    row.put("consecutivo",String.valueOf(rs.getBoolean("consecutivo")));
                    row.put("conciliacion",String.valueOf(rs.getBoolean("conciliacion")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Metodos para catalogo TesBan
    @Override
    public ArrayList<HashMap<String, Object>> getTesBanGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT tes_ban.id,tes_ban.titulo, tes_ban.descripcion "
                            + "FROM tes_ban "
                            + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = tes_ban.id "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        System.out.println("getTesBanGrid: "+sql_to_query+"    "+data_string);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    //obtiene datos de TesBan actual
    @Override
    public ArrayList<HashMap<String, String>> getTesBan_Datos(Integer id) {
        String sql_query = "SELECT id, titulo, descripcion FROM tes_ban WHERE id = ? and borrado_logico=false";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    //Metodos para catalogo TesCon
    @Override
    public ArrayList<HashMap<String, Object>> getTesConGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT tes_con.id,"
                                + "tes_con.titulo, "
                                + "tes_con.descripcion, "
                                + "tes_con.tipo "
                            + "FROM tes_con "
                            + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = tes_con.id "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        System.out.println("getTesBanGrid: "+sql_to_query+"    "+data_string);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("tipo",String.valueOf(rs.getBoolean("tipo")).equals("true") ? "Abono" : "Cargo");
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    //obtiene datos de TesCon actual
    @Override
    public ArrayList<HashMap<String, String>> getTesCon_Datos(Integer id) {
        String sql_query = "SELECT id, titulo, descripcion, tipo FROM tes_con WHERE id = ? and borrado_logico=false";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("tipo",String.valueOf(rs.getBoolean("tipo")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    
    //Metodos para catalogo chequeras
    @Override
    public ArrayList<HashMap<String, Object>> getTesChequeraGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT tes_che.id, "
                                + "tes_che.titulo AS chequera, "
                                + "gral_mon.descripcion_abr AS moneda, "
                                + "tes_ban.titulo as banco, "
                                + "tes_che.num_sucursal, "
                                + "tes_che.nombre_sucursal, "
                                + "tes_che.clabe "
                                + "FROM  tes_che "
                                + "JOIN gral_mon ON gral_mon.id=tes_che.moneda_id "
                                + "JOIN tes_ban ON tes_ban.id=tes_che.tes_ban_id "
                            + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = tes_che.id "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        System.out.println("Datos del Gris de Chequera: "+sql_to_query+"    "+data_string);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("chequera",rs.getString("chequera"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("banco",rs.getString("banco"));
                    row.put("numero_sucursal",rs.getString("num_sucursal"));
                    row.put("nombre_sucursal",rs.getString("nombre_sucursal"));
                    //row.put("clabe",rs.getString("clabe"));
                    
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    //obtiene datos de TesCon actual
    @Override
    public ArrayList<HashMap<String, String>> getTesChequera_Datos(Integer id) {
        String sql_query = "select  id, "
                + "titulo as chequera, "
                + "aut_modif_consecutivo as chk_modificar_consecutivo, "
                + "aut_modif_fecha  as chk_modificar_fecha , "
                + "aut_modif_cheque  as chk_modificar_cheque, "
                + "gral_pais_id as pais_id,"
                + "gral_mun_id as municipio_id,  "
                + "gral_edo_id as estado_id, "
                + "moneda_id , "
                + "tes_ban_id as banco_id, "
                + "imp_cheque_ingles as chk_imp_cheque_ingles , "
                + "calle  , "
                + "numero , "
                + "colonia, "
                + "codigo_postal , "
                + "num_sucursal as numero_sucursal , "
                + "nombre_sucursal, "
                + "telefono1 , "
                + "extencion1 , "
                + "telefono2, "
                + "extencion2  , "
                + "fax  , "
                + "gerente  , "
                + "ejecutivo , "
                + "email "
            + "FROM tes_che "
            + "WHERE id = ? and borrado_logico=false";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("chequera",rs.getString("chequera"));
                    row.put("chk_modificar_consecutivo",String.valueOf(rs.getBoolean("chk_modificar_consecutivo")));
                    row.put("chk_modificar_fecha",  String.valueOf(rs.getBoolean("chk_modificar_fecha")));
                    row.put("chk_modificar_cheque",  String.valueOf(rs.getBoolean("chk_modificar_cheque")));
                    row.put("pais_id",rs.getString("pais_id"));
                    row.put("municipio_id",rs.getString("municipio_id"));
                    row.put("estado_id",rs.getString("estado_id"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("banco_id",rs.getString("banco_id"));
                    row.put("chk_imp_cheque_ingles",  String.valueOf(rs.getBoolean("chk_imp_cheque_ingles")));
                    row.put("calle",rs.getString("calle"));
                    row.put("numero",rs.getString("numero"));
                    row.put("colonia",rs.getString("colonia"));
                    row.put("codigo_postal",rs.getString("codigo_postal"));
                    row.put("numero_sucursal",rs.getString("numero_sucursal"));
                    row.put("nombre_sucursal",rs.getString("nombre_sucursal"));
                    row.put("telefono1",rs.getString("telefono1"));
                    row.put("extencion1",rs.getString("extencion1"));
                    row.put("telefono2",rs.getString("telefono2"));
                    row.put("extencion2",rs.getString("extencion2"));
                    row.put("fax",rs.getString("fax"));
                    row.put("gerente",rs.getString("gerente"));
                    row.put("ejecutivo",rs.getString("ejecutivo"));
                    row.put("email",rs.getString("email"));
                    
                    
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getBancos(Integer idEmpresa) {
        String sql_to_query = "select distinct id as id_banco, titulo as banco from tes_ban "
                +"where borrado_logico=false and gral_emp_id="+idEmpresa+" "
                +"order by titulo ASC;";
        
        ArrayList<HashMap<String, String>> hm_bancos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_banco",rs.getInt("id_banco"));
                    row.put("banco",rs.getString("banco"));
                    return row;
                }
            }
        );
        return hm_bancos;
    }
    
    @Override
    public ArrayList<HashMap<String, String>> getMonedas() {
        String sql_to_query = "SELECT id, descripcion FROM  gral_mon WHERE borrado_logico=FALSE ORDER BY id ASC;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_monedas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm_monedas;
    }

      @Override
    public ArrayList<HashMap<String, String>> getPaises() {
        //String sql_to_query = "SELECT DISTINCT cve_pais ,pais_ent FROM municipios;";
        String sql_to_query = "SELECT DISTINCT id as id_pais, titulo as pais FROM gral_pais;";
        
        ArrayList<HashMap<String, String>> pais = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_pais",rs.getString("id_pais"));
                    row.put("pais",rs.getString("pais"));
                    return row;
                }
            }
        );
        return pais;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getEntidadesForThisPais(String id_pais) {
        String sql_to_query = "SELECT id as id_Estado, titulo as Estado FROM gral_edo WHERE pais_id="+id_pais+" order by Estado;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_Estado",rs.getString("id_Estado"));
                    row.put("Estado",rs.getString("Estado"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getMunicipiosForThisEntidad(String id_pais, String id_entidad) {
        String sql_to_query = "SELECT id as id_municipio, titulo as municipio FROM gral_mun WHERE estado_id="+id_entidad+" and pais_id="+id_pais+" order by municipio;";
        
        System.out.println("Ejecutando query loc_for_this_entidad: "+sql_to_query);
        System.out.println(sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_municipio",rs.getString("id_municipio"));
                    row.put("municipio",rs.getString("municipio"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
