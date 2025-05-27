function makeCall(method, url, formElement, cback, reset = true) {
    const req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === XMLHttpRequest.DONE) {
            if (req.status >= 200 && req.status < 300) {
                cback(req);
            } else {
                console.error("Errore HTTP:", req.status, req.responseText);
            }
        }
    };
    req.open(method, url, true);
    if (formElement === null) {
        req.send();
    } else {
        req.send(new FormData(formElement));
        if (reset) formElement.reset();
    }
}
