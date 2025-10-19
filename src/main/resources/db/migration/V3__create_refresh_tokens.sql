CREATE TABLE IF NOT EXISTS refresh_tokens (
                                              id           BIGSERIAL PRIMARY KEY,
                                              user_id      BIGINT       NOT NULL,
                                              token_hash   VARCHAR(44)  NOT NULL,
                                              issued_at    TIMESTAMP WITH TIME ZONE NOT NULL,
                                              expires_at   TIMESTAMP WITH TIME ZONE NOT NULL,
                                              revoked      BOOLEAN      NOT NULL DEFAULT FALSE,
                                              user_agent   VARCHAR(512) NOT NULL,
                                              ip           VARCHAR(45)  NOT NULL
);

ALTER TABLE refresh_tokens
    ADD CONSTRAINT uq_refresh_tokens_token_hash UNIQUE (token_hash);

ALTER TABLE refresh_tokens
    ADD CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id
    ON refresh_tokens(user_id);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at
    ON refresh_tokens(expires_at);
