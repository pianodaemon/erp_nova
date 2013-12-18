$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de Auxiliar de Cuentas' ,                 
		contextpath : $('#lienzo_recalculable').find('input[name=contextpath]').val(),
		
		userName : $('#lienzo_recalculable').find('input[name=user]').val(),
		ui : $('#lienzo_recalculable').find('input[name=iu]').val(),
		
		getUrlForGetAndPost : function(){
			var url = document.location.protocol + '//' + document.location.host + this.getController();
			return url;
		},
		
		getEmp: function(){
			return this.empresa;
		},
		
		getSuc: function(){
			return this.sucursal;
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
			return this.contextpath + "/controllers/ctbrepauxcta";
			//  return this.controller;
		}
	};
	
	$('#header').find('#header1').find('span.emp').text(config.getEmp());
	$('#header').find('#header1').find('span.suc').text(config.getSuc());
    $('#header').find('#header1').find('span.username').text(config.getUserName());
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append(config.getTituloApp());
	
	$('#barra_acciones').hide();
	
	//barra para el buscador 
	$('#barra_buscador').hide();
	
	var $select_tipo_reporte = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=tipo_reporte]');
	var $select_ano = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_ano]');
	var $select_mes = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_mes]');
	var $select_cuentas = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_cuentas]');
	var $select_cuenta = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_cuenta]');
	var $select_subcuenta = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_subcuenta]');
	var $select_subsubcuenta = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_subsubcuenta]');
	var $select_subsubsubcuenta = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_subsubsubcuenta]');
	var $select_subsubsubsubcuenta = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_subsubsubsubcuenta]');
	
	var $descripcion = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=descripcion]');
	
	var $genera_PDF = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Generar_PDF]');
	var $busqueda_reporte= $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Buscar]');
	var $div_rep= $('#lienzo_recalculable').find('#div_rep');
	
	
	$descripcion.css({'background' : '#DDDDDD'});
	$descripcion.attr('readonly',true);
	$descripcion.css({'background' : '#DDDDDD'});
	$select_cuenta.attr('disabled','-1');
	$select_subcuenta.attr('disabled','-1');
	$select_subsubcuenta.attr('disabled','-1');
	$select_subsubsubcuenta.attr('disabled','-1');
	$select_subsubsubsubcuenta.attr('disabled','-1');
	$descripcion.attr('disabled','-1');
	
	$select_tipo_reporte.children().remove();
	var html='<option value="1">Mensual</option>';
	html+='<option value="2">Anual</option>';
	$select_tipo_reporte.append(html);
	
	$select_cuentas.children().remove();
	var html2='<option value="1">Todas</option>';
	html2+='<option value="2">Una cuenta</option>';
	$select_cuentas.append(html2);
	
	var array_meses = {0:"- Seleccionar -",  1:"Enero",  2:"Febrero", 3:"Marzo", 4:"Abirl", 5:"Mayo", 6:"Junio", 7:"Julio", 8:"Agosto", 9:"Septiembre", 10:"Octubre", 11:"Noviembre", 12:"Diciembre"};
	var array_ctas_nivel1;
	var array_ctas;
	
	
	var arreglo_parametros = { iu:config.getUi() };
	var restful_json_service = config.getUrlForGetAndPost() + '/getDatos.json';
	$.post(restful_json_service,arreglo_parametros,function(entry){
		//carga select de años
		$select_ano.children().remove();
		var html_anio = '';
		$.each(entry['Anios'],function(entryIndex,anio){
			if(parseInt(anio['valor']) == parseInt(entry['Dato'][0]['anioActual']) ){
				html_anio += '<option value="' + anio['valor'] + '" selected="yes">' + anio['valor'] + '</option>';
			}else{
				html_anio += '<option value="' + anio['valor'] + '"  >' + anio['valor'] + '</option>';
			}
		});
		$select_ano.append(html_anio);
		
		//cargar select del Mes inicial
		$select_mes.children().remove();
		var select_html = '';
		for(var i in array_meses){
			if(parseInt(i) == parseInt(entry['Dato'][0]['mesActual']) ){
				select_html += '<option value="' + i + '" selected="yes">' + array_meses[i] + '</option>';	
			}else{
				select_html += '<option value="' + i + '"  >' + array_meses[i] + '</option>';	
			}
		}
		$select_mes.append(select_html);
		
		/*
		$select_cuenta.children().remove();
		var html_cta = '';
		$.each(entry['Cta'],function(entryIndex,cta){
			html_cta += '<option value="' + cta['cta'] + '"  >' + cta['cta'] + '</option>';
		});
		$select_cuenta.append(html_cta);
		*/
		
		array_ctas_nivel1=entry['Cta'];
	});
	
	
	
	
	
	$select_cuentas.change(function(){
		if(parseInt($(this).val())==0){
			$descripcion.val('');
			$select_cuenta.children().remove();
			$select_subcuenta.children().remove();
			$select_subsubcuenta.children().remove();
			$select_subsubsubcuenta.children().remove();
			$select_subsubsubsubcuenta.children().remove();
			
			$descripcion.css({'background' : '#DDDDDD'});
			$select_cuenta.attr('disabled','-1');
			$select_subcuenta.attr('disabled','-1');
			$select_subsubcuenta.attr('disabled','-1');
			$select_subsubsubcuenta.attr('disabled','-1');
			$select_subsubsubsubcuenta.attr('disabled','-1');
			$descripcion.attr('disabled','-1');
		}else{
			$descripcion.css({'background' : '#ffffff'});
			$select_cuenta.removeAttr('disabled');
			$select_subcuenta.removeAttr('disabled');
			$select_subsubcuenta.removeAttr('disabled');
			$select_subsubsubcuenta.removeAttr('disabled');
			$select_subsubsubsubcuenta.removeAttr('disabled');
			$descripcion.removeAttr('disabled');
			
			$select_cuenta.children().remove();
			var html_cta = '';
			$.each(array_ctas_nivel1,function(entryIndex,cta){
				html_cta += '<option value="' + cta['cta'] + '"  >' + cta['cta'] + '</option>';
			});
			$select_cuenta.append(html_cta);
		}
	});
	

	
	$aplicar_evento_change = function(nivel, $campo_select, $campo_select2){
		$campo_select.change(function(){
			var valor_cta=$(this).val();
			$descripcion.val('');
			
			var input_json_cuentas = config.getUrlForGetAndPost() + '/getCtas.json';
			$arreglo = {
				'cta':$select_cuenta.val(),
				'scta':$select_subcuenta.val(),
				'sscta':$select_subsubcuenta.val(),
				'ssscta':$select_subsubsubcuenta.val(),
				'nivel':nivel,
				'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
			}
			
			$.post(input_json_cuentas,$arreglo,function(data){
				//alert(data['Cta'].length);
				if(parseInt(data['Cta'].length)>0){
					$campo_select2.children().remove();
					var html = '';
					$.each(data['Cta'],function(entryIndex,cta){
						if(parseInt(cta['cta'])==0){
							html += '<option value="' + cta['cta'] + '"  ></option>';
						}else{
							html += '<option value="' + cta['cta'] + '"  >' + cta['cta'] + '</option>';
						}
						if($descripcion.val()==''){
							$descripcion.val(cta['descripcion']);
						}
					});
					$campo_select2.append(html);
					
					//Almacena el arreglo del ultimo nivel que trae datos
					array_ctas=data['Cta'];
				}else{
					$.each(array_ctas,function(entryIndex,cta){
						//alert("valor_cta:"+valor_cta+" | cta:"+cta['cta']);
						if(parseInt(valor_cta)==parseInt(cta['cta'])){
							$descripcion.val(cta['descripcion']);
						}
					});
				}
			});
		});
	}
	
	
	//Obtener las subcuentas de acuerdo al nivel que se le indica
	$aplicar_evento_change(2, $select_cuenta, $select_subcuenta);
	$aplicar_evento_change(3, $select_subcuenta, $select_subsubcuenta);
	$aplicar_evento_change(4, $select_subsubcuenta, $select_subsubsubcuenta);
	$aplicar_evento_change(5, $select_subsubsubcuenta, $select_subsubsubsubcuenta);
	
    
	
	//Crear y descargar PDF de Reporte Auxiliar de Cuentas
	$genera_PDF.click(function(event){
		event.preventDefault();
		var cta="0";
		var scta="0";
		var sscta="0";
		var ssscta="0";
		var sssscta="0";
		
		if($select_cuenta.val()!=null && $select_cuenta.val()!=""){
			cta=$select_cuenta.val();
		}
		if($select_subcuenta.val()!=null && $select_subcuenta.val()!=""){
			scta=$select_subcuenta.val();
		}
		if($select_subsubcuenta.val()!=null && $select_subsubcuenta.val()!=""){
			sscta=$select_subsubcuenta.val();
		}
		if($select_subsubsubcuenta.val()!=null && $select_subsubsubcuenta.val()!=""){
			ssscta=$select_subsubsubcuenta.val();
		}
		if($select_subsubsubsubcuenta.val()!=null && $select_subsubsubsubcuenta.val()!=""){
			sssscta=$select_subsubsubsubcuenta.val();
		}
		
		var cadena = $select_tipo_reporte.val()+"___"+$select_ano.val()+"___"+$select_mes.val()+"___"+$select_cuentas.val()+"___"+cta+"___"+scta+"___"+sscta+"___"+ssscta+"___"+sssscta;
		alert(cadena);
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		var input_json = config.getUrlForGetAndPost() + '/getPdfAuxCtas/'+cadena+'/'+iu+'/out.json';
		window.location.href=input_json;
	});//termina llamada json
	
	
	
	$busqueda_reporte.click(function(event){
		event.preventDefault();
		$div_rep.children().remove();
			
			var arreglo_parametros = {	
				tipo_reporte: $select_tipo_reporte.val(),
				cliente: $razon_cliente.val(),
				anio_corte: $select_ano.val(),
				mes_corte: $select_mes.val(),
				iu:config.getUi()
			};
			
			var restful_json_service = config.getUrlForGetAndPost() + '/getReporteSaldos.json'
			var proveedoor="";
			$.post(restful_json_service,arreglo_parametros,function(entry){
				var body_tabla = entry['Facturas'];
				var header_tabla = {
					serie_folio			:'Factura',
					fecha_factura		:'Fecha',
					orden_compra		:'O. Compra',
					moneda_total		:'',
					monto_factura		:'Monto Facturado',
					moneda_pagado		:'',
					importe_pagado  	:'Monto Pagado',
					//ultimo_pago    		:'Ultimo Pago',
					moneda_saldo    	:'',
					saldo_factura    	:'Saldo Pendiente'
				};
                    
				var html_reporte = '<table id="table_rep">';
				var html_fila_vacia='';
				var html_footer = '';
				
				html_reporte +='<thead> <tr>';
				for(var key in header_tabla){
					var attrValue = header_tabla[key];
					if(attrValue == "Factura"){
						html_reporte +='<td width="120px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Fecha"){
						html_reporte +='<td width="100px" align="center">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "O. Compra"){
						html_reporte +='<td width="140px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == ''){
						html_reporte +='<td width="10px" align="right" >'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Monto Facturado"){
						html_reporte +='<td width="130px" align="left" id="monto">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Monto Pagado"){
						html_reporte +='<td width="130px" align="left" id="monto">'+attrValue+'</td>'; 
					}
					/*
					if(attrValue == "Ultimo Pago"){
						html_reporte +='<td width="90px" align="center">'+attrValue+'</td>'; 
					}
					*/
					if(attrValue == "Saldo Pendiente"){
						html_reporte +='<td width="130px" align="left" id="monto">'+attrValue+'</td>'; 
					}
				}
				html_reporte +='</tr> </thead>';
				
				html_fila_vacia +='<tr>';
				html_fila_vacia +='<td align="left"  id="sin_borde" width="120px" height="10"></td>';
				html_fila_vacia +='<td align="left"  id="sin_borde" width="100px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="140px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="10px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="130px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="10px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="130px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="10px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="130px"></td>';
				html_fila_vacia +='</tr>';
				
				var orden_compra="";
				var simbolo_moneda="";
				var cliente_actual="";
				
                //inicializar variables
                var suma_monto_factura_cliente=0.0;
                var suma_importe_pagado_cliente=0.0;
                var suma_saldo_pendiente_cliente=0.0;
                
                var simbolo_moneda_pesos="";
                var suma_monto_factura_moneda_pesos=0.0;
                var suma_importe_pagado_moneda_pesos=0.0;
                var suma_saldo_pendiente_moneda_pesos=0.0;
				
				var simbolo_moneda_dolar="";
                var suma_monto_factura_moneda_dolar=0.0;
                var suma_importe_pagado_moneda_dolar=0.0;
                var suma_saldo_pendiente_moneda_dolar=0.0;
                
                var simbolo_moneda_euro="";
                var suma_monto_factura_moneda_euro=0.0;
                var suma_importe_pagado_moneda_euro=0.0;
                var suma_saldo_pendiente_moneda_euro=0.0;
				
				if(parseInt(body_tabla.length)>0){
					cliente_actual=body_tabla[0]["cliente"];
					simbolo_moneda=body_tabla[0]["moneda_simbolo"];
					
					html_reporte +='<tr id="tr_totales" class="first"><td align="left" colspan="11">'+cliente_actual+'</td></tr>';
					
					for(var i=0; i<body_tabla.length; i++){
						if(body_tabla[i]["orden_compra"]==null){
							orden_compra="";
						}else{
							orden_compra=body_tabla[i]["orden_compra"];
						}
						
						if(cliente_actual==body_tabla[i]["cliente"] && simbolo_moneda==body_tabla[i]["moneda_simbolo"]){
							html_reporte +='<tr>';
							html_reporte +='<td align="left" >'+body_tabla[i]["serie_folio"]+'</td>';
							html_reporte +='<td align="center" >'+body_tabla[i]["fecha_factura"]+'</td>';
							html_reporte +='<td align="left" >'+orden_compra+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["monto_factura"]).toFixed(2))+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["importe_pagado"]).toFixed(2))+'</td>';
							//html_reporte +='<td align="center" >'+body_tabla[i]["ultimo_pago"]+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["saldo_factura"]).toFixed(2))+'</td>';
							html_reporte +='</tr>';
							
							suma_monto_factura_cliente=parseFloat(suma_monto_factura_cliente) + parseFloat(body_tabla[i]["monto_factura"]);
							suma_importe_pagado_cliente=parseFloat(suma_importe_pagado_cliente) + parseFloat(body_tabla[i]["importe_pagado"]);
							suma_saldo_pendiente_cliente=parseFloat(suma_saldo_pendiente_cliente) + parseFloat(body_tabla[i]["saldo_factura"]);
							
							//pesos
							if(parseInt(body_tabla[i]["moneda_id"])==1){
								suma_monto_factura_moneda_pesos=parseFloat(suma_monto_factura_moneda_pesos) + parseFloat(body_tabla[i]["monto_factura"]);
								suma_importe_pagado_moneda_pesos=parseFloat(suma_importe_pagado_moneda_pesos) + parseFloat(body_tabla[i]["importe_pagado"]);
								suma_saldo_pendiente_moneda_pesos=parseFloat(suma_saldo_pendiente_moneda_pesos) + parseFloat(body_tabla[i]["saldo_factura"]);
								simbolo_moneda_pesos=body_tabla[i]["moneda_simbolo"];
							}
							
							//dolares
							if(parseInt(body_tabla[i]["moneda_id"])==2){
								suma_monto_factura_moneda_dolar=parseFloat(suma_monto_factura_moneda_dolar) + parseFloat(body_tabla[i]["monto_factura"]);
								suma_importe_pagado_moneda_dolar=parseFloat(suma_importe_pagado_moneda_dolar) + parseFloat(body_tabla[i]["importe_pagado"]);
								suma_saldo_pendiente_moneda_dolar=parseFloat(suma_saldo_pendiente_moneda_dolar) + parseFloat(body_tabla[i]["saldo_factura"]);
								simbolo_moneda_dolar=body_tabla[i]["moneda_simbolo"];
							}
							
							//euros
							if(parseInt(body_tabla[i]["moneda_id"])==3){
								suma_monto_factura_moneda_euro=parseFloat(suma_monto_factura_moneda_euro) + parseFloat(body_tabla[i]["monto_factura"]);
								suma_importe_pagado_moneda_euro=parseFloat(suma_importe_pagado_moneda_euro) + parseFloat(body_tabla[i]["importe_pagado"]);
								suma_saldo_pendiente_moneda_euro=parseFloat(suma_saldo_pendiente_moneda_euro) + parseFloat(body_tabla[i]["saldo_factura"]);
								simbolo_moneda_euro=body_tabla[i]["moneda_simbolo"];
							}
						}else{
							//imprimir totales
							html_reporte +='<tr id="tr_totales">';
							html_reporte +='<td align="left" id="sin_borde_derecho"></td>';
							html_reporte +='<td align="left" id="sin_borde"></td>';
							html_reporte +='<td align="right" id="sin_borde">Total cliente</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_monto_factura_cliente).toFixed(2))+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_importe_pagado_cliente).toFixed(2))+'</td>';
							//html_reporte +='<td align="left" ></td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_saldo_pendiente_cliente).toFixed(2))+'</td>';
							html_reporte +='</tr>';
							
							//fila vacia
							html_reporte +=html_fila_vacia;
							
							//reinicializar varibles
							suma_monto_factura_cliente=0.0;
							suma_importe_pagado_cliente=0.0;
							suma_saldo_pendiente_cliente=0.0;
							
							//tomar razon social de nuevo prov
							cliente_actual=body_tabla[i]["cliente"];
							simbolo_moneda=body_tabla[i]["moneda_simbolo"]
							
							html_reporte +='<tr id="tr_totales"><td align="left" colspan="12">'+cliente_actual+'</td></tr>';
							//crear primer registro del nuevo prov
							html_reporte +='<tr>';
							html_reporte +='<td align="left" >'+body_tabla[i]["serie_folio"]+'</td>';
							html_reporte +='<td align="center" >'+body_tabla[i]["fecha_factura"]+'</td>';
							html_reporte +='<td align="left" >'+orden_compra+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["monto_factura"]).toFixed(2))+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["importe_pagado"]).toFixed(2))+'</td>';
							//html_reporte +='<td align="center" >'+body_tabla[i]["ultimo_pago"]+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["saldo_factura"]).toFixed(2))+'</td>';
							html_reporte +='</tr>';
							
							//sumar montos del nuevo prov
							suma_monto_factura_cliente=parseFloat(suma_monto_factura_cliente) + parseFloat(body_tabla[i]["monto_factura"]);
							suma_importe_pagado_cliente=parseFloat(suma_importe_pagado_cliente) + parseFloat(body_tabla[i]["importe_pagado"]);
							suma_saldo_pendiente_cliente=parseFloat(suma_saldo_pendiente_cliente) + parseFloat(body_tabla[i]["saldo_factura"]);
							
							//pesos
							if(parseInt(body_tabla[i]["moneda_id"])==1){
								suma_monto_factura_moneda_pesos=parseFloat(suma_monto_factura_moneda_pesos) + parseFloat(body_tabla[i]["monto_factura"]);
								suma_importe_pagado_moneda_pesos=parseFloat(suma_importe_pagado_moneda_pesos) + parseFloat(body_tabla[i]["importe_pagado"]);
								suma_saldo_pendiente_moneda_pesos=parseFloat(suma_saldo_pendiente_moneda_pesos) + parseFloat(body_tabla[i]["saldo_factura"]);
								simbolo_moneda_pesos=body_tabla[i]["moneda_simbolo"];
							}
							
							//dolares
							if(parseInt(body_tabla[i]["moneda_id"])==2){
								suma_monto_factura_moneda_dolar=parseFloat(suma_monto_factura_moneda_dolar) + parseFloat(body_tabla[i]["monto_factura"]);
								suma_importe_pagado_moneda_dolar=parseFloat(suma_importe_pagado_moneda_dolar) + parseFloat(body_tabla[i]["importe_pagado"]);
								suma_saldo_pendiente_moneda_dolar=parseFloat(suma_saldo_pendiente_moneda_dolar) + parseFloat(body_tabla[i]["saldo_factura"]);
								simbolo_moneda_dolar=body_tabla[i]["moneda_simbolo"];
							}
							
							//euros
							if(parseInt(body_tabla[i]["moneda_id"])==3){
								suma_monto_factura_moneda_euro=parseFloat(suma_monto_factura_moneda_euro) + parseFloat(body_tabla[i]["monto_factura"]);
								suma_importe_pagado_moneda_euro=parseFloat(suma_importe_pagado_moneda_euro) + parseFloat(body_tabla[i]["importe_pagado"]);
								suma_saldo_pendiente_moneda_euro=parseFloat(suma_saldo_pendiente_moneda_euro) + parseFloat(body_tabla[i]["saldo_factura"]);
								simbolo_moneda_euro=body_tabla[i]["moneda_simbolo"];
							}

						}
					}
					//imprimir total del ultimo prov
					html_reporte +='<tr id="tr_totales">';
					html_reporte +='<td align="left" id="sin_borde_derecho"></td>';
					html_reporte +='<td align="left" id="sin_borde"></td>';
					html_reporte +='<td align="right" id="sin_borde">Total cliente</td>';
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_monto_factura_cliente).toFixed(2))+'</td>';
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_importe_pagado_cliente).toFixed(2))+'</td>';
					//html_reporte +='<td align="left" ></td>';
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_saldo_pendiente_cliente).toFixed(2))+'</td>';
					html_reporte +='</tr>';
					
					
					//imprimir totales de la moneda PESOS
					html_footer +='<tr id="tr_totales">';
					html_footer +='<td align="left" id="sin_borde_derecho"></td>';
					html_footer +='<td align="left" id="sin_borde"></td>';
					html_footer +='<td align="right" id="sin_borde">Total MN</td>';
					html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_pesos+'</td>';
					html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_monto_factura_moneda_pesos).toFixed(2))+'</td>';
					html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_pesos+'</td>';
					html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_importe_pagado_moneda_pesos).toFixed(2))+'</td>';
					//html_footer +='<td align="left" ></td>';
					html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_pesos+'</td>';
					html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_saldo_pendiente_moneda_pesos).toFixed(2))+'</td>';
					html_footer +='</tr>';
					
					if(parseFloat(suma_saldo_pendiente_moneda_dolar) > 0){
						//imprimir totales de la moneda DOLARES
						html_footer +='<tr id="tr_totales">';
						html_footer +='<td align="left" id="sin_borde_derecho"></td>';
						html_footer +='<td align="left" id="sin_borde"></td>';
						html_footer +='<td align="right" id="sin_borde">Total USD</td>';
						html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_dolar+'</td>';
						html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_monto_factura_moneda_dolar).toFixed(2))+'</td>';
						html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_dolar+'</td>';
						html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_importe_pagado_moneda_dolar).toFixed(2))+'</td>';
						//html_footer +='<td align="left" ></td>';
						html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_dolar+'</td>';
						html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_saldo_pendiente_moneda_dolar).toFixed(2))+'</td>';
						html_footer +='</tr>';
					}
					
					if(parseFloat(suma_saldo_pendiente_moneda_euro) > 0){
						//Imprimir totales de la moneda EUROS
						html_footer +='<tr id="tr_totales">';
						html_footer +='<td align="left" id="sin_borde_derecho"></td>';
						html_footer +='<td align="left" id="sin_borde"></td>';
						html_footer +='<td align="right" id="sin_borde">Total EUR</td>';
						html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_euro+'</td>';
						html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_monto_factura_moneda_euro).toFixed(2))+'</td>';
						html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_euro+'</td>';
						html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_importe_pagado_moneda_euro).toFixed(2))+'</td>';
						//html_footer +='<td align="left" ></td>';
						html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_euro+'</td>';
						html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_saldo_pendiente_moneda_euro).toFixed(2))+'</td>';
						html_footer +='</tr>';
					}
				}
				
				html_reporte +='<tfoot>';
					html_reporte += html_footer;
				html_reporte +='</tfoot>';
				
				html_reporte += '</table>';
				
				
				$div_rep.append(html_reporte); 
				var height2 = $('#cuerpo').css('height');
				var alto = parseInt(height2)-300;
				var pix_alto=alto+'px';
				$('#table_rep').tableScroll({height:parseInt(pix_alto)});
			});
	});
	
	/*
	$(this).aplicarEventoKeypressEjecutaTrigger($select_tipo_reporte, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_ano, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_mes, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($razon_cliente, $busqueda_reporte);
	*/
});   
        
        
        
        
    
