# Projekt im P-Seminar Informatik

Unser derzeitiger Plan ist es, eine Java basierte Web Anwendung zu erstellen. Diese soll ehemaligen Schülern die Möglichkeit geben sich zu registrieren, um mit dem Gymnasium Waldkraiburg in Kontakt zu bleiben. Das Portal könnte beispielsweise dazu genutzt werden, um Ehemalige Schüler mit interessanten Berufen in den Unterricht einzuladen.

## Gruppen
- Java Entwicklung im Back-End Bereich
- Design des Front-Ends
- Datenschutz, etc.
- Dokumentation, Protokollierung des Projekts
- Testing

## Projekt Setup
Diese Version des Prototypen verwendet keine In-Memory Datenbank, sondern eine "richtige" SQL Datenbank

1. Vorraussetzung: [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (JRE und JDK)
3. Installiere die [PostgreSQL](http://www.enterprisedb.com/products-services-training/pgdownload#windows) Datenbank
  - Wähle während der Installation "admin" als Username und "47bVZ7nt8#c6" als Passwort (Es ist wichtig, dass wir alle die gleichen Daten verwenden)
  - Lasse den vorgegebenen Wert 5432 als Port
  - Starte nach der Installation das Programm "pgAdmin III"
  - Wähle links im Objektbrowser den "PostgreSQL 9.5 (localhost:5432)" Server aus und verbinde dich mit einem Rechtsklick
  - Im Objektbrowser findest du nun "Datenbanken" als Unterpunkt des Servers
  - Erstelle mit einem Rechtsklick eine neue Datenbank mit dem Namen "gwedb"
2. Installiere die [Eclipse](https://www.eclipse.org/downloads/) IDE
  - Öffne Eclipse
  - Wähle unter "Window/Perspective/Open Perspective/Other..." "Git" aus
  - Klicke auf "Clone a Git repository..." und gebe "https://github.com/voidc/pinf-projekt.git" als URI und deine Anmeldedaten für GitHub ein
  - Wenn du das Projekt geclont hast, wechsle oben rechts zurück auf die "Java" Ansicht
  - Wähle "File/Import/Maven/Existing Maven Projects" und gebe den geclonten Ordner als Root Directory an
3. Wenn alles funktioniert hat, kannst du jetzt die Application Klasse im de.gymwak.gwe Package als Java Application ausführen
4. Warte 10-15s bis der Server gestartet ist und öffne "localhost:8080" im Browser

## Resourcen
- [Spring Guides](https://spring.io/guides)
- [YouTube - From 0 to Spring Security 4.0](https://www.youtube.com/watch?v=TjlDbIIJBi8)