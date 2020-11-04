$(document).ready(function() {
    'use strict';

    function supportsPrint() {
        return (typeof window.print === 'function');
    }

    if(supportsPrint()) {
        window.print();
    }

});