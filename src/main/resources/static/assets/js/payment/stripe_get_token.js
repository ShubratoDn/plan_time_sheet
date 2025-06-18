/**
 * This below functions for registration plan
 */
function getStripeToken(publishKey) {
	  // This identifies your website in the createToken call below
	  Stripe.setPublishableKey(publishKey);
}

function stripeResponseHandler(status, response) {
	  
	  var $form = $('#paymentForm');
	  
	  if (response.error) {
		// Show the errors on the form
		//toastr.error(response.error.message);
		  
		swal("",response.error.message, "error");
		$form.find('button').prop('disabled', false);
	  } else {
	    // response contains id and card, which contains additional card details
	    var token = response.id;
	    // Insert the token into the form so it gets submitted to the server
	    $form.append($('<input type="hidden" name="stripeToken" id="stripeToken" />').val(token));
	    // and submit
	    swal({
			  title: "Please wait!",
			  text: "",
			  showConfirmButton: false
			});
	    $form.get(0).submit();
	  }
};


//----------------------------------------------------------------------------------------------------//
//---------------------------------This below functions for upgrade plan------------------------------//
//----------------------------------------------------------------------------------------------------//

function getStripeTokenPaymentForm(publishKey,validatingCardMessage, RegisteringAccountMessage) {
	  // This identifies your website in the createToken call below
	  Stripe.setPublishableKey(publishKey);
	  
	   var $form = $('#paymentForm');
	    	
	    // Disable the submit button to prevent repeated clicks
	    $form.find('button').prop('disabled', true);
	
	    Stripe.card.createToken($form, stripeResponseHandlerPaymentForm);
	    // Prevent the form from submitting with the default action
	    return false;
}

function stripeResponseHandlerPaymentForm(status, response) {
	  
	  var $form = $('#paymentForm');
	  
	  if (response.error) {
		// Show the errors on the form
		//toastr.error(response.error.message);
		  
		swal("",response.error.message, "error");
		$form.find('button').prop('disabled', false);
	  } else {
	    // response contains id and card, which contains additional card details
	    var token = response.id;
	    // Insert the token into the form so it gets submitted to the server
	    $form.append($('<input type="hidden" name="stripeToken" id="stripeToken" />').val(token));
	    // and submit
	    swal({
			  title: "Please wait!",
			  text: "",
			  showConfirmButton: false
			});
	    $form.get(0).submit();
	  }
};