/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.n2t;
import com.itextpdf.text.BaseColor;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.commons.lang.StringEscapeUtils;
/**
 *
 * @author paco mora
 */
public class pdfOrdenSubensamble {
    //--variables para pdf--
    private String imagen;
    
    private String telefono;
    //----------------------
    
    public pdfOrdenSubensamble(HashMap<String, String> datos_empresa, HashMap<String, String> datos_entrada, ArrayList<HashMap<String, String>> lista_productos, ArrayList<HashMap<String, String>> lista_componentes, String fileout, String ruta_imagen) throws URISyntaxException {
        
        Font smallsmall = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        
        ImagenPDF ipdf = new ImagenPDF();
        CeldaPDF cepdf = new CeldaPDF();
        TablaPDF tpdf = new TablaPDF();
        
        this.setImagen(ruta_imagen);
        //this.setImagen(Ri);
        
        //this.setTelefono(telefono);
        PdfPTable table;
        PdfPTable table2;
        PdfPCell cell;
        String cadena;
        
        
        
        try {
            Document document = new Document(PageSize.LETTER, -50, -50, 20, 30);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileout));
            document.open();
            
            float [] widths = {6,12,6};
            table = new PdfPTable(widths);
            table.setKeepTogether(false);
            
            //IMAGEN --> logo empresa
            cell = new PdfPCell(ipdf.addContent());
            cell.setBorder(0);
            //cell.setRowspan(10);
            cell.setRowspan(9);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            
            //RAZON SOCIAL --> BeanFromCFD (X_emisor)
            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(datos_empresa.get("emp_razon_social")),largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            ////////////////////////////////////////////////////////////////////////////////
            String tipo_documento = "ORDEN SUBENSAMBLE";
            /*
            if(datEmp.getSerie().equals("B")){
                documento = "NOTA DE CREDITO";
            }
            if(datEmp.getSerie().equals("C")){
                documento = "NOTA DE CARGO";
            }
            */
            
            //metodo para agregar las celdas serie y folio, etc.
            //respectivo contenido --> BeanFromCFD (serie..., _domicilio_fiscal)
            
            /*decomentar ahorita*/
            cadena = tipo_documento + "&" + 
                    datos_entrada.get("folio") + "&" + 
                    StringHelper.capitalizaString(datos_empresa.get("emp_municipio")) + ", " + StringHelper.capitalizaString(datos_empresa.get("emp_estado")) + "\n" + datos_entrada.get("fecha");
                    
            
            cell = new PdfPCell(cepdf.addContent(cadena));
            cell.setBorder(0);
            //cell.setRowspan(13);
            cell.setRowspan(12);
            table.addCell(cell);
            
            
            
            
////////////////////////////////////////////////////////////////////////////////
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            table.addCell(cell);
            
            //DOMICILIO FISCAL --> texto
            cell = new PdfPCell(new Paragraph("DOMICILIO FISCAL", smallBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            /*
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(1);
            table.addCell(cell);
            */
            
            //DOMICILIO FISCAL --> BeanFromCFD (X_emisor, X_domicilio_fiscal)
            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(datos_empresa.get("emp_calle")) + " " + StringHelper.capitalizaString(datos_empresa.get("emp_no_exterior")) +  "\n" + StringHelper.capitalizaString(datos_empresa.get("emp_colonia")) + "\n" + StringHelper.capitalizaString(datos_empresa.get("emp_municipio")) + ", " + StringHelper.capitalizaString(datos_empresa.get("emp_estado"))+ ", " + StringHelper.capitalizaString(datos_empresa.get("emp_pais")) + "\nC.P. " + datos_empresa.get("emp_cp") + "    R.F.C.: " + StringHelper.capitalizaString(datos_empresa.get("emp_rfc")), smallFont));
            cell.setBorder(0);
            cell.setRowspan(6);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            /* //por mi
            PdfPTable tableHelper = new PdfPTable(1);
            
            //CLIENTE --> texto
            cell = new PdfPCell(new Paragraph("PROVEEDOR",smallBoldFont));
            cell.setBorder(0);
            cell.setRightIndent(10);
            tableHelper.addCell(cell);
            */
            /*
            //SUCURSAL --> texto
            cell = new PdfPCell(new Paragraph("EXPEDIDO EN",smallBoldFont));
            cell.setBorder(1);
            cell.setRightIndent(10);
            tableHelper.addCell(cell);
            */
            //DATOS CLIENTE --> BeanFromCFD (X_receptor, X_domicilio)
            
