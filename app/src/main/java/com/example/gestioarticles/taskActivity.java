package com.example.gestioarticles;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class taskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private long idTask;
    private ArticleDataSource bd;
    private String itemSeleccionat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        bd = new ArticleDataSource(this);

        // Busquem el id que estem modificant
        // si el el id es -1 vol dir que s'està creant
        idTask = this.getIntent().getExtras().getLong("id");

        Spinner mySpinner = (Spinner) findViewById(R.id.spinnerFamilia);
        ArrayAdapter<String> familiesAdapter = new ArrayAdapter<String>(taskActivity.this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.families));
        familiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(familiesAdapter);
        mySpinner.setOnItemSelectedListener(this);

        //BOTO ACCEPTAR
        Button btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                aceptarCambios(idTask);
            }
        });

        // BOTO ELIMINAR
        Button  btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });

        // BOTO CANCELAR
        Button  btnCancel = (Button) findViewById(R.id.btnCancelar);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelarCambios();
            }
        });



        if (idTask != -1) {
            //DROPDOWN EDIT
            Cursor datos = bd.task(idTask);
            datos.moveToFirst();
            String familiaItemName = datos.getString(datos.getColumnIndex(ArticleDataSource.ARTICLES_FAMILIA));

            if (familiaItemName.equalsIgnoreCase("SOFTWARE")){
                mySpinner.setSelection(0);
            } else if(familiaItemName.equalsIgnoreCase("HARDWARE")){
                mySpinner.setSelection(1);
            } else{
                mySpinner.setSelection(2);
            }

            TextView tv;
            tv = (TextView) findViewById(R.id.edtFamilia);
            tv.setVisibility(View.GONE);

            // Si estem modificant carreguem les dades en pantalla
            cargarDatos();
        }
        else {
            // Si estem creant amaguem el el botó d'eliminar
            btnDelete.setVisibility(View.GONE);

            TextView tv;
            tv = (TextView) findViewById(R.id.edtEstoc);
            tv.setVisibility(View.GONE);
            tv = (TextView) findViewById(R.id.estocView);
            tv.setVisibility(View.GONE);
            tv = (TextView) findViewById(R.id.edtFamilia);
            tv.setVisibility(View.GONE);
        }
    }

    //MÈTODES
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        String sSelected = parent.getItemAtPosition(pos).toString();

        if (parent.getItemAtPosition(pos).equals("SOFTWARE")){
            this.itemSeleccionat = "SOFTWARE";
        }else if(parent.getItemAtPosition(pos).equals("HARDWARE")){
            this.itemSeleccionat = "HARDWARE";
        }else{
            this.itemSeleccionat = "ALTRES";
            //Toast.makeText(this,"ALTRES", Toast.LENGTH_LONG).show();
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback

    }

    private void cargarDatos() {
        // Demanem un cursor que retorna un sol registre amb les dades de la tasca
        // Això es podria fer amb un classe pero...
        Cursor datos = bd.task(idTask);
        datos.moveToFirst();

        // Carreguem les dades en la interfície
        TextView tv;

        tv = (TextView) findViewById(R.id.edtCodi);
        tv.setText(datos.getString(datos.getColumnIndex(ArticleDataSource.ARTICLES_CODI)));
        tv.setEnabled(false);


        tv = (TextView) findViewById(R.id.edtDescripcio);
        tv.setText(datos.getString(datos.getColumnIndex(ArticleDataSource.ARTICLES_DESCRIPCIO)));

        tv = (TextView) findViewById(R.id.edtFamilia);

        tv.setText(datos.getString(datos.getColumnIndex(ArticleDataSource.ARTICLES_FAMILIA)));
        tv.setEnabled(false);

        tv = (TextView) findViewById(R.id.edtPreu);
        tv.setText(datos.getString(datos.getColumnIndex(ArticleDataSource.ARTICLES_PREU)));

        tv = (TextView) findViewById(R.id.edtEstoc);
        tv.setText(datos.getString(datos.getColumnIndex(ArticleDataSource.ARTICLES_ESTOC)));
        tv.setEnabled(false);

    }

    private void aceptarCambios(long editant) {

        // Validem les dades
        TextView tv;

        // Codi ha d'estar informat
        tv = (TextView) findViewById(R.id.edtCodi);
        String codi = tv.getText().toString();
        if (codi.trim().equals("")) {
            Toast.makeText(this,"Camp codi obligatori", Toast.LENGTH_LONG).show();
            return;
        }

        if (bd.checkCodiExist(codi) && editant == -1) {
            Toast.makeText(this,"El codi ja existeix", Toast.LENGTH_LONG).show();
            return;
        }

        //Descripcio obligatori
        tv = (TextView) findViewById(R.id.edtDescripcio);
        String descripcio = tv.getText().toString();

        //if (descripcio.trim().equals("")) {
        //    Toast.makeText(this,"Camp descripcio obligatori", Toast.LENGTH_LONG).show();
        //    return;
        //}

        //Familia opcional
        tv = (TextView) findViewById(R.id.edtFamilia);
       // String familia = tv.getText().toString();


        tv.setText(this.itemSeleccionat);
        tv.setEnabled(false);
        String familia;
        familia = tv.getText().toString();


        //Preu obligatori
        tv = (TextView) findViewById(R.id.edtPreu);
        String preu = tv.getText().toString();


        if (preu.trim().equals("")) {
            Toast.makeText(this,"Camp preu obligatori", Toast.LENGTH_LONG).show();
            return;
        }

        double preuValor;
        try {
            preuValor = Double.valueOf(preu);
        }
        catch (Exception e) {
            Toast.makeText(this,"Camp preu ha de ser un valor numèric. Per decimals fes servir el punt (.)", Toast.LENGTH_LONG).show();
            return;
        }

        //Estoc
        tv = (TextView) findViewById(R.id.edtEstoc);
        String estoc = tv.getText().toString();
        tv.setEnabled(false);

        // Mirem si estem creant o estem guardant
        if (idTask == -1) {
            idTask = bd.taskAdd(codi, descripcio, familia, String.valueOf(preuValor));
        }
        else {
            bd.taskUpdate(idTask, codi, descripcio, familia, String.valueOf(preuValor), estoc);
        }

        Intent mIntent = new Intent();
        mIntent.putExtra("id", idTask);
        setResult(RESULT_OK, mIntent);

        finish();
    }

    private void cancelarCambios() {
        Intent mIntent = new Intent();
        mIntent.putExtra("id", idTask);
        setResult(RESULT_CANCELED, mIntent);

        finish();
    }

    private void deleteTask() {

        // Pedimos confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("¿Desitja eliminar la tasca?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                bd.taskDelete(idTask);

                Intent mIntent = new Intent();
                mIntent.putExtra("id", -1);  // Devolvemos -1 indicant que s'ha eliminat
                setResult(RESULT_OK, mIntent);

                finish();
            }
        });

        builder.setNegativeButton("No", null);

        builder.show();

    }

}