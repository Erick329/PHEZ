set names utf8mb4;
drop database if exists PHEZ;
create database PHEZ character set utf8mb4 collate utf8mb4_spanish_ci;
use PHEZ;

create table Roles(
idRol int auto_increment primary key,
nombre varchar(100) not null unique
); 

create table Categorias (
idCategoria int auto_increment primary key,
nombre varchar(100) not null unique,
descripcion varchar(255) not null,
status varchar(20) not null default 'ACTIVO',
check (status in ('ACTIVO', 'INACTIVO')) 
);

create table Usuarios (
idUsuario int auto_increment primary key,
nombre varchar(100) not null,
apellido varchar(100) not null,
email varchar(255) not null unique,
nit varchar(20) not null default 'CF',
password varchar(255) not null,
telefono varchar(20), 
direccion varchar(255) not null,
status varchar(20) not null default 'ACTIVO',
fechaRegistro DATETIME DEFAULT CURRENT_TIMESTAMP,
intentosFallidos int not null default 0,
idRol int not null,
foreign key(idRol) references Roles(idRol),
check (status in ('ACTIVO', 'INACTIVO'))
);

create table Productos(
idProducto int auto_increment primary key,
nombre varchar(100) not null,
descripcion varchar(255) not null,
codigo varchar(50) not null unique,
precio decimal(10, 2) not null,
stock int not null default 0,
stockMinimo int not null default 5,
status varchar(20) not null default 'ACTIVO', 
idCategoria int not null,
foreign key(idCategoria) references Categorias(idCategoria),
check (precio >= 0), 
check (stock >= 0),
check (stockMinimo >= 0),
check (status in ('ACTIVO', 'INACTIVO'))
);

create table Lotes(
idLote int auto_increment primary key,
numeroLote varchar(50) not null,
cantidad int not null,
fechaVencimiento date not null,
fechaIngreso datetime default current_timestamp,
status varchar(20) not null default 'ACTIVO',
idProducto int not null,
foreign key(idProducto) references Productos(idProducto),
check (cantidad >= 0),
check (status in ('ACTIVO', 'INACTIVO'))
);

create table Movimientos(
idMovimiento int auto_increment primary key,
tipo varchar(50) not null,
cantidad int not null,
motivo varchar(150),  
fecha datetime default current_timestamp,
idProducto int not null,
idUsuario int not null,
idLote int null,
foreign key(idProducto) references Productos(idProducto),
foreign key(idUsuario) references Usuarios(idUsuario),
foreign key(idLote) references Lotes(idLote),
check (tipo in ('ENTRADA', 'SALIDA', 'AJUSTE'))
);

create table Pedidos(
idPedido int auto_increment primary key,
total decimal(10, 2) not null,
direccion varchar(255) not null,
costoEnvio decimal(10, 2) not null,
fechaPedido datetime default current_timestamp,
status varchar(20) not null default 'PENDIENTE',
subTotal decimal(10, 2) not null,
iva decimal(10, 2) not null,
idUsuario int not null,
foreign key(idUsuario) references Usuarios(idUsuario),
check (status in ('PENDIENTE', 'PAGADO', 'EN_PREPARACION', 'ENVIADO', 'ENTREGADO', 'CANCELADO')) 
);

create table DetallePedido(
idDetallePedido int auto_increment primary key,
cantidad int not null,
precioUnitario decimal(10, 2) not null,
subTotal decimal(10, 2) not null,
idProducto int not null,
idPedido int not null,
foreign key(idProducto) references Productos(idProducto),
foreign key(idPedido) references Pedidos(idPedido),
check (cantidad > 0)    
);

create table Pagos(
idPago int auto_increment primary key,
metodoPago varchar(30) not null, 
monto decimal(10, 2) not null,
status varchar(20) not null default 'PENDIENTE',
fechaPago datetime default current_timestamp,
numeroReferencia varchar(100) not null,
idPedido int not null,
foreign key(idPedido) references Pedidos(idPedido),
check (metodoPago in ('TARJETA', 'TRANSFERENCIA', 'EFECTIVO')), 
check (status in ('PENDIENTE', 'APROBADO', 'RECHAZADO')) 
);
use phez;
select * from usuarios;
update usuarios set idrol = 1 where idusuario = 4;

delimiter //

