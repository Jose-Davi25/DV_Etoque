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

/**
 * Adapter para exibição de itens de saída em RecyclerView
 * Gerencia a criação e atualização dos elementos da lista
 */
public class SaidaAdapter extends RecyclerView.Adapter<SaidaAdapter.ViewHolder> {

    // Interface para comunicação de eventos
    public interface OnItemClickListener {
        void onItemDeleted(int position);  // Quando um item é excluído
        void onClearAll();                 // Quando a lista é toda limpa
    }

    private OnItemClickListener listener;  // Ouvinte de eventos
    private final List<SaidaModel> lista;  // Fonte de dados
    private final Context context;         // Contexto da aplicação

    // Construtor inicializa com lista segura
    public SaidaAdapter(Context context, List<SaidaModel> lista) {
        this.context = context;
        this.lista = new ArrayList<>(lista != null ? lista : new ArrayList<>());
    }

    // Configura o ouvinte de eventos
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Holder para cache de views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvNome, tvQtd, tvPreco, tvData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tv_nome);
            tvQtd = itemView.findViewById(R.id.tv_qtd);
            tvPreco = itemView.findViewById(R.id.tv_preco);
            tvData = itemView.findViewById(R.id.tv_data);
        }
    }

    // Cria novas views
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tabela_saida, parent, false);
        return new ViewHolder(view);
    }

    // Preenche os dados nas views
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SaidaModel saida = lista.get(position);

        // Formatação dos dados
        holder.tvNome.setText(context.getString(R.string.label_produto, saida.getProNome()));
        holder.tvQtd.setText(context.getString(R.string.label_quantidade, saida.getQuantidadeVendida()));
        holder.tvPreco.setText(context.getString(R.string.label_total,
                String.format(Locale.getDefault(), "%.2f", saida.getPrecoTotal())));
        holder.tvData.setText(context.getString(R.string.label_data, saida.getDataSaida()));

        // Clique longo para exclusão
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener.onItemDeleted(position);
                return true;
            }
            return false;
        });
    }

    // Atualiza toda a lista
    public void atualizarLista(List<SaidaModel> novosDados) {
        lista.clear();
        lista.addAll(novosDados);
        notifyDataSetChanged();
    }

    // Remove item específico
    public void removerItem(int position) {
        if (position >= 0 && position < lista.size()) {
            lista.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Limpa toda a lista
    public void limparLista() {
        lista.clear();
        notifyDataSetChanged();
    }

    // Total de itens
    @Override
    public int getItemCount() {
        return lista.size();
    }

    // Obtém item por posição
    public SaidaModel getItem(int position) {
        return lista.get(position);
    }
}