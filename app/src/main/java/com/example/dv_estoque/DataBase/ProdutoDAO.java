package com.example.dv_estoque.DataBase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.dv_estoque.Models.SaidaTotalModel;
import com.example.dv_estoque.Models.ProModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.example.dv_estoque.Models.SaidaModel;

public class ProdutoDAO {

    private SQLiteDatabase db;

    public ProdutoDAO(Context context) {
        DataBase dataBase = new DataBase(context);
        db = dataBase.getWritableDatabase();
    }

    // 📉 Registrar saída de produto e gravar saída lógica
    public boolean registrarSaidaProduto(int produtoId, int quantidadeSaida) {
        Cursor cursor = db.rawQuery("SELECT proNome, proQtddeTotal, proPreco FROM produtos WHERE proId = ?", new String[]{String.valueOf(produtoId)});

        if (cursor.moveToFirst()) {
            String nomeProduto = cursor.getString(0);
            int quantidadeAtual = cursor.getInt(1);
            double precoUnitario = cursor.getDouble(2);

            if (quantidadeSaida <= quantidadeAtual) {
                int novaQuantidade = quantidadeAtual - quantidadeSaida;

                ContentValues valores = new ContentValues();
                valores.put("proQtddeTotal", novaQuantidade);

                int atualizado = db.update("produtos", valores, "proId = ?", new String[]{String.valueOf(produtoId)});
                cursor.close();

                if (atualizado > 0) {
                    // Registrar na tabela de saída lógica
                    String dataAtual = new SimpleDateFormat("dd/MM/yyyy | HH:mm:ss", new Locale("pt", "BR")).format(new Date());
                    registrarSaidaLogica(nomeProduto, quantidadeSaida, precoUnitario, dataAtual);
                    return true;
                }
            }
        }

        cursor.close();
        return false;
    }

    // ✅ Registrar saída lógica no banco
    public boolean registrarSaidaLogica(String proNome, int quantidadeVendida, double precoUnitario, String dataSaida) {
        double precoTotal = precoUnitario * quantidadeVendida;

        ContentValues valores = new ContentValues();
        valores.put("proNome", proNome);
        valores.put("quantidadeVendida", quantidadeVendida);
        valores.put("precoTotal", precoTotal);
        valores.put("dataSaida", dataSaida);

        long resultado = db.insert("saida_logica", null, valores);
        return resultado != -1;
    }

