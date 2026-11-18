package ap.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ap.modelo.Usuario;

public class UsuarioDAO {

	/** Devuelve el usuario con ese DNI o null si no existe. */
	public Usuario buscarPorDNI(String dni) throws SQLException {
		String sql = "SELECT DNI, NombreCompleto, Email, Telefono FROM Usuarios WHERE DNI = ?";
		try (Connection con = ConexionDB.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, dni);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return new Usuario(rs.getString("DNI"), rs.getString("NombreCompleto"),
							rs.getString("Email"), rs.getInt("Telefono"));
				}
			}
		}
		return null;
	}

	/**
	 * Inserta el usuario si no existe o actualiza sus datos si ya existe.
	 * Recibe la conexión para poder formar parte de una transacción (ver RegistroDAO).
	 */
	void guardar(Connection con, Usuario u) throws SQLException {
		boolean existe;
		try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM Usuarios WHERE DNI = ?")) {
			ps.setString(1, u.dni());
			try (ResultSet rs = ps.executeQuery()) {
				existe = rs.next();
			}
		}
		// Los 4 parámetros van en el mismo orden en el UPDATE y en el INSERT
		String sql = existe
				? "UPDATE Usuarios SET NombreCompleto = ?, Email = ?, Telefono = ? WHERE DNI = ?"
				: "INSERT INTO Usuarios (NombreCompleto, Email, Telefono, DNI) VALUES (?, ?, ?, ?)";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, u.nombreCompleto());
			ps.setString(2, u.email());
			ps.setInt(3, u.telefono());
			ps.setString(4, u.dni());
			ps.executeUpdate();
		}
	}
}
