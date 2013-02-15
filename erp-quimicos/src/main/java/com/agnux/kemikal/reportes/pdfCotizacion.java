/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.interfacedaos.CotizacionesInterfaceDao;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author agnux
 */
public class pdfCotizacion {
    private HashMap<String, String> datosHeaderFooter = new HashMap<String, String>();
    private ArrayList<HashMap<String, String>> lista_productos = new ArrayList<HashMap<String, String>>();
    private String ruta_logo;
    private String file_out;
    private String dirImagenes;
    private String tipo;
    private String folio;
    private String tipoCambio;
    private String observaciones;

    private String emisorCalle;
    private String emisorNumero;
    private String emisorColonia;
    private String emisorMunicipio;
    private String emisorEstado;
    private String emisorPais;
    private String emisorCp;
    private String clieCalle;
    private String clieNumero;
    private String clieColonia;
    private String clieMunicipio;
    private String clieEstado;
    private String cliePais;
    private String clieCp;
    private String clieTel;
    private String clieRfc;
    private String clieContacto;
    
    //-----Ccostructor para hacer seters
    public pdfCotizacion(HashMap<String, String> HeaderFooter, HashMap<String, String> datosEmisor, HashMap<String, String> datos, HashMap<String, String> datosCliente, ArrayList<HashMap<String, String>> lista_productos) throws URISyntaxException {
        
    }//termina Constructor pdfCotizacion
    
    
        
        
    public String getClieCalle() {
        return clieCalle;
    }

    public void setClieCalle(String clieCalle) {
        this.clieCalle = clieCalle;
    }

    public String getClieColonia() {
        return clieColonia;
    }

    public void setClieColonia(String clieColonia) {
        this.clieColonia = clieColonia;
    }

    public String getClieContacto() {
        return clieContacto;
    }

    public void setClieContacto(String clieContacto) {
        this.clieContacto = clieContacto;
    }

    public String getClieCp() {
        return clieCp;
    }

    public void setClieCp(String clieCp) {
        this.clieCp = clieCp;
    }

    public String getClieEstado() {
        return clieEstado;
    }

    public void setClieEstado(String clieEstado) {
        this.clieEstado = clieEstado;
    }

    public String getClieMunicipio() {
        return clieMunicipio;
    }

    public void setClieMunicipio(String clieMunicipio) {
        this.clieMunicipio = clieMunicipio;
    }

    public String getClieNumero() {
        return clieNumero;
    }

    public void setClieNumero(String clieNumero) {
        this.clieNumero = clieNumero;
    }

    public String getCliePais() {
        return cliePais;
    }

    public void setCliePais(String cliePais) {
        this.cliePais = cliePais;
    }

    public String getClieRfc() {
        return clieRfc;
    }

    public void setClieRfc(String clieRfc) {
        this.clieRfc = clieRfc;
    }

    public String getClieTel() {
        return clieTel;
    }

    public void setClieTel(String clieTel) {
        this.clieTel = clieTel;
    }

    public HashMap<String, String> getDatosHeaderFooter() {
        return datosHeaderFooter;
    }

    public void setDatosHeaderFooter(HashMap<String, String> datosHeaderFooter) {
        this.datosHeaderFooter = datosHeaderFooter;
    }

    public String getDirImagenes() {
        return dirImagenes;
    }

    public void setDirImagenes(String dirImagenes) {
        this.dirImagenes = dirImagenes;
    }

    public String getEmisorCalle() {
        return emisorCalle;
    }

    public void setEmisorCalle(String emisorCalle) {
        this.emisorCalle = emisorCalle;
    }

    public String getEmisorColonia() {
        return emisorColonia;
    }

    public void setEmisorColonia(String emisorColonia) {
        this.emisorColonia = emisorColonia;
    }

    public String getEmisorCp() {
        return emisorCp;
    }

    public void setEmisorCp(String emisorCp) {
        this.emisorCp = emisorCp;
    }

    public String getEmisorEstado() {
        return emisorEstado;
    }

    public void setEmisorEstado(String emisorEstado) {
        this.emisorEstado = emisorEstado;
    }

    public String getEmisorMunicipio() {
        return emisorMunicipio;
    }

    public void setEmisorMunicipio(String emisorMunicipio) {
        this.emisorMunicipio = emisorMunicipio;
    }

    public String getEmisorNumero() {
        return emisorNumero;
    }

    public void setEmisorNumero(String emisorNumero) {
        this.emisorNumero = emisorNumero;
    }

    public String getEmisorPais() {
        return emisorPais;
    }

    public void setEmisorPais(String emisorPais) {
        this.emisorPais = emisorPais;
    }

    public String getFile_out() {
        return file_out;
    }

    public void setFile_out(String file_out) {
        this.file_out = file_out;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public ArrayList<HashMap<String, String>> getLista_productos() {
        return lista_productos;
    }

    public void setLista_productos(ArrayList<HashMap<String, String>> lista_productos) {
        this.lista_productos = lista_productos;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getRuta_logo() {
        return ruta_logo;
    }

    public void setRuta_logo(String ruta_logo) {
        this.ruta_logo = ruta_logo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
    }
        
}