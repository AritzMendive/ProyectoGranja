package com.example.demo1;

public class Usuario {
    private int id;
    private String nombre;
    private String contrasena;
    private String rol;
    private String rutaFotoPerfil;
    private String descripcion; // Añadir esta línea

    public Usuario(int id, String nombre, String contrasena, String rol, String rutaFotoPerfil, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.rol = rol;
        this.rutaFotoPerfil = rutaFotoPerfil;
        this.descripcion = descripcion; // Añadir esta línea
    }

    // Getters y Setters para todos los atributos, incluyendo la descripción
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public String getRutaFotoPerfil() {
        return rutaFotoPerfil;
    }

    public void setRutaFotoPerfil(String rutaFotoPerfil) {
        this.rutaFotoPerfil = rutaFotoPerfil;
    }

    public String getDescripcion() {
        return descripcion; // Añadir esta línea
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion; // Añadir esta línea
    }
}
