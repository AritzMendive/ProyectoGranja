package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AlimentosSuministrosController {
    Connection connection;

    @FXML
    private Button botonCerrarSesion;
    @FXML
    private TextField fieldAlimentosSuministros;
    @FXML
    private TextField fieldCantidad;
    @FXML
    private TextField fieldPrecio;
    @FXML
    private Button buttonInsertar;
    private HelloApplication a;
    public AlimentosSuministrosController()
    {
        a = new HelloApplication();
    }

    @FXML
    private void inserTarDatos() throws SQLException {
        String nombre = fieldAlimentosSuministros.getText();
        String cantidadStr = fieldCantidad.getText(); // Usar el valor correcto del campo de cantidad
        String precioStr = fieldPrecio.getText(); // Usar el valor correcto del campo de precio
        int stock;
        int precio;

        try {
            stock = Integer.parseInt(cantidadStr); // Convertir correctamente el valor de cantidad a stock
            precio = Integer.parseInt(precioStr); // Convertir correctamente el valor de precio a entero
        } catch (NumberFormatException e) {
            mostrarError("Inserte un numero entero por favor.");
            return;
        }
        if (nombre.isEmpty() || cantidadStr.isEmpty() || precioStr.isEmpty()) {
            mostrarError("Rellene todo por favor.");
        } else {
            // Insertar datos en la base de datos
            if (insertarAlimentoEnBD(nombre, stock, precio)) {
                mostrarMensaje("Alimento " + nombre + " introducido en nuestra base de datos, gracias.");
                limpiarCampos();
            } else {
                // Mostrar mensaje de error
                mostrarError("Error al insertar alimento en la base de datos.");
            }
        }
    }

    public void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    private void limpiarCampos() {
        fieldAlimentosSuministros.clear();
        fieldCantidad.clear();
        fieldPrecio.clear();
    }

    private boolean insertarAlimentoEnBD(String nombre, int stock, int precio) throws SQLException {
        ConexionBBDD conexion = new ConexionBBDD();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://monorail.proxy.rlwy.net:55810/railway", "root", "MNvVtHFDEuiIcdlCusLWfBxfFqPvemBP")) {
            String insertSQL = "INSERT INTO Alimentos (Nombre, Stock, Precio) VALUES (?, ?, ?)";
            //Se hace la insercion en la base de datos.
            try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
                statement.setString(1, nombre);
                statement.setInt(2, stock);
                statement.setInt(3, precio);
                int filasInsertadas = statement.executeUpdate();
                return filasInsertadas > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Hubo un error durante la inserción en la base de datos", e);
        }
    }
    public void cambioVentana(ActionEvent event)
    {
        cerrarVentana(event);
        a.mostrarVentanaSecundaria();
    }
    public static void cerrarVentana(ActionEvent e) {
        Node source = (Node) e.getSource();     //Me devuelve el elemento al que hice click
        Stage stage = (Stage) source.getScene().getWindow();    //Me devuelve la ventana donde se encuentra el elemento
        stage.close();
    }

    @FXML
    private void handleCerrarSesion(ActionEvent event) {
        mostrarConfirmacionCerrarSesion();
    }

    private void mostrarConfirmacionCerrarSesion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de cierre de sesión");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro de que desea cerrar sesión?");

        ButtonType buttonTypeSi = new ButtonType("Sí");
        ButtonType buttonTypeNo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeSi) {
                cerrarSesion();
            }
        });
    }

    private void cerrarSesion() {
        System.out.println("Sesión cerrada");
        redirigirALogin();
    }

    private void redirigirALogin() {
        try {
            // Obtener la ventana actual y cerrarla
            Stage stageActual = (Stage) botonCerrarSesion.getScene().getWindow();
            stageActual.close();

            // Cargar la nueva ventana de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private ImageView fotoPerfilView;
    @FXML
    private void initialize() {
        UsuarioManager.rutaFotoPerfilProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fotoPerfilView.setImage(new Image(newValue));
            }
        });

        // Inicializar la imagen de perfil al cargar la pantalla
        if (UsuarioManager.getUsuarioActual() != null) {
            String rutaFotoPerfil = UsuarioManager.getUsuarioActual().getRutaFotoPerfil();
            if (rutaFotoPerfil != null) {
                fotoPerfilView.setImage(new Image(rutaFotoPerfil));
            } else {
                fotoPerfilView.setImage(new Image(getClass().getResource("/Imagenes/6662120.png").toString()));
            }
        }
    }
}
