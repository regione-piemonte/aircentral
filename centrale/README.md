# Prodotto
Centrale

# Descrizione del prodotto
Questo prodotto fa parte della suite **SRRQA - Rilevamento della Qualità dell'Aria**.

Software sviluppato in Java che gestisce il Centro Operativo Provinciale (COP) per acquisire i dati dalle stazioni di misura della rete di qualità dell'aria.

Il software del centro operativo interroga periodicamente (tipicamente ogni ora) le stazioni di misura per lo scarico delle misure e delle informazioni di stato. A tal fine il software di stazione espone un web service basato sul protocollo http, usando sempre la porta 55000, che rende disponibili le informazioni in formato testo. Questo web service consente anche di inviare alla stazione alcune informazioni di configurazione. 

I dati e le informazioni scaricate dalle stazioni di misura vengono inseriti in una banca dati di appoggio (DB Centrale), dedicata unicamente a questa applicazione. Essa provvede anche a riversare le misure acquisite dalle stazioni nel database principale (DB Aria), utilizzato dal resto del sistema SRRQA. Questa soluzione ha consentito di mantenere la struttura del DB Aria, già esistente prima dello sviluppo del software Centrale e facilita l'eventuale riuso del software in altre reti di misura.

Il software Centrale è dotato di una interfaccia utente di tipo web. L'interfaccia web è esposta tramite protocollo HTTP e utilizza un sistema di autenticazione tramite inserimento di utente e password. Il meccanismo di autenticazione usato si basa su una banca dati (DB Auth), ospitata su un server in CSI Piemonte. In caso di impossibilità di accesso a tale banche dati è possibile accedere comunque all'applicazione, usando una password di sblocco per l'accesso locale.

Il software Centrale implementa anche un meccanismo di proxy che rende fruibile l'interfaccia web delle stazioni di misura a tutti gli utenti dell'interfaccia web del software Centrale, anche nel caso in cui le loro macchine non abbiano la possibilità di accedere direttamente alle stazioni di misura.

# Configurazioni iniziali

Creare il database dettagliato nel prodotto DbCentrale.

Inserire le credenziali di accesso a tale database nel file `conf\db_config.xml` come ad esempio:

```xml
<database>
		<description>Database Centrale</description> 
		<dbName>dbcentrale</dbName>
		<address>ip_del_database</address>
		<port>porta_del_database</port>
		<user>username</user>
		<password>password</password>
		<engine>postgres</engine>
</database>
<database>
		<description>Database Autenticazione</description> 
		<dbName>dbAuth2</dbName>
		<address>ip_del_database</address>
		<port>porta_del_database</port>
		<user>username</user>
		<password>password</password>
		<engine>postgres</engine>
</database>
```
In questo file si possono configurare eventuali database esterni come un database dell'autenticazione o un database per la copia dei dati.
Per appoggiarsi ai database esterni è necessario modificare il sorgente del centrale per adattarsi alla struttura dei database.

Per predisporre il database dell'autenticazione è necessario creare il database dettagliato nel prodotto AIRAUTH.

Per eseguire il centrale e' necessario assicurarsi che l'utente copadmin abbia nel PATH la JAVA_HOME; in seguito si puo' eseguire lo script `run.sh`

# Getting Started

Eseguire il target **release**  di ANT (tramite OpenJDK 1.6).

Per impostare eventuali parametri tipici di ambienti di test o produzione e' possibile specificare la proprieta':
* `-Dtarget=prod`: per deployare su ambiente di produzione (file di properties `prod.properties`)

L'esecuzione di questo target crea un elenco di file tgz nella cartella `dist/prod` del workspace.

# Prerequisiti di sistema

La macchina sulla quale deve essere installato il Centrale, deve avere installati:
* Distribuzione Linux
* Java Virtual Machine 1.6 o superiore
* Postgres 7.2 o superiore

E' necessario creare il database dettagliato nel prodotto DbCentrale per poter utilizzare l'applicativo.

Al fine di visualizzare l'interfaccia utente del Centrale dalla macchina, è necessario che sia installato Firefox 3 o superiore.

Il Centrale &egrave; stato testato con Java Virtual Machine 1.6 e Postgres 8.1.

Fare riferimento al file BOM.csv per verificare l'elenco delle librerie esterne utilizzate in questo software.

# Installazione 
Creare sulla macchina su cui si vuole eseguire il software del centrale un utente linux chiamato `copadmin` che abbia la cartella home.

Copiare i seguenti archivi creati con il target di ANT nella cartella `/home/copadmin`

* centrale_bin_....tgz
* centrale_cfg_...tgz

Estrarre gli archivi come nell'esempio:

```bash
>tar -xvzf centrale_bin_V3.3.5_20220128.tgz
>tar -xvzf centrale_cfg_V3.3.5_20220128.tgz
```
In questo modo si creera' la cartella `/home/copadmin/centrale` e questa conterra' tutto il software necessario.

## Avvio interfaccia grafica
Se il software è stato avviato correttamente e' possibile consultarne l'interfaccia grafica:essa e' accessibile all'indirizzo:

`http://IP:porta/CentraleUI.html`

ad esempio con i parametri di default:

[http://localhost:55000/CentraleUI.html](http://localhost:55000/CentraleUI.html) 

Per un accesso locale (senza database su cui sono memorizzate le informazioni degli utenti) è necessario spuntare la casella "Accesso locale" e digitare soltanto la password che si trova nel file di configurazione `conf/login.xml`

Per l'accesso tramite utente e password configurati nell'applicativo AIRAUTH - Webauth è necessario che l'utente appartenga al gruppo "centrale" e che questo gruppo abbia la funzione "centrale" con permessi di "scrittura" e "avanzata" impostati a  "NO".

# Esecuzione dei test
Sono stati eseguiti test di vulnerabilità DAST e SAST e non sono state rilevate vulnerabilita' gravi.


# Versioning 
Per il versionamento del software si usa la tecnica Semantic Versioning (http://semver.org).

# Authors 
La lista delle persone che hanno partecipato alla realizzazione del software sono qui elencate o si fa riferimento a quanto riportato nel file AUTHORS.txt.

# Copyrights 
L'elenco dei titolari del software sono indicati nel file Copyrights.txt

# License 
SPDX-License-Identifier: EUPL-1.2-or-later

Vedere il file LICENSE per i dettagli.
