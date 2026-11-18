package ap.modelo;

import java.time.LocalDate;

/** Fila de la pantalla de consulta: un alquiler junto con el nombre del usuario. */
public record Alquiler(int id, String dni, String nombreCompleto, int numSerie,
		LocalDate fechaRecogida, LocalDate fechaDevolucion) {
}
