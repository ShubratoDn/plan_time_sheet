$(document).ready(function() {
	
	$('#datatable').DataTable({
		"scrollX": true,
		"bLengthChange": false,
		"processing" : true,
		"ordering" : false
	});
	setFstDropdown();
});

/**
 * get user basic details
 * @param id
 * @returns
 */
function bacisDetails(id) {
	$.ajax({
		type : "GET",
		url : context + "supervisor/basic-detail/"+id,
		success: function(data){
			$('#user-details-modal').replaceWith(data);
		}
	});
}

/**
 * get user basic details
 * @param id
 * @returns
 */
function fileView(id, filePath) {

	if(filePath != "null"){
		var view = "<div id='view-body'><iframe class='col-md-6 height-file-popup' src='https://docs.google.com/gview?" +
		"url="+ location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '') + context + "file/user-time-sheet?id="+ id +"&amp;embedded=true'> </iframe></div>";
			
		$('#view-body').replaceWith(view);
	}
	$.ajax({
		type : "GET",
		url : context + "supervisor/timesheet-view?id="+id,
		success: function(data){
			$('#view-table').replaceWith(data);
			$('#hours-table').DataTable({
				"bLengthChange": false,
				"iDisplayLength":33,
				"processing" : true,
				"ordering" : false,
				"filter": false,
				"scrollX": true,
				"paging": false
			});
			if(filePath == "null"){
				$("#view-table").removeClass("col-md-6");
			}
			 hoursNum();
			 sumOfDailyHours();
			 sumOfExtraHours();
			 sumOfVacationHours();
//			 $('.height-file-popup').css({ height: $(window).innerHeight() });
			 $(".summernote").on("summernote.change", function (e) {   // callback as jquery custom event 
					$("#description").val($('.summernote').summernote('code'));
			 });
			 
			//Approve
			$('#email-form .subject').val($('.approveDiv .subject').val());
			$("#email-form .summernote").summernote("code", $('.approveDiv .message').val());
			
			$("#description").val($('.summernote').summernote('code'));
			
			$("#rejectReason").addClass("hide");
			$("#isApprove").val("true");
		}
  });
	
}

/**
 * set reject reason form value
 * @param dateKey
 * @param userDetailId
 * @returns
 */
function rejectTimeSheet(dateKey, userDetailId) {
	
	$('#reasonId').val(dateKey);
	$('#userDetailId').val(userDetailId);
	
}

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
 * set subject
 * @param emailType
 * @returns
 */
function onClickSubject(emailType) {
	
	if(emailType == 'approve') {
		
		//Approve
		$('#email-form .subject').val($('.approveDiv .subject').val());
		$("#email-form .summernote").summernote("code", $('.approveDiv .message').val());
		
		$("#description").val($('.summernote').summernote('code'));
		
		$("#rejectReason").addClass("hide");
		$("#isApprove").val("true");
		
	} else {
		
		//Reject
		$('#email-form .subject').val($('.rejectDiv .subject').val());
		$("#email-form .summernote").summernote("code", $('.rejectDiv .message').val());
		
		$("#description").val($('.summernote').summernote('code'));
		$("#isApprove").val("false");
		$("#rejectReason").removeClass("hide");
	}
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

function closeViewPopUp(){
	
	$('#view-body').replaceWith('<div class="hide" id="view-body"></div>');
	$('#view-table').replaceWith('<div id="view-table"></div>');
}
