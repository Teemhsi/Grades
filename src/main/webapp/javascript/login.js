import { makeCall } from "./utils.js";

function showError(message) {
    const errorMsg = document.getElementById("error-message");
    errorMsg.textContent = message;
}

function clearError() {
    const errorMsg = document.getElementById("error-message");
    errorMsg.textContent = "";
}

function isValidEmail(email) {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailPattern.test(email);
}

function validateForm(email, password) {
    clearError();

    if (!email || email.trim() === "") {
        showError("Please enter your email address.");
        return false;
    }

    if (!password || password.trim() === "") {
        showError("Please enter your password.");
        return false;
    }

    if (!isValidEmail(email.trim())) {
        showError("Please enter a valid email address.");
        return false;
    }

    return true;
}

function getDataForLogin() {
    document.getElementById("login-form").addEventListener("submit", function (e) {
        e.preventDefault();
        const form = e.target;

        const email = form.querySelector('input[name="email"]').value;
        const password = form.querySelector('input[name="password"]').value;

        console.log("Form values - Email:", email, "Password:", password ? "[HIDDEN]" : "empty");

        if (!validateForm(email, password)) {
            return;
        }

        makeCall("POST", "LoginHandler", form, function (req) {
            if (req.readyState === XMLHttpRequest.DONE) {
                console.log("Response status:", req.status);
                console.log("Response text:", req.responseText);

                if (req.status === 200) {
                    try {
                        const res = JSON.parse(req.responseText);
                        sessionStorage.setItem("user", JSON.stringify(res));

                        if (res.ruolo === "docente") {
                            window.location.href = "professorHome.html";
                        } else if (res.ruolo === "studente") {
                            window.location.href = "studentHome.html";
                        } else {
                            showError("Ruolo non riconosciuto.");
                        }
                    } catch (err) {
                        console.error("JSON parse error:", err);
                        showError("Errore nella risposta del server.");
                    }
                } else {
                    try {
                        const res = JSON.parse(req.responseText);
                        showError(res.error);
                    } catch {
                        showError("Errore durante il login.");
                    }
                }
            }
        }, false);
    });
}

function redirectIfLoggedIn() {
    const userData = sessionStorage.getItem("user");
    if (userData) {
        try {
            const user = JSON.parse(userData);
            if (user.ruolo === "docente") {
                window.location.href = "professorHome.html";
                return true;
            } else if (user.ruolo === "studente") {
                window.location.href = "studentHome.html";
                return true;
            }
        } catch {
           return false;
        }
    }
    return false;
}

document.addEventListener("DOMContentLoaded", function() {
    if (!redirectIfLoggedIn()) {
        getDataForLogin();
    }
});

// Handle back/forward navigation from bfcache (pageshow event)
window.addEventListener('pageshow', function(event) {
    if (event.persisted) {
        // Page loaded from back/forward cache, reload to re-run login check
        window.location.reload();
    }
});
