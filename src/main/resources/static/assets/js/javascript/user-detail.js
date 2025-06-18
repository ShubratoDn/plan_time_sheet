/**
 * get basic details
 * @param id
 * @returns
 */
function bacisDetails(name, id) {
	$.ajax({
	    type : "GET",
	    url : context + "admin/basic-detail/"+id,
	    success: function(data){
	    	$('#user-details-modal').replaceWith(data);
	    	$('#view_title_name').text(name);
	    }
	});
}

$(document).ready(function() {
	
	$("#userDetailTableFile").DataTable();
	$("#userDetailTable").DataTable();
	
	$('.client-name').parsley({
		errorsContainer: function (ParsleyField) {
			$(".client-name.parsley-error").parent( ".form-control" ).addClass( "requiedClassAlert" );
			return false;
		},
		errorsWrapper: false,
	});
	
	getClient();
	getEmployer();
	getVendor();
	checkboxChange();
	console.log(w2OrC2cType);
	getEmployerDetails();
	var checked = $("input[type=radio][name=w2OrC2cType]:checked").val();
	if (w2OrC2cType.$name == 'W2') {
    	$("#select-w2").removeClass("hide");
    	$("#select-c2c").addClass("hide");
    	$("#select-w2 .set-require").prop('required',true);
    	$("#select-c2c .set-require").prop('required',false);
    	
    }
    else if (w2OrC2cType.$name == 'C2C') {
    	$("#select-c2c").removeClass("hide");
    	$("#select-w2").addClass("hide");
    	$("#select-w2 .set-require").prop('required',false);
    	$("#select-c2c .set-require").prop('required',true);
    }
	
	changeValue();
	changeRecurssive();
	rateCountOnChange();
});

function changeRecurssive(){
	$("#c2cOrotherRecurssive").change(function( e ) {
		if($("#c2cOrotherRecurssive").prop('checked')){
			$("#c2cOrotherRecurssiveMonth").val('');
			$("#c2cOrotherRecurssiveMonth").prop('required',false);
			$("#c2cOrotherRecurssiveMonth").prop('disabled',true);
			$("#c2cOrotherRecurssiveMonth option[value='']").text('On all month');
		} else {
			$("#c2cOrotherRecurssiveMonth").val('');
			$("#c2cOrotherRecurssiveMonth").prop('required',true);
			$("#c2cOrotherRecurssiveMonth").prop('disabled',false);
			$("#c2cOrotherRecurssiveMonth option[value='']").text('Select recurssive month');
		}
	});
	$("#recRecurssive").change(function( e ) {
		if($("#recRecurssive").prop('checked')){
			$("#recRecurssiveMonth").val('');
			$("#recRecurssiveMonth").prop('required',false);
			$("#recRecurssiveMonth").prop('disabled',true);
			$("#recRecurssiveMonth option[value='']").text('On all month');
		} else {
			$("#recRecurssiveMonth").val('');
			$("#recRecurssiveMonth").prop('required',true);
			$("#recRecurssiveMonth").prop('disabled',false);
			$("#recRecurssiveMonth option[value='']").text('Select recurssive month');
		}
	});
	$("#bDMRecurssive").change(function( e ) {
		if($("#bDMRecurssive").prop('checked')){
			$("#bDMRecurssiveMonth").val('');
			$("#bDMRecurssiveMonth").prop('required',false);
			$("#bDMRecurssiveMonth").prop('disabled',true);
			$("#bDMRecurssiveMonth option[value='']").text('On all month');
		} else {
			$("#bDMRecurssiveMonth").val('');
			$("#bDMRecurssiveMonth").prop('required',true);
			$("#bDMRecurssiveMonth").prop('disabled',false);
			$("#bDMRecurssiveMonth option[value='']").text('Select recurssive month');
		}
	});
	$("#aCMRecurssive").change(function( e ) {
		if($("#aCMRecurssive").prop('checked')){
			$("#aCMRecurssiveMonth").val('');
			$("#aCMRecurssiveMonth").prop('required',false);
			$("#aCMRecurssiveMonth").prop('disabled',true);
			$("#aCMRecurssiveMonth option[value='']").text('On all month');
		} else {
			$("#aCMRecurssiveMonth").val('');
			$("#aCMRecurssiveMonth").prop('required',true);
			$("#aCMRecurssiveMonth").prop('disabled',false);
			$("#aCMRecurssiveMonth option[value='']").text('Select recurssive month');
		}
	});
}

