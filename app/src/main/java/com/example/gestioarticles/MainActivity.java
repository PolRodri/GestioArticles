package com.example.gestioarticles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static int ACTIVITY_TASK_ADD = 1;
    private static int ACTIVITY_TASK_UPDATE = 2;

    //private static Context mContext;
    //static Context context = this.getApplicationContext();

    private ArticleDataSource bd;
    private long idActual;
    private int positionActual;
    private adapterArticlesFilter scTasks;

    private String dataSeleccionada;

    private filterKind filterActual;
    
   // public LinearLayout fila;

    private static String[] from = new String[]{
            ArticleDataSource.ARTICLES_CODI,
            ArticleDataSource.ARTICLES_DESCRIPCIO,
            ArticleDataSource.ARTICLES_ESTOC,
            ArticleDataSource.ARTICLES_PREU,
            ArticleDataSource.ARTICLES_FAMILIA};

    private static int[] to = new int[]{
            R.id.lblCodi,
            R.id.lblDescripcio,
            R.id.lblEstoc,
            R.id.lblPreu,
            R.id.lblPreuIva};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Llista d'articles");

        //ADD BUTTON
        Button btn = (Button) findViewById(R.id.btnAdd);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addTask();
            }

        });


        ListView lst = (ListView)findViewById(R.id.listArticles);
        lst.setAdapter(scTasks);

        // Capturem el clic d'un element de la listview
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                updateTask(id);
            }
        });

        bd = new ArticleDataSource(this);
        loadTasks();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filter, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuTot:
                filterTot();
                return true;
            case R.id.mnuDescripcio:
                filterDescripcio();
                return true;
            case R.id.mnuEstoc:
                filterEstoc();
                return true;
            case R.id.btnMovimentPerDia:
                escollirDia();
                return true;
            case R.id.btnLocation:
                Intent i = new Intent(getApplicationContext(), WeatherActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onResume(){
        super.onResume();
        loadTasks();
    }

    //MÈTODES
    private void addTask() {
        // Cridem a l'activity del detall de la tasca enviant com a id -1
        Bundle bundle = new Bundle();
        bundle.putLong("id",-1);

        idActual = -1;

        Intent i = new Intent(this, taskActivity.class );
        i.putExtras(bundle);
        startActivityForResult(i,ACTIVITY_TASK_ADD);

    }

    private void updateTask(long id) {
        // Cridem a l'activity del detall de la tasca enviant com a id -1
        Bundle bundle = new Bundle();
        bundle.putLong("id",id);

        idActual = id;

        Intent i = new Intent(this, taskActivity.class );
        i.putExtras(bundle);
        startActivityForResult(i,ACTIVITY_TASK_UPDATE);
    }

    public void updateEstocMagatzem(long id, String tipus) {
        // Cridem a l'activity del detall de la tasca enviant com a id -1
        Bundle bundle = new Bundle();
        bundle.putLong("id",id);

        //Entrada o sortida
        bundle.putString("tipus", tipus);

        //idActual = id;

        Intent i = new Intent(this, magatzemActivity.class);
        i.putExtras(bundle);
        startActivity(i);
    }


    private void loadTasks() {

        // Demanem totes les tasques
        Cursor cursorTasks = bd.articles();

        // Now create a simple cursor adapter and set it to display
        scTasks = new adapterArticlesFilter(this,
                R.layout.row_articles,
                cursorTasks,
                from,
                to,
                1);

        //canviem la columna del preu iva i li afegim

       // String preuIva = String.valueOf(Double.valueOf(cursorTasks.getString(4)) * 0.21);

        //Adaptem l'adapter heh
        ListView lst = (ListView)findViewById(R.id.listArticles);
        lst.setAdapter(scTasks);


    }

    private void filterTot() {
        // Demanem totes les tasques
        Cursor cursorTasks = bd.articles();
        filterActual = filterKind.FILTER_ALL;

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scTasks.changeCursor(cursorTasks);
        scTasks.notifyDataSetChanged();

        // Ens situem en el primer registre
        //getListView().setSelection(0);
    }

    private void escollirDia() {
        //Cursor cursorMagatzem = bd.moviments();
        //Toast.makeText(this, String.valueOf(cursorMagatzem), Toast.LENGTH_LONG).show();
        Calendar c;
        DatePickerDialog dpd;

        c = Calendar.getInstance();
        int dia = c.get(Calendar.DAY_OF_MONTH);
        int mes = c.get(Calendar.MONTH);
        int any = c.get(Calendar.YEAR);

        dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dataSeleccionada = String.valueOf(dayOfMonth) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year);

                Bundle bundle = new Bundle();
                bundle.putString("data", dataSeleccionada);

                Intent i = new Intent(getApplicationContext(), movimentsDiaActivity.class);
                i.putExtras(bundle);
                startActivity(i);

            }
        }, any, mes, dia);
        dpd.show();
    }

    private void filterDescripcio() {
        // Demanem totes les tasques
        Cursor cursorTasks = bd.articlesDescripcio();
        filterActual = filterKind.FILTER_DESCRIPTION;

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scTasks.changeCursor(cursorTasks);
        scTasks.notifyDataSetChanged();

        // Ens situem en el primer registre
        //getListView().setSelection(0);
    }

    private void filterEstoc(){
        Cursor cursorTasks = bd.articlesEstoc();
        filterActual = filterKind.FILTER_STOCK;

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scTasks.changeCursor(cursorTasks);
        scTasks.notifyDataSetChanged();
    }


    public void deleteTask(final int _id) {
        // Pedimos confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("¿Desitja eliminar la tasca?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                bd.taskDelete(_id);
                loadTasks();
            }
        });

        builder.setNegativeButton("No", null);

        builder.show();
    }



}

