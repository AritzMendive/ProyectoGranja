package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

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
            }
        }
    }

    @FXML
    private void handleHelloView(ActionEvent event) {
        mostrarVentana("hello-view.fxml", "Hello View");
    }

    @FXML
    private void handleAlimentacion(ActionEvent event) {
        mostrarVentana("alimentacion.fxml", "Alimentación");
    }

    @FXML
    private void handleAlimentacionTablas(ActionEvent event) {
        mostrarVentana("alimentacionTablas.fxml", "Alimentación Tablas");
    }

    @FXML
    private void handleAnadirAnimal(ActionEvent event) {
        mostrarVentana("AnadirAnimal.fxml", "Añadir Animal");
    }

    @FXML
    private void handleDarDeComer(ActionEvent event) {
        mostrarVentana("dardecomer.fxml", "Dar de Comer");
    }

    @FXML
    private void handleEditarAnimal(ActionEvent event) {
        mostrarVentana("editarAnimal.fxml", "Editar Animal");
    }

    @FXML
    private void handleGestionDietas(ActionEvent event) {
        mostrarVentana("GestionDietas.fxml", "Gestionar Dietas");
    }

    @FXML
    private void handleEditarPerfil(ActionEvent event) {
        mostrarVentana("editarPerfil.fxml", "Editar perfil");
    }

    @FXML
    private void handleGestionSuministros(ActionEvent event) {
        mostrarVentana("alimentacion.fxml", "Gestionar Suministros y Alimentos");
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

    private void mostrarError(String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
