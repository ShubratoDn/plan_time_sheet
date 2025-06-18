function getMonthHours(month,id, year){
	$.ajax({
		type : "GET",
		url : context + "supervisor/hours-month/"+id + "/" + month+ "/" + year ,
		success: function(data){
			$('#user-month-hours-detail').replaceWith(data);
		},error: function(xhr, status, error) {
			console.log("ssssssssssssssss");
		}
	});
}
