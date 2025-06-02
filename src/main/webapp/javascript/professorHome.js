import { makeCall } from "./utils.js";

// View management
const mainTitle = document.getElementById("main-title");

const homeView = document.getElementById("home-view");
const verbaliView = document.getElementById("verbali-view");
const appelliView = document.getElementById("appelli-view");
const iscrittiView = document.getElementById("iscritti-view");
const modificaVotoView = document.getElementById("modifica-voto-view");
const dettaglioVerbaleView = document.getElementById("dettaglio-verbale-view");

const verbaliLink = document.getElementById("verbali-link");
const backToHomeNav = document.getElementById("back-to-home-nav");

// Global variables to track current state
let currentCorsoId = null;
let currentCorsoName = null;
let currentAppelloId = null;
let currentSortField = "cognome";
let currentSortDir = "asc";

// Variabile globale per memorizzare i dati degli iscritti
let iscrittiData = [];

// IMPROVED MODAL STATE MANAGEMENT
let modalState = {
    isOpen: false,
    currentView: null,
    originalBodyOverflow: null
};

// Enhanced view switching functions with modal handling
function showDettaglioVerbaleView() {
    if (modalState.isOpen) {
        closeInserimentoMultiplo();
    }
    homeView.classList.add("hidden");
    verbaliView.classList.add("hidden");
    appelliView.classList.add("hidden");
    iscrittiView.classList.add("hidden");
    modificaVotoView.classList.add("hidden");
    dettaglioVerbaleView.classList.remove("hidden");
    verbaliLink.classList.add("hidden");
    backToHomeNav.classList.remove("hidden");
    mainTitle.textContent = "Dettaglio Verbale";
    hideActionButtons();
}

function showModificaVotoView() {
    if (modalState.isOpen) {
        closeInserimentoMultiplo();
    }
    homeView.classList.add("hidden");
    verbaliView.classList.add("hidden");
    appelliView.classList.add("hidden");
    iscrittiView.classList.add("hidden");
    modificaVotoView.classList.remove("hidden");
    dettaglioVerbaleView.classList.add("hidden");
    verbaliLink.classList.add("hidden");
    backToHomeNav.classList.remove("hidden");
    mainTitle.textContent = "Modifica Voto Studente";
    hideActionButtons();
}

function showHomeView() {
    if (modalState.isOpen) {
        closeInserimentoMultiplo();
    }
    homeView.classList.remove("hidden");
    verbaliView.classList.add("hidden");
    appelliView.classList.add("hidden");
    iscrittiView.classList.add("hidden");
    modificaVotoView.classList.add("hidden");
    dettaglioVerbaleView.classList.add("hidden");
    verbaliLink.classList.remove("hidden");
    backToHomeNav.classList.add("hidden");
    mainTitle.textContent = "Welcome to the Professor's Home Page";
    hideActionButtons();
}

function showVerbaliView() {
    if (modalState.isOpen) {
        closeInserimentoMultiplo();
    }
    homeView.classList.add("hidden");
    verbaliView.classList.remove("hidden");
    appelliView.classList.add("hidden");
    iscrittiView.classList.add("hidden");
    modificaVotoView.classList.add("hidden");
    dettaglioVerbaleView.classList.add("hidden");
    verbaliLink.classList.add("hidden");
    backToHomeNav.classList.remove("hidden");
    mainTitle.textContent = "Verbali degli Studenti";
    hideActionButtons();
}

function showAppelliView() {
    if (modalState.isOpen) {
        closeInserimentoMultiplo();
    }
    homeView.classList.add("hidden");
    verbaliView.classList.add("hidden");
    appelliView.classList.remove("hidden");
    iscrittiView.classList.add("hidden");
    modificaVotoView.classList.add("hidden");
    dettaglioVerbaleView.classList.add("hidden");
    verbaliLink.classList.add("hidden");
    backToHomeNav.classList.remove("hidden");
    mainTitle.textContent = "Seleziona un Appello";
    hideActionButtons();
}

