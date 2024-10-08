package com.example.myapplication;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

public class ListaProductosActivity extends AppCompatActivity {
    private ArrayList<Producto> productList;
    private ArrayAdapter<Producto> adapter;
    private ListView listViewProducts;

    final Context context = this;

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;

    Button btnAgregarProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_productos);

        //Encontrar lista de productos e iniciar nuevo ArrayList
        listViewProducts = findViewById(R.id.listViewProducts);
        productList = new ArrayList<>();

        //Colocar adaptador
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        listViewProducts.setAdapter(adapter);

        listViewProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /*
            Función para detectar click sobre un producto (item) de la lista
            */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDialog(position);
            }
        });

        //Botón Nuevo Producto
        btnAgregarProducto = (Button) findViewById(R.id.btnAgregarProducto);

        btnAgregarProducto.setOnClickListener(new View.OnClickListener() {
            /*
            Función del botón Nuevo Producto
            */
            @Override
            public void onClick(View view) {
                addNewProduct();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        /*
        Mostrar los datos de los productos guardados al reanudar/abrir la app
        */
        super.onResume();

        productList = new ArrayList<>();

        //Crear nuevo sharedPreferences
        sharedPrefs = context.getSharedPreferences("Store", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();

        //Convertir a String los valores de SharedPreferences
        String connectionsJSONString = sharedPrefs.getString("Productos", null);

        //Si el resultado del SharedPreferences no es nulo y tiene datos
        if (connectionsJSONString != null) {
            Type type = new TypeToken<ArrayList<Producto>>() {}.getType();
            productList = new Gson().fromJson(connectionsJSONString, type);
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);

            listViewProducts.setAdapter(adapter);
        }

    }

    @Override
    protected void onPause() {
        /*
        Guardar los datos de los productos al pausar/cerrar la app
        */
        super.onPause();

        //Convertor lista de productos (ArrayList) a JSON (String)
        String connectionsJSONString = new Gson().toJson(productList);
        editor.putString("Productos", connectionsJSONString);
        editor.apply();
        adapter.notifyDataSetChanged();

    }

    private void addNewProduct() {
        /*
        Función para mostrar el form (dialog) para crear un producto
        */
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_nuevo_producto);

        //Hacer que no se pueda cerrar haciendo click afuera del dialog
        dialog.setCancelable(false);

        //EditText para los valores
        EditText editTextName = dialog.findViewById(R.id.editTextName);
        EditText editTextPrice = dialog.findViewById(R.id.editTextPrice);
        EditText editTextQuantity = dialog.findViewById(R.id.editTextQuantity);

        //Botones
        Button buttonSave = dialog.findViewById(R.id.buttonSave);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            /*
            Función para guardar el nuevo producto y actualizar el adpatador
            */
            @Override
            public void onClick(View v) {
                // Obtener nuevos valores
                //!!!!!!!!!!!!!!!!!!!!!!!!!
                // VALIDAR QUE ESTÉN LLENOS Y SEA CORRECTOS
                //!!!!!!!!!!!!!!!!!!!!!!!!!
                String newName = editTextName.getText().toString();
                int newQuantity = Integer.parseInt(editTextQuantity.getText().toString());
                float newPrice = Float.parseFloat(editTextPrice.getText().toString());

                //Agregar nuevo producto a la lista de productos
                productList.add(new Producto(newName, newQuantity, newPrice));

                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            /*
            Cancela el dialogo (cerrar)
            */
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void showEditDialog(final int position) {
        /*
        Función para mostrar los datos del producto e interactuar
         */
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog);

        //Hacer que no se pueda cerrar haciendo click afuera del dialog
        dialog.setCancelable(false);

        //EditText para los valores
        EditText editTextName = dialog.findViewById(R.id.editTextName);
        EditText editTextPrice = dialog.findViewById(R.id.editTextPrice);
        EditText editTextQuantity = dialog.findViewById(R.id.editTextQuantity);

        //Botones
        Button buttonSave = dialog.findViewById(R.id.buttonSave);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        Button buttonDelete = dialog.findViewById(R.id.buttonDelete);

        //Colocar datos del producto en el form
        editTextName.setText(productList.get(position).toString());
        editTextPrice.setText(String.format(Locale.US, "%,.1f", productList.get(position).getPrecio()) );
        editTextQuantity.setText( String.valueOf(productList.get(position).getCantidad()) );

        buttonSave.setOnClickListener(new View.OnClickListener() {
            /*
            Función para actualizar los datos de un producto
            */
            @Override
            public void onClick(View v) {
                // Obtener nuevos valores
                //!!!!!!!!!!!!!!!!!!!!!!!!!
                // VALIDAR QUE ESTÉN LLENOS Y SEA CORRECTOS
                //!!!!!!!!!!!!!!!!!!!!!!!!!
                String newName = editTextName.getText().toString();
                int newQuantity = Integer.parseInt(editTextQuantity.getText().toString());
                float newPrice = Float.parseFloat(editTextPrice.getText().toString());

                productList.get(position).setNombre(newName);
                productList.get(position).setCantidad(newQuantity);
                productList.get(position).setPrecio(newPrice);

                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            /*
            Función para eliminar un producto
            */
            @Override
            public void onClick(View v) {
                productList.remove(position);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            /*
            Cancela el dialogo (cerrar)
            */
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}