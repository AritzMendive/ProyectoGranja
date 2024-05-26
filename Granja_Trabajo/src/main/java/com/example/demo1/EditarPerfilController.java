package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditarPerfilController {
    @FXML
    private TextField nombreField;

    @FXML
    private TextField contrasenaField;

    @FXML
    private ImageView fotoPerfilView;

    private String rutaFotoPerfil;

    private ConexionBBDD conexion;

    public EditarPerfilController() {
        try {
            conexion = new ConexionBBDD();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        Usuario usuario = UsuarioManager.getUsuarioActual();
        if (usuario != null) {
            nombreField.setText(usuario.getNombre());
            contrasenaField.setText(usuario.getContrasena());
            rutaFotoPerfil = usuario.getRutaFotoPerfil();
            if (rutaFotoPerfil != null) {
                fotoPerfilView.setImage(new Image(rutaFotoPerfil));
            }
        }
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagenes", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            rutaFotoPerfil = file.toURI().toString();
            fotoPerfilView.setImage(new Image(rutaFotoPerfil));
        }
    }

    @FXML
    private void guardarCambios() {
        String nombre = nombreField.getText();
        String contrasena = contrasenaField.getText();
        int idUsuario = UsuarioManager.getUsuarioActual().getId(); // Asume que el ID del usuario es conocido

        String sql = "UPDATE Usuarios SET Nombre = ?, Contraseña = ?, FotoPerfil = ? WHERE IdUsuario = ?";

        try (Connection connection = conexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nombre);
            statement.setString(2, contrasena);
            statement.setString(3, rutaFotoPerfil);
            statement.setInt(4, idUsuario);
            statement.executeUpdate();

            // Actualizar la información del usuario en UsuarioManager
            Usuario usuario = UsuarioManager.getUsuarioActual();
            if (usuario != null) {
                usuario.setNombre(nombre);
                usuario.setContrasena(contrasena);
                usuario.setRutaFotoPerfil(rutaFotoPerfil);
            }

            // Notificar el cambio de la imagen de perfil
            UsuarioManager.notificarCambioFotoPerfil();

            // Redirigir a la ventana de inicio de sesión
            redirigirALogin();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button botonCerrarSesion;

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
}
