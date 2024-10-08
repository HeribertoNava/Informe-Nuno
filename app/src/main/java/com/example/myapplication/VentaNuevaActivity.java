package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.content.Context;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class VentaNuevaActivity extends AppCompatActivity {
    private ArrayList<Producto> productList;
    private ArrayAdapter<Producto> adapter;

    final Context context = this;

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;

    TableLayout tableProducts;
    Spinner spProducts;
    private TextView tvTotalGeneral;
    private EditText editRecibido;
    private EditText editCambio;
    private Button btnFinalizar;
    private Button btnCancelar;

    private float totalGeneral = 0;
    private List<Item> itemsVendidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ventas);

        // Obtener elementos a través de su ID
        tableProducts = findViewById(R.id.tabla_productos);
        spProducts = findViewById(R.id.spProducts);
        tvTotalGeneral = findViewById(R.id.tvTotalGeneral); // Asegúrate de que el id coincida
        editRecibido = findViewById(R.id.recibido);
        editCambio = findViewById(R.id.cambio);
        btnFinalizar = findViewById(R.id.btnFinalizar);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Iniciar elementos
        productList = new ArrayList<>();
        itemsVendidos = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productList);
        spProducts.setAdapter(adapter);

        // Al seleccionar un producto (item) del Spinner
        spProducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Obtener datos del producto seleccionado
                String product = productList.get(i).toString();
                int quantity = productList.get(i).getCantidad();
                float price = productList.get(i).getPrecio();

                // Agregar producto seleccionado a la tabla
                agregarProducto(product, quantity, price);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No hace nada
            }
        });

        // Configuración de los botones
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizarVenta();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarVenta();
            }
        });
    }

    @Override
    protected void onResume() {
        // Mostrar los productos guardados al reanudar/abrir la app en el Spinner
        super.onResume();

        productList = new ArrayList<>();

        // Crear nuevo sharedPreferences
        sharedPrefs = context.getSharedPreferences("Store", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();

        // Convertir a String los valores de SharedPreferences
        String connectionsJSONString = sharedPrefs.getString("Productos", null);

        // Si el resultado del SharedPreferences no es nulo y tiene datos
        if (connectionsJSONString != null) {
            Type type = new TypeToken<ArrayList<Producto>>() {}.getType();
            productList = new Gson().fromJson(connectionsJSONString, type);
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);

            spProducts.setAdapter(adapter);
        }
    }

    private void agregarProducto(String producto, int cantidad, float precio) {
        // Crear una nueva fila
        TableRow datos = new TableRow(this);

        // Nombre del producto - Usar layout_txt_view
        TextView tvProducto = (TextView) LayoutInflater.from(this).inflate(R.layout.layout_txt_view, null);
        tvProducto.setText(producto);
        datos.addView(tvProducto);

        // Botón "+" (Aumentar cantidad)
        Button btnAdd = new Button(this);
        btnAdd.setText("+");
        datos.addView(btnAdd);

        // Cantidad del producto - Usar layout_txt_view_2
        TextView tvCantidad = new TextView(this);
        tvCantidad.setText(String.valueOf(0));
        if (cantidad > 0) // Si hay stock (más de 1) colocar 1
            tvCantidad.setText(String.valueOf(1));
        datos.addView(tvCantidad);

        // Botón "-" (Disminuir cantidad)
        Button btnMinus = new Button(this);
        btnMinus.setText("-");
        datos.addView(btnMinus);

        // Precio del producto
        TextView tvPrecio = (TextView) LayoutInflater.from(this).inflate(R.layout.layout_txt_view, null);
        tvPrecio.setText(String.format("$%.2f", precio));
        datos.addView(tvPrecio);

        // Total de los productos
        TextView tvTotal = (TextView) LayoutInflater.from(this).inflate(R.layout.layout_txt_view, null);
        tvTotal.setText(String.format("$%.2f", precio)); // Inicialmente total es igual al precio
        datos.addView(tvTotal);

        // Añadir la fila al TableLayout
        tableProducts.addView(datos);

        // Actualizar el total general
        totalGeneral += precio;
        actualizarTotalGeneral();

        // Crear objeto Item y agregarlo a la lista
        Item item = new Item(producto, 1, precio);
        itemsVendidos.add(item);

        // Manejo del botón +
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nuevaCantidad = Integer.parseInt(tvCantidad.getText().toString()) + 1;
                if (nuevaCantidad > cantidad) return; // No exceder el stock

                tvCantidad.setText(String.valueOf(nuevaCantidad));
                float nuevoTotal = nuevaCantidad * precio;
                tvTotal.setText(String.format("$%.2f", nuevoTotal));
                actualizarTotalGeneral();
            }
        });

        // Manejo del botón -
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nuevaCantidad = Integer.parseInt(tvCantidad.getText().toString()) - 1;
                if (nuevaCantidad < 1) return; // No permitir menos de 1

                tvCantidad.setText(String.valueOf(nuevaCantidad));
                float nuevoTotal = nuevaCantidad * precio;
                tvTotal.setText(String.format("$%.2f", nuevoTotal));
                actualizarTotalGeneral();
            }
        });
    }

    private void actualizarTotalGeneral() {
        tvTotalGeneral.setText(String.format("Total General: $%.2f", totalGeneral));
    }

    private void finalizarVenta() {
        String recibidoText = editRecibido.getText().toString();
        if (recibidoText.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa la cantidad recibida.", Toast.LENGTH_SHORT).show();
            return;
        }

        float recibido = Float.parseFloat(recibidoText);
        if (recibido < totalGeneral) {
            Toast.makeText(this, "La cantidad recibida es menor que el total.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calcular cambio
        float cambio = recibido - totalGeneral;
        editCambio.setText(String.format("%.2f", cambio));

        // Crear objeto Informe y guardarlo
        Informe informe = new Informe(String.valueOf(System.currentTimeMillis()), totalGeneral, itemsVendidos.size(), itemsVendidos);
        guardarInforme(informe);

        Toast.makeText(this, "Venta finalizada con éxito.", Toast.LENGTH_SHORT).show();

        // Limpiar los campos
        limpiarVenta();

        // Iniciar la actividad de informe
        Intent intent = new Intent(this, InformesActivity.class);
        startActivity(intent);
    }

    private void guardarInforme(Informe informe) {
        Gson gson = new Gson();
        ArrayList<Informe> informes = cargarInformes(); // Cargar informes existentes

        // Agregar el nuevo informe a la lista
        informes.add(informe);

        // Guardar la lista actualizada de informes
        try (FileWriter writer = new FileWriter(getFilesDir() + "/informes.json")) {
            gson.toJson(informes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Informe> cargarInformes() {
        Gson gson = new Gson();
        ArrayList<Informe> informes = new ArrayList<>();
        try (FileReader reader = new FileReader(getFilesDir() + "/informes.json")) {
            Type listType = new TypeToken<ArrayList<Informe>>() {}.getType();
            informes = gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return informes;
    }

    private void limpiarVenta() {
        // Limpiar campos y tabla
        editRecibido.setText("");
        editCambio.setText("");
        tableProducts.removeAllViews();
        totalGeneral = 0;
        actualizarTotalGeneral();
        itemsVendidos.clear(); // Limpiar la lista de items vendidos
    }

    private void cancelarVenta() {
        // Regresar a la actividad anterior o limpiar campos
        limpiarVenta();
        finish(); // Cerrar la actividad actual
    }
}
