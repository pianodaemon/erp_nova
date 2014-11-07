/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.springdaos;

import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.interfacedaos.LogInterfaceDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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
 * 30/julio/2012
 */
public class LogSpringDao implements LogInterfaceDao{
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
    public String selectFunctionForLogAdmProcesos(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from log_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";
        
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        valor_retorno = update.get("log_adm_procesos").toString();
        return valor_retorno;
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
    
    
    
    
    
    
    
         
    //metodo que obtiene datos para el grid de Asignacion de Rutas
    @Override
    public ArrayList<HashMap<String, Object>> getRutas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT  log_rutas.id , log_rutas.folio, "
                              +"  log_vehiculos.marca || '  '||log_vehiculos.numero_economico as vehiculo, "
                              +"  log_choferes.nombre|| '  '||log_choferes.apellido_paterno|| '  '||log_choferes.apellido_materno as nombre_chofer "
                              +"  from log_rutas "
                              +"  join  log_choferes on log_choferes.id=log_rutas.log_chofer_id "
                              +"  join log_vehiculos on log_vehiculos.id=log_rutas.log_vehiculo_id " 
                              +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = log_rutas.id "
                              +"WHERE log_rutas.borrado_logico=false  "
                              +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("IMPRIMIENDO EL GRID DE RUTAS checar??: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("vehiculo",rs.getString("vehiculo"));
                    row.put("nombre_chofer",rs.getString("nombre_chofer"));
                   
                    return row;
                }
            }
        );
        return hm; 
    }

    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getchoferes(Integer id_empresa) {
        String sql_to_query = "SELECT log_choferes.id,clave,nombre||'  '||apellido_paterno||'  '||apellido_materno  as nombre_chofer FROM log_choferes "
                + "join gral_emp on gral_emp.id = log_choferes.gral_emp_id "
                + " where log_choferes.gral_emp_id="+id_empresa
                + "  ORDER BY  nombre_chofer ASC;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("IMPRIMIENDO LOS CHOFERES"+sql_to_query);
        ArrayList<HashMap<String, String>> hm_choferes = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("nombre_chofer",rs.getString("nombre_chofer"));
                    
                    return row;
                }
            }
        );
        return hm_choferes;
    }
     
     
     
    @Override
    public ArrayList<HashMap<String, String>> getvehiculo(Integer id_empresa) {
         String sql_to_query = "SELECT log_vehiculos.id, log_vehiculos.marca ||' ' ||numero_economico AS  vehiculo "
                                + "FROM log_vehiculos "
                              + "join gral_emp on gral_emp.id = log_vehiculos.gral_emp_id "
                              + "WHERE log_vehiculos.borrado_logico=FALSE AND log_vehiculos.gral_emp_id="+id_empresa
                              + " ORDER BY  log_vehiculos.marca ASC;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("OBTENIENDO LOS VEHICULOS"+sql_to_query);
        ArrayList<HashMap<String, String>> hm_vehiculo = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("vehiculo",rs.getString("vehiculo"));
                    return row;
                }
            }
        );
        return hm_vehiculo;
    }
    
    
    
    
    
    //obtiene las facturas  que seran enviadas a la ruta
    @Override
    public ArrayList<HashMap<String, String>> getFacturas_entrega_mercancia(Integer id_empresa, String fecha_inicial,String fecha_final, String factura, Integer tipo_busqueda) {
        String where = "";
        String[] fi ;
        String[] ff ;
        
        if(tipo_busqueda==1){
            fi = fecha_inicial.split("-");
            ff = fecha_final.split("-");
            
            fecha_inicial=fi[0]+fi[1]+fi[2];
            fecha_final=ff[0]+ff[1]+ff[2];
            
            where = " AND to_char(fac_docs.momento_creacion,'yyyymmdd')::integer BETWEEN "+fecha_inicial+" AND "+fecha_final+" "
                    + " AND fac_docs.enviar_ruta=TRUE ";
        }
        
        if(tipo_busqueda==2){
            where = " AND fac_docs.serie_folio='"+factura+"' ";
        }
        
        String sql_to_query = ""
                + "SELECT "
                      +"inv_prod.id as id_invprod,fac_docs.id as id_fac_docs, fac_docs.serie_folio as factura,"
                      +"to_char(fac_docs.momento_creacion,'dd-mm-yyyy') as fecha_factura,"
                      +"cxc_clie.razon_social as cliente,"
                      +"inv_prod.sku as codigo,"
                      +"fac_docs_detalles.cantidad,"
                      +"fac_docs_detalles.precio_unitario,"
                      +"inv_prod.descripcion,"
                      +"(fac_docs_detalles.cantidad * fac_docs_detalles.precio_unitario ) as importe "
                  +"FROM  fac_docs "
                  +"join fac_docs_detalles on fac_docs_detalles.fac_doc_id= fac_docs.id "
                  +"join inv_prod on inv_prod.id =fac_docs_detalles.inv_prod_id "
                  +"join cxc_clie on cxc_clie.id=  fac_docs.cxc_clie_id    "
                  +"join erp_proceso on erp_proceso.id=fac_docs.proceso_id  "
                  +"where fac_docs.cancelado=FALSE "
                + "AND erp_proceso.empresa_id="+id_empresa+" "+where;
                
                
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Obteniendo facturas a entregar:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas_entregar = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_invprod",String.valueOf(rs.getInt("id_invprod")));
                    row.put("id_fac_docs",String.valueOf(rs.getInt("id_fac_docs")));
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getString("precio_unitario"),2));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("importe",StringHelper.roundDouble(rs.getString("importe"),2));
                    
                    return row;
                }
            }
        );
        return hm_facturas_entregar;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getFacturas_fac_rev_cobro_detalle(Integer id_empresa, String folio_fac_rev_cobro) {
        
        String sql_to_query = ""
            + "SELECT  "
                + "cxc_fac_rev_cob.folio as folio_programacion, "
                +"cxc_fac_rev_cob_detalle.id AS fac_rev_cob_detalle_id,  "
                +"erp_h_facturas.id AS erp_h_fac_id, "
                +"erp_h_facturas.serie_folio as factura, "
                +"to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') as fecha_factura,  "
                +"cxc_clie.razon_social AS cliente,  "
                +"erp_h_facturas.saldo_factura, "
                + "(CASE WHEN  cxc_fac_rev_cob_detalle.revision_cobro='R' THEN 'REVISION' WHEN  cxc_fac_rev_cob_detalle.revision_cobro='C' THEN 'COBRO' ELSE '' END) AS revision_cobro, "
                +"erp_h_facturas.estatus_revision  "
            +"FROM cxc_fac_rev_cob_detalle "
            +"JOIN erp_h_facturas ON erp_h_facturas.id=cxc_fac_rev_cob_detalle.erp_h_facturas_id "
            +"JOIN cxc_clie ON cxc_clie.id=erp_h_facturas.cliente_id  "
            +"join cxc_fac_rev_cob on cxc_fac_rev_cob.id=cxc_fac_rev_cob_detalle.cxc_fac_rev_cob_id "
            +"WHERE cxc_fac_rev_cob.folio='"+folio_fac_rev_cobro+"' "
            +"AND erp_h_facturas.empresa_id="+id_empresa+" "
            +"AND erp_h_facturas.enviado=FALSE "
            + "order by factura asc"; 
            
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        System.out.println("Obteniendo fac_rev_cobro:::   "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas_rev_cobro = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("folio_programacion",rs.getString("folio_programacion"));
                    row.put("fac_rev_cob_detalle_id",String.valueOf(rs.getInt("fac_rev_cob_detalle_id")));
                    row.put("erp_h_fac_id",String.valueOf(rs.getInt("erp_h_fac_id")));
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("saldo_factura",rs.getString("saldo_factura"));
                    row.put("revision_cobro",rs.getString("revision_cobro"));
                    row.put("estatus_revision",rs.getString("estatus_revision"));
                    return row;
                }
            }
        );
        return hm_facturas_rev_cobro;
    }
    
    
    
    
    
    //obtiene datos de la ruta para ver detalles y editar
    @Override
    public ArrayList<HashMap<String, String>> getdatos_editar_header(Integer id) {
        String sql_to_query = ""
                + "SELECT  "
                    + "log_vehiculos.id as id_vehiculo,  "
                    + "log_vehiculos.marca,  "
                    + "log_vehiculos.numero_economico, "
                    + "log_choferes.id as id_chofer,  "
                    + "log_choferes.nombre||' '||log_choferes.apellido_paterno||' '||  log_choferes.apellido_materno as nombre_chofer,  "
                    + "log_rutas.id as id_ruta,  "
                    + "log_rutas.folio, "
                    + "log_rutas.confirmado "
                + "FROM log_rutas "
                + "join log_choferes on log_choferes.id=log_rutas.log_chofer_id "
                + "join log_vehiculos on log_vehiculos.id=log_rutas.log_vehiculo_id "
                + "where log_rutas.id="+id;
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Onteniedo datos header:::   "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_header = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_vehiculo",String.valueOf(rs.getInt("id_vehiculo")));
                    row.put("marca",rs.getString("marca"));
                    row.put("numero_economico",rs.getString("numero_economico"));
                    row.put("id_chofer",String.valueOf(rs.getInt("id_chofer")));
                    row.put("nombre_chofer",rs.getString("nombre_chofer"));
                    row.put("id_ruta",String.valueOf(rs.getInt("id_ruta")));
                    row.put("folio",rs.getString("folio"));
                    row.put("confirmado",String.valueOf(rs.getBoolean("confirmado")));
                    
                    return row;
                }
            }
        );
        return hm_header;
    }
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getdatos_editar_minigridRutas(Integer id_empresa,Integer id_ruta) {
             
                String sql_to_query = ""
                        + "SELECT "
                            + "log_rutas_detalle.id AS id_detalle,"
                            + "inv_prod.id as id_invprod, fac_docs.id as id_fac_docs,  "
                            + "fac_docs.serie_folio as factura, "
                            + "to_char(fac_docs.momento_creacion,'dd/mm/yyyy') as fecha_factura, "
                            + "cxc_clie.razon_social as cliente, "
                            + "inv_prod.sku as codigo, "
                            + "fac_docs_detalles.cantidad, fac_docs_detalles.precio_unitario, "
                            + "inv_prod.descripcion, "
                            + "(fac_docs_detalles.cantidad * fac_docs_detalles.precio_unitario ) as importe,  (CASE WHEN fac_docs_detalles.enviado=TRUE THEN log_rutas_detalle.envase ELSE '' END) AS envase, "
                            + "fac_docs_detalles.enviado "
                        + "FROM log_rutas_detalle "
                        + "join fac_docs on fac_docs.id=log_rutas_detalle.fac_docs_id  "
                        + "join fac_docs_detalles on fac_docs_detalles.fac_doc_id= fac_docs.id "
                        + "join inv_prod on inv_prod.id=log_rutas_detalle.inv_prod_id  "
                        + "join cxc_clie on cxc_clie.id=fac_docs.cxc_clie_id  "
                        + "AND fac_docs.cancelado= false and log_rutas_detalle.log_ruta_id="+id_ruta+" "
                        + "AND log_rutas_detalle.inv_prod_id=fac_docs_detalles.inv_prod_id "
                        + "ORDER BY log_rutas_detalle.id";
                
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Obteniendo Asignadas a Ruta :::"+ sql_to_query);
        ArrayList<HashMap<String, String>> hm_header = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_detalle",String.valueOf(rs.getInt("id_detalle")));
                    row.put("id_invprod",String.valueOf(rs.getInt("id_invprod")));
                    row.put("id_fac_docs",String.valueOf(rs.getInt("id_fac_docs")));
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("cantidad",rs.getString("cantidad"));
                    row.put("precio_unitario",rs.getString("precio_unitario"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("importe",rs.getString("importe"));
                    row.put("envase",rs.getString("envase"));
                    row.put("enviado",String.valueOf(rs.getBoolean("enviado")));
                    
                    return row;
                }
            }
        );
        return hm_header;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getdatos_editar_minigridFRC(Integer id_ruta) {
        
        String sql_to_query = ""
                + "SELECT "
                    + "cxc_fac_rev_cob.folio as folio_programacion, "
                    + "log_rutas_detalle_cobro.id AS id_detalle,"
                    + "cxc_fac_rev_cob_detalle.id AS fac_rev_cob_detalle_id,  "
                    + "erp_h_facturas.id AS erp_h_fac_id, "
                    + "erp_h_facturas.serie_folio as factura, "
                    + "to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') as fecha_factura,  "
                    + "cxc_clie.razon_social AS cliente,  "
                    + "erp_h_facturas.saldo_factura,  "
                    + "(CASE WHEN  erp_h_facturas.estatus_revision=1 THEN 'REVISION' WHEN  erp_h_facturas.estatus_revision=2 THEN 'COBRO' ELSE '' END) AS revision_cobro, "
                    + "erp_h_facturas.enviado "
                + "FROM log_rutas_detalle_cobro  "
                + "JOIN cxc_fac_rev_cob_detalle on cxc_fac_rev_cob_detalle.id=log_rutas_detalle_cobro.cxc_fac_rev_cob_detalle_id   "
                + "JOIN erp_h_facturas ON erp_h_facturas.id=cxc_fac_rev_cob_detalle.erp_h_facturas_id "
                + "JOIN cxc_clie ON cxc_clie.id=erp_h_facturas.cliente_id  "
                + "join cxc_fac_rev_cob on cxc_fac_rev_cob.id=cxc_fac_rev_cob_detalle.cxc_fac_rev_cob_id  "
                + "WHERE log_rutas_detalle_cobro.log_ruta_id="+id_ruta+" "
                + "ORDER BY log_rutas_detalle_cobro.id;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Obteniendo datos de facturas para enviar a revision y cobro :::"+ sql_to_query);
        ArrayList<HashMap<String, String>> hm_header = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("folio_programacion",rs.getString("folio_programacion"));
                    row.put("id_detalle",String.valueOf(rs.getInt("id_detalle")));
                    row.put("fac_rev_cob_detalle_id",String.valueOf(rs.getInt("fac_rev_cob_detalle_id")));
                    row.put("erp_h_fac_id",String.valueOf(rs.getInt("erp_h_fac_id")));
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("saldo_factura",rs.getString("saldo_factura"));
                    row.put("revision_cobro",rs.getString("revision_cobro"));
                    row.put("enviado",String.valueOf(rs.getBoolean("enviado")));
                    return row;
                }
            }
        );
        return hm_header;
    }
    
    
    
    
    
    //metoodo para pdf de ruta
    @Override
    public HashMap<String, String> getRuta_DatosPdf(Integer id_ruta) {
        
        HashMap<String, String> datos = new HashMap<String, String>();
        
        String sql_query = ""
                + "SELECT "
                    + "folio,"
                    + "fecha,"
                    + "(CASE WHEN num_mes=1 THEN 'Ene'	WHEN num_mes=2 THEN 'Feb' WHEN num_mes=3 THEN 'Mar' WHEN num_mes=4 THEN 'Abr' WHEN num_mes=5 THEN 'May' WHEN num_mes=6 THEN 'Jun' WHEN num_mes=7 THEN 'Jul' WHEN num_mes=8 THEN 'Ago' WHEN num_mes=9 THEN 'Sep' WHEN num_mes=10 THEN 'Oct' WHEN num_mes=11 THEN 'Nov' WHEN num_mes=12 THEN 'Dic' ELSE '' END ) AS nombre_mes, "
                    + "clave_chofer, "
                    + "nombre_chofer, "
                    + "marca_vehiculo "
                + "FROM ( "
                    + "SELECT  "
                        + "log_rutas.folio, "
                        + "to_char(log_rutas.momento_creacion,'dd-mm-yyyy') AS fecha, "
                        + "EXTRACT(MONTH FROM log_rutas.momento_creacion) AS num_mes, "
                        + "log_choferes.clave AS clave_chofer,"
                        + "log_choferes.nombre||' '||log_choferes.apellido_paterno||' '||log_choferes.apellido_materno AS nombre_chofer, "
                        + "log_vehiculos.marca AS marca_vehiculo "
                    + "FROM log_rutas "
                    + "JOIN log_choferes ON log_choferes.id=log_rutas.log_chofer_id "
                    + "JOIN log_vehiculos ON log_vehiculos.id=log_rutas.log_vehiculo_id "
                    + "WHERE log_rutas.id="+id_ruta
                + ")AS sbt";
        
        //System.out.println("DATOS PARA EL PDF:"+sql_query);
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_query);
        
        datos.put("folio", map.get("folio").toString());
        datos.put("fecha", map.get("fecha").toString());
        datos.put("nombre_mes", map.get("nombre_mes").toString() );
        datos.put("clave_chofer", map.get("clave_chofer").toString() );
        datos.put("nombre_chofer", map.get("nombre_chofer").toString() );
        datos.put("clave_vehiculo", "" );
        datos.put("marca_vehiculo", map.get("marca_vehiculo").toString() );
        datos.put("hora_salida", "" );
        datos.put("hora_llegada", "" );
        
        return datos;
    }
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getRuta_ListaFacturasPdf(Integer id_ruta) {
        String sql_query = ""
            + "SELECT * "
            + "FROM ("
                + "SELECT 'Material'::character varying  AS tipo,"
                    + "1::integer as numero_tipo, "
                    + "fac_docs.serie_folio AS factura,"
                    + "cxc_clie.numero_control AS no_cliente,"
                    + "cxc_clie.razon_social AS cliente,"
                    + "fac_docs_detalles.cantidad,"
                    + "inv_prod_unidades.titulo AS unidad,"
                    + "inv_prod.descripcion,"
                    + "(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) AS importe,"
                    + "log_rutas_detalle.envase, "
                    + "gral_mon.id AS moneda_id, "
                    + "gral_mon.simbolo AS moneda "
                + "FROM log_rutas_detalle "
                + "JOIN fac_docs ON fac_docs.id=log_rutas_detalle.fac_docs_id "
                + "JOIN fac_docs_detalles ON fac_docs_detalles.fac_doc_id=fac_docs.id "
                + "JOIN inv_prod ON inv_prod.id=log_rutas_detalle.inv_prod_id "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "JOIN cxc_clie ON cxc_clie.id=fac_docs.cxc_clie_id "
                + "JOIN gral_mon ON gral_mon.id=fac_docs.moneda_id "
                + "WHERE fac_docs_detalles.inv_prod_id=log_rutas_detalle.inv_prod_id AND log_rutas_detalle.log_ruta_id="+id_ruta+" "
                + " "
                + "UNION "
                + " "
                + "SELECT (CASE WHEN cxc_fac_rev_cob_detalle.revision_cobro='R' THEN 'Revision' ELSE 'Cobro' END) AS tipo, "
                    + "2::integer as numero_tipo, "
                    + "erp_h_facturas.serie_folio AS factura, "
                    + "cxc_clie.numero_control AS no_cliente, "
                    + "cxc_clie.razon_social AS cliente, "
                    + "0::double precision as cantidad, "
                    + "''::character varying AS unidad, "
                    + "''::character varying AS descripcion, "
                    + "erp_h_facturas.saldo_factura AS importe, "
                    + "''::character varying AS envase, "
                    + "gral_mon.id AS moneda_id, "
                    + "gral_mon.simbolo AS moneda "
                + "FROM log_rutas_detalle_cobro "
                + "JOIN cxc_fac_rev_cob_detalle ON cxc_fac_rev_cob_detalle.id=log_rutas_detalle_cobro.cxc_fac_rev_cob_detalle_id "
                + "JOIN erp_h_facturas ON erp_h_facturas.id=cxc_fac_rev_cob_detalle.erp_h_facturas_id "
                + "JOIN cxc_clie ON cxc_clie.id=erp_h_facturas.cliente_id "
                + "JOIN gral_mon ON gral_mon.id=erp_h_facturas.moneda_id "
                + "WHERE log_rutas_detalle_cobro.log_ruta_id="+id_ruta +" "
            + ") AS sbt "
            + "ORDER BY numero_tipo, factura";
        
        System.out.println("Obtiene datos pdf ruta: "+sql_query);
        ArrayList<HashMap<String, String>> hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("tipo",rs.getString("tipo"));
                    row.put("factura",rs.getString("factura"));
                    row.put("no_cliente",rs.getString("no_cliente"));
                    row.put("cliente",rs.getString("cliente"));
                    
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    
                    row.put("unidad",rs.getString("unidad"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("envase",rs.getString("envase"));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("aprobado","");
                    row.put("entregado","");
                    
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
    
    //Obtiene datos de la Unidad(Vehiculo)
    @Override
    public ArrayList<HashMap<String, String>> getUnidades_Datos(Integer id) {
        String sql_to_query = ""
        + "select "
            + "log_vehiculos.id, "
            + "log_vehiculos.folio, "
            + "log_vehiculos.anio, "
            + "log_vehiculos.color, "
            + "log_vehiculos.placa, "
            + "log_vehiculos.numero_economico, "
            + "log_vehiculos.numero_serie, "
            + "log_vehiculos.cap_volumen, "
            + "log_vehiculos.cap_peso, "
            + "log_vehiculos.comentarios, "
            + "log_vehiculos.log_vehiculo_tipo_id, "
            + "log_vehiculos.log_vehiculo_clase_id, "
            + "log_vehiculos.log_vehiculo_marca_id, "
            + "log_vehiculos.log_vehiculo_tipo_placa_id, "
            + "log_vehiculos.log_vehiculo_tipo_caja_id, "
            + "log_vehiculos.log_vehiculo_tipo_rodada_id, "
            + "log_vehiculos.clasificacion2, "
            + "(case when cxp_prov.id is null then 0 else cxp_prov.id end) as prov_id, "
            + "(case when cxp_prov.id is null then '' else cxp_prov.folio end) as no_prov, "
            + "(case when cxp_prov.id is null then '' else cxp_prov.razon_social end) as proveedor, "
            + "(case when log_choferes.id is null then 0 else log_choferes.id end) as operador_id, "
            + "(case when log_choferes.id is null then '' else log_choferes.clave end) as no_operador, "
            + "(case when log_choferes.id is null then '' else (case when nombre is null then '' else nombre end)||' '||(case when apellido_paterno is null then  '' ELSE apellido_paterno end)||' '||(case when apellido_materno is null then '' else apellido_materno end) end) as operador "
        + "from log_vehiculos "
        + "left join cxp_prov on cxp_prov.id=log_vehiculos.cxp_prov_id "
        + "left join log_choferes on log_choferes.id=log_vehiculos.log_chofer_id "
        + "where log_vehiculos.id=?";
        
        ArrayList<HashMap<String, String>> dato_vehiculo = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("anio",rs.getString("anio"));
                    row.put("color",rs.getString("color"));
                    row.put("placa",rs.getString("placa"));
                    row.put("numero_economico",rs.getString("numero_economico"));
                    row.put("numero_serie",rs.getString("numero_serie"));
                    row.put("cap_volumen",StringHelper.roundDouble(rs.getString("cap_volumen"),3));
                    row.put("cap_peso",StringHelper.roundDouble(rs.getString("cap_peso"),3));
                    row.put("comentarios",rs.getString("comentarios"));
                    row.put("tipo_id",String.valueOf(rs.getInt("log_vehiculo_tipo_id")));
                    row.put("clase_id",String.valueOf(rs.getInt("log_vehiculo_clase_id")));
                    row.put("marca_id",String.valueOf(rs.getInt("log_vehiculo_marca_id")));
                    row.put("tplaca_id",String.valueOf(rs.getInt("log_vehiculo_tipo_placa_id")));
                    row.put("tcaja_id",String.valueOf(rs.getInt("log_vehiculo_tipo_caja_id")));
                    row.put("trodada_id",String.valueOf(rs.getInt("log_vehiculo_tipo_rodada_id")));
                    row.put("clasificacion2",String.valueOf(rs.getInt("clasificacion2")));
                    row.put("prov_id",String.valueOf(rs.getInt("prov_id")));
                    row.put("no_prov",rs.getString("no_prov"));
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("operador_id",String.valueOf(rs.getInt("operador_id")));
                    row.put("no_operador",rs.getString("no_operador"));
                    row.put("operador",rs.getString("operador"));
                    return row;
                }
            }
        );
        return dato_vehiculo;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getUnidades_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "SELECT "
            + "log_vehiculos.id, "
            + "log_vehiculos.folio, "
            + "(case when log_vehiculo_marca.id is null then '' else log_vehiculo_marca.titulo end) as marca, "
            + "log_vehiculos.anio, "
            + "(case when log_vehiculo_tipo_caja.id is null then '' else log_vehiculo_tipo_caja.titulo end ) as tipo_caja, "
            + "(case when log_vehiculo_clase.id is null then '' else log_vehiculo_clase.titulo end) as clase, "
            + "log_vehiculos.cap_volumen, "
            + "log_vehiculos.cap_peso,"
            + "(case when cxp_prov.id is null then '' else cxp_prov.razon_social end) as transportista "
        +"FROM log_vehiculos "
        + "left join log_vehiculo_marca on log_vehiculo_marca.id=log_vehiculos.log_vehiculo_marca_id "
        + "left join log_vehiculo_tipo_caja on log_vehiculo_tipo_caja.id=log_vehiculos.log_vehiculo_tipo_caja_id "
        + "left join log_vehiculo_clase on log_vehiculo_clase.id=log_vehiculos.log_vehiculo_clase_id "
        + "left join cxp_prov on cxp_prov.id=log_vehiculos.cxp_prov_id "
        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = log_vehiculos.id "
        +"WHERE log_vehiculos.borrado_logico=false "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("marca",rs.getString("marca"));
                    row.put("anio",rs.getString("anio"));
                    row.put("tipo_caja",rs.getString("tipo_caja"));
                    row.put("clase",rs.getString("clase"));
                    row.put("cap_volumen",StringHelper.roundDouble(rs.getString("cap_volumen"),3));
                    row.put("cap_peso",StringHelper.roundDouble(rs.getString("cap_peso"),3));
                    row.put("transportista",rs.getString("transportista"));
                    return row;
                }
            }
        );
        return hm; 
    }

    
    
    
    
    //Obtiene todas las marcas de unidades de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getUnidades_Marcas(Integer idEmp) {
        
        String sql_to_query = "SELECT distinct id, titulo FROM log_vehiculo_marca WHERE gral_emp_id=? AND borrado_logico=false;"; 
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene Tipos de unidades de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getUnidades_Tipos(Integer idEmp) {
        
        String sql_to_query = "SELECT distinct id, titulo FROM log_vehiculo_tipo WHERE gral_emp_id=? AND borrado_logico=false;"; 
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Obtiene las Clases deunidades de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getUnidades_Clases(Integer idEmp) {
        
        String sql_to_query = "SELECT distinct id, titulo FROM log_vehiculo_clase WHERE gral_emp_id=? AND borrado_logico=false;"; 
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Obtiene los Tipos de placas de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getUnidades_TiposPlaca(Integer idEmp) {
        
        String sql_to_query = "SELECT distinct id, titulo FROM log_vehiculo_tipo_placa WHERE gral_emp_id=? AND borrado_logico=false;"; 
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene los Tipos de Rodada para las unidades de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getUnidades_TiposRodada(Integer idEmp) {
        
        String sql_to_query = "SELECT distinct id, titulo FROM log_vehiculo_tipo_rodada WHERE gral_emp_id=? AND borrado_logico=false;"; 
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Obtiene los Tipos de Caja para las unidades de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getUnidades_TiposCaja(Integer idEmp) {
        
        String sql_to_query = "SELECT distinct id, titulo FROM log_vehiculo_tipo_caja WHERE gral_emp_id=? AND borrado_logico=false;"; 
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Calcular numero de años a mostrar en formulario de Unidades(Vehiculos)
    @Override
    public ArrayList<HashMap<String, Object>>  getUnidades_AniosUnidad() {
        ArrayList<HashMap<String, Object>> anios = new ArrayList<HashMap<String, Object>>();
        
        Calendar c1 = Calendar.getInstance();
        Integer annio = c1.get(Calendar.YEAR);//obtiene el año actual
        
        for(int i=0; i<40; i++) {
            HashMap<String, Object> row = new HashMap<String, Object>();
            row.put("valor",(annio-i));
            anios.add(i, row);
        }
        return anios;
    }
    
    
    
    
    //obtiene datos para el buscador de proveedores
    @Override
    public ArrayList<HashMap<String, Object>> getBuscadorProveedores(String rfc, String no_proveedor, String razon_social, String transportista, Integer id_empresa) {
        String where = "";
	if(!rfc.equals("")){
            where +=" AND cxp_prov.rfc ILIKE '%"+rfc.toUpperCase()+"%'";
	}
        
	if(!no_proveedor.equals("")){
            where +=" AND cxp_prov.folio ILIKE '%"+no_proveedor.toUpperCase()+"%'";
	}
        
	if(!razon_social.equals("")){
            where +=" AND (cxp_prov.razon_social ilike '%"+razon_social.toUpperCase()+"%' OR cxp_prov.clave_comercial ilike '%"+razon_social.toUpperCase()+"%')";
	}
        
        if(transportista.toLowerCase().trim().equals("true")){
            where +=" AND cxp_prov.transportista=true";
        }
        
        String sql_to_query = ""
            + "SELECT DISTINCT  cxp_prov.id, "
                + "cxp_prov.folio AS numero_proveedor, "
                + "cxp_prov.rfc, "
                + "cxp_prov.razon_social, "
                + "cxp_prov.calle||' '||cxp_prov.numero||', '||cxp_prov.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo ||' C.P. '||cxp_prov.cp as direccion, "
                + "cxp_prov.proveedortipo_id,  "
                + "cxp_prov.descuento,  "
                + "cxp_prov.dias_credito_id as id_dias_credito,  "
                + "cxp_prov.cxp_prov_tipo_embarque_id as id_tipo_embarque,  "
                + "cxp_prov.credito_a_partir as comienzo_de_credito, "
                + "cxp_prov.limite_credito, "
                + "cxp_prov.moneda_id, "
                + "cxp_prov.impuesto AS impuesto_id,"
                + "(CASE WHEN gral_imptos.iva_1 is null THEN 0 ELSE gral_imptos.iva_1 END) AS valor_impuesto "
            + "FROM cxp_prov "
            + "JOIN gral_pais ON gral_pais.id = cxp_prov.pais_id "
            + "JOIN gral_edo ON gral_edo.id = cxp_prov.estado_id "
            + "JOIN gral_mun ON gral_mun.id = cxp_prov.municipio_id  "
            + "LEFT JOIN gral_imptos ON gral_imptos.id=cxp_prov.impuesto "
            + "WHERE empresa_id=? AND cxp_prov.borrado_logico = false "+ where +";";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm_datos_proveedor = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("numero_proveedor",rs.getString("numero_proveedor"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("proveedortipo_id",String.valueOf(rs.getInt("proveedortipo_id")));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("descuento",StringHelper.roundDouble(String.valueOf(rs.getDouble("descuento")),2));
                    row.put("limite_de_credito",StringHelper.roundDouble(String.valueOf(rs.getDouble("limite_credito")),2));
                    row.put("id_dias_credito",String.valueOf(rs.getInt("id_dias_credito")));
                    row.put("id_tipo_embarque",String.valueOf(rs.getInt("id_tipo_embarque")));
                    row.put("comienzo_de_credito",String.valueOf(rs.getInt("comienzo_de_credito")));
                    row.put("impuesto_id",String.valueOf(rs.getInt("impuesto_id")));
                    row.put("valor_impuesto",StringHelper.roundDouble(String.valueOf(rs.getDouble("valor_impuesto")),2));
                    return row;
                }
            }
        );
        return hm_datos_proveedor;  
    }
    
    
    
    //Obtiene datos del Proveedor a partir del Número de Proveedor
    @Override
    public ArrayList<HashMap<String, Object>> getDatosProveedorByNoProv(String numeroProveedor, String transportista, Integer id_empresa) {
        String where = "";
        
        if(transportista.toLowerCase().trim().equals("true")){
            where +=" AND cxp_prov.transportista=true";
        }
        
        String sql_to_query = ""
                + "SELECT DISTINCT  "
                    + "cxp_prov.id, "
                    + "cxp_prov.folio AS numero_proveedor, "
                    + "cxp_prov.rfc, "
                    + "cxp_prov.razon_social, "
                    + "cxp_prov.calle||' '||cxp_prov.numero||', '||cxp_prov.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo ||' C.P. '||cxp_prov.cp as direccion, "
                    + "cxp_prov.proveedortipo_id,  "
                    + "cxp_prov.descuento,  "
                    + "cxp_prov.dias_credito_id as id_dias_credito,  "
                    + "cxp_prov.cxp_prov_tipo_embarque_id as id_tipo_embarque,  "
                    + "cxp_prov.credito_a_partir as comienzo_de_credito, "
                    + "cxp_prov.limite_credito, "
                    + "cxp_prov.moneda_id,"
                    + "cxp_prov.impuesto AS impuesto_id,"
                    + "(CASE WHEN gral_imptos.iva_1 is null THEN 0 ELSE gral_imptos.iva_1 END) AS valor_impuesto "
                + "FROM cxp_prov "
                + "JOIN gral_pais ON gral_pais.id = cxp_prov.pais_id "
                + "JOIN gral_edo ON gral_edo.id = cxp_prov.estado_id "
                + "JOIN gral_mun ON gral_mun.id = cxp_prov.municipio_id  "
                + "LEFT JOIN gral_imptos ON gral_imptos.id=cxp_prov.impuesto "
                + "WHERE empresa_id=? "+ where +" AND cxp_prov.borrado_logico=false AND cxp_prov.folio='"+numeroProveedor.toUpperCase()+"';";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("numero_proveedor",rs.getString("numero_proveedor"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("proveedortipo_id",String.valueOf(rs.getInt("proveedortipo_id")));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("descuento",StringHelper.roundDouble(String.valueOf(rs.getDouble("descuento")),2));
                    row.put("limite_de_credito",StringHelper.roundDouble(String.valueOf(rs.getDouble("limite_credito")),2));
                    row.put("id_dias_credito",String.valueOf(rs.getInt("id_dias_credito")));
                    row.put("id_tipo_embarque",String.valueOf(rs.getInt("id_tipo_embarque")));
                    row.put("comienzo_de_credito",String.valueOf(rs.getInt("comienzo_de_credito")));
                    row.put("impuesto_id",String.valueOf(rs.getInt("impuesto_id")));
                    row.put("valor_impuesto",StringHelper.roundDouble(String.valueOf(rs.getDouble("valor_impuesto")),2));
                    return row;
                }
            }
        );
        return hm;  
    }
    
    
    
    //Buscador de Operadores(Choferes)
    @Override
    public ArrayList<HashMap<String, Object>> getBuscadorOperadores(String no_operador, String nombre, Integer id_proveedor, Integer id_empresa, Integer id_sucursal) {
        String where="";
        if(id_sucursal!=0){
            where = " AND sbt.gral_suc_id="+id_sucursal;
        }
        
        if(id_proveedor!=0){
            where = " AND sbt.cxp_prov_id="+id_proveedor;
        }
        
	String sql_query = ""
        + "SELECT * FROM ( "
            + "SELECT  id, clave, (CASE WHEN nombre IS NULL THEN '' ELSE nombre END)||' '||(CASE WHEN apellido_paterno IS NULL THEN '' ELSE apellido_paterno END)||' '||(CASE WHEN apellido_materno IS NULL THEN '' ELSE apellido_materno END) AS nombre, cxp_prov_id, gral_emp_id, gral_suc_id, borrado_logico "
            + "FROM log_choferes"
        + ") AS sbt "
        + "WHERE sbt.clave ILIKE '%"+no_operador+"%' "
        + "AND replace(upper(sbt.nombre),' ', '') ilike replace('%"+nombre.toUpperCase()+"%', ' ', '')"
        + "AND sbt.gral_emp_id=? AND sbt.borrado_logico=false "+where+";";
        
        System.out.println("getBuscadorOperadores: "+sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("clave",rs.getString("clave"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("prov_id",rs.getInt("cxp_prov_id"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //obtener datos del Operador a partir de la clave
    @Override
    public ArrayList<HashMap<String, Object>> getDatosOperadorByNo(String no_operador, Integer id_proveedor, Integer id_empresa, Integer id_sucursal) {
        String where="";
        if(id_sucursal!=0){
            where = " AND sbt.gral_suc_id="+id_sucursal;
        }
        
        if(id_proveedor!=0){
            where = " AND sbt.cxp_prov_id="+id_proveedor;
        }
        
	String sql_query = ""
        + "SELECT * FROM ( "
            + "SELECT  id, clave, (CASE WHEN nombre IS NULL THEN '' ELSE nombre END)||' '||(CASE WHEN apellido_paterno IS NULL THEN '' ELSE apellido_paterno END)||' '||(CASE WHEN apellido_materno IS NULL THEN '' ELSE apellido_materno END) AS nombre, cxp_prov_id, gral_emp_id, gral_suc_id, borrado_logico "
            + "FROM log_choferes"
        + ") AS sbt "
        + "WHERE upper(sbt.clave)='"+no_operador.toUpperCase().trim()+"' AND sbt.gral_emp_id=? AND sbt.borrado_logico=false "+where+" LIMIT 1;";
        
        //System.out.println("getBuscadorOperador: "+sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("clave",rs.getString("clave"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("prov_id",rs.getInt("cxp_prov_id"));
                    return row;
                }
            }
        );
        return hm;    
    }
    
    
    
    
    
    
    //Obtiene datos para el gri del Catalogo de Operadores
    @Override
    public ArrayList<HashMap<String, Object>> getOperadores_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
        String sql_to_query = ""
                + "SELECT log_choferes.id, "
                + "log_choferes.clave as numero_control, "
                + "log_choferes.nombre  || ' ' || CASE WHEN log_choferes.apellido_paterno is NULL THEN '' ELSE log_choferes.apellido_paterno END || ' ' || CASE WHEN log_choferes.apellido_materno is NULL THEN '' ELSE log_choferes.apellido_materno END AS nombre,"
                + "(case when cxp_prov.id is null then '' else cxp_prov.razon_social end) as transportista "
                + "FROM log_choferes "
                + "left join cxp_prov on cxp_prov.id=log_choferes.cxp_prov_id "                       
                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = log_choferes.id "
                +"WHERE log_choferes.borrado_logico=false "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("transportista",rs.getString("transportista"));
                   
                    return row;
                }
            }
        );
        return hm; 
    }

    


    ///Obtiene datos de operadores
    @Override
    public ArrayList<HashMap<String, String>> getOperadores_Datos(Integer id) {
        
        String sql_to_query = "SELECT id,clave,nombre,apellido_paterno,apellido_materno, cxp_prov_id as trans_id FROM log_choferes WHERE id="+id;
        
        ArrayList<HashMap<String, String>> dato_operador = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("clave",rs.getString("clave"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("apellido_paterno",rs.getString("apellido_paterno"));
                    row.put("apellido_materno",rs.getString("apellido_materno"));
                    row.put("trans_id",String.valueOf(rs.getInt("trans_id")));
                    return row;
                }
            }
        );
        return dato_operador;
    }
    
    
    
    
    
    //----------------------------------------------------------------------------
    //METODOS PARA CARGA DE DOCUMENTOS
    //----------------------------------------------------------------------------
    
    @Override
    public ArrayList<HashMap<String, Object>> getBuscadorClientes(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal) {
        String where="";

	if(filtro == 1){
		where=" AND cxc_clie.numero_control ilike '%"+cadena+"%'";
	}
	if(filtro == 2){
		where=" AND cxc_clie.rfc ilike '%"+cadena+"%'";
	}
	if(filtro == 3){
		where=" AND cxc_clie.razon_social ilike '%"+cadena+"%'";
	}

	if(filtro == 4){
		where=" AND cxc_clie.curp ilike '%"+cadena+"%'";
	}
	if(filtro == 5){
		where=" AND cxc_clie.alias ilike '%"+cadena+"%'";
	}

        if(id_sucursal==0){
            where +="";
        }else{
            where +=" AND sucursal_id="+id_sucursal;
        }

	String sql_query = ""
        + "SELECT "
            +"sbt.id,"
            +"sbt.numero_control,"
            +"sbt.rfc,"
            +"sbt.razon_social,"
            //+"sbt.direccion,"
            +"sbt.moneda_id,"
            +"gral_mon.descripcion as moneda "
        +"FROM("
            + "SELECT cxc_clie.id,"
                +"cxc_clie.numero_control,"
                +"cxc_clie.rfc, "
                +"cxc_clie.razon_social,"
                //+"cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion, "
                +"cxc_clie.moneda as moneda_id "
            +"FROM cxc_clie "
            + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
            + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
            + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
            +" WHERE empresa_id ="+id_empresa+"  "
            +" AND cxc_clie.borrado_logico=false  "+where+" "
        +") AS sbt "
        +"LEFT JOIN gral_mon on gral_mon.id = sbt.moneda_id ORDER BY sbt.id;";
        //System.out.println("BuscarCliente: "+sql_query);
        ArrayList<HashMap<String, Object>> hm_cli = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    //row.put("direccion",rs.getString("direccion"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("moneda",rs.getString("moneda"));

                    return row;
                }
            }
        );
        return hm_cli;
    }


    //obtener datos del cliente a partir del Numero de Control
    @Override
    public ArrayList<HashMap<String, Object>> getDatosClienteByNoCliente(String no_control, Integer id_empresa, Integer id_sucursal) {

        String where="";
        if(id_sucursal==0){
            where +="";
        }else{
            where +=" AND sucursal_id="+id_sucursal;
        }

	String sql_query = ""
        + "SELECT "
            +"sbt.id,"
            +"sbt.numero_control,"
            +"sbt.rfc,"
            +"sbt.razon_social,"
            //+"sbt.direccion,"
            +"sbt.moneda_id,"
            +"gral_mon.descripcion as moneda "
        +"FROM("
            + "SELECT cxc_clie.id,"
                +"cxc_clie.numero_control,"
                +"cxc_clie.rfc, "
                +"cxc_clie.razon_social,"
                //+"cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion, "
                +"cxc_clie.moneda AS moneda_id "
            +"FROM cxc_clie "
            + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
            + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
            + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
            +" WHERE empresa_id ="+id_empresa+"  "
            +" AND cxc_clie.borrado_logico=false  "+where+" "
            + "AND  cxc_clie.numero_control='"+no_control.toUpperCase()+"'"
        +") AS sbt "
        +"LEFT JOIN gral_mon on gral_mon.id = sbt.moneda_id ORDER BY sbt.id LIMIT 1;";

        //System.out.println("getDatosCliente: "+sql_query);

        ArrayList<HashMap<String, Object>> hm_cli = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    //row.put("direccion",rs.getString("direccion"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("moneda",rs.getString("moneda"));

                    return row;
                }
            }
        );
        return hm_cli;
    }
    
    
    
    
    //Obtiene almacenes de la sucursal especificada.
    //Se utiliza en la Carga de Inventario Fisico
    @Override
    public ArrayList<HashMap<String, String>> getAlmacenesSucursal(Integer id_empresa, Integer id_sucursal) {
	String sql_query = ""
                + "SELECT DISTINCT "
                    + "inv_alm.id, "
                    + "inv_alm.id||' '||inv_alm.titulo AS titulo "
                + "FROM inv_alm  "
                + "JOIN inv_suc_alm ON inv_suc_alm.almacen_id = inv_alm.id "
                + "JOIN gral_suc ON gral_suc.id = inv_suc_alm.sucursal_id "
                + "WHERE gral_suc.empresa_id=? AND gral_suc.id=? AND inv_alm.borrado_logico=FALSE;";
        ArrayList<HashMap<String, String>> hm_alm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa), new Integer(id_sucursal)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_alm;
    }
    
    
    
    //Elimina el contenido de esta tabla de la empresa y sucursal indicada en los parametros.
    @Override
    public int getDeleteFromLogCargaDocTmp(Integer id_emp, Integer id_suc) {
        int row=0;
        String updateSql="";
        
        try{
            updateSql = "DELETE FROM inv_carga_doc_tmp WHERE emp_id=? AND suc_id=?;";
            
            //System.out.println("updateSql: "+updateSql);
            
            // define query arguments
            Object[] params = new Object[] { new Integer(id_emp),new Integer(id_suc)};
            
            // define SQL types of the arguments
            int[] types = new int[] { Types.SMALLINT, Types.SMALLINT };
            
            // execute insert query to insert the data
            // return number of row / rows processed by the executed query
            row = this.getJdbcTemplate().update(updateSql, params, types);
            
            //System.out.println(row + " row inserted.");
        } catch (Exception e) {
            System.out.println("ERROR: "+e.getMessage());
            row=0;
        }
        
        return row;
    }
    
    
    
    //Carga la tabla temporal con los datos del Documento
    @Override
    public HashMap<String, String> getInsertLogCargaDocTmp(String data_string) {
        HashMap<String, String> retorno = new HashMap<String, String>();
        
        int row=0;
        String msj="";
        int rowCountClieId=0;
        int rowCountProdId=0;
        String insertSql = "";
        boolean cargar_registro=false;
        
        String param[] = data_string.split("___");
        
        
        if(param[8].trim().equals("")){
            retorno.put("destinatario", "false___El Cliente Destinatario no tiene numero de cotrol. Revise el archivo.");
            cargar_registro=false;
        }else{
            retorno.put("destinatario", "true___ .");
            cargar_registro=true;
        }
        /*
        rowCountClieId = this.getJdbcTemplate().queryForInt("select count(id) from cxc_destinatarios where upper(trim(folio_ext))='"+param[8]+"' and borrado_logico=false and gral_emp_id="+param[0]+";");
        if(rowCountClieId<=0){
            retorno.put("destinatario", "false___Se agrego este Destinatario al catalogo. ");
        }else{
            retorno.put("destinatario", "true___ .");
        }
        */
        
        /*
        rowCountPoblacionId = this.getJdbcTemplate().queryForInt("select count(id) from gral_mun where upper(trim(titulo))='"+param[10]+"'");
        if(rowCountPoblacionId<=0){
            retorno.put("poblacion", "false___No se encontro la Poblacion. ");
            cargar_registro = false;
        }else{
            retorno.put("poblacion", "true___ .");
        }
        */
        
        if(param[11].trim().equals("")){
            retorno.put("producto", "false___El producto no tiene c&oacute;digo. Revise el archivo.");
            cargar_registro=false;
        }else{
            retorno.put("producto", "true___ .");
            cargar_registro=true;
        }
        
        /*
        rowCountProdId = this.getJdbcTemplate().queryForInt("select count(id) from inv_prod where upper(trim(sku))='"+param[11]+"' and borrado_logico=false and empresa_id="+param[0]+";");
        if(rowCountProdId<=0){
            insertSql = "";
            
            this.getJdbcTemplate().update(msj);
            
            retorno.put("producto", "false___Se agrego este producto al catalogo. ");
        }else{
            retorno.put("producto", "true___ .");
        }
        */
        
        /*
        rowCountUnidadId = this.getJdbcTemplate().queryForInt("select count(id) from inv_prod_unidades where upper(trim(titulo_abr))='"+param[14]+"' and borrado_logico=false;");
        if(rowCountUnidadId<=0){
            retorno.put("unidad", "false___No se encontro la Unidad de Medida en el catalogo. ");
            cargar_registro = false;
        }else{
            retorno.put("unidad", "true___ .");
        }
        */
        
        if(cargar_registro){
            try{
                insertSql = "";
                
                //Cargar en la tabla INV_EXI_TMP
                insertSql = "INSERT INTO log_carga_doc_tmp(emp_id,suc_id,user_id,alm_id,no_carga,no_pedido,pos,fecha_entrega, cliente_id, no_dest,nombre_dest,poblacion_dest,codigo_prod,descripcion_prod,cantidad,unidad,peso,volumen,no_entrega,puesto_exp,fecha_carga,estatus) "
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,true);";
                //System.out.println("insertSql: "+insertSql);
                
                // define query arguments
                Object[] params = new Object[] { new Integer(param[0]),new Integer(param[1]),new Integer(param[2]),new Integer(param[3]),param[4],param[5],param[6],param[7], new Integer(param[20]),param[8],param[9],param[10],param[11],param[12],param[13],param[14],param[15],param[16],param[17],param[18],param[19]};
                
                // define SQL types of the arguments
                int[] types = new int[] {Types.SMALLINT,Types.SMALLINT,Types.SMALLINT,Types.SMALLINT,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.DATE, Types.INTEGER, Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.DATE};
                
                // execute insert query to insert the data
                // return number of row / rows processed by the executed query
                row = this.getJdbcTemplate().update(insertSql, params, types);
                
                msj = "true___ .";
                //System.out.println(row + " row inserted.");
            } catch (Exception e) {
                msj =  "false___No se cargo el registro debido a errores internos["+e.getMessage()+"]. Intente nuevamente.";
            }
        }else{
            msj = "false___No se cargo el registro.";
        }
        
        retorno.put("cargado", msj);
        return retorno;
    }
    
    
    //Verifica que el documento con  las cargas no ha sido dado de alta anteriormente
    @Override
    public int getVerificarDocumento(Integer id_emp, Integer id_clie, String no_carga) {
        int rowCount = this.getJdbcTemplate().queryForInt("select count(log_doc_carga.id) from log_doc join log_doc_carga on (log_doc_carga.log_doc_id=log_doc.id and log_doc_carga.no_carga='"+no_carga+"') where log_doc.gral_emp_id="+id_emp+" and log_doc.cxc_clie_id="+id_clie+";");
        
        return rowCount;
    }
    
    //LLamada al procedimiento que carga las tablas relacionadas al documento y actualiza inventario
    @Override
    public String getUpdateDocInvExi(Integer usuario_id, Integer empresa_id, Integer sucursal_id, Integer id_cliente) {
        String sql_to_query = "select * from log_carga_documentos("+usuario_id+","+empresa_id+","+sucursal_id+","+id_cliente+");";
        
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        valor_retorno = update.get("log_carga_documentos").toString();
        
        return valor_retorno;
    }
    
    //TERMINA METODOS DE CARGA DE DOCUMENTOS------------------------------------
    
    
    
    
    
    
    
    //Metodos para el administrador de vijes
    //Verificar si el usuario es Administrador
    @Override
    public Integer getUserRolAdmin(Integer id_user) {
        HashMap<String, Object> data = new HashMap<String, Object>();
        Integer velor_retorno=0;
        
        //verificar si el usuario tiene  rol de ADMINISTTRADOR
        //si exis es mayor que cero, el usuario si es ADMINISTRADOR
        String sql_to_query = "SELECT count(gral_usr_id) AS exis_rol_admin FROM gral_usr_rol WHERE gral_usr_id="+id_user+" AND gral_rol_id=1;";
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        velor_retorno = Integer.valueOf(map.get("exis_rol_admin").toString());
        
        return velor_retorno;
    }
    
    
    //Obtiene las sucursales de la empresa indicada
    @Override
    public ArrayList<HashMap<String, Object>> getSucursales(Integer idEmp) {
        
        String sql_to_query = "SELECT distinct id, titulo FROM gral_suc WHERE empresa_id=? AND borrado_logico=false;"; 
        ArrayList<HashMap<String, Object>> hm_facturas = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
    
    
    
    //Obtiene las sucursales de la empresa indicada
    @Override
    public ArrayList<HashMap<String, Object>> getTransportistas(Integer idEmp, Integer idSuc) {
        String sql_to_query = "";
        if(idSuc>0){
            //Obtener transportistas de la sucursal
            sql_to_query = "select distinct id, folio, razon_social as titulo from cxp_prov where empresa_id=? and sucursal_id="+idSuc+" and transportista=true and borrado_logico=false order by razon_social;";
        }else{
            //obtener todos los transportistas de la empresa sin importar la sucursal
            sql_to_query = "select distinct id, folio, razon_social as titulo from cxp_prov where empresa_id=? and transportista=true and borrado_logico=false order by razon_social;";
        }
        
        ArrayList<HashMap<String, Object>> hm_facturas = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
    
    
    
    //Buscador de Unidades(Vehiculos)
    @Override
    public ArrayList<HashMap<String, Object>> getBuscadorUnidades(String no_unidad, String marca, Integer id_empresa, Integer id_sucursal) {
        String where="";
        if(id_sucursal!=0){
            where = " and gral_suc_id="+id_sucursal;
        }
        
	String sql_query = ""
        + "select "
            + "log_vehiculos.id,"
            + "log_vehiculos.folio, "
            + "log_vehiculos.numero_economico as no_eco,"
            + "(case when log_vehiculo_marca.id is null then '' else log_vehiculo_marca.titulo end) as marca,"
            + "(case when log_vehiculo_clase.id is null then '' else log_vehiculo_clase.titulo end) as clase_unidad,"
            + "log_vehiculos.placa,"
            + "log_vehiculos.cap_volumen,"
            + "log_vehiculos.cap_peso,"
            + "(case when log_choferes.id is null then '' else log_choferes.clave end) AS no_operador,"
            + "(case when log_choferes.id is null then '' else ((case when log_choferes.nombre is null then '' else log_choferes.nombre||' ' end)||(case when log_choferes.apellido_paterno is null then '' else log_choferes.apellido_paterno||' ' end)||(case when log_choferes.apellido_materno is null then '' else log_choferes.apellido_materno end)) end) AS operador "
        + "from log_vehiculos "
        + "left join log_vehiculo_marca on (log_vehiculo_marca.id=log_vehiculos.log_vehiculo_marca_id  and log_vehiculo_marca.titulo ilike ?) "
        + "left join log_vehiculo_clase on log_vehiculo_clase.id=log_vehiculos.log_vehiculo_clase_id "
        + "left join log_choferes ON log_choferes.id=log_vehiculos.log_chofer_id "
        + "where log_vehiculos.folio ilike ? and log_vehiculos.gral_emp_id=? and log_vehiculos.borrado_logico=false "+where+";";
        
        //System.out.println("getBuscadorUnidades: "+sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{marca, no_unidad, new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("no_eco",rs.getString("no_eco"));
                    row.put("marca",rs.getString("marca"));
                    row.put("clase_unidad",rs.getString("clase_unidad"));
                    row.put("placa",rs.getString("placa"));
                    row.put("cap_volumen",StringHelper.roundDouble(rs.getString("cap_volumen"),3));
                    row.put("cap_peso",StringHelper.roundDouble(rs.getString("cap_peso"),3));
                    row.put("no_operador",rs.getString("no_operador"));
                    row.put("operador",rs.getString("operador"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtener datos de la Unidad a partir del Numero de Economico
    @Override
    public ArrayList<HashMap<String, Object>> getDatosUnidadByNoUnidad(String no_unidad, Integer id_empresa, Integer id_sucursal) {
        
        String where="";
        if(id_sucursal!=0){
            where +=" and gral_suc_id="+id_sucursal;
        }
        
        String sql_query = ""
        + "select "
            + "log_vehiculos.id,"
            + "log_vehiculos.folio, "
            + "log_vehiculos.numero_economico as no_eco,"
            + "(case when log_vehiculo_marca.id is null then '' else log_vehiculo_marca.titulo end) as marca,"
            + "(case when log_vehiculo_clase.id is null then '' else log_vehiculo_clase.titulo end) as clase_unidad,"
            + "log_vehiculos.placa,"
            + "log_vehiculos.cap_volumen,"
            + "log_vehiculos.cap_peso,"
            + "(case when log_choferes.id is null then '' else log_choferes.clave end) AS no_operador,"
            + "(case when log_choferes.id is null then '' else ((case when log_choferes.nombre is null then '' else log_choferes.nombre||' ' end)||(case when log_choferes.apellido_paterno is null then '' else log_choferes.apellido_paterno||' ' end)||(case when log_choferes.apellido_materno is null then '' else log_choferes.apellido_materno end)) end) AS operador "
        + "from log_vehiculos "
        + "left join log_vehiculo_marca on log_vehiculo_marca.id=log_vehiculos.log_vehiculo_marca_id "
        + "left join log_vehiculo_clase on log_vehiculo_clase.id=log_vehiculos.log_vehiculo_clase_id "
        + "left join log_choferes on log_choferes.id=log_vehiculos.log_chofer_id "
        + "where upper(log_vehiculos.folio)=? and log_vehiculos.gral_emp_id=? and log_vehiculos.borrado_logico=false "+where+" limit 1;";
        //System.out.println("getDatosVehiculo: "+sql_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{no_unidad, new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("no_eco",rs.getString("no_eco"));
                    row.put("marca",rs.getString("marca"));
                    row.put("clase_unidad",rs.getString("clase_unidad"));
                    row.put("placa",rs.getString("placa"));
                    row.put("cap_volumen",StringHelper.roundDouble(rs.getString("cap_volumen"),3));
                    row.put("cap_peso",StringHelper.roundDouble(rs.getString("cap_peso"),3));
                    row.put("no_operador",rs.getString("no_operador"));
                    row.put("operador",rs.getString("operador"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //Obtiene todas las cargas pendientes de entregar de acuerdo a los filtros indicados
    @Override
    public ArrayList<HashMap<String, Object>> getLogAdmViaje_CargasPendientes(Integer id_empresa, Integer id_suc_user, String no_clie, String no_carga, String no_ped, String no_dest, String dest, String poblacion) {
        String where="";
        if(id_suc_user!=0){
            where +=" and log_doc.gral_suc_id="+id_suc_user;
        }
        
        //no_clie, no_carga, no_ped, no_dest, dest, poblacion
        if(!no_clie.trim().equals("")){
            where +=" and cxc_clie.numero_control ilike '%"+no_clie.toUpperCase()+"%'";
        }
        
        if(!no_carga.trim().equals("")){
            where +=" and log_doc_carga.no_carga ilike '%"+no_carga.toUpperCase()+"%'";
        }
        
        if(!no_ped.trim().equals("")){
            where +=" and log_doc_ped.no_pedido ilike '%"+no_ped.toUpperCase()+"%'";
        }
        
        if(!no_dest.trim().equals("")){
            where +=" and cxc_destinatarios.folio_ext ilike '%"+no_dest.toUpperCase()+"%'";
        }
        
        if(!dest.trim().equals("")){
            where +=" and cxc_destinatarios.razon_social ilike '%"+dest.toUpperCase()+"%'";
        }
        
        if(!poblacion.trim().equals("")){
            where +=" and gral_mun.titulo ilike '%"+poblacion.toUpperCase()+"%'";
        }
        
        String sql_to_query = ""
        + "select "
            + "cxc_clie.numero_control as no_clie,"
            + "substr(upper(cxc_clie.razon_social),1,12) as clie,"
            + "log_doc.id as doc_id, "
            + "log_doc_carga.id as cga_id, "
            + "log_doc_ped.id as ped_id, "
            + "log_doc_carga.no_carga,"
            + "log_doc_carga.fecha_entrega, "
            + "log_doc_ped.no_pedido, "
            + "cxc_destinatarios.id as id_dest,"
            + "cxc_destinatarios.folio_ext as no_dest, "
            + "cxc_destinatarios.razon_social as nombre_dest, "
            + "cxc_destinatarios.solicitar_firma as firma, "
            + "cxc_destinatarios.solicitar_sello as sello, "
            + "cxc_destinatarios.solicitar_efectivo as efectivo, "
            + "gral_mun.id as mun_id,"
            + "(case when gral_mun.id is null then '' else upper(gral_mun.titulo) end) as municipio, "
            + "log_doc_ped.estatus as status_ped,"
            + "sum(log_doc_ped_det.peso) as peso, "
            + "sum(log_doc_ped_det.volumen) as volumen "
        + "from log_doc "
        + "join log_doc_carga on log_doc_carga.log_doc_id=log_doc.id "
        + "join log_doc_ped on log_doc_ped.log_doc_carga_id=log_doc_carga.id "
        + "join log_doc_ped_det on log_doc_ped_det.log_doc_ped_id=log_doc_ped.id "
        + "join cxc_clie on cxc_clie.id=log_doc.cxc_clie_id "
        + "left join cxc_destinatarios on cxc_destinatarios.id=log_doc_ped.cxc_dest_id  "
        + "left join gral_mun on gral_mun.id=cxc_destinatarios.gral_mun_id  "
        + "where log_doc.gral_emp_id=? "+ where +" "
        + "group by cxc_clie.numero_control, cxc_clie.razon_social, log_doc.id, log_doc_carga.id, log_doc_ped.id, log_doc_carga.no_carga, log_doc_ped.no_pedido, cxc_destinatarios.id, cxc_destinatarios.folio, cxc_destinatarios.razon_social, gral_mun.id, gral_mun.titulo "
        + "order by cxc_destinatarios.razon_social;";
        
        //System.out.println("Pendientes: "+sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("no_clie",rs.getString("no_clie"));
                    row.put("clie",rs.getString("clie"));
                    row.put("doc_id",rs.getInt("doc_id"));
                    row.put("cga_id",rs.getInt("cga_id"));
                    row.put("f_entrega",rs.getString("fecha_entrega"));
                    row.put("ped_id",rs.getInt("ped_id"));
                    row.put("no_carga",rs.getString("no_carga"));
                    row.put("no_pedido",rs.getString("no_pedido"));
                    row.put("id_dest",rs.getInt("id_dest"));
                    row.put("no_dest",rs.getString("no_dest"));
                    row.put("nombre_dest",rs.getString("nombre_dest"));
                    row.put("firma",rs.getBoolean("firma"));
                    row.put("sello",rs.getBoolean("sello"));
                    row.put("efectivo",rs.getBoolean("efectivo"));
                    row.put("mun_id",rs.getInt("mun_id"));
                    row.put("municipio",rs.getString("municipio"));
                    row.put("status_ped",rs.getString("status_ped"));
                    row.put("peso",StringHelper.roundDouble(rs.getString("peso"),3));
                    row.put("volumen",StringHelper.roundDouble(rs.getString("volumen"),3));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Obtiene detalles del pedido
    @Override
    public ArrayList<HashMap<String, Object>> getLogAdmViaje_DetallePedido(Integer id_ped) {
        String sql_to_query = ""
        + "SELECT "
            + "inv_prod.sku AS codigo_prod,"
            + "inv_prod.descripcion AS titulo_prod,"
            + "log_doc_ped_det.cantidad,"
            + "(CASE WHEN inv_prod_unidades.id IS NULL THEN '' ELSE inv_prod_unidades.titulo_abr END) AS unidad,"
            + "log_doc_ped_det.peso,"
            + "log_doc_ped_det.volumen,"
            + "log_doc_ped_det.estatus "
        + "FROM log_doc_ped_det "
        + "JOIN inv_prod ON inv_prod.id=log_doc_ped_det.inv_prod_id "
        + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=log_doc_ped_det.inv_prod_unidad_id "
        + "WHERE log_doc_ped_det.log_doc_ped_id=? ORDER BY log_doc_ped_det.id;";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_ped)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("codigo_prod",rs.getString("codigo_prod"));
                    row.put("titulo_prod",rs.getString("titulo_prod"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("peso",StringHelper.roundDouble(rs.getString("peso"),3));
                    row.put("volumen",StringHelper.roundDouble(rs.getString("volumen"),3));
                    row.put("estatus",rs.getString("estatus"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Metodo que obtiene datos para el grid de Administrador de Viajes
    @Override
    public ArrayList<HashMap<String, Object>> getLogAdmViaje_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "SELECT "
            + "log_viaje.id, "
            + "log_viaje.folio, "
            + "to_char(log_viaje.fecha::timestamp with time zone, 'dd/mm/yyyy') as fecha, "
            + "(CASE WHEN log_choferes.nombre IS NULL THEN '' ELSE log_choferes.nombre END) || (CASE WHEN log_choferes.apellido_paterno is NULL THEN '' ELSE log_choferes.apellido_paterno END) || CASE WHEN log_choferes.apellido_materno is NULL THEN '' ELSE log_choferes.apellido_materno	END AS operador,  "
            + "(CASE WHEN log_vehiculo_marca.id IS NULL THEN '' ELSE log_vehiculo_marca.titulo END) AS marca_unidad, "
            + "(CASE WHEN log_vehiculo_clase.id IS NULL THEN '' ELSE log_vehiculo_clase.titulo END) AS clase "
        + "FROM log_viaje "
        + "LEFT JOIN log_choferes ON log_choferes.id=log_viaje.log_chofer_id "
        + "LEFT JOIN log_vehiculos ON log_vehiculos.id=log_viaje.log_vehiculo_id "
        + "LEFT JOIN log_vehiculo_clase ON log_vehiculo_clase.id=log_viaje.log_vehiculo_clase_id " 
        + "LEFT JOIN log_vehiculo_marca ON log_vehiculo_marca.id=log_vehiculos.log_vehiculo_marca_id " 
        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id=log_viaje.id "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Paginado Viajes: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("operador",rs.getString("operador"));
                    row.put("marca_unidad",rs.getString("marca_unidad"));
                    row.put("clase",rs.getString("clase"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    
    
    //Obtener los datos de un Viaje en especifico
    @Override
    public ArrayList<HashMap<String, Object>> getLoAdmViaje_Datos(Integer id) {
        String sql_to_query = ""
        + "SELECT "
            + "log_viaje.id,"
            + "log_viaje.folio,"
            + "log_viaje.fecha,"
            + "(CASE WHEN EXTRACT(HOUR FROM log_viaje.hora)=0 AND EXTRACT(MINUTE FROM log_viaje.hora)=0 THEN '00:00' ELSE (lpad(EXTRACT(HOUR FROM log_viaje.hora)::character varying, 2, '0')||':'||lpad(EXTRACT(MINUTE FROM log_viaje.hora)::character varying, 2, '0'))END) AS hora, "
            + "log_viaje.gral_suc_id AS suc_id,"
            + "log_viaje.log_vehiculo_id AS vehiculo_id,"
            + "log_vehiculos.folio AS no_unidad, "
            + "(case when log_vehiculo_marca.id is null then '' else log_vehiculo_marca.titulo end) as unidad, "
            + "log_viaje.no_economico, "
            + "log_viaje.placas, "
            + "log_vehiculos.cap_volumen,"
            + "log_vehiculos.cap_peso,"
            + "(CASE WHEN log_vehiculo_clase.id IS NULL THEN '' ELSE log_vehiculo_clase.titulo END) AS clase, "
            + "log_choferes.clave AS no_operador,"
            + "(CASE WHEN log_choferes.nombre IS NULL THEN '' ELSE log_choferes.nombre END) || (CASE WHEN log_choferes.apellido_paterno is NULL THEN '' ELSE log_choferes.apellido_paterno END) || CASE WHEN log_choferes.apellido_materno is NULL THEN '' ELSE log_choferes.apellido_materno END AS operador, "
            + "log_viaje.observaciones, "
            + "log_viaje.status "
        + "from log_viaje "
        + "left join log_choferes on log_choferes.id=log_viaje.log_chofer_id "
        + "join log_vehiculos on log_vehiculos.id=log_viaje.log_vehiculo_id "
        + "left join log_vehiculo_marca on log_vehiculo_marca.id=log_vehiculos.log_vehiculo_marca_id "
        + "left join log_vehiculo_clase on log_vehiculo_clase.id=log_viaje.log_vehiculo_clase_id " 
        + "where log_viaje.id=? order by log_viaje.id;";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha",rs.getDate("fecha"));
                    row.put("hora",rs.getString("hora"));
                    row.put("suc_id",rs.getInt("suc_id"));
                    row.put("vehiculo_id",rs.getInt("vehiculo_id"));
                    row.put("no_unidad",rs.getString("no_unidad"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("no_economico",rs.getString("no_economico"));
                    row.put("placas",rs.getString("placas"));
                    row.put("clase",rs.getString("clase"));
                    row.put("cap_volumen",StringHelper.roundDouble(rs.getString("cap_volumen"),3));
                    row.put("cap_peso",StringHelper.roundDouble(rs.getString("cap_peso"),3));
                    row.put("operador",rs.getString("operador"));
                    row.put("no_operador",rs.getString("no_operador"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("status",rs.getInt("status"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //Obtener el detalle del viaje
    @Override
    public ArrayList<HashMap<String, Object>> getLoAdmViaje_DatosGrid(Integer id) {
        String sql_to_query = ""
        + "SELECT "
            + "cxc_clie.numero_control AS no_clie,"
            + "substr(upper(cxc_clie.razon_social),1,12) AS clie,"
            + "log_viaje_det.id AS det_id,"
            + "log_doc_carga.id AS cga_id,"
            + "log_doc_ped.id AS ped_id,"
            + "log_doc_carga.no_carga,"
            + "log_doc_carga.fecha_entrega,"
            + "log_doc_ped.no_pedido,"
            + "cxc_destinatarios.id AS id_dest,"
            + "cxc_destinatarios.folio_ext AS no_dest,"
            + "cxc_destinatarios.razon_social AS nombre_dest,"
            + "log_viaje_det.solicitar_firma AS firma,"
            + "log_viaje_det.solicitar_sello AS sello,"
            + "log_viaje_det.solicitar_efectivo AS efectivo,"
            + "gral_mun.id AS mun_id,"
            + "upper(gral_mun.titulo) AS municipio,"
            + "log_doc_ped.estatus AS status_ped,"
            + "log_viaje_det.status AS status_det,"
            + "sum(log_doc_ped_det.peso) AS peso,"
            + "sum(log_doc_ped_det.volumen) AS volumen "
        + "FROM log_viaje_det "
        + "JOIN log_doc_carga ON log_doc_carga.id=log_viaje_det.log_doc_carga_id "
        + "JOIN log_doc_ped ON log_doc_ped.id=log_viaje_det.log_doc_ped_id  "
        + "JOIN log_doc_ped_det ON log_doc_ped_det.log_doc_ped_id=log_doc_ped.id "
        + "JOIN cxc_clie ON cxc_clie.id=log_viaje_det.cxc_clie_id "
        + "JOIN cxc_destinatarios ON cxc_destinatarios.id=log_doc_ped.cxc_dest_id  "
        + "JOIN gral_mun ON gral_mun.id=log_viaje_det.gral_mun_id  "
        + "WHERE log_viaje_det.log_viaje_id=? "
        + "group by cxc_clie.numero_control,cxc_clie.razon_social,log_viaje_det.id,log_doc_carga.id,log_doc_ped.id,log_doc_carga.no_carga,log_doc_carga.fecha_entrega, log_doc_ped.no_pedido, cxc_destinatarios.id,cxc_destinatarios.folio_ext,cxc_destinatarios.razon_social,log_viaje_det.solicitar_firma,log_viaje_det.solicitar_sello,log_viaje_det.solicitar_efectivo,gral_mun.id,gral_mun.titulo,log_doc_ped.estatus  "
        + "order by log_viaje_det.id";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("no_clie",rs.getString("no_clie"));
                    row.put("clie",rs.getString("clie"));
                    row.put("det_id",rs.getInt("det_id"));
                    row.put("cga_id",rs.getInt("cga_id"));
                    row.put("f_entrega",rs.getString("fecha_entrega"));
                    row.put("ped_id",rs.getInt("ped_id"));
                    row.put("no_carga",rs.getString("no_carga"));
                    row.put("no_pedido",rs.getString("no_pedido"));
                    row.put("id_dest",rs.getInt("id_dest"));
                    row.put("no_dest",rs.getString("no_dest"));
                    row.put("nombre_dest",rs.getString("nombre_dest"));
                    row.put("firma",rs.getBoolean("firma"));
                    row.put("sello",rs.getBoolean("sello"));
                    row.put("efectivo",rs.getBoolean("efectivo"));
                    row.put("mun_id",rs.getInt("mun_id"));
                    row.put("municipio",rs.getString("municipio"));
                    row.put("status_ped",rs.getString("status_ped"));
                    row.put("status_det",rs.getString("status_det"));
                    row.put("peso",StringHelper.roundDouble(rs.getString("peso"),3));
                    row.put("volumen",StringHelper.roundDouble(rs.getString("volumen"),3));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
 //Método para datos del pdf de viaje
    @Override
    public HashMap<String, String> getLoAdmViaje_DatosPdf(Integer id) {
        
        HashMap<String, String> datos = new HashMap<String, String>();
        
        String sql_to_query = ""
        + "SELECT "
            + "log_viaje.id,"
            + "log_viaje.folio,"
            + "log_viaje.fecha,"
            + "(CASE WHEN EXTRACT(HOUR FROM log_viaje.hora)=0 AND EXTRACT(MINUTE FROM log_viaje.hora)=0 THEN '00:00' ELSE (lpad(EXTRACT(HOUR FROM log_viaje.hora)::character varying, 2, '0')||':'||lpad(EXTRACT(MINUTE FROM log_viaje.hora)::character varying, 2, '0'))END) AS hora, "
            + "log_viaje.gral_suc_id AS suc_id,"
            + "gral_suc.titulo AS nom_suc,"
            + "log_viaje.log_vehiculo_id AS vehiculo_id,"
            + "log_vehiculos.folio AS no_unidad, "
            + "(case when log_vehiculo_marca.id is null then '' else log_vehiculo_marca.titulo end) as unidad, "
            + "log_viaje.no_economico, "
            + "log_viaje.placas, "
            + "(CASE WHEN log_vehiculo_clase.id IS NULL THEN '' ELSE log_vehiculo_clase.titulo END) AS tipo, "
            + "log_choferes.clave AS no_operador,"
            + "(CASE WHEN log_choferes.nombre IS NULL THEN '' ELSE log_choferes.nombre END) || ' ' || (CASE WHEN log_choferes.apellido_paterno is NULL THEN '' ELSE log_choferes.apellido_paterno END) || ' ' || CASE WHEN log_choferes.apellido_materno is NULL THEN '' ELSE log_choferes.apellido_materno END AS operador, "
            + "log_viaje.observaciones, "
            + "log_viaje.status "
        + "from log_viaje "
        + "left JOIN log_choferes on log_choferes.id=log_viaje.log_chofer_id "
        + "join log_vehiculos on log_vehiculos.id=log_viaje.log_vehiculo_id "
        + "left join log_vehiculo_clase on log_vehiculo_clase.id=log_viaje.log_vehiculo_clase_id "
        + "left join log_vehiculo_marca on log_vehiculo_marca.id=log_vehiculos.log_vehiculo_marca_id "
        + "left JOIN gral_suc on gral_suc.id=log_viaje.gral_suc_id "              
        //+ "where log_viaje.id=? order by log_viaje.id;";
        + "WHERE log_viaje.id="+id+";";
        
          
        
        
        System.out.println("DATOS PARA EL PDF:"+sql_to_query);
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        
       datos.put("id",map.get("id").toString());
       datos.put("folio",map.get("folio").toString());
       datos.put("fecha",map.get("fecha").toString());
       datos.put("hora",map.get("hora").toString());
       datos.put("suc_id",map.get("suc_id").toString());
       datos.put("nom_suc",map.get("nom_suc").toString());
       datos.put("vehiculo_id",map.get("vehiculo_id").toString());
       datos.put("no_unidad",map.get("no_unidad").toString());
       datos.put("unidad",map.get("unidad").toString());
       datos.put("no_economico",map.get("no_economico").toString());
       datos.put("placas",map.get("placas").toString());
       datos.put("tipo",map.get("tipo").toString());
       datos.put("operador",map.get("operador").toString());
       datos.put("no_operador",map.get("no_operador").toString());
       datos.put("observaciones",map.get("observaciones").toString());
       datos.put("status",map.get("status").toString());
        
        return datos;
    }
    
    
    
      //Obtener el detalle del viaje
    @Override
    public ArrayList<HashMap<String, String>> getLoAdmViaje_ListaPdf(Integer id) {
        String sql_to_query = ""
        + "SELECT "
            + "cxc_clie.numero_control AS no_clie,"
            + "substr(upper(cxc_clie.razon_social),1,12) AS clie,"
            + "log_viaje_det.id AS det_id,"
            + "log_doc_carga.id AS cga_id,"
            + "log_doc_ped.id AS ped_id,"
            + "log_doc_carga.no_carga,"
            + "log_doc_carga.fecha_entrega,"
            + "log_doc_ped.no_pedido,"
            + "cxc_destinatarios.id AS id_dest,"
            + "cxc_destinatarios.folio_ext AS no_dest,"
            + "cxc_destinatarios.razon_social AS nombre_dest,"
            + "log_viaje_det.solicitar_firma AS firma,"
            + "log_viaje_det.solicitar_sello AS sello,"
            + "log_viaje_det.solicitar_efectivo AS efectivo,"
            + "gral_mun.id AS mun_id,"
            + "upper(gral_mun.titulo) AS municipio,"
            + "log_doc_ped.estatus AS status_ped,"
            + "log_viaje_det.status AS status_det,"
            + "sum(log_doc_ped_det.peso) AS peso,"
            + "sum(log_doc_ped_det.volumen) AS volumen "
        + "FROM log_viaje_det "
        + "JOIN log_doc_carga ON log_doc_carga.id=log_viaje_det.log_doc_carga_id "
        + "JOIN log_doc_ped ON log_doc_ped.id=log_viaje_det.log_doc_ped_id  "
        + "JOIN log_doc_ped_det ON log_doc_ped_det.log_doc_ped_id=log_doc_ped.id "
        + "JOIN cxc_clie ON cxc_clie.id=log_viaje_det.cxc_clie_id "
        + "JOIN cxc_destinatarios ON cxc_destinatarios.id=log_doc_ped.cxc_dest_id  "
        + "JOIN gral_mun ON gral_mun.id=log_viaje_det.gral_mun_id  "
        + "WHERE log_viaje_det.log_viaje_id=? "
        + "group by cxc_clie.numero_control,cxc_clie.razon_social,log_viaje_det.id,log_doc_carga.id,log_doc_ped.id,log_doc_carga.no_carga,log_doc_carga.fecha_entrega, log_doc_ped.no_pedido, cxc_destinatarios.id,cxc_destinatarios.folio_ext,cxc_destinatarios.razon_social,log_viaje_det.solicitar_firma,log_viaje_det.solicitar_sello,log_viaje_det.solicitar_efectivo,gral_mun.id,gral_mun.titulo,log_doc_ped.estatus  "
        + "order by log_viaje_det.id";
        
        System.out.println("DATOS PARA EL PDFLISTA:"+sql_to_query);
       
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("no_clie",rs.getString("no_clie"));
                    row.put("clie",rs.getString("clie"));
                    row.put("det_id",rs.getString("det_id"));
                    row.put("cga_id",rs.getString("cga_id"));
                    row.put("f_entrega",rs.getString("fecha_entrega"));
                    row.put("ped_id",rs.getString("ped_id"));
                    row.put("no_carga",rs.getString("no_carga"));
                    row.put("no_pedido",rs.getString("no_pedido"));
                    row.put("id_dest",rs.getString("id_dest"));
                    row.put("no_dest",rs.getString("no_dest"));
                    row.put("nombre_dest",rs.getString("nombre_dest"));
                    row.put("firma",rs.getString("firma"));
                    row.put("sello",rs.getString("sello"));
                    row.put("efectivo",rs.getString("efectivo"));
                    row.put("mun_id",rs.getString("mun_id"));
                    row.put("municipio",rs.getString("municipio"));
                    row.put("status_ped",rs.getString("status_ped"));
                    row.put("status_det",rs.getString("status_det"));
                    row.put("peso",StringHelper.roundDouble(rs.getString("peso"),3));
                    row.put("volumen",StringHelper.roundDouble(rs.getString("volumen"),3));
                    return row;
                }
            }
        );

        return hm;
    }
    
    
    
}
