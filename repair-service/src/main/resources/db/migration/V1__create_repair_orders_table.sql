-- DDL: Structure for repair_orders table
CREATE TABLE repair_orders (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    device_id BIGINT NOT NULL,
    issue_description VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL,
    estimated_cost DECIMAL(10, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
