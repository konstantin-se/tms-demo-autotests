CREATE TABLE tasks (
    id VARCHAR(20) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    status VARCHAR(20) NOT NULL,
    assignee_id VARCHAR(20)
);

CREATE TABLE users (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    team_id VARCHAR(20) NOT NULL
);


INSERT INTO tasks (id, title, status, assignee_id)
                VALUES ('t-1', 'Investigate flaky login', 'OPEN', NULL);

INSERT INTO users (id, name, team_id) VALUES
    ('u-ava', 'Ava Chen', 'team-falcon'),
    ('u-marcus', 'Marcus Lee', 'team-falcon'),
    ('u-priya', 'Priya Nair', 'team-falcon'),
    ('u-diego', 'Diego Ramirez', 'team-orbit'),
    ('u-sofia', 'Sofia Kim', 'team-orbit'),
    ('u-ethan', 'Ethan Brooks', 'team-orbit'),
    ('u-grace', 'Grace Okafor', 'team-nova'),
    ('u-liam', 'Liam Turner', 'team-nova'),
    ('u-mei', 'Mei Tanaka', 'team-nova');
