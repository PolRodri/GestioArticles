package com.example.gestioarticles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class movimentsDiaActivity extends AppCompatActivity {

    private ArticleDataSource bd;
    private adapterMovementsFilter scMovements;

    private static String[] fromMagatzem = new String[]{
            ArticleDataSource.MOVIMENT_ARTICLE_CODI,
            ArticleDataSource.MOVIMENT_QUANTITAT};

    private static int[] toMagatzem = new int[]{
            R.id.lblCodiMagatzem,
            R.id.lblQuantitatMagatzem};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moviments_dia);

        String data;
        data = this.getIntent().getExtras().getString("data");

        setTitle("Llista moviments a dia " + data);

        ListView lst = (ListView)findViewById(R.id.listMoviments);
        lst.setAdapter(scMovements);

        bd = new ArticleDataSource(this);

        loadMovements();
        filterData(data);
    }

    private void loadMovements() {

        // Demanem totes les tasques
        Cursor cursorMovements = bd.moviments();

        // Now create a simple cursor adapter and set it to display
        scMovements = new adapterMovementsFilter(this,
                R.layout.row_movimentss,
                cursorMovements,
                fromMagatzem,
                toMagatzem,
                1);


        //Adaptem l'adapter heh
        ListView lst = (ListView)findViewById(R.id.listMoviments);
        lst.setAdapter(scMovements);



    }

    private void filterData(String data) {

        //CANVI DE FORMAT DE LA DATA
        if (data.substring(1,2).equalsIgnoreCase("/")){
            data = "0" + data;
        }

        //"04/2/2021"
        if (data.substring(4,5).equalsIgnoreCase("/")){
            //data = data.substring(1,3) + "0";
            data = data.substring(0, 3) + "0" + data.substring(3);
        }

        Cursor cursorMovements = bd.movimentsDataSelected(data);

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scMovements.changeCursor(cursorMovements);
        scMovements.notifyDataSetChanged();
    }


}

class adapterMovementsFilter extends android.widget.SimpleCursorAdapter {

    private  movimentsDiaActivity movementIcon;

    public adapterMovementsFilter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        movementIcon = (movimentsDiaActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        // Agafem l'objecte de la view que es una LINEA DEL CURSOR
        Cursor linia = (Cursor) getItem(position);

        String tipus = linia.getString(linia.getColumnIndexOrThrow(ArticleDataSource.MOVIMENT_TIPUS));

        ImageView entrada = (ImageView)view.findViewById(R.id.btnPlus);
        ImageView sortida = (ImageView)view.findViewById(R.id.btnSortida);

        LinearLayout contingutLiniaLayout = (LinearLayout)view.findViewById(R.id.tipusMovimentContingut);

        if(tipus.equalsIgnoreCase("E")){
            sortida.setVisibility(View.GONE);
            //android:layout_marginLeft="80dp"
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)contingutLiniaLayout.getLayoutParams();
            params.setMargins(155,0,0,0);

        }else {
            entrada.setVisibility(View.GONE);
            view.setBackgroundColor(Color.parseColor("#ff757c"));
        }

/*
        Cursor cursorMovements = bd.movimentsDataSelected(data);
        String tipus = cursorMovements.getString(cursorMovements.getColumnIndexOrThrow(ArticleDataSource.MOVIMENT_TIPUS));

        ImageView entrada = (ImageView)findViewById(R.id.btnPlus);
        ImageView sortida = (ImageView)findViewById(R.id.btnSortida);

        if(tipus.equalsIgnoreCase("entrada")){
            entrada.setVisibility(View.GONE);
        }else {
            sortida.setVisibility(View.GONE);
        }*/

        return view;
    }

}