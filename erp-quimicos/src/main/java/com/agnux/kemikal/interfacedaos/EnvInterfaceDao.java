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
 * 09/abril/2012
 */
public interface EnvInterfaceDao {
    //metodos  de uso general
    public int countAll(String data_string);
    public HashMap<String, String> selectFunctionValidateAplicativo(String data, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);
    //public String selectFunctionForEnvAdmProcesos(String campos_data, String extra_data_array);
    
    public ArrayList<HashMap<String, String>> getBuscadorProductos(String sku, String tipo, String descripcion,Integer id_empresa);
    public ArrayList<HashMap<String, String>> getDataProductBySku(String codigo, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProductoTipos();
    public ArrayList<HashMap<String, String>> getProductoPresentaciones(Integer id_producto, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProductoPresentacionesON(Integer id_producto);
    
    //metodos para Configuracion de Envasado
    public ArrayList<HashMap<String, Object>> getEnvConf_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getEnvConf_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getEnvConf_DatosGrid(Integer id);
    
    
    
    
    
}
