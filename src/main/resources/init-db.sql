INSERT INTO gweuser (id, email, password, first_name, last_name, graduation_year, occupation, discipline) VALUES
  (
    0,
    'admin@test',
    '$2a$10$BC1UyAAdpolxJktRByaJHOpgrEdV18r.7.4U.u48MxfbFYy.1YKHW',
    'Admin', 'Istrator',
    2000,
    'Administrator',
    'KU'
  ),
  (
    1,
    'user@test',
    '$2a$10$BC1UyAAdpolxJktRByaJHOpgrEdV18r.7.4U.u48MxfbFYy.1YKHW',
    'Looser', 'User',
    2000,
    'DAU',
    'RE'
  ),
  (
    2,
    'dominik.stolz21@gmail.com',
    '$2a$10$EXD5MItMup6J8RTu.N51YOrs43uXHDzzFwM7z8HP.Y1B/27V87nO2',
    'Dominik', 'Stolz',
    2017,
    'Schüler',
    'INF'
  ),
  (
    3,
    'oliverjacobsen@vodafone.de',
    '$2a$10$rjm2PkRq7xADD.P/386DMO.aXw5cRTFAn.oLwYJO2Pg5OUT/rXtre',
    'Oliver', 'Jacobsen',
    2017,
    'Schüler',
    'INF'
  );

SELECT setval('hibernate_sequence', 3, TRUE)