function showIscrittiView() {
    if (modalState.isOpen) {
        closeInserimentoMultiplo();
    }
    homeView.classList.add("hidden");
    verbaliView.classList.add("hidden");
    appelliView.classList.add("hidden");
    iscrittiView.classList.remove("hidden");
    modificaVotoView.classList.add("hidden");
    dettaglioVerbaleView.classList.add("hidden");
    verbaliLink.classList.add("hidden");
    backToHomeNav.classList.remove("hidden");
    mainTitle.textContent = "Studenti Iscritti all'Appello";
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

    makeCall("GET", "Appelli?id=" + corsoId, null, function (req) {
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
                            <a href="#" data-appello-id="${appello.idAppello}" data-corso-id="${corsoId}" 
                               class="select-button iscritti-link">Seleziona</a>
                        </div>
                    `).join('');

                    addIscrittiEventListeners();
                } else {
                    appelliContent.innerHTML = "<p>Nessun appello disponibile per questo corso.</p>";
                }
            } else {
                appelliContent.innerHTML = "<p>Errore nel caricamento degli appelli.</p>";
            }
        }
    });
}

// Funzione per convertire i voti in priorità per l'ordinamento
function votoToPriority(voto) {
    if (!voto || voto.trim() === '') return 0;

    switch (voto.toLowerCase()) {
        case "assente": return 1;
        case "rimandato": return 2;
        case "riprovato": return 3;
        case "30 e lode": return 17;
        default:
            const val = parseInt(voto);
            if (!isNaN(val) && val >= 18 && val <= 30) {
                return 3 + (val - 17); // 18 → 4, ..., 30 → 16
            }
            return Number.MAX_SAFE_INTEGER; // Valore sconosciuto
    }
}

// Funzione di ordinamento
function sortIscritti(data, field, direction) {
    return data.sort((a, b) => {
        let valA, valB;

        if (field === 'voto') {
            // Ordinamento semantico per voti
            valA = votoToPriority(a.voto);
            valB = votoToPriority(b.voto);
        } else {
            // Ordinamento normale per altri campi
            valA = a[field] || '';
            valB = b[field] || '';

            // Converti in lowercase per ordinamento case-insensitive
            if (typeof valA === 'string') valA = valA.toLowerCase();
            if (typeof valB === 'string') valB = valB.toLowerCase();
        }

        // Confronto
        if (valA < valB) return direction === 'asc' ? -1 : 1;
        if (valA > valB) return direction === 'asc' ? 1 : -1;
        return 0;
    });
}

function loadIscritti(appelloId, corsoId, sortField = "cognome", sortDir = "asc") {
    currentAppelloId = appelloId;
    currentCorsoId = corsoId;
    currentSortField = sortField;
    currentSortDir = sortDir;

    // URL senza parametri di ordinamento
    const url = `IscrittiAppello?idAppello=${appelloId}&idCorso=${corsoId}`;

    makeCall("GET", url, null, function (req) {
        if (req.readyState === XMLHttpRequest.DONE) {
            if (req.status === 200) {
                try {
                    const data = JSON.parse(req.responseText);

                    // Salva i dati originali
                    iscrittiData = data.iscritti;

                    // Ordina i dati lato client con ordinamento iniziale per cognome crescente
                    const sortedIscritti = sortIscritti([...iscrittiData], sortField, sortDir);

                    updateIscrittiView(
                        sortedIscritti,
                        data.idAppello,
                        data.idCorso,
                        data.callDate,
                        sortField,
                        sortDir,
                        data.numberOfstudentHasInserito,
                        data.numberOfstudentHasPubblicatoAndRifiutato
                    );
                } catch (error) {
                    console.error('Error parsing JSON:', error);
                    document.getElementById("iscritti-content").innerHTML = "<p>Errore nel caricamento degli iscritti.</p>";
                }
            } else {
                document.getElementById("iscritti-content").innerHTML = "<p>Errore nel caricamento degli iscritti.</p>";
            }
        }
    });
}

function updateIscrittiView(iscritti, appelloId, corsoId, callDate, sortField, sortDir, countInserito, countPubblicatoRifiutato) {
    // Update debug info
    document.getElementById("iscritti-appello-id").textContent = appelloId;
    document.getElementById("iscritti-call-date").textContent = "Call Date: " + callDate;
    document.getElementById("iscritti-count").textContent = "Number Of Students in the Call: " + iscritti.length;

    // Update action buttons (enhanced version)
    updateActionButtons(appelloId, corsoId, countInserito, countPubblicatoRifiutato);

    // Update iscritti table
    updateIscrittiTable(iscritti, sortField, sortDir);
}

// Funzione separata per aggiornare solo la tabella
function updateIscrittiTable(iscritti, sortField, sortDir) {
    const iscrittiContent = document.getElementById("iscritti-content");

    if (iscritti.length > 0) {
        iscrittiContent.innerHTML = `
            <table class="iscritti-table">
                <thead>
                    <tr>
                        <th>id_studente</th>
                        <th>
                            <a href="#" data-sort-field="matricola" data-sort-dir="${sortField === 'matricola' && sortDir === 'asc' ? 'desc' : 'asc'}">
                                Matricola
                            </a>
                            <span>${sortField === 'matricola' ? (sortDir === 'asc' ? '↑' : '↓') : ''}</span>
                        </th>
                        <th>
                            <a href="#" data-sort-field="cognome" data-sort-dir="${sortField === 'cognome' && sortDir === 'asc' ? 'desc' : 'asc'}">
                                Cognome
                            </a>
                            <span>${sortField === 'cognome' ? (sortDir === 'asc' ? '↑' : '↓') : ''}</span>
                        </th>
                        <th>
                            <a href="#" data-sort-field="nome" data-sort-dir="${sortField === 'nome' && sortDir === 'asc' ? 'desc' : 'asc'}">
                                Nome
                            </a>
                            <span>${sortField === 'nome' ? (sortDir === 'asc' ? '↑' : '↓') : ''}</span>
                        </th>
                        <th>
                            <a href="#" data-sort-field="email" data-sort-dir="${sortField === 'email' && sortDir === 'asc' ? 'desc' : 'asc'}">
                                Email
                            </a>
                            <span>${sortField === 'email' ? (sortDir === 'asc' ? '↑' : '↓') : ''}</span>
                        </th>
                        <th>
                            <a href="#" data-sort-field="corsoDiLaurea" data-sort-dir="${sortField === 'corsoDiLaurea' && sortDir === 'asc' ? 'desc' : 'asc'}">
                                Corso di Laurea
                            </a>
                            <span>${sortField === 'corsoDiLaurea' ? (sortDir === 'asc' ? '↑' : '↓') : ''}</span>
                        </th>
                        <th>
                            <a href="#" data-sort-field="voto" data-sort-dir="${sortField === 'voto' && sortDir === 'asc' ? 'desc' : 'asc'}">
                                Voto
                            </a>
                            <span>${sortField === 'voto' ? (sortDir === 'asc' ? '↑' : '↓') : ''}</span>
                        </th>
                        <th>
                            <a href="#" data-sort-field="stato_valutazione" data-sort-dir="${sortField === 'stato_valutazione' && sortDir === 'asc' ? 'desc' : 'asc'}">
                                Stato Valutazione
                            </a>
                            <span>${sortField === 'stato_valutazione' ? (sortDir === 'asc' ? '↑' : '↓') : ''}</span>
                        </th>
                        <th>Azione</th>
                    </tr>
                </thead>
                <tbody>
                    ${iscritti.map(studente => `
                        <tr>
                            <td>${studente.idUtente}</td>
                            <td>${studente.matricola}</td>
                            <td>${studente.cognome}</td>
                            <td>${studente.nome}</td>
                            <td>${studente.email}</td>
                            <td>${studente.corsoDiLaurea}</td>
                            <td class="${(studente.voto === 'Rimandato' || studente.voto === 'Riprovato') ? 'red-text' : ''}">
                                ${studente.voto || ''}
                            </td>
                            <td class="${studente.stato_valutazione === 'Rifiutato' ? 'red-text' : ''}">
                                ${studente.stato_valutazione || ''}
                            </td>
                            <td>
                                ${(studente.stato_valutazione === 'Non inserito' || studente.stato_valutazione === 'Inserito') ? `
                                    <button class="action-button" 
                                            data-student-id="${studente.idUtente}" 
                                            data-appello-id="${currentAppelloId}" 
                                            data-corso-id="${currentCorsoId}">
                                        MODIFICA
                                    </button>
                                ` : ''}
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;

        addSortEventListeners();
        addModifyEventListeners();
    } else {
        iscrittiContent.innerHTML = "<p>Nessun iscritto trovato per questo appello.</p>";
    }
}

// ENHANCED updateActionButtons function with modal support
function updateActionButtons(appelloId, corsoId, countInserito, countPubblicatoRifiutato) {
    const actionsContainer = document.getElementById("nav-actions");
    let buttonsHTML = '';

    // Calculate number of non-inserted students
    const countNonInserito = iscrittiData.filter(s => s.stato_valutazione === 'Non inserito').length;

    if (countNonInserito > 0 && canOpenModal()) {
        buttonsHTML += `
            <button class="btn-multiplo" onclick="openInserimentoMultiplo()">
                Inserimento Multiplo (${countNonInserito})
            </button>
        `;
    }

    if (countInserito > 0) {
        buttonsHTML += `
            <button class="action-button pubblica" data-action="pubblica" 
                    data-appello-id="${appelloId}" data-corso-id="${corsoId}">
                Pubblica
            </button>
        `;
    }

    if (countPubblicatoRifiutato > 0) {
        buttonsHTML += `
            <button class="action-button verbalizza" data-action="verbalizza" 
                    data-appello-id="${appelloId}" data-corso-id="${corsoId}">
                Verbalizza
            </button>
        `;
    }

    if (actionsContainer) {
        actionsContainer.innerHTML = buttonsHTML;
        addActionButtonEventListeners();
    }
}

function hideActionButtons() {
    const actionsContainer = document.getElementById("nav-actions");
    if (actionsContainer) {
        actionsContainer.innerHTML = '';
    }
}

// Utility function to check if modal should be accessible
function canOpenModal() {
    // Check if we're in the right view
    if (iscrittiView && iscrittiView.classList.contains('hidden')) {
        return false;
    }

    // Check if there are students to insert
    if (!iscrittiData || !Array.isArray(iscrittiData)) {
        return false;
    }

    const studentiNonInseriti = iscrittiData.filter(s => s.stato_valutazione === 'Non inserito');
    return studentiNonInseriti.length > 0;
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

function addIscrittiEventListeners() {
    document.querySelectorAll('.iscritti-link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const appelloId = this.getAttribute('data-appello-id');
            const corsoId = this.getAttribute('data-corso-id');
            showIscrittiView();
            loadIscritti(appelloId, corsoId);
        });
    });
}

function addSortEventListeners() {
    document.querySelectorAll('.iscritti-table th a').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const sortField = this.getAttribute('data-sort-field');
            const sortDir = this.getAttribute('data-sort-dir');

            if (sortField && iscrittiData.length > 0) {
                // Ordina i dati già caricati invece di fare una nuova chiamata
                currentSortField = sortField;
                currentSortDir = sortDir;

                const sortedIscritti = sortIscritti([...iscrittiData], sortField, sortDir);

                // Aggiorna solo la tabella senza ricaricare i dati
                updateIscrittiTable(sortedIscritti, sortField, sortDir);
            }
        });
    });
}

function addModifyEventListeners() {
    document.querySelectorAll('.action-button[data-student-id]').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const studentId = this.getAttribute('data-student-id');
            const appelloId = this.getAttribute('data-appello-id');
            const corsoId = this.getAttribute('data-corso-id');

            console.log('Modifica clicked - studentId:', studentId, 'appelloId:', appelloId, 'corsoId:', corsoId);

            // Chiamata AJAX per ottenere i dati dello studente
            loadStudentForModification(studentId, appelloId, corsoId);
        });
    });
}

function loadStudentForModification(studentId, appelloId, corsoId) {
    // Crea un form temporaneo per makeCall
    const tempForm = document.createElement('form');

    const inputStudente = document.createElement('input');
    inputStudente.type = 'hidden';
    inputStudente.name = 'idStudente';
    inputStudente.value = studentId;
    tempForm.appendChild(inputStudente);

    const inputAppello = document.createElement('input');
    inputAppello.type = 'hidden';
    inputAppello.name = 'idAppello';
    inputAppello.value = appelloId;
    tempForm.appendChild(inputAppello);

    const inputCorso = document.createElement('input');
    inputCorso.type = 'hidden';
    inputCorso.name = 'idCorso';
    inputCorso.value = corsoId;
    tempForm.appendChild(inputCorso);

    console.log('Calling VisualizzaFormModificaVoto with:', {studentId, appelloId, corsoId});

    makeCall("POST", "VisualizzaFormModificaVoto", tempForm, function(req) {
        if (req.readyState === XMLHttpRequest.DONE) {
            console.log('Response status:', req.status);
            console.log('Response text:', req.responseText);

            if (req.status === 200) {
                try {
                    const data = JSON.parse(req.responseText);

                    // Popola il form con i dati dello studente
                    document.getElementById('modifica-matricola').textContent = data.studente.matricola;
                    document.getElementById('modifica-nome').textContent = data.studente.nome;
                    document.getElementById('modifica-cognome').textContent = data.studente.cognome;
                    document.getElementById('modifica-corso-laurea').textContent = data.studente.corsoDiLaurea;

                    document.getElementById('modifica-id-studente').value = data.studente.idUtente;
                    document.getElementById('modifica-id-appello').value = data.idAppello;
                    document.getElementById('modifica-id-corso').value = data.idCorso;

                    // Seleziona il voto attuale se presente
                    const votoSelect = document.getElementById('voto');
                    votoSelect.value = data.studente.voto || '';

                    // Mostra la view di modifica
                    showModificaVotoView();
                } catch (error) {
                    console.error('Errore parsing JSON:', error);
                    alert('Errore nel caricamento dei dati dello studente');
                }
            } else {
                try {
                    const errorData = JSON.parse(req.responseText);
                    alert(errorData.error || 'Errore nel caricamento dei dati dello studente');
                } catch (e) {
                    alert('Errore nel caricamento dei dati dello studente');
                }
            }
        }
    }, false); // false per non resettare il form
}

// Funzione per tornare alla lista iscritti
window.backToIscritti = function() {
    if (currentAppelloId && currentCorsoId) {
        showIscrittiView();
        // Ricarica la lista per mostrare eventuali modifiche
        loadIscritti(currentAppelloId, currentCorsoId, currentSortField, currentSortDir);
    }
};

function addActionButtonEventListeners() {
    document.querySelectorAll('.action-button[data-action]').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const action = this.getAttribute('data-action');
            const appelloId = this.getAttribute('data-appello-id');
            const corsoId = this.getAttribute('data-corso-id');

            if (action === 'pubblica') {
                // Gestione AJAX per pubblica
                handlePubblicaVoti(appelloId, corsoId);
            } else if (action === 'verbalizza') {
                // Gestione AJAX per verbalizza
                handleVerbalizzaIscritti(appelloId, corsoId);
            }
        });
    });
}

function handlePubblicaVoti(appelloId, corsoId) {
    // Conferma prima di pubblicare
    if (!confirm('Sei sicuro di voler pubblicare i voti? Gli studenti riceveranno una notifica via email.')) {
        return;
    }

    // Crea un form temporaneo per makeCall
    const tempForm = document.createElement('form');

    const inputAppello = document.createElement('input');
    inputAppello.type = 'hidden';
    inputAppello.name = 'idAppello';
    inputAppello.value = appelloId;
    tempForm.appendChild(inputAppello);

    const inputCorso = document.createElement('input');
    inputCorso.type = 'hidden';
    inputCorso.name = 'idCorso';
    inputCorso.value = corsoId;
    tempForm.appendChild(inputCorso);

    makeCall("POST", "PubblicaVoti", tempForm, function(req) {
        if (req.readyState === XMLHttpRequest.DONE) {
            if (req.status === 200) {
                try {
                    const data = JSON.parse(req.responseText);
                    alert(data.message || 'Voti pubblicati con successo');
                    // Ricarica la lista iscritti per aggiornare gli stati
                    loadIscritti(appelloId, corsoId, currentSortField, currentSortDir);
                } catch (error) {
                    console.error('Errore parsing JSON:', error);
                    alert('Errore nella pubblicazione dei voti');
                }
            } else {
                try {
                    const errorData = JSON.parse(req.responseText);
                    alert(errorData.error || 'Errore nella pubblicazione dei voti');
                } catch (e) {
                    alert('Errore nella pubblicazione dei voti');
                }
            }
        }
    }, false);
}

function handleVerbalizzaIscritti(appelloId, corsoId) {
    // Conferma prima di verbalizzare
    if (!confirm('Sei sicuro di voler verbalizzare gli iscritti? Verrà creato un verbale con tutti i voti pubblicati.')) {
        return;
    }

    // Crea un form temporaneo per makeCall
    const tempForm = document.createElement('form');

    const inputAppello = document.createElement('input');
    inputAppello.type = 'hidden';
    inputAppello.name = 'idAppello';
    inputAppello.value = appelloId;
    tempForm.appendChild(inputAppello);

    const inputCorso = document.createElement('input');
    inputCorso.type = 'hidden';
    inputCorso.name = 'idCorso';
    inputCorso.value = corsoId;
    tempForm.appendChild(inputCorso);

    makeCall("POST", "VerbalizzaIscritti", tempForm, function(req) {
        if (req.readyState === XMLHttpRequest.DONE) {
            if (req.status === 200) {
                try {
                    const data = JSON.parse(req.responseText);
                    alert(data.message || 'Verbalizzazione completata con successo');

                    // Mostra il dettaglio del verbale appena creato
                    if (data.idVerbale) {
                        loadDettaglioVerbale(data.idVerbale);
                    } else {
                        // Se non abbiamo l'id del verbale, ricarica la lista iscritti
                        loadIscritti(appelloId, corsoId, currentSortField, currentSortDir);
                    }
                } catch (error) {
                    console.error('Errore parsing JSON:', error);
                    alert('Errore nella verbalizzazione');
                }
            } else {
                try {
                    const errorData = JSON.parse(req.responseText);
                    alert(errorData.error || 'Errore nella verbalizzazione');
                } catch (e) {
                    alert('Errore nella verbalizzazione');
                }
            }
        }
    }, false);
}

function loadVerbali() {
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
                                            <a href="#" class="verbale-link" data-codice="${entry.codiceVerbale}">
                                                Visualizza Dettagli
                                            </a>
                                        </td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    `;

                    // Aggiungi event listeners per i link dei verbali
                    addVerbaliEventListeners();
                } else {
                    verbaliContent.innerHTML = "<p>Nessun verbale disponibile.</p>";
                }
            } else {
                verbaliContent.innerHTML = "<p>Errore nel caricamento dei verbali.</p>";
            }
        }
    });
}

