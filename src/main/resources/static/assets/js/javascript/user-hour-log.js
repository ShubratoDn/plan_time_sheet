$(document).ready(function() {
	$('#defaultDatatable').DataTable({
		"asStripeClasses": [],
		"iDisplayLength":33,
		"bLengthChange": false,
		"ordering" : false,
		"paging":   false,
		"bFilter": false,
		"fixedHeader": {
	        "header": true,
	        "headerOffset":63
	    },
		"columnDefs": [
		     { "width": "150px", "targets": 1 }
		   ]
	} );
	
	$('#datatable').DataTable({
		"bLengthChange": false,
		"bFilter": false,
		"processing" : true,
		"ordering" : false,
	});
	
	$('#monthTable').DataTable({
		"bLengthChange": false,
		"bFilter": false,
		"iDisplayLength":14,
		"ordering" : false,
		"paging": false,
		"scrollX": true,
		"scrollY": false,
	});
	
	if (typeof totalPages !== 'undefined')
		pagination();
	
	if(userDId != undefined)
		$(".year-box").addClass("fstdropdown-select");
	
	hoursNum();
	sumOfDailyHours();
	sumOfExtraHours();
	sumOfVacationHours();
	setFstDropdown();
	onSelectSubject();
	
	$(":input[data-inputmask-mask]").inputmask();
	$(":input[data-inputmask-alias]").inputmask();
	$(":input[data-inputmask-regex]").inputmask("Regex");
	fileSelect();
	
});

function fileSelect(){
	
	$("#hourLogFile").change(function(e){
		//file extention check
			var validExtensions = ['pdf','xls','xlsx','png','jpg','jpeg','docx']; //array of valid extensions
			var fileName = $("#hourLogFile").val();
			var fileNameExt = fileName.substr(fileName.lastIndexOf('.') + 1);
	            
			var fileList = e.target.files;
            var length = e.target.files.length;
            
            var extList = [];
	        for(i=0; i<length; i++ ){
	        	var fileName = e.target.files[i].name;
	        	var d = fileName.substr(fileName.lastIndexOf('.') + 1);
	        	extList.push(d.toLowerCase());
	        	
	        }
	        
			if (!extList.every( ai => validExtensions.includes(ai))){
				
				var errorAlert = "<div class='alert alert-danger alert-dismissible' role='alert'>"+
						            "<button type='button' class='close' data-dismiss='alert' aria-label='Close'>"+
						                "<span aria-hidden='true'>×</span>"+
						            "</button>"+
						           "<strong>Oh snap!</strong> <th:block> Please upload file with extension .pdf, .png, .jpg, .xls or .xlsx </th:block>" +
						        "</div>";
				$("#errorDiv").append(errorAlert);
				$("#hourLogFile").val('');
				$("#selectFile").replaceWith("<div id='selectFile'></div>");
				return;
			} else {
				
				var li = "";
				for(i=0; i<length; i++ ){
		        	var fileName = e.target.files[i].name;
		        	var d = fileName.substr(fileName.lastIndexOf('.') + 1);
		        	
		        	var file = e.target.files[i];
			        var fileList = [];
			        fileList.push(file);			        
			        
			        pdfView = "";
			        var validExtensionsImg = ['pdf','png','jpg','jpeg'];
			        if(validExtensionsImg.includes(d.toLowerCase())){
			        	pdfView = "<i class='fas fa-eye' onclick='PreviewImage(" + i + ")' data-toggle='tooltip' style='color: orange; font-size: 20px;' title='' data-original-title='view file'></i>"
			        }
			        
			        li = li + "<tr><td class='white_space_break'>"
			        + fileName + "</td><td>" +
			        pdfView + " <i class='fas fa-trash' onclick='removeFile(" + i + ")' data-toggle='tooltip' style='color: orange; font-size: 20px;' title='' data-original-title='delete'></i></td></tr>"
		        }
				if(length ==0){
					 li = "<tr><td class='text-center' colspan='2'>No file available</td></tr>"
				}
				
				var fileNameList = "<div id='selectFile' class='col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12'><table class='table col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12'>"
			        +"<thead class='back-g'><tr><th class='col-8 col-sm-8 col-md-8 col-lg-8 col-xl-8'>File Name</th><th class='col-2 col-sm-2 col-md-2 col-lg-2 col-xl-2'>Remove</th></tr></thead><tbody>" +
						            li 
						            +"</tbody></table> </div>";
					
				$("#selectFile").replaceWith(fileNameList);
				PreviewImage(0);
			}        
	        
		});//
		
		$(".summernote").on("summernote.change", function (e) {   // callback as jquery custom event 
			$("#description").val($('.summernote').summernote('code'));
		});
}

