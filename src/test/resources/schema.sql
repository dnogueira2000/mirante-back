CREATE TABLE IF NOT EXISTS events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    description VARCHAR(1000),
    event_date_time TIMESTAMP,
    location VARCHAR(200),
    deleted BOOLEAN
);