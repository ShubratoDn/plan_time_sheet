/* Query param variables */
var PAGE_PARAM = 'page';
/* END Query param variables */

/**
 * Get final url and manage already present params in url
 * @param requestParam
 * @param value
 * @returns
 */
function getFinalUrl(url, requestParam, value) {
	
	var finalUrl = removeURLParameter(url, requestParam);
	
	if(finalUrl.indexOf('?') == -1) {
		finalUrl += '?' + requestParam + '=' + value;
	} else {
		finalUrl += '&' + requestParam + '=' + value;
	}
	
	return finalUrl;
}

/**
 * Remove passed parameter from passed url
 * @param url
 * @param parameter
 * @returns
 */
function removeURLParameter(url, parameter) {
    //prefer to use l.search if you have a location/link object
    var urlparts = url.split('?');   
    if (urlparts.length >= 2) {

        var prefix = encodeURIComponent(parameter) + '=';
        var pars = urlparts[1].split(/[&;]/g);

        //reverse iteration as may be destructive
        for (var i = pars.length; i-- > 0;) {    
            //idiom for string.startsWith
            if (pars[i].lastIndexOf(prefix, 0) !== -1) {  
                pars.splice(i, 1);
            }
        }

        return urlparts[0] + (pars.length > 0 ? '?' + pars.join('&') : '');
    }
    return url;
}

/**
 * Get query param from url 
 * @param key
 * @returns
 */
function qs(key) {
	key = key.replace(/[*+?^$.\[\]{}()|\\\/]/g, "\\$&"); // escape RegEx meta
															// chars
	var match = location.search
			.match(new RegExp("[?&]" + key + "=([^&]+)(&|$)"));
	return match && decodeURIComponent(match[1].replace(/\+/g, " "));
}

/**
 * Pagination
 * @returns
 */
function pagination() {
	
	//When only one page then dont do anything
	if(!totalPages || totalPages < 2) {
		return;
	} 
	
	$('#pagination-ul').twbsPagination({
		totalPages: totalPages,
		// the current page that show on start
		startPage: currentPageNo,
		// maximum visible pages
		visiblePages: 5,
		initiateStartPageClick: false,
		// template for pagination links
		href: false,
		// variable name in href template for page number
		hrefVariable: '{{number}}',
		// Text labels
		first: '',
		prev: 'Previous',
		next: 'Next',
		last: '',
		// carousel-style pagination
		loop: false,
		// pagination Classes
		paginationClass: 'pagination',
		nextClass: 'next',
		prevClass: 'previous',
		//lastClass: 'last',
		//firstClass: 'first',
		pageClass: 'paginate_button page-item ',
		activeClass: 'active',
		disabledClass: 'disabled',
		// callback function
		onPageClick: function (event, currentPage) {
			
			var finalUrl = removeURLParameter(location.href, PAGE_PARAM);
    		finalUrl = getFinalUrl(finalUrl, PAGE_PARAM, currentPage);
    		location.href = finalUrl;
		}
	});
}