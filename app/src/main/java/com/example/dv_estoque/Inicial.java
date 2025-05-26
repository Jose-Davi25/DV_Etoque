package com.example.dv_estoque;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dv_estoque.DataBase.DataBase;

public class Inicial extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicial, container, false);

        // Botão categorias
        Button btnCategorias = view.findViewById(R.id.btnCategorias);
        btnCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoriaActivity.class);
                startActivity(intent);
            }
        });
        // Botão Saidas e Entradas
        Button btnSaidaEntrada = view.findViewById(R.id.btnSaidaEntrada);
        btnSaidaEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SaidaTotalActivity.class);
                startActivity(intent);
            }
        });
        // verificar esses error

//        // Botão para ir para a tela de CadastrarProdutos
//        Button btnIrCadastrarPro = view.findViewById(R.id.adicionar_produto);
//        btnIrCadastrarPro.setOnClickListener(v -> {
//            // Substitui o Fragment atual pelo CadastrarProdutos
//            getParentFragmentManager().beginTransaction()
//                    .replace(R.id.containerPrincipal, new CadastrarProdutos())
//                    .addToBackStack(null) // adiciona à pilha para voltar com o botão "Back"
//                    .commit();
//        });
//        // Botão para ir para a tela da ListaProdutos
//        Button btnIrListarPro = view.findViewById(R.id.ver_estoque);
//        btnIrListarPro.setOnClickListener(v -> {
//            // Substitui o Fragment atual pelo ListaProdutos
//            getParentFragmentManager().beginTransaction()
//                    .replace(R.id.containerPrincipal, new ListaProdutos())
//                    .addToBackStack(null)
//                    .commit();
//        });


        TextView tvProdutos = view.findViewById(R.id.tvProdutosCadastrados);
        int totalProdutos = buscarProdutosCadastrados(getContext());
        tvProdutos.setText(String.valueOf(totalProdutos));

        TextView tvTotalEstoque = view.findViewById(R.id.tvTotalEstoque);
        int totalEstoque = buscarTotalEstoque(getContext());
        tvTotalEstoque.setText(String.valueOf(totalEstoque));

        TextView tvEstoqueBaixo = view.findViewById(R.id.tvEstoqueBaixo);
        int totalEstoqueBaixo = buscarEstoqueBaixo(getContext());
        tvEstoqueBaixo.setText(String.valueOf(totalEstoqueBaixo));

        TextView tvMovimentacoesHoje = view.findViewById(R.id.tvMovimentacoesHoje);
        int totalMovimentacoesHoje = buscarMovimentacoesHoje(getContext());
        tvMovimentacoesHoje.setText(String.valueOf(totalMovimentacoesHoje));

        return view;
    }
    public int buscarProdutosCadastrados(Context context) {
        SQLiteDatabase db = new DataBase(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM produtos", null);
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return total;
    }
    public int buscarTotalEstoque(Context context) {
        SQLiteDatabase db = new DataBase(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(proQtddeTotal) FROM produtos", null);
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return total;
    }
    public int buscarEstoqueBaixo(Context context) {
        SQLiteDatabase db = new DataBase(context).getReadableDatabase();
        // Define o limite do que é considerado "baixo"
        int limite = 10;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM produtos WHERE proQtddeTotal <= ?", new String[]{String.valueOf(limite)});
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return total;
    }
    public int buscarMovimentacoesHoje(Context context) {
        SQLiteDatabase db = new DataBase(context).getReadableDatabase();

        // Captura apenas a data de hoje no formato brasileiro (sem hora)
        String dataHoje = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR")).format(new Date());

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM saida_logica WHERE dataSaida LIKE ?",
                new String[]{dataHoje + "%"} // Vai pegar tudo de hoje, independente da hora
        );

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return total;
    }

}