function checkboxChange(){
	$('input[type=radio][name=w2OrC2cType]').change(function() {
	    if (this.value == 'W2') {
	    	$("#select-w2").removeClass("hide");
	    	$("#select-c2c").addClass("hide");
	    	$("#select-w2 .set-require").prop('required',true);
	    	$("#select-c2c .set-require").prop('required',false);
	    	$("#select-c2c .setValueSelect").val('');
	    	document.getElementById("employerNameSelect").fstdropdown.rebind();
	    	
	    }
	    else if (this.value == 'C2C') {
	    	$("#select-c2c").removeClass("hide");
	    	$("#select-w2").addClass("hide");
	    	$("#select-w2 .set-require").prop('required',false);
	    	$("#select-c2c .set-require").prop('required',true);
	    	$("#select-w2 .setValueSelect").val('');
	    }
	});
}


function clientAdd(){
	$("#client-add-form").submit(function( e ) {
	  e.preventDefault();
	 
	  if($(this).parsley().validate()){
	  
		  var formData = new FormData(this);
		  
		  $("#add-client-form-is-submit").css("display","flex");
		    $.ajax({
			    type : "POST",
			    url : $(this).attr('action'),
			    data: formData,
			    cache: false,
		        contentType: false,
		        processData: false,
			    success: function(data) {
			    	$("#add-client-form-is-submit").css("display","none");
			    	if(formData.get('type') == 'CLIENT'){
			    		$(new Option(data.data.clientName, data.data.id)).appendTo('#clientNameSelect');
				    	$("#clientNameSelect").val(data.data.id);
				    	document.getElementById("clientNameSelect").fstdropdown.rebind();
				    	$("#client-add-form")[0].reset();
				    	$("#client-add").modal("hide");
			    	} else if(formData.get('type') == 'EMPLOYEE'){
			    		$(new Option(data.data.clientName, data.data.id)).appendTo('#employerNameSelect');
						$("#employerNameSelect").val(data.data.id);
						document.getElementById("employerNameSelect").fstdropdown.rebind();
						$("#client-add-form")[0].reset();
				    	$("#client-add").modal("hide");
						$('#select-c2c input[name ="employerEmail"]').val("");
				    	$('#select-c2c input[name ="employerPhone"]').val(data.data.phone);
				    	$('#select-c2c input[name ="address"]').val(data.data.zipCode);
			    	} else if(formData.get('type') == 'VENDOR'){
			    		$(new Option(data.data.clientName, data.data.id)).appendTo('#vendorNameSelect');
				    	$("#vendorNameSelect").val(data.data.id);
				    	document.getElementById("vendorNameSelect").fstdropdown.rebind();
				    	$("#client-add-form")[0].reset();
				    	$("#client-add").modal("hide");
			    	}
			    },
			    error: function(xhr, status, error) {
			    	
			    	var form = $("#client-add-form");
			        var url = form.attr('action');
			        var formData = form.serialize() + '&sectionFormError=true'; 
			        
			        $.ajax({
			            type: "POST",
			            url: url,
			            data: formData, // serializes the form's elements.
			            success: function(data) {
			         	   console.log(data);
			         	  $("#add-client-form-is-submit").css("display","none");
			         	   $('#client-add .modal-content').replaceWith(data);
			         	   $("#client-add input[type=tel]").intlTelInput({
			    				allowDropdown: true,
			    				placeholderNumberType: "MOBILE",
			    				onlyCountries: ["in", "us" ],
			    				initialCountry: "us",
			    				preferredCountries: [],
			    				separateDialCode: false,
			    				utilsScript: "https://cdn.jsdelivr.net/npm/intl-tel-input@14.0.3/build/js/utils.js",
			    				customPlaceholder: function(selectedCountryPlaceholder, selectedCountryData) {
			    					return "+"+ selectedCountryData.dialCode +" "+ selectedCountryPlaceholder;
			    				},
			    				
			    			});
			    			clientAdd();
			    			$('#client-add-form').parsley();
			    			$("#clientAddFormError div" ).remove();
			            }
			    	});
		    	}
			});
	  }
	  
	});
}

