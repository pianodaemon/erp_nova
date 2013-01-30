$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
    //arreglo para select tipo de Documento
    var arrayTiposDocumento = {
				1:"Factura", 
				2:"Remision",
				3:"Ajuste",
				4:"Requisicion",
				5:"Nota de Credito"
			};
	
	
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/invcontrolcostos";
	
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Control de Costos');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	
	var $cadena_busqueda = "";
	var $campo_busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $campo_busqueda_oc = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_oc]');
	var $campo_busqueda_select_tipo_doc = $('#barra_buscador').find('.tabla_buscador').find('select[name=select_tipo_doc]');
	var $campo_busqueda_folio_doc = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio_doc]');
	var $campo_busqueda_cliente = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente]');
	var $campo_busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	var tiposIva = new Array(); //este arreglo carga los select del grid cada que se agrega un nuevo producto
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('#boton_buscador');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('#boton_limpiar');
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $campo_busqueda_folio.val() + "|";
		valor_retorno += "orden_compra" + signo_separador + $campo_busqueda_oc.val() + "|";
		valor_retorno += "folio_doc" + signo_separador + $campo_busqueda_folio_doc.val() + "|";
		valor_retorno += "cliente" + signo_separador + $campo_busqueda_cliente.val()+ "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val() + "|";
		valor_retorno += "tipo_doc" + signo_separador + $campo_busqueda_select_tipo_doc.val() + "|";
		valor_retorno += "codigo" + signo_separador + $campo_busqueda_codigo.val();
		return valor_retorno;
	};
	
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	
	$buscar.click(function(event){
		event.preventDefault();
		cadena = to_make_one_search_string();
		$cadena_busqueda = cadena.toCharCode();
		$get_datos_grid();
	});
	
	$limpiar.click(function(event){
		event.preventDefault();
		$campo_busqueda_folio.val('');
		$campo_busqueda_oc.val('');
		$campo_busqueda_folio_doc.val('');
		$campo_busqueda_cliente.val(''); 
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val(''); 
		$campo_busqueda_codigo.val(''); 
		//para reiniciar el select
		$campo_busqueda_select_tipo_doc.children().remove();
		var select_html = '<option value="0" selected="yes">[-- --]</option>';
		for(var i in arrayTiposDocumento){
			select_html += '<option value="' + i + '" >' + arrayTiposDocumento[i] + '</option>';
		}
		$campo_busqueda_select_tipo_doc.append(select_html);
	
	});
	
	
	$campo_busqueda_select_tipo_doc.children().remove();
	var select_html = '<option value="0" selected="yes">[-- --]</option>';
	for(var i in arrayTiposDocumento){
		select_html += '<option value="' + i + '" >' + arrayTiposDocumento[i] + '</option>';
	}
	$campo_busqueda_select_tipo_doc.append(select_html);
	
	
	
	//visualizar  la barra del buscador
	TriggerClickVisializaBuscador = 0;
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
	});
	
	$tabs_li_funxionalidad = function(){
		var $select_prod_tipo = $('#forma-invcontrolcostos-window').find('select[name=prodtipo]');
		$('#forma-invcontrolcostos-window').find('#submit').mouseover(function(){
			$('#forma-invcontrolcostos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-invcontrolcostos-window').find('#submit').mouseout(function(){
			$('#forma-invcontrolcostos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		
		$('#forma-invcontrolcostos-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-invcontrolcostos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		});
		$('#forma-invcontrolcostos-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-invcontrolcostos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-invcontrolcostos-window').find('#close').mouseover(function(){
			$('#forma-invcontrolcostos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-invcontrolcostos-window').find('#close').mouseout(function(){
			$('#forma-invcontrolcostos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		$('#forma-invcontrolcostos-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-invcontrolcostos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-invcontrolcostos-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-invcontrolcostos-window').find("ul.pestanas li").click(function() {
			$('#forma-invcontrolcostos-window').find(".contenidoPes").hide();
			$('#forma-invcontrolcostos-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-invcontrolcostos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
        
        
        
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
	//----------------------------------------------------------------

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
	
	
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	
	
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
	
	
	
	$aplicar_evento_focus = function( $campo_input ){
		//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$campo_input.focus(function(e){
			if(parseFloat($campo_input.val())<1){
				$campo_input.val('');
			}
		});
	}
	
	
	$aplicar_evento_blur = function( $campo_input ){
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_input.blur(function(e){
			if(parseFloat($campo_input.val())==0||$campo_input.val()==""){
				$campo_input.val(0);
			}
			$(this).val(parseFloat($(this).val()).toFixed(2));
		});
	}
	
	

	
	
	$aplicar_evento_click_input_lote = function( $campo_input ){
		//validar campo cantidad recibida, solo acepte numeros y punto
		$campo_input.dblclick(function(e){
			$(this).select();
		});
	}
	
	
	
	
	
	
	
	//buscador de productos
	$busca_productos = function(producto, tipo_prod, marca, familia, subfamilia ){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscaproducto();
		var $dialogoc =  $('#forma-buscaproducto-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());
		
		$('#forma-buscaproducto-window').css({ "margin-left": -200, 	"margin-top": -200  });
		
		var $tabla_resultados = $('#forma-buscaproducto-window').find('#tabla_resultado');
		
		var $campo_sku = $('#forma-buscaproducto-window').find('input[name=campo_sku]');
		var $select_tipo_producto = $('#forma-buscaproducto-window').find('select[name=tipo_producto]');
		var $campo_descripcion = $('#forma-buscaproducto-window').find('input[name=campo_descripcion]');
		
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
		
		$select_tipo_producto.hide();
		
		/*
		//buscar todos los tipos de productos
		var input_json_tipos = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProductoTipos.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_tipos,$arreglo,function(data){
			//Llena el select tipos de productos en el buscador
			$select_tipo_producto.children().remove();
			var prod_tipos_html = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
			$.each(data['prodTipos'],function(entryIndex,pt){
				prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
			});
			$select_tipo_producto.append(prod_tipos_html);
		});
		* */
		
		$campo_descripcion.val(producto);
		
		//click buscar productos
		$buscar_plugin_producto.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorProductos.json';
			$arreglo = {    'tipo':tipo_prod,
							'marca':marca,
							'familia':familia,
							'subfamilia':subfamilia,
							'sku':$campo_sku.val(),
							'descripcion':$campo_descripcion.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['productos'],function(entryIndex,producto){
					trr = '<tr>';
						trr += '<td width="110">';
							trr += '<span class="sku_prod_buscador">'+producto['sku']+'</span>';
							trr += '<input type="hidden" id="id_prod_buscador" value="'+producto['id']+'">';
						trr += '</td>';
						trr += '<td width="280"><span class="titulo_prod_buscador">'+producto['descripcion']+'</span></td>';
						trr += '<td width="90"><span class="unidad_prod_buscador">'+producto['unidad']+'</span></td>';
						trr += '<td width="100"><span class="tipo_prod_buscador">'+producto['tipo']+'</span></td>';
					trr += '</tr>';
					$tabla_resultados.append(trr);
				});
				$tabla_resultados.find('tr:odd').find('td').css({ 'background-color' : '#e7e8ea'});
				$tabla_resultados.find('tr:even').find('td').css({ 'background-color' : '#FFFFFF'});
				
				$('tr:odd' , $tabla_resultados).hover(function () {
					$(this).find('td').css({ background : '#FBD850'});
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
					$('#forma-invcontrolcostos-window').find('input[name=id_producto]').val($(this).find('#id_prod_buscador').val());
					$('#forma-invcontrolcostos-window').find('input[name=producto]').val($(this).find('span.titulo_prod_buscador').html());
					//elimina la ventana de busqueda
					var remove = function() { $(this).remove(); };
					$('#forma-buscaproducto-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-invcontrolcostos-window').find('input[name=producto]').focus();
				});
			});
		});
		
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if($campo_descripcion.val() != ''){
			$buscar_plugin_producto.trigger('click');
		}
		
		$cancelar_plugin_busca_producto.click(function(event){
			//event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-buscaproducto-overlay').fadeOut(remove);
		});
	}//termina buscador de productos

	
	
	
	
    
	
	//funcion que genera tr para agregar 
	$genera_tr = function(noTr, producto_id, codigo, descripcion, unidad, presentacion, orden_compra, factura_prov, moneda, costo, tipo_cambio, costo_importacion, costo_directo, costo_referencia, precio_minimo, moneda_pm ){
		var tr_prod='';
			tr_prod += '<tr>';
			tr_prod += '<td width="80" class="grid" style="font-size: 11px; border:1px solid #C1DAD7; text-align:left;">';
				tr_prod += '<input type="hidden" name="no_tr" id="notr" value="'+ noTr +'">';
				tr_prod += '<input type="hidden" name="id_prod" id="idprod" value="'+  producto_id +'">';
				//tr_prod += '<input type="text"  name="sku" value="'+codigo+'"  id="codigo'+ noTr +'" class="borde_oculto" style="width:76px;" readOnly="true">';
				tr_prod += codigo;
			tr_prod += '</td>';
			tr_prod += '<td width="148" class="grid" align="right" style="font-size:11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<input type="text" name="desc" value="'+descripcion+'" id="desc'+ noTr +'" class="borde_oculto" style="width:145px;" readOnly="true">';
			tr_prod += '</td>';
			tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<input type="text" name="unidad" class="borde_oculto" value="'+unidad+'" readOnly="true" style="width:66px;">';
			tr_prod += '</td>';
			tr_prod += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:left;">';
				tr_prod += '<input type="hidden" name="id_pres" id="idpres" value="0">';
				//tr_prod += '<input type="text" name="presentacion" class="borde_oculto" value="'+presentacion+'" readOnly="true" style="width:76px;">';
				tr_prod += presentacion;
			tr_prod += '</td>';
			tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:left;">';
				//tr_prod += '<input type="text" name="oc" class="borde_oculto" value="'+orden_compra+'" readOnly="true" style="width:66px;">';
				tr_prod += orden_compra;
			tr_prod += '</td>';
			tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:left;">';
				//tr_prod += '<input type="text" name="fac" class="borde_oculto" value="'+factura_prov+'" readOnly="true" style="width:66px;">';
				tr_prod += factura_prov;
			tr_prod += '</td>';
			tr_prod += '<td width="40" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				//tr_prod += '<input type="text" name="moneda" class="borde_oculto" value="'+moneda+'" readOnly="true" style="width:46px;">';
				tr_prod += moneda;
			tr_prod += '</td>';
			tr_prod += '<td width="60" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:right;">';
				//tr_prod += '<input type="text" name="tc" class="borde_oculto" value="'+tipo_cambio+'" readOnly="true" style="width:56px;">';
				tr_prod +=tipo_cambio;
			tr_prod += '</td>';
			tr_prod += '<td width="60" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				//tr_prod += '<input type="text" name="costo" class="borde_oculto" value="'+costo+'" readOnly="true" style="width:56px;">';
				tr_prod +=costo;
			tr_prod += '</td>';
			tr_prod += '<td width="60" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:right;">';
				//tr_prod += '<input type="text" name="ci" class="borde_oculto" value="'+costo_importacion+'" readOnly="true" style="width:56px;">';
				tr_prod +=costo_importacion;
			tr_prod += '</td>';
			tr_prod += '<td width="60" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:right;">';
				//tr_prod += '<input type="text" name="cd" class="borde_oculto" value="'+costo_directo+'" readOnly="true" style="width:56px;">';
				tr_prod +=costo_directo;
			tr_prod += '</td>';
			tr_prod += '<td width="60" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:right;">';
				//tr_prod += '<input type="text" name="cr" class="borde_oculto" value="'+costo_referencia+'" readOnly="true" style="width:56px;">';
				tr_prod +=costo_referencia;
			tr_prod += '</td>';
			tr_prod += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:right;">';
				//tr_prod += '<input type="text" name="pm" class="borde_oculto" value="'+precio_minimo+'" readOnly="true" style="width:56px;">';
				tr_prod +=precio_minimo;
			tr_prod += '</td>';
			tr_prod += '<td width="45" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += moneda_pm;
			tr_prod += '</td>';
		tr_prod += '</tr>';
		
		return tr_prod;
	}
	
	
	
	
	
	
	//Aquí entra nuevo
	$new.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_invcontrolcostos();
		
		var form_to_show = 'formainvcontrolcostos00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-invcontrolcostos-window').css({ "margin-left": -425, 	"margin-top": -230 });
		
		$forma_selected.prependTo('#forma-invcontrolcostos-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosCalculoCosto.json';
		$arreglo = {'identificador':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
		
		var $identificador = $('#forma-invcontrolcostos-window').find('input[name=identificador]');
		var $folio = $('#forma-invcontrolcostos-window').find('input[name=folio]');
		var $id_producto = $('#forma-invcontrolcostos-window').find('input[name=id_producto]');
		var $producto = $('#forma-invcontrolcostos-window').find('input[name=producto]');
		var $buscar_producto = $('#forma-invcontrolcostos-window').find('#buscar_producto');
		
		var $select_tipo_prod = $('#forma-invcontrolcostos-window').find('select[name=select_tipo_prod]');
		var $select_marca = $('#forma-invcontrolcostos-window').find('select[name=select_marca]');
		var $select_familia = $('#forma-invcontrolcostos-window').find('select[name=select_familia]');
		var $select_subfamilia = $('#forma-invcontrolcostos-window').find('select[name=select_subfamilia]');
		var $select_presentacion = $('#forma-invcontrolcostos-window').find('select[name=select_presentacion]');
		var $tipo_cambio = $('#forma-invcontrolcostos-window').find('input[name=tipo_cambio]');
		
		//var $check_costo_ultimo = $('#forma-invcontrolcostos-window').find('input[name=check_costo_ultimo]');
		//var $check_costo_promedio = $('#forma-invcontrolcostos-window').find('input[name=check_costo_promedio]');
		var $radio_costo_ultimo = $('#forma-invcontrolcostos-window').find('.radio_costo_ultimo');
		var $radio_costo_promedio = $('#forma-invcontrolcostos-window').find('.radio_costo_promedio');
		
		var $costo_importacion = $('#forma-invcontrolcostos-window').find('input[name=costo_importacion]');
		var $costo_directo = $('#forma-invcontrolcostos-window').find('input[name=costo_directo]');
		var $precio_minimo = $('#forma-invcontrolcostos-window').find('input[name=precio_minimo]');
		var $check_simulacion = $('#forma-invcontrolcostos-window').find('input[name=check_simulacion]');
		
		var $busqueda = $('#forma-invcontrolcostos-window').find('#busqueda');
		var $pdf = $('#forma-invcontrolcostos-window').find('#pdf');
		var $excel = $('#forma-invcontrolcostos-window').find('#excel');
		var $aplicar = $('#forma-invcontrolcostos-window').find('#aplicar');
		
		//tabla contenedor del listado de productos
		var $grid_productos = $('#forma-invcontrolcostos-window').find('#grid_productos');
		var $etiqueta_encabezado_tipo_costo = $('#forma-invcontrolcostos-window').find('#tipo_costo');
		
		
		
		var $cerrar_plugin = $('#forma-invcontrolcostos-window').find('#close');
		var $cancelar_plugin = $('#forma-invcontrolcostos-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-invcontrolcostos-window').find('#submit');
		
		
		//quitar enter a todos los campos input
		$('#forma-invcontrolcostos-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		$radio_costo_ultimo.attr('checked',  true );
		$costo_importacion.val(parseFloat(0).toFixed(2));
		$costo_directo.val(parseFloat(0).toFixed(2));
		$precio_minimo.val(parseFloat(0).toFixed(2));
		$tipo_cambio.val(parseFloat(0).toFixed(4));
		
		$permitir_solo_numeros($costo_importacion);
		$permitir_solo_numeros($costo_directo);
		$permitir_solo_numeros($precio_minimo);
		$permitir_solo_numeros($tipo_cambio);
		
		$aplicar_evento_focus( $costo_importacion );
		$aplicar_evento_focus( $costo_directo );
		$aplicar_evento_focus( $precio_minimo );
		$aplicar_evento_focus( $tipo_cambio );
		
		$aplicar_evento_blur( $costo_importacion );
		$aplicar_evento_blur( $costo_directo );
		$aplicar_evento_blur( $precio_minimo );
		//$aplicar_evento_blur( $tipo_cambio );
		
		$tipo_cambio.css({'background' : '#F0F0F0'});
		$tipo_cambio.attr('readonly',true);
		//$pdf
		$excel.hide();//oculto por lo pronto mientras no se utiliza
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("El Pedido se guard&oacute; con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-invcontrolcostos-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				//$('#forma-invcontrolcostos-window').find('.div_one').css({'height':'545px'});//sin errores
				$('#forma-invcontrolcostos-window').find('.invcontrolcostos_div_one').css({'height':'578px'});//con errores
				$('#forma-invcontrolcostos-window').find('div.interrogacion').css({'display':'none'});
				
				$grid_productos.find('#cant').css({'background' : '#ffffff'});
				$grid_productos.find('#cost').css({'background' : '#ffffff'});
				
				$('#forma-invcontrolcostos-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-invcontrolcostos-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-invcontrolcostos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						//alert(tmp.split(':')[0]);
						
						
						
					}
				}
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		//$.getJSON(json_string,function(entry){
		$.post(input_json,$arreglo,function(entry){
			/*
			$check_costo_ultimo
			$check_costo_promedio
			$check_simulacion
			*/
			//$tipo_cambio.val(entry['Tc']['0']['tipo_cambio']);
			
			
			
			//carga select de tipos de producto
			$select_tipo_prod.children().remove();
			//var prodtipos_hmtl = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
			var prodtipos_hmtl = '';
			$.each(entry['ProdTipos'],function(entryIndex,tp){
				if(parseInt(tp['id'])==1){
					prodtipos_hmtl += '<option value="' + tp['id'] + '" selected="yes">' + tp['titulo'] + '</option>';
				}else{
					prodtipos_hmtl += '<option value="' + tp['id'] + '"  >' + tp['titulo'] + '</option>';
				}
			});
			$select_tipo_prod.append(prodtipos_hmtl);
			
			
			//carga select de Marcas
			$select_marca.children().remove();
			var marca_hmtl = '<option value="0" selected="yes">[--Seleccionar Marca--]</option>';
			$.each(entry['Marcas'],function(entryIndex,mar){
				marca_hmtl += '<option value="' + mar['id'] + '"  >' + mar['titulo'] + '</option>';
			});
			$select_marca.append(marca_hmtl);
			
			
			//carga select de familas
			$select_familia.children().remove();
			var familia_hmtl = '<option value="0">[--Seleccionar Familia--]</option>';
			$.each(entry['Familias'],function(entryIndex,fam){
				familia_hmtl += '<option value="' + fam['id'] + '"  >' + fam['titulo'] + '</option>';
			});
			$select_familia.append(familia_hmtl);
			
			
			//Alimentando select de SubFamilias
			$select_subfamilia.children().remove();
			var subfamilia_hmtl = '<option value="0">[--Seleccionar SubFamilia--]</option>';
			$select_subfamilia.append(subfamilia_hmtl);
			
			
			//carga select de Presentaciones
			$select_presentacion.children().remove();
			var presentacion_hmtl = '<option value="0">[--Seleccionar Presentaci&oacute;n--]</option>';
			$.each(entry['Presentaciones'],function(entryIndex,pres){
				presentacion_hmtl += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
			});
			$select_presentacion.append(presentacion_hmtl);
			
			
			$select_tipo_prod.change(function(){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFamiliasByTipoProd.json';
				$arreglo = {	'tipo_prod':$select_tipo_prod.val(),
								'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
							};
				$.post(input_json,$arreglo,function(data){
					$select_familia.children().remove();
					familia_hmtl = '<option value="0">[--Seleccionar Familia--]</option>';
					$.each(data['Familias'],function(entryIndex,fam){
						familia_hmtl += '<option value="' + fam['id'] + '"  >' + fam['titulo'] + '</option>';
					});
					$select_familia.append(familia_hmtl);
				});
			});
			
			$select_familia.change(function(){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getSubFamiliasByFamProd.json';
				$arreglo = {'fam':$select_familia.val(),
								'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
							}
				$.post(input_json,$arreglo,function(data){
					//Alimentando select de Subfamilias
					$select_subfamilia.children().remove();
					var subfamilia_hmtl = '<option value="0">[--Seleccionar Subfmilia--]</option>';
					$.each(data['SubFamilias'],function(dataIndex,subfam){
						subfamilia_hmtl += '<option value="' + subfam['id'] + '"  >' + subfam['titulo'] + '</option>';
					});
					$select_subfamilia.append(subfamilia_hmtl);
				});
			});
			
			
			
		},"json");//termina llamada json
		
		
		
		
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$tipo_cambio.blur(function(e){
			if(parseFloat($tipo_cambio.val())==0||$tipo_cambio.val()==""){
				$tipo_cambio.val(0);
			}
			$(this).val(parseFloat($(this).val()).toFixed(4));
		});
		
		
	
		
		//click al radio buton de Costo Promedio
		$radio_costo_promedio.click(function(event){
			if($check_simulacion.is(':checked')){
				$tipo_cambio.val("1.0000");
				$tipo_cambio.css({'background' : '#F0F0F0'});
				$tipo_cambio.attr('readonly',true);
			}
			$etiqueta_encabezado_tipo_costo.html("C.&nbsp;P.");
		});
		
		//click al radio buton de Costo Ultimo
		$radio_costo_ultimo.click(function(event){
			if($check_simulacion.is(':checked')){
				//quitar propiedad de solo lectura
				$tipo_cambio.val("1.0000");
				$tipo_cambio.css({'background' : '#ffffff'});
				$tipo_cambio.removeAttr('readonly');
			}
			$etiqueta_encabezado_tipo_costo.html("C.&nbsp;U.");
		});
		
		//click al check de Simulacion
		$check_simulacion.click(function(event){
			if($(this).is(':checked')){
				if($radio_costo_ultimo.is(':checked')){
					//quitar propiedad de solo lectura
					$tipo_cambio.val("1.0000");
					$tipo_cambio.css({'background' : '#ffffff'});
					$tipo_cambio.removeAttr('readonly');
					//$submit_actualizar.attr('disabled','-1');//deshabilitar
					$submit_actualizar.hide();
				}
			}else{
				$tipo_cambio.val("1.0000");
				$tipo_cambio.css({'background' : '#F0F0F0'});
				$tipo_cambio.attr('readonly',true);
				//$submit_actualizar.removeAttr('disabled');//habilitar
				$submit_actualizar.show();
			}
		});
		
		
		
		//buscar producto
		$buscar_producto.click(function(event){
			event.preventDefault();
			$busca_productos($producto.val(), $select_tipo_prod.val(), $select_marca.val(), $select_familia.val(), $select_subfamilia.val() );
		});
		
		
		
		
		$busqueda.click(function(event){
			var tipo_costo=0;
			var simulacion="false";
			if($radio_costo_ultimo.is(':checked')){
				tipo_costo=1;
			}
			
			if($radio_costo_promedio.is(':checked')){
				tipo_costo=2;
			}
			
			if($check_simulacion.is(':checked')){
				simulacion="true";
			}
			
			//eliminar contenidos
			$grid_productos.children().remove();
			
			var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBusquedaProductos.json';
			$arreglo2 = {
							'tipo_prod':$select_tipo_prod.val(),
							'mar':$select_marca.val(),
							'fam':$select_familia.val(),
							'subfam':$select_subfamilia.val(),
							'tipo_costo':tipo_costo,
							'producto':$id_producto.val(),
							'pres':$select_presentacion.val(),
							'simulacion':simulacion,
							'importacion':$costo_importacion.val(),
							'directo':$costo_directo.val(),
							'pminimo':$precio_minimo.val(),
							'tc':$tipo_cambio.val(),
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
						
			$.post(input_json2,$arreglo2,function(data){
				
				$.each(data['Grid'],function(dataIndex,prod){
					var producto_id=prod['producto_id'];
					var codigo=prod['codigo'];
					var descripcion=prod['descripcion'];
					var unidad=prod['unidad'];
					var presentacion=prod['presentacion'];
					var orden_compra=prod['orden_compra'];
					var factura_prov=prod['factura_prov'];
					var moneda=prod['moneda'];
					var costo=prod['costo'];
					var tipo_cambio=prod['tipo_cambio'];
					var costo_importacion=prod['costo_importacion'];
					var costo_directo=prod['costo_directo'];
					var costo_referencia=prod['costo_referencia'];
					var precio_minimo=prod['precio_minimo'];
					var moneda_pm=prod['moneda_pm'];
					
					var noTr = $("tr", $grid_productos).size();
					noTr++;
					
					var nuevo_tr = $genera_tr(noTr, producto_id, codigo, descripcion, unidad, presentacion, orden_compra, factura_prov, moneda, costo, tipo_cambio, costo_importacion, costo_directo, costo_referencia, precio_minimo, moneda_pm);
					//alert(nuevo_tr);
					$grid_productos.append(nuevo_tr);//agrega el tr a la tabla
					
				});
			});
		});
		
		
		//al darle aplicar en Simulacion, ejecutamos el click del boton Busqueda
		$aplicar.click(function(event){
			$('#forma-invcontrolcostos-window').find('div.interrogacion').css({'display':'none'});//desaparecer los warning
			var ejecutar=false;
			
			if(parseInt($("tr", $grid_productos).size())>0){
				if($check_simulacion.is(':checked')){
					if(parseFloat($tipo_cambio.val()) > 0 ){
						ejecutar=true;
					}else{
						ejecutar=false;
						//visualizar el warning
						$('#forma-invcontrolcostos-window').find('img[rel=warning_tipocambio]')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: "El Tipo de Cambio debe ser mayor que cero para la Simulaci&oacute;n."});
					}
					
					if(ejecutar){
						if(parseFloat($precio_minimo.val()) > 0 ){
							ejecutar=true;
						}else{
							ejecutar=false;
							jAlert("El porcentaje para el c&aacute;lculo del Precio M&iacute;nimo debe ser mayor que cero.", 'Atencion!');
							
							//visualizar el warning
							$('#forma-invcontrolcostos-window').find('img[rel=warning_preciominimo]')
							.parent()
							.css({'display':'block'})
							.easyTooltip({tooltipId: "easyTooltip2",content: "El porcentaje para el c&aacute;lculo del Precio M&iacute;nimo debe ser mayor que cero."});
						}
					}
					
					if(ejecutar){
						$busqueda.trigger('click');
					}
				}else{
					jAlert("El boton Aplicar es para realizar una Simulaci&oacute;n de c&aacute;lculo de costos.\nSeleccione la casilla de Simulaci&oacute;n y haga click en el boton Aplicar.", 'Atencion!');
				}
			}else{
				jAlert("No hay productos en el listado.", 'Atencion!');
			}
		});
		
		
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_productos).size();
			if(parseInt(trCount) > 0){
				if(parseFloat($precio_minimo.val()) > 0 ){
					return true;
				}else{
					jAlert("El porcentaje para el c&aacute;lculo del Precio M&iacute;nimo debe ser mayor que cero.", 'Atencion!');
					
					//visualizar el warning
					$('#forma-invcontrolcostos-window').find('img[rel=warning_preciominimo]')
					.parent()
					.css({'display':'block'})
					.easyTooltip({tooltipId: "easyTooltip2",content: "El porcentaje para el c&aacute;lculo del Precio M&iacute;nimo debe ser mayor que cero."});
					return false;
				}
			}else{
				jAlert("No hay datos para actualizar", 'Atencion!');
				return false;
			}
		});
		
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-invcontrolcostos-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-invcontrolcostos-overlay').fadeOut(remove);
		});
		
	});
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	var carga_formainvcontrolcostos00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'no_entrada':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la entrada seleccionada?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La entrada fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La entrada no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
			
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formainvcontrolcostos00';
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			//var accion = "get_datos_entrada_mercancia";
			
			$(this).modalPanel_invcontrolcostos();
			
			$('#forma-invcontrolcostos-window').css({ "margin-left": -425, 	"margin-top": -230 });
			
			$forma_selected.prependTo('#forma-invcontrolcostos-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getOrdenSalida.json';
				$arreglo = {'identificador':id_to_show,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var $identificador = $('#forma-invcontrolcostos-window').find('input[name=identificador]');
				var $folio_salida = $('#forma-invcontrolcostos-window').find('input[name=folio_salida]');
				var $estatus = $('#forma-invcontrolcostos-window').find('input[name=estatus]');
				var $accion = $('#forma-invcontrolcostos-window').find('input[name=accion]');
				var $select_tipo_movimiento = $('#forma-invcontrolcostos-window').find('select[name=select_tipo_movimiento]');
				
				//campos del cliente o Proveedor
				var $hidden_id_cliente = $('#forma-invcontrolcostos-window').find('input[name=id_cliente]');
				var $campo_razoncliente = $('#forma-invcontrolcostos-window').find('input[name=razoncliente]');
				var $etiqueta_origen = $('#forma-invcontrolcostos-window').find('input[name=etiqueta_origen]');
				
				var $select_tipo_doc = $('#forma-invcontrolcostos-window').find('select[name=select_tipodoc]');
				var $folio_doc = $('#forma-invcontrolcostos-window').find('input[name=folio_doc]');
				var $fecha_doc = $('#forma-invcontrolcostos-window').find('input[name=fecha_doc]');
				var $select_moneda = $('#forma-invcontrolcostos-window').find('select[name=select_moneda]');
				var $orden_compra = $('#forma-invcontrolcostos-window').find('input[name=orden_compra]');
				var $folio_pedido = $('#forma-invcontrolcostos-window').find('input[name=folio_pedido]');
				var $campo_tc = $('#forma-invcontrolcostos-window').find('input[name=tc]');
				var $observaciones = $('#forma-invcontrolcostos-window').find('textarea[name=observaciones]');
				var $select_almacen_origen = $('#forma-invcontrolcostos-window').find('select[name=select_almacen_origen]');
				
				
				//tabla contenedor del listado de productos
				var $grid_productos = $('#forma-invcontrolcostos-window').find('#grid_productos');
				
				//campos de totales
				var $campo_subtotal = $('#forma-invcontrolcostos-window').find('input[name=subtotal]');
				var $campo_retencion = $('#forma-invcontrolcostos-window').find('input[name=retencion]');
				var $campo_impuesto = $('#forma-invcontrolcostos-window').find('input[name=totimpuesto]');
				var $campo_total = $('#forma-invcontrolcostos-window').find('input[name=total]');
				
				var $confirmar = $('#forma-invcontrolcostos-window').find('#confirmar');
				var $descargar_pdf = $('#forma-invcontrolcostos-window').find('#descargar_pdf');
				
				var $cerrar_plugin = $('#forma-invcontrolcostos-window').find('#close');
				var $cancelar_plugin = $('#forma-invcontrolcostos-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-invcontrolcostos-window').find('#submit');
				//$submit_actualizar.hide();//ocultar boton para que no permita actualizar
				
				$folio_doc.attr("readonly", true);
				$orden_compra.attr("readonly", true);
				$folio_pedido.attr("readonly", true);
				$fecha_doc.attr("readonly", true);
				$campo_tc.attr("readonly", true);
				//$observaciones.attr("readonly", true);
				$accion.val('edit');
				$submit_actualizar.hide();
				$descargar_pdf.attr('disabled','-1'); //deshabilitar
				$confirmar.attr('disabled','-1'); //deshabilitar
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						if ( $accion.val()=='edit'){
							jAlert("La Orden de Salida "+$folio_salida.val()+" se guard&oacute; con &eacute;xito", 'Atencion!');
						}else{
							jAlert("Orden de Salida "+$folio_salida.val()+" se Confirmado con &eacute;xito", 'Atencion!');
						}
						//habilitar boton actualizar
						$submit_actualizar.removeAttr('disabled');
						var remove = function() { $(this).remove(); };
						$('#forma-invcontrolcostos-overlay').fadeOut(remove);
						$get_datos_grid();
					}else{
						//habilitar boton actualizar
						$submit_actualizar.removeAttr('disabled');
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-invcontrolcostos-window').find('div.interrogacion').css({'display':'none'});
						$grid_productos.find('input[name=cant_sur]').css({'background' : '#ffffff'});
						$grid_productos.find('input[name=lote_int]').css({'background' : '#ffffff'});
						
						$('#forma-invcontrolcostos-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-invcontrolcostos-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						$('#forma-invcontrolcostos-window').find('.invcontrolcostos_div_one').css({'height':'585px'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-invcontrolcostos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								var campo = tmp.split(':')[0];
								
								$('#forma-invcontrolcostos-window').find('#div_warning_grid').css({'display':'block'});
								var $campo = $grid_productos.find('.'+campo).css({'background' : '#d41000'});
								
								var codigo_producto = $campo.parent().parent().find('input[name=sku]').val();
								var titulo_producto = $campo.parent().parent().find('input[name=titulo]').val();
								
								var tr_warning = '<tr>';
										tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
										tr_warning += '<td width="100"><INPUT TYPE="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:95px; color:red"></td>';
										tr_warning += '<td width="200"><INPUT TYPE="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:205px; color:red"></td>';
										tr_warning += '<td width="420"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:420px; color:red"></td>';
								tr_warning += '</tr>';
								
								$('#forma-invcontrolcostos-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
								
							}
						}
						$('#forma-invcontrolcostos-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
						$('#forma-invcontrolcostos-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$identificador.attr({ 'value' : entry['Datos']['0']['id'] });
					$folio_salida.attr({ 'value' : entry['Datos']['0']['folio'] });
					$estatus.attr({ 'value' : entry['Datos']['0']['estado'] });
					
					$etiqueta_origen.attr({ 'value' : entry['Datos']['0']['origen_salida'] });
					$hidden_id_cliente.attr({ 'value' : entry['Datos']['0']['id_cliente'] });
					$campo_razoncliente.attr({ 'value' : entry['Datos']['0']['razon_cliente'] });
					
					$folio_doc.attr({ 'value' : entry['Datos']['0']['folio_doc'] });
					$fecha_doc.attr({ 'value' : entry['Datos']['0']['fecha_doc'] });
					$orden_compra.attr({ 'value' : entry['Datos']['0']['orden_compra'] });
					$folio_pedido.attr({ 'value' : entry['Datos']['0']['folio_pedido'] });
					$campo_tc.attr({ 'value' : entry['Datos']['0']['tipo_cambio'] });
					$observaciones.text(entry['Datos']['0']['observaciones']);
					
					//$campo_flete.attr({ 'value' : entry['Datos']['0'][''] });
					$campo_subtotal.attr({ 'value' : $(this).agregar_comas( parseFloat(entry['Datos']['0']['subtotal']).toFixed(2) ) });
					$campo_retencion.attr({ 'value' : $(this).agregar_comas( parseFloat(entry['Datos']['0']['retencion']).toFixed(2) ) });
					$campo_impuesto.attr({ 'value' : $(this).agregar_comas( parseFloat(entry['Datos']['0']['iva']).toFixed(2) ) });
					$campo_total.attr({ 'value' : $(this).agregar_comas( parseFloat(entry['Datos']['0']['total']).toFixed(2) ) });

					
					//$campo_flete.val(parseFloat( entry['Datos']['0']['flete']).toFixed(2));
					//tiposIva = entry['Impuestos'];//asigna los tipos de impuestos al arreglo tiposIva
					
					
					//carga select tipo de Movimiento
					$select_tipo_movimiento.children().remove();
					var tipo_mov_hmtl = '';
					$.each(entry['TMovInv'],function(entryIndex,tmov){
						if(parseInt(tmov['id']) == parseInt(entry['Datos']['0']['tipo_movimiento_id'])){
							tipo_mov_hmtl += '<option value="' + tmov['id'] + '" selected="yes">' + tmov['titulo'] + '</option>';
						}else{
							//moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
						}
					});
					$select_tipo_movimiento.append(tipo_mov_hmtl);
					
					
					//carga select con tipo de documento
					$select_tipo_doc.children().remove();
					var select_html = '';
					for(var i in arrayTiposDocumento){
						if(parseInt(entry['Datos']['0']['tipo_doc'])==parseInt(i)){
							select_html += '<option value="' + i + '" selected="yes">' + arrayTiposDocumento[i] + '</option>';
						}else{
							//select_html += '<option value="' + i + '">' + arrayTiposDocumento[i] + '</option>';
						}
					}
					$select_tipo_doc.append(select_html);
					
					
					
					
					//carga select denominacion con todas las monedas
					$select_moneda.children().remove();
					//var moneda_hmtl = '<option value="0">[--   --]</option>';
					var moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(parseInt(moneda['id']) == parseInt(entry['Datos']['0']['moneda_id'])){
							moneda_hmtl += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion'] + '</option>';
						}else{
							//moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
						}
					});
					$select_moneda.append(moneda_hmtl);
					
					
					//carga select almacen origen
					$select_almacen_origen.children().remove();
					//var almacen_hmtl = '<option value="0" selected="yes">[--   --]</option>';
					var almacen_hmtl="";
					$.each(entry['Almacenes'],function(entryIndex,alm){
						if(parseInt(alm['id']) == parseInt(entry['Datos'][0]['id_almacen'])){
							almacen_hmtl += '<option value="' + alm['id'] + '"  selected="yes">' + alm['titulo'] + '</option>';
						}else{
							//almacen_hmtl += '<option value="' + alm['id'] + '"  >' + alm['titulo'] + '</option>';
						}
					});
					$select_almacen_origen.append(almacen_hmtl);
					
					
					
					if(entry['datosGrid'] != null){
						$.each(entry['datosGrid'],function(entryIndex,prodGrid){
							var trCount = $("tr", $grid_productos).size();
							trCount++;
							
							var valor_pedimento='';
							var tr_prod='';
							var tr_lote='';
							var tipo_registro='PAR';//partida
							
							tr_prod += '<tr>';
								tr_prod += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="hidden" name="no_tr" id="notr" value="'+ trCount +'">';
									tr_prod += '<input type="hidden" name="tipo" id="tip" value="'+ tipo_registro +'">';
									tr_prod += '<input type="hidden" name="id_detalle_os" id="iddetos" value="'+  prodGrid['id_detalle_osal'] +'">';
									tr_prod += '<input type="hidden" name="id_detalle_lot" id="iddetlot" value="0">';
									tr_prod += '<input type="hidden" name="id_alm" id="idalm" value="'+  $select_almacen_origen.val() +'">';
									tr_prod += '<input type="hidden" name="id_prod_grid" id="idprod" value="'+  prodGrid['producto_id'] +'">';
									tr_prod += '<input type="text"  name="sku" value="' + prodGrid['codigo'] + '" id="codigo'+ trCount +'" class="borde_oculto" style="width:76px;" readOnly="true">';
								tr_prod += '</td>';
								tr_prod += '<td width="180" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="titulo" value="' + prodGrid['titulo'] + '" id="titulo'+ trCount +'"  class="borde_oculto" style="width:176px;" readOnly="true">';
								tr_prod += '</td>';
								tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="unidad" class="borde_oculto" value="' + prodGrid['unidad'] + '" readOnly="true" style="width:66px;">';
								tr_prod += '</td>';
								tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="hidden" name="id_pres" id="idpres" value="'+ prodGrid['id_presentacion'] +'">';
									tr_prod += '<input type="text" name="presentacion" class="borde_oculto" value="' + prodGrid['presentacion'] + '" readOnly="true" style="width:66px;">';
								tr_prod += '</td>';
								tr_prod += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="cantidad" id="cant" value="' + $(this).agregar_comas(prodGrid['cant_fac']) + '"  class="borde_oculto" style="width:76px; text-align:right;" readOnly="true">';
									tr_prod += '<input type="hidden" name="eliminado" id="elim" value="1">';//1=registro vivo, 0=Registro eliminado
								tr_prod += '</td>';
								tr_prod += '<td width="85" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="costo" id="cost" value="' + $(this).agregar_comas(prodGrid['precio_unitario']) + '" class="borde_oculto" style="width:79px; text-align:right;" readOnly="true">';
								tr_prod += '</td>';
								
								tr_prod += '<td width="90" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="lote_int" value=" " class="lote_int'+ trCount +'" style="width:66px; display:none;">';
									tr_prod += '<input type="hidden" name="exis_lote" value="0" class="exis_lote'+ trCount +'">';
									tr_prod += '<input type="text" name="importe'+ trCount +'" id="import" value="' + $(this).agregar_comas(prodGrid['importe']) + '" class="borde_oculto" style="width:86px; text-align:right;" readOnly="true">';
								tr_prod += '</td>';
								
								tr_prod += '<td width="90" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="cant_sur" id="cant_s" value="' + prodGrid['cant_sur'] + '" class="cant_sur'+ trCount +'" style="width:86px;" >';
								tr_prod += '</td>';
								
								tr_prod += '<td width="100" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="pedimento" id="ped" value="" class="pedimento'+ trCount +'" style="width:96px; display:none;">';
								tr_prod += '</td>';
								
								tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="caducidad" id="cad" value="" class="caducidad'+ trCount +'" style="width:66px; display:none;">';
								tr_prod += '</td>';
								
							tr_prod += '</tr>';
							$grid_productos.append(tr_prod);//agrega el tr a la tabla
							
							//aqui empieza a agregar lotes si es que el producto tiene lote
							var id_producto = 0;
							var id_detalle_os=0;
							var id_detalle_lot=0;
							var codigo="";
							var titulo="";
							var id_almacen = 0;
							var lote_int = '';
							var trCount2=0;
							var unidad_medida='';
							var pedimento='';
							var caducidad='';
							//estos  se utiliza pra nuevo lote y editar lote
							codigo = prodGrid['codigo'];
							titulo = prodGrid['titulo'];
							unidad_medida = prodGrid['unidad'];
							
							if( parseInt(entry['Datos']['0']['estado'])==0 ){
								trCount2 = $("tr", $grid_productos).size();//obtener de nuevo el numero de trs de la tabla
								trCount2++;
								id_detalle_lot = 0;
								id_producto = prodGrid['producto_id'];
								id_detalle_os = prodGrid['id_detalle_osal'];
								id_almacen = $select_almacen_origen.val();
								lote_int = ' ';
								cant_lote=0;
								tipo_registro = 'LOT';
								pedimento='';
								caducidad='';
								
								//aqui es para crear nuevos registros del lote
								tr_lote = $genera_tr_para_numero_de_lote(tipo_registro, id_detalle_os, id_producto,codigo,titulo,unidad_medida, id_detalle_lot, id_almacen, lote_int,cant_lote, pedimento, caducidad, trCount2);
								$grid_productos.append(tr_lote);
								
								// esta funcion es para agregar un nuevo lote
								//en esta funcion se le aplica evento click a los href Agregar Lote y Eliminar
								agregar_lote_y_eliminar($grid_productos,tipo_registro, trCount2);
								$aplicar_evento_keypress( $grid_productos.find('.cant_sur'+ trCount2 ) );
								$aplicar_evento_blur( $grid_productos.find('.cant_sur'+ trCount2 ) );
								$aplicar_evento_focus( $grid_productos.find('.cant_sur'+ trCount2 ) );
								
								$aplicar_evento_focus_input_lote($grid_productos.find('.lote_int'+ trCount2 ));
								$aplicar_evento_blur_input_lote($grid_productos.find('.lote_int'+ trCount2 ));
								
								$aplicar_evento_keypress_input_lote($grid_productos.find('.lote_int'+ trCount2 ));
								
								$aplicar_evento_click_input_lote($grid_productos.find('.lote_int'+ trCount2 ));
							}else{
								var cont=0;
								$.each(entry['Lotes'],function(entryIndex,lote){
									trCount2 = $("tr", $grid_productos).size();//obtener de nuevo el numero de trs de la tabla
									
									id_detalle_lot = lote['id_lote_detalle'];
									id_detalle_os = lote['id_osal_detalle'];
									id_producto = lote['id_producto'];
									id_almacen = lote['id_almacen'];
									lote_int = lote['lote_int'];
									cant_lote = lote['cantidad_sal'];
									tipo_registro='LOT';
									pedimento=lote['ped_lote'];
									caducidad=lote['cad_lote'];
									
									if( parseInt(prodGrid['id_detalle_osal']) == parseInt(lote['id_osal_detalle']) ){
										trCount2++;
										if( parseInt(cont) > 0 ){
											//ocultar el href anterior de Agregar Lote
											$grid_productos.find('.agrega_lote'+ (parseInt(trCount2)-1) ).hide();
										}
										
										//Aqui ya es para editar el lote
										tr_lote = $genera_tr_para_numero_de_lote(tipo_registro, id_detalle_os, id_producto,codigo,titulo, unidad_medida, id_detalle_lot, id_almacen, lote_int,cant_lote, pedimento, caducidad, trCount2);
										$grid_productos.append(tr_lote);
										
										// esta funcion es para agregar un nuevo lote
										//en esta funcion se le aplica evento click a los href Agregar Lote y Eliminar
										agregar_lote_y_eliminar($grid_productos,tipo_registro, trCount2);
										$aplicar_evento_keypress( $grid_productos.find('.cant_sur'+ trCount2 ) );
										$aplicar_evento_blur( $grid_productos.find('.cant_sur'+ trCount2 ) );
										$aplicar_evento_focus( $grid_productos.find('.cant_sur'+ trCount2 ) );
										
										$aplicar_evento_focus_input_lote($grid_productos.find('.lote_int'+ trCount2 ));
										$aplicar_evento_blur_input_lote($grid_productos.find('.lote_int'+ trCount2 ));
										$aplicar_evento_click_input_lote($grid_productos.find('.lote_int'+ trCount2 ));
										
										//estatus 2=Confirmado, menor que dos aun no esta confirmado por lo tanto le aplicamos el evento keypress
										if(parseInt($estatus.val()) < 2){
											$aplicar_evento_keypress_input_lote($grid_productos.find('.lote_int'+ trCount2 ));
										}
										
										cont++;
									}
									
								});
							}
							
							$aplicar_evento_keypress( $grid_productos.find('.cant_sur'+ trCount ) );
							$aplicar_evento_focus( $grid_productos.find('.cant_sur'+ trCount ) );
							
							if( parseInt($estatus.val()) > 0 ){
								if(parseFloat(prodGrid['cant_fac']) != parseFloat(prodGrid['cant_sur'])){
									$grid_productos.find('.cant_sur'+ trCount ).css({'background' : '#d41000'});
								}
							}
							
							//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
							$grid_productos.find('.cant_sur'+ trCount ).blur(function(e){
								var tr_actual=$(this).parent().parent();
								if(parseFloat($(this).val())<=0 || $(this).val()==""){
									$(this).val(0);
								}
								
								if ( parseFloat(quitar_comas(tr_actual.find('input[name=cantidad]').val())) != parseFloat($(this).val()) ){
									$(this).css({'background' : '#d41000'});
									jAlert("La Cantidad Surtida debe ser igual que la Cantidad de la Factura.", 'Atencion!');
								}else{
									//aqui si es correcto
									$(this).css({'background' : '#ffffff'});
								}
								$(this).val(parseFloat($(this).val()).toFixed(4));
							});
							
						});
						
						if(parseInt($estatus.val())==0 ){
							$submit_actualizar.show();
						}
						
						if(parseInt($estatus.val())==1 ){
							$grid_productos.find('input[name=lote_int]').attr({ 'readOnly':true });
							$submit_actualizar.show();
							$confirmar.removeAttr('disabled');
						}
						
						if(parseInt($estatus.val()) == 2 ){
							$observaciones.attr({ 'readOnly':true });
							$descargar_pdf.removeAttr('disabled');
							$grid_productos.find('input[name=lote_int]').attr({ 'readOnly':true });
							$grid_productos.find('input[name=cant_sur]').attr({ 'readOnly':true });
							$grid_productos.find('a').hide();
							
							//quitar el enter en todos los input del grid
							$grid_productos.find('input').keypress(function(e){
								if(e.which==13 ) {
									return false;
								}
							});
						}
						
						
					}
					
					
					if(entry['Datos']['0']['cancelacion'] == 'true'){
						$select_tipo_movimiento.attr('disabled','-1'); //deshabilitar
						$confirmar.attr('disabled','-1'); //deshabilitar
						$descargar_pdf.attr('disabled','-1'); //deshabilitar
						$folio_salida.attr('disabled','-1'); //deshabilitar
						$campo_razoncliente.attr('disabled','-1'); //deshabilitar
						$folio_doc.attr('disabled','-1'); //deshabilitar
						$fecha_doc.attr('disabled','-1'); //deshabilitar
						$orden_compra.attr('disabled','-1'); //deshabilitar
						$folio_pedido.attr('disabled','-1'); //deshabilitar
						$campo_tc.attr('disabled','-1'); //deshabilitar
						$observaciones.attr('disabled','-1'); //deshabilitar
						
						$campo_subtotal.attr('disabled','-1'); //deshabilitar
						$campo_retencion.attr('disabled','-1'); //deshabilitar
						$campo_impuesto.attr('disabled','-1'); //deshabilitar
						$campo_total.attr('disabled','-1'); //deshabilitar
						
						$select_tipo_doc.attr('disabled','-1'); //deshabilitar
						$select_moneda.attr('disabled','-1'); //deshabilitar
						$select_almacen_origen.attr('disabled','-1'); //deshabilitar
						
						$grid_productos.find('a').hide();
						$grid_productos.find('input').attr('disabled','-1'); //deshabilitar
						$submit_actualizar.hide();
					}
					
				},"json");//termina llamada json
				
				
				
				
				$confirmar.click(function(e){
					$accion.attr({'value' : "confirmar"});
					jConfirm('Confirmar salida del almacen?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
							$submit_actualizar.parents("FORM").submit();
						}else{
							$accion.attr({'value' : "edit"});
						}
					});
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});
				
				
				
				
				
				//descargar pdf de Orden de Salida
				$descargar_pdf.click(function(event){
					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/get_genera_pdf_OrdenSalida/'+$identificador.val()+'/'+iu+'/out.json';
					window.location.href=input_json;
				});
				
				
				
				$('#forma-invcontrolcostos-window').find('input').keypress(function(e){
					if(e.which==13 ) {
						return false;
					}
				});
		
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-invcontrolcostos-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-invcontrolcostos-overlay').fadeOut(remove);
				});
			}
		}
	}
	
	
	$get_datos_grid = function(){
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllCostos.json';
		
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		
		$arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllCostos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
		$.post(input_json,$arreglo,function(data){
			//pinta_grid			
			//aqui se utiliza el mismo datagrid que prefacturas. Solo muesta icono de detalles, el de eliminar No
			$.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formainvcontrolcostos00_for_datagrid00);
			
			//resetea elastic, despues de pintar el grid y el slider
			Elastic.reset(document.getElementById('lienzo_recalculable'));
		},"json");
	}
	
    $get_datos_grid();
});



