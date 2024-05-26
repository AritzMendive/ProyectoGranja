package com.example.demo1.granja;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class AlimentarAnimal {
    public static void main(String[] args) {
        // Establece la conexión a la base de datos
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/granja", "tu_usuario", "tu_contraseña")) {

            // Crea un Statement con ResultSet.TYPE_SCROLL_INSENSITIVE y ResultSet.CONCUR_READ_ONLY
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // Ejecuta la consulta y obtiene el ResultSet
            ResultSet rs = stmt.executeQuery("SELECT * FROM dietas WHERE Animal = 'Cabra'");

            // Procesa los resultados
            while (rs.next()) {
                // Lógica para alimentar a la cabra
                String alimento = rs.getString("Alimento");
                int cantidad = rs.getInt("Cantidad");
                System.out.println("Alimentando a la cabra con " + cantidad + " unidades de " + alimento);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
