# Projekt im P-Seminar Informatik

GWE ist eine Java basierte Web Anwendung.
Diese soll ehemaligen Schülern die Möglichkeit geben sich zu registrieren,
um mit dem Gymnasium Waldkraiburg in Kontakt zu bleiben.
Das Portal könnte beispielsweise dazu genutzt werden,
um ehemalige Schüler mit interessanten Berufen in den Unterricht oder zu Veranstaltungen,
wie dem Berufsinformationsnachmittag, einzuladen.

## Gruppen
- Java Entwicklung im Back-End Bereich und HTML-Entwicklung im Front-End Bereich
- Datenschutz, Nutzungsrichtlinien, etc.
- Dokumentation, Protokollierung des Projekts
- Testing

## Projekt Setup
1. Vorraussetzung: [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (JRE und JDK)
3. Installiere die [PostgreSQL](http://www.enterprisedb.com/products-services-training/pgdownload#windows) Datenbank
  - Wähle während der Installation ein Passwort
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

## Lokal Ausführen
Starte die main-Methode in Application.java
mit den unten angegebenen Umgbungsvariablen und der Option `-Dserver.port=8080`.

## Umgebungsvariablen
    DB_URL=jdbc:postgresql://localhost:5432/gwedb
    DB_USER=postgres
    DB_PASSWORD=<Datenbank Passwort>
    EMAIL_USER=ehemalige@gymnasiumwaldkraiburg.de
    EMAIL_PASSWORD=<Email Passwort>
    GWE_ADDRESS=http://localhost:8080/

## Deployment
GWE wird auf Heroku deployt. Dass geschiet über das Heroku Dashboard, nachdem der Code auf GitHub gepusht wurde.

[Seite aufrufen](https://gymwkb-gwe.herokuapp.com/)

## Resourcen
- [Spring Guides](https://spring.io/guides)
- [YouTube - From 0 to Spring Security 4.0](https://www.youtube.com/watch?v=TjlDbIIJBi8)
