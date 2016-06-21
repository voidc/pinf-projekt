# Projekt im P-Seminar Informatik

Unser derzeitiger Plan ist es, eine Java basierte Web Anwendung zu erstellen. Diese soll ehemaligen Schülern die Möglichkeit geben sich zu registrieren, um mit dem Gymnasium Waldkraiburg in Kontakt zu bleiben. Das Portal könnte beispielsweise dazu genutzt werden, um Ehemalige Schüler mit interessanten Berufen in den Unterricht oder zu Veranstaltungen, wie dem Berufsinformationsnachmittag, einzuladen.

## Gruppen
- Java Entwicklung im Back-End Bereich und HTML-Entwicklung im Front-End Bereich
- Datenschutz, Nutzungsrichtlinien, etc.
- Dokumentation, Protokollierung des Projekts
- Testing

## Roadmap
- [x] Nachricht an mehere Nutzer (an ganzen Jahrgang)
- [x] Link in der Mail um über GWE zu antworten (keine direkte Antwort per Mail -> E-Mails bleiben privat)
- [ ] Weitere Einstellungen für Nutzer: E-Mail für andere sichtbar machen, Nachrichten zulassen, E-Mail-Benachrichtigungs-Einstellungen, Account löschen ...
- [ ] Buxfixes
- [x] Deployment auf den Schulserver

## Projekt Setup
Diese Version des Prototypen verwendet keine In-Memory Datenbank, sondern eine "richtige" SQL Datenbank

1. Vorraussetzung: [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (JRE und JDK)
3. Installiere die [PostgreSQL](http://www.enterprisedb.com/products-services-training/pgdownload#windows) Datenbank
  - Wähle während der Installation "47bVZ7nt8#c6" als Passwort
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
3. Installiere den [Heroku Toolbelt](https://toolbelt.heroku.com/)
4. Erstelle eine .env (siehe unten) Konfigurationsdatei im Hauptverzeichnis

## .env Beispieldatei
    DB_URL=jdbc:postgresql://localhost:5432/gwedb
    DB_USER=postgres
    DB_PASSWORD=<Datenbank Passwort>
    EMAIL_USER=ehemalige@gymnasiumwaldkraiburg.de
    EMAIL_PASSWORD=<Email Passwort>

## Lokal Ausführen
  - Packaging mit Maven (Goal: `package`)
  - Führe `heroku-local.bat` in der CLI aus

## Deployment
GWE wird auf Heroku deployt. Dass geschiet über das Heroku Dashboard, nachdem der Code auf GitHub gepusht wurde.

[Seite aufrufen](https://gymwkb-gwe.herokuapp.com/)

## Resourcen
- [Spring Guides](https://spring.io/guides)
- [YouTube - From 0 to Spring Security 4.0](https://www.youtube.com/watch?v=TjlDbIIJBi8)
