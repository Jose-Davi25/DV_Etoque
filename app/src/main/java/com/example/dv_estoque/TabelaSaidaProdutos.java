package com.example.dv_estoque;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dv_estoque.Adapters.SaidaAdapter;
import com.example.dv_estoque.DataBase.ProdutoDAO;
import com.example.dv_estoque.Models.SaidaModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Fragmento para exibição do histórico de saídas de produtos
 * Gerencia a UI e interações com a lista de saídas registradas
 */
public class TabelaSaidaProdutos extends Fragment {

    private SaidaAdapter adapter;       // Adapter para o RecyclerView
    private ProdutoDAO dao;            // Camada de acesso a dados
    private RecyclerView recyclerView;  // Lista de exibição

    // Cria a view do fragmento
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tabela_saida_produtos, container, false);
    }

    // Configura os componentes após a criação da view
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarComponentes(view);
        configurarRecyclerView();
        configurarListeners();
        carregarDadosIniciais();
    }

    // Inicializa referências aos componentes da UI
    private void inicializarComponentes(View view) {
        recyclerView = view.findViewById(R.id.recyclerSaidas);
        dao = new ProdutoDAO(requireContext());

        // Configura o clique no botão de limpar tudo
        view.findViewById(R.id.btnLimpar).setOnClickListener(v -> limparTodasSaidas());
    }

    // Configura o RecyclerView e seu adapter
    private void configurarRecyclerView() {
        // Define o layout como lista linear vertical
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Cria adapter com lista vazia inicial
        adapter = new SaidaAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    // Configura os ouvintes de eventos
    private void configurarListeners() {
        adapter.setOnItemClickListener(new SaidaAdapter.OnItemClickListener() {
            @Override
            public void onItemDeleted(int position) {
                excluirSaida(position);
            }

            @Override
            public void onClearAll() {
                limparTodasSaidas();
            }
        });
    }

    // Carrega os dados iniciais de forma assíncrona
    private void carregarDadosIniciais() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Obtém dados do banco em background
            List<SaidaModel> dados = dao.obterTodasSaidas();

            // Atualiza UI na thread principal
            requireActivity().runOnUiThread(() -> {
                adapter.atualizarLista(dados);
                if (dados.isEmpty()) mostrarListaVazia();
            });
        });
    }

    // Exclui uma saída específica
    private void excluirSaida(int position) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Obtém item e executa exclusão
            SaidaModel item = adapter.getItem(position);
            boolean sucesso = dao.excluirSaida(item.getSaidaId());

            requireActivity().runOnUiThread(() -> {
                if (sucesso) {
                    adapter.removerItem(position);
                    mostrarToast("Item excluído");
                }
            });
        });
    }

    // Limpa todo o histórico
    private void limparTodasSaidas() {
        Executors.newSingleThreadExecutor().execute(() -> {
            boolean sucesso = dao.limparTodasSaidas();

            requireActivity().runOnUiThread(() -> {
                if (sucesso) {
                    adapter.limparLista();
                    mostrarToast("Histórico limpo");
                    mostrarListaVazia();
                }
            });
        });
    }

    // Exibe mensagens temporárias
    private void mostrarToast(String mensagem) {
        Toast.makeText(requireContext(), mensagem, Toast.LENGTH_SHORT).show();
    }

    // Atualiza UI para lista vazia
    private void mostrarListaVazia() {
        // Implementar estado vazio se necessário
    }

    // Recarrega dados quando o fragmento retorna à tela
    @Override
    public void onResume() {
        super.onResume();
        carregarDadosIniciais();
    }
}