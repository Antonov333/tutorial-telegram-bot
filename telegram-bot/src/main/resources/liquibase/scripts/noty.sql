-- liquibase formatted sql

-- changeset 1:sergei333
CREATE TABLE noty(
id serial
chat_id int8
time_to_notify text
content text
)