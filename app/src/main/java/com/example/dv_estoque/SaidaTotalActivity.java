package com.example.dv_estoque;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dv_estoque.Adapters.SaidaTotalAdapter;
import com.example.dv_estoque.DataBase.ProdutoDAO;
import com.example.dv_estoque.Models.SaidaTotalModel;

import java.util.ArrayList;
import java.util.List;

public class SaidaTotalActivity extends AppCompatActivity {

    private RecyclerView recyclerSaidasEntradas;
    private SaidaTotalAdapter entradaSaidaAdapter;
    private ProdutoDAO produtoDAO;
    private Button limparTudoES;
    private TextView orderName, orderQtd, orderPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saida_total);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;});

        produtoDAO = new ProdutoDAO(this);
        recyclerSaidasEntradas = findViewById(R.id.recyclerSaidasEntradas);
        recyclerSaidasEntradas.setLayoutManager(new LinearLayoutManager(this)); // Adicione esta linha

        // Inicialize o adapter ANTES de carregar os dados
        entradaSaidaAdapter = new SaidaTotalAdapter(this, new ArrayList<>());
        recyclerSaidasEntradas.setAdapter(entradaSaidaAdapter);

        limparTudoES = findViewById(R.id.btnLimparSaidasTotais);
        limparTudoES.setOnClickListener(v -> limpartudo()); // Adicione esta linha

        // BOTÕES DE ORDENAÇÃO
        orderName = findViewById(R.id.listNome);
        orderQtd = findViewById(R.id.listQtdTotal);
        orderPrice = findViewById(R.id.listPriceTotal);

        orderName.setOnClickListener( v -> ordenarPorNome());
        orderQtd.setOnClickListener( v -> ordenarPorQtd());
        orderPrice.setOnClickListener( v -> ordenarPorPreco());

        carregarDados();
    }
    private void ordenarPorNome() {
        List<SaidaTotalModel> lista2 = produtoDAO.obterTodasSaidasAcumuladas2(); // Método alterado
        entradaSaidaAdapter.atualizarLista(lista2);
    }

    private void ordenarPorQtd() {
        List<SaidaTotalModel> lista = produtoDAO.obterTodasSaidasAcumuladas(); // Método alterado
        entradaSaidaAdapter.atualizarLista(lista);
    }

    private void ordenarPorPreco() {
        List<SaidaTotalModel> lista3 = produtoDAO.obterTodasSaidasAcumuladas3(); // Método alterado
        entradaSaidaAdapter.atualizarLista(lista3);
    }

    private void carregarDados() {
        List<SaidaTotalModel> lista = produtoDAO.obterTodasSaidasAcumuladas(); // Método alterado
        entradaSaidaAdapter.atualizarLista(lista);
    }

    private void limpartudo() {
        new AlertDialog.Builder(this)
                .setTitle("Limpar Histórico")
                .setMessage("Deseja apagar todas as entradas e saídas?")
                .setPositiveButton("Sim", (d, w) -> {
                    if (produtoDAO.limparTodasSaidasTotais()) {
                        carregarDados();
                        Toast.makeText(this, "Histórico limpo!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    //
    @Override
    protected void onDestroy(){
        produtoDAO.close();
        super.onDestroy();
    }
}