-- Código recogido del anexo 7 del libro Acceso a datos con IntelliJ IDEA (2a edición) de José Ramón García Sevilla.

drop table if exists departamento;
drop table if exists empleado;

create table departamento (
    depno integer primary key,
    nombre varchar(14),
    ubicacion varchar(13)
);

create table empleado (
    empno serial primary key,
    nombre varchar(10),
    puesto varchar(15),
    depno integer,
    constraint fk_employee_dpt foreign key (depno) references departamento(depno)
);

insert into departamento values
  (10, 'Contabilidad', 'Madrid'),
  (20, 'Marketing', 'Barcelona'),
  (30, 'Ventas', 'Alicante'),
  (40, 'Logística', 'Valencia');

insert into empleado (nombre, puesto, depno) values
  ('García', 'Dependiente', 20),
  ('López', 'Vendedor', 30),
  ('Pérez', 'Vendedor', 30),
  ('Gómez', 'Responsable', 20),
  ('Vázquez', 'Vendedor', 30),
  ('Martínez', 'Responsable', 30),
  ('Sánchez', 'Responsable', 10),
  ('Jiménez', 'Analista', 20),
  ('Fernández', 'Presidente', 10),
  ('Álvarez', 'Vendedor', 30),
  ('Rodríguez', 'Dependiente', 20),
  ('Ramírez', 'Dependiente', 30),
  ('González', 'Analista', 20),
  ('Hernández', 'Dependiente', 10);

