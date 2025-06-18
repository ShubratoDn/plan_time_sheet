jQuery(document).ready(function(){
	$("#datatable").DataTable({
		"bLengthChange": true,
		"bFilter": true,
		"processing" : true,
		"ordering" : false,
	});
	setFstDropdown();
});

function timeSheetDetails(thisVar) {
	arr = JSON.parse(thisVar);
	
	var list = '';
    
	for(var i = 0; i < arr.length; i++){
		list = list + '<tr class="odd gradeX">' + 
	        '<td>'+ (i+1) + '</td>' +
	        '<td>'+ arr[i].userDetail.clientName + '</td>' +
	        '<td>'+ arr[i].startDate + ' TO ' + arr[i].endDate +'</td>'+
	    '</tr>';
		
	}
	var tbody = '<tbody>' + list +  '</tbody>';	
	
	$("#popUpTable").find('tbody').replaceWith(tbody);
//  
}