CREATE DATABASE IF NOT EXISTS granja;


USE granja;

-- Crear tabla Usuarios
CREATE TABLE IF NOT EXISTS Usuarios (
    IdUsuario INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(50) NOT NULL,
    Contraseña VARCHAR(50) NOT NULL,
    Rol ENUM('Granjero', 'Cliente', 'Admin', 'Proveedor') NOT NULL,
    FotoPerfil VARCHAR(255)
);

-- Insertar datos en Usuarios
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
('Alberto', 'alberto321', 'Proveedor')
ON DUPLICATE KEY UPDATE Nombre=VALUES(Nombre), Contraseña=VALUES(Contraseña), Rol=VALUES(Rol);

select * from Usuarios;

-- Crear tabla Alimentos
CREATE TABLE IF NOT EXISTS Alimentos (
    IdAlimento INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Stock INT NOT NULL,
    Precio DECIMAL(10, 2) NOT NULL,
    UNIQUE KEY (Nombre)
);

-- Insertar datos en Alimentos
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
('Suplemento vitamínico', 90, 18.00)
ON DUPLICATE KEY UPDATE Nombre=VALUES(Nombre), Stock=VALUES(Stock), Precio=VALUES(Precio);

select * from Alimentos;

-- Crear tabla animales
CREATE TABLE IF NOT EXISTS animales (
    IdAnimal INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Descripcion VARCHAR(255),
    Stock INT NOT NULL,
    Precio DECIMAL(10, 2) NOT NULL,
    UNIQUE KEY (Nombre)
);

-- Insertar datos en animales
INSERT INTO animales (Nombre, Descripcion, Stock, Precio) VALUES
('Vaca', 'Vaca lechera Holstein', 50, 5000.00),
('Cerdo', 'Cerdo de engorde', 100, 2500.00),
('Gallina', 'Gallina ponedora', 200, 1500.00),
('Cabra', 'Cabra lechera', 30, 3000.00),
('Oveja', 'Oveja de lana', 80, 2000.00),
('Pato', 'Pato de carne', 50, 1800.00),
('Conejo', 'Conejo de carne', 70, 1200.00),
('Caballo', 'Caballo de trabajo', 20, 7000.00),
('Perro', 'Perro guardián', 10, 500.00),
('Gato', 'Gato cazador de ratones', 15, 400.00)
ON DUPLICATE KEY UPDATE Nombre=VALUES(Nombre), Descripcion=VALUES(Descripcion), Stock=VALUES(Stock), Precio=VALUES(Precio);

-- Crear tabla dietas
CREATE TABLE IF NOT EXISTS dietas (
    IdDieta INT AUTO_INCREMENT PRIMARY KEY,
    Animal VARCHAR(100) NOT NULL,
    Alimento VARCHAR(100) NOT NULL,
    Cantidad INT NOT NULL,
    FOREIGN KEY (Animal) REFERENCES animales(Nombre),
    FOREIGN KEY (Alimento) REFERENCES Alimentos(Nombre)
);

-- Insertar datos de ejemplo en dietas
INSERT INTO dietas (Animal, Alimento, Cantidad) VALUES
('Vaca', 'Heno', 5),
('Vaca', 'Maíz', 2),
('Cerdo', 'Maíz', 4),
('Cerdo', 'Trigo', 3),
('Gallina', 'Trigo', 1),
('Gallina', 'Alfalfa', 1),
('Cabra', 'Alfalfa', 2),
('Cabra', 'Salvado de trigo', 2)
ON DUPLICATE KEY UPDATE Animal=VALUES(Animal), Alimento=VALUES(Alimento), Cantidad=VALUES(Cantidad);

-- Procedimiento para eliminar la columna Precio de la tabla animales si existe
DELIMITER $$
CREATE PROCEDURE drop_precio_column_if_exists()
BEGIN
    IF EXISTS (
        SELECT * FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = 'granja'
        AND TABLE_NAME = 'animales'
        AND COLUMN_NAME = 'Precio'
    ) THEN
        ALTER TABLE animales DROP COLUMN Precio;
    END IF;
END$$
DELIMITER ;

-- Llamar al procedimiento para eliminar la columna Precio
CALL drop_precio_column_if_exists();

-- Procedimiento para añadir la columna FotoPerfil a la tabla Usuarios si no existe
DELIMITER $$
CREATE PROCEDURE add_fotoperfil_column_if_not_exists()
BEGIN
    IF NOT EXISTS (
        SELECT * FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = 'granja'
        AND TABLE_NAME = 'Usuarios'
        AND COLUMN_NAME = 'FotoPerfil'
    ) THEN
        ALTER TABLE Usuarios ADD COLUMN FotoPerfil VARCHAR(255);
    END IF;
END$$
DELIMITER ;

-- Llamar al procedimiento para añadir la columna FotoPerfil
CALL add_fotoperfil_column_if_not_exists();
