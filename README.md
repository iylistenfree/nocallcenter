# NoCallCenter 📵🛡️

[Android](https://www.android.com/) [Kotlin](https://kotlinlang.org/) [UI](https://developer.android.com/jetpack/compose) [Privacy](https://github.com/) [PayPal](https://paypal.me/gabrilc) [License: MIT](https://opensource.org/licenses/MIT)

> [**🇮🇹 Leggi in Italiano**](#-italiano) \| [**🇬🇧 Read in English**](#-english)

---

<a id="-italiano"></a>

## 🇬🇧 English

### 😤 The Problem: Aggressive Telemarketing \& Spam Calls

Unsolicited commercial robocalls and aggressive telemarketers have made our phones unbearable. Spoofed numbers bypass spam lists, and popular solutions like *Truecaller* require intrusive subscriptions while **uploading your entire contact list to third-party cloud servers**.

### 💡 The Solution: NoCallCenter

**NoCallCenter** is an open-source, privacy-first Android application that runs **100% on your device (no cloud, no tracking)**. It introduces an asymmetrical barrier to spam:

```
flowchart TD
    A["📞 Incoming Call"] --> B{"Is caller in Contacts?"}
    B -- YES --> C["🔔 Rings normally"]
    B -- NO --> D["🔇 Call declined instantly"]
    D --> E["💬 Auto-SMS Sent:<br/>'Number listed on Do Not Call Registry.<br/>For legitimate inquiries, reach me on WhatsApp.'"]
    E --> F{"Who was calling?"}
    F -- Spam Bot / Call Center --> G["❌ Cannot use WhatsApp or read SMS: Call dropped!"]
    F -- Courier / Doctor / Friend --> H["✅ Contacts you immediately on WhatsApp!"]
```

---

### ✨ Features

- 🛡️ **Instant Silent Call Screening:** Leverages Android's native `CallScreeningService`. Calls from unsaved numbers are dismissed before your phone even vibrates or rings.
- 🤖 **The WhatsApp "Human Filter":** Predictive dialers cannot interact with WhatsApp. Legitimate callers (delivery drivers, clinics, clients) will read the SMS and message you on WhatsApp right away.
- 💰 **Smart SMS Budgeting:** If your carrier plan has a limited monthly SMS quota (e.g. 50/month), configure your threshold (e.g. 40 SMS) and billing renewal day. Calls will still be screened silently once the limit is met without extra carrier costs.
- ⏱️ **48-Hour Anti-Flood:** Protects your SMS quota by sending a maximum of 1 SMS per phone number every 48 hours.
- 🔒 **100% Privacy-First:** No accounts, no cloud database, no analytics. Your contacts and logs never leave your device.

---

### ⚠️ Installation Guide (Bypassing Google's Sideload Restrictions)

Because NoCallCenter sends automatic screening SMS and remains entirely independent of Google's payment ecosystem, it is distributed directly as an APK.

On **Android 13, 14, and 15**, Google enables a security feature called **"Restricted Settings"** for manually installed apps, which greys out the *"Allow"* toggle for SMS permissions.

#### 📲 How to enable it (takes 30 seconds):

1. **Download and install** [`nocallcenter.apk`](https://github.com/iylistenfree/nocallcenter/releases).
2. On first launch, tap **"Open App Info Settings"** *(or go to phone Settings \> Apps \> NoCallCenter)*.
3. Open **Permissions** \> tap **SMS** \> **tap "Allow" (even if greyed out and unselectable)**: Android will show a "Restricted setting" alert. Tap **OK**.
4. Go back one screen to *App Info*: tap the **three vertical dots (⋮)** in the top-right corner. *(The hidden option will now be visible!)*
5. Tap **"Allow restricted settings"** and confirm with your fingerprint or device PIN.
6. Return to **Permissions** \> **SMS** and toggle it to **"Allow"**.
7. Return to the app and set NoCallCenter as your **Default Call Screening App**. You're protected!

---

### ☕ Support the Project

NoCallCenter is completely free, ad-free, and open-source. If it restored your peace of mind and saved your time, consider buying the developer a coffee:

👉 [**Buy me a coffee on PayPal (@gabrilc)**](https://paypal.me/gabrilc)

---

## 

## 🇮🇹 Italiano

### 😤 Il Problema: L'incubo quotidiano del telemarketing

In Italia le chiamate commerciali moleste sono fuori controllo. Tra numeri cellulari clonati (*spoofing*), centralini esteri e risponditori automatici (*predictive dialer*), il Registro Pubblico delle Opposizioni è spesso inefficace. Le app tradizionali (come *Truecaller*), d'altra parte, pretendono abbonamenti costosi e **caricano l'intera tua rubrica su server esteri**, violando la privacy tua e dei tuoi contatti.

### 💡 La Soluzione: NoCallCenter

**NoCallCenter** è un'app Android leggera, open-source e operante al **100% sul tuo dispositivo (zero cloud, nessun dato trasmesso)** che ribalta le regole del gioco con un principio asimmetrico:

```
flowchart TD
    A["📞 Chiamata in Arrivo"] --> B{"Il numero è in Rubrica?"}
    B -- SÌ --> C["🔔 Squilla normalmente"]
    B -- NO --> D["🔇 Chiamata Rifiutata all'istante"]
    D --> E["💬 SMS Automatico:<br/>'Numero iscritto al Registro Opposizioni.<br/>Se sei un corriere o conoscente, scrivimi su WhatsApp'"]
    E --> F{"Chi ha chiamato?"}
    F -- Bot / Call Center --> G["❌ Non legge gli SMS e non usa WhatsApp: Chiamata persa!"]
    F -- Corriere / Medico / Amico --> H["✅ Ti scrive subito su WhatsApp!"]
```

---

### ✨ Caratteristiche Principali

- 🛡️ **Filtro Nativo Silenzioso:** Sfrutta il `CallScreeningService` ufficiale di Android. Se un numero non è salvato nei tuoi contatti, la chiamata viene respinta sul nascere prima ancora del primo squillo.
- 🤖 **Il "Filtro Umano" di WhatsApp:** I centralini automatici dei call center non usano WhatsApp e non leggono gli SMS. Corrieri, banche, medici o conoscenti che non avevi in rubrica leggeranno l'SMS e ti scriveranno immediatamente su WhatsApp.
- 💰 **Salva-Credito SMS (Smart Budgeting):** Hai solo 50 SMS al mese? Imposta la tua soglia mensile (es. 40 SMS) e il giorno di rinnovo dell'offerta. Raggiunta la soglia, l'app continuerà a bloccare le chiamate sospendendo l'invio degli SMS. Se hai SMS illimitati, puoi disattivare il limite con un tocco.
- ⏱️ **Protezione Anti-Flood (48h):** Impedisce ai centralini insistenti di prosciugare il credito: massimo 1 SMS ogni 48 ore allo stesso numero.
- 🔒 **100% Privacy \& Zero Server:** Nessun account, nessuna registrazione, nessun server cloud. Tutti i dati restano sul tuo smartphone.

---

### ⚠️ Guida all'Installazione (Come superare le restrizioni di Google)

Poiché l'app invia SMS automatici in background e rispetta la tua privacy senza intermediari, **non può stare sul Google Play Store** (Google impone vincoli burocratici rigidi sugli SMS e vieta i link per donazioni libere tra privati).

Su **Android 13, 14 e 15**, Google ha introdotto una protezione chiamata **"Impostazioni con restrizioni"** che rende la voce *"Consenti"* per gli SMS grigia e non cliccabile quando installi un file APK.

#### 📲 Procedura di sblocco (30 secondi):

1. **Scarica e installa** l'ultima versione di [`nocallcenter.apk`](https://github.com/iylistenfree/nocallcenter/releases).
2. All'apertura dell'app, tocca **"Apri Informazioni App"** nella schermata di benvenuto *(oppure vai in Impostazioni del telefono \> App \> NoCallCenter)*.
3. Entra in **Autorizzazioni** \> tocca **SMS** \> **tocca "Consenti" (anche se è in grigio e non cliccabile)**: comparirà un popup di sicurezza di Android. Tocca **OK**.
4. Torna indietro di una schermata (su *Informazioni app*): tocca i **3 puntini verticali in alto a destra (⋮)**. *Ora la voce sarà magicamente apparsa!*
5. Tocca **"Consenti impostazioni con restrizioni"** e conferma con la tua impronta digitale o PIN.
6. Rientra in **Autorizzazioni** \> **SMS** e tocca **"Consenti"**.
7. Torna nell'app e impostala come **App Filtro Chiamate predefinita**. Fatto!

---

### ☕ Ti piace NoCallCenter? Offrimi un caffè!

NoCallCenter è gratuita, senza abbonamenti e senza pubblicità. Se ti ha fatto ritrovare la pace eliminando le chiamate commerciali moleste, puoi supportare lo sviluppatore con un caffè tra amici su PayPal:

👉 [**Offrimi un caffè con PayPal (@gabrilc)**](https://paypal.me/gabrilc)

---

\


## 📄 License

Released under the [MIT License](LICENSE).
