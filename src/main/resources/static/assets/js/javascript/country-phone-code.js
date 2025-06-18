$(document).ready(function() {
	
	$("#telephone").intlTelInput({
		allowDropdown: true,
		placeholderNumberType: "MOBILE",
		onlyCountries: ["in", "us" ],
		initialCountry: "us",
		preferredCountries: [],
//		autoPlaceholder: "polite",
		separateDialCode: false,
		utilsScript: "https://cdn.jsdelivr.net/npm/intl-tel-input@14.0.3/build/js/utils.js",
		customPlaceholder: function(selectedCountryPlaceholder, selectedCountryData) {
			return "+"+ selectedCountryData.dialCode +" "+ selectedCountryPlaceholder;
		},
		
	});
	$("#telephone1").intlTelInput({
		allowDropdown: true,
		placeholderNumberType: "MOBILE",
		onlyCountries: ["us", "in" ],
		initialCountry: "us",
		preferredCountries: [],
//		autoPlaceholder: "polite",
		separateDialCode: false,
		utilsScript: "https://cdn.jsdelivr.net/npm/intl-tel-input@14.0.3/build/js/utils.js",
		customPlaceholder: function(selectedCountryPlaceholder, selectedCountryData) {
			return "+"+ selectedCountryData.dialCode +" "+ selectedCountryPlaceholder;
		},
	});
	$("#telephone2").intlTelInput({
		allowDropdown: true,
		placeholderNumberType: "MOBILE",
		onlyCountries: ["us", "in" ],
		initialCountry: "us",
		preferredCountries: [],
//		autoPlaceholder: "polite",
		separateDialCode: false,
		utilsScript: "https://cdn.jsdelivr.net/npm/intl-tel-input@14.0.3/build/js/utils.js",
		  customPlaceholder: function(selectedCountryPlaceholder, selectedCountryData) {
		    return "+"+ selectedCountryData.dialCode +" "+ selectedCountryPlaceholder;
		  },
	});

});



