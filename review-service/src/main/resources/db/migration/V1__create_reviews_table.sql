-- DDL: Structure for reviews table
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    repair_order_id BIGINT NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    rating INTEGER NOT NULL,
    comment VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
