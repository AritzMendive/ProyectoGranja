package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public interface CerrarVentana {
    static void cerrarVentana(ActionEvent e) {
        Node source = (Node) e.getSource();     //Me devuelve el elemento al que hice click
        Stage stage = (Stage) source.getScene().getWindow();    //Me devuelve la ventana donde se encuentra el elemento
        stage.close();
    }
}
