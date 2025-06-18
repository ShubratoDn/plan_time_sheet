$(document).ready(function() {
	
	 $('#schedular-table').DataTable({
		"bLengthChange": false,
		"iDisplayLength":33,
		"processing" : true,
		"ordering" : false,
		"filter": false,
		"scrollX": true,
		"paging": false,
		"fixedHeader": {
	        "header": true,
	        "headerOffset":63
	    },
		"columnDefs": [
		     { "width": "150px", "targets": 1 }
		   ]
	});
	setFstDropdown();
	hoursNum();
	sumOfDailyHours();
	sumOfExtraHours();
	sumOfVacationHours();
	
});

/**
 * remove alphabetic word
 * @returns
 */
function hoursNum() {
	
	$('.hourNum').keypress(function (event) { 
		
		if(/*For dot*/event.which != 46 && event.which != 8 && event.which != 0 && (event.which < 48 || event.which > 57))
			return false;
		return true;
	});
}

/**
 * copy element to next line
 * @param thisValue
 * @returns
 */
function setDailyHoursNextLine(thisValue) {
	
	var currentClass = $(thisValue).closest('tr').find(".dailyHours");
	var nextElement = $(thisValue).closest('tr').next('tr').find(".dailyHours");
	$(nextElement).val($(currentClass).val());
	sumOfDailyHours();
}

/**
 * copy element to next line
 * @param thisValue
 * @returns
 */
function setExtraHoursNextLine(thisValue) {
	
	var currentClass = $(thisValue).closest('tr').find(".extraHours");
	var nextElement = $(thisValue).closest('tr').next('tr').find(".extraHours");
	$(nextElement).val($(currentClass).val());
	sumOfExtraHours();
}

/**
 * copy element to next line
 * @param thisValue
 * @returns
 */
function setVacationHoursNextLine(thisValue) {
	
	var currentClass = $(thisValue).closest('tr').find(".vacationHours");
	var nextElement = $(thisValue).closest('tr').next('tr').find(".vacationHours");
	$(nextElement).val($(currentClass).val());
	sumOfVacationHours();
}

/**
 * sum of column daily hours
 * @returns
 */
function sumOfDailyHours(){
	
	var total = 0.0;
	$(".dailyHours").each(function(i, obj){
		var num = $(obj).val();
		if(num == ""){
			$(obj).val("0.0")
		} else {
			total = total + parseFloat(num);
		}
	});
	$(".totalDailyHours").text(total.toFixed(2));
}

/**
 * sum of Extra hours
 * @returns
 */
function sumOfExtraHours(){
	
	var total = 0.0;
	$(".extraHours").each(function(i, obj){
		var num = $(obj).val();
		if(num == ""){
			$(obj).val("0.0")
		} else {
			total = total + parseFloat(num);
		}
	});
	$(".totalExtraHours").text(total.toFixed(2));
}

/**
 * sum of vacation Hours
 * @returns
 */
function sumOfVacationHours(){
	
	var total = 0.0;
	$(".vacationHours").each(function(i, obj){
		var num = $(obj).val();
		if(num == ""){
			$(obj).val("0.0")
		} else {
			total = total + parseFloat(num);
		}
	});
	$(".totalVacationHours").text(total.toFixed(2));
}

function changeMailTemplate(thisValue){
	var d =$(thisValue).children("option").filter(":selected").data('subjecttext');
	$('#email-form .subject').val(d);
	$("#email-form .summernote").summernote("code", $(thisValue).val());
	
	$("#description").val($('.summernote').summernote('code'));
}

function userShow(thisValue){
	$(".user .dropdown-menu").toggleClass('show');
	$(".user .mdi").toggle();
	
}

