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
	var controller = $contextpath.val()+"/controllers/facpar";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    $('#barra_acciones').hide();
	//var $new_facpar = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	//var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
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
	$('#barra_titulo').find('#td_titulo').append('Configuraci&oacute;n de par&aacute;metros de Facturaci&oacute;n');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $select_sucursal_buscador = $('#barra_buscador').find('.tabla_buscador').find('select[name=select_sucursal_buscador]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "iu" + signo_separador + $('#lienzo_recalculable').find('input[name=iu]').val() + "|";
		return valor_retorno;
	};
	
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	//$cadena_busqueda = cadena;
	
	
	
	
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-facpar-window').find('#submit').mouseover(function(){
			$('#forma-facpar-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-facpar-window').find('#submit').mouseout(function(){
			$('#forma-facpar-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-facpar-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-facpar-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-facpar-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-facpar-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-facpar-window').find('#close').mouseover(function(){
			$('#forma-facpar-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-facpar-window').find('#close').mouseout(function(){
			$('#forma-facpar-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-facpar-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-facpar-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-facpar-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-facpar-window').find("ul.pestanas li").click(function() {
			$('#forma-facpar-window').find(".contenidoPes").hide();
			$('#forma-facpar-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-facpar-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	
    //arreglo para select opcion
    var array_incluye_iva = {
				'false':"No incluye", 
				'true':"Si incluye"
			};
	
	//carga los campos select con los datos que recibe como parametro
	$carga_campos_select = function($campo_select, arreglo_elementos, elemento_seleccionado, texto_elemento_cero, indice1, indice2){
		var option_hmtl='';
		
		if(texto_elemento_cero ==''){
			option_hmtl='';
		}else{
			option_hmtl='<option value="0">'+texto_elemento_cero+'</option>';			
		}
		
		$campo_select.children().remove();
		$.each(arreglo_elementos,function(entryIndex,indice){
			if( parseInt(indice[indice1]) == parseInt(elemento_seleccionado) ){
				option_hmtl += '<option value="' + indice[indice1] + '" selected="yes">' + indice[indice2] + '</option>';
			}else{
				option_hmtl += '<option value="' + indice[indice1] + '"  >' + indice[indice2] + '</option>';
			}
		});
		$campo_select.append(option_hmtl);
	}
	
	
	
	
	var carga_formafacpar_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'identificador':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar Par&aacute;metro seleccionado.', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Par&aacute;metro fue eliminado exitosamente.", 'Atencion!');
							$get_datos_grid();
						}else{
							jAlert("El Par&aacute;metro no pudo ser eliminado.", 'Atencion!');
						}
					},"json");
				}
			});
            
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formafacpar';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_facpar();
			$('#forma-facpar-window').css({ "margin-left": -350, 	"margin-top": -200 });
			
			$forma_selected.prependTo('#forma-facpar-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $identificador = $('#forma-facpar-window').find('input[name=identificador]');
			var $identificador_suc = $('#forma-facpar-window').find('input[name=identificador_suc]');
			var $sucursal = $('#forma-facpar-window').find('input[name=sucursal]');
			
			var $correo_envio = $('#forma-facpar-window').find('input[name=correo_envio]');
			var $passwd_correo_envio = $('#forma-facpar-window').find('input[name=passwd_correo_envio]');
			var $passwd2_correo_envio = $('#forma-facpar-window').find('input[name=passwd2_correo_envio]');
			var $servidor_correo_envio = $('#forma-facpar-window').find('input[name=servidor_correo_envio]');
			var $puerto_correo_envio = $('#forma-facpar-window').find('input[name=puerto_correo_envio]');
			
			var $cerrar_plugin = $('#forma-facpar-window').find('#close');
			var $cancelar_plugin = $('#forma-facpar-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-facpar-window').find('#submit');
			
			if(accion_mode == 'edit'){
				
				
				/*
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getParametro.json';
				$arreglo = {	'id':id_to_show,
								'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-facpar-overlay').fadeOut(remove);
						jAlert("Los datos del Par&aacute;metro se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-facpar-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-facpar-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
								
								if( tmp.split(':')[0] == 'integridad' ){
									jAlert(tmp.split(':')[1], 'Atencion!');
								}
								
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$campo_id.attr({ 'value' : entry['Parametro'][0]['id'] });
					$check_requiere_oc.attr('checked', (entry['Parametro'][0]['oc_requerida'] == 'true')? true:false );
					
					
					//cargar select de Sucursales
					$select_sucursal.children().remove();
					var suc_hmtl = '';
					$.each(entry['Sucursales'],function(entryIndex,suc){
						if(parseInt(entry['Parametro'][0]['gral_suc_id'])==parseInt(suc['id'])){
							suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">' + suc['titulo'] + '</option>';
						}else{
							//suc_hmtl += '<option value="' + suc['id'] + '">' + suc['titulo'] + '</option>';
						}
					});
					$select_sucursal.append(suc_hmtl);
					
					
					//cargar select de anticipo
					elemento_seleccionado = entry['Parametro'][0]['cxp_mov_tipo_id'];
					texto_elemento_cero='Seleccionar Movimiento';
					$carga_campos_select($select_anticipo, entry['TMovanticipo'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");
					
					//cargar select de Aplicado Anticipo
					elemento_seleccionado = entry['Parametro'][0]['cxp_mov_tipo_id_apl_ant'];
					texto_elemento_cero='Seleccionar Movimiento';
					$carga_campos_select($select_apl_anticipo, entry['TMovAplanticipo'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");
					
					//cargar select de Aplicado Factura
					elemento_seleccionado = entry['Parametro'][0]['cxp_mov_tipo_id_apl_fac'];
					texto_elemento_cero='Seleccionar Movimiento';
					$carga_campos_select($select_apl_factura, entry['TMovAplfactura'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");						
					
					//cargar select de mov Cancelacion
					elemento_seleccionado = entry['Parametro'][0]['cxp_mov_tipo_id_can'];
					texto_elemento_cero='Seleccionar Movimiento';
					$carga_campos_select($select_cacelacion, entry['TMovCancelacion'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");						
					
					var option_hmtl='';
					$select_incluye_iva.children().remove();
					if(entry['Parametro'][0]['incluye_iva'] == 'true' ){
						option_hmtl += '<option value="false">No incluye</option>';
						option_hmtl += '<option value="true" selected="yes">Si incluye</option>';
					}else{
						option_hmtl += '<option value="false" selected="yes">No incluye</option>';
						option_hmtl += '<option value="true">Si incluye</option>';
					}
					$select_incluye_iva.append(option_hmtl);
					
					
					//cargar select de consecutivos de sucursales
					elemento_seleccionado = entry['Parametro'][0]['gral_suc_id_consecutivo'];
					texto_elemento_cero='Sucursal Actual';
					$carga_campos_select($select_consecutivo_sucursal, entry['Consecutivo'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");
				},"json");//termina llamada json
				      
				
				*/
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-facpar-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-facpar-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                          
                          
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllFacPar.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllFacPar.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formafacpar_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



