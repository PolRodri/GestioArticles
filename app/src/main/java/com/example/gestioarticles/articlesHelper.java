package com.example.gestioarticles;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class articlesHelper extends SQLiteOpenHelper {

    // database version
    private static final int database_VERSION = 2;

    // database name
    private static final String database_NAME = "articlesDataBase";

    public articlesHelper(Context context){
        super(context, database_NAME, null, database_VERSION);
    }

    //Mètodes creats per defecte
    @Override
    public void onCreate(SQLiteDatabase db) {

        //Els camps obligatoris es defineixen amb NOT NULL
        String CREATE_ARTICLES =
                "CREATE TABLE articles (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "codi TEXT," +
                "descripcio TEXT NOT NULL," +
                "familia TEXT," +
                "preu REAL NOT NULL," +
                "estoc REAL DEFAULT 0)";

        //en un futur posar data de creacio
        String CREATE_MOVIMENT =
                "CREATE TABLE moviment (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "codiArticle TEXT," +
                        "dia TEXT," +
                        "quantitat REAL," +
                        "tipus TEXT," +
                        "FOREIGN KEY(codiArticle) REFERENCES articles(codi))";

        db.execSQL(CREATE_ARTICLES);
        db.execSQL(CREATE_MOVIMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (newVersion > oldVersion) {

            String CREATE_MOVIMENT =
                    "CREATE TABLE moviment (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "codiArticle TEXT," +
                            "dia TEXT," +
                            "quantitat REAL," +
                            "tipus TEXT," +
                            "FOREIGN KEY(codiArticle) REFERENCES articles(codi))";

            db.execSQL(CREATE_MOVIMENT);
        }
    }
}
