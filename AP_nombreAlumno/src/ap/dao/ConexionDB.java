package ap.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexión con la base de datos MySQL ALQUILERPATIN.
 * Si tu usuario, contraseña o puerto son distintos, cámbialos aquí.
 */
public final class ConexionDB {

	private static final String HOST = "localhost";
	private static final String PUERTO = "3306";
	private static final String BASE_DATOS = "ALQUILERPATIN";
	private static final String USUARIO = "root";
	private static final String CLAVE = "1234";

	private static final String URL = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE_DATOS
			+ "?allowPublicKeyRetrieval=true";

	private ConexionDB() {
	}

	/** Abre una conexión nueva. Quien la pida debe cerrarla (try-with-resources). */
	public static Connection obtener() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new SQLException("No se encontró el driver de MySQL. "
					+ "Añade el .jar del conector (mysql-connector-j) al Build Path del proyecto.", e);
		}
		return DriverManager.getConnection(URL, USUARIO, CLAVE);
	}
}
