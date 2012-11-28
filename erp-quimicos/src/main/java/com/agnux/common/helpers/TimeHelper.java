/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.common.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;

/**
 *
 * @author pianodaemon
 */
public class TimeHelper {
    
    public static Long now(){
        String unix_epoch_time = String.valueOf( System.currentTimeMillis() );
	return Long.parseLong(unix_epoch_time); 
    }
    
    	
    /**Compara dos fechas en dia mes y a�o solamente, ya que la comparacion con tipo Date hce comparacion tambien en milisegundos
     * @param calendario1
     * @param calendario2
     * @return 0 si la fecha es igual, -1 si es menor, 1 si es mayor. Si no cumple con las condiciones regresa 99
     */
    public static int compareCalendars(Date fechaAComparar, Date fechaBase){
    	Calendar calendario1 = Calendar.getInstance();
    	calendario1.setTime(fechaAComparar);
    	Calendar calendario2 = Calendar.getInstance();
    	calendario2.setTime(fechaBase);
		if (calendario1.get(Calendar.YEAR) == calendario2.get(Calendar.YEAR) 
			&& calendario1.get(Calendar.MONTH) == calendario2.get(Calendar.MONTH) 
			&& calendario1.get(Calendar.DATE) == calendario2.get(Calendar.DATE)) {
			return 0;
		}
		if (calendario1.get(Calendar.YEAR)  >= calendario2.get(Calendar.YEAR) 
				&& calendario1.get(Calendar.MONTH) >= calendario2.get(Calendar.MONTH) 
				&& calendario1.get(Calendar.DATE) > calendario2.get(Calendar.DATE)) {
			return 1;
		}
		if (calendario1.get(Calendar.YEAR)  <= calendario2.get(Calendar.YEAR) 
				&& calendario1.get(Calendar.MONTH) <= calendario2.get(Calendar.MONTH) 
				&& calendario1.get(Calendar.DATE) < calendario2.get(Calendar.DATE)) {
			return -1;
		}
		return 99;
    }
    
    public static boolean compararFechas(String fechaACompar,String fechaInicio,String fechaFin){
        boolean respuesta = false;
        fechaACompar = fechaACompar.replaceAll("T"," ");
        DateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            Date fecha_comparar = formato.parse(fechaACompar);
            Date fecha_ini = formato.parse(fechaInicio);
            Date fecha_fin = formato.parse(fechaFin);
            if(fecha_ini.after(fecha_comparar) && fecha_fin.before(fecha_comparar)){
                respuesta = true;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return respuesta;
    }
    public static int compareCalendars(String fechaAComparar, String fechaBase){
    	
    	int valor_retorno = -1;
    	
    	DateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
    	
    	try{
    		Date d1 = formato.parse(fechaAComparar);
        	Date d2 = formato.parse(fechaBase);
        	
        	if (d1.equals(d2))
        		valor_retorno = 0;
        	else if (d1.before(d2))
        		valor_retorno = -1;
        	else if ( d1.after(d2))
        		valor_retorno = 1;
        	else
        		valor_retorno = 99;
    	}
    	catch(Exception e){
            e.printStackTrace();
        }
    	return valor_retorno;
    }
    
        
	public static Date getFechaActual(){		
		Date fechasalida = new Date();
		fechasalida = new Date(fechasalida.getTime());		
		return fechasalida;
	}
        
	public static Date getFechaDiaAnterior(){
		Date fecharetorno = new Date();
		fecharetorno = new Date(fecharetorno.getTime()-100000000);
		return fecharetorno;
	}
	
	public static String getFechaActualYMD(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fechasalida = sdf.format(new Date());				
		return fechasalida;
	}
	public static String getFechaActualYMD2(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fechasalida = sdf.format(new Date());				
		return fechasalida;
	}
	
	public static String getFechaActualYMD3(Date fecha){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fechasalida = sdf.format(fecha);				
		return fechasalida;
	}
	
	public static String getFechaActualYMDH(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fechasalida = sdf.format(new Date());				
		return fechasalida;
	}
        
        
	public static String convertirDateToString(Date fecha){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fechasalida = sdf.format(fecha);				
		return fechasalida;		
	}
	public static String convertirDatesToString(Date fecha){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechasalida = sdf.format(fecha);				
		return fechasalida;		
	}
	
	public static String cambiarFormatoMDY(Date fecha){
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		String fechasalida = sdf.format(fecha);				
		return fechasalida;			
	}
	
	public static String getFechaActualMDY(){		
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		Date fechasalida = new Date();
		fechasalida = new Date(fechasalida.getTime());	
		String retorno = sdf.format(fechasalida);
		return retorno;
	}
	
	public static String getFechaActualY(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String fechasalida = sdf.format(new Date());			
		return fechasalida;
	}
        
        
	public static String getMesActual(){
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		String mesSalida = sdf.format(new Date());			
		return mesSalida;
	}
        
        
        //metodo para convertir fechas de numero a nombre del mes 1 = enero.
        public static String ConvertNumToMonth(int mesEntrada){
            String mesSalida = "";
            switch (mesEntrada){
                case 1:
                    mesSalida = "Enero";
                break;
                    case 2:  mesSalida="Febrero";
                break;
                    case 3:  mesSalida="Marzo";
                break;
                    case 4:  mesSalida="Abril";
                break;
                    case 5:  mesSalida="Mayo";
                break;
                    case 6:  mesSalida="Junio";
                break;
                    case 7:  mesSalida="Julio";
                break;
                    case 8:  mesSalida="Agosto";
                break;
                    case 9:  mesSalida="Septiembre";
                break;
                    case 10:  mesSalida="Octubre";
                break;
                    case 11:  mesSalida="Noviembre";
                break;
                    case 12:  mesSalida="Diciembre";
                break;

                default:mesSalida="";
                    break;
            }
            return mesSalida;
        }

        
}
