package com.example.myapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class InformesActivity extends AppCompatActivity {

    private static final String FILE_NAME = "informes.json";
    private ArrayList<Informe> informes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informes);

        // Inicializar lista de informes
        informes = cargarInformes();

        TabHost tabHost = findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Día");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Diario");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Semana");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Semanal");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Mes");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Mensual");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Más vendido");
        spec.setContent(R.id.tab4);
        spec.setIndicator("+ Vendidos");
        tabHost.addTab(spec);

        setupListView(R.id.listViewDailySales, getDailyData());
        setupListView(R.id.listViewWeeklySales, getWeeklyData());
        setupListView(R.id.listViewMonthlySales, getMonthlyData());
        setupListView(R.id.listViewTopProducts, getTopProductsData());
    }

    private void setupListView(int listViewId, ArrayList<String> data) {
        ListView listView = findViewById(listViewId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
    }

    private ArrayList<String> getDailyData() {
        ArrayList<String> data = new ArrayList<>();
        for (Informe informe : informes) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            data.add("ID: " + informe.getId() + " - Fecha: " + sdf.format(informe.getFecha()) + " - Total: $" + informe.getTotalVentas());
        }
        return data;
    }

    private ArrayList<String> getWeeklyData() {
        HashMap<String, Double> weeklyData = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Informe informe : informes) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(informe.getFecha());
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            String weekStart = sdf.format(cal.getTime());

            weeklyData.put(weekStart, weeklyData.getOrDefault(weekStart, 0.0) + informe.getTotalVentas());
        }

        ArrayList<String> data = new ArrayList<>();
        for (String week : weeklyData.keySet()) {
            data.add("Semana comenzando: " + week + " - Total: $" + weeklyData.get(week));
        }
        return data;
    }

    private ArrayList<String> getMonthlyData() {
        HashMap<String, Double> monthlyData = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");

        for (Informe informe : informes) {
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");
            String month = monthFormat.format(informe.getFecha());
            monthlyData.put(month, monthlyData.getOrDefault(month, 0.0) + informe.getTotalVentas());
        }

        ArrayList<String> data = new ArrayList<>();
        for (String month : monthlyData.keySet()) {
            data.add("Mes: " + month + " - Total: $" + monthlyData.get(month));
        }
        return data;
    }

    private ArrayList<String> getTopProductsData() {
        HashMap<String, Integer> topProducts = new HashMap<>();

        for (Informe informe : informes) {
            for (Item item : informe.getItemsVendidos()) {
                topProducts.put(item.getNombre(), topProducts.getOrDefault(item.getNombre(), 0) + item.getCantidad());
            }
        }

        ArrayList<String> data = new ArrayList<>();
        for (String product : topProducts.keySet()) {
            data.add("Producto: " + product + " - Cantidad vendida: " + topProducts.get(product));
        }
        return data;
    }

    private ArrayList<Informe> cargarInformes() {
        Gson gson = new Gson();
        ArrayList<Informe> informes = new ArrayList<>();
        try (FileReader reader = new FileReader(getFilesDir() + "/" + FILE_NAME)) {
            Type listType = new TypeToken<ArrayList<Informe>>() {}.getType();
            informes = gson.fromJson(reader, listType);
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
        }
        return informes;
    }

    private void guardarInformes() {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(getFilesDir() + "/" + FILE_NAME)) {
            gson.toJson(informes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        guardarInformes(); // Guardar datos al salir de la actividad
    }
}