function addVerbaliEventListeners() {
    document.querySelectorAll('a.verbale-link[data-codice]').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const codiceVerbale = this.getAttribute('data-codice');
            loadDettaglioVerbale(codiceVerbale);
        });
    });
}

function loadDettaglioVerbale(codiceVerbale) {
    makeCall("GET", `DettaglioVerbale?codice=${codiceVerbale}`, null, function(req) {
        if (req.readyState === XMLHttpRequest.DONE) {
            if (req.status === 200) {
                try {
                    const data = JSON.parse(req.responseText);

                    // Popola le informazioni del verbale
                    const verbaleInfo = document.getElementById('verbale-info');
                    verbaleInfo.innerHTML = `
                        <h4>Codice Verbale: ${data.verbaleInfo.codiceVerbale}</h4>
                        <p>Data Creazione: ${data.verbaleInfo.dataCreazione}</p>
                        <p>Data Appello: ${data.verbaleInfo.dataAppello}</p>
                        <p>Corso: ${data.verbaleInfo.nomeCorso}</p>
                    `;

                    // Aggiorna il totale studenti
                    document.getElementById('totale-studenti-verbale').textContent = `Totale studenti: ${data.totaleStudenti}`;

                    // Popola la tabella studenti
                    const dettaglioContent = document.getElementById('dettaglio-verbale-content');
                    if (data.studenti.length > 0) {
                        dettaglioContent.innerHTML = `
                            <table>
                                <thead>
                                    <tr>
                                        <th>Matricola</th>
                                        <th>Nome</th>
                                        <th>Cognome</th>
                                        <th>Voto</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    ${data.studenti.map(studente => `
                                        <tr>
                                            <td>${studente.matricola}</td>
                                            <td>${studente.nome}</td>
                                            <td>${studente.cognome}</td>
                                            <td class="${(studente.voto === 'Rimandato' || studente.voto === 'Riprovato') ? 'red-text' : ''}">
                                                ${studente.voto}
                                            </td>
                                        </tr>
                                    `).join('')}
                                </tbody>
                            </table>
                        `;
                    } else {
                        dettaglioContent.innerHTML = "<p>Nessun studente trovato in questo verbale.</p>";
                    }

                    // Mostra la view dettaglio verbale
                    showDettaglioVerbaleView();
                } catch (error) {
                    console.error('Errore parsing JSON:', error);
                    alert('Errore nel caricamento del dettaglio verbale');
                }
            } else {
                try {
                    const errorData = JSON.parse(req.responseText);
                    alert(errorData.error || 'Errore nel caricamento del dettaglio verbale');
                } catch (e) {
                    alert('Errore nel caricamento del dettaglio verbale');
                }
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

// ===========================
// IMPROVED MODAL FUNCTIONS
// ===========================

// Enhanced openInserimentoMultiplo function
window.openInserimentoMultiplo = function() {
    console.log('openInserimentoMultiplo called');

    // Prevent opening modal if not in iscritti view
    if (iscrittiView.classList.contains('hidden')) {
        console.log('Not in iscritti view, returning');
        return;
    }

    // Prevent body scrolling when modal is open
    modalState.originalBodyOverflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';

    const modal = document.getElementById('inserimento-multiplo-modal');
    if (!modal) {
        console.error('Modal not found!');
        return;
    }

    const tbody = document.querySelector('#inserimento-multiplo-table tbody');
    if (!tbody) {
        console.error('Table tbody not found!');
        return;
    }

    // Filter only students with "Non inserito" status
    const studentiNonInseriti = iscrittiData.filter(s => s.stato_valutazione === 'Non inserito');
    console.log('Studenti non inseriti:', studentiNonInseriti.length);

    if (studentiNonInseriti.length === 0) {
        alert('Non ci sono studenti da inserire.');
        document.body.style.overflow = modalState.originalBodyOverflow;
        return;
    }

    // Set modal state
    modalState.isOpen = true;
    modalState.currentView = 'iscritti';

    // Populate modal table
    tbody.innerHTML = studentiNonInseriti.map(studente => `
        <tr>
            <td><input type="checkbox" class="student-checkbox" data-student-id="${studente.idUtente}" aria-label="Select ${studente.nome} ${studente.cognome}"></td>
            <td>${studente.matricola}</td>
            <td>${studente.nome}</td>
            <td>${studente.cognome}</td>
            <td>${studente.email}</td>
            <td>
                <input type="text" 
                       data-student-id="${studente.idUtente}" 
                       placeholder="Inserisci voto..."
                       list="voti-list"
                       maxlength="10"
                       disabled
                       aria-label="Grade for ${studente.nome} ${studente.cognome}">
            </td>
        </tr>
    `).join('');

    // Setup event listeners with proper cleanup
    setupModalEventListeners();

    // Show modal with animation
    modal.style.display = 'flex';
    // Force reflow for animation
    modal.offsetHeight;
    modal.classList.add('show');

    // Focus management for accessibility
    setTimeout(() => {
        const firstCheckbox = modal.querySelector('.student-checkbox');
        if (firstCheckbox) {
            firstCheckbox.focus();
        }
    }, 350);

    console.log('Modal opened successfully');
};

// Enhanced closeInserimentoMultiplo function
window.closeInserimentoMultiplo = function() {
    console.log('closeInserimentoMultiplo called');

    const modal = document.getElementById('inserimento-multiplo-modal');
    if (!modal) return;

    // Restore body scroll
    if (modalState.originalBodyOverflow !== null) {
        document.body.style.overflow = modalState.originalBodyOverflow;
        modalState.originalBodyOverflow = null;
    }

    // Remove modal classes
    modal.classList.remove('show');

    // Reset modal state
    modalState.isOpen = false;
    modalState.currentView = null;

    // Hide modal after animation
    setTimeout(() => {
        modal.style.display = 'none';

        // Clean up modal content
        const tbody = modal.querySelector('#inserimento-multiplo-table tbody');
        if (tbody) {
            tbody.innerHTML = '';
        }

        // Reset select all checkbox
        const selectAll = document.getElementById('select-all-checkbox');
        if (selectAll) {
            selectAll.checked = false;
        }

        // Clean up event listeners
        cleanupModalEventListeners();

    }, 300);

    console.log('Modal closed successfully');
};

// Enhanced inviaVotiMultipli function
window.inviaVotiMultipli = function() {
    const modal = document.getElementById('inserimento-multiplo-modal');
    const voti = [];
    let hasErrors = false;
    const errors = [];

    // Collect data from selected rows
    document.querySelectorAll('.student-checkbox:checked').forEach(checkbox => {
        const studentId = checkbox.getAttribute('data-student-id');
        const votoInput = document.querySelector(`input[type="text"][data-student-id="${studentId}"]`);
        const voto = votoInput ? votoInput.value.trim() : '';

        if (!voto) {
            hasErrors = true;
            const row = checkbox.closest('tr');
            const studentName = `${row.cells[2].textContent} ${row.cells[3].textContent}`;
            errors.push(`Voto mancante per ${studentName}`);
            votoInput.style.borderColor = '#e74c3c';
        } else {
            // Validate vote
            const validVotes = ['Assente', 'Rimandato', 'Riprovato', '30 e lode'];
            const numericVote = parseInt(voto);

            if (!validVotes.includes(voto) && (isNaN(numericVote) || numericVote < 18 || numericVote > 30)) {
                hasErrors = true;
                errors.push(`Voto non valido per ${row.cells[2].textContent} ${row.cells[3].textContent}: ${voto}`);
                votoInput.style.borderColor = '#e74c3c';
            } else {
                votoInput.style.borderColor = '#1ABC9C';
                voti.push({
                    idStudente: parseInt(studentId),
                    voto: voto
                });
            }
        }
    });

    if (hasErrors) {
        alert('Errori nei dati:\n' + errors.join('\n'));
        return;
    }

    if (voti.length === 0) {
        alert('Seleziona almeno uno studente e inserisci il voto prima di inviare');
        return;
    }

    // Confirmation dialog
    if (!confirm(`Stai per inserire ${voti.length} voti. Confermi?`)) {
        return;
    }

    // Show loading state
    modal.classList.add('modal-loading');
    const submitButton = modal.querySelector('.btn-invia');
    const originalText = submitButton.textContent;
    submitButton.textContent = 'Invio in corso...';
    submitButton.disabled = true;

    // Prepare request data
    const requestData = {
        idAppello: currentAppelloId,
        idCorso: currentCorsoId,
        voti: voti
    };

    // Send AJAX request
    const xhr = new XMLHttpRequest();
    xhr.open('POST', 'InserimentoMultiplo', true);
    xhr.setRequestHeader('Content-Type', 'application/json');

    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            // Remove loading state
            modal.classList.remove('modal-loading');
            submitButton.textContent = originalText;
            submitButton.disabled = false;

            if (xhr.status === 200) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    alert(response.message || 'Voti inseriti con successo');

                    // Close modal
                    closeInserimentoMultiplo();

                    // Reload student list
                    loadIscritti(currentAppelloId, currentCorsoId, currentSortField, currentSortDir);
                } catch (error) {
                    console.error('Error parsing JSON:', error);
                    alert('Errore nell\'inserimento multiplo');
                }
            } else {
                try {
                    const errorData = JSON.parse(xhr.responseText);
                    alert(errorData.error || 'Errore nell\'inserimento multiplo');
                } catch (e) {
                    alert('Errore nell\'inserimento multiplo');
                }
            }
        }
    };

    xhr.onerror = function() {
        modal.classList.remove('modal-loading');
        submitButton.textContent = originalText;
        submitButton.disabled = false;
        alert('Errore di rete nell\'inserimento multiplo');
    };

    xhr.send(JSON.stringify(requestData));
};

// Setup modal event listeners
function setupModalEventListeners() {
    // Remove any existing listeners first
    cleanupModalEventListeners();

    // Checkbox change handlers
    const checkboxHandler = function() {
        const studentId = this.getAttribute('data-student-id');
        const votoInput = document.querySelector(`input[type="text"][data-student-id="${studentId}"]`);
        const row = this.closest('tr');

        if (votoInput) {
            votoInput.disabled = !this.checked;
            if (!this.checked) {
                votoInput.value = '';
                votoInput.style.borderColor = '';
                row.classList.remove('selected-row');
            } else {
                row.classList.add('selected-row');
                // Focus on input when checked
                setTimeout(() => votoInput.focus(), 100);
            }
        }
    };

    document.querySelectorAll('.student-checkbox').forEach(checkbox => {
        checkbox.addEventListener('change', checkboxHandler);
    });

    // Select all handler
    const selectAllHandler = function() {
        const checkboxes = document.querySelectorAll('.student-checkbox');
        checkboxes.forEach(cb => {
            cb.checked = this.checked;
            cb.dispatchEvent(new Event('change'));
        });
    };

    const selectAllCheckbox = document.getElementById('select-all-checkbox');
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', selectAllHandler);
    }

    // Input validation handlers
    document.querySelectorAll('#inserimento-multiplo-table input[type="text"]').forEach(input => {
        input.addEventListener('input', function() {
            this.style.borderColor = '';
        });

        input.addEventListener('keydown', function(e) {
            if (e.key === 'Enter') {
                const row = this.closest('tr');
                const nextRow = row.nextElementSibling;
                if (nextRow) {
                    const nextInput = nextRow.querySelector('input[type="text"]:not([disabled])');
                    if (nextInput) {
                        nextInput.focus();
                    }
                }
            }
        });
    });

    // Keyboard navigation
    const keydownHandler = function(e) {
        if (!modalState.isOpen) return;

        switch(e.key) {
            case 'Escape':
                closeInserimentoMultiplo();
                break;
            case 'Enter':
                if (e.ctrlKey || e.metaKey) {
                    inviaVotiMultipli();
                }
                break;
        }
    };

    document.addEventListener('keydown', keydownHandler);

    // Click outside to close
    const modal = document.getElementById('inserimento-multiplo-modal');
    const clickOutsideHandler = function(e) {
        if (e.target === modal) {
            closeInserimentoMultiplo();
        }
    };

    modal.addEventListener('click', clickOutsideHandler);

    // Store handlers for cleanup
    modal._modalHandlers = {
        keydownHandler,
        clickOutsideHandler,
        checkboxHandler,
        selectAllHandler
    };
}

