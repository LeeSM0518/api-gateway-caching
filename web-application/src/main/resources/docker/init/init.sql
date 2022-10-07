CREATE TABLE if not EXISTS sensing
(
    id         uuid PRIMARY KEY          DEFAULT gen_random_uuid(),
    value      DOUBLE PRECISION NOT NULL,
    created_at timestamp        not null default CURRENT_TIMESTAMP
);

INSERT INTO sensing (value)
SELECT random() * 100 as value
from generate_series(1, 1000000);