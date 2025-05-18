package com.example.dv_estoque.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "grcestoque.db";  // Melhor adicionar extensão .db
    private static final int DATABASE_VERSION = 2;

    public DataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criação da tabela categorias
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS categorias (" +
                        "catId INTEGER PRIMARY KEY AUTOINCREMENT," +  // AUTOINCREMENT para IDs únicos
                        "catImg BLOB," +
                        "catNome TEXT NOT NULL UNIQUE)");               // UNIQUE para evitar duplicidade de categorias

        // Criação da tabela produtos com relação à categorias
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS produtos (" +
                        "proId INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "proImg BLOB," +
                        "proNome TEXT NOT NULL," +
                        "catId INTEGER," +
                        "proQtddeTotal INTEGER NOT NULL DEFAULT 0," +
                        "proPreco REAL NOT NULL DEFAULT 0," +
                        "FOREIGN KEY(catId) REFERENCES categorias(catId) ON DELETE SET NULL ON UPDATE CASCADE)");

        // Criação da tabela para controle de saídas de estoque, se necessário
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS tabela (" +
                        "tabId INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "tabNome TEXT," +
                        "tabQtddeTotal INTEGER," +
                        "tabPreco REAL)");

        // Criação da tabela de saída lógica
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS saida_logica (" +
                        "saidaId INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "proNome TEXT," +
                        "quantidadeVendida INTEGER," +
                        "precoTotal REAL," +
                        "dataSaida TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Atualização condicional para versões futuras
        if (oldVersion < 2) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS saida_logica (" +
                            "saidaId INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "proNome TEXT," +
                            "quantidadeVendida INTEGER," +
                            "precoTotal REAL," +
                            "dataSaida TEXT)");
        }
        // Futuras atualizações podem ser colocadas aqui
    }
}
