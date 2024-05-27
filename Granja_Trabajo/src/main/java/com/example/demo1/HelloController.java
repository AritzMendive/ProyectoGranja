package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class HelloController {

    @FXML
    private Button botonLogIn;
    @FXML
    private TextField nombreTxtF;
    @FXML
    private PasswordField contrasenyaTxtF;
    @FXML
    private Label welcomeText;
    @FXML
    private Button botonCerrarSesion;
    @FXML
    private ImageView fotoPerfilView;
    @FXML
    private ImageView defaultImageView;
    private HelloApplication a;

    public HelloController() {
        a = new HelloApplication();
    }

    @FXML
    public void initialize() {
        Usuario usuario = UsuarioManager.getUsuarioActual();
        if (usuario != null && usuario.getRutaFotoPerfil() != null) {
            fotoPerfilView.setImage(new Image(usuario.getRutaFotoPerfil()));
            defaultImageView.setVisible(false); // Oculta la imagen predeterminada
        } else {
            fotoPerfilView.setImage(null);
            defaultImageView.setVisible(true); // Muestra la imagen predeterminada
        }
    }

    @FXML
    protected void onHelloButtonClick() {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error");
        error.setContentText("El usuario con el que está intentando logearse no tiene permisos para acceder a la aplicación, contacte con el CAU para poder solucionarlo");
        error.setHeaderText("Error al Iniciar Sesion");
        error.show();
    }

    @FXML
    protected Usuario ComprobarUsuario() throws SQLException {
        ConexionBBDD connection = new ConexionBBDD();
        connection.statement.execute("USE granja");

        String nombre = nombreTxtF.getText();
        String contrasenya = contrasenyaTxtF.getText();

        String query = "SELECT IdUsuario, Nombre, Contraseña, Rol, FotoPerfil, Descripcion FROM Usuarios WHERE Nombre = ? AND Contraseña = ?";
        try (PreparedStatement preparedStatement = connection.getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, contrasenya);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("IdUsuario");
                String rol = resultSet.getString("Rol");
                String nombreUsuario = resultSet.getString("Nombre");
                String contrasenaUsuario = resultSet.getString("Contraseña");
                String fotoPerfil = resultSet.getString("FotoPerfil");
                String descripcion = resultSet.getString("Descripcion");
                return new Usuario(id, nombreUsuario, contrasenaUsuario, rol, fotoPerfil, descripcion);
            } else {
                System.out.println("Usuario no encontrado");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            connection.close();
        }
    }

    @FXML
    protected void loginButton(ActionEvent event) throws SQLException {
        Usuario usuario = ComprobarUsuario();
        if (usuario != null) {
            UsuarioManager.setUsuarioActual(usuario); // Configurar el usuario actual
            cerrarVentana(event);
            if (Objects.equals(usuario.getRol(), "Proveedor")) {
                a.mostrarVentanaSecundaria();
            } else if (Objects.equals(usuario.getRol(), "Admin")) {
                a.mostrarVentanaCuatro();
            } else if (Objects.equals(usuario.getRol(), "Granjero")) {
                a.mostrarVentanaTres();
            } else {
                mostrarError("Usuario no encontrado o contraseña incorrecta.");
            }
        } else {
            mostrarError("Usuario no encontrado o contraseña incorrecta.");
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
}