create trigger trg_lotes_insert after insert on Lotes for each row
begin
    update Productos
    set stock = (select coalesce(sum(cantidad), 0) from Lotes
                 where idProducto = new.idProducto and status = 'ACTIVO')
    where idProducto = new.idProducto;
end //

create trigger trg_lotes_update after update on Lotes for each row
begin
    update Productos
    set stock = (select coalesce(sum(cantidad), 0) from Lotes
                 where idProducto = new.idProducto and status = 'ACTIVO')
    where idProducto = new.idProducto;
end //

create trigger trg_lotes_delete after delete on Lotes for each row
begin
    update Productos
    set stock = (select coalesce(sum(cantidad), 0) from Lotes
                 where idProducto = old.idProducto and status = 'ACTIVO')
    where idProducto = old.idProducto;
end //

delimiter ;

-- Roles
insert into Roles (nombre) values
('ADMIN'),
('VENDEDOR'),
('CLIENTE');
 
-- Usuarios (uno por rol)
insert into Usuarios (nombre, apellido, email, nit, password, telefono, direccion, idRol, fechaRegistro) values
('Admin', 'Sistema', 'admin@alimentossa.com', 'CF', '$2a$12$5OFX.nK50RGocspboZjy.utYqRTl3c1ytRHWBIsjO14M8PCgeHnKq', '5555-0000', 'Oficinas centrales, Portales', 1, '2026-10-01 08:00:00'),
('Valeria', 'Morales', 'vendedor@alimentossa.com', 'CF', '$2a$12$vyKPI/5ygrowaVqYl/9V4eia8bpcahZdo92wZIQjrZwyLsNtEA.jy', '5555-0001', 'Portales', 2, '2026-10-01 08:30:00'),
('Ana', 'López', 'cliente@correo.com', '1234567-8', '$2a$12$a.6RhHfuP.LoAp9XPfRigebnZyISWQBQlQwT/KGWwvj/chNrj7Zfu', '5512-3456', '3a. calle 4-12 zona 1, Portales', 3, '2026-10-05 10:15:00');
 
-- Categorías
insert into Categorias (nombre, descripcion) values
('Granos básicos', 'Arroz, frijol, maíz y derivados'),
('Lácteos', 'Leche, quesos, crema y yogur'),
('Bebidas', 'Jugos, aguas, café y refrescos'),
('Snacks', 'Galletas, nueces y dulces'),
('Frutas y verduras', 'Productos frescos de temporada');
 
-- Productos (el stock lo calculan los triggers al insertar los lotes)
insert into Productos (nombre, descripcion, codigo, precio, stockMinimo, idCategoria) values
('Arroz blanco 5 lb', 'Arroz grano largo, calidad superior.', 'GB-001', 32.50, 20, 1),
('Frijol negro 2 lb', 'Frijol negro seleccionado y limpio.', 'GB-002', 18.00, 15, 1),
('Harina de maíz 2 lb', 'Harina de maíz nixtamalizado para tortillas.', 'GB-003', 9.75, 20, 1),
('Avena en hojuelas 1 lb', 'Avena integral en hojuelas.', 'GB-004', 11.50, 15, 1),
('Leche entera 1 L', 'Leche pasteurizada de vaca.', 'LA-001', 13.75, 10, 2),
('Queso fresco 1 lb', 'Queso fresco artesanal de la región.', 'LA-002', 28.00, 8, 2),
('Yogur natural 1 L', 'Yogur natural sin azúcar añadida.', 'LA-003', 24.50, 8, 2),
('Crema pura 400 ml', 'Crema pura para acompañar sus comidas.', 'LA-004', 16.00, 8, 2),
('Jugo de naranja 1 L', 'Jugo 100 % natural sin azúcar.', 'BE-001', 17.50, 10, 3),
('Agua pura 3 L', 'Agua purificada.', 'BE-002', 8.50, 24, 3),
('Café molido 400 g', 'Café de altura de Guatemala, tostado medio.', 'BE-003', 42.00, 10, 3),
('Refresco de rosa de Jamaica 1 L', 'Bebida natural de flor de Jamaica.', 'BE-004', 12.00, 10, 3),
('Galletas integrales', 'Paquete de 12 unidades.', 'SN-001', 12.00, 15, 4),
('Mix de nueces 200 g', 'Nueces, almendras y pasas.', 'SN-002', 35.00, 6, 4),
('Chocolate de mesa 4 tabletas', 'Chocolate artesanal para bebida.', 'SN-003', 22.00, 8, 4),
('Aguacate Hass (unidad)', 'Aguacate de temporada, listo para consumir.', 'FV-001', 6.50, 20, 5),
('Banano (docena)', 'Banano maduro de la costa sur.', 'FV-002', 10.00, 15, 5),
('Tomate 1 lb', 'Tomate fresco de invernadero.', 'FV-003', 5.50, 20, 5),
('Güisquil (unidad)', 'Güisquil tierno.', 'FV-004', 3.00, 15, 5),
('Papa 5 lb', 'Papa blanca lavada.', 'FV-005', 24.00, 10, 5);
 
