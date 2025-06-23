import { makeCall } from "./utils.js";

// ========================================
// 1. GLOBAL STATE MANAGEMENT
// ========================================

// Current state variables
let currentCorsoId = null;
let currentCorsoName = null;
let currentAppelloId = null;
let currentSortField = "cognome";
let currentSortDir = "asc";
let iscrittiData = [];

// Modal state management
let modalState = {
    isOpen: false,
    currentView: null,
    originalBodyOverflow: null
};

// ========================================
// 2. DOM ELEMENTS REFERENCES
// ========================================

// View elements
const views = {
    home: document.getElementById("home-view"),
    verbali: document.getElementById("verbali-view"),
    appelli: document.getElementById("appelli-view"),
    iscritti: document.getElementById("iscritti-view"),
    modificaVoto: document.getElementById("modifica-voto-view"),
    dettaglioVerbale: document.getElementById("dettaglio-verbale-view")
};

// Navigation elements
const navigation = {
    mainTitle: document.getElementById("main-title"),
    verbaliLink: document.getElementById("verbali-link"),
    backToHomeNav: document.getElementById("back-to-home-nav")
};

// ========================================
// 3. VIEW MANAGEMENT FUNCTIONS
// ========================================

function hideAllViews() {
    Object.values(views).forEach(view => view.classList.add("hidden"));
}

function showView(viewName, title, showVerbaliLink = false) {
    if (modalState.isOpen) {
        closeInserimentoMultiplo();
    }

    hideAllViews();
    views[viewName].classList.remove("hidden");

    navigation.mainTitle.textContent = title;
    navigation.verbaliLink.classList.toggle("hidden", !showVerbaliLink);
    navigation.backToHomeNav.classList.toggle("hidden", viewName === 'home');

    if (viewName !== 'iscritti') {
        hideActionButtons();
    }
}

const viewFunctions = {
    showHomeView: () => showView('home', "Welcome to the Professor's Home Page", true),
    showVerbaliView: () => showView('verbali', "Verbali degli Studenti", false),
    showAppelliView: () => showView('appelli', "Seleziona un Appello", false),
    showIscrittiView: () => showView('iscritti', "Studenti Iscritti all'Appello", false),
    showModificaVotoView: () => showView('modificaVoto', "Modifica Voto Studente", false),
    showDettaglioVerbaleView: () => showView('dettaglioVerbale', "Dettaglio Verbale", false)
};

// ========================================
// 4. DATA LOADING FUNCTIONS
// ========================================

