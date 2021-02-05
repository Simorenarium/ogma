CREATE TABLE rolestoaddonreaction (
    id BIGINT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    message_id BIGINT NOT NULL,
    target_role_id BIGINT NOT NULL,
    emote varchar
)