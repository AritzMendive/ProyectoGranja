package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AlimentosTablas implements Initializable  {

    public Button ListaBtn;
    public TableColumn<Alimento, String> TablaNombre;
    public TableColumn<Alimento, Integer> TablaStock;
    @FXML
    private Button botonCerrarSesion;
    public TableColumn<Alimento, Integer> TablaPrecio;
    public TableView<Alimento> TablaPorColumna;
    private ObservableList<Alimento> listaAlimentos = FXCollections.observableArrayList();
    private HelloApplication a;
    private AlimentosSuministrosController controllerError;

    public AlimentosTablas() {
        a = new HelloApplication();
    }

    @FXML
    private TextField fieldNombre;

    @FXML
    private TextField fieldStock;

    @FXML
    private TextField fieldPrecio;

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controllerError = new AlimentosSuministrosController();
        TablaPorColumna.setItems(listaAlimentos);
        TablaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        TablaStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        TablaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        TablaPorColumna.setRowFactory(tv -> new TableRow<Alimento>() {
            @Override
            protected void updateItem(Alimento item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    setStyle(getRowStyle(item.getStock()));
                }
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
                    }
                }
            }
        });

        refrescarDatos(); // Para cargar los datos inicialmente
    }

    private String getRowStyle(int stock) {
        if (stock == 0) {
            return "-fx-control-inner-background: red;";
        } else if (stock <= 5) {
            return "-fx-control-inner-background: yellow;";
        } else {
            return ""; // Sin estilo para stocks normales
        }
    }

    @FXML
    public void refrescarDatos() {
        listaAlimentos.clear(); // Limpiar la lista antes de actualizar

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://monorail.proxy.rlwy.net:55810/railway", "root", "MNvVtHFDEuiIcdlCusLWfBxfFqPvemBP")) {
            String selectSQL = "SELECT * FROM alimentos";
            try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String nombre = resultSet.getString("nombre");
                    int stock = resultSet.getInt("stock");
                    int precio = resultSet.getInt("precio");
                    int id = resultSet.getInt("IdAlimento");

                    Alimento nuevoAlimento = new Alimento(nombre, stock, precio, id);
                    listaAlimentos.add(nuevoAlimento); // Añadir cada alimento a la lista observable
                }
            }
        } catch (SQLException e) {
            controllerError.mostrarError("Error al cargar los alimentos.");
            e.printStackTrace();
        }
    }

    @FXML
    public void mostrarInfoAlimento(javafx.scene.input.MouseEvent mouseEvent) {
        Alimento alimentoSeleccionado = TablaPorColumna.getSelectionModel().getSelectedItem();
        if (alimentoSeleccionado != null) {
            fieldNombre.setText(alimentoSeleccionado.getNombre());
            fieldStock.setText(String.valueOf(alimentoSeleccionado.getStock()));
            fieldPrecio.setText(String.valueOf(alimentoSeleccionado.getPrecio()));
        }
    }

    @FXML
    public void eliminarEmpleado(ActionEvent actionEvent) {
        Alimento empleadoSeleccionado = TablaPorColumna.getSelectionModel().getSelectedItem();
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

    @FXML
    public void cambioVista(ActionEvent event) {
        cerrarVentana(event);
        a.mostrarVentanaCinco();
    }

    public static void cerrarVentana(ActionEvent e) {
        Node source = (Node) e.getSource(); // Me devuelve el elemento al que hice click
        Stage stage = (Stage) source.getScene().getWindow(); // Me devuelve la ventana donde se encuentra el elemento
        stage.close();
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
    private ImageView fotoPerfilView;

    @FXML
    private void initialize() {
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
            }
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
    private void mostrarError(String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
