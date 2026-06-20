create table if not exists auditoria (
    id          integer      not null auto_increment,
    accion      varchar(100) not null,
    tabla       varchar(100) not null,
    registro_id integer,
    usuario     varchar(100),
    fecha_hora  datetime,
    detalles    varchar(500),
    primary key (id)
);
