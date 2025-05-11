package com.example.dv_estoque.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DataBase extends SQLiteOpenHelper {

    private static final String database = "grcestoque";
    private static final int VERSAO = 2;

    public DataBase(@Nullable Context context) {
        super(context, database, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criando a tabela de categorias
        db.execSQL("CREATE TABLE IF NOT EXISTS categorias " +
                "(catId INTEGER PRIMARY KEY," +
                "catImg BLOB," +
                "catNome TEXT)");

        // Criando a tabela de produtos (com relação a categorias)
        db.execSQL("CREATE TABLE IF NOT EXISTS produtos " +
                "(proId INTEGER PRIMARY KEY," +
                "proImg BLOB," +
                "proNome TEXT," +
                "catId INTEGER," +  // Referência para categorias
                "proQtddeTotal INTEGER," +
                "proPreco REAL," +
                "FOREIGN KEY(catId) REFERENCES categorias(catId))");

        // Criando tabela de saídas de estoque (se necessário)
        db.execSQL("CREATE TABLE IF NOT EXISTS tabela " +
                "(tabId INTEGER PRIMARY KEY," +
                "tabNome TEXT," +
                "tabQtddeTotal INTEGER," +
                "tabPreco REAL)");

        // Criando a tabela de saída lógica
        db.execSQL("CREATE TABLE IF NOT EXISTS saida_logica (" +
                "saidaId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "proNome TEXT," +
                "quantidadeVendida INTEGER," +
                "precoTotal REAL," +
                "dataSaida TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Método para atualizar o banco de dados caso necessário
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS saida_logica (" +
                    "saidaId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "proNome TEXT," +
                    "quantidadeVendida INTEGER," +
                    "precoTotal REAL," +
                    "dataSaida TEXT)");
        }
    }
}
