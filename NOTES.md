### NOTE SU PROGETTO
Lo sviluppo del progetto ha presentato notevoli sfide tecniche, principalmente nella strutturazione dell'intero sistema da zero. Ho optato per un'architettura esagonale per la sua capacità di separare chiaramente gli aspetti di dominio, applicazione e infrastruttura. Questa separazione semplifica l'esecuzione di test unitari, l'evoluzione del sistema e la sostituzione delle tecnologie sottostanti senza influire sul core business.

#### Aree di miglioramento:

* **Copertura dei test**: è necessario aumentare il numero di test unitari e integrati per garantire la qualità e la stabilità del codice.
* **Architettura dei messaggi**: l'implementazione di un'architettura basata sui messaggi consentirebbe di disaccoppiare i componenti del sistema. Ciò fornirebbe un miglioramento della scalabilità e della resilienza del sistema.
* **Gestione della concorrenza**: l'utilizzo di un framework come RxJava potrebbe ottimizzare la gestione di più attività e migliorare le prestazioni in scenari simultanei.

#### Attività in sospeso:

Configurazione su AWS: a causa della mia limitata esperienza nella gestione del cloud, non è stata eseguita la configurazione dell'infrastruttura su AWS. Sebbene abbia eseguito alcuni test di base, è necessario approfondire questo aspetto per garantire un'implementazione efficiente e scalabile."