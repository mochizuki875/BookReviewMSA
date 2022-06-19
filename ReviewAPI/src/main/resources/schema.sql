CREATE TABLE review (
    id SERIAL NOT NULL PRIMARY KEY,
    evaluation INTEGER NOT NULL DEFAULT 0,
    content TEXT,
    bookid INTEGER NOT NULL,
    userid INTEGER NOT NULL
);

CREATE TABLE totalevaluation (
    bookid INTEGER NOT NULL UNIQUE,
	totalevaluation DOUBLE PRECISION NOT NULL DEFAULT 0
);