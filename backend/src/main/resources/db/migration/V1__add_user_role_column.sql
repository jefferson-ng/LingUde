-- Add role column if it does not exist
DO $$
BEGIN
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'users') THEN
        -- Add role column if it doesn't exist
        IF NOT EXISTS (SELECT FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'role') THEN
            ALTER TABLE users ADD COLUMN role VARCHAR(20);
        END IF;

        -- Set default role USER where it is null
        UPDATE users SET role = 'USER' WHERE role IS NULL;

        -- Ensure the column is NOT NULL from now on
        ALTER TABLE users ALTER COLUMN role SET NOT NULL;
    END IF;
END $$;
