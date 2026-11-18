package ap.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.stage.Window;

/** Mensajes de la aplicación: información, advertencia, error y confirmación. */
public final class Alertas {

	private static final ButtonType ACEPTAR = new ButtonType("Aceptar", ButtonData.OK_DONE);
	private static final ButtonType CANCELAR = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);

	private Alertas() {
	}

	/** Operación realizada correctamente. */
	public static void informacion(Window dueno, String titulo, String mensaje) {
		crear(AlertType.INFORMATION, dueno, titulo, mensaje).showAndWait();
	}

	/** Datos incorrectos o incompletos (validaciones). */
	public static void advertencia(Window dueno, String titulo, String mensaje) {
		crear(AlertType.WARNING, dueno, titulo, mensaje).showAndWait();
	}

	/** Error de conexión o de SQL. */
	public static void error(Window dueno, String titulo, String mensaje) {
		crear(AlertType.ERROR, dueno, titulo, mensaje).showAndWait();
	}

	/** Pregunta con botones Aceptar y Cancelar. Devuelve true si se pulsa Aceptar. */
	public static boolean confirmar(Window dueno, String titulo, String mensaje) {
		Alert alerta = crear(AlertType.CONFIRMATION, dueno, titulo, mensaje);
		alerta.getButtonTypes().setAll(ACEPTAR, CANCELAR);
		return alerta.showAndWait().filter(boton -> boton == ACEPTAR).isPresent();
	}

	private static Alert crear(AlertType tipo, Window dueno, String titulo, String mensaje) {
		Alert alerta = new Alert(tipo);
		if (dueno != null) {
			alerta.initOwner(dueno);
		}
		alerta.setTitle(titulo);
		alerta.setHeaderText(null);
		alerta.setContentText(mensaje);
		alerta.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alerta.getDialogPane().getStylesheets().add(Vistas.rutaCss());
		alerta.getDialogPane().getStyleClass().add("dialogo");
		return alerta;
	}
}
