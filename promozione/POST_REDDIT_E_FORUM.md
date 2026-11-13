# Post per Reddit, Forum e Community

Questo documento contiene i testi completi pronti da copiare e incollare per far conoscere **NoCallCenter** nelle community dove il problema del telemarketing e della privacy è più sentito.

> **Regola d'oro per le community:** Non presentarti mai come un'azienda che fa marketing. Presentati come un dev indie / utente esasperato che ha creato uno strumento libero per sé e lo condivide con tutti. Rispondi con trasparenza nei commenti a dubbi e feedback tecnici.

---

## 1. Reddit: `r/italyinformatica`

* **Tag / Flair consigliato:** `Progetto` oppure `Sviluppo`
* **Titolo:**
  > Stanco del telemarketing selvaggio e dei database di Truecaller, ho scritto un'app Android open-source che usa un "filtro asimmetrico" (SMS -> WhatsApp)

* **Testo del post:**
```markdown
Ciao a tutti!

Come molti di voi qui dentro, ero arrivato al punto di non sopportare più il telefono che squilla 6 volte al giorno: centralini esteri, robocall con voce registrata, spoofing di numeri cellulari italiani per aggirare il Registro delle Opposizioni.

Le soluzioni esistenti sul mercato non mi convincevano per due motivi:
1. Le app stile *Truecaller* o *Hiya* pretendono abbonamenti e – cosa per me inaccettabile – caricano l'intera rubrica dell'utente su server cloud di terze parti, violando la privacy di tutti i contatti.
2. Il semplice blocco di tutti i numeri sconosciuti non è praticabile: se aspetti un pacco da Amazon, la chiamata dell'ASL o dell'idraulico, perdi comunicazioni importanti.

Così ho sviluppato **NoCallCenter**, un'app Android nativa (Kotlin + Jetpack Compose) open-source con licenza MIT che funziona al 100% in locale sul dispositivo.

### 💡 L'idea: il "filtro umano" asimmetrico
Invece di affidarmi a liste nere centralizzate (che con lo spoofing dei numeri non servono più a nulla), l'app sfrutta un principio asimmetrico:
1. Arriva una chiamata da un numero **non presente in rubrica**.
2. Il `CallScreeningService` nativo di Android la rifiuta all'istante, in silenzio (il telefono non squilla e non vibra).
3. L'app invia immediatamente un SMS automatico configurabile: *"Numero iscritto al Registro delle Opposizioni. Se sei un corriere, un medico o un conoscente, scrivimi subito su WhatsApp a questo numero."*
4. **Cosa succede:**
   - I centralini automatici dei call center (*predictive dialer*) non leggono gli SMS e non usano WhatsApp: chiamata persa e drop definitivo.
   - Il corriere di GLS, il medico o l'amico col numero nuovo leggono l'SMS e ti scrivono in 10 secondi su WhatsApp.

### 🛡️ Privacy e funzionalità:
- **Zero cloud, zero server, zero tracker:** tutto gira in locale tramite SQLite/Room.
- **Salva-Credito SMS & Anti-Flood:** puoi impostare un budget mensile di SMS (es. se hai 50 SMS/mese, raggiunta la soglia l'app continua a silenziare senza mandare SMS). Inoltre, non invia più di 1 SMS ogni 48 ore allo stesso numero.
- **Blocco Prefissi Esteri:** toggle per rifiutare direttamente prefissi Extra-UE e/o UE.
- **Richiama dal Registro:** possibilità di richiamare o verificare i numeri filtrati direttamente dall'app.

### ⚠️ Perché è distribuita come APK (GitHub) e non su Google Play:
Google non concede l'autorizzazione di invio SMS in background alle app indipendenti a meno che non siano l'app SMS predefinita del sistema (e non gradisce i link liberi per donazioni). Per questo l'app è distribuita direttamente via APK con un updater interno che controlla le release GitHub.

Il codice sorgente è interamente trasparente su GitHub:
🔗 Repository: https://github.com/iylistenfree/nocallcenter
📥 Download APK: https://github.com/iylistenfree/nocallcenter/releases/latest

Mi farebbe molto piacere ricevere il vostro parere tecnico, feedback sul codice o segnalazioni di edge case che non ho considerato. Grazie a chi vorrà provarla!
```

---

## 2. Reddit: `r/italy`

* **Tag / Flair consigliato:** `Tecnologia` oppure `Cultura Digitale`
* **Titolo:**
  > Come ho risolto (davvero) l'incubo quotidiano dei call center: NoCallCenter, un'app gratuita e open-source

