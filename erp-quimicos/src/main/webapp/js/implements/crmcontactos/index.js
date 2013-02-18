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
	var controller = $contextpath.val()+"/controllers/crmcontactos";
    
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
	$('#barra_titulo').find('#td_titulo').append('Contacto');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
        
        
	
	var $cadena_busqueda = "";
	var $busqueda_titulo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_titulo]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limbuscarpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
                valor_retorno += "descripcion" + signo_separador + $busqueda_titulo.val() + "|";
                //valor_retorno += "descripcion" + signo_separador + $busqueda_titulo.val()+ "|";
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
	
	$limbuscarpiar.click(function(event){
		event.preventDefault();
                $busqueda_titulo.val(' ');
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
	});
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-crmcontactos-window').find('#submit').mouseover(function(){
			$('#forma-crmcontactos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-crmcontactos-window').find('#submit').mouseout(function(){
			$('#forma-crmcontactos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-crmcontactos-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-crmcontactos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-crmcontactos-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-crmcontactos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-crmcontactos-window').find('#close').mouseover(function(){
                    $('#forma-crmcontactos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-crmcontactos-window').find('#close').mouseout(function(){
                    $('#forma-crmcontactos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-crmcontactos-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-crmcontactos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-crmcontactos-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-crmcontactos-window').find("ul.pestanas li").click(function() {
                    $('#forma-crmcontactos-window').find(".contenidoPes").hide();
                    $('#forma-crmcontactos-window').find("ul.pestanas li").removeClass("active");
                    var activeTab = $(this).find("a").attr("href");
                    $('#forma-crmcontactos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
                    $(this).addClass("active");
                    return false;
		});
                
	}
        
        
	//buscador de clientes
	$busca_clientes = function(){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscacliente();
		var $dialogoc =  $('#forma-crmcontactos-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_clientes').find('table.formaBusqueda_clientes').clone());
		$('#forma-buscacliente-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-crmcontactos-window').find('#tabla_resultado');
		var $busca_cliente_modalbox = $('#forma-crmcontactos-window').find('#busca_cliente_modalbox');
		var $cancelar_plugin_busca_cliente = $('#forma-crmcontactos-window').find('#cencela');
		
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
			$tabla_resultados.children().remove();
			
			var arreglo_parametros = {	cadena: $cadena_buscar.val(),
										filtro: $select_filtro_por.val(),
										iu:config.getUi()
									};
			var restful_json_service = config.getUrlForGetAndPost() + '/get_buscador_clientes.json'
			//alert(restful_json_service);
			var cliente="";
			$.post(restful_json_service,arreglo_parametros,function(entry){
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
				
				//seleccionar un cliente del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					$id_cliente_edo_cta.val($(this).find('#idclient').val());
					$razon_cli.val($(this).find('span.razon').html());
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente-overlay').fadeOut(remove);
				});
			});//termina llamada json
		});
		
		$cancelar_plugin_busca_cliente.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscacliente-overlay').fadeOut(remove);
		});
	}//termina buscador de clientes
        
        
	//nuevos puestos
	$new.click(function(event){
            
            event.preventDefault();
            var id_to_show = 0;
            $(this).modalPanel_CrmContactos();   //contacto al plug in 
            
            var form_to_show = 'formaCrmContactos';
            $('#' + form_to_show).each (function(){this.reset();});
            var $forma_selected = $('#' + form_to_show).clone();
            $forma_selected.attr({id : form_to_show + id_to_show});
            
            $('#forma-crmcontactos-window').css({"margin-left": -300, 	"margin-top": -200});
            $forma_selected.prependTo('#forma-crmcontactos-window');
            $forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
            $tabs_li_funxionalidad();
            
            //campos de la vista
            var $campo_id = $('#forma-crmcontactos-window').find('input[name=identificador]'); 
            
            var $tipo_contacto=$('#forma-crmcontactos-window').find('select[name=tipo_contacto]');
            var $folio = $('#forma-crmcontactos-window').find('input[name=folio]');
            var $rfc = $('#forma-crmcontactos-window').find('input[name=rfc]');
            var $id_cliente = $('#forma-crmcontactos-window').find('input[name=id_cliente]');
            var $razon_social = $('#forma-crmcontactos-window').find('input[name=razon_social]');
            
            var $nombre = $('#forma-crmcontactos-window').find('input[name=nombre]');
            var $apellido_paterno = $('#forma-crmcontactos-window').find('input[name=apellido_paterno]');
            var $apellido_materno = $('#forma-crmcontactos-window').find('input[name=apellido_materno]');
            var $telefono_2 = $('#forma-crmcontactos-window').find('input[name=telefono_2]');
            var $telefono_1 = $('#forma-crmcontactos-window').find('input[name=telefono_1]');
            var $fax = $('#forma-crmcontactos-window').find('input[name=fax]');
            var $telefono_directo = $('#forma-crmcontactos-window').find('input[name=telefono_directo]');
            var $correo_1 = $('#forma-crmcontactos-window').find('input[name=correo_1]');
            var $correo_2 = $('#forma-crmcontactos-window').find('input[name=correo_2]');
            
            var $busca_cliente = $('#lienzo_recalculable').find('table#busqueda tr td').find('a[href*=busca_cliente]');
            
            
            
            //botones
            var $cerrar_plugin = $('#forma-crmcontactos-window').find('#close');
            var $cancelar_plugin = $('#forma-crmcontactos-window').find('#boton_cancelar');
            var $submit_actualizar = $('#forma-crmcontactos-window').find('#submit');
            
            $campo_id.attr({'value' : 0});
            
            var respuestaProcesada = function(data){
                    if ( data['success'] == "true" ){
                        jAlert("El contacto fue dado de alta con exito", 'Atencion!');
                        var remove = function() {$(this).remove();};
                        $('#forma-crmcontactos-overlay').fadeOut(remove);

                        $get_datos_grid();
                    }else{
                        // Desaparece todas las interrogaciones si es que existen
                        $('#forma-crmcontactos-window').find('div.interrogacion').css({'display':'none'});
                        
                        var valor = data['success'].split('___');
                        //muestra las interrogaciones
                        for (var element in valor){
                            tmp = data['success'].split('___')[element];
                            longitud = tmp.split(':');
                            if( longitud.length > 1 ){
                                $('#forma-crmcontactos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                                .parent()
                                .css({'display':'block'})
                                .easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
                            }
                        }
                    }
            }
            var options = {dataType :  'json', success : respuestaProcesada};
            $forma_selected.ajaxForm(options);

            var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getContacto.json';
            var parametros={
                id:$campo_id.val(),
                iu: $('#lienzo_recalculable').find('input[name=iu]').val()
            }
            
            //alert($('#lienzo_recalculable').find('input[name=iu]').val())
            $.post(input_json,parametros,function(entry){
                
            });//termina llamada json
            
            
            $cerrar_plugin.bind('click',function(){
                var remove = function() {$(this).remove();};
                $('#forma-crmcontactos-overlay').fadeOut(remove);
            });
            
            $cancelar_plugin.click(function(event){
                var remove = function() {$(this).remove();};
                $('#forma-crmcontactos-overlay').fadeOut(remove);
                $buscar.trigger('click');
            });
	});
        
        var carga_formaClientsgrupos_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
                                    'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
                                    };
			jConfirm('Realmente desea eliminar el contacto seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El contacto fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El contacto  no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
            
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaCrmContactos';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_CrmMotivosLlamada();
			$('#forma-crmmotivosllamada-window').css({ "margin-left": -350, 	"margin-top": -200 });
			
			$forma_selected.prependTo('#forma-crmmotivosllamada-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $campo_id = $('#forma-crmmotivosllamada-window').find('input[name=identificador]');
			var $id =$('#forma-crmmotivosllamada-window').find('input[name=id]');
                        var $descripcion = $('#forma-crmmotivosllamada-window').find('textarea[name=descripcion]');
			
			var $cerrar_plugin = $('#forma-crmmotivosllamada-window').find('#close');
			var $cancelar_plugin = $('#forma-crmmotivosllamada-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-crmmotivosllamada-window').find('#submit');
			
			
		
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMotivoLlamada.json';
				
                                $arreglo = {
                                    
                                    id:id_to_show,
                                    iu: $('#lienzo_recalculable').find('input[name=iu]').val()
                                };
                                
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-crmmotivosllamada-overlay').fadeOut(remove);
						jAlert("El motivo de llamada se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-crmmotivosllamada-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 2 ){
								$('#forma-crmmotivosllamada-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
                                        $campo_id.val(entry['MLlamadas']['0']['id']);
					$id.attr({ 'value' : entry['MLlamadas']['0']['folio_mll'] }); 
					$descripcion.text(entry['MLlamadas']['0']['descripcion'] );
				},"json");//termina llamada json
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-crmmotivosllamada-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-crmmotivosllamada-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                        }
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getContactos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getContactos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaClientsgrupos_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});
