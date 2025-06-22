package com.example.dv_estoque.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dv_estoque.CadastrarProdutos;
import com.example.dv_estoque.DataBase.ProdutoDAO;
import com.example.dv_estoque.Models.ProModel;
import com.example.dv_estoque.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProAdapter extends RecyclerView.Adapter<ProAdapter.ViewHolder> {

    private final Context context;
    private final int itemLayout;
    private List<ProModel> modelArrayList;
    private final OnProActionListener listener;

    // Interface para ações de atualização e exclusão
    public interface OnProActionListener {
        void onProUpdate();
        void onProDeleted(int proId);
    }

    public ProAdapter(Context context, int itemLayout, List<ProModel> modelArrayList, OnProActionListener listener) {
        this.context = context;
        this.itemLayout = itemLayout;
        this.modelArrayList = modelArrayList != null ? modelArrayList : new ArrayList<>();
        this.listener = listener;
    }

    // Atualiza a lista de produtos
    public void atualizarLista(List<ProModel> novaLista) {
        this.modelArrayList = novaLista != null ? novaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do item
        View view = LayoutInflater.from(context).inflate(itemLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Protege contra índices inválidos
        if (position < 0 || position >= modelArrayList.size()) return;

        ProModel proModel = modelArrayList.get(position);
        holder.bind(proModel); // Vincula dados ao item
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size(); // Retorna tamanho da lista
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView proImagem, btnMais, btnMenos;
        TextView proNome, proQuantidade, proPreco, txtQtdSaida, precoSumProduto;
        View btnSaida;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                // Mapeamento dos componentes visuais
                proImagem = itemView.findViewById(R.id.imagemProduto);
                proNome = itemView.findViewById(R.id.nomeProduto);
                proQuantidade = itemView.findViewById(R.id.quantidadeProduto);
                proPreco = itemView.findViewById(R.id.precoproduto);
                btnMais = itemView.findViewById(R.id.btnMais);
                btnMenos = itemView.findViewById(R.id.btnMenos);
                txtQtdSaida = itemView.findViewById(R.id.txtQtdSaida);
                precoSumProduto = itemView.findViewById(R.id.precoSumProduto);
                btnSaida = itemView.findViewById(R.id.btnSaida);

                if (proImagem == null) {
                    Log.e("ProAdapter", "ImageView não encontrada no layout");
                }
            } catch (Exception e) {
                Log.e("ProAdapter", "Erro ao encontrar views", e);
            }
        }

        public void bind(ProModel proModel) {
            // Preenche os dados do produto nas views
            if (proNome != null) {
                proNome.setText(proModel.getProNome());
            }

            if (proQuantidade != null) {
                proQuantidade.setText(String.valueOf(proModel.getProQuantidade()));
            }

            if (proPreco != null) {
                proPreco.setText(String.format(Locale.getDefault(), "R$ %.2f", proModel.getProPreco()));
            }

            if (txtQtdSaida != null) {
                txtQtdSaida.setText("0");
            }

            if (precoSumProduto != null) {
                precoSumProduto.setText("R$ 0.00");
            }

            // Define a imagem do produto ou imagem padrão
            if (proImagem != null) {
                try {
                    if (proModel.getProImagem() != null && proModel.getProImagem().length > 0) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(
                                proModel.getProImagem(), 0, proModel.getProImagem().length);
                        proImagem.setImageBitmap(bitmap);
                    } else {
                        proImagem.setImageResource(R.drawable.ic_image);
                    }
                } catch (Exception e) {
                    proImagem.setImageResource(R.drawable.ic_image);
                    Log.e("ProAdapter", "Erro ao carregar imagem", e);
                }
            }

            // Botão para aumentar quantidade de saída
            if (btnMais != null) {
                btnMais.setOnClickListener(v -> {
                    try {
                        int qtdAtual = Integer.parseInt(txtQtdSaida.getText().toString());
                        int estoque = proModel.getProQuantidade();
                        if (qtdAtual < estoque) {
                            qtdAtual++;
                            txtQtdSaida.setText(String.valueOf(qtdAtual));
                            double precoTotal = qtdAtual * proModel.getProPreco();
                            precoSumProduto.setText(String.format(Locale.getDefault(), "R$ %.2f", precoTotal));
                        }
                    } catch (Exception e) {
                        Log.e("ProAdapter", "Erro ao incrementar quantidade", e);
                    }
                });
            }

            // Botão para diminuir quantidade de saída
            if (btnMenos != null) {
                btnMenos.setOnClickListener(v -> {
                    try {
                        int qtdAtual = Integer.parseInt(txtQtdSaida.getText().toString());
                        if (qtdAtual > 0) {
                            qtdAtual--;
                            txtQtdSaida.setText(String.valueOf(qtdAtual));
                            double precoTotal = qtdAtual * proModel.getProPreco();
                            precoSumProduto.setText(String.format(Locale.getDefault(), "R$ %.2f", precoTotal));
                        }
                    } catch (Exception e) {
                        Log.e("ProAdapter", "Erro ao decrementar quantidade", e);
                    }
                });
            }

            // Botão para registrar a saída
            // Dentro do clique do botão de saída (ProAdapter.java)
            btnSaida.setOnClickListener(v -> {
                try {
                    int qtdSaida = Integer.parseInt(txtQtdSaida.getText().toString());
                    if (qtdSaida > 0 && qtdSaida <= proModel.getProQuantidade()) {
                        ProdutoDAO dao = new ProdutoDAO(context);

                        // 1. Registrar na tabela de saídas detalhadas
                        boolean sucessoSaidaLogica = dao.registrarSaidaProduto(proModel.getProId(), qtdSaida);

                        // 2. Atualizar a tabela consolidada de entradas/saídas
                        boolean sucessoAtualizacao = dao.acumularSaidaTotal(
                                proModel.getProId(),
                                qtdSaida,
                                qtdSaida * proModel.getProPreco()
                        );

                        if (sucessoSaidaLogica && sucessoAtualizacao) {
                            // Atualiza a UI e estoque
                            proModel.setProQuantidade(proModel.getProQuantidade() - qtdSaida);
                            proQuantidade.setText(String.valueOf(proModel.getProQuantidade()));
                            txtQtdSaida.setText("0");
                            precoSumProduto.setText("R$ 0.00");

                            Toast.makeText(context, "Saída de " + qtdSaida + " unidade(s) registrada!", Toast.LENGTH_SHORT).show();
                            notifyItemChanged(getAdapterPosition());

                            if (listener != null) listener.onProUpdate();
                        } else {
                            Toast.makeText(context, "Erro parcial ao registrar saída", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e("ProAdapter", "Erro ao registrar saída", e);
                }
            });

            // Abre menu de contexto ao pressionar item
            itemView.setOnLongClickListener(v -> {
                showContextMenu(v, proModel);
                return true;
            });
        }

        // Menu para editar ou excluir produto
        private void showContextMenu(View anchorView, ProModel proModel) {
            try {
                PopupMenu popupMenu = new PopupMenu(context, anchorView);
                popupMenu.inflate(R.menu.menu);

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.update) {
                        abrirModoEdicao(proModel); // Editar
                        return true;
                    } else if (item.getItemId() == R.id.delete) {
                        if (listener != null) {
                            listener.onProDeleted(proModel.getProId()); // Excluir
                        }
                        return true;
                    }
                    return false;
                });

                popupMenu.show();
            } catch (Exception e) {
                Log.e("ProAdapter", "Erro ao mostrar menu de contexto", e);
            }
        }

        // Abre fragmento para editar produto
        private void abrirModoEdicao(ProModel proModel) {
            try {
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
                            .addToBackStack("edicao")
                            .commit();
                }
            } catch (Exception e) {
                Log.e("ProAdapter", "Erro ao abrir modo de edição", e);
            }
        }
    }
}
