package com.example.demo1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class HelloApplication extends Application {
    private Stage stagePrincipal;
    //private Stage dos;
    public HelloApplication()
    {
        stagePrincipal = new Stage();
    }
    @Override
    public void start(Stage stagePrincipal) {
        this.stagePrincipal = stagePrincipal;
        mostrarVentanaPrincipal();
    }
    public void mostrarVentanaPrincipal() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("AnadirAnimal.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            stagePrincipal.setTitle("Granja");
            stagePrincipal.setScene(scene);
            stagePrincipal.show();
        } catch (IOException ignored) {
        }
    }
    public void mostrarVentanaSecundaria() {
        try {
            FXMLLoader loader = new FXMLLoader(AlimentosTablas.class.getResource("alimentacionTablas.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            stagePrincipal.setTitle("Granja");
            stagePrincipal.setScene(scene);
            stagePrincipal.show();

        } catch (Exception ignored) {
        }
    }
    public void mostrarVentanaTres() {
        try {
            FXMLLoader loader = new FXMLLoader(AlimentosSuministrosController.class.getResource("alimentacion.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            stagePrincipal.setTitle("Granja");
            stagePrincipal.setScene(scene);
            stagePrincipal.show();

        } catch (Exception ignored) {
        }
    }
}