CREATE TABLE projects
(
    id      uuid UNIQUE,
    name    VARCHAR(255),
    created timestamp,
    updated timestamp
)