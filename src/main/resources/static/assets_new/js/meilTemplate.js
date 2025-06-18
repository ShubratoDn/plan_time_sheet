$(document).ready(function(){
	$("#basic-pills-wizard").bootstrapWizard({tabClass:"nav nav-pills nav-justified"});
	$(".summernote").summernote({
		toolbar: [
		    // [groupName, [list of button]]
		    ['style', ['bold', 'italic', 'underline', 'clear']],
		    ['font', ['strikethrough', 'superscript', 'subscript']],
		    ['fontsize', ['fontsize']],
		    ['color', ['color']],
		    ['para', ['ul', 'ol', 'paragraph']],
		    ['table', ['table']],
		    ['insert', ['link']],
		    
		  ],
		minHeight:200,maxHeight:500});
	$(".summernote").on("summernote.change", function (e) {   // callback as jquery custom event 
		$("#description").val($('.summernote').summernote('code'));
 	});
	
	submitTemplate();
});

function setKey(text){
	if($("input[name=insert]:checked").val() == 'suject'){
		var textValue = $("#sujectId").val() + " " +text;
		console.log(textValue.length);
		if(textValue.length < 231){ 
			$("#sujectId").val($("#sujectId").val() + " " +text );
		} else {
			$("#sujectIdError").replaceWith('<div class="validation-message" id="sujectIdError" ><p>subject max length 230</p></div>');
		}
	} else {
		$('.summernote').summernote('editor.insertText', text);
	}
}
function setSignature(text){
	$('.summernote').summernote('editor.pasteHTML', text);
	$('#signatureViewList').modal('hide');
}

function submitTemplate(){
	$("#submitTemplate").submit(function( e ) {
			
		if($("#admin").prop('checked')==false && $("#supervisor").prop('checked')==false && $("#user").prop('checked')==false){
			e.preventDefault();
			$("#errorPermission").show();
		}
	});
	$("#submitTemplateButton").click(function( e ) {
		
		if($("#admin").prop('checked')==false && $("#supervisor").prop('checked')==false && $("#user").prop('checked')==false){
			e.preventDefault();
			$("#errorPermission").show();
		}
		var name = $("#submitTemplate input[name=templateName]").val();
		var id = $("#submitTemplate input[name=id]").val();
		if(id == undefined || id == ''){
			id ='';
		}
		$.ajax({
		    type : "GET",
		    url : context + "admin/template/exist?templateName="+ name+"&id=" +id,
		    success: function(data) {
		    	if(data.data == true){
		    		e.preventDefault();
		    		$("#templateNameError").show();
		    	} else {
		    		$("#submitTemplate").submit();
		    	}
		    	console.log(data);

		    },error: function(XMLHttpRequest, textStatus, errorThrown) {
		    	var msg = '<div id="emailAlert"><div class="alert alert-danger alert-dismissible" role="alert">' + 
			   	'<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
				'<span aria-hidden="true">×</span>' + 
			  '</button>' + 
			  '<strong>Error</strong> Please try again' + 
			  '</div></div>';
		    	$("#emailAlert" ).replaceWith( msg );
		    }
		});
		
	});
}

function cloneTemplate(thisValue){
	var set = $(thisValue).children("option").filter(":selected");
	
	var h=set.data('roleadminpermission');
	if(set.data('roleadminpermission') == true){
		$("#submitTemplate input[name=roleAdminPermission]").prop('checked',true);
	} else {
		$("#submitTemplate input[name=roleAdminPermission]").prop('checked',false);
	}
	
	var x=set.data('rolesupervisorpermission');
	if(set.data('rolesupervisorpermission') == true){
		$("#submitTemplate input[name=roleSupervisorPermission]").prop('checked',true);
	} else {
		$("#submitTemplate input[name=roleSupervisorPermission]").prop('checked',false);
	}
	
	var j=set.data('roleuserpermission');
	if(set.data('roleuserpermission') == true){
		$("#submitTemplate input[name=roleUserPermission]").prop('checked',true);
	} else {
		$("#submitTemplate input[name=roleUserPermission]").prop('checked',false);
	}
	
	var c=set.data('subjecttext');
	$("#sujectId").val(set.data('subjecttext'));
	
	$("#description").val(set.val());
	$('.summernote').summernote("reset");;
	$('.summernote').summernote('editor.pasteHTML', set.val());
}


