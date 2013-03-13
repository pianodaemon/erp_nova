$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Movimientos de Inventario',
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
			return this.contextpath + "/controllers/repinvmovimientos";
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
	var $select_tipo_mov = $('#lienzo_recalculable').find('select[name=select_tipo_mov]');
	var $select_almacen = $('#lienzo_recalculable').find('select[name=select_almacen]');
	var $codigo = $('#lienzo_recalculable').find('input[name=codigo]');
	var $descripcion = $('#lienzo_recalculable').find('input[name=descripcion]');
	var $fecha_inicial = $('#lienzo_recalculable').find('input[name=fecha_inicial]');
	var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');
	
	var $buscar_producto = $('#lienzo_recalculable').find('#buscar_producto');
	var $boton_busqueda = $('#lienzo_recalculable').find('#boton_busqueda');
	var $boton_genera_pdf = $('#lienzo_recalculable').find('#boton_genera_pdf');
	
	

	
	
        
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
	//$fecha_inicial.val(mostrarFecha());
	
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
			
        
	$fecha_final.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
	
	//aplicar mascara a campos para entrada manual de fecha
	//9 indica que va a permitir captura de numeros solamante
	$fecha_inicial.mask('9999-99-99');
	$fecha_final.mask('9999-99-99');
	
	
	//obtiene los almacenes para el reporte
	var input_json = config.getUrlForGetAndPost()+'/getDatosBusqueda.json';
	$arreglo = {'iu':iu = $('#lienzo_recalculable').find('input[name=iu]').val()};
	$.post(input_json,$arreglo,function(entry){
		
		$select_tipo_mov.children().remove();
		var tm_hmtl = '<option value="0" >[--Todos--]</option>';
		$.each(entry['Tmov'],function(entryIndex,tm){
			tm_hmtl += '<option value="' + tm['id'] + '"  >' + tm['titulo'] + '</option>';
		});
		$select_tipo_mov.append(tm_hmtl);
		
		$select_almacen.children().remove();
		//var almacen_hmtl = '<option value="0" selected="yes">[--Seleccionar Almacen--]</option>';
		var almacen_hmtl = '';
		$.each(entry['Almacenes'],function(entryIndex,alm){
			almacen_hmtl += '<option value="' + alm['id'] + '"  >' + alm['titulo'] + '</option>';
		});
		$select_almacen.append(almacen_hmtl);
		
	});//termina llamada json
	
	
	
	$boton_genera_pdf.click(function(event){
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
	
	
	
	//ejecutar busqueda del reporte
	$boton_busqueda.click(function(event){
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
						tr += '<td width="70">';
							tr += '<input type="text" name="cant" class="cant'+trCount+'" value="" readOnly="true" style="width:68px; background:#dddddd; height:15px;">';
							tr += '<input type="hidden" name="tipo_prod" value="'+exi['id_tipo_producto']+'">';
						tr += '</td>';
						tr += '<td width="70">';
							tr += '<select name="select_medida" style="width:70px;">';
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
						tr += '<td width="120">'+exi['lote_int']+'</td>';
						tr += '<td width="110">'+exi['lote_prov']+'</td>';
						tr += '<td width="110">'+exi['codigo']+'</td>';
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
	
	
	
	/*
	var respuestaProcesada = function(data){
		if ( data['success'] == "true" ){
			//jAlert("El Agente fue dado de alta con exito", 'Atencion!');
		}else{
			jAlert("No hay Lotes seleccionados para Impresi&oacute;n", 'Atencion!');
		}
	}
	var options = { dataType :  'json', success : respuestaProcesada };
	$forma_selected.ajaxForm(options);
*/
	
	$aplicar_evento_keypress($select_tipo_mov, $boton_busqueda);
	$aplicar_evento_keypress($select_almacen, $boton_busqueda);
	$aplicar_evento_keypress($codigo, $boton_busqueda);
	$aplicar_evento_keypress($descripcion, $boton_busqueda);
	$aplicar_evento_keypress($fecha_inicial, $boton_busqueda);
	$aplicar_evento_keypress($fecha_final, $boton_busqueda);
	$select_tipo_mov.focus();
	
});



