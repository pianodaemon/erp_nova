/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;

/**
 *
 * @author Noe Martinez
 * gpmarsan@gmail.com
 * 03/diciembre/2013
 */
public class ComRepDiasEntregaPromedio {
    
    public ComRepDiasEntregaPromedio(String fileout, String tituloReporte, String razon_soc_empresa, String periodo,ArrayList<HashMap<String, String>> datos) {
        
        
        // Se crea el libro
        HSSFWorkbook libro = new HSSFWorkbook();

        // Se crea una hoja dentro del libro
        HSSFSheet hoja1 = libro.createSheet();

        // Se crea una fila dentro de la hoja
        HSSFRow fila1 = hoja1.createRow(0);
        int numCols=7;
        
        //8 es el numero de columnas que requiere el reporte
        for (int i=0;i<=numCols;i++){
            // Se crea una celda dentro de la fila
            HSSFCell celda = fila1.createCell((short)i);
            
            HSSFRichTextString texto = new HSSFRichTextString("celda "+ (i+1));
            celda.setCellValue(texto);
        }
        
        // Se crea una fila2 dentro de la hoja
        HSSFRow fila2 = hoja1.createRow(1);
        HSSFCell celda12 = fila2.createCell((short)0);
        
        HSSFRichTextString empresa = new HSSFRichTextString(razon_soc_empresa);
        celda12.setCellValue(empresa);
        
        hoja1.addMergedRegion(new CellRangeAddress(
                1, //first row (0-based)
                1, //last row  (0-based)
                0, //first column (0-based)
                7  //last column  (0-based)
        ));
        
        // Se crea una fila3 dentro de la hoja
        HSSFRow fila3 = hoja1.createRow(2);
        HSSFCell celda13 = fila3.createCell((short)0);
        
        HSSFRichTextString titulo_reporte = new HSSFRichTextString(tituloReporte);
        celda13.setCellValue(titulo_reporte);
        
        hoja1.addMergedRegion(new CellRangeAddress(2,2,0,7));
        
        
        // Se crea una fila3 dentro de la hoja
        HSSFRow fila4 = hoja1.createRow(3);
        HSSFCell celda14 = fila4.createCell((short)0);
        
        HSSFRichTextString period = new HSSFRichTextString(periodo);
        celda14.setCellValue(period);
        
        hoja1.addMergedRegion(new CellRangeAddress(3,3,0,7));
        
        
        
        // Se salva el libro.
        try {
            FileOutputStream elFichero = new FileOutputStream(fileout);
            libro.write(elFichero);
            elFichero.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