function getClient() {
	$.ajax({
		type : "GET",
		url : context + "admin/get-client",
		success: function(data){
			$(new Option('Select client', '')).appendTo('#clientNameSelect');
			var i;
			for (i = 0; i < data.data.length; i++) { 
				if(data.data[i].id != clientIdSelected){
					$(new Option(data.data[i].clientName, data.data[i].id)).appendTo('#clientNameSelect');
				} else {
					$('#clientNameSelect').append('<option value='+ data.data[i].id +' selected="selected">'+data.data[i].clientName+'</option>');
				}
			}
//			$("#clientNameSelect").fstdropdown.rebind();
			document.getElementById("clientNameSelect").fstdropdown.rebind();
		},
		error: function(xhr, status, error) {
			console.log(error);
		}
	});
}

function getEmployer() {
	$.ajax({
	    type : "GET",
	    url : context + "admin/get-employer",
	    success: function(data){
	    	$(new Option('Select employer', '')).appendTo('#employerNameSelect');
	    	var i;
	    	for (i = 0; i < data.data.length; i++) { 
	    		
	    		if(data.data[i].id != employerIdSelected){
	    			$(new Option(data.data[i].clientName, data.data[i].id)).appendTo('#employerNameSelect');
				} else {
					$('#employerNameSelect').append('<option value='+ data.data[i].id +' selected="selected">'+data.data[i].clientName+'</option>');
				}
	    	}
	    	document.getElementById("employerNameSelect").fstdropdown.rebind();
	    },
	    error: function(xhr, status, error) {
	    	console.log(error);
    	}
	});
}

function getVendor() {
	$.ajax({
		type : "GET",
		url : context + "admin/get-vendor",
		success: function(data){
			$(new Option('Select vendor', '')).appendTo('#vendorNameSelect');
			var i;
			for (i = 0; i < data.data.length; i++) { 
				if(data.data[i].id != vendorIdSelected){
					$(new Option(data.data[i].clientName, data.data[i].id)).appendTo('#vendorNameSelect');
				} else {
					$('#vendorNameSelect').append('<option value='+ data.data[i].id +' selected="selected">'+data.data[i].clientName+'</option>');
				}
			}
			document.getElementById("vendorNameSelect").fstdropdown.rebind();
		},
		error: function(xhr, status, error) {
			console.log(error);
		}
	});
}

function clientAddModal(){
	addClient('client');
}

function employerAddModal(){
	addClient('employee');
}

function vendorAddModal(){
	addClient('vendor');
}

function addClient(type){
	$.ajax({
		type : "GET",
		url : context + "admin/new-client-add?type=" + type,
		success: function(data){
			if(data.includes("!DOCTYPE html")){
				location.href= context + "supervisor/unauthorized";
			}
			$("#client-add").replaceWith(data);
			$("#client-add input[type=tel]").intlTelInput({
				allowDropdown: true,
				placeholderNumberType: "MOBILE",
				onlyCountries: ["in", "us" ],
				initialCountry: "us",
				preferredCountries: [],
				separateDialCode: false,
				utilsScript: "https://cdn.jsdelivr.net/npm/intl-tel-input@14.0.3/build/js/utils.js",
				customPlaceholder: function(selectedCountryPlaceholder, selectedCountryData) {
					return "+"+ selectedCountryData.dialCode +" "+ selectedCountryPlaceholder;
				},
				
			});
			$("#client-add").modal("show");
			clientAdd();
			$('#client-add-form').parsley();
			$("#clientAddFormError div" ).remove();
		},
		error: function(xhr, status, error) {
			console.log(error);
		}
	});
}

function getEmployerDetails(){

	$("#employerNameSelect").change(function( e ) {
		if(this.value !=""){
			$.ajax({
			    type : "GET",
			    url : context + "admin/get-employer-details/"+this.value,
			    success: function(data){
			    	console.log();
			    	$('#select-c2c input[name ="employerEmail"]').val(data.data.email);
			    	$('#select-c2c input[name ="employerPhone"]').val(data.data.phone);
			    	$('#select-c2c input[name ="address"]').val(data.data.zipCode);
			    },
			    error: function(xhr, status, error) {
			    	console.log(error);
		    	}
			});
		} else {
			$('#select-c2c input[name ="employerEmail"]').val("");
	    	$('#select-c2c input[name ="employerPhone"]').val("");
	    	$('#select-c2c input[name ="address"]').val("");
		}
	});
}

