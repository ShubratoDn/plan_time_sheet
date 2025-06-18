function deleteTemplate(id){
	if (confirm('Are you sure ?')) {
		$.ajax({
		    type : "GET",
		    url : context + "admin/template/delete/"+ id,
		    success: function(data) {
		    	location.reload();
		    },error: function(XMLHttpRequest, textStatus, errorThrown) {
		    }
		});
    }else
    {
      console.log('cancel')
    }
}