            //String datosCliente = StringHelper.capitalizaString(datEmp.getRazon_social_receptor());
            //String datosProveedor = StringHelper.capitalizaString(datos_proveedor.get("prov_razon_social"));//por mi
            
            /*
            if(sucursal == null ? "null" != null : !sucursal.equals("null") && (sucursal == null ? "" != null : !sucursal.equals(""))){
             datosCliente+="\n"+StringHelper.capitalizaString(sucursal);
            }
            */
            
            
            /* //por mi
            datosProveedor+=" \n"+StringHelper.capitalizaString(datos_proveedor.get("prov_calle")) +" "+ datos_proveedor.get("prov_numero") + ", " + StringHelper.capitalizaString(datos_proveedor.get("prov_colonia"))+ ", " + StringHelper.capitalizaString(datos_proveedor.get("prov_municipio")) + ", " + StringHelper.capitalizaString(datos_proveedor.get("prov_estado")) + ", " + StringHelper.capitalizaString(datos_proveedor.get("prov_pais")) + " \nC.P. " + datos_proveedor.get("prov_cp") + "     TEL. "+ datos_proveedor.get("prov_telefono") +  "\nR.F.C.: " + StringHelper.capitalizaString(datos_proveedor.get("prov_rfc"));
            
            
            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(datosProveedor), smallFont));
            cell.setBorder(0);
            cell.setRightIndent(10);
            cell.setFixedHeight(35);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("CONTACTO",smallBoldFont));
            cell.setBorder(0);
            cell.setRightIndent(10);
            tableHelper.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(datos_proveedor.get("prov_contacto")),smallFont));
            cell.setBorder(0);
            cell.setRightIndent(10);
            tableHelper.addCell(cell);
            */
//            String datosCliente = StringHelper.capitalizaString(datEmp.getRazon_social_receptor())+ "\n"+ StringHelper.capitalizaString(datEmp.getCalle_domicilio()) +" "+datEmp.getNoExterior_domicilio() + "\n" + StringHelper.capitalizaString(datEmp.getColonia_domicilio())+ "\n" + StringHelper.capitalizaString(esteAtributoSeDejoNulo(datEmp.getMunicipio_domicilio())) + ", " + StringHelper.capitalizaString(datEmp.getEstado_domicilio()) + "\nC.P. " + datEmp.getCodigoPostal_domicilio() + "   TEL. " +  "\nR.F.C.: " + StringHelper.capitalizaString(datEmp.getRfc_receptor());
//            if(sucursal == null ? "null" != null : !sucursal.equals("null") && (sucursal == null ? "" != null : !sucursal.equals(""))){
//                datosCliente+="\nSUC. "+StringHelper.capitalizaString(sucursal);
//            }
//            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(datosCliente), smallFont));
//            cell.setBorder(0);
//            cell.setRightIndent(10);
//            tableHelper.addCell(cell);

