/**
* 
* 
*/




!function($) {
    "use strict";

    var CalendarApp = function() {
        this.$body = $("body")
        this.$modal = $('#event-modal'),
        this.$event = ('#external-events div.external-event'),
        this.$calendar = $('#calendar'),
        this.$saveCategoryBtn = $('.save-category'),
        this.$categoryForm = $('#add-category form'),
        this.$extEvents = $('#external-events'),
        this.$calendarObj = null
    };


    /* on drop */
    CalendarApp.prototype.onDrop = function (eventObj, date) { 
        var $this = this;
            // retrieve the dropped element's stored Event Object
            var originalEventObject = eventObj.data('eventObject');
            var $categoryClass = eventObj.attr('data-class');
            // we need to copy it, so that multiple events don't have a reference to the same object
            var copiedEventObject = $.extend({}, originalEventObject);
            // assign it the date that was reported
            copiedEventObject.start = date;
            if ($categoryClass)
                copiedEventObject['className'] = [$categoryClass];
            // render the event on the calendar
            $this.$calendar.fullCalendar('renderEvent', copiedEventObject, true);
            // is the "remove after drop" checkbox checked?
            if ($('#drop-remove').is(':checked')) {
                // if so, remove the element from the "Draggable Events" list
                eventObj.remove();
            }
    },
    /* on click on event */
    CalendarApp.prototype.onEventClick =  function (calEvent, jsEvent, view) {
        var $this = this;
            var form = $("<form></form>");
            form.append("<label>Daily Hours & Extra Hours</label>");
            form.append("<div><input style='border: none;' type=text value='" + calEvent.title + "' readonly/></div>");
            form.append("<div style='display: none;'><input type=hidden name='id' value='" + calEvent.className + "' /></div>");
//            form.append("<div class='input-group'><input class='form-control' type=text value='" + calEvent.title + "' /><span class='input-group-btn'><button type='submit' class='btn btn-success waves-effect waves-light'><i class='fa fa-check'></i> Save</button></span></div>");
            $this.$modal.modal({
                backdrop: 'static'
            });
            if(calEvent.approve) {
	            $this.$modal.find('.delete-event').hide().end().find('.save-event').hide().end().find('.modal-body').empty().prepend(form).end().find('.delete-event').unbind('click').click(function () {
	            	$this.$calendarObj.fullCalendar('removeEvents', function (ev) {
	            		return (ev._id == calEvent._id);
	            	});
	            	
	            	//Delete hours
	            	$.ajax({
	            		type : "POST",
	            		url : context + "supervisor/delete-hours/"+ calEvent.id,
	            		success: function(data) {
	            			
	            			//Do nothing
	            		}, 
	            		error: function(xhr, status, error) {
	//            	    	unBlockUi();
	            		}
	            	});
	            	
	            	$this.$modal.modal('hide');
	            });
	            
            } else {
	            $this.$modal.find('.delete-event').show().end().find('.save-event').hide().end().find('.modal-body').empty().prepend(form).end().find('.delete-event').unbind('click').click(function () {
	                $this.$calendarObj.fullCalendar('removeEvents', function (ev) {
	                    return (ev._id == calEvent._id);
	                });
	                
	                //Delete hours
	                $.ajax({
	            		type : "POST",
	            	    url : context + "supervisor/delete-hours/"+ calEvent.id,
	            	    success: function(data) {
	            	    	
	            	    	//Do nothing
	            	    }, 
	            	    error: function(xhr, status, error) {
	//            	    	unBlockUi();
	                	}
	            	});
	                
	                $this.$modal.modal('hide');
	            });
            }
            $this.$modal.find('form').on('submit', function () {
                calEvent.title = form.find("input[type=text]").val();
                $this.$calendarObj.fullCalendar('updateEvent', calEvent);
                $this.$modal.modal('hide');
                return false;
            });
    },
    /* on select */
    CalendarApp.prototype.onSelect = function (start, end, allDay) {
    	
    	var $this = this;
    	
    	var params = new window.URLSearchParams(window.location.search);
    	var id = params.get('id');
    	var date = start._d.getTime();
    	
    	//Check date
        $.ajax({
    		type : "GET",
    	    url : context + "supervisor/check-date-approve?date="+ date,
    	    success: function(data) {
    	    	
    	    	 if(data != 'success') {
    	    		 location.href = context + "supervisor/add-hours?id=" + id + "&error=" + data;
    	    	 } else {
                 $this.$modal.modal({
                     backdrop: 'static'
                 });
                 var form = $("<form></form>");
                 form.append("<div class='row'></div>");
                 form.find(".row")
//                 	.append("<div class='col-md-6'><div class='form-group'><label class='control-label'>Event Name</label><input class='form-control' placeholder='Insert Event Name' type='text' name='title'/></div></div>")
//                     .append("<div class='col-md-6'><div class='form-group'><label class='control-label'>Category</label><select class='form-control' name='category'></select></div></div>")
                 
                 	.append("<div class='col-md-6'><div class='form-group'><label class='control-label'>Daily Hours</label><input id='clockpicker1' class='form-control clockpicker' placeholder='Insert Daily Hours' type='text' name='dailyHours'/></div></div>")
                     .append("<div class='col-md-6'><div class='form-group'><label class='control-label'>Extra Hours</label><input id='clockpicker2' class='form-control clockpicker' placeholder='Insert Extra Hours' type='text' name='extraHours'/></div></div>")
//                     .append("<div class='col-md-6'><div class='form-group'><label class='control-label'>Category</label><select class='form-control' name='category'></select></div></div>")
//                     .find("select[name='category']")
//                     .append("<option value='bg-danger'>Danger</option>")
//                     .append("<option value='bg-success'>Success</option>")
//                     .append("<option value='bg-purple'>Purple</option>")
//                     .append("<option value='bg-primary'>Primary</option>")
//                     .append("<option value='bg-pink'>Pink</option>")
//                     .append("<option value='bg-info'>Info</option>")
//                     .append("<option value='bg-inverse'>Inverse</option>")
//                     .append("<option value='bg-orange'>Orange</option>")
//                     .append("<option value='bg-brown'>Brown</option>")
//                     .append("<option value='bg-teal'>Teal</option>")
//                     .append("<option value='bg-warning'>Warning</option></div></div>");
                     .append("</div></div>");
                 $this.$modal.find('.delete-event').hide().end().find('.save-event').show().end().find('.modal-body').empty().prepend(form).end().find('.save-event').unbind('click').click(function () {
                     form.submit();
                 });
                 //Clock Picker
                 $('.clockpicker').clockpicker({
                 	autoclose: true
                 });
                 $this.$modal.find('form').on('submit', function () {
                 	var dailyHours = form.find("input[name='dailyHours']").val();
                     var extraHours = form.find("input[name='extraHours']").val();
                     var beginning = form.find("input[name='beginning']").val();
                     var ending = form.find("input[name='ending']").val();
                     
                     if ( (extraHours !== null && extraHours.length != 0) || (dailyHours !== null && dailyHours.length != 0)) {
                     	var title = "";
                     	if(extraHours !== null && extraHours.length != 0 && dailyHours !== null && dailyHours.length != 0 && extraHours != "0:00" && dailyHours != "0:00") {
                     		title = 'D - ' + dailyHours + ' & ' + 'E - ' + extraHours;
                     	} else if(dailyHours !== null && dailyHours.length != 0 && dailyHours != "0:00") {
                     		title = 'D - ' + dailyHours;
                     	} else if(extraHours !== null && extraHours.length != 0 && extraHours != "0:00") {
                     		title = 'E - ' + extraHours;
                     	}
                     	
                     	//Save hours
                         $.ajax({
                     		type : "POST",
                     	    url : context + "supervisor/add-hours?dailyHours="+ dailyHours +"&extraHours="+ extraHours +"&date="+ date + "&userDetailId="+ id,
                     	    success: function(data) {
                     	    	
                     	         $this.$calendarObj.fullCalendar('renderEvent', {
                                  	title:title,
                                      start:start,
                                      end: end,
                                      allDay: false,
                                      className: data.id
                                  }, true);  
                                  
                                  $this.$modal.modal('hide');
                                  
                                  location.href = context + "supervisor/add-hours?id=" + id;
                     	    	
                     	    }, 
                     	    error: function(xhr, status, error) {
//                     	    	unBlockUi();
                         	}
                     	});
                         
                
                     }
                     else{
                         alert('You have to give daily hours or extra hours');
                     }
                     return false;
                     
                 });
                 $this.$calendarObj.fullCalendar('unselect');
                 
    	    	 }
    	    	
    	    }, 
    	    error: function(xhr, status, error) {
//    	    	unBlockUi();
        	}
    	});
            
    },
    CalendarApp.prototype.enableDrag = function() {
        //init events
        $(this.$event).each(function () {
            // create an Event Object (http://arshaw.com/fullcalendar/docs/event_data/Event_Object/)
            // it doesn't need to have a start or end
            var eventObject = {
                title: $.trim($(this).text()) // use the element's text as the event title
            };
            // store the Event Object in the DOM element so we can get to it later
            $(this).data('eventObject', eventObject);
            // make the event draggable using jQuery UI
            $(this).draggable({
                zIndex: 999,
                revert: true,      // will cause the event to go back to its
                revertDuration: 0  //  original position after the drag
            });
        });
    }
    /* Initializing */
    CalendarApp.prototype.init = function() {
        this.enableDrag();
        /*  Initialize the calendar  */
        var date = new Date();
        var d = date.getDate();
        var m = date.getMonth();
        var y = date.getFullYear();
        var form = '';
        var today = new Date($.now());
        
        var $this = this;
    	var params = new window.URLSearchParams(window.location.search);
    	var id = params.get('id')
        //Get add hours
        $.ajax({
    		type : "GET",
    	    url : context + "supervisor/get-hours-list/" + id,
    	    success: function(data) {
    	    	
    	    	var defaultEvents = data;
    	    	
    	        $this.$calendarObj = $this.$calendar.fullCalendar({
    	            slotDuration: '00:15:00', /* If we want to split day time each 15minutes */
    	            minTime: '08:00:00',
    	            maxTime: '19:00:00',  
    	            defaultView: 'month',  
    	            handleWindowResize: true,   
    	            height: $(window).height() - 200,   
    	            header: {
    	                left: 'prev,next today',
    	                center: 'title',
    	                right: 'month'
    	            },
    	            events: defaultEvents,
    	            editable: true,
    	            droppable: true, // this allows things to be dropped onto the calendar !!!
    	            eventLimit: true, // allow "more" link when too many events
    	            selectable: true,
    	            drop: function(date) { $this.onDrop($(this), date); },
    	            select: function (start, end, allDay) { $this.onSelect(start, end, allDay); },
    	            eventClick: function(calEvent, jsEvent, view) { $this.onEventClick(calEvent, jsEvent, view); }

    	        });
    	    }, 
    	    error: function(xhr, status, error) {
//    	    	unBlockUi();
        	}
    	});
        
        function getCalendar() {
        	
        }

        //on new event
        this.$saveCategoryBtn.on('click', function(){
            var categoryName = $this.$categoryForm.find("input[name='category-name']").val();
            var categoryColor = $this.$categoryForm.find("select[name='category-color']").val();
            if (categoryName !== null && categoryName.length != 0) {
                $this.$extEvents.append('<div class="external-event bg-' + categoryColor + '" data-class="bg-' + categoryColor + '" style="position: relative;"><i class="mdi mdi-checkbox-blank-circle m-r-10 vertical-middle"></i>' + categoryName + '</div>')
                $this.enableDrag();
            }

        });
    },

   //init CalendarApp
    $.CalendarApp = new CalendarApp, $.CalendarApp.Constructor = CalendarApp
    
}(window.jQuery),

//initializing CalendarApp
function($) {
    "use strict";
    $.CalendarApp.init()
}(window.jQuery);