// Clean up modal event listeners
function cleanupModalEventListeners() {
    const modal = document.getElementById('inserimento-multiplo-modal');
    if (modal && modal._modalHandlers) {
        document.removeEventListener('keydown', modal._modalHandlers.keydownHandler);
        modal.removeEventListener('click', modal._modalHandlers.clickOutsideHandler);

        // Clean up other handlers
        document.querySelectorAll('.student-checkbox').forEach(checkbox => {
            checkbox.removeEventListener('change', modal._modalHandlers.checkboxHandler);
        });

        const selectAllCheckbox = document.getElementById('select-all-checkbox');
        if (selectAllCheckbox) {
            selectAllCheckbox.removeEventListener('change', modal._modalHandlers.selectAllHandler);
        }

        delete modal._modalHandlers;
    }
}

// ===========================
// DOCUMENT READY AND EVENT LISTENERS
// ===========================

document.addEventListener("DOMContentLoaded", () => {
    loadSessionInfo();
    loadCourses();

    // Initialize modal functionality
    initializeEnhancedModal();

    // Verbali link click handler
    document.getElementById("verbali-link").addEventListener("click", function(e) {
        e.preventDefault();
        showVerbaliView();
        loadVerbali();
    });

    // Back to home button (main navigation)
    document.getElementById("back-to-home-nav").addEventListener("click", function(e) {
        e.preventDefault();

        // Se siamo nella view dettaglio verbale, torna ai verbali
        if (!dettaglioVerbaleView.classList.contains("hidden")) {
            showVerbaliView();
            loadVerbali();
        }
        // Se siamo nella view modifica voto, torna agli iscritti
        else if (!modificaVotoView.classList.contains("hidden")) {
            backToIscritti();
        }
        // Se siamo nella view iscritti, torna agli appelli
        else if (!iscrittiView.classList.contains("hidden")) {
            if (currentCorsoId && currentCorsoName) {
                showAppelliView();
                loadAppelli(currentCorsoId, currentCorsoName);
            } else {
                showHomeView();
            }
        } else {
            // Altrimenti torna alla home
            showHomeView();
        }
    });

    // Form submit handler per modifica voto
    document.getElementById("modifica-voto-form").addEventListener("submit", function(e) {
        e.preventDefault();

        // makeCall si aspetta un form element, non FormData
        makeCall("POST", "ModificaVoto", this, function(req) {
            if (req.readyState === XMLHttpRequest.DONE) {
                if (req.status === 200) {
                    try {
                        const data = JSON.parse(req.responseText);
                        alert(data.message || 'Voto modificato con successo');
                        backToIscritti();
                    } catch (error) {
                        console.error('Errore parsing JSON:', error);
                        alert('Errore nella modifica del voto');
                    }
                } else {
                    try {
                        const errorData = JSON.parse(req.responseText);
                        alert(errorData.error || 'Errore nella modifica del voto');
                    } catch (e) {
                        alert('Errore nella modifica del voto');
                    }
                }
            }
        }, false); // false per non resettare il form automaticamente
    });
});

