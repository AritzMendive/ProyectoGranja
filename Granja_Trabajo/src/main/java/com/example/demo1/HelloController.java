package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class HelloController implements CerrarVentana{

    public Button botonLogIn;
    @FXML
    private TextField nombreTxtF;

    @FXML
    private PasswordField contrasenyaTxtF;
    @FXML
    private Label welcomeText;
    @FXML
    private HelloApplication a;

    @FXML
    private Button botonCerrarSesion;

    public HelloController()
    {
        a = new HelloApplication();
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
    protected String ComprobarUsuario() throws SQLException {
        ConexionBBDD connection = new ConexionBBDD();

        connection.statement.execute("use granja");
        String nombre = nombreTxtF.getText();
        String contrasenya = contrasenyaTxtF.getText();

        ResultSet resultSet = connection.statement.executeQuery("select Nombre, Contraseña, Rol from usuarios where Nombre = '" + nombre + "' AND Contraseña = '" + contrasenya + "'");
        if (resultSet.next()) {
            String nombreEnBaseDeDatos = resultSet.getString("Nombre");
            String rol = resultSet.getString("Rol");
            if (nombreEnBaseDeDatos.equals(nombre)) {
                System.out.println(rol);
                return rol;
            }
        } else {
            System.out.println("Usuario no encontrado");
            return null;
        }
        return null;
    }
    @FXML
    protected void loginButton(ActionEvent event) throws SQLException {
        if (Objects.equals(ComprobarUsuario(), "Proveedor"))
        {
            CerrarVentana.cerrarVentana(event);
            a.mostrarVentanaSecundaria();
        }
        else if (Objects.equals(ComprobarUsuario(), "Admin"))
        {
            CerrarVentana.cerrarVentana(event);
            a.mostrarVentanaPrincipal();
        }
        else if (Objects.equals(ComprobarUsuario(), "Granjero"))
        {
            CerrarVentana.cerrarVentana(event);
            a.mostrarVentanaPrincipal();
        }
        else
        {
            onHelloButtonClick();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
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