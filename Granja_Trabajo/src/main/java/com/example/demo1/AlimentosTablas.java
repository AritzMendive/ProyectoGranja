package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AlimentosTablas implements Initializable, CerrarVentana {

    public Button ListaBtn;
    public TableColumn<Alimento,String> TablaNombre;
    public TableColumn<Alimento,Integer> TablaStock;
    @FXML
    private Button botonCerrarSesion;
    public TableColumn<Alimento,Integer> TablaPrecio;
    public TableView<Alimento> TablaPorColumna;
    @FXML
    ListView<Alimento> alimentosView;
    ObservableList<Alimento> listaAlimentos = FXCollections.observableArrayList();
    ArrayList<Alimento> listaA = new ArrayList<>();

    @FXML
    private TextField fieldNombre;

    @FXML
    private TextField fieldStock;

    @FXML
    private TextField fieldPrecio;
    private HelloApplication a;
    AlimentosSuministrosController controllerError;
    public AlimentosTablas()
    {
        a = new HelloApplication();
    }
    ObservableList<Alimento> list = FXCollections.observableArrayList(
            refrescarDatos()
    );
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controllerError = new AlimentosSuministrosController();
        TablaPorColumna.setItems(listaAlimentos);
        TablaNombre.setCellValueFactory(new PropertyValueFactory<Alimento,String>("nombre"));
        TablaStock.setCellValueFactory(new PropertyValueFactory<Alimento,Integer>("stock"));
        TablaPrecio.setCellValueFactory(new PropertyValueFactory<Alimento,Integer>("precio"));
        alimentosView.setCellFactory(lv -> new ListCell<Alimento>() {
            @Override
            protected void updateItem(Alimento item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    setStyle(getRowStyle(item.getStock()));
                }
            }
        });
        TablaPorColumna.setItems(list);
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



    public ObservableList<Alimento> refrescarDatos() {
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

                    Alimento nuevoAlimento = new Alimento(nombre, stock, precio, id);
                    listaAlimentos.add(nuevoAlimento);
                }
                return listaAlimentos;
            }
        } catch (SQLException e) {
            controllerError.mostrarError("Error al cargar los alimentos.");
            e.printStackTrace();
        }
        return null;
    }

    public void mostrarInfoAlimento(javafx.scene.input.MouseEvent mouseEvent) {
        String nombre = TablaPorColumna.getSelectionModel().getSelectedItem().getNombre();
        fieldNombre.setText(nombre);
        fieldStock.setText(String.valueOf(TablaPorColumna.getSelectionModel().getSelectedItem().getStock()));
        fieldPrecio.setText(String.valueOf(TablaPorColumna.getSelectionModel().getSelectedItem().getPrecio()));

    }

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
    public void cambioVista(ActionEvent event)
    {
        CerrarVentana.cerrarVentana(event);
        a.mostrarVentanaTres();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
