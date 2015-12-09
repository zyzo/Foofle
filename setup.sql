CREATE DATABASE IF NOT EXISTS Foofle;
USE Foofle;
DROP IF EXISTS TABLE LookupTable;
CREATE TABLE LookupTable
(
  term VARCHAR(255),
  link VARCHAR(2000),
  occur INT,
  tfidf FLOAT
);