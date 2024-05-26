package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditarAnimalController {
    @FXML
    private TextField nombreField;
    @FXML
    private TextField stockField;

    private Animal animal;
    private Connection connection;

    public void setAnimal(Animal animal, Connection connection) {
        this.animal = animal;
        this.connection = connection;

        nombreField.setText(animal.getNombre());
        stockField.setText(String.valueOf(animal.getStock()));
    }

    @FXML
    private void guardarCambios() {
        String nuevoNombre = nombreField.getText().trim();
        String stockStr = stockField.getText().trim();

        if (nuevoNombre.isEmpty() || stockStr.isEmpty()) {
            mostrarError("Todos los campos son obligatorios.");
            return;
        }

        int nuevoStock;
        try {
            nuevoStock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            mostrarError("El stock debe ser un número entero.");
            return;
        }

        String updateSQL = "UPDATE animales SET Nombre = ?, Stock = ? WHERE Nombre = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateSQL)) {
            statement.setString(1, nuevoNombre);
            statement.setInt(2, nuevoStock);
            statement.setString(3, animal.getNombre());
            statement.executeUpdate();
            mostrarMensaje("Animal actualizado correctamente.");
            Stage stage = (Stage) nombreField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            mostrarError("Error al actualizar el animal: " + e.getMessage());
            e.printStackTrace();
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
}
