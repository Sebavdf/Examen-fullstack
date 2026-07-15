-- DML: The seed user inserted in V2 has "password_encriptado_jwt" stored as plain text,
-- which is NOT a valid BCrypt hash. Now that AuthService hashes passwords with BCrypt,
-- that row can never authenticate successfully.
-- We remove it here instead of editing V2 directly, since Flyway migrations that were
-- already applied must never be modified (checksum mismatch on next run).
-- For the demo flow, create your test user through POST /api/v1/auth/register instead.
DELETE FROM users WHERE username = 'seba_admin' AND password = 'password_encriptado_jwt';
