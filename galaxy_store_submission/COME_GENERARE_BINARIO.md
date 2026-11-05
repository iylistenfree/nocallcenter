# Come Generare il File Binario (APK o AAB) per il Galaxy Store

Samsung Galaxy Store accetta entrambi i formati:
* **`.aab` (Android App Bundle):** È il formato **consigliato** (produce download più leggeri per gli utenti perché Samsung crea APK ottimizzati per ogni modello di smartphone).
* **`.apk`:** È ugualmente supportato.

> ⚠️ **IMPORTANTE:** Il file **non deve essere in modalità Debug** (`android:debuggable="false"`). Deve essere compilato in variante **Release** e firmato con una chiave keystore.

---

## Metodo 1: Tramite Android Studio (Il più semplice e guidato)

Se utilizzi Android Studio:

1. Apri il progetto in **Android Studio**.
2. Dal menu in alto, clicca su **Build** > **Generate Signed Bundle / APK...**
3. Scegli **Android App Bundle** (oppure **APK**) e clicca su **Next**.
4. Nella schermata di firma (*Key store path*):
   * Se hai già una chiave di rilascio, selezionala.
   * Se non l'hai mai creata, clicca su **"Create new..."**:
     * *Key store path:* Salva il file (es. `nocallcenter-release-key.jks`) in un luogo sicuro.
     * *Password:* Inserisci una password sicura sia per il keystore che per la chiave.
     * *Alias:* Es. `nocallcenter`.
     * *Validity:* Lascia 25 anni.
     * *Certificate:* Compila almeno il campo "First and Last Name".
5. Clicca su **Next**.
6. Seleziona la variante **release**.
7. Clicca su **Create**.
8. Al termine della compilazione, Android Studio ti mostrerà un popup con il link alla cartella contenente il file generato (es. `app-release.aab` o `app-release.apk`). Quello è il file da caricare sul Galaxy Store!

---

## Metodo 2: Tramite Riga di Comando (Gradle)

Se hai già configurato un keystore nel tuo `app/build.gradle.kts` o nelle variabili d'ambiente:

### Per generare l'App Bundle (.aab):
```powershell
.\gradlew bundleRelease
```
Il file generato si troverà in:
`app/build/outputs/bundle/release/app-release.aab`

### Per generare l'APK (.apk):
```powershell
.\gradlew assembleRelease
```
Il file generato si troverà in:
`app/build/outputs/apk/release/app-release.apk`

---

## Verifica del Binario prima del caricamento

Prima di caricarlo sul portale Samsung:
* Verifica che il `versionCode` e il `versionName` (in `app/build.gradle.kts`) siano aggiornati.
* Assicurati di **conservare con cura il file `.jks` della chiave di firma** e la relativa password: ogni futuro aggiornamento di NoCallCenter sullo store dovrà essere firmato tassativamente con la medesima chiave.
