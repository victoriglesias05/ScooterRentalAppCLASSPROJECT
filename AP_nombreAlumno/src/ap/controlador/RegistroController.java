package ap.controlador;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ap.dao.RegistroDAO;
import ap.dao.UsuarioDAO;
import ap.modelo.Patinete;
import ap.modelo.Usuario;
import ap.util.Alertas;
import ap.util.Fechas;
import ap.util.Validador;
import ap.util.Vistas;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

/** Pantalla de registro de un alquiler nuevo. */
public class RegistroController {

	private static final String ESTILO_ERROR = "campo-error";

	@FXML
	private TextField txtDni;
	@FXML
	private TextField txtNombre;
	@FXML
	private TextField txtEmail;
	@FXML
	private TextField txtTelefono;
	@FXML
	private TextField txtPatinete;
	@FXML
	private DatePicker dpRecogida;
	@FXML
	private DatePicker dpDevolucion;
	@FXML
	private Button btnLupa;

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final RegistroDAO registroDAO = new RegistroDAO();

	/** DNI del alquiler guardado; sigue siendo null si no se llegó a guardar nada. */
	private String dniRegistrado;

	/** Qué hacer al terminar: recibe el DNI guardado o null si se canceló. */
	private Consumer<String> alVolver = dni -> {
	};

	@FXML
	private void initialize() {
		// Filtros: impiden escribir caracteres no permitidos
		txtDni.setTextFormatter(Validador.formatoDni());
		txtNombre.setTextFormatter(Validador.limite(100));
		txtEmail.setTextFormatter(Validador.limite(100));
		txtTelefono.setTextFormatter(Validador.formatoTelefono());

		// El patinete NO se puede escribir: solo se rellena desde la lista (lupa)
		txtPatinete.setEditable(false);
		txtPatinete.setFocusTraversable(false);

		configurarFecha(dpRecogida);
		configurarFecha(dpDevolucion);
		// Solo se pueden elegir días válidos en el calendario
		dpRecogida.setDayCellFactory(dp -> celdaDia(() -> LocalDate.now()));
		dpDevolucion.setDayCellFactory(dp -> celdaDia(this::fechaMinimaDevolucion));
		dpRecogida.valueProperty().addListener((obs, antigua, nueva) -> {
			LocalDate devolucion = dpDevolucion.getValue();
			if (nueva != null && devolucion != null && devolucion.isBefore(nueva)) {
				dpDevolucion.setValue(null);
			}
		});

		// El marcado en rojo desaparece en cuanto se corrige el campo
		for (TextField campo : List.of(txtDni, txtNombre, txtEmail, txtTelefono, txtPatinete)) {
			campo.textProperty().addListener((obs, antes, ahora) -> campo.getStyleClass().remove(ESTILO_ERROR));
		}
		for (DatePicker fecha : List.of(dpRecogida, dpDevolucion)) {
			fecha.valueProperty().addListener((obs, antes, ahora) -> fecha.getStyleClass().remove(ESTILO_ERROR));
		}

		// Si el DNI ya existe, al salir del campo se rellenan sus datos
		txtDni.focusedProperty().addListener((obs, tenia, tiene) -> {
			if (!tiene) {
				autocompletarUsuario();
			}
		});
	}

	// ---------------------------------------------------------------- botones

	@FXML
	private void onBuscarPatinete() {
		try {
			FXMLLoader cargador = Vistas.cargador("ListaPatinetes.fxml");
			Parent raiz = cargador.load();
			ListaPatinetesController lista = cargador.getController();
			Stage registro = (Stage) ventana();
			Stage ventanaLista = Vistas.ventana(raiz, "Alquiler de patinetes - Lista de patinetes");

			// Al terminar (Seleccionar o Cancelar) se vuelve a esta misma ventana con los datos intactos
			lista.setAlVolver(elegido -> {
				if (elegido != null) {
					txtPatinete.setText(String.valueOf(elegido.numSerie()));
				}
				Vistas.cambiar(ventanaLista, registro);
			});
			// Cerrar con la X equivale a Cancelar: se vuelve a Registro
			ventanaLista.setOnCloseRequest(e -> {
				e.consume();
				lista.cancelar();
			});
			Vistas.cambiar(registro, ventanaLista); // Registro se oculta, queda solo la Lista
		} catch (IOException e) {
			Alertas.error(ventana(), "Error", "No se pudo abrir la lista de patinetes:\n" + e.getMessage());
		}
	}

	@FXML
	private void onLimpiar() {
		txtDni.clear();
		txtNombre.clear();
		txtEmail.clear();
		txtTelefono.clear();
		txtPatinete.clear();
		dpRecogida.setValue(null);
		dpDevolucion.setValue(null);
		quitarErrores();
		txtDni.requestFocus();
	}

	@FXML
	private void onCancelar() {
		cancelar();
	}

	/** Vuelve a la pantalla anterior sin guardar nada. */
	public void cancelar() {
		alVolver.accept(dniRegistrado);
	}

	/** Quien abre esta pantalla indica aquí qué hacer al terminar (volver a la anterior). */
	public void setAlVolver(Consumer<String> alVolver) {
		this.alVolver = alVolver;
	}

