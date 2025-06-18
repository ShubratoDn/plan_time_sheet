$(document).ready(function(){
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
	
	$("#submitSignature").submit(function( e ) {
		console.log("submitSignaturesubmitSignature");
		e.preventDefault();
	    $.ajax({
		    type : "POST",
		    url : $(this).attr('action'),
		    data: new FormData(this),
		    cache: false,
	        contentType: false,
	        processData: false,
		    success: function(data) {
		    	location.reload();
		    },
		    error: function(xhr, status, error) {
		    	var msg = '<div id="emailAlert"><div class="alert alert-danger alert-dismissible" role="alert">' + 
			   	'<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
				'<span aria-hidden="true">×</span>' + 
			  '</button>' + 
			  '<strong>Error</strong> Please try again' + 
			  '</div></div>';
		    	$("#emailAlert" ).replaceWith( msg );
	    	}
		});
	});
});

function viewSignature(textHtml){
	var text = "<div id='signatureViewText'>"+textHtml+" <div>"
	$("#signatureViewText").replaceWith(text);
	$("#signatureView").modal('show');
}

function deleteSignature(id){
	if (confirm('Are you sure ?')) {
		$.ajax({
		    type : "GET",
		    url : context + "admin/signature/delete/"+ id,
		    success: function(data) {
		    	location.reload();
		    },error: function(XMLHttpRequest, textStatus, errorThrown) {
		    }
		});
    }else{
      console.log('cancel')
    }
}


