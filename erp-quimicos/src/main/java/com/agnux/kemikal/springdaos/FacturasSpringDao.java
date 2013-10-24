/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.agnux.kemikal.springdaos;

import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.helpers.n2t;
import com.agnux.kemikal.interfacedaos.FacturasInterfaceDao;

import java.math.BigInteger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Noé Martinez
 * gpmarsan@gmail.com
 * 
 */

public class FacturasSpringDao implements FacturasInterfaceDao{
    private JdbcTemplate jdbcTemplate;
    private String fechaComprobante;
    private String subTotal;
    private String impuestoTrasladado;
    private String impuestoRetenido;
    private String tasaRetencion;
    private String total;
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public String getSubTotal() {
        return subTotal;
    }
    
    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }
    
    public String getImpuestoTrasladado() {
        return impuestoTrasladado;
    }
    public void setImpuestoTrasladado(String impuesto) {
        this.impuestoTrasladado = impuesto;
    }
    
    public String getImpuestoRetenido() {
        return impuestoRetenido;
    }

    public void setImpuestoRetenido(String impuestoRetenido) {
        this.impuestoRetenido = impuestoRetenido;
    }
    
    public String getTasaRetencion() {
        return tasaRetencion;
    }

    public void setTasaRetencion(String tasaRetencion) {
        this.tasaRetencion = tasaRetencion;
    }
    
    
    public String getTotal() {
        return total;
    }
    
    public void setTotal(String total) {
        this.total = total;
    }
    
    public void setFechaComprobante(String fechaComprobante) {
        this.fechaComprobante = fechaComprobante;
    }
    
    @Override
    public String getFechaComprobante() {
        return fechaComprobante;
    }
    
    
    @Override
    public int countAll(String data_string) {
        String sql_busqueda = "select id from gral_bus_catalogos('"+data_string+"') as foo (id integer)";
        String sql_to_query = "select count(id)::int as total from ("+sql_busqueda+") as subt";
        
        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        return rowCount;
    }
    
    
    @Override
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String string_array) {
        String sql_to_query = "select erp_fn_validaciones_por_aplicativo from erp_fn_validaciones_por_aplicativo('"+data+"',"+idApp+",array["+string_array+"]);";
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
    
    
    
    //Ejecuta procesos relacionados a facturacion
    @Override
    public String selectFunctionForFacAdmProcesos(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from fac_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";
        
        //System.out.println("selectFunctionForFacAdmProcesos: "+sql_to_query);
        
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        valor_retorno = update.get("fac_adm_procesos").toString();
        return valor_retorno;
    }
    
    
    

    @Override
    public HashMap<String, String> getFac_Parametros(Integer id_emp, Integer id_suc) {
        HashMap<String, String> mapDatos = new HashMap<String, String>();
        String sql_query = "SELECT * FROM fac_par WHERE gral_emp_id="+id_emp+" AND gral_suc_id="+id_suc+";";
        System.out.println("sql_query: "+sql_query);
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_query);
        
        mapDatos.put("gral_suc_id", String.valueOf(map.get("gral_suc_id")));
        mapDatos.put("gral_suc_id_consecutivo", String.valueOf(map.get("gral_suc_id_consecutivo")));
        mapDatos.put("cxc_mov_tipo_id", String.valueOf(map.get("cxc_mov_tipo_id")));
        mapDatos.put("inv_alm_id", String.valueOf(map.get("inv_alm_id")));
        mapDatos.put("gral_emp_id", String.valueOf(map.get("gral_emp_id")));
        mapDatos.put("formato_pedido", String.valueOf(map.get("formato_pedido")));
        mapDatos.put("formato_factura", String.valueOf(map.get("formato_factura")));
        mapDatos.put("permitir_pedido", String.valueOf(map.get("permitir_pedido")));
        mapDatos.put("permitir_remision", String.valueOf(map.get("permitir_remision")));
        mapDatos.put("permitir_cambio_almacen", String.valueOf(map.get("permitir_cambio_almacen")));
        mapDatos.put("permitir_servicios", String.valueOf(map.get("permitir_servicios")));
        mapDatos.put("permitir_articulos", String.valueOf(map.get("permitir_articulos")));
        mapDatos.put("permitir_kits", String.valueOf(map.get("permitir_kits")));
        mapDatos.put("incluye_adenda", String.valueOf(map.get("incluye_adenda")).toLowerCase());
        return mapDatos;
    }

    
    @Override
    public ArrayList<HashMap<String, Object>> getFacturas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT DISTINCT "
                                    +"fac_docs.id, "
                                    +"fac_docs.serie_folio, "
                                    +"cxc_clie.razon_social as cliente, "
                                    +"fac_docs.total, "
                                    +"gral_mon.descripcion_abr AS moneda, "
                                    +"to_char(fac_docs.momento_creacion,'dd/mm/yyyy') AS fecha_facturacion, "
                                    +"to_char(fac_docs.fecha_vencimiento,'dd/mm/yyyy') AS fecha_venc, "
                                    +"(CASE WHEN fac_docs.folio_pedido IS NULL THEN '' ELSE fac_docs.folio_pedido END ) AS folio_pedido, "
                                    +"(CASE WHEN fac_docs.orden_compra IS NULL THEN '' ELSE fac_docs.orden_compra END) AS oc, "
                                    +"(CASE WHEN fac_docs.cancelado=FALSE THEN (CASE WHEN erp_h_facturas.pagado=TRUE THEN 'PAGADO' ELSE '' END) ELSE 'CANCELADO' END) AS estado, "
                                    +"(CASE WHEN fac_docs.cancelado=FALSE THEN (CASE WHEN erp_h_facturas.pagado=TRUE THEN to_char(fecha_ultimo_pago::timestamp with time zone,'dd/mm/yyyy') ELSE '' END) ELSE '' END) AS fecha_pago "
                            +"FROM fac_docs  "
                            +"JOIN erp_proceso on erp_proceso.id=fac_docs.proceso_id  "
                            +"LEFT JOIN cxc_clie on cxc_clie.id=fac_docs.cxc_clie_id  "
                            +"LEFT JOIN gral_mon ON gral_mon.id=fac_docs.moneda_id  "
                            +"LEFT JOIN erp_h_facturas ON erp_h_facturas.serie_folio=fac_docs.serie_folio "
        +"JOIN ("+sql_busqueda+") as subt on subt.id=fac_docs.id "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        //System.out.println("cliente: "+cliente+ "fecha_inicial:"+fecha_inicial+" fecha_final: "+fecha_final+ " offset:"+offset+ " pageSize: "+pageSize+" orderBy:"+orderBy+" asc:"+asc);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("total"),2)));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("fecha_facturacion",rs.getString("fecha_facturacion"));
                    row.put("fecha_venc",rs.getString("fecha_venc"));
                    row.put("folio_pedido",rs.getString("folio_pedido"));
                    row.put("oc",rs.getString("oc"));
                    row.put("estado",rs.getString("estado"));
                    row.put("fecha_pago",rs.getString("fecha_pago"));
                    
                    if(rs.getString("estado").toUpperCase().equals("CANCELADO")){
                        row.put("accion","<td><INPUT TYPE=\"button\" classs=\"cancel\" id=\"cancel_"+rs.getInt("id")+"\" value=\"Cancelar\" disabled=\"true\" style=\"width:65px; height:15px; font-weight:bold;\"></td>");
                    }else{
                        row.put("accion","<td><INPUT TYPE=\"button\" classs=\"cancel\" id=\"cancel_"+rs.getInt("id")+"\" value=\"Cancelar\" style=\"width:65px; height:15px; font-weight:bold;\"></td>");
                    }
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //obtiene  los datos de la Factura
    @Override
    public ArrayList<HashMap<String, Object>> getFactura_Datos(Integer id_factura) {
        
        String sql_query = "SELECT fac_docs.id, "
                +"fac_docs.folio_pedido,  "
                +"fac_docs.serie_folio,  "
                +"fac_docs.moneda_id,  "
                +"fac_docs.observaciones,  "
                +"cxc_clie.id as cliente_id,  "
                +"cxc_clie.rfc,  "
                +"cxc_clie.razon_social,  "
                + "fac_docs.cxc_clie_df_id, "
                + "(CASE WHEN fac_docs.cxc_clie_df_id > 1 THEN "
                    + "sbtdf.calle||' '||sbtdf.numero_interior||' '||sbtdf.numero_exterior||', '||sbtdf.colonia||', '||sbtdf.municipio||', '||sbtdf.estado||', '||sbtdf.pais||' C.P. '||sbtdf.cp "
                + "ELSE "
                    + "cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp "
                + "END ) AS direccion,"
                +"fac_docs.subtotal,"
                +"fac_docs.impuesto,"
                +"fac_docs.total,"
                +"fac_docs.monto_retencion,"
                +"fac_docs.tipo_cambio,"
                +"(CASE WHEN fac_docs.cancelado=FALSE THEN '' ELSE 'CANCELADO' END) as estado,   "
                +"fac_docs.cxc_agen_id,"
                +"fac_docs.terminos_id,"
                +"fac_docs.orden_compra, "
                + "fac_docs.fac_metodos_pago_id,  "
                + "fac_docs.no_cuenta, "
                + "cxc_clie.tasa_ret_immex/100 AS tasa_ret_immex, "
                + "erp_h_facturas.saldo_factura "
        +"FROM fac_docs   "
        +"JOIN erp_h_facturas ON erp_h_facturas.serie_folio=fac_docs.serie_folio "
        +"LEFT JOIN cxc_clie ON cxc_clie.id=fac_docs.cxc_clie_id "
        +"LEFT JOIN gral_mon ON gral_mon.id = fac_docs.moneda_id "
        +"LEFT JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
        +"LEFT JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
        +"LEFT JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
        +"LEFT JOIN (SELECT cxc_clie_df.id, (CASE WHEN cxc_clie_df.calle IS NULL THEN '' ELSE cxc_clie_df.calle END) AS calle, (CASE WHEN cxc_clie_df.numero_interior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_interior IS NULL OR cxc_clie_df.numero_interior='' THEN '' ELSE 'NO.INT.'||cxc_clie_df.numero_interior END)  END) AS numero_interior, (CASE WHEN cxc_clie_df.numero_exterior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_exterior IS NULL OR cxc_clie_df.numero_exterior='' THEN '' ELSE 'NO.EXT.'||cxc_clie_df.numero_exterior END )  END) AS numero_exterior, (CASE WHEN cxc_clie_df.colonia IS NULL THEN '' ELSE cxc_clie_df.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,(CASE WHEN cxc_clie_df.cp IS NULL THEN '' ELSE cxc_clie_df.cp END) AS cp  FROM cxc_clie_df LEFT JOIN gral_pais ON gral_pais.id = cxc_clie_df.gral_pais_id LEFT JOIN gral_edo ON gral_edo.id = cxc_clie_df.gral_edo_id LEFT JOIN gral_mun ON gral_mun.id = cxc_clie_df.gral_mun_id ) AS sbtdf ON sbtdf.id = fac_docs.cxc_clie_df_id "
        +"WHERE fac_docs.id=? ";
        
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id_factura)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("folio_pedido",rs.getString("folio_pedido"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("cliente_id",rs.getString("cliente_id"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("df_id",rs.getInt("cxc_clie_df_id"));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("subtotal",StringHelper.roundDouble(rs.getDouble("subtotal"),2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getDouble("impuesto"),2));
                    row.put("monto_retencion",StringHelper.roundDouble(rs.getDouble("monto_retencion"),2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"),2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
                    row.put("estado",rs.getString("estado"));
                    row.put("cxc_agen_id",rs.getString("cxc_agen_id"));
                    row.put("terminos_id",rs.getString("terminos_id"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("fac_metodos_pago_id",String.valueOf(rs.getInt("fac_metodos_pago_id")));
                    row.put("no_cuenta",rs.getString("no_cuenta"));
                    row.put("tasa_ret_immex",StringHelper.roundDouble(rs.getDouble("tasa_ret_immex"),2));
                    row.put("saldo_fac",StringHelper.roundDouble(rs.getDouble("saldo_factura"),2));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //obtiene el listado de conceptos de la factura
    @Override
    public ArrayList<HashMap<String, Object>> getFactura_DatosGrid(Integer id_factura) {
        String sql_query = "SELECT fac_docs_detalles.inv_prod_id, "
                +"inv_prod.sku  AS codigo_producto, "
                +"inv_prod.descripcion AS titulo, "
                +"(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) AS unidad, "
                +"(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS decimales, "
                +"(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) AS id_presentacion, "
                +"(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion, "
                +"fac_docs_detalles.cantidad, "
                +"fac_docs_detalles.precio_unitario, "
                +"(fac_docs_detalles.cantidad * fac_docs_detalles.precio_unitario) AS importe,"
                + "fac_docs_detalles.gral_imptos_id, "
                + "fac_docs_detalles.valor_imp, "
                + "fac_docs_detalles.cantidad_devolucion "
        +"FROM fac_docs_detalles "
        +"LEFT JOIN inv_prod on inv_prod.id = fac_docs_detalles.inv_prod_id  "
        +"LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = fac_docs_detalles.inv_prod_unidad_id  "
        +"LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = fac_docs_detalles.inv_prod_presentacion_id  "
        +"WHERE fac_docs_detalles.fac_doc_id = ? ";
        
        //System.out.println("Obtiene datos grid FACTURA: "+sql_query);
        //System.out.println("id_factura: "+id_factura);
        ArrayList<HashMap<String, Object>> hm_grid = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id_factura)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("inv_prod_id",rs.getString("inv_prod_id"));
                    row.put("codigo_producto",rs.getString("codigo_producto"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",rs.getString("id_presentacion"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"), rs.getInt("decimales") ));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("id_impto",rs.getString("gral_imptos_id"));
                    row.put("tasa_iva",StringHelper.roundDouble(rs.getDouble("valor_imp"),2) );
                    row.put("cant_dev",StringHelper.roundDouble(rs.getDouble("cantidad_devolucion"),2) );
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
    //obtiene todas la monedas
    @Override
    public ArrayList<HashMap<String, Object>> getFactura_Monedas() {
        String sql_to_query = "SELECT id, descripcion FROM  gral_mon WHERE borrado_logico=FALSE AND ventas=TRUE ORDER BY id ASC;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm_monedas = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
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
    public ArrayList<HashMap<String, Object>> getFactura_Agentes(Integer id_empresa, Integer id_sucursal) {
        //String sql_to_query = "SELECT id,nombre_pila||' '||apellido_paterno||' '||apellido_materno AS nombre_vendedor FROM erp_empleados WHERE borrado_logico=FALSE AND vendedor=TRUE AND empresa_id="+id_empresa+" AND sucursal_id="+id_sucursal;
        
        String sql_to_query = "SELECT cxc_agen.id,  "
                                        +"cxc_agen.nombre AS nombre_vendedor "
                                +"FROM cxc_agen "
                                +"JOIN gral_usr_suc ON gral_usr_suc.gral_usr_id=cxc_agen.gral_usr_id "
                                +"JOIN gral_suc ON gral_suc.id=gral_usr_suc.gral_suc_id "
                                +"WHERE gral_suc.empresa_id="+id_empresa+" ORDER BY cxc_agen.id;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm_vendedor = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id")  );
                    row.put("nombre_vendedor",rs.getString("nombre_vendedor"));
                    return row;
                }
            }
        );
        return hm_vendedor;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getFactura_DiasDeCredito() {
        String sql_to_query = "SELECT id,descripcion FROM cxc_clie_credias WHERE borrado_logico=FALSE;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id")  );
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getMetodosPago() {
        String sql_to_query = "SELECT id, titulo FROM fac_metodos_pago WHERE borrado_logico=false;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id"))  );
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, Integer>>  getFactura_AnioInforme() {
        ArrayList<HashMap<String, Integer>> anios = new ArrayList<HashMap<String, Integer>>();
        
        Calendar c1 = Calendar.getInstance();
        Integer annio = c1.get(Calendar.YEAR);//obtiene el año actual
        
        for(int i=0; i<15; i++) {
            HashMap<String, Integer> row = new HashMap<String, Integer>();
            row.put("valor",(annio-i));
            anios.add(i, row);
        }
        return anios;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //obtiene el tipo de cambio actual
    //se utiliza en prefacturas y facturas
    @Override
    public Double getTipoCambioActual() {
        //System.out.println("FECHA ACTUAL: "+TimeHelper.getFechaActualYMD2());
        String sql_to_query = "SELECT valor AS tipo_cambio FROM erp_monedavers WHERE momento_creacion<=now() AND moneda_id=2 ORDER BY momento_creacion DESC LIMIT 1;";
        Map<String, Object> tipo_cambio = this.getJdbcTemplate().queryForMap(sql_to_query);
        Double valor_tipo_cambio = Double.parseDouble(StringHelper.roundDouble(tipo_cambio.get("tipo_cambio").toString(),4));
        
        return valor_tipo_cambio;
    }
    
    
    
    //obtiene valor del impuesto. retorna 0.16 o 0.11
    @Override
    public ArrayList<HashMap<String, Object>> getValoriva(Integer id_sucursal) {
        String sql_to_query = ""
                + "SELECT "
                    + "gral_imptos.id AS id_impuesto, "
                    + "gral_imptos.iva_1 AS valor_impuesto "
                + "FROM gral_suc "
                + "JOIN gral_imptos ON gral_imptos.id=gral_suc.gral_impto_id "
                + "WHERE gral_imptos.borrado_logico=FALSE AND gral_suc.id=?";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm_valoriva = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, new Object[]{new Integer(id_sucursal)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_impuesto",rs.getString("id_impuesto"));
                    row.put("valor_impuesto",StringHelper.roundDouble(rs.getString("valor_impuesto"),2));
                    return row;
                }
            }
        );
        return hm_valoriva;
    }
    
    //obtiene tasa actual del iva de la sucursal. Retorna la tasa 16% u 11%
    private String getTasaIva(Integer id_sucursal) {
        //System.out.println("FECHA ACTUAL: "+TimeHelper.getFechaActualYMD2());
        String sql_to_query = "SELECT "
                + "gral_imptos.iva_1*100 as valor  "
                + "FROM gral_suc "
                + "JOIN gral_imptos ON gral_imptos.id=gral_suc.gral_impto_id "
                + "WHERE gral_imptos.borrado_logico=FALSE AND gral_suc.id="+id_sucursal;
        
        Map<String, Object> map_iva = this.getJdbcTemplate().queryForMap(sql_to_query);
        String valor_iva = StringHelper.roundDouble(map_iva.get("valor").toString(),2);
        return valor_iva;
    }
    
    
    //extrae los datos para crear el informe mensual
    @Override
    public ArrayList<HashMap<String, Object>> getComprobantesActividadPorMes(String year,String month,Integer id_empresa){       
        String sql_to_query = "SELECT DISTINCT * FROM fac_cfds "
                            + "WHERE 1=1 AND (momento_expedicion ILIKE '" + year + "-" + month + "%' OR momento_cancelacion::character varying ILIKE '" + year + "-" + month + "%')  "
                            + "AND empresa_id="+ id_empresa
                            + " ORDER BY id;";
        
        //System.out.println("SQL comprobantes por mes: "+sql_to_query);
        
        ArrayList<HashMap<String, Object>> mn = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    
                    row.put("pedimento",rs.getString("pedimento"));
                    row.put("numero_de_aprobacion",rs.getString("numero_de_aprobacion"));
                    row.put("estado_del_comprobante",rs.getString("estado_del_comprobante"));
                    row.put("fecha_de_pedimento",rs.getString("fecha_de_pedimento"));
                    row.put("aduana",rs.getString("aduana"));
                    row.put("anoaprovacion",rs.getString("anoaprovacion"));
                    row.put("monto_de_la_operacion",rs.getString("monto_de_la_operacion"));
                    row.put("monto_del_impuesto",rs.getString("monto_del_impuesto"));
                    row.put("momento_expedicion",rs.getString("momento_expedicion"));
                    row.put("folio_del_comprobante_fiscal",rs.getString("folio_del_comprobante_fiscal"));
                    row.put("rfc_cliente",rs.getString("rfc_cliente"));
                    row.put("serie",rs.getString("serie"));
                    row.put("efecto_de_comprobante",rs.getString("tipo_comprobante"));//se toma el valor de tipo_comprobante
                    
                    return row;
                }
            }
        );
        return mn;
    }
    
    
    
    
    
    //obtiene datos para la factura(CFD, CFDI, CFDTF)
    @Override
    public HashMap<String, String> getDataFacturaXml(Integer id_prefactura) {
        HashMap<String, String> data = new HashMap<String, String>();
        
        //obtener id del cliente
        String sql_to_query = ""
                + "SELECT erp_prefacturas.cliente_id, "
                        + "fac_metodos_pago.titulo AS metodo_pago, "
                        + "(CASE WHEN fac_metodos_pago.id=7 THEN 'NO APLICA' ELSE erp_prefacturas.no_cuenta END ) AS no_cuenta, "
                        + "cxc_clie_credias.descripcion AS condicion_pago,"
                        + "gral_mon.iso_4217 AS moneda, "
                        + "gral_mon.iso_4217_anterior AS moneda2, "
                        + "gral_mon.simbolo AS simbolo_moneda, "
                        + "erp_prefacturas.tipo_cambio, "
                        + "cxc_clie.numero_control, "
                        + "cxc_clie.razon_social, "
                        + "cxc_clie.rfc, "
                        + "cxc_clie.cxc_clie_tipo_adenda_id AS adenda_id, "
                        + "erp_prefacturas.orden_compra, "
                        + "(CASE WHEN cxc_clie.localidad_alternativa IS NULL THEN '' ELSE cxc_clie.localidad_alternativa END) AS localidad_alternativa, "
                        + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN sbtdf.calle ELSE cxc_clie.calle END ) AS calle,"
                        + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN sbtdf.numero_interior ELSE (CASE WHEN cxc_clie.numero='' OR cxc_clie.numero IS NULL THEN '' ELSE cxc_clie.numero END)  END ) AS numero_interior,"
                        + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN sbtdf.numero_exterior ELSE (CASE WHEN cxc_clie.numero_exterior='' OR cxc_clie.numero_exterior IS NULL THEN '' ELSE cxc_clie.numero_exterior END) END ) AS numero_exterior,"
                        + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN sbtdf.colonia ELSE cxc_clie.colonia END ) AS colonia,"
                        + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN sbtdf.municipio ELSE gral_mun.titulo END ) AS municipio,"
                        + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN sbtdf.estado ELSE gral_edo.titulo END ) AS estado,"
                        + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN sbtdf.pais ELSE gral_pais.titulo END ) AS pais,"
                        + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN sbtdf.cp ELSE cxc_clie.cp END ) AS cp "
                + "FROM erp_prefacturas  "
                + "LEFT JOIN cxc_clie ON cxc_clie.id=erp_prefacturas.cliente_id "
                + "LEFT JOIN fac_metodos_pago ON fac_metodos_pago.id=erp_prefacturas.fac_metodos_pago_id "
                + "LEFT JOIN cxc_clie_credias ON cxc_clie_credias.id=erp_prefacturas.terminos_id "
                + "LEFT JOIN gral_mon ON gral_mon.id=erp_prefacturas.moneda_id "
                + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
                + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
                + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
                + "LEFT JOIN (SELECT cxc_clie_df.id, (CASE WHEN cxc_clie_df.calle IS NULL THEN '' ELSE cxc_clie_df.calle END) AS calle, (CASE WHEN cxc_clie_df.numero_interior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_interior IS NULL OR cxc_clie_df.numero_interior='' THEN '' ELSE 'NO.INT.'||cxc_clie_df.numero_interior END)  END) AS numero_interior, (CASE WHEN cxc_clie_df.numero_exterior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_exterior IS NULL OR cxc_clie_df.numero_exterior='' THEN '' ELSE 'NO.EXT.'||cxc_clie_df.numero_exterior END )  END) AS numero_exterior, (CASE WHEN cxc_clie_df.colonia IS NULL THEN '' ELSE cxc_clie_df.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,(CASE WHEN cxc_clie_df.cp IS NULL THEN '' ELSE cxc_clie_df.cp END) AS cp  FROM cxc_clie_df LEFT JOIN gral_pais ON gral_pais.id = cxc_clie_df.gral_pais_id LEFT JOIN gral_edo ON gral_edo.id = cxc_clie_df.gral_edo_id LEFT JOIN gral_mun ON gral_mun.id = cxc_clie_df.gral_mun_id ) AS sbtdf ON sbtdf.id = erp_prefacturas.cxc_clie_df_id "
                + "WHERE erp_prefacturas.id="+id_prefactura;
                
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        int id_cliente = Integer.parseInt(map.get("cliente_id").toString());
        
        String fecha = TimeHelper.getFechaActualYMDH();
        String[] fecha_hora = fecha.split(" ");
        //formato fecha: 2011-03-01T00:00:00
        this.setFechaComprobante(fecha_hora[0]+"T"+fecha_hora[1]);//este solo se utiliza en pdfcfd
        
        data.put("comprobante_attr_fecha",fecha_hora[0]+"T"+fecha_hora[1]);
        data.put("comprobante_attr_condicionesdepago",map.get("condicion_pago").toString().toUpperCase());
        data.put("comprobante_attr_formadepago","PAGO EN UNA SOLA EXIBICION");
        data.put("comprobante_attr_motivodescuento","");
        data.put("comprobante_attr_descuento","0.00");
        data.put("comprobante_attr_subtotal",this.getSubTotal());
        data.put("comprobante_attr_total",this.getTotal());
        data.put("comprobante_attr_moneda",map.get("moneda").toString().toUpperCase());
        
        //Este campo es utilizado para la adenda de Quimiproductos
        data.put("moneda2",map.get("moneda2").toString().toUpperCase());
        data.put("orden_compra",map.get("orden_compra").toString().toUpperCase());
        
        
                
        data.put("comprobante_attr_simbolo_moneda",map.get("simbolo_moneda").toString().toUpperCase());
        data.put("comprobante_attr_tc",StringHelper.roundDouble(map.get("tipo_cambio").toString(), 4));
        data.put("comprobante_attr_metododepago",map.get("metodo_pago").toString().toUpperCase());
        String no_cta ="";
        if (!map.get("no_cuenta").toString().equals("null") && !map.get("no_cuenta").toString().equals("")){
            no_cta=map.get("no_cuenta").toString();
        }
        data.put("comprobante_attr_numerocuenta",no_cta);
        
        String numero_ext="";
        if(map.get("numero_exterior").toString().equals("'")){
            numero_ext = "";
        }else{
            numero_ext = map.get("numero_exterior").toString();
        }
        
        String numero_int="";
        if(map.get("numero_interior").toString().equals("'")){
            numero_int = "";
        }else{
            numero_int = map.get("numero_interior").toString();
        }
        
        //datos del cliente
        data.put("comprobante_receptor_attr_nombre",map.get("razon_social").toString());
        data.put("comprobante_receptor_attr_rfc",map.get("rfc").toString());
        data.put("comprobante_receptor_domicilio_attr_calle",map.get("calle").toString());
        data.put("comprobante_receptor_domicilio_attr_noexterior",numero_ext);
        data.put("comprobante_receptor_domicilio_attr_nointerior",numero_int);
        data.put("comprobante_receptor_domicilio_attr_colonia",map.get("colonia").toString());
        data.put("comprobante_receptor_domicilio_attr_localidad",map.get("localidad_alternativa").toString());
        data.put("comprobante_receptor_domicilio_attr_referencia","");
        data.put("comprobante_receptor_domicilio_attr_municipio",map.get("municipio").toString());
        data.put("comprobante_receptor_domicilio_attr_estado",map.get("estado").toString());
        data.put("comprobante_receptor_domicilio_attr_pais",map.get("pais").toString());
        data.put("comprobante_receptor_domicilio_attr_codigopostal",map.get("cp").toString());
        data.put("adenda_id",map.get("adenda_id").toString());
        
        //este solo se utiliza en el pdfcfd y cfdi
        data.put("numero_control",map.get("numero_control").toString());
        
        return data;
    }
    
    
    
    
    //este se utiliza en: 
    //xml de Factura CFD y Nota de Credito CFD
    public void calcula_Totales_e_Impuestos(ArrayList<LinkedHashMap<String, String>> conceptos) throws SQLException{
        Double sumaImporte = 0.0;
        Double sumaImpuesto = 0.0;
        Double tasa_retencion=0.0;
        Double monto_retencion=0.0;
        Double montoTotal=0.0;
        
        for (int x=0; x<=conceptos.size()-1;x++){
            LinkedHashMap<String,String> con = conceptos.get(x);
            sumaImporte = sumaImporte + Double.parseDouble(StringHelper.roundDouble(con.get("importe"),2));
            sumaImpuesto = sumaImpuesto + Double.parseDouble(StringHelper.roundDouble(con.get("importe_impuesto"),2));
            tasa_retencion = Double.parseDouble(StringHelper.roundDouble(con.get("tasa_retencion"),2));
        }
        
        //System.out.println("Canculando Totales de la Factura");
        
        monto_retencion = sumaImporte * tasa_retencion;
        montoTotal = sumaImporte + sumaImpuesto - monto_retencion;
        
        this.setSubTotal(StringHelper.roundDouble(sumaImporte,2));
        this.setImpuestoTrasladado(StringHelper.roundDouble(sumaImpuesto,2));
        this.setImpuestoRetenido(StringHelper.roundDouble(monto_retencion,2));
        this.setTasaRetencion(StringHelper.roundDouble(tasa_retencion,2));
        this.setTotal(StringHelper.roundDouble(montoTotal,2));
        
        //System.out.println("tasa_retencion: "+ tasa_retencion);
        //System.out.println("Subtotal: "+ this.getSubTotal()+"      Trasladado: "+ sumaImpuesto+ "      Retenido: "+ monto_retencion+ "      Total: "+ this.getTotal());
        
    }
    
    
    
    
    
    //obtiene la lista de conceptos para la factura
    @Override
    public ArrayList<LinkedHashMap<String, String>> getListaConceptosFacturaXml(Integer id_prefactura) {
        String sql_query = "SELECT sku,"
                                + "titulo_producto,"
                                + "unidad,"
                                + "cantidad,"
                                + "(CASE WHEN moneda_id=1 THEN precio_unitario ELSE precio_unitario * tipo_cambio END) AS precio_unitario,"
                                + "(CASE WHEN moneda_id=1 THEN importe ELSE importe * tipo_cambio END) AS importe,"
                                + "(CASE WHEN moneda_id=1 THEN importe * valor_imp ELSE (importe * tipo_cambio) * valor_imp END) AS importe_impuesto,"
                                + "id_impto,"
                                + "tasa_impuesto,"
                                + "valor_imp,"
                                + "tasa_retencion_immex,"
                                + "moneda_id,"
                                + "nombre_moneda,"
                                + "moneda_abr,"
                                + "simbolo_moneda,"
                                + "tipo_cambio "
                        + "FROM( "
                                + "SELECT inv_prod.sku,"
                                        + "inv_prod.descripcion AS titulo_producto,"
                                        + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
                                        + "erp_prefacturas_detalles.cant_facturar AS cantidad,"
                                        + "erp_prefacturas_detalles.precio_unitario,"
                                        + "(erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) AS importe, "
                                        + "erp_prefacturas_detalles.tipo_impuesto_id AS id_impto,"
                                        + "erp_prefacturas_detalles.valor_imp,"
                                        + "(erp_prefacturas_detalles.valor_imp * 100::double precision) AS tasa_impuesto,"
                                        + "erp_prefacturas.tasa_retencion_immex,"
                                        + "erp_prefacturas.moneda_id,"
                                        + "gral_mon.descripcion AS nombre_moneda,"
                                        + "gral_mon.descripcion_abr AS moneda_abr,"
                                        + "gral_mon.simbolo AS simbolo_moneda,"
                                        + "erp_prefacturas.tipo_cambio "
                                + "FROM erp_prefacturas "
                                + "JOIN erp_prefacturas_detalles on erp_prefacturas_detalles.prefacturas_id=erp_prefacturas.id "
                                + "JOIN gral_mon on gral_mon.id=erp_prefacturas.moneda_id "
                                + "LEFT JOIN inv_prod on inv_prod.id = erp_prefacturas_detalles.producto_id "
                                + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = erp_prefacturas_detalles.inv_prod_unidad_id "
                                + "WHERE erp_prefacturas_detalles.prefacturas_id="+id_prefactura+" "
                        + ") AS sbt";
        
        //System.out.println(sql_query);
        //System.out.println("Obteniendo lista de conceptos: "+sql_query);
        
        //System.out.println("noIdentificacion "+" | descripcion      "+" | cant"+" | precio_uni"+" | importe"+" | importe_imp"+" | valor_imp"+" | tasa_ret"  );
        
        ArrayList<LinkedHashMap<String, String>> hm_conceptos = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    //row = aplicar_tipo_cambio_ListaConceptosFactura(rs);
                    row.put("noIdentificacion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("sku"))));
                    row.put("descripcion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("titulo_producto"))));
                    row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("unidad"))));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("valorUnitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),4) );
                    row.put("id_impto",String.valueOf(rs.getInt("id_impto")));
                    row.put("tasa_impuesto",StringHelper.roundDouble(rs.getDouble("tasa_impuesto"),2) );
                    row.put("valor_imp",StringHelper.roundDouble(rs.getDouble("valor_imp"),2) );
                    row.put("importe_impuesto",StringHelper.roundDouble(rs.getDouble("importe_impuesto"),4) );
                    row.put("numero_aduana","");
                    row.put("fecha_aduana","");
                    row.put("aduana_aduana","");
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("nombre_moneda",rs.getString("nombre_moneda"));
                    row.put("moneda_abr",rs.getString("moneda_abr"));
                    row.put("simbolo_moneda",rs.getString("simbolo_moneda"));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4) );
                    row.put("tasa_retencion",StringHelper.roundDouble(rs.getDouble("tasa_retencion_immex"),2) );
                    
                    //System.out.println(row.get("noIdentificacion")+"   "+row.get("descripcion")+"   "+row.get("cantidad")+"   "+row.get("valorUnitario")+"   "+row.get("importe")+"   "+row.get("importe_impuesto")+"   "+row.get("valor_imp")+"   "+row.get("tasa_retencion"));
                    
                    return row;
                }
            }
        );
        
        try {
            calcula_Totales_e_Impuestos(hm_conceptos);
        } catch (SQLException ex) {
            Logger.getLogger(FacturasSpringDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return hm_conceptos;
    }
    
    
    
    //Éste método se utiliza para CFD y CFDI con Timbre Fiscal
    @Override
    public ArrayList<LinkedHashMap<String, String>> getImpuestosRetenidosFacturaXml() {
        ArrayList<LinkedHashMap<String, String>>  impuestos = new ArrayList<LinkedHashMap<String, String>>();
        LinkedHashMap<String,String> impuesto = new LinkedHashMap<String,String>();
        impuesto.put("impuesto", "IVA");
        impuesto.put("importe", this.getImpuestoRetenido());
        impuesto.put("tasa", this.getTasaRetencion());
        impuestos.add(impuesto);
        
        return impuestos;
    }
    
    
    
    
    //Éste método se utiliza para CFD y CFDI con Timbre Fiscal
    @Override
    public ArrayList<LinkedHashMap<String, String>> getImpuestosTrasladadosFacturaXml(Integer id_sucursal, ArrayList<LinkedHashMap<String,String>> conceptos) {
        ArrayList<LinkedHashMap<String, String>>  impuestos = new ArrayList<LinkedHashMap<String, String>>();
        LinkedHashMap<String,String> impuesto;
        
        Double sumaImpuesto1=0.0;//1;"IVA 16%";0.16
        Double sumaImpuesto2=0.0;//2;"IVA TASA 0";0
        Double sumaImpuesto3=0.0;//3;"IVA 11%";0.11
        Double sumaImpuesto4=0.0;//4;"EXENTO";0
        Double sumaImpuesto5=0.0;//4;"EXENTO";0
        String tasaImpuesto1="0.00";
        String tasaImpuesto2="0.00";
        String tasaImpuesto3="0.00";
        String tasaImpuesto5="0.00";
        boolean impuesto1=false;
        boolean impuesto2=false;
        boolean impuesto3=false;
        boolean impuesto5=false;
        
        for (int x=0; x<=conceptos.size()-1;x++){
            LinkedHashMap<String,String> con = conceptos.get(x);
            Integer idImpuestoProd = Integer.parseInt(con.get("id_impto"));
            Double importeImpuestoProd = Double.parseDouble(StringHelper.roundDouble(con.get("importe_impuesto"),2));
            String tasa = con.get("tasa_impuesto");
            
            if(idImpuestoProd==1){
                sumaImpuesto1 = sumaImpuesto1 + importeImpuestoProd;
                tasaImpuesto1 = tasa;
                impuesto1=true;
            }
            
            if(idImpuestoProd==2 || idImpuestoProd==4){
                sumaImpuesto2 = sumaImpuesto2 + importeImpuestoProd;
                tasaImpuesto2 = tasa;
                impuesto2=true;
            }
            
            if(idImpuestoProd==3){
                sumaImpuesto3 = sumaImpuesto3 + importeImpuestoProd;
                tasaImpuesto3 = tasa;
                impuesto3=true;
            }
            
            //esto por si agregan un impuesto mas
            if(idImpuestoProd==5){
                sumaImpuesto5 = sumaImpuesto5 + importeImpuestoProd;
                tasaImpuesto5 = tasa;
                impuesto5=true;
            }
            //System.out.println("idImpuestoProd:"+idImpuestoProd+" | tasa:"+tasa+" | importe:"+importeImpuestoProd);
        }
        
        if(impuesto1 == true){
            impuesto = new LinkedHashMap<String,String>();
            impuesto.put("impuesto", "IVA");
            impuesto.put("importe", StringHelper.roundDouble(sumaImpuesto1,2));
            impuesto.put("tasa", tasaImpuesto1);
            impuestos.add(impuesto);
            //System.out.println("impuesto1:IVA | tasa:"+tasaImpuesto1+" | importe:"+sumaImpuesto1);
        }
        
        if(impuesto2 == true){
            impuesto = new LinkedHashMap<String,String>();
            impuesto.put("impuesto", "IVA");
            impuesto.put("importe", StringHelper.roundDouble(sumaImpuesto2,2));
            impuesto.put("tasa", tasaImpuesto2);
            impuestos.add(impuesto);
            //System.out.println("impuesto2:IVA | tasa:"+tasaImpuesto2+" | importe:"+sumaImpuesto2);
        }
        
        if(impuesto3 == true){
            impuesto = new LinkedHashMap<String,String>();
            impuesto.put("impuesto", "IVA");
            impuesto.put("importe", StringHelper.roundDouble(sumaImpuesto3,2));
            impuesto.put("tasa", tasaImpuesto3);
            impuestos.add(impuesto);
            //System.out.println("impuesto3:IVA | tasa:"+tasaImpuesto3+" | importe:"+sumaImpuesto3);
        }
        
        //esto por si agregan un impuesto mas
        if(impuesto5 == true){
            impuesto = new LinkedHashMap<String,String>();
            impuesto.put("impuesto", "IVA");
            impuesto.put("importe", StringHelper.roundDouble(sumaImpuesto5,2));
            impuesto.put("tasa", tasaImpuesto5);
            impuestos.add(impuesto);
            //System.out.println("impuesto5:IVA | tasa:"+tasaImpuesto5+" | importe:"+sumaImpuesto5);
        }
        
        return impuestos;
    }
    
    
    //obtiene datos extras para la factura
    @Override
    public LinkedHashMap<String, String> getDatosExtrasFacturaXml(String id_prefactura, String tipo_cambio_vista, String id_usuario, String id_moneda, Integer id_empresa, Integer id_sucursal,  String refacturar, Integer app_selected, String command_selected, String extra_data_array) {
        LinkedHashMap<String,String> datosExtras = new LinkedHashMap<String,String>();
        //estos son requeridos para cfd
        datosExtras.put("prefactura_id", id_prefactura);
        datosExtras.put("tipo_cambio", tipo_cambio_vista);
        datosExtras.put("moneda_id", id_moneda);
        datosExtras.put("usuario_id", id_usuario);
        datosExtras.put("empresa_id", String.valueOf(id_empresa));
        datosExtras.put("sucursal_id", String.valueOf(id_sucursal));
        datosExtras.put("refacturar", refacturar);
        datosExtras.put("app_selected", String.valueOf(app_selected));
        datosExtras.put("command_selected", command_selected);
        datosExtras.put("extra_data_array", extra_data_array);
        
        return datosExtras;
    }
    
    
    
    //obtiene datos extras cfdi
    @Override
    public LinkedHashMap<String, String> getDatosExtrasCfdi(Integer id_factura) {
        LinkedHashMap<String,String> datosExtras = new LinkedHashMap<String,String>();
        String monto_factura="";
        String id_moneda="";
        String denom="";
        String denominacion = "";
        String cantidad_letras="";
        
        //obtener id del cliente
        String sql_to_query = ""
                + "SELECT translate(fac_docs.serie_folio,'0123456789 ','') AS serie,"
                        + "translate(fac_docs.serie_folio,'ABCDEFGHIJKLMNÑOPQRSTUVWXYZ abcdefghijklmnñopqrstuvwxyz','') AS folio,"
                        + "(CASE WHEN fac_docs.orden_compra='' THEN '-' ELSE fac_docs.orden_compra END) AS orden_compra,"
                        + "fac_docs.moneda_id, "
                        + "gral_mon.descripcion_abr AS simbolo_moneda, "
                        + "gral_mon.iso_4217 AS nombre_moneda,"
                        + "fac_docs.tipo_cambio,"
                        + "(CASE WHEN fac_docs.cxc_agen_id=0 THEN '' ELSE cxc_agen.nombre END ) AS clave_agente, "
                        + "fac_docs.subtotal as subtotal_conceptos, "
                        + "fac_docs.impuesto, "
                        + "fac_docs.total as monto_total, "
                        + "fac_docs.observaciones "
                + "FROM fac_docs "
                + "JOIN gral_mon ON gral_mon.id=fac_docs.moneda_id "
                + "JOIN cxc_agen ON cxc_agen.id=fac_docs.cxc_agen_id "
                + "WHERE  fac_docs.id="+id_factura+";";
        
        //System.out.println("DatosExtrasCfdi: "+sql_to_query);
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        
        
        //estos son requeridos para cfdi
        datosExtras.put("serie", map.get("serie").toString());
        datosExtras.put("folio", map.get("folio").toString());
        datosExtras.put("orden_compra", map.get("orden_compra").toString());
        datosExtras.put("clave_agente", map.get("clave_agente").toString());
        datosExtras.put("nombre_moneda", map.get("nombre_moneda").toString());
        datosExtras.put("tipo_cambio", StringHelper.roundDouble(map.get("tipo_cambio").toString(),4));
        datosExtras.put("subtotal_conceptos", StringHelper.roundDouble(map.get("subtotal_conceptos").toString(),2));
        datosExtras.put("monto_total", StringHelper.roundDouble(map.get("monto_total").toString(),2));
        datosExtras.put("observaciones", map.get("observaciones").toString());
        
        monto_factura = StringHelper.roundDouble(map.get("monto_total").toString(),2);
        id_moneda = map.get("moneda_id").toString();
        
        BigInteger num = new BigInteger(monto_factura.split("\\.")[0]);
        n2t cal = new n2t();
        String centavos = monto_factura.substring(monto_factura.indexOf(".")+1);
        String numero = cal.convertirLetras(num);
        
        //convertir a mayuscula la primera letra de la cadena
        String numeroMay = numero.substring(0, 1).toUpperCase() + numero.substring(1, numero.length());
        
        denom = map.get("simbolo_moneda").toString();
        
        if(centavos.equals(num.toString())){
            centavos="00";
        }
        
        if(id_moneda.equals("1")){
            denominacion = "pesos";
        }
        
        if(id_moneda.equals("2")){
            denominacion = "dolares";
        }
        
        cantidad_letras=numeroMay + " " + denominacion + ", " +centavos+"/100 "+ denom;
        datosExtras.put("monto_total_texto", cantidad_letras.toUpperCase());
        
        return datosExtras;
    }
    
    
    
    
    
    //obtiene la lista de conceptos para cfdi con Buzon Fiscal
    @Override
    public ArrayList<LinkedHashMap<String, String>> getListaConceptosCfdi(Integer id_factura, String rfcEmisor) {
        
        final String rfc = rfcEmisor;
        
        String sql_query = "SELECT inv_prod.sku,"
                                    +"inv_prod.descripcion,"
                                    +"(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
                                    +"(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion, "
                                    +"fac_docs_detalles.cantidad,"
                                    +"fac_docs_detalles.precio_unitario,"
                                    +"(fac_docs_detalles.cantidad * fac_docs_detalles.precio_unitario) AS importe, "
                                    +"fac_docs.moneda_id AS moneda_factura "
                            +"FROM fac_docs "
                            +"JOIN fac_docs_detalles on fac_docs_detalles.fac_doc_id=fac_docs.id "
                            +"LEFT JOIN inv_prod on inv_prod.id = fac_docs_detalles.inv_prod_id "
                            +"LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = fac_docs_detalles.inv_prod_unidad_id "
                            +"LEFT JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=fac_docs_detalles.inv_prod_presentacion_id "
                            +"WHERE fac_docs.id="+id_factura+";";
        
        //System.out.println("Obteniendo lista de conceptos para cfdi: "+sql_query);
        ArrayList<LinkedHashMap<String, String>> hm_conceptos = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("valorUnitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),2) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("noIdentificacion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("sku"))));
                    row.put("descripcion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("descripcion"))));
                    /*
                    if( rfc.equals("PIS850531CS4") ){
                        row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("presentacion"))));
                    }else{
                        row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("unidad"))));
                    }
                    */
                    row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("unidad"))));
                    
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("numero_aduana","");
                    row.put("fecha_aduana","");
                    row.put("aduana_aduana","");
                    row.put("moneda_factura",rs.getString("moneda_factura"));
                    return row;
                }
            }
        );
        return hm_conceptos;
    }
    
    
    
    @Override
    public ArrayList<LinkedHashMap<String, String>> getImpuestosTrasladadosCfdi(Integer id_factura, Integer id_sucursal) {
        String sql_to_query = "SELECT impuesto FROM fac_docs WHERE  id="+id_factura+" AND impuesto >0 AND impuesto IS NOT NULL;";
        
        final Integer id_suc = id_sucursal;
        
        ArrayList<LinkedHashMap<String, String>> tras = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("impuesto","IVA");
                    row.put("importe",StringHelper.roundDouble(rs.getString("impuesto"),2));
                    row.put("tasa", getTasaIva(id_suc) );
                    return row;
                }
            }
        );
        return tras;
    }
    
    /*
    @Override
    public ArrayList<LinkedHashMap<String, String>> getImpuestosTrasladadosCfdi(Integer id_factura) {
        String sql_to_query = ""
                + "SELECT id_impto, valor_imp, sum(importe) AS importe "
                + "FROM ( "
                    + "SELECT  "
                        + "(CASE WHEN gral_imptos_id=4 THEN 2 ELSE gral_imptos_id END) as id_impto, "
                        + "valor_imp, "
                        + "((cantidad * precio_unitario) * valor_imp) AS importe "
                    + "FROM fac_docs_detalles  "
                    + "WHERE fac_doc_id="+id_factura+" "
                + ") AS sbt "
                + "GROUP BY id_impto, valor_imp;";
        
        ArrayList<LinkedHashMap<String, String>> tras = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("impuesto","IVA");
                    row.put("importe",StringHelper.roundDouble(rs.getString("importe"),2));
                    row.put("tasa", StringHelper.roundDouble(rs.getString("valor_imp"),2) );
                    return row;
                }
            }
        );
        return tras;
    }
    */
    
    
    
    
    @Override
    public ArrayList<LinkedHashMap<String, String>> getImpuestosRetenidosCfdi(Integer id_factura) {
        String sql_to_query = "SELECT monto_retencion, tasa_retencion_immex as tasa FROM fac_docs WHERE id="+id_factura+"  AND monto_retencion >0 AND monto_retencion IS NOT NULL;";
        
        ArrayList<LinkedHashMap<String, String>> ret = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("impuesto","IVA");
                    row.put("importe",StringHelper.roundDouble(rs.getString("monto_retencion"),2));
                    row.put("tasa", StringHelper.roundDouble(rs.getString("tasa"),2));
                    return row;
                }
            }
        );
        return ret;
    }
    
    
    
    
    @Override
    public ArrayList<String> getLeyendasEspecialesCfdi(Integer id_empresa) {
        final ArrayList<String> retorno = new ArrayList<String>();
        String sql_to_query = "SELECT leyenda FROM gral_emp_leyenda WHERE gral_emp_id="+id_empresa+";";
        //System.out.println("getLeyendasEspecialesCfdi:"+sql_to_query);
        
        ArrayList<HashMap<String, String>> ret = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("leyenda",rs.getString("leyenda"));
                    retorno.add(rs.getString("leyenda"));
                    return row;
                }
            }
        );
        return retorno;
    }
    
    
    //***************************************************************************************************************************
    //Comienza métodos específicos para Facturación CFDI TIMBRE FISCAL
    //***************************************************************************************************************************
    //obtiene la lista de conceptos para la factura
    @Override
    public ArrayList<LinkedHashMap<String, String>> getListaConceptosXmlCfdiTf(Integer id_prefactura) {
        String sql_query = ""
                + "SELECT "
                        + "inv_prod.sku,"
                        + "inv_prod.descripcion,"
                        + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) AS unidad,"
                        + "erp_prefacturas_detalles.cant_facturar AS cantidad,"
                        + "erp_prefacturas_detalles.precio_unitario,"
                        + "(erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) AS importe, "
                        + "(erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) * erp_prefacturas_detalles.valor_imp AS importe_impuesto, "
                        + "erp_prefacturas_detalles.tipo_impuesto_id AS id_impto,"
                        + "(erp_prefacturas_detalles.valor_imp * 100::double precision) AS tasa_impuesto,"
                        + "erp_prefacturas_detalles.valor_imp,"
                        + "erp_prefacturas.tasa_retencion_immex "
                + "FROM erp_prefacturas "
                + "JOIN erp_prefacturas_detalles on erp_prefacturas_detalles.prefacturas_id=erp_prefacturas.id "
                + "JOIN gral_mon on gral_mon.id=erp_prefacturas.moneda_id "
                + "LEFT JOIN inv_prod on inv_prod.id = erp_prefacturas_detalles.producto_id "
                + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = erp_prefacturas_detalles.inv_prod_unidad_id "
                + "WHERE erp_prefacturas_detalles.prefacturas_id="+id_prefactura+";";
        
        //System.out.println(sql_query);
        //System.out.println("getListaConceptosXmlCfdiTimbreFiscal: "+sql_query);
        
        //System.out.println("noIdentificacion "+" | descripcion      "+" | cant"+" | precio_uni"+" | importe"+" | importe_imp"+" | valor_imp"+" | tasa_ret"  );
        
        ArrayList<LinkedHashMap<String, String>> hm_conceptos = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("noIdentificacion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("sku"))));
                    row.put("descripcion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("descripcion"))));
                    row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("unidad"))));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("valorUnitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),4) );
                    row.put("importe_impuesto",StringHelper.roundDouble(rs.getDouble("importe_impuesto"),4) );
                    row.put("numero_aduana","");
                    row.put("fecha_aduana","");
                    row.put("aduana_aduana","");
                    row.put("id_impto",String.valueOf(rs.getInt("id_impto")));
                    row.put("tasa_impuesto",StringHelper.roundDouble(rs.getDouble("tasa_impuesto"),2) );
                    row.put("valor_imp",StringHelper.roundDouble(rs.getDouble("valor_imp"),2) );
                    row.put("tasa_retencion",StringHelper.roundDouble(rs.getDouble("tasa_retencion_immex"),2) );
                    
                    //System.out.println(row.get("noIdentificacion")+"   "+row.get("descripcion")+"   "+row.get("cantidad")+"   "+row.get("valorUnitario")+"   "+row.get("importe")+"   "+row.get("importe_impuesto")+"   "+row.get("valor_imp")+"   "+row.get("tasa_retencion"));
                    
                    return row;
                }
            }
        );
        
        try {
            calcula_Totales_e_Impuestos(hm_conceptos);
        } catch (SQLException ex) {
            Logger.getLogger(FacturasSpringDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return hm_conceptos;
    }
    
    
    
    //Termina métodos específicos para Facturación CFDI TIMBRE FISCAL
    //***************************************************************************************************************************

    
    
    
    
    /*
    //ejecuta procesos relacionados a facturacion
    @Override
    public Boolean update_fac_docs_salidas(String serie_folio, String nombre_archivo){
        String sql_to_query = "UPDATE fac_docs SET salida=TRUE, nombre_archivo='"+nombre_archivo+".xml' WHERE serie_folio ='"+serie_folio+"' returning salida;";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        Boolean valor_retorno= Boolean.parseBoolean(update.get("salida").toString());
        return valor_retorno;
    }
    */
    
    
    
    
    
    
    
    //obtiene la lista de conceptos  de la factura para el pdfCfd
    @Override
    public ArrayList<HashMap<String, String>> getListaConceptosPdfCfd(String serieFolio) {
        
        String sql_query = ""
        + "SELECT "
            + "fac_docs_detalles.id as id_detalle,"
            + "fac_docs_detalles.inv_prod_id AS producto_id,"
            + "inv_prod.sku,"
            + "inv_prod.descripcion as titulo,"
            + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) AS unidad,"
            + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS decimales,"
            + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) AS id_presentacion,"
            + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion,"
            + "fac_docs_detalles.cantidad,"
            + "fac_docs_detalles.precio_unitario,"
            + "(fac_docs_detalles.cantidad * fac_docs_detalles.precio_unitario) AS importe, "
            + "gral_mon.descripcion as moneda,"
            + "gral_mon.simbolo AS simbolo_moneda "
        + "FROM fac_docs "
        + "JOIN fac_docs_detalles on fac_docs_detalles.fac_doc_id=fac_docs.id "
        + "LEFT JOIN gral_mon on gral_mon.id = fac_docs.moneda_id "
        + "LEFT JOIN inv_prod on inv_prod.id = fac_docs_detalles.inv_prod_id "
        + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = fac_docs_detalles.inv_prod_unidad_id "
        + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = fac_docs_detalles.inv_prod_presentacion_id "
        + "WHERE fac_docs.serie_folio='"+serieFolio+"';";
        
        //System.out.println("Obtiene lista conceptos pdf: "+sql_query);
        ArrayList<HashMap<String, String>> hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_detalle",rs.getInt("id_detalle"));
                    row.put("producto_id",rs.getString("producto_id"));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("numero_lote","");
                    
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",rs.getString("id_presentacion"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"),2 ));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("moneda",rs.getString("moneda"));
                    row.put("simbolo_moneda",rs.getString("simbolo_moneda"));
                    row.put("denominacion","");
                    
                    /*
                    System.out.println(rs.getString("moneda")+"  "
                            + ""+rs.getString("sku")+"  "
                            + "    "+StringHelper.roundDouble(rs.getDouble("precio_unitario"),4)
                            + "    "+StringHelper.roundDouble(rs.getDouble("importe"),2)
                            
                            );
                    */
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
    
    
    
    @Override
    public HashMap<String, String> getDatosExtrasPdfCfd(String serieFolio, String proposito, String cadena_original, String sello_digital, Integer id_sucursal) {
        HashMap<String, String> extras = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> valorIva = new ArrayList<HashMap<String, Object>>();
        
        valorIva= getValoriva(id_sucursal);//obtiene el valor del iva
        
        //obtener datos del vendedor y terminos de pago
        String sql_to_query = ""
                + "SELECT fac_docs.subtotal, "
                    + "fac_docs.impuesto, "
                    + "fac_docs.monto_retencion, "
                    + "fac_docs.total, "
                    + "(CASE WHEN fac_docs.fecha_vencimiento IS NULL THEN '' ELSE to_char(fac_docs.fecha_vencimiento,'dd/mm/yyyy') END) AS fecha_vencimiento, "
                    + "fac_docs.orden_compra, "
                    + "fac_docs.orden_compra AS folio_pedido, "
                    + "fac_docs.observaciones, "
                    + "cxc_agen.nombre AS nombre_vendedor, "
                    + "cxc_clie_credias.descripcion AS terminos,"
                    + "cxc_clie_credias.dias, "
                    + "gral_mon.descripcion AS nombre_moneda,"
                    + "gral_mon.descripcion_abr AS moneda_abr "
                + "FROM fac_docs  "
                + "LEFT JOIN cxc_clie_credias ON cxc_clie_credias.id = fac_docs.terminos_id "
                + "LEFT JOIN gral_mon on gral_mon.id = fac_docs.moneda_id "
                + "JOIN cxc_agen ON cxc_agen.id =  fac_docs.cxc_agen_id  "
                + "WHERE fac_docs.serie_folio='"+serieFolio+"';";
        
        Map<String, Object> mapVendedorCondiciones = this.getJdbcTemplate().queryForMap(sql_to_query);
        extras.put("subtotal", StringHelper.roundDouble(mapVendedorCondiciones.get("subtotal").toString(),2));
        extras.put("impuesto", StringHelper.roundDouble(mapVendedorCondiciones.get("impuesto").toString(),2));
        extras.put("monto_retencion", StringHelper.roundDouble(mapVendedorCondiciones.get("monto_retencion").toString(),2));
        extras.put("total", StringHelper.roundDouble(mapVendedorCondiciones.get("total").toString(),2));
        extras.put("nombre_moneda", mapVendedorCondiciones.get("nombre_moneda").toString());
        extras.put("moneda_abr", mapVendedorCondiciones.get("moneda_abr").toString());
        
        
        extras.put("nombre_vendedor", mapVendedorCondiciones.get("nombre_vendedor").toString());
        extras.put("terminos", mapVendedorCondiciones.get("terminos").toString());
        extras.put("dias", mapVendedorCondiciones.get("dias").toString());
        extras.put("orden_compra", mapVendedorCondiciones.get("orden_compra").toString() );
        extras.put("folio_pedido", mapVendedorCondiciones.get("folio_pedido").toString() );
        extras.put("observaciones", mapVendedorCondiciones.get("observaciones").toString() );
        extras.put("fecha_vencimiento", mapVendedorCondiciones.get("fecha_vencimiento").toString() );
        
        extras.put("proposito", proposito);
        extras.put("cadena_original", cadena_original);
        extras.put("sello_digital", sello_digital);
        extras.put("serieFolio", serieFolio);
        extras.put("valor_iva", StringHelper.roundDouble(valorIva.get(0).get("valor_impuesto").toString(),2));
        extras.put("fecha_comprobante", getFechaComprobante());
        
        return extras;
    }
    
    
    
    
    //Obtener el tipo de Facturacion
    @Override
    public String getTipoFacturacion(Integer idEmp) {
        String valor_retorno="";
        String sql_query = "SELECT tipo_facturacion FROM gral_emp WHERE id="+idEmp+";";
        
        //System.out.println("Obtiene tipo de facturacion: "+ sql_query);
        Map<String, Object> tipo = this.getJdbcTemplate().queryForMap(sql_query);
        valor_retorno= tipo.get("tipo_facturacion").toString();
        
        return valor_retorno;
    }
    
    
    //Obtener el numero del PAC para el Timbrado de la Factura
    @Override
    public String getNoPacFacturacion(Integer idEmp) {
        String valor_retorno="";
        String sql_query = "SELECT pac_facturacion FROM gral_emp WHERE id="+idEmp+";";
        
        //System.out.println("Obtiene tipo de facturacion: "+ sql_query);
        Map<String, Object> tipo = this.getJdbcTemplate().queryForMap(sql_query);
        valor_retorno= tipo.get("pac_facturacion").toString();
        
        return valor_retorno;
    }
    
    
    //Ambiente de Facturacion PRUEBAS ó PRODUCCION, solo aplica para Facturacion por Timbre FIscal(cfditf)
    @Override
    public String getAmbienteFacturacion(Integer idEmp) {
        String valor_retorno="";
        String sql_query = "SELECT (CASE WHEN ambiente_facturacion=true THEN 'produccion' ELSE 'prueba' END) AS ambiente_facturacion FROM gral_emp WHERE id="+idEmp+";";
        
        //System.out.println("Obtiene tipo de facturacion: "+ sql_query);
        Map<String, Object> tipo = this.getJdbcTemplate().queryForMap(sql_query);
        valor_retorno= tipo.get("ambiente_facturacion").toString();
        
        return valor_retorno;
    }
    
    
    
    
    
    
    
    /*
    //verificar si ya se generó el xml y pdf de la factura en el buzon fiscal
    @Override
    public String verifica_fac_docs_salidas(Integer id_factura){
        String sql_to_query = "SELECT salida FROM fac_docs WHERE id ="+id_factura;
        Map<String, Object> salida = this.getJdbcTemplate().queryForMap(sql_to_query);
        String valor_retorno= salida.get("salida").toString();
        return valor_retorno;
    }
    */
    
    
    
    @Override
    public String getSerieFolioFactura(Integer id_factura, Integer idEmp) {
        String sql_to_query="";
        
        //obtener tipo de facturacion
        String tipo_facturacion = getTipoFacturacion(idEmp);
        if(tipo_facturacion.equals("cfd")){
            //para facturacion tipo CFD
            sql_to_query = "SELECT split_part(fac_cfds.nombre_archivo, '.', 1) AS nombre_archivo FROM fac_docs  JOIN erp_proceso ON erp_proceso.id=fac_docs.proceso_id JOIN  fac_cfds ON fac_cfds.proceso_id= erp_proceso.id WHERE fac_docs.id="+id_factura+" ORDER BY fac_cfds.id DESC LIMIT 1;";
        }
        
        if(tipo_facturacion.equals("cfdi")){
            //para facturacion tipo CFDI Buzon Fiscal
            sql_to_query = "SELECT serie_folio AS nombre_archivo FROM fac_docs WHERE id ="+id_factura+";";
        }
        
        if(tipo_facturacion.equals("cfditf")){
            //para facturacion tipo CFDI Buzon Fiscal
            sql_to_query = "SELECT serie_folio AS nombre_archivo FROM fac_docs WHERE id ="+id_factura+";";
        }

        
        //System.out.println(sql_to_query);
        Map<String, Object> map_iva = this.getJdbcTemplate().queryForMap(sql_to_query);
        String serie_folio = map_iva.get("nombre_archivo").toString();
        return serie_folio;
    }
    
    
    
    
    @Override
    public String getSerieFolioFacturaByIdPrefactura(Integer id_prefactura, Integer idEmp) {
        String sql_to_query="";
        
        //obtener tipo de facturacion
        String tipo_facturacion = getTipoFacturacion(idEmp);
        
        if(tipo_facturacion.equals("cfd")){
            //para facturacion tipo CFD
            sql_to_query = "SELECT split_part(fac_cfds.nombre_archivo, '.', 1) AS serie_folio FROM erp_prefacturas  JOIN erp_proceso ON erp_proceso.id=erp_prefacturas.proceso_id JOIN  fac_cfds ON fac_cfds.proceso_id= erp_proceso.id WHERE erp_prefacturas.id="+id_prefactura+" ORDER BY fac_cfds.id DESC LIMIT 1;";
        }
        
        if(tipo_facturacion.equals("cfdi")){
            //para facturacion tipo CFDI Timbre Fiscal
            sql_to_query = "SELECT fac_docs.serie_folio FROM erp_prefacturas  JOIN  fac_docs ON fac_docs.proceso_id=erp_prefacturas.proceso_id WHERE erp_prefacturas.id="+id_prefactura+" AND fac_docs.cancelado=false ORDER BY fac_docs.id DESC LIMIT 1;";
        }
        
        if(tipo_facturacion.equals("cfditf")){
            //para facturacion tipo CFDI Timbre Fiscal
            sql_to_query = "SELECT fac_docs.serie_folio FROM erp_prefacturas  JOIN  fac_docs ON fac_docs.proceso_id=erp_prefacturas.proceso_id WHERE erp_prefacturas.id="+id_prefactura+" AND fac_docs.cancelado=false ORDER BY fac_docs.id DESC LIMIT 1;";
        }
        
        //System.out.println("GetSerieFolio:"+sql_to_query);
        Map<String, Object> map_iva = this.getJdbcTemplate().queryForMap(sql_to_query);
        String serie_folio = map_iva.get("serie_folio").toString();
        return serie_folio;
    }
    
    
    
    @Override
    public Integer getIdPrefacturaByIdFactura(Integer id_factura) {
        String sql_to_query="";
        
        sql_to_query = "SELECT erp_prefacturas.id AS id_prefactura FROM fac_docs JOIN erp_prefacturas ON erp_prefacturas.proceso_id=fac_docs.proceso_id WHERE fac_docs.id="+id_factura+" LIMIT 1;";
        
        //System.out.println(sql_to_query);
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        Integer id_prefactura = Integer.parseInt(map.get("id_prefactura").toString());
        return id_prefactura;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getTiposCancelacion() {
        String sql_to_query = "SELECT id, titulo FROM fac_docs_tipos_cancelacion WHERE borrado_logico=false ORDER BY id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm;
    }
    
    
    
    
    
    //forma una cadena con todos los conceptos de la factura
    @Override
    public String formar_cadena_conceptos(ArrayList<LinkedHashMap<String,String>> concepts){
            
        String valor_retorno = new String();
        
        if(concepts.size() > 0){
            for (HashMap<String,String> concept : concepts ){
                String precio_unitario = String.valueOf(concept.get("valorUnitario"));
                String importe = String.valueOf(concept.get("importe") );
                String concepto = concept.get("noIdentificacion") + "+&+" + concept.get("cantidad") + "+&+" + concept.get("descripcion").replace("'", "\"") + "+&+" + precio_unitario + "+&+" + concept.get("unidad") + "+&+" + importe;
                valor_retorno += concepto + "$$$";
            }
            return valor_retorno.substring(0, valor_retorno.length() - 3);
        }
        return " ";
    }
    
    
    
    @Override
    public String formar_cadena_traslados(String cantidad_lana_iva,String tasa_iva){
        String valor_retorno = new String();
        double tasa = Double.parseDouble(tasa_iva);
        valor_retorno = cantidad_lana_iva+"+&+IVA+&+"+String.valueOf(tasa);
        return valor_retorno;
    }
    
    
    @Override
    public String formar_cadena_retenidos(String cantidad_lana_iva,String tasa_iva){
        String valor_retorno = new String();
        double tasa = Double.parseDouble(tasa_iva);
        valor_retorno = cantidad_lana_iva+"+&+IVA+&+"+String.valueOf(tasa);
        return valor_retorno;
    }
    
    
    
    
    
    //guarda los datos en la tabla fac_cfds
    @Override
    public void fnSalvaDatosFacturas(String rfc_receptor,
                        String serie_factura,
                        String folio_factura,
                        String no_probacion,
                        String total,
                        String tot_imp_trasladados,
                        String edo_comprobante,
                        String xml_file_name,
                        String fecha,
                        String razon_social_receptor,
                        String tipo_comprobante,
                        String proposito,
                        String ano_probacion ,
                        String cadena_conceptos,
                        String cadena_imp_trasladados,
                        String cadena_imp_retenidos,
                        Integer prefactura_id,
                        Integer id_usuario,
                        Integer id_moneda,
                        String tipo_cambio,
                        String refacturar,
                        String regimen_fiscal,
                        String metodo_pago,
                        String num_cuenta,
                        String lugar_de_expedicion
                       ) {
        
        String sql_insert = "select erp_fn_salva_datos_factura from erp_fn_salva_datos_factura('"+ rfc_receptor +"','" + 
                                serie_factura +"','" + 
                                folio_factura +"','" + 
                                no_probacion +"'," + 
                                total +"," + 
                                tot_imp_trasladados +",'" + 
                                edo_comprobante +"','" + 
                                xml_file_name +"','" + 
                                fecha +"','" + 
                                razon_social_receptor +"','" + 
                                tipo_comprobante +"','" + 
                                proposito +"','" + 
                                ano_probacion +"','" + 
                                cadena_conceptos +"','" + 
                                cadena_imp_trasladados +"','" + 
                                cadena_imp_retenidos +"'," + 
                                prefactura_id +"," + 
                                id_usuario +"," + 
                                id_moneda +"," + 
                                tipo_cambio+",'"+
                                refacturar+"','"+
                                regimen_fiscal+"','"+
                                metodo_pago+"','"+
                                num_cuenta+"','"+
                                lugar_de_expedicion +"')";
        
        //System.out.println("Iniciando:"+sql_insert);
        System.out.println("Iniciando salvar datos factura");
        Map<String, Object> map_salva = this.getJdbcTemplate().queryForMap(sql_insert);
        
        String salvado = map_salva.get("erp_fn_salva_datos_factura").toString();
        if(salvado.equals("true")){
            //System.out.println("Datos salvados en fac_cfds: "+sql_insert);
        }else{
            //System.out.println("Datos NO salvados en fac_cfds: "+sql_insert);
        }
    }
    
    
    
    
    
    
    //obtiene lista de facturas de un periodo
    @Override
    public ArrayList<HashMap<String, String>> getDatosReporteFacturacion(Integer opcion, String factura, String cliente, String fecha_inicial, String fecha_final, Integer id_empresa) {
        
        String where="";
        
        //opcion = 1, Ventas Totales(Ventas a filiales y  y No filiales)
        //ventas a filiales
        if (opcion==2){
            where=" AND cxc_clie.filial=TRUE";
        }
        
        //ventas Netas(vantas a No Filiales)
        if (opcion==3){
            where=" AND cxc_clie.filial=FALSE";
        }
        
        String sql_to_query = ""
                + "SELECT "
                            + "fac_docs.id, "
                            + "fac_docs.serie_folio, "
                            + "fac_docs.orden_compra, "
                            + "to_char(fac_docs.momento_creacion,'dd/mm/yyyy') as fecha_factura,"
                            + "(CASE WHEN fac_docs.cancelado=FALSE THEN cxc_clie.razon_social ELSE 'CANCELADA' END) AS cliente, "
                            + "gral_mon.id AS id_moneda, "
                            + "gral_mon.descripcion_abr AS moneda_factura, "
                            + "gral_mon.simbolo AS simbolo_moneda, "
                            + "(CASE WHEN fac_docs.cancelado=FALSE THEN fac_docs.subtotal ELSE 0.0 END) AS subtotal, "
                            + "(CASE WHEN fac_docs.cancelado=FALSE THEN fac_docs.subtotal*tipo_cambio ELSE 0.0 END) AS subtotal_mn, "
                            + "(CASE WHEN fac_docs.cancelado=FALSE THEN fac_docs.impuesto ELSE 0.0 END) AS impuesto, "
                            + "(CASE WHEN fac_docs.cancelado=FALSE THEN fac_docs.impuesto*tipo_cambio ELSE 0.0 END) AS impuesto_mn, "
                            + "(CASE WHEN fac_docs.cancelado=FALSE THEN fac_docs.total ELSE 0.0 END) AS total, "
                            + "(CASE WHEN fac_docs.cancelado=FALSE THEN fac_docs.total*tipo_cambio ELSE 0.0 END) AS total_mn,  "
                            + "(CASE WHEN fac_docs.moneda_id=1 THEN 1 ELSE fac_docs.tipo_cambio END) AS tipo_cambio  "
                    + "FROM fac_docs   "
                    + "JOIN erp_proceso ON erp_proceso.id=fac_docs.proceso_id  "
                    + "JOIN cxc_clie ON cxc_clie.id = fac_docs.cxc_clie_id   "
                    + "JOIN gral_mon ON gral_mon.id = fac_docs.moneda_id  "
                    + "WHERE erp_proceso.empresa_id ="+id_empresa + "  "
                    + "AND fac_docs.serie_folio ILIKE '"+factura+"' "
                    + "AND cxc_clie.razon_social ILIKE '"+cliente+"'  "+where+" "
                    + "AND (to_char(fac_docs.momento_creacion,'yyyymmdd') BETWEEN  to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd') AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')) "
                    + "ORDER BY fac_docs.id;";
            
        //System.out.println("ReporteFacturacion:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("id_moneda",rs.getString("id_moneda"));
                    row.put("moneda_factura",rs.getString("moneda_factura"));
                    row.put("simbolo_moneda",rs.getString("simbolo_moneda"));
                    row.put("subtotal",StringHelper.roundDouble(rs.getDouble("subtotal"), 2));
                    row.put("subtotal_mn",StringHelper.roundDouble(rs.getDouble("subtotal_mn"), 2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getDouble("impuesto"), 2));
                    row.put("impuesto_mn",StringHelper.roundDouble(rs.getDouble("impuesto_mn"), 2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"), 2));
                    row.put("total_mn",StringHelper.roundDouble(rs.getDouble("total_mn"), 2));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
    
    
    
    
    
    
    //obtiene datos para el Reporte de Remisiones
    //variable estatus agregado por paco
    @Override
    public ArrayList<HashMap<String, String>> getDatosReporteRemision(Integer opcion, String remision, String cliente, String fecha_inicial, String fecha_final, Integer id_empresa, Integer estatus) {
        
        String where="";
        //opcion = 1, Ventas Totales(Ventas a filiales y  y No filiales)
        //ventas a filiales
        if (opcion==2){
            where=" AND cxc_clie.filial=TRUE ";
        }
        
        //ventas Netas(vantas a No Filiales)
        if (opcion==3){
            where=" AND cxc_clie.filial=FALSE ";
        }
        
        if (estatus==0 || estatus==1){
            where+=" AND fac_rems.estatus="+estatus+" AND fac_rems.facturado=FALSE AND fac_rems.cancelado=FALSE ";
        }
        
        if (estatus==2){
            where+=" AND fac_rems.facturado=TRUE AND fac_rems.cancelado=FALSE";
        }

        if (estatus==3){
            where+=" AND fac_rems.cancelado=TRUE";
        }
        
        String sql_to_query = ""
                + "SELECT fac_rems.folio, "
                            + "fac_rems.orden_compra, "
                            + "to_char(fac_rems.momento_creacion,'dd/mm/yyyy') as fecha_remision,"
                            + "(CASE WHEN fac_rems.cancelado=FALSE THEN cxc_clie.razon_social ELSE 'CANCELADA' END) AS cliente, "
                            + "fac_rems.moneda_id, "
                            + "gral_mon.simbolo AS moneda_simbolo, "
                            + "gral_mon.descripcion_abr AS moneda_remision, "
                            + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.subtotal ELSE 0.0 END) AS subtotal, "
                            + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.subtotal*tipo_cambio ELSE 0.0 END) AS subtotal_mn, "
                            + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.impuesto ELSE 0.0 END) AS impuesto, "
                            + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.impuesto*tipo_cambio ELSE 0.0 END) AS impuesto_mn, "
                            + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.total ELSE 0.0 END) AS total, "
                            + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.total*tipo_cambio ELSE 0.0 END) AS total_mn,  "
                            + "(CASE WHEN fac_rems.moneda_id=1 THEN 1 ELSE fac_rems.tipo_cambio END) AS tipo_cambio  "
                    + "FROM fac_rems   "
                    + "JOIN erp_proceso ON erp_proceso.id=fac_rems.proceso_id  "
                    + "JOIN cxc_clie ON cxc_clie.id = fac_rems.cxc_clie_id   "
                    + "JOIN gral_mon ON gral_mon.id = fac_rems.moneda_id  "
                    + "WHERE erp_proceso.empresa_id ="+id_empresa + "  "
                    + "AND fac_rems.folio ILIKE '"+remision+"' " 
                    + "AND cxc_clie.razon_social ILIKE '"+cliente+"'  "+where+" "
                    + "AND (to_char(fac_rems.momento_creacion,'yyyymmdd') BETWEEN  to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd') AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')) "
                    + "ORDER BY fac_rems.id;";
            
        //System.out.println("getDatosReporteRemision:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_remisiones = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("remision",rs.getString("folio"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("fecha_remision",rs.getString("fecha_remision"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("moneda_simbolo",rs.getString("moneda_simbolo"));
                    row.put("moneda_remision",rs.getString("moneda_remision"));
                    row.put("subtotal",StringHelper.roundDouble(rs.getDouble("subtotal"), 2));
                    row.put("subtotal_mn",StringHelper.roundDouble(rs.getDouble("subtotal_mn"), 2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getDouble("impuesto"), 2));
                    row.put("impuesto_mn",StringHelper.roundDouble(rs.getDouble("impuesto_mn"), 2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"), 2));
                    row.put("total_mn",StringHelper.roundDouble(rs.getDouble("total_mn"), 2));
                    return row;
                }
            }
        );
        return hm_remisiones;
    }
    
    
    
    
    //obtiene datos para el Reporte de Remisiones faturadas
    @Override
    public ArrayList<HashMap<String, String>> getDatosReporteRemision_facturada(Integer opcion, String remision, String cliente, String fecha_inicial, String fecha_final, Integer id_empresa) {
        
        String where="";
        
        //remision facturada
        if (opcion==1){
            where=" AND fac_rems.facturado=TRUE";
        }
        
        //remision no facturada
        if (opcion==2){
            where=" AND fac_rems.facturado=FALSE";
        }
        
        
        String sql_to_query = ""
                + "SELECT  "
                    + "(CASE WHEN tbl_fac.serie_folio IS NULL THEN '' ELSE tbl_fac.serie_folio END ) AS factura,  "
                    + "fac_rems.folio as remision,  "
                    + "to_char (fac_rems.momento_creacion,'dd/mm/yyyy' ) as fecha_remision_facturada,  "
                    + "cxc_clie.razon_social as cliente, (CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.subtotal ELSE 0.0 END) AS monto, "
                    + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.subtotal*fac_rems.tipo_cambio ELSE 0.0 END) AS monto_mn, "
                    + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.impuesto ELSE 0.0 END) AS iva, "
                    + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.impuesto*fac_rems.tipo_cambio ELSE 0.0 END) AS impuesto_mn, "
                    + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.total ELSE 0.0 END) AS total, "
                    + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.total*fac_rems.tipo_cambio ELSE 0.0 END) AS total_mn,   "
                    + "(CASE WHEN fac_rems.moneda_id=1 THEN 'M.N.' else 'USD' end) as moneda_remision_facturada, "
                    + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.impuesto ELSE 0.0 END) AS impuesto,  "
                    + "fac_rems.moneda_id, "
                    + "gral_mon.descripcion_abr AS moneda_abr, "
                    + "gral_mon.simbolo AS moneda_simbolo "
                + "FROM fac_rems   "
                + "LEFT JOIN erp_proceso ON erp_proceso.id=fac_rems.proceso_id   "
                + "LEFT JOIN cxc_clie ON cxc_clie.id=fac_rems.cxc_clie_id   "
                + "LEFT JOIN ( SELECT fac_rems_docs.fac_rem_id,fac_docs.serie_folio  FROM fac_docs LEFT JOIN fac_rems_docs  ON fac_rems_docs.erp_proceso_id=fac_docs.proceso_id LEFT JOIN erp_proceso ON erp_proceso.id=fac_rems_docs.erp_proceso_id WHERE erp_proceso.empresa_id="+id_empresa+" ) AS tbl_fac ON tbl_fac.fac_rem_id=fac_rems.id  "
                + "LEFT JOIN gral_mon ON gral_mon.id=fac_rems.moneda_id "
                +" WHERE erp_proceso.empresa_id ="+id_empresa+"  "
                + "AND fac_rems.folio ILIKE '"+remision+"'  "
                + "AND cxc_clie.razon_social ILIKE '"+cliente+"' "+where+" "
                + "AND (to_char(fac_rems.momento_creacion,'yyyymmdd')::integer BETWEEN to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::integer  AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::integer ) "
                + "ORDER BY fac_rems.id; ";
        
        //System.out.println("getDatosReporteRemision_facturada:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_remisiones = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("factura",rs.getString("factura"));
                    row.put("remision",rs.getString("remision"));
                    row.put("fecha_remision_facturada",rs.getString("fecha_remision_facturada"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("moneda_remision_facturada",rs.getString("moneda_remision_facturada"));
                    row.put("monto",StringHelper.roundDouble(rs.getDouble("monto"), 2));
                    row.put("monto_mn",StringHelper.roundDouble(rs.getDouble("monto_mn"), 2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getDouble("iva"), 2));
                    row.put("impuesto_mn",StringHelper.roundDouble(rs.getDouble("impuesto_mn"), 2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"), 2));
                    row.put("total_mn",StringHelper.roundDouble(rs.getDouble("total_mn"), 2));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("moneda_abr",rs.getString("moneda_abr"));
                    row.put("moneda_simbolo",rs.getString("moneda_simbolo"));
                    return row;
                }
            }
        );
        return hm_remisiones;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getNotasCredito_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
                + "SELECT DISTINCT "
                    + "fac_nota_credito.id, "
                    + "fac_nota_credito.serie_folio,"
                    + "cxc_clie.razon_social as cliente,"
                    + "fac_nota_credito.total, "
                    + "to_char(fac_nota_credito.momento_creacion,'dd/mm/yyyy') AS fecha_expedicion,"
                    + "gral_mon.descripcion_abr AS moneda, "
                    + "fac_nota_credito.serie_folio_factura AS factura, "
                    + "(CASE WHEN fac_nota_credito.cancelado=FALSE THEN '' ELSE 'CANCELADO' END) AS estado "
                + "FROM fac_nota_credito "
                + "LEFT JOIN cxc_clie on cxc_clie.id = fac_nota_credito.cxc_clie_id  "
                + "LEFT JOIN gral_mon ON gral_mon.id=fac_nota_credito.moneda_id  "
                +"JOIN ("+sql_busqueda+") as subt on subt.id=fac_nota_credito.id "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        //System.out.println("data_string: "+data_string);
        //System.out.println("cliente: "+cliente+ "fecha_inicial:"+fecha_inicial+" fecha_final: "+fecha_final+ " offset:"+offset+ " pageSize: "+pageSize+" orderBy:"+orderBy+" asc:"+asc);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("serie_folio"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("total"),2)));
                    row.put("fecha_expedicion",rs.getString("fecha_expedicion"));
                    row.put("factura",rs.getString("factura"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("estado",rs.getString("estado"));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //obtiene los datos de la Nota de Credito
    @Override
    public ArrayList<HashMap<String, Object>> getNotasCredito_Datos(Integer id_nota_credito) {
	String sql_query = ""
                + "SELECT "
                    + "fac_nota_credito.id,"
                    + "fac_nota_credito.serie_folio,"
                    + "fac_nota_credito.cxc_clie_id,"
                    + "cxc_clie.numero_control AS no_cliente,"
                    + "cxc_clie.razon_social,"
                    + "fac_nota_credito.cxc_clie_df_id,"
                    + "cxc_clie.empresa_immex,"
                    + "fac_nota_credito.cxc_agen_id,"
                    + "fac_nota_credito.moneda_id,"
                    + "fac_nota_credito.valor_impuesto,"
                    + "fac_nota_credito.tasa_retencion_immex,"
                    + "fac_nota_credito.tipo_cambio,"
                    + "fac_nota_credito.subtotal AS importe,"
                    + "fac_nota_credito.concepto,"
                    + "fac_nota_credito.observaciones,"
                    + "fac_nota_credito.serie_folio_factura as factura,"
                    + "erp_h_facturas.moneda_id as id_moneda_factura, "
                    + "fac_nota_credito.cancelado,"
                    + "erp_h_facturas.monto_total AS monto_factura, "
                    + "erp_h_facturas.saldo_factura, "
                    + "to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') AS fecha_factura "
                + "FROM fac_nota_credito "
                + "JOIN cxc_clie ON cxc_clie.id = fac_nota_credito.cxc_clie_id "
                + "JOIN erp_h_facturas ON erp_h_facturas.serie_folio = fac_nota_credito.serie_folio_factura "
                + "WHERE fac_nota_credito.id="+id_nota_credito;
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("cxc_clie_id",String.valueOf(rs.getInt("cxc_clie_id")));
                    row.put("cxc_agen_id",String.valueOf(rs.getInt("cxc_agen_id")));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("no_cliente",rs.getString("no_cliente"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("df_id",String.valueOf(rs.getInt("cxc_clie_df_id")));
                    row.put("concepto",rs.getString("concepto"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("cancelado",String.valueOf(rs.getBoolean("cancelado")));
                    row.put("empresa_immex",String.valueOf(rs.getBoolean("empresa_immex")));
                    row.put("valor_impuesto",StringHelper.roundDouble(rs.getDouble("valor_impuesto"), 2));
                    row.put("tasa_retencion_immex",StringHelper.roundDouble(rs.getDouble("tasa_retencion_immex"), 2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"), 4));
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"), 2));
                    
                    row.put("factura",rs.getString("factura"));
                    row.put("id_moneda_factura",String.valueOf(rs.getInt("id_moneda_factura")));
                    row.put("monto_factura",StringHelper.roundDouble(rs.getDouble("monto_factura"), 2));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getDouble("saldo_factura"), 2));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    //buscador de clientes, se utiliza en notas de credito
    //buscador de clientes de una sucursal
    @Override
    public ArrayList<HashMap<String, String>> getBuscadorClientes(String cadena, Integer filtro,Integer id_empresa, Integer id_sucursal) {
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
                                    +"sbt.id,"
                                    +"sbt.numero_control,"
                                    +"sbt.rfc,"
                                    +"sbt.razon_social,"
                                    +"sbt.direccion,"
                                    +"sbt.moneda_id,"
                                    +"gral_mon.descripcion as moneda, "
                                    +"sbt.cxc_agen_id,"
                                    +"sbt.terminos_id, "
                                    +"sbt.empresa_immex, "
                                    +"sbt.tasa_ret_immex, "
                                    +"sbt.cta_pago_mn, "
                                    +"sbt.cta_pago_usd "
                            +"FROM(SELECT cxc_clie.id,"
                                            +"cxc_clie.numero_control,"
                                            +"cxc_clie.rfc, "
                                            +"cxc_clie.razon_social,"
                                            +"cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion, "
                                            +"cxc_clie.moneda as moneda_id, "
                                            +"cxc_clie.cxc_agen_id, "
                                            +"cxc_clie.dias_credito_id AS terminos_id, "
                                            +"cxc_clie.empresa_immex, "
                                            +"(CASE WHEN cxc_clie.tasa_ret_immex IS NULL THEN 0 ELSE cxc_clie.tasa_ret_immex/100 END) AS tasa_ret_immex, "
                                            + "cxc_clie.cta_pago_mn,"
                                            + "cxc_clie.cta_pago_usd  "
                                    +"FROM cxc_clie "
                                    + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
                                    + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
                                    + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
                                    +" WHERE empresa_id ="+id_empresa+"  AND sucursal_id="+id_sucursal
                                    + " AND cxc_clie.borrado_logico=false  "+where+" "
                            +") AS sbt "
                            +"LEFT JOIN gral_mon on gral_mon.id = sbt.moneda_id;";
        
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
                    
                    return row;
                }
            }
        );
        return hm_cli;
    }
    
    
    //obtener datos del cliente a partir del Numero de Control
    @Override
    public ArrayList<HashMap<String, String>> getDatosClienteByNoCliente(String no_control, Integer id_empresa, Integer id_sucursal) {
        
        String sql_query = ""
                + "SELECT "
                        +"sbt.id,"
                        +"sbt.numero_control,"
                        +"sbt.rfc,"
                        +"sbt.razon_social,"
                        +"sbt.direccion,"
                        +"sbt.moneda_id,"
                        +"gral_mon.descripcion as moneda, "
                        +"sbt.cxc_agen_id,"
                        +"sbt.terminos_id, "
                        +"sbt.empresa_immex, "
                        +"sbt.tasa_ret_immex, "
                        +"sbt.cta_pago_mn, "
                        +"sbt.cta_pago_usd "
                +"FROM("
                    + "SELECT cxc_clie.id,"
                        +"cxc_clie.numero_control,"
                        +"cxc_clie.rfc, "
                        +"cxc_clie.razon_social,"
                        +"cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion, "
                        +"cxc_clie.moneda as moneda_id, "
                        +"cxc_clie.cxc_agen_id, "
                        +"cxc_clie.dias_credito_id AS terminos_id, "
                        +"cxc_clie.empresa_immex, "
                        +"(CASE WHEN cxc_clie.tasa_ret_immex IS NULL THEN 0 ELSE cxc_clie.tasa_ret_immex/100 END) AS tasa_ret_immex, "
                        + "cxc_clie.cta_pago_mn,"
                        + "cxc_clie.cta_pago_usd  "
                    +"FROM cxc_clie "
                    + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
                    + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
                    + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
                    +" WHERE empresa_id ="+id_empresa+"  AND sucursal_id="+id_sucursal+" "
                    + "AND cxc_clie.borrado_logico=false   "
                    + "AND cxc_clie.numero_control='"+no_control.toUpperCase()+"'"
                +") AS sbt "
                +"LEFT JOIN gral_mon ON gral_mon.id=sbt.moneda_id LIMIT 1;";

        //System.out.println("getDatosCliente: "+sql_query);
        
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
                    
                    return row;
                }
            }
        );
        return hm_cli;
    }
    
    
    //obtiene las facturas del cliente con saldo pendiente de pago para Notas de Credito
    @Override
    public ArrayList<HashMap<String, String>> getNotasCredito_FacturasCliente(Integer id_cliente, String serie_folio) {
	String sql_query = ""
                + "SELECT "
                    + "erp_h_facturas.serie_folio AS factura, "
                    + "erp_h_facturas.monto_total AS monto_factura, "
                    + "erp_h_facturas.saldo_factura, "
                    + "erp_h_facturas.moneda_id, "
                    + "gral_mon.descripcion_abr AS moneda, "
                    + "erp_h_facturas.cxc_agen_id,"
                    + "to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') AS fecha_factura "
                + "FROM erp_h_facturas "
                + "JOIN gral_mon ON gral_mon.id=erp_h_facturas.moneda_id "
                + "WHERE erp_h_facturas.cliente_id="+id_cliente+" "
                + "AND erp_h_facturas.pagado=FALSE "
                + "AND erp_h_facturas.cancelacion=FALSE "
                + "AND erp_h_facturas.serie_folio ILIKE '%"+serie_folio.toUpperCase()+"%';";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("factura",rs.getString("factura"));
                    row.put("monto_factura",StringHelper.roundDouble(rs.getDouble("monto_factura"), 2));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getDouble("saldo_factura"), 2));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("cxc_agen_id",String.valueOf(rs.getInt("cxc_agen_id")));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //obtiene datos de una Factura en Especifico a partir del Serie y Folio
    //Dicha factura debe terner saldo pendiente de pago para Notas de Credito
    @Override
    public ArrayList<HashMap<String, String>> getNotasCredito_DatosFactura(Integer id_cliente, String serie_folio) {
	String sql_query = ""
                + "SELECT "
                    + "erp_h_facturas.serie_folio AS factura, "
                    + "erp_h_facturas.monto_total AS monto_factura, "
                    + "erp_h_facturas.saldo_factura, "
                    + "erp_h_facturas.moneda_id, "
                    + "gral_mon.descripcion_abr AS moneda, "
                    + "erp_h_facturas.cxc_agen_id,"
                    + "to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') AS fecha_factura "
                + "FROM erp_h_facturas "
                + "JOIN gral_mon ON gral_mon.id=erp_h_facturas.moneda_id "
                + "WHERE erp_h_facturas.cliente_id="+id_cliente+" "
                + "AND erp_h_facturas.serie_folio='"+serie_folio.toUpperCase()+"' "
                + "AND erp_h_facturas.pagado=FALSE "
                + "AND erp_h_facturas.cancelacion=FALSE LIMIT 1;";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("factura",rs.getString("factura"));
                    row.put("monto_factura",StringHelper.roundDouble(rs.getDouble("monto_factura"), 2));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getDouble("saldo_factura"), 2));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("cxc_agen_id",String.valueOf(rs.getInt("cxc_agen_id")));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //obtiene la lista de conceptos de la nota de credito
    @Override
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfd_ListaConceptosXml(Integer id_nota_credito) {
        String sql_query = ""
                + "SELECT "
                    + "'1'::character varying AS no_identificacion,"
                    + "1::double precision AS cantidad,"
                    + "concepto AS descripcion, "
                    + "'No aplica'::character varying AS unidad, "
                    + "(CASE WHEN moneda_id=1 THEN subtotal ELSE subtotal*tipo_cambio END) AS valor_unitario, "
                    + "(CASE WHEN moneda_id=1 THEN subtotal ELSE subtotal*tipo_cambio END) AS importe, "
                    + "(CASE WHEN moneda_id=1 THEN impuesto ELSE impuesto*tipo_cambio END) AS importe_impuesto, "
                    + "tasa_retencion_immex AS tasa_retencion, "
                    + "''::character varying AS numero_aduana, "
                    + "''::character varying AS fecha_aduana, "
                    + "''::character varying AS aduana_aduana "
                    + "FROM fac_nota_credito "
                + "WHERE id="+id_nota_credito;
                
        //System.out.println(sql_query);
        //System.out.println("Obteniendo lista de conceptos: "+sql_query);
        
        ArrayList<LinkedHashMap<String, String>> hm_conceptos = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("noIdentificacion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("no_identificacion"))));
                    row.put("cantidad",rs.getString("cantidad"));
                    row.put("descripcion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("descripcion"))));
                    row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("unidad"))));
                    row.put("valorUnitario",StringHelper.roundDouble(rs.getDouble("valor_unitario"),2) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("importe_impuesto",StringHelper.roundDouble(rs.getDouble("importe_impuesto"),2) );
                    row.put("tasa_retencion",StringHelper.roundDouble(rs.getDouble("tasa_retencion"),2) );
                    row.put("numero_aduana","");
                    row.put("fecha_aduana","");
                    row.put("aduana_aduana","");
                    return row;
                }
            }
        );
        
        try {
            calcula_Totales_e_Impuestos(hm_conceptos);
        } catch (SQLException ex) {
            Logger.getLogger(FacturasSpringDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return hm_conceptos;
    }
    
    
    
    
    
    
    @Override
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfd_CfdiTf_ImpuestosRetenidosXml() {
        ArrayList<LinkedHashMap<String, String>>  impuestos = new ArrayList<LinkedHashMap<String, String>>();
        LinkedHashMap<String,String> impuesto = new LinkedHashMap<String,String>();
        impuesto.put("impuesto", "IVA");
        impuesto.put("importe", this.getImpuestoRetenido());
        impuesto.put("tasa", this.getTasaRetencion());
        impuestos.add(impuesto);
        
        return impuestos;
    }
    
    
    @Override
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfd_CfdiTf_ImpuestosTrasladadosXml(Integer id_sucursal) {
        ArrayList<LinkedHashMap<String, String>>  impuestos = new ArrayList<LinkedHashMap<String, String>>();
        LinkedHashMap<String,String> impuesto = new LinkedHashMap<String,String>();
        impuesto.put("impuesto", "IVA");
        impuesto.put("importe", this.getImpuestoTrasladado());
        impuesto.put("tasa", this.getTasaIva(id_sucursal));
        impuestos.add(impuesto);
        
        return impuestos;
    }
    
    
    
    
    //este metodo se utiliza para Nota de Credito CFD y CFDI
    @Override
    public HashMap<String, String> getNotaCreditoCfd_Cfdi_Datos(Integer id_nota_credito) {
        HashMap<String, String> data = new HashMap<String, String>();
        
        //obtener id del cliente
        String sql_to_query = ""
                + "SELECT "
                    + "fac_nota_credito.cxc_clie_id, "
                    + "'No Identificado'::character varying AS metodo_pago, "
                    + "''::character varying AS no_cuenta, "
                    + "'No aplica'::character varying AS condicion_pago, "
                    + "fac_nota_credito.subtotal, "
                    + "gral_mon.descripcion_abr AS moneda_abr, "
                    + "gral_mon.simbolo AS simbolo_moneda,"
                    + "gral_mon.iso_4217 AS moneda_iso,"
                    + "gral_mon.descripcion AS moneda_titulo,"
                    + "cxc_clie.numero_control, "
                    + "cxc_clie.razon_social, "
                    + "cxc_clie.rfc, "
                    + "cxc_clie.cxc_clie_tipo_adenda_id AS adenda_id, "
                    + "(CASE WHEN cxc_clie.localidad_alternativa IS NULL THEN '' ELSE cxc_clie.localidad_alternativa END) AS localidad_alternativa, "
                    + "(CASE WHEN fac_nota_credito.cxc_clie_df_id > 1 THEN sbtdf.calle ELSE cxc_clie.calle END ) AS calle,"
                    + "(CASE WHEN fac_nota_credito.cxc_clie_df_id > 1 THEN sbtdf.numero_interior ELSE (CASE WHEN cxc_clie.numero='' OR cxc_clie.numero IS NULL THEN cxc_clie.numero_exterior ELSE cxc_clie.numero END)  END ) AS numero_interior,"
                    + "(CASE WHEN fac_nota_credito.cxc_clie_df_id > 1 THEN sbtdf.numero_exterior ELSE (CASE WHEN cxc_clie.numero_exterior='' OR cxc_clie.numero_exterior IS NULL THEN cxc_clie.numero ELSE cxc_clie.numero_exterior END) END ) AS numero_exterior,"
                    + "(CASE WHEN fac_nota_credito.cxc_clie_df_id > 1 THEN sbtdf.colonia ELSE cxc_clie.colonia END ) AS colonia,"
                    + "(CASE WHEN fac_nota_credito.cxc_clie_df_id > 1 THEN sbtdf.municipio ELSE gral_mun.titulo END ) AS municipio,"
                    + "(CASE WHEN fac_nota_credito.cxc_clie_df_id > 1 THEN sbtdf.estado ELSE gral_edo.titulo END ) AS estado,"
                    + "(CASE WHEN fac_nota_credito.cxc_clie_df_id > 1 THEN sbtdf.pais ELSE gral_pais.titulo END ) AS pais,"
                    + "(CASE WHEN fac_nota_credito.cxc_clie_df_id > 1 THEN sbtdf.cp ELSE cxc_clie.cp END ) AS cp "
                + "FROM fac_nota_credito  "
                + "LEFT JOIN cxc_clie ON cxc_clie.id=fac_nota_credito.cxc_clie_id "
                + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
                + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
                + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
                + "JOIN gral_mon ON gral_mon.id=fac_nota_credito.moneda_id "
                + "LEFT JOIN (SELECT cxc_clie_df.id, (CASE WHEN cxc_clie_df.calle IS NULL THEN '' ELSE cxc_clie_df.calle END) AS calle, (CASE WHEN cxc_clie_df.numero_interior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_interior IS NULL OR cxc_clie_df.numero_interior='' THEN '' ELSE 'NO.INT.'||cxc_clie_df.numero_interior END)  END) AS numero_interior, (CASE WHEN cxc_clie_df.numero_exterior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_exterior IS NULL OR cxc_clie_df.numero_exterior='' THEN '' ELSE 'NO.EXT.'||cxc_clie_df.numero_exterior END )  END) AS numero_exterior, (CASE WHEN cxc_clie_df.colonia IS NULL THEN '' ELSE cxc_clie_df.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,(CASE WHEN cxc_clie_df.cp IS NULL THEN '' ELSE cxc_clie_df.cp END) AS cp  FROM cxc_clie_df LEFT JOIN gral_pais ON gral_pais.id = cxc_clie_df.gral_pais_id LEFT JOIN gral_edo ON gral_edo.id = cxc_clie_df.gral_edo_id LEFT JOIN gral_mun ON gral_mun.id = cxc_clie_df.gral_mun_id ) AS sbtdf ON sbtdf.id = fac_nota_credito.cxc_clie_df_id "
                + "WHERE fac_nota_credito.id="+id_nota_credito;
        
        //System.out.println("sql_to_query"+sql_to_query);
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        //int id_cliente = Integer.parseInt(map.get("cxc_clie_id").toString());
        
        String fecha = TimeHelper.getFechaActualYMDH();
        String[] fecha_hora = fecha.split(" ");
        //formato fecha: 2011-03-01T00:00:00
        this.setFechaComprobante(fecha_hora[0]+"T"+fecha_hora[1]);//este solo se utiliza en pdfcfd
        
        data.put("comprobante_attr_fecha",fecha_hora[0]+"T"+fecha_hora[1]);
        data.put("comprobante_attr_condicionesdepago",map.get("condicion_pago").toString().toUpperCase());
        data.put("comprobante_attr_formadepago","PAGO EN UNA SOLA EXIBICION");
        data.put("comprobante_attr_motivodescuento","");
        data.put("comprobante_attr_descuento","0.00");
        data.put("comprobante_attr_subtotal",map.get("subtotal").toString().toUpperCase());
        data.put("comprobante_attr_total",this.getTotal());
        data.put("comprobante_attr_metododepago",map.get("metodo_pago").toString().toUpperCase());
        data.put("comprobante_attr_simbolo_moneda",map.get("simbolo_moneda").toString().toUpperCase());
        data.put("moneda_abr",map.get("moneda_abr").toString().toUpperCase());
        data.put("nombre_moneda",map.get("moneda_titulo").toString().toUpperCase());
        data.put("comprobante_attr_moneda",map.get("moneda_iso").toString().toUpperCase());        
        
        String no_cta ="";
        if (!map.get("no_cuenta").toString().equals("null") && !map.get("no_cuenta").toString().equals("")){
            no_cta=map.get("no_cuenta").toString();
        }
        data.put("comprobante_attr_numerocuenta",no_cta);
        
        String no_ext="";
        String no_int="";
        
        if(map.get("numero_exterior").toString().equals("'")){
            no_ext="";
        }else{
            no_ext=map.get("numero_exterior").toString();
        }
        
        if(map.get("numero_interior").toString().equals("'")){
            no_int="";
        }else{
            no_int=map.get("numero_interior").toString();
        }
        
        //datos del cliente
        data.put("comprobante_receptor_attr_nombre",map.get("razon_social").toString());
        data.put("comprobante_receptor_attr_rfc",map.get("rfc").toString());
        data.put("comprobante_receptor_domicilio_attr_pais",map.get("pais").toString());
        data.put("comprobante_receptor_domicilio_attr_calle",map.get("calle").toString());
        data.put("comprobante_receptor_domicilio_attr_noexterior",no_ext);
        data.put("comprobante_receptor_domicilio_attr_nointerior",no_int);
        data.put("comprobante_receptor_domicilio_attr_colonia",map.get("colonia").toString());
        data.put("comprobante_receptor_domicilio_attr_localidad",map.get("localidad_alternativa").toString());
        data.put("comprobante_receptor_domicilio_attr_referencia","");
        data.put("comprobante_receptor_domicilio_attr_municipio",map.get("municipio").toString());
        data.put("comprobante_receptor_domicilio_attr_estado",map.get("estado").toString());
        data.put("comprobante_receptor_domicilio_attr_codigopostal",map.get("cp").toString());
        data.put("adenda_id",map.get("adenda_id").toString());
        
        //este solo se utiliza en el pdfcfd y cfdi
        data.put("numero_control",map.get("numero_control").toString());
        
        return data;
    }
    
    
    
    
    @Override
    public LinkedHashMap<String, String> getNotaCreditoCfd_DatosExtrasXml(Integer id_nota_credito, String tipo_cambio,String id_usuario,String moneda_id, Integer id_empresa, Integer id_sucursal, Integer app_selected, String command_selected, String extra_data_array, String fac_saldado) {
        
        LinkedHashMap<String,String> datosExtras = new LinkedHashMap<String,String>();
        //estos son requeridos para cfd
        datosExtras.put("tipo_cambio", tipo_cambio);
        datosExtras.put("moneda_id", moneda_id);
        datosExtras.put("usuario_id", id_usuario);
        datosExtras.put("empresa_id", String.valueOf(id_empresa));
        datosExtras.put("sucursal_id", String.valueOf(id_sucursal));
        datosExtras.put("id_nota_credito", String.valueOf(id_nota_credito));
        datosExtras.put("app_selected", String.valueOf(app_selected));
        datosExtras.put("command_selected", command_selected);
        datosExtras.put("extra_data_array", extra_data_array);
        datosExtras.put("fac_saldado", fac_saldado);
        return datosExtras;
    }
    
    
    @Override
    public String getSerieFolioNotaCredito(Integer id_nota_credito) {
        String sql_to_query = "SELECT serie_folio FROM fac_nota_credito WHERE id="+id_nota_credito+" LIMIT 1;";
        //System.out.println("GetSerieFolioNotaCredito:"+sql_to_query);
        Map<String, Object> map_iva = this.getJdbcTemplate().queryForMap(sql_to_query);
        String serie_folio = map_iva.get("serie_folio").toString();
        return serie_folio;
    }
    
    
    
    
    //obtiene la lista de conceptos  de la factura para el pdfCfd
    @Override
    public ArrayList<HashMap<String, String>> getNotaCreditoCfd_ListaConceptosPdf(String serieFolio) {
        
        String sql_query = ""
                + "SELECT "
                    + "''::character varying AS sku, "
                    + "fac_nota_credito.concepto AS titulo, "
                    + "''::character varying AS numero_lote, "
                    + "1::double precision AS cantidad, "
                    + "''::character varying AS unidad, "
                    + "gral_mon.descripcion as moneda, "
                    + "fac_nota_credito.subtotal AS precio_unitario, "
                    + "fac_nota_credito.subtotal AS importe "
                + "FROM fac_nota_credito "
                + "LEFT JOIN gral_mon ON gral_mon.id = fac_nota_credito.moneda_id "
                + "WHERE fac_nota_credito.serie_folio='"+serieFolio+"';";
        
        //System.out.println("Obtiene lista conceptos pdf: "+sql_query);
        ArrayList<HashMap<String, String>> hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("numero_lote","");
                    row.put("unidad",rs.getString("unidad"));
                    row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"),2 ));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("moneda",rs.getString("moneda"));
                    row.put("denominacion","");
                    
                    /*
                    System.out.println(rs.getString("moneda")+"  "
                            + ""+rs.getString("sku")+"  "
                            + "    "+StringHelper.roundDouble(rs.getDouble("precio_unitario"),4)
                            + "    "+StringHelper.roundDouble(rs.getDouble("importe"),2)
                            
                            );
                    */
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
    
    @Override
    public HashMap<String, String> getNotaCreditoCfd_DatosExtrasPdf(String serieFolio, String proposito, String cadena_original, String sello_digital, Integer id_sucursal) {
        HashMap<String, String> extras = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> valorIva = new ArrayList<HashMap<String, Object>>();
        
        valorIva= getValoriva(id_sucursal);//obtiene el valor del iva
        
        //obtener datos del vendedor y terminos de pago
        String sql_to_query = ""
                + "SELECT fac_nota_credito.subtotal, "
                    + "fac_nota_credito.impuesto, "
                    + "fac_nota_credito.monto_retencion, "
                    + "fac_nota_credito.total, "
                    + "to_char(fac_nota_credito.momento_expedicion,'yyyy-mm-dd') AS fecha_comprobante, "
                    + "fac_nota_credito.observaciones, "
                    + "cxc_agen.nombre AS nombre_vendedor, "
                    + "gral_mon.descripcion AS nombre_moneda,"
                    + "gral_mon.descripcion_abr AS moneda_abr "
                + "FROM fac_nota_credito  "
                + "LEFT JOIN gral_mon on gral_mon.id = fac_nota_credito.moneda_id "
                + "JOIN cxc_agen ON cxc_agen.id =  fac_nota_credito.cxc_agen_id  "
                + "WHERE fac_nota_credito.serie_folio='"+serieFolio+"';";
        
        Map<String, Object> mapVendedorCondiciones = this.getJdbcTemplate().queryForMap(sql_to_query);
        extras.put("subtotal", StringHelper.roundDouble(mapVendedorCondiciones.get("subtotal").toString(),2));
        extras.put("impuesto", StringHelper.roundDouble(mapVendedorCondiciones.get("impuesto").toString(),2));
        extras.put("monto_retencion", StringHelper.roundDouble(mapVendedorCondiciones.get("monto_retencion").toString(),2));
        extras.put("total", StringHelper.roundDouble(mapVendedorCondiciones.get("total").toString(),2));
        extras.put("nombre_moneda", mapVendedorCondiciones.get("nombre_moneda").toString());
        extras.put("moneda_abr", mapVendedorCondiciones.get("moneda_abr").toString());
        extras.put("nombre_vendedor", mapVendedorCondiciones.get("nombre_vendedor").toString());
        extras.put("observaciones", mapVendedorCondiciones.get("observaciones").toString() );
        //extras.put("fecha_comprobante", mapVendedorCondiciones.get("fecha_comprobante").toString() );
        extras.put("terminos", "");
        extras.put("dias", "0");
        extras.put("orden_compra", "" );
        extras.put("folio_pedido", "" );
        extras.put("fecha_vencimiento", "" );
        extras.put("proposito", proposito);
        extras.put("cadena_original", cadena_original);
        extras.put("sello_digital", sello_digital);
        extras.put("serieFolio", serieFolio);
        return extras;
    }
    
    
    
    
    
    
    //obtiene la lista de conceptos para la NOTA DE CREDITO CFDI y CFDITF
    @Override
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfdi_ListaConceptos(Integer id_nota_credito) {
        
        String sql_query = ""
                + "SELECT "
                    + "'1'::character varying AS no_identificacion, "
                    + "1::double precision AS cantidad, "
                    + "concepto AS descripcion, "
                    + "'No aplica'::character varying AS unidad, "
                    + "subtotal  AS valor_unitario, "
                    + "subtotal AS importe "
                + "FROM fac_nota_credito "
                + "WHERE id="+id_nota_credito+";";
        
        //System.out.println("Obteniendo lista de conceptos para cfdi: "+sql_query);
        ArrayList<LinkedHashMap<String, String>> hm_conceptos = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("noIdentificacion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("no_identificacion"))));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("descripcion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("descripcion"))));
                    row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("unidad"))));
                    row.put("valorUnitario",StringHelper.roundDouble(rs.getDouble("valor_unitario"),2) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    return row;
                }
            }
        );
        return hm_conceptos;
    }
    
    
    
    
    //obtiene datos extras para la NOTA DE CREDITO CFDI 
    @Override
    public LinkedHashMap<String, String> getNotaCreditoCfdi_DatosExtras(Integer id_nota_credito, String serie, String folio) {
        LinkedHashMap<String,String> datosExtras = new LinkedHashMap<String,String>();
        String monto_factura="";
        String id_moneda="";
        String denom="";
        String denominacion = "";
        String cantidad_letras="";
        
        //obtener id del cliente
        String sql_to_query = ""
                + "SELECT "
                    + "fac_nota_credito.moneda_id, "
                    + "gral_mon.descripcion_abr AS moneda_abr, "
                    + "gral_mon.iso_4217 AS nombre_moneda,"
                    + "gral_mon.simbolo AS simbolo_moneda, "
                    + "fac_nota_credito.tipo_cambio, "
                    + "(CASE WHEN fac_nota_credito.cxc_agen_id=0 THEN '' ELSE cxc_agen.nombre END ) AS clave_agente, "
                    + "fac_nota_credito.subtotal as subtotal_conceptos, "
                    + "fac_nota_credito.impuesto, "
                    + "fac_nota_credito.total as monto_total, "
                    + "fac_nota_credito.observaciones, "
                    + "(CASE WHEN fac_nota_credito.moneda_id=1 THEN 1 ELSE fac_nota_credito.tipo_cambio END) AS tipo_cambio "
                + "FROM fac_nota_credito "
                + "JOIN gral_mon ON gral_mon.id=fac_nota_credito.moneda_id "
                + "JOIN cxc_agen ON cxc_agen.id=fac_nota_credito.cxc_agen_id "
                + "WHERE  fac_nota_credito.id="+id_nota_credito+";";
        
        //System.out.println("sql_to_query: "+sql_to_query);
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        //estos son requeridos para cfdi
        datosExtras.put("serie", serie);
        datosExtras.put("folio", folio);
        datosExtras.put("orden_compra", "");
        datosExtras.put("clave_agente", map.get("clave_agente").toString());
        datosExtras.put("nombre_moneda", map.get("nombre_moneda").toString());
        datosExtras.put("simbolo_moneda", map.get("simbolo_moneda").toString());
        datosExtras.put("moneda_abr", map.get("moneda_abr").toString());
        datosExtras.put("tipo_cambio", StringHelper.roundDouble(map.get("tipo_cambio").toString(),4));
        datosExtras.put("subtotal_conceptos", StringHelper.roundDouble(map.get("subtotal_conceptos").toString(),2));
        datosExtras.put("monto_total", StringHelper.roundDouble(map.get("monto_total").toString(),2));
        datosExtras.put("observaciones", map.get("observaciones").toString());
        
        monto_factura = StringHelper.roundDouble(map.get("monto_total").toString(),2);
        id_moneda = map.get("moneda_id").toString();
        
        BigInteger num = new BigInteger(monto_factura.split("\\.")[0]);
        n2t cal = new n2t();
        String centavos = monto_factura.substring(monto_factura.indexOf(".")+1);
        String numero = cal.convertirLetras(num);
        
        //convertir a mayuscula la primera letra de la cadena
        String numeroMay = numero.substring(0, 1).toUpperCase() + numero.substring(1, numero.length());
        
        denom = map.get("moneda_abr").toString();
        
        if(centavos.equals(num.toString())){
            centavos="00";
        }
        
        if(id_moneda.equals("1")){
            denominacion = "pesos";
        }
        
        if(id_moneda.equals("2")){
            denominacion = "dolares";
        }
        
        cantidad_letras=numeroMay + " " + denominacion + ", " +centavos+"/100 "+ denom;
        datosExtras.put("monto_total_texto", cantidad_letras.toUpperCase());
        
        return datosExtras;
    }
    
    
    
    //Obtiene los impuestos trasladados para la NOTA DE CREDITO CFDI 
    @Override
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfdi_ImpuestosTrasladados(Integer id_nota_credito) {
        String sql_to_query = "SELECT impuesto,valor_impuesto FROM fac_nota_credito WHERE  id="+id_nota_credito+" AND impuesto >0 AND impuesto IS NOT NULL;";
        //System.out.println("sql_to_query imp tras:"+sql_to_query);
        
        ArrayList<LinkedHashMap<String, String>> tras = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("impuesto","IVA");
                    row.put("importe",StringHelper.roundDouble(rs.getString("impuesto"),2));
                    row.put("tasa", StringHelper.roundDouble(rs.getString("valor_impuesto"),2));
                    return row;
                }
            }
        );
        return tras;
    }
    
    
    
    
    
    @Override
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfdi_ImpuestosRetenidos(Integer id_nota_credito) {
        String sql_to_query = "SELECT monto_retencion, tasa_retencion_immex as tasa FROM fac_nota_credito WHERE id="+id_nota_credito+"  AND monto_retencion >0 AND monto_retencion IS NOT NULL;";
        
        //System.out.println("sql_to_query retenidos:"+sql_to_query);
        ArrayList<LinkedHashMap<String, String>> ret = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("impuesto","IVA");
                    row.put("importe",StringHelper.roundDouble(rs.getString("monto_retencion"),2));
                    row.put("tasa", StringHelper.roundDouble(rs.getString("tasa"),2));
                    return row;
                }
            }
        );
        return ret;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getFacDevoluciones_DatosNotaCredito(String factura) {
        String sql_to_query = ""
                + "SELECT "
                    + "serie_folio AS folio_nota, "
                    + "subtotal AS subtotal_nota, "
                    + "impuesto AS impuesto_nota, "
                    + "monto_retencion AS monto_ret_nota, "
                    + "total AS total_nota, "
                    + "tipo_cambio AS tc_nota "
                + "FROM fac_nota_credito "
                + "WHERE serie_folio_factura='"+factura+"' "
                + "AND gral_app_id_creacion=76;";
        
        ArrayList<HashMap<String, Object>> ret = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("folio_nota",rs.getString("folio_nota"));
                    row.put("subtotal_nota",StringHelper.roundDouble(rs.getString("subtotal_nota"),2));
                    row.put("impuesto_nota",StringHelper.roundDouble(rs.getString("impuesto_nota"),2));
                    row.put("monto_ret_nota",StringHelper.roundDouble(rs.getString("monto_ret_nota"),2));
                    row.put("total_nota",StringHelper.roundDouble(rs.getString("total_nota"),2));
                    row.put("tc_nota",StringHelper.roundDouble(rs.getString("tc_nota"),4));
                    return row;
                }
            }
        );
        return ret;
    }
    
    
    
    
    @Override
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfdiTf_ListaConceptosXml(Integer id_nota_credito) {
        String sql_query = ""
                + "SELECT "
                    + "'1'::character varying AS no_identificacion,"
                    + "1::double precision AS cantidad,"
                    + "concepto AS descripcion, "
                    + "'No aplica'::character varying AS unidad, "
                    + "subtotal as valor_unitario,"
                    + "subtotal as importe,"
                    + "impuesto as importe_impuesto,"
                    + "tasa_retencion_immex AS tasa_retencion, "
                    + "''::character varying AS numero_aduana, "
                    + "''::character varying AS fecha_aduana, "
                    + "''::character varying AS aduana_aduana "
                    + "FROM fac_nota_credito "
                + "WHERE id="+id_nota_credito;
                
                
        
        //System.out.println(sql_query);
        //System.out.println("getListaConceptosXmlCfdiTimbreFiscal: "+sql_query);
        
        //System.out.println("noIdentificacion "+" | descripcion      "+" | cant"+" | precio_uni"+" | importe"+" | importe_imp"+" | valor_imp"+" | tasa_ret"  );
        
        ArrayList<LinkedHashMap<String, String>> hm_conceptos = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("noIdentificacion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("no_identificacion"))));
                    row.put("cantidad",rs.getString("cantidad"));
                    row.put("descripcion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("descripcion"))));
                    row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("unidad"))));
                    row.put("valorUnitario",StringHelper.roundDouble(rs.getDouble("valor_unitario"),2) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("importe_impuesto",StringHelper.roundDouble(rs.getDouble("importe_impuesto"),2) );
                    row.put("tasa_retencion",StringHelper.roundDouble(rs.getDouble("tasa_retencion"),2) );
                    row.put("numero_aduana","");
                    row.put("fecha_aduana","");
                    row.put("aduana_aduana","");
                    return row;
                }
            }
        );
        
        try {
            calcula_Totales_e_Impuestos(hm_conceptos);
        } catch (SQLException ex) {
            Logger.getLogger(FacturasSpringDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return hm_conceptos;
    }
    
    @Override
    public LinkedHashMap<String, Object> getDatosAdenda(Integer tipoDoc, Integer noAdenda, HashMap<String,String> dataFactura) {
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        String sql_query = "select * from cxc_clie_adenda_datos where cxc_clie_adenda_tipo="+noAdenda;
        
        //Agregar el tipo de DOCUMENTO(1=Factura, 2=Consignacion, 3=Retenciones(Honorarios, Arrendamientos, Fletes), 8=Nota de Cargo, 9=Nota de Credito)
        data.put("claseDoc", tipoDoc);
        
        if(noAdenda==1){
            //Adenda FEMSA QUIMIPRODUCTOS
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql_query);
            for (Map row : rows) {
                String campo = String.valueOf(row.get("campo"));
                String valor = String.valueOf(row.get("valor"));
                
                if(campo.equals("noVersAdd")){
                    data.put(campo, valor);
                }
                if(campo.equals("noSociedad")){
                    data.put(campo, valor);
                }
                if(campo.equals("noProveedor")){
                    data.put(campo, valor);
                }
            }
            
            data.put("noPedido", dataFactura.get("orden_compra"));
            data.put("moneda", dataFactura.get("moneda2"));
            data.put("noEntrada", "");
            data.put("noRemision", "");
            data.put("noSocio", "");
            data.put("centro", "");
            data.put("iniPerLiq", "");
            data.put("finPerLiq", "");
            data.put("retencion1", "");
            data.put("retencion2", "");
            data.put("email", "");
        }
        
        return data;
    }
    
    
    
    
    
}