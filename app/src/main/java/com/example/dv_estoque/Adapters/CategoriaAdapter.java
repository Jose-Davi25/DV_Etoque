package com.example.dv_estoque.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dv_estoque.R;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder> {

    // Ajustado para ter apenas o método de clique longo
    public interface OnItemClickListener {
        void onItemLongClick(String categoria);
    }

    private List<String> categorias;
    private OnItemClickListener listener;

    public CategoriaAdapter(List<String> categorias, OnItemClickListener listener) {
        this.categorias = categorias;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categoria, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String categoria = categorias.get(position);
        holder.tvCategoria.setText(categoria);

        // Clique longo
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(categoria);
                return true;  // consome o evento
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return categorias != null ? categorias.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoria;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoria = itemView.findViewById(R.id.tvCategoria); // confere seu item_categoria.xml
        }
    }
}