* **Testo del post:**
```markdown
Ciao r/italy!

Negli ultimi mesi la situazione chiamate spam in Italia è diventata ingestibile. Ricevere 4-5 squilli al giorno per finte offerte luce/gas o trading online è la normalità per chiunque abbia un cellulare. L'iscrizione al Registro delle Opposizioni ormai non ferma più i call center aggressivi, che usano software esteri e clonano numeri a caso (*spoofing*).

Fino a ieri le alternative erano due:
- Sopportare o bloccare i numeri uno a uno (inutile).
- Installare Truecaller, pagando e soprattutto caricando l'intera propria rubrica sui loro server (cosa folle per la privacy).

Essendo uno sviluppatore, ho provato a ribaltare il problema: **come faccio a distinguere un call center da un corriere o da un dottore che mi chiama da un numero sconosciuto?**

La risposta è semplice: **un centralino bot non usa WhatsApp, un essere umano sì.**

Ho quindi creato **NoCallCenter**, un'applicazione gratuita e open-source:
1. Quando chiama un numero che non avete in rubrica, l'app zittisce la chiamata sul nascere.
2. Invia in automatico un breve SMS che dice: *"Numero iscritto al Registro Opposizioni. Se sei un corriere o un conoscente, scrivimi su WhatsApp"*.
3. Il call center cade nel vuoto. Il corriere di Amazon o il medico vi manda un messaggio su WhatsApp e risolvete la cosa subito.

L'app è al 100% gratuita, non ha pubblicità, non raccoglie un singolo dato (funziona solo all'interno del telefono, nessun server) ed è open-source con codice verificabile da chiunque.

Se volete provarla, trovate il codice e il download dell'APK qui:
👉 https://github.com/iylistenfree/nocallcenter

*(Nota: dato che invia SMS in automatico, non può stare sul Google Play Store per via delle loro policy rigide, quindi va installata come APK; all'interno dell'app e su GitHub c'è la mini-guida di 30 secondi su come abilitare il permesso su Android 13/14/15).*

Spero possa far ritrovare un po' di pace anche a voi come l'ha fatta ritrovare a me!
```

---

## 3. Forum: **FibraClick** (Sezione *Telefonia Mobile* o *Off-Topic / Tech*)

* **Perché FibraClick:** È la community italiana di riferimento per appassionati di reti, telco, privacy e normative AGCOM. Apprezzeranno particolarmente la precisione tecnica, l'uso di `CallScreeningService` e la trasparenza.
* **Titolo discussione:**
  > [Progetto Open-Source] NoCallCenter: contrastare il telemarketing aggressivo con CallScreeningService e filtro WhatsApp

* **Testo del post:**
```text
Ciao a tutti,

Seguo da tempo le discussioni sul forum relative alle campagne di telemarketing selvaggio, all'inefficacia parziale del Registro Pubblico delle Opposizioni contro i risponditori predittivi esteri e allo spoofing del CLI (Calling Line Identification) che rende inutili i classici database a blacklist.

Volevo condividere con voi un progetto open-source che ho sviluppato per Android: **NoCallCenter**.

L'idea alla base è quella di creare una barriera asimmetrica a zero impatto sulla privacy:
- Utilizza il `CallScreeningService` introdotto nativamente nelle API Android recenti per intercettare i numeri non registrati in rubrica prima che il framework avvii la suoneria o la notifica a schermo.
- Invece di interrogare database cloud esterni (evitando il tracciamento dei tabulati o l'upload della rubrica stile Truecaller), l'app chiude la chiamata localmente e invia un SMS di risposta automatico che invita il chiamante legittimo (es. logistica, studi medici, utility) a ricontattare tramite WhatsApp.
- È presente una gestione accurata della quota SMS (per chi ha piani con plafond limitato di messaggi, disattivando l'invio al raggiungimento del tetto) e una protezione anti-flood (massimo 1 SMS ogni 48h verso lo stesso numero).
- Dalla v1.2.0 è stato integrato anche il blocco mirato dei prefissi internazionali (extra-UE e UE).

Il codice sorgente è licenziato MIT, scritto in Kotlin con Jetpack Compose e SQLite Room:
Repository: https://github.com/iylistenfree/nocallcenter
Release APK: https://github.com/iylistenfree/nocallcenter/releases/latest

Per via delle restrizioni di Google sulle autorizzazioni SEND_SMS in background per le app non-default dialer, la distribuzione avviene tramite GitHub Releases con update-checker integrato.

Mi farebbe molto piacere ricevere opinioni, test di compatibilità con i vari dialer OEM (Samsung OneUI, Xiaomi HyperOS, Pixel) ed eventuali spunti di miglioramento!
```

---

## 4. Reddit: `r/fossdroid` & `r/privacy` (In Inglese)

* **Titolo:**
  > [FOSS] NoCallCenter: A privacy-first Android call blocker that uses an SMS/WhatsApp "Turing test" against telemarketers

* **Testo del post:**
```markdown
Hi everyone,

Like many of you, I got sick of robocalls and aggressive telemarketing. Commercial apps like Truecaller are spyware masquerading as utilities—harvesting your entire contact list and uploading it to third-party databases.

I built **NoCallCenter**, a 100% on-device, open-source Android app (MIT license) built with Kotlin & Jetpack Compose.

### The Idea: The Asymmetric Human Filter
Instead of relying on outdated cloud blacklists that easily get bypassed with caller ID spoofing:
1. When an unsaved number calls, Android's native `CallScreeningService` drops it instantly without ringing.
2. The app automatically dispatches a customizable SMS: *"This number is protected. If you are a courier, client, or friend, message me on WhatsApp."*
3. **The result:** Automated predictive dialers and spam bots cannot read SMS or open WhatsApp, so spam calls simply die. Legitimate humans (delivery drivers, clinics, unexpected acquaintances) text you within seconds on WhatsApp.

### Highlights:
- **Zero Cloud / Zero Tracking:** No accounts, no external telemetry, your data never leaves the device.
- **SMS Budgeting:** Set a monthly SMS quota matching your carrier plan so you never get billed extra.
- **Anti-Flood Protection:** Max 1 SMS per phone number every 48 hours.
- **International Prefix Filtering:** Block extra-EU or foreign calls with one toggle.
- **In-App Updater:** Checks GitHub Releases directly for updates.

Source code & APK releases:
🔗 GitHub: https://github.com/iylistenfree/nocallcenter
📦 Latest APK: https://github.com/iylistenfree/nocallcenter/releases/latest

Feedback and contributions are very welcome!
```
