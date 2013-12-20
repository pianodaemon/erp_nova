/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.springdaos;
import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.interfacedaos.CtbInterfaceDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author Noe Martinez
 * gpmarsan@gmail.com
 * 22/03/2012
 */
public class CtbSpringDao implements CtbInterfaceDao{
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
    public String selectFunctionForCtbAdmProcesos(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from ctb_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";
        
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        valor_retorno = update.get("ctb_adm_procesos").toString();
        return valor_retorno;
    }
    
    
    
    @Override
    public int countAll(String data_string) {
        String sql_busqueda = "select id from gral_bus_catalogos('"+data_string+"') as foo (id integer)";
        String sql_to_query = "select count(id)::int as total from ("+sql_busqueda+") as subt";
        
        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        return rowCount;
    }
    
    
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getCentroCostos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT   ctb_cc.id, ctb_cc.titulo, ctb_cc.descripcion FROM ctb_cc "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = ctb_cc.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
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
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCentroCosto_Datos(Integer id) {
        String sql_query = "SELECT id,titulo,descripcion FROM ctb_cc WHERE id = ?;";
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
    
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getTipoPolizas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT ctb_tpol.id,ctb_tpol.tipo,ctb_tpol.titulo, ctb_tpol_grupos.titulo as grupo "
                            + "FROM ctb_tpol "
                            + "JOIN ctb_tpol_grupos ON ctb_tpol_grupos.id = ctb_tpol.ctb_tpol_grupo_id "
                            +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = ctb_tpol.id "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("getTipoPolizas_PaginaGrid: "+sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("tipo",rs.getInt("tipo"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("grupo",rs.getString("grupo"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getTipoPoliza_Grupos() {
        String sql_query = "SELECT id, titulo FROM ctb_tpol_grupos ORDER BY id;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //obtiene datos de la poliza actual
    @Override
    public ArrayList<HashMap<String, String>> getTipoPoliza_Datos(Integer id) {
        String sql_query = "SELECT id,titulo, tipo, ctb_tpol_grupo_id AS grupo_id FROM ctb_tpol WHERE id = ?;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("grupo_id",String.valueOf(rs.getInt("grupo_id")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getConceptosContables_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT ctb_con.id, ctb_con.titulo, ctb_con.descripcion FROM ctb_con "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = ctb_con.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
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
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getConceptoContable_Datos(Integer id) {
        String sql_query = "SELECT id,titulo,descripcion FROM ctb_con WHERE id = ?;";
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
    
    

    @Override
    public ArrayList<HashMap<String, Object>> getCuentasMayor_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT ctb_may.id,ctb_may.ctb_may_clase_id, ctb_may.clasificacion, "
                                +"(CASE WHEN ctb_may.descripcion IS NULL OR ctb_may.descripcion='' THEN  (CASE WHEN ctb_may.descripcion_ing IS NULL OR ctb_may.descripcion_ing='' THEN ctb_may.descripcion_otr ELSE ctb_may.descripcion_ing END) ELSE ctb_may.descripcion END) AS descripcion, "
                                +"'Si'::character varying AS ligada_a_cuenta "
                        +"FROM ctb_may "
                        +"JOIN ctb_may_clases ON ctb_may_clases.id=ctb_may.ctb_may_clase_id "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = ctb_may.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("ctb_may_clase_id",rs.getString("ctb_may_clase_id"));
                    row.put("clasificacion",rs.getString("clasificacion"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("ligada_a_cuenta",rs.getString("ligada_a_cuenta"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCuentasMayor_Clases() {
        String sql_query = "SELECT id, titulo FROM ctb_may_clases ORDER BY id;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }

    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCuentaDeMayor_Datos(Integer id) {
        String sql_query = "SELECT id, ctb_may_clase_id, clasificacion , "
                +"(CASE WHEN ctb_may.descripcion IS NULL OR ctb_may.descripcion='' THEN  (CASE WHEN ctb_may.descripcion_ing IS NULL OR ctb_may.descripcion_ing='' THEN ctb_may.descripcion_otr ELSE ctb_may.descripcion_ing END) ELSE ctb_may.descripcion END) AS predeterminada, "
                + "descripcion, descripcion_ing, descripcion_otr FROM ctb_may WHERE id = ?;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("ctb_may_clase_id",rs.getString("ctb_may_clase_id"));
                    row.put("clasificacion",rs.getString("clasificacion"));
                    row.put("predeterminada",rs.getString("predeterminada"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("descripcion_ing",rs.getString("descripcion_ing"));
                    row.put("descripcion_otr",rs.getString("descripcion_otr"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    //**********************************************************************************
    //METODOS PARA CATALOGO DE CUENTAS CONTABLES
    //**********************************************************************************
    @Override
    public ArrayList<HashMap<String, Object>> getCuentasContables_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
                + "SELECT "
                    + "ctb_cta.id,"
                    + "cta_mayor AS m, "
                    + "clasifica AS c, "
                    + "(CASE 	WHEN nivel_cta=1 THEN rpad(cta::character varying, 4, '0')  "
                        + "WHEN nivel_cta=2 THEN '&nbsp;&nbsp;'||rpad(cta::character varying, 4, '0')||'-'||lpad(subcta::character varying, 4, '0') "
                        + "WHEN nivel_cta=3 THEN '&nbsp;&nbsp;&nbsp;&nbsp;'||rpad(cta::character varying, 4, '0')||'-'||lpad(subcta::character varying, 4, '0')||'-'||lpad(ssubcta::character varying, 4, '0')   "
                        + "WHEN nivel_cta=4 THEN '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'||rpad(cta::character varying, 4, '0')||'-'||lpad(subcta::character varying, 4, '0')||'-'||lpad(ssubcta::character varying, 4, '0')||'-'||lpad(sssubcta::character varying, 4, '0') "
                        + "WHEN nivel_cta=5 THEN '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'||rpad(cta::character varying, 4, '0')||'-'||lpad(subcta::character varying, 4, '0')||'-'||lpad(ssubcta::character varying, 4, '0')||'-'||lpad(sssubcta::character varying, 4, '0')||'-'||lpad(ssssubcta::character varying, 4, '0') "
                        + "ELSE '' "
                        + "END ) AS cuenta, "
                    + "(CASE WHEN detalle=0 THEN 'NO' WHEN detalle=1 THEN 'SI' ELSE '' END) AS detalle, "
                    + "(CASE WHEN descripcion IS NULL OR descripcion='' THEN  (CASE WHEN descripcion_ing IS NULL OR descripcion_ing='' THEN  descripcion_otr ELSE descripcion_ing END )  ELSE descripcion END ) AS descripcion, "
                    + "nivel_cta AS nivel, "
                    + "(CASE WHEN estatus=1 THEN 'ACTIVA' WHEN estatus=2 THEN 'INACTIVA' ELSE '' END) AS estatus "
                + "FROM ctb_cta "
                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = ctb_cta.id "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("m",rs.getString("m"));
                    row.put("c",rs.getString("c"));
                    row.put("cuenta",rs.getString("cuenta"));
                    row.put("detalle",rs.getString("detalle"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("nivel",rs.getString("nivel"));
                    row.put("estatus",rs.getString("estatus"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCuentasContables_Datos(Integer id) {
        String sql_query = ""
                + "SELECT "
                    + "id, "
                    + "(CASE WHEN cta=0 THEN '' ELSE cta::character varying END) AS cta, "
                    + "(CASE WHEN subcta=0 THEN '' ELSE subcta::character varying END) AS subcta, "
                    + "(CASE WHEN ssubcta=0 THEN '' ELSE ssubcta::character varying END) AS ssubcta, "
                    + "(CASE WHEN sssubcta=0 THEN '' ELSE sssubcta::character varying END) AS sssubcta, "
                    + "(CASE WHEN ssssubcta=0 THEN '' ELSE ssssubcta::character varying END) AS ssssubcta, "
                    + "cta_mayor, "
                    + "clasifica, "
                    + "detalle, "
                    + "descripcion, "
                    + "descripcion_ing, "
                    + "descripcion_otr, "
                    + "nivel_cta, "
                    + "consolida, "
                    + "estatus "
                + "FROM ctb_cta "
                + "WHERE id=?;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("cta",rs.getString("cta"));
                    row.put("subcta",rs.getString("subcta"));
                    row.put("ssubcta",rs.getString("ssubcta"));
                    row.put("sssubcta",rs.getString("sssubcta"));
                    row.put("ssssubcta",rs.getString("ssssubcta"));
                    row.put("cta_mayor",String.valueOf(rs.getInt("cta_mayor")));
                    row.put("clasifica",String.valueOf(rs.getInt("clasifica")));
                    row.put("detalle",String.valueOf(rs.getInt("detalle")));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("descripcion_ing",rs.getString("descripcion_ing"));
                    row.put("descripcion_otr",rs.getString("descripcion_otr"));
                    row.put("nivel_cta",String.valueOf(rs.getInt("nivel_cta")));
                    row.put("estatus",String.valueOf(rs.getInt("estatus")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCuentasContables_CuentasMayor(Integer id_empresa) {
        String sql_query = ""
                + "SELECT "
                    + "id, "
                    + "ctb_may_clase_id AS cta_mayor, "
                    + "clasificacion, "
                    + "(CASE WHEN descripcion IS NULL OR descripcion='' THEN (CASE WHEN descripcion_ing IS NULL OR descripcion_ing='' THEN descripcion_ing ELSE descripcion_otr END) ELSE descripcion END) AS descripcion "
                + "FROM ctb_may "
                + "WHERE borrado_logico=false "
                + "AND empresa_id=?;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("cta_mayor",String.valueOf(rs.getInt("cta_mayor")));
                    row.put("clasificacion",String.valueOf(rs.getInt("clasificacion")));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    //TERMINA METODOS PARA CATALOGO DE CUENTAS CONTABLES
    //**********************************************************************************
    
    
    
    //Medotdos para reporte de Auxiliar de Cuentas------------------------------------------------------------------------------
    //Calcular años a mostrar en el reporte de Auxiliar de Cuentas
    @Override
    public ArrayList<HashMap<String, Object>>  getCtbRepAuxCtas_Anios() {
        ArrayList<HashMap<String, Object>> anios = new ArrayList<HashMap<String, Object>>();
        
        Calendar c1 = Calendar.getInstance();
        Integer annio = c1.get(Calendar.YEAR);//obtiene el año actual
        
        for(int i=0; i<5; i++) {
            HashMap<String, Object> row = new HashMap<String, Object>();
            row.put("valor",(annio-i));
            anios.add(i, row);
        }
        return anios;
    }
    
    
    //Obtener las Subcuentas del Nivel que se le indique
    @Override
    public ArrayList<HashMap<String, Object>> getCtbRepAuxCtas_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa) {
        String sql_query="";
        
        switch(nivel) {
            case 1: 
                sql_query = "SELECT rpad(cta::character varying, 4, '0') AS cta,descripcion FROM ( SELECT DISTINCT cta, (CASE WHEN subcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=?  AND borrado_logico=FALSE AND estatus=1 ORDER BY cta ) AS sbt WHERE trim(descripcion)<>'' ORDER BY cta;";
                break;
            case 2: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT subcta AS cta, (CASE WHEN ssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY subcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 3: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssubcta AS cta, (CASE WHEN sssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND borrado_logico=FALSE AND estatus=1 ORDER BY ssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 4: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT sssubcta AS cta, (CASE WHEN ssssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY sssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 5: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssssubcta AS cta, descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+" AND sssubcta="+ssscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY ssssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
        }
        
        System.out.println("getCtasNivel "+nivel+": "+sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cta",rs.getString("cta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCtbRepAuxCtas_Datos(String data_string) {
        
        String sql_to_query = "select * from ctb_reporte(?) as foo(cuenta character varying, descripcion character varying, saldo_inicial character varying, debe character varying, haber character varying, saldo_final character varying);"; 
        System.out.println("data_string: "+data_string);
        System.out.println("Ctb_DatosRepAuxCtas:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("cuenta",rs.getString("cuenta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("saldo_inicial",rs.getString("saldo_inicial"));
                    row.put("debe",rs.getString("debe"));
                    row.put("haber",rs.getString("haber"));
                    row.put("saldo_final",rs.getString("saldo_final"));
                    return row;
                }
            }
        );
        return hm_facturas;
    }


    
}
