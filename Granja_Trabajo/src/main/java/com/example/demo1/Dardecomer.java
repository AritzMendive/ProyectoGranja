package com.example.demo1;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class Dardecomer implements Initializable {
    public TableView<String> tablaanimales;
    public TableColumn<String, String> animalc;
    public TableColumn<String, Integer> stockc;
    public Button NAnimalBTN;
    public Button EliminarBTN;
    public Button EditarBTN;
    public Button ComerBTN;
    public Button MataderoBTN;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
