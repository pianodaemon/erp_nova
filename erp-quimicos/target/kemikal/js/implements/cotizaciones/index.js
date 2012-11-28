$(function() {
        //var controller = "com.mycompany_Kemikal_war_1.0-SNAPSHOT/controllers/cotizaciones";
        
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
	var controller = $contextpath.val()+"/controllers/cotizaciones";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_cotizacion = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Cotizaciones');
	
	//barra para el buscador 
	$('#barra_buscador').css({'height':'80px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	$('#barra_buscador').hide();
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_cliente = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	//var almacenes = new Array(); //este arreglo carga el select de almacen, los select almacen destino del grid
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "cliente" + signo_separador + $busqueda_cliente.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val();
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
        


	//visualizar  la barra del buscador
	$visualiza_buscador.click(function(event){
		event.preventDefault();
         $('#barra_buscador').toggle( 'blind');
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
        
    
            
        
        
        
    //datos para el buscador
    //$arreglo = {}
    //var json_string = document.location.protocol + '//' + document.location.host + '/' + controller + '/data_buscador/out.json';
    //alimenta campos buscador
    

	
	$tabs_li_funxionalidad = function(){
            var $select_prod_tipo = $('#forma-cotizacions-window').find('select[name=prodtipo]');
            $('#forma-cotizacions-window').find('#submit').mouseover(function(){
                $('#forma-cotizacions-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
            });
            $('#forma-cotizacions-window').find('#submit').mouseout(function(){
                $('#forma-cotizacions-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
            });
            
            $('#forma-cotizacions-window').find('#boton_cancelar').mouseover(function(){
                $('#forma-cotizacions-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            });
            $('#forma-cotizacions-window').find('#boton_cancelar').mouseout(function(){
                $('#forma-cotizacions-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            });
            
            $('#forma-cotizacions-window').find('#close').mouseover(function(){
                $('#forma-cotizacions-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            });
            $('#forma-cotizacions-window').find('#close').mouseout(function(){
                $('#forma-cotizacions-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            });

            $('#forma-cotizacions-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-cotizacions-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-cotizacions-window').find(".contenidoPes:first").show(); //Show first tab content

            //On Click Event
            $('#forma-cotizacions-window').find("ul.pestanas li").click(function() {
                $('#forma-cotizacions-window').find(".contenidoPes").hide();
                $('#forma-cotizacions-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-cotizacions-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
                $(this).addClass("active");
                return false;
            });
	}

	
	//buscador de clientes
	$busca_clientes = function(){
            //limpiar_campos_grids();
            $(this).modalPanel_Buscacliente();
            var $dialogoc =  $('#forma-buscacliente-window');
            //var $dialogoc.prependTo('#forma-buscaproduct-window');
            $dialogoc.append($('div.buscador_clientes').find('table.formaBusqueda_clientes').clone());
            $('#forma-buscacliente-window').css({"margin-left": -200, 	"margin-top": -200});

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
                $arreglo = {'cadena':$cadena_buscar.val(),
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
                                trr += '<input type="hidden" id="contacto" value="'+cliente['contacto']+'">';
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
                        //asignar a los campos correspondientes el sku y y descripcion
                        $('#forma-cotizacions-window').find('input[name=id_cliente]').val($(this).find('#idclient').val());
                        $('#forma-cotizacions-window').find('input[name=nocontrolcliente]').val($(this).find('span.no_control').html());
                        $('#forma-cotizacions-window').find('input[name=rfccliente]').val($(this).find('span.rfc').html());
                        $('#forma-cotizacions-window').find('input[name=razoncliente]').val($(this).find('span.razon').html());
                        $('#forma-cotizacions-window').find('input[name=dircliente]').val($(this).find('#direccion').val());
						$('#forma-cotizacions-window').find('input[name=contactocliente]').val($(this).find('#contacto').val());
						
						var selecionado = $('#forma-cotizacions-window').find('select[name=moneda]').val();
						$('#forma-cotizacions-window').find('select[name=moneda]').find('option[value="'+selecionado+'"]').removeAttr('selected');
						
                        $('#forma-cotizacions-window').find('select[name=moneda]').find('option[value="'+$(this).find('#id_moneda').val()+'"]').attr('selected','selected');
                        //alert($(this).find('#id_moneda').val());
                        //carga select de la moneda del cliente
                        //$('#forma-cotizacions-window').find('select[name=moneda]').children().remove();
                        //var moneda_hmtl = '<option value="'+ $(this).find('#id_moneda').val() +'" selected="yes">' + $(this).find('#moneda').val() + '</option>';
                        //$('#forma-cotizacions-window').find('select[name=moneda]').append(moneda_hmtl);
						
						
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
		
		$('#forma-buscaproducto-window').css({"margin-left": -200, 	"margin-top": -200});
		
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
					$('#forma-cotizacions-window').find('input[name=sku_producto]').val($(this).find('span.sku_prod_buscador').html());
					$('#forma-cotizacions-window').find('input[name=nombre_producto]').val($(this).find('span.titulo_prod_buscador').html());
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaproducto-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-cotizacions-window').find('input[name=sku_producto]').focus();
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
	
	
	
	
	
	//buscador de presentaciones disponibles para un producto
	$buscador_presentaciones_producto = function(rfc_cliente, sku_producto,$nombre_producto,$grid_productos){
		//verifica si el campo rfc proveedor no esta vacio
		if(rfc_cliente != ''){
                    //verifica si el campo sku no esta vacio para realizar busqueda
                    if(sku_producto != ''){
                        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_presentaciones_producto.json';
                        $arreglo = {'sku':sku_producto};

                        var trr = '';

                        $.post(input_json,$arreglo,function(entry){

                                //verifica si el arreglo  retorno datos
                                if (entry['Presentaciones'].length > 0){
                                    $(this).modalPanel_Buscapresentacion();
                                    var $dialogoc =  $('#forma-buscapresentacion-window');
                                    $dialogoc.append($('div.buscador_presentaciones').find('table.formaBusqueda_presentaciones').clone());
                                    $('#forma-buscapresentacion-window').css({"margin-left": -200, "margin-top": -200});

                                    var $tabla_resultados = $('#forma-buscapresentacion-window').find('#tabla_resultado');
                                    var $cancelar_plugin_busca_lotes_producto = $('#forma-buscapresentacion-window').find('a[href*=cencela]');
                                    $tabla_resultados.children().remove();

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

                                        $nombre_producto.val(titulo);//muestra el titulo del producto en el campo nombre del producto de la ventana de cotizaciones

                                        //aqui se pasan datos a la funcion que agrega el tr en el grid
                                        $agrega_producto_grid($grid_productos,id_prod,sku,titulo,unidad,id_pres,pres);


                                        //elimina la ventana de busqueda
                                        var remove = function() {$(this).remove();};
                                        $('#forma-buscapresentacion-overlay').fadeOut(remove);
                                    });

                                    $cancelar_plugin_busca_lotes_producto.click(function(event){
                                        event.preventDefault();
                                        var remove = function() {$(this).remove();};
                                        $('#forma-buscapresentacion-overlay').fadeOut(remove);
                                    });

                                }else{
                                    jAlert("El producto que intenta agregar no existe, pruebe ingresando otro.\nHaga clic en Buscar.",'! Atencion');
                                    $('#forma-cotizacions-window').find('input[name=titulo_producto]').val('');
                                }
                        },"json");
				
                    }else{
                            jAlert("Es necesario ingresar un Sku de producto valido", 'Atencion!');
                    }
		}else{
			jAlert("Es necesario seleccionar un Cliente", 'Atencion!');
		}
		
	}//termina buscador dpresentaciones disponibles de un producto
	
        
       
	/*
	
	//calcula totales(subtotal, impuesto, total)
	$calcula_totales = function(){
		var $campo_subtotal = $('#forma-cotizacions-window').find('input[name=subtotal]');
		var $campo_impuesto = $('#forma-cotizacions-window').find('input[name=impuesto]');
		var $campo_total = $('#forma-cotizacions-window').find('input[name=total]');
		//var $campo_tc = $('#forma-cotizacions-window').find('input[name=tc]');
		var $valor_impuesto = $('#forma-cotizacions-window').find('input[name=valorimpuesto]');
		var $grid_productos = $('#forma-cotizacions-window').find('#grid_productos');
		
		var sumaSubTotal = 0; //es la suma de todos los importes
		var sumaImpuesto = 0; //valor del iva
		var sumaTotal = 0; //suma del subtotal + totalImpuesto
		
		//si  el campo tipo de cambio es null o vacio, se le asigna un 0
		//if( $campo_tc.val()== null || $campo_tc.val()== ''){
		//	$campo_tc.val(0);
		//}
		
		//si  el campo tipo de cambio es null o vacio, se le asigna un 0
		if( $valor_impuesto.val()== null || $valor_impuesto.val()== ''){
			$valor_impuesto.val(0);
		}
		
		$grid_productos.find('tr').each(function (index){
			if(( $(this).find('#cost').val() != ' ') && ( $(this).find('#cant').val() != ' ' )){
				//alert($(this).find('#cost').val());
				//acumula los importes en la variable subtotal
				sumaSubTotal = parseFloat(sumaSubTotal) + parseFloat($(this).find('#import').val());
				//alert($(this).find('#import').val());
				if($(this).find('#totimp').val() != ''){
					sumaImpuesto =  parseFloat(sumaImpuesto) + parseFloat($(this).find('#totimp').val());
				}
			}
		});
		
                
                
                
		//calcula el total sumando el subtotal y el impuesto
		sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaImpuesto);
		
		
		//redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
		$campo_subtotal.val(Math.round(parseFloat(sumaSubTotal)*100)/100);
		//redondea a dos digitos el impuesto y lo asigna al campo impuesto
		$campo_impuesto.val(Math.round(parseFloat(sumaImpuesto)*100)/100);
		//redondea a dos digitos la suma  total y se asigna al campo total
		$campo_total.val(Math.round(parseFloat(sumaTotal)*100)/100);
		
	}//termina calcular totales
    */
        
        
        
        
	
	
	//agregar producto al grid
	$agrega_producto_grid = function($grid_productos,id_prod,sku,titulo,unidad,id_pres,pres){
		var $valor_impuesto = $('#forma-cotizacions-window').find('input[name=valorimpuesto]');
		
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
			
			//obtiene numero de trs
			var tr = $("tr", $grid_productos).size();
			tr++;
			
			var trr = '';
			trr = '<tr>';
				trr += '<td width="60">';
					trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
					trr += '<input type="hidden" name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
					trr += '<input type="hidden" name="iddetalle" id="idd" value="0">';//este es el id del registro que ocupa el producto en la tabla cotizacions_detalles
				trr += '</td>';
				trr += '<td width="90">';
					trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ id_prod +'">';
					trr += '<INPUT TYPE="text" name="sku'+ tr +'" value="'+ sku +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:87px;">';
				trr += '</td>';
				trr += '<td width="200"><INPUT TYPE="text" 	name="nombre'+ tr +'" 	value="'+ titulo +'" 	id="nom" class="borde_oculto" readOnly="true" style="width:196px;"></td>';
				trr += '<td width="90"><INPUT TYPE="text" 	name="unidad'+ tr +'" 	value="'+ unidad +'" 	id="uni" class="borde_oculto" readOnly="true" style="width:86px;"></td>';
				trr += '<td width="110">';
					trr += '<INPUT type="hidden"    name="id_presentacion"        	value="'+  id_pres +'" 	id="idpres">';
					trr += '<INPUT TYPE="text" 		name="presentacion'+ tr +'"		value="'+  pres +'" 	id="pres" class="borde_oculto" readOnly="true" style="width:106px;">';
				trr += '</td>';
					trr += '<td width="80"><INPUT TYPE="text" 	name="cantidad" 	value=" " id="cant" style="width:76px;"></td>';
					trr += '<td width="85"><INPUT TYPE="text" 	name="costo" 		value=" " id="cost" style="width:81px;"></td>';
					trr += '<td width="50">';
						trr += '<SELECT NAME="monedagrid" class="moneda'+ tr +'" style="width:50px;"></SELECT>';
					trr += '</td>';
				trr += '<td width="95"><INPUT TYPE="text" 	name="importe'+ tr +'" 		value="" id="import" readOnly="true" style="width:90px; text-align:right;"></td>';
				trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="0">';
			trr += '</tr>';
                        
			$grid_productos.append(trr);
			
			//$('#datos option:selected').clone().appendTo("#recibe");
            
            $('#forma-cotizacions-window').find('select[name=moneda2]').find('option').clone().appendTo('.moneda'+ tr);
                    
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
				{   //calcula el importe
					$(this).parent().parent().find('#import').val(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cost').val()));
					//redondea el importe en dos decimales
					$(this).parent().parent().find('#import').val(Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100);

					//calcula el impuesto para este producto multiplicando el importe por el valor del iva
					//$(this).parent().parent().find('#totimp').val(parseFloat($(this).parent().parent().find('#import').val()) * parseFloat($valor_impuesto.val()));
					
				}else{
					$(this).parent().parent().find('#import').val('');
					//$(this).parent().parent().find('#totimp').val('');
				}
				//$calcula_totales();//llamada a la funcion que calcula totales
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
				{   //calcula el importe
					$(this).parent().parent().find('#import').val(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cant').val()));
					//redondea el importe en dos decimales
					$(this).parent().parent().find('#import').val(Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100);

					//calcula el impuesto para este producto multiplicando el importe por el valor del iva
					//$(this).parent().parent().find('#totimp').val(parseFloat($(this).parent().parent().find('#import').val()) * parseFloat($valor_impuesto.val()));

				}else{
					$(this).parent().parent().find('#import').val('');
					//$(this).parent().parent().find('#totimp').val('');
				}
				//$calcula_totales();//llamada a la funcion que calcula totales
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
			$grid_productos.find('#cost').keypress(function(e){
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
					//$calcula_totales();//llamada a la funcion que calcula totales
				}
			});
			
		}else{
			jAlert("El producto: "+sku+" con presentacion: "+pres+" ya se encuentra en el listado, seleccione otro diferente.", 'Atencion!');
		}
		
	}//termina agregar producto al grid
	
	
	
	//nueva cotizacion
	$new_cotizacion.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_cotizacions();
		
		var form_to_show = 'formaCotizacions00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$('#forma-cotizacions-window').css({"margin-left": -370, 	"margin-top": -220});
		
		$forma_selected.prependTo('#forma-cotizacions-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		//var json_string = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + accion + '/' + id_to_show + '/out.json';
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCotizacion.json';
		$arreglo = {'id_cotizacion':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
                
		var $id_cotizacion = $('#forma-cotizacions-window').find('input[name=id_cotizacion]');
		var $total_tr = $('#forma-cotizacions-window').find('input[name=total_tr]');
		var $select_tipocotizacion = $('#forma-cotizacions-window').find('select[name=tipocotizacion]');
		
		var $busca_cliente = $('#forma-cotizacions-window').find('a[href*=busca_cliente]');
		var $id_cliente = $('#forma-cotizacions-window').find('input[name=id_cliente]');
		var $rfc_cliente = $('#forma-cotizacions-window').find('input[name=rfccliente]');
		var $razon_cliente = $('#forma-cotizacions-window').find('input[name=razoncliente]');
		var $dir_cliente = $('#forma-cotizacions-window').find('input[name=dircliente]');
		var $select_moneda = $('#forma-cotizacions-window').find('select[name=moneda]');
		var $select_moneda2 = $('#forma-cotizacions-window').find('select[name=moneda2]');
		
		//var $campo_tc = $('#forma-cotizacions-window').find('input[name=tc]');
		var $id_impuesto = $('#forma-cotizacions-window').find('input[name=id_impuesto]');
		var $valor_impuesto = $('#forma-cotizacions-window').find('input[name=valorimpuesto]');
		
		var $observaciones = $('#forma-cotizacions-window').find('textarea[name=observaciones]');
		//var $select_almacen = $('#forma-cotizacions-window').find('select[name=almacen]');
		var $sku_producto = $('#forma-cotizacions-window').find('input[name=sku_producto]');
		var $nombre_producto = $('#forma-cotizacions-window').find('input[name=nombre_producto]');
		
		
		//buscar producto
		var $busca_sku = $('#forma-cotizacions-window').find('a[href*=busca_sku]');
		//href para agregar producto al grid
		var $agregar_producto = $('#forma-cotizacions-window').find('a[href*=agregar_producto]');
		
		
		var $boton_genera_pdf = $('#forma-cotizacions-window').find('#genera_pdf');
		
		//grid de productos
		var $grid_productos = $('#forma-cotizacions-window').find('#grid_productos');
		//grid de errores
		var $grid_warning = $('#forma-cotizacions-window').find('#div_warning_grid').find('#grid_warning');
		
		
		
		//var $flete = $('#forma-cotizacions-window').find('input[name=flete]');
		var $subtotal = $('#forma-cotizacions-window').find('input[name=subtotal]');
		var $impuesto = $('#forma-cotizacions-window').find('input[name=impuesto]');
		var $total = $('#forma-cotizacions-window').find('input[name=total]');
		
		var $cerrar_plugin = $('#forma-cotizacions-window').find('#close');
		var $cancelar_plugin = $('#forma-cotizacions-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-cotizacions-window').find('#submit');
		
		
		$id_cotizacion.val(0);//para nueva cotizacion el folio es 0
		$select_moneda2.hide();
		
		//$campo_factura.css({'background' : '#ffffff'});
		
		//ocultar boton de generar pdf. Solo debe estar activo en editar
		$boton_genera_pdf.hide();
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La cotizaci&oacute;n se guard&oacute; con exito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-cotizacions-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				//$('#forma-cotizacions-window').find('.div_one').css({'height':'545px'});//sin errores
				$('#forma-cotizacions-window').find('.cotizacions_div_one').css({'height':'592px'});//con errores
				$('#forma-cotizacions-window').find('div.interrogacion').css({'display':'none'});

				$grid_productos.find('#cant').css({'background' : '#ffffff'});
				$grid_productos.find('#cost').css({'background' : '#ffffff'});

				$('#forma-cotizacions-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-cotizacions-window').find('#div_warning_grid').find('#grid_warning').children().remove();

				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');

					if( longitud.length > 1 ){
							$('#forma-cotizacions-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
							.parent()
							.css({'display':'block'})
							.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
							
							//alert(tmp.split(':')[0]);

						if(parseInt($("tr", $grid_productos).size())>0){
							for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
								if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='costo'+i)){
									//alert(tmp.split(':')[0]);
									$('#forma-cotizacions-window').find('.cotizacions_div_one').css({'height':'575px'});
									//$('#forma-cotizacions-window').find('.div_three').css({'height':'910px'});

									$('#forma-cotizacions-window').find('#div_warning_grid').css({'display':'block'});
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
			
                        
			//carga select denominacion con todas las monedas
			$select_moneda.children().remove();
			var moneda_hmtl = '';
			$.each(entry['Monedas'],function(entryIndex,moneda){
				moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
			});
			$select_moneda.append(moneda_hmtl);
			$select_moneda2.append(moneda_hmtl);//este  esta oculto
			
		},"json");//termina llamada json
		
		
        //seleccionar tipo de movimiento
		$select_moneda.change(function(){
			var seleccionado = $(this).val();
			//alert("select_moneda: "+$select_moneda.find('option:selected').val()+"   seleccionado:"+seleccionado);
			$grid_productos.find('select[name=monedagrid]').children().remove();
			$select_moneda2.find('option').clone().appendTo($grid_productos.find('select[name=monedagrid]'));
			$grid_productos.find('select[name=monedagrid]').find('option[value="'+seleccionado+'"]').attr('selected','selected');
			
		});
        
		
		
		//buscador de clientes
		$busca_cliente.click(function(event){
			event.preventDefault();
			$busca_clientes();
		});


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
				$buscador_presentaciones_producto($rfc_cliente.val(), $sku_producto.val(),$nombre_producto,$grid_productos);
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
		
		/*
		$boton_genera_pdf.click(function(event){
			//cuando la cotizacion es nueva no se genera pdf, hasta despues de guardar
		});
		*/
		
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
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-cotizacions-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-cotizacions-overlay').fadeOut(remove);
		});
		
	});
	
	
	
	var carga_formaCotizacions00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id_cotizacion':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la cotizacion?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La cotizacion fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La cotizacion no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
				
                        
		}else{
			//aqui  entra para editar un registro
			$('#forma-cotizacions-window').remove();
			$('#forma-cotizacions-overlay').remove();
			
			var form_to_show = 'formaCotizacions00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_cotizacions();
			
			$('#forma-cotizacions-window').css({"margin-left": -370, 	"margin-top": -220});
			
			$forma_selected.prependTo('#forma-cotizacions-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCotizacion.json';
				$arreglo = {'id_cotizacion':id_to_show,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
                                
				var $total_tr = $('#forma-cotizacions-window').find('input[name=total_tr]');
				var $id_cotizacion = $('#forma-cotizacions-window').find('input[name=id_cotizacion]');
				
				var $busca_cliente = $('#forma-cotizacions-window').find('a[href*=busca_cliente]');
				var $id_cliente = $('#forma-cotizacions-window').find('input[name=id_cliente]');
				var $no_control_cliente = $('#forma-cotizacions-window').find('input[name=nocontrolcliente]');
				var $rfc_cliente = $('#forma-cotizacions-window').find('input[name=rfccliente]');
				var $razon_cliente = $('#forma-cotizacions-window').find('input[name=razoncliente]');
				var $dir_cliente = $('#forma-cotizacions-window').find('input[name=dircliente]');
				var $select_moneda = $('#forma-cotizacions-window').find('select[name=moneda]');
				var $select_moneda2 = $('#forma-cotizacions-window').find('select[name=moneda2]');
				
				//var $campo_tc = $('#forma-cotizacions-window').find('input[name=tc]');
				var $id_impuesto = $('#forma-cotizacions-window').find('input[name=id_impuesto]');
				var $valor_impuesto = $('#forma-cotizacions-window').find('input[name=valorimpuesto]');
				
				var $observaciones = $('#forma-cotizacions-window').find('textarea[name=observaciones]');
				//var $select_almacen = $('#forma-cotizacions-window').find('select[name=almacen]');
				var $sku_producto = $('#forma-cotizacions-window').find('input[name=sku_producto]');
				var $nombre_producto = $('#forma-cotizacions-window').find('input[name=nombre_producto]');
				
				//buscar producto
				var $busca_sku = $('#forma-cotizacions-window').find('a[href*=busca_sku]');
				//href para agregar producto al grid
				var $agregar_producto = $('#forma-cotizacions-window').find('a[href*=agregar_producto]');
				
				var $boton_genera_pdf = $('#forma-cotizacions-window').find('#genera_pdf');
				
				//grid de productos
				var $grid_productos = $('#forma-cotizacions-window').find('#grid_productos');
				//grid de errores
				var $grid_warning = $('#forma-cotizacions-window').find('#div_warning_grid').find('#grid_warning');
				
				//var $flete = $('#forma-cotizacions-window').find('input[name=flete]');
				var $subtotal = $('#forma-cotizacions-window').find('input[name=subtotal]');
				var $impuesto = $('#forma-cotizacions-window').find('input[name=impuesto]');
				var $total = $('#forma-cotizacions-window').find('input[name=total]');
				
				var $cerrar_plugin = $('#forma-cotizacions-window').find('#close');
				var $cancelar_plugin = $('#forma-cotizacions-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-cotizacions-window').find('#submit');
				
				
				$select_moneda2.hide();
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						jAlert("La cotizaci&oacute;n se guard&oacute; con exito", 'Atencion!');
						var remove = function() {$(this).remove();};
						$('#forma-cotizacions-overlay').fadeOut(remove);
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						//$('#forma-cotizacions-window').find('.div_one').css({'height':'545px'});//sin errores
						$('#forma-cotizacions-window').find('.cotizacions_div_one').css({'height':'575px'});//con errores
						$('#forma-cotizacions-window').find('div.interrogacion').css({'display':'none'});
						
						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});
						
						$('#forma-cotizacions-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-cotizacions-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							
							if( longitud.length > 1 ){
								$('#forma-cotizacions-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								//alert(tmp.split(':')[0]);

								if(parseInt($("tr", $grid_productos).size())>0){
									for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
										if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='costo'+i)){
											$('#forma-cotizacions-window').find('.cotizacions_div_one').css({'height':'592px'});
											$('#forma-cotizacions-window').find('#div_warning_grid').css({'display':'block'});
											
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
				//$.getJSON(json_string,function(entry){
				$.post(input_json,$arreglo,function(entry){
					
					$id_cotizacion.val(entry['datosCotizacion']['0']['id']);
					$id_cliente.val(entry['datosCotizacion']['0']['cliente_id']);
					$no_control_cliente.val(entry['datosCotizacion']['0']['numero_control']);
					$rfc_cliente.val(entry['datosCotizacion']['0']['rfc']);
					$razon_cliente.val(entry['datosCotizacion']['0']['razon_social']);
					$dir_cliente.val(entry['datosCotizacion']['0']['direccion']);
					$observaciones.text(entry['datosCotizacion']['0']['observaciones']);
					
                                        
					//carga select denominacion con todas las monedas
					$select_moneda.children().remove();
					//var moneda_hmtl = '<option value="0">[--   --]</option>';
					var moneda_hmtl = '';
					var moneda_hmtl2 = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(moneda['id'] == entry['datosCotizacion']['0']['moneda_id']){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
						}else{
							moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
						}
						moneda_hmtl2 += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
					});
					$select_moneda.append(moneda_hmtl);
					//este select esta oculto, de esta se copian los elementos para el select de moneda del grid
					$select_moneda2.append(moneda_hmtl2);
					
                                        /*
					$select_moneda.children().remove();
					var moneda_hmtl = '<option value="' + entry['datosCotizacion']['0']['moneda_id'] + '" selected="yes">' + entry['datosCotizacion']['0']['moneda'] + '</option>';
					$select_moneda.append(moneda_hmtl);
                                        */
                                        
					//$campo_tc.val();
					//$id_impuesto.val();
					//$valor_impuesto.val();
					//$campo_tc.val(entry['tc']['tipo_cambio']);
					$id_impuesto.val(entry['iva']['0']['id_impuesto']);
					$valor_impuesto.val(entry['iva']['0']['valor_impuesto']);
					
					
					$busca_cliente.hide();
					
					if(entry['datosGrid'] != null){
						$.each(entry['datosGrid'],function(entryIndex,prod){
							
							//obtiene numero de trs
							var tr = $("tr", $grid_productos).size();
							tr++;
							
							var trr = '';
							trr = '<tr>';
								trr += '<td width="60">';
									trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
									trr += '<input type="hidden" name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
									trr += '<input type="hidden" name="iddetalle" id="idd" value="'+ prod['id_detalle'] +'">';//este es el id del registro que ocupa el producto en la tabla cotizacions_detalles
									//trr += '<span id="elimina">1</span>';
								trr += '</td>';
								trr += '<td width="90">';
									trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ prod['producto_id'] +'">';
									trr += '<INPUT TYPE="text" name="sku'+ tr +'" value="'+ prod['sku'] +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:87px;">';
								trr += '</td>';
								trr += '<td width="200"><INPUT TYPE="text" 	name="nombre'+ tr +'" 	value="'+ prod['titulo'] +'" 	id="nom" class="borde_oculto" readOnly="true" style="width:196px;"></td>';
								trr += '<td width="90"><INPUT TYPE="text" 	name="unidad'+ tr +'" 	value="'+ prod['unidad'] +'" 	id="uni" class="borde_oculto" readOnly="true" style="width:86px;"></td>';
								trr += '<td width="110">';
									trr += '<INPUT type="hidden" 	name="id_presentacion"  value="'+  prod['id_presentacion'] +'" 	id="idpres">';
									trr += '<INPUT TYPE="text" 		name="presentacion'+ tr +'" 	value="'+  prod['presentacion'] +'" 	id="pres" class="borde_oculto" readOnly="true" style="width:106px;">';
								trr += '</td>';
								trr += '<td width="80"><INPUT TYPE="text" 	name="cantidad" value="'+  prod['cantidad'] +'" 		id="cant" style="width:76px;"></td>';
								trr += '<td width="85"><INPUT TYPE="text" 	name="costo" 	value="'+  prod['precio_unitario'] +'" 	id="cost" style="width:81px; text-align:right;"></td>';
								trr += '<td width="50">';
									trr += '<SELECT NAME="monedagrid" class="moneda'+ tr +'" style="width:50px;"></SELECT>';
								trr += '</td>';
								trr += '<td width="95"><INPUT TYPE="text" 	name="importe'+ tr +'" 	value="'+  prod['importe'] +'" 			id="import" readOnly="true" style="width:90px; text-align:right;"></td>';
								
								trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="'+parseFloat(prod['importe']) * parseFloat($valor_impuesto.val())+'">';
							trr += '</tr>';
							$grid_productos.append(trr);
							//asigna las monedas al select de monedas en el grid
							$select_moneda2.find('option').clone().appendTo($grid_productos.find('.moneda'+ tr));
							
							// selecciona la moneda del producto cotizado
							$grid_productos.find('.moneda'+ tr).find('option[value="'+prod['moneda_id']+'"]').attr('selected','selected');
							
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
									//$(this).parent().parent().find('#totimp').val(parseFloat($(this).parent().parent().find('#import').val()) * parseFloat($valor_impuesto.val()));
									
								}else{
									$(this).parent().parent().find('#import').val('');
									//$(this).parent().parent().find('#totimp').val('');
								}
								//$calcula_totales();//llamada a la funcion que calcula totales
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
									//$(this).parent().parent().find('#totimp').val(parseFloat($(this).parent().parent().find('#import').val()) * parseFloat($valor_impuesto.val()));

								}else{
									$(this).parent().parent().find('#import').val('');
									//$(this).parent().parent().find('#totimp').val('');
								}
								//$calcula_totales();//llamada a la funcion que calcula totales
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
							$grid_productos.find('#cost').keypress(function(e){
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
									var iddetalle = $(this).parent().find('#idd').val();

									//asigna espacios en blanco a todos los input de la fila eliminada
									$(this).parent().parent().find('input').val(' ');

									//asigna un 0 al input eliminado como bandera para saber que esta eliminado
									$(this).parent().find('#elim').val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
									$(this).parent().find('#idd').val(iddetalle);
									//oculta la fila eliminada
									$(this).parent().parent().hide();
									//$calcula_totales();//llamada a la funcion que calcula totales
								}
							});
							
						});
						
						
					}
					
					//$calcula_totales();//llamada a la funcion que calcula totales
					
					
				});//termina llamada json
				
				//seleccionar tipo de moneda
				$select_moneda.change(function(){
					var seleccionado = $(this).val();
					//alert("select_moneda: "+$select_moneda.find('option:selected').val()+"   seleccionado:"+seleccionado);
					$grid_productos.find('select[name=monedagrid]').children().remove();
					$select_moneda2.find('option').clone().appendTo($grid_productos.find('select[name=monedagrid]'));
					$grid_productos.find('select[name=monedagrid]').find('option[value="'+seleccionado+'"]').attr('selected','selected');
				});
				
				
				
				//buscador de productos
				$busca_sku.click(function(event){
					event.preventDefault();
					$busca_productos($sku_producto.val());
				});
				
				//agregar producto al grid
				$agregar_producto.click(function(event){
					event.preventDefault();
					$buscador_presentaciones_producto($rfc_cliente.val(), $sku_producto.val(),$nombre_producto,$grid_productos);
				});
				
				
				//desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
				$sku_producto.keypress(function(e){
					if(e.which == 13){
						$agregar_producto.trigger('click');
						return false;
					}
				});
				
				
				$boton_genera_pdf.click(function(event){
					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/get_genera_pdf_cotizacion/'+$id_cotizacion.val()+'/'+iu+'/out.json';
					window.location.href=input_json;
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
					$('#forma-cotizacions-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-cotizacions-overlay').fadeOut(remove);
				});
				
			}
		}
	}
        
        
        
        
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCotizaciones.json';
		
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getCotizaciones.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCotizacions00_for_datagrid00);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



