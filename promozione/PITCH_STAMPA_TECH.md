# Pitch per la Stampa Tech e Blog di Settore

Le redazioni tecnologiche italiane ricevono decine di comunicati stampa noiosi e promozionali ogni giorno. 
Per attirare la loro attenzione, il trucco è **non inviare un comunicato stampa aziendale**, ma **una breve email personale da sviluppatore indipendente**, offrendo una notizia/storia con un forte angolo di attualità (la piaga del telemarketing e il fallimento del Registro delle Opposizioni).

---

## 1. Pitch per DDay.it (Consigliatissimo)
* **Destinatari:** `redazione@dday.it` (o direttamente ai redattori che trattano telecomunicazioni come Gianfranco Giardina o Roberto Pezzali).
* **Oggetto:** *Progetto indie: un'alternativa open source e privacy-first a Truecaller contro il telemarketing selvaggio*

```text
Gentile redazione di DDay,

Seguo con grande interesse le vostre inchieste sul telemarketing selvaggio, sul fallimento del Registro delle Opposizioni e sul fenomeno dello spoofing telefonico.

Come sviluppatore software indipendente (e utente esasperato dalle continue chiamate moleste), ho notato che la maggior parte delle persone oggi si trova davanti a un bivio obbligato:
- Subire le chiamate commerciali quotidiane;
- Oppure affidarsi ad app come Truecaller, che però risolvono il problema violando la privacy degli utenti e caricando le rubriche telefoniche su server terzi.

Per questo ho sviluppato e rilasciato NoCallCenter (https://github.com/iylistenfree/nocallcenter), un'applicazione Android open-source (licenza MIT) e gratuita, che funziona al 100% in locale senza alcun server cloud.

L'app non si basa su database centralizzati (ormai resi inutili dal caller ID spoofing), ma su un principio asimmetrico:
1. Intercetta tramite CallScreeningService le chiamate da numeri non in rubrica, rifiutandole all'istante e in silenzio.
2. Invia contestualmente un SMS automatico: "Numero iscritto al Registro Opposizioni. Se sei un corriere o conoscente, scrivimi su WhatsApp".
3. Risultato: i centralini automatici (predictive dialer) non sanno interagire con WhatsApp e la chiamata decade; corrieri, medici o conoscenti inviano un messaggio in pochi secondi.

L'app include salvaguardie per il credito telefonico (budget SMS mensile, anti-flood 48h, blocco prefissi esteri) ed è distribuita liberamente su GitHub per via dei vincoli di Google sulle API SMS in background.

Se ritenete l'argomento e la soluzione di interesse per i vostri lettori, sarò felice di fornirvi ulteriori dettagli tecnici o rispondere a qualsiasi domanda.

Un cordiale saluto,
[Tuo Nome / Gabriele]
Sviluppatore NoCallCenter
GitHub: https://github.com/iylistenfree/nocallcenter
```

---

## 2. Pitch per IlSoftware.it
* **Destinatari:** `info@ilsoftware.it` (all'attenzione di Michele Nasi).
* **Oggetto:** *NoCallCenter: utility Android open source per bloccare chiamate spam in locale con CallScreeningService*

```text
Gentile Michele, gentile redazione di IlSoftware.it,

Vi scrivo per segnalarvi un piccolo progetto open-source che ho recentemente sviluppato per contrastare il problema sempre più pressante del telemarketing aggressivo su smartphone Android: NoCallCenter (https://github.com/iylistenfree/nocallcenter).

Spesso su IlSoftware affrontate il tema della privacy e delle alternative indipendenti ai servizi commerciali che monetizzano sui dati degli utenti. NoCallCenter nasce proprio come risposta privacy-friendly alle varie app di blacklist cloud:

- Funziona al 100% sul dispositivo: nessun dato o tabulato lascia mai lo smartphone (zero telemetria, zero server).
- Sfrutta le API native Android CallScreeningService e un meccanismo di risposta SMS automatica verso WhatsApp che funge da "filtro umano" contro i risponditori automatici.
- Dispone di gestione smart del budget SMS dell'operatore, protezione anti-flood (1 SMS max ogni 48h) e blocco prefissi internazionali extra-UE ed UE.

Il progetto è completamente gratuito, senza pubblicità, licenziato MIT con codice sorgente trasparente.

Sarei felice se potesse essere uno spunto utile per una segnalazione o un articolo per la vostra community di appassionati e professionisti.

Grazie mille per il tempo e complimenti per il lavoro editoriale che svolgete ogni giorno.

Cordiali saluti,
[Tuo Nome / Gabriele]
https://github.com/iylistenfree/nocallcenter
```

---

## 3. Pitch per HDBlog & SmartWorld (AndroidWorld)
* **Destinatari:** Form contatti / redazione (`redazione@hdblog.it`, `redazione@smartworld.it`).
* **Oggetto:** *Segnalazione app: NoCallCenter, l'app open source italiana per bloccare i call center senza cedere la rubrica*

```text
Ciao al team di HDBlog / SmartWorld,

Vi segnalo una nuova applicazione Android open-source pensata per chi è stanco di ricevere chiamate da call center e bot ma non vuole cedere i dati della propria rubrica ad app commerciali come Truecaller:

NoCallCenter (https://github.com/iylistenfree/nocallcenter)

Caratteristiche salienti:
- 100% Locale & Open Source (licenza MIT): nessun cloud, nessun account, privacy assoluta.
- Rifiuto istantaneo e silenzioso dei numeri non in rubrica tramite CallScreeningService.
- "Filtro WhatsApp": invia un SMS automatico per invitare chi ha un motivo legittimo (es. corrieri, medici) a scrivere su WhatsApp. I centralini commerciali automatici non possono farlo e desistono.
- Protezione salva-credito: imposti il tuo tetto massimo di SMS mensili e l'app sospende l'invio al raggiungimento della soglia.
- Blocco prefissi esteri (extra-UE / UE).

L'APK è scaricabile direttamente dalle GitHub Releases con supporto per aggiornamenti integrati:
https://github.com/iylistenfree/nocallcenter/releases/latest

Spero possa interessare alla vostra rubrica dedicata alle migliori app Android o utility indipendenti!

Buona giornata,
[Tuo Nome / Gabriele]
```
