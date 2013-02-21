/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.interfacedaos;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * @author Noé Martinez
 * gpmarsan@gmail.com
 * 21/junio/2012
 */
public interface PocInterfaceDao{
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);
    public int countAll(String data_string);
    
    public ArrayList<HashMap<String, String>> getBuscadorClientes(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getBuscadorProspectos(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getBuscadorProductos(String sku, String tipo, String descripcion, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProductoTipos();
    public ArrayList<HashMap<String, String>> getPresentacionesProducto(String sku,String lista_precio, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getMonedas();
    public ArrayList<HashMap<String, String>> getAgentes(Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getCondicionesDePago();
    public Double getTipoCambioActual();
    public ArrayList<HashMap<String, String>> getValoriva(Integer id_sucursal);
    
    //metodos para aplicativo pedidos y autorizacion de pedidos
    public ArrayList<HashMap<String, Object>> getPocPedidos_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getPocPedido_Datos(Integer id_pedido);
    public ArrayList<HashMap<String, String>> getPocPedido_DatosGrid(Integer id_pedido);
    public ArrayList<HashMap<String, String>> getPocPedido_Almacenes(Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getPocPedido_DireccionesFiscalesCliente(Integer id_cliente);
    public HashMap<String, String> getDatosPDF(Integer id_pedido);
    
    //metodos para aplicativo Remisiones de CLientes
    public ArrayList<HashMap<String, Object>> getRemisiones_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getRemisiones_Datos(Integer id_remision);
    public ArrayList<HashMap<String, String>> getRemisiones_DatosGrid(Integer id_remision);
    public ArrayList<HashMap<String, String>> getMetodosPago();
    
    public HashMap<String, String> getRemisiones_DatosPdf(Integer id_remision);
    public ArrayList<HashMap<String, String>> getRemisiones_ConceptosPdf(Integer id_remision, String rfc_empresa);
    
    
    //metodos para generar reporte Pedidos
    public ArrayList<HashMap<String,String>>getReportePedidos(Integer opcion, Integer agente, String cliente, String fecha_inicial, String fecha_final,Integer id_empresa);
    //metodo para alimentar el select de agentes
    public ArrayList<HashMap<String,String>> getAgente(Integer id_empresa);
    //metodo para alimentar el select de los estados de los pedidos
    public ArrayList<HashMap<String,String>> getEstadoPedido();
    
    
    
    //reporte de Articulos Reservados   pocDao(Proceso Comercial).
    public ArrayList<HashMap<String, String>> getReporteArticulosReservados( Integer id_empresa, Integer id_usuario,String codigo, String descripcion);
    
    //trae la lista de precios
    public ArrayList<HashMap<String, String>> getListaPrecio(Integer lista_precio);
    
    
    //metodos para aplicativo de Cotizaciones
    public ArrayList<HashMap<String, Object>> getCotizacion_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getCotizacion_Datos(Integer id_cot);
    public ArrayList<HashMap<String, String>> getCotizacion_DatosCliente(Integer id_cot);
    public ArrayList<HashMap<String, String>> getCotizacion_DatosProspecto(Integer id_cot);
    public ArrayList<HashMap<String, String>> getCotizacion_DatosGrid(Integer id);
    
}