-- Lotes: uno por producto (lote 1 = producto 1, ..., lote 20 = producto 20).
-- La cantidad es la existencia actual (ya descontadas las ventas de los pedidos de abajo).
insert into Lotes (numeroLote, cantidad, fechaVencimiento, fechaIngreso, idProducto) values
('A-1001', 58,  '2027-06-30', '2026-10-01 09:00:00', 1),
('F-2001', 50,  '2027-05-31', '2026-10-01 09:00:00', 2),
('H-2101', 45,  '2027-03-31', '2026-10-01 09:00:00', 3),
('O-2201', 12,  '2027-02-28', '2026-10-01 09:00:00', 4),
('L-3001', 27,  '2026-11-07', '2026-10-20 09:00:00', 5),
('Q-4001', 13,  '2026-11-20', '2026-10-20 09:00:00', 6),
('Y-4101', 20,  '2026-11-10', '2026-10-20 09:00:00', 7),
('C-4201', 18,  '2026-11-25', '2026-10-20 09:00:00', 8),
('J-5001', 7,   '2026-11-30', '2026-10-15 09:00:00', 9),
('W-5101', 96,  '2027-08-31', '2026-10-01 09:00:00', 10),
('C-6001', 29,  '2027-04-30', '2026-10-01 09:00:00', 11),
('R-6101', 25,  '2026-12-15', '2026-10-15 09:00:00', 12),
('G-7001', 40,  '2027-01-31', '2026-10-01 09:00:00', 13),
('N-7101', 17,  '2027-02-10', '2026-10-01 09:00:00', 14),
('T-7201', 22,  '2027-04-15', '2026-10-01 09:00:00', 15),
('V-8001', 40,  '2026-11-18', '2026-10-25 09:00:00', 16),
('V-8101', 26,  '2026-11-16', '2026-10-25 09:00:00', 17),
('V-8201', 44,  '2026-11-17', '2026-10-25 09:00:00', 18),
('V-8301', 35,  '2026-11-21', '2026-10-25 09:00:00', 19),
('V-8401', 30,  '2026-12-10', '2026-10-25 09:00:00', 20);
 
-- Pedidos de la clienta Ana López (idUsuario 3).
-- Precios con IVA incluido: iva = subTotal - subTotal / 1.12; envío Q25, gratis desde Q200.
insert into Pedidos (total, direccion, costoEnvio, fechaPedido, status, subTotal, iva, idUsuario) values
(173.25, '3a. calle 4-12 zona 1, Portales', 25.00, '2026-10-26 10:30:00', 'ENTREGADO', 148.25, 15.88, 3),
(154.00, '3a. calle 4-12 zona 1, Portales', 25.00, '2026-10-29 16:45:00', 'PENDIENTE', 129.00, 13.82, 3),
(218.00, '3a. calle 4-12 zona 1, Portales', 0.00,  '2026-10-31 09:15:00', 'PAGADO',    218.00, 23.36, 3);
 
-- Detalle de cada pedido
insert into DetallePedido (cantidad, precioUnitario, subTotal, idProducto, idPedido) values
(2, 32.50, 65.00, 1, 1),
(3, 13.75, 41.25, 5, 1),
(1, 42.00, 42.00, 11, 1),
(4, 10.00, 40.00, 17, 2),
(2, 28.00, 56.00, 6, 2),
(6, 5.50, 33.00, 18, 2),
(3, 35.00, 105.00, 14, 3),
(2, 17.50, 35.00, 9, 3),
(4, 8.50, 34.00, 10, 3),
(2, 22.00, 44.00, 15, 3);
 
