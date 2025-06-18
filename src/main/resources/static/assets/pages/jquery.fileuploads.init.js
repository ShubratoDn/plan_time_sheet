
/**
* 
*/

$(document).ready(function(){

	'use-strict';

    //Example 2
    $('#filer_input2').filer({
        limit: 3,
        maxSize: 3,
        extensions: ['jpg', 'jpeg', 'png', 'gif', 'psd'],
        changeInput: true,
        showThumbs: true,
        addMore: true
    });

});