            /*
            //DATOS SUCURSAL --> BeanFromCFD (X_emisor, X_expedido_en)
            //cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(datEmp.getRazon_social_emisor()) + "\n" + StringHelper.capitalizaString(datEmp.getCalle_expedido_en()) + " " + datEmp.getNoExterior_expedido_en() + "\n" + StringHelper.capitalizaString(datEmp.getColonia_expedido_en()) + "\n" + StringHelper.capitalizaString(datEmp.getMunicipio_expedido_en()) + ", " + StringHelper.capitalizaString(datEmp.getEstado_expedido_en()) + "\nC.P. " + datEmp.getCp_expedido_en() + "   TEL.: " + getTelefono() + "\nR.F.C.: " + StringHelper.capitalizaString(datEmp.getRfc_emisor()),smallFont));
            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString("Razon emisor") + "\n" + StringHelper.capitalizaString("Expedido en ") + " " + "20" + "\n" + StringHelper.capitalizaString("Colonia expedido en") + "\n" + StringHelper.capitalizaString("Municipio expedido en") + ", " + StringHelper.capitalizaString("estado expedido en") + "\nC.P. " + "67180" + "   TEL.: " + "8347447474" + "\nR.F.C.: " + StringHelper.capitalizaString("rfc emisor"),smallFont));
            cell.setBorder(1);
            cell.setRightIndent(10);
            tableHelper.addCell(cell);
           */
            
            
            /*//por mi
            cell = new PdfPCell(tableHelper);
            cell.setBorder(0);
            cell.setColspan(2);
            cell.setRowspan(3);
            table.addCell(cell);
            
            table.setSpacingAfter(10f);
             
             */
            //document.addHeader("s", table.toString());
            document.add(table);
            
////////////////////////////////////////////////////////////////////////////////
            //metodo para agregar lista conceptos --> BeanFromCFD
            /*descomentar tambie ahorita*/
            document.add(tpdf.addContent(datos_entrada, lista_productos, lista_componentes));
            //document.add(tpdf.addContent(daoEntradas.getListaConceptos(), daoEntradas.getSubtotal(), daoEntradas.getImpuesto(), daoEntradas.getTotal(), daoEntradas.getMoneda_id(), daoEntradas.getMoneda()));
            //(ArrayList<LinkedHashMap<String,String>> conceptos, String subTotal,String impuesto, String total, String moneda)
////////////////////////////////////////////////////////////////////////////////
            
                    
            table2 = new PdfPTable(1);
            table2.setKeepTogether(true);
            
            /*
            if(observaciones == null ? "null" != null : !observaciones.equals("null") && (observaciones == null ? "" != null : !observaciones.equals(""))){
                 //obseravciones --> texto
                cell = new PdfPCell(new Paragraph("OBSERVACIONES:",smallBoldFont));
                cell.setBorder(0);
                table2.addCell(cell);
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(observaciones),smallFont));
                
                //observacions contenido
                table2.addCell(cell);
                table2.setSpacingAfter(10f);
                document.add(table2);
                
                table2 = new PdfPTable(1);
                table2.setKeepTogether(true);
            }
            
            */
            
            
            
            //CADENA ORIGINAL --> texto
            //cell = new PdfPCell(new Paragraph("CADENA ORIGINAL:",smallBoldFont));
            
