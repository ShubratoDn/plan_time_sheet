$(document).ready(function() {
	$("form").submit(function(e){
				
		e.preventDefault();
	    $.ajax({
		    type : "POST",
		    url : $(this).attr('action'),
		    data: new FormData(this),
		    cache: false,
	        contentType: false,
	        processData: false,
		    success: function (response) {
				var msg = '<div id="permission_error"><div class="alert alert-success alert-dismissible" role="alert">' + 
	        		   	'<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
	        			'<span aria-hidden="true">×</span>' + 
					    '</button>' + 
					    '<strong>Success</strong> permission plan changed successfully' + 
					    '</div></div>';
				$("#permission_error" ).replaceWith( msg );
		    },error: function(XMLHttpRequest, textStatus, errorThrown) {
   				var d = '<div id="permission_error"><div class="alert alert-danger alert-dismissible" role="alert">'+
                			'<button type="button" class="close" data-dismiss="alert" aria-label="Close">'+
                				'<span aria-hidden="true">×</span>'+
            				'</button>' + 
            				'<strong>Oh snap!</strong> Please try again ' +
        				'</div></div>';
				$("#permission_error").replaceWith(d);
		   	}
		});
	});
	
});

function setUserLimitAdd(){
	
	var value = parseInt($("#setUserlimit").val());

	if(value != undefined && value != null && value != ""){
		var v = parseInt($("#userLimitId").val());
		var s = v + value;
		$("#limitId").text(s);
		$("#userLimitId").val(s);
		$("#modalUser").modal("hide");
	}
}

function setUserLimitRemove(){

	var value = parseInt($("#setUserlimit").val());
	
	if(value != undefined && value != null && value != ""){
		var v = parseInt($("#userLimitId").val());
		var s = v - value;
		if(s >= currentUser){
			
			$("#limitId").text(s);
			$("#userLimitId").val(s);
			$("#modalUser").modal("hide");
		} else {
			$("#modalUser").modal("hide");
			alert("user can not reduce");
		}
	}
}

	