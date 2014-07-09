$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
    //arreglo para select tipo de Ajuste
    var arrayTiposAjuste = {
				0:"Positivo", //grupo Entradas en la tabla tipos de movimiento de Invetario
				2:"Negativo",//grupo salidas en la tabla tipos de movimiento de Invetario
			};
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/invcarga";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    var $new = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseover(function(){
		$(this).removeClass("onmouseOutNewItem").addClass("onmouseOverNewItem");
	});
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseout(function(){
		$(this).removeClass("onmouseOverNewItem").addClass("onmouseOutNewItem");
	});
	
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Carga de inventario f&iacute;sico');
	
	$('#barra_buscador').hide();
	
	
	
	
    
	
	$tabs_li_funxionalidad = function(){
		var $select_prod_tipo = $('#forma-invcarga-window').find('select[name=prodtipo]');
		$('#forma-invcarga-window').find('#submit').mouseover(function(){
			$('#forma-invcarga-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-invcarga-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		})
		$('#forma-invcarga-window').find('#submit').mouseout(function(){
			$('#forma-invcarga-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-invcarga-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		})
		$('#forma-invcarga-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-invcarga-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-invcarga-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-invcarga-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-invcarga-window').find('#close').mouseover(function(){
			$('#forma-invcarga-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		})
		$('#forma-invcarga-window').find('#close').mouseout(function(){
			$('#forma-invcarga-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		})
		
		$('#forma-invcarga-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-invcarga-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-invcarga-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-invcarga-window').find("ul.pestanas li").click(function() {
			$('#forma-invcarga-window').find(".contenidoPes").hide();
			$('#forma-invcarga-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-invcarga-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
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
	
	
	
	
	$aplicar_evento_keypress = function( $campo_input ){
		//validar campo cantidad recibida, solo acepte numeros y punto
		$campo_input.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if(e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
	}
	
	$aplicar_evento_focus_input = function( $campo_input ){
		$campo_input.focus(function(e){
			if($(this).val() == ' ' || parseFloat($(this).val()) <= 0){
				$(this).val('');
			}
		});
	}
	
	$aplicar_evento_blur_input = function( $campo_input ){
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_input.blur(function(e){
			if(parseFloat($campo_input.val())==0 || $campo_input.val()==""){
				$campo_input.val(0);
				$campo_input.val(parseFloat($campo_input.val()).toFixed(2))
			}
		});
	}
	
	
	
	
	$aplicar_evento_focus = function( $campo_input ){
		$campo_input.focus(function(e){
			if($(this).val() == ' '){
				$(this).val('');
			}
		});
	}
	
	
	$aplicar_evento_blur = function( $campo_input ){
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_input.blur(function(e){
			if($campo_input.val()=='0' || $campo_input.val()==""){
				$campo_input.val(' ');
			}
		});
	}
	
	//funcion para aplicar metodo click para eliminar tr
	$aplicar_evento_eliminar = function( $campo_href ){
		//eliminar un lote
		$campo_href.click(function(e){
			e.preventDefault();
			$tr_padre=$(this).parent().parent();
			//$tr_padre.find('input').val('');//asignar vacio a todos los input del tr
			//$tr_padre.find('input[name=eliminado]').val('0');//asignamos 0 para indicar que se ha eliminado
			$tr_padre.remove();//eliminar el tr
			
			$grid_productos = $tr_padre.parent();
			
			
		});
	}
	
	

	
	
	
	
	//nuevo 
	$new.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_invcarga();
		
		var form_to_show = 'formainvcarga00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$('#forma-invcarga-window').css({"margin-left": -200, 	"margin-top": -210});
		
		$forma_selected.prependTo('#forma-invcarga-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCargar.json';
		$arreglo = {'identificador':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
        
		var $identificador = $('#forma-invcarga-window').find('input[name=identificador]');
		var $folio = $('#forma-invcarga-window').find('input[name=folio]');
		var $exis_pres = $('#forma-invcarga-window').find('input[name=exis_pres]');
		
		
		var $select_almacen = $('#forma-invcarga-window').find('select[name=select_almacen]');
		var $descargar_formato = $('#forma-invcarga-window').find('#descargar_formato');
		var $seleccionar_archivo = $('#forma-invcarga-window').find('#seleccionar_archivo');
		var $nombre_archivo = $('#forma-invcarga-window').find('#nombre_archivo');
		
		
		var $tipo_ajuste = $('#forma-invcarga-window').find('input[name=tipo_ajuste]');
		
		var $fecha_ajuste = $('#forma-invcarga-window').find('input[name=fecha_ajuste]');
		var $select_tipo_mov = $('#forma-invcarga-window').find('select[name=select_tipo_mov]');
		var $id_tipo_mov = $('#forma-invcarga-window').find('input[name=id_tipo_mov]');
		
		var $tipo_costo = $('#forma-invcarga-window').find('input[name=tipo_costo]');
		var $select_almacen = $('#forma-invcarga-window').find('select[name=select_almacen]');
		var $id_almacen = $('#forma-invcarga-window').find('input[name=id_almacen]');
		
		var $observaciones = $('#forma-invcarga-window').find('textarea[name=observaciones]');
		
		var $sku_producto = $('#forma-invcarga-window').find('input[name=sku_producto]');
		var $nombre_producto = $('#forma-invcarga-window').find('input[name=nombre_producto]');
		
		//buscar producto
		var $busca_sku = $('#forma-invcarga-window').find('a[href*=busca_sku]');
		//href para agregar producto al grid
		var $agregar_producto = $('#forma-invcarga-window').find('a[href*=agregar_producto]');
		var $descargarpdf = $('#forma-invcarga-window').find('#descargarpdf');
		
		//grid de productos
		var $grid_productos = $('#forma-invcarga-window').find('#grid_productos');
		//grid de errores
		var $grid_warning = $('#forma-invcarga-window').find('#div_warning_grid').find('#grid_warning');
		
		
		var $cerrar_plugin = $('#forma-invcarga-window').find('#close');
		var $cancelar_plugin = $('#forma-invcarga-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-invcarga-window').find('#submit');
		
		//$campo_factura.css({'background' : '#ffffff'});
		
		//ocultar boton de facturar y descargar pdf. Solo debe estar activo en editar
		$descargarpdf.attr('disabled','-1');
		$identificador.val(0);//para nueva pedido el id es 0
		
		$folio.css({'background' : '#F0F0F0'});
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("El Ajuste se guard&oacute; con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-invcarga-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				//habilitar boton actualizar
				$submit_actualizar.removeAttr('disabled');
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-invcarga-window').find('.invcarga_div_one').css({'height':'550px'});//con errores
				$('#forma-invcarga-window').find('div.interrogacion').css({'display':'none'});
				$grid_productos.find('input[name=cant_ajuste]').css({'background' : '#ffffff'});
				$grid_productos.find('input[name=costo_ajuste]').css({'background' : '#ffffff'});
				
				$('#forma-invcarga-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-invcarga-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-invcarga-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						var campo = tmp.split(':')[0];
						
						$('#forma-invcarga-window').find('#div_warning_grid').css({'display':'block'});
						var $campo = $grid_productos.find('.'+campo).css({'background' : '#d41000'});
						
						var codigo_producto = $campo.parent().parent().find('input[name=codigo]').val();
						var titulo_producto = $campo.parent().parent().find('input[name=nombre]').val();
						
						var tr_warning = '<tr>';
								tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
								tr_warning += '<td width="150"><INPUT TYPE="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:150px; color:red"></td>';
								tr_warning += '<td width="250"><INPUT TYPE="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:250px; color:red"></td>';
								tr_warning += '<td width="380"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:350px; color:red"></td>';
						tr_warning += '</tr>';
						
						$('#forma-invcarga-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
						
					}
				}
				$('#forma-invcarga-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
				$('#forma-invcarga-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		//$.getJSON(json_string,function(entry){
		$.post(input_json,$arreglo,function(entry){
			
			
			
			//carga select Con los almacenes
			$select_almacen.children().remove();
			var alm_hmtl = '<option value="0">Todos</option>';
			$.each(entry['Alms'],function(entryIndex,alm){
				alm_hmtl += '<option value="' + alm['id'] + '"  >' + alm['titulo'] + '</option>';
			});
			$select_almacen.append(alm_hmtl);
			
			
			/*
			$fecha_ajuste.val(entry['AnoActual'][0]['fecha_actual']);
			$exis_pres.val(entry['Par'][0]['exis_pres']);
			
			$select_tipo_ajuste.children().remove();
			var select_html = '';
			for(var i in arrayTiposAjuste){
				select_html += '<option value="' + i + '" >' + arrayTiposAjuste[i] + '</option>';
			}
			$select_tipo_ajuste.append(select_html);
			
			//guardamos el tipo de ajuste
			$tipo_ajuste.val($select_tipo_ajuste.val());
			
			
			//carga select con los tipos de Movimientos
			$select_tipo_mov.children().remove();
			var tmov_hmtl = '';
			$.each(entry['TMov'],function(entryIndex,mov){
				if(parseInt($select_tipo_ajuste.val())==parseInt(mov['grupo'])){
					tmov_hmtl += '<option value="' + mov['id'] + '"  >' + mov['titulo'] + '</option>';
				}
			});
			$select_tipo_mov.append(tmov_hmtl);
			$id_tipo_mov.val($select_tipo_mov.val());
			
			
			//tomar el tipo de Costo que se utilizara de acuerdo al tipo de movimiento selecionado
			//el tipo de costo en la vista define si el campo costo ajuste debe estar habilitado para edicion o no.
			$.each(entry['TMov'],function(entryIndex,mov){
				if(parseInt($select_tipo_mov.val())==parseInt(mov['id'])){
					$tipo_costo.val(mov['tipo_costo']);
				}
			});
			
			
			//carga select con todos los Almacenes de la Empresa
			$select_almacen.children().remove();
			var almacen_hmtl = '';
			$.each(entry['Almacenes'],function(entryIndex,almacen){
				almacen_hmtl += '<option value="' + almacen['id'] + '"  >' + almacen['titulo'] + '</option>';
			});
			$select_almacen.append(almacen_hmtl);
			$id_almacen.val($select_almacen.val());
			
			
			//cambiar tipo de ajuste
			$select_tipo_ajuste.change(function(){
				var valor_tipo = $(this).val();
				var valor_tipo_anterior = $tipo_ajuste.val();//tomamos el valor anterior
				
				//0:"Positivo", //grupo Entradas en la tabla tipos de movimiento de Invetario
				//2:"Negativo",//grupo salidas en la tabla tipos de movimiento de Invetario
				
				if (parseInt($("tr", $grid_productos).size()) > 0 ){
					//aqui regresamos el tipo seleccionado anteriormente porque hay productos en el grid
					$select_tipo_ajuste.children().remove();
					var select_html = '';
					for(var i in arrayTiposAjuste){
						if(valor_tipo_anterior == i){
							select_html += '<option value="' + i + '" selected="yes">' + arrayTiposAjuste[i] + '</option>';
						}else{
							select_html += '<option value="' + i + '">' + arrayTiposAjuste[i] + '</option>';
						}
					}
					$select_tipo_ajuste.append(select_html);
					jAlert("No es posible cambiar el Tipo de Ajuste mientras existan productos en el listado.", 'Atencion!');
				}else{
					$select_tipo_mov.children().remove();
					var tmov_hmtl = '';
					$.each(entry['TMov'],function(entryIndex,mov){
						if(parseInt(valor_tipo)==parseInt(mov['grupo'])){
							tmov_hmtl += '<option value="' + mov['id'] + '"  >' + mov['titulo'] + '</option>';
							$tipo_costo.val(mov['tipo_costo']);
						}
					});
					$select_tipo_mov.append(tmov_hmtl);
					$tipo_ajuste.val(valor_tipo);//tomamos el valor del nuevo tipo seleccionado
					$id_tipo_mov.val($select_tipo_mov.val());
				}
			});
			
			
			//cambiar el tipo de Movimiento
			$select_tipo_mov.change(function(){
				var valor_tipo = $(this).val();
				var valor_tipo_mov_anterior = $id_tipo_mov.val();//tomamos el valor anterior
				
				if (parseInt($("tr", $grid_productos).size()) > 0 ){
					$select_tipo_mov.children().remove();
					var tmov_hmtl = '';
					$.each(entry['TMov'],function(entryIndex,mov){
						if(parseInt($select_tipo_ajuste.val())==parseInt(mov['grupo'])){
							if(parseInt(valor_tipo_mov_anterior)==parseInt(mov['id'])){
								tmov_hmtl += '<option value="' + mov['id'] + '" selected="yes">' + mov['titulo'] + '</option>';
							}else{
								tmov_hmtl += '<option value="' + mov['id'] + '"  >' + mov['titulo'] + '</option>';
							}
						}
					});
					$select_tipo_mov.append(tmov_hmtl);
					
					jAlert("No es posible cambiar el Tipo de Movimiento mientras existan productos en el listado.", 'Atencion!');
				}else{
					$id_tipo_mov.val(valor_tipo);//guardamos el nuevo tipo seleccionado
					
					//tomar el tipo de Costo que se utilizara de acuerdo al tipo de movimiento selecionado
					//el tipo de costo en la vista define si el campo costo ajuste debe estar habilitado para edicion o no.
					$.each(entry['TMov'],function(entryIndex,mov){
						if(parseInt($select_tipo_mov.val())==parseInt(mov['id'])){
							$tipo_costo.val(mov['tipo_costo']);
						}
					});
					
					
					
				}
			});
			
			
			
			
			
			//cambiar el almacen
			$select_tipo_mov.change(function(){
				var valor_tipo = $(this).val();
				var valor_tipo_mov_anterior = $id_tipo_mov.val();//tomamos el valor anterior
				
				if (parseInt($("tr", $grid_productos).size()) > 0 ){
					$select_tipo_mov.children().remove();
					var tmov_hmtl = '';
					$.each(entry['TMov'],function(entryIndex,mov){
						if(parseInt($select_tipo_ajuste.val())==parseInt(mov['grupo'])){
							if(parseInt(valor_tipo_mov_anterior)==parseInt(mov['id'])){
								tmov_hmtl += '<option value="' + mov['id'] + '" selected="yes">' + mov['titulo'] + '</option>';
							}else{
								tmov_hmtl += '<option value="' + mov['id'] + '"  >' + mov['titulo'] + '</option>';
							}
						}
					});
					$select_tipo_mov.append(tmov_hmtl);
					
					jAlert("No es posible cambiar el Tipo de Movimiento mientras existan productos en el listado.", 'Atencion!');
					
				}else{
					$id_tipo_mov.val(valor_tipo);//guardamos el nuevo tipo seleccionado
				}
			});
			
			
			
			//cambiar el almacen
			$select_almacen.change(function(){
				var valor_alm = $(this).val();
				var valor_alm_anterior = $id_almacen.val();//tomamos el valor anterior
				
				if (parseInt($("tr", $grid_productos).size()) > 0 ){
					$select_almacen.children().remove();
					var almacen_hmtl = '';
					$.each(entry['Almacenes'],function(entryIndex,almacen){
						if(parseInt(valor_alm_anterior)==parseInt(almacen['id'])){
							almacen_hmtl += '<option value="' + almacen['id'] + '" selected="yes">' + almacen['titulo'] + '</option>';
						}else{
							almacen_hmtl += '<option value="' + almacen['id'] + '"  >' + almacen['titulo'] + '</option>';
						}
					});
					$select_almacen.append(almacen_hmtl);
					jAlert("No es posible cambiar el Tipo de Movimiento mientras existan productos en el listado.", 'Atencion!');
				}else{
					$id_almacen.val(valor_alm);//guardamos el nuevo id de almacen seleccionado
				}
			});
			
			
			
			//agregar producto al grid
			$agregar_producto.click(function(event){
				event.preventDefault();
				var codigo_producto = $sku_producto.val();
				var id_alm = $select_almacen.val();
				var fecha = $fecha_ajuste.val();
				var tipo_costo = $tipo_costo.val();
				var exisPres = $exis_pres.val();
				
				//alert("tipo_costo:"+$tipo_costo.val());
				$buscador_datos_producto($grid_productos, codigo_producto, id_alm, fecha, tipo_costo, exisPres, $select_tipo_ajuste);
				
			});
			*/
			
		},"json");//termina llamada json
		
		
		
		/*
		var $select_almacen = $('#forma-invcarga-window').find('select[name=select_almacen]');
		var $descargar_formato = $('#forma-invcarga-window').find('#descargar_formato');
		var $buttonSeleccionarArchivo = $('#forma-invcarga-window').find('#seleccionar_archivo');
		var $nombre_archivo = $('#forma-invcarga-window').find('#nombre_archivo');
		*/
		
		
		
		/*Codigo para subir la xls*/
		var input_json_upload = document.location.protocol + '//' + document.location.host + '/'+controller+'/fileUpload.json';
		var $buttonSeleccionarArchivo = $('#forma-invcarga-window').find('#seleccionar_archivo'), interval;
		new AjaxUpload($buttonSeleccionarArchivo,{
			action: input_json_upload, 
			name: 'file',
			onSubmit : function(file , ext){
				if ((/^(xls)$/.test(ext)) || (/^(xlsx)$/.test(ext))){
					$buttonSeleccionarArchivo.text('Cargando..');
					this.disable();
				} else {
					jAlert("Fichero no valido.", 'Atencion!');
					return false;
				}
			},
			onComplete: function(file, response){
				//button_pdf.text('Cambiar PDF');
				window.clearInterval(interval);
				this.enable();
				$nombre_archivo.val(file);
				if(response=="true"){
					
				}
				//$edito_pdf.val(1);
				//$('#forma-invcarga-window').find('#contenidofile_pdf').text(file);
			},
		});
		
		




		$descargar_formato.click(function(event){
			event.preventDefault();
			
			var cadena = $select_almacen.val();
			
			var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller + '/getFormato/'+cadena+'/'+iu+'/out.json'
			window.location.href=input_json;
			
		});//termina llamada json



		
		
		//deshabilitar tecla enter  en todo el plugin
		$('#forma-invcarga-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		
				
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_productos).size();
			if(parseInt(trCount) > 0){
				return true;
			}else{
				jAlert("No hay productos para ajuste.", 'Atencion!');
				return false;
			}
		});
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-invcarga-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-invcarga-overlay').fadeOut(remove);
		});
		
	});
	
	

    
    
});



