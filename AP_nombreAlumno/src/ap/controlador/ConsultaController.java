package ap.controlador;

import java.io.IOException;
import java.sql.SQLException;

import ap.dao.RegistroDAO;
import ap.modelo.Alquiler;
import ap.util.Alertas;
import ap.util.Fechas;
import ap.util.Validador;
import ap.util.Vistas;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

/** Pantalla principal: busca los alquileres de un DNI (de 5 en 5), da de alta y elimina. */
public class ConsultaController {

	private static final int TAMANIO_PAGINA = 5;

	@FXML
	private TextField txtDni;
	@FXML
	private TableView<Alquiler> tabla;
	@FXML
	private TableColumn<Alquiler, String> colNombre;
	@FXML
	private TableColumn<Alquiler, Integer> colNumSerie;
	@FXML
	private TableColumn<Alquiler, String> colRecogida;
	@FXML
	private TableColumn<Alquiler, String> colDevolucion;
	@FXML
	private Label lblPagina;
	@FXML
	private Button btnAnterior;
	@FXML
	private Button btnSiguiente;
	@FXML
	private Button btnEliminar;

	private final RegistroDAO registroDAO = new RegistroDAO();
	private final Label mensajeVacio = new Label("Introduce un DNI y pulsa Buscar");

	private String dniActual; // DNI de la última búsqueda
	private int pagina = 0;

	@FXML
	private void initialize() {
		txtDni.setTextFormatter(Validador.formatoDni());

		colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nombreCompleto()));
		colNumSerie.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().numSerie()));
		colRecogida.setCellValueFactory(c -> new SimpleStringProperty(Fechas.formatear(c.getValue().fechaRecogida())));
		colDevolucion.setCellValueFactory(c -> new SimpleStringProperty(Fechas.formatear(c.getValue().fechaDevolucion())));
		colNumSerie.setStyle("-fx-alignment: CENTER;");
		colRecogida.setStyle("-fx-alignment: CENTER;");
		colDevolucion.setStyle("-fx-alignment: CENTER;");
		Vistas.repartirColumnas(tabla, 0.37, 0.16, 0.225, 0.225);

		tabla.setPlaceholder(mensajeVacio);

		// "Eliminar" solo está activo si hay una fila seleccionada
		btnEliminar.disableProperty().bind(tabla.getSelectionModel().selectedItemProperty().isNull());
		btnAnterior.setDisable(true);
		btnSiguiente.setDisable(true);
	}

	@FXML
	private void onBuscar() {
		String dni = txtDni.getText().trim();
		if (dni.isEmpty()) {
			Alertas.advertencia(ventana(), "DNI obligatorio", "Introduce un DNI para realizar la búsqueda.");
			return;
		}
		if (!Validador.dniValido(dni)) {
			Alertas.advertencia(ventana(), "DNI no válido",
					"El DNI debe tener 8 números seguidos de una letra (por ejemplo, 12345678A).");
			return;
		}
		dniActual = dni;
		pagina = 0;
		cargarPagina();
	}

	private void cargarPagina() {
		if (dniActual == null) {
			return;
		}
		try {
			int total = registroDAO.contarPorDNI(dniActual);
			int totalPaginas = Math.max(1, (int) Math.ceil(total / (double) TAMANIO_PAGINA));
			pagina = Math.min(pagina, totalPaginas - 1); // por si se borró el último de la página

			tabla.getItems().setAll(registroDAO.buscarPorDNI(dniActual, pagina, TAMANIO_PAGINA));
			mensajeVacio.setText("No hay alquileres registrados para el DNI " + dniActual);
			lblPagina.setText("Página " + (pagina + 1) + " de " + totalPaginas);
			btnAnterior.setDisable(pagina == 0);
			btnSiguiente.setDisable(pagina >= totalPaginas - 1);
		} catch (SQLException e) {
			Alertas.error(ventana(), "Error de base de datos",
					"No se pudieron consultar los alquileres:\n" + e.getMessage());
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
	private void onAlta() {
		try {
			FXMLLoader cargador = Vistas.cargador("Registro.fxml");
			Parent raiz = cargador.load();
			RegistroController controlador = cargador.getController();
			Stage consulta = (Stage) ventana();
			Stage registro = Vistas.ventana(raiz, "Alquiler de patinetes - Registro");

			// Al terminar (Aceptar o Cancelar) se vuelve a Consulta
			controlador.setAlVolver(dniRegistrado -> {
				Vistas.cambiar(registro, consulta);
				// Si se guardó un alquiler, se muestran los alquileres de ese DNI
				if (dniRegistrado != null) {
					txtDni.setText(dniRegistrado);
					dniActual = dniRegistrado;
					pagina = 0;
					cargarPagina();
				}
			});
			// Cerrar con la X equivale a Cancelar: se vuelve a Consulta
			registro.setOnCloseRequest(e -> {
				e.consume();
				controlador.cancelar();
			});
			Vistas.cambiar(consulta, registro); // Consulta se oculta, queda solo Registro
		} catch (IOException e) {
			Alertas.error(ventana(), "Error", "No se pudo abrir la pantalla de registro:\n" + e.getMessage());
		}
	}

	@FXML
	private void onEliminar() {
		Alquiler alquiler = tabla.getSelectionModel().getSelectedItem();
		if (alquiler == null) {
			return;
		}
		String mensaje = "¿Desea eliminar el registro seleccionado?\n\nPatinete " + alquiler.numSerie() + "  ·  "
				+ Fechas.formatear(alquiler.fechaRecogida()) + " - " + Fechas.formatear(alquiler.fechaDevolucion());
		if (!Alertas.confirmar(ventana(), "Eliminar alquiler", mensaje)) {
			return; // Cancelar: no se hace nada
		}
		try {
			registroDAO.eliminar(alquiler.id());
			cargarPagina();
		} catch (SQLException e) {
			Alertas.error(ventana(), "Error de base de datos",
					"No se pudo eliminar el alquiler:\n" + e.getMessage());
		}
	}

	@FXML
	private void onSalir() {
		Platform.exit();
	}

	private Window ventana() {
		return txtDni.getScene().getWindow();
	}
}
