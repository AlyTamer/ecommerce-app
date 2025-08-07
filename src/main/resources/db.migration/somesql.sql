-- Create carts table
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE
);

-- Create cart_items table
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity >= 1),
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0)
);

-- Index on cart_id for performance
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);