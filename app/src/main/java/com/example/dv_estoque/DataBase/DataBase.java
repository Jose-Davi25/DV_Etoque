package com.example.dv_estoque.DataBase;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DataBase extends SQLiteOpenHelper {

    private static final String database = "grcestoque";
    private static final int VERSAO = 1;

    public DataBase(@Nullable Context context) {
        super(context, database, null, VERSAO);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE IF NOT EXISTS categorias " +
                "(catId INTEGER PRIMARY KEY," +
                "catImg BLOB," +
                "catNome TEXT)");

        db.execSQL(" CREATE TABLE IF NOT EXISTS produtos " +
                "(proId INTEGER PRIMARY KEY," +
                "proImg BLOB," +
                "proNome TEXT," +
                "proQtddeTotal INTEGER," +
                "proPreco REAL)");

//        db.execSQL(" CREATE TABLE IF NOT EXISTS produtos " +
//                "(proId INTEGER PRIMARY KEY," +
//                "proImg BLOB," +
//                "proNome TEXT," +
//                "catId INTEGER," +
//                "proQtddeTotal INTEGER," +
//                "proPreco REAL," +
//                "FOREIGN KEY(catId) REFERENCES categorias(catId))");

        db.execSQL(" CREATE TABLE IF NOT EXISTS tabela " +
                "(tabId INTEGER PRIMARY KEY," +
                "tabNome TEXT," +
                "tabQtddeTotal INTEGER," +
                "tabPreco REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//METODO NÃO UTILIZADO APRIORE
//        "proQtddeLocal INTEGER," +
    }
}
