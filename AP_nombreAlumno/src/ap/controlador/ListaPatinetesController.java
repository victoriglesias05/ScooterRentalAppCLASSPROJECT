package ap.controlador;

import java.sql.SQLException;
import java.util.function.Consumer;

import ap.dao.PatineteDAO;
import ap.modelo.Patinete;
import ap.util.Alertas;
import ap.util.Vistas;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

/** Ventana "Lista de patinetes": tabla paginada de 10 en 10 para elegir un patinete. */
public class ListaPatinetesController {

	private static final int TAMANIO_PAGINA = 10;

	@FXML
	private TableView<Patinete> tabla;
	@FXML
	private TableColumn<Patinete, Integer> colNumSerie;
	@FXML
	private TableColumn<Patinete, String> colColor;
	@FXML
	private TableColumn<Patinete, String> colModelo;
	@FXML
	private TableColumn<Patinete, String> colMarca;
	@FXML
	private Label lblPagina;
	@FXML
	private Button btnAnterior;
	@FXML
	private Button btnSiguiente;
	@FXML
	private Button btnSeleccionar;

	private final PatineteDAO patineteDAO = new PatineteDAO();
	private int pagina = 0;
	/** Qué hacer al terminar: recibe el patinete elegido o null si se canceló. */
	private Consumer<Patinete> alVolver = elegido -> {
	};

	@FXML
	private void initialize() {
		colNumSerie.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().numSerie()));
		colColor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().color()));
		colModelo.setCellValueFactory(c -> new SimpleStringProperty(textoOGuion(c.getValue().modelo())));
		colMarca.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().marca()));
		colNumSerie.setStyle("-fx-alignment: CENTER;");
		Vistas.repartirColumnas(tabla, 0.2, 0.2, 0.33, 0.25);

		// "Seleccionar" solo está activo si hay una fila elegida
		btnSeleccionar.disableProperty().bind(tabla.getSelectionModel().selectedItemProperty().isNull());

		// Doble clic sobre una fila = Seleccionar
		tabla.setRowFactory(tv -> {
			TableRow<Patinete> fila = new TableRow<>();
			fila.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2 && !fila.isEmpty()) {
					onSeleccionar();
				}
			});
			return fila;
		});

		cargarPagina();
	}

	private void cargarPagina() {
		try {
			int total = patineteDAO.contar();
			int totalPaginas = Math.max(1, (int) Math.ceil(total / (double) TAMANIO_PAGINA));
			pagina = Math.min(pagina, totalPaginas - 1);

			tabla.getItems().setAll(patineteDAO.listarPagina(pagina, TAMANIO_PAGINA));
			lblPagina.setText("Página " + (pagina + 1) + " de " + totalPaginas);
			btnAnterior.setDisable(pagina == 0);
			btnSiguiente.setDisable(pagina >= totalPaginas - 1);
		} catch (SQLException e) {
			Alertas.error(null, "Error de base de datos",
					"No se pudo cargar la lista de patinetes:\n" + e.getMessage());
		}
	}

	@FXML
	private void onAnterior() {
		pagina--;
		cargarPagina();
	}

	@FXML
	private void onSiguiente() {
		pagina++;
		cargarPagina();
	}

	@FXML
	private void onSeleccionar() {
		Patinete elegido = tabla.getSelectionModel().getSelectedItem();
		if (elegido != null) {
			alVolver.accept(elegido);
		}
	}

	@FXML
	private void onCancelar() {
		cancelar();
	}

	/** Vuelve a la pantalla anterior sin elegir ningún patinete. */
	public void cancelar() {
		alVolver.accept(null);
	}

	/** Quien abre esta pantalla indica aquí qué hacer al terminar (volver a la anterior). */
	public void setAlVolver(Consumer<Patinete> alVolver) {
		this.alVolver = alVolver;
	}

	private static String textoOGuion(String texto) {
		return texto == null || texto.isBlank() ? "-" : texto;
	}
}
