function sendMailSelectedType(type,userDetailsId, startDate, endDate){
	$.ajax({
		type : "GET",
		url : context + "admin/mail/send-mail/reminder?urlParams=" + type + '&userDetailsId=' + userDetailsId + '&startDate=' + startDate + '&endDate=' + endDate,
		success: function(data){
			
			$("#pending-mail-send-div").replaceWith(data);
			
			$("#pending-mail-send-div .summernote").summernote({
				toolbar: [
				    ['style', ['bold', 'italic', 'underline', 'clear']],
				    ['font', ['strikethrough', 'superscript', 'subscript']],
				    ['fontsize', ['fontsize']],
				    ['color', ['color']],
				    ['para', ['ul', 'ol', 'paragraph']],
				    ['table', ['table']],
				    ['insert', ['link']],
				    
				  ],
				minHeight:200,maxHeight:500});
			
			$("#pending-mail-send-div #description").val($("#pending-mail-send-div select").find("option:selected").val());
			$("#pending-mail-send-div .summernote").summernote("code", $("#pending-mail-send-div select").find("option:selected").val());
			$("#pending-mail-send-div input[name=subject]").val($("#pending-mail-send-div select").find("option:selected").data('subjecttext'));
			
			
			$("#pending-mail-send-div select").on("change", function (e) { 
				$("#pending-mail-send-div #description").val($("#pending-mail-send-div select").find("option:selected").val());
				$("#pending-mail-send-div .summernote").summernote("code", $("#pending-mail-send-div select").find("option:selected").val());
				$("#pending-mail-send-div input[name=subject]").val($("#pending-mail-send-div select").find("option:selected").data('subjecttext'));
			});
			$("#pending-mail-send-div .summernote").on("summernote.change", function (e) {   // callback as jquery custom event 
				$("#pending-mail-send-div #description").val($('#pending-mail-send-div .summernote').summernote('code'));
			});
			
			$("#pending-mail-send-div").modal('show');
			
			
			$("#pending-mail-send-form").submit(function( e ) {
	   			e.preventDefault();
	   			
	   			var valueTo =$('#pending-mail-send-form input[name="toEmail"]').val();
				if(valueTo == undefined || valueTo == '' || valueTo == null){
					$(".typeEmailError").show();
					return ;
				}
				var valueSubject =$('#pending-mail-send-form input[name="subject"]').val();
				if(valueSubject == undefined || valueSubject == '' || valueSubject == null){
					$(".typeSubjectError").show();
					return ;
				}
				var valueDescription =$('#pending-mail-send-form input[name="description"]').val();
				if(valueDescription == undefined || valueDescription == '' || valueDescription == null){
					$(".typeDescriptionError").show();
					return ;
				}
	   			
	   			var formData = new FormData(this);
	   			$.ajax({
	   				type : "POST",
	   				url : $(this).attr('action'),
	   				data: formData,
	   				cache: false,
	   				contentType: false,
	   				processData: false,
	   				success: function(data) {
	   					
	   					$("#pending-mail-send-div").modal('hide');
			
	   				},
	   				error: function(xhr, status, error) {
	   					
	   					var msg = '<div id="emailAlertInTypeMail"><div class="alert alert-danger alert-dismissible" role="alert">' + 
	   				   	'<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
	   					'<span aria-hidden="true">×</span>' + 
	   				    '</button>' + 
	   				    '<strong>Error</strong> Please try again' + 
	   				    '</div></div>';
	   			    	$("#emailAlertInTypeMail" ).replaceWith( msg );
	   			    	
	   				}
	   			});
	   			
	   		});
			
		}
	});
}

