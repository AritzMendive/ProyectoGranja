package com.example.demo1;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UsuarioManager {

    private static Usuario usuarioActual;
    private static StringProperty rutaFotoPerfil = new SimpleStringProperty();

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
        if (usuario != null) {
            rutaFotoPerfil.set(usuario.getRutaFotoPerfil());
        }
    }

    public static StringProperty rutaFotoPerfilProperty() {
        return rutaFotoPerfil;
    }

    public static void notificarCambioFotoPerfil() {
        if (usuarioActual != null) {
            rutaFotoPerfil.set(usuarioActual.getRutaFotoPerfil());
        }
    }
}
