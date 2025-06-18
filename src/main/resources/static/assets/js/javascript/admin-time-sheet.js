$(document).ready(function() {
	
	$('#datatable').DataTable({
		"scrollX": true
	});
	
});

/**
 * set reson form value
 * @param dateKey
 * @param userDetailId
 * @returns
 */
function rejectTimeSheet(dateKey, userDetailId) {
	
	$('#reasonId').val(dateKey);
	$('#userDetailId').val(userDetailId);
	
}