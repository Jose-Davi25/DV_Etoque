package com.example.dv_estoque;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dv_estoque.DataBase.DataBase;

import java.io.ByteArrayOutputStream;

public class CadastrarProdutos extends Fragment {

    // Variáveis para guardar os dados do produto e elementos da interface
    private int proId = 0; // ID do produto, usado para edição
    private EditText proNome, proquantidade, proPreco;
    private ImageView proImagem;
    private Button btnSalvar, btnAtualizar, btnGaleria;
    private DataBase db;
    private SQLiteDatabase conexao;
    private static final int PICK_IMAGE = 100; // Código para requisição de imagem da galeria

    // Método chamado ao criar a visualização do fragmento
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadastrar_produtos, container, false);
        inicializarComponentes(view); // Inicializa os componentes da tela
        configurarListeners();        // Define as ações dos botões
        verificarModoEdicao();        // Verifica se está no modo de edição ou cadastro
        return view;
    }

    // Inicializa os componentes da interface do usuário
    private void inicializarComponentes(View view) {
        proNome = view.findViewById(R.id.proCadNome);
        proImagem = view.findViewById(R.id.proImagemView);
        proquantidade = view.findViewById(R.id.proCadQuantidade);
        proPreco = view.findViewById(R.id.proCadPreco);
        btnSalvar = view.findViewById(R.id.buttonSalvarProduto);
        btnAtualizar = view.findViewById(R.id.buttonAtualizarProduto);
        btnGaleria = view.findViewById(R.id.buttonGaleria);
        db = new DataBase(requireContext()); // Cria instância do banco de dados
    }

    // Configura os cliques dos botões
    private void configurarListeners() {
        btnGaleria.setOnClickListener(v -> abrirGaleria());     // Abre a galeria de imagens
        btnSalvar.setOnClickListener(v -> salvarProduto());     // Salva um novo produto
        btnAtualizar.setOnClickListener(v -> atualizarProduto());// Atualiza um produto existente
    }

    // Verifica se há argumentos passados para saber se está em modo de edição
    private void verificarModoEdicao() {
        if (getArguments() != null) {
            proId = getArguments().getInt("proId");
            byte[] imagem = getArguments().getByteArray("proImg");

            if (imagem != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imagem, 0, imagem.length);
                proImagem.setImageBitmap(bitmap); // Exibe imagem do produto
            }

            // Preenche os campos com os dados recebidos
            proNome.setText(getArguments().getString("proNome"));
            proquantidade.setText(String.valueOf(getArguments().getInt("proQtddeTotal")));
            proPreco.setText(String.valueOf(getArguments().getDouble("proPreco")));

            // Esconde botão de salvar e mostra botão de atualizar
            btnSalvar.setVisibility(View.GONE);
            btnAtualizar.setVisibility(View.VISIBLE);
        }
    }

    // Abre a galeria para o usuário escolher uma imagem
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    // Método chamado quando o usuário escolhe uma imagem da galeria
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            Uri imageUri = data.getData();
            proImagem.setImageURI(imageUri); // Exibe a imagem escolhida
        }
    }

    // Salva os dados do produto no banco de dados
    private void salvarProduto() {
        String nome = proNome.getText().toString().trim();
        String quantidadeStr = proquantidade.getText().toString().trim();
        String precoStr = proPreco.getText().toString().trim();

        // Verifica se todos os campos estão preenchidos
        if (nome.isEmpty() || quantidadeStr.isEmpty() || precoStr.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int quantidade = Integer.parseInt(quantidadeStr);
            double preco = Double.parseDouble(precoStr);

            SQLiteDatabase db = this.db.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put("proNome", nome);
            valores.put("proQtddeTotal", quantidade);
            valores.put("proPreco", preco);

            // Converte imagem para byte array, se houver
            if (proImagem.getDrawable() != null) {
                valores.put("proImg", ImageViewToByte(proImagem));
            }

            // Insere no banco de dados
            long resultado = db.insert("produtos", null, valores);
            db.close();

            // Verifica se inserção foi bem-sucedida
            if (resultado != -1) {
                Toast.makeText(getContext(), "Produto adicionado", Toast.LENGTH_SHORT).show();
                limparCampos(); // Limpa os campos após salvar
            } else {
                Toast.makeText(getContext(), "Erro ao adicionar produto", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Valores inválidos", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Erro ao salvar produto", Toast.LENGTH_SHORT).show();
        }
    }

    // Atualiza os dados do produto existente
    private void atualizarProduto() {
        String nome = proNome.getText().toString().trim();
        String quantidadeStr = proquantidade.getText().toString().trim();
        String precoStr = proPreco.getText().toString().trim();

        if (nome.isEmpty() || quantidadeStr.isEmpty() || precoStr.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int quantidade = Integer.parseInt(quantidadeStr);
            double preco = Double.parseDouble(precoStr);

            SQLiteDatabase db = this.db.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put("proNome", nome);
            valores.put("proQtddeTotal", quantidade);
            valores.put("proPreco", preco);

            if (proImagem.getDrawable() != null) {
                valores.put("proImg", ImageViewToByte(proImagem));
            }

            // Atualiza a linha com base no ID do produto
            int linhasAfetadas = db.update("produtos", valores, "proId = ?", new String[]{String.valueOf(proId)});
            db.close();

            if (linhasAfetadas > 0) {
                Toast.makeText(getContext(), "Produto atualizado", Toast.LENGTH_SHORT).show();
                limparCampos(); // Limpa os campos após atualizar

                // Atualiza a lista de produtos no fragment principal
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).recarregarListaProdutos();
                }

                // Volta para o fragmento anterior
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                }
            } else {
                Toast.makeText(getContext(), "Nenhum produto atualizado", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Valores inválidos", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Erro ao atualizar", Toast.LENGTH_SHORT).show();
        }
    }

    // Limpa todos os campos do formulário e reseta imagem
    private void limparCampos() {
        proNome.setText("");
        proquantidade.setText("");
        proPreco.setText("");
        proImagem.setImageResource(R.drawable.product); // Define imagem padrão
        proId = 0;
    }

    // Converte imagem do ImageView em array de bytes (para salvar no banco de dados)
    private byte[] ImageViewToByte(ImageView produtoImagem) {
        try {
            Bitmap bitmap = ((BitmapDrawable) produtoImagem.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}
