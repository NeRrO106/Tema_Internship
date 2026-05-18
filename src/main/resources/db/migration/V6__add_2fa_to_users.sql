ALTER TABLE users ADD COLUMN totp_secret VARCHAR(255);
ALTER TABLE users ADD COLUMN two_factor_enabled BOOLEAN DEFAULT false;
ALTER TABLE users ADD COLUMN two_factor_verified BOOLEAN DEFAULT false;
ALTER TABLE users ADD COLUMN two_factor_setup_at TIMESTAMP;

CREATE INDEX idx_users_two_factor_enabled ON users(two_factor_enabled);