function setInternalUser(type,thisValue){
	if(thisValue !=""){
		$.ajax({
			type : "GET",
			url : context + "admin/set-internal-user/"+thisValue,
			success: function(data){
				if(type == "AMN"){
					$('#aCMCommissionId input[name ="aCMCommission"]').val(data.data.rate);
					if(data.data.recurssive == true){
						$("#aCMRecurssive").prop("checked", true);
						$("#aCMRecurssiveMonth").val('');
						$("#aCMRecurssiveMonth").prop('required',false);
						$("#aCMRecurssiveMonth").prop('disabled',true);
						$("#aCMRecurssiveMonth option[value='']").text('On all month');
						
						if(data.data.rateCountOn == "ON_HOURS"){
							$("#canNotChangeCoverAMN").css("display","block");
							$("#canNotChangeAMN").css("display","block");
							$("#aCMRateType").addClass("readOnlyClass");
						} else {
							$("#canNotChangeCoverAMN").css("display","none");
							$("#canNotChangeAMN").css("display","none");
							$("#aCMRateType").removeClass("readOnlyClass");
						}
						
					} else {
						$("#aCMRecurssive").prop("checked", false);
						$("#aCMRecurssiveMonth").val('');
						$("#aCMRecurssiveMonth").prop('required',true);
						$("#aCMRecurssiveMonth").prop('disabled',false);
						$("#aCMRecurssiveMonth option[value='']").text('Select recurssive month');
					}
					
					$("#aCMRateCountOn").val(data.data.rateCountOn);
					$("#aCMRateType").val(data.data.rateType);
					
					if(data.data.defaultUser == true){
						$("#aCMRateType").addClass('readonlyColor');
						$("#aCMRateCountOn").addClass('readonlyColor');
						$('#aCMCommissionId input[name ="aCMCommission"]').prop('readonly',true);
						$('#aCMCommissionId input[name ="aCMCommission"]').addClass('readonlyColor');
						$("#aCMreadonlyRecurssive").addClass('show');
						$("#aCMreadonlyRateType").addClass('show');
						$("#aCMreadonlyCountOn").addClass('show');
						
					} else {
						$("#aCMRateType").removeClass('readonlyColor');
						$("#aCMRateCountOn").removeClass('readonlyColor');
						$('#aCMCommissionId input[name ="aCMCommission"]').prop('readonly',false);
						$('#aCMCommissionId input[name ="aCMCommission"]').removeClass('readonlyColor');
						$("#aCMreadonlyRecurssive").removeClass('show');
						$("#aCMreadonlyRateType").removeClass('show');
						$("#aCMreadonlyCountOn").removeClass('show');
					}
					
				} else if(type=="BDM"){
					
					$('#bdmId input[name ="bDMCommission"]').val(data.data.rate);
					if(data.data.recurssive == true){
						$("#bDMRecurssive").prop("checked", true);
						$("#bDMRecurssiveMonth").val('');
						$("#bDMRecurssiveMonth").prop('required',false);
						$("#bDMRecurssiveMonth").prop('disabled',true);
						$("#bDMRecurssiveMonth option[value='']").text('On all month');
						
						if(data.data.rateCountOn == "ON_HOURS"){
							$("#canNotChangeCoverBDM").css("display","block");
							$("#canNotChangeBDM").css("display","block");
							$("#bDMRateType").addClass("readOnlyClass");
						} else {
							$("#canNotChangeCoverBDM").css("display","none");
							$("#canNotChangeBDM").css("display","none");
							$("#bDMRateType").removeClass("readOnlyClass");
						}
//						
						
					} else {
						$("#bDMRecurssive").prop("checked", false);
						$("#bDMRecurssiveMonth").val('');
						$("#bDMRecurssiveMonth").prop('required',true);
						$("#bDMRecurssiveMonth").prop('disabled',false);
						$("#bDMRecurssiveMonth option[value='']").text('Select recurssive month');
					}
					
					$("#bDMRateType").val(data.data.rateType);
					$("#bDMRateCountOn").val(data.data.rateCountOn);
					
					if(data.data.defaultUser == true){
						$("#bDMRecurssive").addClass('readonlyColor');
						$("#bDMRateType").addClass('readonlyColor');
						$("#bDMRateCountOn").addClass('readonlyColor');
						$('#bdmId input[name ="bDMCommission"]').prop('readonly',true);
						$('#bdmId input[name ="bDMCommission"]').addClass('readonlyColor');
						$("#bDMreadonlyRecurssive").addClass('show');
						$("#bDMreadonlyRateType").addClass('show');
						$("#bDMreadonlyCountOn").addClass('show');
						
					} else {
						$("#bDMRecurssive").removeClass('readonlyColor');
						$("#bDMRateType").removeClass('readonlyColor');
						$("#bDMRateCountOn").removeClass('readonlyColor');
						$('#bdmId input[name ="bDMCommission"]').prop('readonly',false);
						$('#bdmId input[name ="bDMCommission"]').removeClass('readonlyColor');
						$("#bDMreadonlyRecurssive").removeClass('show');
						$("#bDMreadonlyRateType").removeClass('show');
						$("#bDMreadonlyCountOn").removeClass('show');
					}
					
				} else if(type=="REN"){
					
					$('#renId input[name ="recCommission"]').val(data.data.rate);
					if(data.data.recurssive == true){
						$("#recRecurssive").prop("checked", true);
						$("#recRecurssiveMonth").val('');
						$("#recRecurssiveMonth").prop('required',false);
						$("#recRecurssiveMonth").prop('disabled',true);
						$("#recRecurssiveMonth option[value='']").text('On all month');
						
						if(data.data.rateCountOn == "ON_HOURS"){
							$("#canNotChangeCoverREN").css("display","block");
							$("#canNotChangeREN").css("display","block");
							$("#recRateType").addClass("readOnlyClass");
						} else {
							$("#canNotChangeCoverREN").css("display","none");
							$("#canNotChangeREN").css("display","none");
							$("#recRateType").removeClass("readOnlyClass");
						}
						
					} else {
						$("#recRecurssive").prop("checked", false);
						$("#recRecurssiveMonth").val('');
						$("#recRecurssiveMonth").prop('required',true);
						$("#recRecurssiveMonth").prop('disabled',false);
						$("#recRecurssiveMonth option[value='']").text('Select recurssive month');
					}
					
					$("#recRateType").val(data.data.rateType);
					$("#recRateCountOn").val(data.data.rateCountOn);
					if(data.data.defaultUser == true){
						$("#recRecurssive").addClass('readonlyColor');
						$("#recRateType").addClass('readonlyColor');
						$("#recRateCountOn").addClass('readonlyColor');
						$('#renId input[name ="recCommission"]').prop('readonly',true);
						$('#renId input[name ="recCommission"]').addClass('readonlyColor');
						$("#rECreadonlyRecurssive").addClass('show');
						$("#rECreadonlyRateType").addClass('show');
						$("#rECreadonlyCountOn").addClass('show');
					} else {
						$("#recRecurssive").removeClass('readonlyColor');
						$("#recRateType").removeClass('readonlyColor');
						$("#recRateCountOn").removeClass('readonlyColor');
						$('#renId input[name ="recCommission"]').prop('readonly',false);
						$('#renId input[name ="recCommission"]').removeClass('readonlyColor');
						$("#rECreadonlyRecurssive").removeClass('show');
						$("#rECreadonlyRateType").removeClass('show');
						$("#rECreadonlyCountOn").removeClass('show');
					}
				}
			},
			error: function(xhr, status, error) {
				console.log(error);
			}
		});
	} else {
		if(type == "AMN"){
			
			$('#aCMCommissionId input[name ="aCMCommission"]').val("");
			$("#aCMRecurssive").prop("checked", false);
			
			$("#aCMRateType").val('');
			$("#aCMRateCountOn").val('');
			
			$("#aCMRecurssive").prop("checked", false);
			$("#aCMRecurssiveMonth").val('');
			$("#aCMRecurssiveMonth").prop('required',true);
			$("#aCMRecurssiveMonth").prop('disabled',false);
			$("#aCMRecurssiveMonth option[value='']").text('Select recurssive month');
			
			$("#aCMRecurssive").removeClass('readonlyColor');
			$("#aCMRateType").removeClass('readonlyColor');
			$("#aCMRateCountOn").removeClass('readonlyColor');
			$('#aCMCommissionId input[name ="aCMCommission"]').prop('readonly',false);
			$('#aCMCommissionId input[name ="aCMCommission"]').removeClass('readonlyColor');
			$("#aCMreadonlyRecurssive").removeClass('show');
			$("#aCMreadonlyRateType").removeClass('show');
			$("#aCMreadonlyCountOn").removeClass('show');
		} else if(type=="BDM"){
			
			$('#bdmId input[name ="bDMCommission"]').val("");
			$("#bDMRecurssive").prop("checked", false);
			
			$("#bDMRateType").val('');
			$("#bDMRateCountOn").val('');
			
			$("#bDMRecurssive").prop("checked", false);
			$("#bDMRecurssiveMonth").val('');
			$("#bDMRecurssiveMonth").prop('required',true);
			$("#bDMRecurssiveMonth").prop('disabled',false);
			$("#bDMRecurssiveMonth option[value='']").text('Select recurssive month');
			
			$("#bDMRecurssive").removeClass('readonlyColor');
			$("#bDMRateType").removeClass('readonlyColor');
			$("#bDMRateCountOn").removeClass('readonlyColor');
			$('#bdmId input[name ="bDMCommission"]').prop('readonly',false);
			$('#bdmId input[name ="bDMCommission"]').removeClass('readonlyColor');
			$("#bDMreadonlyRecurssive").removeClass('show');
			$("#bDMreadonlyRateType").removeClass('show');
			$("#bDMreadonlyCountOn").removeClass('show');
		} else if(type=="REN"){
			
			$('#renId input[name ="recCommission"]').val("");
			$("#recRecurssive").prop("checked", false);
			$("#recRateType").val('');
			$("#recRateCountOn").val('');
			
			$("#recRecurssive").prop("checked", false);
			$("#recRecurssiveMonth").val('');
			$("#recRecurssiveMonth").prop('required',true);
			$("#recRecurssiveMonth").prop('disabled',false);
			$("#recRecurssiveMonth option[value='']").text('Select recurssive month');
			
			$("#recRecurssive").removeClass('readonlyColor');
			$("#recRateType").removeClass('readonlyColor');
			$("#recRateCountOn").removeClass('readonlyColor');
			$('#renId input[name ="recCommission"]').prop('readonly',false);
			$('#renId input[name ="recCommission"]').removeClass('readonlyColor');
			$("#rECreadonlyRecurssive").removeClass('show');
			$("#rECreadonlyRateType").removeClass('show');
			$("#rECreadonlyCountOn").removeClass('show');
		}
	}
}

