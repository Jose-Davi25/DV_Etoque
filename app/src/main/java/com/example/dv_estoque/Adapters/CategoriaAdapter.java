package com.example.dv_estoque.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder> {

    private List<String> categorias;

    public CategoriaAdapter(List<String> categorias) {
        this.categorias = categorias;
    }

    @NonNull
    @Override
    public CategoriaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaAdapter.ViewHolder holder, int position) {
        String categoria = categorias.get(position);
        holder.tvCategoria.setText(categoria);
    }

    @Override
    public int getItemCount() {
        return categorias == null ? 0 : categorias.size();
    }

    // Método para atualizar a lista de categorias
    public void atualizarCategorias(List<String> novasCategorias) {
        this.categorias = novasCategorias;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoria;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoria = itemView.findViewById(android.R.id.text1);
        }
    }
}
