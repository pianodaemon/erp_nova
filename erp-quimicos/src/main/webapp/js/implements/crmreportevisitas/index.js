$(function() {

var config =  {
                    tituloApp: 'Reporte visitas' ,
                    contextpath : $('#lienzo_recalculable').find('input[name=contextpath]').val(),

                    userName : $('#lienzo_recalculable').find('input[name=user]').val(),
                    ui : $('#lienzo_recalculable').find('input[name=iu]').val(),


                    empresa:$('#lienzo_recalculable').find('input[name=emp]').val(),
                    sucursal:$('#lienzo_recalculable').find('input[name=suc]').val(),


                    getUrlForGetAndPost : function(){
                        var url = document.location.protocol + '//' + document.location.host + this.getController();
                        return url;
                    },

                    getUserName: function(){
                        return this.userName;
                    },

                    getUi: function(){
                        return this.ui;
                    },

                    getEmpresa: function(){
                        return this.empresa;
                    },
                    getSucursal: function(){
                        return this.sucursal;
                    },

                    getTituloApp: function(){
                        return this.tituloApp;
                    },


                    getController: function(){
                        return this.contextpath + "/controllers/crmreportevisitas";
                        //  return this.controller;
                    }

          };


          $('#header').find('#header1').find('span.emp').text(config.getEmpresa());
          $('#header').find('#header1').find('span.suc').text(config.getSucursal());
          $('#header').find('#header1').find('span.username').text(config.getUserName());

          var $username = $('#header').find('#header1').find('span.username');
          $username.text($('#lienzo_recalculable').find('input[name=user]').val());

          //aqui va el titulo del catalogo
          $('#barra_titulo').find('#td_titulo').append(config.getTituloApp());



          var $fecha_inicial = $('#lienzo_recalculable').find('input[name=fecha_inicial]');
        var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');
        var $genera_PDF_reporte_visitas = $('#lienzo_recalculable').find('div.repvisitas').find('table#filtros tr td').find('input[value$=Generar_PDF]');
        var $Buscar_reporte_visitas= $('#lienzo_recalculable').find('div.repvisitas').find('table#filtros tr td').find('input[value$=Buscar]');

        var $div_tabla_resultados= $('#tablaresultadosvisitas');
        var html_trs="";

        $fecha_inicial.attr('readonly',true);
		$fecha_final.attr('readonly',true);

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



		$fecha_inicial.DatePicker({
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
				$fecha_inicial.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha_inicial.val(),mostrarFecha());

					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$fecha_inicial.val(mostrarFecha());
					}else{
						$fecha_inicial.DatePickerHide();
					}
				}
			}
		});

		$fecha_inicial.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
		});

		mostrarFecha($fecha_inicial.val());
		//fecha final


		$fecha_final.DatePicker({
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
				$fecha_inicial.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha_final.val(),mostrarFecha());

					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$fecha_final.val(mostrarFecha());
					}else{
						$fecha_final.DatePickerHide();
					}
				}
			}
		});



        $fecha_final.DatePicker({
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
			$fecha_final.val(formated);
			if (formated.match(patron) ){
				var valida_fecha=mayor($fecha_final.val(),mostrarFecha());

				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$fecha_final.val(mostrarFecha());



				}else{
					$fecha_final.DatePickerHide();
				}
			}
		}
	});

	$fecha_final.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});



        mostrarFecha($fecha_final.val());

        //click generar reporte de visitas


         //genera pdf del reporte
	$genera_PDF_reporte_visitas.click(function(event){
		event.preventDefault();
                if($fecha_inicial.val() != "" && $fecha_final.val() !=""){
                var cadena = $fecha_inicial.val()+"___"+$fecha_final.val();
		var input_json = config.getUrlForGetAndPost() + '/Crear_PDF_reg_Visitas/'+cadena+'/'+config.getUi()+'/out.json';
		window.location.href=input_json;
                }else{
                jAlert("Debe elegir el rango la fecha inicial y su fecha final ","Atencion!!!")
            }
	});//termina llamada json




$Buscar_reporte_visitas.click(function(event){
        $div_tabla_resultados.children().remove();
var html_trs="";
        if($fecha_inicial.val() != "" && $fecha_final.val() != ""){

                    var arreglo_parametros = {fecha_inicial : $fecha_inicial.val() , fecha_final : $fecha_final.val(), iu:config.getUi()};

                    var restful_json_service = config.getUrlForGetAndPost() + '/getVisitas.json'

                    $.post(restful_json_service,arreglo_parametros,function(entry){
                        //$div_tabla_resultados.children().remove();
                         if(entry['Datos'].length > 0){

                            html_trs+='<table id="resultados" >'
                            html_trs+='<thead><tr>'
                            html_trs+='<td >Empeado</td>'
                            html_trs+='<td >Fecha&nbsp;Visita</td>'
                            html_trs+='<td >Nombre&nbsp;del&nbsp;contacto</td>'
                            html_trs+='<td >Motivo&nbsp;Visita</td>'
                            html_trs+='<td width="10">Tipo&nbsp;Seguimiento</td>'
                            html_trs+='<td width="10">Calificacion&nbsp;visita</td>'
                            html_trs+='<td width="50">Existe&nbsp;Oportunidad</td>'
                            html_trs+='<td width="10">Resultado</td>'
                            html_trs+='</tr></thead>'


                                for(var i=0; i<entry['Datos'].length; i++){
                                                    html_trs+='<tr>'
                                                    html_trs+='<td >'+entry['Datos'][i]["nombre_empleado"]+'</td>'
                                                    html_trs+='<td >'+entry['Datos'][i]["fecha_visita"]+'</td>'
                                                    html_trs+='<td >'+entry['Datos'][i]["nombre_contacto"]+'</td>'
                                                    html_trs+='<td >'+entry['Datos'][i]["motivo_visita"]+'</td>'
                                                    html_trs+='<td >'+entry['Datos'][i]["tipo_seguimiento_visita"]+'</td>'
                                                    html_trs+='<td >'+entry['Datos'][i]["calificacion_visita"]+'</td>'
                                                    html_trs+='<td >'+entry['Datos'][i]["existe_oportunidad"]+'</td>'
                                                    html_trs+='<td >'+entry['Datos'][i]["resultado"]+'</td>'
                                                    html_trs+=' </tr>'
                                }

                                                    html_trs +='<tfoot>';
                                                        html_trs +='<tr>';
                                                        html_trs +='<td colspan="8" align="center">&nbsp;</td>';
                                                        html_trs +='</tr>';
                                                    html_trs +='</tfoot>';
                                                    html_trs +='</table>';
                                                    $div_tabla_resultados.append(html_trs);
                        }else{  jAlert("Esta consulta no genero ningun Resultado pruebe ingresando otros Parametros",'Atencion!!!!')}
                        var height2 = $('#cuerpo').css('height');
                        var alto = parseInt(height2)-275;
                        var pix_alto=alto+'px';
                        $('#resultados').tableScroll({height:parseInt(pix_alto)});
                        });

                    }else{
                       jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
                    }
});















});
