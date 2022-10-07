CREATE TABLE if not EXISTS sensing
(
    id    INTEGER PRIMARY KEY,
    value DOUBLE PRECISION NOT NULL
);

INSERT INTO sensing
SELECT generate_series as id, random() * 100 as value from generate_series(1, 1000000);

create sequence hibernate_sequence;

alter sequence hibernate_sequence owner to postgres;

select max(id) from sensing;