function changeValue(){
	$('input[type=radio][name=bDMRateType]').change(function() {
		$('#bdmId input[name ="bDMCommission"]').val('');
	});
	$('input[type=radio][name=recRateType]').change(function() {
		$('#renId input[name ="recCommission"]').val('');
	});
	$('input[type=radio][name=aCMRateType]').change(function() {
		$('#aCMCommissionId input[name ="aCMCommission"]').val('');
	});
}

/**
 * Add or remove rows by ajax
 * @param rowIndex
 * @param sectionName
 * @returns
 */
function addOrRemoveRow(rowIndex, sectionName) {
	
	var form = $("#client-add-form");
    var url = form.attr('action');
    var formData = form.serialize() + '&' + sectionName + '=' + rowIndex; 
    
	$.ajax({
        type: "POST",
        url: url,
        data: formData, // serializes the form's elements.
        success: function(data) {
     	   console.log(data);
     	   $('#client-add .modal-content').replaceWith(data);
     	  $("#client-add input[type=tel]").intlTelInput({
				allowDropdown: true,
				placeholderNumberType: "MOBILE",
				onlyCountries: ["in", "us" ],
				initialCountry: "us",
				preferredCountries: [],
				separateDialCode: false,
				utilsScript: "https://cdn.jsdelivr.net/npm/intl-tel-input@14.0.3/build/js/utils.js",
				customPlaceholder: function(selectedCountryPlaceholder, selectedCountryData) {
					return "+"+ selectedCountryData.dialCode +" "+ selectedCountryPlaceholder;
				},
				
			});
			clientAdd();
			$('#client-add-form').parsley();
			$("#clientAddFormError div" ).remove();
        }
	});
}


