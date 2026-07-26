--liquibase formatted sql

--changeset rafael:008-alter-table-customer-add-status
ALTER TABLE customer
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ACTIVE';

COMMENT ON COLUMN customer.status IS
    'Status disponíveis do cliente: ACTIVE, INACTIVE, SUSPENDED, PENDING';

--rollback ALTER TABLE customer DROP COLUMN IF EXISTS status;
