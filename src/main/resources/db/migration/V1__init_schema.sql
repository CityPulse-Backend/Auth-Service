CREATE TABLE users (
                        id BIGSERIAL PRIMARY KEY,
                        username VARCHAR(32) UNIQUE NOT NULL,
                        password VARCHAR(100) NOT NULL,
                        email VARCHAR(64) UNIQUE NOT NULL
);

INSERT INTO users (username, password, email)
VALUES
    ('user', '$2a$10$DAjQRY8vJOAj/M/JDvy58e0pL3NU6L0QMkXuV.ZnlfQtIMek53IEi', 'user@email.com'),
    ('user2', '$2a$10$PyGgXLUB5BRPNpwCUer8../UO8uRZP7TEobCR6118nPSZhzi.yBou', 'user2@email.com');