function userchoose(){
	
	$('.dropdown-item input[name="toEmail"]').on('change', function() {
		if (this.checked) {
			var text='<span class="badge badge-info ml-1 mr-1" value="USER_' + this.value.replace(".", "_") + '">'+this.value+'</span>'  
			$(".userShow").append(text);
		} else {
			var id = "[value='USER_"+this.value.replace(".", "_")+"'";
			$('.userShow .badge' + id).remove();
		} 
		
		console.log($('.dropdown-item input[name="toEmail"]:checked').length);
		$("#selectedUserSize").text($('.dropdown-item input[name="toEmail"]:checked').length);
	});
}

function clickOutToMail(){
	
	$('body').on('click', function(e) {
	    var container = $(".user");

	    // if the target of the click isn't the container nor a descendant of the container
	    if (!container.is(e.target) && container.has(e.target).length === 0) 
	    {
	    	$(".user .dropdown-menu").removeClass('show');
	    	$(".user .mdi-close").css("display", "none");
	    	$(".user .mdi-menu-down").css("display", "contents");
	    	$("#filterId").val('')
	    	$("#theList input[type=checkbox]").each(function() {
	    		$(this).closest('span').show();
	    	});
	    	$("#mailNameNotfound").hide();
	    }
	});
}

function filter(element,what) {
    var value = $(element).val();
    value = value.toLowerCase().replace(/\b[a-z]/g, function(letter) {
        return letter;
    });

    if (value == '') {
    	$("#theList input[type=checkbox]").each(function() {
    		$(this).closest('span').show();
    	});
    	$("#mailNameNotfound").hide();
    }else {
        
    	$("#theList input[type=checkbox]").each(function() {
    		if ($(this).val().search(value) > -1) {
    			$(this).closest('span').show();
    		}
    		else {
    			$(this).closest('span').hide();
    		}
    	});
    	
        $("#theList label sup").each(function() {
            if ($(this).text().search(value) > -1) {
                $(this).closest('span').show();
            }
            else {
            }
        });
       var s =  $("#theList input[type=checkbox][value*='" + value + "']").length;
       var a = $("#theList label sup:contains("+value+")").length;
       if(s==0 && a==0){
    	   $("#mailNameNotfound").show();
       }else {
    	   $("#mailNameNotfound").hide();
       }
    }
};

//#################################################################################################

function setChange(thisValue){
	var value = $(thisValue).prop("checked");
	$("#dayOff").val(value);
	if(value == true){
		$("#set-remark").css("display","block");
	}else {
		$("#set-remark").css("display","none");
	}
}
function datePopover(day, month, year,thisData){
	
	if(permission == true){
	    $.ajax({
			type : "GET",
			url : context + "supervisor/schedular/day-hours-form?day="+day+"&month="+month+"&year="+year,
			success: function(data){
				$(".popover").remove();
				 
				 $(thisData).popover({
				      html: true,
				      placement: 'top',
				      async: true,
				      sanitize: false,
				      content: data
			    });
				$(thisData).data("bs.popover").config.content=data;
				$(thisData).popover('show');
				hoursNum();
				saveDayDate();
			}
		});
	}
}

function hidePopover(){
	$(".popover").remove();
}

