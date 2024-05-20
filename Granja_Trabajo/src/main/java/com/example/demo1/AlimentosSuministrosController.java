package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AlimentosSuministrosController implements CerrarVentana{
    Connection connection;

    @FXML
    private TextField fieldAlimentosSuministros;
    @FXML
    private TextField fieldCantidad;
    @FXML
    private TextField fieldPrecio;

    @FXML
    private Button botonCerrarSesion;
    @FXML
    private Button buttonInsertar;
    private HelloApplication a;
    public AlimentosSuministrosController()
    {
        a = new HelloApplication();
    }

    @FXML
    private void inserTarDatos() throws SQLException{
        String nombre = fieldAlimentosSuministros.getText();
        String puesto = fieldCantidad.getText();
        String salarioStr = fieldPrecio.getText();
        int stock;

        try {
            stock = Integer.parseInt(salarioStr);
        } catch (NumberFormatException e) {
            mostrarError("Inserte un numero entero por favor.");
            return;
        }
        if (nombre.isEmpty() || puesto == null || salarioStr.isEmpty()) {
            mostrarError("Rellene todo por favor.");
        } else {
            // Insertar datos en la base de datos
            if (insertarAlimentoEnBD(nombre, stock, stock)) {
                mostrarMensaje("Alimento " + nombre + " introducido en nuestra base de dats, gracias.");
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

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/granja", "root", "root")) {
            String insertSQL = "INSERT INTO alimentos (Nombre, Stock, Precio) VALUES (?, ?, ?)";
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
        CerrarVentana.cerrarVentana(event);
        a.mostrarVentanaSecundaria();
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
            // Cargar la nueva ventana de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();

            // Cerrar la ventana actual
            Stage stageActual = (Stage) botonCerrarSesion.getScene().getWindow();
            stageActual.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
