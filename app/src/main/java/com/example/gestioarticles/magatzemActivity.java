package com.example.gestioarticles;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class magatzemActivity extends AppCompatActivity {

    private long idTask;
    private ArticleDataSource bd;
    private String tipus;

    Calendar c;
    DatePickerDialog dpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magatzem);

        bd = new ArticleDataSource(this);

        // Busquem el id que estem modificant
        idTask = this.getIntent().getExtras().getLong("id");

        //Busquem el tipus (entrada o sortida)
        tipus = this.getIntent().getExtras().getString("tipus");

        TextView tvQ = (TextView) findViewById(R.id.quantitatView);
        if(tipus.equalsIgnoreCase("entrada")){
            setTitle("Entrada d'estoc al magatzem");
            tvQ.setText("QUANTITAT A AFEGIR");

        } else {
            setTitle("Sortida d'estoc del magatzem");
            tvQ.setText("QUANTITAT A RETIRAR");
        }

        //BOTO ACCEPTAR
        Button btnOk = (Button) findViewById(R.id.btnAfegirEstoc);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                aceptarCambios(idTask);
            }
        });

        Button  btnCancel = (Button) findViewById(R.id.btnCancelarEstoc);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelarCambios();
            }
        });

        //DATEPICKER
        final TextView tvData;

        tvData = (TextView) findViewById(R.id.edtDataMagatzem);

        c = Calendar.getInstance();

        Format formatter = new SimpleDateFormat("dd/MM/yyyy");
        String s = formatter.format(c.getTime());

        tvData.setText(s);

        tvData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = Calendar.getInstance();
                int dia = c.get(Calendar.DAY_OF_MONTH);
                int mes = c.get(Calendar.MONTH);
                int any = c.get(Calendar.YEAR);

                dpd = new DatePickerDialog(magatzemActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tvData.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, any, mes, dia);
                dpd.show();
            }
        });


        cagarDatos();
    }

    private void cagarDatos() {
        // Demanem un cursor que retorna un sol registre amb les dades de la tasca
        // Això es podria fer amb un classe pero...
        Cursor datos = bd.task(idTask);
        datos.moveToFirst();

        //Toast.makeText(this, datos.getString(datos.getColumnIndex(ArticleDataSource.ARTICLES_CODI)), Toast.LENGTH_LONG).show();

        // Carreguem les dades en la interfície
        TextView tv;

        tv = (TextView) findViewById(R.id.edtCodiMagatzem);
        tv.setText(datos.getString(datos.getColumnIndex(ArticleDataSource.ARTICLES_CODI)));
        tv.setEnabled(false);

        tv = (TextView) findViewById(R.id.edtEstocMagatzem);
        tv.setText(datos.getString(datos.getColumnIndex(ArticleDataSource.ARTICLES_ESTOC)));
        tv.setEnabled(false);

    }

    private void aceptarCambios(long editant) {
        TextView tv;

        tv = (TextView) findViewById(R.id.edtCodiMagatzem);
        String codi = tv.getText().toString();

        tv = (TextView) findViewById(R.id.edtQuantitatMagatzem);
        String quantitat = tv.getText().toString();

        tv = (TextView) findViewById(R.id.edtDataMagatzem);
        String data = tv.getText().toString();

        tv = (TextView) findViewById(R.id.edtEstocMagatzem);
        String estocVell = tv.getText().toString();

        if (quantitat.trim().equals("")) {
            Toast.makeText(this,"Camp quantitat obligatori", Toast.LENGTH_LONG).show();
            return;
        }

        int quantitatNum;
        try {
            quantitatNum = Integer.valueOf(quantitat);

            if (quantitatNum < 0){
                Toast.makeText(this,"La quantitat no pot ser negativa", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(this,"Camp quantitat ha de ser un valor numèric.", Toast.LENGTH_LONG).show();
            return;
        }

        String tipusChar;
        String nouEstoc;
        if(tipus.equalsIgnoreCase("entrada")){
            tipusChar = "E";
            nouEstoc = String.valueOf(Integer.valueOf(estocVell) + Integer.valueOf(quantitat));
        }else{
            tipusChar = "S";
            nouEstoc = String.valueOf(Integer.valueOf(estocVell) - Integer.valueOf(quantitat));
        }


        bd.taskEstocUpdate(idTask, nouEstoc);
        idTask = bd.magatzemAdd(codi, quantitat, data, tipusChar);

        Intent mIntent = new Intent();
        mIntent.putExtra("id", idTask);
        setResult(RESULT_OK, mIntent);

        finish();
        //Toast.makeText(this, codi + ", " + quantitat + ", " + data, Toast.LENGTH_LONG).show();
    }

    private void cancelarCambios() {
        Intent mIntent = new Intent();
        mIntent.putExtra("id", idTask);
        setResult(RESULT_CANCELED, mIntent);
        finish();
    }
}