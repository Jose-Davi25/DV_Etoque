package com.example.dv_estoque.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dv_estoque.Models.SaidaModel;
import com.example.dv_estoque.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Adapter para exibir dados de saída de produtos em um RecyclerView
public class SaidaAdapter extends RecyclerView.Adapter<SaidaAdapter.ViewHolder> {

    private final List<SaidaModel> lista; // Lista de saídas
    private final Context context; // Contexto da aplicação

    // Construtor do adapter
    public SaidaAdapter(Context context, List<SaidaModel> lista) {
        this.context = context;
        this.lista = lista != null ? lista : new ArrayList<>();
    }

    // Método para atualizar os dados da lista e notificar a RecyclerView
    public void atualizarDados(List<SaidaModel> novaLista) {
        this.lista.clear();
        this.lista.addAll(novaLista != null ? novaLista : new ArrayList<>());
        notifyDataSetChanged(); // Atualiza a exibição
    }

    // ViewHolder que representa cada item da lista
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvNome, tvQtd, tvPreco, tvData; // Componentes do item

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Mapeamento dos componentes no layout do item
            tvNome = itemView.findViewById(R.id.tv_nome);
            tvQtd = itemView.findViewById(R.id.tv_qtd);
            tvPreco = itemView.findViewById(R.id.tv_preco);
            tvData = itemView.findViewById(R.id.tv_data);
        }
    }

    // Criação do layout para cada item
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout item_tabela_saida.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tabela_saida, parent, false);
        return new ViewHolder(view);
    }

    // Liga os dados da lista ao layout do item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Protege contra posições inválidas
        if (position < 0 || position >= lista.size()) return;

        SaidaModel saida = lista.get(position);

        // Define os textos nos TextViews com os dados formatados
        holder.tvNome.setText(context.getString(R.string.label_produto, saida.getProNome()));
        holder.tvQtd.setText(context.getString(R.string.label_quantidade, saida.getQuantidadeVendida()));
        holder.tvPreco.setText(context.getString(R.string.label_total,
                String.format(Locale.getDefault(), "%.2f", saida.getPrecoTotal())));
        holder.tvData.setText(context.getString(R.string.label_data, saida.getDataSaida()));
    }

    // Retorna o total de itens na lista
    @Override
    public int getItemCount() {
        return lista.size();
    }

    // Interface para implementar cliques em itens, se necessário futuramente
    public interface OnItemClickListener {
        void onItemClick(SaidaModel saida);
    }
}