function PreviewImage(index) {
	var length = $("#hourLogFile")[0].files.length;
	if(length > 0){
		var d = $("#hourLogFile")[0].files[index];
		var s = d.name.substr(d.name.lastIndexOf('.') + 1);
		if(s.toLowerCase() == "pdf"){
			pdffile=document.getElementById("hourLogFile").files[index];
	    	pdffile_url=URL.createObjectURL(pdffile);
	    
	    	$('#viewer').attr('src',pdffile_url);
	    	$("#filePdfView").removeClass("hide");
	    	$("#fileImgView").addClass("hide");
	    }
		var validExtensionsImg = ['png','jpg','jpeg'];
		if(validExtensionsImg.includes(s.toLowerCase())){ 
			pdffile=document.getElementById("hourLogFile").files[index];
	    	pdffile_url=URL.createObjectURL(pdffile);
			$('#fileImgView').find("img").attr('src',pdffile_url);
			$("#fileImgView").removeClass("hide");
	    	$("#filePdfView").addClass("hide");
		}
		var validExtensionsNotView = ['xls','xlsx','docx'];
		if(validExtensionsNotView.includes(s.toLowerCase())){
			$("#filePdfView").addClass("hide");
			$('#fileImgView').find("img").attr('src',"/assets_new/images/filenotview.svg");
			$("#fileImgView").removeClass("hide");
		}
	} else {
		$("#filePdfView").addClass("hide");
		$('#fileImgView').find("img").attr('src',"/assets_new/images/filenotview.svg");
		$("#fileImgView").removeClass("hide");
	}
	
	var trV = $("#selectFile").find("tbody").find("tr");
	
	trV.each(function( indexV ) {
		if(index == indexV){
			$(this).addClass("currentFileView");
		} else {
			$(this).removeClass("currentFileView");
		}
	});

}

function removeFile(name){
	var fileName = "You want to remove "+ $("#hourLogFile")[0].files[name].name +" file!”";
    Swal.fire({
    	title: "Are you sure?", 
    	text: fileName,
    	type: "warning", 
    	showCancelButton: !0, 
    	confirmButtonColor: "#34c38f",
    	cancelButtonColor: "#f46a6a",
    	confirmButtonText: "Yes, remove it!" }).then(
        function (t) {
        	if(t.value){
        		var listsw = $("#hourLogFile")[0].files;
        		var list = $("#hourLogFile")[0].files;
        		var length = list.length;
        		 var fileList = [];
        		for(i=0; i<length; i++ ){
        	    	if(i != name) {
        		        fileList.push($("#hourLogFile")[0].files[i]);
        	    	}
        	    }
        		$("#hourLogFile")[0].length = length-1;
        		$("#hourLogFile")[0].files = new FileListItems(fileList);
        		
        		//////////////////////////////////////
        		var li = "";
        		var files = $("#hourLogFile")[0];
        		length = files.length
				for(i=0; i<length; i++ ){
		        	var fileName = files.files[i].name;
		        	var d = fileName.substr(fileName.lastIndexOf('.') + 1);
		        	
		        	var file = files.files[i];
			        var fileList = [];
			        fileList.push(file);			        
			        
			        pdfView = "";
			        var validExtensionsImg = ['pdf','png','jpg','jpeg'];
			        if(validExtensionsImg.includes(d.toLowerCase())){
			        	pdfView = "<i class='fas fa-eye' onclick='PreviewImage(" + i + ")' data-toggle='tooltip' style='color: orange; font-size: 20px;' title='' data-original-title='view file'></i>"
			        }
			        
			        li = li + "<tr><td class='white_space_break'>"
			        + fileName + "</td><td>" +
			        pdfView + " <i class='fas fa-trash' onclick='removeFile(" + i + ")' data-toggle='tooltip' style='color: orange; font-size: 20px;' title='' data-original-title='delete'></i></td></tr>"
		        }
				if(length ==0){
					 li = "<tr><td class='text-center' colspan='2'>No file available</td></tr>"
				}
				
				var fileNameList = "<div id='selectFile' class='col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12'><table class='table col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12'>"
			        +"<thead class='back-g'><tr><th class='col-8 col-sm-8 col-md-8 col-lg-8 col-xl-8'>File Name</th><th class='col-2 col-sm-2 col-md-2 col-lg-2 col-xl-2'>Remove</th></tr></thead><tbody>" +
						            li 
						            +"</tbody></table> </div>";
					
				$("#selectFile").replaceWith(fileNameList);
				PreviewImage(0);
        		//////////////////////////////////////
        		
        		Swal.fire("Removed!", "This file has been remove.", "success");
           
        	}
        }
    );
}

