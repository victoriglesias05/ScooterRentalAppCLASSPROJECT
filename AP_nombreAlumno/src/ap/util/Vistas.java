package ap.util;

import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/** Utilidades para cargar pantallas FXML y abrir ventanas. */
public final class Vistas {

	private static final String RUTA = "/ap/vista/";

	private Vistas() {
	}

	/** Prepara el cargador de un FXML (por ejemplo "Registro.fxml"). Después se llama a loader.load(). */
	public static FXMLLoader cargador(String fxml) {
		return new FXMLLoader(Vistas.class.getResource(RUTA + fxml));
	}

	/** Dirección de la hoja de estilos de la aplicación. */
	public static String rutaCss() {
		return Vistas.class.getResource(RUTA + "application.css").toExternalForm();
	}

	/** Crea una escena con los estilos de la aplicación. */
	public static Scene escena(Parent raiz) {
		Scene escena = new Scene(raiz);
		escena.getStylesheets().add(rutaCss());
		return escena;
	}

	/** Crea una ventana con la pantalla indicada (todavía no se muestra). */
	public static Stage ventana(Parent raiz, String titulo) {
		Stage ventana = new Stage();
		ventana.setTitle(titulo);
		ventana.setResizable(false);
		ventana.setScene(escena(raiz));
		return ventana;
	}

	/**
	 * Cambia de pantalla: muestra la siguiente ventana y oculta la actual, de modo que
	 * siempre haya una sola ventana abierta. Primero se muestra la nueva; si se ocultase
	 * antes la actual, JavaFX cerraría la aplicación al quedarse sin ventanas.
	 */
	public static void cambiar(Stage actual, Stage siguiente) {
		siguiente.show();
		actual.hide();
	}

	/** Reparte el ancho de la tabla entre sus columnas según los pesos indicados (suman < 1). */
	public static void repartirColumnas(TableView<?> tabla, double... pesos) {
		List<? extends TableColumn<?, ?>> columnas = tabla.getColumns();
		for (int i = 0; i < columnas.size() && i < pesos.length; i++) {
			TableColumn<?, ?> columna = columnas.get(i);
			columna.prefWidthProperty().bind(tabla.widthProperty().multiply(pesos[i]));
			columna.setResizable(false);
			columna.setSortable(false);
		}
	}
}