class adapterArticlesFilter extends android.widget.SimpleCursorAdapter {
    private static final String colorEstocNegatiu = "#ff757c";
    private static final String colorEstocPositiu = "#ffffff";

    private  MainActivity articleIcon;

    public adapterArticlesFilter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        articleIcon = (MainActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        // Agafem l'objecte de la view que es una LINEA DEL CURSOR
        Cursor linia = (Cursor) getItem(position);
        double estoc = linia.getDouble(linia.getColumnIndexOrThrow(ArticleDataSource.ARTICLES_ESTOC));
        double preuIva = linia.getDouble(linia.getColumnIndexOrThrow(ArticleDataSource.ARTICLES_PREU)) + (linia.getDouble(linia.getColumnIndexOrThrow(ArticleDataSource.ARTICLES_PREU)) * 0.21);

        // Pintem el fons de la view segons està completada o no
        if (estoc<=0.0) {
            view.setBackgroundColor(Color.parseColor(colorEstocNegatiu));
        }else{
            view.setBackgroundColor(Color.parseColor(colorEstocPositiu));

        }

        TextView preuIvaTV = (TextView) view.findViewById(R.id.lblPreuIva);
        preuIvaTV.setText(String.valueOf(preuIva));


        // Capturem botons
        ImageView btnMensage = (ImageView) view.findViewById(R.id.btnDelete);

        btnMensage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Busco la ROW
                View row = (View) v.getParent();
                // Busco el ListView
                ListView lv = (ListView) row.getParent();
                // Busco quina posicio ocupa la Row dins de la ListView
                int position = lv.getPositionForView(row);

                // Carrego la linia del cursor de la posició.
                Cursor linia = (Cursor) getItem(position);

                articleIcon.deleteTask(linia.getInt(linia.getColumnIndexOrThrow(ArticleDataSource.ARTICLES_ID)));
            }
        });

        //BOTO AFEGIR
        ImageView btnPlus = (ImageView) view.findViewById(R.id.btnPlus);
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Busco la ROW
                View row = (View) v.getParent();
                // Busco el ListView
                ListView lv = (ListView) row.getParent().getParent();
                // Busco quina posicio ocupa la Row dins de la ListView
                int position = lv.getPositionForView(row);

                Cursor linia = (Cursor) getItem(position);

                articleIcon.updateEstocMagatzem(linia.getInt(linia.getColumnIndexOrThrow(ArticleDataSource.ARTICLES_ID)), "entrada");
            }
        });

        //BOTO RETIRAR
        ImageView btnMinus = (ImageView) view.findViewById(R.id.btnMinus);
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Busco la ROW
                View row = (View) v.getParent();
                // Busco el ListView
                ListView lv = (ListView) row.getParent().getParent();
                // Busco quina posicio ocupa la Row dins de la ListView
                int position = lv.getPositionForView(row);

                Cursor linia = (Cursor) getItem(position);

                articleIcon.updateEstocMagatzem(linia.getInt(linia.getColumnIndexOrThrow(ArticleDataSource.ARTICLES_ID)), "sortida");
            }
        });


        return view;
    }

}