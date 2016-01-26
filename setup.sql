CREATE DATABASE IF NOT EXISTS Foofle;
USE Foofle;
DROP TABLE if exists LookupTable;
CREATE TABLE LookupTable
(
  term VARCHAR(255),
  link VARCHAR(2000),
  occur INT,
  tfidf FLOAT,
  robertsontf FLOAT,
  normalizedtf FLOAT,
  customrobertsontf FLOAT,
  htmlp FLOAT
);