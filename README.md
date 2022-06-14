# Prodotto
Aircentral
# Descrizione del prodotto
Questo prodotto fa parte della suite **SRRQA - Rilevamento della Qualità dell'Aria**.

Il Centro Operativo Provinciale è un concentratore di dati provenienti delle stazioni di misura della Rete Regionale di Rilevamento della Qualità dell'Aria, dislocate sul territorio della Regione Piemonte; acquisisce i dati e le informazioni di stato provenienti dalle stazioni di misura.  L'applicazione permette un controllo dello stato della rete in tempo reale, la consultazione dei dati e lo scarico dei dati su un database prestabilito. Permette anche di collegarsi all'interfaccia grafica della stazione (prodotto airstation).
Gli utenti dell'applicazione sono i tecnici di ARPA o delle ditte di manutenzione incaricate da ARPA  che si occupano della rete di monitoraggio.

Le componenti di questo prodotto sono:
* aircentral-centrale: <https://github.com/regione-piemonte/aircentral-centrale>
* aircentral-dbcentrale: <https://github.com/regione-piemonte/aircentral-dbcentrale>

# Prerequisiti di sistema
Il software **centrale** funziona appoggiandosi ad un database interno per salvare i dati e le informazioni di stato utili all'applicativo per funzionare, pertanto il centrale non puo' funzionare senza la componente **dbcentrale**.
Inoltre il centrale deve essere utilizzato con il prodotto **airstation** in quanto l'obiettivo è proprio interagire con il software **periferico**.

Il prodotto airstation si trova seguendo questo link: <https://github.com/regione-piemonte/airstation>

La componente periferico si trova seguendo questo link: <https://github.com/regione-piemonte/airstation-periferico>

Fare riferimento al file BOM.csv di ciascuna componente per verificare l'elenco delle librerie esterne utilizzate in questo software.

# Versioning
Per il versionamento del software si usa la tecnica Semantic Versioning (http://semver.org).

# Authors
La lista delle persone che hanno partecipato alla realizzazione del software sono  elencate nel file AUTHORS.txt.

# Copyrights
L'elenco dei titolari del software sono indicati nel file Copyrights.txt

# License 
SPDX-License-Identifier: EUPL-1.2-or-later

Vedere il file LICENSE per i dettagli.