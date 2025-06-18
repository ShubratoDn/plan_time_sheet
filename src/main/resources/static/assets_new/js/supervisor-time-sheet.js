$(document).ready(function() {
	
	$('#datatable').DataTable({
		"scrollX": true,
		"bLengthChange": false,
		"processing" : true,
		"ordering" : false
	});
	$('#datatablePending').DataTable({
		"scrollX": true,
		"bLengthChange": false,
		"processing" : true,
		"ordering" : false
	});
	setFstDropdown();
	addMoreFileForm();


	
});

/**
 * get user basic details
 * 
 * @param id
 * @returns
 */
function bacisDetails(name, id) {
	$.ajax({
		type : "GET",
		url : context + "supervisor/basic-detail/"+id,
		success: function(data){
			$('#view_title_name').text(name);
			$('#user-details-modal').replaceWith(data);

			
		}
	});
}

/**
 * get user basic details
 * 
 * @param id
 * @returns
 */
function fileView(id, filePath) {

	var fileLenth;
	var combo = $("<select class='form-control' onchange='changeFile("+ id +",this.value)'></select>").attr("id", "selectFileView");
	if(filePath != "null"){
		
		$.ajax({
			type : "GET",
			url : context + "supervisor/timesheet-view-file-list?id="+id,
			success: function(data){
				
				var fileLength = 0;
			    $.each(data, function (i, file) {
			        combo.append("<option value="+ file.id +" data-index="+i+">" + file.fileOriginalName + "</option>");
			        fileLength = i+1;
			    });
			    
			    var nextButton ="<div class='col-2'></div>";
			    
			    if(fileLength > 1){
			    	nextButton = "<div class='col-2'><a class='btn btn-sm btn-primary' id='nextButton' href='javascript:void(0);' onclick='next("+id+")'>Next</a> </div>";
			    }
			    
			    var view = "<div id='view-body' class='col-12 col-sm-12 col-md-12 col-lg-6 col-xl-6'>" +
			    				"<div class='row mb-1'>"+
			    					"<div class='col-2'><a class='btn btn-sm btn-primary' id='previousButton' style='display:none;' href='javascript:void(0);' onclick='previous("+id+")'>Previous</a> </div>" +
			    					"<label class='col-2 mb-0 col-form-label'>Total file : "+ data.length +"</label>"+
			    					"<div id='selectorFile' class='col-5'> </div>" +
			    					"<div class='col-1'><a class='btn btn-sm btn-primary' href='javascript:void(0);' onclick='deleteHourLogFile("+id+")'><i class='fas fa-trash'></i></a></div>" +
			    					nextButton +
			    				"</div>"+
								" <div id='iframeDiv'> <iframe class='col-12 height-file-popup' src='https://docs.google.com/gview?" +
									"url="+ location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '') + context + "file/user-time-sheet/"+ id +"?companyUrl="+companyUrl+"&fileId=&amp;embedded=true'> </iframe>"+
									" </div>" + 
								"</div>";
					
				$('#view-body').replaceWith(view);
				$("#selectorFile").append(combo);
			}
	    });
		
	} else {
		var view = "<div id='view-body' class='col-12 col-sm-12 col-md-12 col-lg-6 col-xl-6'>"+
		    	   		"<div class='col-12 row'>"+
		    	   			"<div class='form-group row col-4' style='display:none;'>"+
		    	   				"<label class='col-8 mb-0 col-form-label'>Total file : </label>"+
		    	   				"<input class='form-control border-top-0 border-right-0 border-left-0 rounded-0 col-4' type='hidden' value=0 readonly>"+
		    	   			"</div>"+
		    	   			"<div id='selectorFile' class='col-8'> </div>"+
		    	   		"</div></br>"+
						" <div id='iframeDiv' class=''>"+
							"<div class='no-file col-12 height-file-popup d-flex justify-content-center align-items-center'>"+
								"<h4>" +
								"No file avilable" +
								"</h4>"+
							"</div>"+
						" </div>" + 
					"</div>";
			
		$('#view-body').replaceWith(view);
// $("#selectorFile").append(combo);
	}
	
	$.ajax({
		type : "GET",
		url : context + "supervisor/timesheet-view?id="+id,
		success: function(data){
			$('#view-table').replaceWith(data);
			if(filePath == "null"){
				$("#view-table").addClass("col-lg-6 col-xl-6");
			} else {
				$("#view-table").addClass("col-lg-6 col-xl-6");
			}
			 hoursNum();
			 sumOfDailyHours();
			 sumOfExtraHours();
			 sumOfVacationHours();
			 
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
			 
// $('.height-file-popup').css({ height: $(window).innerHeight() });
			 $(".summernote").on("summernote.change", function (e) {   // callback
																		// as
																		// jquery
																		// custom
																		// event
					$("#description").val($('.summernote').summernote('code'));
			 });
			 
			// Approve
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
 * 
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
 * 
 * @returns
 */
function hoursNum() {
	
	$('.hourNum').keypress(function (event) { 
		
		if(/* For dot */event.which != 46 && event.which != 8 && event.which != 0 && (event.which < 48 || event.which > 57))
			return false;
		return true;
	});
}

/**
 * copy element to next line
 * 
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
 * 
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
 * 
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
 * 
 * @param emailType
 * @returns
 */
function onClickSubject(emailType) {
	
	if(emailType == 'approve') {
		
		$(".rejectTemplate").addClass("hide");
		$(".approvalTemplate").removeClass("hide");
		
		// Approve
		$('#email-form .subject').val($('.approveDiv .subject').val());
		$("#email-form .summernote").summernote("code", $('.approveDiv .message').val());
		
		$("#description").val($('.summernote').summernote('code'));
		
		$("#rejectReason").addClass("hide");
		$("#isApprove").val("true");
		$("#mailTemplate").val($('.approveDiv .message').val());
		
	} else {
		$(".approvalTemplate").addClass("hide");
		$(".rejectTemplate").removeClass("hide");
		// Reject
		$('#email-form .subject').val($('.rejectDiv .subject').val());
		$("#email-form .summernote").summernote("code", $('.rejectDiv .message').val());
		
		$("#description").val($('.summernote').summernote('code'));
		$("#isApprove").val("false");
		$("#rejectReason").removeClass("hide");
		$("#mailTemplate").val($('.rejectDiv .message').val());
	}
}

/**
 * sum of column daily hours
 * 
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
 * 
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
 * 
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

function changeFile(id,thisValue){
	var viewFram = " <div id='iframeDiv'> <iframe class='col-12 height-file-popup' src='https://docs.google.com/gview?" +
	"url="+ location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '') + context + "file/user-time-sheet/"+ id + "?companyUrl="+companyUrl+"&fileId="+ thisValue + "&amp;embedded=true'> </iframe>"+
	" </div>";
	
	$('#iframeDiv').replaceWith(viewFram);
	
	console.log("currentIndex1");
	
}

function changeMailTemplate(thisValue){
	var d =$(thisValue).children("option").filter(":selected").data('subjecttext');
	$('#email-form .subject').val(d);
	$("#email-form .summernote").summernote("code", $(thisValue).val());
	
	$("#description").val($('.summernote').summernote('code'));
}

function next(id){
	var totalLength = $("#selectFileView").find("option").length;
	var currentIndex = $("#selectFileView").find("option:selected").data('index');
	if(currentIndex != totalLength-1){
		var nextN = currentIndex + 1;
		var value = $("#selectFileView").find("option[data-index="+nextN +"]").val();
		$("#selectFileView").val(value);
		changeFile(id,value);
		
		if(nextN == 0 ){
			$("#previousButton").css({ display: 'none'});
		} else {
			$("#previousButton").css({ display: 'block'});
		}
		if(nextN == totalLength-1 ){
			$("#nextButton").css({ display: 'none'});
		} else {
			$("#nextButton").css({ display: 'block'});
		}
	}
}
function previous(id){
	var totalLength = $("#selectFileView").find("option").length;
	var currentIndex = $("#selectFileView").find("option:selected").data('index');
	if(currentIndex != 0){
		var nextN = currentIndex - 1;
		var value = $("#selectFileView").find("option[data-index="+nextN +"]").val();
		$("#selectFileView").val(value);
		changeFile(id,value);
		
		if(nextN == 0 ){
			$("#previousButton").css({ display: 'none'});
		} else {
			$("#previousButton").css({ display: 'block'});
		}
		if(nextN == totalLength-1 ){
			$("#nextButton").css({ display: 'none'});
		} else {
			$("#nextButton").css({ display: 'block'});
		}
	}
}

function addMoreFile(hourLogId){
	$("#more-file-add-model").find("input[name=hourLogFileId]").val(hourLogId);
	$('#add-more-file-user').text($("#user-name").val());
	$('#add-more-file-client').text($("#user-client").val());
	$("#more-file-add-model").modal("show");
}

function addMoreFileForm(){
	$("#file-more-added").on("change",function(e){
		
		var formData = new FormData();
		var s = $(this)[0].files.length;
		for(i=0; i < s;i++){
			formData.append("file", $(this)[0].files[i]);
		}
		formData.append("hourLogFileId", $("input[name=hourLogFileId]").val());
		$("#more-file-add-model").modal("hide");
		$("#more-file-submit").css({ display: 'flex'});
	    $.ajax({
		    type : "POST",
		    url : $("#time-sheet-more-file").attr('action'),
		    data: formData,
		    cache: false,
	        contentType: false,
	        processData: false,
		    success: function(data) {
		    	$("#more-file-add-model").modal("hide");
		    	$("#more-file-submit").css({ display: 'none'});
		    	$("#file-more-added").val("")
		    	$("input[name=hourLogFileId]").val("")
		    	fileView(data.data.id, data.data.filePath);
   			},error: function(XMLHttpRequest, textStatus, errorThrown) {
   				console.log("Error");
   				$("#more-file-submit").css({ display: 'none'});
		   	}
		});
	});
}
//
// function deleteHourLogFile(hourLogId){
// var value = $("#selectFileView").val();
// var valueLength = $("#selectFileView").find("option").length;
//	 $.ajax({
//		 type : "GET",
//		 url : context + "supervisor/hours-log-file/delete?id="+value+"&hourLogId="+hourLogId,
//		 success: function(data){
//			 if(valueLength > 1){
//				 fileView(hourLogId, 'Multiple file');
//			 } else {
//				 fileView(hourLogId, 'null');
//			 }
//		 }
//	 });
// }

function deleteHourLogFile(hourLogId) {
	var value = $("#selectFileView").val();
	var valueLength = $("#selectFileView").find("option").length;
	Swal.fire({
		title: 'Are you sure?',
		text: 'You want to delete this file !',
		type: 'warning',
		showCancelButton: true,
		confirmButtonText: 'Yes, delete it!',
		cancelButtonText: 'No'
	}).then((result) => {
		if(result.value){
			$.ajax({
				type : "GET",
				url : context + "supervisor/hours-log-file/delete?id="+value+"&hourLogId="+hourLogId,
				success: function(data){
					if(valueLength > 1){
						fileView(hourLogId, 'Multiple file');
					} else {
						fileView(hourLogId, 'null');
					}
				}
			});
		}
	});
}








