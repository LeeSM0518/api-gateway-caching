CREATE TABLE if not EXISTS sensing
(
    id    INTEGER PRIMARY KEY,
    value DOUBLE PRECISION NOT NULL
);

INSERT INTO sensing
SELECT generate_series as id, random() * 100 as value from generate_series(1, 1000000);