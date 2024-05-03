package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HelloController {

    public Button botonLogIn;
    @FXML
    private TextField nombreTxtF;
    @FXML
    private Label welcomeText;
    @FXML
    private HelloApplication a;
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
    protected boolean ComprobarUsuario() throws SQLException {
        ConexionBBDD connection = new ConexionBBDD();

        connection.statement.execute("use granja");
        String nombre = nombreTxtF.getText();

        ResultSet resultSet = connection.statement.executeQuery("select Nombre from usuarios where Nombre = '" + nombre + "'");
        if (resultSet.next()) {
            String nombreEnBaseDeDatos = resultSet.getString("Nombre");
            if (nombreEnBaseDeDatos.equals(nombre)) {
                System.out.println("Usuario correcto");
                return true;
            }
        } else {
            System.out.println("Usuario no encontrado");
            return false;
        }
        return false;
    }

    @FXML
    protected void loginButton(ActionEvent event) throws SQLException {
        if (ComprobarUsuario())
        {
            cerrarVentana(event);
            a.mostrarVentanaSecundaria();
        }
        else
        {
            onHelloButtonClick();
        }
    }
    public static void cerrarVentana(ActionEvent e) {
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}