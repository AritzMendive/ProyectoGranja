create database granja;

use granja;


CREATE TABLE Usuarios (
    IdRol INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(50) NOT NULL,
    Contraseña VARCHAR(50) NOT NULL,
    Rol ENUM('Granjero', 'Cliente', 'Admin', 'Proveedor') NOT NULL
);

INSERT INTO Usuarios (Nombre, Contraseña, Rol) VALUES
('Lucas', 'lucas123', 'Cliente'),
('Patricia', 'patricia456', 'Proveedor'),
('Hugo', 'hugo789', 'Admin'),
('Cristina', 'cristina321', 'Granjero'),
('Roberto', 'roberto654', 'Cliente'),
('Elena', 'elena987', 'Proveedor'),
('Mario', 'mario123', 'Admin'),
('Sandra', 'sandra456', 'Granjero'),
('Ángela', 'angela789', 'Cliente'),
('Alberto', 'alberto321', 'Proveedor');

select * from Usuarios; -- comprobacion, funciona bien el autoincrement.


CREATE TABLE Alimentos (
    IdAlimento INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Stock INT NOT NULL,
    Precio DECIMAL(10, 2) NOT NULL
);


INSERT INTO Alimentos (Nombre, Stock, Precio) VALUES
('Heno', 200, 10.00),
('Maíz', 150, 8.50),
('Trigo', 100, 12.00),
('Alfalfa', 80, 15.00),
('Salvado de trigo', 120, 7.00),
('Harina de pescado', 50, 20.00),
('Minerales para ganado', 40, 25.00),
('Paja', 180, 6.50),
('Concentrado de proteínas', 70, 30.00),
('Suplemento vitamínico', 90, 18.00);

select * from Alimentos;

CREATE TABLE StockAnimales (
                               IdAnimal INT AUTO_INCREMENT PRIMARY KEY,
                               Nombre VARCHAR(100) NOT NULL,
                               Descripcion VARCHAR(255),
                               Stock INT NOT NULL,
                               Precio DECIMAL(10, 2) NOT NULL
);

INSERT INTO StockAnimales (Nombre, Descripcion, Stock, Precio) VALUES
                                                                   ('Vaca', 'Vaca lechera Holstein', 50, 5000.00),
                                                                   ('Cerdo', 'Cerdo de engorde', 100, 2500.00),
                                                                   ('Gallina', 'Gallina ponedora', 200, 1500.00),
                                                                   ('Cabra', 'Cabra lechera', 30, 3000.00),
                                                                   ('Oveja', 'Oveja de lana', 80, 2000.00),
                                                                   ('Pato', 'Pato de carne', 50, 1800.00),
                                                                   ('Conejo', 'Conejo de carne', 70, 1200.00),
                                                                   ('Caballo', 'Caballo de trabajo', 20, 7000.00),
                                                                   ('Perro', 'Perro guardián', 10, 500.00),
                                                                   ('Gato', 'Gato cazador de ratones', 15, 400.00);




