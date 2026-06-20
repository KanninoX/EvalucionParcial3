create table if not exists cargos (
    id              integer      not null auto_increment,
    nombre          varchar(100) not null,
    descripcion     varchar(255),
    estado          varchar(20)  not null default 'ACTIVO',
    departamento_id integer,
    primary key (id)
);
