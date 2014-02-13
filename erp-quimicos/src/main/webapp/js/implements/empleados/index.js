$(function() {
	//var controller = "com.mycompany_Kemikal_war_1.0-SNAPSHOT/controllers/empleados";
	//var controller = "controllers/empleados";

    //arreglo para select Base Precio
    var array_dias_semana = {
				1:"Domingo",
				2:"Lunes",
				3:"Martes",
				4:"Miercoles",
				5:"Jueves",
				6:"Viernes",
				7:"Sabado"
			};
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
	var controller = $contextpath.val()+"/controllers/empleados";

        //Barra para las acciones
        $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
        $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_cliente = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');

	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Empleados');

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
	html+='<option value="1">No.de Empleado</option>';
	html+='<option value="2">Nombre Empleado</option>';
	html+='<option value="3">CURP</option>';
        html+='<option value="4">Puesto</option>';
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
                $get_datos_grid();
	});


	/*
	//visualizar  la barra del buscador
	$visualiza_buscador.click(function(event){
		event.preventDefault();
         $('#barra_buscador').toggle( 'blind');
	});
	*/

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
		$('#forma-empleados-window').find('#submit').mouseover(function(){
			$('#forma-empleados-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-prefacturas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});

		$('#forma-empleados-window').find('#submit').mouseout(function(){
			$('#forma-empleados-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-prefacturas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});

		$('#forma-empleados-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-empleados-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})

		$('#forma-empleados-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-empleados-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});

		$('#forma-empleados-window').find('#close').mouseover(function(){
			$('#forma-empleados-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});

		$('#forma-empleados-window').find('#close').mouseout(function(){
			$('#forma-empleados-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});


		$('#forma-empleados-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-empleados-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-empleados-window').find(".contenidoPes:first").show(); //Show first tab content

		//On Click Event
		$('#forma-empleados-window').find("ul.pestanas li").click(function() {
			$('#forma-empleados-window').find(".contenidoPes").hide();
			$('#forma-empleados-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-empleados-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");

			if(activeTab == '#tabx-1'){
                            if($('#forma-empleados-window').find('input[name=consignacion]').is(':checked')){
                                    $('#forma-empleados-window').find('#div_consignacion_grid').css({'display':'block'});
                                    $('#forma-empleados-window').find('.empleados_div_one').css({'height':'165px'});
                                    $('#forma-empleados-window').find('.empleados_div_one').css({'width':'810px'});
                                    $('#forma-empleados-window').find('.empleados_div_two').css({'width':'810px'});
                                    $('#forma-empleados-window').find('.empleados_div_three').css({'width':'800px'});
                                    $('#forma-empleados-window').find('#cierra').css({'width':'765px'});
                                    $('#forma-empleados-window').find('#botones').css({'width':'790px'});
                            }else{
                                    $('#forma-empleados-window').find('#div_consignacion_grid').css({'display':'none'});
                                    $('#forma-empleados-window').find('.empleados_div_one').css({'height':'370px'});
                                    $('#forma-empleados-window').find('.empleados_div_one').css({'width':'810px'});
                                    $('#forma-empleados-window').find('.empleados_div_two').css({'width':'810px'});
                                    $('#forma-empleados-window').find('.empleados_div_three').css({'width':'800px'});
                                    $('#forma-empleados-window').find('#cierra').css({'width':'765px'});
                                    $('#forma-empleados-window').find('#botones').css({'width':'790px'});
                            }

			}
			if(activeTab == '#tabx-2'){
				$('#forma-empleados-window').find('.empleados_div_one').css({'height':'290px'});
				$('#forma-empleados-window').find('.empleados_div_three').css({'height':'270px'});
				$('#forma-empleados-window').find('.empleados_div_one').css({'width':'810px'});
				$('#forma-empleados-window').find('.empleados_div_two').css({'width':'810px'});
				$('#forma-empleados-window').find('.empleados_div_three').css({'width':'800px'});
				$('#forma-empleados-window').find('#cierra').css({'width':'765px'});
				$('#forma-empleados-window').find('#botones').css({'width':'790px'});
			}
			if(activeTab == '#tabx-3'){
				$('#forma-empleados-window').find('.empleados_div_one').css({'height':'240px'});
				$('#forma-empleados-window').find('.empleados_div_three').css({'height':'220px'});
				$('#forma-empleados-window').find('.empleados_div_one').css({'width':'810px'});
				$('#forma-empleados-window').find('.empleados_div_two').css({'width':'810px'});
				$('#forma-empleados-window').find('.empleados_div_three').css({'width':'800px'});
				$('#forma-empleados-window').find('#cierra').css({'width':'765px'});
				$('#forma-empleados-window').find('#botones').css({'width':'790px'});
			}
			if(activeTab == '#tabx-4'){
				$('#forma-empleados-window').find('.empleados_div_one').css({'height':'460px'});
				//$('#forma-empleados-window').find('.empleados_div_three').css({'height':'320px'});
				$('#forma-empleados-window').find('.empleados_div_one').css({'width':'810px'});
				$('#forma-empleados-window').find('.empleados_div_two').css({'width':'810px'});
				$('#forma-empleados-window').find('.empleados_div_three').css({'width':'800px'});
				$('#forma-empleados-window').find('#cierra').css({'width':'765px'});
				$('#forma-empleados-window').find('#botones').css({'width':'790px'});
			}
			if(activeTab == '#tabx-5'){
				$('#forma-empleados-window').find('.empleados_div_one').css({'height':'255px'});
				//$('#forma-empleados-window').find('.empleados_div_three').css({'height':'270px'});
				$('#forma-empleados-window').find('.empleados_div_one').css({'width':'810px'});
				$('#forma-empleados-window').find('.empleados_div_two').css({'width':'810px'});
				$('#forma-empleados-window').find('.empleados_div_three').css({'width':'800px'});
				$('#forma-empleados-window').find('#cierra').css({'width':'765px'});
				$('#forma-empleados-window').find('#botones').css({'width':'790px'});
			}
			if(activeTab == '#tabx-6'){
				$('#forma-empleados-window').find('.empleados_div_one').css({'height':'200px'});
				$('#forma-empleados-window').find('.empleados_div_three').css({'height':'225px'});
				$('#forma-empleados-window').find('.empleados_div_one').css({'width':'810px'});
				$('#forma-empleados-window').find('.empleados_div_two').css({'width':'810px'});
				$('#forma-empleados-window').find('.empleados_div_three').css({'width':'800px'});
				$('#forma-empleados-window').find('#cierra').css({'width':'765px'});
				$('#forma-empleados-window').find('#botones').css({'width':'790px'});
			}
			return false;
		});

	}





	//carga los campos select con los datos que recibe como parametro
	$carga_campos_select = function($campo_select, arreglo_elementos, elemento_seleccionado, texto_elemento_cero){
		$campo_select.children().remove();
		var select_html = '<option value="0">'+texto_elemento_cero+'</option>';
		for(var i in arreglo_elementos){
			if( parseInt(i) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + i + '" selected="yes">' + arreglo_elementos[i] + '</option>';
			}else{
				select_html += '<option value="' + i + '"  >' + arreglo_elementos[i] + '</option>';
			}
		}
		$campo_select.append(select_html);
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
	
	$add_ceros = function($campo){
		$campo.val(0);
		$campo.val(parseFloat($campo.val()).toFixed(2));
	}
	
	$accion_focus = function($campo){
		//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$campo.focus(function(e){
			$valor_tmp = $(this).val().split(",").join("");
			
			if( ($valor_tmp != '') && ($valor_tmp != ' ') && ($valor_tmp != null) ){
				if(parseFloat($valor_tmp)<1){
					$(this).val('');
				}else{
					$(this).val($valor_tmp);
				}
			}
		});
	}
	
	$accio_blur = function($campo){
		//recalcula importe al perder enfoque el campo costo
		$campo.blur(function(){
			$valor_tmp = $(this).val().split(",").join("");
			
			if ($valor_tmp == ''  || $valor_tmp == null){
				$(this).val(0);
			}else{
				$(this).val($valor_tmp);
			}
			
			$(this).val( $(this).agregar_comas(parseFloat($(this).val()).toFixed(2)) );
		});
	}


	//nuevo cliente
	$new_cliente.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel();
		
		//aqui entra nuevo
		var form_to_show = 'formaEmpleados00';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });

		//alert("si entra");
		$('#forma-empleados-window').css({ "margin-left": -400, 	"margin-top": -290 });
		$forma_selected.prependTo('#forma-empleados-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		//alert("si pasa");
		$tabs_li_funxionalidad();

		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_empleado.json';
		$arreglo = {'id':id_to_show,
					'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
                };
                
		//tab 1 Datos personales
		var $total_tr = $('#forma-empleados-window').find('input[name=total_tr]');
		var $campo_empleado_id = $('#forma-empleados-window').find('input[name=identificador_empleado]');
		var $identificador=$('#forma-empleados-window').find('input[name=identificador]');
		var $campo_num_empleado=$('#forma-empleados-window').find('input=[name=empleado_id]');
		var $campo_nombre = $('#forma-empleados-window').find('input[name=nombre]');
		var $campo_appaterno= $('#forma-empleados-window').find('input[name=appaterno]');
		var $campo_apmaterno = $('#forma-empleados-window').find('input[name=apmaterno]');
		var $campo_imss = $('#forma-empleados-window').find('input[name=imss]');
		var $clave_infonavit = $('#forma-empleados-window').find('input[name=infonavit]');
		var $campo_curp = $('#forma-empleados-window').find('input[name=curp]');
		var $campo_rfc= $('#forma-empleados-window').find('input[name=rfc]');
		var $campo_fecha_nacimiento = $('#forma-empleados-window').find('input[name=f_nacimiento]');
		var $campo_fecha_ingreso = $('#forma-empleados-window').find('input[name=f_ingreso]');
		var $select_escolaridad= $('#forma-empleados-window').find('select[name=escolaridad]');
		var $select_genero_sexo= $('#forma-empleados-window').find('select[name=sexo]');
		var $select_edo_civil = $('#forma-empleados-window').find('select[name=edocivil]');
		var $select_religion = $('#forma-empleados-window').find('select[name=religion]');
		
		// tab 2 Direccion y contacto
		var $campo_telefono = $('#forma-empleados-window').find('input[name=telefono]');
		var $campo_movil = $('#forma-empleados-window').find('input[name=movil]');
		var $campo_correo_personal = $('#forma-empleados-window').find('input[name=correo_personal]');
		var $select_pais = $('#forma-empleados-window').find('select[name=pais]');
		var $select_entidad = $('#forma-empleados-window').find('select[name=estado]');
		var $select_localidad = $('#forma-empleados-window').find('select[name=municipio]');
		var $campo_comp_calle = $('#forma-empleados-window').find('input[name=calle]');
		var $campo_comp_numcalle = $('#forma-empleados-window').find('input[name=numero]');
		var $campo_comp_colonia = $('#forma-empleados-window').find('input[name=colonia]');
		var $campo_comp_cp = $('#forma-empleados-window').find('input[name=cp]');
		
		//tab4 Salud y Enfermedades
		var $campo_contacto = $('#forma-empleados-window').find('input[name=contacto]');
		var $campo_tel_contacto = $('#forma-empleados-window').find('input[name=telcontacto]');
		var $select_tipo_sangre = $('#forma-empleados-window').find('select[name=tipo_sangre]');
		var $txtarea_enfermedades = $('#forma-empleados-window').find('textarea[name=enfermedades]');
		var $txtarea_alergias= $('#forma-empleados-window').find('textarea[name=alergias]');
		
        //tab4 organizacion
		var $select_sucursal = $('#forma-empleados-window').find('select[name=sucursal]');
		var $select_puesto = $('#forma-empleados-window').find('select[name=puesto]');
		var $select_categoria_puesto = $('#forma-empleados-window').find('select[name=categoria]');
		var $correo_institucional = $('#forma-empleados-window').find('input[name=correo_institucional]');
		var $campo_comentarios =$('#forma-empleados-window').find('textarea[name=cometarios]');
		
		//tab5 Roles
		var $campo_nom_usuario = $('#forma-empleados-window').find('input[name=email_usr]');
		var $campo_password = $('#forma-empleados-window').find('input[name=password]');
		var $campo_verifica_password = $('#forma-empleados-window').find('input[name=verifica_pass]');
		var $div_roles=$('#forma-empleados-window').find('#rol_empleado tr td').find('#roles');
		var $select_rols_acceso=$('#forma-empleados-window').find('select[name=permite]');

		//tab 6 Agentes
		var $campo_comision=$('#forma-empleados-window').find('input[name=comision]');
		var $monto_comision=$('#forma-empleados-window').find('#montocomision').hide();
		var $dias_comision=$('#forma-empleados-window').find('#diascomision');
		
		var $select_region=$('#forma-empleados-window').find('select[name=region]');
		var $select_tipo_comision=$('#forma-empleados-window').find('select[name=tipo_comision]');
		var $campo_comision2=$('#forma-empleados-window').find('input[name=comision2]');
		var $campo_comision3=$('#forma-empleados-window').find('input[name=comision3]');
		var $campo_comision4=$('#forma-empleados-window').find('input[name=comision4]');
		var $campo_diascomision=$('#forma-empleados-window').find('input[name=dias_comision]');
		var $campo_diascomision2=$('#forma-empleados-window').find('input[name=dias_comision2]');
		var $campo_diascomision3=$('#forma-empleados-window').find('input[name=dias_comision3]');
		
		var $campo_montocomision=$('#forma-empleados-window').find('input[name=monto_comision]');
		var $campo_montocomision2=$('#forma-empleados-window').find('input[name=monto_comision2]');
		var $campo_montocomision3=$('#forma-empleados-window').find('input[name=monto_comision3]');
		
		var $tabla_roles = $('#forma-empleados-window').find('#rol_empleado');
		var $cerrar_plugin = $('#forma-empleados-window').find('#close');
		var $cancelar_plugin = $('#forma-empleados-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-empleados-window').find('#submit');
		var $txt_roles =$('#forma-empleados-windows').find('textarea[name=roles');
		var $limpia_campos="";
		
		$campo_empleado_id.attr({ 'value' : 0 });
		$campo_num_empleado.css({'background' : '#DDDDDD'});
		$campo_num_empleado.attr('disabled','-1');
		
		var tipo_comision_hmtl="";
		tipo_comision_hmtl += '<option value="1"  selected="yes">Comision por Dias</option>';
		tipo_comision_hmtl += '<option value="2"  >Comision por Montos</option>';
		$select_tipo_comision.append(tipo_comision_hmtl);
		
		$select_tipo_comision.change(function () {
			if(parseInt($select_tipo_comision.val()) == 1){
				$dias_comision.show();
				$monto_comision.hide();
			}
			
			if(parseInt($select_tipo_comision.val()) == 2){
				$dias_comision.hide();
				$monto_comision.show();
			}
		});
		
		$permitir_solo_numeros($campo_comision);
		$permitir_solo_numeros($campo_comision2);
		$permitir_solo_numeros($campo_comision3);
		$permitir_solo_numeros($campo_comision4);
		$permitir_solo_numeros($campo_diascomision);
		$permitir_solo_numeros($campo_diascomision2);
		$permitir_solo_numeros($campo_diascomision3);
		$permitir_solo_numeros($campo_montocomision);
		$permitir_solo_numeros($campo_montocomision2);
		$permitir_solo_numeros($campo_montocomision3);
		
		$accio_blur($campo_comision);
		$accio_blur($campo_comision2);
		$accio_blur($campo_comision3);
		$accio_blur($campo_comision4);
		$accio_blur($campo_diascomision);
		$accio_blur($campo_diascomision2);
		$accio_blur($campo_diascomision3);
		$accio_blur($campo_montocomision);
		$accio_blur($campo_montocomision2);
		$accio_blur($campo_montocomision3);
		
		$accion_focus($campo_comision);
		$accion_focus($campo_comision2);
		$accion_focus($campo_comision3);
		$accion_focus($campo_comision4);
		$accion_focus($campo_diascomision);
		$accion_focus($campo_diascomision2);
		$accion_focus($campo_diascomision3);
		$accion_focus($campo_montocomision);
		$accion_focus($campo_montocomision2);
		$accion_focus($campo_montocomision3);
		
		//pone 0 en los campos
		$add_ceros($campo_comision);
		$add_ceros($campo_comision2);
		$add_ceros($campo_comision3);
		$add_ceros($campo_comision4);
		$add_ceros($campo_diascomision);
		$add_ceros($campo_diascomision2);
		$add_ceros($campo_diascomision3);
		$add_ceros($campo_montocomision);
		$add_ceros($campo_montocomision2);
		$add_ceros($campo_montocomision3);

		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Empleado dado de alta", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-empleados-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}
			else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-empleados-window').find('div.interrogacion').css({'display':'none'});

				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
						tmp = data['success'].split('___')[element];
						longitud = tmp.split(':');
						if( longitud.length > 1 ){
								$('#forma-empleados-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
						}
				}
			}
		}
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
		
		$.post(input_json,$arreglo,function(entry){
			//Alimentando los campos select de las pais
			$select_pais.children().remove();
			var pais_hmtl = '<option value="0" selected="yes">[-Seleccionar pais-]</option>';
			$.each(entry['Paises'],function(entryIndex,pais){
				pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
			});
			$select_pais.append(pais_hmtl);


			var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar entidad--]</option>';
			$select_entidad.children().remove();
			$select_entidad.append(entidad_hmtl);

			var localidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar municipio-]</option>';
			$select_localidad.children().remove();
			$select_localidad.append(localidad_hmtl);

			//carga select estados al cambiar el pais
			$select_pais.change(function(){
				var valor_pais = $(this).val();
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
				$arreglo = {'id_pais':valor_pais};
				$.post(input_json,$arreglo,function(entry){
					$select_entidad.children().remove();
					var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
					$.each(entry['Entidades'],function(entryIndex,entidad){
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
					});
					$select_entidad.append(entidad_hmtl);
					var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Localidad alternativa' + '</option>';
					$select_localidad.children().remove();
					$select_localidad.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});

			//carga select municipios al cambiar el estado
			$select_entidad.change(function(){
				var valor_entidad = $(this).val();
				var valor_pais = $select_pais.val();
				//alert("Pais: "+valor_pais+"    Entidad:"+valor_entidad);
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
				$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
				$.post(input_json,$arreglo,function(entry){
					$select_localidad.children().remove();
					var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
					$.each(entry['Localidades'],function(entryIndex,mun){
						trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
					});
					$select_localidad.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});

			//alimenta el select de escolaridad
			$select_escolaridad.children().remove();

			var escolaridad_hmtl = '<option value="0"  selected="yes">[-Seleccionar Escolaridad-]</option>'
			$.each(entry['Escolaridad'],function(entryIndex,escolaridad){
				escolaridad_hmtl += '<option value="' + escolaridad['id'] + '"  >' + escolaridad['titulo'] + '</option>';
			});
			$select_escolaridad.append(escolaridad_hmtl);


		   //alimenta el select de genero sexual
		   $select_genero_sexo.children().remove();

			var genero_hmtl = '<option value="0"  selected="yes">[-Seleccionar Genero-]</option>'
			$.each(entry['Genero'],function(entryIndex,genero){
				genero_hmtl += '<option value="' + genero['id'] + '"  >' + genero['titulo'] + '</option>';
			});
			$select_genero_sexo.append(genero_hmtl);

		   //alimenta el select de edocivil

			$select_edo_civil.children().remove();
			var civils_hmtl = '<option value="0"  selected="yes">[-Seleccionar Estado Civil-]</option>'
			$.each(entry['EdoCivil'],function(entryIndex,civil){
				civils_hmtl += '<option value="' + civil['id'] + '"  >' + civil['titulo'] + '</option>';
			});
			$select_edo_civil.append(civils_hmtl);


		  //alimenta select de religion

			$select_religion.children().remove();
			var religion_hmtl = '<option value="0"  selected="yes">[-Seleccionar Religion-]</option>'
			$.each(entry['Religion'],function(entryIndex,religion){
				religion_hmtl += '<option value="' + religion['id'] + '"  >' + religion['titulo'] + '</option>';
			});
			$select_religion.append(religion_hmtl);

			//alimenta select de tipo sangre

			$select_tipo_sangre.children().remove();
			var tipo_sangre_hmtl = '<option value="0"  selected="yes">[-Seleccionar Tipo Sangre-]</option>'
			$.each(entry['Sangre'],function(entryIndex,sangre){
				tipo_sangre_hmtl += '<option value="' + sangre['id'] + '"  >' + sangre['titulo'] + '</option>';
			});
			$select_tipo_sangre.append(tipo_sangre_hmtl);

			//alimentando el select de sucursal
			$select_sucursal.children().remove();
			var sucursal_hmtl='<option value="0" selected="yes">[-Seleccione Sucursal-]</option>'
			$.each(entry['Sucursal'],function(entryIndex,sucursales){
			   sucursal_hmtl +='<option value="'+sucursales['id']+'">'+sucursales['titulo']+'</option>';
			});
			$select_sucursal.append(sucursal_hmtl);


			///carga select pruestos
			$select_puesto.children().remove();
			var puesto_hmtl = '<option value="0"  selected="yes">[-Seleccionar Puesto-]</option>'
			$.each(entry['Puesto'],function(entryIndex,puestos){
				puesto_hmtl += '<option value="' + puestos['id'] + '"  >' + puestos['titulo'] + '</option>';
			});
			$select_puesto.append(puesto_hmtl);


			var categoria_hmtl = '<option value="00" selected="yes" >[-Seleccionar Categoria--]</option>';
			$select_categoria_puesto.children().remove();
			$select_categoria_puesto.append(categoria_hmtl);



			//carga select categorias al cambiar el puesto
			$select_puesto.change(function(){
				var valor_puesto = $(this).val();
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCategorias.json';
				$arreglo = {'id_puesto':valor_puesto};
				$.post(input_json,$arreglo,function(entry){
					$select_categoria_puesto.children().remove();
					var categoria_hmtl = '<option value="0"  selected="yes">[-Seleccionar Categoria-]</option>'
					$.each(entry['Categoria'],function(entryIndex,categoria){
						categoria_hmtl += '<option value="' + categoria['id'] + '"  >' + categoria['titulo'] + '</option>';
					});
					$select_categoria_puesto.append(categoria_hmtl);


				},"json");//termina llamada json
			});

                        //inhabilita y deshabilita los checks del div de roles
                        /*$rols_acceso.change(function(){

                            if($rols_acceso.val()==0){
                                $div_roles.find('input[name=micheck]').removeAttr('disabled');
                            }else{

                               $div_roles.find('input[name=micheck]').attr('disabled','-1');
                            }
                        });

                        */
                       //carga select de permiso de sistema
                        var html = '';
                        $select_rols_acceso.children().remove();
                            html='<option value="true">SI</option>';
                            html+='<option value="false">NO</option>';
                        $select_rols_acceso.append(html);

                        //carga los checks de roles
                        var arreglo_parametros = {
                            id:$identificador.val()
                        }

                        var input_json = document.location.protocol+'//' +document.location.host+'/'+controller+'/getRoles.json';
                        $.post(input_json,arreglo_parametros,function(entry){
                            var encuentra_chks="";
                            var $div_roles=$('#forma-empleados-window').find('#rol_empleado tr td').find('div#roles');//.find('table #rols');
                            var html="";
                            $total_tr=0;
                            html+='<table border="0" whidth="100%" id="rols">';
                            $.each(entry['Roles'],function(entryIndex,rol){
                                html+='<tr>';
                                    html+='<td class="grid" style=font-size: 11px;  width="40">';
                                        html+='<input type="checkbox" name="micheck">';
                                        html+='<input type="hidden" name="seleccionado" value="0">';
                                    html+='</td>';
                                    html+='<td><input type="hidden" name="id_rol" value="'+rol['id']+'">&nbsp;&nbsp;</td>';
                                    html+='<td class="grid" style="font-size: 11px; width="350px">'+rol['titulo']+'</td>';
                                html+='</tr>';

                             $total_tr=$total_tr+1;
                            });
                            html+='</table>';
                            $div_roles.append(html);
                            seleccionar_roles_check($div_roles.find('#rols'));

                        });

                        //---------------------------------------------------------------

                        //valida la fecha de nacimiento seleccionada
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


                        $campo_fecha_nacimiento.click(function (s){
							var a=$('div.datepicker');
							a.css({'z-index':100});
                        });

                        $campo_fecha_nacimiento.DatePicker({
							format:'Y-m-d',
							date: $campo_fecha_nacimiento.val(),
							current: $campo_fecha_nacimiento.val(),
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
								$campo_fecha_nacimiento.val(formated);
								if (formated.match(patron) ){
									var valida_fecha=mayor($campo_fecha_nacimiento.val(),mostrarFecha());

									if (valida_fecha==true){
											jAlert("Fecha no valida",'! Atencion');
											$campo_fecha_nacimiento.val(mostrarFecha());
									}else{
											$campo_fecha_nacimiento.DatePickerHide();
									}
								}
							}
                        });

                        //valida la fecha de ingreso seleccionada
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


                        $campo_fecha_ingreso.click(function (s){
							var a=$('div.datepicker');
							a.css({'z-index':100});
                        });

                        $campo_fecha_ingreso.DatePicker({
                                format:'Y-m-d',
                                date: $campo_fecha_ingreso.val(),
                                current: $campo_fecha_ingreso.val(),
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
									$campo_fecha_ingreso.val(formated);
									if (formated.match(patron) ){
										var valida_fecha=mayor($campo_fecha_ingreso.val(),mostrarFecha());
										if (valida_fecha==true){
											jAlert("Fecha no valida",'! Atencion');
											$campo_fecha_ingreso.val(mostrarFecha());
										}else{
											$campo_fecha_ingreso.DatePickerHide();
										}
									}
                                }
                        });

                        $select_region.children().remove();
                        var region_hmtl = '<option value="0"  selected="yes">[-Seleccionar Region-]</option>'
                        $.each(entry['Region'],function(entryIndex,region){
                                region_hmtl += '<option value="' + region['id'] + '"  >' + region['titulo'] + '</option>';
                        });
                        $select_region.append(region_hmtl);

                    });


                $submit_actualizar.bind('click',function(){
                    var $total_tr = $('#forma-empleados-window').find('input[name=total_tr]');
                    var selec=0;
                    //checa facturas a revision seleccionadas
                    selec = contar_seleccionados($tabla_roles);

                    $total_tr.val(selec);

                    if(parseInt($total_tr.val()) > 0){
                        return true;
                    }else{
                        jAlert("No hay roles seleccionadas para actualizar", 'Atencion!');
                        return false;
                    }
                });

		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-empleados-overlay').fadeOut(remove);
		});

		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-empleados-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});

	});

        var seleccionar_roles_check = function($tabla){
            $tabla.find('input[name=micheck]').each(function(){

                $(this).click(function(event){

                        if(this.checked){
                                $(this).parent().find('input[name=seleccionado]').val("1");
                                //alert("seleccionado");
                        }else{
                                //$(this).parent().find('input[name=micheck]').removeAttr('checked');
                                $(this).parent().find('input[name=seleccionado]').val("0");
                                //alert("no seleccionado");
                        }
                });
            });
        }

	var contar_seleccionados= function($tabla_roles){
            var seleccionados=0;

            $tabla_roles.find('input[name=micheck]').each(function(){
            if(this.checked){
                seleccionados = parseInt(seleccionados) + 1;
            }
            });

            return seleccionados;
        }



	var carga_formaEmpleados00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el Empleado seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Empleado fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El Empleado no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaEmpleados00';

			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			//var accion = "get_cliente";

			$(this).modalPanel();
			$('#forma-empleados-window').css({ "margin-left": -400, 	"margin-top": -290 });

			$forma_selected.prependTo('#forma-empleados-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});

			$tabs_li_funxionalidad();


			//alert(id_to_show);

			if(accion_mode == 'edit'){

				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_empleado.json';
				$arreglo = {
							'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
				
				//tab 1 Datos personales
				var $total_tr = $('#forma-empleados-window').find('input[name=total_tr]');
				var $campo_empleado_id = $('#forma-empleados-window').find('input[name=identificador_empleado]');
				var $identificador=$('#forma-empleados-window').find('input[name=identificador]');
				var $campo_num_empleado=$('#forma-empleados-window').find('input=[name=empleado_id]');
				var $campo_nombre = $('#forma-empleados-window').find('input[name=nombre]');
				var $campo_appaterno= $('#forma-empleados-window').find('input[name=appaterno]');
				var $campo_apmaterno = $('#forma-empleados-window').find('input[name=apmaterno]');
				var $campo_imss = $('#forma-empleados-window').find('input[name=imss]');
				var $clave_infonavit = $('#forma-empleados-window').find('input[name=infonavit]');
				var $campo_curp = $('#forma-empleados-window').find('input[name=curp]');
				var $campo_rfc= $('#forma-empleados-window').find('input[name=rfc]');
				var $campo_fecha_nacimiento = $('#forma-empleados-window').find('input[name=f_nacimiento]');
				var $campo_fecha_ingreso = $('#forma-empleados-window').find('input[name=f_ingreso]');
				var $select_escolaridad= $('#forma-empleados-window').find('select[name=escolaridad]');
				var $select_genero_sexo= $('#forma-empleados-window').find('select[name=sexo]');
				var $select_edo_civil = $('#forma-empleados-window').find('select[name=edocivil]');
				var $select_religion = $('#forma-empleados-window').find('select[name=religion]');
				
				// tab 2 Direccion y contacto
				var $campo_telefono = $('#forma-empleados-window').find('input[name=telefono]');
				var $campo_movil = $('#forma-empleados-window').find('input[name=movil]');
				var $campo_correo_personal = $('#forma-empleados-window').find('input[name=correo_personal]');
				var $select_pais = $('#forma-empleados-window').find('select[name=pais]');
				var $select_entidad = $('#forma-empleados-window').find('select[name=estado]');
				var $select_localidad = $('#forma-empleados-window').find('select[name=municipio]');
				var $campo_comp_calle = $('#forma-empleados-window').find('input[name=calle]');
				var $campo_comp_numcalle = $('#forma-empleados-window').find('input[name=numero]');
				var $campo_comp_colonia = $('#forma-empleados-window').find('input[name=colonia]');
				var $campo_comp_cp = $('#forma-empleados-window').find('input[name=cp]');
				
				//tab4 Salud y Enfermedades
				var $campo_contacto = $('#forma-empleados-window').find('input[name=contacto]');
				var $campo_tel_contacto = $('#forma-empleados-window').find('input[name=telcontacto]');
				var $select_tipo_sangre = $('#forma-empleados-window').find('select[name=tipo_sangre]');
				var $txtarea_enfermedades = $('#forma-empleados-window').find('textarea[name=enfermedades]');
				var $txtarea_alergias= $('#forma-empleados-window').find('textarea[name=alergias]');
				
				//tab4 organizacion
				var $select_sucursal = $('#forma-empleados-window').find('select[name=sucursal]');
				var $select_puesto = $('#forma-empleados-window').find('select[name=puesto]');
				var $select_categoria_puesto = $('#forma-empleados-window').find('select[name=categoria]');
				var $correo_institucional = $('#forma-empleados-window').find('input[name=correo_institucional]');
				var $campo_comentarios =$('#forma-empleados-window').find('textarea[name=cometarios]');
				//tab5 Roles
				
				var $campo_nom_usuario = $('#forma-empleados-window').find('input[name=email_usr]');
				var $campo_password = $('#forma-empleados-window').find('input[name=password]');
				var $campo_verifica_password = $('#forma-empleados-window').find('input[name=verifica_pass]');
				var $div_roles=$('#forma-empleados-window').find('#rol_empleado tr td').find('#roles');
				var $select_rols_acceso=$('#forma-empleados-window').find('select[name=permite]');
				
				//tab 6 Agentes
				var $campo_comision=$('#forma-empleados-window').find('input[name=comision]');
				var $select_region=$('#forma-empleados-window').find('select[name=region]');
				var $select_tipo_comision=$('#forma-empleados-window').find('select[name=tipo_comision]');
				var $monto_comision=$('#forma-empleados-window').find('#montocomision').hide();
				var $dias_comision=$('#forma-empleados-window').find('#diascomision');
				var $campo_comision2=$('#forma-empleados-window').find('input[name=comision2]');
				var $campo_comision3=$('#forma-empleados-window').find('input[name=comision3]');
				var $campo_comision4=$('#forma-empleados-window').find('input[name=comision4]');
				var $campo_diascomision=$('#forma-empleados-window').find('input[name=dias_comision]');
				var $campo_diascomision2=$('#forma-empleados-window').find('input[name=dias_comision2]');
				var $campo_diascomision3=$('#forma-empleados-window').find('input[name=dias_comision3]');
				var $campo_montocomision=$('#forma-empleados-window').find('input[name=monto_comision]');
				var $campo_montocomision2=$('#forma-empleados-window').find('input[name=monto_comision2]');
				var $campo_montocomision3=$('#forma-empleados-window').find('input[name=monto_comision3]');
				var $tabla_roles = $('#forma-empleados-window').find('#rol_empleado');
				var $cerrar_plugin = $('#forma-empleados-window').find('#close');
				var $cancelar_plugin = $('#forma-empleados-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-empleados-window').find('#submit');
				var $txt_roles =$('#forma-empleados-windows').find('textarea[name=roles');
				
				$permitir_solo_numeros($campo_comision);
				$permitir_solo_numeros($campo_comision2);
				$permitir_solo_numeros($campo_comision3);
				$permitir_solo_numeros($campo_comision4);
				$permitir_solo_numeros($campo_diascomision);
				$permitir_solo_numeros($campo_diascomision2);
				$permitir_solo_numeros($campo_diascomision3);
				$permitir_solo_numeros($campo_montocomision);
				$permitir_solo_numeros($campo_montocomision2);
				$permitir_solo_numeros($campo_montocomision3);
				
				$accio_blur($campo_comision);
				$accio_blur($campo_comision2);
				$accio_blur($campo_comision3);
				$accio_blur($campo_comision4);
				$accio_blur($campo_diascomision);
				$accio_blur($campo_diascomision2);
				$accio_blur($campo_diascomision3);
				$accio_blur($campo_montocomision);
				$accio_blur($campo_montocomision2);
				$accio_blur($campo_montocomision3);
				
				$accion_focus($campo_comision);
				$accion_focus($campo_comision2);
				$accion_focus($campo_comision3);
				$accion_focus($campo_comision4);
				$accion_focus($campo_diascomision);
				$accion_focus($campo_diascomision2);
				$accion_focus($campo_diascomision3);
				$accion_focus($campo_montocomision);
				$accion_focus($campo_montocomision2);
				$accion_focus($campo_montocomision3);
				
				//$campo_titulo.attr({ 'readOnly':true });
				$campo_num_empleado.attr('disabled','-1'); //deshabilitar
				$campo_num_empleado.css({'background' : '#DDDDDD'});
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-empleados-overlay').fadeOut(remove);
						jAlert("Los datos del empleado se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-empleados-window').find('div.interrogacion').css({'display':'none'});

						//alert(data['success']);

						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-empleados-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					$campo_empleado_id.attr({ 'value' : entry['Empleados']['0']['empleado_id'] });
					$campo_num_empleado.attr({ 'value' : entry['Empleados']['0']['clave'] });
					$campo_nombre.attr({ 'value' : entry['Empleados']['0']['nombre_pila'] });
					$campo_appaterno.attr({ 'value' : entry['Empleados']['0']['apellido_paterno'] });
					$campo_apmaterno.attr({ 'value' : entry['Empleados']['0']['apellido_materno'] });
					$campo_imss.attr({ 'value' : entry['Empleados']['0']['imss'] });
					$clave_infonavit.attr({ 'value' : entry['Empleados']['0']['infonavit'] });
					$campo_curp.attr({ 'value' : entry['Empleados']['0']['curp'] });
					$campo_rfc.attr({ 'value' : entry['Empleados']['0']['rfc'] });
					$campo_fecha_nacimiento.attr({ 'value' : entry['Empleados']['0']['fecha_nacimiento'] });
					$campo_fecha_ingreso.attr({ 'value' : entry['Empleados']['0']['fecha_ingreso'] });
					$campo_telefono.attr({ 'value' : entry['Empleados']['0']['telefono'] });
					$campo_movil.attr({ 'value' : entry['Empleados']['0']['telefono_movil'] });
					$campo_correo_personal.attr({ 'value' : entry['Empleados']['0']['correo_personal'] });
					$campo_comp_calle.attr({ 'value' : entry['Empleados']['0']['calle'] });
					$campo_comp_numcalle.attr({ 'value' : entry['Empleados']['0']['numero'] });
					$campo_comp_colonia.attr({ 'value' : entry['Empleados']['0']['colonia'] });
					$campo_comp_cp.attr({ 'value' : entry['Empleados']['0']['cp'] });
					$campo_contacto.attr({ 'value' : entry['Empleados']['0']['contacto_emergencia'] });
					$campo_tel_contacto.attr({'value': entry['Empleados']['0']['telefono_emergencia']});
					$campo_nom_usuario.attr({'value': entry['Empleados']['0']['username']});
					$campo_password.attr({'value':entry['Empleados']['0']['password']});
					$campo_verifica_password.attr({'value':entry['Empleados']['0']['password']});
					$campo_comentarios.text(entry['Empleados']['0']['comentarios']);
					$txtarea_enfermedades.text(entry['Empleados']['0']['enfermedades']);
					$txtarea_alergias.text(entry['Empleados']['0']['alergias']);
					$campo_comision.attr({'value': $(this).agregar_comas(entry['Empleados']['0']['comision_agen'])});
					$campo_comision2.attr({'value': $(this).agregar_comas(entry['Empleados']['0']['comision2_agen'])});
					$campo_comision3.attr({'value': $(this).agregar_comas(entry['Empleados']['0']['comision3_agen'])});
					$campo_comision4.attr({'value': $(this).agregar_comas(entry['Empleados']['0']['comision4_agen'])});
					$correo_institucional.attr({'value':entry['Empleados']['0']['correo_empresa']});
					
					//Alimentando $select_tipo_comision
					$select_tipo_comision.children().remove();
					var tipo_comision_hmtl = "";
					if (parseInt(entry['Empleados']['0']['tipo_comision'])==1){
						tipo_comision_hmtl += '<option value="1"  selected="yes">Comision por Dias</option>';
						tipo_comision_hmtl += '<option value="2"  >Comision por Montos</option>';
					}
					
					if (parseInt(entry['Empleados']['0']['tipo_comision'])==2){
						tipo_comision_hmtl += '<option value="1"  >Comision por Dias</option>';
						tipo_comision_hmtl += '<option value="2"  selected="yes">Comision por Montos</option>';
					}
					$select_tipo_comision.append(tipo_comision_hmtl);
/*
					$campo_diascomision.attr({'value':'0'});
					$campo_diascomision2.attr({'value':'0'});
					$campo_diascomision3.attr({'value':'0'});
					$campo_montocomision.attr({'value':'0'});
					$campo_montocomision2.attr({'value':'0'});
					$campo_montocomision3.attr({'value':'0'});
*/

					$campo_diascomision.attr({'value': $(this).agregar_comas(entry['Empleados']['0']['dias_tope_comision'])});
					$campo_diascomision2.attr({'value': $(this).agregar_comas(entry['Empleados']['0']['dias_tope_comision2'])});
					$campo_diascomision3.attr({'value': $(this).agregar_comas(entry['Empleados']['0']['dias_tope_comision3'])});
					$campo_montocomision.attr({'value': $(this).agregar_comas(entry['Empleados']['0']['monto_tope_comision'])});
					$campo_montocomision2.attr({'value': $(this).agregar_comas(entry['Empleados']['0']['monto_tope_comision2'])});
					$campo_montocomision3.attr({'value': $(this).agregar_comas(entry['Empleados']['0']['monto_tope_comision3'])});
				   if (parseInt(entry['Empleados']['0']['tipo_comision'])==1){
						$dias_comision.show();
						$monto_comision.hide();
						//$campo_montocomision.attr({'value':'0'});
					   // $campo_montocomision2.attr({'value':'0'});
						//$campo_montocomision3.attr({'value':'0'});
						//$campo_diascomision.attr({'value':entry['Empleados']['0']['dias_tope_comision']});
						//$campo_diascomision2.attr({'value':entry['Empleados']['0']['dias_tope_comision2']});
						//$campo_diascomision3.attr({'value':entry['Empleados']['0']['dias_tope_comision3']});
					}
					
					if (parseInt(entry['Empleados']['0']['tipo_comision'])==2){
						$dias_comision.hide();
						$monto_comision.show();
						//$campo_diascomision.attr({'value':'0'});
						//$campo_diascomision2.attr({'value':'0'});
						//$campo_diascomision3.attr({'value':'0'});
						//$campo_montocomision.attr({'value':entry['Empleados']['0']['monto_tope_comision']});
						//$campo_montocomision2.attr({'value':entry['Empleados']['0']['monto_tope_comision2']});
						//$campo_montocomision3.attr({'value':entry['Empleados']['0']['monto_tope_comision3']});
					}
					
					$select_tipo_comision.change(function(){
					   if (parseInt($select_tipo_comision.val())==1){
							$dias_comision.show();
							$monto_comision.hide();
							$campo_diascomision.attr({'value': $(this).agregar_comas(entry['Empleados']['0']['dias_tope_comision'])});
							$campo_diascomision2.attr({'value':$(this).agregar_comas(entry['Empleados']['0']['dias_tope_comision2'])});
							$campo_diascomision3.attr({'value':$(this).agregar_comas(entry['Empleados']['0']['dias_tope_comision3'])});
						}
						
						if (parseInt($select_tipo_comision.val())==2){
							$dias_comision.hide();
							$monto_comision.show();
							$campo_montocomision.attr({'value': $(this).agregar_comas(entry['Empleados']['0']['monto_tope_comision']) });
							$campo_montocomision2.attr({'value': $(this).agregar_comas(entry['Empleados']['0']['monto_tope_comision2']) });
							$campo_montocomision3.attr({'value': $(this).agregar_comas(entry['Empleados']['0']['monto_tope_comision3']) });
						}
					});
					
					//Alimentando los campos select de las pais
					$select_pais.children().remove();
					var pais_hmtl = "";
					$.each(entry['Paises'],function(entryIndex,pais){
						if(pais['cve_pais'] == entry['Empleados']['0']['gral_pais_id']){
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  selected="yes">' + pais['pais_ent'] + '</option>';
						}else{
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
						}
					});
					$select_pais.append(pais_hmtl);
					
					
					//Alimentando los campos select del estado
					$select_entidad.children().remove();
					var entidad_hmtl = "";
					$.each(entry['Entidades'],function(entryIndex,entidad){
						if(entidad['cve_ent'] == entry['Empleados']['0']['gral_edo_id']){
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  selected="yes">' + entidad['nom_ent'] + '</option>';
						}else{
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						}
					});
					$select_entidad.append(entidad_hmtl);


					//Alimentando los campos select de los municipios
					$select_localidad.children().remove();
					var localidad_hmtl = "";
					$.each(entry['Localidades'],function(entryIndex,mun){
						if(mun['cve_mun'] == entry['Empleados']['0']['gral_mun_id']){
							localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  selected="yes">' + mun['nom_mun'] + '</option>';
						}else{
							localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						}
					});
					$select_localidad.append(localidad_hmtl);


					//carga select estados al cambiar el pais
					$select_pais.change(function(){
						var valor_pais = $(this).val();
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
						$arreglo = {'id_pais':valor_pais};
						$.post(input_json,$arreglo,function(entry){
							$select_entidad.children().remove();
							var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
							$.each(entry['Entidades'],function(entryIndex,entidad){
								entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
							});
							$select_entidad.append(entidad_hmtl);
							var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Localidad alternativa' + '</option>';
							$select_localidad.children().remove();
							$select_localidad.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});
					
					
					//carga select municipios al cambiar el estado
					$select_entidad.change(function(){
						var valor_entidad = $(this).val();
						var valor_pais = $select_pais.val();
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
						$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
						$.post(input_json,$arreglo,function(entry){
							$select_localidad.children().remove();
							var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
							$.each(entry['Localidades'],function(entryIndex,mun){
								trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
							});
							$select_localidad.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});

						//alimenta el select de escolaridad
						$select_escolaridad.children().remove();

						var escolaridad_hmtl ="";
						// '<option value="0"  selected="yes">[-Seleccionar Escolaridad-]</option>'

						$.each(entry['Escolaridad'],function(entryIndex,escolaridad){
								if(escolaridad['id']== entry['Empleados']['0']['gral_escolaridad_id']){
									escolaridad_hmtl += '<option value="' + escolaridad['id'] + '"selected="yes" >' + escolaridad['titulo'] + '</option>';
								}else{
									escolaridad_hmtl += '<option value="' + escolaridad['id'] + '"  >' + escolaridad['titulo'] + '</option>';

								}
						});
						$select_escolaridad.append(escolaridad_hmtl);


						//alimenta el select de genero sexual
						$select_genero_sexo.children().remove();

						var genero_hmtl = "";//'<option value="0"  selected="yes">[-Seleccionar Genero-]</option>'
						$.each(entry['Genero'],function(entryIndex,genero){
							if(genero['id']==entry['Empleados']['0']['gral_sexo_id']){
								genero_hmtl += '<option value="' + genero['id'] + '"selected="yes">' + genero['titulo'] + '</option>';

							}else{
							   genero_hmtl += '<option value="' + genero['id'] + '"  >' + genero['titulo'] + '</option>';

							}
						});
						$select_genero_sexo.append(genero_hmtl);

						//alimenta el select de edocivil

						$select_edo_civil.children().remove();
						var civils_hmtl ="";
						$.each(entry['EdoCivil'],function(entryIndex,civil){
							if(civil['id']==entry['Empleados']['0']['gral_edo_id']){
								civils_hmtl += '<option value="' + civil['id'] + '"selected="yes">' + civil['titulo'] + '</option>';
							}else{
								 civils_hmtl += '<option value="' + civil['id'] + '"  >' + civil['titulo'] + '</option>';
							}

						});
						$select_edo_civil.append(civils_hmtl);


					//alimenta select de religion

						$select_religion.children().remove();
						var religion_hmtl ="";
						$.each(entry['Religion'],function(entryIndex,religion){
							if(religion['id']==entry['Empleados']['0']['gral_religion_id']){
								religion_hmtl += '<option value="' + religion['id'] + '"selected="yes">' + religion['titulo'] + '</option>';
							}else{
								 religion_hmtl += '<option value="' + religion['id'] + '"  >' + religion['titulo'] + '</option>';

							}
						});
						$select_religion.append(religion_hmtl);

						//alimenta select de tipo sangre

						$select_tipo_sangre.children().remove();
						var tipo_sangre_hmtl = "";
						$.each(entry['Sangre'],function(entryIndex,sangre){
							if(sangre['id']==entry['Empleados']['0']['gral_sangretipo_id']){
								tipo_sangre_hmtl += '<option value="' + sangre['id'] + '"selected="yes">' + sangre['titulo'] + '</option>';

							}else{
								tipo_sangre_hmtl += '<option value="' + sangre['id'] + '"  >' + sangre['titulo'] + '</option>';

							}
						});
						$select_tipo_sangre.append(tipo_sangre_hmtl);

						//alimenta select de puestos

						$select_puesto.children().remove();
						var puesto_hmtl ="";
						$.each(entry['Puesto'],function(entryIndex,puestos){
							if(puestos['id']==entry['Empleados']['0']['gral_puesto_id']){
								puesto_hmtl += '<option value="' + puestos['id'] + '"selected="yes">' + puestos['titulo'] + '</option>';

							}else{
								puesto_hmtl += '<option value="' + puestos['id'] + '"  >' + puestos['titulo'] + '</option>';

							}
						});
						$select_puesto.append(puesto_hmtl);

						$select_categoria_puesto.children().remove();
						var categoria_hmtl ="";
						$.each(entry['Categoria'],function(entryIndex,categoria){
							if(categoria['id']==entry['Empleados']['0']['gral_categ_id']){
								categoria_hmtl += '<option value="' + categoria['id'] + '"selected="yes">' + categoria['titulo'] + '</option>';

							}else{
								categoria_hmtl += '<option value="' + categoria['id'] + '"  >' + categoria['titulo'] + '</option>';

							}
						});

						$select_categoria_puesto.append(categoria_hmtl);

						//carga select categorias al cambiar el puesto
						$select_puesto.change(function(){
							var valor_puesto = $(this).val();
							var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCategorias.json';
							$arreglo = {'id_puesto':valor_puesto};
							$.post(input_json,$arreglo,function(entry){
									$select_categoria_puesto.children().remove();
									var categoria_hmtl = '<option value="0"  selected="yes">[-Seleccionar Categoria-]</option>'
									$.each(entry['Categoria'],function(entryIndex,categoria){
											categoria_hmtl += '<option value="' + categoria['id'] + '"  >' + categoria['titulo'] + '</option>';
									});
									$select_categoria_puesto.append(categoria_hmtl);


							},"json");//termina llamada json
						});

						//alimentando el select de sucursal
						$select_sucursal.children().remove();
						var sucursal_hmtl='<option value="0" selected="yes">[-Seleccione Sucursal-]</option>'
						$.each(entry['Sucursal'],function(entryIndex,sucursales){
							if(sucursales['id']==entry['Empleados']['0']['gral_suc_id_empleado']){
								sucursal_hmtl +='<option value="'+sucursales['id']+'"selected="yes">'+sucursales['titulo']+'</option>';
							}else{
								sucursal_hmtl +='<option value="'+sucursales['id']+'" >'+sucursales['titulo']+'</option>';
							}
						});
						$select_sucursal.append(sucursal_hmtl);

						//carga select de permiso de sistema

						var html = '';
						$select_rols_acceso.children().remove();
							if(entry['Empleados']['0']['enabled']=="true"){
								html+='<option value="true" selected="yes">SI</option>';
								html+='<option value="false">NO</option>';
							}else{
								html+='<option value="true" >SI</option>';
								html+='<option value="false" selected="yes">NO</option>';
							}
						$select_rols_acceso.append(html);

						//carga los checks de roles

						var $div_roles=$('#forma-empleados-window').find('#rol_empleado tr td').find('div#roles');//.find('table #rols');
						var html="";
						$total_tr=0;
						html+='<table border="0" whidth="100%" id="rols">';
						$.each(entry['RolsEdit'],function(entryIndex,rols){
							html+='<tr>';
								html+='<td class="grid" style=font-size: 11px;  width="40">';
									html+='<input type="checkbox" name="micheck" '+rols['checkeado']+'>';
									html+='<input type="hidden" name="seleccionado" value="'+rols['seleccionado']+'" >';
								html+='</td>';
								html+='<td class="grid" style="font-size: 11px; width="20"><input type="hidden" name="id_rol" value="'+rols['id']+'">&nbsp;&nbsp;</td>';
								html+='<td class="grid" style="font-size: 11px; width="350px">'+rols['titulo']+'</td>';

							html+='</tr>';

						$total_tr=$total_tr+1;
						});
						html+='</table>';
						$div_roles.append(html);
						seleccionar_roles_check($div_roles.find('#rols'));

						//carga las regiones de los agentes
						$select_region.children().remove();
						var region_hmtl = "";
						$.each(entry['Region'],function(entryIndex,region){
							if(region['id']==entry['Empleados']['0']['region_id_agen']){
								region_hmtl += '<option value="' + region['id'] + '"selected="yes"  >' + region['titulo'] + '</option>';
							}else{
								region_hmtl += '<option value="' + region['id'] + '"  >' + region['titulo'] + '</option>';
							}

						});
						$select_region.append(region_hmtl);

						$submit_actualizar.bind('click',function(){
							var $total_tr = $('#forma-empleados-window').find('input[name=total_tr]');
							var selec=0;
							//checa facturas a revision seleccionadas
							selec = contar_seleccionados($tabla_roles);

							$total_tr.val(selec);

							if(parseInt($total_tr.val()) > 0){
								return true;
							}else{
								jAlert("No hay roles seleccionadas para actualizar", 'Atencion!');
								return false;
							}
						});


						//Ligamos el boton cancelar al evento click para eliminar la forma
						$cancelar_plugin.bind('click',function(){
								var remove = function() { $(this).remove(); };
								$('#forma-empleados-overlay').fadeOut(remove);
						});

						$cerrar_plugin.bind('click',function(){
								var remove = function() { $(this).remove(); };
								$('#forma-empleados-overlay').fadeOut(remove);
								$buscar.trigger('click');
						});
				});

				var seleccionar_roles_check = function($tabla){
					$tabla.find('input[name=micheck]').each(function(){
						$(this).click(function(event){
							if(this.checked){
								$(this).parent().find('input[name=seleccionado]').val("1");
								//alert("seleccionado");
							}else{
								//$(this).parent().find('input[name=micheck]').removeAttr('checked');
								$(this).parent().find('input[name=seleccionado]').val("0");
								//alert("no seleccionado");
							}
						});
					});
				}

				var contar_seleccionados= function($tabla_roles){
					var seleccionados=0;
					$tabla_roles.find('input[name=micheck]').each(function(){
						if(this.checked){
							seleccionados = parseInt(seleccionados) + 1;
						}
					});

					return seleccionados;
				}

			}
		}
	}

    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEmpleados.json';

        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();

        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getEmpleados.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}

        $.post(input_json,$arreglo,function(data){

            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaEmpleados00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();


});
