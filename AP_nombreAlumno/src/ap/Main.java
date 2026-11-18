package ap;

import ap.util.Vistas;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;

/** Arranque de la aplicación: abre la pantalla de Consulta. */
public class Main extends Application {

	@Override
	public void start(Stage ventana) throws Exception {
		Parent raiz = Vistas.cargador("Consulta.fxml").load();
		ventana.setTitle("Alquiler de patinetes - Consulta");
		ventana.setScene(Vistas.escena(raiz));
		ventana.show();
		ventana.setMinWidth(ventana.getWidth());
		ventana.setMinHeight(ventana.getHeight());
	}

	public static void main(String[] args) {
		launch(args);
	}
}
