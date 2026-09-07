
CREATE EXTENSION IF NOT EXISTS vector;

-- Tabela de Livros
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,              
    book_id INT UNIQUE,                     
    title VARCHAR(500) NOT NULL,
    description TEXT,
    page_count INT,
    year_published INT,
    rating NUMERIC(3, 2),
    
    -- Vetor de 768 dimensões para o nomic-embed-text
    embedding vector(768)
);

-- Tabela de Gêneros
CREATE TABLE genres (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Tabela de Autores
CREATE TABLE authors (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Tabela Associativa: Livro <-> Gênero
CREATE TABLE book_genres (
    book_id BIGINT REFERENCES books(id) ON DELETE CASCADE,
    genre_id INT REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, genre_id)
);

-- Tabela Associativa: Livro <-> Autor
CREATE TABLE book_authors (
    book_id BIGINT REFERENCES books(id) ON DELETE CASCADE,
    author_id INT REFERENCES authors(id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, author_id)
);

-- Índice HNSW para acelerar as buscas por IA/vetor no Postgres
CREATE INDEX idx_books_embedding ON books 
USING hnsw (embedding vector_cosine_ops);