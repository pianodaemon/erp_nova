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
    
    
    
}
