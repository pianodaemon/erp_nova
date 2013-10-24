/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.cfdi.adendas;

import java.util.LinkedHashMap;

/**
 *
 * @author Noe Martinez
 * gpmarsan@gmail.com
 * 22/octubre/2013
 * 
 */
public class AdendaCliente extends Adenda{
    @Override
    public void createAdenda(Integer noAdenda, LinkedHashMap<String, Object> dataAdenda, String dirXml, String fileNameXml) {
        AdendaFemsaQuimiproductos femsaQuimiproductos = new AdendaFemsaQuimiproductos();
        //Adenda lala = new AdendaLala();
        //Adenda soriana = new AdendaSoriana();
        //Adenda walmart = new AdendaWalmart();
        
        this.SetNext(femsaQuimiproductos);
        //femsa.SetNext(lala);
        //lala.SetNext(soriana);
        //soriana.SetNext(walmart);
        
        next.createAdenda(noAdenda, dataAdenda, dirXml, fileNameXml);
    }
}
