-- Add role column if it does not exist
ALTER TABLE IF EXISTS users
    ADD COLUMN IF NOT EXISTS role VARCHAR(20);

-- Set default role USER where it is null
UPDATE users
SET role = 'USER'
WHERE role IS NULL;

-- Ensure the column is NOT NULL from now on
ALTER TABLE users
    ALTER COLUMN role SET NOT NULL;
