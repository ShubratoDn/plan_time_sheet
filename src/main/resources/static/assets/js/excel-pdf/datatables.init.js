$(document).ready(function() {
			$("#datatable").DataTable();
			
			$("#datatable-buttons").DataTable({
				lengthChange : !1,
				buttons : [ "excel", "pdf"]
			}).buttons().container().appendTo("#datatable-buttons_wrapper .col-md-6:eq(0)");
			
	
		});