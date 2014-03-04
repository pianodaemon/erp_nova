$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
	//Arreglo que almacena las unidades de medida
	var arrayUM;
	//Variable que indica si se debe permitir cambiar la unidad de medida al agregar el producto
	var cambiarUM;
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/facnomina";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    var $nuevo = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseover(function(){
		$(this).removeClass("onmouseOutNewItem").addClass("onmouseOverNewItem");
	});
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseout(function(){
		$(this).removeClass("onmouseOverNewItem").addClass("onmouseOutNewItem");
	});
	
	
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseover(function(){
		$(this).removeClass("onmouseOutVisualizaBuscador").addClass("onmouseOverVisualizaBuscador");
	});
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseout(function(){
		$(this).removeClass("onmouseOverVisualizaBuscador").addClass("onmouseOutVisualizaBuscador");
	});
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Nomina');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_cliente = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente]');
	var $busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	var $busqueda_producto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_producto]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	var $busqueda_select_agente = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_agente]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('#boton_buscador');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('#boton_limpiar');
	
	
	$buscar.mouseover(function(){
		$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
	});
	$buscar.mouseout(function(){
		$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
	});
	   
	$limpiar.mouseover(function(){
		$(this).removeClass("onmouseOutLimpiar").addClass("onmouseOverLimpiar");
	});
	$limpiar.mouseout(function(){
		$(this).removeClass("onmouseOverLimpiar").addClass("onmouseOutLimpiar");
	});
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "cliente" + signo_separador + $busqueda_cliente.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val()+ "|";
		valor_retorno += "codigo" + signo_separador + $busqueda_codigo.val() + "|";
		valor_retorno += "producto" + signo_separador + $busqueda_producto.val() + "|";
		valor_retorno += "agente" + signo_separador + $busqueda_select_agente.val();
		return valor_retorno;
	};
    
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	
	$buscar.click(function(event){
		//event.preventDefault();
		cadena = to_make_one_search_string();
		$cadena_busqueda = cadena.toCharCode();
		$get_datos_grid();
	});
	
	
	//esta funcion carga los datos para el buscador del paginado
	$cargar_datos_buscador_principal= function(){
		var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAgentesParaBuscador.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_lineas,$arreglo,function(data){
			//Alimentando los campos select_agente
			$busqueda_select_agente.children().remove();
			var agente_hmtl = '<option value="0">[-Seleccionar Agente-]</option>';
			$.each(data['Agentes'],function(entryIndex,agente){
				agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
			});
			$busqueda_select_agente.append(agente_hmtl);
		});
	}
	
	//llamada a funcion
	$cargar_datos_buscador_principal();
	
	$limpiar.click(function(event){
		$busqueda_folio.val('');
		$busqueda_cliente.val('');
		$busqueda_codigo.val('');
		$busqueda_producto.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		//llamada a funcion al limpiar campos
		$cargar_datos_buscador_principal();
		
		$busqueda_folio.focus();
		
		$get_datos_grid();
	});
	
	
	TriggerClickVisializaBuscador = 0;
	//visualizar  la barra del buscador
	$visualiza_buscador.click(function(event){
		event.preventDefault();
		var alto=0;
		if(TriggerClickVisializaBuscador==0){
			 TriggerClickVisializaBuscador=1;
			 var height2 = $('#cuerpo').css('height');
			 //alert('height2: '+height2);
			 
			 alto = parseInt(height2)-220;
			 var pix_alto=alto+'px';
			 //alert('pix_alto: '+pix_alto);
			 
			 $('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
			 $('#barra_buscador').animate({height: '80px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
			 
			 //alert($('#cuerpo').css('height'));
		}else{
			 TriggerClickVisializaBuscador=0;
			 var height2 = $('#cuerpo').css('height');
			 alto = parseInt(height2)+220;
			 var pix_alto=alto+'px';
			 
			 $('#barra_buscador').animate({height:'0px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
		};
		$busqueda_folio.focus();
	});
	/*
	//desencadena evento del $campo_ejecutar al pulsar Enter en $campo
	$(this).aplicarEventoKeypressEjecutaTrigger = function($campo, $campo_ejecutar){
		$campo.keypress(function(e){
			if(e.which == 13){
				$campo_ejecutar.trigger('click');
				return false;
			}
		});
	}
	*/
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_cliente, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_codigo, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_producto, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_inicial, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_final, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_agente, $buscar);
	
	
	//----------------------------------------------------------------
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
	
	
	$busqueda_fecha_inicial.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
        
	$busqueda_fecha_inicial.DatePicker({
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
			$busqueda_fecha_inicial.val(formated);
			if (formated.match(patron) ){
				var valida_fecha=mayor($busqueda_fecha_inicial.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$busqueda_fecha_inicial.val(mostrarFecha());
				}else{
					$busqueda_fecha_inicial.DatePickerHide();	
				}
			}
		}
	});
	
	$busqueda_fecha_final.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
	
	$busqueda_fecha_final.DatePicker({
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
			$busqueda_fecha_final.val(formated);
			if (formated.match(patron) ){
				var valida_fecha=mayor($busqueda_fecha_final.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$busqueda_fecha_final.val(mostrarFecha());
				}else{
					$busqueda_fecha_final.DatePickerHide();	
				}
			}
		}
	});
	
    
	$busqueda_folio.focus();
	
	$tabs_li_funxionalidad = function(){
		var $select_prod_tipo = $('#forma-facnomina-window').find('select[name=prodtipo]');
		$('#forma-facnomina-window').find('#submit').mouseover(function(){
			$('#forma-facnomina-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-facnomina-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		})
		$('#forma-facnomina-window').find('#submit').mouseout(function(){
			$('#forma-facnomina-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-facnomina-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		})
		$('#forma-facnomina-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-facnomina-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-facnomina-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-facnomina-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-facnomina-window').find('#close').mouseover(function(){
			$('#forma-facnomina-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		})
		$('#forma-facnomina-window').find('#close').mouseout(function(){
			$('#forma-facnomina-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		})
		
		$('#forma-facnomina-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-facnomina-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-facnomina-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-facnomina-window').find("ul.pestanas li").click(function() {
			$('#forma-facnomina-window').find(".contenidoPes").hide();
			$('#forma-facnomina-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-facnomina-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	$tabs_li_funxionalidad_nominaempleado = function(){
		$('#forma-nominaempleado-window').find('#boton_actualizar_forma_consignacion').mouseover(function(){
			$('#forma-nominaempleado-window').find('#boton_actualizar_forma_consignacion').css({ backgroundImage:"url(../../img/modalbox/bt1.png)"});
		})
		$('#forma-nominaempleado-window').find('#boton_actualizar_forma_consignacion').mouseout(function(){
			$('#forma-nominaempleado-window').find('#boton_actualizar_forma_consignacion').css({ backgroundImage:"url(../../img/modalbox/btn1.png)"});
		})
		
		
		$('#forma-nominaempleado-window').find('#boton_cancelar_forma_consignacion').mouseover(function(){
			$('#forma-nominaempleado-window').find('#boton_cancelar_forma_consignacion').css({ backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-nominaempleado-window').find('#boton_cancelar_forma_consignacion').mouseout(function(){
			$('#forma-nominaempleado-window').find('#boton_cancelar_forma_consignacion').css({ backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-nominaempleado-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-nominaempleado-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-nominaempleado-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-nominaempleado-window').find("ul.pestanas li").click(function() {
			$('#forma-nominaempleado-window').find(".contenidoPes").hide();
			$('#forma-nominaempleado-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-nominaempleado-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	//Funcion para hacer que un campo solo acepte numeros
	$permitir_solo_numeros = function($campo){
		$campo.keypress(function(e){
			//Permitir  numeros, borrar, suprimir, TAB, puntos
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
	}
	



	
	//Agregar datos del remitente
	$agregar_datos_remitente = function($rem_id, $nombre_remitente, $noremitente, $dir_remitente, $busca_remitente, rem_id, rem_nombre, rem_numero, rem_dir){
		$rem_id.val(rem_id);
		$nombre_remitente.val(rem_nombre);
		$noremitente.val(rem_numero);
		$dir_remitente.val(rem_dir);
		
		//Aplicar solo lectura una vez que se ha escogido un remitente
		$aplicar_readonly_input($nombre_remitente);
		
		//Oculta link buscar remitente
		$busca_remitente.hide();
		
		$noremitente.focus();
	}
	
	//Buscador de Remitentes
	$busca_remitentes= function($rem_id, $nombre_remitente, $noremitente, $dir_remitente, $id_cliente, $busca_remitente){
		$(this).modalPanel_buscaremitente();
		var $dialogoc =  $('#forma-buscaremitente-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_remitentes').find('table.formaBusqueda_remitentes').clone());
		$('#forma-buscaremitente-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscaremitente-window').find('#tabla_resultado');
		
		var $boton_buscaremitente = $('#forma-buscaremitente-window').find('#boton_buscaremitente');
		var $cancelar_busqueda = $('#forma-buscaremitente-window').find('#cencela');
		
		var $cadena_buscar = $('#forma-buscaremitente-window').find('input[name=cadena_buscar]');
		var $select_filtro_por = $('#forma-buscaremitente-window').find('select[name=filtropor]');
		
		//funcionalidad botones
		$boton_buscaremitente.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$boton_buscaremitente.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$boton_buscaremitente.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		
		$boton_buscaremitente.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		var html = '';		
		$select_filtro_por.children().remove();
		html='<option value="0">[-- Opcion busqueda --]</option>';
		
		if($noremitente.val() !='' && $nombre_remitente.val()==''){
			html+='<option value="1" selected="yes">No. de control</option>';
			$cadena_buscar.val($noremitente.val());
		}else{
			html+='<option value="1">No. de control</option>';
		}
		html+='<option value="2">RFC</option>';
		if($nombre_remitente.val()!=''){
			$cadena_buscar.val($nombre_remitente.val());
			html+='<option value="3" selected="yes">Razon social</option>';
		}
		if($noremitente.val() =='' && $nombre_remitente.val()==''){
			html+='<option value="3" selected="yes">Razon social</option>';
		}
		$select_filtro_por.append(html);
		
		//click buscar clientes
		$boton_buscaremitente.click(function(event){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorRemitentes.json';
			$arreglo = {'cadena':$cadena_buscar.val(),
						 'filtro':$select_filtro_por.val(),
						 'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						}
						
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Remitentes'],function(entryIndex,remitente){
					trr = '<tr>';
						trr += '<td width="80">';
							trr += '<input type="hidden" id="id" value="'+remitente['id']+'">';
							trr += '<input type="hidden" id="dir" value="'+remitente['dir']+'">';
							trr += '<span class="no_control">'+remitente['folio']+'</span>';
						trr += '</td>';
						trr += '<td width="145"><span class="rfc">'+remitente['rfc']+'</span></td>';
						trr += '<td width="375"><span class="razon">'+remitente['razon_social']+'</span></td>';
					trr += '</tr>';
					
					$tabla_resultados.append(trr);
				});
				
				$tabla_resultados.find('tr:odd').find('td').css({'background-color' : '#e7e8ea'});
				$tabla_resultados.find('tr:even').find('td').css({'background-color' : '#FFFFFF'});

				$('tr:odd' , $tabla_resultados).hover(function () {
					$(this).find('td').css({background : '#FBD850'});
				}, function() {
						//$(this).find('td').css({'background-color':'#DDECFF'});
					$(this).find('td').css({'background-color':'#e7e8ea'});
				});
				$('tr:even' , $tabla_resultados).hover(function () {
					$(this).find('td').css({'background-color':'#FBD850'});
				}, function() {
					$(this).find('td').css({'background-color':'#FFFFFF'});
				});
				
				//Seleccionar un elemento del resultado
				$tabla_resultados.find('tr').click(function(){
					var rem_id = $(this).find('#id').val();
					var rem_nombre = $(this).find('span.razon').html();
					var rem_numero = $(this).find('span.no_control').html();
					var rem_dir = $(this).find('#dir').val();
					
					$agregar_datos_remitente($rem_id, $nombre_remitente, $noremitente, $dir_remitente, $busca_remitente, rem_id, rem_nombre, rem_numero, rem_dir);
					
					//Elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaremitente-overlay').fadeOut(remove);

					$nombre_remitente.focus();
				});
			});
		});//termina llamada json
		
		
		//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
		if($cadena_buscar.val() != ''){
			$boton_buscaremitente.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_buscar, $boton_buscaremitente);
		$(this).aplicarEventoKeypressEjecutaTrigger($select_filtro_por, $boton_buscaremitente);
		
		$cancelar_busqueda.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-buscaremitente-overlay').fadeOut(remove);
			
			//$('#forma-clientsdf-window').find('input[name=cliente]').focus();
		});		
		$cadena_buscar.focus();
	}//Termina buscador de Remitentes
	

    
    //funcion para aplicar evento a trs de una tabla para permitir seleccionar elemento desde el teclado
    $aplicarEventoSeleccionarTrkeypress = function($grid){
		var tr = $("tr", $grid).size();
		tr;
		
		//$('tr:first', $grid).css({background : '#FBD850'});
		$('tr:eq(0)', $grid).find('td').css({background : '#FBD850'});
		
		$('tr:eq(0)', $grid).focus();
		
		$campo_sku.onkeyup(function(e){
			if(e.which == 13){
				$agregar_producto.trigger('click');
				return false;
			}
		});
	}
	
	
	
	

	
	
	
	
	

    
	
	//calcula totales(subtotal, impuesto, total)
	$calcula_totales = function(){
		var $campo_subtotal = $('#forma-facnomina-window').find('input[name=subtotal]');
		var $campo_ieps = $('#forma-facnomina-window').find('input[name=ieps]');
		var $campo_impuesto = $('#forma-facnomina-window').find('input[name=impuesto]');
		var $campo_impuesto_retenido = $('#forma-facnomina-window').find('input[name=impuesto_retenido]');
		var $campo_total = $('#forma-facnomina-window').find('input[name=total]');
		//var $campo_tc = $('#forma-facnomina-window').find('input[name=tc]');
		//var $valor_impuesto = $('#forma-facnomina-window').find('input[name=valorimpuesto]');
		var $grid_empleados = $('#forma-facnomina-window').find('#grid_empleados');
		var $empresa_immex = $('#forma-facnomina-window').find('input[name=empresa_immex]');
		var $tasa_ret_immex = $('#forma-facnomina-window').find('input[name=tasa_ret_immex]');
		
		var sumaSubTotal = 0; //es la suma de todos los importes
		//Suma de todos los importes del IEPS
		var sumaIeps = 0;
		//Suma de los importes del IVA
		var sumaImpuesto = 0;
		//Monto del iva retenido de acuerdo a la tasa de retencion immex
		var impuestoRetenido = 0;
		//Suma del subtotal + totalImpuesto + sumaIeps - impuestoRetenido
		var sumaTotal = 0;
		
		/*
		//si valor del impuesto es null o vacio, se le asigna un 0
		if( $valor_impuesto.val()== null || $valor_impuesto.val()== ''){
			$valor_impuesto.val(0);
		}
		*/
		
		$grid_empleados.find('tr').each(function (index){
			if(( $(this).find('#cost').val().trim() != '') && ( $(this).find('#cant').val().trim() != '' )){
				//Acumula los importes sin IVA, sin IEPS en la variable subtotal
				sumaSubTotal = parseFloat(sumaSubTotal) + parseFloat(quitar_comas($(this).find('#import').val()));
				
				//Acumula los importes del IEPS
				sumaIeps =  parseFloat(sumaIeps) + parseFloat($(this).find('#importeIeps').val());
				
				if($(this).find('#totimp').val() != ''){
					//Acumula los importes del IVA
					sumaImpuesto =  parseFloat(sumaImpuesto) + parseFloat($(this).find('#totimp').val());
				}
			}
		});
		
		//calcular  la tasa de retencion IMMEX
		impuestoRetenido = parseFloat(sumaSubTotal) * parseFloat(parseFloat($tasa_ret_immex.val()));
		
		//Calcula el total sumando el sumaSubTotal + sumaIeps + sumaImpuesto - impuestoRetenido
		sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaIeps) + parseFloat(sumaImpuesto) - parseFloat(impuestoRetenido);
		
		//redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
		$campo_subtotal.val($(this).agregar_comas(  parseFloat(sumaSubTotal).toFixed(2)  ));
		
		//Redondea a dos digitos el IEPS y lo asigna  al campo ieps
		$campo_ieps.val($(this).agregar_comas(  parseFloat(sumaIeps).toFixed(2)  ));
		
		//redondea a dos digitos el impuesto y lo asigna al campo impuesto
		$campo_impuesto.val($(this).agregar_comas(  parseFloat(sumaImpuesto).toFixed(2)  ));
		
		//Redondea a dos digitos el impuesto y lo asigna al campo retencion
		$campo_impuesto_retenido.val($(this).agregar_comas(  parseFloat(impuestoRetenido).toFixed(2)  ));
		
		//Redondea a dos digitos la suma  total y se asigna al campo total
		$campo_total.val($(this).agregar_comas(  parseFloat(sumaTotal).toFixed(2)  ));
		
		//Ocultar campos si tienen valor menor o igual a cero
		if(parseFloat(sumaIeps)<=0){
			$('#forma-facnomina-window').find('#tr_ieps').hide();
		}else{
			$('#forma-facnomina-window').find('#tr_ieps').show();
		}
		if(parseFloat(impuestoRetenido)<=0){
			$('#forma-facnomina-window').find('#tr_retencion').hide();
		}else{
			$('#forma-facnomina-window').find('#tr_retencion').show();
		}
		
		if(parseFloat(sumaIeps)>0 && parseFloat(impuestoRetenido)<=0){
			$('#forma-facnomina-window').find('.facnomina_div_one').css({'height':'560px'});
		}
		
		if(parseFloat(sumaIeps)<=0 && parseFloat(impuestoRetenido)>0){
			$('#forma-facnomina-window').find('.facnomina_div_one').css({'height':'560px'});
		}
		
		if(parseFloat(sumaIeps)>0 && parseFloat(impuestoRetenido)>0){
			$('#forma-facnomina-window').find('.facnomina_div_one').css({'height':'580px'});
		}
	}//termina calcular totales
	
	
	
	
	
	$aplicar_evento_click_a_input_check = function($input_check){
		//aplicar click a los campso check del grid
		$input_check.click(function(event){
			if( this.checked ){
				$(this).parent().find('input[name=seleccionado]').val("1");
				$(this).parent().parent().find('input[name=cantidad]').attr("readonly", true);
				
			}else{
				$(this).parent().find('input[name=seleccionado]').val("0");
				$(this).parent().parent().find('input[name=cantidad]').attr("readonly", false);
			}
		});
	}
	
	
	
	
	//agregar producto al grid
	$agrega_producto_grid = function($grid_empleados, id_prod, sku, titulo, unidadId, unidad,id_pres,pres,prec_unitario,$select_moneda, id_moneda, $tipo_cambio,num_dec, arrayMonedas, tcMonProd, idImpto, valorImpto, id_ieps, tasa_ieps){
		var $id_impuesto = $('#forma-facnomina-window').find('input[name=id_impuesto]');
		var $valor_impuesto = $('#forma-facnomina-window').find('input[name=valorimpuesto]');
		var $incluye_produccion = $('#forma-facnomina-window').find('input[name=incluye_pro]');
		var $num_lista_precio = $('#forma-facnomina-window').find('input[name=num_lista_precio]');
		
		//esta es la moneda definida para el pedido
		var idMonedaPedido = $select_moneda.val();
		var precioOriginal = prec_unitario;
		var precioCambiado = 0.00;
		var importeImpuesto=0.00;
		
		//verificamos si la Lista de Precio trae moneda
		if(parseInt($num_lista_precio.val())>0){
			//verificamos si el grid no tiene registros
			if(parseInt($("tr", $grid_empleados).size())<=0){
				//aqui fijamos la moneda seleccionada para el pedido, esto evita que cambien la moneda
				var moneda_prod='';
				$select_moneda.children().remove();
				$.each(arrayMonedas ,function(entryIndex,moneda){
					if( parseInt(moneda['id']) == parseInt(idMonedaPedido) ){
						moneda_prod += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion'] + '</option>';
					}else{
						//moneda_prod += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
					}
				});
				$select_moneda.append(moneda_prod);
			}
			
			//Si la moneda del Pedido es diferente a la moneda del Precio del Producto
			//Entonces convertimos el precio a la moneda del pedido de acuerdo al tipo de cambio actual
			if( parseInt(idMonedaPedido) != parseInt(id_moneda) ){
				if(parseInt(idMonedaPedido)==1 && parseInt(id_moneda)!=1){
					//si la moneda del pedido es pesos y la moneda del precio es diferente de Pesos,
					//entonces calculamos su equivalente a pesos
					precioCambiado = parseFloat( parseFloat(precioOriginal) * parseFloat(tcMonProd)).toFixed(4);
				}
				if(parseInt(idMonedaPedido)!=1 && parseInt(id_moneda)==1){
					//alert("precioOriginal:"+precioOriginal +"		tc_original:"+$tc_original.val());
					//si la moneda original es dolar y la moneda del precio es Pesos, calculamos su equivalente a dolar
					precioCambiado = parseFloat( parseFloat(precioOriginal) / parseFloat($tipo_cambio.val()) ).toFixed(4);
				}
			}else{
				precioCambiado = prec_unitario;
			}
		}else{
			precioCambiado = prec_unitario;
		}
		
		
		//si  el campo tipo de cambio es null o vacio, se le asigna un 0
		if( $valor_impuesto.val()== null || $valor_impuesto.val()== ''){
			$valor_impuesto.val(0);
		}
		
		var encontrado = 0;
		//busca el sku y la presentacion en el grid
		$grid_empleados.find('tr').each(function (index){
			if(( $(this).find('#skuprod').val() == sku.toUpperCase() )  && (parseInt($(this).find('#idpres').val())== parseInt(id_pres) ) && (parseInt($(this).find('#elim').val())!=0)){
				encontrado=1;//el producto ya esta en el grid
			}
		});
		
		if(parseInt(encontrado)!=1){//si el producto no esta en el grid entra aqui
			//ocultamos el boton facturar para permitir Guardar los cambios  antes de facturar
			$('#forma-facnomina-window').find('#facturar').hide();
			//obtiene numero de trs
			var tr = $("tr", $grid_empleados).size();
			tr++;
			
			var trr = '';
			trr = '<tr>';
				trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
					trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
					trr += '<input type="hidden" 	name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
					trr += '<input type="hidden" 	name="iddetalle" id="idd" value="0">';//este es el id del registro que ocupa el producto en la tabla facnomina_detalles
					trr += '<input type="hidden" 	name="noTr" value="'+ tr +'">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="116">';
					trr += '<input type="hidden" 	name="idproducto" id="idprod" value="'+ id_prod +'">';
					trr += '<input type="text" 		name="sku" value="'+ sku +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="200">';
					trr += '<input type="text" 		name="nombre" 	value="'+ titulo +'" id="nom" class="borde_oculto" readOnly="true" style="width:196px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
					trr += '<select name="select_umedida" class="select_umedida'+ tr +'" style="width:86px;"></select>';
					trr += '<input type="text" 		name="unidad'+ tr +'" 	value="'+ unidad +'" id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
					trr += '<input type="hidden"    name="id_presentacion"  	value="'+  id_pres +'" id="idpres">';
					trr += '<input type="hidden"    name="numero_decimales"     value="'+  num_dec +'" id="numdec">';
					trr += '<input type="text" 	name="presentacion'+ tr +'" value="'+  pres +'" id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size:11px; border:1px solid #C1DAD7;" width="80">';
					trr += '<input type="text" name="cantidad" value=" " class="cantidad'+ tr +'" id="cant" style="width:76px;">';
				trr += '</td>';
				trr += '<td class="grid2" style="font-size: 11px; border:1px solid #C1DAD7;" width="90">';
					trr += '<input type="text" name="costo" value="'+ precioCambiado +'" class="costo'+ tr +'" id="cost" style="width:86px; text-align:right;">';
				trr += '</td>';
				trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
					trr += '<input type="text" name="importe'+ tr +'" value="" id="import" class="borde_oculto" readOnly="true" style="width:86px; text-align:right;">';
					trr += '<input type="hidden" name="id_imp_prod"   value="'+  idImpto +'" id="idimppord">';
					trr += '<input type="hidden" name="valor_imp"     value="'+  valorImpto +'" id="ivalorimp">';
					trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="0">';
				trr += '</td>';
				
				
				trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
					trr += '<input type="hidden" name="idIeps"     value="'+ id_ieps +'" id="idIeps">';
					trr += '<input type="text" name="tasaIeps" value="'+ tasa_ieps +'" class="borde_oculto" id="tasaIeps" style="width:56px; text-align:right;" readOnly="true">';
				trr += '</td>';
				
				trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
					trr += '<input type="text" name="importeIeps" value="'+parseFloat(0).toFixed(4)+'" class="borde_oculto" id="importeIeps" style="width:76px; text-align:right;" readOnly="true">';
				trr += '</td>';
				
				trr += '<td class="grid2" id="td_oculto'+ tr +'" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
					trr += '<input type="text" 		name="produccion" 	value="" 	 class="borde_oculto" readOnly="true" style="width:76px; text-align:right;">';
					trr += '<input type="hidden"    name="existencia" 	value="0">';
				trr += '</td>';
				
				var desactivado="";
				var check="";
				var valor_seleccionado="0";
				trr += '<td class="grid2" id="td_oculto'+ tr +'" style="font-size: 11px;  border:1px solid #C1DAD7;" width="20">';
					trr += '<input type="checkbox" 	name="checkProd" class="checkProd'+ tr +'" '+check+' '+desactivado+'>';
					trr += '<input type="hidden" 	name="seleccionado" value="'+valor_seleccionado+'">';//el 1 significa que el registro no ha sido eliminado
				trr += '</td>';
			trr += '</tr>';
			
			$grid_empleados.append(trr);
			
			
			var unidadLitroKilo=false;
			
			if(parseInt(unidad.toUpperCase().search(/KILO/))>-1){
				unidadLitroKilo=true;
			}else{
				if(parseInt(unidad.toUpperCase().search(/LITRO/))>-1){
					unidadLitroKilo=true;
				}
			}
			
			var hmtl_um="";
			if(cambiarUM.trim()=='true'){
				if(unidadLitroKilo){
					$grid_empleados.find('select.select_umedida'+tr).children().remove();
					$.each(arrayUM,function(entryIndex,um){
						if(parseInt(unidadId) == parseInt(um['id'])){
							hmtl_um += '<option value="' + um['id'] + '" selected="yes" >' + um['titulo'] + '</option>';
						}else{
							if(parseInt(um['titulo'].toUpperCase().search(/KILO/))>-1 || parseInt(um['titulo'].toUpperCase().search(/LITRO/))>-1){
								hmtl_um += '<option value="' + um['id'] + '">' + um['titulo'] + '</option>';
							}
						}
					});
					$grid_empleados.find('select.select_umedida'+tr).append(hmtl_um);
				}else{
					$grid_empleados.find('select.select_umedida'+tr).children().remove();
					$.each(arrayUM,function(entryIndex,um){
						if(parseInt(unidadId) == parseInt(um['id'])){
							hmtl_um += '<option value="' + um['id'] + '" selected="yes" >' + um['titulo'] + '</option>';
						}
					});
					$grid_empleados.find('select.select_umedida'+tr).append(hmtl_um);
				}
				//Ocultar campo input porque se debe mostrar select para permitir cambio de unidad de medida
				$grid_empleados.find('input[name=unidad'+ tr +']').hide();
			}else{
				//Carga select de pais Origen
				var elemento_seleccionado = unidadId;
				var texto_elemento_cero = '';
				var index_elem = 'id';
				var index_text_elem = 'titulo';
				$carga_campos_select($grid_empleados.find('select.select_umedida'+tr), arrayUM, elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
				
				//Ocultar porque no se permitirá cambiar de unidad de medida
				$grid_empleados.find('select.select_umedida'+tr).hide();
			}
			
			
			if($incluye_produccion.val()=='true'){
				//aplicar evento click al check, cuando la empresa incluya modulo de produccion
				$aplicar_evento_click_a_input_check($grid_empleados.find('.checkProd'+ tr));
				$grid_empleados.find('.checkProd'+ tr).hide();//ocultar check porque es un registro nuevo, se debe mostrar  hasta que se genere un warning
			}else{
				//ocualtar campos,  cuando la empresa no incluya modulo de produccion
				$grid_empleados.find('#td_oculto'+tr).hide();
			}
			
			//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
			$grid_empleados.find('#cant').focus(function(e){
				if($(this).val() == ' '){
						$(this).val('');
				}
			});
			
			//recalcula importe al perder enfoque el campo cantidad
			$grid_empleados.find('#cant').blur(function(){
				var $campoCantidad = $(this);
				var $campoPrecioU = $(this).parent().parent().find('#cost');
				var $campoImporte = $(this).parent().parent().find('#import');
				
				var $campoTasaIeps = $(this).parent().parent().find('#tasaIeps');
				var $importeIeps = $(this).parent().parent().find('#importeIeps');
				
				var $campoTasaIva = $(this).parent().parent().find('#ivalorimp');
				var $importeIva = $(this).parent().parent().find('#totimp');
				
				if ($campoCantidad.val() == ''){
					$campoCantidad.val(' ');
				}
				
				if( ($campoCantidad.val().trim() != '') && ($campoPrecioU.val().trim() != '') ){
					//Calcular y redondear el importe
					$campoImporte.val( parseFloat( parseFloat($campoCantidad.val()) * parseFloat($campoPrecioU.val()) ).toFixed(4));
					
					//Calcular y redondear el importe del IEPS
					$importeIeps.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
					
					//Calcular el impuesto para este producto multiplicando el importe + ieps por la tasa del iva
					$importeIva.val( (parseFloat($campoImporte.val()) + parseFloat($importeIeps.val())) * parseFloat( $campoTasaIva.val() ));
				}else{
					$campoImporte.val('');
					$importeIva.val('');
				}
				
				var numero_decimales = $(this).parent().parent().find('#numdec').val();
				var patron = /^-?[0-9]+([,\.][0-9]{0,0})?$/;
				if(parseInt(numero_decimales)==1){
					patron = /^-?[0-9]+([,\.][0-9]{0,1})?$/;
				}
				if(parseInt(numero_decimales)==2){
					patron = /^-?[0-9]+([,\.][0-9]{0,2})?$/;
				}
				if(parseInt(numero_decimales)==3){
					patron = /^-?[0-9]+([,\.][0-9]{0,3})?$/;
				}
				if(parseInt(numero_decimales)==4){
					patron = /^-?[0-9]+([,\.][0-9]{0,4})?$/;
				}
				
				/*
				if(!patron.test($(this).val())){
					//alert("Si valido"+$(this).val());
				}else{
					
				}
				*/
				
				//Buscar cuantos puntos tiene  cantidad
				var coincidencias = $(this).val().match(/\./g);
				var numPuntos = coincidencias ? coincidencias.length : 0;
				if(parseInt(numPuntos)>1){
					jAlert('El valor ingresado para Cantidad es incorrecto, tiene mas de un punto('+$(this).val()+').', 'Atencion!', function(r) { 
						$campoCantidad.focus();
					});
				}else{
					//Llamada a la funcion que calcula totales
					$calcula_totales();
				}
			});
			
			
			//Al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
			$grid_empleados.find('#cost').focus(function(e){
				if($(this).val() == ' '){
					$(this).val('');
				}
			});
			
			//Recalcula importe al perder enfoque el campo costo
			$grid_empleados.find('#cost').blur(function(){
				var $campoCantidad = $(this).parent().parent().find('#cant');
				var $campoPrecioU = $(this);
				var $campoImporte = $(this).parent().parent().find('#import');
				
				var $campoTasaIeps = $(this).parent().parent().find('#tasaIeps');
				var $importeIeps = $(this).parent().parent().find('#importeIeps');
				
				var $campoTasaIva = $(this).parent().parent().find('#ivalorimp');
				var $importeIva = $(this).parent().parent().find('#totimp');
				
				if ($campoPrecioU.val().trim() == ''){
					$campoPrecioU.val(' ');
				}
				
				if( ($campoPrecioU.val().trim()!= '') && ($campoCantidad.val().trim() != '')){	
					//Calcular y redondear el importe
					$campoImporte.val( parseFloat(parseFloat($campoPrecioU.val()) * parseFloat( $campoCantidad.val())).toFixed(4) );
					
					//Calcular y redondear el importe del IEPS
					$importeIeps.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
					
					//calcula el impuesto para este producto multiplicando el importe por el valor del iva
					$importeIva.val( (parseFloat($campoImporte.val()) + parseFloat($importeIeps.val())) * parseFloat( $campoTasaIva.val() ));
				}else{
					$campoImporte.val('');
					$importeIva.val('');
				}
				
				//Buscar cuantos puntos tiene  Precio Unitario
				var coincidencias = $(this).val().match(/\./g);
				var numPuntos = coincidencias ? coincidencias.length : 0;
				if(parseInt(numPuntos)>1){
					jAlert('El valor ingresado para Precio Unitario es incorrecto, tiene mas de un punto('+$(this).val()+').', 'Atencion!', function(r) { 
						$campoPrecioU.focus();
					});
				}else{
					//Llamada a la funcion que calcula totales
					$calcula_totales();
				}
			});
			
			//Validar campo costo, solo acepte numeros y punto
			$permitir_solo_numeros( $grid_empleados.find('#cost') );
			$permitir_solo_numeros( $grid_empleados.find('#cant') );
			
			//elimina un producto del grid
			$grid_empleados.find('#delete'+ tr).bind('click',function(event){
				event.preventDefault();
				if(parseInt($(this).parent().find('#elim').val()) != 0){
					//asigna espacios en blanco a todos los input de la fila eliminada
					$(this).parent().parent().find('input').val(' ');
					
					//asigna un 0 al input eliminado como bandera para saber que esta eliminado
					$(this).parent().find('#elim').val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
					
					//oculta la fila eliminada
					$(this).parent().parent().hide();
					$calcula_totales();//llamada a la funcion que calcula totales
				}
			});
			
			//limpiar campos
			$('#forma-facnomina-window').find('input[name=sku_producto]').val('');
			$('#forma-facnomina-window').find('input[name=nombre_producto]').val('');
			
			//asignar el enfoque al campo catidad
			$grid_empleados.find('.cantidad'+ tr).focus();
		}else{
			jAlert('El producto: '+sku+' con presentacion: '+pres+' ya se encuentra en el listado, seleccione otro diferente.', 'Atencion!', function(r) { 
				$('#forma-facnomina-window').find('input[name=nombre_producto]').val('');
				$('#forma-facnomina-window').find('input[name=sku_producto]').focus();
			});
		}
	}//termina agregar producto al grid
	
	
	$aplicar_readonly_input = function($input){
		$input.css({'background' : '#f0f0f0'});
		$input.attr('readonly',true);
	}
	
	$quitar_readonly_input = function($input){
		$input.css({'background' : '#ffffff'});
		$input.attr('readonly',false);
	}
	
	//Carga select con arreglo Indice=Valor
	$carga_campos_select = function($campo_select, arreglo_elementos, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, fijo){
		//la variable fijo indica que solo se mostrará el elemento seleccionado sin dar opción a cambiar
		
		$campo_select.children().remove();
		var select_html = '';
		
		if(texto_elemento_cero.trim()!=''){
			select_html = '<option value="0">'+texto_elemento_cero+'</option>';
		}
		$.each(arreglo_elementos,function(entryIndex,data){
			if(parseInt(elemento_seleccionado)==parseInt(data[campo_indice])){
				select_html += '<option value="' + data[campo_indice] + '" selected="yes">' + data[campo_valor] + '</option>';
			}else{
				if(!fijo){
					select_html += '<option value="' + data[campo_indice] + '" >' + data[campo_valor] + '</option>';
				}
			}
		});
		$campo_select.children().remove();
		$campo_select.append(select_html);
	}
	
	
	
	
	
	
	
	
	
	

	
	//Ventana para la Nomina de cada Empleado
	$forma_nomina_empleado = function(id_empleado, id_reg, $total_percep, $total_deduc, $neto_pagar, arrayPar, arrayDeptos, arrayPuestos, arrayRegimenContrato, arrayTipoContrato, arrayTipoJornada, arrayPeriodicidad, arrayBancos, arrayRiesgos, arrayImpuestoRet, arrayPercep, arrayDeduc){
		$('#forma-nominaempleado-window').remove();
		$('#forma-nominaempleado-overlay').remove();
		$(this).modalPanel_nominaempleado();
		var $dialogoc =  $('#forma-nominaempleado-window');
		$dialogoc.append($('div.nominaempleado').find('table.formaNominaEmpleado').clone());
		
		//$('#forma-nominaempleado-window').css({ "margin-left": -320, 	"margin-top": -235  });
		$('#forma-nominaempleado-window').css({ "margin-left": -320, 	"margin-top": -280  });
		$tabs_li_funxionalidad_nominaempleado();
		
		var $id_empleado = $('#forma-nominaempleado-window').find('input[name=id_empleado]');
		var $no_empleado = $('#forma-nominaempleado-window').find('input[name=no_empleado]');
		var $nombre_empleado = $('#forma-nominaempleado-window').find('input[name=nombre_empleado]');
		var $fecha_contrato = $('#forma-nominaempleado-window').find('input[name=fecha_contrato]');
		var $antiguedad = $('#forma-nominaempleado-window').find('input[name=antiguedad]');
		var $curp = $('#forma-nominaempleado-window').find('input[name=curp]');
		var $clabe = $('#forma-nominaempleado-window').find('input[name=clabe]');
		var $imss = $('#forma-nominaempleado-window').find('input[name=imss]');
		var $reg_patronal = $('#forma-nominaempleado-window').find('input[name=reg_patronal]');
		var $salario_base = $('#forma-nominaempleado-window').find('input[name=salario_base]');
		var $fecha_ini_pago = $('#forma-nominaempleado-window').find('input[name=fecha_ini_pago]');
		var $fecha_fin_pago = $('#forma-nominaempleado-window').find('input[name=fecha_fin_pago]');
		var $salario_integrado = $('#forma-nominaempleado-window').find('input[name=salario_integrado]');
		var $no_dias_pago = $('#forma-nominaempleado-window').find('input[name=no_dias_pago]');
		
		var $concepto_descripcion = $('#forma-nominaempleado-window').find('input[name=concepto_descripcion]');
		var $concepto_unidad = $('#forma-nominaempleado-window').find('input[name=concepto_unidad]');
		var $concepto_cantidad = $('#forma-nominaempleado-window').find('input[name=concepto_cantidad]');
		var $concepto_valor_unitario = $('#forma-nominaempleado-window').find('input[name=concepto_valor_unitario]');
		var $concepto_importe = $('#forma-nominaempleado-window').find('input[name=concepto_importe]');
		
		var $descuento = $('#forma-nominaempleado-window').find('input[name=descuento]');
		var $motivo_descuento = $('#forma-nominaempleado-window').find('input[name=motivo_descuento]');
		
		var $select_impuesto_retencion = $('#forma-nominaempleado-window').find('select[name=select_impuesto_retencion]');
		var $importe_retencion = $('#forma-nominaempleado-window').find('input[name=importe_retencion]');
		
		var $comp_subtotal = $('#forma-nominaempleado-window').find('input[name=comp_subtotal]');
		var $comp_descuento = $('#forma-nominaempleado-window').find('input[name=comp_descuento]');
		var $comp_retencion = $('#forma-nominaempleado-window').find('input[name=comp_retencion]');
		var $comp_total = $('#forma-nominaempleado-window').find('input[name=comp_total]');
		
		var $select_lista_percepciones = $('#forma-nominaempleado-window').find('select[name=select_lista_percepciones]');
		var $agregar_percepcion = $('#forma-nominaempleado-window').find('#agregar_percepcion');
		var $grid_percepciones = $('#forma-facnomina-window').find('#grid_percepciones');
		var $percep_total_gravado = $('#forma-nominaempleado-window').find('input[name=percep_total_gravado]');
		var $percep_total_excento = $('#forma-nominaempleado-window').find('input[name=percep_total_excento]');
		
		var $select_lista_deducciones = $('#forma-nominaempleado-window').find('select[name=select_lista_deducciones]');
		var $agregar_deduccion = $('#forma-nominaempleado-window').find('#agregar_deduccion');
		var $grid_deducciones = $('#forma-facnomina-window').find('#grid_deducciones');
		var $deduc_total_gravado = $('#forma-nominaempleado-window').find('input[name=deduc_total_gravado]');
		var $deduc_total_excento = $('#forma-nominaempleado-window').find('input[name=deduc_total_excento]');
		
		var $agregar_hora_extra = $('#forma-nominaempleado-window').find('#agregar_hora_extra');
		var $grid_horas_extras = $('#forma-facnomina-window').find('#grid_horas_extras');
		
		var $agregar_incapacidad = $('#forma-nominaempleado-window').find('#agregar_incapacidad');
		var $grid_incapacidades = $('#forma-facnomina-window').find('#grid_incapacidades');
		
		var $select_departamento = $('#forma-nominaempleado-window').find('select[name=select_departamento]');
		var $select_puesto = $('#forma-nominaempleado-window').find('select[name=select_puesto]');
		var $select_reg_contratacion = $('#forma-nominaempleado-window').find('select[name=select_reg_contratacion]');
		var $select_tipo_contrato = $('#forma-nominaempleado-window').find('select[name=select_tipo_contrato]');
		var $select_tipo_jornada = $('#forma-nominaempleado-window').find('select[name=select_tipo_jornada]');
		var $select_preriodo_pago = $('#forma-nominaempleado-window').find('select[name=select_preriodo_pago]');
		var $select_banco = $('#forma-nominaempleado-window').find('select[name=select_banco]');
		var $select_riesgo_puesto = $('#forma-nominaempleado-window').find('select[name=select_riesgo_puesto]');
		
		
		var $cierra_forma_nominaempleado = $('#forma-nominaempleado-window').find('#cierra_forma_nominaempleado');
		var $boton_cancelar_forma_nominaempleado = $('#forma-nominaempleado-window').find('#boton_cancelar_forma_nominaempleado');
		var $boton_actualizar_forma_nominaempleado = $('#forma-nominaempleado-window').find('#boton_actualizar_forma_nominaempleado');
		
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataNominaEmpleado.json';
		$arreglo = {"id_reg":id_reg, "id_empleado":id_empleado, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json,$arreglo,function(entry){
			//arrayPar, arrayDeptos, arrayPuestos, arrayRegimenContrato, arrayTipoContrato, arrayTipoJornada, arrayPeriodicidad, arrayBancos, arrayRiesgos, arrayImpuestoRet, arrayPercep, arrayDeduc
			var elemento_seleccionado=0;
			var texto_elemento_cero = '[--Seleccionar Departamento--]';
			var campo_indice = 'id';
			var campo_valor = 'titulo';
			$carga_campos_select($select_departamento, arrayDeptos, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, false);
			
			
			elemento_seleccionado=0;
			texto_elemento_cero = '[--Seleccionar Puesto--]';
			campo_indice = 'id';
			campo_valor = 'titulo';
			$carga_campos_select($select_puesto, arrayPuestos, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, true);
			
			
			
			
		},"json");
		
		
		
		/*
		$boton_actualizar_forma_nominaempleado.bind('click',function(){
			//Llamada a la funcion que valida los campos
			var dir_loc_alt;
			var dir_fax;
			
			if($textarea_localternativa.val() == ""){
				dir_loc_alt =" ";
			}else{
				dir_loc_alt = $textarea_localternativa.val();
			}
			
			if($campo_fax.val() == ""){
				dir_fax=" ";
			}else{
				dir_fax = $campo_fax.val();
			}
			
			var confirma = '';
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/valida_direcciones_nominaempleado.json';
			$arreglo = {
				'pais':$select_pais.val(),
				'entidad':$select_entidad.val(),
				'localidad':$select_localidad.val(),
				'calle':$campo_calle.val(),
				'numero':$campo_numero.val(),
				'colonia':$campo_colonia.val(),
				'cp':$campo_cp.val(),
				'telefono':$campo_telefono.val(),
				'localternativa':dir_loc_alt,
				'fax':dir_fax
			};
			
			
			$.post(input_json,$arreglo,function(entry){
				//confirma = entry['success']['fn_validaciones_dir_nominaempleado_cliente'];
				confirma = entry['success'];
				if ( confirma == "true" ){
					//llamada a la funcion que agrega tr al grid de direcciones de nominaempleado
					$agrega_tr_grid_nominaempleado(numFila,$select_pais,$select_entidad,$select_localidad,$campo_calle,$campo_numero,$campo_colonia,$campo_cp,$campo_telefono,$textarea_localternativa,$campo_fax);

					var remove = function() { $(this).remove(); };
					$('#forma-nominaempleado-overlay').fadeOut(remove);
				}else{
					// Desaparece todas las interrogaciones si es que existen
					$('#forma-nominaempleado-window').find('div.interrogacion').css({'display':'none'});
					var valor = confirma.split('___');
					//muestra las interrogaciones
					for (var element in valor){
						tmp = confirma.split('___')[element];
						longitud = tmp.split(':');
						if( longitud.length > 1 ){
							$('#forma-nominaempleado-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
							.parent()
							.css({'display':'block'})
							.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
						}
					}
				}
			},"json");
		});
		*/
		
		$boton_cancelar_forma_nominaempleado.click(function(event){
			event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-nominaempleado-overlay').fadeOut(remove);
		});
		
		$cierra_forma_nominaempleado.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-nominaempleado-overlay').fadeOut(remove);
		});
	}
	//Termina forma para nominaempleado
	
	
	
	
	
	
	
	
	
	
	
	$agrega_empleado_grid = function($grid_empleados, id_reg, id_empleado, nombre_empleado, arrayPar, arrayDeptos, arrayPuestos, arrayRegimenContrato, arrayTipoContrato, arrayTipoJornada, arrayPeriodicidad, arrayBancos, arrayRiesgos, arrayImpuestoRet, arrayPercep, arrayDeduc){
		//Obtiene numero de trs
		var tr = $("tr", $grid_empleados).size();
		tr++;
		
		var trr = '';
		trr = '<tr>';
		trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="25">';
			//trr += '<input type="checkbox" name="micheck" value="true" '+check+' '+desactivado+'>';
			//trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
			trr += '<input type="checkbox" name="micheck" value="'+ id_empleado +'">';
			trr += '<input type="hidden" name="id_reg" id="id_reg" value="'+ id_reg +'">';
			trr += '<input type="hidden" name="id_emp" id="id_emp" value="'+ id_empleado +'">';
			trr += '<input type="hidden" name="selec" id="selec" value="1">';
			trr += '<input type="hidden" name="noTr" value="'+ tr +'">';
		trr += '</td>';
		trr += '<td class="grid" style="font-size:11px;  border:1px solid #C1DAD7;" width="50">';
			trr += '<a href="#" id="editar'+ tr +'">Editar</a>';
		trr += '</td>';
		trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="400">'+ nombre_empleado +'</td>';
		trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:right;" width="120">';
			trr += '<span id="tpercep">0.00</span>';
			//trr += '<input type="text" name="tpercep'+ tr +'" value="0.00" id="tpercep" class="borde_oculto" readOnly="true" style="width:116px; text-align:right;">';
		trr += '</td>';
		trr += '<td class="grid" style="font-size:11px; border:1px solid #C1DAD7; text-align:right;" width="120">';
			trr += '<span id="tdeduc">0.00</span>';
			//trr += '<input type="text" name="tdeduc'+ tr +'" value="0.00" id="tdeduc" class="borde_oculto" readOnly="true" style="width:116px; text-align:right;">';
		trr += '</td>';
		trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:right;" width="105">';
			trr += '<span id="neto_pagar">0.00</span>';
			//trr += '<input type="text" name="neto'+ tr +'" value="0.00" id="neto" class="borde_oculto" readOnly="true" style="width:101px; text-align:right;">';
		trr += '</td>';
		trr += '</tr>';
		$grid_empleados.append(trr);
		
		
		
		$grid_empleados.find('#editar'+ tr).click(function(){
			$fila=$(this).parent().parent();
			//alert($fila.find('#id_emp').val());
			
			var idEmpleado = $fila.find('#id_emp').val();
			var id_reg = $fila.find('#id_reg').val();
			var $total_percep = $fila.find('#tpercep').val();
			var $total_deduc = $fila.find('#tdeduc').val();
			var $neto_pagar = $fila.find('#neto_pagar').val();
			
			//Llamada a la función que crea la ventana para la nomina del empleado
			$forma_nomina_empleado(idEmpleado, id_reg, $total_percep, $total_deduc, $neto_pagar, arrayPar, arrayDeptos, arrayPuestos, arrayRegimenContrato, arrayTipoContrato, arrayTipoJornada, arrayPeriodicidad, arrayBancos, arrayRiesgos, arrayImpuestoRet, arrayPercep, arrayDeduc);
		});
	}
	
	//arrayPar, arrayDeptos, arrayPuestos, arrayRegimenContrato, arrayTipoContrato, arrayTipoJornada, arrayPeriodicidad, arrayBancos, arrayRiesgos, arrayImpuestoRet, arrayPercep, arrayDeduc
	
	$get_empleados = function(id_periodicidad_pago, $grid_empleados, $select_comp_periodicidad, arrayPar, arrayDeptos, arrayPuestos, arrayRegimenContrato, arrayTipoContrato, arrayTipoJornada, arrayPeriodicidad, arrayBancos, arrayRiesgos, arrayImpuestoRet, arrayPercep, arrayDeduc){
		$grid_empleados.children().remove();
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEmpleados.json';
		$arreglo = {'id':id_periodicidad_pago, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
		$.post(input_json,$arreglo,function(entry){
			if (entry['Empleados'].length > 0){
				$.each(entry['Empleados'],function(entryIndex,data){
					var id_reg = data['id_reg'];
					var id_empleado = data['id'];
					var nombre_empleado = data['nombre'];
					$agrega_empleado_grid($grid_empleados, id_reg, id_empleado, nombre_empleado, arrayPar, arrayDeptos, arrayPuestos, arrayRegimenContrato, arrayTipoContrato, arrayTipoJornada, arrayPeriodicidad, arrayBancos, arrayRiesgos, arrayImpuestoRet, arrayPercep, arrayDeduc);
				});
			}else{
				jAlert('No hay empleados que se le pague con la Periodicidad seleccionada.', 'Atencion!', function(r) { 
					$select_comp_periodicidad.focus();
				});
			}
		},"json");
	}
	
	
	
	$get_periodos_por_tipo_periodicidad = function(id_periodicidad_pago, id_periodo_selec, $select_no_periodo){
		$select_no_periodo.children().remove();
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPeriodosPorTipoPeridicidad.json';
		$arreglo = {'id':id_periodicidad_pago, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
		$.post(input_json,$arreglo,function(entry){
			elemento_seleccionado = id_periodo_selec;
			texto_elemento_cero = '[-Seleccionar-]';
			campo_indice = 'id';
			campo_valor = 'periodo';
			$carga_campos_select($select_no_periodo, entry['Periodos'], elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, false);
			
			if (entry['Periodos'].length <= 0){
				jAlert('No hay periodos configurados para la Periodicidad seleccionada.', 'Atencion!', function(r) { 
					$select_no_periodo.focus();
				});
			}
		},"json");
	}
	
	
	
	
	
	
	//Nuevo 
	$nuevo.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_facnomina();
		
		var form_to_show = 'formafacnomina00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$('#forma-facnomina-window').css({"margin-left": -340, 	"margin-top": -220});
		
		$forma_selected.prependTo('#forma-facnomina-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		//var json_string = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + accion + '/' + id_to_show + '/out.json';
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getNomina.json';
		$arreglo = {'identificador':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
        
		var $identificador = $('#forma-facnomina-window').find('input[name=identificador]');
		var $accion = $('#forma-facnomina-window').find('input[name=accion]');
		
		var $emisor_rfc = $('#forma-facnomina-window').find('input[name=emisor_rfc]');
		var $emisor_nombre = $('#forma-facnomina-window').find('input[name=emisor_nombre]');
		var $emisor_regimen = $('#forma-facnomina-window').find('input[name=emisor_regimen]');
		var $emisor_dir = $('#forma-facnomina-window').find('input[name=emisor_dir]');
		var $comp_tipo = $('#forma-facnomina-window').find('input[name=comp_tipo]');
		var $comp_forma_pago = $('#forma-facnomina-window').find('input[name=comp_forma_pago]');
		var $comp_tc = $('#forma-facnomina-window').find('input[name=comp_tc]');
		var $comp_no_cuenta = $('#forma-facnomina-window').find('input[name=comp_no_cuenta]');
		var $fecha_pago = $('#forma-facnomina-window').find('input[name=fecha_pago]');
		
		var $select_comp_metodo_pago = $('#forma-facnomina-window').find('select[name=select_comp_metodo_pago]');
		var $select_comp_moneda = $('#forma-facnomina-window').find('select[name=select_comp_moneda]');
		var $select_comp_periodicidad = $('#forma-facnomina-window').find('select[name=select_comp_periodicidad]');
		var $periodicidad_selec = $('#forma-facnomina-window').find('input[name=periodicidad_selec]');
		var $select_no_periodo = $('#forma-facnomina-window').find('select[name=select_no_periodo]');
		var $no_periodo_selec = $('#forma-facnomina-window').find('input[name=no_periodo_selec]');
		
		//Grid de productos
		var $grid_empleados = $('#forma-facnomina-window').find('#grid_empleados');
		
		
		//Grid de errores
		var $grid_warning = $('#forma-facnomina-window').find('#div_warning_grid').find('#grid_warning');
		
		
		var $cerrar_plugin = $('#forma-facnomina-window').find('#close');
		var $cancelar_plugin = $('#forma-facnomina-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-facnomina-window').find('#submit');
		
		//Para nuevo el identificador siempre es 0
		$identificador.val(id_to_show);
		$accion.val("new");
		$permitir_solo_numeros($comp_tc);
		
		$aplicar_readonly_input($emisor_rfc);
		$aplicar_readonly_input($emisor_nombre);
		$aplicar_readonly_input($emisor_regimen);
		$aplicar_readonly_input($emisor_dir);
		$aplicar_readonly_input($comp_tipo);
		$aplicar_readonly_input($comp_forma_pago);
		
		//quitar enter a todos los campos input
		$('#forma-facnomina-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("El Pedido se guard&oacute; con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-facnomina-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				//$('#forma-facnomina-window').find('.div_one').css({'height':'545px'});//sin errores
				$('#forma-facnomina-window').find('.facnomina_div_one').css({'height':'580px'});//con errores
				
				if(parseFloat($ieps.val())>0 && parseFloat($impuesto_retenido.val())<=0){
					$('#forma-facnomina-window').find('.facnomina_div_one').css({'height':'580px'});
				}
				
				if(parseFloat($ieps.val())<=0 && parseFloat($impuesto_retenido.val())>0){
					$('#forma-facnomina-window').find('.facnomina_div_one').css({'height':'580px'});
				}
				
				if(parseFloat($ieps.val())>0 && parseFloat($impuesto_retenido.val())>0){
					$('#forma-facnomina-window').find('.facnomina_div_one').css({'height':'600px'});
				}
				
				$('#forma-facnomina-window').find('div.interrogacion').css({'display':'none'});
				
				$grid_empleados.find('#cant').css({'background' : '#ffffff'});
				$grid_empleados.find('#cost').css({'background' : '#ffffff'});
				$grid_empleados.find('#pres').css({'background' : '#ffffff'});
				
				$('#forma-facnomina-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-facnomina-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-facnomina-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						//alert(tmp.split(':')[0]);
						
						var campo = tmp.split(':')[0];
						var $campo_input;
						var cantidad_existencia=0;
						var  width_td=0;
						
						if((tmp.split(':')[0].substring(0, 8) == 'cantidad') || (tmp.split(':')[0].substring(0, 5) == 'costo') || (tmp.split(':')[0].substring(0, 12) == 'presentacion')){
							
							$('#forma-facnomina-window').find('#div_warning_grid').css({'display':'block'});
							
							if(tmp.split(':')[0].substring(0, 12) == 'presentacion'){
								$campo_input = $grid_empleados.find('input[name='+campo+']');
							}else{
								$campo_input = $grid_empleados.find('.'+campo);
							}
							$campo_input.css({'background' : '#d41000'});
							
							var codigo_producto = $campo_input.parent().parent().find('input[name=sku]').val();
							var titulo_producto = $campo_input.parent().parent().find('input[name=nombre]').val();
							
							if($incluye_produccion.val() == 'true' ){
								width_td = 370;
							}else{
								width_td = 255;
							}
							
							var tr_warning = '<tr>';
									tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
									tr_warning += '<td width="90"><INPUT TYPE="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:88px; color:red"></td>';
									tr_warning += '<td width="160"><INPUT TYPE="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:160px; color:red"></td>';
									tr_warning += '<td width="'+width_td+'"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:'+(parseInt(width_td) - 5)+'px; color:red"></td>';
							tr_warning += '</tr>';
							
							$('#forma-facnomina-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
						}
						
						if(campo == 'backorder'){
							$campo_input = $grid_empleados.find('.'+tmp.split(':')[1]);
							cantidad_existencia = tmp.split(':')[2];
							var cant_prod = parseFloat( $campo_input.val() ) - parseFloat(cantidad_existencia);
							
							$campo_input.parent().parent().find('input[name=produccion]').val(parseFloat(cant_prod).toFixed(2));
							$campo_input.parent().parent().find('input[name=existencia]').val(parseFloat(cantidad_existencia).toFixed(2));
							
							if(parseFloat(cant_prod) > 0 ){
								$campo_input.parent().parent().find('input[name=checkProd]').show();
							}
						}
						
					}
				}
				
				$grid_warning.find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
				$grid_warning.find('tr:even').find('td').css({'background-color' : '#e7e8ea'});
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		$.post(input_json,$arreglo,function(entry){
			
			//alert(entry['Extra'][0]['identificador']);
			$identificador.val(entry['Extra'][0]['identificador']);
			$emisor_rfc.val(entry['Extra'][0]['emp_rfc']);
			$emisor_nombre.val(entry['Extra'][0]['emp_razon_social']);
			$emisor_regimen.val(entry['Extra'][0]['emp_regimen_fiscal']);
			$emisor_dir.val(entry['Extra'][0]['emp_direccion']);
			
			$comp_tipo.val(entry['Par'][0]['tipo_comprobante']);
			$comp_forma_pago.val(entry['Par'][0]['forma_pago']);
			$comp_no_cuenta.val(entry['Par'][0]['no_cuenta_pago']);
			$comp_tc.val(entry['Par'][0]['tc']);
			
			var $total_percep="";
			var $total_deduc="";
			var $neto_pagar="";
			//$forma_nomina_empleado($identificador.val(), $total_percep, $total_deduc, $neto_pagar);
			
			/*
			$fecha_pago
			*/
			
			var elemento_seleccionado=0;
			var texto_elemento_cero = '[--Seleccionar--]';
			var campo_indice = 'id';
			var campo_valor = 'titulo';
			$carga_campos_select($select_comp_metodo_pago, entry['MetodosPago'], elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, false);
			
			
			elemento_seleccionado=entry['Par'][0]['mon_id'];
			texto_elemento_cero = '';
			campo_indice = 'id';
			campo_valor = 'descripcion';
			$carga_campos_select($select_comp_moneda, entry['Monedas'], elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, true);
			
			
			elemento_seleccionado=0;
			texto_elemento_cero = '[-Seleccionar-]';
			campo_indice = 'id';
			campo_valor = 'titulo';
			$carga_campos_select($select_comp_periodicidad, entry['Periodicidad'], elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, false);
			
			//entry['Par'], entry['Deptos'], entry['Puestos'], entry['RegimenContrato'], entry['TipoContrato'], entry['TipoJornada'], entry['Periodicidad'], entry['Bancos'], entry['Riesgos'], entry['ImpuestoRet'], entry['Percepciones'], entry['Deducciones']
			
			
			
			//Cambiar la periodicidad
			$select_comp_periodicidad.change(function(){
				var valor = $(this).val();
				var id_periodicidad_pago=0;
				if(parseInt(valor)>0){
					if(parseInt($periodicidad_selec.val())>0){
						if(parseInt($("tr", $grid_empleados).size())>0){
							jConfirm('Hay empleados en la Lista. Al cambiar la Periodicidad de Pago, se sustituir&aacute; el listado actual por una nueva lista.\nEst&aacute; seguro que desea cambiar los empleados del listado actual?', 'Dialogo de Confirmacion', function(r) {
								if (r){
									id_periodicidad_pago=0;
									$get_empleados(valor, $grid_empleados, $select_comp_periodicidad, entry['Par'], entry['Deptos'], entry['Puestos'], entry['RegimenContrato'], entry['TipoContrato'], entry['TipoJornada'], entry['Periodicidad'], entry['Bancos'], entry['Riesgos'], entry['ImpuestoRet'], entry['Percepciones'], entry['Deducciones']);
									$get_periodos_por_tipo_periodicidad(valor, id_periodicidad_pago, $select_no_periodo);
									$periodicidad_selec.val(valor);
									$no_periodo_selec.val(id_periodicidad_pago);
								}else{
									//Volvemos a cargar el select para dejar seleccionado la Periodicidad anterior
									elemento_seleccionado=$periodicidad_selec.val();
									texto_elemento_cero = '[-Seleccionar-]';
									campo_indice = 'id';
									campo_valor = 'titulo';
									$carga_campos_select($select_comp_periodicidad, entry['Periodicidad'], elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, false);
									$get_periodos_por_tipo_periodicidad($periodicidad_selec.val(), $no_periodo_selec.val(), $select_no_periodo);
								}
							});
						}else{
							id_periodicidad_pago=0;
							$get_empleados(valor, $grid_empleados, $select_comp_periodicidad, entry['Par'], entry['Deptos'], entry['Puestos'], entry['RegimenContrato'], entry['TipoContrato'], entry['TipoJornada'], entry['Periodicidad'], entry['Bancos'], entry['Riesgos'], entry['ImpuestoRet'], entry['Percepciones'], entry['Deducciones']);
							$get_periodos_por_tipo_periodicidad(valor, id_periodicidad_pago, $select_no_periodo);
							$periodicidad_selec.val(valor);
							$no_periodo_selec.val(id_periodicidad_pago);
						}
					}else{
						id_periodicidad_pago=0;
						$get_empleados(valor, $grid_empleados, $select_comp_periodicidad, entry['Par'], entry['Deptos'], entry['Puestos'], entry['RegimenContrato'], entry['TipoContrato'], entry['TipoJornada'], entry['Periodicidad'], entry['Bancos'], entry['Riesgos'], entry['ImpuestoRet'], entry['Percepciones'], entry['Deducciones']);
						$get_periodos_por_tipo_periodicidad(valor, id_periodicidad_pago, $select_no_periodo);
						$periodicidad_selec.val(valor);
						$no_periodo_selec.val(id_periodicidad_pago);
					}
				}else{
					jAlert('Opcion no valido.', 'Atencion!', function(r) {
						//Volvemos a cargar el select para dejar seleccionado la Periodicidad anterior
						elemento_seleccionado = $periodicidad_selec.val();
						texto_elemento_cero = '[-Seleccionar-]';
						campo_indice = 'id';
						campo_valor = 'titulo';
						$carga_campos_select($select_comp_periodicidad, entry['Periodicidad'], elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, false);
						$get_periodos_por_tipo_periodicidad($periodicidad_selec.val(), $no_periodo_selec.val(), $select_no_periodo);
						$select_comp_periodicidad.focus();
					});
				}
			});
			
			
			//Cambiar el periodo
			$select_no_periodo.change(function(){
				var valor = $(this).val();
				$no_periodo_selec.val(valor);
			});
			
		},"json");//termina llamada json
		
		
		
		
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_empleados).size();
			$total_tr.val(trCount);
			if(parseInt(trCount) > 0){
				$subtotal.val(quitar_comas($subtotal.val()));
				$impuesto.val(quitar_comas($impuesto.val()));
				$total.val(quitar_comas($total.val()));
				return true;
			}else{
				//jAlert("No hay datos para actualizar", 'Atencion!');
				jAlert('No hay datos para actualizar', 'Atencion!', function(r) { $sku_producto.focus(); });
				return false;
			}
		});
		
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-facnomina-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-facnomina-overlay').fadeOut(remove);
		});
		
	});
	
	
	
	var carga_formafacnomina00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una prefactura
		if(accion_mode == 'cancel'){
			
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id_pedido':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
			jConfirm('Realmente desea eliminar  el pedido?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El pedido fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El pedido no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
			
		}else{
			//aqui  entra para editar un registro
			$('#forma-facnomina-window').remove();
			$('#forma-facnomina-overlay').remove();
            
			var form_to_show = 'formafacnomina00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_facnomina();
			
			$('#forma-facnomina-window').css({"margin-left": -400, 	"margin-top": -235});
			
			$forma_selected.prependTo('#forma-facnomina-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $total_tr = $('#forma-facnomina-window').find('input[name=total_tr]');
			var $id_pedido = $('#forma-facnomina-window').find('input[name=id_pedido]');
			var $accion_proceso = $('#forma-facnomina-window').find('input[name=accion_proceso]');
			var $folio = $('#forma-facnomina-window').find('input[name=folio]');
			var $incluye_produccion = $('#forma-facnomina-window').find('input[name=incluye_pro]');
			
			var $busca_cliente = $('#forma-facnomina-window').find('a[href*=busca_cliente]');
			var $id_cliente = $('#forma-facnomina-window').find('input[name=id_cliente]');
			var $nocliente = $('#forma-facnomina-window').find('input[name=nocliente]');
			var $razon_cliente = $('#forma-facnomina-window').find('input[name=razoncliente]');
			var $id_df = $('#forma-facnomina-window').find('input[name=id_df]');
			var $dir_cliente = $('#forma-facnomina-window').find('input[name=dircliente]');
			var $empresa_immex = $('#forma-facnomina-window').find('input[name=empresa_immex]');
			var $tasa_ret_immex = $('#forma-facnomina-window').find('input[name=tasa_ret_immex]');
			var $cliente_listaprecio=  $('#forma-facnomina-window').find('input[name=num_lista_precio]');
			
			var $select_moneda = $('#forma-facnomina-window').find('select[name=select_moneda]');
			var $select_moneda_original = $('#forma-facnomina-window').find('input[name=select_moneda_original]');
			var $tipo_cambio = $('#forma-facnomina-window').find('input[name=tipo_cambio]');
			var $tipo_cambio_original = $('#forma-facnomina-window').find('input[name=tipo_cambio_original]');
			var $orden_compra = $('#forma-facnomina-window').find('input[name=orden_compra]');
			var	$orden_compra_original = $('#forma-facnomina-window').find('input[name=orden_compra_original]');
			
			var $id_impuesto = $('#forma-facnomina-window').find('input[name=id_impuesto]');
			var $valor_impuesto = $('#forma-facnomina-window').find('input[name=valorimpuesto]');
			
			var $check_enviar_obser = $('#forma-facnomina-window').find('input[name=check_enviar_obser]');
			var $observaciones = $('#forma-facnomina-window').find('textarea[name=observaciones]');
			var $observaciones_original = $('#forma-facnomina-window').find('textarea[name=observaciones_original]');
			
			var $select_condiciones = $('#forma-facnomina-window').find('select[name=select_condiciones]');
			var $select_condiciones_original = $('#forma-facnomina-window').find('select[name=select_condiciones_original]');
			
			var $select_vendedor = $('#forma-facnomina-window').find('select[name=vendedor]');
			var $select_vendedor_original = $('#forma-facnomina-window').find('select[name=vendedor_original]');
			var $select_almacen = $('#forma-facnomina-window').find('select[name=select_almacen]');
			
			var $select_metodo_pago = $('#forma-facnomina-window').find('select[name=select_metodo_pago]');
			var $no_cuenta = $('#forma-facnomina-window').find('input[name=no_cuenta]');
			var $etiqueta_digit = $('#forma-facnomina-window').find('input[name=etiqueta_digit]');
			var $cta_mn = $('#forma-facnomina-window').find('input[name=cta_mn]');
			var $cta_usd = $('#forma-facnomina-window').find('input[name=cta_usd]');
			var $check_ruta = $('#forma-facnomina-window').find('input[name=check_ruta]');
			
			var $transporte = $('#forma-facnomina-window').find('input[name=transporte]');
			var $transporte_original = $('#forma-facnomina-window').find('input[name=transporte_original]');
			
			var $lugar_entrega = $('#forma-facnomina-window').find('input[name=lugar_entrega]');
			var $lugar_entrega_original = $('#forma-facnomina-window').find('input[name=lugar_entrega_original]');
			
			var $fecha_compromiso = $('#forma-facnomina-window').find('input[name=fecha_compromiso]');
			var $fecha_compromiso_original = $('#forma-facnomina-window').find('input[name=fecha_compromiso_original]');
			
			//var $select_almacen = $('#forma-facnomina-window').find('select[name=almacen]');
			var $sku_producto = $('#forma-facnomina-window').find('input[name=sku_producto]');
			var $nombre_producto = $('#forma-facnomina-window').find('input[name=nombre_producto]');
			
			//buscar producto
			var $busca_sku = $('#forma-facnomina-window').find('a[href*=busca_sku]');
			//href para agregar producto al grid
			var $agregar_producto = $('#forma-facnomina-window').find('a[href*=agregar_producto]');
			
			
			var $descargarpdf = $('#forma-facnomina-window').find('#descargarpdf');
			var $cancelar_pedido = $('#forma-facnomina-window').find('#cancelar_pedido');
			var $cancelado = $('#forma-facnomina-window').find('input[name=cancelado]');
			
			//grid de productos
			var $grid_empleados = $('#forma-facnomina-window').find('#grid_empleados');
			//grid de errores
			var $grid_warning = $('#forma-facnomina-window').find('#div_warning_grid').find('#grid_warning');
			
			//var $flete = $('#forma-facnomina-window').find('input[name=flete]');
			var $subtotal = $('#forma-facnomina-window').find('input[name=subtotal]');
			var $ieps = $('#forma-facnomina-window').find('input[name=ieps]');
			var $impuesto = $('#forma-facnomina-window').find('input[name=impuesto]');
			var $campo_impuesto_retenido = $('#forma-facnomina-window').find('input[name=impuesto_retenido]');
			var $total = $('#forma-facnomina-window').find('input[name=total]');
			
			//Variables para transportista
			var $pestana_transportista = $('#forma-facnomina-window').find('ul.pestanas').find('a[href=#tabx-2]');
			var $transportista = $('#forma-facnomina-window').find('input[name=transportista]');
			var $check_flete = $('#forma-facnomina-window').find('input[name=check_flete]');
			var $nombre_documentador = $('#forma-facnomina-window').find('input[name=nombre_documentador]');
			var $valor_declarado = $('#forma-facnomina-window').find('input[name=valor_declarado]');
			var $select_tviaje = $('#forma-facnomina-window').find('select[name=select_tviaje]');
			var $remolque1 = $('#forma-facnomina-window').find('input[name=remolque1]');
			var $remolque2 = $('#forma-facnomina-window').find('input[name=remolque2]');
			
			var $id_vehiculo = $('#forma-facnomina-window').find('input[name=id_vehiculo]');
			var $no_economico = $('#forma-facnomina-window').find('input[name=no_economico]');
			var $marca_vehiculo = $('#forma-facnomina-window').find('input[name=marca_vehiculo]');
			
			var $no_operador = $('#forma-facnomina-window').find('input[name=no_operador]');
			var $nombre_operador = $('#forma-facnomina-window').find('input[name=nombre_operador]');
			
			var $agena_id = $('#forma-facnomina-window').find('input[name=agena_id]');
			var $agena_no = $('#forma-facnomina-window').find('input[name=agena_no]');
			var $agena_nombre = $('#forma-facnomina-window').find('input[name=agena_nombre]');
			
			var $select_pais_origen = $('#forma-facnomina-window').find('select[name=select_pais_origen]');
			var $select_estado_origen = $('#forma-facnomina-window').find('select[name=select_estado_origen]');
			var $select_municipio_origen = $('#forma-facnomina-window').find('select[name=select_municipio_origen]');
			
			var $select_pais_dest = $('#forma-facnomina-window').find('select[name=select_pais_dest]');
			var $select_estado_dest = $('#forma-facnomina-window').find('select[name=select_estado_dest]');
			var $select_municipio_dest = $('#forma-facnomina-window').find('select[name=select_municipio_dest]');
			
			var $rem_id = $('#forma-facnomina-window').find('input[name=rem_id]');
			var $rem_no = $('#forma-facnomina-window').find('input[name=rem_no]');
			var $rem_nombre = $('#forma-facnomina-window').find('input[name=rem_nombre]');
			var $rem_dir = $('#forma-facnomina-window').find('input[name=rem_dir]');
			var $rem_dir_alterna = $('#forma-facnomina-window').find('input[name=rem_dir_alterna]');
			
			var $dest_id = $('#forma-facnomina-window').find('input[name=dest_id]');
			var $dest_no = $('#forma-facnomina-window').find('input[name=dest_no]');
			var $dest_nombre = $('#forma-facnomina-window').find('input[name=dest_nombre]');
			var $dest_dir = $('#forma-facnomina-window').find('input[name=dest_dir]');
			var $dest_dir_alterna = $('#forma-facnomina-window').find('input[name=dest_dir_alterna]');
			
			var $observaciones_transportista = $('#forma-facnomina-window').find('textarea[name=observaciones_transportista]');
			
			var $busca_vehiculo = $('#forma-facnomina-window').find('a[href=busca_vehiculo]');
			var $busca_operador = $('#forma-facnomina-window').find('a[href=busca_operador]');
			var $busca_agena = $('#forma-facnomina-window').find('a[href=busca_agena]');
			var $busca_remitente = $('#forma-facnomina-window').find('a[href=busca_remitente]');
			var $busca_dest = $('#forma-facnomina-window').find('a[href=busca_dest]');
			//Termina variables para transportista
			
			var $cerrar_plugin = $('#forma-facnomina-window').find('#close');
			var $cancelar_plugin = $('#forma-facnomina-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-facnomina-window').find('#submit');
			
			$pestana_transportista.parent().hide();
			
			//ocultar boton descargar y facturar. Despues de facturar debe mostrarse
			//$boton_descargarpdf.hide();
			//$boton_cancelarfactura.hide();
			$busca_cliente.hide();
			$razon_cliente.attr("readonly", true);
			$empresa_immex.val('false');
			$tasa_ret_immex.val('0');
			$busca_cliente.hide();
			$cancelado.hide();
			$permitir_solo_numeros($no_cuenta);
			$no_cuenta.attr('disabled','-1');
			$etiqueta_digit.attr('disabled','-1');
			$folio.css({'background' : '#F0F0F0'});
			$nocliente.css({'background' : '#F0F0F0'});
			$dir_cliente.css({'background' : '#F0F0F0'});
			
			//quitar enter a todos los campos input
			$('#forma-facnomina-window').find('input').keypress(function(e){
				if(e.which==13 ) {
					return false;
				}
			});
			
			if(accion_mode == 'edit'){
				$accion_proceso.attr({'value' : "edit"});
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPedido.json';
				$arreglo = {'id_pedido':id_to_show,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						$('#forma-facnomina-window').find('div.interrogacion').css({'display':'none'});
						
						if($accion_proceso.val() == 'cancelar'){
							if ( data['actualizo'] == "1" ){
								jAlert("El Pedido se Cancel&oacute; con &eacute;xito", 'Atencion!');
							}else{
								jAlert(data['actualizo'], 'Atencion!');
							}
						}else{
							jAlert("El Pedido se guard&oacute; con &eacute;xito", 'Atencion!');
						}
						
						var remove = function() {$(this).remove();};
						$('#forma-facnomina-overlay').fadeOut(remove);
						
						//ocultar boton actualizar porque ya se actualizo, ya no se puede guardar cambios, hay que cerrar y volver a abrir
						$submit_actualizar.hide();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						//$('#forma-facnomina-window').find('.div_one').css({'height':'545px'});//sin errores
						$('#forma-facnomina-window').find('.facnomina_div_one').css({'height':'588px'});//con errores
						if(parseFloat($ieps.val())>0 && parseFloat($campo_impuesto_retenido.val())<=0){
							$('#forma-facnomina-window').find('.facnomina_div_one').css({'height':'580px'});
						}
						
						if(parseFloat($ieps.val())<=0 && parseFloat($campo_impuesto_retenido.val())>0){
							$('#forma-facnomina-window').find('.facnomina_div_one').css({'height':'580px'});
						}
						
						if(parseFloat($ieps.val())>0 && parseFloat($campo_impuesto_retenido.val())>0){
							$('#forma-facnomina-window').find('.facnomina_div_one').css({'height':'600px'});
						}
						$('#forma-facnomina-window').find('div.interrogacion').css({'display':'none'});
						
						$grid_empleados.find('#cant').css({'background' : '#ffffff'});
						$grid_empleados.find('#cost').css({'background' : '#ffffff'});
						$grid_empleados.find('#pres').css({'background' : '#ffffff'});
						
						$('#forma-facnomina-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-facnomina-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							
							if( longitud.length > 1 ){
								$('#forma-facnomina-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								//alert(tmp.split(':')[0]);
								
								var campo = tmp.split(':')[0];
								var $campo_input;
								var cantidad_existencia=0;
								var  width_td=0;
								
								if((tmp.split(':')[0].substring(0, 8) == 'cantidad') || (tmp.split(':')[0].substring(0, 5) == 'costo') || (tmp.split(':')[0].substring(0, 12) == 'presentacion')){
									
									$('#forma-facnomina-window').find('#div_warning_grid').css({'display':'block'});
									
									if(tmp.split(':')[0].substring(0, 12) == 'presentacion'){
										$campo_input = $grid_empleados.find('input[name='+campo+']');
									}else{
										$campo_input = $grid_empleados.find('.'+campo);
									}
									
									$campo_input.css({'background' : '#d41000'});
									
									var codigo_producto = $campo_input.parent().parent().find('input[name=sku]').val();
									var titulo_producto = $campo_input.parent().parent().find('input[name=nombre]').val();
									
									if($incluye_produccion.val() == 'true' ){
										width_td = 370;
									}else{
										width_td = 255;
									}
									
									var tr_warning = '<tr>';
											tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
											tr_warning += '<td width="90"><INPUT TYPE="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:88px; color:red"></td>';
											tr_warning += '<td width="160"><INPUT TYPE="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:160px; color:red"></td>';
											tr_warning += '<td width="'+width_td+'"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:'+(parseInt(width_td) - 5)+'px; color:red"></td>';
									tr_warning += '</tr>';
									
									$('#forma-facnomina-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
								}
								
								if(campo == 'backorder'){
									$campo_input = $grid_empleados.find('.'+tmp.split(':')[1]);
									cantidad_existencia = tmp.split(':')[2];
									var cant_prod = parseFloat( $campo_input.val() ) - parseFloat(cantidad_existencia);
									
									$campo_input.parent().parent().find('input[name=produccion]').val(parseFloat(cant_prod).toFixed(2));
									$campo_input.parent().parent().find('input[name=existencia]').val(parseFloat(cantidad_existencia).toFixed(2));
									
									if(parseFloat(cant_prod) > 0 ){
										$campo_input.parent().parent().find('input[name=checkProd]').show();
									}
								}
								
							}
						}
						
						$grid_warning.find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
						$grid_warning.find('tr:even').find('td').css({'background-color' : '#e7e8ea'});
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					//Almacenar el arreglo de unidades de medida en la variable
					arrayUM = entry['UM'];
					
					//Almacenar valor para variable que indica si se debe permitir el cambio de la unidad de medida
					cambiarUM = entry['Extras'][0]['cambioUM']
					
					$incluye_produccion.val(entry['Extras']['0']['mod_produccion']);
					
					if(entry['Extras']['0']['mod_produccion']=='true'){
						$('#forma-facnomina-window').css({"margin-left": -450, 	"margin-top": -235});
						$('#forma-facnomina-window').find('.facnomina_div_one').css({'width':'1180px'});
						$('#forma-facnomina-window').find('.facnomina_div_two').css({'width':'1180px'});
						$('#forma-facnomina-window').find('#titulo_plugin').css({'width':'1140px'});
						$('#forma-facnomina-window').find('.header_grid').css({'width':'1155px'});
						$('#forma-facnomina-window').find('.contenedor_grid').css({'width':'1145px'});
						$('#forma-facnomina-window').find('#div_botones').css({'width':'1153px'});
						$('#forma-facnomina-window').find('#div_botones').find('.tabla_botones').find('.td_left').css({'width':'1053px'});
						$('#forma-facnomina-window').find('#div_warning_grid').css({'width':'810px'});
						$('#forma-facnomina-window').find('#div_warning_grid').find('.td_head').css({'width':'470px'});
						$('#forma-facnomina-window').find('#div_warning_grid').find('.div_cont_grid_warning').css({'width':'800px'});
						$('#forma-facnomina-window').find('#div_warning_grid').find('.div_cont_grid_warning').find('#grid_warning').css({'width':'780px'});
					}else{
						//ocultar td porque la empresa no incluye Produccion
						$('#forma-facnomina-window').find('.tabla_header_grid').find('#td_oculto').hide();
					}
					
					
					
					$tasa_ret_immex.val(entry['datosPedido']['0']['tasa_retencion_immex']);
					$id_pedido.val(entry['datosPedido']['0']['id']);
					$folio.val(entry['datosPedido']['0']['folio']);
					$id_cliente.val(entry['datosPedido']['0']['cliente_id']);
					$nocliente.val(entry['datosPedido']['0']['numero_control']);
					$razon_cliente.val(entry['datosPedido']['0']['razon_social']);
					$id_df.val(entry['datosPedido']['0']['df_id']);
					$dir_cliente.val(entry['datosPedido']['0']['direccion']);
					$cliente_listaprecio.val(entry['datosPedido']['0']['lista_precio']);
					
					$check_enviar_obser.attr('checked',  (entry['datosPedido']['0']['enviar_obser'] == 'true')? true:false );
					$observaciones.text(entry['datosPedido']['0']['observaciones']);
					$observaciones_original.val(entry['datosPedido']['0']['observaciones']);
					
					$orden_compra.val(entry['datosPedido']['0']['orden_compra']);
					$orden_compra_original.val(entry['datosPedido']['0']['orden_compra']);
                    
					$transporte.val(entry['datosPedido']['0']['transporte']);
					$transporte_original.val(entry['datosPedido']['0']['transporte']);
                    
					$lugar_entrega.val(entry['datosPedido']['0']['lugar_entrega']);
					$lugar_entrega_original.val(entry['datosPedido']['0']['lugar_entrega']);
					
					$fecha_compromiso.val(entry['datosPedido']['0']['fecha_compromiso']);
					$fecha_compromiso_original.val(entry['datosPedido']['0']['fecha_compromiso']);
					
					$tipo_cambio.val(entry['datosPedido']['0']['tipo_cambio']);
					$tipo_cambio_original.val(entry['datosPedido']['0']['tipo_cambio']);
					
					$no_cuenta.val(entry['datosPedido']['0']['no_cuenta']);
					
					$cta_mn.val(entry['datosPedido']['0']['cta_pago_mn']);
					$cta_usd.val(entry['datosPedido']['0']['cta_pago_usd']);
					
					$check_ruta.attr('checked',  (entry['datosPedido']['0']['enviar_ruta'] == 'true')? true:false );
					
					//carga select denominacion con todas las monedas
					$select_moneda.children().remove();
					var moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(moneda['id'] == entry['datosPedido']['0']['moneda_id']){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
							$select_moneda_original.val(moneda['id']);
						}else{
							if(parseInt(entry['datosPedido']['0']['proceso_flujo_id'])==4){
								//moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
							}
						}
					});
					$select_moneda.append(moneda_hmtl);
					$select_moneda.find('option').clone().appendTo($select_moneda_original);
                    
					//$campo_tc.val();
					//$id_impuesto.val();
					//$valor_impuesto.val();
					//$campo_tc.val(entry['tc']['tipo_cambio']);
					$id_impuesto.val(entry['iva']['0']['id_impuesto']);
					$valor_impuesto.val(entry['iva']['0']['valor_impuesto']);
					
					//carga select de vendedores
					$select_vendedor.children().remove();
					var hmtl_vendedor;
					$.each(entry['Vendedores'],function(entryIndex,vendedor){
						if(entry['datosPedido']['0']['cxc_agen_id'] == vendedor['id']){
							hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes" >' + vendedor['nombre_agente'] + '</option>';
						}else{
							if(parseInt(entry['datosPedido']['0']['proceso_flujo_id'])==4){
								hmtl_vendedor += '<option value="' + vendedor['id'] + '">' + vendedor['nombre_agente'] + '</option>';
							}
						}
					});
					$select_vendedor.append(hmtl_vendedor);
					$select_vendedor.find('option').clone().appendTo($select_vendedor_original);
					
					//carga select de condiciones
					$select_condiciones.children().remove();
					var hmtl_condiciones;
					$.each(entry['Condiciones'],function(entryIndex,condicion){
						if(entry['datosPedido']['0']['cxp_prov_credias_id'] == condicion['id']){
							hmtl_condiciones += '<option value="' + condicion['id'] + '" selected="yes" >' + condicion['descripcion'] + '</option>';
						}else{
							if(parseInt(entry['datosPedido']['0']['proceso_flujo_id'])==4){
								hmtl_condiciones += '<option value="' + condicion['id'] + '">' + condicion['descripcion'] + '</option>';
							}
						}
					});
					$select_condiciones.append(hmtl_condiciones);
					$select_condiciones.find('option').clone().appendTo($select_condiciones_original);
					
					
					//carga select de almacenes
					$select_almacen.children().remove();
					var hmtl_alm;
					$.each(entry['Almacenes'],function(entryIndex,alm){
						hmtl_alm += '<option value="' + alm['id'] + '"  >' + alm['titulo'] + '</option>';
					});
					$select_almacen.append(hmtl_alm);
					
					
					
					var valor_metodo = entry['datosPedido']['0']['metodo_pago_id'];
					
					//carga select de metodos de pago
					$select_metodo_pago.children().remove();
					var hmtl_metodo="";
					$.each(entry['MetodosPago'],function(entryIndex,metodo){
						if(valor_metodo == metodo['id']){
							hmtl_metodo += '<option value="' + metodo['id'] + '" selected="yes" >' + metodo['titulo'] + '</option>';
						}else{
							if(parseInt(entry['datosPedido']['0']['proceso_flujo_id'])==4){
								hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
							}
						}
					});
					$select_metodo_pago.append(hmtl_metodo);
					
					
					
					if(parseInt(valor_metodo)==2 || parseInt(valor_metodo)==3){
						//si esta desahabilitado, hay que habilitarlo para permitir la captura de los digitos de la tarjeta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						//quitar propiedad de solo lectura
						$no_cuenta.removeAttr('readonly');
						
						if($etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.removeAttr('disabled');
						}
						$etiqueta_digit.val('Ingrese los ultimos 4 Digitos de la Tarjeta');
					}
					
					
					if(parseInt(valor_metodo)==4 || parseInt(valor_metodo)==5){
						//si esta desahabilitado, hay que habilitarlo para permitir la captura del Numero de cuenta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						
						//fijar propiedad de solo lectura en verdadero
						$no_cuenta.attr('readonly',true);
						
						if(parseInt($select_moneda.val())==1){
							$etiqueta_digit.val('Numero de Cuenta para pago en Pesos');
						}else{
							$etiqueta_digit.val('Numero de Cuenta en Dolares');
						}
					}
					
					//valor_metodo 1=Efectivo, 6=No Identificado
					if(parseInt(valor_metodo)==1 || parseInt(valor_metodo)==6){
						//si esta desahabilitado, hay que habilitarlo para permitir la captura del Numero de cuenta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						if($etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.removeAttr('disabled');
						}
						//fijar propiedad de solo lectura en verdadero
						$no_cuenta.attr('readonly',true);
						
						if(parseInt($select_moneda.val())==1){
							$etiqueta_digit.val('Numero de Cuenta para pago en Pesos');
						}else{
							$etiqueta_digit.val('Numero de Cuenta en Dolares');
						}
					}
					
					//valor_metodo 7=NA
					if(parseInt(valor_metodo)==7){
						//si esta desahabilitado, hay que habilitarlo para permitir la captura del Numero de cuenta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						if($etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.removeAttr('disabled');
						}
						//fijar propiedad de solo lectura en verdadero
						$no_cuenta.attr('readonly',true);
						
						if(parseInt($select_moneda.val())==1){
							$etiqueta_digit.val('Numero de Cuenta para pago en Pesos');
						}else{
							$etiqueta_digit.val('Numero de Cuenta en Dolares');
						}
					}
					
					
					
					
					if(entry['datosGrid'] != null){
						$.each(entry['datosGrid'],function(entryIndex,prod){
							
							//obtiene numero de trs
							var tr = $("tr", $grid_empleados).size();
							tr++;
							
							var trr = '';
							trr = '<tr>';
							trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
									trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
									//El 1 significa que el registro no ha sido eliminado
									trr += '<input type="hidden" name="eliminado" id="elim" value="1">';
									//Este es el id del registro que ocupa el producto en la tabla facnomina_detalles
									trr += '<input type="hidden" name="iddetalle" id="idd" value="'+ prod['id_detalle'] +'">';
									trr += '<input type="hidden" name="noTr" value="'+ tr +'">';
									//trr += '<span id="elimina">1</span>';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="116">';
									trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ prod['inv_prod_id'] +'">';
									trr += '<input type="text" name="sku" value="'+ prod['codigo'] +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="200">';
								trr += '<input type="text" 	name="nombre" 	value="'+ prod['titulo'] +'" 	id="nom" class="borde_oculto" readOnly="true" style="width:196px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<select name="select_umedida" class="select_umedida'+ tr +'" style="width:86px;"></select>';
								trr += '<input type="text" 		name="unidad'+ tr +'" 	value="'+ prod['unidad'] +'" 	id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="100">';
									trr += '<input type="hidden" 	name="id_presentacion"  value="'+  prod['id_presentacion'] +'" 	id="idpres">';
									trr += '<input type="text" 		name="presentacion'+ tr +'" 	value="'+  prod['presentacion'] +'" 	id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<input type="text" 	name="cantidad" value="'+  prod['cantidad'] +'" class="cantidad'+ tr +'" id="cant" style="width:76px;">';
							trr += '</td>';
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<input type="text" 		name="costo" 	value="'+  prod['precio_unitario'] +'" 	class="costo'+ tr +'" id="cost" style="width:86px; text-align:right;">';
								trr += '<input type="hidden" value="'+  prod['precio_unitario'] +'" id="costor">';
							trr += '</td>';
							
							trr += '<td class="grid2" style="font-size:11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<input type="text" 		name="importe'+ tr +'" 	value="'+  prod['importe'] +'" 	id="import" class="borde_oculto" readOnly="true" style="width:86px; text-align:right;">';
								trr += '<input type="hidden"    name="id_imp_prod"  value="'+  prod['gral_imp_id'] +'" 		id="idimppord">';
								trr += '<input type="hidden"    name="valor_imp" 	value="'+  prod['valor_imp'] +'" 	id="ivalorimp">';
								
								var importeIeps= parseFloat(parseFloat(prod['importe']) * (parseFloat(prod['valor_ieps'])/100)).toFixed(4);
								
								trr += '<input type="hidden" 	name="totimpuesto'+ tr +'" id="totimp" value="'+(parseFloat(prod['importe']) + parseFloat(importeIeps)) * parseFloat( prod['valor_imp'] )+'">';
							trr += '</td>';
							

							
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
								trr += '<input type="hidden" name="idIeps"     value="'+ prod['ieps_id'] +'" id="idIeps">';
								trr += '<input type="text" name="tasaIeps" value="'+ prod['valor_ieps'] +'" class="borde_oculto" id="tasaIeps" style="width:56px; text-align:right;" readOnly="true">';
							trr += '</td>';
							
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<input type="text" name="importeIeps" value="'+ importeIeps +'" class="borde_oculto" id="importeIeps" style="width:76px; text-align:right;" readOnly="true">';
							trr += '</td>';
							
							
							var cant_prod = prod['cant_produccion'];
							
							trr += '<td class="grid2" id="td_oculto'+ tr +'" style="font-size:11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<input type="text" 		name="produccion" 	value="'+cant_prod+'" 	 class="borde_oculto" readOnly="true" style="width:76px; text-align:right;">';
								trr += '<input type="hidden"    name="existencia" 	value="0" 	>';
							trr += '</td>';
							
							var desactivado="";
							var check=prod['valor_check'];
							var valor_seleccionado = prod['valor_selecionado'];
							
							
							trr += '<td class="grid2" id="td_oculto'+ tr +'" style="font-size: 11px;  border:1px solid #C1DAD7;" width="20">';
								trr += '<input type="checkbox" 	name="checkProd" class="checkProd'+ tr +'" '+check+' '+desactivado+'>';
								trr += '<input type="hidden" 	name="seleccionado" value="'+valor_seleccionado+'">';//el 1 significa que el registro no ha sido eliminado
							trr += '</td>';
							
							trr += '</tr>';
							$grid_empleados.append(trr);
                            
                            
                           
							//carga select de metodos de pago
							$grid_empleados.find('select.select_umedida'+tr).children().remove();
							var hmtl_um="";
							$.each(arrayUM,function(entryIndex,um){
								if(parseInt(prod['unidad_id']) == parseInt(um['id'])){
									hmtl_um += '<option value="' + um['id'] + '" selected="yes" >' + um['titulo'] + '</option>';
								}
							});
							$grid_empleados.find('select.select_umedida'+tr).append(hmtl_um);
							
							if(cambiarUM.trim()=='true'){
								//Ocultar campo input porque se debe mostrar select para permitir cambio de unidad de medida
								$grid_empleados.find('input[name=unidad'+ tr +']').hide();
							}else{
								//Ocultar porque no se permitirá cambiar de unidad de medida
								$grid_empleados.find('select.select_umedida'+tr).hide();
							}
                            
                            
                            
                            
                            if(entry['Extras']['0']['mod_produccion']=='true'){
								//Aplicar evento click al check, cuando la empresa incluya modulo de produccion
								$aplicar_evento_click_a_input_check($grid_empleados.find('.checkProd'+ tr));
								
								if(parseFloat(cant_prod) <=0 ){
									//Ocualtar check, solo se debe mostrar cuando el producto no tenga existencia suficiente
									$grid_empleados.find('.checkProd'+tr).hide();
								}
                            }else{
								//ocualtar campos,  cuando la empresa no incluya modulo de produccion
								$grid_empleados.find('#td_oculto'+tr).hide();
							}
                            
                            
                            
							//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
							$grid_empleados.find('#cant').focus(function(e){
								if($(this).val() == ' '){
									$(this).val('');
								}
							});
							
							//recalcula importe al perder enfoque el campo cantidad
							$grid_empleados.find('#cant').blur(function(){
								var $campoCantidad = $(this);
								var $campoPrecioU = $(this).parent().parent().find('#cost');
								var $campoImporte = $(this).parent().parent().find('#import');
								
								var $campoTasaIeps = $(this).parent().parent().find('#tasaIeps');
								var $importeIeps = $(this).parent().parent().find('#importeIeps');
								
								var $campoTasaIva = $(this).parent().parent().find('#ivalorimp');
								var $importeIva = $(this).parent().parent().find('#totimp');
								
								
								if ($campoCantidad.val().trim() == ''){
									$campoCantidad.val(' ');
								}else{
									$campoCantidad.val(parseFloat($campoCantidad.val()).toFixed(parseInt(prod['no_dec'])));
								}
								
								if( ($campoCantidad.val().trim() != '') && ($campoPrecioU.val().trim() != '') ){   
									//Calcula el importe
									$campoImporte.val(parseFloat($campoCantidad.val()) * parseFloat($campoPrecioU.val()));
									
									//Redondea el importe en dos decimales
									//$(this).parent().parent().find('#import').val( Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100 );
									$campoImporte.val( parseFloat($campoImporte.val()).toFixed(4) );
									
									//Calcular el importe del IEPS
									$importeIeps.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
									
									//Calcula el IVA para este producto multiplicando el importe + ieps por la tasa del iva
									$importeIva.val((parseFloat($campoImporte.val()) + parseFloat($importeIeps.val()) ) * parseFloat( $campoTasaIva.val() ));
								}else{
									$campoImporte.val(0);
									$importeIva.val(0);
								}
								//Llamada a la funcion que calcula totales
								$calcula_totales();
							});
							
							//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
							$grid_empleados.find('#cost').focus(function(e){
								if($(this).val() == ' '){
									$(this).val('');
								}
							});
							
							//Recalcula importe al perder enfoque el campo costo
							$grid_empleados.find('#cost').blur(function(){
								var $campoCantidad = $(this).parent().parent().find('#cant');
								var $campoPrecioU = $(this);
								var $campoImporte = $(this).parent().parent().find('#import');
								
								var $campoTasaIeps = $(this).parent().parent().find('#tasaIeps');
								var $importeIeps = $(this).parent().parent().find('#importeIeps');
								
								var $campoTasaIva = $(this).parent().parent().find('#ivalorimp');
								var $importeIva = $(this).parent().parent().find('#totimp');
								
								if ($campoPrecioU.val().trim() == ''){
									$campoPrecioU.val(' ');
								}else{
									$campoPrecioU.val(parseFloat($campoPrecioU.val()).toFixed(4));
								}
								
								if( ($campoPrecioU.val().trim() != '') && ($campoCantidad.val().trim() != '') ){
									//Calcula el importe
									$campoImporte.val(parseFloat($campoPrecioU.val()) * parseFloat($campoCantidad.val()));
									//Redondea el importe en dos decimales
									//$(this).parent().parent().find('#import').val(Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100);
									$campoImporte.val( parseFloat($campoImporte.val()).toFixed(4));
									
									//Calcular el importe del IEPS
									$importeIeps.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
									
									//Calcula el impuesto para este producto multiplicando el importe por el valor del iva
									$importeIva.val((parseFloat($campoImporte.val()) + parseFloat($importeIeps.val())) * parseFloat($campoTasaIva.val()));
								}else{
									$campoImporte.val(0);
									$importeIva.val(0);
								}
								
								//Llamada a la funcion que calcula totales
								$calcula_totales();
							});
							
							//validar campo costo, solo acepte numeros y punto
							$permitir_solo_numeros( $grid_empleados.find('#cost') );
							$permitir_solo_numeros( $grid_empleados.find('#cant') );
							
							//Elimina un producto del grid
							$grid_empleados.find('#delete'+ tr).bind('click',function(event){
								event.preventDefault();
								if(parseInt($(this).parent().find('#elim').val()) != 0){
									var iddetalle = $(this).parent().find('#idd').val();
									
									//asigna espacios en blanco a todos los input de la fila eliminada
									$(this).parent().parent().find('input').val(' ');
									
									//asigna un 0 al input eliminado como bandera para saber que esta eliminado
									$(this).parent().find('#elim').val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
									$(this).parent().find('#idd').val(iddetalle);
									//oculta la fila eliminada
									$(this).parent().parent().hide();
									$calcula_totales();//llamada a la funcion que calcula totales
								}
							});
						});
						
						
					}
					
					$calcula_totales();//llamada a la funcion que calcula totales 
					
					
					
					
					
					
					
					//Inicia carga de datos para pestaña de transportista
					if(entry['Extras'][0]['transportista']=='true'){
						var elemento_seleccionado = 0;
						var texto_elemento_cero = '';
						var index_elem = '';
						var index_text_elem = '';
						
						//LLamada a la funcion para aplicar el evento change al select tipo de viaje
						$aplicar_evento_change_select_tviaje($select_tviaje, $remolque1, $remolque2);
						
						$check_flete.attr('checked',  (entry['datosPedido'][0]['flete']=='true')? true:false );
						$pestana_transportista.parent().show();
						
						if(entry['datosPedido'][0]['flete']=='true'){
							$nombre_documentador.val(entry['datosTrans'][0]['documentador'].trim());
							$valor_declarado.val(entry['datosTrans'][0]['valor_declarado'].trim());
							
							$remolque1.val(entry['datosTrans'][0]['remolque1'].trim());
							$remolque2.val(entry['datosTrans'][0]['remolque2'].trim());
							
							$id_vehiculo.val(entry['datosTrans'][0]['vehiculo_id']);
							$no_economico.val(entry['datosTrans'][0]['vehiculo_no']);
							$marca_vehiculo.val(entry['datosTrans'][0]['vehiculo_marca']);
							
							$no_operador.val(entry['datosTrans'][0]['no_operador']);
							$nombre_operador.val(entry['datosTrans'][0]['nombre_operador']);
							
							$agena_id.val(entry['datosTrans'][0]['agena_id']);
							$agena_no.val(entry['datosTrans'][0]['agena_no']);
							$agena_nombre.val(entry['datosTrans'][0]['agena_nombre']);
							
							$rem_id.val(entry['datosTrans'][0]['rem_id']);
							$rem_no.val(entry['datosTrans'][0]['rem_no']);
							$rem_nombre.val(entry['datosTrans'][0]['rem_nombre']);
							$rem_dir.val(entry['datosTrans'][0]['rem_dir']);
							$rem_dir_alterna.val(entry['datosTrans'][0]['rem_dir_alterna']);
							
							$dest_id.val(entry['datosTrans'][0]['dest_id']);
							$dest_no.val(entry['datosTrans'][0]['dest_no']);
							$dest_nombre.val(entry['datosTrans'][0]['dest_nombre']);
							$dest_dir.val(entry['datosTrans'][0]['dest_dir']);
							$dest_dir_alterna.val(entry['datosTrans'][0]['dest_dir_alterna']);
							
							$observaciones_transportista.text(entry['datosTrans'][0]['trans_observaciones']);
							
							if(parseInt(entry['datosTrans'][0]['vehiculo_id'])!=0){
								$busca_vehiculo.hide();
								$aplicar_readonly_input($marca_vehiculo);
							}
							if(entry['datosTrans'][0]['nombre_operador']!=''){
								$busca_operador.hide();
							}
							if(parseInt(entry['datosTrans'][0]['agena_id'])!=0){
								$busca_agena.hide();
								$aplicar_readonly_input($agena_nombre);
							}
							if(parseInt(entry['datosTrans'][0]['rem_id'])!=0){
								$busca_remitente.hide();
								$aplicar_readonly_input($rem_nombre);
								$aplicar_readonly_input($rem_dir);
							}
							if(parseInt(entry['datosTrans'][0]['dest_id'])!=0){
								$busca_dest.hide();
								$aplicar_readonly_input($dest_nombre);
								$aplicar_readonly_input($dest_dir);
							}
							
							var tviaje_hmtl = '';
							if(parseInt(entry['datosTrans'][0]['tipo_viaje'])==1){
								$aplicar_readonly_input($remolque2);
								tviaje_hmtl = '<option value="1" selected="yes">Sencilla</option>';
								tviaje_hmtl += '<option value="2" >Full</option>';
							}else{
								tviaje_hmtl = '<option value="1">Sencilla</option>';
								tviaje_hmtl += '<option value="2" selected="yes">Full</option>';
							}
							//Alimentar select de tipo de viaje
							$select_tviaje.children().remove();
							$select_tviaje.append(tviaje_hmtl);
							
							//carga select de pais Origen
							elemento_seleccionado = entry['datosTrans'][0]['pais_id_orig'];
							texto_elemento_cero = '[-Seleccionar Pais-]';
							index_elem = 'cve_pais';
							index_text_elem = 'pais_ent';
							$carga_campos_select($select_pais_origen, entry['Paises'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
							
							//Carga select de estado Origen
							elemento_seleccionado = entry['datosTrans'][0]['edo_id_orig'];
							texto_elemento_cero = '[-Seleccionar Estado--]';
							index_elem = 'cve_ent';
							index_text_elem = 'nom_ent';
							$carga_campos_select($select_estado_origen, entry['EdoOrig'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
							
							//Carga select de municipio Origen
							elemento_seleccionado = entry['datosTrans'][0]['mun_id_orig'];
							texto_elemento_cero = '[-Seleccionar Municipio-]';
							index_elem = 'cve_mun';
							index_text_elem = 'nom_mun';
							$carga_campos_select($select_municipio_origen, entry['MunOrig'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
							
							
							//carga select de pais Destino
							elemento_seleccionado = entry['datosTrans'][0]['pais_id_dest'];
							texto_elemento_cero = '[-Seleccionar Pais-]';
							index_elem = 'cve_pais';
							index_text_elem = 'pais_ent';
							$carga_campos_select($select_pais_dest, entry['Paises'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
							
							//Carga select de estado Destino
							elemento_seleccionado = entry['datosTrans'][0]['edo_id_dest'];
							texto_elemento_cero = '[-Seleccionar Estado--]';
							index_elem = 'cve_ent';
							index_text_elem = 'nom_ent';
							$carga_campos_select($select_estado_dest, entry['EdoDest'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
							
							//Carga select de municipio Destino
							elemento_seleccionado = entry['datosTrans'][0]['mun_id_dest'];
							texto_elemento_cero = '[-Seleccionar Municipio-]';
							index_elem = 'cve_mun';
							index_text_elem = 'nom_mun';
							$carga_campos_select($select_municipio_dest, entry['MunDest'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
						}else{
							//Aqui entra cuando no es pedido de flete
							//Esta informacion que se agrega en esta parte es para permitir al usuario la posibilidad de convertirlo en pedido de Flete
							//carga select de pais Origen
							elemento_seleccionado = 0;
							texto_elemento_cero = '[-Seleccionar Pais-]';
							index_elem = 'cve_pais';
							index_text_elem = 'pais_ent';
							$carga_campos_select($select_pais_origen, entry['Paises'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
							
							//carga select de pais Destino
							elemento_seleccionado = 0;
							texto_elemento_cero = '[-Seleccionar Pais-]';
							index_elem = 'cve_pais';
							index_text_elem = 'pais_ent';
							$carga_campos_select($select_pais_dest, entry['Paises'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
							
							$aplicar_readonly_input($remolque2);
						}
						
						
						//Carga select estados al cambiar el pais Origen
						$select_pais_origen.change(function(){
							var valor_pais = $(this).val();
							var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEstados.json';
							$arreglo = {'id_pais':valor_pais};
							$.post(input_json,$arreglo,function(entry){
								$select_estado_origen.children().remove();
								var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Estado--]</option>'
								$.each(entry['Estados'],function(entryIndex,entidad){
									entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
								});
								$select_estado_origen.append(entidad_hmtl);
								
								var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>';
								$select_municipio_origen.children().remove();
								$select_municipio_origen.append(trama_hmtl_localidades);
							},"json");//termina llamada json
						});
						
						//Carga select municipios al cambiar el estado origen
						$select_estado_origen.change(function(){
							var valor_entidad = $(this).val();
							var valor_pais = $select_pais_origen.val();
							
							var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMunicipios.json';
							$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
							$.post(input_json,$arreglo,function(entry){
								$select_municipio_origen.children().remove();
								var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>'
								$.each(entry['Municipios'],function(entryIndex,mun){
									trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
								});
								$select_municipio_origen.append(trama_hmtl_localidades);
							},"json");//termina llamada json
						});
						
						
						
						
						//Carga select estados al cambiar el pais destino
						$select_pais_dest.change(function(){
							var valor_pais = $(this).val();
							var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEstados.json';
							$arreglo = {'id_pais':valor_pais};
							$.post(input_json,$arreglo,function(entry){
								$select_estado_dest.children().remove();
								var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Estado--]</option>'
								$.each(entry['Estados'],function(entryIndex,entidad){
									entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
								});
								$select_estado_dest.append(entidad_hmtl);
								
								var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>';
								$select_municipio_dest.children().remove();
								$select_municipio_dest.append(trama_hmtl_localidades);
							},"json");//termina llamada json
						});
						
						//Carga select municipios al cambiar el estado destino
						$select_estado_dest.change(function(){
							var valor_entidad = $(this).val();
							var valor_pais = $select_pais_dest.val();
							
							var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMunicipios.json';
							$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
							$.post(input_json,$arreglo,function(entry){
								$select_municipio_dest.children().remove();
								var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>'
								$.each(entry['Municipios'],function(entryIndex,mun){
									trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
								});
								$select_municipio_dest.append(trama_hmtl_localidades);
							},"json");//termina llamada json
						});
						
						
						

											
						//Buscador de Unidades(Vehiculo)
						$busca_vehiculo.click(function(event){
							event.preventDefault();
							$busca_unidades($id_vehiculo, $no_economico, $marca_vehiculo, $busca_vehiculo);
						});
						
						$(this).aplicarEventoKeypressEjecutaTrigger($marca_vehiculo, $busca_vehiculo);
						
						$no_economico.keypress(function(e){
							var valor=$(this).val();
							if(e.which == 13){
								if($no_economico.val().trim()!=''){
									var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataUnidadByNoEco.json';
									$arreglo2 = {'no_economico':$no_economico.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
									$.post(input_json2,$arreglo2,function(entry2){
										if(parseInt(entry2['Vehiculo'].length) > 0 ){
											$id_vehiculo.val(entry2['Vehiculo'][0]['id']);
											$no_economico.val(entry2['Vehiculo'][0]['numero_economico']);
											$marca_vehiculo.val(entry2['Vehiculo'][0]['marca']);
											$busca_vehiculo.hide();
											//Aplicar solo lectura una vez que se ha escogido la unidad
											$aplicar_readonly_input($marca_vehiculo);
											$no_economico.focus(); 
										}else{
											jAlert('N&uacute;mero econ&oacute;mico desconocido.', 'Atencion!', function(r) {
												$no_economico.val('');
												$no_economico.focus(); 
											});
										}
									},"json");//termina llamada json
								}
								return false;
							}else{
								if (parseInt(e.which) == 8) {
									//Si se oprime la tecla borrar se vacía el campo no_economico 
									if(parseInt(valor.length)>0 && parseInt($id_vehiculo.val())>0){
										jConfirm('Seguro que desea cambiar la Unidad seleccionada?', 'Dialogo de Confirmacion', function(r) {
											// If they confirmed, manually trigger a form submission
											if (r) {
												$id_vehiculo.val(0);
												$no_economico.val('');
												$marca_vehiculo.val('');
												$busca_vehiculo.show();
												//Quitar solo lectura una vez que se ha borrado la unidad
												$quitar_readonly_input($marca_vehiculo);
												$no_economico.focus();
											}else{
												$no_economico.val(valor);
												$no_economico.focus();
											}
										});
									}else{
										$no_economico.focus();
									}
								}
							}
						});
						
						
						
						//Buscador de Operadores
						$busca_operador.click(function(event){
							event.preventDefault();
							$busca_operadores($no_operador, $nombre_operador);
						});
						
						$(this).aplicarEventoKeypressEjecutaTrigger($nombre_operador, $busca_operador);
						
						$no_operador.keypress(function(e){
							var valor=$(this).val();
							if(e.which == 13){
								if($no_operador.val().trim()!=''){
									var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataOperadorByNo.json';
									$arreglo2 = {'no_operador':$no_operador.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
									$.post(input_json2,$arreglo2,function(entry2){
										if(parseInt(entry2['Operador'].length) > 0 ){
											$no_operador.val(entry2['Operador'][0]['clave']);
											$nombre_operador.val(entry2['Operador'][0]['nombre']);
										}else{
											jAlert('N&uacute;mero de Operador desconocido.', 'Atencion!', function(r) {
												$no_operador.val('');
												$no_operador.focus(); 
											});
										}
									},"json");//termina llamada json
								}
								return false;
							}
						});
						
						
						
						//Buscador de Agentes Aduanales
						$busca_agena.click(function(event){
							event.preventDefault();
							$busca_agentes_aduanales($agena_id, $agena_no, $agena_nombre, $busca_agena);
						});
						
						$(this).aplicarEventoKeypressEjecutaTrigger($agena_nombre, $busca_agena);
						
						$agena_no.keypress(function(e){
							var valor=$(this).val();
							if(e.which == 13){
								if($agena_no.val().trim()!=''){
									var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoAgen.json';
									$arreglo2 = {'no_control':$agena_no.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
									
									$.post(input_json2,$arreglo2,function(entry2){
										if(parseInt(entry2['AgenA'].length) > 0 ){
											$agena_id.val(entry2['AgenA'][0]['id']);
											$agena_no.val(entry2['AgenA'][0]['folio']);
											$agena_nombre.val(entry2['AgenA'][0]['razon_social']);
											$busca_agena.hide();
											
											//Aplicar solo lectura una vez que se ha escogido un agente aduanal
											$aplicar_readonly_input($agena_nombre);
										}else{
											jAlert('N&uacute;mero de Agente Aduanal desconocido.', 'Atencion!', function(r) { 
												$agena_no.focus(); 
											});
										}
									},"json");//termina llamada json
								}
								return false;
							}else{
								if (parseInt(e.which) == 8) {
									//Si se oprime la tecla borrar se vacía el campo agena_no 
									if(parseInt(valor.length)>0 && parseInt($agena_id.val())>0){
										jConfirm('Seguro que desea cambiar el Agente Aduanal seleccionado?', 'Dialogo de Confirmacion', function(r) {
											// If they confirmed, manually trigger a form submission
											if (r) {
												$agena_id.val(0);
												$agena_no.val('');
												$agena_nombre.val('');
												$busca_agena.show();
												
												//Quitar solo lectura una vez que se ha eliminado datos del Agente Aduanal
												$quitar_readonly_input($agena_nombre);
												
												$agena_no.focus();
											}else{
												$agena_no.val(valor);
												$agena_no.focus();
											}
										});
									}else{
										$agena_no.focus();
									}
								}
							}
						});

						
						
						
						//Buscador de Remitentes
						$busca_remitente.click(function(event){
							event.preventDefault();
							$busca_remitentes($rem_id, $rem_nombre, $rem_no, $rem_dir, $id_cliente, $busca_remitente);
						});
						
						$(this).aplicarEventoKeypressEjecutaTrigger($rem_nombre, $busca_remitente);
							
						$rem_no.keypress(function(e){
							var valor=$(this).val();
							if(e.which == 13){
								if($rem_no.val().trim()!=''){
									var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoRemitente.json';
									$arreglo2 = {'no_control':$rem_no.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
									$.post(input_json2,$arreglo2,function(entry2){
										if(parseInt(entry2['Remitente'].length) > 0 ){
											var rem_id = entry2['Remitente'][0]['id'];
											var rem_numero = entry2['Remitente'][0]['folio'];
											var rem_nombre = entry2['Remitente'][0]['razon_social'];
											var rem_dir = entry2['Remitente'][0]['dir'];
											$agregar_datos_remitente($rem_id, $rem_nombre, $rem_no, $rem_dir, $busca_remitente, rem_id, rem_nombre, rem_numero, rem_dir);
										}else{
											jAlert('N&uacute;mero de Remitente desconocido.', 'Atencion!', function(r) { 
												$rem_no.focus(); 
											});
										}
									},"json");//termina llamada json
								}
								return false;
							}else{
								if (parseInt(e.which) == 8) {
									//Si se oprime la tecla borrar se vacía el campo agena_no 
									if(parseInt(valor.length)>0 && parseInt($rem_id.val())>0){
										jConfirm('Seguro que desea cambiar el Remitente seleccionado?', 'Dialogo de Confirmacion', function(r) {
											// If they confirmed, manually trigger a form submission
											if (r) {
												$rem_id.val(0);
												$rem_no.val('');
												$rem_nombre.val('');
												$rem_dir.val('');
												
												//Quitar solo lectura una vez que se ha eliminado datos del Remitente
												$quitar_readonly_input($rem_nombre);
												
												//Mostrar link busca remitente
												$busca_remitente.show();
												
												$rem_no.focus();
											}else{
												$rem_no.val(valor);
												$rem_no.focus();
											}
										});
									}else{
										$rem_no.focus();
									}
								}
							}
						});
						
						
						
						
						//Buscador de Destinatarios
						$busca_dest.click(function(event){
							event.preventDefault();
							$busca_destinatarios($dest_id, $dest_nombre, $dest_no, $dest_dir, $id_cliente, $busca_dest);
						});
						
						$(this).aplicarEventoKeypressEjecutaTrigger($dest_nombre, $busca_dest);
						
						$dest_no.keypress(function(e){
							var valor=$(this).val();
							if(e.which == 13){
								if($dest_no.val().trim()!=''){
									var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoDestinatario.json';
									$arreglo2 = {'no_control':$dest_no.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
									$.post(input_json2,$arreglo2,function(entry2){
										if(parseInt(entry2['Dest'].length) > 0 ){
											var dest_id = entry2['Dest'][0]['id'];
											var dest_numero = entry2['Dest'][0]['folio'];
											var dest_nombre = entry2['Dest'][0]['razon_social'];
											var dest_dir = entry2['Dest'][0]['dir'];
																	
											$agregar_datos_remitente($dest_id, $dest_nombre, $dest_no, $dest_dir, $busca_dest, dest_id, dest_nombre, dest_numero, dest_dir);
										}else{
											jAlert('N&uacute;mero de Destinatario desconocido.', 'Atencion!', function(r) { 
												$dest_no.focus(); 
											});
										}
									},"json");//termina llamada json
								}
								return false;
							}else{
								if (parseInt(e.which) == 8) {
									//Si se oprime la tecla borrar se vacía el campo agena_no 
									if(parseInt(valor.length)>0 && parseInt($dest_id.val())>0){
										jConfirm('Seguro que desea cambiar el Destinatario seleccionado?', 'Dialogo de Confirmacion', function(r) {
											// If they confirmed, manually trigger a form submission
											if (r) {
												$dest_id.val(0);
												$dest_no.val('');
												$dest_nombre.val('');
												$dest_dir.val('');
												
												//Quitar solo lectura una vez que se ha eliminado datos del Remitente
												$quitar_readonly_input($dest_nombre);
												
												//Mostrar link busca remitente
												$busca_dest.show();
												
												$dest_no.focus();
											}else{
												$dest_no.val(valor);
												$dest_no.focus();
											}
										});
									}else{
										$dest_no.focus();
									}
								}
							}
						});						
					}//Termina datos para transportista
					
					
					
					
					
					
					
					
					//si es refacturacion, no se puede cambiar los datos del grid, solo el header de la factura
					if(entry['datosPedido']['0']['cancelado']=="true"){
						$cancelar_pedido.hide();
						$submit_actualizar.hide();
						$busca_sku.hide();
						$agregar_producto.hide();
						$cancelado.show();
						$folio.attr('disabled','-1'); //deshabilitar
						$check_ruta.attr('disabled','-1'); //deshabilitar
						$check_enviar_obser.attr('disabled','-1'); //deshabilitar
						$sku_producto.attr('disabled','-1'); //deshabilitar
						$nombre_producto.attr('disabled','-1'); //deshabilitar
						$nocliente.attr('disabled','-1'); //deshabilitar
						$razon_cliente.attr('disabled','-1'); //deshabilitar
						$dir_cliente.attr('disabled','-1'); //deshabilitar
						$observaciones.attr('disabled','-1'); //deshabilitar
						$tipo_cambio.attr('disabled','-1'); //deshabilitar
						$orden_compra.attr('disabled','-1'); //deshabilitar
						$transporte.attr('disabled','-1'); //deshabilitar
						$lugar_entrega.attr('disabled','-1'); //deshabilitar
						$fecha_compromiso.attr('disabled','-1'); //deshabilitar
						$select_moneda.attr('disabled','-1'); //deshabilitar
						$select_condiciones.attr('disabled','-1'); //deshabilitar
						$select_vendedor.attr('disabled','-1'); //deshabilitar
						
						$grid_empleados.find('a[href*=elimina_producto]').hide();
						$grid_empleados.find('input').attr('disabled','-1'); //deshabilitar todos los campos input del grid
						$subtotal.attr('disabled','-1'); //deshabilitar
						$impuesto.attr('disabled','-1'); //deshabilitar
						$campo_impuesto_retenido.attr('disabled','-1'); //deshabilitar
						$total.attr('disabled','-1'); //deshabilitar
						
						$('#forma-facnomina-window').find('#tabx-2').find('input').attr('disabled','-1'); //deshabilitar
						$('#forma-facnomina-window').find('#tabx-2').find('select').attr('disabled','-1'); //deshabilitar
						$('#forma-facnomina-window').find('#tabx-2').find('textarea').attr('disabled','-1'); //deshabilitar
						$('#forma-facnomina-window').find('#tabx-2').find('a').hide();
					}
					
					
					//proceso_flujo_id=4 :Pedido, diferente de 4 ya esta en otro estado del proceso
					if(parseInt(entry['datosPedido']['0']['proceso_flujo_id'])!=4){
						$cancelar_pedido.hide();
						$submit_actualizar.hide();
						$busca_sku.hide();
						$agregar_producto.hide();
						$check_enviar_obser.attr('disabled','-1'); //deshabilitar
						$check_ruta.attr('disabled','-1'); //deshabilitar
						$sku_producto.attr('disabled','-1'); //deshabilitar
						$nombre_producto.attr('disabled','-1'); //deshabilitar
						//$nocliente.attr('disabled','-1'); //deshabilitar
						//$razon_cliente.attr('disabled','-1'); //deshabilitar
						//$dir_cliente.attr('disabled','-1'); //deshabilitar
						$observaciones.attr("readonly", true);
						$tipo_cambio.attr("readonly", true);
						$orden_compra.attr("readonly", true);
						$transporte.attr("readonly", true);
						$lugar_entrega.attr("readonly", true);
						//$fecha_compromiso.attr('disabled','-1'); //deshabilitar
						//$select_moneda.attr('disabled','-1'); //deshabilitar
						//$select_condiciones.attr('disabled','-1'); //deshabilitar
						//$select_vendedor.attr('disabled','-1'); //deshabilitar
						$grid_empleados.find('a[href*=elimina_producto]').hide();
						$grid_empleados.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
						$grid_empleados.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid
						$grid_empleados.find('input[name=checkProd]').attr('disabled','-1'); //deshabilitar
						
						
						$('#forma-facnomina-window').find('#tabx-2').find('input').attr("readonly", true);
						$('#forma-facnomina-window').find('#tabx-2').find('input').css({'background' : '#F0F0F0'});
						$('#forma-facnomina-window').find('#tabx-2').find('textarea').attr("readonly", true);
						$('#forma-facnomina-window').find('#tabx-2').find('select').attr('disabled','-1'); //deshabilitar
						$('#forma-facnomina-window').find('#tabx-2').find('input[name=check_flete]').attr('disabled','-1'); //deshabilitar
						$('#forma-facnomina-window').find('#tabx-2').find('a').hide();
					}else{

						//$fecha_compromiso.val(mostrarFecha());
						$fecha_compromiso.click(function (s){
							var a=$('div.datepicker');
							a.css({'z-index':100});
						});
						
						$fecha_compromiso.DatePicker({
							format:'Y-m-d',
							date: $fecha_compromiso.val(),
							current: $fecha_compromiso.val(),
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
								$fecha_compromiso.val(formated);
								if (formated.match(patron) ){
									var valida_fecha=mayor($fecha_compromiso.val(),mostrarFecha());
									
									if (valida_fecha==true){
										$fecha_compromiso.DatePickerHide();	
									}else{
										jAlert("Fecha no valida, debe ser mayor a la actual.",'! Atencion');
										$fecha_compromiso.val(mostrarFecha());
									}
								}
							}
						});
					}
					
					
					
					//agregar producto al grid
					$agregar_producto.click(function(event){
						event.preventDefault();
						$buscador_presentaciones_producto($id_cliente, $nocliente.val(), $sku_producto.val(),$nombre_producto,$grid_empleados,$select_moneda,$tipo_cambio, entry['Monedas']);
					});
					
				});//termina llamada json
                
                
                
				//cambiar metodo de pago
				$select_metodo_pago.change(function(){
					var valor_metodo = $(this).val();
					$no_cuenta.val('');
					
					//valor_metodo 2=Tarjeta Credito, 3=Tarjeta Debito
					if(parseInt(valor_metodo)==2 || parseInt(valor_metodo)==3){
						$no_cuenta.val('');
						//si esta desahabilitado, hay que habilitarlo para permitir la captura de los digitos de la tarjeta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						//quitar propiedad de solo lectura
						$no_cuenta.removeAttr('readonly');
						
						//$no_cuenta.attr('readonly',true);
						
						if($etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.removeAttr('disabled');
						}
						
						$etiqueta_digit.val('Ingrese los ultimos 4 Digitos de la Tarjeta');
					}
					
					//valor_metodo 4=Cheque Nominativo, 5=Transferencia Electronica de Fondos
					if(parseInt(valor_metodo)==4 || parseInt(valor_metodo)==5){
						$no_cuenta.val('');
						$no_cuenta.show();
						//si esta desahabilitado, hay que habilitarlo para permitir la captura del Numero de cuenta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						
						//fijar propiedad de solo lectura en verdadero
						$no_cuenta.attr('readonly',true);
						
						if($etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.removeAttr('disabled');
						}
						
						if(parseInt($select_moneda.val())==1){
							$etiqueta_digit.val('Numero de Cuenta para pago en Pesos');
							$no_cuenta.val($cta_mn.val());
						}else{
							$etiqueta_digit.val('Numero de Cuenta en Dolares');
							$no_cuenta.val($cta_usd.val());
						}
					}
					
					//valor_metodo 1=Efectivo, 6=No Identificado
					if(parseInt(valor_metodo)==1 || parseInt(valor_metodo)==6){
						$no_cuenta.val('');
						if(!$no_cuenta.is(':disabled')) {
							$no_cuenta.attr('disabled','-1');
						}
						if(!$etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.attr('disabled','-1');
						}
					}
					
					if(parseInt(valor_metodo)==7){
						$no_cuenta.show();
						$no_cuenta.val('NA');
						//si esta desahabilitado, hay que habilitarlo para permitir la captura del Numero de cuenta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						if($etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.removeAttr('disabled');
						}
						if(parseInt($select_moneda.val())==1){
							$etiqueta_digit.val('Numero de Cuenta para pago en Pesos');
						}else{
							$etiqueta_digit.val('Numero de Cuenta en Dolares');
						}
					}
					
				});
				
                
                
				
				$tipo_cambio.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}		
				});

				
				//buscador de clientes
				$busca_cliente.click(function(event){
					event.preventDefault();
					$busca_clientes();
				});
				
				
				//buscador de productos
				$busca_sku.click(function(event){
					event.preventDefault();
					$busca_productos($sku_producto.val(), $nombre_producto.val());
				});
				
				
				
				//ejecutar clic del href Agregar producto al pulsar enter en el campo sku del producto
				$sku_producto.keypress(function(e){
					if(e.which == 13){
						$agregar_producto.trigger('click');
						return false;
					}
				});
				
				//desencadena clic del href Buscar Producto al pulsar enter en el campo Nombre del producto
				$nombre_producto.keypress(function(e){
					if(e.which == 13){
						$busca_sku.trigger('click');
						return false;
					}
				});
						
				
				$cancelar_pedido.click(function(e){
					$accion_proceso.attr({'value' : "cancelar"});
					jConfirm('Desea Cancelar el Pedido?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
							$submit_actualizar.parents("FORM").submit();
						}else{
							$accion_proceso.attr({'value' : "edit"});
						}
					});
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});
				
				
				//click generar reporte de pedidos 
				$descargarpdf.click(function(event){
					event.preventDefault();
					var id_pedido = $id_pedido.val();
					if($id_pedido.val() != 0 ){
						var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
						var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/get_genera_pdf_pedido/'+id_pedido+'/'+iu+'/out.json';
						window.location.href=input_json;

					}else{
						jAlert("No se esta enviando el identificador  del pedido","Atencion!!!")
					}
				 });
                
                
				$submit_actualizar.bind('click',function(){
					var trCount = $("tr", $grid_empleados).size();
					$total_tr.val(trCount);
					if(parseInt(trCount) > 0){
						$grid_empleados.find('tr').each(function (index){
							$(this).find('#cost').val(quitar_comas( $(this).find('#cost').val() ));
						});
						return true;
					}else{
						jAlert('No hay datos para actualizar', 'Atencion!', function(r) { 
							$('#forma-facnomina-window').find('input[name=sku_producto]').focus();
						});
						return false;
					}
				});
                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-facnomina-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-facnomina-overlay').fadeOut(remove);
				});
				
			}
		}
	}
	
	
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllPedidos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllPedidos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formafacnomina00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



