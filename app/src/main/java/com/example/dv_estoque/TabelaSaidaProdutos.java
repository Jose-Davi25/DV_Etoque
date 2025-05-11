package com.example.dv_estoque;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dv_estoque.Adapters.SaidaAdapter;
import com.example.dv_estoque.DataBase.ProdutoDAO;
import com.example.dv_estoque.Models.SaidaModel;

import java.util.List;

public class TabelaSaidaProdutos extends Fragment {

    private RecyclerView recyclerView;
    private SaidaAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tabela_saida_produtos, container, false);

        recyclerView = view.findViewById(R.id.recyclerSaidas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ProdutoDAO dao = new ProdutoDAO(getContext());
        List<SaidaModel> listaSaidas = dao.obterTodasSaidas(); // <-- usa o método novo

        adapter = new SaidaAdapter(getContext(), listaSaidas);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
