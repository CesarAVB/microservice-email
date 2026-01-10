-- V2__add_html_column_to_tb_email.sql
-- Adiciona coluna html para armazenar conteúdo HTML dos emails

ALTER TABLE tb_email 
ADD COLUMN html TEXT;