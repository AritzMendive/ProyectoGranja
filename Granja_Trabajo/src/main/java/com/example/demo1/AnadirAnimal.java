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
import java.sql.ResultSet;
import java.sql.SQLException;

public class AnadirAnimal {
    @FXML
    private Button editarBTN;
    @FXML
    private Button restarBTN;
    @FXML
    private Button mostrarAlimentos;
    @FXML
    private Button GestionBTN;
    @FXML
    private TextField fieldNombre;
    @FXML
    private TextField fieldStock;
    @FXML
    private Button botonCerrarSesion;
    @FXML
    private ListView<String> AlimentacionListView;



    @FXML
    private void editarStock() {
        String selectedAlimento = AlimentacionListView.getSelectionModel().getSelectedItem();
        if (selectedAlimento == null) {
            mostrarError("Seleccione un alimento de la lista.");
            return;
        }

        String nombre = selectedAlimento.split(",")[0].split(":")[1].trim();
        String cantidadStr = fieldStock.getText().trim();
        if (cantidadStr.isEmpty()) {
            mostrarError("Ingrese una cantidad válida.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
        } catch (NumberFormatException e) {
            mostrarError("La cantidad debe ser un número entero.");
            return;
        }

        modificarStockAnimal(nombre, cantidad);
    }

    private void modificarStockAnimal(String nombre, int cantidad) {
        if (nombre.isEmpty()) {
            mostrarError("El nombre del alimento no puede estar vacío.");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://monorail.proxy.rlwy.net:55810/railway", "root", "MNvVtHFDEuiIcdlCusLWfBxfFqPvemBP")) {
            String updateSQL = "UPDATE alimentos SET Stock = ? WHERE Nombre = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateSQL)) {
                statement.setInt(1, cantidad);
                statement.setString(2, nombre);
                int filasActualizadas = statement.executeUpdate();
                if (filasActualizadas > 0) {
                    mostrarMensaje("Stock de " + nombre + " actualizado correctamente.");
                    cargarAlimentos(); // Recargar la lista de alimentos
                } else {
                    mostrarError("No se encontró el alimento con nombre " + nombre + ".");
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al actualizar el stock en la base de datos.");
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

    @FXML
    private void mostrarAlimentos(ActionEvent event) {
        cargarAlimentos();
    }

    private void cargarAlimentos() {
        AlimentacionListView.getItems().clear(); // Limpiar la lista antes de actualizar

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://monorail.proxy.rlwy.net:55810/railway", "root", "MNvVtHFDEuiIcdlCusLWfBxfFqPvemBP")) {
            String selectSQL = "SELECT Nombre, Stock FROM alimentos";
            try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String nombre = resultSet.getString("Nombre");
                    int stock = resultSet.getInt("Stock");

                    String alimentoInfo = String.format("Nombre: %s, Stock: %d", nombre, stock);
                    AlimentacionListView.getItems().add(alimentoInfo); // Añadir cada alimento a la lista
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar los alimentos.");
            e.printStackTrace();
        }
    }

    @FXML
    private void gestionarAnimales(ActionEvent event) {
        cambiarVentana(event, "dardecomer.fxml");
    }

    private void cambiarVentana(ActionEvent event, String fxml) {
        try {
            Node source = (Node) event.getSource();
            Stage stageActual = (Stage) source.getScene().getWindow();
            stageActual.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    private void handleEditarPerfil(ActionEvent event) {
        mostrarVentana("editarPerfil.fxml", "Editar perfil");
    }

    private void mostrarVentana(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar la ventana: " + e.getMessage());
        }
    }



}
