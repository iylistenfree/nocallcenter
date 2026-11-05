# Privacy Policy – NoCallCenter

*Ultimo aggiornamento: 5 Novembre 2026*

La presente Informativa sulla Privacy descrive come l'applicazione **NoCallCenter** ("l'Applicazione") gestisce i dati personali, i permessi di sistema e la riservatezza degli utenti.

Il principio fondamentale su cui è costruita NoCallCenter è la **totale riservatezza e l'elaborazione locale (100% on-device)**.

---

## 1. Titolare del Trattamento e Filosofia dell'App

NoCallCenter è un'applicazione open source, gratuita e senza fini di lucro, creata per proteggere gli utenti dalle chiamate moleste e dal telemarketing aggressivo.

* **Nessun server cloud:** L'Applicazione non possiede server proprietari, non trasmette dati a database remoti e non effettua alcun tracciamento o profilazione dell'utente.
* **Nessun account richiesto:** Per utilizzare l'Applicazione non è richiesta alcuna registrazione, inserimento di indirizzo email, password o credenziali.
* **Zero pubblicità e tracker di terze parti:** L'Applicazione non include librerie di annunci pubblicitari (ad es. Google AdMob) né framework di analisi o telemetria di terze parti (ad es. Firebase Analytics, Facebook SDK).

---

## 2. Dati Trattati e Permessi di Sistema Richiesti

Per svolgere le proprie funzioni di filtro chiamate e risposta automatica, l'Applicazione richiede i seguenti permessi di sistema Android, utilizzati **esclusivamente in locale**:

### A. Accesso ai Contatti (`android.permission.READ_CONTACTS`)
* **Finalità:** Verificare istantaneamente se il numero di telefono che sta chiamando è già memorizzato nella rubrica del dispositivo.
* **Modalità di trattamento:** Il confronto avviene esclusivamente nella memoria RAM locale del telefono al momento della chiamata. **I contatti dell'utente non vengono mai salvati in modo permanente dall'app, né caricati, sincronizzati o trasmessi a terzi.** Se il contatto è in rubrica, la chiamata viene lasciata squillare normalmente.

### B. Servizio Filtro Chiamate (`android.permission.BIND_SCREENING_SERVICE` / `CallScreeningService`)
* **Finalità:** Permettere all'Applicazione di operare come servizio di Call Screening predefinito di Android, consentendo il blocco e il silenziamento preventivo delle chiamate provenienti da numeri sconosciuti o telemarketing prima del primo squillo.
* **Modalità di trattamento:** La decisione di consentire o respingere la chiamata viene presa in tempo reale a livello di sistema operativo.

### C. Invio di SMS (`android.permission.SEND_SMS`)
* **Finalità:** Inviare un singolo messaggio SMS automatico di cortesia ai numeri respinti, informandoli dell'iscrizione al Registro Pubblico delle Opposizioni e invitando i mittenti legittimi (es. corrieri, medici, conoscenti) a ricontattare l'utente su WhatsApp.
* **Controllo dell'utente e tutela del credito:**
  * L'invio dell'SMS è opzionale e può essere disattivato o personalizzato dall'utente nelle Impostazioni in qualsiasi momento.
  * È attiva una protezione "Anti-Flood" che limita l'invio a massimo 1 SMS ogni 48 ore verso lo stesso numero.
  * È disponibile una funzione di "Salva-Credito" per impostare un tetto massimo mensile di SMS inviabili.

### D. Notifiche (`android.permission.POST_NOTIFICATIONS`)
* **Finalità:** Mostrare all'utente notifiche discrete ed informative relative alle chiamate schermate o all'avvicinamento al limite del plafond SMS mensile.

### E. Accesso a Internet (`android.permission.INTERNET`)
* **Finalità:** Utilizzato unicamente per:
  1. La verifica periodica facoltativa della presenza di nuove versioni dell'applicazione tramite le API pubbliche di GitHub Releases.
  2. Il reindirizzamento facoltativo e volontario dell'utente alla pagina PayPal.me del titolare nel caso in cui l'utente desideri offrire una donazione a supporto del progetto.
* Nessun dato relativo a chiamate, rubrica o messaggi viene mai inviato tramite la connessione internet.

---

## 3. Conservazione dei Dati

Tutti i dati generati durante l'utilizzo dell'app (il registro locale delle chiamate schermate, il contatore degli SMS e le preferenze di configurazione):
* Sono salvati esclusivamente nello spazio di archiviazione privato dell'applicazione sul dispositivo dell'utente.
* Possono essere cancellati dall'utente in qualsiasi momento direttamente dall'interfaccia dell'app (tasto "Svuota registro") o tramite la cancellazione dei dati dell'app dalle impostazioni di sistema di Android.
* Vengono eliminati definitivamente e automaticamente in caso di disinstallazione dell'Applicazione.

---

## 4. Condivisione dei Dati con Terze Parti

**Nessun dato personale viene condiviso, venduto, noleggiato o comunicato a terzi.**

---

## 5. Diritti dell'Utente (GDPR / Normativa Privacy)

Poiché l'Applicazione non raccoglie né archivia dati su server remoti, l'utente esercita il pieno ed esclusivo controllo sui propri dati direttamente sul proprio dispositivo:
* Diritto di accesso e revoca dei permessi: tramite le Impostazioni di Android in qualsiasi momento.
* Diritto di cancellazione: tramite il tasto di svuotamento registro nell'app o disinstallando l'app.

---

## 6. Contatti e Codice Sorgente

Per qualsiasi chiarimento o domanda relativa a questa Informativa sulla Privacy:
* **Codice Sorgente Open Source:** [https://github.com/iylistenfree/nocallcenter](https://github.com/iylistenfree/nocallcenter)
* **Sviluppatore:** Gabriele Licchelli
* **Licenza:** MIT License
