/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;

/**
 *
 * @author agnux
 */
public class InvListaProductosXls {
    
    public InvListaProductosXls(String fileout, ArrayList<HashMap<String, String>> datos) {
        
        // Se crea el libro
        HSSFWorkbook libro = new HSSFWorkbook();

        // Se crea una hoja dentro del libro
        HSSFSheet hoja1 = libro.createSheet("PRODUCTOS");
        
        /*
        hoja1.autoSizeColumn(0);
        hoja1.autoSizeColumn(1);
        hoja1.autoSizeColumn(2);
        hoja1.autoSizeColumn(3);
        hoja1.autoSizeColumn(4);
        hoja1.autoSizeColumn(5);
        hoja1.autoSizeColumn(6);
        hoja1.setDefaultColumnWidth(30);
        */
        hoja1.setDefaultColumnWidth(15);
        
        DataFormat format = libro.createDataFormat();
        boolean formatoNumerico=false;
        short sin_formato = format.getFormat("");
        int noRow=0;
        /*
        
        // Se crea una fila1 dentro de la hoja
        HSSFRow fila1 = hoja1.createRow(noRow);
        //HSSFCell celda11 = fila1.createCell((short)0);

        createCell(libro, fila1,(short) 0, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, razon_soc_empresa, Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);

        //Combinar celdas
        hoja1.addMergedRegion(new CellRangeAddress(
                noRow, //first row (0-based)
                noRow, //last row  (0-based)
                0, //first column (0-based)
                7  //last column  (0-based)
        ));


        // Se crea una fila2 dentro de la hoja
        noRow=1;
        HSSFRow fila2 = hoja1.createRow(noRow);
        createCell(libro, fila2,(short) 0, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, tituloReporte, Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,0,7));

        // Se crea una fila3 dentro de la hoja
        noRow=2;
        HSSFRow fila3 = hoja1.createRow(noRow);
        createCell(libro, fila3,(short) 0, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, periodo, Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,0,7));
        */
        
        // Se crea una fila5 dentro de la hoja
        //Aqui se crean los encabezados de las columnas
        noRow=0;
        HSSFRow fila5 = hoja1.createRow(noRow);
        createCell(libro, fila5,(short) 0, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "NO_PROD", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 1, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "CODIGO_PROD", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 2, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "DESCRIPCION_PROD", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 3, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "UNIDAD", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 4, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "TIPO_PROD", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 5, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "NO_ALMACEN", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 6, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "ALMACEN", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 7, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "FAMILIA", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 8, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "SUBFAMILIA", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 9, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "LINEA", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 10, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "EXISTENCIA", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        
        
        noRow=1;
        if(datos.size()>0){
            for (int x=0; x<=datos.size()-1;x++){
                HashMap<String,String> registro = datos.get(x);
                HSSFRow fila = hoja1.createRow(noRow);
                createCell(libro, fila,(short) 0, CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_CENTER, registro.get("id_prod"), Font.BOLDWEIGHT_NORMAL, formatoNumerico, sin_formato);
                createCell(libro, fila,(short) 1, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("codigo_producto"), Font.BOLDWEIGHT_NORMAL, formatoNumerico, sin_formato);
                createCell(libro, fila,(short) 2, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("descripcion_producto"), Font.BOLDWEIGHT_NORMAL, formatoNumerico, sin_formato);
                createCell(libro, fila,(short) 3, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("unidad"), Font.BOLDWEIGHT_NORMAL, formatoNumerico, sin_formato);
                createCell(libro, fila,(short) 4, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("tipo_producto"), Font.BOLDWEIGHT_NORMAL, formatoNumerico, sin_formato);
                createCell(libro, fila,(short) 5, CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_CENTER, registro.get("no_almacen"), Font.BOLDWEIGHT_NORMAL, formatoNumerico, sin_formato);
                createCell(libro, fila,(short) 6, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("almacen"), Font.BOLDWEIGHT_NORMAL, formatoNumerico, sin_formato);
                createCell(libro, fila,(short) 7, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("familia"), Font.BOLDWEIGHT_NORMAL, formatoNumerico, sin_formato);
                createCell(libro, fila,(short) 8, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("subfamilia"), Font.BOLDWEIGHT_NORMAL, formatoNumerico, sin_formato);
                createCell(libro, fila,(short) 9, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("linea"), Font.BOLDWEIGHT_NORMAL, formatoNumerico, sin_formato);
                createCell(libro, fila,(short) 10, CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_CENTER, registro.get("existencia"), Font.BOLDWEIGHT_NORMAL, formatoNumerico, sin_formato);
                
                //System.out.println(registro.get("codigo_producto") +" => "+registro.get("existencia"));
                noRow++;
            }
        }

        // Se salva el libro.
        try {
            FileOutputStream elFichero = new FileOutputStream(fileout);
            libro.write(elFichero);
            elFichero.close();
        } catch (Exception e) {
            e.printStackTrace();
        }   

        
    }
    
    /**
     * Creates a cell and aligns it a certain way.
     */
    private static void createCell(HSSFWorkbook wb, HSSFRow row, short column, short halign, short valign, String value, short stylefont, boolean numeric, short formato) {
        Cell cell = row.createCell(column);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(halign);
        cellStyle.setVerticalAlignment(valign);
        
        Font font = wb.createFont();
        font.setBoldweight(stylefont);
        cellStyle.setFont(font);
        if(numeric){
            cell.setCellValue(Float.parseFloat(value));
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            cellStyle.setDataFormat(formato);
        }else{
            HSSFRichTextString cellValue = new HSSFRichTextString(value);
            cell.setCellValue(cellValue);
        }

        cell.setCellStyle(cellStyle);
    }

}
