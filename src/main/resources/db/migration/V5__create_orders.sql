CREATE TABLE orders
(
    id            UUID         NOT NULL,
    user_id       UUID         NOT NULL,
    status        VARCHAR(255) NOT NULL,
    total_price   DECIMAL(19, 2),
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    cart_snapshot OID,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);