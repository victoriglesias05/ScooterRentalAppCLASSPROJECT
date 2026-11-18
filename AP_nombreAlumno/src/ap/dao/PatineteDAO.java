package ap.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ap.modelo.Patinete;

public class PatineteDAO {

	/** Número total de patinetes (para calcular las páginas). */
	public int contar() throws SQLException {
		try (Connection con = ConexionDB.obtener();
				PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM Patinetes");
				ResultSet rs = ps.executeQuery()) {
			rs.next();
			return rs.getInt(1);
		}
	}

	/** Devuelve una página de patinetes (la página 0 es la primera). */
	public List<Patinete> listarPagina(int pagina, int tamanio) throws SQLException {
		String sql = "SELECT NumSerie, Color, Marca, Modelo FROM Patinetes ORDER BY NumSerie LIMIT ? OFFSET ?";
		List<Patinete> lista = new ArrayList<>();
		try (Connection con = ConexionDB.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, tamanio);
			ps.setInt(2, pagina * tamanio);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lista.add(new Patinete(rs.getInt("NumSerie"), rs.getString("Color"),
							rs.getString("Marca"), rs.getString("Modelo")));
				}
			}
		}
		return lista;
	}
}
