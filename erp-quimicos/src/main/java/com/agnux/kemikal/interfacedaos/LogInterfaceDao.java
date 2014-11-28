/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.interfacedaos;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Noe Martinez
 * gpmarsan@gmail.com
 * 30/julio/2012
 */
public interface LogInterfaceDao {
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);
    public String selectFunctionForLogAdmProcesos(String campos_data, String extra_data_array);
    public int countAll(String data_string);
    
    //catalogo de asignacion de rutas
    public ArrayList<HashMap<String, Object>> getRutas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    //public ArrayList<HashMap<String, String>> getFolioRuta(Integer id_empresa, Integer id_sucursal );
    public ArrayList<HashMap<String, String>> getFacturas_entrega_mercancia(Integer id_empresa, String fecha_inicial, String fecha_final, String factura, Integer tipo_busqueda);
    public ArrayList<HashMap<String, String>> getFacturas_fac_rev_cobro_detalle(Integer id_empresa,String folio_fac_rev_cobro);
    
    public ArrayList<HashMap<String, String>> getchoferes(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getvehiculo(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getdatos_editar_header(Integer id);  
    public ArrayList<HashMap<String, String>> getdatos_editar_minigridRutas(Integer id_empresa,Integer id_ruta);
    public ArrayList<HashMap<String, String>> getdatos_editar_minigridFRC(Integer id_ruta);
    
    
    //metodo para pdf de ruta
    public HashMap<String, String> getRuta_DatosPdf(Integer id_ruta);
    public ArrayList<HashMap<String, String>> getRuta_ListaFacturasPdf(Integer id_ruta);
    
    public ArrayList<HashMap<String, String>> getUnidades_Datos(Integer id); 
    public ArrayList<HashMap<String, Object>> getUnidades_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, Object>> getUnidades_Marcas(Integer idEmp);
    public ArrayList<HashMap<String, Object>> getUnidades_Tipos(Integer idEmp);
    public ArrayList<HashMap<String, Object>> getUnidades_Clases(Integer idEmp);
    public ArrayList<HashMap<String, Object>> getUnidades_TiposPlaca(Integer idEmp);
    public ArrayList<HashMap<String, Object>> getUnidades_TiposRodada(Integer idEmp);
    public ArrayList<HashMap<String, Object>> getUnidades_TiposCaja(Integer idEmp);
    public ArrayList<HashMap<String, Object>> getUnidades_AniosUnidad();
    public ArrayList<HashMap<String, Object>> getBuscadorProveedores(String rfc, String no_proveedor, String razon_social, String transportista,Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getDatosProveedorByNoProv(String numeroProveedor, String transportista, Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getBuscadorOperadores(String no_operador, String nombre, Integer id_proveedor, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getDatosOperadorByNo(String no_operador, Integer id_proveedor, Integer id_empresa, Integer id_sucursal);
    
    
    //Metodos para el catalogo de Operadores
    public ArrayList<HashMap<String, String>> getOperadores_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getOperadores_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    
    //Carga de Documentos
    public ArrayList<HashMap<String, Object>> getBuscadorClientes(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getDatosClienteByNoCliente(String no_control, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getAlmacenesSucursal(Integer id_empresa, Integer id_sucursal);
    public int getDeleteFromLogCargaDocTmp(Integer id_emp, Integer suc);
    public HashMap<String, String> getInsertLogCargaDocTmp(String data_string);
    public String getUpdateDocInvExi(Integer usuario_id, Integer empresa_id, Integer sucursal_id, Integer id_cliente);
    public int getVerificarDocumento(Integer id_emp, Integer id_clie, String no_carga);
    
    
    
    public Integer getUserRolAdmin(Integer id_user);
    public ArrayList<HashMap<String, Object>> getBuscadorUnidades(String no_unidad, String marca, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getDatosUnidadByNoUnidad(String no_unidad, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getSucursales(Integer idEmp);
    public ArrayList<HashMap<String, Object>> getTransportistas(Integer idEmp, Integer idSuc);
    public ArrayList<HashMap<String, Object>> getLogAdmViaje_CargasPendientes(Integer id_empresa, Integer id_suc_user, String no_clie, String no_carga, String no_ped, String no_dest, String dest, String poblacion);
    public ArrayList<HashMap<String, Object>> getLogAdmViaje_DetallePedido(Integer id_ped);
    public ArrayList<HashMap<String, Object>> getLogAdmViaje_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, Object>> getLoAdmViaje_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getLoAdmViaje_DatosGrid(Integer id);
    public ArrayList<HashMap<String, Object>> getLoAdmViaje_Adicionales(Integer id);
    public HashMap<String, String> getLoAdmViaje_DatosPdf(Integer id);
    public ArrayList<HashMap<String, String>> getLoAdmViaje_ListaPdf(Integer id_);
    public ArrayList<HashMap<String, Object>> getBuscadorServiciosAdicionales(String sku, String descripcion, Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getDataServicioAdicional(String codigo, Integer id_empresa);
 
    
    //Catalogo de servicios adicionales
    public ArrayList<HashMap<String, Object>> getServAdic_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, Object>> getServAdic_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getBuscadorProductos(String no_cliente, String sku, String tipo, String descripcion, Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getDataProductBySku(String no_cliente, String codigo, String tipo, Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getProducto_Tipos();
    
    //Metodos para el catalogo de Vehiculo Marca
    public ArrayList<HashMap<String, String>> getVehiculoMarca_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getVehiculoMarca_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);

    //Metodos para el catalogo de Vehiculo Tipo Rodada
    public ArrayList<HashMap<String, String>> getVehiculoTipoRodada_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getVehiculoTipoRodada_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);

    //Metodos para el catalogo de Vehiculo Tipo Caja
    public ArrayList<HashMap<String, String>> getVehiculoTipoCaja_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getVehiculoTipoCaja_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    
    //Metodos para el catalogo de Vehiculo Tipo Unidades
    public ArrayList<HashMap<String, String>> getVehiculoTipoUnidades_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getVehiculoTipoUnidades_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    
    //app = 188 Aplicativo Captura de Numero de Facturas(LOG)
    public ArrayList<HashMap<String, Object>> getLogRegCarga_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, Object>> getLogRegCarga_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getLogRegCarga_DatosGrid(Integer id);
    public ArrayList<HashMap<String, Object>> tratar_datos_grid(ArrayList<HashMap<String, Object>> partidas);
    public HashMap<String, Object> getLogRegCarga_VerificaStatusPartida(Integer id_reg);
    
    public ArrayList<HashMap<String, Object>> getLogPar(Integer id_emp, Integer id_suc);
    public ArrayList<HashMap<String, Object>> getBuscadorDestinatarios(String cadena, Integer filtro, Integer cliente_id, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getDatosByNoDestinatario(String no_control, Integer cliente_id, Integer id_empresa, Integer id_sucursal);
    
}
