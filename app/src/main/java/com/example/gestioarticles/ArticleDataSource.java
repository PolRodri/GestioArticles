package com.example.gestioarticles;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ArticleDataSource {

    public static final String table_ARTICLES = "articles";
    public static final String ARTICLES_ID = "_id";
    public static final String ARTICLES_CODI = "codi";
    public static final String ARTICLES_DESCRIPCIO = "descripcio";
    public static final String ARTICLES_FAMILIA = "familia";
    public static final String ARTICLES_PREU = "preu";
    public static final String ARTICLES_ESTOC = "estoc";

    public static final String table_MOVIMENT = "moviment";
    public static final String MOVIMENT_ID = "_id";
    public static final String MOVIMENT_ARTICLE_CODI = "codiArticle";
    public static final String MOVIMENT_DATA = "dia";
    public static final String MOVIMENT_QUANTITAT = "quantitat";
    public static final String MOVIMENT_TIPUS= "tipus";


    private articlesHelper dbHelper;

    //Per escriure i llegir
    private SQLiteDatabase dbW, dbR;

    //CONSTRUCTOR PUBLIC
    public ArticleDataSource(Context ctx){

        //el primer que farà el controlador és obrir la connexió amb la base de dades
        dbHelper = new articlesHelper(ctx);

        //write & read
        open();

    }


    //MÈTODES

        // DESTRUCTOR
    protected void finalize () {
        dbW.close();
        dbR.close();
    }

    private void open() {
        dbW = dbHelper.getWritableDatabase();
        dbR = dbHelper.getReadableDatabase();
    }

    //FUNCIONS i FILTRES
    public Cursor task(long id) {
        // Retorna un cursor només amb el id indicat
        return dbR.query(table_ARTICLES, new String[]{ARTICLES_ID,ARTICLES_CODI,ARTICLES_DESCRIPCIO,ARTICLES_FAMILIA,ARTICLES_PREU,ARTICLES_ESTOC},
                ARTICLES_ID+ "=?", new String[]{String.valueOf(id)},
                null, null, null);

    }

    public Cursor dadesMagatzem(long id) {
        // Retorna un cursor només amb el id indicat
        return dbR.query(table_ARTICLES, new String[]{ARTICLES_ID,ARTICLES_CODI,ARTICLES_DESCRIPCIO,ARTICLES_FAMILIA,ARTICLES_PREU,ARTICLES_ESTOC},
                ARTICLES_ID+ "=?", new String[]{String.valueOf(id)},
                null, null, null);

    }

    public long taskAdd(String codi, String descriptio, String familia, String preu) {

        ContentValues values = new ContentValues();
        values.put(ARTICLES_CODI, codi);
        values.put(ARTICLES_DESCRIPCIO, descriptio);
        values.put(ARTICLES_FAMILIA, familia);
        values.put(ARTICLES_PREU, preu);
        values.put(ARTICLES_ESTOC,0);  // Forcem 0

        return dbW.insert(table_ARTICLES,null,values);
    }

    //ADD MAGATZEM (taula moviment)

    public long magatzemAdd(String codi, String quantitat, String data, String tipusChar) {

        ContentValues values = new ContentValues();
        values.put(MOVIMENT_ARTICLE_CODI, codi);
        values.put(MOVIMENT_QUANTITAT, quantitat);
        values.put(MOVIMENT_DATA, data);
        values.put(MOVIMENT_TIPUS, tipusChar);

        return dbW.insert(table_MOVIMENT,null,values);
    }

    public void taskUpdate(long id, String codi, String descriptio, String familia, String preu, String estoc) {

        ContentValues values = new ContentValues();
        values.put(ARTICLES_CODI, codi);
        values.put(ARTICLES_DESCRIPCIO, descriptio);
        values.put(ARTICLES_FAMILIA, familia);
        values.put(ARTICLES_PREU, preu);
        values.put(ARTICLES_ESTOC, estoc);

        dbW.update(table_ARTICLES,values, ARTICLES_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public void taskEstocUpdate(long id, String estoc) {

        ContentValues values = new ContentValues();
        values.put(ARTICLES_ESTOC, estoc);

        dbW.update(table_ARTICLES,values, ARTICLES_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public void taskDelete(long id) {
        // Eliminem la task amb clau primària "id"
        dbW.delete(table_ARTICLES,ARTICLES_ID + " = ?", new String[] { String.valueOf(id) });
    }

    //FILTRAR PER TIPUS
    public Cursor movimentsTipus(long id) {
        // Retorem moviments
        return dbR.query(table_MOVIMENT, new String[]{MOVIMENT_ID,MOVIMENT_ARTICLE_CODI, MOVIMENT_QUANTITAT, MOVIMENT_DATA, MOVIMENT_TIPUS},
                MOVIMENT_ID + "=?", new String[]{String.valueOf(id)},
                null, null, MOVIMENT_ID);
    }

    public Cursor articles() {
        // Retorem tots els articles
        return dbR.query(table_ARTICLES, new String[]{ARTICLES_ID,ARTICLES_CODI,ARTICLES_DESCRIPCIO,ARTICLES_FAMILIA,ARTICLES_PREU,ARTICLES_ESTOC},
                null, null,
                null, null, ARTICLES_ID);
    }

    //MAGATZEM
    public Cursor moviments() {
        // Retorem moviments
        return dbR.query(table_MOVIMENT, new String[]{MOVIMENT_ID,MOVIMENT_ARTICLE_CODI,MOVIMENT_QUANTITAT},
                null, null,
                null, null, MOVIMENT_ID);
    }

    //FILTRE PER DATA
    public Cursor movimentsDataSelected(String data) {
        // Retorem moviments
        return dbR.query(table_MOVIMENT, new String[]{MOVIMENT_ID,MOVIMENT_ARTICLE_CODI, MOVIMENT_QUANTITAT, MOVIMENT_DATA, MOVIMENT_TIPUS},
                MOVIMENT_DATA + "=?", new String[]{data},
                null, null, MOVIMENT_ARTICLE_CODI + " DESC");
    }

    public Cursor articlesDescripcio() {
        // Retornem els articles amb el camp DESCRIPCIO != ""
        return dbR.query(table_ARTICLES, new String[]{ARTICLES_ID,ARTICLES_CODI,ARTICLES_DESCRIPCIO,ARTICLES_FAMILIA,ARTICLES_PREU,ARTICLES_ESTOC},
                ARTICLES_DESCRIPCIO + "!=?", new String[]{""},
                null, null, ARTICLES_ID);

    }

    public Cursor articlesEstoc() {
        // Retornem els articles amb el camp DESCRIPCIO != ""
        return dbR.query(table_ARTICLES, new String[]{ARTICLES_ID,ARTICLES_CODI,ARTICLES_DESCRIPCIO,ARTICLES_FAMILIA,ARTICLES_PREU,ARTICLES_ESTOC},
                ARTICLES_ESTOC + "<=?", new String[]{String.valueOf(0)},
                null, null, ARTICLES_ID);

    }

    public boolean checkCodiExist(String valorCodi) {
        // Retorem si existeix el codi
        Cursor cursor= dbR.rawQuery(
                "SELECT * FROM " + table_ARTICLES + " WHERE " + ARTICLES_CODI + "=?",
                new String[] { String.valueOf(valorCodi) }
        );

        if(cursor.getCount() > 0){
            return true;
        } else {
            return false;
        }

    }
}
