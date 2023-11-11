CREATE TABLE example_table
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255)
);

INSERT INTO example_table (name)
VALUES ('John'),
       ('Alice'),
       ('Bob'),
       ('Eve');