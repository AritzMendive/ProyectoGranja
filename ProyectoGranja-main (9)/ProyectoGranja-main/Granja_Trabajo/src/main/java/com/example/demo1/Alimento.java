package com.example.demo1;

public class Alimento {
    private String nombre;
    private int stock;
    private int precio;

    private int id;

    public Alimento(String nombre, int stock, int precio, int id) {
        this.nombre = nombre;
        this.stock = stock;
        this.precio = precio;
        this.id = id;
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return nombre + " - Stock: " + stock + " - Precio: " + precio;
    }

    public int getId() {
        return this.id;
    }
}
