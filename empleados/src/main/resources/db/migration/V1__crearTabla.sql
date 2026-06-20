create table if not exists empleados (
    id              integer      not null auto_increment,
    nombre          varchar(100) not null,
    apellido        varchar(100) not null,
    email           varchar(150) not null unique,
    telefono        varchar(20),
    estado          varchar(20)  not null default 'ACTIVO',
    departamento_id integer,
    cargo_id        integer,
    primary key (id)
);
