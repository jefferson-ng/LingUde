-- Password reset tokens table
CREATE TABLE IF NOT EXISTS password_reset_tokens (
                                                     id UUID PRIMARY KEY,
                                                     user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                                     token_hash VARCHAR(64) NOT NULL, -- SHA-256 hex (64 chars)
                                                     expires_at TIMESTAMP NOT NULL,
                                                     used_at TIMESTAMP NULL,
                                                     created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Optional: speed up lookups
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_token_hash
    ON password_reset_tokens(token_hash);

CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_user_id
    ON password_reset_tokens(user_id);
