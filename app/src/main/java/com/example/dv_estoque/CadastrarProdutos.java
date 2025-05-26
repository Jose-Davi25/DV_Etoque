package com.example.dv_estoque;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dv_estoque.DataBase.DataBase;
import com.example.dv_estoque.DataBase.ProdutoDAO;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CadastrarProdutos extends Fragment {

    private int proId = 0;
    private EditText proNome, proquantidade, proPreco;
    private ImageView proImagem;
    private Button btnSalvar, btnAtualizar, btnGaleria;
    private Spinner spinnerCategoria;
    private DataBase db;
    private ProdutoDAO dao;

    private static final int PICK_IMAGE = 100;

    // Para guardar categorias e seus IDs
    private List<String> categoriasNome;
    private List<Integer> categoriasId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadastrar_produtos, container, false);

        // Inicializa componentes
        proNome = view.findViewById(R.id.proCadNome);
        proImagem = view.findViewById(R.id.proImagemView);
        proquantidade = view.findViewById(R.id.proCadQuantidade);
        proPreco = view.findViewById(R.id.proCadPreco);
        btnSalvar = view.findViewById(R.id.buttonSalvarProduto);
        btnAtualizar = view.findViewById(R.id.buttonAtualizarProduto);
        btnGaleria = view.findViewById(R.id.buttonGaleria);
        spinnerCategoria = view.findViewById(R.id.proCadCategoria);

        db = new DataBase(requireContext());
        dao = new ProdutoDAO(requireContext());


        // Configura o campo de preço para aceitar vírgulas
        configurarCampoPreco();

        carregarCategoriasNoSpinner();
        configurarListeners();
        verificarModoEdicao();

        return view;
    }

    private void configurarCampoPreco() {
        proPreco.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Apenas validação básica
                String currentText = s.toString();

                // Verifica se há mais de uma vírgula
                if (currentText.replaceAll("[^,]", "").length() > 1) {
                    // Remove vírgula extra
                    String newText = currentText.substring(0, currentText.length() - 1);
                    proPreco.setText(newText);
                    proPreco.setSelection(newText.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void carregarCategoriasNoSpinner() {
        categoriasNome = new ArrayList<>();
        categoriasId = new ArrayList<>();

        // Adiciona o hint como primeiro item
        categoriasNome.add("Selecione uma categoria");
        categoriasId.add(-1);

        SQLiteDatabase leitura = db.getReadableDatabase();
        Cursor cursor = leitura.rawQuery("SELECT catId, catNome FROM categorias ORDER BY catNome", null);

        if (cursor.moveToFirst()) {
            do {
                categoriasId.add(cursor.getInt(0));
                categoriasNome.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        leitura.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoriasNome);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);

        // Personaliza a cor do hint
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    TextView textView = (TextView) view;
                    if (position == 0) {
                        textView.setTextColor(Color.GRAY);
                        textView.setTextSize(16f);
                    } else {
                        textView.setTextColor(Color.BLACK);
                        textView.setTextSize(16f);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void configurarListeners() {
        btnGaleria.setOnClickListener(v -> abrirGaleria());
        btnSalvar.setOnClickListener(v -> salvarProduto());
        btnAtualizar.setOnClickListener(v -> atualizarProduto());
    }

    private void verificarModoEdicao() {
        if (getArguments() != null) {
            proId = getArguments().getInt("proId");
            byte[] imagem = getArguments().getByteArray("proImg");

            if (imagem != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imagem, 0, imagem.length);
                proImagem.setImageBitmap(bitmap);
            }

            proNome.setText(getArguments().getString("proNome"));
            proquantidade.setText(String.valueOf(getArguments().getInt("proQtddeTotal")));

            // Formata o preço com vírgula para exibição
            double preco = getArguments().getDouble("proPreco");
            proPreco.setText(String.format(Locale.getDefault(), "%.2f", preco).replace(".", ","));

            // Selecionar categoria no spinner
            int catIdSelecionado = getArguments().getInt("catId", -1);
            if (catIdSelecionado != -1) {
                int pos = categoriasId.indexOf(catIdSelecionado);
                if (pos != -1) {
                    spinnerCategoria.setSelection(pos);
                }
            }

            btnSalvar.setVisibility(View.GONE);
            btnAtualizar.setVisibility(View.VISIBLE);
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            Uri imageUri = data.getData();
            proImagem.setImageURI(imageUri);
        }
    }

    private void salvarProduto() {
        String nome = proNome.getText().toString().trim();
        String quantidadeStr = proquantidade.getText().toString().trim();
        String precoStr = proPreco.getText().toString().trim();

        // Validação dos campos obrigatórios
        if (nome.isEmpty() || quantidadeStr.isEmpty() || precoStr.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validação da categoria
        int posCategoria = spinnerCategoria.getSelectedItemPosition();
        if (posCategoria <= 0) {
            Toast.makeText(getContext(), "Selecione uma categoria válida", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Validação e conversão da quantidade
            int quantidade = Integer.parseInt(quantidadeStr);
            if (quantidade < 0) {
                Toast.makeText(getContext(), "Quantidade não pode ser negativa", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validação e formatação do preço
            precoStr = precoStr.replace(",", ".");
            if (precoStr.startsWith(".") || precoStr.endsWith(".")) {
                Toast.makeText(getContext(), "Formato de preço inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            double preco = Double.parseDouble(precoStr);
            if (preco <= 0) {
                Toast.makeText(getContext(), "Preço deve ser maior que zero", Toast.LENGTH_SHORT).show();
                return;
            }

            // Preparação dos valores para o banco de dados
            SQLiteDatabase escrita = db.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put("proNome", nome);
            valores.put("proQtddeTotal", quantidade);
            valores.put("proPreco", preco);
            valores.put("catId", categoriasId.get(posCategoria));

            // Tratamento da imagem
            if (proImagem.getDrawable() != null &&
                    !((BitmapDrawable)proImagem.getDrawable()).getBitmap().sameAs(
                            BitmapFactory.decodeResource(getResources(), R.drawable.product))) {
                valores.put("proImg", ImageViewToByte(proImagem));
            }

            // Inserção no banco de dados
            long resultado = escrita.insert("produtos", null, valores);
            escrita.close();
            int novoProId = (int) resultado; // O próprio insert retorna o ID

            if (resultado != -1) {
                Toast.makeText(getContext(), "Produto cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                limparCampos();

                // Fechar a conexão após todas as operações
                escrita.close();

                // Atualizar lista
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).recarregarListaProdutos();
                }
            } else {
                escrita.close();
                Toast.makeText(getContext(), "Falha ao cadastrar produto", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Formato numérico inválido", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void atualizarProduto() {
        String nome = proNome.getText().toString().trim();
        String quantidadeStr = proquantidade.getText().toString().trim();
        String precoStr = proPreco.getText().toString().trim();

        if (nome.isEmpty() || quantidadeStr.isEmpty() || precoStr.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int posCategoria = spinnerCategoria.getSelectedItemPosition();
        if (posCategoria <= 0) {
            Toast.makeText(getContext(), "Selecione uma categoria", Toast.LENGTH_SHORT).show();
            return;
        }
        int catId = categoriasId.get(posCategoria);

        try {
            int quantidade = Integer.parseInt(quantidadeStr);
            double preco = Double.parseDouble(precoStr.replace(",", "."));

            SQLiteDatabase escrita = db.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put("proNome", nome);
            valores.put("proQtddeTotal", quantidade);
            valores.put("proPreco", preco);
            valores.put("catId", catId);

            if (proImagem.getDrawable() != null) {
                valores.put("proImg", ImageViewToByte(proImagem));
            }

            int linhasAfetadas = escrita.update("produtos", valores, "proId = ?", new String[]{String.valueOf(proId)});
            escrita.close();

            if (linhasAfetadas > 0) {
                Toast.makeText(getContext(), "Produto atualizado", Toast.LENGTH_SHORT).show();
                limparCampos();

                if (getActivity() != null) {
                    ((MainActivity) getActivity()).recarregarListaProdutos();
                }

                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                }
            } else {
                Toast.makeText(getContext(), "Nenhum produto atualizado", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Valores inválidos", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Erro ao atualizar produto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void limparCampos() {
        proNome.setText("");
        proquantidade.setText("");
        proPreco.setText("");
        proImagem.setImageResource(R.drawable.product);
        spinnerCategoria.setSelection(0);
        proId = 0;
    }

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