function rateCountOnChange(){
	$("#bDMRateCountOn").change(function (){
		if($(this).val() == "ON_HOURS"){
			$("#canNotChangeCoverBDM").css("display","block");
			$("#canNotChangeBDM").css("display","block");
			$("#bDMRateType").addClass("readOnlyClass");
			$("#bDMRecurssive").prop("checked",true);
			$("#bDMRateType").val("FIX");
			
			$("#bDMRecurssiveMonth").val('');
			$("#bDMRecurssiveMonth").prop('required',false);
			$("#bDMRecurssiveMonth").prop('disabled',true);
			$("#bDMRecurssiveMonth option[value='']").text('On all month');
			
			
		} else {
			$("#canNotChangeCoverBDM").css("display","none");
			$("#canNotChangeBDM").css("display","none");
			$("#bDMRateType").removeClass("readOnlyClass");
			$("#bDMRecurssive").prop("checked",false);
			$("#bDMRateType").val("");
			
			$("#bDMRecurssiveMonth").val('');
			$("#bDMRecurssiveMonth").prop('required',true);
			$("#bDMRecurssiveMonth").prop('disabled',false);
			$("#bDMRecurssiveMonth option[value='']").text('Select recurssive month');
		}
		
	});
	$("#aCMRateCountOn").change(function (){
		if($(this).val() == "ON_HOURS"){
			$("#canNotChangeCoverAMN").css("display","block");
			$("#canNotChangeAMN").css("display","block");
			$("#aCMRateType").addClass("readOnlyClass");
			$("#aCMRecurssive").prop("checked",true);
			$("#aCMRateType").val("FIX");
			
			$("#aCMRecurssiveMonth").val('');
			$("#aCMRecurssiveMonth").prop('required',false);
			$("#aCMRecurssiveMonth").prop('disabled',true);
			$("#aCMRecurssiveMonth option[value='']").text('On all month');
			
		} else {
			$("#canNotChangeCoverAMN").css("display","none");
			$("#canNotChangeAMN").css("display","none");
			$("#aCMRateType").removeClass("readOnlyClass");
			$("#aCMRecurssive").prop("checked",false);
			$("#aCMRateType").val("");
			
			$("#aCMRecurssiveMonth").val('');
			$("#aCMRecurssiveMonth").prop('required',true);
			$("#aCMRecurssiveMonth").prop('disabled',false);
			$("#aCMRecurssiveMonth option[value='']").text('Select recurssive month');
		}
		
	});
	$("#recRateCountOn").change(function (){
		if($(this).val() == "ON_HOURS"){
			$("#canNotChangeCoverREN").css("display","block");
			$("#canNotChangeREN").css("display","block");
			$("#recRateType").addClass("readOnlyClass");
			$("#recRecurssive").prop("checked",true);
			$("#recRateType").val("FIX");
			
			$("#recRecurssiveMonth").val('');
			$("#recRecurssiveMonth").prop('required',false);
			$("#recRecurssiveMonth").prop('disabled',true);
			$("#recRecurssiveMonth option[value='']").text('On all month');
		
		} else {
			$("#canNotChangeCoverREN").css("display","none");
			$("#canNotChangeREN").css("display","none");
			$("#recRateType").removeClass("readOnlyClass");
			$("#recRecurssive").prop("checked",false);
			$("#recRateType").val("");
			
			$("#recRecurssiveMonth").val('');
			$("#recRecurssiveMonth").prop('required',true);
			$("#recRecurssiveMonth").prop('disabled',false);
			$("#recRecurssiveMonth option[value='']").text('Select recurssive month');
		}
		
	});
}


function setInvoiceTo(value){
	if(value == "client"){
		$("#clientNameSelect").prop('required',true);
		$("#vendorNameSelect").prop('required',false);
		$("#vendorNameSelect").parent( ".select-client" ).removeClass( "requiedClassAlert");
	} else {
		$("#vendorNameSelect").prop('required',true);
		$("#clientNameSelect").prop('required',false);
		$("#clientNameSelect").parent( ".select-client" ).removeClass( "requiedClassAlert");
	}
}