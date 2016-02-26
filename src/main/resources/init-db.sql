INSERT INTO gweuser (id, email, password, first_name, last_name, graduation_type, graduation_year, occupation, discipline, activated) VALUES
  (
    0,
    'admin@test',
    '$2a$10$BC1UyAAdpolxJktRByaJHOpgrEdV18r.7.4U.u48MxfbFYy.1YKHW',
    'Admin', 'Istrator',
    0,
    2000,
    'Administrator',
    'KU',
    true
  ),
  (
    1,
    'user@test',
    '$2a$10$BC1UyAAdpolxJktRByaJHOpgrEdV18r.7.4U.u48MxfbFYy.1YKHW',
    'Looser', 'User',
    0,
    2000,
    'DAU',
    'RE',
    true
  ),
  (
    2,
    'dominik.stolz21@gmail.com',
    '$2a$10$EXD5MItMup6J8RTu.N51YOrs43uXHDzzFwM7z8HP.Y1B/27V87nO2',
    'Dominik', 'Stolz',
    0,
    2017,
    'Schüler',
    'INF',
    true
  ),
  (
    3,
    'oliverjacobsen@vodafone.de',
    '$2a$10$rjm2PkRq7xADD.P/386DMO.aXw5cRTFAn.oLwYJO2Pg5OUT/rXtre',
    'Oliver', 'Jacobsen',
    0,
    2017,
    'Schüler',
    'INF',
    true
  );

SELECT setval('hibernate_sequence', 3, TRUE)
