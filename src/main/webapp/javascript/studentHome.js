import { makeCall } from "./utils.js";

// View management
const mainTitle = document.getElementById("main-title");

const homeView = document.getElementById("home-view");
const appelliView = document.getElementById("appelli-view");
const esitoView = document.getElementById("esito-view");

const backToHomeNav = document.getElementById("back-to-home-nav");

// Global variables to track current state
let currentCorsoId = null;
let currentCorsoName = null;

function showHomeView() {
    homeView.classList.remove("hidden");
    appelliView.classList.add("hidden");
    esitoView.classList.add("hidden");
    backToHomeNav.classList.add("hidden");
    mainTitle.textContent = "Welcome to the Student's Home Page";
}

function showAppelliView() {
    homeView.classList.add("hidden");
    appelliView.classList.remove("hidden");
    esitoView.classList.add("hidden");
    backToHomeNav.classList.remove("hidden");
    mainTitle.textContent = "Exam Sessions";
}

function showEsitoView() {
    homeView.classList.add("hidden");
    appelliView.classList.add("hidden");
    esitoView.classList.remove("hidden");
    backToHomeNav.classList.remove("hidden");
    mainTitle.textContent = "Exam Result";
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
                emailSpan.textContent = "Email: unknown";
                roleSpan.textContent = "Role: unknown";
                window.location.href = "login.html";
            }
        }
    });
}

