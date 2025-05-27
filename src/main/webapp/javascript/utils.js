export function makeCall(method, url, formElement, cback, reset = true) {
    const req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        cback(req);
    };
    req.open(method, url);
    if (formElement == null) {
        req.send();
    } else {
        req.send(new FormData(formElement));
    }
    if (formElement !== null && reset === true) {
        formElement.reset();
    }
}
