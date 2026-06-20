create table if not exists departamentos (
    id          integer      not null auto_increment,
    nombre      varchar(100) not null unique,
    descripcion varchar(255),
    estado      varchar(20)  not null default 'ACTIVO',
    primary key (id)
);
