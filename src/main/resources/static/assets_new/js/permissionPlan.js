$(document).ready(function(){

	if(accessReq == true){
		accessMailSend();
		$("#access-mail-send").modal("show");
	}
	
	$("#access-mail-add-form").submit(function( e ) {
		e.preventDefault();
		$("#access-mail-add-form-is-submit").css("display","flex");
		var formData = new FormData(this);
		$.ajax({ 
			type : "POST",
			url : $(this).attr('action'),
			data: formData,
			cache: false,
			contentType: false,
			processData: false,
			success: function(data) {
				$("#access-mail-add-form-is-submit").css("display","none");
				$("#access-mail-send").modal("hide");
				var msg = '<div id="emailAlertInAccessMail"><div class="alert alert-success alert-dismissible" role="alert">' + 
	        		   	'<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
	        			'<span aria-hidden="true">×</span>' + 
					    '</button>' + 
					    '<strong>Success</strong> Email send successfully' + 
					    '</div></div>';
				$("#emailAlertInAccessMail" ).replaceWith( msg );

			},
			error: function(xhr, status, error) {
				$("#access-mail-add-form-is-submit").css("display","none");
				$("#access-mail-send").modal("hide");
				var msg = '<div id="emailAlertInAccessMail"><div class="alert alert-danger alert-dismissible" role="alert">' + 
			   	'<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
				'<span aria-hidden="true">×</span>' + 
			    '</button>' + 
			    '<strong>Error</strong> Please try again' + 
			    '</div></div>';
		    	$("#emailAlertInAccessMail" ).replaceWith( msg );
		    	
			}
		});
			
	});
	
//	<--------------------------------------------------------------------->
	
	$("#access-mail-cancel-form").submit(function( e ) {
		e.preventDefault();
		$("#access-mail-cancel-form-is-submit").css("display","flex");
		var formData = new FormData(this);
		$.ajax({ 
			type : "POST",
			url : $(this).attr('action'),
			data: formData,
			cache: false,
			contentType: false,
			processData: false,
			success: function(data) {
				$("#access-mail-cancel-form-is-submit").css("display","none");
				$("#access-mail-cancel").modal("hide");
				var msg = '<div id="emailAlertInAccessMail"><div class="alert alert-success alert-dismissible" role="alert">' + 
	        		   	'<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
	        			'<span aria-hidden="true">×</span>' + 
					    '</button>' + 
					    '<strong>Success</strong> Email send successfully' + 
					    '</div></div>';
				$("#emailAlertInAccessMail" ).replaceWith( msg );

			},
			error: function(xhr, status, error) {
				$("#access-mail-cancel-form-is-submit").css("display","none");
				$("#access-mail-cancel").modal("hide");
				var msg = '<div id="emailAlertInAccessMail"><div class="alert alert-danger alert-dismissible" role="alert">' + 
			   	'<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
				'<span aria-hidden="true">×</span>' + 
			    '</button>' + 
			    '<strong>Error</strong> Please try again' + 
			    '</div></div>';
		    	$("#emailAlertInAccessMail" ).replaceWith( msg );
		    	
			}
		});
			
	});
});

function accessMailSend(){
	$("#access-mail-send").modal("show");
	
	$("#access-mail-send .summernote").summernote({
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
		
		$("#access-mail-send .summernote").on("summernote.change", function (e) {   // callback as jquery custom event 
			$("#access-mail-send #description").val($('#access-mail-send .summernote').summernote('code'));
		});
}

function accessMailCancel(){
	$("#access-mail-send").modal("hide");
	$("#access-mail-cancel").modal("show");
	
	$("#access-mail-cancel .summernote").summernote({
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
		
		$("#access-mail-cancel .summernote").on("summernote.change", function (e) {   // callback as jquery custom event 
			$("#access-mail-cancel #description").val($('#access-mail-cancel .summernote').summernote('code'));
		});
}