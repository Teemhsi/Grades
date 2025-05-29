/**
 * AJAX call management
 */

export function makeCall(method, url, requestBody, callBackFunction, reset = true) {
    let req = new XMLHttpRequest(); // visible by closure
    req.onreadystatechange = function() {
        callBackFunction(req);
    }; // closure
    req.open(method, url);
    //If the XMLHttpRequest doesn't need a request body (formElement)
    if (requestBody === null) {
        req.send();
    } else {
        req.send(new FormData(requestBody));
    }
    if (requestBody !== null && reset === true) {
        requestBody.reset();
    }
}
