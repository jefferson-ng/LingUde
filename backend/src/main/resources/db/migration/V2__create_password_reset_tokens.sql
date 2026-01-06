CREATE TABLE IF NOT EXISTS password_reset_tokens (
                                                     id UUID PRIMARY KEY,
                                                     email VARCHAR(255) NOT NULL,
                                                     token_hash VARCHAR(64) NOT NULL,
                                                     expires_at TIMESTAMPTZ NOT NULL,
                                                     used_at TIMESTAMPTZ NULL,
                                                     created_at TIMESTAMPTZ NOT NULL
);

-- Helpful indexes
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_email
    ON password_reset_tokens(email);

CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_token_hash
    ON password_reset_tokens(token_hash);

CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_created_at
    ON password_reset_tokens(created_at);
