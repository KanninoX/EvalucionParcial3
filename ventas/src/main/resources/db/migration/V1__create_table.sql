CREATE TABLE IF NOT EXISTS ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT,
    empleado_id BIGINT,
    producto VARCHAR(200) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DOUBLE NOT NULL,
    total DOUBLE,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    fecha DATETIME
);
