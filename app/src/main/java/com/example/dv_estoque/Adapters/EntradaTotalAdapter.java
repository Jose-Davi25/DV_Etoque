package com.example.dv_estoque.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dv_estoque.Models.EntradaTotalModel;
import com.example.dv_estoque.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EntradaTotalAdapter extends RecyclerView.Adapter<EntradaTotalAdapter.ViewHolder>{

    private final Context context;
    private final int itemLayout;
    private List<EntradaTotalModel> modelList;

    public EntradaTotalAdapter(Context context, int itemLayout, List<EntradaTotalModel> modelList) {
        this.context = context;
        this.itemLayout = itemLayout;
        this.modelList = modelList != null ? modelList : new ArrayList<>();
    }

    public void atualizarLista(List<EntradaTotalModel> novaLista) {
        this.modelList = novaLista != null ? novaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EntradaTotalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_entrada_total, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntradaTotalAdapter.ViewHolder holder, int position) {
        EntradaTotalModel item = modelList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeEn, quantidadeEn, precoEn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeEn = itemView.findViewById(R.id.tv_nome_Ent);
            quantidadeEn = itemView.findViewById(R.id.tv_qtdde_total_Ent);
            precoEn = itemView.findViewById(R.id.tv_preco_total_Ent);
        }

        public void bind(EntradaTotalModel item){
            nomeEn.setText(item.getEntrNome());
            quantidadeEn.setText(String.valueOf(item.getEntrQtddeTotal()));
            precoEn.setText(String.format(Locale.getDefault(), "R$ %.2f", item.getEntrPrecoTotal()));
        }
    }

    public void atualizar(List<EntradaTotalModel> novaLista){
        this.modelList = novaLista;
        notifyDataSetChanged();
    }
}
