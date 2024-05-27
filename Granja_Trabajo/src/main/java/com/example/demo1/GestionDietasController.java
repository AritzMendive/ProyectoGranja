package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class GestionDietasController {

    @FXML
    private TableView<Dieta> tablaDietas;
    @FXML
    private TableColumn<Dieta, String> columnaAnimal;
    @FXML
    private TableColumn<Dieta, String> columnaAlimento;
    @FXML
    private TableColumn<Dieta, Integer> columnaCantidad;
    @FXML
    private TextField campoAnimal;
    @FXML
    private TextField campoAlimento;
    @FXML
    private Button volverBtn;
    @FXML
    private TextField campoCantidad;
    @FXML
    private Button botonAgregar;
    @FXML
    private Button botonEliminar;
    @FXML
    private Button botonActualizar;

    private Connection connection;

    public GestionDietasController() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://monorail.proxy.rlwy.net:55810/railway", "root", "MNvVtHFDEuiIcdlCusLWfBxfFqPvemBP");
        } catch (SQLException e) {
            mostrarError("Error al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        columnaAnimal.setCellValueFactory(cellData -> cellData.getValue().animalProperty());
        columnaAlimento.setCellValueFactory(cellData -> cellData.getValue().alimentoProperty());
        columnaCantidad.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());

        cargarDietas();
    }



    private void cargarDietas() {
        tablaDietas.getItems().clear();
        String selectSQL = "SELECT Animal, Alimento, Cantidad FROM dietas";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {
            while (resultSet.next()) {
                String animal = resultSet.getString("Animal");
                String alimento = resultSet.getString("Alimento");
                int cantidad = resultSet.getInt("Cantidad");
                Dieta dieta = new Dieta(animal, alimento, cantidad);
                tablaDietas.getItems().add(dieta);
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar las dietas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void agregarDieta() {
        String animal = campoAnimal.getText().trim();
        String alimento = campoAlimento.getText().trim();
        int cantidad;

        try {
            cantidad = Integer.parseInt(campoCantidad.getText().trim());
        } catch (NumberFormatException e) {
            mostrarError("La cantidad debe ser un número entero.");
            return;
        }

        if (animal.isEmpty() || alimento.isEmpty() || cantidad <= 0) {
            mostrarError("Todos los campos son obligatorios y la cantidad debe ser mayor que 0.");
            return;
        }

        String insertSQL = "INSERT INTO dietas (Animal, Alimento, Cantidad) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
            statement.setString(1, animal);
            statement.setString(2, alimento);
            statement.setInt(3, cantidad);
            statement.executeUpdate();
            mostrarMensaje("Dieta agregada correctamente.");
            cargarDietas();
            limpiarCampos();
        } catch (SQLException e) {
            mostrarError("Error al agregar la dieta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarDieta() {
        Dieta dietaSeleccionada = tablaDietas.getSelectionModel().getSelectedItem();
        if (dietaSeleccionada == null) {
            mostrarError("Seleccione una dieta para eliminar.");
            return;
        }

        String deleteSQL = "DELETE FROM dietas WHERE Animal = ? AND Alimento = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
            statement.setString(1, dietaSeleccionada.getAnimal());
            statement.setString(2, dietaSeleccionada.getAlimento());
            statement.executeUpdate();
            mostrarMensaje("Dieta eliminada correctamente.");
            cargarDietas();
        } catch (SQLException e) {
            mostrarError("Error al eliminar la dieta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void actualizarDieta() {
        Dieta dietaSeleccionada = tablaDietas.getSelectionModel().getSelectedItem();
        if (dietaSeleccionada == null) {
            mostrarError("Seleccione una dieta para actualizar.");
            return;
        }

        String animal = campoAnimal.getText().trim();
        String alimento = campoAlimento.getText().trim();
        int cantidad;

        try {
            cantidad = Integer.parseInt(campoCantidad.getText().trim());
        } catch (NumberFormatException e) {
            mostrarError("La cantidad debe ser un número entero.");
            return;
        }

        if (animal.isEmpty() || alimento.isEmpty() || cantidad <= 0) {
            mostrarError("Todos los campos son obligatorios y la cantidad debe ser mayor que 0.");
            return;
        }

        String updateSQL = "UPDATE dietas SET Cantidad = ? WHERE Animal = ? AND Alimento = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateSQL)) {
            statement.setInt(1, cantidad);
            statement.setString(2, animal);
            statement.setString(3, alimento);
            statement.executeUpdate();
            mostrarMensaje("Dieta actualizada correctamente.");
            cargarDietas();
            limpiarCampos();
        } catch (SQLException e) {
            mostrarError("Error al actualizar la dieta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        campoAnimal.clear();
        campoAlimento.clear();
        campoCantidad.clear();
    }

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleVolverAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dardecomer.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Dar de Comer");
            stage.setScene(new Scene(root));
            stage.show();

            // Cerrar la ventana actual
            Stage currentStage = (Stage) volverBtn.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar la ventana de Dar de Comer: " + e.getMessage());
        }
    }
}
