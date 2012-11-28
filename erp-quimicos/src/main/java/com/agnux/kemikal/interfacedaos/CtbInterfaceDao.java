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
 * 22/03/2012
 */
public interface CtbInterfaceDao {
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);
    public String selectFunctionForCtbAdmProcesos(String campos_data, String extra_data_array);
    public int countAll(String data_string);
    
    public ArrayList<HashMap<String, Object>> getCentroCostos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getCentroCosto_Datos(Integer id);
    
    
    public ArrayList<HashMap<String, Object>> getTipoPolizas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getTipoPoliza_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getTipoPoliza_Grupos();
    
    public ArrayList<HashMap<String, Object>> getConceptosContables_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getConceptoContable_Datos(Integer id);
    
    public ArrayList<HashMap<String, Object>> getCuentasMayor_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getCuentasMayor_Clases();
    public ArrayList<HashMap<String, String>> getCuentaDeMayor_Datos(Integer id);
    
    
    //metodos para catalogo de cuentas contables 
    public ArrayList<HashMap<String, Object>> getCuentasContables_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getCuentasContables_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getCuentasContables_CuentasMayor(Integer id_empresa);
    
    
}
