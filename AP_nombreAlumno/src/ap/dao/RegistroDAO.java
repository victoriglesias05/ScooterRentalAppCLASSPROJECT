package ap.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ap.modelo.Alquiler;
import ap.modelo.Usuario;

public class RegistroDAO {

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	/**
	 * Guarda un alquiler nuevo. En una sola transacción: crea o actualiza el usuario
	 * e inserta el registro. Si algo falla, no se guarda nada (rollback).
	 */
	public void insertar(Usuario usuario, int numSerie, LocalDate recogida, LocalDate devolucion)
			throws SQLException {
		try (Connection con = ConexionDB.obtener()) {
			con.setAutoCommit(false);
			try {
				usuarioDAO.guardar(con, usuario);

				String sql = "INSERT INTO Registro (ID, DNI, NumSerie, FechaRecog, FechaDev) VALUES (?, ?, ?, ?, ?)";
				try (PreparedStatement ps = con.prepareStatement(sql)) {
					ps.setInt(1, siguienteId(con));
					ps.setString(2, usuario.dni());
					ps.setInt(3, numSerie);
					ps.setDate(4, Date.valueOf(recogida));
					ps.setDate(5, Date.valueOf(devolucion));
					ps.executeUpdate();
				}
				con.commit();
			} catch (SQLException e) {
				con.rollback();
				throw e;
			}
		}
	}

	/** Calcula el siguiente ID (funciona tanto si la columna ID es AUTO_INCREMENT como si no). */
	private int siguienteId(Connection con) throws SQLException {
		try (PreparedStatement ps = con.prepareStatement("SELECT COALESCE(MAX(ID), 0) + 1 FROM Registro");
				ResultSet rs = ps.executeQuery()) {
			rs.next();
			return rs.getInt(1);
		}
	}

	/** Elimina un alquiler por su ID. Devuelve true si se borró alguna fila. */
	public boolean eliminar(int id) throws SQLException {
		try (Connection con = ConexionDB.obtener();
				PreparedStatement ps = con.prepareStatement("DELETE FROM Registro WHERE ID = ?")) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		}
	}

	/** Número de alquileres de un DNI (para calcular las páginas). */
	public int contarPorDNI(String dni) throws SQLException {
		try (Connection con = ConexionDB.obtener();
				PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM Registro WHERE DNI = ?")) {
			ps.setString(1, dni);
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				return rs.getInt(1);
			}
		}
	}

	/** Devuelve una página de alquileres de un DNI (la página 0 es la primera). */
	public List<Alquiler> buscarPorDNI(String dni, int pagina, int tamanio) throws SQLException {
		String sql = "SELECT r.ID, r.DNI, u.NombreCompleto, r.NumSerie, r.FechaRecog, r.FechaDev "
				+ "FROM Registro r JOIN Usuarios u ON u.DNI = r.DNI "
				+ "WHERE r.DNI = ? ORDER BY r.FechaRecog DESC, r.ID DESC LIMIT ? OFFSET ?";
		List<Alquiler> lista = new ArrayList<>();
		try (Connection con = ConexionDB.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, dni);
			ps.setInt(2, tamanio);
			ps.setInt(3, pagina * tamanio);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lista.add(new Alquiler(rs.getInt("ID"), rs.getString("DNI"),
							rs.getString("NombreCompleto"), rs.getInt("NumSerie"),
							rs.getDate("FechaRecog").toLocalDate(), rs.getDate("FechaDev").toLocalDate()));
				}
			}
		}
		return lista;
	}
}
