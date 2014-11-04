/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.springdaos;

import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.interfacedaos.LogInterfaceDao;
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
    
    
    
    
    ///guarda los datos de los vehiculos
    @Override
    public ArrayList<HashMap<String, String>> getVehiculo_Datos(Integer id) {
        
        String sql_to_query = "SELECT marca,id,numero_economico FROM log_vehiculos WHERE id="+id;
        
        ArrayList<HashMap<String, String>> dato_vehiculo = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("marca",rs.getString("marca"));
                    row.put("numero_economico",rs.getString("numero_economico"));
                    return row;
                }
            }
        );
        return dato_vehiculo;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getVehiculos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT log_vehiculos.id, log_vehiculos.marca, log_vehiculos.numero_economico "                              
                                +"FROM log_vehiculos "                        
                                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = log_vehiculos.id "
                                +"WHERE log_vehiculos.borrado_logico=false "
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        System.out.println("esto es el query  :  "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("marca",rs.getString("marca"));
                    row.put("numero_economico",rs.getString("numero_economico"));
                   
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
                + "SELECT log_choferes.id, log_choferes.clave as numero_control, log_choferes.nombre  || ' ' || CASE " +
                                "WHEN log_choferes.apellido_paterno is NULL THEN '' " +
                                "ELSE log_choferes.apellido_paterno END || ' ' || CASE " +
                                "WHEN log_choferes.apellido_materno is NULL THEN '' " +
                                "ELSE log_choferes.apellido_materno END AS nombre " +
                                "FROM log_choferes "                       
                                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = log_choferes.id "
                                +"WHERE log_choferes.borrado_logico=false "
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("nombre",rs.getString("nombre"));
                   
                    return row;
                }
            }
        );
        return hm; 
    }

    


    ///Obtiene datos de operadores
    @Override
    public ArrayList<HashMap<String, String>> getOperadores_Datos(Integer id) {
        
        String sql_to_query = "SELECT id,clave,nombre,apellido_paterno,apellido_materno FROM log_choferes WHERE id="+id;
        
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
                    return row;
                }
            }
        );
        return dato_operador;
    }
    
    
    
    
    
    
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
    
    
    
    //Buscador de Unidades(Vehiculos)
    @Override
    public ArrayList<HashMap<String, Object>> getBuscadorUnidades(String no_unidad, String marca, Integer id_empresa, Integer id_sucursal) {
        String where="";
        if(id_sucursal!=0){
            where = "AND gral_suc_id="+id_sucursal;
        }
        
	String sql_query = "SELECT "
                    + "log_vehiculos.id,"
                    + "log_vehiculos.folio, "
                    + "log_vehiculos.numero_economico as no_eco,"
                    + "log_vehiculos.marca,"
                    + "(CASE WHEN log_vehiculo_tipo.id IS NULL THEN '' ELSE log_vehiculo_tipo.titulo END) AS tipo_unidad,"
                    + "log_vehiculos.placa,"
                    + "(CASE WHEN log_choferes.id IS NULL THEN '' ELSE log_choferes.clave END) AS no_operador,"
                    + "(CASE WHEN log_choferes.id IS NULL THEN '' ELSE ((CASE WHEN log_choferes.nombre IS NULL THEN '' ELSE log_choferes.nombre||' ' END)||(CASE WHEN log_choferes.apellido_paterno IS NULL THEN '' ELSE log_choferes.apellido_paterno||' ' END)||(CASE WHEN log_choferes.apellido_materno IS NULL THEN '' ELSE log_choferes.apellido_materno END)) END) AS operador "
                + "FROM log_vehiculos "
                + "LEFT JOIN log_vehiculo_tipo ON log_vehiculo_tipo.id=log_vehiculos.log_vehiculo_tipo_id "
                + "LEFT JOIN log_choferes ON log_choferes.id=log_vehiculos.log_chofer_id "
                + "WHERE log_vehiculos.folio ILIKE ? AND log_vehiculos.marca ILIKE ? AND log_vehiculos.gral_emp_id=? AND log_vehiculos.borrado_logico=false "+where+";";
        
        System.out.println("getBuscadorUnidades: "+sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{no_unidad, marca, new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("no_eco",rs.getString("no_eco"));
                    row.put("marca",rs.getString("marca"));
                    row.put("tipo_unidad",rs.getString("tipo_unidad"));
                    row.put("placa",rs.getString("placa"));
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
            where +=" AND gral_suc_id="+id_sucursal;
        }
        
        String sql_query = ""
                + "SELECT "
                    + "log_vehiculos.id,"
                    + "log_vehiculos.folio, "
                    + "log_vehiculos.numero_economico as no_eco,"
                    + "log_vehiculos.marca,"
                    + "(CASE WHEN log_vehiculo_tipo.id IS NULL THEN '' ELSE log_vehiculo_tipo.titulo END) AS tipo_unidad,"
                    + "log_vehiculos.placa,"
                    + "(CASE WHEN log_choferes.id IS NULL THEN '' ELSE log_choferes.clave END) AS no_operador,"
                    + "(CASE WHEN log_choferes.id IS NULL THEN '' ELSE ((CASE WHEN log_choferes.nombre IS NULL THEN '' ELSE log_choferes.nombre||' ' END)||(CASE WHEN log_choferes.apellido_paterno IS NULL THEN '' ELSE log_choferes.apellido_paterno||' ' END)||(CASE WHEN log_choferes.apellido_materno IS NULL THEN '' ELSE log_choferes.apellido_materno END)) END) AS operador "
                + "FROM log_vehiculos "
                + "LEFT JOIN log_vehiculo_tipo ON log_vehiculo_tipo.id=log_vehiculos.log_vehiculo_tipo_id "
                + "LEFT JOIN log_choferes ON log_choferes.id=log_vehiculos.log_chofer_id "
                + "WHERE upper(log_vehiculos.folio)=? AND log_vehiculos.gral_emp_id=? AND log_vehiculos.borrado_logico=false "+where+" LIMIT 1;";
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
                    row.put("tipo_unidad",rs.getString("tipo_unidad"));
                    row.put("placa",rs.getString("placa"));
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
            where +=" AND log_doc.gral_suc_id="+id_suc_user;
        }
        
        //no_clie, no_carga, no_ped, no_dest, dest, poblacion
        if(!no_clie.trim().equals("")){
            where +=" AND cxc_clie.numero_control ilike '%"+no_clie.toUpperCase()+"%'";
        }
        
        if(!no_carga.trim().equals("")){
            where +=" AND log_doc_carga.no_carga ilike '%"+no_carga.toUpperCase()+"%'";
        }
        
        if(!no_ped.trim().equals("")){
            where +=" AND log_doc_ped.no_pedido ilike '%"+no_ped.toUpperCase()+"%'";
        }
        
        if(!no_dest.trim().equals("")){
            where +=" AND cxc_destinatarios.folio_ext ilike '%"+no_dest.toUpperCase()+"%'";
        }
        
        if(!dest.trim().equals("")){
            where +=" AND cxc_destinatarios.razon_social ilike '%"+dest.toUpperCase()+"%'";
        }
        
        if(!poblacion.trim().equals("")){
            where +=" AND gral_mun.titulo ilike '%"+poblacion.toUpperCase()+"%'";
        }
        
        String sql_to_query = ""
        + "SELECT "
            + "cxc_clie.numero_control AS no_clie,"
            + "substr(upper(cxc_clie.razon_social),1,12) AS clie,"
            + "log_doc.id AS doc_id, "
            + "log_doc_carga.id AS cga_id, "
            + "log_doc_ped.id AS ped_id, "
            + "log_doc_carga.no_carga,"
            + "log_doc_carga.fecha_entrega, "
            + "log_doc_ped.no_pedido, "
            + "cxc_destinatarios.id AS id_dest,"
            + "cxc_destinatarios.folio_ext AS no_dest, "
            + "cxc_destinatarios.razon_social AS nombre_dest, "
            + "cxc_destinatarios.solicitar_firma AS firma, "
            + "cxc_destinatarios.solicitar_sello AS sello, "
            + "cxc_destinatarios.solicitar_efectivo AS efectivo, "
            + "gral_mun.id AS mun_id,"
            + "upper(gral_mun.titulo) AS municipio, "
            + "log_doc_ped.estatus AS status_ped,"
            + "sum(log_doc_ped_det.peso) AS peso, "
            + "sum(log_doc_ped_det.volumen) AS volumen "
        + "FROM log_doc "
        + "JOIN log_doc_carga ON log_doc_carga.log_doc_id=log_doc.id "
        + "JOIN log_doc_ped ON log_doc_ped.log_doc_carga_id=log_doc_carga.id "
        + "JOIN log_doc_ped_det ON log_doc_ped_det.log_doc_ped_id=log_doc_ped.id "
        + "JOIN cxc_clie ON cxc_clie.id=log_doc.cxc_clie_id "
        + "JOIN cxc_destinatarios ON cxc_destinatarios.id=log_doc_ped.cxc_dest_id  "
        + "JOIN gral_mun ON gral_mun.id=log_doc_ped.gral_mun_id  "
        + "WHERE log_doc.gral_emp_id=? "+ where +" "
        + "GROUP BY cxc_clie.numero_control, cxc_clie.razon_social, log_doc.id, log_doc_carga.id, log_doc_ped.id, log_doc_carga.no_carga, log_doc_ped.no_pedido, cxc_destinatarios.id, cxc_destinatarios.folio, cxc_destinatarios.razon_social, gral_mun.id, gral_mun.titulo "
        + "ORDER BY cxc_destinatarios.razon_social;";
        
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
            + "(CASE WHEN inv_prod_unidades.id IS NULL THEN '' ELSE inv_prod_unidades.titulo END) AS unidad,"
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
            + "(CASE WHEN log_vehiculos.id IS NULL THEN '' ELSE log_vehiculos.marca END) AS vehiculo, "
            + "(CASE WHEN log_vehiculo_tipo.id IS NULL THEN '' ELSE log_vehiculo_tipo.titulo END) AS tipo "
        + "FROM log_viaje "
        + "LEFT JOIN log_choferes ON log_choferes.id=log_viaje.log_chofer_id "
        + "LEFT JOIN log_vehiculos ON log_vehiculos.id=log_viaje.log_vehiculo_id "
        + "LEFT JOIN log_vehiculo_tipo ON log_vehiculo_tipo.id=log_viaje.log_vehiculo_tipo_id " 
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
                    row.put("vehiculo",rs.getString("vehiculo"));
                    row.put("tipo",rs.getString("tipo"));
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
            + "log_vehiculos.marca AS unidad, "
            + "log_viaje.no_economico, "
            + "log_viaje.placas, "
            + "(CASE WHEN log_vehiculo_tipo.id IS NULL THEN '' ELSE log_vehiculo_tipo.titulo END) AS tipo, "
            + "log_choferes.clave AS no_operador,"
            + "(CASE WHEN log_choferes.nombre IS NULL THEN '' ELSE log_choferes.nombre END) || (CASE WHEN log_choferes.apellido_paterno is NULL THEN '' ELSE log_choferes.apellido_paterno END) || CASE WHEN log_choferes.apellido_materno is NULL THEN '' ELSE log_choferes.apellido_materno END AS operador, "
            + "log_viaje.observaciones, "
            + "log_viaje.status "
        + "from log_viaje "
        + "left JOIN log_choferes on log_choferes.id=log_viaje.log_chofer_id "
        + "join log_vehiculos on log_vehiculos.id=log_viaje.log_vehiculo_id "
        + "left join log_vehiculo_tipo on log_vehiculo_tipo.id=log_viaje.log_vehiculo_tipo_id " 
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
                    row.put("tipo",rs.getString("tipo"));
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
    
    
    
    
    
    
}