function saveDayDate(){
	$('#setDayDate').on('submit', function(e) {
		 e.preventDefault();
		 var formData = new FormData(this);
		 var s = this;
		 $(s).find(".set-msg").replaceWith("<span class='set-msg'></span>");
		 $.ajax({
			    type : "POST",
			    url : $(this).attr('action'),
			    data: formData,
			    cache: false,
		        contentType: false,
		        processData: false,
			    success: function(data) {
			    	
			    	$(s).find(".set-msg").replaceWith("<span class='text-success set-msg'>Save successfully !</span>");
			    	var date    = new Date(data.data.hoursDate),
			        yr      = date.getFullYear(),
			        m = date.getMonth()+1;
			        month   = date.getMonth()+1 < 10 ? '0' + m : m,
			        day     = date.getDate() < 10 ? '0' + date.getDate() : date.getDate(),
			        newDate = day+ '-' + month + '-' + yr;
			        
			        if(data.data.dayOff == true){
			        	$("#id_"+day+"_"+date.getMonth()).attr( "data-off", "true" );
			        } else{
			        	$("#id_"+day+"_"+date.getMonth()).attr( "data-off", "false" );
			        }
			    	
			        var oldOff = $("[data-date-row='"+newDate+"']").closest("tr").data("off");
			        
			        $("[data-date-row='"+newDate+"']").find(".badge-success").text(data.data.dailyHours);
			        $("[data-date-row='"+newDate+"']").find(".badge-info").text(data.data.extraHours);
			        $("[data-date-row='"+newDate+"']").find(".badge-warning").text(data.data.vacationHours);
			        
			        var totalD = 0.0;
			        $("[data-date-row='"+newDate+"']").closest(".table").find("tr").each(function( index ) {
			        	totalD = totalD + parseFloat($(this).find(".badge-success").text()) ;
			        });
			        
			        var offM = parseFloat( $("[data-date-row='"+newDate+"']").closest(".table").closest(".card").find("a").find(".badge-danger").text() );
			        if(oldOff != data.data.dayOff){
				        if(oldOff == true){
				        	$("[data-date-row='"+newDate+"']").closest(".table").closest(".card").find("a").find(".badge-danger").text(offM-1);
				        } else {
				        	$("[data-date-row='"+newDate+"']").closest(".table").closest(".card").find("a").find(".badge-danger").text(offM+1);
				        }
			        }
			        $("[data-date-row='"+newDate+"']").closest(".table").closest(".card").find("a").find(".badge-success").text(totalD);
			        $("[data-date-row='"+newDate+"']").closest("tr").data( "off", data.data.dayOff );

			        setTimeout(function(){ hidePopover(); }, 500);

			        
			    }, error: function(XMLHttpRequest, textStatus, errorThrown) {
			    	$(s).find(".set-msg").replaceWith("<span class='text-danger set-msg'>Please try again !</span>");
			    	console.log(XMLHttpRequest);
			    }
		 });
	});
}

function viewHours(){
	$("#table-1").toggleClass("col-sm-12");
	$("#table-1").toggleClass("col-sm-9");
	$("#table-2").toggleClass("hide");
}

function sendEmail(month, year){
	$.ajax({
		type : "GET",
		url : context + "supervisor/schedular-send-email?month="+month+"&year="+year,
		success: function(data){
			 console.log(data);
			 $("#schedularEmailId").replaceWith(data);
			 setEmailInit();
			 $("#Email-modal").modal("show");
			 
		}
	});
}
function setEmailInit(){
	$(".summernote").summernote({
		toolbar: [
		    // [groupName, [list of button]]
		    ['style', ['bold', 'italic', 'underline', 'clear']],
		    ['font', ['strikethrough', 'superscript', 'subscript']],
		    ['fontsize', ['fontsize']],
		    ['color', ['color']],
		    ['para', ['ul', 'ol', 'paragraph']],
		    ['table', ['table']],
		    ['insert', ['link']],
		    
		  ],
		minHeight:200,maxHeight:500});
	
	$(".summernote").on("summernote.change", function (e) {   // callback as jquery custom event 
		$("#description").val($('.summernote').summernote('code'));
	});
	//Approve
	$('#email-form .subject').val($('.approveDiv .subject').val());
	$("#email-form .summernote").summernote("code", $('.approveDiv .message').val());
	$("#description").val($('.summernote').summernote('code'));
	userchoose();
	clickOutToMail();
	
	$('#form').on('submit', function(e) {
		var valueTo =$('#form input[name="toEmail"]:checked').val();
		if(valueTo == undefined || valueTo == '' || valueTo == null){
			e.preventDefault();
			$(".toEmailError").show();
		}
		var valueSubject =$('#form input[name="subject"]').val();
		if(valueSubject == undefined || valueSubject == '' || valueSubject == null){
			e.preventDefault();
			$(".valueSubjectError").show();
		}
	});
}

