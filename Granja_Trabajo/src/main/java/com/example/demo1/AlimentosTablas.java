package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AlimentosTablas implements Initializable {

    @FXML
    ListView<Alimento> alimentosView;
    ObservableList<Alimento> listaAlimentos = FXCollections.observableArrayList();

    @FXML
    private TextField fieldNombre;

    @FXML
    private TextField fieldStock;

    @FXML
    private TextField fieldPrecio;

    AlimentosSuministrosController controllerError;


    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controllerError = new AlimentosSuministrosController();
        alimentosView.setItems(listaAlimentos);
    }

    public void refrescarDatos() {
        listaAlimentos.clear();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/granja", "root", "root")) {
            String selectSQL = "SELECT * FROM Alimentos";
            try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String nombre = resultSet.getString("nombre");
                    int stock = resultSet.getInt("stock");
                    int precio = resultSet.getInt("precio");
                    int id = resultSet.getInt("IdAlimento");
                    listaAlimentos.add(new Alimento(nombre, stock, precio, id));
                }
            }
        } catch (SQLException e) {
            controllerError.mostrarError("Error al cargar los alimentos.");
            e.printStackTrace();
        }
    }

    public void mostrarInfoAlimento(javafx.scene.input.MouseEvent mouseEvent) {
        Alimento alimentoSeleccionado = alimentosView.getSelectionModel().getSelectedItem();
        fieldNombre.setText(alimentoSeleccionado.getNombre());
        fieldStock.setText(String.valueOf(alimentoSeleccionado.getStock()));
        fieldPrecio.setText(String.valueOf(alimentoSeleccionado.getPrecio()));

    }

    public void eliminarEmpleado(ActionEvent actionEvent) {
        Alimento empleadoSeleccionado = alimentosView.getSelectionModel().getSelectedItem();
        if (empleadoSeleccionado != null) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/granja", "root", "root")) {
                String deleteSQL = "DELETE FROM Alimentos WHERE IdAlimento = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
                    statement.setInt(1, empleadoSeleccionado.getId());
                    int filasEliminadas = statement.executeUpdate();
                    if (filasEliminadas > 0) {
                        controllerError.mostrarMensaje("Alimento eliminado correctamente.");
                        listaAlimentos.remove(empleadoSeleccionado);
                    } else {
                       controllerError.mostrarError("No se pudo eliminar el alimento.");
                    }
                }
            } catch (SQLException e) {
                controllerError.mostrarError("Error al eliminar el alimento.");
                e.printStackTrace();
            }
        } else {
            controllerError.mostrarError("Por favor, seleccione un alimento de la lista.");
        }
    }
}
