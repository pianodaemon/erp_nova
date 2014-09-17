$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
		work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
	//Almacena las cuentas de mayor
	var CtaMay;
	var ArraySuc;
	var ArrayTPol;
	var ArrayCon;
	var array_meses = {1:"Enero", 2:"Febrero", 3:"Marzo", 4:"Abirl", 5:"Mayo", 6:"Junio", 7:"Julio", 8:"Agosto", 9:"Septiembre", 10:"Octubre", 11:"Noviembre", 12:"Diciembre"};
	var array_status = {0:"[--- ---]", 1:"No afectana", 2:"Afectada", 3:"Cancelada"};
	var parametros;
	
	
	//------------------------------------------------------------------
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
	//------------------------------------------------------------------

	
	//Carga los campos select con los datos que recibe como parametro
	$carga_select_con_arreglo_fijo = function($campo_select, arreglo_elementos, elemento_seleccionado, mostrar_opciones){
		$campo_select.children().remove();
		var select_html = '';
		for(var i in arreglo_elementos){
			if( parseInt(i) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + i + '" selected="yes">' + arreglo_elementos[i] + '</option>';
			}else{
				if (mostrar_opciones=='true'){
					select_html += '<option value="' + i + '"  >' + arreglo_elementos[i] + '</option>';
				}
			}
		}
		$campo_select.append(select_html);
	}
	
	
	
	//Carga los campos select con los datos que recibe como parametro
	$carga_campos_select = function($campo_select, $arreglo_elementos, elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem){
		var select_html = '';
		
		if(texto_elemento_cero != ''){
			select_html = '<option value="0">'+texto_elemento_cero+'</option>';
		}
		
		$.each($arreglo_elementos,function(entryIndex,elemento){
			if( parseInt(elemento[index_elem]) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + elemento[index_elem] + '" selected="yes">' + elemento[index_text_elem] + '</option>';
			}else{
				select_html += '<option value="' + elemento[index_elem] + '" >' + elemento[index_text_elem] + '</option>';
			}
		});
		$campo_select.children().remove();
		$campo_select.append(select_html);
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
	
	
	//Valida que la cantidad ingresada no tenga mas de un punto decimal
	$validar_numero_puntos = function($campo, campo_nombre){
		//Buscar cuantos puntos tiene  Precio Unitario
		var coincidencias = $campo.val().match(/\./g);
		var numPuntos = coincidencias ? coincidencias.length : 0;
		if(parseInt(numPuntos)>1){
			jAlert('El valor ingresado para el campo '+campo_nombre+' es incorrecto, tiene mas de un punto('+$campo.val()+').', 'Atencion!', function(r) { 
				$campo.focus();
			});
		}
	}
	
	
	$aplica_evento_focus_input_numerico = function($campo){
		//Al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
		$campo.focus(function(e){
			$(this).val($(this).val().trim());
			
			if($(this).val() != ''){
				if(parseFloat($(this).val())<=0){
					$(this).val('');
				}
			}
		});
	}
	
	
	
	
	
	
	
	
	
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/ctbpolizacontable";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('P&oacute;lizas Contables');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
	var $cadena_busqueda = "";
	var $busqueda_select_sucursal = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_sucursal]');
	var $busqueda_select_tipo_poliza = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_tipo_poliza]');
	var $busqueda_select_estatus = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_estatus]');
	var $busqueda_poliza = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_poliza]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	var $busqueda_select_concepto = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_concepto]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "sucursal" + signo_separador + $busqueda_select_sucursal.val() + "|";
		valor_retorno += "tipo_pol" + signo_separador + $busqueda_select_tipo_poliza.val() + "|";
		valor_retorno += "status" + signo_separador + $busqueda_select_estatus.val() + "|";
		valor_retorno += "poliza" + signo_separador + $busqueda_poliza.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val()+ "|";
		valor_retorno += "concepto" + signo_separador + $busqueda_select_concepto.val() + "|";
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
	
	
	

	
	$iniciar_campos_busqueda = function(){
		
		$busqueda_poliza.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		
		//Cargar select con estatus
		var elemento_seleccionado = 0;
		var mostrar_opciones = 'true';
		$carga_select_con_arreglo_fijo($busqueda_select_estatus, array_status, elemento_seleccionado, mostrar_opciones);

		
		var input_json_cuentas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCuentasMayor.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_cuentas,$arreglo,function(data){
			
			
			
			$busqueda_select_sucursal.children().remove();
			var suc_hmtl = '';
			$.each(data['Suc'],function(entryIndex,suc){
				if(parseInt(suc['id'])==parseInt(data['Data']['suc'])){
					suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">'+ suc['titulo'] + '</option>';
				}else{
					if(data['Data']['versuc']==true){
						suc_hmtl += '<option value="' + suc['id'] + '">'+ suc['titulo'] + '</option>';
					}
				}
			});
			$busqueda_select_sucursal.append(suc_hmtl);
			
			
			//Carga select de Tipos de Poliza
			var elemento_seleccionado = 0;
			var texto_elemento_cero = '[-- --]';
			var index_elem = 'id';
			var index_text_elem = 'titulo';
			$carga_campos_select($busqueda_select_tipo_poliza, data['TPol'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
			
			//Cargar select de Conceptos Contables
			elemento_seleccionado = 0;
			texto_elemento_cero = '[-- --]';
			index_elem = 'id';
			index_text_elem = 'titulo';
			$carga_campos_select($busqueda_select_concepto, data['Con'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
			
			
			
			/*
			if(parseInt(data['Data']['nivel_cta'])==1){
				$busqueda_poliza.attr({'value' : '0000'});
				$busqueda_poliza.mask("9999");
			}
			if(parseInt(data['Data']['nivel_cta'])==2){
				$busqueda_poliza.attr({'value' : '0000-0000'});
				$busqueda_poliza.mask("9999-9999");
			}
			if(parseInt(data['Data']['nivel_cta'])==3){
				$busqueda_poliza.attr({'value' : '0000-0000-0000'});
				$busqueda_poliza.mask("9999-9999-9999");
			}
			if(parseInt(data['Data']['nivel_cta'])==4){
				$busqueda_poliza.attr({'value' : '0000-0000-0000-0000'});
				$busqueda_poliza.mask("9999-9999-9999-9999");
			}
			if(parseInt(data['Data']['nivel_cta'])==5){
				$busqueda_poliza.attr({'value' : '0000-0000-0000-0000-0000'});
				$busqueda_poliza.mask("9999-9999-9999-9999-9999");
			}
			

			$busqueda_poliza.focus(function(e){
				$(this).val($(this).val().trim());
				
				if($(this).val() != ''){
					if(parseFloat($(this).val().replace("-", ""))<=0){
						$(this).val('');
					}
				}
			});
			
			
			$busqueda_poliza.blur(function(){
				if($(this).val().trim()==''){
					if(parseInt(data['Data']['nivel_cta'])==1){
						$busqueda_poliza.val('0000');
					}
					if(parseInt(data['Data']['nivel_cta'])==2){
						$busqueda_poliza.val('0000-0000');
					}
					if(parseInt(data['Data']['nivel_cta'])==3){
						$busqueda_poliza.val('0000-0000-0000');
					}
					if(parseInt(data['Data']['nivel_cta'])==4){
						$busqueda_poliza.val('0000-0000-0000-0000');
					}
					if(parseInt(data['Data']['nivel_cta'])==5){
						$busqueda_poliza.val('0000-0000-0000-0000-0000');
					}
				}
			});
			*/
			
			CtaMay = data['CtaMay'];
			ArraySuc = data['Suc'];
			ArrayTPol = data['TPol'];
			ArrayCon = data['Con'];
			parametros = data['Data'];
			
			$busqueda_select_sucursal.focus();
		});
	}
	
	
	//LLamarda a la funcion que inicializa los campos de busqueda
	$iniciar_campos_busqueda();
	
	
	$limpiar.click(function(event){
		event.preventDefault();
		//LLamarda a la funcion que inicializa los campos de busqueda
		$iniciar_campos_busqueda();
		
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
			 $('#barra_buscador').animate({height: '60px'}, 500);
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
		
		$busqueda_select_sucursal.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_sucursal, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_tipo_poliza, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_poliza, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_inicial, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_final, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_concepto, $buscar);
	
	
	
	
	

	
	
	
	
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
		
		$('#forma-ctbpolizacontable-window').find('#submit').mouseover(function(){
			$('#forma-ctbpolizacontable-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-ctbpolizacontable-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-ctbpolizacontable-window').find('#submit').mouseout(function(){
			$('#forma-ctbpolizacontable-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-ctbpolizacontable-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-ctbpolizacontable-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-ctbpolizacontable-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-ctbpolizacontable-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-ctbpolizacontable-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-ctbpolizacontable-window').find('#close').mouseover(function(){
			$('#forma-ctbpolizacontable-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-ctbpolizacontable-window').find('#close').mouseout(function(){
			$('#forma-ctbpolizacontable-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-ctbpolizacontable-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-ctbpolizacontable-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-ctbpolizacontable-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-ctbpolizacontable-window').find("ul.pestanas li").click(function() {
			$('#forma-ctbpolizacontable-window').find(".contenidoPes").hide();
			$('#forma-ctbpolizacontable-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-ctbpolizacontable-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	

	

	
	
	//buscador de Cuentas Contables
	$busca_cuentas_contables = function(tipo, nivel_cta, arrayCtasMayor, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta){
		//limpiar_campos_grids();
		$(this).modalPanel_buscactacontable();
		var $dialogoc =  $('#forma-buscactacontable-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_cuentas').find('table.formaBusqueda_cuentas').clone());
		
		$('#forma-buscactacontable-window').css({"margin-left": -200, 	"margin-top": -160});
		
		var $tabla_resultados = $('#forma-buscactacontable-window').find('#tabla_resultado');
		
		var $select_cta_mayor = $('#forma-buscactacontable-window').find('select[name=select_cta_mayor]');
		var $campo_clasif = $('#forma-buscactacontable-window').find('input[name=clasif]');
		var $campo_cuenta = $('#forma-buscactacontable-window').find('input[name=cuenta]');
		var $campo_scuenta = $('#forma-buscactacontable-window').find('input[name=scuenta]');
		var $campo_sscuenta = $('#forma-buscactacontable-window').find('input[name=sscuenta]');
		var $campo_ssscuenta = $('#forma-buscactacontable-window').find('input[name=ssscuenta]');
		var $campo_sssscuenta = $('#forma-buscactacontable-window').find('input[name=sssscuenta]');
		var $campo_descripcion = $('#forma-buscactacontable-window').find('input[name=campo_descripcion]');
		
		var $boton_busca = $('#forma-buscactacontable-window').find('#boton_busca');
		var $boton_cencela = $('#forma-buscactacontable-window').find('#boton_cencela');
		var mayor_seleccionado=0;
		var detalle=0;
		var clasifica='';
		
		$campo_cuenta.hide();
		$campo_scuenta.hide();
		$campo_sscuenta.hide();
		$campo_ssscuenta.hide();
		$campo_sssscuenta.hide();
		
		$permitir_solo_numeros($campo_clasif);
		$permitir_solo_numeros($campo_cuenta);
		$permitir_solo_numeros($campo_scuenta);
		$permitir_solo_numeros($campo_sscuenta);
		$permitir_solo_numeros($campo_ssscuenta);
		$permitir_solo_numeros($campo_sssscuenta);
		
		//funcionalidad botones
		$boton_busca.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		
		$boton_busca.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$boton_cencela.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		
		$boton_cencela.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		if(parseInt(nivel_cta) >=1 ){ $campo_cuenta.show(); $campo_cuenta.val($cuenta.val())};
		if(parseInt(nivel_cta) >=2 ){ $campo_scuenta.show(); $campo_scuenta.val($scuenta.val())};
		if(parseInt(nivel_cta) >=3 ){ $campo_sscuenta.show(); $campo_sscuenta.val($sscuenta.val())};
		if(parseInt(nivel_cta) >=4 ){ $campo_ssscuenta.show(); $campo_ssscuenta.val($ssscuenta.val())};
		if(parseInt(nivel_cta) >=5 ){ $campo_sssscuenta.show(); $campo_sssscuenta.val($sssscuenta.val())};
		
		
		//mayor_seleccionado 1=Activo	clasifica=1(Activo Circulante)
		//mayor_seleccionado 5=Egresos	clasifica=1(Costo de Ventas)
		//mayor_seleccionado 4=Activo	clasifica=1(Ventas)
		//if(parseInt(tipo)==1 ){mayor_seleccionado=1; detalle=1; clasifica=1; };
		//if(parseInt(tipo)==2 ){mayor_seleccionado=5; detalle=1; clasifica=1; };
		//if(parseInt(tipo)==3 ){mayor_seleccionado=4; detalle=1; clasifica=1; };
		
		detalle=1;
		
		$campo_clasif.val(clasifica);
		
		//carga select de cuentas de Mayor
		$select_cta_mayor.children().remove();
		var ctamay_hmtl = '';
		$.each(arrayCtasMayor,function(entryIndex,ctamay){
			/*
			if (parseInt(mayor_seleccionado) == parseInt( ctamay['id']) ){
				ctamay_hmtl += '<option value="' + ctamay['id'] + '">'+ ctamay['titulo'] + '</option>';
			}
			*/
			ctamay_hmtl += '<option value="' + ctamay['id'] + '">'+ ctamay['descripcion'] + '</option>';
		});
		$select_cta_mayor.append(ctamay_hmtl);
		
		//click buscar Cuentas Contables
		$boton_busca.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorCuentasContables.json';
			$arreglo = {	'cta_mayor':$select_cta_mayor.val(),
							'detalle':detalle,
							'clasifica':$campo_clasif.val(),
							'cta':$campo_cuenta.val(),
							'scta':$campo_scuenta.val(),
							'sscta':$campo_sscuenta.val(),
							'ssscta':$campo_ssscuenta.val(),
							'sssscta':$campo_sssscuenta.val(),
							'descripcion':$campo_descripcion.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				var notr=0;
				$.each(entry['CtaContables'],function(entryIndex,cuenta){
					//obtiene numero de trs
					notr = $("tr", $tabla_resultados).size();
					notr++;
					
					trr = '<tr class="tr'+notr+'">';
						trr += '<td width="30">'+cuenta['m']+'</td>';
						trr += '<td width="30">'+cuenta['c']+'</td>';
						trr += '<td width="170">';
							trr += '<input type="hidden" name="id_cta" value="'+cuenta['id']+'" >';
							trr += '<input type="text" name="cta" value="'+cuenta['cuenta']+'" class="borde_oculto" style="width:166px; readOnly="true">';
							trr += '<input type="hidden" name="campo_cta" value="'+cuenta['cta']+'" >';
							trr += '<input type="hidden" name="campo_scta" value="'+cuenta['subcta']+'" >';
							trr += '<input type="hidden" name="campo_sscta" value="'+cuenta['ssubcta']+'" >';
							trr += '<input type="hidden" name="campo_ssscta" value="'+cuenta['sssubcta']+'" >';
							trr += '<input type="hidden" name="campo_ssscta" value="'+cuenta['ssssubcta']+'" >';
						trr += '</td>';
						trr += '<td width="230"><input type="text" name="des" value="'+cuenta['descripcion']+'" class="borde_oculto" style="width:226px; readOnly="true"></td>';
						trr += '<td width="70">'+cuenta['detalle']+'</td>';
						trr += '<td width="50">'+cuenta['nivel_cta']+'</td>';
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
					var id_cta = $(this).find('input[name=id_cta]').val();
					var cta = $(this).find('input[name=campo_cta]').val();
					var scta = $(this).find('input[name=campo_scta]').val();
					var sscta = $(this).find('input[name=campo_sscta]').val();
					var ssscta = $(this).find('input[name=campo_ssscta]').val();
					var sssscta = $(this).find('input[name=campo_ssscta]').val();
					var desc = $(this).find('input[name=des]').val();
					
					if(parseInt(tipo)==1 ){
						$('#forma-ctbpolizacontable-window').find('input[name=id_cta]').val(id_cta);
						$('#forma-ctbpolizacontable-window').find('input[name=cuenta]').val(cta);
						$('#forma-ctbpolizacontable-window').find('input[name=scuenta]').val(scta);
						$('#forma-ctbpolizacontable-window').find('input[name=sscuenta]').val(sscta);
						$('#forma-ctbpolizacontable-window').find('input[name=ssscuenta]').val(ssscta);
						$('#forma-ctbpolizacontable-window').find('input[name=sssscuenta]').val(sssscta);
						$('#forma-ctbpolizacontable-window').find('input[name=descripcion_cuenta]').val(desc);
						
						if(parseInt(nivel_cta) ==1 ){ $campo_scuenta.val(''); $campo_sscuenta.val(''); $campo_ssscuenta.val(''); $campo_sssscuenta.val('');};
						if(parseInt(nivel_cta) ==2 ){ $campo_sscuenta.val(''); $campo_ssscuenta.val(''); $campo_sssscuenta.val(''); };
						if(parseInt(nivel_cta) ==3 ){ $campo_ssscuenta.val(''); $campo_sssscuenta.val(''); };
						if(parseInt(nivel_cta) ==4 ){ $campo_sssscuenta.val(''); };
						if(parseInt(nivel_cta) ==5 ){ /*Aqui no hay nada*/ };
					};
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscactacontable-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-ctbpolizacontable-window').find('input[name=cuenta]').focus();
				});
			});//termina llamada json
		});
		
		$campo_clasif.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_cuenta.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_scuenta.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_sscuenta.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_ssscuenta.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_sssscuenta.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_descripcion.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$boton_cencela.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscactacontable-overlay').fadeOut(remove);
		});
	}//termina buscador de Cuentas Contables





	
	//nuevo 
	$new.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_ctbpolizacontable();
		
		var form_to_show = 'formactbpolizacontable00';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		$('#forma-ctbpolizacontable-window').css({ "margin-left": -470, 	"margin-top": -200 });
		$forma_selected.prependTo('#forma-ctbpolizacontable-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $identificador = $('#forma-ctbpolizacontable-window').find('input[name=identificador]');
		var $select_sucursal = $('#forma-ctbpolizacontable-window').find('select[name=select_sucursal]');
		var $select_mes = $('#forma-ctbpolizacontable-window').find('select[name=select_mes]');
		var $select_anio = $('#forma-ctbpolizacontable-window').find('select[name=select_anio]');
		
		var $no_poliza = $('#forma-ctbpolizacontable-window').find('input[name=no_poliza]');
		var $fecha = $('#forma-ctbpolizacontable-window').find('input[name=fecha]');
		var $select_moneda = $('#forma-ctbpolizacontable-window').find('select[name=select_moneda]');
		
		var $select_tipo = $('#forma-ctbpolizacontable-window').find('select[name=select_tipo]');
		var $select_concepto = $('#forma-ctbpolizacontable-window').find('select[name=select_concepto]');
		
		var $id_cta = $('#forma-ctbpolizacontable-window').find('input[name=id_cta]');
		var $cuenta = $('#forma-ctbpolizacontable-window').find('input[name=cuenta]');
		var $scuenta = $('#forma-ctbpolizacontable-window').find('input[name=scuenta]');
		var $sscuenta = $('#forma-ctbpolizacontable-window').find('input[name=sscuenta]');
		var $ssscuenta = $('#forma-ctbpolizacontable-window').find('input[name=ssscuenta]');
		var $sssscuenta = $('#forma-ctbpolizacontable-window').find('input[name=sssscuenta]');
		var $descripcion_cuenta = $('#forma-ctbpolizacontable-window').find('input[name=descripcion_cuenta]');
		
		var $select_centro_costo = $('#forma-ctbpolizacontable-window').find('select[name=select_centro_costo]');
		var $debe = $('#forma-ctbpolizacontable-window').find('input[name=debe]');
		var $haber = $('#forma-ctbpolizacontable-window').find('input[name=haber]');
		
		var $busca_cuenta_contble = $('#forma-ctbpolizacontable-window').find('#busca_cuenta_contble');
		var $agrega_cuenta_contble = $('#forma-ctbpolizacontable-window').find('#agrega_cuenta_contble');
		
		var $cerrar_plugin = $('#forma-ctbpolizacontable-window').find('#close');
		var $cancelar_plugin = $('#forma-ctbpolizacontable-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-ctbpolizacontable-window').find('#submit');
		
		$permitir_solo_numeros($cuenta);
		$permitir_solo_numeros($scuenta);
		$permitir_solo_numeros($sscuenta);
		$permitir_solo_numeros($ssscuenta);
		$permitir_solo_numeros($sssscuenta);
		$permitir_solo_numeros($debe);
		$permitir_solo_numeros($haber);
		
		$cuenta.hide();
		$scuenta.hide();
		$sscuenta.hide();
		$ssscuenta.hide();
		$sssscuenta.hide();
		

		$no_poliza.attr("readonly", true);
		$fecha.attr("readonly", true);
		$no_poliza.css({'background' : '#F0F0F0'});
		$descripcion_cuenta.css({'background' : '#F0F0F0'});
		
		$identificador.attr({ 'value' : 0 });
		
		$debe.val(0);
		$haber.val(0);
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La Cuenta fue dada de alta con exito", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-ctbpolizacontable-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-ctbpolizacontable-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-ctbpolizacontable-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
					}
				}
			}
		}
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPoliza.json';
		$arreglo = {
			'id':id_to_show,
			'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
		};
		
		$.post(input_json,$arreglo,function(entry){
			//Visualizar subcuentas de acuerdo al nivel definido para la empresa
			if(parseInt(entry['Extras'][0]['nivel_cta']) >=1 ){ $cuenta.show(); };
			if(parseInt(entry['Extras'][0]['nivel_cta']) >=2 ){ $scuenta.show(); };
			if(parseInt(entry['Extras'][0]['nivel_cta']) >=3 ){ $sscuenta.show(); };
			if(parseInt(entry['Extras'][0]['nivel_cta']) >=4 ){ $ssscuenta.show(); };
			if(parseInt(entry['Extras'][0]['nivel_cta']) >=5 ){ $sssscuenta.show(); };
			
			var fecha_actual = entry['Extras'][0]['fecha_actual'];
			
			//mostrarFecha();
			
			var valor = fecha_actual.split('-');
			
			//Cargar select con meses
			var elemento_seleccionado = valor[1];
			var mostrar_opciones = 'true';
			$carga_select_con_arreglo_fijo($select_mes, array_meses, elemento_seleccionado, mostrar_opciones);
			
			
			
			//carga select de Años
			$select_anio.children().remove();
			var anio_html = '';
			$.each(entry['Anios'],function(entryIndex,anio){
				if(parseInt(anio['valor'])==parseInt(valor[0])){
					anio_html += '<option value="' + anio['valor'] + '"  >'+ anio['valor'] + '</option>';
				}else{
					//anio_html += '<option value="' + anio['valor'] + '"  >'+ anio['valor'] + '</option>';
				}
			});
			$select_anio.append(anio_html);
			
			
			
			
			//carga select de cuentas de Mayor
			$select_sucursal.children().remove();
			var suc_hmtl = '';
			$.each(ArraySuc,function(entryIndex,suc){
				if(parseInt(suc['id'])==parseInt(parametros['suc'])){
					suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">'+ suc['titulo'] + '</option>';
				}else{
					if(parametros['versuc']==true){
						suc_hmtl += '<option value="' + suc['id'] + '">'+ suc['titulo'] + '</option>';
					}
				}
			});
			$select_sucursal.append(suc_hmtl);
			
			//Carga la moneda por default MN
			$select_moneda.children().remove();
			var moneda_html = '';
			moneda_html += '<option value="1" selected="yes">M.N.</option>';
			$select_moneda.append(moneda_html);
			
			
			//Carga select de Tipos de Poliza
			var elemento_seleccionado = 0;
			var texto_elemento_cero = '';
			var index_elem = 'id';
			var index_text_elem = 'titulo';
			$carga_campos_select($select_tipo, ArrayTPol, elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
			
			//Cargar select de Conceptos Contables
			elemento_seleccionado = 0;
			texto_elemento_cero = '';
			index_elem = 'id';
			index_text_elem = 'titulo';
			$carga_campos_select($select_concepto, ArrayCon, elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
			
			//Cargar select de Centro de Costos
			elemento_seleccionado = 0;
			texto_elemento_cero = '[--Seleccionar--]';
			index_elem = 'id';
			index_text_elem = 'titulo';
			$carga_campos_select($select_centro_costo, entry['CC'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
			
			
			
			//Busca Cuentas Contables
			$busca_cuenta_contble.click(function(event){
				event.preventDefault();
				$busca_cuentas_contables(1, entry['Extras'][0]['nivel_cta'], CtaMay, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta);
			});
			
			
			
			
			
			
			
			//generar tr para agregar al grid
			$agrega_tr = function(noTr, id_det, id_tmov, codigo, descripcion, unidad, cant_traspaso, readOnly, densidad, idPres, presentacion, cantPres, noDec, cantEquiv){
				var trr = '';
				trr = '<tr>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="120">';
						trr += '<input type="hidden" 	name="id_det" value="'+ id_det +'">';
						trr += '<input type="text" 		name="codigo" value="'+ codigo +'" class="borde_oculto" readOnly="true" style="width:116px;">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="200">';
						trr += '<select name="select_tmov" class="select_umedida'+ tr +'" style="width:86px;"></select>';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="120">';
						trr += '<input type="text" 		name="unidad" 	value="'+ unidad +'" class="borde_oculto" readOnly="true" style="width:116px;">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="120">';
						trr += '<input type="text" 		name="unidad" 	value="'+ presentacion +'" class="borde_oculto" readOnly="true" style="width:116px;">';
						trr += '<input type="text" 		name="lote_int" class="lote_int'+ noTr +'" value="" style="width:116px; display:none;">';
					trr += '</td>';
					
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
						trr += '<input type="text" 		name="cantidad" class="cantidad'+noTr+'"  value="'+$(this).agregar_comas(cant_traspaso)+'" '+readOnly+' style="width:86px; text-align:right; border-color:transparent; background:transparent;">';
						trr += '<input type="text" 		name="cant_traspaso" value="'+cant_traspaso+'" class="cant_traspaso'+noTr+'"  style="width:86px; display:none;">';
					trr += '</td>';
					
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
						trr += '<input type="hidden" 	name="idPres" id="idPres" value="'+ idPres +'">';
						trr += '<input type="hidden" 	name="cantEquiv" id="cantEquiv" value="'+ cantEquiv +'">';
						trr += '<input type="text" 		name="cantPres" class="cantPres'+noTr+'"  value="'+$(this).agregar_comas(cantPres)+'" '+readOnly+' style="width:86px; text-align:right; border-color:transparent; background:transparent;">';
					trr += '</td>';
					
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="15">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="15">';
						trr += '<input type="hidden" 	name="eliminado" value="1">';//el 1 significa que el registro no ha sido eliminado
						trr += '<input type="hidden" 	name="no_tr" value="'+ noTr +'">';
					trr += '</td>';
					//agregado por paco
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
							trr += '<input name="densidad_litro" value="'+densidad+'" type="hidden">';
							trr += '<input name="cantidad_kilos" id="cantidad_kilos'+ noTr +'" value="0.00" class="borde_oculto" readonly="true" style="width:56px; text-align:right;" type="text">';
					trr += '</td>';
				trr += '</tr>';
				
				return trr;
			}
					
			
			
			
			
			$agrega_cuenta_contble.click(function(event){
				event.preventDefault();
				
				alert("Agregar");
				
				
				
				
				//$buscador_presentaciones_producto($id_cliente,$nocliente.val(), $sku_producto.val(),$nombre_producto,$grid_productos,$select_moneda,$tipo_cambio, entry['Monedas']);
			});
			
			
			
			$fecha.val(fecha_actual);

			$select_sucursal.focus();
		},"json");//termina llamada json
		
		
		
		

		
		$fecha.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
			
		$fecha.DatePicker({
			format:'Y-m-d',
			date: $fecha.val(),
			current: $fecha.val(),
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
		
		
		
		$aplica_evento_focus_input_numerico($debe);
		$aplica_evento_focus_input_numerico($haber);
		
		$debe.blur(function(){
			$validar_numero_puntos($debe, "Debe");
			if($(this).val().trim()==''){
				$(this).val(0);
			}
		});
		
		$haber.blur(function(){
			$validar_numero_puntos($haber, "Haber");
			if($(this).val().trim()==''){
				$(this).val(0);
			}
		});
		
		
		$cuenta.keypress(function(e){
			if (e.which == 8) {
				$descripcion_cuenta.val('');
				$id_cta.val('0');
			}else{
				if((parseInt($cuenta.val().length)+1)==4){
					$scuenta.focus();
				}
			}
		});
		
		$scuenta.keypress(function(e){
			if (e.which == 8) {
				$descripcion_cuenta.val('');
				$id_cta.val('0');
			}else{
				if((parseInt($scuenta.val().length)+1)==4){
					$sscuenta.focus();
				}
			}
		});
		
		$sscuenta.keypress(function(e){
			if (e.which == 8) {
				$descripcion_cuenta.val('');
				$id_cta.val('0');
			}else{
				if((parseInt($sscuenta.val().length)+1)==4){
					$ssscuenta.focus();
				}
			}
		});
		
		$ssscuenta.keypress(function(e){
			if (e.which == 8) {
				$descripcion_cuenta.val('');
				$id_cta.val('0');
			}else{
				if((parseInt($ssscuenta.val().length)+1)==4){
					$sssscuenta.focus();
				}
			}
		});
		
		$sssscuenta.keypress(function(e){
			if (e.which == 8) {
				$descripcion_cuenta.val('');
				$id_cta.val('0');
			}
		});
		
		
		
		
		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-ctbpolizacontable-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-ctbpolizacontable-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
	});
	
	
        
        
        
	
	var carga_formaCC00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la Cuenta Contable seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La Cuenta Contable fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La Cuenta Contable no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
			
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formactbpolizacontable00';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_ctbpolizacontable();
			$('#forma-ctbpolizacontable-window').css({ "margin-left": -470, 	"margin-top": -200 });
			
			$forma_selected.prependTo('#forma-ctbpolizacontable-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $identificador = $('#forma-ctbpolizacontable-window').find('input[name=identificador]');
			var $select_sucursal = $('#forma-ctbpolizacontable-window').find('select[name=select_sucursal]');
			var $select_mes = $('#forma-ctbpolizacontable-window').find('select[name=select_mes]');
			var $select_anio = $('#forma-ctbpolizacontable-window').find('select[name=select_anio]');
			
			var $no_poliza = $('#forma-ctbpolizacontable-window').find('input[name=no_poliza]');
			var $fecha = $('#forma-ctbpolizacontable-window').find('input[name=fecha]');
			var $select_moneda = $('#forma-ctbpolizacontable-window').find('select[name=select_moneda]');
			
			var $select_tipo = $('#forma-ctbpolizacontable-window').find('select[name=select_tipo]');
			var $select_concepto = $('#forma-ctbpolizacontable-window').find('select[name=select_concepto]');
			
			var $id_cta = $('#forma-ctbpolizacontable-window').find('input[name=id_cta]');
			var $cuenta = $('#forma-ctbpolizacontable-window').find('input[name=cuenta]');
			var $scuenta = $('#forma-ctbpolizacontable-window').find('input[name=scuenta]');
			var $sscuenta = $('#forma-ctbpolizacontable-window').find('input[name=sscuenta]');
			var $ssscuenta = $('#forma-ctbpolizacontable-window').find('input[name=ssscuenta]');
			var $sssscuenta = $('#forma-ctbpolizacontable-window').find('input[name=sssscuenta]');
			var $descripcion_cuenta = $('#forma-ctbpolizacontable-window').find('input[name=descripcion_cuenta]');
			
			var $select_centro_costo = $('#forma-ctbpolizacontable-window').find('select[name=select_centro_costo]');
			var $debe = $('#forma-ctbpolizacontable-window').find('input[name=debe]');
			var $haber = $('#forma-ctbpolizacontable-window').find('input[name=haber]');
			
			var $busca_cuenta_contble = $('#forma-ctbpolizacontable-window').find('#busca_cuenta_contble');
			var $agrega_cuenta_contble = $('#forma-ctbpolizacontable-window').find('#agrega_cuenta_contble');
			
			var $cerrar_plugin = $('#forma-ctbpolizacontable-window').find('#close');
			var $cancelar_plugin = $('#forma-ctbpolizacontable-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-ctbpolizacontable-window').find('#submit');
			
			$permitir_solo_numeros($cuenta);
			$permitir_solo_numeros($scuenta);
			$permitir_solo_numeros($sscuenta);
			$permitir_solo_numeros($ssscuenta);
			$permitir_solo_numeros($sssscuenta);
			$permitir_solo_numeros($debe);
			$permitir_solo_numeros($haber);
			
			$cuenta.hide();
			$scuenta.hide();
			$sscuenta.hide();
			$ssscuenta.hide();
			$sssscuenta.hide();
			
			$no_poliza.attr("readonly", true);
			$fecha.attr("readonly", true);
			$no_poliza.css({'background' : '#F0F0F0'});
			$descripcion_cuenta.css({'background' : '#F0F0F0'});
			
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPoliza.json';
				$arreglo = {	'id':id_to_show,
								'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-ctbpolizacontable-overlay').fadeOut(remove);
						jAlert("Los datos de la Cuenta se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-ctbpolizacontable-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-ctbpolizacontable-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					//Visualizar subcuentas de acuerdo al nivel definido para la empresa
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=1 ){ $cuenta.show(); };
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=2 ){ $scuenta.show(); };
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=3 ){ $sscuenta.show(); };
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=4 ){ $ssscuenta.show(); };
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=5 ){ $sssscuenta.show(); };
					
					var fecha_actual = entry['Extras'][0]['fecha_actual'];
					
					$identificador.attr({ 'value' : entry['Data'][0]['id'] });
					$no_poliza.attr({ 'value' : entry['Data'][0]['no_poliza'] });
					
					$id_cta.attr({ 'value' : entry['Data'][0]['cta_id'] });
					$cuenta.attr({ 'value' : entry['Data'][0]['cta'] });
					$scuenta.attr({ 'value' : entry['Data'][0]['subcta'] });
					$sscuenta.attr({ 'value' : entry['Data'][0]['ssubcta'] });
					$ssscuenta.attr({ 'value' : entry['Data'][0]['sssubcta'] });
					$sssscuenta.attr({ 'value' : entry['Data'][0]['ssssubcta'] });
					$descripcion_cuenta.attr({ 'value' : entry['Data'][0]['descripcion'] });
					
					$debe.attr({ 'value' : entry['Data'][0]['debe'] });
					$haber.attr({ 'value' : entry['Data'][0]['haber'] });
					
					//mostrarFecha();
					
					var valor = entry['Data'][0]['fecha'].split('-');
					
					//Cargar select con meses
					var elemento_seleccionado = entry['Data'][0]['mes'];
					var mostrar_opciones = 'false';
					$carga_select_con_arreglo_fijo($select_mes, array_meses, elemento_seleccionado, mostrar_opciones);
					
					
					
					//Carga select de Años
					$select_anio.children().remove();
					var anio_html = '';
					$.each(entry['Anios'],function(entryIndex,anio){
						if(parseInt(anio['valor'])==parseInt(entry['Data'][0]['anio'])){
							anio_html += '<option value="' + anio['valor'] + '"  >'+ anio['valor'] + '</option>';
						}else{
							//anio_html += '<option value="' + anio['valor'] + '"  >'+ anio['valor'] + '</option>';
						}
					});
					$select_anio.append(anio_html);
					
					
					
					//Carga select de Sucursal
					$select_sucursal.children().remove();
					var suc_hmtl = '';
					$.each(ArraySuc,function(entryIndex,suc){
						if(parseInt(suc['id'])==parseInt(entry['Data'][0]['suc_id'])){
							suc_hmtl += '<option value="' + suc['id'] + '">'+ suc['titulo'] + '</option>';
						}else{
							suc_hmtl += '<option value="' + suc['id'] + '">'+ suc['titulo'] + '</option>';
						}
					});
					$select_sucursal.append(suc_hmtl);
					
					
					//Carga la moneda por default MN
					$select_moneda.children().remove();
					var moneda_html = '';
					$.each(entry['Monedas'],function(entryIndex,mon){
						if(parseInt(mon['id'])==parseInt(entry['Data'][0]['mon_id'])){
							moneda_html += '<option value="' + mon['id'] + '">'+ mon['descripcion_abr'] + '</option>';
						}else{
							moneda_html += '<option value="' + mon['id'] + '">'+ mon['descripcion_abr'] + '</option>';
						}
					});
					$select_moneda.append(moneda_html);
					
					
					//Carga select de Tipos de Poliza
					var elemento_seleccionado = entry['Data'][0]['tpol_id'];
					var texto_elemento_cero = '';
					var index_elem = 'id';
					var index_text_elem = 'titulo';
					$carga_campos_select($select_tipo, ArrayTPol, elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
					
					//Cargar select de Conceptos Contables
					elemento_seleccionado = entry['Data'][0]['con_id'];
					texto_elemento_cero = '';
					index_elem = 'id';
					index_text_elem = 'titulo';
					$carga_campos_select($select_concepto, ArrayCon, elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
					
					//Cargar select de Centro de Costos
					elemento_seleccionado = entry['Data'][0]['cc_id'];
					texto_elemento_cero = '[--Seleccionar--]';
					index_elem = 'id';
					index_text_elem = 'titulo';
					$carga_campos_select($select_centro_costo, entry['CC'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
					
					
					
					//Busca Cuentas Contables
					$busca_cuenta_contble.click(function(event){
						event.preventDefault();
						$busca_cuentas_contables(1, entry['Extras'][0]['nivel_cta'], CtaMay, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta);
					});
					
					
					
					$fecha.val(fecha_actual);

					$select_sucursal.focus();
					
					
				
					$fecha.click(function (s){
						var a=$('div.datepicker');
						a.css({'z-index':100});
					});
						
					$fecha.DatePicker({
						format:'Y-m-d',
						date: fecha_actual,
						current: fecha_actual,
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
						
					
					
					/*
					//visualizar subcuentas de acuerdo al nivel definido para la empresa
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=1 ){ $cuenta.show(); };
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=2 ){ $scuenta.show(); };
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=3 ){ $sscuenta.show(); };
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=4 ){ $ssscuenta.show(); };
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=5 ){ $sssscuenta.show(); };
					
					$identificador.attr({ 'value' : entry['Cc']['0']['id'] });
					$cuenta.attr({ 'value' : entry['Cc']['0']['cta'] });
					$scuenta.attr({ 'value' : entry['Cc']['0']['subcta'] });
					$sscuenta.attr({ 'value' : entry['Cc']['0']['ssubcta'] });
					$ssscuenta.attr({ 'value' : entry['Cc']['0']['sssubcta'] });
					$sssscuenta.attr({ 'value' : entry['Cc']['0']['ssssubcta'] });
					
					$descripcion.attr({ 'value' : entry['Cc']['0']['descripcion'] });
					$descripcion_es.attr({ 'value' : entry['Cc']['0']['descripcion'] });
					$descripcion_in.attr({ 'value' : entry['Cc']['0']['descripcion_ing'] });
					$descripcion_otro.attr({ 'value' : entry['Cc']['0']['descripcion_otr'] });
					
					//carga select de cuentas de Mayor
					$select_cuenta_mayor.children().remove();
					var ctamay_hmtl = '';
					$.each(entry['CtaMay'],function(entryIndex,ctamay){
						if(entry['Cc']['0']['cta_mayor']==ctamay['cta_mayor'] && entry['Cc']['0']['clasifica']==ctamay['clasificacion']){
							ctamay_hmtl += '<option value="' + ctamay['id'] + '" selected="yes">( ' + ctamay['cta_mayor']+', '+ ctamay['clasificacion'] +' ) '+ ctamay['descripcion'] + '</option>';
						}else{
							ctamay_hmtl += '<option value="' + ctamay['id'] + '">( ' + ctamay['cta_mayor']+', '+ ctamay['clasificacion'] +' ) '+ ctamay['descripcion'] + '</option>';
						}
					});
					$select_cuenta_mayor.append(ctamay_hmtl);
					
					$chk_cta_detalle.attr('checked',  (entry['Cc']['0']['detalle'] == '1')? true:false );
					
					var estatus_hmtl = '';
					if(entry['Cc']['0']['estatus']=='1'){
						estatus_hmtl += '<option value="1" selected="yes">Activada</option>';
						estatus_hmtl += '<option value="2">Desactivada</option>';
					}
					if(entry['Cc']['0']['estatus']=='2'){
						estatus_hmtl += '<option value="1">Activada</option>';
						estatus_hmtl += '<option value="2" selected="yes">Desactivada</option>';
					}
					//carga select de cuentas de Estatus
					$select_estatus.children().remove();
					$select_estatus.append(estatus_hmtl);
					
					$cuenta.focus();
					* */
				},"json");//termina llamada json
				
		
		

				
				
				
				$aplica_evento_focus_input_numerico($debe);
				$aplica_evento_focus_input_numerico($haber);
				
				$debe.blur(function(){
					$validar_numero_puntos($debe, "Debe");
					if($(this).val().trim()==''){
						$(this).val(0);
					}
				});
				
				$haber.blur(function(){
					$validar_numero_puntos($haber, "Haber");
					if($(this).val().trim()==''){
						$(this).val(0);
					}
				});
				
				
				$cuenta.keypress(function(e){
					if (e.which == 8) {
						$descripcion_cuenta.val('');
						$id_cta.val('0');
					}else{
						if((parseInt($cuenta.val().length)+1)==4){
							$scuenta.focus();
						}
					}
				});
				
				$scuenta.keypress(function(e){
					if (e.which == 8) {
						$descripcion_cuenta.val('');
						$id_cta.val('0');
					}else{
						if((parseInt($scuenta.val().length)+1)==4){
							$sscuenta.focus();
						}
					}
				});
				
				$sscuenta.keypress(function(e){
					if (e.which == 8) {
						$descripcion_cuenta.val('');
						$id_cta.val('0');
					}else{
						if((parseInt($sscuenta.val().length)+1)==4){
							$ssscuenta.focus();
						}
					}
				});
				
				$ssscuenta.keypress(function(e){
					if (e.which == 8) {
						$descripcion_cuenta.val('');
						$id_cta.val('0');
					}else{
						if((parseInt($ssscuenta.val().length)+1)==4){
							$sssscuenta.focus();
						}
					}
				});
				
				$sssscuenta.keypress(function(e){
					if (e.which == 8) {
						$descripcion_cuenta.val('');
						$id_cta.val('0');
					}
				});
				
				
		
		
		
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-ctbpolizacontable-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-ctbpolizacontable-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllPolizas.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllPolizas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



