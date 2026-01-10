-- V1__create_tb_email.sql
-- Criação da tabela tb_email para armazenar emails

CREATE TABLE tb_email (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_ref VARCHAR(255),
    email_from VARCHAR(255),
    email_to VARCHAR(255),
    subject VARCHAR(255),
    text TEXT,
    send_date_email TIMESTAMP,
    status_email VARCHAR(50)
);