function loadCourses() {
    makeCall("GET", "student-home", null, function (req) {
        if (req.readyState === XMLHttpRequest.DONE) {
            const container = document.getElementById("courses-container");
            const emptyContainer = document.getElementById("no-courses-container");

            if (req.status === 200) {
                const corsi = JSON.parse(req.responseText);

                if (corsi.length > 0) {
                    container.innerHTML = `
                        <p>As a student, you can access your courses, grades, and more. Use the navigation above to explore.</p>
                        <p>Total Courses Enrolled: ${corsi.length}</p>
                        <div class="course-list">
                            ${corsi.map(corso => `
                                <div class="course-item">
                                    <span class="course-name">${corso.nome}</span>
                                    <a href="#" data-corso-id="${corso.idCorso}" 
                                       data-corso-name="${corso.nome}" 
                                       class="select-button appelli-link">Select</a>
                                </div>
                            `).join('')}
                        </div>
                    `;
                    container.style.display = "block";
                    emptyContainer.style.display = "none";

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
    currentCorsoId = corsoId;
    currentCorsoName = corsoName;

    document.getElementById("course-name-title").textContent = corsoName;

    makeCall("GET", "AppelliStudente?id=" + corsoId, null, function (req) {
        if (req.readyState === XMLHttpRequest.DONE) {
            const appelliContent = document.getElementById("appelli-content");
            const appelliTotal = document.getElementById("appelli-total");

            if (req.status === 200) {
                const appelli = JSON.parse(req.responseText);
                appelliTotal.textContent = appelli.length;

                if (appelli.length > 0) {
                    appelliContent.innerHTML = appelli.map(appello => `
                        <div class="appello-item">
                            <span class="appello-date">${formatDate(appello.dataAppello)}</span>
                            <a href="#" data-appello-id="${appello.idAppello}" 
                               data-corso-id="${corsoId}" 
                               class="select-button esito-link">View Result</a>
                        </div>
                    `).join('');

                    addEsitoEventListeners();
                } else {
                    appelliContent.innerHTML = "<p>No exam sessions available for this course.</p>";
                }
            } else {
                appelliContent.innerHTML = "<p>Error loading exam sessions.</p>";
            }
        }
    });
}

function addAppelliEventListeners() {
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

function addEsitoEventListeners() {
    document.querySelectorAll('.esito-link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const appelloId = this.getAttribute('data-appello-id');
            const corsoId = this.getAttribute('data-corso-id');
            loadEsito(appelloId, corsoId);
        });
    });
}

function loadEsito(appelloId, corsoId) {
    makeCall("GET", `GestioneVotiStudente?idAppello=${appelloId}&idCorso=${corsoId}`, null, function(req) {
        if (req.readyState === XMLHttpRequest.DONE) {
            const esitoContent = document.getElementById("esito-content");

            if (req.status === 200) {
                try {
                    const data = JSON.parse(req.responseText);

                    if (!data.iscrizionePresente) {
                        esitoContent.innerHTML = "<p>If you cannot see any call details, you might not be enrolled in the call.</p>";
                        showEsitoView();
                        return;
                    }

                    const iscrizione = data.iscrizione;
                    const appello = data.appello;
                    const corso = data.corso;
                    const studente = data.studente;
                    const votiValidi = data.votiValidi;

                    // Check if vote is published, refused or verbalized
                    if (iscrizione.statoValutazione === 'Pubblicato' ||
                        iscrizione.statoValutazione === 'Rifiutato' ||
                        iscrizione.statoValutazione === 'Verbalizzato') {

                        let tableHTML = `
                            <table>
                                <thead>
                                    <tr>
                                        <th>ID Studente</th>
                                        <th>ID Appello</th>
                                        <th>Voto</th>
                                        <th>Stato Valutazione</th>
                                        ${iscrizione.statoValutazione === 'Rifiutato' ? '<th>Note</th>' : ''}
                                        <th>Data Appello</th>
                                        <th>Nome Corso</th>
                                        <th>Matricola</th>
                                        <th>Nome</th>
                                        <th>Cognome</th>
                                        <th>Corso di Laurea</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr id="esito-row" 
                                        ${iscrizione.statoValutazione === 'Pubblicato' && votiValidi.includes(iscrizione.voto) ?
                            'class="draggable-row" draggable="true"' : ''}>
                                        <td>${iscrizione.idStudente}</td>
                                        <td>${iscrizione.idAppello}</td>
                                        <td class="${(iscrizione.voto === 'Rimandato' || iscrizione.voto === 'Riprovato') ? 'red-text' : ''}">
                                            ${iscrizione.voto}
                                        </td>
                                        <td class="${iscrizione.statoValutazione === 'Rifiutato' ? 'red-text' : ''}">
                                            ${iscrizione.statoValutazione}
                                        </td>
                                        ${iscrizione.statoValutazione === 'Rifiutato' ? '<td>Il voto è stato rifiutato</td>' : ''}
                                        <td>${formatDate(appello.dataAppello)}</td>
                                        <td>${corso.nome}</td>
                                        <td>${studente.matricola}</td>
                                        <td>${studente.nome}</td>
                                        <td>${studente.cognome}</td>
                                        <td>${studente.corsoDiLaurea}</td>
                                    </tr>
                                </tbody>
                            </table>
                        `;

                        esitoContent.innerHTML = tableHTML;

                        // Show trash if vote can be refused
                        if (iscrizione.statoValutazione === 'Pubblicato' && votiValidi.includes(iscrizione.voto)) {
                            document.getElementById('trash-container').style.display = 'block';
                            setupDragAndDrop(iscrizione.idStudente, iscrizione.idAppello, data.idCorso);
                        } else {
                            document.getElementById('trash-container').style.display = 'none';
                        }

                    } else {
                        // Vote not yet defined
                        esitoContent.innerHTML = "<p>Voto ancora non definito.</p>";
                        document.getElementById('trash-container').style.display = 'none';
                    }

                    showEsitoView();
                } catch (error) {
                    console.error('Error parsing JSON:', error);
                    esitoContent.innerHTML = "<p>Error loading exam result.</p>";
                }
            } else {
                esitoContent.innerHTML = "<p>Error loading exam result.</p>";
            }
        }
    });
}

// Global variables for drag and drop
let draggedData = null;

function setupDragAndDrop(idStudente, idAppello, idCorso) {
    const draggableRow = document.getElementById('esito-row');
    const trashIcon = document.getElementById('trash-icon');
    const trashContainer = document.getElementById('trash-container');

    draggableRow.addEventListener('dragstart', function(e) {
        e.target.classList.add('dragging');
        draggedData = {
            idStudente: idStudente,
            idAppello: idAppello,
            idCorso: idCorso
        };
    });

    draggableRow.addEventListener('dragend', function(e) {
        e.target.classList.remove('dragging');
    });

    trashContainer.addEventListener('dragover', function(e) {
        e.preventDefault();
        trashIcon.classList.add('drag-over');
    });

    trashContainer.addEventListener('dragleave', function(e) {
        trashIcon.classList.remove('drag-over');
    });

    trashContainer.addEventListener('drop', function(e) {
        e.preventDefault();
        trashIcon.classList.remove('drag-over');

        if (draggedData) {
            // Show confirmation modal
            document.getElementById('confirm-modal').style.display = 'block';
        }
    });
}

// Modal functions
window.cancelReject = function() {
    document.getElementById('confirm-modal').style.display = 'none';
    draggedData = null;
};

window.confirmReject = function() {
    if (!draggedData) return;

    const tempForm = document.createElement('form');

    const inputStudente = document.createElement('input');
    inputStudente.type = 'hidden';
    inputStudente.name = 'idStudente';
    inputStudente.value = draggedData.idStudente;
    tempForm.appendChild(inputStudente);

    const inputAppello = document.createElement('input');
    inputAppello.type = 'hidden';
    inputAppello.name = 'idAppello';
    inputAppello.value = draggedData.idAppello;
    tempForm.appendChild(inputAppello);

    const inputCorso = document.createElement('input');
    inputCorso.type = 'hidden';
    inputCorso.name = 'idCorso';
    inputCorso.value = draggedData.idCorso;
    tempForm.appendChild(inputCorso);

    makeCall("POST", "RifiutaVoto", tempForm, function(req) {
        if (req.readyState === XMLHttpRequest.DONE) {
            document.getElementById('confirm-modal').style.display = 'none';

            if (req.status === 200) {
                try {
                    const data = JSON.parse(req.responseText);
                    alert(data.message || 'Voto rifiutato con successo');
                    // Reload esito to show updated status
                    loadEsito(draggedData.idAppello, draggedData.idCorso);
                } catch (error) {
                    console.error('Error parsing JSON:', error);
                    alert('Errore nel rifiuto del voto');
                }
            } else {
                try {
                    const errorData = JSON.parse(req.responseText);
                    alert(errorData.error || 'Errore nel rifiuto del voto');
                } catch (e) {
                    alert('Errore nel rifiuto del voto');
                }
            }
            draggedData = null;
        }
    }, false);
};

// Utility function for date formatting
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

    // Back to home button
    document.getElementById("back-to-home-nav").addEventListener("click", function(e) {
        e.preventDefault();

        // If we're in esito view, go back to appelli
        if (!esitoView.classList.contains("hidden")) {
            if (currentCorsoId && currentCorsoName) {
                showAppelliView();
                loadAppelli(currentCorsoId, currentCorsoName);
            } else {
                showHomeView();
            }
        } else {
            // Otherwise go to home
            showHomeView();
        }
    });
});

// Logout handler
document.querySelector("a[href='LogoutHandler']").addEventListener("click", function (e) {
    e.preventDefault();

    fetch("LogoutHandler", {
        method: "GET",
        credentials: "include"
    }).then(() => {
        currentCorsoId = null;
        currentCorsoName = null;

        sessionStorage.removeItem("user");
        window.location.href = "login.html";
    }).catch(() => {
        alert("Logout failed");
    });
});