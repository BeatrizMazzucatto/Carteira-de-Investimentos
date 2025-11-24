-- Script SQL para adicionar a coluna data_atualizacao na tabela investidores
-- Execute este script no seu banco de dados MySQL/MariaDB se a coluna não existir

ALTER TABLE investidores 
ADD COLUMN IF NOT EXISTS data_atualizacao DATETIME(6) NULL;

