-- Manual post-backfill migration. Back up first; see ../AUTH_SETUP.md.
-- These changes intentionally fail if existing accounts are not ready.
BEGIN;
ALTER TABLE users ALTER COLUMN name SET NOT NULL;
ALTER TABLE users ALTER COLUMN email SET NOT NULL;
ALTER TABLE users ALTER COLUMN role SET NOT NULL;
ALTER TABLE users ALTER COLUMN password SET NOT NULL;
ALTER TABLE users ADD CONSTRAINT users_email_normalized CHECK (email = lower(trim(email)));
CREATE UNIQUE INDEX IF NOT EXISTS users_email_normalized_unique ON users (lower(trim(email)));
ALTER TABLE users ADD CONSTRAINT users_password_bcrypt CHECK (
    password ~ '^\$2[aby]\$[0-9]{2}\$[./A-Za-z0-9]{53}$'
);
COMMIT;
