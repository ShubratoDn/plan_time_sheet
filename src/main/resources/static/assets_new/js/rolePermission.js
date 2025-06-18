$(document).ready(function(){
	$('input[type=checkbox]').change(function( e ) {
		var form = $(this).closest("tr").find('form');
        var url = form.attr('action');
        var formData = form.serialize(); 
        
        $.ajax({
            type: "POST",
            url: url,
            data: formData, // serializes the form's elements.
            success: function(data) {
         	   console.log(data);
            }
    	});
        
	});
	
	$('#datatableRoleSuper').DataTable({
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
//		"columnDefs": [
//			{ "width": "170px", "targets": 0 },
//			{ "width": "170px", "targets": 1 }
//			]
	});
	
});