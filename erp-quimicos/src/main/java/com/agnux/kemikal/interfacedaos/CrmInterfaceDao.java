
package com.agnux.kemikal.interfacedaos;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author luis Carrillo
 */
public interface CrmInterfaceDao {
     
    
    //metodos  de uso general
    public int countAll(String data_string);
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);

    //metodos para aplicativo de Motivos de Visita
    public ArrayList<HashMap<String, String>> getMotivoVisita_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getMotivosVisita_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc,Integer id_empresa);

    //metodos para aplicativo de Formas de Contacto
    public ArrayList<HashMap<String, Object>> getFormasContacto_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc,Integer id_empresa);
    public ArrayList<HashMap<String, String>> getFormasContacto_Datos(Integer id);
    
    //metodos para aplicativo de Motivos de LLamadas
    public ArrayList<HashMap<String, String>> getMotivosLlamada_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getMotivosLlamada_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc,Integer id_empresa);
    
    
    
    
    

}
