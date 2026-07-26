--liquibase formatted sql

--changeset rafael:009-insert-default-customers
INSERT INTO customer (id, name, document, phone, email, status)
VALUES
    (
        '366941cf-9853-4514-ae99-1e1ea2b984ea',
        'Cliente Pitflow',
        '78177454048',
        '11999990001',
        'rafaelsmoreiras@gmail.com',
        'ACTIVE'
    ),
    (
        '9f460d68-9936-4f2e-ba6b-6bb9ac3d3618',
        'Cliente Demonstração 2',
        '06678477073',
        '11999990002',
        'cliente2@pitflow.com.br',
        'ACTIVE'
    ),
    (
        '4c4846a4-9c83-4573-b702-b66c81e2eaf0',
        'Cliente Demonstração 3',
        '42634554010',
        '11999990003',
        'cliente3@pitflow.com.br',
        'ACTIVE'
    ),
    (
        '9e3df500-6b56-4bd8-a2db-7c4732cb5145',
        'Cliente Demonstração 4',
        '27278293022',
        '11999990004',
        'cliente4@pitflow.com.br',
        'ACTIVE'
    )
ON CONFLICT (document) DO NOTHING;

--rollback DELETE FROM customer WHERE id IN (
--rollback     '366941cf-9853-4514-ae99-1e1ea2b984ea',
--rollback     '9f460d68-9936-4f2e-ba6b-6bb9ac3d3618',
--rollback     '4c4846a4-9c83-4573-b702-b66c81e2eaf0',
--rollback     '9e3df500-6b56-4bd8-a2db-7c4732cb5145'
--rollback );
