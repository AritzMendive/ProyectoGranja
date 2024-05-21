package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AnadirAnimal implements Initializable {

    @FXML
    ListView<Alimento> AlimentacionListView;
    ObservableList<Alimento> listaAlimentos = FXCollections.observableArrayList();

    @FXML
    private TextField fieldNombre;

    @FXML
    private TextField fieldStock;

    @FXML
    private TextField NombreTXT;

    @FXML
    private Button sumarBTN;

    @FXML
    private Button restarBTN;

    AlimentosSuministrosController controllerError;

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controllerError = new AlimentosSuministrosController();
        AlimentacionListView.setItems(listaAlimentos);
        AlimentacionListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fieldNombre.setText(newValue.getNombre());
                fieldStock.setText("0");
            }
        });

        sumarBTN.setOnAction(event -> {
            int currentStock = Integer.parseInt(fieldStock.getText());
            fieldStock.setText(String.valueOf(currentStock + 1));
        });

        restarBTN.setOnAction(event -> {
            int currentStock = Integer.parseInt(fieldStock.getText());
            if (currentStock > 0) {
                fieldStock.setText(String.valueOf(currentStock - 1));
            }
        });
    }

    public void mostrarAlimentos() {
        listaAlimentos.clear();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/granja", "root", "root")) {
            String selectSQL = "SELECT * FROM Alimentos";
            try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String nombre = resultSet.getString("nombre");
                    int stock = resultSet.getInt("stock");
                    int precio = resultSet.getInt("precio");
                    listaAlimentos.add(new Alimento(nombre, stock, precio));
                }
            }
        } catch (SQLException e) {
            controllerError.mostrarError("Error al cargar los alimentos.");
            e.printStackTrace();
        }
    }

    @FXML
    public void anadirAnimal(MouseEvent event) {
        Alimento alimentoSeleccionado = AlimentacionListView.getSelectionModel().getSelectedItem();
        String nombreAnimal = NombreTXT.getText();
        String cantidad = fieldStock.getText();

        if (alimentoSeleccionado != null && !nombreAnimal.isEmpty() && !cantidad.isEmpty()) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/granja", "root", "root")) {
                boolean animalExiste = false;

                String checkAnimalSQL = "SELECT COUNT(*) FROM StockAnimales WHERE Nombre = ?";
                try (PreparedStatement checkStatement = connection.prepareStatement(checkAnimalSQL)) {
                    checkStatement.setString(1, nombreAnimal);
                    ResultSet resultSet = checkStatement.executeQuery();
                    resultSet.next();
                    int count = resultSet.getInt(1);
                    animalExiste = count > 0;
                }

                if (!animalExiste) {
                    String insertAnimalSQL = "INSERT INTO StockAnimales (Nombre, Descripcion, Stock, Precio) VALUES (?, '', 0, 0.0)";
                    try (PreparedStatement statement = connection.prepareStatement(insertAnimalSQL)) {
                        statement.setString(1, nombreAnimal);
                        statement.executeUpdate();
                        controllerError.mostrarMensaje("El nuevo animal se ha añadido a la base de datos correctamente.");
                    }
                }

                String insertSQL = "INSERT INTO Alimentar (NOMBREANIMAL, NOMBREALIMENTO, CANTIDAD) VALUES (?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
                    statement.setString(1, nombreAnimal);
                    statement.setString(2, alimentoSeleccionado.getNombre());
                    statement.setInt(3, Integer.parseInt(cantidad));
                    statement.executeUpdate();
                    controllerError.mostrarMensaje("Nueva alimentación añadida para el animal.");
                }
            } catch (SQLException e) {
                controllerError.mostrarError("Error al añadir el alimento.");
                e.printStackTrace();
            }
        }
    }
}