    @SuppressLint("Range")
    public List<ProModel> obterTodosProdutos() {
        List<ProModel> lista = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM produtos", null);
        if (cursor.moveToFirst()) {
            do {
                ProModel p = new ProModel();
                p.setProId(cursor.getInt(cursor.getColumnIndex("proId")));
                p.setProNome(cursor.getString(cursor.getColumnIndex("proNome")));
                p.setProQuantidade(cursor.getInt(cursor.getColumnIndex("proQtddeTotal")));
                p.setProPreco(cursor.getDouble(cursor.getColumnIndex("proPreco")));
                lista.add(p);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }
    /// /////////////////////////////////////////////////////////////
    // Método para acumular saídas
    public boolean acumularSaidaTotal(int proId, int quantidadeSaida, double precoTotal) {
        try {
            ContentValues values = new ContentValues();
            String sql = "UPDATE saidasTotais SET " +
                    "SQtddeSaidaTotal = SQtddeSaidaTotal + ?, " +
                    "SPrecoTotalSaida = SPrecoTotalSaida + ? " +
                    "WHERE proId = ?";

            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindLong(1, quantidadeSaida);
            stmt.bindDouble(2, precoTotal);
            stmt.bindLong(3, proId);

            int rowsAffected = stmt.executeUpdateDelete();

            if (rowsAffected == 0) {
                ContentValues newValues = new ContentValues();
                newValues.put("proId", proId);
                newValues.put("SQtddeSaidaTotal", quantidadeSaida);
                newValues.put("SPrecoTotalSaida", precoTotal);

                Cursor cursor = db.rawQuery("SELECT proNome FROM produtos WHERE proId=?",
                        new String[]{String.valueOf(proId)});
                if (cursor.moveToFirst()) {
                    newValues.put("SNome", cursor.getString(0));
                }
                cursor.close();

                return db.insert("saidasTotais", null, newValues) != -1;
            }
            return true;
        } catch (Exception e) {
            Log.e("DAO", "Erro ao acumular saída", e);
            return false;
        }
    }
    // Obter dados ajustado
    @SuppressLint("Range")
    public List<SaidaTotalModel> obterTodasSaidasAcumuladas() {
        List<SaidaTotalModel> lista = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT SaiId, proId, SNome, SQtddeSaidaTotal, SPrecoTotalSaida FROM saidasTotais ORDER BY 4 DESC", null);
        while (cursor.moveToNext()) {
            SaidaTotalModel item = new SaidaTotalModel(
                    cursor.getInt(cursor.getColumnIndex("SaiId")),
                    cursor.getInt(cursor.getColumnIndex("proId")),
                    cursor.getString(cursor.getColumnIndex("SNome")),
                    cursor.getInt(cursor.getColumnIndex("SQtddeSaidaTotal")), // Coluna nova
                    cursor.getDouble(cursor.getColumnIndex("SPrecoTotalSaida"))
            );
            lista.add(item);
        }
        cursor.close();
        return lista;
    }public List<SaidaTotalModel> obterTodasSaidasAcumuladas2() {
        List<SaidaTotalModel> lista2 = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT SaiId, proId, SNome, SQtddeSaidaTotal, SPrecoTotalSaida FROM saidasTotais ORDER BY 3", null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") SaidaTotalModel item = new SaidaTotalModel(
                    cursor.getInt(cursor.getColumnIndex("SaiId")),
                    cursor.getInt(cursor.getColumnIndex("proId")),
                    cursor.getString(cursor.getColumnIndex("SNome")),
                    cursor.getInt(cursor.getColumnIndex("SQtddeSaidaTotal")), // Coluna nova
                    cursor.getDouble(cursor.getColumnIndex("SPrecoTotalSaida"))
            );
            lista2.add(item);
        }
        cursor.close();
        return lista2;

    }public List<SaidaTotalModel> obterTodasSaidasAcumuladas3() {
        List<SaidaTotalModel> lista3 = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT SaiId, proId, SNome, SQtddeSaidaTotal, SPrecoTotalSaida FROM saidasTotais ORDER BY 5 DESC", null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") SaidaTotalModel item = new SaidaTotalModel(
                    cursor.getInt(cursor.getColumnIndex("SaiId")),
                    cursor.getInt(cursor.getColumnIndex("proId")),
                    cursor.getString(cursor.getColumnIndex("SNome")),
                    cursor.getInt(cursor.getColumnIndex("SQtddeSaidaTotal")), // Coluna nova
                    cursor.getDouble(cursor.getColumnIndex("SPrecoTotalSaida"))
            );
            lista3.add(item);
        }
        cursor.close();
        return lista3;
    }
    /// LIMPA TUDO
    public boolean limparTodasSaidasTotais() {
        int linhasAfetadas = db.delete("saidasTotais", null, null);
        return linhasAfetadas > 0;
    }
    /// ///////////////////////////////////////////////////////

    // Buscar produto por ID
    public Cursor buscarProdutoPorId(int produtoId) {
        return db.rawQuery("SELECT * FROM produtos WHERE proId = ?", new String[]{String.valueOf(produtoId)});
    }

    // Deletar produto
    public void deletarProduto(int id) {
        db.delete("produtos", "proId = ?", new String[]{String.valueOf(id)});
    }

    // Fechar banco de dados
    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
    /**
     * Obtém todas as saídas registradas
     * @return Lista de modelos de saída
     */
    @SuppressLint("Range")
    public List<SaidaModel> obterTodasSaidasnome() {
        List<SaidaModel> saidas = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT saidaId, proNome, quantidadeVendida, precoTotal, dataSaida FROM saida_logica ORDER BY 2", null);

        while (cursor.moveToNext()) {
            SaidaModel saida = new SaidaModel(
                    cursor.getInt(0), // saidaId
                    cursor.getString(1), // proNome
                    cursor.getInt(2), // quantidadeVendida
                    cursor.getDouble(3), // precoTotal
                    cursor.getString(4) // dataSaida
            );
            saidas.add(saida);
        }

        cursor.close();
        return saidas;
    }
    /// ///// quantidade
    @SuppressLint("Range")
    public List<SaidaModel> obterTodasSaidasQtd() {
        List<SaidaModel> saidas = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT saidaId, proNome, quantidadeVendida, precoTotal, dataSaida FROM saida_logica ORDER BY 3 DESC", null);

        while (cursor.moveToNext()) {
            SaidaModel saida = new SaidaModel(
                    cursor.getInt(0), // saidaId
                    cursor.getString(1), // proNome
                    cursor.getInt(2), // quantidadeVendida
                    cursor.getDouble(3), // precoTotal
                    cursor.getString(4) // dataSaida
            );
            saidas.add(saida);
        }

        cursor.close();
        return saidas;
    }
    ////////////// preco
    @SuppressLint("Range")
    public List<SaidaModel> obterTodasSaidas() {
        List<SaidaModel> saidas = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT saidaId, proNome, quantidadeVendida, precoTotal, dataSaida FROM saida_logica ORDER BY 4 DESC", null);

        while (cursor.moveToNext()) {
            SaidaModel saida = new SaidaModel(
                    cursor.getInt(0), // saidaId
                    cursor.getString(1), // proNome
                    cursor.getInt(2), // quantidadeVendida
                    cursor.getDouble(3), // precoTotal
                    cursor.getString(4) // dataSaida
            );
            saidas.add(saida);
        }

        cursor.close();
        return saidas;
    }
    ///// data
    @SuppressLint("Range")
    public List<SaidaModel> obterTodasSaidasdata() {
        List<SaidaModel> saidas = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT saidaId, proNome, quantidadeVendida, precoTotal, dataSaida FROM saida_logica ORDER BY 5 DESC", null);

        while (cursor.moveToNext()) {
            SaidaModel saida = new SaidaModel(
                    cursor.getInt(0), // saidaId
                    cursor.getString(1), // proNome
                    cursor.getInt(2), // quantidadeVendida
                    cursor.getDouble(3), // precoTotal
                    cursor.getString(4) // dataSaida
            );
            saidas.add(saida);
        }

        cursor.close();
        return saidas;
    }
    /**
     * Exclui uma saída específica
     * @param saidaId ID da saída a ser excluída
     * @return Verdadeiro se bem sucedido
     */
    // EXCLUIR UMA SAIDA ESPECIFICA
    public boolean excluirSaida(int saidaId) {
        int linhasAfetadas = db.delete("saida_logica", "saidaId = ?", new String[]{String.valueOf(saidaId)});
        return linhasAfetadas > 0;
    }
    /**
     * Limpa todo o histórico de saídas
     * @return Verdadeiro se bem sucedido
     */
    // EXCLUI TODAS AS SAIDAS
    public boolean limparTodasSaidas(){
        int linhasAfetadas = db.delete("saida_logica",null, null);
        return linhasAfetadas >0;
    }
}
