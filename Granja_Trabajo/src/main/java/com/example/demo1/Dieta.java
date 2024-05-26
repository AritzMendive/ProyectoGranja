package com.example.demo1;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;

public class Dieta {
    private final StringProperty animal;
    private final StringProperty alimento;
    private final IntegerProperty cantidad;

    public Dieta(String animal, String alimento, int cantidad) {
        this.animal = new SimpleStringProperty(animal);
        this.alimento = new SimpleStringProperty(alimento);
        this.cantidad = new SimpleIntegerProperty(cantidad);
    }

    public String getAnimal() {
        return animal.get();
    }

    public StringProperty animalProperty() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal.set(animal);
    }

    public String getAlimento() {
        return alimento.get();
    }

    public StringProperty alimentoProperty() {
        return alimento;
    }

    public void setAlimento(String alimento) {
        this.alimento.set(alimento);
    }

    public int getCantidad() {
        return cantidad.get();
    }

    public IntegerProperty cantidadProperty() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad.set(cantidad);
    }
}
