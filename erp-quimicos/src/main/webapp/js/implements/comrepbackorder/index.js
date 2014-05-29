$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de BackOrder' ,                 
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
			return this.contextpath + "/controllers/comrepbackorder";
			//  return this.controller;
		}
	};
	
	//desencadena evento del $campo_ejecutar al pulsar Enter en $campo
	$aplicar_evento_keypress = function($campo, $campo_ejecutar){
		$campo.keypress(function(e){
			if(e.which == 13){
				$campo_ejecutar.trigger('click');
				return false;
			}
		});
	}
	
	$('#header').find('#header1').find('span.emp').text(config.getEmp());
	$('#header').find('#header1').find('span.suc').text(config.getSuc());
    $('#header').find('#header1').find('span.username').text(config.getUserName());
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append(config.getTituloApp());
	
	$('#barra_acciones').hide();
	//barra para el buscador 
	$('#barra_buscador').hide();
	
	var $tabla_existencias = $('#lienzo_recalculable').find('#table_exis');
	var $select_opciones = $('#lienzo_recalculable').find('select[name=opciones]');
	var $no_oc = $('#lienzo_recalculable').find('input[name=no_oc]');
	var $codigo = $('#lienzo_recalculable').find('input[name=codigo]');
	var $descripcion = $('#lienzo_recalculable').find('input[name=descripcion]');
	var $proveedor = $('#lienzo_recalculable').find('input[name=proveedor]');
	var $fecha_inicial = $('#lienzo_recalculable').find('input[name=busqueda_fecha_inicial]');
	var $fecha_final = $('#lienzo_recalculable').find('input[name=busqueda_fecha_final]');
	
	var $genera_pdf = $('#lienzo_recalculable').find('#genera_reporte');
	var $buscar = $('#lienzo_recalculable').find('#boton_buscador');
	
	$select_opciones.children().remove();
	var almacen_hmtl = '<option value="1" selected="yes">Orden de Compra</option>';
		almacen_hmtl += '<option value="2">Producto</option>';
		almacen_hmtl += '<option value="3">Proveedor</option>'
	$select_opciones.append(almacen_hmtl);
	
	
	
	
	$genera_pdf.click(function(event){
		event.preventDefault();
		
		var codigo='';
		var descripcion='';
		var lote_interno='';
		
		if($codigo_producto.val()==''){
			codigo = '0';
		}else{
			codigo = $codigo_producto.val();
		}
		
		if($descripcion.val()==''){
			descripcion = '0';
		}else{
			descripcion = $descripcion.val();
		}
		
		if($lote_interno.val()==''){
			lote_interno = '0';
		}else{
			lote_interno = $lote_interno.val();
		}
		
		var busqueda = $select_opciones.val() +"___"+ $select_almacen.val() +"___"+ codigo +"___"+ descripcion + "___"+lote_interno;
		
		var input_json = config.getUrlForGetAndPost() + '/getReporteExistencias/'+busqueda+'/'+config.getUi()+'/out.json';
		if(parseInt($select_almacen.val()) > 0){
			window.location.href=input_json;
		}else{
			alert("Selecciona un Almacen.");
		}
	});
	
	
	
	
	
	var height2 = $('#cuerpo').css('height');
	var alto = parseInt(height2)-240;
	var pix_alto=alto+'px';
	
	
	$('#table_exis').tableScroll({height:parseInt(pix_alto)});
	
	
	$buscar.click(function(event){
		var id_almacen = $select_almacen.val();
		var primero=0;
		$tabla_existencias.find('tbody').children().remove();
		var input_json = config.getUrlForGetAndPost()+'/getExistencias.json';
		$arreglo = {'tipo':$select_opciones.val(), 
					'almacen':id_almacen, 
					'codigo':$codigo_producto.val(), 
					'descripcion':$descripcion.val(),
					'lote_interno':$lote_interno.val(),
					'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
					};
		if(parseInt($select_almacen.val()) > 0){
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Existencias'],function(entryIndex,exi){
					var trCount = $("tr", $tabla_existencias.find('tbody')).size();
					
					var tr_first='';
					if(primero==0){
						tr_first='class="first"';
						primero=1;
					}else{
						tr_first='';
					}
					
					var tr = '<tr '+tr_first+'>';
						tr += '<td width="20">';
							tr += '<input type="hidden" name="id_lote" class="idlote'+trCount+'" value="'+exi['id_lote']+'">';
							tr += '<input type="hidden" name="selec" class="selec'+trCount+'" value="0">';
							tr += '<input type="checkbox" name="micheck" class="micheck'+trCount+'" value="true">';
						tr += '</td>';
						tr += '<td width="60">';
							tr += '<input type="text" name="cant" class="cant'+trCount+'" value="" readOnly="true" style="width:58px; background:#dddddd; height:15px;">';
							tr += '<input type="hidden" name="tipo_prod" value="'+exi['id_tipo_producto']+'">';
						tr += '</td>';
                                                tr += '<td width="60">';
							tr += '<input type="text" name="cantProd" class="cantProd'+trCount+'" value="" readOnly="true" style="width:58px; background:#dddddd; height:15px;">';
						tr += '</td>';
						tr += '<td width="60">';
							tr += '<select name="select_medida" style="width:58px;">';
							//aqui se carga el select con los tipos de iva
							$.each(entry['MedidasEtiqueta'],function(entryIndex,med){
								if(med['id'] == exi['id_medida_etiqueta']){
									tr += '<option value="' + med['id'] + '"  selected="yes">' + med['titulo'] + '</option>';
								}else{
									tr += '<option value="' + med['id'] + '"  >' + med['titulo'] + '</option>';
								}
							});
							tr += '</select>';
						tr += '</td>';
						tr += '<td width="100">'+exi['lote_int']+'</td>';
						tr += '<td width="100">'+exi['lote_prov']+'</td>';
						tr += '<td width="100">'+exi['codigo']+'</td>';
						tr += '<td width="350">'+exi['descripcion']+'</td>';
						tr += '<td width="100">'+exi['unidad_medida']+'</td>';
						tr += '<td width="100" align="right">'+$(this).agregar_comas(parseFloat(exi['existencia']).toFixed(4))+'</td>';
						tr += '<td width="100">'+exi['fecha_entrada']+'</td>';
					tr += '</tr>';
					$tabla_existencias.find('tbody').append(tr);
					
					
					//aplicar click a los campso check del grid
					$tabla_existencias.find('tbody').find('input.micheck'+trCount).click(function(event){
						if( this.checked ){
							//Para la cantidad de etiquertas
							$(this).parent().find('input[name=selec]').val("1");
							$(this).parent().parent().find('input[name=cant]').css({'background' : '#ffffff'});
							$(this).parent().parent().find('input[name=cant]').attr("readonly", false);//habilitar campo
							$(this).parent().parent().find('input[name=cant]').val("1");
								
							//Para la cantidad de productos
							$(this).parent().parent().find('input[name=cantProd]').css({'background' : '#ffffff'});
							$(this).parent().parent().find('input[name=cantProd]').attr("readonly", false);//habilitar campo
							$(this).parent().parent().find('input[name=cantProd]').val("1");
						}else{
							//Para la cantidad de etiquetas
							$(this).parent().find('input[name=selec]').val("0");
							$(this).parent().parent().find('input[name=cant]').val("");
							$(this).parent().parent().find('input[name=cant]').css({'background' : '#dddddd'});
							$(this).parent().parent().find('input[name=cant]').attr("readonly", true);//deshabilitar campo
							
							//Para la cantidad de producto
							$(this).parent().parent().find('input[name=cantProd]').val("");
							$(this).parent().parent().find('input[name=cantProd]').css({'background' : '#dddddd'});
							$(this).parent().parent().find('input[name=cantProd]').attr("readonly", true);//deshabilitar campo
						}
					});
					
					
					$tabla_existencias.find('tbody').find('input.cant'+trCount).keypress(function(e){
						// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
						if (e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
							return true;
						}else {
							return false;
						}
					});
					
					$tabla_existencias.find('tbody').find('input.cantProd'+trCount).keypress(function(e){
						// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
						if (e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
							return true;
						}else {
							return false;
						}
					});
					
					//pone uno al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
					$tabla_existencias.find('tbody').find('input.cantProd'+trCount).blur(function(e){
						if(parseFloat($(this).val())==0 || $(this).val()==""){
							if($(this).parent().parent().find('input[name=selec]').val() == '1'){
								$(this).val(1);
							}
						}
					});
                                        
					//pone uno al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
					$tabla_existencias.find('tbody').find('input.cant'+trCount).blur(function(e){
						if(parseFloat($(this).val())==0 || $(this).val()==""){
							if($(this).parent().parent().find('input[name=selec]').val() == '1'){
								$(this).val(1);
							}
						}
					});
					
					
					
				});
				
				var height2 = $('#cuerpo').css('height');
				var alto = parseInt(height2)-240;
				var pix_alto=alto+'px';
				
				$('#table_exis').tableScroll({height:parseInt(pix_alto)});
			});//termina llamada json
		}else{
			jAlert("Selecciona un Almacen.",'! Atencion');
		}
	});
	
	
	

	
	$aplicar_evento_keypress($select_opciones, $buscar);
	$aplicar_evento_keypress($no_oc, $buscar);
	$aplicar_evento_keypress($codigo, $buscar);
	$aplicar_evento_keypress($descripcion, $buscar);
	$aplicar_evento_keypress($proveedor, $buscar);
	$aplicar_evento_keypress($fecha_inicial, $buscar);
	$aplicar_evento_keypress($fecha_final, $buscar);
	$no_oc.focus();
    
});



