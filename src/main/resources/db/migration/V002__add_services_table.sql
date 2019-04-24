CREATE TABLE services
(
    id      uuid UNIQUE,
    project_id uuid REFERENCES projects(id),
    coordinate_x DECIMAL ,
    coordinate_y DECIMAL ,
    name    VARCHAR(255),
    kind    VARCHAR(255),
    created timestamp,
    updated timestamp
)