-- Pagos (tarjeta de prueba 4111 1111 1111 1111; no se guarda el número completo)
insert into Pagos (metodoPago, monto, status, fechaPago, numeroReferencia, idPedido) values
('TARJETA',  173.25, 'APROBADO',  '2026-10-26 10:30:00', 'SIM-DEMO0001 ****1111', 1),
('EFECTIVO', 154.00, 'PENDIENTE', '2026-10-29 16:45:00', 'SIM-DEMO0002', 2),
('TARJETA',  218.00, 'APROBADO',  '2026-10-31 09:15:00', 'SIM-DEMO0003 ****1111', 3);
 
-- Movimientos de inventario: ENTRADA inicial de cada lote y SALIDA por cada venta.
-- (lote - ventas = cantidad actual del lote)
insert into Movimientos (tipo, cantidad, motivo, fecha, idProducto, idUsuario, idLote) values
('ENTRADA', 60,  'Inventario inicial', '2026-10-01 09:00:00', 1, 1, 1),
('ENTRADA', 50,  'Inventario inicial', '2026-10-01 09:00:00', 2, 1, 2),
('ENTRADA', 45,  'Inventario inicial', '2026-10-01 09:00:00', 3, 1, 3),
('ENTRADA', 12,  'Inventario inicial', '2026-10-01 09:00:00', 4, 1, 4),
('ENTRADA', 30,  'Inventario inicial', '2026-10-20 09:00:00', 5, 1, 5),
('ENTRADA', 15,  'Inventario inicial', '2026-10-20 09:00:00', 6, 1, 6),
('ENTRADA', 20,  'Inventario inicial', '2026-10-20 09:00:00', 7, 1, 7),
('ENTRADA', 18,  'Inventario inicial', '2026-10-20 09:00:00', 8, 1, 8),
('ENTRADA', 9,   'Inventario inicial', '2026-10-15 09:00:00', 9, 1, 9),
('ENTRADA', 100, 'Inventario inicial', '2026-10-01 09:00:00', 10, 1, 10),
('ENTRADA', 30,  'Inventario inicial', '2026-10-01 09:00:00', 11, 1, 11),
('ENTRADA', 25,  'Inventario inicial', '2026-10-15 09:00:00', 12, 1, 12),
('ENTRADA', 40,  'Inventario inicial', '2026-10-01 09:00:00', 13, 1, 13),
('ENTRADA', 20,  'Inventario inicial', '2026-10-01 09:00:00', 14, 1, 14),
('ENTRADA', 24,  'Inventario inicial', '2026-10-01 09:00:00', 15, 1, 15),
('ENTRADA', 40,  'Inventario inicial', '2026-10-25 09:00:00', 16, 1, 16),
('ENTRADA', 30,  'Inventario inicial', '2026-10-25 09:00:00', 17, 1, 17),
('ENTRADA', 50,  'Inventario inicial', '2026-10-25 09:00:00', 18, 1, 18),
('ENTRADA', 35,  'Inventario inicial', '2026-10-25 09:00:00', 19, 1, 19),
('ENTRADA', 30,  'Inventario inicial', '2026-10-25 09:00:00', 20, 1, 20),
('SALIDA', 2, 'Venta pedido #1', '2026-10-26 10:30:00', 1, 3, 1),
('SALIDA', 3, 'Venta pedido #1', '2026-10-26 10:30:00', 5, 3, 5),
('SALIDA', 1, 'Venta pedido #1', '2026-10-26 10:30:00', 11, 3, 11),
('SALIDA', 4, 'Venta pedido #2', '2026-10-29 16:45:00', 17, 3, 17),
('SALIDA', 2, 'Venta pedido #2', '2026-10-29 16:45:00', 6, 3, 6),
('SALIDA', 6, 'Venta pedido #2', '2026-10-29 16:45:00', 18, 3, 18),
('SALIDA', 3, 'Venta pedido #3', '2026-10-31 09:15:00', 14, 3, 14),
('SALIDA', 2, 'Venta pedido #3', '2026-10-31 09:15:00', 9, 3, 9),
('SALIDA', 4, 'Venta pedido #3', '2026-10-31 09:15:00', 10, 3, 10),
('SALIDA', 2, 'Venta pedido #3', '2026-10-31 09:15:00', 15, 3, 15);
 


 

 