const dataLoaders = {
    // Load session info
    loadSessionInfo() {
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
    },

    // Load courses
    loadCourses() {
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

                        eventHandlers.addAppelliEventListeners();
                    } else {
                        container.style.display = "none";
                        emptyContainer.style.display = "block";
                    }
                } else {
                    window.location.href = "login.html";
                }
            }
        });
    },

    // Load appelli
    loadAppelli(corsoId, corsoName) {
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
                                <span class="appello-date">${utils.formatDate(appello.dataAppello)}</span>
                                <a href="#" data-appello-id="${appello.idAppello}" data-corso-id="${corsoId}" 
                                   class="select-button iscritti-link">Seleziona</a>
                            </div>
                        `).join('');

                        eventHandlers.addIscrittiEventListeners();
                    } else {
                        appelliContent.innerHTML = "<p>Nessun appello disponibile per questo corso.</p>";
                    }
                } else {
                    appelliContent.innerHTML = "<p>Errore nel caricamento degli appelli.</p>";
                }
            }
        });
    },

    // Load iscritti
    loadIscritti(appelloId, corsoId, sortField = "cognome", sortDir = "asc") {
        currentAppelloId = appelloId;
        currentCorsoId = corsoId;
        currentSortField = sortField;
        currentSortDir = sortDir;

        const url = `IscrittiAppello?idAppello=${appelloId}&idCorso=${corsoId}`;

        makeCall("GET", url, null, function (req) {
            if (req.readyState === XMLHttpRequest.DONE) {
                if (req.status === 200) {
                    try {
                        const data = JSON.parse(req.responseText);
                        // Salvo i dati originali così posso riordinarli lato client senza fare altre chiamate
                        iscrittiData = data.iscritti;

                        // Ordino subito i dati secondo il criterio scelto (di default per cognome)
                        const sortedIscritti = sortingUtils.sortIscritti([...iscrittiData], sortField, sortDir);

                        updateViews.updateIscrittiView(
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
    },

    // Load verbali
    loadVerbali() {
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
                                            <td>${utils.formatDateTime(entry.dataCreazione)}</td>
                                            <td>${entry.nomeCorso}</td>
                                            <td>${utils.formatDate(entry.dataAppello)}</td>
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

                        eventHandlers.addVerbaliEventListeners();
                    } else {
                        verbaliContent.innerHTML = "<p>Nessun verbale disponibile.</p>";
                    }
                } else {
                    verbaliContent.innerHTML = "<p>Errore nel caricamento dei verbali.</p>";
                }
            }
        });
    },

    // Load dettaglio verbale
    loadDettaglioVerbale(codiceVerbale) {
        makeCall("GET", `DettaglioVerbale?codice=${codiceVerbale}`, null, function(req) {
            if (req.readyState === XMLHttpRequest.DONE) {
                if (req.status === 200) {
                    try {
                        const data = JSON.parse(req.responseText);

                        const verbaleInfo = document.getElementById('verbale-info');
                        verbaleInfo.innerHTML = `
                            <h4>Codice Verbale: ${data.verbaleInfo.codiceVerbale}</h4>
                            <p>Data Creazione: ${data.verbaleInfo.dataCreazione}</p>
                            <p>Data Appello: ${data.verbaleInfo.dataAppello}</p>
                            <p>Corso: ${data.verbaleInfo.nomeCorso}</p>
                        `;

                        document.getElementById('totale-studenti-verbale').textContent = `Totale studenti: ${data.totaleStudenti}`;

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

                        viewFunctions.showDettaglioVerbaleView();
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
    },

    // Load student for modification
    loadStudentForModification(studentId, appelloId, corsoId) {
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

                        document.getElementById('modifica-matricola').textContent = data.studente.matricola;
                        document.getElementById('modifica-nome').textContent = data.studente.nome;
                        document.getElementById('modifica-cognome').textContent = data.studente.cognome;
                        document.getElementById('modifica-corso-laurea').textContent = data.studente.corsoDiLaurea;

                        document.getElementById('modifica-id-studente').value = data.studente.idUtente;
                        document.getElementById('modifica-id-appello').value = data.idAppello;
                        document.getElementById('modifica-id-corso').value = data.idCorso;

                        const votoSelect = document.getElementById('voto');
                        votoSelect.value = data.studente.voto || '';

                        viewFunctions.showModificaVotoView();
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
        }, false);
    }
};

// ========================================
// 5. SORTING UTILITIES
// ========================================

const sortingUtils = {
    // Devo convertire i voti in numeri per poterli ordinare correttamente
    // perché "30 e lode" deve venire dopo 30, e "assente" prima di tutti i voti numerici
    votoToPriority(voto) {
        if (!voto || voto.trim() === '') return 0;

        // Ho deciso di dare priorità così:
        // Assente = 1 (viene per primo)
        // Rimandato = 2 (subito dopo)
        // Riprovato = 3 (poi questo)
        // Voti numerici = 4-16 (dal 18 al 30)
        // 30 e lode = 17 (il massimo)
        switch (voto.toLowerCase()) {
            case "assente": return 1;
            case "rimandato": return 2;
            case "riprovato": return 3;
            case "30 e lode": return 17;
            default:
                const val = parseInt(voto);
                // Se è un voto numerico valido (18-30), lo mappo in un range 4-16
                if (!isNaN(val) && val >= 18 && val <= 30) {
                    return 3 + (val - 17); // 18 diventa 4, 19 diventa 5, ecc.
                }
                // Se non riconosco il voto, lo metto in fondo
                return Number.MAX_SAFE_INTEGER;
        }
    },

    // Ordino gli studenti in base al campo selezionato
    sortIscritti(data, field, direction) {
        return data.sort((a, b) => {
            let valA, valB;

            // Se sto ordinando per voto, devo usare la mia funzione speciale
            // altrimenti i voti non verrebbero ordinati correttamente
            if (field === 'voto') {
                valA = this.votoToPriority(a.voto);
                valB = this.votoToPriority(b.voto);
            } else {
                // Per tutti gli altri campi (nome, cognome, ecc.) uso l'ordinamento normale
                valA = a[field] || '';
                valB = b[field] || '';

                // Rendo l'ordinamento case-insensitive così "andrea" e "Andrea" sono uguali
                if (typeof valA === 'string') valA = valA.toLowerCase();
                if (typeof valB === 'string') valB = valB.toLowerCase();
            }

            // Confronto finale: se direction è 'asc' ordino crescente, altrimenti decrescente
            if (valA < valB) return direction === 'asc' ? -1 : 1;
            if (valA > valB) return direction === 'asc' ? 1 : -1;
            return 0;
        });
    }
};

// ========================================
// 6. VIEW UPDATE FUNCTIONS
// ========================================

const updateViews = {
    updateIscrittiView(iscritti, appelloId, corsoId, callDate, sortField, sortDir, countInserito, countPubblicatoRifiutato) {
        document.getElementById("iscritti-appello-id").textContent = appelloId;
        document.getElementById("iscritti-call-date").textContent = "Call Date: " + callDate;
        document.getElementById("iscritti-count").textContent = "Number Of Students in the Call: " + iscritti.length;

        this.updateActionButtons(appelloId, corsoId, countInserito, countPubblicatoRifiutato);
        this.updateIscrittiTable(iscritti, sortField, sortDir);
    },

    updateIscrittiTable(iscritti, sortField, sortDir) {
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

            eventHandlers.addSortEventListeners();
            eventHandlers.addModifyEventListeners();
        } else {
            iscrittiContent.innerHTML = "<p>Nessun iscritto trovato per questo appello.</p>";
        }
    },

    updateActionButtons(appelloId, corsoId, countInserito, countPubblicatoRifiutato) {
        const actionsContainer = document.getElementById("nav-actions");
        let buttonsHTML = '';

        // Conto quanti studenti non hanno ancora un voto
        const countNonInserito = iscrittiData.filter(s => s.stato_valutazione === 'Non inserito').length;

        // Mostro il bottone per l'inserimento multiplo solo se ci sono studenti senza voto
        if (countNonInserito > 0 && modalUtils.canOpenModal()) {
            buttonsHTML += `
                <button class="btn-multiplo" onclick="openInserimentoMultiplo()">
                    Inserimento Multiplo (${countNonInserito})
                </button>
            `;
        }

        // Se ci sono voti inseriti ma non ancora pubblicati, mostro il bottone pubblica
        if (countInserito > 0) {
            buttonsHTML += `
                <button class="action-button pubblica" data-action="pubblica" 
                        data-appello-id="${appelloId}" data-corso-id="${corsoId}">
                    Pubblica
                </button>
            `;
        }

        // Se ci sono voti pubblicati o rifiutati, posso verbalizzare
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
            eventHandlers.addActionButtonEventListeners();
        }
    }
};

function hideActionButtons() {
    const actionsContainer = document.getElementById("nav-actions");
    if (actionsContainer) {
        actionsContainer.innerHTML = '';
    }
}

// ========================================
// 7. ACTION HANDLERS
// ========================================

const actionHandlers = {
    handlePubblicaVoti(appelloId, corsoId) {
        if (!confirm('Sei sicuro di voler pubblicare i voti? Gli studenti riceveranno una notifica via email.')) {
            return;
        }

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
                        dataLoaders.loadIscritti(appelloId, corsoId, currentSortField, currentSortDir);
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
    },

    handleVerbalizzaIscritti(appelloId, corsoId) {
        if (!confirm('Sei sicuro di voler verbalizzare gli iscritti? Verrà creato un verbale con tutti i voti pubblicati.')) {
            return;
        }

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

                        if (data.idVerbale) {
                            dataLoaders.loadDettaglioVerbale(data.idVerbale);
                        } else {
                            dataLoaders.loadIscritti(appelloId, corsoId, currentSortField, currentSortDir);
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
};

// ========================================
// 8. EVENT HANDLERS
// ========================================

const eventHandlers = {
    addAppelliEventListeners() {
        document.querySelectorAll('.appelli-link').forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const corsoId = this.getAttribute('data-corso-id');
                const corsoName = this.getAttribute('data-corso-name');
                viewFunctions.showAppelliView();
                dataLoaders.loadAppelli(corsoId, corsoName);
            });
        });
    },

    addIscrittiEventListeners() {
        document.querySelectorAll('.iscritti-link').forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const appelloId = this.getAttribute('data-appello-id');
                const corsoId = this.getAttribute('data-corso-id');
                viewFunctions.showIscrittiView();
                dataLoaders.loadIscritti(appelloId, corsoId);
            });
        });
    },

    addSortEventListeners() {
        document.querySelectorAll('.iscritti-table th a').forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const sortField = this.getAttribute('data-sort-field');
                const sortDir = this.getAttribute('data-sort-dir');

                if (sortField && iscrittiData.length > 0) {
                    // Memorizzo i nuovi criteri di ordinamento
                    currentSortField = sortField;
                    currentSortDir = sortDir;

                    // Riordino i dati che ho già in memoria invece di fare una nuova chiamata al server
                    const sortedIscritti = sortingUtils.sortIscritti([...iscrittiData], sortField, sortDir);

                    // Aggiorno solo la tabella con i dati riordinati
                    updateViews.updateIscrittiTable(sortedIscritti, sortField, sortDir);
                }
            });
        });
    },

    addModifyEventListeners() {
        document.querySelectorAll('.action-button[data-student-id]').forEach(button => {
            button.addEventListener('click', function(e) {
                e.preventDefault();
                const studentId = this.getAttribute('data-student-id');
                const appelloId = this.getAttribute('data-appello-id');
                const corsoId = this.getAttribute('data-corso-id');

                console.log('Modifica clicked - studentId:', studentId, 'appelloId:', appelloId, 'corsoId:', corsoId);
                dataLoaders.loadStudentForModification(studentId, appelloId, corsoId);
            });
        });
    },

    addActionButtonEventListeners() {
        document.querySelectorAll('.action-button[data-action]').forEach(button => {
            button.addEventListener('click', function(e) {
                e.preventDefault();
                const action = this.getAttribute('data-action');
                const appelloId = this.getAttribute('data-appello-id');
                const corsoId = this.getAttribute('data-corso-id');

                if (action === 'pubblica') {
                    actionHandlers.handlePubblicaVoti(appelloId, corsoId);
                } else if (action === 'verbalizza') {
                    actionHandlers.handleVerbalizzaIscritti(appelloId, corsoId);
                }
            });
        });
    },

    addVerbaliEventListeners() {
        document.querySelectorAll('a.verbale-link[data-codice]').forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const codiceVerbale = this.getAttribute('data-codice');
                dataLoaders.loadDettaglioVerbale(codiceVerbale);
            });
        });
    }
};

// ========================================
// 9. MODAL MANAGEMENT
// ========================================

const modalUtils = {
    // Controllo se posso aprire il modal - non ha senso aprirlo se non ci sono studenti da valutare
    canOpenModal() {
        // Prima verifico di essere nella vista giusta
        if (views.iscritti && views.iscritti.classList.contains('hidden')) {
            return false;
        }

        // Poi controllo che ci siano effettivamente dei dati
        if (!iscrittiData || !Array.isArray(iscrittiData)) {
            return false;
        }

        // Infine verifico che ci siano studenti senza voto
        const studentiNonInseriti = iscrittiData.filter(s => s.stato_valutazione === 'Non inserito');
        return studentiNonInseriti.length > 0;
    },

    // Imposto tutti gli event listener necessari per il modal
    setupModalEventListeners() {
        // Prima rimuovo eventuali listener esistenti per evitare duplicati
        this.cleanupModalEventListeners();

        // Handler per quando clicco su una checkbox studente
        const checkboxHandler = function() {
            const studentId = this.getAttribute('data-student-id');
            const votoInput = document.querySelector(`input[type="text"][data-student-id="${studentId}"]`);
            const row = this.closest('tr');

            if (votoInput) {
                // Se seleziono lo studente, abilito l'input del voto
                // Se lo deseleziono, lo disabilito e cancello il voto inserito
                votoInput.disabled = !this.checked;
                if (!this.checked) {
                    votoInput.value = '';
                    votoInput.style.borderColor = '';
                    row.classList.remove('selected-row');
                } else {
                    row.classList.add('selected-row');
                    // Do focus all'input così posso subito scrivere il voto
                    setTimeout(() => votoInput.focus(), 100);
                }
            }
        };

        document.querySelectorAll('.student-checkbox').forEach(checkbox => {
            checkbox.addEventListener('change', checkboxHandler);
        });

        // Handler per il "seleziona tutti"
        const selectAllHandler = function() {
            const checkboxes = document.querySelectorAll('.student-checkbox');
            // Quando clicco su "seleziona tutti", attivo/disattivo tutte le checkbox
            checkboxes.forEach(cb => {
                cb.checked = this.checked;
                // Triggero l'evento change così si attivano/disattivano anche gli input
                cb.dispatchEvent(new Event('change'));
            });
        };

        const selectAllCheckbox = document.getElementById('select-all-checkbox');
        if (selectAllCheckbox) {
            selectAllCheckbox.addEventListener('change', selectAllHandler);
        }

        // Gestisco la validazione in tempo reale degli input
        document.querySelectorAll('#inserimento-multiplo-table input[type="text"]').forEach(input => {
            // Quando scrivo qualcosa, rimuovo il bordo rosso di errore
            input.addEventListener('input', function() {
                this.style.borderColor = '';
            });

            // Quando premo Enter, passo automaticamente al prossimo studente
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

        // Gestisco le scorciatoie da tastiera
        const keydownHandler = function(e) {
            if (!modalState.isOpen) return;

            switch(e.key) {
                case 'Escape':
                    // ESC per chiudere il modal
                    closeInserimentoMultiplo();
                    break;
                case 'Enter':
                    // CTRL+Enter per inviare i voti
                    if (e.ctrlKey || e.metaKey) {
                        inviaVotiMultipli();
                    }
                    break;
            }
        };

        document.addEventListener('keydown', keydownHandler);

        // Chiudo il modal se clicco fuori
        const modal = document.getElementById('inserimento-multiplo-modal');
        const clickOutsideHandler = function(e) {
            if (e.target === modal) {
                closeInserimentoMultiplo();
            }
        };

        modal.addEventListener('click', clickOutsideHandler);

        // Salvo i riferimenti agli handler per poterli rimuovere dopo
        modal._modalHandlers = {
            keydownHandler,
            clickOutsideHandler,
            checkboxHandler,
            selectAllHandler
        };
    },

    // Rimuovo tutti gli event listener del modal per evitare memory leak
    cleanupModalEventListeners() {
        const modal = document.getElementById('inserimento-multiplo-modal');
        if (modal && modal._modalHandlers) {
            document.removeEventListener('keydown', modal._modalHandlers.keydownHandler);
            modal.removeEventListener('click', modal._modalHandlers.clickOutsideHandler);

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
};

// Funzione globale per aprire il modal di inserimento multiplo
window.openInserimentoMultiplo = function() {
    console.log('openInserimentoMultiplo called');

    // Non posso aprire il modal se non sono nella vista iscritti
    if (views.iscritti.classList.contains('hidden')) {
        console.log('Not in iscritti view, returning');
        return;
    }

    // Blocco lo scroll della pagina quando il modal è aperto
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

    // Filtro solo gli studenti che non hanno ancora un voto
    const studentiNonInseriti = iscrittiData.filter(s => s.stato_valutazione === 'Non inserito');
    console.log('Studenti non inseriti:', studentiNonInseriti.length);

    if (studentiNonInseriti.length === 0) {
        alert('Non ci sono studenti da inserire.');
        document.body.style.overflow = modalState.originalBodyOverflow;
        return;
    }

    // Aggiorno lo stato del modal
    modalState.isOpen = true;
    modalState.currentView = 'iscritti';

    // Popolo la tabella del modal con gli studenti senza voto
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

    // Configuro tutti gli event listener necessari
    modalUtils.setupModalEventListeners();

    // Mostro il modal con animazione
    modal.style.display = 'flex';
    // Forzo il reflow per far partire l'animazione
    modal.offsetHeight;
    modal.classList.add('show');

    // Do focus alla prima checkbox per l'accessibilità
    setTimeout(() => {
        const firstCheckbox = modal.querySelector('.student-checkbox');
        if (firstCheckbox) {
            firstCheckbox.focus();
        }
    }, 350);

    console.log('Modal opened successfully');
};

// Funzione per chiudere il modal
window.closeInserimentoMultiplo = function() {
    console.log('closeInserimentoMultiplo called');

    const modal = document.getElementById('inserimento-multiplo-modal');
    if (!modal) return;

    // Ripristino lo scroll della pagina
    if (modalState.originalBodyOverflow !== null) {
        document.body.style.overflow = modalState.originalBodyOverflow;
        modalState.originalBodyOverflow = null;
    }

    // Avvio l'animazione di chiusura
    modal.classList.remove('show');

    // Resetto lo stato
    modalState.isOpen = false;
    modalState.currentView = null;

    // Aspetto che l'animazione finisca prima di nascondere completamente il modal
    setTimeout(() => {
        modal.style.display = 'none';

        // Pulisco il contenuto del modal
        const tbody = modal.querySelector('#inserimento-multiplo-table tbody');
        if (tbody) {
            tbody.innerHTML = '';
        }

        // Resetto la checkbox "seleziona tutti"
        const selectAll = document.getElementById('select-all-checkbox');
        if (selectAll) {
            selectAll.checked = false;
        }

        // Rimuovo tutti gli event listener
        modalUtils.cleanupModalEventListeners();

    }, 300);

    console.log('Modal closed successfully');
};

// Funzione per inviare i voti di più studenti contemporaneamente
window.inviaVotiMultipli = function() {
    const modal = document.getElementById('inserimento-multiplo-modal');
    const voti = [];
    let hasErrors = false;
    const errors = [];

    // Raccolgo i dati da tutte le righe selezionate
    document.querySelectorAll('.student-checkbox:checked').forEach(checkbox => {
        const studentId = checkbox.getAttribute('data-student-id');
        const votoInput = document.querySelector(`input[type="text"][data-student-id="${studentId}"]`);
        const voto = votoInput ? votoInput.value.trim() : '';

        // Controllo che il voto sia stato inserito
        if (!voto) {
            hasErrors = true;
            const row = checkbox.closest('tr');
            const studentName = `${row.cells[2].textContent} ${row.cells[3].textContent}`;
            errors.push(`Voto mancante per ${studentName}`);
            // Marco l'input in rosso così l'utente capisce dove c'è l'errore
            votoInput.style.borderColor = '#e74c3c';
        } else {
            // Valido il voto inserito
            const validVotes = ['Assente', 'Rimandato', 'Riprovato', '30 e lode'];
            const numericVote = parseInt(voto);

            // Il voto deve essere uno di quelli speciali o un numero tra 18 e 30
            if (!validVotes.includes(voto) && (isNaN(numericVote) || numericVote < 18 || numericVote > 30)) {
                hasErrors = true;
                const row = checkbox.closest('tr');
                errors.push(`Voto non valido per ${row.cells[2].textContent} ${row.cells[3].textContent}: ${voto}`);
                votoInput.style.borderColor = '#e74c3c';
            } else {
                // Voto valido, lo marco in verde e lo aggiungo alla lista
                votoInput.style.borderColor = '#1ABC9C';
                voti.push({
                    idStudente: parseInt(studentId),
                    voto: voto
                });
            }
        }
    });

    // Se ci sono errori, li mostro all'utente
    if (hasErrors) {
        alert('Errori nei dati:\n' + errors.join('\n'));
        return;
    }

    // Controllo di aver selezionato almeno uno studente
    if (voti.length === 0) {
        alert('Seleziona almeno uno studente e inserisci il voto prima di inviare');
        return;
    }

    // Chiedo conferma prima di procedere
    if (!confirm(`Stai per inserire ${voti.length} voti. Confermi?`)) {
        return;
    }

    // Mostro uno stato di caricamento
    modal.classList.add('modal-loading');
    const submitButton = modal.querySelector('.btn-invia');
    const originalText = submitButton.textContent;
    submitButton.textContent = 'Invio in corso...';
    submitButton.disabled = true;

    // Preparo i dati da inviare al server
    const requestData = {
        idAppello: currentAppelloId,
        idCorso: currentCorsoId,
        voti: voti
    };

    // Invio la richiesta AJAX
    const xhr = new XMLHttpRequest();
    xhr.open('POST', 'InserimentoMultiplo', true);
    xhr.setRequestHeader('Content-Type', 'application/json');

    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            // Rimuovo lo stato di caricamento
            modal.classList.remove('modal-loading');
            submitButton.textContent = originalText;
            submitButton.disabled = false;

            if (xhr.status === 200) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    alert(response.message || 'Voti inseriti con successo');

                    // Chiudo il modal e ricarico la lista studenti
                    closeInserimentoMultiplo();
                    dataLoaders.loadIscritti(currentAppelloId, currentCorsoId, currentSortField, currentSortDir);
                } catch (error) {
                    console.error('Error parsing JSON:', error);
                    alert('Errore nell\'inserimento multiplo');
                }
            } else {
                // Gestisco gli errori del server
                try {
                    const errorData = JSON.parse(xhr.responseText);
                    alert(errorData.error || 'Errore nell\'inserimento multiplo');
                } catch (e) {
                    alert('Errore nell\'inserimento multiplo');
                }
            }
        }
    };

    // Gestisco eventuali errori di rete
    xhr.onerror = function() {
        modal.classList.remove('modal-loading');
        submitButton.textContent = originalText;
        submitButton.disabled = false;
        alert('Errore di rete nell\'inserimento multiplo');
    };

    xhr.send(JSON.stringify(requestData));
};

window.backToIscritti = function() {
    if (currentAppelloId && currentCorsoId) {
        viewFunctions.showIscrittiView();
        dataLoaders.loadIscritti(currentAppelloId, currentCorsoId, currentSortField, currentSortDir);
    }
};

// ========================================
// 10. UTILITY FUNCTIONS
// ========================================

const utils = {
    formatDateTime(timestamp) {
        const date = new Date(timestamp);
        return date.toLocaleString('it-IT', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
    },

    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('it-IT', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    }
};

// ========================================
// 11. INITIALIZATION
// ========================================

function initializeEnhancedModal() {
    window.addEventListener('error', function(e) {
        if (modalState.isOpen) {
            console.error('Error occurred while modal was open:', e.error);
            const modal = document.getElementById('inserimento-multiplo-modal');
            if (modal) {
                modal.classList.remove('modal-loading');
            }
        }
    });

    document.addEventListener('visibilitychange', function() {
        if (document.hidden && modalState.isOpen) {
            console.log('Page hidden while modal open');
        }
    });

    window.addEventListener('popstate', function() {
        if (modalState.isOpen) {
            closeInserimentoMultiplo();
        }
    });

    console.log('Enhanced modal functionality initialized');
}

// ========================================
// 12. MAIN INITIALIZATION
// ========================================

document.addEventListener("DOMContentLoaded", () => {
    // Initialize application
    dataLoaders.loadSessionInfo();
    dataLoaders.loadCourses();
    initializeEnhancedModal();

    // Navigation event listeners
    document.getElementById("verbali-link").addEventListener("click", function(e) {
        e.preventDefault();
        viewFunctions.showVerbaliView();
        dataLoaders.loadVerbali();
    });

    document.getElementById("back-to-home-nav").addEventListener("click", function(e) {
        e.preventDefault();

        if (!views.dettaglioVerbale.classList.contains("hidden")) {
            viewFunctions.showVerbaliView();
            dataLoaders.loadVerbali();
        } else if (!views.modificaVoto.classList.contains("hidden")) {
            backToIscritti();
        } else if (!views.iscritti.classList.contains("hidden")) {
            if (currentCorsoId && currentCorsoName) {
                viewFunctions.showAppelliView();
                dataLoaders.loadAppelli(currentCorsoId, currentCorsoName);
            } else {
                viewFunctions.showHomeView();
            }
        } else {
            viewFunctions.showHomeView();
        }
    });

    // Form submission handler
    document.getElementById("modifica-voto-form").addEventListener("submit", function(e) {
        e.preventDefault();

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
        }, false);
    });

    // Logout handler
    document.querySelector("a[href='LogoutHandler']").addEventListener("click", function (e) {
        e.preventDefault();

        if (modalState.isOpen) {
            closeInserimentoMultiplo();
        }

        fetch("LogoutHandler", {
            method: "GET",
            credentials: "include"
        }).then(() => {
            // Reset all state
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
});