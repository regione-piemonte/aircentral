# Prodotto
DbCentrale

# Descrizione del prodotto
Questo prodotto fa parte della suite **SRRQA - Rilevamento della Qualità dell'Aria**.

Database interno per memorizzare le informazioni del funzionamento interno del prodotto Centrale

# Configurazioni iniziali

Per eseguire il centrale e' necessario assicurarsi che l'utente copadmin abbia nel PATH la JAVA_HOME; in seguito si puo' eseguire lo script `run.sh`

# Getting Started

Eseguire il target **distribution**  di ANT (tramite OpenJDK 1.6).

Per impostare eventuali parametri tipici di ambienti di test o produzione e' possibile specificare la proprieta':
* `-Dtarget=prod`: per deployare su ambiente di produzione (file di properties `prod.properties`)

L'esecuzione di questo target crea un file tgz nella cartella `dist/prod` del workspace.

# Prerequisiti di sistema

La macchina sulla quale deve essere installato il dbCentrale, deve avere:
* Distribuzione Linux
* Postgres 7.2 o superiore

Il dbCentrale &egrave; stato testato con Postgres 8.1.

# Installazione

Creare una crtella di appoggio dove poter scompattare l'archivio  creato con il target di ANT nella cartella home di postgres

* dbcentrale_V....tgz

Estrarre l' archivio come nell'esempio:

```bash
>tar -xvzf dbcentrale_V3.3.1_20220209.tgz
```
Nella cartella ci saranno i file necessari per la creazione e l'inizializzazione della banca dati.

Eseguire come utente postgres le istruzioni contenute nel file `createdbcentrale.sql` per creare la banca dati; se richiesta la password per il ruolo sceglierne una a proprio piacimento (ad esempio mypass).
Per creare le tabelle del DB, eseguire:

```bash
psql dbcentrale centrale
\i sql/dbcentrale.sql
```
Compilare le tabelle cop, phisical_dimension, alarm_name, analyzer_alarm_type, avg_period, measure_unit e parameter con i rispettivi file di dump e il comando

```bash
\i sql/cop.dump
\i sql/phisical_dimension.dump
\i sql/alarm_name.dump
\i sql/analyzer_alarm_type.dump
\i sql/avg_period.dump
\i sql/comeasure_unitp.dump
\i sql/parameter.dump
```

Aggiornare nella tabella cop il campo cop_ip con l'indirizzo IP della macchina su cui è installato in prodotto Centrale.

Compilare la tabella modem con i modem presenti sul cop ad esempio: 

```sql
INSERT INTO modem (device_id, shared_line) VALUES ('ttyS0', 1);
```

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
