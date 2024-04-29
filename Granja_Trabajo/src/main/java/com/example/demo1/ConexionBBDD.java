package com.example.demo1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;

public class ConexionBBDD {

    Connection connection;
    Statement statement;

    public ConexionBBDD() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "root");
        statement = connection.createStatement();
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
            /*ResultSet resultSet = statement.executeQuery("select * from alimentos");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("Nombre"));
                System.out.println(resultSet.getString("Stock"));
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String selectEnTabla(String tabla, String columna) throws SQLException {
        ResultSet resultSet = statement.executeQuery("select " + columna + " from " + tabla);
        return resultSet.getString(columna);


    }
}
