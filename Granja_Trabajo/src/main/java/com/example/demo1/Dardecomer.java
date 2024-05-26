package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class Dardecomer {

    @FXML
    private TableView<Animal> tablaanimales;
    @FXML
    private TableColumn<Animal, String> animalc;
    @FXML
    private TableColumn<Animal, Integer> stockc;
    @FXML
    private Button NAnimalBTN;
    @FXML
    private Button EliminarBTN;
    @FXML
    private Button EditarBTN;
    @FXML
    private Button ComerBTN;
    @FXML
    private Button GestionDietasBTN;
    @FXML
    private Button botonCerrarSesion;
    @FXML
    private ImageView fotoPerfilView;

    private Connection connection;

    public Dardecomer() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/granja", "root", "root");
        } catch (SQLException e) {
            mostrarError("Error al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        animalc.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        stockc.setCellValueFactory(cellData -> cellData.getValue().stockProperty().asObject());

        cargarAnimales();
        UsuarioManager.rutaFotoPerfilProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fotoPerfilView.setImage(new Image(newValue));
            }
        });

        // Inicializar la imagen de perfil al cargar la pantalla
        if (UsuarioManager.getUsuarioActual() != null) {
            String rutaFotoPerfil = UsuarioManager.getUsuarioActual().getRutaFotoPerfil();
            if (rutaFotoPerfil != null) {
                fotoPerfilView.setImage(new Image(rutaFotoPerfil));
            } else {
                fotoPerfilView.setImage(new Image(getClass().getResource("/Imagenes/6662120.png").toString()));
            }
        }
    }

    private void cargarAnimales() {
        tablaanimales.getItems().clear();
        String selectSQL = "SELECT Nombre, Stock FROM animales";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {
            while (resultSet.next()) {
                String nombre = resultSet.getString("Nombre");
                int stock = resultSet.getInt("Stock");
                Animal animal = new Animal(nombre, stock);
                tablaanimales.getItems().add(animal);
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar los animales: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void nuevoAnimal(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nuevo Animal");
        dialog.setHeaderText("Añadir Nuevo Animal");
        dialog.setContentText("Nombre del Animal:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String nombre = result.get();
            String selectSQL = "SELECT COUNT(*) FROM animales WHERE Nombre = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectSQL)) {
                selectStatement.setString(1, nombre);
                ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    mostrarError("El animal que intenta insertar ya está en la base de datos, revise los datos por favor.");
                } else {
                    String insertSQL = "INSERT INTO animales (Nombre, Stock) VALUES (?, 0)";
                    try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
                        statement.setString(1, nombre);
                        statement.executeUpdate();
                        mostrarMensaje("Animal " + nombre + " añadido correctamente.");
                        cargarAnimales();
                    }
                }
            } catch (SQLException e) {
                mostrarError("Error al añadir el animal: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void eliminarAnimal(ActionEvent event) {
        Animal selectedAnimal = tablaanimales.getSelectionModel().getSelectedItem();
        if (selectedAnimal == null) {
            mostrarError("Seleccione un animal para eliminar.");
            return;
        }

        String deleteSQL = "DELETE FROM animales WHERE Nombre = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
            statement.setString(1, selectedAnimal.getNombre());
            statement.executeUpdate();
            mostrarMensaje("Animal " + selectedAnimal.getNombre() + " eliminado correctamente.");
            cargarAnimales();
        } catch (SQLException e) {
            mostrarError("Error al eliminar el animal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void editarAnimal(ActionEvent event) {
        Animal selectedAnimal = tablaanimales.getSelectionModel().getSelectedItem();
        if (selectedAnimal == null) {
            mostrarError("Seleccione un animal para editar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("editarAnimal.fxml"));
            Parent root = loader.load();

            EditarAnimalController controller = loader.getController();
            controller.setAnimal(selectedAnimal, connection);

            Stage stage = new Stage();
            stage.setTitle("Editar Animal");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarAnimales(); // Recargar la lista de animales después de editar
        } catch (IOException e) {
            mostrarError("Error al cargar la ventana de edición: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void darDeComer(ActionEvent event) {
        Animal selectedAnimal = tablaanimales.getSelectionModel().getSelectedItem();
        if (selectedAnimal == null) {
            mostrarError("Seleccione un animal para alimentar.");
            return;
        }

        String nombre = selectedAnimal.getNombre();
        String selectSQL = "SELECT Alimento, Cantidad FROM dietas WHERE Animal = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            selectStatement.setString(1, nombre);
            ResultSet resultSet = selectStatement.executeQuery();

            boolean faltaAlimento = false;
            while (resultSet.next()) {
                String alimento = resultSet.getString("Alimento");
                int cantidad = resultSet.getInt("Cantidad");

                String stockSQL = "SELECT Stock FROM alimentos WHERE Nombre = ?";
                try (PreparedStatement stockStatement = connection.prepareStatement(stockSQL)) {
                    stockStatement.setString(1, alimento);
                    ResultSet stockResult = stockStatement.executeQuery();
                    if (stockResult.next()) {
                        int stock = stockResult.getInt("Stock");
                        if (stock < cantidad) {
                            faltaAlimento = true;
                            mostrarError("Falta alimento para alimentar a este animal: " + alimento);
                            break;
                        }
                    }
                }
            }

            if (!faltaAlimento) {
                resultSet.beforeFirst(); // Reiniciar el cursor del ResultSet
                while (resultSet.next()) {
                    String alimento = resultSet.getString("Alimento");
                    int cantidad = resultSet.getInt("Cantidad");

                    String updateSQL = "UPDATE alimentos SET Stock = Stock - ? WHERE Nombre = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateSQL)) {
                        updateStatement.setInt(1, cantidad);
                        updateStatement.setString(2, alimento);
                        updateStatement.executeUpdate();
                    }
                }
                mostrarMensaje("El animal ha sido alimentado.");
            }
        } catch (SQLException e) {
            mostrarError("Error al alimentar al animal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void gestionarDietas(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GestionDietas.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestionar Dietas");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarError("Error al cargar la ventana de gestión de dietas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void alMatadero(ActionEvent event) {
        mostrarMensaje("Este animal ha pasado a mejor vida, próximamente en vuestros estómagos.");
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
    private void handleCerrarSesion(ActionEvent event) {
        mostrarConfirmacionCerrarSesion();
    }

    private void mostrarConfirmacionCerrarSesion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de cierre de sesión");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro de que desea cerrar sesión?");

        ButtonType buttonTypeSi = new ButtonType("Sí");
        ButtonType buttonTypeNo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeSi) {
                cerrarSesion();
            }
        });
    }

    private void cerrarSesion() {
        System.out.println("Sesión cerrada");
        redirigirALogin();
    }

    private void redirigirALogin() {
        try {
            // Obtener la ventana actual y cerrarla
            Stage stageActual = (Stage) botonCerrarSesion.getScene().getWindow();
            stageActual.close();

            // Cargar la nueva ventana de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleEditarPerfil(ActionEvent event) {
        mostrarVentana("editarPerfil.fxml", "Editar perfil");
    }

    private void mostrarVentana(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar la ventana: " + e.getMessage());
        }
    }


}
