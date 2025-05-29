import { makeCall } from "./utils.js";

// View management
const mainTitle = document.getElementById("main-title");

const homeView = document.getElementById("home-view");
const verbaliView = document.getElementById("verbali-view");
const appelliView = document.getElementById("appelli-view");

const verbaliLink = document.getElementById("verbali-link");
const backToHomeNav = document.getElementById("back-to-home-nav");

function showHomeView() {
    homeView.classList.remove("hidden");
    verbaliView.classList.add("hidden");
    appelliView.classList.add("hidden");
    verbaliLink.classList.remove("hidden");
    backToHomeNav.classList.add("hidden");
    mainTitle.textContent = "Welcome to the Professor's Home Page";
}

function showVerbaliView() {
    homeView.classList.add("hidden");
    verbaliView.classList.remove("hidden");
    appelliView.classList.add("hidden");
    verbaliLink.classList.add("hidden");
    backToHomeNav.classList.remove("hidden");
    mainTitle.textContent = "Verbali degli Studenti";
}

function showAppelliView() {
    homeView.classList.add("hidden");
    verbaliView.classList.add("hidden");
    appelliView.classList.remove("hidden");
    verbaliLink.classList.add("hidden");
    backToHomeNav.classList.remove("hidden");
    mainTitle.textContent = "Seleziona un Appello";
}

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
                //not logged in, or something else generated error, direct accessed via link
                emailSpan.textContent = "Email: unknown";
                roleSpan.textContent = "Role: unknown";
                window.location.href = "login.html";
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
                                    <a href="#" data-corso-id="${corso.idCorso}" data-corso-name="${corso.nome}" class="select-button appelli-link">Select</a>
                                </div>
                            `).join('')}
                        </div>
                    `;
                    container.style.display = "block";
                    emptyContainer.style.display = "none";

                    // Add event listeners to the new appelli links
                    addAppelliEventListeners();
                } else {
                    container.style.display = "none";
                    emptyContainer.style.display = "block";
                }
            } else {
                window.location.href = "login.html";
            }
        }
    });
}

function loadAppelli(corsoId, corsoName) {
    makeCall("GET", "appelli-data?id=" + corsoId, null, function (req) {
        if (req.readyState === XMLHttpRequest.DONE) {
            const appelliContent = document.getElementById("appelli-content");
            const appelliTotal = document.getElementById("appelli-total");
            const corsoIdDisplay = document.getElementById("corso-id-display");

            if (req.status === 200) {
                const appelli = JSON.parse(req.responseText);
                appelliTotal.textContent = appelli.length;
                corsoIdDisplay.textContent = corsoId;

                if (appelli.length > 0) {
                    appelliContent.innerHTML = appelli.map(appello => `
                        <div class="appello-item">
                            <span class="appello-date">${formatDate(appello.dataAppello)}</span>
                            <a href="IscrittiAppello?idAppello=${appello.idAppello}&idCorso=${corsoId}" 
                               class="select-button">Seleziona</a>
                        </div>
                    `).join('');
                } else {
                    appelliContent.innerHTML = "<p>Nessun appello disponibile per questo corso.</p>";
                }
            } else {
                appelliContent.innerHTML = "<p>Errore nel caricamento degli appelli.</p>";
            }
        }
    });
}

function addAppelliEventListeners() {
    // Add event listeners to course select buttons
    document.querySelectorAll('.appelli-link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const corsoId = this.getAttribute('data-corso-id');
            const corsoName = this.getAttribute('data-corso-name');
            showAppelliView();
            loadAppelli(corsoId, corsoName);
        });
    });
}

function loadVerbali(){
    makeCall("GET", "Verbali", null, function (req) {
        if (req.readyState === XMLHttpRequest.DONE) {
            const verbaliContent = document.getElementById("verbali-content");
            const verbaliCount = document.getElementById("verbali-count");

            if (req.status === 200) {
                const verbali = JSON.parse(req.responseText);
                verbaliCount.textContent = `Total Verbals created: ${verbali.length}`;

                if (verbali.length > 0) {
                    verbaliContent.innerHTML = `
                        <table class="verbali-table">
                            <thead>
                                <tr>
                                    <th>Codice Verbale</th>
                                    <th>Data Creazione Verbale</th>
                                    <th>Corso</th>
                                    <th>Data Appello</th>
                                    <th>Azione</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${verbali.map(entry => `
                                    <tr>
                                        <td>${entry.codiceVerbale}</td>
                                        <td>${formatDateTime(entry.dataCreazione)}</td>
                                        <td>${entry.nomeCorso}</td>
                                        <td>${formatDate(entry.dataAppello)}</td>
                                        <td>
                                            <a href="DettaglioVerbale?codice=${entry.codiceVerbale}" 
                                               class="verbale-link">
                                                Visualizza Dettagli
                                            </a>
                                        </td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    `;
                } else {
                    verbaliContent.innerHTML = "<p>Nessun verbale disponibile.</p>";
                }
            } else {
                verbaliContent.innerHTML = "<p>Errore nel caricamento dei verbali.</p>";
            }
        }
    });
}



// Utility functions for date formatting
function formatDateTime(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleString('it-IT', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('it-IT', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    });
}

// Event listeners
document.addEventListener("DOMContentLoaded", () => {
    loadSessionInfo();
    loadCourses();

    // Verbali link click handler
    document.getElementById("verbali-link").addEventListener("click", function(e) {
        e.preventDefault();
        showVerbaliView();
        loadVerbali();
    });

    // Back to home button
    document.getElementById("back-to-home-nav").addEventListener("click", function(e) {
        e.preventDefault();
        showHomeView();
    });
});

// Logout handler
document.querySelector("a[href='LogoutHandler']").addEventListener("click", function (e) {
    e.preventDefault();

    fetch("LogoutHandler", {
        method: "GET",
        credentials: "include"
    }).then(() => {
        sessionStorage.removeItem("user");
        window.location.href = "login.html";
    }).catch(() => {
        alert("Logout failed");
    });
});