package com.example.dv_estoque;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dv_estoque.Adapters.EntradaSaidaAdapter;
import com.example.dv_estoque.Adapters.ProAdapter;
import com.example.dv_estoque.DataBase.DataBase;
import com.example.dv_estoque.DataBase.ProdutoDAO;
import com.example.dv_estoque.Models.EntradaSaidaModel;

import java.util.ArrayList;
import java.util.List;

public class Saidas_EntradasActivity extends AppCompatActivity {

    private RecyclerView recyclerSaidasEntradas;
    private EntradaSaidaAdapter entradaSaidaAdapter;
    private ProdutoDAO produtoDAO;
    private Button limparTudoES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saidas_entradas);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;});

        produtoDAO = new ProdutoDAO(this);

        recyclerSaidasEntradas = findViewById(R.id.recyclerSaidasEntradas);
        recyclerSaidasEntradas.setLayoutManager(new LinearLayoutManager(this));

        limparTudoES = findViewById(R.id.btnLimpartudoES);
        limparTudoES.setOnClickListener(v -> limpartudo());

        carregarDados();
    }

    private void carregarDados() {
        List<EntradaSaidaModel> lista = produtoDAO.obterTodasEntradasSaidas();

        // Verifique os dados no Logcat
        Log.d("DEBUG_ENTRADAS_SAIDAS", "Total de registros: " + lista.size());
        for (EntradaSaidaModel item : lista) {
            Log.d("DEBUG_ENTRADAS_SAIDAS",
                    "Item: " + item.getESNome() + " | Saídas: " + item.getESQtddeSaida());
        }

        entradaSaidaAdapter = new EntradaSaidaAdapter(this, lista);
        recyclerSaidasEntradas.setAdapter(entradaSaidaAdapter);
    }
    private void limpartudo() {
        new AlertDialog.Builder(this)
                .setTitle("Limpar Hitorico")
                .setMessage("Deseja Apagar todas as entradas e saidas ?")
                .setPositiveButton("Sim", (d, w) -> {
                    if (produtoDAO.limparTodasEntradasSaidas()) {
                        carregarDados();
                        Toast.makeText(this, "Hitorico limpo!", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Não", null).show();
    }
    //
    @Override
    protected void onDestroy(){
        produtoDAO.close();
        super.onDestroy();
    }
}