CREATE TABLE review (
    id SERIAL NOT NULL PRIMARY KEY,
    evaluation INTEGER NOT NULL DEFAULT 0,
    content TEXT,
    bookid INTEGER NOT NULL,
    userid INTEGER NOT NULL
);