function FileListItems (files) {
  var b = new ClipboardEvent("").clipboardData || new DataTransfer()
  for (var i = 0, len = files.length; i<len; i++) b.items.add(files[i])
  return b.files
}
/**
 * on start date change
 * @param startDate
 * @returns
 */
function changeStartDate(startDate){
	if(!isEdit || isEdit == undefined){
		if(moment(startDate, 'MM/DD/YYYY',true).isValid()) {
			var url1 = context + "user/add-time-sheet/changeDate";
			var finalUrl = getFinalUrl(url1,'startDate', startDate);
			//    	location.href = finalUrl;
			$.ajax({
				type : "GET",
				url : finalUrl,
				success: function(data){
					$("#addTimeSheet").replaceWith(data);
					$('#defaultDatatable').DataTable({
						"asStripeClasses": [],
						"iDisplayLength":33,
						"bLengthChange": false,
						"ordering" : false,
						"paging":   false,
						"bFilter": false,
						"columnDefs": [
						     { "width": "150px", "targets": 1 }
						   ]
					} );
					hoursNum();
					sumOfDailyHours();
					sumOfExtraHours();
					sumOfVacationHours();
					setFstDropdown();
					onSelectSubject();
					fileSelect();
					$(":input[data-inputmask-mask]").inputmask();
					$(":input[data-inputmask-alias]").inputmask();
					$(":input[data-inputmask-regex]").inputmask("Regex");
				}
			});
			
			
		} else {
			$("#startDateError").removeClass("hide");
		}
	}
}

/**
 * on start date change
 * @param startDate
 * @returns
 */
function changeStartDateSuperviser(startDate){
	if(!isEdit || isEdit == undefined){
		if(moment(startDate, 'MM/DD/YYYY',true).isValid()) {
			var url1 = context + "supervisor/add-time-sheet/changeDate";
			var finalUrl1 = getFinalUrl(url1,'startDate', startDate);
			var finalUrl = getFinalUrl(finalUrl1,'user', userSelect.id);
	//    	location.href = finalUrl;
	    	$.ajax({
	    		type : "GET",
	    		url : finalUrl,
	    		success: function(data){
	    			$("#addTimeSheet").replaceWith(data);
	    			$('#defaultDatatable').DataTable({
	    				"asStripeClasses": [],
	    				"iDisplayLength":33,
	    				"bLengthChange": false,
	    				"ordering" : false,
	    				"paging":   false,
	    				"bFilter": false,
	    				"columnDefs": [
	    				     { "width": "150px", "targets": 1 }
	    				   ]
	    			} );
	    			hoursNum();
	    			sumOfDailyHours();
	    			sumOfExtraHours();
	    			sumOfVacationHours();
	    			setFstDropdown();
	    			onSelectSubject();
	    			fileSelect();
	    			$(":input[data-inputmask-mask]").inputmask();
	    			$(":input[data-inputmask-alias]").inputmask();
	    			$(":input[data-inputmask-regex]").inputmask("Regex");
	    		}
	    	});
	    	
	    	
		} else {
			$("#startDateError").removeClass("hide");
		}
	}
}

/**
 * on end datre change
 * @param endDate
 * @returns
 */
