$(function() {
        var config =  {
                    tituloApp: 'Ventas Netas por Producto Desglosado por factura' ,                 
                    contextpath : $('#lienzo_recalculable').find('input[name=contextpath]').val(),
                    
                    userName : $('#lienzo_recalculable').find('input[name=user]').val(),
                    ui : $('#lienzo_recalculable').find('input[name=iu]').val(),
                    
                    
                    empresa:$('#lienzo_recalculable').find('input[name=emp]').val(),
                    sucursal:$('#lienzo_recalculable').find('input[name=suc]').val(),
                    
                   
                    getUrlForGetAndPost : function(){
                        var url = document.location.protocol + '//' + document.location.host + this.getController();
                        return url;
                    },
                    getController: function(){
                        return this.contextpath + "/controllers/repventasnetasproductofactura";
                        //  return this.controller;
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
                    }


                    
                    
                    
                
        };
  

                    $('#header').find('#header1').find('span.emp').text(config.getEmpresa());
                    $('#header').find('#header1').find('span.suc').text(config.getSucursal());
                    $('#header').find('#header1').find('span.username').text(config.getUserName());

                    var $username = $('#header').find('#header1').find('span.username');
                    $username.text($('#lienzo_recalculable').find('input[name=user]').val());

                    //aqui va el titulo del catalogo
                    $('#barra_titulo').find('#td_titulo').append(config.getTituloApp());
                    $('#barra_acciones').hide();

                    //barra para el buscador 
                    $('#barra_buscador').hide();

                    var $select_tipo_reporte = $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('select[name=ventas]');
                    var $fecha_inicial = $('#lienzo_recalculable').find('input[name=fecha_inicial]');
                    var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');

                    var $genera_reporte_ventas_netasproductofactura= $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('input[value$=Generar_PDF]');
                    var $Buscar_ventas_netasproductofactura= $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('input[value$=Buscar]');

                    var $Nombre_Cliente= $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('input[name=nombrecliente]');
                    var $Nombre_Producto= $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('input[name=nombreproducto]');
                    
                    var $select_linea = $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('select[name=linea]');
                    var $select_marca = $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('select[name=marca]');
                    var $select_familia = $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('select[name=familia]');
                    var $select_subfamilia = $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('select[name=subfamilia]');
                    
                    
                    
                   
                    var restful_json_service = config.getUrlForGetAndPost() +'/get_cargando_filtros.json';
                     arreglo_parametros ={ linea:$select_linea.val(),
                                            marca:$select_marca.val(),
                                            familia:$select_familia.val(),
                                            subfamilia:$select_subfamilia.val(),
                                            iu:config.getUi()
                                         };
                    //alert("Linea::"+$select_linea.val()+"Marca::"+$select_marca.val()+"Familia::"+$select_familia.val()+"Subfamilia::"+$select_subfamilia.val())
                    $.post(restful_json_service,arreglo_parametros,function(data){
                            //Llena el select lineas
                            $select_linea.children().remove();
                            
                            var lineas_html = '<option value="0" selected="yes">[--Seleccionar Linea--]</option>';
                            $.each(data['lineas'],function(entryIndex,pt){
                                   lineas_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
                            });
                            $select_linea.append(lineas_html);
                            
                           
                            $select_marca.children().remove();
                            var marcas_html = '<option value="0" selected="yes">[--Seleccionar Marca--]</option>';
                            $.each(data['marcas'],function(entryIndex,pt){
                                   marcas_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
                            });
                            $select_marca.append(marcas_html);
                            
                            $select_familia.children().remove();
                            var familias_html = '<option value="0" selected="yes">[--Seleccionar Familia--]</option>';
                            $.each(data['familias'],function(entryIndex,pt){
                                   familias_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
                            });
                            $select_familia.append(familias_html);
                            
                            
                            

                    });
                    
                    $select_familia.change(function(){
                        var id_familia = $(this).val();
                        //alert("id_Familia::  "+id_familia);
                        var restful_json_service = config.getUrlForGetAndPost() +'/get_cargando_filtros.json';
                                  arreglo_parametros ={ linea:$select_linea.val(),
                                            marca:$select_marca.val(),
                                            familia:$select_familia.val(),
                                            subfamilia:$select_subfamilia.val(),
                                            iu:config.getUi()
                                         };    
                            
                        $.post(restful_json_service,arreglo_parametros,function(data){
                          $select_subfamilia.children().remove();
                            var subfamilias_html = '<option value="0" selected="yes">[--Seleccionar Familia--]</option>';
                            $.each(data['subfamilias'],function(entryIndex,pt){
                                   subfamilias_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
                            });
                            $select_subfamilia.append(subfamilias_html);
                          
                        }); 
                    });
                    
                    
                    $fecha_inicial.attr({'readOnly':true});
                    $fecha_final.attr({'readOnly':true});
                    $Nombre_Cliente.attr({'readOnly':true});
                    $Nombre_Producto.attr({'readOnly':true});
                    var $Buscar_clientes= $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('a[href*=busca_cliente]');
                    var $Buscar_productos= $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('a[href*=busca_producto]');
                    var $div_ventas_netas_productofactura= $('#ventasnetasproductofactura');
	
	
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
                            //mostrarFecha.attr({ 'readOnly':true });
	
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
	
	
	
                            //buscador de productos
                            busca_productos = function(sku_buscar){
                                    $(this).modalPanel_Buscaproducto();
                                    var $dialogoc =  $('#forma-buscaproducto-window');
                                    $dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());

                                    $('#forma-buscaproducto-window').css({"margin-left": -200, 	"margin-top": -200});

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

                                    //buscar todos los tipos de productos
                                    var input_json_tipos = config.getUrlForGetAndPost() + '/getProductoTipos.json';
                                    $arreglo = { iu:config.getUi() 		};
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
                                            $tabla_resultados.children().remove();
                                            var restful_json_service = config.getUrlForGetAndPost()+'/get_buscador_productos.json';
                                            arreglo_parametros = {    	sku:$campo_sku.val(),
                                                                                                    tipo:$select_tipo_producto.val(),
                                                                                                    descripcion:$campo_descripcion.val(),
                                                                                                    iu:config.getUi()
                                                                                            };
                                            var trr = '';
                                            $.post(restful_json_service,arreglo_parametros,function(entry){

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

                                                            $Nombre_Producto.val($(this).find('span.titulo_prod_buscador').html());
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
    
                            //click generar reporte de pronostico de Cobranza
                            $genera_reporte_ventas_netasproductofactura.click(function(event){
                                    event.preventDefault();

                                    var tipo_reporte=$select_tipo_reporte.val();
                                    var cliente=$Nombre_Cliente.val();
                                    var producto=$Nombre_Producto.val();
                                    var fecha_inicial = $fecha_inicial.val();
                                    var fecha_final = $fecha_final.val();
                                    var usuario=config.getUi();
                                    var cadena = tipo_reporte+"___"+cliente+"___"+producto+"___"+fecha_inicial+"___"+fecha_final+"___"+usuario

                                    if(fecha_inicial != 0 && fecha_final !=0){
                                        var input_json = config.getUrlForGetAndPost() + '/getrepventasnetasproductofactura/'+cadena+'/out.json';
                                        window.location.href=input_json;
                                    }else{
                                        jAlert("Debe elegir el rango la fecha inicial y su fecha final par la busqueda","Atencion!!!")
                                    }
                            });
		
       
                            $Buscar_clientes.click(function(event){
                                    event.preventDefault();
                                    busca_clientes();

                            });


                            $Buscar_productos.click(function(event){
                                event.preventDefault();
                                busca_productos();
                            });
    
    
                            busca_clientes=function(){
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
                                                //var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_clientes.json';
                                                var restful_json_service = config.getUrlForGetAndPost()+'/get_buscador_clientes.json';
                                        var  arreglo_parametros = {'cadena':$cadena_buscar.val(),'filtro':$select_filtro_por.val(),  'iu': $('#lienzo_recalculable').find('input[name=iu]').val()}

                                                var trr = '';
                                                $tabla_resultados.children().remove();
                                                //$.post(input_json,$arreglo,function(entry){
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

                                                        //seleccionar un producto del grid de resultados
                                                        $tabla_resultados.find('tr').click(function(){
                                                                //$('#forma-carteras-window').find('input[name=identificador_cliente]').val($(this).find('#idclient').val());
                                                                //$('#forma-carteras-window').find('input[name=rfccliente]').val($(this).find('span.rfc').html());
                                                                $('#forma-carteras-window').find('input[name=cliente]').val($(this).find('span.razon').html());

                                                            $('#forma-carteras-window').find('select[name=tipo_mov]').removeAttr('disabled');//habilitar select

                                                                $Nombre_Cliente.val($(this).find('span.razon').html());

                                                                //elimina la ventana de busqueda
                                                                var remove = function() {$(this).remove();};
                                                                $('#forma-buscacliente-overlay').fadeOut(remove);
                                                                //asignar el enfoque al campo sku del producto
                                                        });

                                                });
                                        });//termina llamada json

                                        $cancelar_plugin_busca_cliente.click(function(event){
                                                var remove = function() {$(this).remove();};
                                                $('#forma-buscacliente-overlay').fadeOut(remove);
                                        });
                           }
         
         
         
         
         
         
                            $Buscar_ventas_netasproductofactura.click(function(event){
                                event.preventDefault();
        
                                ///GENERA LA VISTA DE LA IMPRESION DEL REPORTE DE POR CLIENTE
                                if($select_tipo_reporte.val()== 1 ){ 
                                    $div_ventas_netas_productofactura.children().remove();
                                    var tipo_reporte= $select_tipo_reporte.val();
                                    var cliente = $Nombre_Cliente.val();
                                    var producto = $Nombre_Producto.val();
                                    var fecha_inicial = $fecha_inicial.val();
                                    var fecha_final = $fecha_final.val();
                                    var usuario = config.getUi();

                                    if(fecha_inicial != "" && fecha_final != ""){ 
                                        var arreglo_parametros = {tipo_reporte : $select_tipo_reporte.val() ,cliente : $Nombre_Cliente.val() , producto : $Nombre_Producto.val(), fecha_inicial : $fecha_inicial.val() , fecha_final : $fecha_final.val(),linea:$select_linea.val(),marca:$select_marca.val(),familia:$select_familia.val(),subfamilia:$select_subfamilia.val(),iu:config.getUi()};
                                        var restful_json_service = config.getUrlForGetAndPost() + '/getVentasNetasProductoFactura/out.json';
                                        var cliente="";
                                        var producto="";
				
                                            $.post(restful_json_service,arreglo_parametros,function(entry){
                                                var body_tabla = entry; 
                                                var header_tabla = {
                                                                    Codigo         :'Codigo',
                                                                    Producto       :'Producto',
                                                                    Factura        :'Factura',
                                                                    Fecha_factura  : 'Fecha Factura',
                                                                    Unidad         : 'Unidad',
                                                                    Cantidad       : 'Cantidad',
                                                                    Monedapu       :"",
                                                                    Precio_unitario: 'P.Unitario',
                                                                    Monedavn       :"",
                                                                    Venta_Neta     : 'V.Neta',
                                                                    denominacion   : 'Denom.',
                                                                    MonedaT_C      : '',
                                                                    Tipo_Cambio    :"T.Cambio"
                                                };

                                                var totalpesos = 0.0;
                                                var totalxcliente= 0.0;
                                                var totalunidades=0.0;
                                                var tmp = 0;
                                                var html_ventasnetas="";
                                                    html_ventasnetas = '<table id="ventas" width="100%">';

                                                                html_ventasnetas +='<thead> <tr>';
						for(var key in header_tabla){
							var attrValue = header_tabla[key];

						      //html_ventasnetas +='<td  align="left">'+attrValue+'</td>'; 
                                               
							if(attrValue == "Codigo"){
								html_ventasnetas +='<td   align="left" >'+attrValue+'</td>'; 
							}
							if(attrValue == "Producto"){
								html_ventasnetas +='<td    align="left" >'+attrValue+'</td>'; 
							}
							if(attrValue == "Factura"){
								html_ventasnetas +='<td  width="63px" align="center">'+attrValue+'</td>'; 
							}
                                                        
                                                        if(attrValue == "Fecha Factura"){
								html_ventasnetas +='<td   align="center">'+attrValue+'</td>'; 
							}
                                                        
							if(attrValue == "Unidad"){
								html_ventasnetas +='<td width="40px" align="right" >'+attrValue+'</td>'; 
							}
							if(attrValue == "Cantidad"){
								html_ventasnetas +='<td width="74px" align="right" >'+attrValue+'</td>'; 
							}
							if(attrValue == ""){
								html_ventasnetas +='<td width="5px" align="right" >'+attrValue+'</td>'; 
							}
							if(attrValue == "P.Unitario"){
								html_ventasnetas +='<td width="73px"  align="right" >'+attrValue+'</td>'; 
							}
							
							if(attrValue == "V.Neta"){
								html_ventasnetas +='<td width="80px" align="right" >'+attrValue+'</td>'; 
							}
							if(attrValue == "Denom."){
								html_ventasnetas +='<td width="50px" align="right" >'+attrValue+'</td>'; 
                                                        }
							if(attrValue == "T.Cambio"){
								html_ventasnetas +='<td width="50px" align="right" >'+attrValue+'</td>'; 
							}
						}
                                                                html_ventasnetas +='</tr> </thead>';
                            
                                                for(var i=0; i<body_tabla.length; i++){
                                                        unidad=body_tabla[i]["unidad"];
							if(cliente != body_tabla[i]["razon_social"]&& unidad ){
                                                                if (tmp == 0){
									html_ventasnetas +='<tr>';
										html_ventasnetas +='<td align="left" colspan ="13" > <strong>'+body_tabla[i]["razon_social"]+'</strong></td>'
									html_ventasnetas +='</tr>';
										
									html_ventasnetas +='<tr>';
                                                                                html_ventasnetas +='<td width="50px" align="left" >'+body_tabla[i]["codigo"]+'</td>'; 
										html_ventasnetas +='<td width="240px" align="left" >'+body_tabla[i]["producto"]+'</td>'; 
										html_ventasnetas +='<td width="80px" align="center" >'+body_tabla[i]["factura"]+'</td>'; 
                                                                                html_ventasnetas +='<td width="80px" align="center" >'+body_tabla[i]["fecha_factura"]+'</td>'; 
										html_ventasnetas +='<td width="40px" align="right" >'+body_tabla[i]["unidad"]+'</td>';
										html_ventasnetas +='<td width="70px" align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
										html_ventasnetas +='<td width="5px" align="right">'+"$"+'</td>'; 
										html_ventasnetas +='<td width="70px" align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>';
										html_ventasnetas +='<td width="5px" align="right">'+"$"+'</td>'; 
										html_ventasnetas +='<td width="70px" align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_pesos"]).toFixed(2))+'</td>';
										html_ventasnetas +='<td width="50px"  align="right" >'+body_tabla[i]["moneda"]+'</td>';
										html_ventasnetas +='<td width="5px"  align="right">'+"$"+'</td>'; 
										html_ventasnetas +='<td width="50px" align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["tipo_cambio"]).toFixed(2))+'</td>';
									html_ventasnetas +='</tr>';
									
									
									
								}
                                                                if (tmp != 0){
									html_ventasnetas +='<tr>';
                                                                                html_ventasnetas +='<td align="right" colspan="5" ><strong>Total:</strong></td>';
                                                                                html_ventasnetas +='<td align="right" ><font  color="Black"> <strong>'+$(this).agregar_comas(parseFloat(totalunidades).toFixed(2))+'</strong></td>';
										html_ventasnetas +='<td  colspan="2" ><strong>'+"Total por cliente:"+'</strong></td>'; 
										html_ventasnetas +='<td  align="right">'+"$"+'</td>'; //$(this).agregar_comas(parseFloat(totalxcliente).toFixed(2)) 
										html_ventasnetas +='<td align="right" ><font  color="Black"> <strong>'+$(this).agregar_comas(parseFloat(totalxcliente).toFixed(2))+'</strong></td>';
										html_ventasnetas +='<td align="right" >'+""+'</td>';
                                                                                html_ventasnetas +='<td align="right" >'+""+'</td>';
                                                                                html_ventasnetas +='<td align="right" >'+""+'</td>';
									html_ventasnetas +='</tr>';
                                                                        totalxcliente=0.0;
                                                                        totalunidades=0.0;
                                                                        unidad="";
                                                                        
                                                                        html_ventasnetas +='<tr>';
										html_ventasnetas +='<td align="left" colspan ="13" ><font  color="Black"> <strong>'+body_tabla[i]["razon_social"]+'</strong></td>'
									html_ventasnetas +='</tr>';
										
									html_ventasnetas +='<tr>';
									//html_ventasnetas +='<td align="left" >'+body_tabla[i]["razon_social"]+'</td>'; 
										html_ventasnetas +='<td align="left" >'+body_tabla[i]["codigo"]+'</td>'; 
										html_ventasnetas +='<td align="left" >'+body_tabla[i]["producto"]+'</td>'; 
										html_ventasnetas +='<td align="center" >'+body_tabla[i]["factura"]+'</td>'; 
                                                                                html_ventasnetas +='<td align="center" >'+body_tabla[i]["fecha_factura"]+'</td>'; 
										html_ventasnetas +='<td align="right" >'+body_tabla[i]["unidad"]+'</td>';
										html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
										html_ventasnetas +='<td  align="right">'+"$"+'</td>'; 
										html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>';
										html_ventasnetas +='<td  align="right">'+"$"+'</td>'; 
										html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_pesos"]).toFixed(2))+'</td>';
										html_ventasnetas +='<td align="right" >'+body_tabla[i]["moneda"]+'</td>';
										html_ventasnetas +='<td  align="right">'+"$"+'</td>'; 
										html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["tipo_cambio"]).toFixed(2))+'</td>';
									html_ventasnetas +='</tr>';
                                                                        
                                                                  }
                                                                tmp=1;
								totalunidades=totalunidades +parseFloat(body_tabla[i]["cantidad"]);
								totalpesos=totalpesos +parseFloat(body_tabla[i]["total_pesos"]);
								totalxcliente=totalxcliente +parseFloat(body_tabla[i]["total_pesos"]);
								cliente= body_tabla[i]["razon_social"];
                                                                unidad=body_tabla[i]["unidad"];
                                                                
							}else{  
								html_ventasnetas +='<tr>';
									html_ventasnetas +='<td align="left" >'+body_tabla[i]["codigo"]+'</td>'; 
									html_ventasnetas +='<td align="left" >'+body_tabla[i]["producto"]+'</td>'; 
									html_ventasnetas +='<td align="center" >'+body_tabla[i]["factura"]+'</td>'; 
                                                                        html_ventasnetas +='<td align="center" >'+body_tabla[i]["fecha_factura"]+'</td>'; 
									html_ventasnetas +='<td align="right" >'+body_tabla[i]["unidad"]+'</td>';
									html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
									html_ventasnetas +='<td  align="right">'+"$"+'</td>'; 
									html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>';
									html_ventasnetas +='<td  align="right">'+"$"+'</td>'; 
									html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_pesos"]).toFixed(2))+'</td>';
									html_ventasnetas +='<td align="right" >'+body_tabla[i]["moneda"]+'</td>';
									html_ventasnetas +='<td  align="right">'+"$"+'</td>'; 
									html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["tipo_cambio"]).toFixed(2))+'</td>';
								html_ventasnetas +='</tr>';
								cliente= body_tabla[i]["razon_social"];
                                                                unidad=body_tabla[i]["unidad"];
								totalpesos=totalpesos +parseFloat(body_tabla[i]["total_pesos"]);
                                                                totalxcliente=totalxcliente +parseFloat(body_tabla[i]["total_pesos"]);
                                                                totalunidades=totalunidades +parseFloat(body_tabla[i]["cantidad"]);
                                                                
                                                                
							}
                                                }
                                                                html_ventasnetas +='<tr>';
                                                                        html_ventasnetas +='<td align="right" colspan="5"><strong>'+"Total:"+'</strong></td>';
                                                                        html_ventasnetas +='<td align="right" ><font  color="Black"> <strong>'+$(this).agregar_comas(parseFloat(totalunidades).toFixed(2))+'</strong></td>';
                                                                        html_ventasnetas +='<td  colspan="2" ><strong>'+"Total por cliente:"+'</strong></td>'; 
                                                                        html_ventasnetas +='<td  align="right">'+"$"+'</td>'; //$(this).agregar_comas(parseFloat(totalxcliente).toFixed(2)) 
                                                                        html_ventasnetas +='<td align="right" ><font  color="Black"> <strong>'+$(this).agregar_comas(parseFloat(totalxcliente).toFixed(2))+'</strong></td>'
                                                                        html_ventasnetas +='<td align="right" >'+""+'</td>';
                                                                        html_ventasnetas +='<td align="right" >'+""+'</td>';
                                                                        html_ventasnetas +='<td align="right" >'+""+'</td>';
                                                                        
                                                                html_ventasnetas +='</tr>';
                                                    
                                                                html_ventasnetas +='<tr>';
                                                                        html_ventasnetas +='<td align="right" colspan ="8" ><strong>'+"TOTAL GENERAL:   "+'</strong></td>'; 
                                                                        html_ventasnetas +='<td align="right"  >'+"$"+'</td>'; 
                                                                        html_ventasnetas +='<td align="right" ><strong>'+$(this).agregar_comas(parseFloat(totalpesos).toFixed(2))+'</strong></td>'; 
                                                                        html_ventasnetas +='<td align="right"  >'+"   "+'</td>'; 
                                                                        html_ventasnetas +='<td align="right"  >'+"   "+'</td>'; 
                                                                        html_ventasnetas +='<td align="right"  >'+"   "+'</td>'; 
                                                                html_ventasnetas +='</tr>';
						html_ventasnetas += '</table>';

						$div_ventas_netas_productofactura.append(html_ventasnetas); 
						var height2 = $('#cuerpo').css('height');
						var alto = parseInt(height2)-250;
						var pix_alto=alto+'px';

						$('#ventas').tableScroll({height:parseInt(pix_alto)});
                                });//fin del json
			}else{
					jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
			}
		}
    
    
    
    
    
    
    ///GENERA LA VISTA DE LA IMPRESION DEL REPORTE DE POR PRODUCTO
    
    
    
    
    
    
		if ($select_tipo_reporte.val() == 2 ){
                        $div_ventas_netas_productofactura.children().remove();
			var tipo_reporte= $select_tipo_reporte.val();
			var cliente = $Nombre_Cliente.val();
			var producto = $Nombre_Producto.val();
			var fecha_inicial = $fecha_inicial.val();
			var fecha_final = $fecha_final.val();
			var usuario = config.getUi();
			
			if(fecha_inicial != "" && fecha_final != ""){ 
				var arreglo_parametros = {tipo_reporte : $select_tipo_reporte.val() ,cliente : $Nombre_Cliente.val() , producto : $Nombre_Producto.val(), fecha_inicial : $fecha_inicial.val() , fecha_final : $fecha_final.val(), iu:config.getUi()};
				var restful_json_service = config.getUrlForGetAndPost() + '/getVentasNetasProductoFactura/out.json';
				var cliente="";
				var producto="";
				
				$.post(restful_json_service,arreglo_parametros,function(entry){
					var body_tabla = entry; 
					var header_tabla = {
                                                            Codigo:'N.Control',
                                                            Producto  :'Cliente',
                                                            Factura : 'Factura',
                                                            Fecha_Factura : 'F.Factura',
                                                            Unidad : 'Unidad',
                                                            Cantidad : 'Cantidad',
                                                            Monedapu    :"",
                                                            Precio_unitario : 'P.Unitario',
                                                            Monedavn    :"",
                                                            Venta_Neta : 'V.Neta',
                                                            denominacion : 'Denom.',
                                                            MonedaT_C : '',
                                                            Tipo_Cambio    :"T.Cambio"
					};

					var totalpesos = 0.0;
					var totalunidades=0.0;
                                        var totalxproducto= 0.0;
					var tmp = 0;
					var html_ventasnetas = '<table id="ventas" width="100%" >';

					html_ventasnetas +='<thead> <tr>';
					for(var key in header_tabla){
							var attrValue = header_tabla[key];

							//html_ventasnetas +='<td  align="left">'+attrValue+'</td>'; 
                                               
							if(attrValue == "N.Control"){
								html_ventasnetas +='<td  align="left" width="5px"  >'+attrValue+'</td>'; 
							}
							if(attrValue == "Cliente"){
								html_ventasnetas +='<td    align="left" width="320px" >'+attrValue+'</td>'; 
							}
							if(attrValue == "Factura"){
								html_ventasnetas +='<td  align="center" width="5px">'+attrValue+'</td>'; 
							}
                                                        
                                                        if(attrValue == "F.Factura"){
								html_ventasnetas +='<td  align="center" width="4px" >'+attrValue+'</td>'; 
							}
                                                        
							if(attrValue == "Unidad"){
								html_ventasnetas +='<td  align="right"  width="85px" >'+attrValue+'</td>'; 
							}
							if(attrValue == "Cantidad"){
								html_ventasnetas +='<td  align="right" width="68px">'+attrValue+'</td>'; 
							}
							if(attrValue == ""){
								html_ventasnetas +='<td  align="right" width="38px">'+attrValue+'</td>'; 
							}
							if(attrValue == "P.Unitario"){
								html_ventasnetas +='<td   align="right" width="8px">'+attrValue+'</td>'; 
							}
							
							if(attrValue == "V.Neta"){
								html_ventasnetas +='<td  align="right" width="5px" >'+attrValue+'</td>'; 
							}
							if(attrValue == "Denom."){
								html_ventasnetas +='<td  align="right" width="10px">'+attrValue+'</td>'; 
                                                        }
                                                        
							if(attrValue == "T.Cambio"){
								html_ventasnetas +='<td align="right" width="20px" >'+attrValue+'</td>'; 
							}
                                                        //width="30px"
                                        }
                                                                html_ventasnetas +='</tr> </thead>';
                            
                                        for(var i=0; i<body_tabla.length; i++){
                                                        unidad=body_tabla[i]["unidad"];
							if(producto != body_tabla[i]["producto"] && unidad){
                                                            
                                                                if (tmp == 0){
									html_ventasnetas +='<tr>';
										html_ventasnetas +='<td align="left" colspan ="14" > <strong>'+body_tabla[i]["producto"]+'</strong></td>'
									html_ventasnetas +='</tr>';
										
									html_ventasnetas +='<tr>';
									//html_ventasnetas +='<td align="left" >'+body_tabla[i]["razon_social"]+'</td>'; 
										html_ventasnetas +='<td align="left" width="50px"  >'+body_tabla[i]["numero_control"]+'</td>'; 
										html_ventasnetas +='<td  align="left" width="300px">'+body_tabla[i]["razon_social"]+'</td>'; 
										html_ventasnetas +='<td  align="center" width="60px">'+body_tabla[i]["factura"]+'</td>'; 
                                                                                html_ventasnetas +='<td  align="center" width="80px">'+body_tabla[i]["fecha_factura"]+'</td>'; 
										html_ventasnetas +='<td  align="right" width="85px"">'+body_tabla[i]["unidad"]+'</td>';
										html_ventasnetas +='<td  align="right" width="65px">'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
										html_ventasnetas +='<td  align="right" width="50px">'+"$"+'</td>'; 
										html_ventasnetas +='<td  align="right" width="75px">'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>';
										html_ventasnetas +='<td  align="right" width="50px">'+"$"+'</td>'; 
										html_ventasnetas +='<td  align="right" width="85px">'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_pesos"]).toFixed(2))+'</td>';
										html_ventasnetas +='<td align="right" width="45px">'+body_tabla[i]["moneda"]+'</td>';
										html_ventasnetas +='<td  align="right" width="50px">'+"$"+'</td>'; 
										html_ventasnetas +='<td align="right" width="55px">'+$(this).agregar_comas(parseFloat(body_tabla[i]["tipo_cambio"]).toFixed(2))+'</td>';
									html_ventasnetas +='</tr>';
									
									
									
								}
                                                                if (tmp != 0){
									html_ventasnetas +='<tr>';
									//html_ventasnetas +='<td align="left" >'+body_tabla[i]["razon_social"]+'</td>'; 
                                                                                html_ventasnetas +='<td align="right" colspan="5" ><strong>'+"Total:"+'</strong></td>'; 
										html_ventasnetas +='<td align="right" ><font  color="Black"> <strong>'+$(this).agregar_comas(parseFloat(totalunidades).toFixed(2))+'</strong></td>' 
										html_ventasnetas +='<td align="right" colspan="2" ><strong>'+"Total por Producto:"+'</strong></td>'; 
										html_ventasnetas +='<td align="right" width="15px">'+"$"+'</td>'; 
										html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(totalxproducto).toFixed(2))+'</td>';
										html_ventasnetas +='<td align="right" colspan="3" >'+""+'</td>';
										
                                                                               
                                                                                
									html_ventasnetas +='</tr>';
                                                                        totalxproducto=0.0;
                                                                        totalunidades=0.0;
                                                                        unidad="";
                                                                        html_ventasnetas +='<tr>';
										html_ventasnetas +='<td align="left" colspan ="14" ><strong>'+body_tabla[i]["producto"]+'</strong></td>'
									html_ventasnetas +='</tr>';
										
									html_ventasnetas +='<tr>';
									//html_ventasnetas +='<td align="left" >'+body_tabla[i]["razon_social"]+'</td>'; 
										html_ventasnetas +='<td align="left" >'+body_tabla[i]["numero_control"]+'</td>'; 
										html_ventasnetas +='<td align="left" >'+body_tabla[i]["razon_social"]+'</td>'; 
										html_ventasnetas +='<td align="center" >'+body_tabla[i]["factura"]+'</td>'; 
                                                                                html_ventasnetas +='<td align="center" >'+body_tabla[i]["fecha_factura"]+'</td>';
										html_ventasnetas +='<td align="right"  width="15px" >'+body_tabla[i]["unidad"]+'</td>';
										html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
										html_ventasnetas +='<td align="right" width="35px">'+"$"+'</td>'; 
										html_ventasnetas +='<td align="right" width="75px">'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>';
										html_ventasnetas +='<td align="right" width="35px">'+"$"+'</td>'; 
										html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_pesos"]).toFixed(2))+'</td>';
										html_ventasnetas +='<td align="right" >'+body_tabla[i]["moneda"]+'</td>';
										html_ventasnetas +='<td align="right" width="35px">'+"$"+'</td>'; 
										html_ventasnetas +='<td align="right" width="45px">'+$(this).agregar_comas(parseFloat(body_tabla[i]["tipo_cambio"]).toFixed(2))+'</td>';
									html_ventasnetas +='</tr>';
                                                                }
                                                                tmp=1;
								totalunidades=totalunidades +parseFloat(body_tabla[i]["cantidad"]); 
								totalpesos=totalpesos +parseFloat(body_tabla[i]["total_pesos"]);
								totalxproducto=totalxproducto +parseFloat(body_tabla[i]["total_pesos"]);
								producto= body_tabla[i]["producto"];
                                                                unidad=body_tabla[i]["cantidad"];
							}else{  
								html_ventasnetas +='<tr>';
									html_ventasnetas +='<td align="left" >'+body_tabla[i]["numero_control"]+'</td>'; 
									html_ventasnetas +='<td align="left" >'+body_tabla[i]["razon_social"]+'</td>'; 
									html_ventasnetas +='<td align="center" >'+body_tabla[i]["factura"]+'</td>'; 
                                                                        html_ventasnetas +='<td align="center" >'+body_tabla[i]["fecha_factura"]+'</td>';
									html_ventasnetas +='<td align="right" >'+body_tabla[i]["unidad"]+'</td>';
									html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
									html_ventasnetas +='<td align="right" width="15">'+"$"+'</td>'; 
									html_ventasnetas +='<td align="right" width="75px">'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>';
									html_ventasnetas +='<td align="right"width="15" >'+"$"+'</td>'; 
									html_ventasnetas +='<td align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_pesos"]).toFixed(2))+'</td>';
									html_ventasnetas +='<td align="right" >'+body_tabla[i]["moneda"]+'</td>';
									html_ventasnetas +='<td align="right" width="15">'+"$"+'</td>'; 
									html_ventasnetas +='<td align="right" width="45px">'+$(this).agregar_comas(parseFloat(body_tabla[i]["tipo_cambio"]).toFixed(2))+'</td>';
								html_ventasnetas +='</tr>';
								producto= body_tabla[i]["producto"];
								totalpesos=totalpesos +parseFloat(body_tabla[i]["total_pesos"]);
                                                                totalxproducto=totalxproducto +parseFloat(body_tabla[i]["total_pesos"]);
                                                                totalunidades=totalunidades +parseFloat(body_tabla[i]["cantidad"]);
							}
                                        }
                                                                html_ventasnetas +='<tr>';
                                                                        html_ventasnetas +='<td align="right" colspan="5" ><strong>'+"Total:"+'</strong></td>'; 
                                                                        html_ventasnetas +='<td align="right" ><font  color="Black"> <strong>'+$(this).agregar_comas(parseFloat(totalunidades).toFixed(2))+'</strong></td>';
                                                                        html_ventasnetas +='<td align="right" colspan="2" ><strong>'+"Total por Producto:"+'</strong></td>'; 
                                                                        html_ventasnetas +='<td  align="right" widht="15px">'+"$"+'</td>'; //$(this).agregar_comas(parseFloat(totalxcliente).toFixed(2)) 
                                                                        html_ventasnetas +='<td align="right" ><font  color="Black"> <strong>'+$(this).agregar_comas(parseFloat(totalxproducto).toFixed(2))+'</strong></td>';
                                                                        html_ventasnetas +='<td align="right" >'+""+'</td>'; 
                                                                        html_ventasnetas +='<td align="right" >'+""+'</td>';
                                                                        html_ventasnetas +='<td align="right" >'+""+'</td>';
                                                                        
                                                                       
                                                                html_ventasnetas +='</tr>';
                                                        
                                                        
                                                                html_ventasnetas +='<tr>';
                                                                        /*html_ventasnetas +='<td align="right" colspan ="5" ><strong>'+"TOTAL GENERAL KILOS:   "+'</strong></td>'; 
                                                                        html_ventasnetas +='<td align="right"  >'+""+'</td>'; */
                                                                        html_ventasnetas +='<td align="right" colspan ="8" ><strong>'+"TOTAL GENERAL:   "+'</strong></td>'; 
                                                                        html_ventasnetas +='<td align="right"  >'+"$"+'</td>'; 
                                                                        html_ventasnetas +='<td align="right" ><font  color="Black"> <strong>'+$(this).agregar_comas(parseFloat(totalpesos).toFixed(2))+'</strong></font></td>';             
                                                                        html_ventasnetas +='<td align="right" >'+""+'</td>'; 
                                                                        html_ventasnetas +='<td align="right" >'+""+'</td>';
                                                                        html_ventasnetas +='<td align="right" >'+""+'</td>';
                                                                html_ventasnetas +='</tr>';
						html_ventasnetas += '</table>';

						$div_ventas_netas_productofactura.append(html_ventasnetas); 
						var height2 = $('#cuerpo').css('height');
						var alto = parseInt(height2)-250;
						var pix_alto=alto+'px';

						$('#ventas').tableScroll({height:parseInt(pix_alto)});
				});
			}else{
				jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
			}
                        
                }
		
                
                
       
   ////////////////////// VENTAS NETAS SUMARIZADO POR PRODUCTO
   
   
   
   
                if ($select_tipo_reporte.val() == 3 ){
                        $div_ventas_netas_productofactura.children().remove();
                        var tipo_reporte= $select_tipo_reporte.val();
                        var cliente = $Nombre_Cliente.val();
                        var producto = $Nombre_Producto.val();
                        var fecha_inicial = $fecha_inicial.val();
                        var sfecha_final = $fecha_final.val();
                        var usuario = config.getUi();
			
			if(fecha_inicial != "" && fecha_final != ""){ 
				var arreglo_parametros = {tipo_reporte : $select_tipo_reporte.val() ,cliente : $Nombre_Cliente.val() , producto : $Nombre_Producto.val(), fecha_inicial : $fecha_inicial.val() , fecha_final : $fecha_final.val(), iu:config.getUi()};
				var restful_json_service = config.getUrlForGetAndPost() + '/getVentasNetasProductoFactura/out.json';
				var cliente="";
				var producto="";
                                var unidad="";
                                var cantidad=0.0;
                                var precio_unitario=0.0;
                                var venta_neta=0.0;
                                var contador_precio_unitario=0;
				
				$.post(restful_json_service,arreglo_parametros,function(entry){
					var body_tabla = entry; 
					var header_tabla = {
                                                            Producto  :'Producto',
                                                            Unidad : 'Unidad',
                                                            Total : 'Total',
                                                            Monedapu    :"",
                                                            Precio_unitario : 'P.Promedio',
                                                            Monedavn    :"",
                                                            Venta_Neta : 'V.Total'
					};

					var totalpesos = 0.0;
					var totalxproducto= 0.0;
					var tmp = 0;
					var html_ventasnetas = '<table id="ventas" width="100%" >';

                                                html_ventasnetas +='<thead> <tr>';
						for(var key in header_tabla){
							var attrValue = header_tabla[key];
                                                        //html_ventasnetas +='<td  align="left">'+attrValue+'</td>'; 
                                                        if(attrValue == "Producto"){
								html_ventasnetas +='<td  width="50px" align="left" >'+attrValue+'</td>'; 
							}
							
                                                        
							if(attrValue == "Unidad"){
								html_ventasnetas +='<td  align="right" >'+attrValue+'</td>'; 
							}
							if(attrValue == "Total"){
								html_ventasnetas +='<td  align="right" >'+attrValue+'</td>'; 
							}
							if(attrValue == ""){
								html_ventasnetas +='<td width="8px" align="right" >'+attrValue+'</td>'; 
							}
							if(attrValue == "P.Promedio"){
								html_ventasnetas +='<td   align="right" >'+attrValue+'</td>'; 
							}
							
							if(attrValue == "V.Total"){
								html_ventasnetas +='<td  align="right" >'+attrValue+'</td>'; 
							}
						}
                                                                html_ventasnetas +='</tr> </thead>';
                                                var n_c=1;  
                                                for(var i=0; i<body_tabla.length; i++){
                            
                                                    if(producto != body_tabla[i]["producto"] ){
                                                        if (tmp == 0){
                                                                if(n_c==1){
                                                                        if( $Nombre_Cliente.val() !=0){
                                                                            html_ventasnetas +='<tr>';
                                                                                html_ventasnetas +='<td  colspan="5" align="left" ><strong>'+body_tabla[i]["razon_social"]+'<strong></td>';
                                                                            html_ventasnetas +='<tr>'; 
                                                                            n_c=2;
                                                                        }
                                                                }
                                                                /*html_ventasnetas +='<tr>';
                                                                        html_ventasnetas +='<td width="300px" align="left" >'+body_tabla[i]["producto"]+'</td>'; 
                                                                        html_ventasnetas +='<td  align="right" >'+body_tabla[i]["unidad"]+'</td>';
                                                                        html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
                                                                        html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                        html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>';
                                                                        html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                        html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_pesos"]).toFixed(2))+'</td>';
                                                                html_ventasnetas +='</tr>';*/
                                                                venta_neta= venta_neta+parseFloat(body_tabla[i]["total_pesos"]);
                                                                contador_precio_unitario=contador_precio_unitario+1;
                                                        }

                                                        if (tmp != 0){
                                                    
                                                            html_ventasnetas +='<tr>';
                                                                        html_ventasnetas +='<td width="300px" align="left" >'+producto+'</td>'; 
                                                                        html_ventasnetas +='<td  align="right" >'+unidad+'</td>';
                                                                        html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(cantidad).toFixed(2))+'</td>';
                                                                        html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                        html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(precio_unitario/contador_precio_unitario).toFixed(2))+'</td>';
                                                                        html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                        html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(venta_neta).toFixed(2))+'</td>';
                                                            html_ventasnetas +='</tr>';
                                                    
                                                            producto=body_tabla[i]["producto"];
                                                            cantidad=0.0;
                                                            precio_unitario=0.0;
                                                            contador_precio_unitario=0;
                                                            venta_neta=0.0
                                            
                                                            /*html_ventasnetas +='<tr>';
                                                                    html_ventasnetas +='<td width="300px" align="left" >'+body_tabla[i]["producto"]+'</td>'; 
                                                                    html_ventasnetas +='<td  align="right" >'+body_tabla[i]["unidad"]+'</td>';
                                                                    html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
                                                                    html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                    html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>';
                                                                    html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                    html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_pesos"]).toFixed(2))+'</td>';
                                                            html_ventasnetas +='</tr>';*/
                                                
                                                            venta_neta= venta_neta+parseFloat(body_tabla[i]["total_pesos"]);
                                                            contador_precio_unitario=contador_precio_unitario+1;

                                                        }

                                                        tmp=1;
                                                        cantidad=cantidad + parseFloat(body_tabla[i]["cantidad"]);
                                                        precio_unitario=precio_unitario + parseFloat(body_tabla[i]["precio_unitario"]);
                                                        producto=body_tabla[i]["producto"];
                                                        unidad=body_tabla[i]["unidad"];

                                                        //contador_precio_unitario=contador_precio_unitario+1;
                                
                                                    }else{
                                                        /*html_ventasnetas +='<tr>';
                                                                html_ventasnetas +='<td width="300px" align="left" >'+"mismo prod"+'</td>'; 
                                                                html_ventasnetas +='<td  align="right" >'+"misma unidad"+'</td>';
                                                                html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
                                                                html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>';
                                                                html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_pesos"]).toFixed(2))+'</td>';
                                                        html_ventasnetas +='</tr>';*/
                                                    
                                                        cantidad=cantidad + parseFloat(body_tabla[i]["cantidad"]);
                                                        precio_unitario=precio_unitario + parseFloat(body_tabla[i]["precio_unitario"]);

                                                        venta_neta= venta_neta+parseFloat(body_tabla[i]["total_pesos"]);
                                                        contador_precio_unitario= contador_precio_unitario+1;
                                                    }
                            
                                                }
                                                                html_ventasnetas +='<tr>';
                                                                            html_ventasnetas +='<td width="300px" align="left" >'+producto+'</td>'; 
                                                                            html_ventasnetas +='<td  align="right" >'+unidad+'</td>';
                                                                            html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(cantidad).toFixed(2))+'</td>';
                                                                            html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                            html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(precio_unitario/contador_precio_unitario).toFixed(2))+'</td>';
                                                                            html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                            html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(venta_neta).toFixed(2))+'</td>';
                                                                html_ventasnetas +='</tr>';  
                                                       
                                                        html_ventasnetas += '</table>';

                                                $div_ventas_netas_productofactura.append(html_ventasnetas); 
                                                var height2 = $('#cuerpo').css('height');
                                                var alto = parseInt(height2)-250;
                                                var pix_alto=alto+'px';
                                                $('#ventas').tableScroll({height:parseInt(pix_alto)});
				});
			}else{
				jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
			}
                }//FIN DE LA VISTA DE PRODUCTO SUMARIZADO
                
                
                
                
                
                
   ////////////////////////////////////// SUMARIZADO POR  CLIENTE
   
   
   
   
   
   
                if ($select_tipo_reporte.val() == 4 ){
                            $div_ventas_netas_productofactura.children().remove();
                            var tipo_reporte= $select_tipo_reporte.val();
                            var cliente = $Nombre_Cliente.val();
                            var producto = $Nombre_Producto.val();
                            var fecha_inicial = $fecha_inicial.val();
                            var sfecha_final = $fecha_final.val();
                            var usuario = config.getUi();

                            if(fecha_inicial != "" && fecha_final != ""){ 
                                    var arreglo_parametros = {tipo_reporte : $select_tipo_reporte.val() ,cliente : $Nombre_Cliente.val() , producto : $Nombre_Producto.val(), fecha_inicial : $fecha_inicial.val() , fecha_final : $fecha_final.val(), iu:config.getUi()};
                                    var restful_json_service = config.getUrlForGetAndPost() + '/getVentasNetasProductoFactura/out.json';
                                    var cliente="";
                                    var producto="";
                                    var unidad="";
                                    var cantidad=0.0;
                                    var precio_unitario=0.0;
                                    var venta_neta=0.0;
                                    var contador_precio_unitario=0;

                                        $.post(restful_json_service,arreglo_parametros,function(entry){
                                                var body_tabla = entry; 
                                                var header_tabla = {
                                                                    Cliente  :'Cliente',
                                                                    Unidad : 'Unidad',
                                                                    Total : 'Total',
                                                                    Monedapu    :"",
                                                                    Precio_unitario : 'P.Promedio',
                                                                    Monedavn    :"",
                                                                    Venta_Neta : 'V.Total'

                                                };

                                                var totalpesos = 0.0;

                                                var totalxcliente= 0.0;
                                                var tmp = 0;
                                                var html_ventasnetas = '<table id="ventas" width="100%" >';

                                                            html_ventasnetas +='<thead> <tr>';
                                                for(var key in header_tabla){
                                                                var attrValue = header_tabla[key];

                                                                //html_ventasnetas +='<td  align="left">'+attrValue+'</td>'; 
                                                                if(attrValue == "Cliente"){
                                                                        html_ventasnetas +='<td  width="50px" align="left" >'+attrValue+'</td>'; 
                                                                }

                                                                if(attrValue == "Unidad"){
                                                                        html_ventasnetas +='<td  align="right" >'+attrValue+'</td>'; 
                                                                }
                                                                if(attrValue == "Total"){
                                                                        html_ventasnetas +='<td  align="right" >'+attrValue+'</td>'; 
                                                                }
                                                                if(attrValue == ""){
                                                                        html_ventasnetas +='<td width="8px" align="right" >'+attrValue+'</td>'; 
                                                                }
                                                                if(attrValue == "P.Promedio"){
                                                                        html_ventasnetas +='<td   align="right" >'+attrValue+'</td>'; 
                                                                }

                                                                if(attrValue == "V.Total"){
                                                                        html_ventasnetas +='<td  align="right" >'+attrValue+'</td>'; 
                                                                }

                                                                //width="30px"
                                                }
                                                                        html_ventasnetas +='</tr> </thead>';
                               
                                                var n_c=1;  
                                                for(var i=0; i<body_tabla.length; i++){

                                                        if(cliente != body_tabla[i]["razon_social"] ){
                                                            if (tmp == 0){
                                                                if(n_c==1){
                                                                    if($Nombre_Producto.val() !=0 ){
                                                                        html_ventasnetas +='<tr>';
                                                                            html_ventasnetas +='<td  colspan="5" align="left" ><strong>'+body_tabla[i]["producto"]+'<strong></td>';
                                                                        html_ventasnetas +='<tr>'; 
                                                                        n_c=2;
                                                                    }
                                                                }

                                                                /*html_ventasnetas +='<tr>';
                                                                        html_ventasnetas +='<td width="300px" align="left" >'+body_tabla[i]["producto"]+'</td>'; 
                                                                        html_ventasnetas +='<td  align="right" >'+body_tabla[i]["unidad"]+'</td>';
                                                                        html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
                                                                        html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                        html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>';
                                                                        html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                        html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_pesos"]).toFixed(2))+'</td>';
                                                                html_ventasnetas +='</tr>';*/
                                                                venta_neta= venta_neta+parseFloat(body_tabla[i]["total_pesos"]);
                                                                contador_precio_unitario=contador_precio_unitario+1;

                                                            }

                                                            if (tmp != 0){

                                                                    //alert('Este es el valor del cliente'+$Nombre_Cliente.val());
                                                                html_ventasnetas +='<tr>';
                                                                            html_ventasnetas +='<td width="300px" align="left" >'+cliente+'</td>'; 
                                                                            html_ventasnetas +='<td  align="right" >'+unidad+'</td>';
                                                                            html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(cantidad).toFixed(2))+'</td>';
                                                                            html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                            html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(precio_unitario/contador_precio_unitario).toFixed(2))+'</td>';
                                                                            html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                            html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(venta_neta).toFixed(2))+'</td>';

                                                                html_ventasnetas +='</tr>';


                                                                // producto=body_tabla[i]["producto"];
                                                                cliente=body_tabla[i]["razon_social"];
                                                                cantidad=0.0;
                                                                precio_unitario=0.0;
                                                                contador_precio_unitario=0;
                                                                venta_neta=0.0

                                                                /*html_ventasnetas +='<tr>';
                                                                        html_ventasnetas +='<td width="300px" align="left" >'+body_tabla[i]["producto"]+'</td>'; 
                                                                        html_ventasnetas +='<td  align="right" >'+body_tabla[i]["unidad"]+'</td>';
                                                                        html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
                                                                        html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                        html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>';
                                                                        html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                        html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_pesos"]).toFixed(2))+'</td>';

                                                                html_ventasnetas +='</tr>';*/
                                                                venta_neta= venta_neta+parseFloat(body_tabla[i]["total_pesos"]);
                                                                contador_precio_unitario=contador_precio_unitario+1;
                                                            }

                                                            tmp=1;
                                                            cantidad=cantidad + parseFloat(body_tabla[i]["cantidad"]);
                                                            precio_unitario=precio_unitario + parseFloat(body_tabla[i]["precio_unitario"]);
                                                            cliente=body_tabla[i]["razon_social"];
                                                            unidad=body_tabla[i]["unidad"];

                                                            //contador_precio_unitario=contador_precio_unitario+1;

                                                        }else{
                                                            /*html_ventasnetas +='<tr>';
                                                                    html_ventasnetas +='<td width="300px" align="left" >'+"mismo prod"+'</td>'; 
                                                                    html_ventasnetas +='<td  align="right" >'+"misma unidad"+'</td>';
                                                                    html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
                                                                    html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                    html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>';
                                                                    html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                    html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_pesos"]).toFixed(2))+'</td>';

                                                            html_ventasnetas +='</tr>';*/

                                                            cantidad=cantidad + parseFloat(body_tabla[i]["cantidad"]);
                                                            precio_unitario=precio_unitario + parseFloat(body_tabla[i]["precio_unitario"]);

                                                            venta_neta= venta_neta+parseFloat(body_tabla[i]["total_pesos"]);
                                                            contador_precio_unitario= contador_precio_unitario+1;
                                                        }

                                                }
                                                            html_ventasnetas +='<tr>';
                                                                        html_ventasnetas +='<td width="300px" align="left" >'+cliente+'</td>'; 
                                                                        html_ventasnetas +='<td  align="right" >'+unidad+'</td>';
                                                                        html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(cantidad).toFixed(2))+'</td>';
                                                                        html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                        html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(precio_unitario/contador_precio_unitario).toFixed(2))+'</td>';
                                                                        html_ventasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                        html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(venta_neta).toFixed(2))+'</td>';

                                                            html_ventasnetas +='</tr>';  

                                                html_ventasnetas += '</table>';

                                                $div_ventas_netas_productofactura.append(html_ventasnetas); 
                                                var height2 = $('#cuerpo').css('height');
                                                var alto = parseInt(height2)-250;
                                                var pix_alto=alto+'px';

                                                $('#ventas').tableScroll({height:parseInt(pix_alto)});
                                        });
                            }else{
                                jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
                            }

                }//FIN DE LA VISTA DE  SUMARIZADO POR CLIENTE
                
    }); 
});   
       
        
        
        
    