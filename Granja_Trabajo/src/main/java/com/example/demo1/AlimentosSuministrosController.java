package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AlimentosSuministrosController {
    Connection connection;

    @FXML
    private TextField fieldAlimentosSuministros;
    @FXML
    private TextField fieldCantidad;
    @FXML
    private TextField fieldPrecio;
    @FXML
    private Button buttonInsertar;



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

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
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

}
