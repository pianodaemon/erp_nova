$(function() {
	String.prototype.toCharCode = function(){
		var str = this.split(''), len = str.length, work = new Array(len);
		for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
		}
		return work.join(',');
	};
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
        
	var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/crmregistrovisitas";
    
	//Barra para las acciones
	$('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
	$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_crmregistrovisitas = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Direcciones Fiscales de Clientes');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_tipo_visita = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_tipo_visita]');
	var $busqueda_contacto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_contacto]');
	var $busqueda_agente = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_agente]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('#boton_buscador');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('#boton_limpiar');
	
	
	var html = '';
	$busqueda_tipo_visita.children().remove();
	html='<option value="0">[-- Todos --]</option>';
	html+='<option value="1">Cliente</option>';
	html+='<option value="2">Prospecto</option>';
	$busqueda_tipo_visita.append(html);
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "tipo_visita" + signo_separador + $busqueda_tipo_visita.val() + "|";
		valor_retorno += "contacto" + signo_separador + $busqueda_contacto.val() + "|";
		valor_retorno += "agente" + signo_separador + $busqueda_agente.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val();
		valor_retorno += "iu" + signo_separador + $('#lienzo_recalculable').find('input[name=iu]').val() + "|";
		return valor_retorno;
	};
	
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	//$cadena_busqueda = cadena;
	
	$buscar.click(function(event){
		event.preventDefault();
		cadena = to_make_one_search_string();
		$cadena_busqueda = cadena.toCharCode();
		$get_datos_grid();
	});
	
	
	
	$limpiar.click(function(event){
		event.preventDefault();
		/*
		var html_tipo = '';
		$busqueda_tipo_visita.children().remove();
		html_tipo='<option value="0">[-- Todos --]</option>';
		html_tipo+='<option value="1">Cliente</option>';
		html_tipo+='<option value="2">Prospecto</option>';
		$busqueda_tipo_visita.append(html_tipo);
		*/
		$busqueda_folio.val('');
		$busqueda_contacto.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		$busqueda_tipo_visita.find('option[index=0]').attr('selected','selected');
		$busqueda_agente.find('option[index=0]').attr('selected','selected');
	});
	
	
	TriggerClickVisializaBuscador = 0;

	$visualiza_buscador.click(function(event){
		event.preventDefault();
		
		var alto=0;
		if(TriggerClickVisializaBuscador==0){
			 TriggerClickVisializaBuscador=1;
			 var height2 = $('#cuerpo').css('height');

			 alto = parseInt(height2)-220;
			 var pix_alto=alto+'px';
			 
			 $('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
			 $('#barra_buscador').animate({height: '60px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
				
		}else{
			 TriggerClickVisializaBuscador=0;
			 var height2 = $('#cuerpo').css('height');
			 alto = parseInt(height2)+220;
			 var pix_alto=alto+'px';
			 
			 $('#barra_buscador').animate({height:'0px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
		};
	});
	
	
	

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
	
	
	
	$tabs_li_funxionalidad = function(){
		$('#forma-crmregistrovisitas-window').find('#submit').mouseover(function(){
			$('#forma-crmregistrovisitas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-crmregistrovisitas-window').find('#submit').mouseout(function(){
			$('#forma-crmregistrovisitas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-crmregistrovisitas-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-crmregistrovisitas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-crmregistrovisitas-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-crmregistrovisitas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-crmregistrovisitas-window').find('#close').mouseover(function(){
			$('#forma-crmregistrovisitas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-crmregistrovisitas-window').find('#close').mouseout(function(){
			$('#forma-crmregistrovisitas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});		
		
		$('#forma-crmregistrovisitas-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-crmregistrovisitas-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-crmregistrovisitas-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-crmregistrovisitas-window').find("ul.pestanas li").click(function() {
			$('#forma-crmregistrovisitas-window').find(".contenidoPes").hide();
			$('#forma-crmregistrovisitas-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-crmregistrovisitas-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	
	
	//buscador de clientes
	$busca_clientes = function(razon_social_cliente){
		$(this).modalPanel_Buscacliente();
		var $dialogoc =  $('#forma-buscacliente-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_clientes').find('table.formaBusqueda_clientes').clone());
		$('#forma-buscacliente-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscacliente-window').find('#tabla_resultado');
		
		var $busca_cliente_modalbox = $('#forma-buscacliente-window').find('#busca_cliente_modalbox');
		var $cancelar_plugin_busca_cliente = $('#forma-buscacliente-window').find('#cencela');
		
		var $cadena_buscar = $('#forma-buscacliente-window').find('input[name=cadena_buscar]');
		var $select_filtro_por = $('#forma-buscacliente-window').find('select[name=filtropor]');
		
		//funcionalidad botones
		$busca_cliente_modalbox.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$busca_cliente_modalbox.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$cancelar_plugin_busca_cliente.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		
		$cancelar_plugin_busca_cliente.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		var seleccionado='';
		if(razon_social_cliente != ''){
			//asignamos la Razon Social del Cliente al campo Nombre
			$cadena_buscar.val(razon_social_cliente);
			seleccionado='selected="yes"';
		}
		
		var html = '';
		$select_filtro_por.children().remove();
		html='<option value="0">[-- Opcion busqueda --]</option>';
		html+='<option value="1">No. de control</option>';
		html+='<option value="2">RFC</option>';
		html+='<option value="3" '+seleccionado+'>Razon social</option>';
		html+='<option value="4">CURP</option>';
		html+='<option value="5">Alias</option>';
		$select_filtro_por.append(html);
		
		
		
		//click buscar clientes
		$busca_cliente_modalbox.click(function(event){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorClientes.json';
			$arreglo = {'cadena':$cadena_buscar.val(),
						 'filtro':$select_filtro_por.val(),
						 'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						}
						
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Clientes'],function(entryIndex,cliente){
					trr = '<tr>';
						trr += '<td width="80">';
							trr += '<input type="hidden" id="idclient" value="'+cliente['id']+'">';
							trr += '<input type="hidden" id="direccion" value="'+cliente['direccion']+'">';
							trr += '<input type="hidden" id="id_moneda" value="'+cliente['moneda_id']+'">';
							trr += '<input type="hidden" id="moneda" value="'+cliente['moneda']+'">';
							trr += '<span class="no_control">'+cliente['numero_control']+'</span>';
						trr += '</td>';
						trr += '<td width="145"><span class="rfc">'+cliente['rfc']+'</span></td>';
						trr += '<td width="375"><span class="razon">'+cliente['razon_social']+'</span></td>';
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
				
				//seleccionar un producto del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					$('#forma-crmregistrovisitas-window').find('input[name=id_cliente]').val($(this).find('#idclient').val());
					$('#forma-crmregistrovisitas-window').find('input[name=rfc]').val($(this).find('span.rfc').html());
					$('#forma-crmregistrovisitas-window').find('input[name=cliente]').val($(this).find('span.razon').html());
					$('#forma-crmregistrovisitas-window').find('input[name=nocontrol]').val($(this).find('span.no_control').html());
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
				});

			});
		});//termina llamada json
		
		
		//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
		if($cadena_buscar.val() != ''){
			$busca_cliente_modalbox.trigger('click');
		}
		
		$cancelar_plugin_busca_cliente.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-buscacliente-overlay').fadeOut(remove);
		});
	}//termina buscador de clientes

	
	
	//nuevo
	$new_crmregistrovisitas.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_crmregistrovisitas();
		
		var form_to_show = 'formacrmregistrovisitas';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-crmregistrovisitas-window').css({"margin-left": -400, 	"margin-top": -265});
		$forma_selected.prependTo('#forma-crmregistrovisitas-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();		
		
		var $identificador = $('#forma-crmregistrovisitas-window').find('input[name=identificador]');
		var $folio = $('#forma-crmregistrovisitas-window').find('input[name=folio]');
		var $select_agente = $('#forma-crmregistrovisitas-window').find('select[name=select_agente]');
		var $id_contacto = $('#forma-crmregistrovisitas-window').find('input[name=id_contacto]');
		var $contacto = $('#forma-crmregistrovisitas-window').find('input[name=contacto]');
		var $busca_contacto = $('#forma-crmregistrovisitas-window').find('#busca_contacto');
		
		var $fecha = $('#forma-crmregistrovisitas-window').find('input[name=fecha]');
		var $hora_visita = $('#forma-crmregistrovisitas-window').find('input[name=hora_visita]');
		var $hora_duracion = $('#forma-crmregistrovisitas-window').find('input[name=hora_duracion]');
		
		var $select_motivo_visita = $('#forma-crmregistrovisitas-window').find('select[name=select_motivo_visita]');
		var $select_calif_visita = $('#forma-crmregistrovisitas-window').find('select[name=select_calif_visita]');
		var $select_tipo_seguimiento = $('#forma-crmregistrovisitas-window').find('select[name=select_tipo_seguimiento]');
		var $select_oportunidad = $('#forma-crmregistrovisitas-window').find('select[name=select_oportunidad]');
		
		var $recusrsos_visita = $('#forma-crmregistrovisitas-window').find('textarea[name=recusrsos_visita]');
		var $resultado_visita = $('#forma-crmregistrovisitas-window').find('textarea[name=resultado_visita]');
		var $observaciones_visita = $('#forma-crmregistrovisitas-window').find('textarea[name=observaciones_visita]');
		
		var $fecha_proxima_visita = $('#forma-crmregistrovisitas-window').find('input[name=fecha_proxima_visita]');
		var $hora_proxima_visita = $('#forma-crmregistrovisitas-window').find('input[name=hora_proxima_visita]');
		var $comentarios_proxima_visita = $('#forma-crmregistrovisitas-window').find('textarea[name=comentarios_proxima_visita]');
		
		var $cerrar_plugin = $('#forma-crmregistrovisitas-window').find('#close');
		var $cancelar_plugin = $('#forma-crmregistrovisitas-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-crmregistrovisitas-window').find('#submit');
		
		$folio.css({'background' : '#DDDDDD'});
		$identificador.attr({'value' : 0});
		$id_contacto.attr({'value' : 0});
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La visita se registr&oacute; con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-crmregistrovisitas-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-crmregistrovisitas-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
                                     
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					//telUno: Numero Telefonico no Valido___
					if( longitud.length > 1 ){
						$('#forma-crmregistrovisitas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRegistroVisita.json';
		$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json,$arreglo,function(entry){
			
			//Alimentando los campos select_agente
			$select_agente.children().remove();
			var agente_hmtl = '';
			if(parseInt(entry['Extra'][0]['exis_rol_admin']) > 0){
				agente_hmtl += '<option value="0" >[-- Selecionar Agente --]</option>';
			}
			
			$.each(entry['Agentes'],function(entryIndex,agente){
				if(parseInt(agente['id'])==parseInt(entry['Extra'][0]['id_agente'])){
					agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
				}else{
					//si exis_rol_admin es mayor que cero, quiere decir que el usuario logueado es un administrador
					if(parseInt(entry['Extra'][0]['exis_rol_admin']) > 0){
						agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
					}
				}
			});
			$select_agente.append(agente_hmtl);
			
			
			//Alimentando los campos select de Motivos
			$select_motivo_visita.children().remove();
			var motivo_hmtl = '';
			$.each(entry['Motivos'],function(entryIndex,motivo){
				motivo_hmtl += '<option value="' + motivo['id'] + '"  >' + motivo['descripcion'] + '</option>';
			});
			$select_motivo_visita.append(motivo_hmtl);
			
			//Alimentando los campos select de Calificaciones de Visita
			$select_calif_visita.children().remove();
			var calif_hmtl = '';
			$.each(entry['Calificaciones'],function(entryIndex,calif){
				calif_hmtl += '<option value="' + calif['id'] + '"  >' + calif['titulo'] + '</option>';
			});
			$select_calif_visita.append(calif_hmtl);
			
			//Alimentando los campos select de Seguimiento
			$select_tipo_seguimiento.children().remove();
			var seguimiento_hmtl = '';
			$.each(entry['Seguimientos'],function(entryIndex,seg){
				seguimiento_hmtl += '<option value="' + seg['id'] + '"  >' + seg['titulo'] + '</option>';
			});
			$select_tipo_seguimiento.append(seguimiento_hmtl);
			
			//Alimentando los campos select de oportunidad
			$select_oportunidad.children().remove();
			var oportunidad_hmtl = '<option value="1">Si</option>';
			oportunidad_hmtl += '<option value="0">No</option>';
			$select_oportunidad.append(oportunidad_hmtl);
			
																				
																		
			/*
			//Alimentando los campos select de las pais
			$select_pais.children().remove();
			var pais_hmtl = '<option value="0" selected="yes">[-Seleccionar Pais-]</option>';
			$.each(entry['Paises'],function(entryIndex,pais){
				pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
			});
			$select_pais.append(pais_hmtl);
			
			var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Estado--]</option>';
			$select_estado.children().remove();
			$select_estado.append(entidad_hmtl);
			
			var localidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>';
			$select_municipio.children().remove();
			$select_municipio.append(localidad_hmtl);
			
			
			//carga select estados al cambiar el pais
			$select_pais.change(function(){
				var valor_pais = $(this).val();
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEstados.json';
				$arreglo = {'id_pais':valor_pais};
				$.post(input_json,$arreglo,function(entry){
					$select_estado.children().remove();
					var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Estado--]</option>'
					$.each(entry['Estados'],function(entryIndex,entidad){
						entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
					});
					$select_estado.append(entidad_hmtl);
					var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>';
					$select_municipio.children().remove();
					$select_municipio.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
			
			//carga select municipios al cambiar el estado
			$select_estado.change(function(){
				var valor_entidad = $(this).val();
				var valor_pais = $select_pais.val();
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMunicipios.json';
				$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
				$.post(input_json,$arreglo,function(entry){
					$select_municipio.children().remove();
					var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>'
					$.each(entry['Municipios'],function(entryIndex,mun){
						trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
					});
					$select_municipio.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
			*/
		},"json");//termina llamada json
        
        //$('.input1').TimepickerInputMask();
        
        $hora_visita.TimepickerInputMask();
		$hora_duracion.TimepickerInputMask();
        $hora_proxima_visita.TimepickerInputMask();
        
        //fecha de la visita
		$fecha.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
			
		$fecha.DatePicker({
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
				$fecha.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha.val(),mostrarFecha());
					
					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$fecha.val(mostrarFecha());
					}else{
						$fecha.DatePickerHide();	
					}
				}
			}
		});
		
			
        
        //fecha para la proxima visita
		$fecha_proxima_visita.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		$fecha_proxima_visita.DatePicker({
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
				$fecha_proxima_visita.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha_proxima_visita.val(),mostrarFecha());
					
					if (valida_fecha==true){
						$fecha_proxima_visita.DatePickerHide();	
					}else{
						jAlert("Fecha no valida, debe ser mayor a la actual.",'! Atencion');
						$fecha_proxima_visita.val(mostrarFecha());
					}
				}
			}
		});
        
        
        
        
        //buscar contacto
        $busca_contacto.click(function(event){
			event.preventDefault();
			$busca_clientes($cliente.val());
        });
        
        
        
        
        
        
        
        $cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-crmregistrovisitas-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-crmregistrovisitas-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});		
	});
	
	
	
	
	
	var carga_formaDirecciones_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la direci&oacute;n seleccionada', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La direcci&oacute;n  fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La direci&oacute;n no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
            
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formacrmregistrovisitas';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_crmregistrovisitas();
			$('#forma-crmregistrovisitas-window').css({"margin-left": -400, 	"margin-top": -265});
			
			$forma_selected.prependTo('#forma-crmregistrovisitas-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
                        
			var $identificador = $('#forma-crmregistrovisitas-window').find('input[name=identificador]');
			var $busca_cliente = $('#forma-crmregistrovisitas-window').find('a[href*=busca_cliente]');
			var $id_cliente = $('#forma-crmregistrovisitas-window').find('input[name=id_cliente]');
			var $cliente = $('#forma-crmregistrovisitas-window').find('input[name=cliente]');
			var $nocontrol = $('#forma-crmregistrovisitas-window').find('input[name=nocontrol]');
			var $rfc = $('#forma-crmregistrovisitas-window').find('input[name=rfc]');
			var $calle = $('#forma-crmregistrovisitas-window').find('input[name=calle]');
			var $numero_int = $('#forma-crmregistrovisitas-window').find('input[name=numero_int]');
			var $numero_ext = $('#forma-crmregistrovisitas-window').find('input[name=numero_ext]');
			var $entrecalles = $('#forma-crmregistrovisitas-window').find('input[name=entrecalles]');
			var $colonia = $('#forma-crmregistrovisitas-window').find('input[name=colonia]');
			var $cp = $('#forma-crmregistrovisitas-window').find('input[name=cp]');
			var $select_pais = $('#forma-crmregistrovisitas-window').find('select[name=select_pais]');
			var $select_estado = $('#forma-crmregistrovisitas-window').find('select[name=select_estado]');
			var $select_municipio = $('#forma-crmregistrovisitas-window').find('select[name=select_municipio]');
			var $contacto = $('#forma-crmregistrovisitas-window').find('input[name=contacto]');
			var $email = $('#forma-crmregistrovisitas-window').find('input[name=email]');
			var $tel1 = $('#forma-crmregistrovisitas-window').find('input[name=tel1]');
			var $ext1 = $('#forma-crmregistrovisitas-window').find('input[name=ext1]');
			var $fax = $('#forma-crmregistrovisitas-window').find('input[name=fax]');
			var $tel2 = $('#forma-crmregistrovisitas-window').find('input[name=tel2]');
			var $ext2 = $('#forma-crmregistrovisitas-window').find('input[name=ext2]');
                        
			var $cerrar_plugin = $('#forma-crmregistrovisitas-window').find('#close');
			var $cancelar_plugin = $('#forma-crmregistrovisitas-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-crmregistrovisitas-window').find('#submit');
			
			$nocontrol.css({'background' : '#DDDDDD'});
			$rfc.css({'background' : '#DDDDDD'});
			$busca_cliente.hide();
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRegistroVisita.json';
				$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						jAlert("La direcci&oacute;n fue dada de alta con &eacute;xito", 'Atencion!');
						var remove = function() {$(this).remove();};
						$('#forma-crmregistrovisitas-overlay').fadeOut(remove);
						//refresh_table();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-crmregistrovisitas-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
											 
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							//telUno: Numero Telefonico no Valido___
							if( longitud.length > 1 ){
								$('#forma-crmregistrovisitas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$identificador.attr({'value' : entry['Datos']['0']['identificador']});
					$id_cliente.attr({'value' : entry['Datos']['0']['id_cliente']});
					$cliente.attr({'value' : entry['Datos']['0']['cliente']});
					$nocontrol.attr({'value' : entry['Datos']['0']['numero_control']});
					$rfc.attr({'value' : entry['Datos']['0']['rfc']});
					$calle.attr({'value' : entry['Datos']['0']['calle']});
					$numero_int.attr({'value' : entry['Datos']['0']['numero_interior']});
					$numero_ext.attr({'value' : entry['Datos']['0']['numero_exterior']});
					$entrecalles.attr({'value' : entry['Datos']['0']['entre_calles']});
					$colonia.attr({'value' : entry['Datos']['0']['colonia']});
					$cp.attr({'value' : entry['Datos']['0']['cp']});
					$contacto.attr({'value' : entry['Datos']['0']['contacto']});
					$email.attr({'value' : entry['Datos']['0']['email']});
					$tel1.attr({'value' : entry['Datos']['0']['telefono1']});
					$ext1.attr({'value' : entry['Datos']['0']['extension1']});
					$fax.attr({'value' : entry['Datos']['0']['fax']});
					$tel2.attr({'value' : entry['Datos']['0']['telefono2']});
					$ext2.attr({'value' : entry['Datos']['0']['extension2']});
					
					$select_pais = $('#forma-crmregistrovisitas-window').find('select[name=select_pais]');
					$select_estado = $('#forma-crmregistrovisitas-window').find('select[name=select_estado]');
					$select_municipio = $('#forma-crmregistrovisitas-window').find('select[name=select_municipio]');
					
					
					//Alimentando los campos select de las pais
					$select_pais.children().remove();
					var pais_hmtl = '<option value="0" >[-Seleccionar Pais-]</option>';
					$.each(entry['Paises'],function(entryIndex,pais){
						if(pais['cve_pais'] == entry['Datos']['0']['pais_id']){
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  selected="yes">' + pais['pais_ent'] + '</option>';
						}else{
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
						}
					});
					$select_pais.append(pais_hmtl);
					
					
					
					//Alimentando los campos select del estado
					$select_estado.children().remove();
					var entidad_hmtl = '<option value="00"  >[-Seleccionar Estado--]</option>';
					$.each(entry['Estados'],function(entryIndex,entidad){
						if(entidad['cve_ent'] == entry['Datos']['0']['estado_id']){
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  selected="yes">' + entidad['nom_ent'] + '</option>';
						}else{
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						}
					});
					$select_estado.append(entidad_hmtl);
					
					
					//Alimentando los campos select de los municipios
					$select_municipio.children().remove();
					var localidad_hmtl = '<option value="00" >[-Seleccionar Municipio-]</option>';
					$.each(entry['Municipios'],function(entryIndex,mun){
						if(mun['cve_mun'] == entry['Datos']['0']['municipio_id']){
							localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  selected="yes">' + mun['nom_mun'] + '</option>';
						}else{
							localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						}
					});
					$select_municipio.append(localidad_hmtl);
					
					
					//carga select estados al cambiar el pais
					$select_pais.change(function(){
						var valor_pais = $(this).val();
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEstados.json';
						$arreglo = {'id_pais':valor_pais};
						$.post(input_json,$arreglo,function(entry){
							$select_estado.children().remove();
							var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Estado--]</option>'
							$.each(entry['Estados'],function(entryIndex,entidad){
								entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
							});
							$select_estado.append(entidad_hmtl);
							var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>';
							$select_municipio.children().remove();
							$select_municipio.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});
					
					//carga select municipios al cambiar el estado
					$select_estado.change(function(){
						var valor_entidad = $(this).val();
						var valor_pais = $select_pais.val();
						
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMunicipios.json';
						$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
						$.post(input_json,$arreglo,function(entry){
							$select_municipio.children().remove();
							var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>'
							$.each(entry['Municipios'],function(entryIndex,mun){
								trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
							});
							$select_municipio.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});
					
				},"json");//termina llamada json
                                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-crmregistrovisitas-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-crmregistrovisitas-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllRegistroVisitas.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllRegistroVisitas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaDirecciones_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    $get_datos_grid();
    
});
