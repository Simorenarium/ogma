CREATE TABLE helpmessage (
    message_id BIGINT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    channel_id BIGINT NOT NULL,
    create_date DATETIME NOT NULL
)