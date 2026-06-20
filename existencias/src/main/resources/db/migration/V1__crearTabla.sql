CREATE TABLE IF NOT EXISTS existencia (
    id BIGINT NOT NULL AUTO_INCREMENT,
    referencia VARCHAR(255),
    cantidad DECIMAL(19, 4),
    PRIMARY KEY (id)
);
