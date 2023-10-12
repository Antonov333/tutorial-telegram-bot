-- liquibase formatted sql

-- changeset 1:sergei333
CREATE TABLE noty(
id bigserial primary key,
chat_id INT8,
time_to_notify TEXT,
content TEXT
)