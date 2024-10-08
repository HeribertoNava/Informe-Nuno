package com.example.myapplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Informe {
    private static int contador = 0; // Contador para asignar IDs
    private int id; // ID de la venta
    private Date fecha; // Fecha de la venta
    private double totalVentas; // Total de ventas
    private int totalItemsVendidos; // Total de items vendidos
    private List<Item> itemsVendidos; // Lista de items vendidos

    public Informe(String fechaStr, double totalVentas, int totalItemsVendidos, List<Item> itemsVendidos) {
        this.id = ++contador; // Asignar ID secuencialmente
        this.fecha = convertirStringADate(fechaStr);
        this.totalVentas = totalVentas;
        this.totalItemsVendidos = totalItemsVendidos;
        this.itemsVendidos = itemsVendidos;
    }

    private Date convertirStringADate(String fechaStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // Hacer el formato más estricto
        try {
            return sdf.parse(fechaStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(); // Retornar la fecha actual si hay un error
        }
    }

    public int getId() {
        return id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = convertirStringADate(fecha);
    }

    public double getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(double totalVentas) {
        this.totalVentas = totalVentas;
    }

    public int getTotalItemsVendidos() {
        return totalItemsVendidos;
    }

    public void setTotalItemsVendidos(int totalItemsVendidos) {
        this.totalItemsVendidos = totalItemsVendidos;
    }

    public List<Item> getItemsVendidos() {
        return itemsVendidos;
    }

    public void setItemsVendidos(List<Item> itemsVendidos) {
        this.itemsVendidos = itemsVendidos;
    }
}
