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
        
	String sql_to_query = "SELECT  log_rutas.id , folio, "
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

    
    
    
    
    
    
    
    
    
    
    
}
