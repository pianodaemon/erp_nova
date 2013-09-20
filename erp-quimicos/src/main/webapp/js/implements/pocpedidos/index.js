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
	var controller = $contextpath.val()+"/controllers/pocpedidos";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    var $new_pedido = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Pedidos de Clientes');
	
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
            var $select_prod_tipo = $('#forma-pocpedidos-window').find('select[name=prodtipo]');
            $('#forma-pocpedidos-window').find('#submit').mouseover(function(){
                $('#forma-pocpedidos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
                //$('#forma-pocpedidos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
            })
            $('#forma-pocpedidos-window').find('#submit').mouseout(function(){
                $('#forma-pocpedidos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
                //$('#forma-pocpedidos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
            })
            $('#forma-pocpedidos-window').find('#boton_cancelar').mouseover(function(){
                $('#forma-pocpedidos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            })
            $('#forma-pocpedidos-window').find('#boton_cancelar').mouseout(function(){
                $('#forma-pocpedidos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            })
            
            $('#forma-pocpedidos-window').find('#close').mouseover(function(){
                $('#forma-pocpedidos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            })
            $('#forma-pocpedidos-window').find('#close').mouseout(function(){
                $('#forma-pocpedidos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            })
            
            $('#forma-pocpedidos-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-pocpedidos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-pocpedidos-window').find(".contenidoPes:first").show(); //Show first tab content
            
            //On Click Event
            $('#forma-pocpedidos-window').find("ul.pestanas li").click(function() {
                $('#forma-pocpedidos-window').find(".contenidoPes").hide();
                $('#forma-pocpedidos-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-pocpedidos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
                $(this).addClass("active");
                return false;
            });
	}
	
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	//funcion para hacer que un campo solo acepte numeros
	$permitir_solo_numeros = function($campo){
		//validar campo costo, solo acepte numeros y punto
		$campo.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
	}
	
	
	//Buscador de Unidades(Vehiculo)
	$busca_unidades= function($id_vehiculo, $no_economico, $marca_vehiculo){
		$(this).modalPanel_busquedaunidad();
		var $dialogoc =  $('#forma-busquedaunidad-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_busquedaunidad').find('table.formaBusqueda_busquedaunidad').clone());
		$('#forma-busquedaunidad-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-busquedaunidad-window').find('#tabla_resultado');
		
		var $boton_busquedaunidad = $('#forma-busquedaunidad-window').find('#boton_busquedaunidad');
		var $cancelar_busqueda = $('#forma-busquedaunidad-window').find('#cencela');
		
		var $cadena_noeconomico = $('#forma-busquedaunidad-window').find('input[name=cadena_noeconomico]');
		var $cadena_marca = $('#forma-busquedaunidad-window').find('input[name=cadena_marca]');
		
		
		//funcionalidad botones
		$boton_busquedaunidad.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$boton_busquedaunidad.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$cancelar_busqueda.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		
		$cancelar_busqueda.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		$cadena_noeconomico.val($no_economico.val());
		$cadena_marca.val($marca_vehiculo.val());
		
		
		//click buscar clientes
		$boton_busquedaunidad.click(function(event){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorUnidades.json';
			$arreglo = { 'no_economico':$cadena_noeconomico.val(),
						 'marca':$cadena_marca.val(),
						 'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						}
						
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Agentes'],function(entryIndex,agen){
					trr = '<tr>';
						trr += '<td width="180">';
							trr += '<input type="hidden" id="id" value="'+agen['id']+'">';
							trr += '<span class="no_eco">'+agen['folio']+'</span>';
						trr += '</td>';
						trr += '<td width="420"><span class="marca">'+agen['razon_social']+'</span></td>';
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
					var idDetalle=0;
					var idAgena = $(this).find('#id').val();
					var noControl = $(this).find('span.no_control').html();
					var razonSocial = $(this).find('span.razon').html();
					
					//LLamada a la funcion que agrega tr al grid
					$agrega_agente_aduanal_grid($grid_agenaduanales, idDetalle, idCliente, idAgena, noControl, razonSocial);
					
					//Elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-busquedaunidad-overlay').fadeOut(remove);
					
					//Limpiar campos
					$nombre_agena.val('');
					$noagena.val('');

					$nombre_agena.focus();
				});
			});
		});//termina llamada json
		
		
		//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
		if($cadena_noeconomico.val().trim()!='' || $cadena_marca.val().trim()!=''){
			$boton_busquedaunidad.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_noeconomico, $boton_busquedaunidad);
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_marca, $boton_busquedaunidad);
		
		$cancelar_busqueda.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-busquedaunidad-overlay').fadeOut(remove);
			
			//$('#forma-clientsdf-window').find('input[name=cliente]').focus();
		});
		
		$cadena_noeconomico.focus();
	}//Termina buscador de Unidades(Vehiculos)

	
	
	//buscador de presentaciones disponibles para un producto
	$buscador_direcciones_fiscales = function(id_cliente){
		$(this).modalPanel_df();
		var $dialogoc =  $('#forma-df-window');
		$dialogoc.append($('div.buscador_direcciones_fiscales').find('table.formaBusqueda_df').clone());
		$('#forma-df-window').css({"margin-left": -150, "margin-top": -180});
		
		var $tabla_resultados = $('#forma-df-window').find('#tabla_resultado');
		//var $cancelar_plugin_busca_lotes_producto = $('#forma-buscapresentacion-window').find('a[href*=cencela]');
		var $cancelar_plugin_busca_lotes_producto = $('#forma-df-window').find('#cencela');
		$tabla_resultados.children().remove();
		
		$cancelar_plugin_busca_lotes_producto.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar_plugin_busca_lotes_producto.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		//aquí se arma la cadena json para traer las Direcciones Fiscales del cliente
		var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDireccionesFiscalesCliente.json';
		$arreglo2 = { 'id_cliente':id_cliente }
		$.post(input_json2,$arreglo2,function(entrydf){
			//crea el tr con los datos del producto seleccionado
			$.each(entrydf['DirFiscal'],function(entryIndex ,df){
				trr = '<tr>';
					trr += '<td width="430">';
						trr += df['direccion_fiscal'];
						trr += '<input type="hidden" id="iddf" value="'+df['id_df']+'">';
						trr += '<input type="hidden" id="direccion" value="'+df['direccion_fiscal']+'">';
					trr += '</td>';
				trr += '</tr>';
				$tabla_resultados.append(trr);
			});//termina llamada json
			
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
				//llamada a la funcion que busca y agrega producto al grid, se le pasa como parametro el lote y el almacen
				var id_prod = $(this).find('span.id_prod').html();
				var prec_unitario= $(this).find('span.costo').html();
				$('#forma-pocpedidos-window').find('input[name=id_df]').val($(this).find('#iddf').val());
				$('#forma-pocpedidos-window').find('input[name=dircliente]').val($(this).find('#direccion').val());
				//elimina la ventana de busqueda
				var remove = function() {$(this).remove();};
				$('#forma-df-overlay').fadeOut(remove);
			});
			
			$cancelar_plugin_busca_lotes_producto.click(function(event){
				//event.preventDefault();
				var remove = function() {$(this).remove();};
				$('#forma-df-overlay').fadeOut(remove);
			});
		});
		
	}//termina buscador dpresentaciones disponibles de un producto
	
    
    
    //funcion para aplicar evento a trs de una tabla para permitir seleccionar elemento desde el teclado
    $aplicarEventoSeleccionarTrkeypress = function($grid){
		var tr = $("tr", $grid).size();
		tr;
		
		//$('tr:first', $grid).css({background : '#FBD850'});
		$('tr:eq(0)', $grid).find('td').css({background : '#FBD850'});
		
		$('tr:eq(0)', $grid).focus();
		
		
		//$('tr:first' , $grid).find('td').css({background : '#FBD850'});
		
		//alert($('tr:first' , $grid).find('td:eq(0)').find('#direccion').val());
		
		
		//.css({background : '#FBD850'});
		/*
		$('tr:odd' , $grid).keypress(function () {
			$(this).find('td').css({'background-color': '#FBD850'});
		}, function() {
			$(this).find('td').css({'background-color':'#e7e8ea'});
		});
		
		$('tr:even' , $grid).keypress(function () {
			$(this).find('td').css({'background-color':'#FBD850'});
		}, function() {
			$(this).find('td').css({'background-color':'#FFFFFF'});
		});
		*/
		
		
		/*
		$grid.find('tr').each(function (index){
			$(this).find('td').css({'background':'#FBD850'});
		});
		*/
		
		
		$campo_sku.onkeyup(function(e){
			if(e.which == 13){
				$agregar_producto.trigger('click');
				return false;
			}
		});
			/*
		var oTable = $('#example').dataTable( {
			"sScrollY": 200,
			"sScrollX": "100%",
			"sScrollXInner": "110%"
		} );
		
		var keys = new KeyTable( {
			"table": document.getElementById('example'),
			"datatable": oTable
		} );
		*/
	}
	
	
	
	
	$agregarDatosClienteSeleccionado = function($select_moneda,$select_condiciones,$select_vendedor, $select_metodo_pago, array_monedas, array_condiciones, array_vendedores, array_metodos_pago, $no_cuenta, $etiqueta_digit, id_cliente, no_control, razon_social, dir_cliente, empresa_immex, tasa_ret_immex, cuenta_mn, cuenta_usd, id_moneda, id_termino, id_vendedor, num_lista_precio, id_metodo_de_pago, tiene_dir_fiscal, cred_susp){
		
		if(cred_susp=='false'){
			//asignar a los campos correspondientes el sku y y descripcion
			$('#forma-pocpedidos-window').find('input[name=id_cliente]').val( id_cliente );
			$('#forma-pocpedidos-window').find('input[name=nocliente]').val( no_control );
			$('#forma-pocpedidos-window').find('input[name=razoncliente]').val( razon_social );
			$('#forma-pocpedidos-window').find('input[name=empresa_immex]').val( empresa_immex );
			$('#forma-pocpedidos-window').find('input[name=tasa_ret_immex]').val( tasa_ret_immex );
			$('#forma-pocpedidos-window').find('input[name=cta_mn]').val( cuenta_mn );
			$('#forma-pocpedidos-window').find('input[name=cta_usd]').val( cuenta_usd );
			$('#forma-pocpedidos-window').find('input[name=num_lista_precio]').val( num_lista_precio );
			//por default asignamos cero para el campo id de Direccion Fiscal, esto significa que la direccion se tomara de la tabla de clientes
			$('#forma-pocpedidos-window').find('input[name=id_df]').val(0);
			
			if(tiene_dir_fiscal=='true'){
				//llamada a la funcion que busca las direcciones fiscales del cliente.
				//se le pasa como parametro el id del cliente
				$buscador_direcciones_fiscales($('#forma-pocpedidos-window').find('input[name=id_cliente]').val());
			}else{
				//si no tiene varias direcciones fiscales, se asigna la direccion default
				$('#forma-pocpedidos-window').find('input[name=dircliente]').val(dir_cliente);
				$('#forma-pocpedidos-window').find('input[name=id_df]').val(0);
			}
			var moneda_hmtl = '';
			
			/*
			//carga el select de monedas  con la moneda del cliente seleccionada por default
			if(parseInt(num_lista_precio)>0){
				//aquí se arma la cadena json para traer la moneda de la lista de precio
				var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMonedaListaCliente.json';
				$arreglo2 = { 'lista_precio':num_lista_precio }
				$.post(input_json2,$arreglo2,function(entry2){
					id_moneda=entry2['listaprecio'][0]['moneda_id'];
					$select_moneda.children().remove();
					$.each(array_monedas ,function(entryIndex,moneda){
						if( parseInt(moneda['id']) == parseInt(id_moneda) ){
							moneda_hmtl += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion'] + '</option>';
						}else{
							//moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
						}
					});
					$select_moneda.append(moneda_hmtl);
				});
			}else{
				$select_moneda.children().remove();
				$.each(array_monedas ,function(entryIndex,moneda){
					if( parseInt(moneda['id']) == parseInt(id_moneda) ){
						moneda_hmtl += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion'] + '</option>';
					}else{
						moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
					}
				});
				$select_moneda.append(moneda_hmtl);
			}
			*/
			
			
			$select_moneda.children().remove();
			$.each(array_monedas ,function(entryIndex,moneda){
				if( parseInt(moneda['id']) == parseInt(id_moneda) ){
					moneda_hmtl += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion'] + '</option>';
				}else{
					moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
				}
			});
			$select_moneda.append(moneda_hmtl);
			
			
			
			//carga select de condiciones con los dias de Credito default del Cliente
			$select_condiciones.children().remove();
			var hmtl_condiciones;
			$.each(array_condiciones, function(entryIndex,condicion){
				if( parseInt(condicion['id']) == parseInt(id_termino) ){
					hmtl_condiciones += '<option value="' + condicion['id'] + '" selected="yes">' + condicion['descripcion'] + '</option>';
				}else{
					hmtl_condiciones += '<option value="' + condicion['id'] + '" >' + condicion['descripcion'] + '</option>';
				}
			});
			$select_condiciones.append(hmtl_condiciones);
			
			//carga select de vendedores
			$select_vendedor.children().remove();
			var hmtl_vendedor;
			$.each(array_vendedores,function(entryIndex,vendedor){
				if( parseInt(vendedor['id']) == parseInt(id_vendedor) ){
					hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes">' + vendedor['nombre_agente'] + '</option>';
				}else{
					hmtl_vendedor += '<option value="' + vendedor['id'] + '" >' + vendedor['nombre_agente'] + '</option>';
				}
			});
			$select_vendedor.append(hmtl_vendedor);
			
			//alert("id_metodo_de_pago: "+id_metodo_de_pago);
			if(parseInt(id_metodo_de_pago)==0){
				id_metodo_de_pago=6;//si el cliente no tiene asignado un metodo de pago, se le asigna por default 6=No Identificado
			}
			
			//carga select de metodos de pago
			$select_metodo_pago.children().remove();
			var hmtl_metodo;
			$.each(array_metodos_pago,function(entryIndex,metodo){
				if ( parseInt(metodo['id']) == parseInt(id_metodo_de_pago) ){
					hmtl_metodo += '<option value="' + metodo['id'] + '" selected="yes">' + metodo['titulo'] + '</option>';
				}else{
					hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
				}
			});
			$select_metodo_pago.append(hmtl_metodo);
			
			
			if(parseInt(id_metodo_de_pago)>0){
				$no_cuenta.val('');
				
				//valor_metodo 2=Tarjeta Credito, 3=Tarjeta Debito
				if(parseInt(id_metodo_de_pago)==2 || parseInt(id_metodo_de_pago)==3){
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
				
				//id_metodo_de_pago 4=Cheque Nominativo, 5=Transferencia Electronica de Fondos
				if(parseInt(id_metodo_de_pago)==4 || parseInt(id_metodo_de_pago)==5){
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
						$no_cuenta.val($('#forma-pocpedidos-window').find('input[name=cta_mn]').val());
					}else{
						$etiqueta_digit.val('Numero de Cuenta en Dolares');
						$no_cuenta.val($('#forma-pocpedidos-window').find('input[name=cta_usd]').val());
					}
				}
				
				//id_metodo_de_pago 1=Efectivo, 6=No Identificado
				if(parseInt(id_metodo_de_pago)==1 || parseInt(id_metodo_de_pago)==6){
					if(!$no_cuenta.is(':disabled')) {
						$no_cuenta.attr('disabled','-1');
					}
					if(!$etiqueta_digit.is(':disabled')) {
						$etiqueta_digit.attr('disabled','-1');
					}
				}
				
				//id_metodo_de_pago 7=NA(No Aplica)
				if(parseInt(id_metodo_de_pago)==7){
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
			}
		}else{
			jAlert('El cliente '+razon_social+', tiene Cr&eacute;dito Suspendido.', 'Atencion!', function(r) { 
				$('#forma-pocpedidos-window').find('input[name=nocliente]').val('');
				$('#forma-pocpedidos-window').find('input[name=razoncliente]').val('');
				$('#forma-pocpedidos-window').find('input[name=nocliente]').focus();
			});
		}
	}
	
	
	
	//buscador de clientes
	$busca_clientes = function($select_moneda,$select_condiciones,$select_vendedor, $select_metodo_pago, array_monedas, array_condiciones, array_vendedores, array_metodos_pago, $no_cuenta, $etiqueta_digit, razon_social_cliente, numero_control ){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscacliente();
		var $dialogoc =  $('#forma-buscacliente-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_clientes').find('table.formaBusqueda_clientes').clone());
		$('#forma-buscacliente-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscacliente-window').find('#tabla_resultado');
		
		//var $busca_cliente_modalbox = $('#forma-buscacliente-window').find('a[href*=busca_cliente_modalbox]');
		//var $cancelar_plugin_busca_cliente = $('#forma-buscacliente-window').find('a[href*=cencela]');
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
		
		var html = '';
		$select_filtro_por.children().remove();
		html='<option value="0">[-- Opcion busqueda --]</option>';
		
		if(numero_control != ''){
			//asignamos el numero de control al campo de busqueda
			$cadena_buscar.val(numero_control);
			if(razon_social_cliente == ''){
				html+='<option value="1" selected="yes">No. de control</option>';
			}else{
				html+='<option value="1">No. de control</option>';
			}
		}else{
			html+='<option value="1">No. de control</option>';
		}
		html+='<option value="2">RFC</option>';
		if(razon_social_cliente != ''){
			//asignamos la Razon Social del Cliente al campo Nombre
			$cadena_buscar.val(razon_social_cliente);
			html+='<option value="3" selected="yes">Razon social</option>';
		}else{
			if(razon_social_cliente == '' && numero_control == ''){
				html+='<option value="3" selected="yes">Razon social</option>';
			}else{
				html+='<option value="3">Razon social</option>';
			}
		}
		html+='<option value="4">CURP</option>';
		html+='<option value="5">Alias</option>';
		$select_filtro_por.append(html);
		
		
		$cadena_buscar.focus();
		
		//click buscar clientes
		$busca_cliente_modalbox.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorClientes.json';
			$arreglo = {	'cadena':$cadena_buscar.val(),
							'filtro':$select_filtro_por.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                        }
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['clientes'],function(entryIndex,cliente){
					trr = '<tr>';
						trr += '<td width="80">';
							trr += '<input type="hidden" id="idclient" value="'+cliente['id']+'">';
							trr += '<input type="hidden" id="direccion" value="'+cliente['direccion']+'">';
							trr += '<input type="hidden" id="id_moneda" value="'+cliente['moneda_id']+'">';
							trr += '<input type="hidden" id="moneda" value="'+cliente['moneda']+'">';
							trr += '<input type="hidden" id="vendedor_id" value="'+cliente['cxc_agen_id']+'">';
							trr += '<input type="hidden" id="terminos_id" value="'+cliente['terminos_id']+'">';
							trr += '<input type="hidden" id="emp_immex" value="'+cliente['empresa_immex']+'">';
							trr += '<input type="hidden" id="tasa_immex" value="'+cliente['tasa_ret_immex']+'">';
							trr += '<span class="no_control">'+cliente['numero_control']+'</span>';
							trr += '<input type="hidden" id="cta_mn" value="'+cliente['cta_pago_mn']+'">';
							trr += '<input type="hidden" id="cta_usd" value="'+cliente['cta_pago_usd']+'">';
							trr += '<input type="hidden" id="lista_precios" value="'+cliente['lista_precio']+'">';
							trr += '<input type="hidden" id="metodo_id" value="'+cliente['metodo_pago_id']+'">';
							trr += '<input type="hidden" id="tiene_df" value="'+cliente['tiene_dir_fiscal']+'">';//variable para indicar si tiene direccion fiscal
							trr += '<input type="hidden" id="cred_susp" value="'+cliente['credito_suspendido']+'">';//variable para indicar si el credito esta suspendido
						trr += '</td>';
						trr += '<td width="145"><span class="rfc">'+cliente['rfc']+'</span></td>';
						trr += '<td width="375"><span class="razon">'+cliente['razon_social']+'</span></td>';
					trr += '</tr>';
					
					$tabla_resultados.append(trr);
				});
				
				//$tabla_resultados.find('tr').focus();
				
				$tabla_resultados.find('tr:odd').find('td').css({'background-color' : '#e7e8ea'});
				$tabla_resultados.find('tr:even').find('td').css({'background-color' : '#FFFFFF'});
				
				$('tr:odd' , $tabla_resultados).hover(function () {
					$(this).find('td').css({background : '#FBD850'});
				}, function() {
					$(this).find('td').css({'background-color':'#e7e8ea'});
				});
				$('tr:even' , $tabla_resultados).hover(function () {
					$(this).find('td').css({'background-color':'#FBD850'});
				}, function() {
					$(this).find('td').css({'background-color':'#FFFFFF'});
				});
				
				
				//seleccionar un producto del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					var id_cliente = $(this).find('#idclient').val();
					var no_control = $(this).find('span.no_control').html();
					var razon_social = $(this).find('span.razon').html();
					var dir_cliente = $(this).find('#direccion').val();
					var empresa_immex = $(this).find('#emp_immex').val();
					var tasa_ret_immex = $(this).find('#tasa_immex').val();
					var cuenta_mn = $(this).find('#cta_mn').val();
					var cuenta_usd = $(this).find('#cta_usd').val();
					
					var id_moneda=$(this).find('#id_moneda').val();
					var id_termino=$(this).find('#terminos_id').val();
					var id_vendedor=$(this).find('#vendedor_id').val();
					//almacena el valor de la lista
					var num_lista_precio =$(this).find('#lista_precios').val();
					var id_metodo_de_pago=$(this).find('#metodo_id').val();
					var tiene_dir_fiscal=$(this).find('#tiene_df').val();
					var cred_susp = $(this).find('#cred_susp').val();
					
					//llamada a la funcion para agregar los datos del cliente seleccionado
					$agregarDatosClienteSeleccionado($select_moneda,$select_condiciones,$select_vendedor, $select_metodo_pago, array_monedas, array_condiciones, array_vendedores, array_metodos_pago, $no_cuenta, $etiqueta_digit, id_cliente, no_control, razon_social, dir_cliente, empresa_immex, tasa_ret_immex, cuenta_mn, cuenta_usd, id_moneda, id_termino, id_vendedor, num_lista_precio, id_metodo_de_pago, tiene_dir_fiscal, cred_susp);
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente-overlay').fadeOut(remove);
					//asignar el enfoque al campo Razon social del cliente
					$('#forma-pocpedidos-window').find('input[name=razoncliente]').focus();
				});
				
				
				//$aplicarEventoSeleccionarTrkeypress($tabla_resultados);
			});
		});//termina llamada json
		
		
		//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
		if($cadena_buscar.val() != ''){
			$busca_cliente_modalbox.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_buscar, $busca_cliente_modalbox);
		$(this).aplicarEventoKeypressEjecutaTrigger($select_filtro_por, $busca_cliente_modalbox);
		
		$cancelar_plugin_busca_cliente.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscacliente-overlay').fadeOut(remove);
			$('#forma-pocpedidos-window').find('input[name=razoncliente]').focus();
		});
	}//termina buscador de clientes
	
	
	
	
	
	//buscador de productos
	$busca_productos = function(sku_buscar, descripcion){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscaproducto($('#forma-pocpedidos-window').find('input[name=nombre_producto]'));
		var $dialogoc =  $('#forma-buscaproducto-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());
		
		$('#forma-buscaproducto-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscaproducto-window').find('#tabla_resultado');
		
		var $campo_sku = $('#forma-buscaproducto-window').find('input[name=campo_sku]');
		var $select_tipo_producto = $('#forma-buscaproducto-window').find('select[name=tipo_producto]');
		var $campo_descripcion = $('#forma-buscaproducto-window').find('input[name=campo_descripcion]');
		
		//var $buscar_plugin_producto = $('#forma-buscaproducto-window').find('a[href*=busca_producto_modalbox]');
		//var $cancelar_plugin_busca_producto = $('#forma-buscaproducto-window').find('a[href*=cencela]');
		var $buscar_plugin_producto = $('#forma-buscaproducto-window').find('#busca_producto_modalbox');
		var $cancelar_plugin_busca_producto = $('#forma-buscaproducto-window').find('#cencela');
		
		//funcionalidad botones
		$buscar_plugin_producto.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$buscar_plugin_producto.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		   
		$cancelar_plugin_busca_producto.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar_plugin_busca_producto.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});

		//buscar todos los tipos de productos
		var input_json_tipos = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProductoTipos.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_tipos,$arreglo,function(data){
			//Llena el select tipos de productos en el buscador
			$select_tipo_producto.children().remove();
			var prod_tipos_html = '<option value="0">[--Seleccionar Tipo--]</option>';
			$.each(data['prodTipos'],function(entryIndex,pt){
				/*
				if(parseInt( pt['id']) == 1 ){
					prod_tipos_html += '<option value="' + pt['id'] + '"  selected="yes">' + pt['titulo'] + '</option>';
				}else{
					prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
				}*/
				prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
			});
			$select_tipo_producto.append(prod_tipos_html);
		});
		
		//Aqui asigno al campo sku del buscador si el usuario ingresó un sku antes de hacer clic en buscar en la ventana principal
		$campo_sku.val(sku_buscar);
		
		//asignamos la descripcion del producto, si el usuario capturo la descripcion antes de abrir el buscador
		$campo_descripcion.val(descripcion);
		
		$campo_sku.focus();
		
		//click buscar productos
		$buscar_plugin_producto.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorProductos.json';
			$arreglo = {	'sku':$campo_sku.val(),
							'tipo':$select_tipo_producto.val(),
							'descripcion':$campo_descripcion.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['productos'],function(entryIndex,producto){
					trr = '<tr>';
						trr += '<td width="120">';
							trr += '<input type="hidden" id="id_prod_buscador" value="'+producto['id']+'">';
							trr += '<span class="sku_prod_buscador">'+producto['sku']+'</span>';
						trr += '</td>';
						trr += '<td width="280"><span class="titulo_prod_buscador">'+producto['descripcion']+'</span></td>';
						trr += '<td width="90">';
							trr += '<span class="unidad_id" style="display:none;">'+producto['unidad_id']+'</span>';
							trr += '<span class="utitulo">'+producto['unidad']+'</span>';
                                                       
						trr += '</td>';
						trr += '<td width="90"><span class="tipo_prod_buscador">'+producto['tipo']+'</span></td>';
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
					//asignar a los campos correspondientes el sku y y descripcion
					$('#forma-pocpedidos-window').find('input[name=sku_producto]').val($(this).find('span.sku_prod_buscador').html());
					$('#forma-pocpedidos-window').find('input[name=nombre_producto]').val($(this).find('span.titulo_prod_buscador').html());
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaproducto-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-pocpedidos-window').find('input[name=sku_producto]').focus();
				});
				
			});//termina llamada json
		});
		
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if($campo_sku.val() != ''){
			$buscar_plugin_producto.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_sku, $buscar_plugin_producto);
		$(this).aplicarEventoKeypressEjecutaTrigger($select_tipo_producto, $buscar_plugin_producto);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_descripcion, $buscar_plugin_producto);
		
		$cancelar_plugin_busca_producto.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscaproducto-overlay').fadeOut(remove);
			$('#forma-pocpedidos-window').find('input[name=nombre_producto]').focus();
		});
		
	}//termina buscador de productos
	
	
	
	
	
	//buscador de presentaciones disponibles para un producto
	$buscador_presentaciones_producto = function($id_cliente,nocliente, sku_producto,$nombre_producto,$grid_productos,$select_moneda,$tipo_cambio, arrayMonedas){
		//verifica si el campo rfc proveedor no esta vacio
		var cliente_listaprecio=  $('#forma-pocpedidos-window').find('input[name=num_lista_precio]').val();
		if(nocliente != ''){
			//verifica si el campo sku no esta vacio para realizar busqueda
			if(sku_producto != ''){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPresentacionesProducto.json';
				$arreglo = {'sku':sku_producto,
							'lista_precios':cliente_listaprecio,
							'id_client':$id_cliente.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
				
				var trr = '';
				
				$.post(input_json,$arreglo,function(entry){
					
					//verifica si el arreglo  retorno datos
					if (entry['Presentaciones'].length > 0){
						
						//if (entry['Presentaciones'][0]['exis_prod_lp']=='1'){
							$(this).modalPanel_Buscapresentacion();
							var $dialogoc =  $('#forma-buscapresentacion-window');
							$dialogoc.append($('div.buscador_presentaciones').find('table.formaBusqueda_presentaciones').clone());
							$('#forma-buscapresentacion-window').css({"margin-left": -200, "margin-top": -180});
							
							var $tabla_resultados = $('#forma-buscapresentacion-window').find('#tabla_resultado');
							//var $cancelar_plugin_busca_lotes_producto = $('#forma-buscapresentacion-window').find('a[href*=cencela]');
							var $cancelar_plugin_busca_lotes_producto = $('#forma-buscapresentacion-window').find('#cencela');
							$tabla_resultados.children().remove();
							
							$cancelar_plugin_busca_lotes_producto.mouseover(function(){
								$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
							});
							$cancelar_plugin_busca_lotes_producto.mouseout(function(){
								$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
							});
							
							//crea el tr con los datos del producto seleccionado
							$.each(entry['Presentaciones'],function(entryIndex,pres){
								trr = '<tr>';
									trr += '<td width="100">';
										trr += '<span class="id_prod" style="display:none">'+pres['id']+'</span>';
										trr += '<span class="sku">'+pres['sku']+'</span>';
									trr += '</td>';
									trr += '<td width="250"><span class="titulo">'+pres['titulo']+'</span></td>';
									trr += '<td width="80">';
										trr += '<span class="unidad" style="display:none">'+pres['unidad']+'</span>';
										trr += '<span class="id_pres" style="display:none">'+pres['id_presentacion']+'</span>';
										trr += '<span class="pres">'+pres['presentacion']+'</span>';
										trr += '<span class="costo" style="display:none">'+pres['precio']+'</span>';
										trr += '<span class="idmon" style="display:none">'+pres['id_moneda']+'</span>';
										trr += '<span class="tc" style="display:none">'+pres['tc']+'</span>';
										trr += '<span class="dec" style="display:none">'+pres['decimales']+'</span>';
										trr += '<span class="exislp" style="display:none">'+pres['exis_prod_lp']+'</span>';
										trr += '<span class="idImpto" style="display:none">'+pres['id_impto']+'</span>';
										trr += '<span class="valorImpto" style="display:none">'+pres['valor_impto']+'</span>';
									trr += '</td>';
								trr += '</tr>';
								$tabla_resultados.append(trr);
							});//termina llamada json
							
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
								
								//llamada a la funcion que busca y agrega producto al grid, se le pasa como parametro el lote y el almacen
								var id_prod = $(this).find('span.id_prod').html();
								var sku = $(this).find('span.sku').html();
								var titulo = $(this).find('span.titulo').html();
								var unidad = $(this).find('span.unidad').html();
								var id_pres = $(this).find('span.id_pres').html();
								var pres = $(this).find('span.pres').html();
								var num_dec = $(this).find('span.dec').html();
								var prec_unitario = $(this).find('span.costo').html();
								var id_moneda = $(this).find('span.idmon').html();
								//tipo de cambio de la moneda del producto
								var tcMonProd = $(this).find('span.tc').html();
								var exislp = $(this).find('span.exislp').html();
								
								var idImpto = $(this).find('span.idImpto').html();
								var valorImpto = $(this).find('span.valorImpto').html();
								
								if($tipo_cambio.val()!='' && $tipo_cambio.val()!=' '){
									if(exislp=='1'){
										//llamada a la funcion que agrega el producto al grid
										$agrega_producto_grid($grid_productos,id_prod,sku,titulo,unidad,id_pres,pres,prec_unitario,$select_moneda,id_moneda,$tipo_cambio,num_dec, arrayMonedas, tcMonProd, idImpto, valorImpto);
									}else{
										jAlert(exislp, 'Atencion!', function(r) { 
											$('#forma-pocpedidos-window').find('input[name=sku_producto]').focus();
										});
									}
								}else{
									jAlert('Es necesario ingresar el Tipo de Cambio.', 'Atencion!');
								}
								
								//elimina la ventana de busqueda
								var remove = function() {$(this).remove();};
								$('#forma-buscapresentacion-overlay').fadeOut(remove);
							});
							
							$cancelar_plugin_busca_lotes_producto.click(function(event){
								//event.preventDefault();
								var remove = function() {$(this).remove();};
								$('#forma-buscapresentacion-overlay').fadeOut(remove);
								$('#forma-pocpedidos-window').find('input[name=sku_producto]').focus();
							});
					}else{
						jAlert('El producto que intenta agregar no existe, pruebe ingresando otro.\nHaga clic en Buscar.', 'Atencion!', function(r) { 
							$('#forma-pocpedidos-window').find('input[name=titulo_producto]').val('');
						});
					}
				});
				
			}else{
				jAlert('Es necesario ingresar un Sku de producto valido.', 'Atencion!', function(r) { 
					$('#forma-pocpedidos-window').find('input[name=sku_producto]').focus();
				});
			}
		}else{
			jAlert('Es necesario seleccionar un Cliente.', 'Atencion!', function(r) { 
				$('#forma-pocpedidos-window').find('input[name=nocliente]').focus();
			});
		}
		
	}//termina buscador dpresentaciones disponibles de un producto
	
    
    
    
	
	//calcula totales(subtotal, impuesto, total)
	$calcula_totales = function(){
		var $campo_subtotal = $('#forma-pocpedidos-window').find('input[name=subtotal]');
		var $campo_impuesto = $('#forma-pocpedidos-window').find('input[name=impuesto]');
		var $campo_impuesto_retenido = $('#forma-pocpedidos-window').find('input[name=impuesto_retenido]');
		var $campo_total = $('#forma-pocpedidos-window').find('input[name=total]');
		//var $campo_tc = $('#forma-pocpedidos-window').find('input[name=tc]');
		//var $valor_impuesto = $('#forma-pocpedidos-window').find('input[name=valorimpuesto]');
		var $grid_productos = $('#forma-pocpedidos-window').find('#grid_productos');
		var $empresa_immex = $('#forma-pocpedidos-window').find('input[name=empresa_immex]');
		var $tasa_ret_immex = $('#forma-pocpedidos-window').find('input[name=tasa_ret_immex]');
		
		var sumaSubTotal = 0; //es la suma de todos los importes
		var sumaImpuesto = 0; //valor del iva
		var impuestoRetenido = 0; //monto del iva retenido de acuerdo a la tasa de retencion immex
		var sumaTotal = 0; //suma del subtotal + totalImpuesto
		
		/*
		//si valor del impuesto es null o vacio, se le asigna un 0
		if( $valor_impuesto.val()== null || $valor_impuesto.val()== ''){
			$valor_impuesto.val(0);
		}
		*/
		
		$grid_productos.find('tr').each(function (index){
			if(( $(this).find('#cost').val() != ' ') && ( $(this).find('#cant').val() != ' ' )){
				//alert($(this).find('#cost').val());
				//acumula los importes en la variable subtotal
				sumaSubTotal = parseFloat(sumaSubTotal) + parseFloat(quitar_comas($(this).find('#import').val()));
				//alert($(this).find('#import').val());
				if($(this).find('#totimp').val() != ''){
					//alert($(this).find('#totimp').val());
					sumaImpuesto =  parseFloat(sumaImpuesto) + parseFloat($(this).find('#totimp').val());
				}
			}
		});
		
		//calcular  la tasa de retencion IMMEX
		impuestoRetenido = parseFloat(sumaSubTotal) * parseFloat(parseFloat($tasa_ret_immex.val()));
		
		//calcula el total sumando el subtotal y el impuesto menos la retencion
		sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaImpuesto) - parseFloat(impuestoRetenido);
		
		//redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
		$campo_subtotal.val($(this).agregar_comas(  parseFloat(sumaSubTotal).toFixed(2)  ));
		//redondea a dos digitos el impuesto y lo asigna al campo impuesto
		$campo_impuesto.val($(this).agregar_comas(  parseFloat(sumaImpuesto).toFixed(2)  ));
		//redondea a dos digitos el impuesto y lo asigna al campo retencion
		$campo_impuesto_retenido.val($(this).agregar_comas(  parseFloat(impuestoRetenido).toFixed(2)  ));
		//redondea a dos digitos la suma  total y se asigna al campo total
		$campo_total.val($(this).agregar_comas(  parseFloat(sumaTotal).toFixed(2)  ));
		
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
	$agrega_producto_grid = function($grid_productos,id_prod,sku,titulo,unidad,id_pres,pres,prec_unitario,$select_moneda, id_moneda, $tipo_cambio,num_dec, arrayMonedas, tcMonProd, idImpto, valorImpto){
		var $id_impuesto = $('#forma-pocpedidos-window').find('input[name=id_impuesto]');
		var $valor_impuesto = $('#forma-pocpedidos-window').find('input[name=valorimpuesto]');
		var $incluye_produccion = $('#forma-pocpedidos-window').find('input[name=incluye_pro]');
		var $num_lista_precio = $('#forma-pocpedidos-window').find('input[name=num_lista_precio]');
		
		//esta es la moneda definida para el pedido
		var idMonedaPedido = $select_moneda.val();
		var precioOriginal = prec_unitario;
		var precioCambiado = 0.00;
		var importeImpuesto=0.00;
		
		//verificamos si la Lista de Precio trae moneda
		if(parseInt($num_lista_precio.val())>0){
			//verificamos si el grid no tiene registros
			if(parseInt($("tr", $grid_productos).size())<=0){
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
			
			//si la moneda del Pedido es diferente a la moneda del Precio del Producto
			//entonces convertimos el precio a la moneda del pedido de acuerdo al tipo de cambio actual
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
			$grid_productos.find('tr').each(function (index){
				if(( $(this).find('#skuprod').val() == sku.toUpperCase() )  && (parseInt($(this).find('#idpres').val())== parseInt(id_pres) ) && (parseInt($(this).find('#elim').val())!=0)){
					encontrado=1;//el producto ya esta en el grid
				}
			});
			
			
			if(parseInt(encontrado)!=1){//si el producto no esta en el grid entra aqui
				//ocultamos el boton facturar para permitir Guardar los cambios  antes de facturar
				$('#forma-pocpedidos-window').find('#facturar').hide();
				//obtiene numero de trs
				var tr = $("tr", $grid_productos).size();
				tr++;
				
				var trr = '';
				trr = '<tr>';
					trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
						trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
						trr += '<input type="hidden" 	name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
						trr += '<input type="hidden" 	name="iddetalle" id="idd" value="0">';//este es el id del registro que ocupa el producto en la tabla pocpedidos_detalles
						trr += '<input type="hidden" 	name="noTr" value="'+ tr +'">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="114">';
						trr += '<input type="hidden" 	name="idproducto" id="idprod" value="'+ id_prod +'">';
						trr += '<input type="text" 		name="sku" value="'+ sku +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="202">';
						trr += '<input type="text" 		name="nombre" 	value="'+ titulo +'" id="nom" class="borde_oculto" readOnly="true" style="width:198px;">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
						trr += '<input type="text" 		name="unidad'+ tr +'" 	value="'+ unidad +'" id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
						trr += '<input type="hidden"    name="id_presentacion"  	value="'+  id_pres +'" id="idpres">';
						trr += '<input type="hidden"    name="numero_decimales"     value="'+  num_dec +'" id="numdec">';
						trr += '<input type="text" 	name="presentacion'+ tr +'" value="'+  pres +'" id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
						trr += '<input type="text" name="cantidad" value=" " class="cantidad'+ tr +'" id="cant" style="width:76px;">';
					trr += '</td>';
					trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
						trr += '<input type="text" name="costo" value="'+ precioCambiado +'" class="costo'+ tr +'" id="cost" style="width:86px; text-align:right;">';
					trr += '</td>';
					trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
						trr += '<input type="text" name="importe'+ tr +'" value="" id="import" class="borde_oculto" readOnly="true" style="width:86px; text-align:right;">';
						trr += '<input type="hidden" name="id_imp_prod"   value="'+  idImpto +'" id="idimppord">';
						trr += '<input type="hidden" name="valor_imp"     value="'+  valorImpto +'" id="ivalorimp">';
						trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="0">';
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
				
				$grid_productos.append(trr);
				
				
				if($incluye_produccion.val()=='true'){
					//aplicar evento click al check, cuando la empresa incluya modulo de produccion
					$aplicar_evento_click_a_input_check($grid_productos.find('.checkProd'+ tr));
					$grid_productos.find('.checkProd'+ tr).hide();//ocultar check porque es un registro nuevo, se debe mostrar  hasta que se genere un warning
				}else{
					//ocualtar campos,  cuando la empresa no incluya modulo de produccion
					$grid_productos.find('#td_oculto'+tr).hide();
				}
				
				//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
				$grid_productos.find('#cant').focus(function(e){
					if($(this).val() == ' '){
							$(this).val('');
					}
				});
				
				//recalcula importe al perder enfoque el campo cantidad
				$grid_productos.find('#cant').blur(function(){
					if ($(this).val() == ''){
						$(this).val(' ');
					}
					if( ($(this).val() != ' ') && ($(this).parent().parent().find('#cost').val() != ' ') )
					{	//calcula el importe
						$(this).parent().parent().find('#import').val(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cost').val()));
						//redondea el importe en dos decimales
						//$(this).parent().parent().find('#import').val( Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100 );
						$(this).parent().parent().find('#import').val( parseFloat($(this).parent().parent().find('#import').val()).toFixed(2) );
						
						//calcula el impuesto para este producto multiplicando el importe por el valor del iva
						$(this).parent().parent().find('#totimp').val( parseFloat( $(this).parent().parent().find('#import').val() ) * parseFloat(  $(this).parent().parent().find('#ivalorimp').val()  ));
					}else{
						$(this).parent().parent().find('#import').val('');
						$(this).parent().parent().find('#totimp').val('');
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
					if(patron.test($(this).val())){
						alert("Si valido"+$(this).val());
					}else{
						alert("El numero de decimales es incorrecto: "+$(this).val());
						$(this).val('')
					}
					*/
					
					if(!patron.test($(this).val())){
						//alert("Si valido"+$(this).val());
					}else{
						
					}
					
					$calcula_totales();//llamada a la funcion que calcula totales
				});
				
				//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
				$grid_productos.find('#cost').focus(function(e){
					if($(this).val() == ' '){
						$(this).val('');
					}
				});
				
				//recalcula importe al perder enfoque el campo costo
				$grid_productos.find('#cost').blur(function(){
					if ($(this).val() == ''){
						$(this).val(' ');
					}
					
					if( ($(this).val() != ' ') && ($(this).parent().parent().find('#cant').val() != ' ') )
					{	//calcula el importe
						$(this).parent().parent().find('#import').val( parseFloat($(this).val()) * parseFloat( $(this).parent().parent().find('#cant').val()) );
						//redondea el importe en dos decimales
						//$(this).parent().parent().find('#import').val(Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100);
						$(this).parent().parent().find('#import').val( parseFloat($(this).parent().parent().find('#import').val()).toFixed(2));
						
						//calcula el impuesto para este producto multiplicando el importe por el valor del iva
						$(this).parent().parent().find('#totimp').val( parseFloat($(this).parent().parent().find('#import').val()) * parseFloat(  $(this).parent().parent().find('#ivalorimp').val()  ));
					}else{
						$(this).parent().parent().find('#import').val('');
						$(this).parent().parent().find('#totimp').val('');
					}
					$calcula_totales();//llamada a la funcion que calcula totales
				});
				
				//validar campo costo, solo acepte numeros y punto
				$permitir_solo_numeros( $grid_productos.find('#cost') );
				$permitir_solo_numeros( $grid_productos.find('#cant') );
				
				/*
				//validar campo costo, solo acepte numeros y punto
				$grid_productos.find('#cost').keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				//validar campo cantidad, solo acepte numeros y punto
				$grid_productos.find('#cant').keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				*/
				
				//elimina un producto del grid
				$grid_productos.find('#delete'+ tr).bind('click',function(event){
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
				$('#forma-pocpedidos-window').find('input[name=sku_producto]').val('');
				$('#forma-pocpedidos-window').find('input[name=nombre_producto]').val('');
				
				//asignar el enfoque al campo catidad
				$grid_productos.find('.cantidad'+ tr).focus();
				
			}else{
				jAlert('El producto: '+sku+' con presentacion: '+pres+' ya se encuentra en el listado, seleccione otro diferente.', 'Atencion!', function(r) { 
					$('#forma-pocpedidos-window').find('input[name=nombre_producto]').val('');
					$('#forma-pocpedidos-window').find('input[name=sku_producto]').focus();
				});
			}
	}//termina agregar producto al grid
	
	
	
	
	//nuevo pedido
	$new_pedido.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_pocpedidos();
		
		var form_to_show = 'formapocpedidos00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$('#forma-pocpedidos-window').css({"margin-left": -340, 	"margin-top": -235});
		
		$forma_selected.prependTo('#forma-pocpedidos-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		//var json_string = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + accion + '/' + id_to_show + '/out.json';
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPedido.json';
		$arreglo = {'id_pedido':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
        
		var $id_pedido = $('#forma-pocpedidos-window').find('input[name=id_pedido]');
		var $folio = $('#forma-pocpedidos-window').find('input[name=folio]');
		var $total_tr = $('#forma-pocpedidos-window').find('input[name=total_tr]');
		var $busca_cliente = $('#forma-pocpedidos-window').find('a[href*=busca_cliente]');
		var $id_cliente = $('#forma-pocpedidos-window').find('input[name=id_cliente]');
		var $nocliente = $('#forma-pocpedidos-window').find('input[name=nocliente]');
		var $razon_cliente = $('#forma-pocpedidos-window').find('input[name=razoncliente]');
		var $id_df = $('#forma-pocpedidos-window').find('input[name=id_df]');
		var $dir_cliente = $('#forma-pocpedidos-window').find('input[name=dircliente]');
		var $empresa_immex = $('#forma-pocpedidos-window').find('input[name=empresa_immex]');
		var $tasa_ret_immex = $('#forma-pocpedidos-window').find('input[name=tasa_ret_immex]');
		var $incluye_produccion = $('#forma-pocpedidos-window').find('input[name=incluye_pro]');
		
		var $select_moneda = $('#forma-pocpedidos-window').find('select[name=select_moneda]');
		var $tipo_cambio = $('#forma-pocpedidos-window').find('input[name=tipo_cambio]');
		var $id_impuesto = $('#forma-pocpedidos-window').find('input[name=id_impuesto]');
		var $valor_impuesto = $('#forma-pocpedidos-window').find('input[name=valorimpuesto]');
		var $check_enviar_obser = $('#forma-pocpedidos-window').find('input[name=check_enviar_obser]');
		var $observaciones = $('#forma-pocpedidos-window').find('textarea[name=observaciones]');
		
		var $select_condiciones = $('#forma-pocpedidos-window').find('select[name=select_condiciones]');
		var $select_metodo_pago = $('#forma-pocpedidos-window').find('select[name=select_metodo_pago]');
		var $no_cuenta = $('#forma-pocpedidos-window').find('input[name=no_cuenta]');
		var $etiqueta_digit = $('#forma-pocpedidos-window').find('input[name=etiqueta_digit]');
		var $cta_mn = $('#forma-pocpedidos-window').find('input[name=cta_mn]');
		var $cta_usd = $('#forma-pocpedidos-window').find('input[name=cta_usd]');
		var $check_ruta = $('#forma-pocpedidos-window').find('input[name=check_ruta]');
		
		var $select_vendedor = $('#forma-pocpedidos-window').find('select[name=vendedor]');
		var $orden_compra = $('#forma-pocpedidos-window').find('input[name=orden_compra]');
		var $transporte = $('#forma-pocpedidos-window').find('input[name=transporte]');
		var $lugar_entrega = $('#forma-pocpedidos-window').find('input[name=lugar_entrega]');
		var $fecha_compromiso = $('#forma-pocpedidos-window').find('input[name=fecha_compromiso]');
		var $select_almacen = $('#forma-pocpedidos-window').find('select[name=select_almacen]');
		
		var $sku_producto = $('#forma-pocpedidos-window').find('input[name=sku_producto]');
		var $nombre_producto = $('#forma-pocpedidos-window').find('input[name=nombre_producto]');
		
		//buscar producto
		var $busca_sku = $('#forma-pocpedidos-window').find('a[href*=busca_sku]');
		//href para agregar producto al grid
		var $agregar_producto = $('#forma-pocpedidos-window').find('a[href*=agregar_producto]');
		
		var $cancelar_pedido = $('#forma-pocpedidos-window').find('#cancelar_pedido');
		var $descargarpdf = $('#forma-pocpedidos-window').find('#descargarpdf');
		var $cancelado = $('#forma-pocpedidos-window').find('input[name=cancelado]');
		
		//grid de productos
		var $grid_productos = $('#forma-pocpedidos-window').find('#grid_productos');
		//grid de errores
		var $grid_warning = $('#forma-pocpedidos-window').find('#div_warning_grid').find('#grid_warning');
		
		//var $flete = $('#forma-pocpedidos-window').find('input[name=flete]');
		var $subtotal = $('#forma-pocpedidos-window').find('input[name=subtotal]');
		var $impuesto = $('#forma-pocpedidos-window').find('input[name=impuesto]');
		var $total = $('#forma-pocpedidos-window').find('input[name=total]');
		
		
		var $pestana_transportista = $('#forma-pocpedidos-window').find('ul.pestanas').find('a[href=#tabx-2]');
		var $check_flete = $('#forma-pocpedidos-window').find('input[name=check_flete]');
		var $nombre_documentador = $('#forma-pocpedidos-window').find('input[name=nombre_documentador]');
		var $valor_declarado = $('#forma-pocpedidos-window').find('input[name=valor_declarado]');
		var $select_tviaje = $('#forma-pocpedidos-window').find('select[name=select_tviaje]');
		var $remolque1 = $('#forma-pocpedidos-window').find('input[name=remolque1]');
		var $remolque2 = $('#forma-pocpedidos-window').find('input[name=remolque2]');
		
		var $id_vehiculo = $('#forma-pocpedidos-window').find('input[name=id_vehiculo]');
		var $no_economico = $('#forma-pocpedidos-window').find('input[name=no_economico]');
		var $marca_vehiculo = $('#forma-pocpedidos-window').find('input[name=marca_vehiculo]');
		
		var $no_operador = $('#forma-pocpedidos-window').find('input[name=no_operador]');
		var $nombre_operador = $('#forma-pocpedidos-window').find('input[name=nombre_operador]');
		
		var $agena_id = $('#forma-pocpedidos-window').find('input[name=agena_id]');
		var $agena_no = $('#forma-pocpedidos-window').find('input[name=agena_no]');
		var $agena_nombre = $('#forma-pocpedidos-window').find('input[name=agena_nombre]');
		
		var $select_pais_origen = $('#forma-pocpedidos-window').find('select[name=select_pais_origen]');
		var $select_estado_origen = $('#forma-pocpedidos-window').find('select[name=select_estado_origen]');
		var $select_municipio_origen = $('#forma-pocpedidos-window').find('select[name=select_municipio_origen]');
		
		var $select_pais_dest = $('#forma-pocpedidos-window').find('select[name=select_pais_dest]');
		var $select_estado_dest = $('#forma-pocpedidos-window').find('select[name=select_estado_dest]');
		var $select_municipio_dest = $('#forma-pocpedidos-window').find('select[name=select_municipio_dest]');
		
		var $rem_id = $('#forma-pocpedidos-window').find('input[name=rem_id]');
		var $rem_no = $('#forma-pocpedidos-window').find('input[name=rem_no]');
		var $rem_nombre = $('#forma-pocpedidos-window').find('input[name=rem_nombre]');
		var $rem_dir = $('#forma-pocpedidos-window').find('input[name=rem_dir]');
		var $rem_dir_alterna = $('#forma-pocpedidos-window').find('input[name=rem_dir_alterna]');
		
		var $dest_id = $('#forma-pocpedidos-window').find('input[name=dest_id]');
		var $dest_no = $('#forma-pocpedidos-window').find('input[name=dest_no]');
		var $dest_nombre = $('#forma-pocpedidos-window').find('input[name=dest_nombre]');
		var $dest_dir = $('#forma-pocpedidos-window').find('input[name=dest_dir]');
		var $dest_dir_alterna = $('#forma-pocpedidos-window').find('input[name=dest_dir_alterna]');
		
		var $observaciones_transportista = $('#forma-pocpedidos-window').find('textarea[name=observaciones_transportista]');
		
		var $busca_vehiculo = $('#forma-pocpedidos-window').find('a[href=busca_vehiculo]');
		var $busca_operador = $('#forma-pocpedidos-window').find('a[href=busca_operador]');
		var $busca_agena = $('#forma-pocpedidos-window').find('a[href=busca_agena]');
		var $busca_remitente = $('#forma-pocpedidos-window').find('a[href=busca_remitente]');
		var $busca_dest = $('#forma-pocpedidos-window').find('a[href=busca_dest]');
		
		
		var $cerrar_plugin = $('#forma-pocpedidos-window').find('#close');
		var $cancelar_plugin = $('#forma-pocpedidos-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-pocpedidos-window').find('#submit');
		
		$pestana_transportista.parent().hide();
		
		//$campo_factura.css({'background' : '#ffffff'});
		
		//ocultar boton de facturar y descargar pdf. Solo debe estar activo en editar
		//$boton_descargarpdf.hide();
		$id_pedido.val(0);//para nueva pedido el id es 0
		$empresa_immex.val('false');
		$tasa_ret_immex.val('0');
		$cancelar_pedido.hide();
		$descargarpdf.hide();
		$cancelado .hide();
		
		$permitir_solo_numeros($no_cuenta);
		$no_cuenta.attr('disabled','-1');
		$etiqueta_digit.attr('disabled','-1');
		$folio.css({'background' : '#F0F0F0'});
		//$nocliente.css({'background' : '#F0F0F0'});
		$dir_cliente.css({'background' : '#F0F0F0'});
		$remolque2.css({'background' : '#F0F0F0'});
		$remolque2.attr('readonly',true);
		
		//quitar enter a todos los campos input
		$('#forma-pocpedidos-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		$nocliente.focus();
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("El Pedido se guard&oacute; con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-pocpedidos-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				//$('#forma-pocpedidos-window').find('.div_one').css({'height':'545px'});//sin errores
				$('#forma-pocpedidos-window').find('.pocpedidos_div_one').css({'height':'578px'});//con errores
				$('#forma-pocpedidos-window').find('div.interrogacion').css({'display':'none'});
				
				$grid_productos.find('#cant').css({'background' : '#ffffff'});
				$grid_productos.find('#cost').css({'background' : '#ffffff'});
				$grid_productos.find('#pres').css({'background' : '#ffffff'});
				
				$('#forma-pocpedidos-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-pocpedidos-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-pocpedidos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						//alert(tmp.split(':')[0]);
						
						var campo = tmp.split(':')[0];
						var $campo_input;
						var cantidad_existencia=0;
						var  width_td=0;
						
						if((tmp.split(':')[0].substring(0, 8) == 'cantidad') || (tmp.split(':')[0].substring(0, 5) == 'costo') || (tmp.split(':')[0].substring(0, 12) == 'presentacion')){
							
							$('#forma-pocpedidos-window').find('#div_warning_grid').css({'display':'block'});
							
							if(tmp.split(':')[0].substring(0, 12) == 'presentacion'){
								$campo_input = $grid_productos.find('input[name='+campo+']');
							}else{
								$campo_input = $grid_productos.find('.'+campo);
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
							
							$('#forma-pocpedidos-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
						}
						
						if(campo == 'backorder'){
							$campo_input = $grid_productos.find('.'+tmp.split(':')[1]);
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
		
		//$.getJSON(json_string,function(entry){
		$.post(input_json,$arreglo,function(entry){
			$incluye_produccion.val(entry['Extras'][0]['mod_produccion']);
			
			if(entry['Extras'][0]['mod_produccion']=='true'){
				$('#forma-pocpedidos-window').css({"margin-left": -400, 	"margin-top": -235});
				$('#forma-pocpedidos-window').find('.pocpedidos_div_one').css({'width':'1030px'});
				$('#forma-pocpedidos-window').find('.pocpedidos_div_two').css({'width':'1030px'});
				$('#forma-pocpedidos-window').find('#titulo_plugin').css({'width':'990px'});
				$('#forma-pocpedidos-window').find('.header_grid').css({'width':'1005px'});
				$('#forma-pocpedidos-window').find('.contenedor_grid').css({'width':'995px'});
				$('#forma-pocpedidos-window').find('#div_botones').css({'width':'1003px'});
				$('#forma-pocpedidos-window').find('#div_botones').find('.tabla_botones').find('.td_left').css({'width':'903px'});
				$('#forma-pocpedidos-window').find('#div_warning_grid').css({'width':'710px'});
				$('#forma-pocpedidos-window').find('#div_warning_grid').find('.td_head').css({'width':'370px'});
				$('#forma-pocpedidos-window').find('#div_warning_grid').find('.div_cont_grid_warning').css({'width':'700px'});
				$('#forma-pocpedidos-window').find('#div_warning_grid').find('.div_cont_grid_warning').find('#grid_warning').css({'width':'680px'});
				
			}else{
				//ocultar td porque la empresa no incluye Produccion
				$('#forma-pocpedidos-window').find('.tabla_header_grid').find('#td_oculto').hide();
			}
			
			//$campo_tc.val(entry['tc']['tipo_cambio']);
			$id_impuesto.val(entry['iva']['0']['id_impuesto']);
			$valor_impuesto.val(entry['iva']['0']['valor_impuesto']);
			$tipo_cambio.val(entry['Tc']['0']['tipo_cambio']);
			
			//carga select denominacion con todas las monedas
			$select_moneda.children().remove();
			var moneda_hmtl = '';
			$.each(entry['Monedas'],function(entryIndex,moneda){
				moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
			});
			$select_moneda.append(moneda_hmtl);
			
			//carga select de vendedores
			$select_vendedor.children().remove();
			var hmtl_vendedor;
			$.each(entry['Vendedores'],function(entryIndex,vendedor){
				hmtl_vendedor += '<option value="' + vendedor['id'] + '"  >' + vendedor['nombre_agente'] + '</option>';
			});
			$select_vendedor.append(hmtl_vendedor);
			
			
			//carga select de terminos
			$select_condiciones.children().remove();
			var hmtl_condiciones;
			$.each(entry['Condiciones'],function(entryIndex,condicion){
				hmtl_condiciones += '<option value="' + condicion['id'] + '"  >' + condicion['descripcion'] + '</option>';
			});
			$select_condiciones.append(hmtl_condiciones);
			
			
			//carga select de metodos de pago
			$select_metodo_pago.children().remove();
			var hmtl_metodo;
			$.each(entry['MetodosPago'],function(entryIndex,metodo){
				hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
			});
			$select_metodo_pago.append(hmtl_metodo);
			
			//carga select de almacenes
			$select_almacen.children().remove();
			var hmtl_alm;
			$.each(entry['Almacenes'],function(entryIndex,alm){
				hmtl_alm += '<option value="' + alm['id'] + '"  >' + alm['titulo'] + '</option>';
			});
			$select_almacen.append(hmtl_alm);
			
			
			
			if(entry['Extras'][0]['transportista']=='true'){
				$check_flete.attr('checked',  (entry['Extras'][0]['transportista']=='true')? true:false );
				$pestana_transportista.parent().show();
				$nombre_documentador.val(entry['Extras'][0]['nombre_empleado'].trim());
				
				$select_tviaje.change(function(){
					var valor = $(this).val();
					if(parseInt(valor)==1){
						$remolque1.css({'background' : '#ffffff'});
						$remolque2.css({'background' : '#F0F0F0'});
						$remolque2.val('');
						$remolque2.attr('readonly',true);
					}else{
						$remolque1.css({'background' : '#ffffff'});
						$remolque2.css({'background' : '#ffffff'});
						$remolque2.attr('readonly',false);
					}
				});
				
				
				//Alimentar select pais origen
				$select_pais_origen.children().remove();
				var pais_hmtl = '<option value="0" selected="yes">[-Seleccionar Pais-]</option>';
				$.each(entry['Paises'],function(entryIndex,pais){
					pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
				});
				$select_pais_origen.append(pais_hmtl);
				
				//Alimentar select estado origen
				var entidad_hmtl = '<option value="0" selected="yes" >[-Seleccionar Estado--]</option>';
				$select_estado_origen.children().remove();
				$select_estado_origen.append(entidad_hmtl);
				
				//Alimentar select municipio origen
				var localidad_hmtl = '<option value="0" selected="yes" >[-Seleccionar Municipio-]</option>';
				$select_municipio_origen.children().remove();
				$select_municipio_origen.append(localidad_hmtl);
		
				//Alimentar select pais destino
				$select_pais_dest.children().remove();
				$select_pais_dest.append(pais_hmtl);
				
				//Alimentar select estado destino
				$select_estado_dest.children().remove();
				$select_estado_dest.append(entidad_hmtl);
				
				//Alimentar select municipio destino
				$select_municipio_dest.children().remove();
				$select_municipio_dest.append(localidad_hmtl);
				
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
					$busca_unidades($id_vehiculo, $no_economico, $marca_vehiculo);
				});
				
				//Buscador de Operadores
				$busca_operador.click(function(event){
					event.preventDefault();
					//$busca_unidades($id_vehiculo, $no_economico, $marca_vehiculo);
				});
				
				//Buscador de Agentes Aduanales
				$busca_agena.click(function(event){
					event.preventDefault();
					//$busca_unidades($id_vehiculo, $no_economico, $marca_vehiculo);
				});
				
				//Buscador de Remitentes
				$busca_remitente.click(function(event){
					event.preventDefault();
					//$busca_unidades($id_vehiculo, $no_economico, $marca_vehiculo);
				});
				
				//Buscador de Destinatarios
				$busca_dest.click(function(event){
					event.preventDefault();
					//$busca_unidades($id_vehiculo, $no_economico, $marca_vehiculo);
				});
				
			}
			
			
			
			
			
			
			
			
			//buscador de clientes
			$busca_cliente.click(function(event){
				event.preventDefault();
				$busca_clientes($select_moneda,$select_condiciones,$select_vendedor, $select_metodo_pago, entry['Monedas'], entry['Condiciones'],entry['Vendedores'], entry['MetodosPago'], $no_cuenta, $etiqueta_digit, $razon_cliente.val(), $nocliente.val());
			});
			
			
			$nocliente.keypress(function(e){
				if(e.which == 13){
					
					var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoClient.json';
					$arreglo2 = {'no_control':$nocliente.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
					
					$.post(input_json2,$arreglo2,function(entry2){
						
						if(parseInt(entry2['Cliente'].length) > 0 ){
							var id_cliente = entry2['Cliente'][0]['id'];
							var no_control = entry2['Cliente'][0]['numero_control'];
							var razon_social = entry2['Cliente'][0]['razon_social'];
							var dir_cliente = entry2['Cliente'][0]['direccion'];
							var empresa_immex = entry2['Cliente'][0]['empresa_immex'];
							var tasa_ret_immex = entry2['Cliente'][0]['tasa_ret_immex'];
							var cuenta_mn = entry2['Cliente'][0]['cta_pago_mn'];
							var cuenta_usd = entry2['Cliente'][0]['cta_pago_usd'];
							
							var id_moneda = entry2['Cliente'][0]['moneda_id'];
							var id_termino = entry2['Cliente'][0]['terminos_id'];
							var id_vendedor = entry2['Cliente'][0]['cxc_agen_id'];
							//almacena el valor de la lista
							var num_lista_precio = entry2['Cliente'][0]['lista_precio'];
							var id_metodo_de_pago = entry2['Cliente'][0]['metodo_pago_id'];
							var tiene_dir_fiscal = entry2['Cliente'][0]['tiene_dir_fiscal'];
							var cred_susp = entry2['Cliente'][0]['credito_suspendido'];
							
							$agregarDatosClienteSeleccionado($select_moneda,$select_condiciones,$select_vendedor, $select_metodo_pago, entry['Monedas'], entry['Condiciones'],entry['Vendedores'], entry['MetodosPago'], $no_cuenta, $etiqueta_digit, id_cliente, no_control, razon_social, dir_cliente, empresa_immex, tasa_ret_immex, cuenta_mn, cuenta_usd, id_moneda, id_termino, id_vendedor, num_lista_precio, id_metodo_de_pago, tiene_dir_fiscal, cred_susp);
							
						}else{
							$('#forma-pocpedidos-window').find('input[name=id_cliente]').val('');
							$('#forma-pocpedidos-window').find('input[name=nocliente]').val('');
							$('#forma-pocpedidos-window').find('input[name=razoncliente]').val('');
							$('#forma-pocpedidos-window').find('input[name=dircliente]').val('');
							$('#forma-pocpedidos-window').find('input[name=empresa_immex]').val('');
							$('#forma-pocpedidos-window').find('input[name=tasa_ret_immex]').val('');
							$('#forma-pocpedidos-window').find('input[name=cta_mn]').val('');
							$('#forma-pocpedidos-window').find('input[name=cta_usd]').val('');
							$('#forma-pocpedidos-window').find('input[name=num_lista_precio]').val(0);
							//por default asignamos cero para el campo id de Direccion Fiscal, esto significa que la direccion se tomara de la tabla de clientes
							$('#forma-pocpedidos-window').find('input[name=id_df]').val(0);
							
							jAlert('N&uacute;mero de cliente desconocido.', 'Atencion!', function(r) { 
								$('#forma-pocpedidos-window').find('input[name=nocliente]').focus(); 
							});
						}
					},"json");//termina llamada json
					
					return false;
				}
			});
			
			
			//agregar producto al grid
			$agregar_producto.click(function(event){
				event.preventDefault();
				$buscador_presentaciones_producto($id_cliente,$nocliente.val(), $sku_producto.val(),$nombre_producto,$grid_productos,$select_moneda,$tipo_cambio, entry['Monedas']);
			});
		
		},"json");//termina llamada json
		
		
		//asignar evento keypress al campo Razon Social del cliente
		$(this).aplicarEventoKeypressEjecutaTrigger($razon_cliente, $busca_cliente);
		
		//asignar evento keypress al campo Numero de Control del cliente
		//$(this).aplicarEventoKeypressEjecutaTrigger($nocliente, $busca_cliente);
		
		
		//$fecha_compromiso.val(mostrarFecha());
		$fecha_compromiso.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		$fecha_compromiso.DatePicker({
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
		
		
		
		//buscador de productos
		$busca_sku.click(function(event){
			event.preventDefault();
			$busca_productos($sku_producto.val(), $nombre_producto.val());
		});
		
		
		//desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
		$(this).aplicarEventoKeypressEjecutaTrigger($sku_producto, $agregar_producto);
		
		//desencadena clic del href Buscar Producto al pulsar enter en el campo Nombre del producto
		$(this).aplicarEventoKeypressEjecutaTrigger($nombre_producto, $busca_sku);
		
		
		
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_productos).size();
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
			$('#forma-pocpedidos-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-pocpedidos-overlay').fadeOut(remove);
		});
		
	});
	
	
	
	var carga_formapocpedidos00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una prefactura
		if(accion_mode == 'cancel'){
			
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id_pedido':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
			jConfirm('Realmente desea eliminar  la factura?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La factura fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La factura no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
			
		}else{
			//aqui  entra para editar un registro
			$('#forma-pocpedidos-window').remove();
			$('#forma-pocpedidos-overlay').remove();
            
			var form_to_show = 'formapocpedidos00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_pocpedidos();
			
			$('#forma-pocpedidos-window').css({"margin-left": -340, 	"margin-top": -235});
			
			$forma_selected.prependTo('#forma-pocpedidos-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $total_tr = $('#forma-pocpedidos-window').find('input[name=total_tr]');
			var $id_pedido = $('#forma-pocpedidos-window').find('input[name=id_pedido]');
			var $accion_proceso = $('#forma-pocpedidos-window').find('input[name=accion_proceso]');
			var $folio = $('#forma-pocpedidos-window').find('input[name=folio]');
			var $incluye_produccion = $('#forma-pocpedidos-window').find('input[name=incluye_pro]');
			
			var $busca_cliente = $('#forma-pocpedidos-window').find('a[href*=busca_cliente]');
			var $id_cliente = $('#forma-pocpedidos-window').find('input[name=id_cliente]');
			var $nocliente = $('#forma-pocpedidos-window').find('input[name=nocliente]');
			var $razon_cliente = $('#forma-pocpedidos-window').find('input[name=razoncliente]');
			var $id_df = $('#forma-pocpedidos-window').find('input[name=id_df]');
			var $dir_cliente = $('#forma-pocpedidos-window').find('input[name=dircliente]');
			var $empresa_immex = $('#forma-pocpedidos-window').find('input[name=empresa_immex]');
			var $tasa_ret_immex = $('#forma-pocpedidos-window').find('input[name=tasa_ret_immex]');
			var $cliente_listaprecio=  $('#forma-pocpedidos-window').find('input[name=num_lista_precio]');
			
			var $select_moneda = $('#forma-pocpedidos-window').find('select[name=select_moneda]');
			var $select_moneda_original = $('#forma-pocpedidos-window').find('input[name=select_moneda_original]');
			var $tipo_cambio = $('#forma-pocpedidos-window').find('input[name=tipo_cambio]');
			var $tipo_cambio_original = $('#forma-pocpedidos-window').find('input[name=tipo_cambio_original]');
			var $orden_compra = $('#forma-pocpedidos-window').find('input[name=orden_compra]');
			var	$orden_compra_original = $('#forma-pocpedidos-window').find('input[name=orden_compra_original]');
			
			var $id_impuesto = $('#forma-pocpedidos-window').find('input[name=id_impuesto]');
			var $valor_impuesto = $('#forma-pocpedidos-window').find('input[name=valorimpuesto]');
			
			var $check_enviar_obser = $('#forma-pocpedidos-window').find('input[name=check_enviar_obser]');
			var $observaciones = $('#forma-pocpedidos-window').find('textarea[name=observaciones]');
			var $observaciones_original = $('#forma-pocpedidos-window').find('textarea[name=observaciones_original]');
			
			var $select_condiciones = $('#forma-pocpedidos-window').find('select[name=select_condiciones]');
			var $select_condiciones_original = $('#forma-pocpedidos-window').find('select[name=select_condiciones_original]');
			
			var $select_vendedor = $('#forma-pocpedidos-window').find('select[name=vendedor]');
			var $select_vendedor_original = $('#forma-pocpedidos-window').find('select[name=vendedor_original]');
			var $select_almacen = $('#forma-pocpedidos-window').find('select[name=select_almacen]');
			
			var $select_metodo_pago = $('#forma-pocpedidos-window').find('select[name=select_metodo_pago]');
			var $no_cuenta = $('#forma-pocpedidos-window').find('input[name=no_cuenta]');
			var $etiqueta_digit = $('#forma-pocpedidos-window').find('input[name=etiqueta_digit]');
			var $cta_mn = $('#forma-pocpedidos-window').find('input[name=cta_mn]');
			var $cta_usd = $('#forma-pocpedidos-window').find('input[name=cta_usd]');
			var $check_ruta = $('#forma-pocpedidos-window').find('input[name=check_ruta]');
			
			var $transporte = $('#forma-pocpedidos-window').find('input[name=transporte]');
			var $transporte_original = $('#forma-pocpedidos-window').find('input[name=transporte_original]');
			
			var $lugar_entrega = $('#forma-pocpedidos-window').find('input[name=lugar_entrega]');
			var $lugar_entrega_original = $('#forma-pocpedidos-window').find('input[name=lugar_entrega_original]');
			
			var $fecha_compromiso = $('#forma-pocpedidos-window').find('input[name=fecha_compromiso]');
			var $fecha_compromiso_original = $('#forma-pocpedidos-window').find('input[name=fecha_compromiso_original]');
			
			//var $select_almacen = $('#forma-pocpedidos-window').find('select[name=almacen]');
			var $sku_producto = $('#forma-pocpedidos-window').find('input[name=sku_producto]');
			var $nombre_producto = $('#forma-pocpedidos-window').find('input[name=nombre_producto]');
			
			//buscar producto
			var $busca_sku = $('#forma-pocpedidos-window').find('a[href*=busca_sku]');
			//href para agregar producto al grid
			var $agregar_producto = $('#forma-pocpedidos-window').find('a[href*=agregar_producto]');
			
			
			var $descargarpdf = $('#forma-pocpedidos-window').find('#descargarpdf');
			var $cancelar_pedido = $('#forma-pocpedidos-window').find('#cancelar_pedido');
			var $cancelado = $('#forma-pocpedidos-window').find('input[name=cancelado]');
			
			//grid de productos
			var $grid_productos = $('#forma-pocpedidos-window').find('#grid_productos');
			//grid de errores
			var $grid_warning = $('#forma-pocpedidos-window').find('#div_warning_grid').find('#grid_warning');
			
			//var $flete = $('#forma-pocpedidos-window').find('input[name=flete]');
			var $subtotal = $('#forma-pocpedidos-window').find('input[name=subtotal]');
			var $impuesto = $('#forma-pocpedidos-window').find('input[name=impuesto]');
			var $campo_impuesto_retenido = $('#forma-pocpedidos-window').find('input[name=impuesto_retenido]');
			var $total = $('#forma-pocpedidos-window').find('input[name=total]');
			
			var $cerrar_plugin = $('#forma-pocpedidos-window').find('#close');
			var $cancelar_plugin = $('#forma-pocpedidos-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-pocpedidos-window').find('#submit');
			
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
			$('#forma-pocpedidos-window').find('input').keypress(function(e){
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
						$('#forma-pocpedidos-window').find('div.interrogacion').css({'display':'none'});
						
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
						$('#forma-pocpedidos-overlay').fadeOut(remove);
						
						//ocultar boton actualizar porque ya se actualizo, ya no se puede guardar cambios, hay que cerrar y volver a abrir
						$submit_actualizar.hide();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						//$('#forma-pocpedidos-window').find('.div_one').css({'height':'545px'});//sin errores
						$('#forma-pocpedidos-window').find('.pocpedidos_div_one').css({'height':'578px'});//con errores
						$('#forma-pocpedidos-window').find('div.interrogacion').css({'display':'none'});
						
						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});
						$grid_productos.find('#pres').css({'background' : '#ffffff'});
						
						$('#forma-pocpedidos-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-pocpedidos-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							
							if( longitud.length > 1 ){
								$('#forma-pocpedidos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								//alert(tmp.split(':')[0]);
								
								var campo = tmp.split(':')[0];
								var $campo_input;
								var cantidad_existencia=0;
								var  width_td=0;
								
								if((tmp.split(':')[0].substring(0, 8) == 'cantidad') || (tmp.split(':')[0].substring(0, 5) == 'costo') || (tmp.split(':')[0].substring(0, 12) == 'presentacion')){
									
									$('#forma-pocpedidos-window').find('#div_warning_grid').css({'display':'block'});
									
									if(tmp.split(':')[0].substring(0, 12) == 'presentacion'){
										$campo_input = $grid_productos.find('input[name='+campo+']');
									}else{
										$campo_input = $grid_productos.find('.'+campo);
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
									
									$('#forma-pocpedidos-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
								}
								
								if(campo == 'backorder'){
									$campo_input = $grid_productos.find('.'+tmp.split(':')[1]);
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
					$incluye_produccion.val(entry['Extras']['0']['mod_produccion']);
					
					if(entry['Extras']['0']['mod_produccion']=='true'){
						$('#forma-pocpedidos-window').css({"margin-left": -400, 	"margin-top": -235});
						$('#forma-pocpedidos-window').find('.pocpedidos_div_one').css({'width':'1030px'});
						$('#forma-pocpedidos-window').find('.pocpedidos_div_two').css({'width':'1030px'});
						$('#forma-pocpedidos-window').find('#titulo_plugin').css({'width':'990px'});
						$('#forma-pocpedidos-window').find('.header_grid').css({'width':'1005px'});
						$('#forma-pocpedidos-window').find('.contenedor_grid').css({'width':'995px'});
						$('#forma-pocpedidos-window').find('#div_botones').css({'width':'1003px'});
						$('#forma-pocpedidos-window').find('#div_botones').find('.tabla_botones').find('.td_left').css({'width':'903px'});
						$('#forma-pocpedidos-window').find('#div_warning_grid').css({'width':'710px'});
						$('#forma-pocpedidos-window').find('#div_warning_grid').find('.td_head').css({'width':'370px'});
						$('#forma-pocpedidos-window').find('#div_warning_grid').find('.div_cont_grid_warning').css({'width':'700px'});
						$('#forma-pocpedidos-window').find('#div_warning_grid').find('.div_cont_grid_warning').find('#grid_warning').css({'width':'680px'});
					}else{
						//ocultar td porque la empresa no incluye Produccion
						$('#forma-pocpedidos-window').find('.tabla_header_grid').find('#td_oculto').hide();
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
							var tr = $("tr", $grid_productos).size();
							tr++;
							
							var trr = '';
							trr = '<tr>';
							trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
									trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
									trr += '<input type="hidden" name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
									trr += '<input type="hidden" name="iddetalle" id="idd" value="'+ prod['id_detalle'] +'">';//este es el id del registro que ocupa el producto en la tabla pocpedidos_detalles
									trr += '<input type="hidden" name="noTr" value="'+ tr +'">';
									//trr += '<span id="elimina">1</span>';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="114">';
									trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ prod['inv_prod_id'] +'">';
									trr += '<input type="text" name="sku" value="'+ prod['codigo'] +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="202">';
								trr += '<input type="text" 	name="nombre" 	value="'+ prod['titulo'] +'" 	id="nom" class="borde_oculto" readOnly="true" style="width:198px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<input type="text" 	name="unidad'+ tr +'" 	value="'+ prod['unidad'] +'" 	id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
									trr += '<input type="hidden" 	name="id_presentacion"  value="'+  prod['id_presentacion'] +'" 	id="idpres">';
									trr += '<input type="text" 		name="presentacion'+ tr +'" 	value="'+  prod['presentacion'] +'" 	id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<input type="text" 	name="cantidad" value="'+  prod['cantidad'] +'" class="cantidad'+ tr +'" id="cant" style="width:76px;">';
							trr += '</td>';
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<input type="text" 		name="costo" 	value="'+  prod['precio_unitario'] +'" 	class="costo'+ tr +'" id="cost" style="width:86px; text-align:right;">';
								trr += '<input type="hidden" value="'+  prod['precio_unitario'] +'" id="costor">';
							trr += '</td>';
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<input type="text" 		name="importe'+ tr +'" 	value="'+  prod['importe'] +'" 	id="import" class="borde_oculto" readOnly="true" style="width:86px; text-align:right;">';
								trr += '<input type="hidden"    name="id_imp_prod"  value="'+  prod['gral_imp_id'] +'" 		id="idimppord">';
								trr += '<input type="hidden"    name="valor_imp" 	value="'+  prod['valor_imp'] +'" 	id="ivalorimp">';
								trr += '<input type="hidden" 	name="totimpuesto'+ tr +'" id="totimp" value="'+parseFloat(prod['importe']) * parseFloat( prod['valor_imp'] )+'">';
							trr += '</td>';
							
							var cant_prod = prod['cant_produccion'];
							
							trr += '<td class="grid2" id="td_oculto'+ tr +'" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
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
							$grid_productos.append(trr);
                            
                            if(entry['Extras']['0']['mod_produccion']=='true'){
								//aplicar evento click al check, cuando la empresa incluya modulo de produccion
								$aplicar_evento_click_a_input_check($grid_productos.find('.checkProd'+ tr));
								
								if(parseFloat(cant_prod) <=0 ){
									//ocualtar check, solo se debe mostrar cuando el producto no tenga existencia suficiente
									$grid_productos.find('.checkProd'+tr).hide();
								}
								
                            }else{
								//ocualtar campos,  cuando la empresa no incluya modulo de produccion
								$grid_productos.find('#td_oculto'+tr).hide();
							}
                            
                            
                            
							//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
							$grid_productos.find('#cant').focus(function(e){
								if($(this).val() == ' '){
									$(this).val('');
								}
							});
							
							//recalcula importe al perder enfoque el campo cantidad
							$grid_productos.find('#cant').blur(function(){
								if ($(this).val().trim() == ''){
									$(this).val(' ');
								}else{
									$(this).val(parseFloat($(this).val()).toFixed(parseInt(prod['no_dec'])));
								}
								
								if( ($(this).val() != ' ') && ($(this).parent().parent().find('#cost').val() != ' ') )
								{   //calcula el importe
									$(this).parent().parent().find('#import').val(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cost').val()));
									//redondea el importe en dos decimales
									//$(this).parent().parent().find('#import').val( Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100 );
									$(this).parent().parent().find('#import').val( parseFloat($(this).parent().parent().find('#import').val()).toFixed(4) );

									//calcula el impuesto para este producto multiplicando el importe por el valor del iva
									$(this).parent().parent().find('#totimp').val(parseFloat($(this).parent().parent().find('#import').val()) * parseFloat(  $(this).parent().parent().find('#ivalorimp').val()  ));
									
								}else{
									$(this).parent().parent().find('#import').val('');
									$(this).parent().parent().find('#totimp').val('');
								}
								$calcula_totales();//llamada a la funcion que calcula totales
							});
							
							//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
							$grid_productos.find('#cost').focus(function(e){
								if($(this).val() == ' '){
									$(this).val('');
								}
							});
							
							//recalcula importe al perder enfoque el campo costo
							$grid_productos.find('#cost').blur(function(){
								if ($(this).val().trim() == ''){
									$(this).val(' ');
								}else{
									$(this).val(parseFloat($(this).val()).toFixed(4));
								}
								
								if( ($(this).val().trim() != '') && ($(this).parent().parent().find('#cant').val().trim() != '') )
								{	//calcula el importe
									$(this).parent().parent().find('#import').val(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cant').val()));
									//redondea el importe en dos decimales
									//$(this).parent().parent().find('#import').val(Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100);
									$(this).parent().parent().find('#import').val( parseFloat($(this).parent().parent().find('#import').val()).toFixed(4));
									
									//calcula el impuesto para este producto multiplicando el importe por el valor del iva
									$(this).parent().parent().find('#totimp').val(parseFloat($(this).parent().parent().find('#import').val()) * parseFloat( $(this).parent().parent().find('#ivalorimp').val()  ));
								}else{
									$(this).parent().parent().find('#import').val('');
									$(this).parent().parent().find('#totimp').val('');
								}
								$calcula_totales();//llamada a la funcion que calcula totales
							});
							
							//validar campo costo, solo acepte numeros y punto
							$permitir_solo_numeros( $grid_productos.find('#cost') );
							$permitir_solo_numeros( $grid_productos.find('#cant') );
							
							//elimina un producto del grid
							$grid_productos.find('#delete'+ tr).bind('click',function(event){
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
						
						$grid_productos.find('a[href*=elimina_producto]').hide();
						$grid_productos.find('input').attr('disabled','-1'); //deshabilitar todos los campos input del grid
						$subtotal.attr('disabled','-1'); //deshabilitar
						$impuesto.attr('disabled','-1'); //deshabilitar
						$campo_impuesto_retenido.attr('disabled','-1'); //deshabilitar
						$total.attr('disabled','-1'); //deshabilitar
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
						$grid_productos.find('a[href*=elimina_producto]').hide();
						$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
						$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid
						$grid_productos.find('input[name=checkProd]').attr('disabled','-1'); //deshabilitar
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
						$buscador_presentaciones_producto($id_cliente, $nocliente.val(), $sku_producto.val(),$nombre_producto,$grid_productos,$select_moneda,$tipo_cambio, entry['Monedas']);
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
					var trCount = $("tr", $grid_productos).size();
					$total_tr.val(trCount);
					if(parseInt(trCount) > 0){
						$grid_productos.find('tr').each(function (index){
							$(this).find('#cost').val(quitar_comas( $(this).find('#cost').val() ));
						});
						return true;
					}else{
						jAlert('No hay datos para actualizar', 'Atencion!', function(r) { 
							$('#forma-pocpedidos-window').find('input[name=sku_producto]').focus();
						});
						return false;
					}
				});
                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-pocpedidos-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-pocpedidos-overlay').fadeOut(remove);
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
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formapocpedidos00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



