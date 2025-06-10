package com.example.dv_estoque;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.dv_estoque.DataBase.DataBase;

import java.util.ArrayList;
import java.util.List;
public class EntradaEstoqueActivity extends AppCompatActivity {

    AutoCompleteTextView autoCompleteProduto;
    EditText editQuantidade;
    Button btnConfirmarEntrada;

    ImageView imgProduto;
    TextView txtNomeProduto, txtCategoriaProduto, txtQuantidadeAtual, txtPrecoProduto;
    CardView cardInfoProduto;


    ArrayList<String> listaProdutos = new ArrayList<>();
    ArrayAdapter<String> adapter;

    int produtoId = -1;
    int quantidadeAtual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrada_estoque);

        autoCompleteProduto = findViewById(R.id.autoCompleteProduto);
        editQuantidade = findViewById(R.id.editQuantidade);
        btnConfirmarEntrada = findViewById(R.id.btnConfirmarEntrada);

        imgProduto = findViewById(R.id.imgProduto);
        txtNomeProduto = findViewById(R.id.txtNomeProduto);
        txtCategoriaProduto = findViewById(R.id.txtCategoriaProduto);
        txtQuantidadeAtual = findViewById(R.id.txtQuantidadeAtual);
        txtPrecoProduto = findViewById(R.id.txtPrecoProduto);
        cardInfoProduto = findViewById(R.id.cardInfoProduto);

        carregarProdutos();

        autoCompleteProduto.setOnItemClickListener((adapterView, view, i, l) -> {
            String nomeSelecionado = adapterView.getItemAtPosition(i).toString();
            mostrarInfoProduto(nomeSelecionado);
        });

        btnConfirmarEntrada.setOnClickListener(v -> {
            String quantidadeStr = editQuantidade.getText().toString().trim();
            if (produtoId > 0 && !quantidadeStr.isEmpty()) {
                int qtd = Integer.parseInt(quantidadeStr);
                adicionarEstoque(produtoId, qtd);
            } else {
                Toast.makeText(this, "Selecione um produto e informe a quantidade", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarProdutos() {
        DataBase dbHelper = new DataBase(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT proNome FROM produtos", null);

        while (cursor.moveToNext()) {
            listaProdutos.add(cursor.getString(0));
        }

        cursor.close();
        db.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, listaProdutos);
        autoCompleteProduto.setAdapter(adapter);
    }

    private void mostrarInfoProduto(String nomeProduto) {
        DataBase dbHelper = new DataBase(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT p.proId, p.proImg, p.proNome, c.catNome, p.proQtddeTotal, p.proPreco " +
                "FROM produtos p LEFT JOIN categorias c ON p.catId = c.catId WHERE p.proNome = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{nomeProduto});

        if (cursor.moveToFirst()) {
            produtoId = cursor.getInt(0);
            byte[] imgBytes = cursor.getBlob(1);
            String nome = cursor.getString(2);
            String categoria = cursor.getString(3);
            quantidadeAtual = cursor.getInt(4);
            double preco = cursor.getDouble(5);

            // Mostra os dados
            txtNomeProduto.setText("Nome: " + nome);
            txtCategoriaProduto.setText("Categoria: " + categoria);
            txtQuantidadeAtual.setText("Quantidade atual: " + quantidadeAtual);
            txtPrecoProduto.setText(String.format("Preço: R$ %.2f", preco));

            // Carrega imagem se existir
            if (imgBytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                imgProduto.setImageBitmap(bitmap);
            } else {
                imgProduto.setImageResource(R.drawable.ic_produto_padrao);
            }

            cardInfoProduto.setVisibility(View.VISIBLE);
        }

        cursor.close();
        db.close();
    }

    private void adicionarEstoque(int id, int quantidadeAdicionar) {
        DataBase dbHelper = new DataBase(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("UPDATE produtos SET proQtddeTotal = proQtddeTotal + ? WHERE proId = ?",
                new Object[]{quantidadeAdicionar, id});

        db.close();
        Toast.makeText(this, "Entrada de estoque registrada com sucesso!", Toast.LENGTH_SHORT).show();
        finish();
    }
}