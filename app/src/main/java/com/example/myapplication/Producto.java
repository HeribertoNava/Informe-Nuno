package com.example.myapplication;

import androidx.annotation.NonNull;

public class Producto {
    /*
    Atributos de la clase Producto
    */
    private String nombre;
    private int cantidad;
    private float precio;

    public Producto(String nombre, int cantidad, float precio) {
        /*
        Constructor de la clase Producto
        */
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    @NonNull
    @Override
    public String toString() {
        /*
        Devuelve el nombre del producto
        */
        return nombre;
    }

    public void setNombre(String nombre) {
        /*
        Actualiza el nombre del producto
        */
        this.nombre = nombre;
    }

    public int getCantidad() {
        /*
        Devuelve la descripción del producto
        */
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        /*
        Asinga (actualiza) la cantidad del producto
        */
        this.cantidad = cantidad;
    }

    public float getPrecio() {
        /*
        Devueleve el precio del producto
        */
        return precio;
    }

    public void setPrecio(float precio) {
        /*
        Asigna (actualiza) el precio del producto
        */
        this.precio = precio;
    }

}