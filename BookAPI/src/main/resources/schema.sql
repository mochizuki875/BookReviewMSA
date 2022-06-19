CREATE TABLE book (
    id SERIAL NOT NULL PRIMARY KEY,
    title TEXT NOT NULL,
    overview TEXT,
    totalevaluation DOUBLE PRECISION NOT NULL DEFAULT 0
);