	@FXML
	private void onAceptar() {
		quitarErrores();
		List<String> errores = validar();
		if (!errores.isEmpty()) {
			Alertas.advertencia(ventana(), "Revisa los datos", String.join("\n", errores));
			return;
		}

		Usuario usuario = new Usuario(txtDni.getText().trim(), txtNombre.getText().trim(),
				txtEmail.getText().trim(), Integer.parseInt(txtTelefono.getText().trim()));
		int numSerie = Integer.parseInt(txtPatinete.getText().trim());
		try {
			registroDAO.insertar(usuario, numSerie, dpRecogida.getValue(), dpDevolucion.getValue());
			dniRegistrado = usuario.dni();
			Alertas.informacion(ventana(), "Alquiler registrado", "El alquiler se ha registrado correctamente.");
			alVolver.accept(dniRegistrado);
		} catch (SQLException e) {
			Alertas.error(ventana(), "Error de base de datos",
					"No se pudo guardar el alquiler:\n" + e.getMessage());
		}
	}

	// ------------------------------------------------------------ validaciones

	/** Comprueba todos los campos, marca en rojo los incorrectos y devuelve la lista de errores. */
	private List<String> validar() {
		List<String> errores = new ArrayList<>();

		// 1. Todos los campos son obligatorios
		boolean hayVacios = false;
		for (TextField campo : List.of(txtDni, txtNombre, txtEmail, txtTelefono, txtPatinete)) {
			if (campo.getText().isBlank()) {
				marcarError(campo);
				hayVacios = true;
			}
		}
		for (DatePicker fecha : List.of(dpRecogida, dpDevolucion)) {
			if (fecha.getValue() == null) {
				marcarError(fecha);
				hayVacios = true;
			}
		}
		if (hayVacios) {
			errores.add("• Todos los campos son obligatorios.");
		}

		// 2. Formatos (solo de los campos que no están vacíos)
		if (!txtDni.getText().isBlank() && !Validador.dniValido(txtDni.getText().trim())) {
			marcarError(txtDni);
			errores.add("• El DNI debe tener 8 números seguidos de una letra (por ejemplo, 12345678A).");
		}
		if (!txtEmail.getText().isBlank() && !Validador.emailValido(txtEmail.getText().trim())) {
			marcarError(txtEmail);
			errores.add("• El email no tiene un formato válido (por ejemplo, usuario@correo.com).");
		}
		if (!txtTelefono.getText().isBlank() && !Validador.telefonoValido(txtTelefono.getText().trim())) {
			marcarError(txtTelefono);
			errores.add("• El teléfono solo puede contener números (9 dígitos).");
		}

		// 3. Fechas
		LocalDate recogida = dpRecogida.getValue();
		LocalDate devolucion = dpDevolucion.getValue();
		if (recogida != null && recogida.isBefore(LocalDate.now())) {
			marcarError(dpRecogida);
			errores.add("• La fecha de recogida no puede ser anterior al día de hoy.");
		}
		if (recogida != null && devolucion != null && devolucion.isBefore(recogida)) {
			marcarError(dpDevolucion);
			errores.add("• La fecha de devolución no puede ser anterior a la fecha de recogida.");
		}
		return errores;
	}

	private void marcarError(Control control) {
		if (!control.getStyleClass().contains(ESTILO_ERROR)) {
			control.getStyleClass().add(ESTILO_ERROR);
		}
	}

	private void quitarErrores() {
		for (Control control : List.of(txtDni, txtNombre, txtEmail, txtTelefono, txtPatinete, dpRecogida,
				dpDevolucion)) {
			control.getStyleClass().remove(ESTILO_ERROR);
		}
	}

	// ----------------------------------------------------------------- ayudas

	private void configurarFecha(DatePicker selector) {
		selector.setConverter(Fechas.conversor());
		selector.setPromptText("DD/MM/AAAA");
		selector.setEditable(false); // se elige en el calendario: no se puede escribir una fecha mal
		selector.getEditor().setOnMouseClicked(e -> selector.show());
	}

	/** Celda del calendario que se desactiva si la fecha es anterior a la mínima permitida. */
	private DateCell celdaDia(java.util.function.Supplier<LocalDate> minima) {
		return new DateCell() {
			@Override
			public void updateItem(LocalDate fecha, boolean vacia) {
				super.updateItem(fecha, vacia);
				setDisable(!vacia && fecha != null && fecha.isBefore(minima.get()));
			}
		};
	}

	private LocalDate fechaMinimaDevolucion() {
		LocalDate recogida = dpRecogida.getValue();
		return recogida != null ? recogida : LocalDate.now();
	}

	private void autocompletarUsuario() {
		String dni = txtDni.getText();
		if (!Validador.dniValido(dni)) {
			return;
		}
		try {
			Usuario usuario = usuarioDAO.buscarPorDNI(dni);
			if (usuario != null) {
				// Solo rellena lo que esté vacío, para no pisar lo que ya se haya escrito
				if (txtNombre.getText().isBlank()) {
					txtNombre.setText(usuario.nombreCompleto());
				}
				if (txtEmail.getText().isBlank()) {
					txtEmail.setText(usuario.email());
				}
				if (txtTelefono.getText().isBlank()) {
					txtTelefono.setText(String.valueOf(usuario.telefono()));
				}
			}
		} catch (SQLException e) {
			// Es solo una ayuda. Si falla la conexión, el error se mostrará al pulsar Aceptar.
		}
	}

	private Window ventana() {
		return txtDni.getScene().getWindow();
	}
}
