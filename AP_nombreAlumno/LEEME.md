# Alquiler de patinetes (JavaFX + MySQL)

Aplicación de escritorio con tres pantallas: **Consulta** (principal), **Registro** y **Lista de patinetes**.

## 1. Base de datos
1. Abre MySQL Workbench (o la consola) y ejecuta **`sql/alquilerpatin.sql`**.
   Crea la BD `ALQUILERPATIN`, las tablas `Usuarios`, `Patinetes` y `Registro` (con fechas `DATE`)
   y precarga **50 patinetes** (Nº de serie 1001 a 1050). Se puede ejecutar varias veces sin duplicar nada.
2. Si las tablas ya existen en tu MySQL con otros nombres de tabla o columna, comprueba con
   `DESCRIBE Registro;` y ajusta las consultas SQL de `src/ap/dao/` (están todas ahí).

## 2. Importar en Eclipse
1. `File > Import > General > Existing Projects into Workspace` y elige esta carpeta.
2. Botón derecho sobre el proyecto > `Build Path > Configure Build Path > Libraries > Classpath > Add External JARs`
   y añade, igual que en el proyecto de SceneBuilder de clase:
   - los `.jar` de la carpeta `lib` del **JavaFX SDK** (javafx.base, controls, fxml, graphics...)
   - el conector **mysql-connector-j-xxx.jar**
3. Comprueba que el proyecto usa tu JDK: `Properties > Java Compiler` (Java 25).
4. Si Eclipse crea un `module-info.java`, bórralo (el proyecto no es modular).

## 3. Ejecutar
`Run > Run Configurations > Java Application` con la clase **`ap.Main`**. En la pestaña **Arguments > VM arguments**:

```
--module-path "C:\ruta\a\javafx-sdk-25\lib" --add-modules javafx.controls,javafx.fxml
```

## 4. Datos de conexión
Están en `src/ap/dao/ConexionDB.java` (localhost, puerto 3306, usuario `root`, contraseña `1234`).

## Estructura
```
src/ap/Main.java                  arranque (abre Consulta)
src/ap/controlador/               RegistroController, ConsultaController, ListaPatinetesController
src/ap/dao/                       ConexionDB, UsuarioDAO, PatineteDAO, RegistroDAO (JDBC + PreparedStatement)
src/ap/modelo/                    Usuario, Patinete, Alquiler
src/ap/util/                      Validador, Alertas, Fechas, Vistas
src/ap/vista/                     Consulta.fxml, Registro.fxml, ListaPatinetes.fxml, application.css
sql/alquilerpatin.sql             tablas + 50 patinetes
```
Los `.fxml` se pueden abrir y retocar con SceneBuilder (clic derecho > Open with SceneBuilder).

## Antes de entregar
Cambia el nombre del proyecto y del ZIP a `AP_nombreCompletoAlumno`
(Eclipse: clic derecho > `Refactor > Rename`).