// Initialize enhanced modal functionality
function initializeEnhancedModal() {
    // Add global error handling for modal
    window.addEventListener('error', function(e) {
        if (modalState.isOpen) {
            console.error('Error occurred while modal was open:', e.error);
            const modal = document.getElementById('inserimento-multiplo-modal');
            if (modal) {
                modal.classList.remove('modal-loading');
            }
        }
    });

    // Handle page visibility changes
    document.addEventListener('visibilitychange', function() {
        if (document.hidden && modalState.isOpen) {
            console.log('Page hidden while modal open');
        }
    });

    // Handle browser back/forward buttons
    window.addEventListener('popstate', function() {
        if (modalState.isOpen) {
            closeInserimentoMultiplo();
        }
    });

    console.log('Enhanced modal functionality initialized');
}

// Logout handler
document.querySelector("a[href='LogoutHandler']").addEventListener("click", function (e) {
    e.preventDefault();

    // Close modal if open before logout
    if (modalState.isOpen) {
        closeInserimentoMultiplo();
    }

    fetch("LogoutHandler", {
        method: "GET",
        credentials: "include"
    }).then(() => {
        currentCorsoId = null;
        currentCorsoName = null;
        currentAppelloId = null;
        currentSortField = "cognome";
        currentSortDir = "asc";
        iscrittiData = [];

        sessionStorage.removeItem("user");
        window.location.href = "login.html";
    }).catch(() => {
        alert("Logout failed");
    });
});