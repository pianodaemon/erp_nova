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
	var controller = $contextpath.val()+"/controllers/clientsdf";
    
        //Barra para las acciones
        $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
        $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_clientsdf = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	var $campo_busqueda = $('#barra_buscador').find('.tabla_buscador').find('input[name=cadena_buscar]');
	var $select_filtro_por = $('#barra_buscador').find('.tabla_buscador').find('select[name=filtropor]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	var html = '';
	$select_filtro_por.children().remove();
	html='<option value="0">[-- Opcion busqueda --]</option>';
	html+='<option value="1">No. de control</option>';
	html+='<option value="2">RFC</option>';
	html+='<option value="3">Razon social</option>';
	html+='<option value="4">CURP</option>';
	$select_filtro_por.append(html);
	
	//alert($select_filtro_por.val());
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "cadena_busqueda" + signo_separador + $campo_busqueda.val() + "|";
		valor_retorno += "filtro_por" + signo_separador + $select_filtro_por.val() + "|";
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
		$campo_busqueda.val('');
		$select_filtro_por.find('option[index=0]').attr('selected','selected');
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
	
	
	
	
	$tabs_li_funxionalidad = function(){
		$('#forma-clientsdf-window').find('#submit').mouseover(function(){
			$('#forma-clientsdf-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-clientsdf-window').find('#submit').mouseout(function(){
			$('#forma-clientsdf-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-clientsdf-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-clientsdf-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-clientsdf-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-clientsdf-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-clientsdf-window').find('#close').mouseover(function(){
			$('#forma-clientsdf-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-clientsdf-window').find('#close').mouseout(function(){
			$('#forma-clientsdf-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});		
		
		$('#forma-clientsdf-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-clientsdf-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-clientsdf-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-clientsdf-window').find("ul.pestanas li").click(function() {
			$('#forma-clientsdf-window').find(".contenidoPes").hide();
			$('#forma-clientsdf-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-clientsdf-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
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
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_clientes.json';
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
					$('#forma-clientsdf-window').find('input[name=id_cliente]').val($(this).find('#idclient').val());
					$('#forma-clientsdf-window').find('input[name=rfc]').val($(this).find('span.rfc').html());
					$('#forma-clientsdf-window').find('input[name=cliente]').val($(this).find('span.razon').html());
					$('#forma-clientsdf-window').find('input[name=nocontrol]').val($(this).find('span.no_control').html());
					
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

	
	
	//nuevo direcciones de proveedores
	$new_clientsdf.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_clientsdf();
		
		var form_to_show = 'formaDirecciones';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-clientsdf-window').css({"margin-left": -400, 	"margin-top": -290});
		$forma_selected.prependTo('#forma-clientsdf-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();		
		
		var $identificador = $('#forma-clientsdf-window').find('input[name=identificador]');
		var $busca_cliente = $('#forma-clientsdf-window').find('a[href*=busca_cliente]');
		var $id_cliente = $('#forma-clientsdf-window').find('input[name=id_cliente]');
		var $cliente = $('#forma-clientsdf-window').find('input[name=cliente]');
		var $nocontrol = $('#forma-clientsdf-window').find('input[name=nocontrol]');
		var $rfc = $('#forma-clientsdf-window').find('input[name=rfc]');
		var $calle = $('#forma-clientsdf-window').find('input[name=calle]');
		var $numero_int = $('#forma-clientsdf-window').find('input[name=numero_int]');
		var $numero_ext = $('#forma-clientsdf-window').find('input[name=numero_ext]');
		var $entrecalles = $('#forma-clientsdf-window').find('input[name=entrecalles]');
		var $colonia = $('#forma-clientsdf-window').find('input[name=colonia]');
		var $select_pais = $('#forma-clientsdf-window').find('select[name=select_pais]');
		var $select_estado = $('#forma-clientsdf-window').find('select[name=select_estado]');
		var $select_municipio = $('#forma-clientsdf-window').find('select[name=select_municipio]');
		var $contacto = $('#forma-clientsdf-window').find('input[name=contacto]');
		var $email = $('#forma-clientsdf-window').find('input[name=email]');
		var $tel1 = $('#forma-clientsdf-window').find('input[name=tel1]');
		var $ext1 = $('#forma-clientsdf-window').find('input[name=ext1]');
		var $fax = $('#forma-clientsdf-window').find('input[name=fax]');
		var $tel2 = $('#forma-clientsdf-window').find('input[name=tel2]');
		var $ext2 = $('#forma-clientsdf-window').find('input[name=ext2]');
		
		var $cerrar_plugin = $('#forma-clientsdf-window').find('#close');
		var $cancelar_plugin = $('#forma-clientsdf-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-clientsdf-window').find('#submit');
		
                        
		$identificador.attr({'value' : 0});
		$id_cliente.attr({'value' : 0});
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La direccion fue dada de alta con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-clientsdf-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-clientsdf-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
                                     
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					//telUno: Numero Telefonico no Valido___
					if( longitud.length > 1 ){
						$('#forma-clientsdf-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getclientsdf.json';
		$arreglo = {'id':id_to_show};
		
		$.post(input_json,$arreglo,function(entry){
		   $campo_select_pais.children().remove();
			var pais_hmtl = '<option value="0" selected="yes">[-Seleccionar pais-]</option>';
			$.each(entry['paises'],function(entryIndex,pais){
				pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
			});
			$campo_select_pais.append(pais_hmtl);
                        
			var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar entidad--]</option>';
			$campo_select_estado.children().remove();
			$campo_select_estado.append(entidad_hmtl);
                        
			var localidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar municipio-]</option>';
			$campo_select_municipio.children().remove();
			$campo_select_municipio.append(localidad_hmtl);
                        
			//carga select estados al cambiar el pais
			$campo_select_pais.change(function(){
				var valor_pais = $(this).val();
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
				$arreglo = {'id_pais':valor_pais};
				$.post(input_json,$arreglo,function(entry){
					$campo_select_estado.children().remove();
					var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
					$.each(entry['Entidades'],function(entryIndex,entidad){
						entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
					});
					$campo_select_estado.append(entidad_hmtl);
					var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Localidad alternativa' + '</option>';
					$campo_select_municipio.children().remove();
					$campo_select_municipio.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
			
			//carga select municipios al cambiar el estado
			$campo_select_estado.change(function(){
				var valor_entidad = $(this).val();
				var valor_pais = $campo_select_pais.val();
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
				$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
				$.post(input_json,$arreglo,function(entry){
					$campo_select_municipio.children().remove();
					var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
					$.each(entry['Localidades'],function(entryIndex,mun){
						trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
					});
					$campo_select_municipio.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
		},"json");//termina llamada json
        
        //buscar cliente
        $busca_cliente.click(function(event){
			event.preventDefault();
			$busca_clientes($cliente.val());
        });
        
        $cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-clientsdf-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-clientsdf-overlay').fadeOut(remove);
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
							jAlert("La direccion  fue eliminada exitosamente", 'Atencion!');
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
			var form_to_show = 'formaDirecciones';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_clientsdf();
			$('#forma-clientsdf-window').css({"margin-left": -400, 	"margin-top": -290});
			
			$forma_selected.prependTo('#forma-clientsdf-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
                        
			var $id_proveedor = $('#forma-clientsdf-window').find('input[name=id_proveedor]');
                        var $campo_id = $('#forma-clientsdf-window').find('input[name=identificador]');
                        var $campo_proveedor = $('#forma-clientsdf-window').find('input[name=proveedor]');
                        //var $campo_id_proveedor = $('#forma-clientsdf-window').find('input[name=id_proveedor]');
                        var $campo_calle = $('#forma-clientsdf-window').find('input[name=calle]');
                        var $campo_entreCalles = $('#forma-clientsdf-window').find('input[name=entreCalles]');
                        var $campo_numeroInterior = $('#forma-clientsdf-window').find('input[name=numInterior]');
                        var $campo_numeroExterior = $('#forma-clientsdf-window').find('input[name=numExterior]');
                        var $campo_colonia = $('#forma-clientsdf-window').find('input[name=colonia]');
                        var $campo_select_pais = $('#forma-clientsdf-window').find('select[name=select_pais]');
                        var $campo_select_estado = $('#forma-clientsdf-window').find('select[name=select_estado]');
                        var $campo_select_municipio = $('#forma-clientsdf-window').find('select[name=select_municipio]');
                        var $campo_codigoPostal = $('#forma-clientsdf-window').find('input[name=codigoPostal]');
                        var $campo_telUno = $('#forma-clientsdf-window').find('input[name=telUno]');
                        var $campo_extUno = $('#forma-clientsdf-window').find('input[name=extUno]');
                        var $campo_telDos = $('#forma-clientsdf-window').find('input[name=telDos]');
                        var $campo_extDos = $('#forma-clientsdf-window').find('input[name=extDos]');   
                        
			var $cerrar_plugin = $('#forma-clientsdf-window').find('#close');
			var $cancelar_plugin = $('#forma-clientsdf-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-clientsdf-window').find('#submit');
		
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getclientsdf.json';
				$arreglo = {'id':id_to_show};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-clientsdf-overlay').fadeOut(remove);
						jAlert("La direccion del proveedor se Actiualizo correctamente.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-clientsdf-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
                                                for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 2 ){
								$('#forma-clientsdf-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
                                var hmtl_paises = ''; 
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$campo_proveedor.attr({'value' : entry['Direcciones']['0']['razon_social']});
                                        $id_proveedor.attr({'value' : entry['Direcciones']['0']['proveedor_id']});
                                        $campo_id.attr({'value' : entry['Direcciones']['0']['id']});
                                        $campo_calle.attr({'value' : entry['Direcciones']['0']['calle']});
                                        $campo_entreCalles.attr({'value' : entry['Direcciones']['0']['entre_calles']});
                                        $campo_numeroInterior.attr({'value' : entry['Direcciones']['0']['numero_interior']});
                                        $campo_numeroExterior.attr({'value' : entry['Direcciones']['0']['numero_exterior']});
                                        $campo_colonia.attr({'value' : entry['Direcciones']['0']['colonia']});
                                        $campo_codigoPostal.attr({'value' : entry['Direcciones']['0']['cp']});
                                        $campo_telUno.attr({'value' : entry['Direcciones']['0']['telefono1']});
                                        $campo_extUno.attr({'value' : entry['Direcciones']['0']['extension1']});
                                        $campo_telDos.attr({'value' : entry['Direcciones']['0']['telefono2']});
                                        $campo_extDos.attr({'value' : entry['Direcciones']['0']['extension2']});                                      
                        
                                        //carga select estados al cambiar el pais
                                        $campo_select_pais.change(function(){
                                                var valor_pais = $(this).val();
                                                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
                                                $arreglo = {'id_pais':valor_pais};
                                                $.post(input_json,$arreglo,function(entry){
                                                        $campo_select_estado.children().remove();
                                                        var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
                                                        $.each(entry['Entidades'],function(entryIndex,entidad){
                                                            entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
                                                        });
                                                        
                                                        $campo_select_estado.append(entidad_hmtl);
                                                        
                                                        var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Localidad alternativa' + '</option>';$campo_select_municipio.children().remove();
                                                        $campo_select_municipio.append(trama_hmtl_localidades);
                                                },"json");//termina llamada json
                                        });                                        
                                        
                                        $campo_select_pais.children().remove();
					var pais_hmtl = "";
					$.each(entry['paises'],function(entryIndex,pais){
						if(pais['cve_pais'] == entry['Direcciones']['0']['pais_id']){
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  selected="yes">' + pais['pais_ent'] + '</option>';
						}else{
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
						}
					});
                                        
                                          $campo_select_pais.append(pais_hmtl);
                                          
                                        //carga select municipios al cambiar el estado
                                        $campo_select_estado.change(function(){
                                                var valor_entidad = $(this).val();
                                                var valor_pais = $campo_select_pais.val();
                                                //alert("Pais: "+valor_pais+"    Entidad:"+valor_entidad);
                                                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
                                                $arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
                                                $.post(input_json,$arreglo,function(entry){
                                                        $campo_select_municipio.children().remove();
                                                        var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
                                                        $.each(entry['Localidades'],function(entryIndex,mun){
                                                                trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
                                                        });
                                                        $campo_select_municipio.append(trama_hmtl_localidades);
                                                },"json");//termina llamada json
                                        })
                                        
                                         //cargar estado
                                      $campo_select_estado.children().remove();
					var estado_hmtl = "";
					$.each(entry['estados'],function(entryIndex,estado){
						if(estado['cve_ent'] == entry['Direcciones']['0']['estado_id']){
							estado_hmtl += '<option value="' + estado['cve_ent'] + '"  selected="yes">' + estado['nom_ent'] + '</option>';
						}else{
							estado_hmtl += '<option value="' + estado['cve_ent'] + '"  >' + estado['nom_ent'] + '</option>';
						}
					});
                                        $campo_select_estado.append(estado_hmtl);                                        
                                        
                                         //cargar municipio
                                      $campo_select_municipio.children().remove();
					var municipio_hmtl = "";
					$.each(entry['municipios'],function(entryIndex,municipio){
						if(municipio['cve_mun'] == entry['Direcciones']['0']['municipio_id']){
							municipio_hmtl += '<option value="' + municipio['cve_mun'] + '"  selected="yes">' + municipio['nom_mun'] + '</option>';
						}else{
							municipio_hmtl += '<option value="' + municipio['cve_mun'] + '"  >' + municipio['nom_mun'] + '</option>';
						}
					});
                                        $campo_select_municipio.append(municipio_hmtl);
					
				},"json");//termina llamada json
                                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-clientsdf-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-clientsdf-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllClientsDf.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllClientsDf.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaDirecciones_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    $get_datos_grid();
    
});
