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

    @FXML
    private TextField nombreTxtF;
    @FXML
    private Label welcomeText;
    @FXML
    private Button BotonLogin;
    @FXML
    private HelloApplication a;
    @FXML

    protected void onHelloButtonClick() {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error");
        error.setContentText("El usuario con el que está intentando logearse no tiene permisos para acceder a la aplicación, contacte con el CAU para poder solucionarlo");
        error.setHeaderText("Error al Iniciar Sesion");
        error.show();
    }

    @FXML
    protected void ComprobarUsuario() throws SQLException {
        ConexionBBDD connection = new ConexionBBDD();

        connection.statement.execute("use granja");
        String nombre = nombreTxtF.getText();

        ResultSet resultSet = connection.statement.executeQuery("select Nombre from usuarios where Nombre = '" + nombre + "'");
        if (resultSet.next()) {
            String nombreEnBaseDeDatos = resultSet.getString("Nombre");
            if (nombreEnBaseDeDatos.equals(nombre)) {
                System.out.println("Usuario correcto");
            }
        } else {
            System.out.println("Usuario no encontrado");
        }


    }
    @FXML
    protected void loginButton(ActionEvent event) throws IOException {
        cerrarVentana(event);
        a = new HelloApplication();
        onHelloButtonClick();
        a.mostrarVentanaSecundaria();
    }
    public static void cerrarVentana(ActionEvent e) {
        Node source = (Node) e.getSource();     //Me devuelve el elemento al que hice click
        Stage stage = (Stage) source.getScene().getWindow();    //Me devuelve la ventana donde se encuentra el elemento
        stage.close();
    }
}