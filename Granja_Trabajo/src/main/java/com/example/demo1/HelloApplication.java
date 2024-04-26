package com.example.demo1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Granja");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader("src/main/BBDD/granja.sql"));

        try {
            String st;
            ArrayList<String> lineas = new ArrayList<>();
            while((st = br.readLine()) != null){
                lineas.add(st);
            }

            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "root");
            Statement statement = connection.createStatement();
            for(String linea : lineas){
                statement.executeUpdate(linea);
            }
/*
            ResultSet resultSet = statement.executeQuery("select * from alimentos");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("Nombre"));
                System.out.println(resultSet.getString("Stock"));
            }
*/

        }catch (Exception e) {
            e.printStackTrace();
        }

        launch();

    }
}