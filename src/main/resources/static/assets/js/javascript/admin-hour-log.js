$(document).ready(function() {
	
	$('form').parsley();
	$('#datepicker').datepicker();
	$('#datepicker2').datepicker();
	$('#datatable1').DataTable({
		"scrollX": true
	});
	$('#datatable3').DataTable({
		"scrollX": false,
		"bLengthChange": false,
		"bFilter": false,
		"processing" : true,
		"ordering" : false
	});
});



