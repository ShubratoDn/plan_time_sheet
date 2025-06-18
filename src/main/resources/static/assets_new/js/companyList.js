function resendEmailVarify(id){
	$.ajax({
	    type : "GET",
	    url : context + "super-admin/resend-varify-email-link/"+ id,
	    success: function(data) {
	    	//Check error/success
	    		var msg = '<div id="emailAlert"><div class="alert alert-success alert-dismissible" role="alert">' + 
                		   	'<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
                			'<span aria-hidden="true">×</span>' + 
	    				  '</button>' + 
	    				  '<strong>Success</strong> Email send successfully' + 
	    				  '</div></div>';
	    		$("#emailAlert" ).replaceWith( msg );

	    },error: function(XMLHttpRequest, textStatus, errorThrown) {
	    	var msg = '<div id="emailAlert"><div class="alert alert-danger alert-dismissible" role="alert">' + 
		   	'<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
			'<span aria-hidden="true">×</span>' + 
		  '</button>' + 
		  '<strong>Error</strong> Please try again' + 
		  '</div></div>';
	    	$("#emailAlert" ).replaceWith( msg );
	    }
	});
}