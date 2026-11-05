# Guida Passo-Passo: Pubblicazione su Samsung Galaxy Store

Questa guida ti accompagna punto per punto nella registrazione e pubblicazione di **NoCallCenter** sul **Samsung Galaxy Store**.

Tutti i testi, le note tecniche, l'icona e l'informativa sulla privacy sono già pronti in questa cartella.

---

## Panoramica del Materiale Preparato

All'interno della cartella `galaxy_store_submission` trovi:

1. 🖼️ **`icon_512x512.png`**  
   L'icona ufficiale dell'app alle dimensioni esatte richieste da Samsung (512×512 pixel, PNG a 32-bit).
2. 📝 **`METADATI_STORE.md`**  
   Titoli, slogan, descrizioni complete (IT/EN), tag e risposte al questionario età pronti da copiare e incollare.
3. 🕵️ **`NOTE_PER_I_TESTER_SAMSUNG.md`**  
   Il testo fondamentale per i revisori umani di Samsung che spiega perché l'app richiede permessi sensibili (`CallScreeningService`, Contatti, SMS).
4. 🔒 **`PRIVACY_POLICY.md`** & **`PRIVACY_POLICY.html`**  
   L'Informativa sulla Privacy formattata e conforme al GDPR.
5. ⚙️ **`COME_GENERARE_BINARIO.md`**  
   Istruzioni per compilare e firmare il file `.aab` o `.apk` in modalità Release.

---

## Fase 1: Creazione Account Sviluppatore Samsung (Gratis)

1. Vai sul portale sviluppatori Samsung: [**seller.samsungapps.com**](https://seller.samsungapps.com).
2. Clicca su **"Sign in"** (Accedi) o **"Sign up"** (Registrati).
3. Puoi effettuare l'accesso direttamente con il tuo account Samsung esistente (o crearne uno nuovo associato alla tua email).
4. Scegli il tipo di account:
   * **Individual (Privato):** consigliato se pubblichi a tuo nome. Non richiede Partita IVA o documentazione aziendale complessa.
5. Completa la verifica via email e numero di cellulare.
6. **Costo:** 0 € (l'iscrizione è gratuita a vita per la distribuzione di app gratuite).

---

## Fase 2: Creazione della Nuova Applicazione

1. Nella dashboard del Seller Portal, clicca sul pulsante **"Add New Application"** (Aggiungi nuova applicazione).
2. Seleziona la piattaforma: **"Android"**.
3. Seleziona la lingua predefinita: **"Italian"** (o "English").
4. Inserisci il nome dell'applicazione:  
   `NoCallCenter - Filtro Chiamate & Anti-Spam`
5. Clicca su **"Save"**.

---

## Fase 3: Compilazione della Scheda Applicazione (App Information)

Nel menu laterale troverai le sezioni da compilare:

### 1. App Information & Categorizzazione
* **Category:** Seleziona `Utilities` (Utilità) oppure `Productivity`.
* **Sub-category:** Selezionabile a piacere (es. *Communication*).
* **Tag / Keywords:** Copia la lista di tag da `METADATI_STORE.md`.
* **Icon:** Trascina il file `icon_512x512.png`.
* **Screenshots:** Carica almeno 4 screenshot dell'app (puoi catturarli dal tuo smartphone o generare schermate da Android Studio).
* **Short Description & Full Description:** Copia e incolla i testi già formattati da `METADATI_STORE.md`.
* **Support Email:** La tua email per gli utenti.
* **Privacy Policy URL:**  
  * Puoi incollare il link al file presente su GitHub:  
    `https://github.com/iylistenfree/nocallcenter/blob/main/galaxy_store_submission/PRIVACY_POLICY.md`  
  * Oppure abilitare GitHub Pages per il file `PRIVACY_POLICY.html`.

### 2. Country / Price (Prezzo e Paesi)
* **Price:** `Free` (Gratuito).
* **Countries:** Seleziona `Italia` (oppure `All Countries` se desideri raggiungere anche utenti all'estero).

### 3. Age Rating (Classificazione dei Contenuti)
* Compila il questionario di autovalutazione (rispondi "No" a violenza, contenuti espliciti, acquisti in-app, ecc.). Il sistema assegnerà la valutazione per tutte le età (**3+**).

---

## Fase 4: Caricamento del Binario (Binary)

1. Clicca sulla scheda **"Binary"** nel menu a sinistra.
2. Clicca su **"Add Binary"**.
3. Seleziona la compatibilità con i dispositivi:
   * Spunta **"Google Play Services: Not required"** (NoCallCenter non dipende da GMS proprietari).
   * Risoluzioni / Dispositivi: lascia le impostazioni standard per smartphone Galaxy.
4. Trascina il tuo file `.aab` o `.apk` compilato in modalità **Release** (seguendo le indicazioni in `COME_GENERARE_BINARIO.md`).
5. Il sistema leggerà automaticamente:
   * Package Name: `com.nocallcenter.app`
   * Version Code e Version Name.
   * Lista dei permessi richiesti.

---

## Fase 5: Inserimento Note per i Tester e Invio (Submit)

Questa è la fase più importante per evitare blocchi:

1. Cerca il campo **"Review Comments"** o **"Notes to Reviewer"** (Note per la revisione).
2. Apri il file **`NOTE_PER_I_TESTER_SAMSUNG.md`**, copia l'intero testo e incollalo nel riquadro.
3. Rivedi il riepilogo in alto: tutte le sezioni devono avere una spunta verde.
4. Clicca sul pulsante **"Submit"** (Invia per la revisione).

---

## Cosa succede dopo l'invio?

* **Tempi:** La revisione di Samsung richiede mediamente **da 2 a 4 giorni lavorativi**.
* **Test Umani:** A differenza di Google (che usa bot automatici spesso inflessibili), Samsung impiega tecnici umani che testano l'app su dispositivi Galaxy reali. Grazie alle note che abbiamo preparato, sapranno esattamente come provarla.
* **Pubblicazione:** Una volta approvata, l'app sarà immediatamente scaricabile dal Galaxy Store con un solo tocco su milioni di dispositivi Samsung in tutta Italia!
