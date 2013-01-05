/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.springdaos;

import com.agnux.kemikal.interfacedaos.PocInterfaceDao;

import com.agnux.common.helpers.StringHelper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Noé Martinez
 * gpmarsan@gmail.com
 * 21/junio/2012
 */
public class PocSpringDao implements PocInterfaceDao{
    private JdbcTemplate jdbcTemplate;
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    
    @Override
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String string_array) {
        String sql_to_query = "select erp_fn_validaciones_por_aplicativo from erp_fn_validaciones_por_aplicativo('"+data+"',"+idApp+",array["+string_array+"]);";
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
        String sql_to_query = "select * from poc_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";
        
        System.out.println("Ejacutando Guardar:"+sql_to_query);
        //int update = this.getJdbcTemplate().queryForInt(sql_to_query);
        //return update;
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        valor_retorno = update.get("poc_adm_procesos").toString();
        
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
    public ArrayList<HashMap<String, Object>> getPocPedidos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
	String sql_to_query = "SELECT DISTINCT  "
                                    +"poc_pedidos.id, "
                                    +"poc_pedidos.folio, "
                                    +"cxc_clie.razon_social as cliente, "
                                    +"poc_pedidos.total, "
                                    +"gral_mon.descripcion_abr AS denominacion, "
                                    +"(CASE WHEN poc_pedidos.cancelado=TRUE THEN 'CANCELADO' ELSE erp_proceso_flujo.titulo END) as estado, "
                                    +"to_char(poc_pedidos.momento_creacion,'dd/mm/yyyy') as fecha_creacion,"
                                    + "gral_suc.titulo AS suc "
                            +"FROM poc_pedidos "
                            +"LEFT JOIN erp_proceso on erp_proceso.id = poc_pedidos.proceso_id "
                            +"LEFT JOIN fac_docs on fac_docs.proceso_id = erp_proceso.id "
                            +"LEFT JOIN erp_proceso_flujo on erp_proceso_flujo.id = erp_proceso.proceso_flujo_id "
                            +"LEFT JOIN cxc_clie on cxc_clie.id = poc_pedidos.cxc_clie_id "
                            +"LEFT JOIN gral_mon ON gral_mon.id=poc_pedidos.moneda_id "
                            + "LEFT JOIN gral_suc ON gral_suc.id=erp_proceso.sucursal_id "
                            +"JOIN ("+sql_busqueda+") as subt on subt.id=poc_pedidos.id "
                            + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("data_string: "+data_string);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("total"),2)));
                    row.put("denominacion",rs.getString("denominacion"));
                    row.put("estado",rs.getString("estado"));
                    row.put("fecha_creacion",rs.getString("fecha_creacion"));
                    row.put("suc",rs.getString("suc"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //obtiene datos del header del pedido
    @Override
    public ArrayList<HashMap<String, String>> getPocPedido_Datos(Integer id_pedido) {
        String sql_query = ""
        + "SELECT poc_pedidos.id,"
                + "poc_pedidos.folio,"
                + "erp_proceso.proceso_flujo_id,"
                + "poc_pedidos.moneda_id,"
                + "gral_mon.descripcion as moneda,"
                + "poc_pedidos.observaciones,"
                + "cxc_clie.id as cliente_id,"
                + "cxc_clie.numero_control,"
                + "cxc_clie.razon_social,"
                + "cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp AS direccion,"
                + "poc_pedidos.subtotal,"
                + "poc_pedidos.impuesto,"
                + "poc_pedidos.total,"
                + "poc_pedidos.tipo_cambio,"
                + "poc_pedidos.cxc_agen_id,"
                + "poc_pedidos.cxp_prov_credias_id,"
                + "poc_pedidos.orden_compra, "
                + "poc_pedidos.fecha_compromiso,"
                + "poc_pedidos.lugar_entrega,"
                + "poc_pedidos.transporte,"
                + "poc_pedidos.cancelado,"
                + "poc_pedidos.tasa_retencion_immex,"
                + "poc_pedidos.tipo_documento, "
                + "poc_pedidos.fac_metodos_pago_id AS metodo_pago_id,"
                + "poc_pedidos.no_cuenta, "
                + "poc_pedidos.enviar_ruta, "
                + "cxc_clie.cta_pago_mn, "
                + "cxc_clie.cta_pago_usd "
        + "FROM poc_pedidos "
        + "LEFT JOIN erp_proceso ON erp_proceso.id = poc_pedidos.proceso_id "
        + "LEFT JOIN gral_mon ON gral_mon.id = poc_pedidos.moneda_id "
        + "LEFT JOIN cxc_clie ON cxc_clie.id=poc_pedidos.cxc_clie_id "
        + "LEFT JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
        + "LEFT JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
        + "LEFT JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
        + "WHERE poc_pedidos.id="+id_pedido;
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("tipo_documento",String.valueOf(rs.getInt("tipo_documento")));
                    row.put("folio",rs.getString("folio"));
                    row.put("proceso_flujo_id",String.valueOf(rs.getInt("proceso_flujo_id")));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("cliente_id",rs.getString("cliente_id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("subtotal",StringHelper.roundDouble(rs.getDouble("subtotal"),2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getDouble("impuesto"),2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"),2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
                    row.put("cxc_agen_id",rs.getString("cxc_agen_id"));
                    row.put("cxp_prov_credias_id",rs.getString("cxp_prov_credias_id"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("fecha_compromiso",String.valueOf(rs.getDate("fecha_compromiso")));
                    row.put("lugar_entrega",rs.getString("lugar_entrega"));
                    row.put("transporte",rs.getString("transporte"));
                    row.put("cancelado",String.valueOf(rs.getBoolean("cancelado")));
                    row.put("tasa_retencion_immex",StringHelper.roundDouble(rs.getDouble("tasa_retencion_immex"),2));
                    row.put("metodo_pago_id",String.valueOf(rs.getInt("metodo_pago_id")));
                    row.put("no_cuenta",rs.getString("no_cuenta"));
                    row.put("enviar_ruta",String.valueOf(rs.getBoolean("enviar_ruta")));
                    row.put("cta_pago_mn",rs.getString("cta_pago_mn"));
                    row.put("cta_pago_usd",rs.getString("cta_pago_usd"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getPocPedido_DatosGrid(Integer id_pedido) {
        String sql_query = ""
                + "SELECT poc_pedidos_detalle.id as id_detalle,"
                    + "poc_pedidos_detalle.inv_prod_id,"
                    + "inv_prod.sku AS codigo,"
                    + "inv_prod.descripcion AS titulo,"
                    + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
                    + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS decimales,"
                    + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) as id_presentacion,"
                    + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) as presentacion,"
                    + "poc_pedidos_detalle.cantidad,"
                    + "poc_pedidos_detalle.precio_unitario,"
                    + "(poc_pedidos_detalle.cantidad * poc_pedidos_detalle.precio_unitario) AS importe, "
                    + "poc_pedidos_detalle.gral_imp_id,"
                    + "poc_pedidos_detalle.valor_imp,"
                    + "(CASE WHEN poc_pedidos_detalle.backorder=TRUE THEN 'checked' ELSE '' END) AS valor_check, "
                    + "(CASE WHEN poc_pedidos_detalle.backorder=TRUE THEN 1 ELSE 0 END) AS valor_selecionado, "
                    + "(poc_pedidos_detalle.cantidad - poc_pedidos_detalle.reservado) AS cant_produccion   "
                + "FROM poc_pedidos_detalle "
                + "LEFT JOIN inv_prod on inv_prod.id = poc_pedidos_detalle.inv_prod_id "
                + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
                + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = poc_pedidos_detalle.presentacion_id "
                + "WHERE poc_pedidos_detalle.poc_pedido_id="+id_pedido;
        
        //System.out.println("Obtiene datos grid prefactura: "+sql_query);
        ArrayList<HashMap<String, String>> hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_detalle",String.valueOf(rs.getInt("id_detalle")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",String.valueOf(rs.getInt("id_presentacion")));
                    row.put("presentacion",rs.getString("presentacion"));
                    //row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"), rs.getInt("decimales") ));
                    row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"), 2 ));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    
                    row.put("gral_imp_id",String.valueOf(rs.getInt("gral_imp_id")));
                    row.put("valor_imp",StringHelper.roundDouble(rs.getDouble("valor_imp"),2) );
                    
                    row.put("valor_check",rs.getString("valor_check"));
                    row.put("valor_selecionado",String.valueOf(rs.getInt("valor_selecionado")));
                    row.put("cant_produccion",StringHelper.roundDouble(rs.getDouble("cant_produccion"),2) );
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
   @Override
    public HashMap<String, String> getDatosPDF(Integer id_pedido) {
        HashMap<String, String> mappdf = new HashMap<String, String>();
       
        String sql_query = ""
        + "SELECT poc_pedidos.id,"
                + "poc_pedidos.folio,"
                + "erp_proceso.proceso_flujo_id,"
                + "poc_pedidos.moneda_id,"
                + "gral_mon.descripcion as moneda,"
                + "poc_pedidos.observaciones,"
                + "cxc_clie.id as cliente_id,"
                + "cxc_clie.numero_control,"
                + "cxc_clie.razon_social,"
                + "cxc_clie.calle, "
                + "cxc_clie.numero, "
                + "cxc_clie.colonia, "
                + "gral_mun.titulo AS municipio, "
                + "gral_edo.titulo  AS Estado, "
                + "gral_pais.titulo AS pais, "
                + "cxc_clie.cp AS cp,"
                + "cxc_clie.rfc AS rfc,"
                + "cxc_clie.telefono1 AS telefono, "
                + "poc_pedidos.subtotal, "
                + "poc_pedidos.impuesto, "
                + "poc_pedidos.total,"
                + "poc_pedidos.tipo_cambio, "
                + "poc_pedidos.monto_retencion, "
                + "poc_pedidos.cxc_agen_id,"
                + "poc_pedidos.cxp_prov_credias_id,"
                + "poc_pedidos.orden_compra, "
                + "poc_pedidos.fecha_compromiso, "
                + "poc_pedidos.lugar_entrega, "
                + "poc_pedidos.transporte, "
                + "poc_pedidos.cancelado, "
                + "poc_pedidos.tasa_retencion_immex, "
                + "poc_pedidos.observaciones,"
                + "to_char(poc_pedidos.momento_creacion,'dd-mm-yyyy HH24:MI')as fecha_expedicion, "
                + "poc_pedidos.gral_usr_id_autoriza, "
                + "(CASE WHEN poc_pedidos.gral_usr_id_autoriza=0 THEN '' ELSE gral_empleados.nombre_pila||' ' ||gral_empleados.apellido_paterno||' ' ||gral_empleados.apellido_materno END) AS nombre_autorizo_pedido,  "
                + "(case when cxc_agen.nombre is null then '' else cxc_agen.nombre  end) AS nombre_agente,  "
                + "poc_pedidos.cancelado "
        + "FROM poc_pedidos "
        + "LEFT JOIN erp_proceso ON erp_proceso.id = poc_pedidos.proceso_id "
        + "LEFT JOIN gral_mon ON gral_mon.id = poc_pedidos.moneda_id "
        + "LEFT JOIN cxc_clie ON cxc_clie.id=poc_pedidos.cxc_clie_id "
        + "LEFT JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
        + "LEFT JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
        + "LEFT JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
        + "LEFT JOIN gral_usr ON gral_usr.id = poc_pedidos.gral_usr_id_autoriza "
        + "LEFT JOIN gral_empleados ON gral_empleados.id = gral_usr.gral_empleados_id  "
        + "LEFT JOIN cxc_agen ON cxc_agen.id = poc_pedidos.cxc_agen_id "        
        + "WHERE poc_pedidos.id="+id_pedido;
        
        System.out.println("DATOS PARA EL PDF:"+sql_query);
        Map<String, Object> mapdatosquery = this.getJdbcTemplate().queryForMap(sql_query);
        
        mappdf.put("pedido_id", mapdatosquery.get("id").toString());
        mappdf.put("folio", mapdatosquery.get("folio").toString());
        mappdf.put("proceso_flujo_id", mapdatosquery.get("proceso_flujo_id").toString());
        mappdf.put("moneda_id", mapdatosquery.get("moneda_id").toString() );
        mappdf.put("moneda", mapdatosquery.get("moneda").toString() );
        mappdf.put("observaciones", mapdatosquery.get("observaciones").toString() );
        mappdf.put("cliente_id", mapdatosquery.get("cliente_id").toString() );
        mappdf.put("numero_control", mapdatosquery.get("numero_control").toString() );
        mappdf.put("razon_social", mapdatosquery.get("razon_social").toString() );
        
        mappdf.put("calle", mapdatosquery.get("calle").toString() );
        mappdf.put("numero", mapdatosquery.get("numero").toString() );
        mappdf.put("colonia", mapdatosquery.get("colonia").toString() );
        mappdf.put("municipio", mapdatosquery.get("municipio").toString() );
        mappdf.put("Estado", mapdatosquery.get("Estado").toString() );
        mappdf.put("pais", mapdatosquery.get("pais").toString() );
        mappdf.put("cp", mapdatosquery.get("cp").toString() );
        mappdf.put("rfc", mapdatosquery.get("rfc").toString() );
        mappdf.put("telefono", mapdatosquery.get("telefono").toString() );
        
        mappdf.put("observaciones", mapdatosquery.get("observaciones").toString() );
        mappdf.put("monto_retencion", mapdatosquery.get("monto_retencion").toString() );
        mappdf.put("nombre_autorizo_pedido", mapdatosquery.get("nombre_autorizo_pedido").toString() );
        mappdf.put("nombre_agente", mapdatosquery.get("nombre_agente").toString() );
        
        //mappdf.put("direccion", mapdatosquery.get("direccion").toString() );
        mappdf.put("subtotal", mapdatosquery.get("subtotal").toString() );
        mappdf.put("impuesto", mapdatosquery.get("impuesto").toString() );
        mappdf.put("total", mapdatosquery.get("total").toString() );
        mappdf.put("tipo_cambio", mapdatosquery.get("tipo_cambio").toString() );
        mappdf.put("cxc_agen_id", mapdatosquery.get("cxc_agen_id").toString() ); 
        mappdf.put("cxp_prov_credias_id", mapdatosquery.get("cxp_prov_credias_id").toString() ); 
        mappdf.put("orden_compra", mapdatosquery.get("orden_compra").toString() ); 
        mappdf.put("fecha_compromiso", mapdatosquery.get("fecha_compromiso").toString() ); 
        mappdf.put("lugar_entrega", mapdatosquery.get("lugar_entrega").toString() ); 
        mappdf.put("transporte", mapdatosquery.get("transporte").toString() ); 
        mappdf.put("cancelado", mapdatosquery.get("cancelado").toString() ); 
        mappdf.put("tasa_retencion_immex", mapdatosquery.get("tasa_retencion_immex").toString() ); 
        mappdf.put("fecha_expedicion", mapdatosquery.get("fecha_expedicion").toString() ); 
        mappdf.put("cancelado", mapdatosquery.get("cancelado").toString() ); 
        
        return mappdf;
    }
    
    
   
    
    @Override
    public ArrayList<HashMap<String, String>> getPocPedido_Almacenes(Integer id_sucursal) {
        String sql_to_query = "SELECT inv_alm.id, inv_alm.titulo FROM fac_par JOIN inv_alm ON inv_alm.id=fac_par.inv_alm_id WHERE gral_suc_id="+id_sucursal+";";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_monedas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_monedas;
    }
   
   
    
    
    @Override
    public ArrayList<HashMap<String, String>> getMonedas() {
        String sql_to_query = "SELECT id, descripcion FROM  gral_mon WHERE borrado_logico=FALSE AND ventas=TRUE ORDER BY id ASC;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_monedas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_monedas;
    }
    
    @Override
    public ArrayList<HashMap<String, String>> getAgentes(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "SELECT cxc_agen.id, cxc_agen.nombre AS nombre_agente "
                                +"FROM cxc_agen "
                                +"JOIN gral_usr_suc ON gral_usr_suc.gral_usr_id=cxc_agen.gral_usr_id "
                                +"JOIN gral_suc ON gral_suc.id=gral_usr_suc.gral_suc_id "
                                +"WHERE gral_suc.empresa_id="+id_empresa+" ORDER BY cxc_agen.id;";
        
        System.out.println("Obtener agentes:"+sql_to_query);
        
        ArrayList<HashMap<String, String>> hm_vendedor = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_vendedor;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCondicionesDePago() {
        String sql_to_query = "SELECT id,descripcion FROM cxc_clie_credias WHERE borrado_logico=FALSE;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id"))  );
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //obtiene el tipo de cambio actual
    @Override
    public Double getTipoCambioActual() {
        //System.out.println("FECHA ACTUAL: "+TimeHelper.getFechaActualYMD2());
        String sql_to_query = "SELECT valor AS tipo_cambio FROM erp_monedavers WHERE momento_creacion<=now() AND moneda_id=2 ORDER BY momento_creacion DESC LIMIT 1;";
        Map<String, Object> tipo_cambio = this.getJdbcTemplate().queryForMap(sql_to_query);
        Double valor_tipo_cambio = Double.parseDouble(StringHelper.roundDouble(tipo_cambio.get("tipo_cambio").toString(),4));
        
        return valor_tipo_cambio;
    }
    
    
    //obtiene valor del impuesto. retorna 0.16
    @Override
    public ArrayList<HashMap<String, String>> getValoriva(Integer id_sucursal) {
        String sql_to_query = ""
                + "SELECT "
                    + "gral_imptos.id AS id_impuesto, "
                    + "gral_imptos.iva_1 AS valor_impuesto "
                + "FROM gral_suc "
                + "JOIN gral_imptos ON gral_imptos.id=gral_suc.gral_impto_id "
                + "WHERE gral_imptos.borrado_logico=FALSE AND gral_suc.id=?";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_valoriva = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_sucursal)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_impuesto",String.valueOf(rs.getInt("id_impuesto")));
                    row.put("valor_impuesto",StringHelper.roundDouble(rs.getString("valor_impuesto"),2));
                    return row;
                }
            }
        );
        return hm_valoriva;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getBuscadorClientes(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal) {
        String where="";
	if(filtro == 1){
		where=" AND cxc_clie.numero_control ilike '%"+cadena.toUpperCase()+"%'";
	}
	if(filtro == 2){
		where=" AND cxc_clie.rfc ilike '%"+cadena.toUpperCase()+"%'";
	}
	if(filtro == 3){
		where=" AND cxc_clie.razon_social ilike '%"+cadena.toUpperCase()+"%'";
	}

	if(filtro == 4){
		where=" AND cxc_clie.curp ilike '%"+cadena.toUpperCase()+"%'";
	}
	if(filtro == 5){
		where=" AND cxc_clie.alias ilike '%"+cadena.toUpperCase()+"%'";
	}
	
        
        
	String sql_query = "SELECT "
                                    +"sbt.id, "
                                    +"sbt.numero_control, "
                                    +"sbt.rfc, "
                                    +"sbt.razon_social, "
                                    +"sbt.direccion, "
                                    +"sbt.moneda_id, "
                                    +"gral_mon.descripcion as moneda, "
                                    +"sbt.cxc_agen_id, "
                                    +"sbt.terminos_id, "
                                    +"sbt.empresa_immex, "
                                    +"sbt.tasa_ret_immex, "
                                    +"sbt.cta_pago_mn, "
                                    +"sbt.cta_pago_usd, "
                                    +"sbt.lista_precio "
                                    
                            +"FROM(SELECT cxc_clie.id, "
                                            +"cxc_clie.numero_control, "
                                            +"cxc_clie.rfc, "
                                            +"cxc_clie.razon_social,"
                                            +"cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion, "
                                            +"cxc_clie.moneda as moneda_id, "
                                            +"cxc_clie.cxc_agen_id, "
                                            +"cxc_clie.dias_credito_id AS terminos_id, "
                                            +"cxc_clie.empresa_immex, "
                                            +"(CASE WHEN cxc_clie.tasa_ret_immex IS NULL THEN 0 ELSE cxc_clie.tasa_ret_immex/100 END) AS tasa_ret_immex, "
                                            + "cxc_clie.cta_pago_mn,"
                                            + "cxc_clie.cta_pago_usd,  "
                                            + "cxc_clie.lista_precio "
                                           
                                    +"FROM cxc_clie "
                                    + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
                                    + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
                                    + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
                                    
                                    //+" WHERE empresa_id ="+id_empresa+"  AND sucursal_id="+id_sucursal
                                    +" WHERE empresa_id ="+id_empresa+" "
                                    + " AND cxc_clie.borrado_logico=false  "+where+" "
                            +") AS sbt "
                            +"LEFT JOIN gral_mon on gral_mon.id = sbt.moneda_id ";
                            
        System.out.println("Resultado del Query"+"___"+sql_query);
        
        ArrayList<HashMap<String, String>> hm_cli = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("cxc_agen_id",rs.getString("cxc_agen_id"));
                    row.put("terminos_id",rs.getString("terminos_id"));
                    row.put("empresa_immex",String.valueOf(rs.getBoolean("empresa_immex")));
                    row.put("tasa_ret_immex",StringHelper.roundDouble(String.valueOf(rs.getDouble("tasa_ret_immex")),2));
                    row.put("cta_pago_mn",rs.getString("cta_pago_mn"));
                    row.put("cta_pago_usd",rs.getString("cta_pago_usd"));
                    row.put("lista_precio",rs.getString("lista_precio"));
                    return row;
                }
            }
        );
        return hm_cli;
    }

    @Override
    public ArrayList<HashMap<String, String>> getProductoTipos() {
	String sql_query = "SELECT DISTINCT id ,titulo FROM inv_prod_tipos WHERE borrado_logico=false order by id;";
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
                            +"inv_prod.id, "
                            +"inv_prod.sku, "
                            +"inv_prod.descripcion, "
                            + "inv_prod.unidad_id, "
                            + "inv_prod_unidades.titulo AS unidad, "
                            +"inv_prod_tipos.titulo AS tipo, "
                            + "inv_prod_unidades.decimales "
                            
		+"FROM inv_prod "
                + "LEFT JOIN inv_prod_tipos ON inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
                + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                
                + "WHERE inv_prod.empresa_id="+id_empresa+" AND inv_prod.borrado_logico=false "+where+" ORDER BY inv_prod.descripcion;";
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
                    //row.put("precio",rs.getString("precio"));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }

    
    
    @Override
    public ArrayList<HashMap<String, String>> getPresentacionesProducto(String sku,String lista_precio, Integer id_empresa) {
	String sql_query = "SELECT "
                                +"inv_prod.id,"
                                +"inv_prod.sku,"
                                +"inv_prod.descripcion AS titulo,"
                                +"(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) AS unidad,"
                                +"(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) AS id_presentacion,"
                                +"(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion, "
                                +"(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS  decimales, "
                                +"inv_pre.precio_"+lista_precio+" as precio "
                        +"FROM inv_prod "
                        +"LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
                        +"LEFT JOIN inv_prod_pres_x_prod on inv_prod_pres_x_prod.producto_id = inv_prod.id "
                        +"LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = inv_prod_pres_x_prod.presentacion_id "
                        +"LEFT JOIN inv_pre on inv_pre.inv_prod_id = inv_prod.id "
                        +"WHERE  empresa_id = "+id_empresa+" AND inv_prod.sku ILIKE '"+sku+"' AND inv_prod.borrado_logico=false;";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",String.valueOf(rs.getInt("id_presentacion")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("decimales",rs.getString("decimales"));
                    row.put("precio",rs.getString("precio"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getRemisiones_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
	String sql_to_query = "SELECT DISTINCT  "
                                +"fac_rems.id, "
                                +"fac_rems.folio, "
                                +"cxc_clie.razon_social as cliente, "
                                +"fac_rems.total, "
                                +"gral_mon.descripcion_abr AS denominacion, "
                                +"(CASE WHEN fac_rems.cancelado=TRUE THEN 'CANCELADO' "
                                + "ELSE "
                                    + "(CASE WHEN fac_rems.facturado=TRUE THEN 'FACTURADO' ELSE '' END )"
                                + "END) as estado, "
                                +"to_char(fac_rems.momento_creacion,'dd/mm/yyyy') as fecha_creacion "
                            +"FROM fac_rems "
                            +"LEFT JOIN erp_proceso on erp_proceso.id = fac_rems.proceso_id "
                            +"LEFT JOIN fac_docs on fac_docs.proceso_id = erp_proceso.id "
                            +"LEFT JOIN erp_proceso_flujo on erp_proceso_flujo.id = erp_proceso.proceso_flujo_id "
                            +"LEFT JOIN cxc_clie on cxc_clie.id = fac_rems.cxc_clie_id "
                            +"LEFT JOIN gral_mon ON gral_mon.id=fac_rems.moneda_id "
                            +"JOIN ("+sql_busqueda+") as subt on subt.id=fac_rems.id "
                            + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("sql_to_query: "+sql_to_query);
        //System.out.println("data_string: "+data_string);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("total"),2)));
                    row.put("denominacion",rs.getString("denominacion"));
                    row.put("estado",rs.getString("estado"));
                    row.put("fecha_creacion",rs.getString("fecha_creacion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getMetodosPago() {
        String sql_to_query = "SELECT id, titulo FROM fac_metodos_pago WHERE borrado_logico=false;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id"))  );
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //obtiene datos de la remision para visualizar al consultar
    @Override
    public ArrayList<HashMap<String, String>> getRemisiones_Datos(Integer id_remision) {
        String sql_query = ""
                + "SELECT fac_rems.id, "
                        + "fac_rems.folio, "
                        + "fac_rems.folio_pedido, "
                        + "erp_proceso.proceso_flujo_id, "
                        + "fac_rems.moneda_id, "
                        + "gral_mon.descripcion as moneda, "
                        + "fac_rems.observaciones, "
                        + "cxc_clie.id as cliente_id, "
                        + "cxc_clie.numero_control, "
                        + "cxc_clie.razon_social, "
                        + "cxc_clie.empresa_immex, "
                        + "fac_rems.subtotal, "
                        + "fac_rems.impuesto, "
                        + "fac_rems.total,  "
                        + "fac_rems.tipo_cambio, "
                        + "fac_rems.cxc_agen_id,  "
                        + "fac_rems.cxc_clie_credias_id,  "
                        + "fac_rems.orden_compra,  "
                        + "fac_rems.cancelado,"
                        + "fac_rems.facturado,"
                        + "fac_rems.fac_metodos_pago_id,  "
                        + "fac_rems.no_cuenta, "
                        + "fac_rems.tasa_retencion_immex, "
                        + "(CASE WHEN (select count(id) from fac_rems_docs where fac_rem_id="+id_remision+") = 0 AND fac_rems.cancelado is false AND  fac_rems.estatus=0 THEN 0 ELSE 1 END) as estatus "//agregado por paco, por el boton de pagar
                + "FROM fac_rems  "
                + "LEFT JOIN erp_proceso ON erp_proceso.id = fac_rems.proceso_id  "
                + "LEFT JOIN gral_mon ON gral_mon.id = fac_rems.moneda_id  "
                + "LEFT JOIN cxc_clie ON cxc_clie.id=fac_rems.cxc_clie_id   "
                + "WHERE fac_rems.id="+id_remision; 

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("folio_pedido",rs.getString("folio_pedido"));
                    row.put("proceso_flujo_id",String.valueOf(rs.getInt("proceso_flujo_id")));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("cliente_id",rs.getString("cliente_id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("subtotal",StringHelper.roundDouble(rs.getDouble("subtotal"),2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getDouble("impuesto"),2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"),2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
                    row.put("cxc_agen_id",rs.getString("cxc_agen_id"));
                    row.put("terminos_id",rs.getString("cxc_clie_credias_id"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("cancelado",String.valueOf(rs.getBoolean("cancelado")));
                    row.put("facturado",String.valueOf(rs.getBoolean("facturado")));
                    row.put("fac_metodos_pago_id",String.valueOf(rs.getInt("fac_metodos_pago_id")));
                    row.put("no_cuenta",rs.getString("no_cuenta"));
                    row.put("empresa_immex",String.valueOf(rs.getBoolean("empresa_immex")));
                    row.put("tasa_retencion_immex",StringHelper.roundDouble(rs.getDouble("tasa_retencion_immex"),2));
                    row.put("estatus",String.valueOf(rs.getInt("estatus")));
                    return row;
                }
            }
        );
        return hm;
    }    
    

    @Override
    public ArrayList<HashMap<String, String>> getRemisiones_DatosGrid(Integer id_remision) {
        String sql_query = ""
                + "SELECT fac_rems_detalles.id as id_detalle,"
                        + "fac_rems_detalles.inv_prod_id,"
                        + "inv_prod.sku AS codigo,"
                        + "inv_prod.descripcion AS titulo,"
                        + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
                        + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS decimales,"
                        + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) as id_presentacion,"
                        + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) as presentacion,"
                        + "fac_rems_detalles.cantidad,"
                        + "fac_rems_detalles.precio_unitario,"
                        + "(fac_rems_detalles.cantidad * fac_rems_detalles.precio_unitario) AS importe, "
                        + "fac_rems_detalles.gral_imp_id,"
                        + "fac_rems_detalles.valor_imp "
                + "FROM fac_rems_detalles "
                + "LEFT JOIN inv_prod on inv_prod.id = fac_rems_detalles.inv_prod_id "
                + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
                + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = fac_rems_detalles.inv_prod_presentacion_id "
                + "WHERE fac_rems_detalles.fac_rems_id="+id_remision;

        //System.out.println("Obtiene datos grid prefactura: "+sql_query);
        ArrayList<HashMap<String, String>> hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_detalle",String.valueOf(rs.getInt("id_detalle")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",String.valueOf(rs.getInt("id_presentacion")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"), 2 ));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("gral_imp_id",String.valueOf(rs.getInt("gral_imp_id")));
                    row.put("valor_imp",StringHelper.roundDouble(rs.getDouble("valor_imp"),2) );
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
    
    
    @Override
    public HashMap<String, String> getRemisiones_DatosPdf(Integer id_remision) {
        HashMap<String, String> datos = new HashMap<String, String>();
        String sql_to_query = ""
                + "SELECT "
                        + "fac_rems.id AS id_remision,"
                        + "fac_rems.folio,"
                        + "fac_rems.subtotal,"
                        + "fac_rems.impuesto,"
                        + "fac_rems.monto_retencion,"
                        + "fac_rems.total,"
                        + "fac_rems.tipo_cambio,"
                        + "fac_rems.no_cuenta,"
                        + "fac_rems.orden_compra,"
                        + "fac_rems.observaciones,"
                        + "to_char(fac_rems.momento_creacion,'dd/mm/yyyy') AS fecha_remision,"
                        + "gral_mon.simbolo AS simbolo_moneda,"
                        + "gral_mon.descripcion_abr AS moneda_abr,"
                        + "gral_mon.descripcion AS titulo_moneda,"
                        + "cxc_clie_credias.dias AS dias_credito,"
                        + "cxc_clie.razon_social AS cliente,"
                        + "cxc_clie.rfc AS cliente_rfc, "
                        + "cxc_clie.calle AS cliente_calle,"
                        + "cxc_clie.numero AS cliente_numero,"
                        + "cxc_clie.colonia AS cliente_colonia,"
                        + "gral_mun.titulo AS cliente_municipio,"
                        + "gral_edo.titulo AS cliente_estado,"
                        + "gral_pais.titulo AS cliente_pais,"
                        + "cxc_clie.cp AS cliente_cp,"
                        + "cxc_clie.telefono1 AS cliente_telefono,"
                        + "cxc_agen.nombre AS vendedor, "
                        + "(CASE WHEN fac_rems.cancelado=TRUE THEN 'REMISION CANCELADA' ELSE 'NO' END) AS cancelado "
                + "FROM fac_rems "
                + "JOIN gral_mon ON gral_mon.id=fac_rems.moneda_id "
                + "JOIN cxc_clie ON cxc_clie.id=fac_rems.cxc_clie_id "
                + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
                + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
                + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
                + "JOIN cxc_clie_credias ON cxc_clie_credias.id = fac_rems.cxc_clie_credias_id "
                + "JOIN cxc_agen ON cxc_agen.id=fac_rems.cxc_agen_id "
                + "WHERE fac_rems.id="+id_remision;
        
        Map<String, Object> hm = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        datos.put("id_remision", hm.get("id_remision").toString());
        datos.put("folio", hm.get("folio").toString());
        datos.put("subtotal", StringHelper.roundDouble(hm.get("subtotal").toString(),2));
        datos.put("impuesto", StringHelper.roundDouble(hm.get("impuesto").toString(),2));
        datos.put("monto_retencion", StringHelper.roundDouble(hm.get("monto_retencion").toString(),2));
        datos.put("total", StringHelper.roundDouble(hm.get("total").toString(),2));
        datos.put("tipo_cambio", StringHelper.roundDouble(hm.get("tipo_cambio").toString(),4));
        datos.put("no_cuenta", hm.get("no_cuenta").toString());
        datos.put("orden_compra", hm.get("orden_compra").toString());
        datos.put("observaciones", hm.get("observaciones").toString());
        datos.put("fecha_remision", hm.get("fecha_remision").toString());
        datos.put("simbolo_moneda", hm.get("simbolo_moneda").toString());
        datos.put("moneda_abr", hm.get("moneda_abr").toString());
        datos.put("titulo_moneda", hm.get("titulo_moneda").toString());
        datos.put("dias_credito", hm.get("dias_credito").toString());
        datos.put("cliente", hm.get("cliente").toString());
        datos.put("cliente_rfc", hm.get("cliente_rfc").toString());
        datos.put("cliente_calle", hm.get("cliente_calle").toString());
        datos.put("cliente_numero", hm.get("cliente_numero").toString());
        datos.put("cliente_colonia", hm.get("cliente_colonia").toString());
        datos.put("cliente_municipio", hm.get("cliente_municipio").toString());
        datos.put("cliente_estado", hm.get("cliente_estado").toString());
        datos.put("cliente_pais", hm.get("cliente_pais").toString());
        datos.put("cliente_cp", hm.get("cliente_cp").toString());
        datos.put("vendedor", hm.get("vendedor").toString());
        datos.put("cliente_telefono", hm.get("cliente_telefono").toString());
        datos.put("cancelado", hm.get("cancelado").toString());
        return datos; 
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getRemisiones_ConceptosPdf(Integer id_remision, String rfc_empresa) {
        final String rfc = rfc_empresa;
	String sql_query = ""
                + "SELECT "
                        + "inv_prod.sku AS codigo,"
                        + "inv_prod.descripcion,"
                        + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
                        + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion,"
                        + "fac_rems_detalles.cantidad,"
                        + "fac_rems_detalles.precio_unitario,"
                        + "(fac_rems_detalles.cantidad * fac_rems_detalles.precio_unitario) AS importe "
                + "FROM fac_rems_detalles "
                + "LEFT JOIN inv_prod on inv_prod.id = fac_rems_detalles.inv_prod_id "
                + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
                + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = fac_rems_detalles.inv_prod_presentacion_id "
                + "WHERE fac_rems_detalles.fac_rems_id= "+id_remision;
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    //row.put("unidad",rs.getString("unidad"));
                    //row.put("presentacion",rs.getString("presentacion"));
                    
                    if( rfc.equals("PIS850531CS4") ){
                        row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("presentacion"))));
                    }else{
                        row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("unidad"))));
                    }
                    
                    row.put("cantidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("cantidad")),2));
                    row.put("precio_unitario",StringHelper.roundDouble(String.valueOf(rs.getDouble("precio_unitario")),2));
                    row.put("importe",StringHelper.roundDouble(String.valueOf(rs.getDouble("importe")),2));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
   //obtiene lista de peidos de un periodo
    @Override
    public ArrayList<HashMap<String, String>> getReportePedidos(Integer opcion, Integer agente, String cliente, String fecha_inicial, String fecha_final,Integer id_empresa) {
        String where="";
        
        if (opcion!=0){
            where+=" AND erp_proceso.proceso_flujo_id="+opcion;
        }
        
        if (agente!=0){
            where+=" AND poc_pedidos.cxc_agen_id="+agente;
        }
        
        if (!cliente.equals("")){
            where+=" AND cxc_clie.razon_social='"+cliente+"'";
        }
        
        String sql_to_query = ""
                + "SELECT "
                        + "folio,"
                        + "orden_compra,"
                        + "fecha_factura,"
                        + "cliente,"
                        + "moneda_factura,"
                        + "simbolo_moneda,"
                        + "subtotal,"
                        + "subtotal*tipo_cambio AS subtotal_mn, "
                        + "impuesto,"
                        + "impuesto*tipo_cambio AS impuesto_mn, "
                        + "total,"
                        + "total*tipo_cambio AS total_mn "
                + "FROM ( "
                        + "SELECT  "
                                + "poc_pedidos.id, "
                                + "poc_pedidos.folio, "
                                + "(CASE WHEN poc_pedidos.orden_compra IS NULL THEN '' ELSE poc_pedidos.orden_compra END) AS orden_compra,  "
                                + "to_char(poc_pedidos.momento_creacion,'dd/mm/yyyy') as fecha_factura, "
                                + "(CASE WHEN poc_pedidos.cancelado=FALSE THEN cxc_clie.razon_social ELSE 'CANCELADA' END) AS cliente, "
                                + "poc_pedidos.moneda_id, "
                                + "gral_mon.descripcion_abr AS moneda_factura, "
                                + "gral_mon.simbolo AS simbolo_moneda, "
                                + "(CASE WHEN poc_pedidos.cancelado=FALSE THEN poc_pedidos.subtotal ELSE 0.0 END) AS subtotal,  "
                                + "(CASE WHEN poc_pedidos.cancelado=FALSE THEN poc_pedidos.impuesto ELSE 0.0 END) AS impuesto, "
                                + "(CASE WHEN poc_pedidos.cancelado=FALSE THEN poc_pedidos.total ELSE 0.0 END) AS total,  "
                                + "(CASE WHEN poc_pedidos.moneda_id=1 THEN 1 ELSE poc_pedidos.tipo_cambio END) AS tipo_cambio   "
                        + "FROM poc_pedidos "
                        + "JOIN erp_proceso ON erp_proceso.id=poc_pedidos.proceso_id "
                        + "JOIN cxc_clie ON cxc_clie.id = poc_pedidos.cxc_clie_id   "
                        + "JOIN gral_mon ON gral_mon.id = poc_pedidos.moneda_id  "
                        + "WHERE erp_proceso.empresa_id="+id_empresa+" "+where+" "
                        + "AND (to_char(poc_pedidos.momento_creacion,'yyyymmdd')::integer BETWEEN  to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::integer AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::integer) "
                + ") AS sbt "
                + "ORDER BY id";
            
        System.out.println("Buscando facturas: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("folio",rs.getString("folio"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("moneda_factura",rs.getString("moneda_factura"));
                    row.put("simbolo_moneda",rs.getString("simbolo_moneda"));
                    row.put("subtotal",StringHelper.roundDouble(rs.getString("subtotal"),2));
                    row.put("subtotal_mn",StringHelper.roundDouble(rs.getString("subtotal_mn"),2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getString("impuesto"),2));
                    row.put("impuesto_mn",StringHelper.roundDouble(rs.getString("impuesto_mn"),2));
                    row.put("total",StringHelper.roundDouble(rs.getString("total"),2));
                    row.put("total_mn",StringHelper.roundDouble(rs.getString("total_mn"),2));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
    //alimenta el select de los agentes en reporte de pedidos
    
    @Override
    public ArrayList<HashMap<String, String>> getAgente(Integer id_empresa) {
        String sql_to_query = "SELECT cxc_agen.id, cxc_agen.nombre "
                               +"FROM cxc_agen "
                               +"join gral_usr on gral_usr.id = cxc_agen.gral_usr_id "
                               +"join gral_usr_suc on gral_usr_suc.gral_usr_id= gral_usr.id "
                               +"join gral_suc on gral_suc.id =gral_usr_suc.gral_suc_id "
                               +"WHERE gral_suc.empresa_id ="+id_empresa;
                                      
        
        ArrayList<HashMap<String,String>> hmtl_agente =(ArrayList<HashMap<String,String>>) this.jdbcTemplate.query(
            sql_to_query,new Object[]{},new RowMapper(){
                
             @Override
             public Object mapRow(ResultSet rs,int rowNum) throws SQLException{
                 HashMap<String, String > row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("nombre",String.valueOf(rs.getString("nombre")));
                    return row; 
             }
            }
        );
        
        return hmtl_agente;
                
    }
    @Override
    public ArrayList<HashMap<String, String>> getEstadoPedido() {
        String sql_to_query = "SELECT erp_proceso_flujo.titulo, "
                              +"erp_proceso_flujo.id "
                              +"FROM erp_proceso_flujo "
                              +"WHERE erp_proceso_flujo.id in(2,3,4,5)"
                              +"ORDER BY titulo";
        ArrayList<HashMap<String,String>> hmtl_agente =(ArrayList<HashMap<String,String>>) this.jdbcTemplate.query(
            sql_to_query,new Object[]{},new RowMapper(){
                
             @Override
             public Object mapRow(ResultSet rs,int rowNum) throws SQLException{
                 HashMap<String, String > row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",String.valueOf(rs.getString("titulo")));
                    return row; 
             }
            }
        );
        
        return hmtl_agente;
                
    }

    
    
    
    
    //metodo para reporte de articulos reservados
    @Override
    public ArrayList<HashMap<String, String>> getReporteArticulosReservados(Integer id_empresa,Integer id_usuario,String codigo, String descripcion) {
        //System.out.println("Codigo: "+codigo+"Descripcion: "+descripcion);
        String   cadena_where="";
        if(!codigo.equals("")){
            cadena_where=" and inv_prod.sku = '"+codigo +"'";
        }
        
        if(!descripcion.equals("")){
            cadena_where=" and inv_prod.descripcion ilike '"+descripcion +"'";
        }
        
       String sql_to_query =""
               + "SELECT  "
                   + "poc_pedidos.id as id_pedido, "
                   + "poc_pedidos.folio as pedido, "
                   + "to_char(poc_pedidos.momento_creacion,'yyyy-mm-dd') as fecha, "
                   + "cxc_clie.razon_social as cliente,  "
                   + "poc_pedidos_detalle.cantidad,  "
                   + "(case when poc_pedidos.moneda_id=1 then  poc_pedidos_detalle.precio_unitario  else (poc_pedidos_detalle.precio_unitario * tipo_cambio  )end ) as precio_unitario,  "
                   + "poc_pedidos.moneda_id, "
                   + "poc_pedidos_detalle.cantidad * poc_pedidos_detalle.precio_unitario as importe_sin_checar_tipo_cambio, "
                   + "(case when poc_pedidos.moneda_id=1 then  poc_pedidos_detalle.cantidad * poc_pedidos_detalle.precio_unitario else ((poc_pedidos_detalle.precio_unitario * tipo_cambio )*  poc_pedidos_detalle.cantidad )  end) as importe, "
                   + "inv_prod.sku, "
                   + "inv_prod.descripcion "
               + "FROM poc_pedidos_detalle "
               + "join inv_prod on inv_prod.id = poc_pedidos_detalle.inv_prod_id  "
               + "join poc_pedidos on poc_pedidos.id =  poc_pedidos_detalle.poc_pedido_id  "
               + "join cxc_clie on cxc_clie.id = poc_pedidos.cxc_clie_id "
               + "join erp_proceso on erp_proceso.id = poc_pedidos.proceso_id  "
               + "WHERE poc_pedidos.cancelado=false AND erp_proceso.proceso_flujo_id IN (2,4) "
               + "AND erp_proceso.empresa_id= "+id_empresa +" "+cadena_where
               + "  order by sku asc ";
      
       System.out.println("Articulos Reservados:"+ sql_to_query);
       
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query, 
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_pedido",String.valueOf(rs.getInt("id_pedido")));
                    row.put("pedido",rs.getString("pedido"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("cantidad",String.valueOf(rs.getDouble("cantidad")));
                    row.put("precio_unitario",String.valueOf(rs.getDouble("precio_unitario")));
                    row.put("importe",String.valueOf(rs.getDouble("importe")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    
                    return row;
                }
            }
        );
        return hm; 
    }
    
    @Override
    public ArrayList<HashMap<String, String>> getListaPrecio(Integer lista_precio) {
        String sql_query="SELECT "  
                        +"gral_mon_id_pre"+lista_precio+" as moneda_id "
                        + "FROM inv_pre "
                        
                        + " LIMIT 1 ";
                           
        System.out.println("Resultado de la Moneda de lista"+"___"+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_query, 
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    
                    
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    
    
}
