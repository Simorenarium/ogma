CREATE TABLE rolestoautoapply (
    id BIGINT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    event varchar
)