function changeEndDate(endDate){
	if(moment(endDate, 'MM/DD/YYYY',true).isValid()) {
		
		var finalUrl = getFinalUrl(location.href,'startDate', startDate);
		finalUrl = getFinalUrl(finalUrl,'endDate', endDate);
    	location.href = finalUrl;
	} else {
		$("#endDateError").removeClass("hide");
	}
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
function onSelectSubject() {
	
	$("#email-form .summernote").summernote({
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
	  
	if(!resubmit) {
		
		//Approve
		$('#email-form .subject').val($('.approveDiv .subject').val());
		$("#email-form .summernote").summernote("code", $('.approveDiv .message').val());
		
		$("#description").val($('.summernote').summernote('code'));
		
	} else {
		
		//Reject
		$('#email-form .subject').val($('.rejectDiv .subject').val());
		$("#email-form .summernote").summernote("code", $('.rejectDiv .message').val());
		
		$("#description").val($('.summernote').summernote('code'));
	}
}

/**
 * select client
 * @param value
 * @param timeSheet
 * @param report
 * @returns
 */
function selectClient(value, timeSheet, report){
	location.href = context + 'user/user-hour-log/'+value + '?timeSheet=' + timeSheet + '&report=' + report;
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

/**
 * delete file
 * @param id
 * @returns
 */
function deleteFile(id) {
	Swal.fire({
		title: 'Are you sure?',
		text: 'You want to delete this file !',
		type: 'warning',
		showCancelButton: true,
		confirmButtonText: 'Yes, delete it!',
		cancelButtonText: 'No'
	}).then((result) => {
		if(result.value) {
			location.href = context + "user/hour-log-file/delete/" + id + "/" + userDetailId;
		}
	})
	
}

/**
 * year select
 * @param year
 * @param userDetailsId
 * @returns
 */
function selectYear(year){
	location.href = context + "user/add-time-sheet?year=" + year;
}

/**
 * get schedul time
 * @param year
 * @param userDetailsId
 * @returns
 */
function setSchedulTimesheet(){
	
	var url1 = context + "user/schedule-time-sheet";
	var finalUrl = getFinalUrl(url1,'startDate', startDate);
	finalUrl = getFinalUrl(finalUrl,'endDate', endDate);
	finalUrl = getFinalUrl(finalUrl,'scheduleTime', true);
	$.ajax({
		type : "GET",
		url : finalUrl,
		success: function(data){
			var i;
			for (i = 0; i < data.length; i++) { 
				var value = data[i];
				
				$("#defaultDatatable").find("tr").eq(i + 2).find(".dailyHours").val(String(value.dailyHours));
				$("#defaultDatatable").find("tr").eq(i + 2).find(".extraHours").val(String(value.extraHours));
				$("#defaultDatatable").find("tr").eq(i + 2).find(".vacationHours").val(String(value.vacationHours));
			}
			
			sumOfDailyHours();
			sumOfExtraHours();
			sumOfVacationHours();
		}
	});
}

function setDailyTimesheet(){
		
	var url1 = context + "user/default-time-sheet";
	var finalUrl = getFinalUrl(url1,'startDate', startDate);
	finalUrl = getFinalUrl(finalUrl,'endDate', endDate);
	finalUrl = getFinalUrl(finalUrl,'scheduleTime', true);
	
	$.ajax({
		type : "GET",
		url : finalUrl,
		success: function(data){
			var i;
			for (i = 0; i < data.length; i++) { 
				var value = data[i];
				
				$("#defaultDatatable").find("tr").eq(i + 2).find(".dailyHours").val(String(value.dailyHours));
				$("#defaultDatatable").find("tr").eq(i + 2).find(".extraHours").val(String(value.extraHours));
				$("#defaultDatatable").find("tr").eq(i + 2).find(".vacationHours").val(String(value.vacationHours));
			}
			
			sumOfDailyHours();
			sumOfExtraHours();
			sumOfVacationHours();
		}
	});
		
}

function changeMailTemplate(thisValue){
	var d =$(thisValue).children("option").filter(":selected").data('subjecttext');
	$('#email-form .subject').val(d);
	$("#email-form .summernote").summernote("code", $(thisValue).val());
	
	$("#description").val($('.summernote').summernote('code'));
	
	console.log(d);
}
