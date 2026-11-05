# Note per i Revisori e Tester di Samsung (Reviewer Notes & Test Instructions)

Nel portale **Samsung Seller Portal**, prima di premere *Submit*, c'è un campo dedicato:  
👉 **"Review Comments" / "Notes to Reviewer"** (Note per i tester).

Incolla il seguente testo (bilingue Inglese/Italiano) in quel campo per spiegare esattamente lo scopo dell'applicazione e le istruzioni di collaudo:

---

### Testo da incollare nel campo "Review Comments":

```text
[ENGLISH VERSION]
Dear Samsung App Review Team,

NoCallCenter is a privacy-first, on-device call screening utility and telemarketing blocker designed to protect users from persistent commercial spam and predictive dialers.

CORE FUNCTIONALITY & PERMISSIONS RATIONALE:
1. CallScreeningService (android.permission.BIND_SCREENING_SERVICE):
   - Fundamental core requirement. It allows the app to act as the default Call Screening role (RoleManager.ROLE_CALL_SCREENING), intercepting incoming calls silently before they vibrate or ring.
2. READ_CONTACTS:
   - Queried 100% locally on the device to check whether the incoming phone number belongs to the user's saved contacts. If saved, the call rings normally. If unsaved, the call is screened. No contact data is ever uploaded or transmitted outside the device.
3. SEND_SMS:
   - Used exclusively to send an automated single courtesy SMS to the screened caller inviting legitimate people (couriers, doctors, acquaintances) to reach out via WhatsApp (since telemarketing bots cannot use WhatsApp).
   - The user has full control over this feature: it can be toggled on/off, customized, and is strictly safeguarded by an anti-flood limiter (max 1 SMS per 48 hours per number) and an optional monthly SMS budget limiter.
4. READ_PHONE_STATE & POST_NOTIFICATIONS:
   - Used to receive call state events and to display non-intrusive notifications about screened calls and SMS budget status.

HOW TO TEST:
1. Launch the app. On the first launch, tap "Imposta come App Predefinita" (Set as Default Screening App) and confirm the system dialog.
2. The Dashboard will show "Filtro Chiamate Attivo" (Call Filter Active).
3. Test Call 1 (Whitelisted): Place an incoming call from a number currently saved in the device's Contacts. Result: The phone rings normally.
4. Test Call 2 (Screened): Place an incoming call from an unsaved number. Result: The call is silently dismissed without ringing. An entry appears in the "Registro" (History) tab. If SMS is enabled, the courtesy SMS is queued/sent according to anti-flood rules.
5. All core features work out-of-the-box without requiring any user account, registration, or internet connection.

Thank you very much for your time and review!

=======================================================
[VERSIONE ITALIANA]
Gentile Team di Revisione Samsung,

NoCallCenter è un'utilità di filtro chiamate e blocco anti-telemarketing operante al 100% in locale sul dispositivo.

SPIEGAZIONE PERMESSI E FUNZIONALITÀ:
- BIND_SCREENING_SERVICE: Necessario per impostare l'app come servizio di Call Screening predefinito (RoleManager).
- READ_CONTACTS: Interrogato solo localmente in RAM per discriminare i contatti salvati (che squillano normalmente) dai numeri sconosciuti (che vengono respinti). Nessun dato lascia mai lo smartphone.
- SEND_SMS: Utilizzato esclusivamente per inviare l'SMS automatico di cortesia che invita chi chiama a scrivere su WhatsApp, con rigorosi controlli salva-credito e protezione anti-flood (massimo 1 SMS ogni 48 ore allo stesso numero).

ISTRUZIONI PER IL TEST:
1. Aprire l'app e toccare "Imposta come App Predefinita".
2. La Dashboard indicherà "Filtro Chiamate Attivo".
3. Chiamando da un numero salvato in rubrica, il telefono squilla normalmente.
4. Chiamando da un numero sconosciuto, la chiamata viene respinta sul nascere e registrata nella scheda "Registro".
```
