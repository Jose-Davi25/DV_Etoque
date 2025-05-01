package com.example.dv_estoque.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dv_estoque.CadastrarProdutos;
import com.example.dv_estoque.Models.ProModel;
import com.example.dv_estoque.R;
import java.util.List;
import java.util.Locale;

public class ProAdapter extends RecyclerView.Adapter<ProAdapter.ViewHolder> {

    private final Context context;
    private final int itemLayout;
    private List<ProModel> modelArrayList;
    private final OnProActionListener listener;

    // Interface para comunicação com o fragmento pai (remover ou editar produto)
    public interface OnProActionListener {
        void onProUpdate();
        void onProDeleted(int proId);
    }

    // Construtor do adapter
    public ProAdapter(Context context, int itemLayout, List<ProModel> modelArrayList, OnProActionListener listener) {
        this.context = context;
        this.itemLayout = itemLayout;
        this.modelArrayList = modelArrayList;
        this.listener = listener;
    }

    // Atualiza a lista de produtos e notifica a RecyclerView
    public void atualizarLista(List<ProModel> novaLista) {
        this.modelArrayList = novaLista;
        notifyDataSetChanged();
    }

    // Cria as views de item do RecyclerView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(itemLayout, parent, false);
        return new ViewHolder(view);
    }

    // Associa os dados à ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProModel proModel = modelArrayList.get(position);
        holder.bind(proModel, position);
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    // ViewHolder interno que representa cada item da lista
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView proImagem;
        TextView proNome, proQuantidade, proPreco;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            proImagem = itemView.findViewById(R.id.imagemProduto);
            proNome = itemView.findViewById(R.id.nomeProduto);
            proQuantidade = itemView.findViewById(R.id.quantidadeProduto);
            proPreco = itemView.findViewById(R.id.precoproduto);
        }

        // Associa os dados do ProModel aos componentes visuais
        public void bind(ProModel proModel, int position) {
            try {
                if (proModel.getProImagem() != null && proModel.getProImagem().length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(
                            proModel.getProImagem(), 0, proModel.getProImagem().length);
                    proImagem.setImageBitmap(bitmap);
                } else {
                    proImagem.setImageResource(R.drawable.product); // Imagem padrão
                }
            } catch (Exception e) {
                proImagem.setImageResource(R.drawable.product);
            }

            proNome.setText(proModel.getProNome());
            proQuantidade.setText(String.valueOf(proModel.getProQuantidade()));
            proPreco.setText(String.format(Locale.getDefault(), "R$ %.2f", proModel.getProPreco()));

            // Ao pressionar e segurar o item, mostra menu com opções
            itemView.setOnLongClickListener(v -> {
                showContextMenu(v, proModel);
                return true;
            });
        }

        // Exibe o menu de contexto (editar/deletar)
        private void showContextMenu(View anchorView, ProModel proModel) {
            PopupMenu popupMenu = new PopupMenu(context, anchorView);
            popupMenu.inflate(R.menu.menu); // menu.xml com opções de editar e excluir

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.update) {
                    abrirModoEdicao(proModel);
                    return true;
                } else if (item.getItemId() == R.id.delete) {
                    if (listener != null) {
                        listener.onProDeleted(proModel.getProId());
                    }
                    return true;
                }
                return false;
            });

            popupMenu.show();
        }

        // Abre o fragmento de edição e envia os dados do produto via Bundle
        private void abrirModoEdicao(ProModel proModel) {
            CadastrarProdutos fragment = new CadastrarProdutos();
            Bundle args = new Bundle();
            args.putInt("proId", proModel.getProId());
            args.putByteArray("proImg", proModel.getProImagem());
            args.putString("proNome", proModel.getProNome());
            args.putInt("proQtddeTotal", proModel.getProQuantidade());
            args.putDouble("proPreco", proModel.getProPreco());
            fragment.setArguments(args);

            if (context instanceof FragmentActivity) {
                ((FragmentActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerPrincipal, fragment)
                        .addToBackStack("edicao") // Adiciona à pilha de navegação
                        .commit();
            }
        }
    }
}
