CREATE TABLE tasks (
    id VARCHAR(20) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    status VARCHAR(20) NOT NULL,
    assignee_id VARCHAR(20)
);
