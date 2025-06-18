$(function() {

    var owner = $('#owner');
    var cardNumber = $('#cardNumber');
    var cardNumberField = $('#card-number-field');
    var CVV = $("#cvv");
    var mastercard = $("#mastercard");
    var confirmButton = $('#confirm-purchase');
    var visa = $("#visa");
    var amex = $("#amex");

    // Use the payform library to format and validate
    // the payment fields.

    cardNumber.payform('formatCardNumber');
    CVV.payform('formatCardCVC');


    cardNumber.keyup(function() {

        amex.removeClass('transparent');
        visa.removeClass('transparent');
        mastercard.removeClass('transparent');

        if ($.payform.validateCardNumber(cardNumber.val()) == false) {
            cardNumberField.addClass('has-error');
        } else {
            cardNumberField.removeClass('has-error');
            cardNumberField.addClass('has-success');
        }

        if ($.payform.parseCardType(cardNumber.val()) == 'visa') {
            mastercard.addClass('transparent');
            amex.addClass('transparent');
        } else if ($.payform.parseCardType(cardNumber.val()) == 'amex') {
            mastercard.addClass('transparent');
            visa.addClass('transparent');
        } else if ($.payform.parseCardType(cardNumber.val()) == 'mastercard') {
            amex.addClass('transparent');
            visa.addClass('transparent');
        }
    });

    confirmButton.click(function(e) {

        e.preventDefault();

        var isCardValid = $.payform.validateCardNumber(cardNumber.val());
        var isCvvValid = $.payform.validateCardCVC(CVV.val());

        if(owner.val().length < 5){
            swal("","Please enter correct card name", "error");
        } else if (!isCardValid) {
            swal("","Please enter correct card number", "error");
        } else if (!isCvvValid) {
            swal("","Please enter correct cvv number", "error");
        } else if ($('#amount').val() == null || $('#amount').val() == '') {
        	alert(amount);
            swal("","Please insert an amount", "error");
        } else {
 	    	swal({
 	   		  title: "Please wait!",
 	   		  text: "Your payment is proceed..",
 	   		  showConfirmButton: false
 	   		});
 		    // Disable the submit button to prevent repeated clicks
 	    	$('form').find('button').prop('disabled', true);
 		    Stripe.card.createToken( $('form'), stripeResponseHandler);
 		    // Prevent the form from submitting with the default action
 		    return false;
        }
    });
});
