package com.example.demo1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;

public class ConexionBBDD {

    private Connection connection;
    Statement statement;

    public ConexionBBDD() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://monorail.proxy.rlwy.net:55810/railway", "root", "MNvVtHFDEuiIcdlCusLWfBxfFqPvemBP");
        statement = connection.createStatement();
    }

    public Connection getConnection() {
        return connection;
    }

    public void creacionBBDD() throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader("src/main/BBDD/granja.sql"));
        try {
            String st;
            ArrayList<String> lineas = new ArrayList<>();
            while ((st = br.readLine()) != null) {
                lineas.add(st);
            }

            for (String linea : lineas) {
                statement.executeUpdate(linea);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void migracionClienteProveedor() throws SQLException {
        String sql = "CREATE PROCEDURE if not exists ActualizarRoles()\n" +
                "BEGIN\n" +
                "    UPDATE Usuarios\n" +
                "    SET Rol = 'Proveedor'\n" +
                "    WHERE Rol = 'Cliente';\n" +
                "END";
        statement.execute(sql);
    }

    public void close() {
        try {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
