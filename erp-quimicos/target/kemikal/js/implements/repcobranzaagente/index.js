$(function() {
    var config =  {
		tituloApp: 'Cobranza por Agente' ,                 
		contextpath : $('#lienzo_recalculable').find('input[name=contextpath]').val(),

		userName : $('#lienzo_recalculable').find('input[name=user]').val(),
		ui : $('#lienzo_recalculable').find('input[name=iu]').val(),

		getUrlForGetAndPost : function(){
			var url = document.location.protocol + '//' + document.location.host + this.getController();
			return url;
		},

		getUserName: function(){
			return this.userName;
		},

		getUi: function(){
			return this.ui;
		},
		getTituloApp: function(){
			return this.tituloApp;
		},

		getController: function(){
			return this.contextpath + "/controllers/repcobranzaagente";
			//  return this.controller;
		}
    };
    
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	$('#barra_acciones').hide();
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append(config.getTituloApp());
	
	//barra para el buscador 
	$('#barra_buscador').hide();
  
  
	var $select_opciones = $('#lienzo_recalculable').find('select[name=opciones]');
	var $select_agente = $('#lienzo_recalculable').find('select[name=select_agente]');
	var $fecha_inicial = $('#lienzo_recalculable').find('input[name=fecha_inicial]');
	var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');
	$fecha_inicial.attr("readonly", true);
	$fecha_final.attr("readonly", true);
	var $genera_reporte_CobranzaAgente = $('#lienzo_recalculable').find('div.repcobranzaagente').find('table#fechas tr td').find('input[value$=Generar_PDF]');
	var $Buscar_CobranzaAgente= $('#lienzo_recalculable').find('div.repcobranzaagente').find('table#fechas tr td').find('input[value$=Buscar]');
	var $div_cobranza_agente= $('#cobranzaagente');

	$select_opciones.children().remove();
	//var Cob_Agnt_hmtl = '<option  value="0" selected="yes">[--Selecciona una Opcion--]</option>';
	var Cob_Agnt_hmtl = '';
	Cob_Agnt_hmtl += '<option value="1">Cobranza por Agente</option>';
	Cob_Agnt_hmtl += '<option value="2">Ventas por Agente</option>'
	$select_opciones.append(Cob_Agnt_hmtl);
	
	
	
	var arreglo_parametros = { 
		iu:config.getUi()
	 };
	var restful_json_service = config.getUrlForGetAndPost() + '/getBuscaDatos.json';
	$.post(restful_json_service,arreglo_parametros,function(entry){
		//cargar select de agentes
		$select_agente.children().remove();
		var agente_hmtl = '<option value= "0" >[--Todos--]</option>';
		$.each(entry['Agentes'],function(entryIndex,data){
			agente_hmtl +='<option value= "' + data['id'] + '" >' + data['nombre_agente'] + '</option>';
		});
		$select_agente.append(agente_hmtl);	
		
	});
	
	
        
	//valida la fecha seleccionada
	function mayor(fecha, fecha2){
		var xMes=fecha.substring(5, 7);
		var xDia=fecha.substring(8, 10);
		var xAnio=fecha.substring(0,4);
		var yMes=fecha2.substring(5, 7);
		var yDia=fecha2.substring(8, 10);
		var yAnio=fecha2.substring(0,4);
		
		if (xAnio > yAnio){
			return(true);
		}else{
			if (xAnio == yAnio){
				if (xMes > yMes){
					return(true);
				}
				if (xMes == yMes){
					if (xDia > yDia){
						return(true);
					}else{
						return(false);
					}
				}else{
					return(false);
				}
			}else{
				return(false);
			}
		}
	}
	//muestra la fecha actual
	var mostrarFecha = function mostrarFecha(){
		var ahora = new Date();
		var anoActual = ahora.getFullYear();
		var mesActual = ahora.getMonth();
		mesActual = mesActual+1;
		mesActual = (mesActual <= 9)?"0" + mesActual : mesActual;
		var diaActual = ahora.getDate();
		diaActual = (diaActual <= 9)?"0" + diaActual : diaActual;
		var Fecha = anoActual + "-" + mesActual + "-" + diaActual;		
		return Fecha;
	}
	//----------------------------------------------------------------
        
        
	
	$fecha_inicial.DatePicker({
		format:'Y-m-d',
		date: $(this).val(),
		current: $(this).val(),
		starts: 1,
		position: 'bottom',
		locale: {
			days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado','Domingo'],
			daysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vir', 'Sab','Dom'],
			daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa','Do'],
			months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo','Junio', 'Julio', 'Agosto', 'Septiembre','Octubre', 'Noviembre', 'Diciembre'],
			monthsShort: ['Ene', 'Feb', 'Mar', 'Abr','May', 'Jun', 'Jul', 'Ago','Sep', 'Oct', 'Nov', 'Dic'],
			weekMin: 'se'
		},
		onChange: function(formated, dates){
			var patron = new RegExp("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");
			$fecha_inicial.val(formated);
			if (formated.match(patron) ){
				var valida_fecha=mayor($fecha_inicial.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$fecha_inicial.val(mostrarFecha());
				}else{
					$fecha_inicial.DatePickerHide();	
				}
			}
		}
	});
        
        $fecha_inicial.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
        
        $fecha_inicial.val(mostrarFecha());
        //fecha final
        
        
        $fecha_final.DatePicker({
		format:'Y-m-d',
		date: $(this).val(),
		current: $(this).val(),
		starts: 1,
		position: 'bottom',
		locale: {
			days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado','Domingo'],
			daysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vir', 'Sab','Dom'],
			daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa','Do'],
			months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo','Junio', 'Julio', 'Agosto', 'Septiembre','Octubre', 'Noviembre', 'Diciembre'],
			monthsShort: ['Ene', 'Feb', 'Mar', 'Abr','May', 'Jun', 'Jul', 'Ago','Sep', 'Oct', 'Nov', 'Dic'],
			weekMin: 'se'
		},
		onChange: function(formated, dates){
			var patron = new RegExp("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");
			$fecha_inicial.val(formated);
			if (formated.match(patron) ){
				var valida_fecha=mayor($fecha_final.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$fecha_final.val(mostrarFecha());
				}else{
					$fecha_final.DatePickerHide();	
				}
			}
		}
	});
        
        
        
        $fecha_final.DatePicker({
		format:'Y-m-d',
		date: $(this).val(),
		current: $(this).val(),
		starts: 1,
		position: 'bottom',
		locale: {
			days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado','Domingo'],
			daysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vir', 'Sab','Dom'],
			daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa','Do'],
			months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo','Junio', 'Julio', 'Agosto', 'Septiembre','Octubre', 'Noviembre', 'Diciembre'],
			monthsShort: ['Ene', 'Feb', 'Mar', 'Abr','May', 'Jun', 'Jul', 'Ago','Sep', 'Oct', 'Nov', 'Dic'],
			weekMin: 'se'
		},
		onChange: function(formated, dates){
			var patron = new RegExp("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");
			$fecha_final.val(formated);
			if (formated.match(patron) ){
				var valida_fecha=mayor($fecha_final.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$fecha_final.val(mostrarFecha());      
				}else{
					$fecha_final.DatePickerHide();	
				}
			}
		}
	});
        
	$fecha_final.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
	
	
	$fecha_final.val(mostrarFecha());
        
        
        //click generar reporte de pronostico de Cobranza
        $genera_reporte_CobranzaAgente.click(function(event){
			event.preventDefault();
			var fecha_inicial = $fecha_inicial.val();
			var fecha_final = $fecha_final.val();
			var opcion_seleccionada = $select_opciones.val();
			var id_agente=$select_agente.val();
			var tipo_rep="";
			
			if ($select_opciones.val()==1){
				tipo_rep=" Cobranza  Por Agente"
			}else{
				tipo_rep=" ventas por  Agente"
			} 
			var input_json = config.getUrlForGetAndPost() +'/reporte_cobranza_venta_agente/' +opcion_seleccionada+'/'+id_agente+'/'+fecha_inicial+'/'+fecha_final+'/'+config.getUi()+'/out.json';
			window.location.href=input_json;
        });
        
        
		
                
       $Buscar_CobranzaAgente.click(function(event){
        event.preventDefault();
        
          //if ($select_opciones.val()!=0){
               if ($select_opciones.val()==1){
                    $div_cobranza_agente.children().remove();
                    var fecha_inicial = $fecha_inicial.val();
                    var fecha_final = $fecha_final.val();
                    if(fecha_inicial != "" && fecha_final != ""){ 
                    
                    var arreglo_parametros = {fecha_inicial:$fecha_inicial.val(), fecha_final:$fecha_final.val(), id_agente:$select_agente.val(), iu:config.getUi()};

                    var restful_json_service = config.getUrlForGetAndPost() + '/getCobranzaAgente.json'
                    var cliente="";
                    $.post(restful_json_service,arreglo_parametros,function(entry){
                    var body_tabla = entry;

                        var header_tabla = {
                                            //numero:'Numero',
                                            nombre  	:'Nombre',
                                            factura    	:"Factura",
                                            fecha_factura   :"Fecha&nbsp;Factura",
                                            cliente  	:'Cliente',
                                            fecha_pago	:"Fecha&nbsp;Pago",
                                            Dias 		:"Numero&nbsp;Dias",
                                            moneda  	:'',
                                            valor    	:'Subtotal',
                                            sesentadias  :'Comision',
                                            moneda2  	:'',
                                            noventadias  :'Total&nbsp;Comision'

                        };
                    var TPventa_neta=0.0;
                    var Sumatoriaventa_neta = 0.0;
                    var sumatotoriaporciento = 0.0;
                    var html_ventasnetas = '<table id="ventas" >';
                    var porcentaje = 0.0;
                    
                    
                    var numero_control=0.0; 
                    var cliente=0.0; 
                    var moneda="$"; 
                    var venta_neta=0.0; 
                    var porciento=0.0;
                    var tmp= 0;
                    
                    html_ventasnetas +='<thead> <tr>';
                        for(var key in header_tabla){
                            var attrValue = header_tabla[key];
                            /*if(attrValue == "Numero"){
                                html_ventasnetas +='<td  align="left">'+attrValue+'</td>'; 
                            }*/
                            if(attrValue == "Nombre"){
                                html_ventasnetas +='<td  align="left" width="250">'+attrValue+'</td>'; 
                            }
                            
                            if(attrValue == "Factura"){
                                html_ventasnetas +='<td  align="center">'+attrValue+'</td>'; 
                            }
                            if(attrValue == "Fecha&nbsp;Factura"){
                                html_ventasnetas +='<td  align="center">'+attrValue+'</td>'; 
                            }
                            if(attrValue == "Cliente"){
                                html_ventasnetas +='<td  align="left" width="480">'+attrValue+'</td>'; 
                            }
                            if(attrValue == "Fecha&nbsp;Pago"){
                                html_ventasnetas +='<td  align="center">'+attrValue+'</td>'; 
                            }
                            if(attrValue == "Numero&nbsp;Dias"){
                                html_ventasnetas +='<td  align="center">'+attrValue+'</td>'; 
                            }
                            
                            if(attrValue == ""){
                                html_ventasnetas +='<td  align="right" >'+attrValue+'</td>'; 
                            }
                            
                            if(attrValue == "Subtotal"){
                                html_ventasnetas +='<td  align="right" >'+attrValue+'</td>'; 
                            }
                            
                            if(attrValue == "Comision"){
                                html_ventasnetas +='<td  align="right"   >'+attrValue+'</td>'; 
                            }
                            
                            if(attrValue == "Total&nbsp;Comision"){
                                html_ventasnetas +='<td  align="right" >'+attrValue+'</td>'; 
                            }
                        }
                    html_ventasnetas +='</tr> </thead>';
                    
              //numero_agente||nombre_agente||serie_folio||cliente||valor||comision||moneda_factura||total_comision
                    
                    var primer_registro = 0;
                    var nombre_agente = "";
                    var total_comision = 0.0;
                    var valor= 0.0;
                    var denominacion = "";
                    var simbolo_moneda = ""
                    for(var i=0; i<body_tabla.length; i++){
						if (primer_registro == 0){
							nombre_agente =body_tabla[i]["nombre_agente"];
							denominacion = body_tabla[i]["moneda_factura"];
							
							if(body_tabla[i]["moneda_factura"]== "M.N."){
								simbolo_moneda = "$";
							}
										
							if(body_tabla[i]["moneda_factura"]== "USD"){
								simbolo_moneda = "USD";
							}
							primer_registro=1;
						}
                             
                              if(nombre_agente == body_tabla[i]["nombre_agente"]  && denominacion == body_tabla[i]["moneda_factura"]){
                                  if (tmp == 0 ){
                                    html_ventasnetas +='<tr>';
                                        //html_ventasnetas +='<td align="left"  >'+body_tabla[i]["numero_agente"]+'</td>'; 
                                        html_ventasnetas +='<td align="left" >'+body_tabla[i]["nombre_agente"]+'</td>';
                                        html_ventasnetas +='<td align="left" >'+body_tabla[i]["serie_folio"]+'</td>';
                                        html_ventasnetas +='<td align="left" >'+body_tabla[i]["fecha_factura"]+'</td>';
                                        html_ventasnetas +='<td align="left" >'+body_tabla[i]["cliente"]+'</td>';
                                        html_ventasnetas +='<td align="left" >'+body_tabla[i]["fecha_pago"]+'</td>';
                                        html_ventasnetas +='<td align="center" >'+body_tabla[i]["numero_dias_pago"]+'</td>';
                                        html_ventasnetas +='<td align="right" width="30px" >'+simbolo_moneda+'</td>'; 
                                        html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["subtotal"]).toFixed(2))+'</td>';
                                        html_ventasnetas +='<td align="center" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["comision"]).toFixed(2))+"  %"+'</td>'; 
                                        html_ventasnetas +='<td align="right" width="30px" >'+simbolo_moneda+'</td>'; 
                                        html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_comision"]).toFixed(2))+'</td>';
                                        //total_comision = total_comision + parseFloat(body_tabla[i]["total_comision"]);
                                        //valor = valor + parseFloat(body_tabla[i]["valor"]);
                                        
                                    html_ventasnetas +='</tr>';
                                  }
                                  
                                  if (tmp != 0 ){
                                    html_ventasnetas +='<tr>';
                                        //html_ventasnetas +='<td align="left" >'+""+'</td>'; 
                                        html_ventasnetas +='<td align="left" >'+""+'</td>';
                                        html_ventasnetas +='<td align="left" >'+body_tabla[i]["serie_folio"]+'</td>';
                                        html_ventasnetas +='<td align="left" >'+body_tabla[i]["fecha_factura"]+'</td>';
                                        html_ventasnetas +='<td align="left" >'+body_tabla[i]["cliente"]+'</td>';
                                        html_ventasnetas +='<td align="left" >'+body_tabla[i]["fecha_pago"]+'</td>';
                                        html_ventasnetas +='<td align="center" >'+body_tabla[i]["numero_dias_pago"]+'</td>';
                                        html_ventasnetas +='<td align="right" width="30px" >'+simbolo_moneda+'</td>'; 
                                        html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["subtotal"]).toFixed(2))+'</td>';
                                        html_ventasnetas +='<td align="center" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["comision"]).toFixed(2))+"  %"+'</td>'; 
                                        html_ventasnetas +='<td align="right" width="30px" >'+simbolo_moneda+'</td>'; 
                                        html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_comision"]).toFixed(2))+'</td>';
                                        
                                    html_ventasnetas +='</tr>';
                                    
                                  }
                                  
                                  tmp =1;
                                  total_comision = total_comision + parseFloat(body_tabla[i]["total_comision"]);
                                  valor =valor + parseFloat(body_tabla[i]["subtotal"]);
                                  //numero_agente = body_tabla[i]["numero_agente"];
                                  //denominacion = body_tabla[i]["moneda_factura"];
                              }else{
                                    html_ventasnetas +='<tr>';  
                                    html_ventasnetas +='<td colspan ="6" align="right"> Total:</td>';
                                    html_ventasnetas +='<td align="right" width="30px" >'+simbolo_moneda+'</td>'; 
                                    html_ventasnetas +=' <td align="right">'+$(this).agregar_comas(parseFloat(valor).toFixed(2))+'</td>';
                                    html_ventasnetas +='<td></td>';
                                    html_ventasnetas +='<td align="right" width="30px" >'+simbolo_moneda+'</td>'; 
                                    html_ventasnetas +='<td align="right">'+$(this).agregar_comas(parseFloat(total_comision).toFixed(2))+'</td>';
                                    html_ventasnetas +='</tr>';
                                    
                                    html_ventasnetas +='<tr>';  
                                    html_ventasnetas +='<td colspan ="11" height="30px" > </td>';
                                    html_ventasnetas +='</tr>';
                                    
                                    
                                    total_comision=0;
                                    valor= 0;
                                    
                                    if(body_tabla[i]["moneda_factura"]== "M.N."){
                                    simbolo_moneda = "$";
                                    }

                                    if(body_tabla[i]["moneda_factura"]== "USD"){
                                    simbolo_moneda = "USD";
                                    }
                                   html_ventasnetas +='<tr>';
                                        html_ventasnetas +='<td align="left" >'+body_tabla[i]["nombre_agente"]+'</td>';
                                        html_ventasnetas +='<td align="left" >'+body_tabla[i]["serie_folio"]+'</td>';
                                        html_ventasnetas +='<td align="left" >'+body_tabla[i]["fecha_factura"]+'</td>';
                                        html_ventasnetas +='<td align="left" >'+body_tabla[i]["cliente"]+'</td>';
                                        html_ventasnetas +='<td align="left" >'+body_tabla[i]["fecha_pago"]+'</td>';
                                        html_ventasnetas +='<td align="center" >'+body_tabla[i]["numero_dias_pago"]+'</td>';
                                        html_ventasnetas +='<td align="right" width="30px" >'+simbolo_moneda+'</td>'; 
                                        html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["subtotal"]).toFixed(2))+'</td>';
                                        html_ventasnetas +='<td align="center" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["comision"]).toFixed(2))+"  %"+'</td>'; 
                                        html_ventasnetas +='<td align="right" width="30px" >'+simbolo_moneda+'</td>'; 
                                        html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_comision"]).toFixed(2))+'</td>';
                                        //total_comision = total_comision + parseFloat(body_tabla[i]["total_comision"]);
                                        
                                    html_ventasnetas +='</tr>';
                                    total_comision = total_comision + parseFloat(body_tabla[i]["total_comision"]);
                                    valor =valor + parseFloat(body_tabla[i]["subtotal"]);
									nombre_agente =body_tabla[i]["nombre_agente"];
									denominacion = body_tabla[i]["moneda_factura"];  
                              }
                     }
					html_ventasnetas +='<tr>';  
					html_ventasnetas +='<td colspan ="6" align="right"> Total:</td>';
					html_ventasnetas +='<td align="right" >'+simbolo_moneda+'</td>'; 
					html_ventasnetas +='<td align="right">'+$(this).agregar_comas(parseFloat(valor).toFixed(2))+'</td>';
					html_ventasnetas +='<td></td>';
					html_ventasnetas +='<td align="right" >'+simbolo_moneda+'</td>'; 
					html_ventasnetas +='<td align="right">'+$(this).agregar_comas(parseFloat(total_comision).toFixed(2))+'</td>';
					html_ventasnetas +='</tr>';
                     
                    html_ventasnetas += '</table>';
                    
                    $div_cobranza_agente.append(html_ventasnetas); 
                    var height2 = $('#cuerpo').css('height');
                    var alto = parseInt(height2)-250;
                    var pix_alto=alto+'px';


                    $('#ventas').tableScroll({height:parseInt(pix_alto)});
                    });
                }else{
                    jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
                    
                }
               }//termina reporte cobranza por agente
               
               
               
               
				
               
               
               if ($select_opciones.val()==2){
                    //jAlert("Esto no se ha hecho",'Atencion')
                    $div_cobranza_agente.children().remove();
                    var fecha_inicial = $fecha_inicial.val();
                    var fecha_final = $fecha_final.val();
                    if(fecha_inicial != "" && fecha_final != ""){ 
                    
                    var arreglo_parametros = {fecha_inicial : $fecha_inicial.val() , fecha_final : $fecha_final.val(),id_agente:$select_agente.val(), iu:config.getUi()};

                    var restful_json_service = config.getUrlForGetAndPost() + '/getVentaxAgente.json'
                    var cliente="";
                    $.post(restful_json_service,arreglo_parametros,function(entry){
                    var body_tabla = entry;
					
					var header_tabla = {
							//numero:'Numero',
							nombre  :'Nombre',
							factura    :"Factura",
							fecha_factura    :"Fecha&nbsp;Factura",
							cliente  :'Cliente',
							moneda_importe:'',
							fecha_pago:"Importe",
							iva :"Iva",
							moneda_total  :'',
							total    :'Total'
					};
					
					
                    var TPventa_neta=0.0;
                    var Sumatoriaventa_neta = 0.0;
                    var sumatotoriaporciento = 0.0;
                    var html_ventasxagente = '<table id="ventas" >';
                    var porcentaje = 0.0;
                    
                    
                    var numero_control=0.0; 
                    var cliente=0.0; 
                    var moneda="$"; 
                    var venta_neta=0.0; 
                    var porciento=0.0;
                    var tmp= 0;
                    
                    html_ventasxagente +='<thead> <tr>';
                        for(var key in header_tabla){
                            var attrValue = header_tabla[key];
                            /*if(attrValue == "Numero"){
                                html_ventasxagente +='<td  align="left">'+attrValue+'</td>'; 
                            }*/
                            if(attrValue == "Nombre"){
                                html_ventasxagente +='<td  align="left" " width="250px">'+attrValue+'</td>'; 
                            }
                            
                            if(attrValue == "Factura"){
                                html_ventasxagente +='<td  align="center" >'+attrValue+'</td>'; 
                            }
                            if(attrValue == "Fecha&nbsp;Factura"){
                                html_ventasxagente +='<td  align="center">'+attrValue+'</td>'; 
                            }
                            if(attrValue == "Cliente"){
                                html_ventasxagente +='<td  align="left" width="500px">'+attrValue+'</td>'; 
                            }
                            if(attrValue == "Importe"){
                                html_ventasxagente +='<td  align="center">'+attrValue+'</td>'; 
                            }
                            if(attrValue == "Iva"){
                                html_ventasxagente +='<td  align="center">'+attrValue+'</td>'; 
                            }
                            
                            if(attrValue == ""){
                                html_ventasxagente +='<td  align="right" >'+attrValue+'</td>'; 
                            }
                            
                            if(attrValue == "Total"){
                                html_ventasxagente +='<td  align="right" >'+attrValue+'</td>'; 
                            }
                            
                            
                        }
                    html_ventasxagente +='</tr> </thead>';
                    
              //numero_agente||nombre_agente||serie_folio||cliente||valor||comision||moneda_factura||total_comision
                    
                    var primer_registro = 0;
                    var nombre_agente = "";
                    var total = 0.0;
                    var importe= 0.0;
                    var denominacion = "";
                    var simbolo_moneda = ""
                    for(var i=0; i<body_tabla.length; i++){
                              if (primer_registro == 0){
                                 
				nombre_agente =body_tabla[i]["nombre_agente"];
				denominacion = body_tabla[i]["moneda_factura"];
				
				if(body_tabla[i]["moneda_factura"]== "M.N."){
				simbolo_moneda = "$";
				}
                                
				if(body_tabla[i]["moneda_factura"]== "USD"){
				simbolo_moneda = "USD";
				}
				primer_registro=1;
			
                              }
                             
                              if(nombre_agente == body_tabla[i]["nombre_agente"]  && denominacion == body_tabla[i]["moneda_factura"]){
                                  if (tmp == 0 ){
                                    html_ventasxagente +='<tr>';
                                        //html_ventasxagente +='<td align="left"  >'+body_tabla[i]["numero_agente"]+'</td>'; 
                                        html_ventasxagente +='<td align="left" >'+body_tabla[i]["nombre_agente"]+'</td>';
                                        html_ventasxagente +='<td align="left" >'+body_tabla[i]["serie_folio"]+'</td>';
                                        html_ventasxagente +='<td align="left" >'+body_tabla[i]["fecha_factura"]+'</td>';
                                        html_ventasxagente +='<td align="left" >'+body_tabla[i]["cliente"]+'</td>';
                                        
                                        html_ventasxagente +='<td align="right" width="30px" >'+simbolo_moneda+'</td>'; 
                                        html_ventasxagente +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["importe"]).toFixed(2))+'</td>';
                                        html_ventasxagente +='<td align="center" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["iva"]).toFixed(2))+'</td>'; 
                                        html_ventasxagente +='<td align="right" width="30px" >'+simbolo_moneda+'</td>'; 
                                        html_ventasxagente +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total"]).toFixed(2))+'</td>';
                                        //total_comision = total_comision + parseFloat(body_tabla[i]["total_comision"]);
                                        //valor = valor + parseFloat(body_tabla[i]["valor"]);
                                        
                                    html_ventasxagente +='</tr>';
                                  }
                                  
                                  if (tmp != 0 ){
                                    
                                    
                                    
                                    html_ventasxagente +='<tr>';
                                        //html_ventasxagente +='<td align="left" >'+""+'</td>'; 
                                        html_ventasxagente +='<td align="left" >'+""+'</td>';
                                        html_ventasxagente +='<td align="left" >'+body_tabla[i]["serie_folio"]+'</td>';
                                        html_ventasxagente +='<td align="left" >'+body_tabla[i]["fecha_factura"]+'</td>';
                                        html_ventasxagente +='<td align="left" >'+body_tabla[i]["cliente"]+'</td>';
                                        html_ventasxagente +='<td align="right" width="30px" >'+simbolo_moneda+'</td>'; 
                                        html_ventasxagente +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["importe"]).toFixed(2))+'</td>';
                                        html_ventasxagente +='<td align="center" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["iva"]).toFixed(2))+'</td>'; 
                                        html_ventasxagente +='<td align="right" width="30px" >'+simbolo_moneda+'</td>'; 
                                        html_ventasxagente +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total"]).toFixed(2))+'</td>';
                                        
                                    html_ventasxagente +='</tr>';
                                    
                                  }
                                  
                                  tmp =1;
                                  total = total + parseFloat(body_tabla[i]["total"]);
                                  importe =importe + parseFloat(body_tabla[i]["importe"]);
                                  //numero_agente = body_tabla[i]["numero_agente"];
                                  //denominacion = body_tabla[i]["moneda_factura"];
                                  
                                  
                              }else{
                                    html_ventasxagente +='<tr>';  
                                    html_ventasxagente +='<td colspan ="4" align="right"> Total:</td>';
                                    html_ventasxagente +='<td align="right" width="30px" >'+simbolo_moneda+'</td>'; 
                                    html_ventasxagente +=' <td align="right">'+$(this).agregar_comas(parseFloat(importe).toFixed(2))+'</td>';
                                    html_ventasxagente +='<td></td>';
                                    html_ventasxagente +='<td align="right" width="30px" >'+simbolo_moneda+'</td>'; 
                                    html_ventasxagente +='<td align="right">'+$(this).agregar_comas(parseFloat(total).toFixed(2))+'</td>';
                                    html_ventasxagente +='</tr>';
                                    
                                    html_ventasxagente +='<tr>';  
                                    html_ventasxagente +='<td colspan ="9" height="30px" > </td>';
                                    html_ventasxagente +='</tr>';
                                    
                                    
                                    total=0;
                                    importe= 0;
                                    
                                    if(body_tabla[i]["moneda_factura"]== "M.N."){
                                    simbolo_moneda = "$";
                                    }

                                    if(body_tabla[i]["moneda_factura"]== "USD"){
                                    simbolo_moneda = "USD";
                                    }
                                   html_ventasxagente +='<tr>';
                                        html_ventasxagente +='<td align="left" >'+body_tabla[i]["nombre_agente"]+'</td>';
                                        html_ventasxagente +='<td align="left" >'+body_tabla[i]["serie_folio"]+'</td>';
                                        html_ventasxagente +='<td align="left" >'+body_tabla[i]["fecha_factura"]+'</td>';
                                        html_ventasxagente +='<td align="left" >'+body_tabla[i]["cliente"]+'</td>';
                                       
                                        html_ventasxagente +='<td align="right" width="30px" >'+simbolo_moneda+'</td>'; 
                                        html_ventasxagente +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["importe"]).toFixed(2))+'</td>';
                                        html_ventasxagente +='<td align="center" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["iva"]).toFixed(2))+'</td>'; 
                                        html_ventasxagente +='<td align="right" width="30px" >'+simbolo_moneda+'</td>'; 
                                        html_ventasxagente +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total"]).toFixed(2))+'</td>';
                                        //total_comision = total_comision + parseFloat(body_tabla[i]["total_comision"]);
                                        
                                    html_ventasxagente +='</tr>';
                                    
                                    total = total + parseFloat(body_tabla[i]["total"]);
                                    importe =importe + parseFloat(body_tabla[i]["importe"]);
                                    nombre_agente =body_tabla[i]["nombre_agente"];
                                    denominacion = body_tabla[i]["moneda_factura"];  
                                    
                                  
                                  
                              }
                             
                             
                                
                               
                     }
                                    html_ventasxagente +='<tr>';  
                                    html_ventasxagente +='<td colspan ="4" align="right"> Total:</td>';
                                    html_ventasxagente +='<td align="right" >'+simbolo_moneda+'</td>'; 
                                    html_ventasxagente +='<td align="right">'+$(this).agregar_comas(parseFloat(importe).toFixed(2))+'</td>';
                                    html_ventasxagente +='<td></td>';
                                    html_ventasxagente +='<td align="right" >'+simbolo_moneda+'</td>'; 
                                    html_ventasxagente +='<td align="right">'+$(this).agregar_comas(parseFloat(total).toFixed(2))+'</td>';
                                    html_ventasxagente +='</tr>';
                     
                    html_ventasxagente += '</table>';

                    
                    
                    
                    
                    
                    $div_cobranza_agente.append(html_ventasxagente); 
                    var height2 = $('#cuerpo').css('height');
                    var alto = parseInt(height2)-250;
                    var pix_alto=alto+'px';


                    $('#ventas').tableScroll({height:parseInt(pix_alto)});
                    
                        
                        

                    });
                }else{
                    jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
                    
                }
                    
               }
               
               
               
               
               /*
          }else{
               jAlert("Elija una Opcion",'Atencion!!!!!');
          }
          */
        
        
             
       });        
});
