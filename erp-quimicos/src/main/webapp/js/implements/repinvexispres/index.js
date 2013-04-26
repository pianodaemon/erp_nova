$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de Existencias por Presentaciones' ,                 
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
			return this.contextpath + "/controllers/repinvexispres";
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
	var $select_almacen = $('#lienzo_recalculable').find('select[name=select_almacen]');
	var $codigo_producto = $('#lienzo_recalculable').find('input[name=codigo]');
	var $descripcion = $('#lienzo_recalculable').find('input[name=descripcion]');
	var $select_presentacion = $('#lienzo_recalculable').find('select[name=select_presentacion]');
	var $genera_reporte_exis = $('#lienzo_recalculable').find('#genera_reporte');
	var $imprimir = $('#lienzo_recalculable').find('#imprimir');
	
	var $buscar = $('#lienzo_recalculable').find('#boton_buscador');
	var $forma_selected = $('#formaRepExisLote');
	
	$select_opciones.children().remove();
	var almacen_hmtl = '<option value="1" >General</option>';
		almacen_hmtl += '<option value="2" selected="yes">Con Existencia</option>';
		almacen_hmtl += '<option value="3">Sin Existencia</option>'
	$select_opciones.append(almacen_hmtl);
	
	
	//obtiene los almacenes para el reporte
	var input_json = config.getUrlForGetAndPost()+'/getDatosBuscador.json';
	
	$arreglo = {'iu':iu = $('#lienzo_recalculable').find('input[name=iu]').val()};
	$.post(input_json,$arreglo,function(entry){
		$select_almacen.children().remove();
		//var almacen_hmtl = '<option value="0" selected="yes">[--Seleccionar Almacen--]</option>';
		var almacen_hmtl = '';
		$.each(entry['Almacenes'],function(entryIndex,alm){
			almacen_hmtl += '<option value="' + alm['id'] + '"  >' + alm['titulo'] + '</option>';
		});
		$select_almacen.append(almacen_hmtl);
	});//termina llamada json
	
	
	
	$genera_reporte_exis.click(function(event){
		event.preventDefault();
		
		var codigo='';
		var descripcion='';
		
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
		
		var busqueda = $select_opciones.val() +"___"+ $select_almacen.val() +"___"+ codigo +"___"+ descripcion + "___"+$select_presentacion.val();
		
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
					'presentacion':$select_presentacion.val(),
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
	$aplicar_evento_keypress($select_almacen, $buscar);
	$aplicar_evento_keypress($codigo_producto, $buscar);
	$aplicar_evento_keypress($descripcion, $buscar);
	$aplicar_evento_keypress($lote_interno, $buscar);
	$codigo_producto.focus();
    
});



