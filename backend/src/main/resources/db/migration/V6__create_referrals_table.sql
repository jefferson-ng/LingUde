-- Create referrals table for storing user referral codes
CREATE TABLE IF NOT EXISTS referrals (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    referral_code VARCHAR(6) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_referrals_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Index for fast lookups by referral code
CREATE INDEX IF NOT EXISTS idx_referrals_referral_code ON referrals(referral_code);
