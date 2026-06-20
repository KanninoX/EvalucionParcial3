create table if not exists usuarios (
    id          integer      not null auto_increment,
    username    varchar(100) not null unique,
    password    varchar(255) not null,
    rol         varchar(50)  not null default 'USER',
    estado      varchar(20)  not null default 'ACTIVO',
    empleado_id integer,
    primary key (id)
);