            if (datos_entrada.get("comentarios").isEmpty()){
                cell = new PdfPCell(new Paragraph("",smallBoldFont));
                cell.setBorder(0);
                table2.addCell(cell);
                
                //CADENA ORIGINAL --> BeanFromCFD (getCadenaOriginal)
                
                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setBorder(0);
                table2.addCell(cell);
            }else{
                cell = new PdfPCell(new Paragraph("OBSERVACIONES:",smallBoldFont));
                cell.setBorder(0);
                table2.addCell(cell);
                
                //CADENA ORIGINAL --> BeanFromCFD (getCadenaOriginal)
                
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(datos_entrada.get("comentarios")), smallFont));
                cell.setBorder(0);
                table2.addCell(cell); 
            }
            
            table2.setSpacingAfter(10f);
            document.add(table2);
            
            table2 = new PdfPTable(1);
            table2.setKeepTogether(true);
            
            /*
            //SELLO DIGITAL --> texto
            cell = new PdfPCell(new Paragraph("SELLO DIGITAL:",smallBoldFont));
            cell.setBorder(0);
            table2.addCell(cell);
            
            //SELLO DIGITAL --> BeanFromCFD (getSelloDigital)
            cell = new PdfPCell(new Paragraph("Este es el sello digital", smallsmall));
            table2.addCell(cell);
            */
            
            
            document.add(table2);
            
            document.close();
            
            PdfReader reader = new PdfReader(fileout);
            
            PdfStamper stamper = new PdfStamper(reader,new FileOutputStream(fileout+".pdf"));
            PdfContentByte over;
            int total = reader.getNumberOfPages() + 1;
            for (int i=1; i<total; i++) {
                over = stamper.getOverContent(i);
                PdfTemplate f = over.createAppearance(500,200);
                f.setBoundingBox(table.getDefaultCell());
                over.addTemplate(f, 70,770);
                over.beginText();
                over.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 7);
                over.showTextAligned(PdfContentByte.ALIGN_CENTER, "PAG. " + i + " DE " + (total-1), 570, 773, 0);
                over.endText();
                
                /*
                //cadena = datEmp.getCondicionesDePago();
                cadena = "Estas son las condiciones de pago";
                
                if(cadena.length()>100){
                    String cadenasub = cadena.substring(0, 99) + "...";
                    over.beginText();
                    over.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 7);
                    over.showTextAligned(PdfContentByte.ALIGN_CENTER, StringHelper.capitalizaString(cadenasub), 300, 25, 0);
                    over.endText();
                }
                else{
                    over.beginText();
                    over.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 7);
                    over.showTextAligned(PdfContentByte.ALIGN_CENTER, StringHelper.capitalizaString(cadena), 300, 25, 0);
                    over.endText();
                }
                over.beginText();
                over.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 7);
                over.showTextAligned(PdfContentByte.ALIGN_CENTER, "ESTE DOCUMENTO ES UNA REPRESENTACIÃ“N IMPRESA DE UN CFD", 300, 15, 0);
                //over.showTextAligned(PdfContentByte.ALIGN_CENTER, "ESTE DOCUMENTO ES UNA IMPRESIÃ“N DE UN COMPROBANTE FISCAL DIGITAL", 300, 15, 0);
                over.endText();

                over.beginText();
                over.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 7);
                over.showTextAligned(PdfContentByte.ALIGN_CENTER, "FC-FS-7.2-09 REV1", 560, 15, 0);
                over.endText();
                */
                
                
            }
            stamper.close();
            reader.close();
            
            
            
        }
        catch (Exception e) {
                e.printStackTrace();
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    public void setImagen(String imagen) {
    	this.imagen = imagen;
    }
    
    public String getImagen() {
    	return imagen;
    }
    
    public void setTelefono(String telefono) {
    	this.telefono = telefono;
    }
    
    public String getTelefono() {
    	return telefono;
    }
    
    
    private class ImagenPDF {
        public Image addContent() {
            Image img = null;
            try {
                img = Image.getInstance(getImagen());
                //img.scaleAbsoluteHeight(100);
                img.scaleAbsoluteHeight(65);
                img.scaleAbsoluteWidth(120);
                img.setAlignment(0);
            }
            catch(Exception e){
                System.out.println(e);
            }
            return img;
        }
    }
    
    
    
    
    
    private class CeldaPDF {
        public PdfPTable addContent(String cadena) {
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font sont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
            Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
            Font smallFontCancelado = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.RED);
            
            
            String [] temp = cadena.split("&");
            PdfPTable table = new PdfPTable(1);
            PdfPCell cell;
            //System.out.println("Esta es la cadena: "+cadena);
            
            cell = new PdfPCell(new Paragraph(temp[0],largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("FOLIO",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(temp[1],sont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            
            
            
            
            
            
            
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            table.addCell(cell);
            /*
            cell = new PdfPCell(new Paragraph("LUGAR Y FECHA",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(temp[2],smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            */
            return table;
        }
    }
    
    
    
    private class TablaPDF {
        //document.add(tpdf.addContent(                     datEmp.getListaConceptos(),                     datEmp.getListaRetenciones(),                        datEmp.getListaTraslados(),    datEmp.getSubTotal(),   datEmp.getTotal(),  datEmp.getDescuento(),  datEmp.getTotalImpuestosRetenidos(),datEmp.getTotalImpuestosTrasladados(),  datEmp.ExistenRetenciones(),datEmp.ExistenTraslados(), generar_en));
        public PdfPTable addContent(HashMap<String, String> datos_entrada,ArrayList<HashMap<String, String>> conceptos, ArrayList<HashMap<String, String>> componentes) {
            
            //suma_cantidad();
            Font small = new Font(Font.FontFamily.COURIER,6,Font.NORMAL,BaseColor.BLACK);
            //Font smallFont = new Font(Font.FontFamily.COURIER,8,Font.NORMAL,BaseColor.BLACK);
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
            Font smallBoldFont1 = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
            Font smallBoldFontBlack = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
            Font largeFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
            
            
            //float [] widths = {2f, 5.5f, 3f, 2f, 1.5f, 2f,2f,2f,2f};
            float [] widths = {3.5f, 7.5f, 4.5f,4f};
            PdfPTable table = new PdfPTable(widths);
            PdfPCell cell;

            Iterator it;

            table.setKeepTogether(false);
            table.setHeaderRows(1);
            
            
            cell = new PdfPCell(new Paragraph("Subensambles",largeFont));
            cell.setColspan(4);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthBottom((float) 0.5);
            cell.setBorderWidthBottom(0);
            cell.setFixedHeight(20);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("CODIGO",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("DESCRIPCIÓN",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("UNIDAD",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("CANT.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            
            it = conceptos.iterator();
            while(it.hasNext()){
            //for(  HashMap<String, Object> i : this.getConceptos()){
                HashMap<String,String> map = (HashMap<String,String>)it.next();
                float a = 0;
                float b = 0;
                if (map.get("sku") != null){
                    a = 0;
                    b = (float) 0.5;
                }
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("sku")), smallFont));
                cell.setIndent(3);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidthBottom(a);
                cell.setBorderWidthTop(b);
                table.addCell(cell);
                
                if(map.get("descripcion") !=null){
                    String descripcion = map.get("descripcion");
                    descripcion =  StringEscapeUtils.unescapeHtml(descripcion);
                    /*
                    String[] valor = descripcion.split("::");
                    if(valor.length > 1){
                        descripcion = valor[0] + "\nSerie: " + valor[1];
                    } 
                     */
                    //descripcion =  StringEscapeUtils.unescapeHtml(descripcion);
                    cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(descripcion), smallFont));
                    cell.setIndent(3);
                    cell.setUseDescender(true);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setBorderWidthBottom(a);
                    cell.setBorderWidthTop(b);
                    table.addCell(cell);
                }
                
                
                //UNIDAD
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("utitulo"))), smallFont));
                cell.setIndent(3);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidthBottom(a);
                cell.setBorderWidthTop(b);
                table.addCell(cell);
                
                
                //CANTIDAD
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("cantidad")), smallFont));
                cell.setRightIndent(3);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidthBottom(a);
                cell.setBorderWidthTop(b);
                table.addCell(cell);
                
                
                
            }
            
            //System.out.println("conceptos.size:"+conceptos.size());
            
            cell = new PdfPCell(new Paragraph("Productos materia prima",largeFont));
            cell.setColspan(4);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthBottom((float) 0.5);
            cell.setBorderWidthBottom(0);
            cell.setFixedHeight(20);
            table.addCell(cell);
            
            
            
            cell = new PdfPCell(new Paragraph("CODIGO",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("DESCRIPCIÓN",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("UNIDAD",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("CANT.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            
            
            
            it = componentes.iterator();
            while(it.hasNext()){
            //for(  HashMap<String, Object> i : this.getConceptos()){
                HashMap<String,String> map = (HashMap<String,String>)it.next();
                float a = 0;
                float b = 0;
                if (map.get("sku") != null){
                    a = 0;
                    b = (float) 0.5;
                }
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("sku")), smallFont));
                cell.setIndent(3);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidthBottom(a);
                cell.setBorderWidthTop(b);
                table.addCell(cell);
                
                if(map.get("descripcion") !=null){
                    String descripcion = map.get("descripcion");
                    descripcion =  StringEscapeUtils.unescapeHtml(descripcion);
                    /*
                    String[] valor = descripcion.split("::");
                    if(valor.length > 1){
                        descripcion = valor[0] + "\nSerie: " + valor[1];
                    } 
                     */
                    //descripcion =  StringEscapeUtils.unescapeHtml(descripcion);
                    cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(descripcion), smallFont));
                    cell.setIndent(3);
                    cell.setUseDescender(true);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setBorderWidthBottom(a);
                    cell.setBorderWidthTop(b);
                    table.addCell(cell);
                }
                
                
                //UNIDAD
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("utitulo"))), smallFont));
                cell.setIndent(3);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidthBottom(a);
                cell.setBorderWidthTop(b);
                table.addCell(cell);
                
                
                //CANTIDAD
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("cantidad")), smallFont));
                cell.setRightIndent(3);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidthBottom(a);
                cell.setBorderWidthTop(b);
                table.addCell(cell);
                
                
                
            }
            
            
            
            
            
            
            cell = new PdfPCell(new Paragraph("", smallBoldFontBlack));
            cell.setColspan(4);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setMinimumHeight(17);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBorderWidthTop((float) 0.5);
            table.addCell(cell);
            
            
            table.setSpacingAfter(10);
            
            
            return table;
        }
        
    }
    
    
    
    
    public String esteAtributoSeDejoNulo(String atributo){
         return (atributo != null) ? (atributo) : new String();
    }

}
