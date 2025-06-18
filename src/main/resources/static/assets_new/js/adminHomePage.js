$(document).ready(function () {
	var table = $("#datatable-buttons").DataTable({
        "pageLength": 50,
	});

    $(".toggle-vis").each(function( index ) {
    	var column = table.column( $(this).attr('data-column') );
    	 
        column.visible( false );
    });
    
    $('.toggle-vis').on( 'change', function (e) {
        e.preventDefault();
 
        // Get the column API object
        var column = table.column( $(this).attr('data-column') );
 
        // Toggle the visibility
        console.log( $(this).prop( "checked" ) );
        column.visible( $(this).prop( "checked" ) );
    } );
    
    $('#showAllColumn').on( 'click', function (e) {
	    $(".toggle-vis").each(function( index ) {
	    	var column = table.column( $(this).attr('data-column') );
	        column.visible( true );
	        $(this).prop( "checked",true )
	    });
    });
    clickOutToTable();
});

function tableColumnShowHide(thisValue){
	$("#tableShowHide").toggleClass('show');
}

function clickOutToTable(){
		
	$('body').on('click', function(e) {
	    var container = $(".tableShowHide");
	 
	    // if the target of the click isn't the container nor a descendant of the container
	    if (!container.is(e.target) && container.has(e.target).length === 0) 
	    {
	    	$("#tableShowHide").removeClass('show');
	    	
	    }
	    
	});
}		
	
/**
 * get user basic details
 * @param id
 * @returns
 */
function bacisDetails(name, id) {
	$.ajax({
		type : "GET",
		url : context + "supervisor/basic-detail/"+id,
		success: function(data){
		
			$('#user-details-modal').replaceWith(data);
		}
	});
}

function hoursDetails(name, id) {
	$.ajax({
		type : "GET",
		url : context + "admin/user-hours-detail/"+id + "?year="+selectYear,
		success: function(data){
			$('#view_title_hour_name').text(name);
			$('#user-hours-modal').replaceWith(data);
		}
	});
}
		
		

