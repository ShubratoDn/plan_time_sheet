$(document).ready(function() {
	
});

function saveNote(){
	var noteId = $('#new-note-id').val();
	var note = $('#new-note').val();
	if(note != undefined && note != null && note != ''){
		$("#"+noteId).closest("td").find("input").val(note);
		$("#"+noteId).find(".fa-file-alt").addClass("text-danger");
	}
	$("#add-notes").modal("hide");	
}

function addNotes(thisValue){

	console.log($(thisValue).closest("td").find("input").data('oldnotes'));
	var note =$(thisValue).closest("td").find("input").val();
	var textDate =$(thisValue).closest("tr").find(".date-text").text();
	var textName = $("#user-name").find(":selected").text();
	var textClient = $("#user-client").find(":selected").text();
	var noteId =$(thisValue).attr('id');
	var oldNotes = $(thisValue).closest("td").find("input").data('oldnotes');
	
	if(oldNotes != undefined && oldNotes != null && oldNotes != ''){
		console.log("yes working "+oldNotes);
		var oldNoteDiv = "<div id='oldNote'>"+oldNotes+"<div>";
		$('#oldNote').replaceWith(oldNoteDiv);
	}else{
		var oldNoteDiv = "<div id='oldNote'><div>";
		$('#oldNote').replaceWith(oldNoteDiv);
	}
	
	$('#new-note').val(note);
	$('#new-note-id').val(noteId);
	$("#add-notes").modal("show");
	$("#note-date").text(textDate);
	$("#note-user").text(textName);
	$("#note-client").text(textClient);
	
}

function addRemark(thisValue){
	

	
	var x =document.getElementById("remark");

	if (x.hidden == true) {
	    x.hidden = false;
	  } else {
	    x.hidden = true;
	  }
}

function hideNote(){

	$("#add-notes").modal("hide");

}
