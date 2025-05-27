import { makeCall } from "./utils.js";

function loadSessionInfo() {
    makeCall("GET", "session-info", null, function (req) {
        if (req.readyState === XMLHttpRequest.DONE) {
            const emailSpan = document.getElementById("user-email");
            const roleSpan = document.getElementById("user-role");

            if (req.status === 200) {
                const user = JSON.parse(req.responseText);
                emailSpan.textContent = "Email: " + user.email;
                roleSpan.textContent = "Role: " + user.ruolo;
            } else {
                emailSpan.textContent = "Email: unknown";
                roleSpan.textContent = "Role: unknown";
            }
        }
    });
}

function loadCourses() {
    makeCall("GET", "professor-home", null, function (req) {
        if (req.readyState === XMLHttpRequest.DONE) {
            const container = document.getElementById("courses-container");
            const emptyContainer = document.getElementById("no-courses-container");

            if (req.status === 200) {
                const corsi = JSON.parse(req.responseText);

                if (corsi.length > 0) {
                    container.innerHTML = `
            <p>As a professor, you can manage your courses and students here. Select one of your courses below.</p>
            <p>Total Courses Held: ${corsi.length}</p>
            <div class="course-list">
              ${corsi.map(corso => `
                <div class="course-item">
                  <span class="course-name">${corso.nome}</span>
                  <a href="Appelli?id=${corso.idCorso}" class="select-button">Select</a>
                </div>
              `).join('')}
            </div>
          `;
                    container.style.display = "block";
                    emptyContainer.style.display = "none";
                } else {
                    container.style.display = "none";
                    emptyContainer.style.display = "block";
                }
            } else {
                container.innerHTML = "<p>Errore nel caricamento dei corsi.</p>";
                container.style.display = "block";
                emptyContainer.style.display = "none";
            }
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    loadSessionInfo();
    loadCourses();
});
