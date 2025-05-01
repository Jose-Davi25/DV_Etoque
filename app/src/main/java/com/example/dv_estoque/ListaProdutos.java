package com.example.dv_estoque;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.dv_estoque.Adapters.ProAdapter;
import com.example.dv_estoque.DataBase.DataBase;
import com.example.dv_estoque.Models.ProModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento responsável por listar os produtos armazenados no banco de dados.
 * Utiliza RecyclerView para exibir os dados e se comunica com o adapter para atualizar a interface.
 */
public class ListaProdutos extends Fragment implements ProAdapter.OnProActionListener {

    // Componentes
    private RecyclerView recyclerView;
    private ProAdapter proAdapter;
    private DataBase dbHelper;

    /**
     * Método chamado para criar a interface do fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Infla o layout do fragmento (XML)
        View view = inflater.inflate(R.layout.fragment_listar_produtos, container, false);

        // Inicializa componentes da interface
        inicializarComponentes(view);

        // Configura a RecyclerView
        configurarRecyclerView();

        // Carrega os produtos do banco de dados
        carregarProdutos();

        return view;
    }

    /**
     * Inicializa os componentes da interface.
     */
    private void inicializarComponentes(View view) {
        recyclerView = view.findViewById(R.id.listaProdutos); // Referência à RecyclerView no layout
        dbHelper = new DataBase(requireContext()); // Inicializa o helper do banco de dados
    }

    /**
     * Define o layout da RecyclerView e otimizações.
     */
    private void configurarRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext())); // Exibe em lista vertical
        recyclerView.setHasFixedSize(true); // Otimização caso os itens tenham tamanho fixo
    }

    /**
     * Carrega os produtos do banco de dados em uma nova thread para evitar travamentos na interface.
     */
    private void carregarProdutos() {
        new Thread(() -> {
            List<ProModel> proList = new ArrayList<>();
            SQLiteDatabase db = null;
            Cursor cursor = null;

            try {
                db = dbHelper.getReadableDatabase(); // Abertura do banco em modo leitura

                // Consulta os dados necessários da tabela "produtos"
                cursor = db.rawQuery("SELECT proId, proImg, proNome, proQtddeTotal, proPreco FROM produtos", null);

                // Lê os dados do cursor e adiciona na lista
                while (cursor.moveToNext()) {
                    proList.add(new ProModel(
                            cursor.getInt(0), // ID
                            cursor.isNull(1) ? null : cursor.getBlob(1), // Imagem (pode ser nula)
                            cursor.getString(2), // Nome
                            cursor.getInt(3), // Quantidade total
                            cursor.getDouble(4) // Preço
                    ));
                }

                // Atualiza a interface (deve ser feito na thread principal)
                requireActivity().runOnUiThread(() -> {
                    if (proAdapter == null) {
                        // Cria o adapter se ainda não existir
                        proAdapter = new ProAdapter(requireContext(), R.layout.itens_products, proList, this);
                        recyclerView.setAdapter(proAdapter);
                    } else {
                        // Atualiza a lista do adapter se ele já existir
                        proAdapter.atualizarLista(proList);
                    }
                });

            } catch (Exception e) {
                // Mostra mensagem de erro se falhar
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Erro ao carregar produtos", Toast.LENGTH_SHORT).show());
            } finally {
                // Fecha recursos para evitar vazamentos
                if (cursor != null) cursor.close();
                if (db != null) db.close();
            }
        }).start();
    }

    /**
     * Método chamado quando o fragmento volta à tela (ex: após editar algo).
     * Força o recarregamento dos dados.
     */
    @Override
    public void onResume() {
        super.onResume();
        carregarProdutos(); // Atualiza os dados sempre que o fragmento voltar a ficar visível
    }

    /**
     * Implementação da interface do adapter para reagir à exclusão de um produto.
     */
    @Override
    public void onProDeleted(int proId) {
        new Thread(() -> {
            SQLiteDatabase db = null;

            try {
                db = dbHelper.getWritableDatabase(); // Abertura do banco em modo escrita

                // Deleta o produto pelo ID
                int linhasAfetadas = db.delete(
                        "produtos",
                        "proId = ?",
                        new String[]{String.valueOf(proId)}
                );

                // Atualiza a interface com base no sucesso ou falha
                requireActivity().runOnUiThread(() -> {
                    if (linhasAfetadas > 0) {
                        Toast.makeText(requireContext(), "Produto removido", Toast.LENGTH_SHORT).show();
                        carregarProdutos(); // Recarrega a lista
                    } else {
                        Toast.makeText(requireContext(), "Erro ao remover produto", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Erro ao remover produto", Toast.LENGTH_SHORT).show());
            } finally {
                if (db != null) db.close();
            }
        }).start();
    }

    /**
     * Implementação da interface do adapter para atualização de produto (se necessário).
     * Neste caso, apenas recarrega os dados.
     */
    @Override
    public void onProUpdate() {
        carregarProdutos(); // Atualiza os dados após uma ação no produto
    }
}
