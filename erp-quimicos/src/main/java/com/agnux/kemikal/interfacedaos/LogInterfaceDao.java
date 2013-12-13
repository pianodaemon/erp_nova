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
    
    public ArrayList<HashMap<String, String>> getVehiculo_Datos(Integer id); 
    public ArrayList<HashMap<String, Object>> getVehiculos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    
    //Metodos para el catalogo de Operadores
    public ArrayList<HashMap<String, String>> getOperadores_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getOperadores_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);

}
