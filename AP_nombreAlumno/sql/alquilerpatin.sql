-- =====================================================================
--  BD ALQUILERPATIN  ·  Alquiler de patinetes
--  Se puede ejecutar varias veces sin problema (no duplica datos).
-- =====================================================================
CREATE DATABASE IF NOT EXISTS ALQUILERPATIN CHARACTER SET utf8mb4;
USE ALQUILERPATIN;

CREATE TABLE IF NOT EXISTS Usuarios (
    DNI             VARCHAR(9)   NOT NULL,
    NombreCompleto  VARCHAR(100) NOT NULL,
    Email           VARCHAR(100) NOT NULL,
    Telefono        INT          NOT NULL,
    PRIMARY KEY (DNI)
);

CREATE TABLE IF NOT EXISTS Patinetes (
    NumSerie  INT         NOT NULL,
    Color     VARCHAR(20) NOT NULL,
    Marca     VARCHAR(50) NOT NULL,
    Modelo    VARCHAR(50) NULL,
    PRIMARY KEY (NumSerie)
);

-- Actualización 10-NOV: las fechas son de tipo DATE (no TIMESTAMP)
CREATE TABLE IF NOT EXISTS Registro (
    ID          INT         NOT NULL AUTO_INCREMENT,
    DNI         VARCHAR(9)  NOT NULL,
    NumSerie    INT         NOT NULL,
    FechaRecog  DATE        NOT NULL,
    FechaDev    DATE        NOT NULL,
    PRIMARY KEY (ID),
    CONSTRAINT fk_registro_usuario   FOREIGN KEY (DNI)      REFERENCES Usuarios (DNI),
    CONSTRAINT fk_registro_patinete  FOREIGN KEY (NumSerie) REFERENCES Patinetes (NumSerie)
);

-- Si la tabla Registro ya existía con TIMESTAMP, descomenta y ejecuta esto:
-- ALTER TABLE Registro MODIFY FechaRecog DATE NOT NULL, MODIFY FechaDev DATE NOT NULL;

-- ---------------------------------------------------------------------
--  Precarga de 50 patinetes (NumSerie 1001 a 1050)
-- ---------------------------------------------------------------------
INSERT IGNORE INTO Patinetes (NumSerie, Color, Marca, Modelo) VALUES
    (1001, 'Negro', 'Xiaomi', 'Mi Scooter 3'),
    (1002, 'Verde', 'Segway', 'Ninebot F40'),
    (1003, 'Rojo', 'Cecotec', 'Bongo Serie D'),
    (1004, 'Azul', 'Segway', 'Ninebot Max G30'),
    (1005, 'Gris', 'Cecotec', 'Bongo Serie M'),
    (1006, 'Blanco', 'Xiaomi', 'Scooter 4 Lite'),
    (1007, 'Blanco', 'Cecotec', 'Bongo Serie A'),
    (1008, 'Negro', 'Xiaomi', NULL),
    (1009, 'Verde', 'Segway', 'Ninebot E25'),
    (1010, 'Rojo', 'Xiaomi', 'Mi Scooter Pro 2'),
    (1011, 'Azul', 'Segway', 'Ninebot E25'),
    (1012, 'Gris', 'Xiaomi', 'Mi Scooter 3'),
    (1013, 'Gris', 'Segway', 'Ninebot F40'),
    (1014, 'Blanco', 'Cecotec', 'Bongo Serie D'),
    (1015, 'Negro', 'Segway', 'Ninebot Max G30'),
    (1016, 'Verde', 'Cecotec', 'Bongo Serie M'),
    (1017, 'Rojo', 'Xiaomi', 'Scooter 4 Lite'),
    (1018, 'Azul', 'Cecotec', 'Bongo Serie A'),
    (1019, 'Azul', 'Xiaomi', 'Scooter 4 Lite'),
    (1020, 'Gris', 'Cecotec', 'Bongo Serie A'),
    (1021, 'Blanco', 'Xiaomi', 'Mi Scooter Pro 2'),
    (1022, 'Negro', 'Segway', 'Ninebot E25'),
    (1023, 'Verde', 'Xiaomi', 'Mi Scooter 3'),
    (1024, 'Rojo', 'Segway', NULL),
    (1025, 'Rojo', 'Cecotec', 'Bongo Serie D'),
    (1026, 'Azul', 'Segway', 'Ninebot Max G30'),
    (1027, 'Gris', 'Cecotec', 'Bongo Serie M'),
    (1028, 'Blanco', 'Segway', 'Ninebot Max G30'),
    (1029, 'Negro', 'Cecotec', 'Bongo Serie M'),
    (1030, 'Verde', 'Xiaomi', 'Scooter 4 Lite'),
    (1031, 'Verde', 'Cecotec', 'Bongo Serie A'),
    (1032, 'Rojo', 'Xiaomi', 'Mi Scooter Pro 2'),
    (1033, 'Azul', 'Segway', 'Ninebot E25'),
    (1034, 'Gris', 'Xiaomi', 'Mi Scooter 3'),
    (1035, 'Blanco', 'Segway', 'Ninebot F40'),
    (1036, 'Negro', 'Cecotec', 'Bongo Serie D'),
    (1037, 'Negro', 'Segway', 'Ninebot F40'),
    (1038, 'Verde', 'Cecotec', 'Bongo Serie D'),
    (1039, 'Rojo', 'Segway', 'Ninebot Max G30'),
    (1040, 'Azul', 'Cecotec', 'Bongo Serie M'),
    (1041, 'Gris', 'Xiaomi', 'Scooter 4 Lite'),
    (1042, 'Blanco', 'Cecotec', NULL),
    (1043, 'Blanco', 'Xiaomi', 'Mi Scooter Pro 2'),
    (1044, 'Negro', 'Segway', 'Ninebot E25'),
    (1045, 'Verde', 'Xiaomi', 'Mi Scooter 3'),
    (1046, 'Rojo', 'Segway', 'Ninebot E25'),
    (1047, 'Azul', 'Xiaomi', 'Mi Scooter 3'),
    (1048, 'Gris', 'Segway', 'Ninebot F40'),
    (1049, 'Gris', 'Cecotec', 'Bongo Serie D'),
    (1050, 'Blanco', 'Segway', 'Ninebot Max G30');
