package ap.util;

import java.util.regex.Pattern;

import javafx.scene.control.TextFormatter;

/** Validaciones de formato y filtros para impedir escribir caracteres no permitidos. */
public final class Validador {

	private static final Pattern DNI = Pattern.compile("^\\d{8}[A-Za-z]$");
	private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
	private static final Pattern TELEFONO = Pattern.compile("^\\d{9}$");

	private Validador() {
	}

	// ---------- Validaciones (se usan al pulsar Aceptar / Buscar) ----------

	/** 8 números seguidos de una letra, por ejemplo 12345678A. */
	public static boolean dniValido(String dni) {
		return dni != null && DNI.matcher(dni).matches();
	}

	/** Formato usuario@dominio.ext, por ejemplo usuario@correo.com. */
	public static boolean emailValido(String email) {
		return email != null && EMAIL.matcher(email).matches();
	}

	/** Solo números, 9 dígitos (la columna Telefono es INT(9)). */
	public static boolean telefonoValido(String telefono) {
		return telefono != null && TELEFONO.matcher(telefono).matches();
	}

	// ---------- Filtros para los TextField (impiden escribir lo no permitido) ----------

	/** DNI: máximo 9 caracteres, 8 números y después una letra (se pasa a mayúscula). */
	public static TextFormatter<String> formatoDni() {
		return new TextFormatter<>(cambio -> {
			String nuevo = cambio.getControlNewText();
			if (nuevo.matches("\\d{0,8}") || nuevo.matches("\\d{8}[A-Za-z]")) {
				cambio.setText(cambio.getText().toUpperCase());
				return cambio;
			}
			return null; // cambio rechazado: no se escribe nada
		});
	}

	/** Teléfono: solo números, máximo 9. */
	public static TextFormatter<String> formatoTelefono() {
		return new TextFormatter<>(cambio -> cambio.getControlNewText().matches("\\d{0,9}") ? cambio : null);
	}

	/** Limita la longitud del texto (para respetar el VARCHAR de la base de datos). */
	public static TextFormatter<String> limite(int maximo) {
		return new TextFormatter<>(cambio -> cambio.getControlNewText().length() <= maximo ? cambio : null);
	}
}
