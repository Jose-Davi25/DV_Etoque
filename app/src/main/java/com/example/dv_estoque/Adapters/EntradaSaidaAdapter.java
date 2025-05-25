package com.example.dv_estoque.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dv_estoque.Models.EntradaSaidaModel;
import com.example.dv_estoque.R;
import java.util.List;

public class EntradaSaidaAdapter extends RecyclerView.Adapter<EntradaSaidaAdapter.ViewHolder> {

    private final Context context;
    private List<EntradaSaidaModel> modelArrayList;

    public EntradaSaidaAdapter(Context context, List<EntradaSaidaModel> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_saida_entrada, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EntradaSaidaModel item = modelArrayList.get(position);

        holder.ESNome.setText(item.getESNome());
        holder.ESQtddeSaida.setText(String.valueOf(item.getESQtddeSaida()));
        holder.ESQtddeEntrada.setText(String.valueOf(item.getESQtddeEntrada())); // Corrigido
        holder.ESPrecoSaida.setText(String.format("R$ %.2f", item.getESPrecoSaida()));
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ESNome, ESQtddeEntrada, ESQtddeSaida, ESPrecoSaida;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ESNome = itemView.findViewById(R.id.tv_nome_ES);
            ESQtddeEntrada = itemView.findViewById(R.id.tv_qtdde_entradas_ES);
            ESQtddeSaida = itemView.findViewById(R.id.tv_qtdde_saidas_ES);
            ESPrecoSaida = itemView.findViewById(R.id.tv_preco_total_saidas_ES);
        }
    }
}