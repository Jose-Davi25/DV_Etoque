package com.example.dv_estoque.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dv_estoque.Models.SaidaTotalModel;
import com.example.dv_estoque.R;
import java.util.List;

public class SaidaTotalAdapter extends RecyclerView.Adapter<SaidaTotalAdapter.ViewHolder> {

    private final Context context;
    private List<SaidaTotalModel> modelArrayList;

    public SaidaTotalAdapter(Context context, List<SaidaTotalModel> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_saida_total, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SaidaTotalModel item = modelArrayList.get(position);

        holder.ESNome.setText(item.getESNome());
        holder.ESQtddeSaidaTotal.setText(String.valueOf(item.getESQtddeSaidaTotal())); // Novo campo
        holder.ESPrecoTotalSaida.setText(String.format("R$ %.2f", item.getESPrecoTotalSaida()));
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ESNome, ESQtddeSaidaTotal, ESPrecoTotalSaida; // Remova entrada

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ESNome = itemView.findViewById(R.id.tv_nome_ES);
            ESQtddeSaidaTotal = itemView.findViewById(R.id.tv_qtdde_saidas_total); // Novo ID no layout
            ESPrecoTotalSaida = itemView.findViewById(R.id.tv_preco_total_saidas_ES);
        }
    }

    public void atualizarLista(List<SaidaTotalModel> novaLista) {
        this.modelArrayList = novaLista;
        notifyDataSetChanged();
    }
}