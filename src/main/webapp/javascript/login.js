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
    // Simple email validation regex
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailPattern.test(email);
}

function validateForm(email, password) {
    clearError();

    // Check for null/empty values
    if (!email || email.trim() === "") {
        showError("Please enter your email address.");
        return false;
    }

    if (!password || password.trim() === "") {
        showError("Please enter your password.");
        return false;
    }

    // Check email format
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

        // Get form values
        const email = form.querySelector('input[name="email"]').value;
        const password = form.querySelector('input[name="password"]').value;

        console.log("Form values - Email:", email, "Password:", password ? "[HIDDEN]" : "empty");

        // Validate form
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
                        if (res.ruolo === "docente") {
                            window.location.href = "professorHome.html";
                        } else if (res.ruolo === "studente") {
                            window.location.href = "studentHome.html";
                        } else {
                            showError("Ruolo non riconosciuto.");
                        }
                        sessionStorage.setItem("user", JSON.stringify(res));
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

// Call the function after DOM is loaded
document.addEventListener("DOMContentLoaded", function() {
    getDataForLogin();
});