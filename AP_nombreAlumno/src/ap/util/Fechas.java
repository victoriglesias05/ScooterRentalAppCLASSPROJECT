package ap.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.util.StringConverter;

/** Formato de fechas de la aplicación: DD/MM/AAAA. */
public final class Fechas {

	public static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private Fechas() {
	}

	public static String formatear(LocalDate fecha) {
		return fecha == null ? "" : FORMATO.format(fecha);
	}

	/** Conversor para los DatePicker (muestran la fecha como DD/MM/AAAA). */
	public static StringConverter<LocalDate> conversor() {
		return new StringConverter<>() {
			@Override
			public String toString(LocalDate fecha) {
				return formatear(fecha);
			}

			@Override
			public LocalDate fromString(String texto) {
				if (texto == null || texto.isBlank()) {
					return null;
				}
				try {
					return LocalDate.parse(texto.trim(), FORMATO);
				} catch (DateTimeParseException e) {
					return null;
				}
			}
		};
	}
}
