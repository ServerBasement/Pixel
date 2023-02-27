# Pixel

## Introduzione

Pixel è una libreria Java per la gestione automatica del matchmaking e delle istanze per server NON Multi-Arena usata
nei minigames di minecraft.
Permette l'integrazione con il proprio minigame in modo da controllare le code per i player (sono supportate infinite
tipologie) e la creazione e distruzione dei match.
Pixel supporta più match su una singola istanza e più lobby.

## Sviluppo

Pixel è stato pensato e creato da @BowYard. Successivamente sistemato e mantenuto da @ohAlee
E' organizzato in moduli:

- master
- instance
- common

Il modulo master gestisce tutta la parte della lobby
Il modulo instance gestisce ogni istanza
Il modulo common è in comune al master e all'instance

## Implementazione

Per usare Pixel nel proprio minigame è altamente consigliato gestire il proprio progetto nel seguente modo:

- creare un progetto multi modulo con gradle
- create i 3 moduli: common, master, instance
- aggiungere le varie dipendenze
- aggiungere Pixel come dipendenza

### Gradle

```bash
  implementation "it.bowyard.pixel:master:1.0"    # master module
  implementation "it.bowyard.pixel:instance:1.0"  # instance module
  implementation "it.bowyard.pixel:common:1.0"    # common module
```

### Master

```java

```

### Instance

```java

```
