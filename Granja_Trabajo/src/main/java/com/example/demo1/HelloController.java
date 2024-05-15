package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class HelloController {

    public Button botonLogIn;
    @FXML
    private TextField nombreTxtF;

    @FXML
    private PasswordField contrasenyaTxtF;
    @FXML
    private Label welcomeText;
    @FXML
    private HelloApplication a;

    public HelloController() {
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
        if (Objects.equals(ComprobarUsuario(), "Proveedor")) {
            cerrarVentana(event);
            a.mostrarVentanaSecundaria();
        } else if (Objects.equals(ComprobarUsuario(), "Admin")) {
            cerrarVentana(event);
            a.mostrarVentanaPrincipal();
        } else if (Objects.equals(ComprobarUsuario(), "Granjero")) {
            cerrarVentana(event);
            a.mostrarVentanaPrincipal();
        }
    }

    public static void cerrarVentana(ActionEvent e) {
        Node source = (Node) e.getSource();     //Me devuelve el elemento al que hice click
        Stage stage = (Stage) source.getScene().getWindow();    //Me devuelve la ventana donde se encuentra el elemento
        stage.close();
    }
}