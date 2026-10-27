-- Demo users. Passwords are stored as BCrypt hashes:
--   admin@example.com / admin123 (ADMIN)
--   user@example.com  / user123  (USER)
INSERT INTO app_user (id, email, first_name, last_name, password, role)
VALUES
    (gen_random_uuid(), 'admin@example.com', 'Demo', 'Admin', '$2a$12$vnL7R5FaZdeXwqmZaUH7zOwGBomYdcepvF8aSzoO9Zang99OG69vm', 'ADMIN'),
    (gen_random_uuid(), 'user@example.com', 'Demo', 'User', '$2a$12$a30PvbETykzLEh3N7GkC.uD33mTJ/HTD1Pdq1NGaXAmPJ0jd5DD/2', 'USER');
