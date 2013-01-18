$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
    //arreglo para select tipo de documento
    var array_select_documento = {
				1:"Factura", 
				2:"Remisi&oacute;n",
				3:"Factura&nbsp;de&nbsp;Remisi&oacute;n"
			};
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/prefacturas";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    
    var $new_prefactura = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//$new_prefactura.hide();
	
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseover(function(){
		$(this).removeClass("onmouseOutVisualizaBuscador").addClass("onmouseOverVisualizaBuscador");
	});
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseout(function(){
		$(this).removeClass("onmouseOverVisualizaBuscador").addClass("onmouseOutVisualizaBuscador");
	});
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Facturaci&oacute;n');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	var $cadena_busqueda = "";
	//var $busqueda_factura = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_factura]');
	var $busqueda_cliente = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
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
		//valor_retorno += "factura" + signo_separador + $busqueda_factura.val() + "|";
		valor_retorno += "cliente" + signo_separador + $busqueda_cliente.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val();
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
	
	$limpiar.click(function(event){
		$busqueda_factura.val('');
		$busqueda_cliente.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
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
		var $select_prod_tipo = $('#forma-prefacturas-window').find('select[name=prodtipo]');
		$('#forma-prefacturas-window').find('#submit').mouseover(function(){
			$('#forma-prefacturas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-prefacturas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		})
		$('#forma-prefacturas-window').find('#submit').mouseout(function(){
			$('#forma-prefacturas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-prefacturas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		})
		$('#forma-prefacturas-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-prefacturas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-prefacturas-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-prefacturas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-prefacturas-window').find('#close').mouseover(function(){
			$('#forma-prefacturas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		})
		$('#forma-prefacturas-window').find('#close').mouseout(function(){
			$('#forma-prefacturas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		})
		
		$('#forma-prefacturas-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-prefacturas-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-prefacturas-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-prefacturas-window').find("ul.pestanas li").click(function() {
			$('#forma-prefacturas-window').find(".contenidoPes").hide();
			$('#forma-prefacturas-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-prefacturas-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	
	//carga los campos select con los datos que recibe como parametro
	$carga_select_con_arreglo_fijo = function($campo_select, arreglo_elementos, elemento_seleccionado){
		$campo_select.children().remove();
		var select_html = '';
		for(var i in arreglo_elementos){
			if( parseInt(i) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + i + '" selected="yes">' + arreglo_elementos[i] + '</option>';
			}else{
				//3=Facturacion de Remisiones, solo debe mostrarse cuando se abra la ventana desde el Icono de Nuevo
				if(parseInt(elemento_seleccionado)==0  && parseInt(i)!=3 ){
					select_html += '<option value="' + i + '"  >' + arreglo_elementos[i] + '</option>';
				}
			}
		}
		$campo_select.append(select_html);
	}
	
	
	//buscador de clientes
	$busca_clientes = function($select_moneda,$select_condiciones,$select_vendedor, array_monedas, array_condiciones, array_vendedores ){
            //limpiar_campos_grids();
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
            
            var html = '';
            $select_filtro_por.children().remove();
            html='<option value="0">[-- Opcion busqueda --]</option>';
            html+='<option value="1">No. de control</option>';
            html+='<option value="2">RFC</option>';
            html+='<option value="3">Razon social</option>';
            html+='<option value="4">CURP</option>';
            html+='<option value="5">Alias</option>';
            $select_filtro_por.append(html);
			
			
			
            //click buscar clientes
            $busca_cliente_modalbox.click(function(event){
                //event.preventDefault();
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_clientes.json';
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
								trr += '<input type="hidden" id="cta_mn" value="'+cliente['cta_pago_mn']+'">';
								trr += '<input type="hidden" id="cta_usd" value="'+cliente['cta_pago_usd']+'">';
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
                        $('#forma-prefacturas-window').find('input[name=id_cliente]').val($(this).find('#idclient').val());
                        $('#forma-prefacturas-window').find('input[name=nocliente]').val($(this).find('span.no_control').html());
                        $('#forma-prefacturas-window').find('input[name=razoncliente]').val($(this).find('span.razon').html());
						$('#forma-prefacturas-window').find('input[name=empresa_immex]').val($(this).find('#emp_immex').val());
						$('#forma-prefacturas-window').find('input[name=tasa_ret_immex]').val($(this).find('#tasa_immex').val());
						$('#forma-prefacturas-window').find('input[name=cta_mn]').val($(this).find('#cta_mn').val());
						$('#forma-prefacturas-window').find('input[name=cta_usd]').val($(this).find('#cta_usd').val());						
						
						var id_moneda=$(this).find('#id_moneda').val();
						var id_termino=$(this).find('#terminos_id').val();
						var id_vendedor=$(this).find('#vendedor_id').val();
						
						//carga el select de monedas  con la moneda del cliente seleccionada por default
						$select_moneda.children().remove();
						var moneda_hmtl = '';
						$.each(array_monedas ,function(entryIndex,moneda){
							if( parseInt(moneda['id']) == parseInt(id_moneda) ){
								moneda_hmtl += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion'] + '</option>';
							}else{
								//moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
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
								//hmtl_condiciones += '<option value="' + condicion['id'] + '" >' + condicion['descripcion'] + '</option>';
							}
						});
						$select_condiciones.append(hmtl_condiciones);
						
						//carga select de vendedores
						$select_vendedor.children().remove();
						var hmtl_vendedor;
						$.each(array_vendedores,function(entryIndex,vendedor){
							if( parseInt(vendedor['id']) == parseInt(id_vendedor) ){
								hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes">' + vendedor['nombre_vendedor'] + '</option>';
							}else{
								//hmtl_vendedor += '<option value="' + vendedor['id'] + '" >' + vendedor['nombre_agente'] + '</option>';
							}
						});
						$select_vendedor.append(hmtl_vendedor);
						
                        
                        //elimina la ventana de busqueda
                        var remove = function() {$(this).remove();};
                        $('#forma-buscacliente-overlay').fadeOut(remove);
                        //asignar el enfoque al campo sku del producto
                    });
                });
            });//termina llamada json
			
            $cancelar_plugin_busca_cliente.click(function(event){
                //event.preventDefault();
                var remove = function() {$(this).remove();};
                $('#forma-buscacliente-overlay').fadeOut(remove);
            });
	}//termina buscador de clientes
	
	
	
	
	
	//buscador de productos
	$busca_productos = function(sku_buscar){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscaproducto();
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
			var prod_tipos_html = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
			$.each(data['prodTipos'],function(entryIndex,pt){
				prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
			});
			$select_tipo_producto.append(prod_tipos_html);
		});
		
		//Aqui asigno al campo sku del buscador si el usuario ingresó un sku antes de hacer clic en buscar en la ventana principal
		$campo_sku.val(sku_buscar);
		
		//click buscar productos
		$buscar_plugin_producto.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_productos.json';
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
					$('#forma-prefacturas-window').find('input[name=sku_producto]').val($(this).find('span.sku_prod_buscador').html());
					$('#forma-prefacturas-window').find('input[name=nombre_producto]').val($(this).find('span.titulo_prod_buscador').html());
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaproducto-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-prefacturas-window').find('input[name=sku_producto]').focus();
				});
				
			});//termina llamada json
		});
		
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if($campo_sku.val() != ''){
			$buscar_plugin_producto.trigger('click');
		}
		
		$cancelar_plugin_busca_producto.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscaproducto-overlay').fadeOut(remove);
		});
	}//termina buscador de productos
	
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Carga datos de la remision seleccionada al Grid de Productos de la factura
	$agrega_productos_remision_al_grid = function($grid_productos, $select_moneda,$select_metodo_pago, $folio_pedido, $orden_compra, $no_cuenta, id_remision, id_moneda_remision,  array_monedas, array_metodos_pago, id_alm){
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosRemision.json';
		$arreglo = {'id_remision':id_remision };
		
		
		$.post(input_json,$arreglo,function(entry){
			var trCount = $("tr", $grid_productos).size();
			var valor_orden_compra = $orden_compra.val();
			var valor_folio_pedido = $folio_pedido.val();
			var valor_folio_remision = entry['Datos']['0']['folio_remision'];
			
			if(parseInt(trCount) <= 0){
				//carga select denominacion con con la moneda de la primera remision seleccionada
				$select_moneda.children().remove();
				var moneda_hmtl = '';
				$.each(array_monedas,function(entryIndex,moneda){
					if(parseInt(moneda['id']) == parseInt(id_moneda_remision)){
						moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
					}else{
						//moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
					}
				});
				$select_moneda.append(moneda_hmtl);
				
				
				//carga select de metodos de pago
				$select_metodo_pago.children().remove();
				var hmtl_metodo;
				$.each(array_metodos_pago,function(entryIndex,metodo){
					if(parseInt(metodo['id']) == parseInt(entry['Datos']['0']['fac_metodos_pago_id'])){
						hmtl_metodo += '<option value="' + metodo['id'] + '" selected="yes">' + metodo['titulo'] + '</option>';
					}else{
						//hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
					}
				});
				$select_metodo_pago.append(hmtl_metodo);
				$no_cuenta.val(entry['Datos']['0']['no_cuenta']);
				
			}
			
			if( valor_orden_compra != null && valor_orden_compra != '' ){
				if( entry['Datos']['0']['orden_compra'] != null && entry['Datos']['0']['orden_compra'] !='' ){
					$orden_compra.val( valor_orden_compra + "," + entry['Datos']['0']['orden_compra']);
				}
			}else{
				$orden_compra.val(entry['Datos']['0']['orden_compra']);
			}
			
			
			if( valor_folio_pedido != null && valor_folio_pedido != '' ){
				if( entry['Datos']['0']['folio_pedido'] != null && entry['Datos']['0']['folio_pedido'] !='' ){
					$folio_pedido.val( valor_folio_pedido + "," + entry['Datos']['0']['folio_pedido']);
				}
			}else{
				$folio_pedido.val(entry['Datos']['0']['folio_pedido']);
			}
			
			
			
			if(entry['Conceptos'] != null){
				$.each(entry['Conceptos'],function(entryIndex,prod){
					//obtiene numero de trs
					var tr = $("tr", $grid_productos).size();
					tr++;
					
					var trr = '';
					trr = '<tr>';
					trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
							trr += '<a href="elimina_producto" id="delete'+ tr +'"></a>';
							trr += '<input type="hidden" name="id_almacen" id="id_alm" value="'+id_alm+'">';//id del almacen de donde se saco el producto remisionado
							trr += '<input type="hidden" name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
							trr += '<input type="hidden" name="iddetalle" id="idd" value="'+ prod['id_detalle'] +'">';//este es el id del registro que ocupa el producto en la tabla prefacturas_detalles
							trr += '<input type="hidden" name="id_remision" id="id_rem" value="'+ prod['id_remision'] +'">';//id de la  remision seleccionada
							trr += '<input type="text" 	name="remision" value="'+ valor_folio_remision +'" 	id="id_rem" class="borde_oculto" readOnly="true" style="width:60px;">';
							trr += '<input type="hidden" name="id_mon_rem" id="id_mon" value="'+ id_moneda_remision +'">';//id de la moneda de la remision
							trr += '<input type="hidden" name="costo_promedio" id="costprom" value="'+ prod['costo_prom'] +'">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="114">';
							trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ prod['producto_id'] +'">';
							trr += '<INPUT TYPE="text" name="sku'+ tr +'" value="'+ prod['codigo'] +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="202">';
						trr += '<INPUT TYPE="text" 	name="nombre'+ tr +'" 	value="'+ prod['titulo'] +'" 	id="nom" class="borde_oculto" readOnly="true" style="width:198px;">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
						trr += '<INPUT TYPE="text" 	name="unidad'+ tr +'" 	value="'+ prod['unidad'] +'" 	id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
							trr += '<INPUT type="hidden" 	name="id_presentacion"  value="'+  prod['id_presentacion'] +'" 	id="idpres">';
							trr += '<INPUT TYPE="text" 		name="presentacion'+ tr +'" 	value="'+  prod['presentacion'] +'" 	id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
						trr += '<INPUT TYPE="text" 	name="cantidad" value="'+  prod['cantidad'] +'" 		id="cant" style="width:76px;">';
					trr += '</td>';
					trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
						trr += '<INPUT TYPE="text" 	name="costo" 	value="'+  prod['precio_unitario'] +'" 	id="cost" style="width:86px; text-align:right;">';
						trr += '<INPUT type="hidden" value="'+  prod['precio_unitario'] +'" id="costor">';
					trr += '</td>';
					trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
						trr += '<INPUT TYPE="text" 	name="importe'+ tr +'" 	value="'+  prod['importe'] +'" 	id="import" readOnly="true" style="width:86px; text-align:right;">';
						trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="'+parseFloat(prod['importe']) * parseFloat(prod['valor_imp'])+'">';
						trr += '<INPUT type="hidden"    name="id_imp_prod"  value="'+  prod['gral_imp_id'] +'" id="idimppord">';
						trr += '<INPUT type="hidden"    name="valor_imp" 	value="'+  prod['valor_imp'] +'" 		id="ivalorimp">';
					trr += '</td>';
					trr += '</tr>';
					$grid_productos.append(trr);
					$grid_productos.find('a').hide();//ocultar
				   
				});
			}
			
			$calcula_totales();//llamada a la funcion que calcula totales 
		});
		
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	//buscador de Remisiones  sin facturar del cliente seleccionado
	$busca_remisiones = function($grid_productos, $select_moneda,$select_metodo_pago, $folio_pedido, $orden_compra, $no_cuenta, id_cliente, array_monedas, array_metodos_pago, $select_almacen, arrayAlmacenes ){
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRemisionesCliente.json';
		$arreglo = {'id_cliente':id_cliente	};
		
		var trr = '';
		
		$.post(input_json,$arreglo,function(entry){
				
				//verifica si el arreglo  retorno datos
				if (entry['Remisiones'].length > 0){
					$(this).modalPanel_buscaremision();
					var $dialogoc =  $('#forma-buscaremision-window');
					$dialogoc.append($('div.buscador_remisiones').find('table.formaBusqueda_remisiones').clone());
					$('#forma-buscaremision-window').css({"margin-left": -110, "margin-top": -150});
					
					var $tabla_resultados = $('#forma-buscaremision-window').find('#tabla_resultado');
					//var $cancelar_plugin_busca_lotes_producto = $('#forma-buscaremision-window').find('a[href*=cencela]');
					var $cancelar_busca_remisiones = $('#forma-buscaremision-window').find('#cencela');
					$tabla_resultados.children().remove();
					
					$cancelar_busca_remisiones.mouseover(function(){
						$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
					});
					$cancelar_busca_remisiones.mouseout(function(){
						$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
					});
					
					
					//crea el tr con los datos del producto seleccionado
					$.each(entry['Remisiones'],function(entryIndex,rem){
						trr = '<tr>';
							trr += '<td width="100">';
								trr += '<span class="id_rem" style="display:none">'+rem['id']+'</span>';
								trr += '<span class="id_alm" style="display:none">'+rem['id_almacen']+'</span>';
								trr += '<span class="folio">'+rem['folio']+'</span>';
							trr += '</td>';
							trr += '<td width="100"><span>'+rem['monto_remision']+'</span></td>';
							trr += '<td width="90">';
								trr += '<span class="id_mon" style="display:none">'+rem['moneda_id']+'</span>';
								trr += rem['moneda'];
							trr += '</td>';
							trr += '<td width="90">'+rem['fecha_remision']+'</td>';
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
					
					
					//seleccionar un remision del grid de resultados
					$tabla_resultados.find('tr').click(function(){
						//llamada a la funcion que busca los datos de la remision seleccionada y carga los datos en el grid de productos
						var id_rem = $(this).find('span.id_rem').html();
						var id_moneda = $(this).find('span.id_mon').html();
						var folio = $(this).find('span.folio').html();
						var id_alm = $(this).find('span.id_alm').html();
						
						var encontrado = 0;
						var moneda_diferente = 0;
						var almacen_diferente = 0;
						
						//busca el sku y la presentacion en el grid
						$grid_productos.find('tr').each(function (index){
							if(( $(this).find('#id_rem').val() == id_rem )){
								encontrado=1;//la remision ya se encuentra en el grid
							}
							
							if(( $(this).find('#id_mon').val() != id_moneda )){
								moneda_diferente=1;//la moneda es diferente a la que se encuentra en el grid
							}
							
							if(( $(this).find('#id_alm').val() != id_alm )){
								almacen_diferente=1;//el almacen es diferente a la que ya se encuentra en el grid
							}
						});
						
						if( parseInt(encontrado) != 1 ) {
							if( parseInt(moneda_diferente) != 1 ) {
								if( parseInt(almacen_diferente) != 1 ) {
									$agrega_productos_remision_al_grid($grid_productos, $select_moneda, $select_metodo_pago, $folio_pedido, $orden_compra, $no_cuenta, id_rem, id_moneda, array_monedas, array_metodos_pago, id_alm);
									
									//carga select de almacen con el almacen de donde se saco los productos de la remision
									$select_almacen.children().remove();
									var hmtl_alm='';
									$.each(arrayAlmacenes,function(entryIndex,alm){
										if(id_alm == alm['id']){
											hmtl_alm += '<option value="' + alm['id'] + '"  selected="yes">' + alm['titulo'] + '</option>';
										}
									});
									$select_almacen.append(hmtl_alm);
									
								}else{
									jAlert("No se puede mezclar productos remisionados de diferentes Almacenes.",'! Atencion');
								}
							}else{
								jAlert("No se puede mezclar remisiones de diferentes monedas.",'! Atencion');
							}
						}else{
							jAlert("La remisi&oacute;n ya fue seleccionado. Intente agregar una diferente.",'! Atencion');
						}
						
						//$nombre_producto.val(titulo);//muestra el titulo del producto en el campo nombre del producto de la ventana de cotizaciones
						
						//elimina la ventana de busqueda
						var remove = function() {$(this).remove();};
						$('#forma-buscaremision-overlay').fadeOut(remove);
					});
					
					$cancelar_busca_remisiones.click(function(event){
						//event.preventDefault();
						var remove = function() {$(this).remove();};
						$('#forma-buscaremision-overlay').fadeOut(remove);
					});
				}else{
					jAlert("El cliente seleccionado no tiene Remisiones pendientes de Facturar.\nSeleccione un cliente diferente y haga click en Agregar Remisiones.",'! Atencion');
					$('#forma-prefacturas-window').find('input[name=titulo_producto]').val('');
				}
		});
	}//termina buscador de remisiones del cliente
	
    
	
	
	//buscador de presentaciones disponibles para un producto
	$buscador_presentaciones_producto = function($id_cliente,no_cliente, sku_producto,$nombre_producto,$grid_productos,$select_moneda,$tipo_cambio){
		//verifica si el campo rfc proveedor no esta vacio
		if(no_cliente != ''){
                    //verifica si el campo sku no esta vacio para realizar busqueda
                    if(sku_producto != ''){
                        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_presentaciones_producto.json';
                        $arreglo = {'sku':sku_producto,
									'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
									};

                        var trr = '';
						
                        $.post(input_json,$arreglo,function(entry){
                                //verifica si el arreglo  retorno datos
                                if (entry['Presentaciones'].length > 0){
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
                                                trr += '<span class="dec" style="display:none">'+pres['decimales']+'</span>';
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
										//$agrega_producto_grid($(this).find('span.lote').html(),$(this).find('input.idalmacen').val());
										var id_prod = $(this).find('span.id_prod').html();
										var sku = $(this).find('span.sku').html();
										var titulo = $(this).find('span.titulo').html();
										var unidad = $(this).find('span.unidad').html();
										var id_pres = $(this).find('span.id_pres').html();
										var pres = $(this).find('span.pres').html();
										var num_dec = $(this).find('span.dec').html();
										
										var prec_unitario;
										var id_moneda=0;
										
										//cadena json para buscar si el producto ha sido cotizado para el cliente actual
										//si ha sido cotizado anteriormente, traer el precio_unitario
										var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_precio_unitario.json';
										$arreglo2 = {'id_cliente':$id_cliente.val(),
													'id_producto':id_prod,
													'id_pres':id_pres,
													'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
												};
										
										//aqui se pasan datos a la funcion que agrega el tr en el grid
										$.post(input_json2,$arreglo2,function(prod){
											if(prod['Pu']['precio_unitario']==""){
												//alert("No hay precio unitario:"+prec_unitario);
												$agrega_producto_grid($grid_productos,id_prod,sku,titulo,unidad,id_pres,pres," ",$select_moneda,id_moneda,$tipo_cambio,num_dec);
											}else{
												//alert("Si hay precio unitario:"+prec_unitario);
												prec_unitario = prod['Pu']['precio_unitario'];
												id_moneda = prod['Pu']['moneda_id'];
												$agrega_producto_grid($grid_productos,id_prod,sku,titulo,unidad,id_pres,pres,prec_unitario,$select_moneda,id_moneda,$tipo_cambio,num_dec);
											}
										});
										
										$nombre_producto.val(titulo);//muestra el titulo del producto en el campo nombre del producto de la ventana de cotizaciones
										
                                        //elimina la ventana de busqueda
                                        var remove = function() {$(this).remove();};
                                        $('#forma-buscapresentacion-overlay').fadeOut(remove);
                                    });
									
                                    $cancelar_plugin_busca_lotes_producto.click(function(event){
                                        //event.preventDefault();
                                        var remove = function() {$(this).remove();};
                                        $('#forma-buscapresentacion-overlay').fadeOut(remove);
                                    });
									
                                }else{
                                    jAlert("El producto que intenta agregar no existe, pruebe ingresando otro.\nHaga clic en Buscar.",'! Atencion');
                                    $('#forma-prefacturas-window').find('input[name=titulo_producto]').val('');
                                }
                        });
						
                    }else{
                            jAlert("Es necesario ingresar un Sku de producto valido", 'Atencion!');
                    }
		}else{
			jAlert("Es necesario seleccionar un Cliente", 'Atencion!');
		}
		
	}//termina buscador dpresentaciones disponibles de un producto
	
    
    
    //convertir costos en dolar y pesos
	$convertir_costos = function($tipo_cambio,moneda_id,$campo_subtotal,$campo_impuesto,$campo_total,$valor_impuesto,$grid_productos){
		var sumaSubTotal = 0; //es la suma de todos los importes
		var sumaImpuesto = 0; //valor del iva
		var sumaTotal = 0; //suma del subtotal + totalImpuesto
		var $moneda_original = $('#forma-prefacturas-window').find('input[name=moneda_original]');
		//si  el campo tipo de cambio es null o vacio, se le asigna un 0
		if( $valor_impuesto.val()== null || $valor_impuesto.val()== ''){
			$valor_impuesto.val(0);
		}
		
		$grid_productos.find('tr').each(function (index){
			var precio_cambiado=0;
			var importe_cambiado=0;
			if(( $(this).find('#cost').val() != ' ') && ( $(this).find('#cant').val() != ' ' )){
				if( parseInt($moneda_original.val()) != parseInt(moneda_id) ){
					if(parseInt($moneda_original.val())==1){
						//si la moneda original es pesos, calculamos su equivalente a dolares
						precio_cambiado = parseFloat(quitar_comas($(this).find('#costor').val())) / parseFloat($tipo_cambio.val());
					}else{
						//si la moneda original es dolar, calculamos su equivalente a pesos
						precio_cambiado = parseFloat(quitar_comas($(this).find('#costor').val())) * parseFloat($tipo_cambio.val());
					}
					
					$(this).find('#cost').val($(this).agregar_comas(parseFloat(precio_cambiado).toFixed(4)));
					//calcula el nuevo importe
					importe_cambiado = parseFloat($(this).find('#cant').val()) * parseFloat(precio_cambiado).toFixed(4);
					//asignamos el nuevo laor del importe
					$(this).find('#import').val($(this).agregar_comas(parseFloat(importe_cambiado).toFixed(4) ) );
				}else{
					//aqui entra si la moneda seleccionada es la moneda original. Le devolvemos al campo costo su valor original
					$(this).find('#cost').val( $(this).find('#costor').val()  );
					//calcula el nuevo importe
					importe_cambiado = parseFloat($(this).find('#cant').val()) * parseFloat($(this).find('#cost').val()).toFixed(2);
					//asignamos el nuevo laor del importe
					$(this).find('#import').val($(this).agregar_comas(parseFloat(importe_cambiado).toFixed(2) ) );
				}
				
				//acumula los importes en la variable subtotal
				sumaSubTotal = parseFloat(sumaSubTotal) + parseFloat(quitar_comas($(this).find('#import').val()));
				if($(this).find('#totimp').val() != ''){
					$(this).find('#totimp').val(parseFloat( quitar_comas($(this).find('#import').val()) ) * parseFloat($valor_impuesto.val()));
					sumaImpuesto =  parseFloat(sumaImpuesto) + parseFloat($(this).find('#totimp').val());
				}
			}
		});
		
		if( parseInt($moneda_original.val()) != parseInt(moneda_id) ){
			//calcula el total sumando el subtotal y el impuesto
			sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaImpuesto);
			//redondea a 4 digitos el  subtotal y lo asigna  al campo subtotal
			$campo_subtotal.val($(this).agregar_comas(parseFloat(sumaSubTotal).toFixed(4)));
			//redondea a 4 digitos el impuesto y lo asigna al campo impuesto
			$campo_impuesto.val($(this).agregar_comas(parseFloat(sumaImpuesto).toFixed(4)));
			//redondea a 4 digitos la suma  total y se asigna al campo total
			$campo_total.val($(this).agregar_comas(parseFloat(sumaTotal).toFixed(4)));
		}else{
			//calcula el total sumando el subtotal y el impuesto
			sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaImpuesto);
			//redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
			$campo_subtotal.val($(this).agregar_comas(parseFloat(sumaSubTotal).toFixed(2)));
			//redondea a dos digitos el impuesto y lo asigna al campo impuesto
			$campo_impuesto.val($(this).agregar_comas(parseFloat(sumaImpuesto).toFixed(2)));
			//redondea a dos digitos la suma  total y se asigna al campo total
			$campo_total.val($(this).agregar_comas(parseFloat(sumaTotal).toFixed(2)));
		}
	}//termina convertir dolar pesos
	
	
	
	//calcula totales(subtotal, impuesto, total)
	$calcula_totales = function(){
		var $campo_subtotal = $('#forma-prefacturas-window').find('input[name=subtotal]');
		var $campo_impuesto = $('#forma-prefacturas-window').find('input[name=impuesto]');
		var $campo_impuesto_retenido = $('#forma-prefacturas-window').find('input[name=impuesto_retenido]');
		var $campo_total = $('#forma-prefacturas-window').find('input[name=total]');
		var $empresa_immex = $('#forma-prefacturas-window').find('input[name=empresa_immex]');
		var $tasa_ret_immex = $('#forma-prefacturas-window').find('input[name=tasa_ret_immex]');
		
		var $grid_productos = $('#forma-prefacturas-window').find('#grid_productos');
		
		var sumaSubTotal = 0; //es la suma de todos los importes
		var sumaImpuesto = 0; //suma del iva
		var impuestoRetenido = 0; //monto del iva retenido de acuerdo a la tasa de retencion immex
		var sumaTotal = 0; //suma del subtotal + totalImpuesto
		
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
		
		//calcula el total sumando el subtotal y el impuesto
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
	
	
	
	
	
	
	//agregar producto al grid
	$agrega_producto_grid = function($grid_productos,id_prod,sku,titulo,unidad,id_pres,pres,prec_unitario,$select_moneda, id_moneda, $tipo_cambio,num_dec){
		var $valor_impuesto = $('#forma-prefacturas-window').find('input[name=valorimpuesto]');
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
		
		var pu;
		//prec_unitario trae el precio unitario del producto
		//id_moneda trae la moneda del precio unitario
		//$select_moneda.val() trae la moneda de la prefactura actual
		if(prec_unitario != " "){
			if(parseInt($select_moneda.val())==1){
				if(parseInt(id_moneda)==1){
					pu = prec_unitario;
				}else{
					if(parseInt(id_moneda)==2){
						pu = parseFloat(parseFloat(prec_unitario) * parseFloat($tipo_cambio.val())).toFixed(2);
					}
				}
			}else{
				if(parseInt($select_moneda.val())==2){
					if(parseInt(id_moneda)==1){
						pu = parseFloat(parseFloat(prec_unitario) / parseFloat($tipo_cambio.val())).toFixed(2);
					}else{
						if(parseInt(id_moneda)==2){
							pu = prec_unitario;
						}
					}
				}
			}
		}else{
			pu = prec_unitario;
		}
		
		
		
		if(parseInt(encontrado)!=1){//si el producto no esta en el grid entra aqui
			//ocultamos el boton facturar para permitir Guardar los cambios  antes de facturar
			$('#forma-prefacturas-window').find('#facturar').hide();
			//obtiene numero de trs
			var tr = $("tr", $grid_productos).size();
			tr++;
			
			var trr = '';
			trr = '<tr>';
				trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
					trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
					trr += '<input type="hidden" name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
					trr += '<input type="hidden" name="iddetalle" id="idd" value="0">';//este es el id del registro que ocupa el producto en la tabla prefacturas_detalles
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="114">';
					trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ id_prod +'">';
					trr += '<INPUT TYPE="text" name="sku'+ tr +'" value="'+ sku +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="202">';
					trr += '<INPUT TYPE="text" 	name="nombre'+ tr +'" 	value="'+ titulo +'" id="nom" class="borde_oculto" readOnly="true" style="width:198px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
					trr += '<INPUT TYPE="text" 	name="unidad'+ tr +'" 	value="'+ unidad +'" id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
					trr += '<INPUT type="hidden"    name="id_presentacion"        	value="'+  id_pres +'" id="idpres">';
					trr += '<INPUT type="hidden"    name="numero_decimales"        	value="'+  num_dec +'" id="numdec">';
					trr += '<INPUT TYPE="text" 		name="presentacion'+ tr +'"         value="'+  pres +'" id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
					trr += '<INPUT TYPE="text" 	name="cantidad" value=" " id="cant" style="width:76px;">';
				trr += '</td>';
				trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
					trr += '<INPUT TYPE="text" 	name="costo" 	value="'+ pu +'" id="cost" style="width:86px; text-align:right;">';
				trr += '</td>';
				trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
					trr += '<INPUT TYPE="text" 	name="importe'+ tr +'" 	value="" id="import" readOnly="true" style="width:86px; text-align:right;">';
					trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="0">';
				trr += '</td>';
			trr += '</tr>';
            
			$grid_productos.append(trr);
			
			
			//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
			$grid_productos.find('#cant').focus(function(e){
				if($(this).val() == ' '){
						$(this).val('');
				}
			});
			
			//recalcula importe al perder enfoque el campo cantidad
			//$grid_productos.find('input[name=cantidad['+ tr +']]').blur(function(){
			$grid_productos.find('#cant').blur(function(){
				if ($(this).val() == ''){
					$(this).val(' ');
				}
				if( ($(this).val() != ' ') && ($(this).parent().parent().find('#cost').val() != ' ') )
				{	//calcula el importe
					$(this).parent().parent().find('#import').val(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cost').val()));
					//redondea el importe en dos decimales
					$(this).parent().parent().find('#import').val(Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100);
					
					//calcula el impuesto para este producto multiplicando el importe por el valor del iva
					$(this).parent().parent().find('#totimp').val(parseFloat($(this).parent().parent().find('#import').val()) * parseFloat($valor_impuesto.val()));
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
				//$grid_productos.find('input[name=costo['+ tr +']]').blur(function(){
				if( ($(this).val() != ' ') && ($(this).parent().parent().find('#cant').val() != ' ') )
				{	//calcula el importe
					$(this).parent().parent().find('#import').val(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cant').val()));
					//redondea el importe en dos decimales
					$(this).parent().parent().find('#import').val(Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100);
					
					//calcula el impuesto para este producto multiplicando el importe por el valor del iva
					$(this).parent().parent().find('#totimp').val(parseFloat($(this).parent().parent().find('#import').val()) * parseFloat($valor_impuesto.val()));
				}else{
					$(this).parent().parent().find('#import').val('');
					$(this).parent().parent().find('#totimp').val('');
				}
				$calcula_totales();//llamada a la funcion que calcula totales
			});
			
			//validar campo costo, solo acepte numeros y punto
			//$grid_productos.find('input[name=costo['+ tr +']]').keypress(function(e){
			$grid_productos.find('#cost').keypress(function(e){
				// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
				if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
					return true;
				}else {
					return false;
				}
			});
			
			//validar campo cantidad, solo acepte numeros y punto
			//$grid_productos.find('input[name=cantidad['+ tr +']]').keypress(function(e){
			$grid_productos.find('#cant').keypress(function(e){
				// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
				if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
					return true;
				}else {
					return false;
				}		
			});
			
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
			
		}else{
			jAlert("El producto: "+sku+" con presentacion: "+pres+" ya se encuentra en el listado, seleccione otro diferente.", 'Atencion!');
		}
		
	}//termina agregar producto al grid
	
	
	
	
	
	
	
	
	
	
	
	
	
	//nueva prefactura
	$new_prefactura.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_prefacturas();
		
		var form_to_show = 'formaPrefacturas00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$('#forma-prefacturas-window').css({"margin-left": -340, 	"margin-top": -220});
		
		$forma_selected.prependTo('#forma-prefacturas-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		//var json_string = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + accion + '/' + id_to_show + '/out.json';
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPrefactura.json';
		$arreglo = {'id_prefactura':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
        
        var $folio_pedido = $('#forma-prefacturas-window').find('input[name=folio_pedido]');
        var $select_tipo_documento = $('#forma-prefacturas-window').find('select[name=select_tipo_documento]');
		var $id_prefactura = $('#forma-prefacturas-window').find('input[name=id_prefactura]');
		var $refacturar = $('#forma-prefacturas-window').find('input[name=refacturar]');
		var $accion = $('#forma-prefacturas-window').find('input[name=accion]');		
		var $total_tr = $('#forma-prefacturas-window').find('input[name=total_tr]');
		
		var $busca_cliente = $('#forma-prefacturas-window').find('a[href*=busca_cliente]');
		var $id_cliente = $('#forma-prefacturas-window').find('input[name=id_cliente]');
		var $no_cliente = $('#forma-prefacturas-window').find('input[name=nocliente]');
		var $razon_cliente = $('#forma-prefacturas-window').find('input[name=razoncliente]');
		var $dir_cliente = $('#forma-prefacturas-window').find('input[name=dircliente]');
		var $select_moneda = $('#forma-prefacturas-window').find('select[name=moneda]');
		var $moneda_original = $('#forma-prefacturas-window').find('input[name=moneda_original]');
		var $tipo_cambio = $('#forma-prefacturas-window').find('input[name=tipo_cambio]');
		var $tasa_ret_immex = $('#forma-prefacturas-window').find('input[name=tasa_ret_immex]');
		var $empresa_immex = $('#forma-prefacturas-window').find('input[name=empresa_immex]');
		
		var $id_impuesto = $('#forma-prefacturas-window').find('input[name=id_impuesto]');
		var $valor_impuesto = $('#forma-prefacturas-window').find('input[name=valorimpuesto]');
		
		var $observaciones = $('#forma-prefacturas-window').find('textarea[name=observaciones]');
		
		var $select_condiciones = $('#forma-prefacturas-window').find('select[name=condiciones]');
		var $select_vendedor = $('#forma-prefacturas-window').find('select[name=vendedor]');
		var $orden_compra = $('#forma-prefacturas-window').find('input[name=orden_compra]');
		
		var $select_metodo_pago = $('#forma-prefacturas-window').find('select[name=select_metodo_pago]');
		var $etiqueta_digit = $('#forma-prefacturas-window').find('input[name=digit]');
		var $digitos_original = $('#forma-prefacturas-window').find('input[name=digitos_original]');
		var $no_cuenta = $('#forma-prefacturas-window').find('input[name=no_cuenta]');
		var $cta_mn = $('#forma-prefacturas-window').find('input[name=cta_mn]');
		var $cta_usd = $('#forma-prefacturas-window').find('input[name=cta_usd]');
		var $select_almacen = $('#forma-prefacturas-window').find('select[name=select_almacen]');
		
		
		
		
		//var $sku_producto = $('#forma-prefacturas-window').find('input[name=sku_producto]');
		//var $nombre_producto = $('#forma-prefacturas-window').find('input[name=nombre_producto]');
		
		
		//buscar producto
		//var $busca_sku = $('#forma-prefacturas-window').find('a[href*=busca_sku]');
		//href para agregar producto al grid
		//var $agregar_producto = $('#forma-prefacturas-window').find('a[href*=agregar_producto]');
		
		
		var $boton_facturar = $('#forma-prefacturas-window').find('#facturar');
		//var $boton_descargarpdf = $('#forma-prefacturas-window').find('#descargarpdf');
		//var $boton_cancelarfactura = $('#forma-prefacturas-window').find('#cancelarfactura');
		//var $boton_descargarxml = $('#forma-prefacturas-window').find('#descargarxml');
		
		
		//busca remisiones para agregar y facturar
		var $agregar_remision = $('#forma-prefacturas-window').find('a[href*=agregar_remision]');
		
		//grid de productos
		var $grid_productos = $('#forma-prefacturas-window').find('#grid_productos');
		var $titulo_delete = $('#forma-prefacturas-window').find('.titulo_delete');
		var $titulo_remision = $('#forma-prefacturas-window').find('#titulo_remision');
		
		
		
		//grid de errores
		var $grid_warning = $('#forma-prefacturas-window').find('#div_warning_grid').find('#grid_warning');
		
		
		//var $flete = $('#forma-prefacturas-window').find('input[name=flete]');
		var $subtotal = $('#forma-prefacturas-window').find('input[name=subtotal]');
		var $impuesto = $('#forma-prefacturas-window').find('input[name=impuesto]');
		var $total = $('#forma-prefacturas-window').find('input[name=total]');
		
		var $cerrar_plugin = $('#forma-prefacturas-window').find('#close');
		var $cancelar_plugin = $('#forma-prefacturas-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-prefacturas-window').find('#submit');
		
		
		$id_prefactura.val(0);//para nueva cotizacion el folio es 0
		
		//$campo_factura.css({'background' : '#ffffff'});
		
		//ocultar boton de facturar y descargar pdf. Solo debe estar activo en editar
		$boton_facturar.hide();
		$titulo_delete.hide();
		//$boton_descargarpdf.hide();
		//$boton_cancelarfactura.hide();
		//$boton_descargarxml.hide();
		$refacturar.val('false');
		$accion.val('new');
		$etiqueta_digit.attr('disabled','-1');
		$folio_pedido.css({'background' : '#F0F0F0'});
		$no_cliente.css({'background' : '#F0F0F0'});
		$razon_cliente.css({'background' : '#F0F0F0'});
		$dir_cliente.css({'background' : '#F0F0F0'});
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La prefactura se guard&oacute; con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-prefacturas-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				//$('#forma-prefacturas-window').find('.div_one').css({'height':'545px'});//sin errores
				$('#forma-prefacturas-window').find('.prefacturas_div_one').css({'height':'578px'});//con errores
				$('#forma-prefacturas-window').find('div.interrogacion').css({'display':'none'});

				$grid_productos.find('#cant').css({'background' : '#ffffff'});
				$grid_productos.find('#cost').css({'background' : '#ffffff'});

				$('#forma-prefacturas-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-prefacturas-window').find('#div_warning_grid').find('#grid_warning').children().remove();

				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-prefacturas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						//alert(tmp.split(':')[0]);
						
						if(parseInt($("tr", $grid_productos).size())>0){
							for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
								if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='costo'+i)){
									//alert(tmp.split(':')[0]);
									$('#forma-prefacturas-window').find('.prefacturas_div_one').css({'height':'578px'});
									//$('#forma-prefacturas-window').find('.div_three').css({'height':'910px'});
									
									$('#forma-prefacturas-window').find('#div_warning_grid').css({'display':'block'});
									if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
										$grid_productos.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
										//alert();
									}else{
										if(tmp.split(':')[0].substring(0, 5) == 'costo'){
											$grid_productos.find('input[name=costo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
										}
									}
									
									//$grid_productos.find('input[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});
									//$grid_productos.find('select[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});

									var tr_warning = '<tr>';
										tr_warning += '<td width="20"><div><IMG SRC="../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
										tr_warning += '<td width="120">';
										tr_warning += '<INPUT TYPE="text" value="'+$grid_productos.find('input[name=sku' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:116px; color:red">';
										tr_warning += '</td>';
										tr_warning += '<td width="200">';
										tr_warning += '<INPUT TYPE="text" value="'+$grid_productos.find('input[name=nombre' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:196px; color:red">';
										tr_warning += '</td>';
										tr_warning += '<td width="235">';
										tr_warning += '<INPUT TYPE="text" value="'+ tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:230px; color:red">';
										tr_warning += '</td>';
									tr_warning += '</tr>';
									$grid_warning.append(tr_warning);
								}
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
			//$campo_tc.val(entry['tc']['tipo_cambio']);
			$id_impuesto.val(entry['iva']['0']['id_impuesto']);
			$valor_impuesto.val(entry['iva']['0']['valor_impuesto']);
			$tipo_cambio.val(entry['Tc']['0']['tipo_cambio']);
			
                    
			//cargar select de tipos de movimiento
			var elemento_seleccionado = 3;//Facturacion de Remisiones
			$carga_select_con_arreglo_fijo($select_tipo_documento, array_select_documento, elemento_seleccionado);
			
			
			//carga select denominacion con todas las monedas
			$select_moneda.children().remove();
			var moneda_hmtl = '';
			$.each(entry['Monedas'],function(entryIndex,moneda){
				moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
			});
			$select_moneda.append(moneda_hmtl);
			
			
			//carga select de vendedores
			$select_vendedor.children().remove();
			var hmtl_vendedor='';
			$.each(entry['Vendedores'],function(entryIndex,vendedor){
				hmtl_vendedor += '<option value="' + vendedor['id'] + '"  >' + vendedor['nombre_vendedor'] + '</option>';
			});
			$select_vendedor.append(hmtl_vendedor);
			
			
			//carga select de terminos
			$select_condiciones.children().remove();
			var hmtl_condiciones='';
			$.each(entry['Condiciones'],function(entryIndex,condicion){
				hmtl_condiciones += '<option value="' + condicion['id'] + '"  >' + condicion['descripcion'] + '</option>';
			});
			$select_condiciones.append(hmtl_condiciones);
			
			//carga select de metodos de pago
			$select_metodo_pago.children().remove();
			var hmtl_metodo='';
			$.each(entry['MetodosPago'],function(entryIndex,metodo){
				hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
			});
			$select_metodo_pago.append(hmtl_metodo);
			
			
			//carga select de almacen de la venta
			$select_almacen.children().remove();
			var hmtl_alm='';
			$.each(entry['Almacenes'],function(entryIndex,alm){
				hmtl_alm += '<option value="' + alm['id'] + '"  selected="yes">' + alm['titulo'] + '</option>';
			});
			$select_almacen.append(hmtl_alm);
			
			
			//buscador de clientes
			$busca_cliente.click(function(event){
				event.preventDefault();
				$busca_clientes( $select_moneda,$select_condiciones,$select_vendedor, entry['Monedas'], entry['Condiciones'],entry['Vendedores'] );
			});
			
			
			//buscador de remisiones para agregar al grid
			$agregar_remision.click(function(event){
				event.preventDefault();
				if(parseInt($id_cliente.val()) != 0){
					
					$busca_remisiones($grid_productos, $select_moneda,$select_metodo_pago, $folio_pedido, $orden_compra, $no_cuenta, $id_cliente.val(), entry['Monedas'], entry['MetodosPago'], $select_almacen, entry['Almacenes'] );
				}else{
					jAlert("Es necesario seleccionar un Cliente", 'Atencion!');
				}
			});
			
			
		},"json");//termina llamada json
		
		
		
		
		$tipo_cambio.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		
		/*
		//buscador de productos
		$busca_sku.click(function(event){
			event.preventDefault();
			//if(parseInt($select_almacen.val()) != 0){
				$busca_productos($sku_producto.val());
			//}else{
			//	jAlert("Es necesario seleccionar un almacen", 'Atencion!');
			//}
		});
		
		//agregar producto al grid
		$agregar_producto.click(function(event){
			event.preventDefault();
			//if($sku_producto.val() != ''){
				//$agrega_producto_grid($sku_producto,$grid_productos);
				$buscador_presentaciones_producto($id_cliente,$no_cliente.val(), $sku_producto.val(),$nombre_producto,$grid_productos,$select_moneda,$tipo_cambio);
			//}else{
			//	jAlert("Es necesario ingresar un sku valido", '¡ Atencion !');
			//}
			
		});
		
		
		
		
		//desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
		$sku_producto.keypress(function(e){
			if(e.which == 13){
				$agregar_producto.trigger('click');
				return false;
			}
		});
		*/
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_productos).size();
			$total_tr.val(trCount);
			if(parseInt(trCount) > 0){
				$subtotal.val(quitar_comas($subtotal.val()));
				$impuesto.val(quitar_comas($impuesto.val()));
				$total.val(quitar_comas($total.val()));
				return true;
			}else{
				jAlert("No hay datos para actualizar", 'Atencion!');
				return false;
			}
		});
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-prefacturas-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-prefacturas-overlay').fadeOut(remove);
		});
		
	});//termina nueva prefactura
	
	
	
	
	
	
	
	
	
	
	
	
	
	var carga_formaPrefacturas00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una prefactura
		if(accion_mode == 'cancel'){
			
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id_prefactura':id_to_show,
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
			$('#forma-prefacturas-window').remove();
			$('#forma-prefacturas-overlay').remove();
            
			var form_to_show = 'formaPrefacturas00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_prefacturas();
			
			$('#forma-prefacturas-window').css({"margin-left": -340, 	"margin-top": -220});
			
			$forma_selected.prependTo('#forma-prefacturas-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPrefactura.json';
				$arreglo = {'id_prefactura':id_to_show,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var $select_tipo_documento = $('#forma-prefacturas-window').find('select[name=select_tipo_documento]');
				var $folio_pedido = $('#forma-prefacturas-window').find('input[name=folio_pedido]');
				var $total_tr = $('#forma-prefacturas-window').find('input[name=total_tr]');
				var $id_prefactura = $('#forma-prefacturas-window').find('input[name=id_prefactura]');
				var $refacturar = $('#forma-prefacturas-window').find('input[name=refacturar]');
				var $accion = $('#forma-prefacturas-window').find('input[name=accion]');
				
				var $busca_cliente = $('#forma-prefacturas-window').find('a[href*=busca_cliente]');
				var $id_cliente = $('#forma-prefacturas-window').find('input[name=id_cliente]');
				var $no_cliente = $('#forma-prefacturas-window').find('input[name=nocliente]');
				var $razon_cliente = $('#forma-prefacturas-window').find('input[name=razoncliente]');
				var $dir_cliente = $('#forma-prefacturas-window').find('input[name=dircliente]');
				var $tasa_ret_immex = $('#forma-prefacturas-window').find('input[name=tasa_ret_immex]');
				var $empresa_immex = $('#forma-prefacturas-window').find('input[name=empresa_immex]');
				
				var $select_moneda = $('#forma-prefacturas-window').find('select[name=moneda]');
				var $moneda_original = $('#forma-prefacturas-window').find('input[name=moneda_original]');
				var $tipo_cambio = $('#forma-prefacturas-window').find('input[name=tipo_cambio]');
				var $tipo_tipo_cambio_original = $('#forma-prefacturas-window').find('input[name=tipo_cambio_original]');
				var $orden_compra = $('#forma-prefacturas-window').find('input[name=orden_compra]');
				var	$orden_compra_original = $('#forma-prefacturas-window').find('input[name=orden_compra_original]');
				
				var $select_metodo_pago = $('#forma-prefacturas-window').find('select[name=select_metodo_pago]');
				var $select_metodo_pago_original = $('#forma-prefacturas-window').find('select[name=select_metodo_pago_original]');
				var $etiqueta_digit = $('#forma-prefacturas-window').find('input[name=digit]');
				var $digitos_original = $('#forma-prefacturas-window').find('input[name=digitos_original]');
				var $no_cuenta = $('#forma-prefacturas-window').find('input[name=no_cuenta]');
				var $cta_mn = $('#forma-prefacturas-window').find('input[name=cta_mn]');
				var $cta_usd = $('#forma-prefacturas-window').find('input[name=cta_usd]');
				
				var $id_impuesto = $('#forma-prefacturas-window').find('input[name=id_impuesto]');
				var $valor_impuesto = $('#forma-prefacturas-window').find('input[name=valorimpuesto]');
				
				var $observaciones = $('#forma-prefacturas-window').find('textarea[name=observaciones]');
				var $select_condiciones = $('#forma-prefacturas-window').find('select[name=condiciones]');
				var $select_vendedor = $('#forma-prefacturas-window').find('select[name=vendedor]');
				var $observaciones_original = $('#forma-prefacturas-window').find('textarea[name=observaciones_original]');
				var $select_condiciones_original = $('#forma-prefacturas-window').find('select[name=condiciones_original]');
				var $select_vendedor_original = $('#forma-prefacturas-window').find('select[name=vendedor_original]');
				var $select_almacen = $('#forma-prefacturas-window').find('select[name=select_almacen]');
				
				
				//var $select_almacen = $('#forma-prefacturas-window').find('select[name=almacen]');
				//var $sku_producto = $('#forma-prefacturas-window').find('input[name=sku_producto]');
				//var $nombre_producto = $('#forma-prefacturas-window').find('input[name=nombre_producto]');
				
				//buscar producto
				//var $busca_sku = $('#forma-prefacturas-window').find('a[href*=busca_sku]');
				//href para agregar producto al grid
				//var $agregar_producto = $('#forma-prefacturas-window').find('a[href*=agregar_producto]');
				
				var $boton_facturar = $('#forma-prefacturas-window').find('#facturar');
				
				var $agregar_remision = $('#forma-prefacturas-window').find('a[href*=agregar_remision]');
				
				//grid de productos
				var $grid_productos = $('#forma-prefacturas-window').find('#grid_productos');
				var $titulo_delete = $('#forma-prefacturas-window').find('.titulo_delete');
				var $titulo_remision = $('#forma-prefacturas-window').find('#titulo_remision');
				
				//grid de errores
				var $grid_warning = $('#forma-prefacturas-window').find('#div_warning_grid').find('#grid_warning');
				
				var $subtotal = $('#forma-prefacturas-window').find('input[name=subtotal]');
				var $impuesto = $('#forma-prefacturas-window').find('input[name=impuesto]');
				var $total = $('#forma-prefacturas-window').find('input[name=total]');
				
				var $cerrar_plugin = $('#forma-prefacturas-window').find('#close');
				var $cancelar_plugin = $('#forma-prefacturas-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-prefacturas-window').find('#submit');
				
				//ocultar boton actualizar porque ya esta facturado, ya no se puede guardar cambios
				$submit_actualizar.hide();
				$titulo_remision.hide();
				$agregar_remision.hide();
				
				$etiqueta_digit.attr('disabled','-1');
				
				$refacturar.val('');
				$boton_facturar.hide();
				$accion.val('actualizar');
				
				$folio_pedido.css({'background' : '#F0F0F0'});
				$no_cliente.css({'background' : '#F0F0F0'});
				$razon_cliente.css({'background' : '#F0F0F0'});
				$dir_cliente.css({'background' : '#F0F0F0'});
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						$('#forma-prefacturas-window').find('div.interrogacion').css({'display':'none'});
						
						//factura
						if( parseInt($select_tipo_documento.val()) == 1 ){
							
							jAlert("Se gener&oacute; la Factura: "+data['folio'], 'Atencion!');
							
							$no_cliente.attr('disabled','-1'); //deshabilitar
							$razon_cliente.attr('disabled','-1'); //deshabilitar
							$observaciones.attr('disabled','-1'); //deshabilitar
							$select_moneda.attr('disabled','-1'); //deshabilitar
							$tipo_cambio.attr('disabled','-1'); //deshabilitar
							$select_vendedor.attr('disabled','-1'); //deshabilitar
							$select_condiciones.attr('disabled','-1'); //deshabilitar
							//$sku_producto.attr('disabled','-1'); //deshabilitar
							//$nombre_producto.attr('disabled','-1'); //deshabilitar
							$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
							$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid
							$grid_productos.find('#cant').attr('disabled','-1'); //deshabilitar
							$grid_productos.find('#cost').attr('disabled','-1'); //deshabilitar
							$grid_productos.find('a').hide();//ocultar
							$orden_compra.attr('disabled','-1'); //deshabilitar
							$select_metodo_pago.attr('disabled','-1'); //deshabilitar
							$select_metodo_pago_original.attr('disabled','-1'); //deshabilitar
							$select_almacen.attr('disabled','-1'); //deshabilitar
							//$busca_sku.hide();
							//$agregar_producto.hide();
							$boton_facturar.hide();
							
						}
						
						
						//remision
						if( parseInt($select_tipo_documento.val()) == 2 ){
							
							jAlert("Se gener&oacute; la Remisi&oacute;n con Folio: "+data['folio'], 'Atencion!');
							
							$no_cliente.attr('disabled','-1'); //deshabilitar
							$razon_cliente.attr('disabled','-1'); //deshabilitar
							$observaciones.attr('disabled','-1'); //deshabilitar
							$select_moneda.attr('disabled','-1'); //deshabilitar
							$tipo_cambio.attr('disabled','-1'); //deshabilitar
							$select_vendedor.attr('disabled','-1'); //deshabilitar
							$select_condiciones.attr('disabled','-1'); //deshabilitar
							//$sku_producto.attr('disabled','-1'); //deshabilitar
							//$nombre_producto.attr('disabled','-1'); //deshabilitar
							$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
							$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid
							$grid_productos.find('#cant').attr('disabled','-1'); //deshabilitar
							$grid_productos.find('#cost').attr('disabled','-1'); //deshabilitar
							$grid_productos.find('a').hide();//ocultar
							$orden_compra.attr('disabled','-1'); //deshabilitar
							$select_metodo_pago.attr('disabled','-1'); //deshabilitar
							$select_metodo_pago_original.attr('disabled','-1'); //deshabilitar
							$select_almacen.attr('disabled','-1'); //deshabilitar
							
							//$busca_sku.hide();
							//$agregar_producto.hide();
							$boton_facturar.hide();
						}
						
						
						
						//guardar nueva prefactura generada con datos de remisiones
						if( parseInt($select_tipo_documento.val()) == 3 ){
							//ocultar boton actualizar porque ya se actualizo, ya no se puede guardar cambios, hay que cerrar y volver a abrir
							$submit_actualizar.hide();
							$get_datos_grid();
							if($accion.val() == 'actualizar'){
								jAlert("Se gener&oacute; la Factura: "+data['folio'], 'Atencion!');
								
								$no_cliente.attr('disabled','-1'); //deshabilitar
								$razon_cliente.attr('disabled','-1'); //deshabilitar
								$observaciones.attr('disabled','-1'); //deshabilitar
								$select_moneda.attr('disabled','-1'); //deshabilitar
								$tipo_cambio.attr('disabled','-1'); //deshabilitar
								$select_vendedor.attr('disabled','-1'); //deshabilitar
								$select_condiciones.attr('disabled','-1'); //deshabilitar
								//$sku_producto.attr('disabled','-1'); //deshabilitar
								//$nombre_producto.attr('disabled','-1'); //deshabilitar
								$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
								$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid
								$grid_productos.find('#cant').attr('disabled','-1'); //deshabilitar
								$grid_productos.find('#cost').attr('disabled','-1'); //deshabilitar
								$grid_productos.find('a').hide();//ocultar
								$orden_compra.attr('disabled','-1'); //deshabilitar
								$select_metodo_pago.attr('disabled','-1'); //deshabilitar
								$select_metodo_pago_original.attr('disabled','-1'); //deshabilitar
								$select_almacen.attr('disabled','-1'); //deshabilitar
								
								//$busca_sku.hide();
								//$agregar_producto.hide();
								$boton_facturar.hide();
								
							}else{
								
								jAlert("La prefactura se guard&oacute; con &eacute;xito", 'Atencion!');
							}
							
							
						}
						
						$get_datos_grid();
						var remove = function() {$(this).remove();};
						$('#forma-prefacturas-overlay').fadeOut(remove);
						
						
						
					}else{
						// Desaparece todas las interrogaciones si es que existen
						//$('#forma-prefacturas-window').find('.div_one').css({'height':'550px'});//sin errores
						$('#forma-prefacturas-window').find('.prefacturas_div_one').css({'height':'578px'});//con errores
						$('#forma-prefacturas-window').find('div.interrogacion').css({'display':'none'});
						
						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});
						
						$('#forma-prefacturas-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-prefacturas-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							
							if( longitud.length > 1 ){
								$('#forma-prefacturas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								//alert(tmp.split(':')[0]);
								
								if(parseInt($("tr", $grid_productos).size())>0){
									for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
										if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='costo'+i)){
											$('#forma-prefacturas-window').find('.prefacturas_div_one').css({'height':'578px'});
											$('#forma-prefacturas-window').find('#div_warning_grid').css({'display':'block'});
											
											if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
												$grid_productos.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
												//alert();
											}else{
												if(tmp.split(':')[0].substring(0, 5) == 'costo'){
													$grid_productos.find('input[name=costo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
												}
											}
											
											var tr_warning = '<tr>';
												tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
												tr_warning += '<td width="120">';
												tr_warning += '<INPUT TYPE="text" value="'+$grid_productos.find('input[name=sku' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:116px; color:red">';
												tr_warning += '</td>';
												tr_warning += '<td width="200">';
												tr_warning += '<INPUT TYPE="text" value="'+$grid_productos.find('input[name=nombre' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:196px; color:red">';
												tr_warning += '</td>';
												tr_warning += '<td width="235">';
												tr_warning += '<INPUT TYPE="text" value="'+ tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:230px; color:red">';
												tr_warning += '</td>';
											tr_warning += '</tr>';
											$grid_warning.append(tr_warning);
										}
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
					var flujo_proceso = entry['datosPrefactura']['0']['proceso_flujo_id'];
					$folio_pedido.val(entry['datosPrefactura']['0']['folio_pedido']);
					
					$id_prefactura.val(entry['datosPrefactura']['0']['id']);
					$refacturar.val(entry['datosPrefactura']['0']['refacturar']);
					$id_cliente.val(entry['datosPrefactura']['0']['cliente_id']);
					$no_cliente.val(entry['datosPrefactura']['0']['numero_control']);
					$razon_cliente.val(entry['datosPrefactura']['0']['razon_social']);
					$observaciones.text(entry['datosPrefactura']['0']['observaciones']);
					$observaciones_original.val(entry['datosPrefactura']['0']['observaciones']);
                    $orden_compra.val(entry['datosPrefactura']['0']['orden_compra']);
                    $orden_compra_original.val(entry['datosPrefactura']['0']['orden_compra']);
                    $tasa_ret_immex.val(entry['datosPrefactura']['0']['tasa_retencion_immex']);
                    $empresa_immex.val(entry['datosPrefactura']['0']['empresa_immex']);
                    
					$cta_mn.val(entry['datosPrefactura']['0']['cta_pago_mn']);
					$cta_usd.val(entry['datosPrefactura']['0']['cta_pago_usd']);
					
					$no_cuenta.val(entry['datosPrefactura']['0']['no_cuenta']);
                    
					//cargar select de tipos de movimiento
					var elemento_seleccionado = entry['datosPrefactura']['0']['tipo_documento'];
					$carga_select_con_arreglo_fijo($select_tipo_documento, array_select_documento, elemento_seleccionado);
					
                    
					//carga select denominacion con todas las monedas
					$select_moneda.children().remove();
					//var moneda_hmtl = '<option value="0">[--   --]</option>';
					var moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(moneda['id'] == entry['datosPrefactura']['0']['moneda_id']){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
							$moneda_original.val(moneda['id']);
						}else{
							if(parseInt(flujo_proceso)==2){
								moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
							}
						}
					});
					$select_moneda.append(moneda_hmtl);
					
                    
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
						if(entry['datosPrefactura']['0']['empleado_id'] == vendedor['id']){
							hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes" >' + vendedor['nombre_vendedor'] + '</option>';
						}else{
							/*
							if(parseInt(flujo_proceso)==2){
								hmtl_vendedor += '<option value="' + vendedor['id'] + '">' + vendedor['nombre_vendedor'] + '</option>';
							}
							*/
						}
					});
					$select_vendedor.append(hmtl_vendedor);
					$select_vendedor.find('option').clone().appendTo($select_vendedor_original);
					
					
					//carga select de condiciones
					$select_condiciones.children().remove();
					var hmtl_condiciones;
					$.each(entry['Condiciones'],function(entryIndex,condicion){
						if(entry['datosPrefactura']['0']['terminos_id'] == condicion['id']){
							hmtl_condiciones += '<option value="' + condicion['id'] + '" selected="yes" >' + condicion['descripcion'] + '</option>';
						}else{
							/*
							if(parseInt(flujo_proceso)==2){
								hmtl_condiciones += '<option value="' + condicion['id'] + '">' + condicion['descripcion'] + '</option>';
							}
							*/
						}
					});
					$select_condiciones.append(hmtl_condiciones);
					$select_condiciones.find('option').clone().appendTo($select_condiciones_original);
					
					
					var valor_metodo = entry['datosPrefactura']['0']['fac_metodos_pago_id'];
					
					//carga select de metodos de pago
					$select_metodo_pago.children().remove();
					var hmtl_metodo;
					$.each(entry['MetodosPago'],function(entryIndex,metodo){
						if(valor_metodo == metodo['id']){
							hmtl_metodo += '<option value="' + metodo['id'] + '"  selected="yes">' + metodo['titulo'] + '</option>';
						}else{
							/*
							if(parseInt(flujo_proceso)==2){
								hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
							}
							*/
						}
					});
					$select_metodo_pago.append(hmtl_metodo);
					$select_metodo_pago.find('option').clone().appendTo($select_metodo_pago_original);
					
					
					var valor_id_almacen = entry['datosPrefactura']['0']['id_almacen'];
					
					//carga select de almacen de la venta
					$select_almacen.children().remove();
					var hmtl_alm;
					$.each(entry['Almacenes'],function(entryIndex,alm){
						if(valor_id_almacen == alm['id']){
							hmtl_alm += '<option value="' + alm['id'] + '"  selected="yes">' + alm['titulo'] + '</option>';
						}
					});
					$select_almacen.append(hmtl_alm);
					
					
					//metodo pago 2=Tarjeta de credito, 3=tarjeta de debito
					if(parseInt(valor_metodo)==2 || parseInt(valor_metodo)==3){
						//si esta desahabilitado, hay que habilitarlo para permitir la captura de los digitos de la tarjeta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						//quitar propiedad de solo lectura
						//$no_cuenta.removeAttr('readonly');
						
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
					
					if(parseInt(valor_metodo)==1 || parseInt(valor_metodo)==6){
						$no_cuenta.val('');
						if(!$no_cuenta.is(':disabled')) {
							$no_cuenta.attr('disabled','-1');
						}
						if(!$etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.attr('disabled','-1');
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
									trr += '<input type="hidden" name="iddetalle" id="idd" value="'+ prod['id_detalle'] +'">';//este es el id del registro que ocupa el producto en la tabla prefacturas_detalles
									trr += '<input type="hidden" name="id_remision" value="0" 	id="id_rem">';
									trr += '<input type="hidden" name="costo_promedio" id="costprom" value="'+ prod['costo_prom'] +'">';
									//trr += '<span id="elimina">1</span>';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="114">';
									trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ prod['producto_id'] +'">';
									trr += '<INPUT TYPE="text" name="sku'+ tr +'" value="'+ prod['sku'] +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="202">';
								trr += '<INPUT TYPE="text" 	name="nombre'+ tr +'" 	value="'+ prod['titulo'] +'" 	id="nom" class="borde_oculto" readOnly="true" style="width:198px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT TYPE="text" 	name="unidad'+ tr +'" 	value="'+ prod['unidad'] +'" 	id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
									trr += '<INPUT type="hidden" 	name="id_presentacion"  value="'+  prod['id_presentacion'] +'" 	id="idpres">';
									trr += '<INPUT TYPE="text" 		name="presentacion'+ tr +'" 	value="'+  prod['presentacion'] +'" 	id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<INPUT TYPE="text" 	name="cantidad" value="'+  prod['cantidad'] +'" 		id="cant" style="width:76px;">';
							trr += '</td>';
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT TYPE="text" 	name="costo" 	value="'+  prod['precio_unitario'] +'" 	id="cost" style="width:86px; text-align:right;">';
								trr += '<INPUT type="hidden" value="'+  prod['precio_unitario'] +'" id="costor">';
							trr += '</td>';
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT TYPE="text" 	name="importe'+ tr +'" 	value="'+  prod['importe'] +'" 	id="import" readOnly="true" style="width:86px; text-align:right;">';
								trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="'+parseFloat(prod['importe']) * parseFloat(prod['valor_imp'])+'">';
								trr += '<INPUT type="hidden"    name="id_imp_prod"  value="'+  prod['tipo_impuesto_id'] +'" id="idimppord">';
								trr += '<INPUT type="hidden"    name="valor_imp" 	value="'+  prod['valor_imp'] +'" 		id="ivalorimp">';
							trr += '</td>';
							trr += '</tr>';
							$grid_productos.append(trr);
                            
							
						});
					}
					
					$calcula_totales();//llamada a la funcion que calcula totales 
					
					//si es refacturacion, no se puede cambiar los datos del grid, solo el header de la factura
					if(entry['datosPrefactura']['0']['refacturar']=='true'){
						$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
						$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid
					}
					
					//flujo_proceso 2=Prefactura
					if(parseInt(flujo_proceso)==2){
						$boton_facturar.show();
					}
					
					//proceso_flujo_id 3=Facturado,5=Remision
					if(parseInt(flujo_proceso)==3 || parseInt(flujo_proceso)==5){
						$tipo_cambio.val(entry['datosPrefactura']['0']['tipo_cambio']);
						$tipo_tipo_cambio_original.val(entry['datosPrefactura']['0']['tipo_cambio']);
					}else{
						$tipo_cambio.val(entry['datosPrefactura']['0']['tipo_cambio']);
						$tipo_tipo_cambio_original.val(entry['datosPrefactura']['0']['tipo_cambio']);
					}
					
					//$observaciones.attr("readonly", true);
					//$tipo_cambio.attr("readonly", true);
					$orden_compra.attr("readonly", true);
					//$digitos.attr("readonly", true);
					
					$busca_cliente.hide();
					//$busca_sku.hide();
					//$agregar_producto.hide();
					
					$grid_productos.find('a[href*=elimina_producto]').hide();
					$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
					$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid
					
					
				});//termina llamada json
                
				
				
				
				
				$select_moneda.change(function(){
					var moneda_id = $(this).val();
					//alert(moneda_id);
					$convertir_costos($tipo_cambio,moneda_id,$subtotal,$impuesto,$total,$valor_impuesto,$grid_productos);
				});
				
				$tipo_cambio.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}		
				});

				
				//aplicar tipo de cambio a todos los costos al cambiar valor de tipo de cambio
				$tipo_cambio.blur(function(){
					$grid_productos.find('tr').each(function (index){
						var precio_cambiado=0;
						var importe_cambiado=0;
						if($(this).find('#cost').val() != ' '){
							//si la moneda inicial de la prefactura es diferente a la moneda actual seleccionada
							//entonces recalculamos los costos de acuerdo al tipo de cambio
							if( parseInt($moneda_original.val()) != parseInt($select_moneda.val()) ){
								if(parseInt($moneda_original.val())==1){
									//si la moneda original es pesos, calculamos su equivalente a dolares
									precio_cambiado = parseFloat($(this).find('#costor').val()) / parseFloat($tipo_cambio.val());
								}else{
									//si la moneda original es dolar, calculamos su equivalente a pesos
									precio_cambiado = parseFloat($(this).find('#costor').val()) * parseFloat($tipo_cambio.val());
								}
								$(this).find('#cost').val(parseFloat(precio_cambiado).toFixed(4));
								importe_cambiado = parseFloat($(this).find('#cant').val()) * parseFloat($(this).find('#cost').val());
								$(this).find('#import').val(parseFloat(importe_cambiado).toFixed(4));
							}else{
								//aqui no se cambia porque es la misma moneda en la que se hizo la prefactura, asi que no se aplica tipo de cambio
							}
						}
					});
					$calcula_totales();//llamada a la funcion que calcula totales
				});
				
				
				
				//buscador de clientes
				$busca_cliente.click(function(event){
					event.preventDefault();
					$busca_clientes();
				});
				
				/*
				//buscador de productos
				$busca_sku.click(function(event){
					event.preventDefault();
					//if(parseInt($select_almacen.val()) != 0){
						$busca_productos($sku_producto.val());
					//}else{
					//	jAlert("Es necesario seleccionar un almacen", 'Atencion!');
					//}
				});
				
				
				//agregar producto al grid
				$agregar_producto.click(function(event){
					event.preventDefault();
					//if($sku_producto.val() != ''){
						//$agrega_producto_grid($sku_producto,$grid_productos);
						$buscador_presentaciones_producto($id_cliente, $no_cliente.val(), $sku_producto.val(),$nombre_producto,$grid_productos,$select_moneda,$tipo_cambio);
					//}else{
					//	jAlert("Es necesario ingresar un sku valido", '¡ Atencion !');
					//}
					
				});
				
				
				//ejecutar clic del href Agregar producto al pulsar enter en el campo sku del producto
				$sku_producto.keypress(function(e){
					
					if(e.which == 13){
						$agregar_producto.trigger('click');
						return false;
					}
					
				});
				*/
				
				
				
				//cambiar metodo de pago
				$select_tipo_documento.change(function(){
					var tipo = $(this).val();
					if(parseInt(tipo)==1){
						$boton_facturar.val('Facturar');
					}else{
						$boton_facturar.val('Remisionar');
					}
					
				});
				
				
				
				
				
				
				
				$boton_facturar.click(function(event){
					var tipo = '';
					if( parseInt($select_tipo_documento.val())==1  ||  parseInt($select_tipo_documento.val())==3 ) {
						tipo = 'Facturacion';
					}
					
					if( parseInt($select_tipo_documento.val())==2 ) {
						tipo = 'Remision';
					}
					
					jConfirm('Confirmar '+tipo+'?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
							$submit_actualizar.trigger('click');
						}else{
							//aqui no hay nada
						}
					});
					
				});
                
				
				
                
				$submit_actualizar.bind('click',function(){
					var trCount = $("tr", $grid_productos).size();
					$total_tr.val(trCount);
					if(parseInt(trCount) > 0){
						return true;
					}else{
						jAlert("No hay datos para actualizar", 'Atencion!');
						return false;
					}
				});
                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-prefacturas-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-prefacturas-overlay').fadeOut(remove);
				});
				
			}
		}
	}
	
	
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPrefacturas.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getPrefacturas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaPrefacturas00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



