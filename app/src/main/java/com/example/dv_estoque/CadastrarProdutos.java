package com.example.dv_estoque;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.text.Editable;
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
    private TextView textCad, textEdit;
    private Button btnSalvar, btnAtualizar, btnGaleria, btnCamera;
    private Spinner spinnerCategoria;
    private DataBase db;
    private ProdutoDAO dao;

    private static final int PICK_IMAGE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int PERMISSION_REQUEST_CODE = 200;

    // Para guardar categorias e seus IDs
    private List<String> categoriasNome;
    private List<Integer> categoriasId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadastrar_produtos, container, false);

        // Verificação de permissão para Android 6.0+ (Marshmallow)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Verifica se devemos mostrar a explicação
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explica ao usuário porque a permissão é necessária
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Permissão necessária")
                            .setMessage("Esta permissão é necessária para acessar fotos da galeria")
                            .setPositiveButton("OK", (dialog, which) -> requestPermissions(
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    PERMISSION_REQUEST_CODE))
                            .setNegativeButton("Cancelar", null)
                            .create()
                            .show();
                } else {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                }
            }
        }
        // Inicializa componentes
        proNome = view.findViewById(R.id.proCadNome);
        proImagem = view.findViewById(R.id.proImagemView);
        proquantidade = view.findViewById(R.id.proCadQuantidade);
        proPreco = view.findViewById(R.id.proCadPreco);
        spinnerCategoria = view.findViewById(R.id.proCadCategoria);
        ///
        btnSalvar = view.findViewById(R.id.buttonSalvarProduto);
        btnAtualizar = view.findViewById(R.id.buttonAtualizarProduto);
        textCad = view.findViewById(R.id.textViewCadastro);
        textEdit = view.findViewById(R.id.textViewAtualizar);
        ///
        btnCamera = view.findViewById(R.id.buttonCamera);
        btnGaleria = view.findViewById(R.id.buttonGaleria);

        db = new DataBase(requireContext());
        dao = new ProdutoDAO(requireContext());


        // Configura o campo de preço para aceitar vírgulas
        configurarCampoPreco();

        carregarCategoriasNoSpinner();
        configurarListeners();
        verificarModoEdicao();

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permissão concedida", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Permissão negada", Toast.LENGTH_SHORT).show();
            }
        }
        // Adicionar tratamento para permissão da câmera
        else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamera(); // Tenta abrir novamente após permissão concedida
            } else {
                Toast.makeText(requireContext(), "Permissão da câmera negada", Toast.LENGTH_SHORT).show();
            }
        }
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
        btnCamera.setOnClickListener(v -> abrirCamera());
        ///
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
            //limpa o campo de quantidade
            proquantidade.setText("0");

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
            textCad.setVisibility(View.GONE);
            btnAtualizar.setVisibility(View.VISIBLE);
            textEdit.setVisibility(View.VISIBLE);

        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }
    private void abrirCamera() {
        // Verificar se a permissão da câmera foi concedida
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Solicitar permissão se não tiver
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_IMAGE_CAPTURE
            );
        } else {
            // Permissão já concedida, abrir câmera
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(
                        requireContext(),
                        "Nenhum aplicativo de câmera encontrado",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE && data != null) {
                // Resultado da galeria
                Uri imageUri = data.getData();
                proImagem.setImageURI(imageUri);
            }
            else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                // Resultado da câmera
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                proImagem.setImageBitmap(imageBitmap);
            }
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
            int quantidade = Integer.parseInt(quantidadeStr);
            if (quantidade < 0) {
                Toast.makeText(getContext(), "Quantidade não pode ser negativa", Toast.LENGTH_SHORT).show();
                return;
            }

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

            // Verificar se já existe produto com o mesmo nome
            SQLiteDatabase leitura = db.getReadableDatabase();
            Cursor cursor = leitura.rawQuery("SELECT proId FROM produtos WHERE LOWER(proNome) = ?", new String[]{nome.toLowerCase()});
            if (cursor.moveToFirst()) {
                cursor.close();
                leitura.close();
                Toast.makeText(getContext(), "Já existe um produto com esse nome", Toast.LENGTH_SHORT).show();
                return;
            }
            cursor.close();
            leitura.close();

            // Preparar valores
            SQLiteDatabase escrita = db.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put("proNome", nome);
            valores.put("proQtddeTotal", quantidade);
            valores.put("proPreco", preco);
            valores.put("catId", categoriasId.get(posCategoria));

            if (proImagem.getDrawable() != null &&
                    !((BitmapDrawable)proImagem.getDrawable()).getBitmap().sameAs(
                            BitmapFactory.decodeResource(getResources(), R.drawable.ic_image))) {
                valores.put("proImg", ImageViewToByte(proImagem));
            }

            long resultado = escrita.insert("produtos", null, valores);
            escrita.close();

            if (resultado != -1) {
                // Acumular na tabela de entradas totais
                long novoProId = resultado;
                double valorTotalEntrada = quantidade * preco;
                new ProdutoDAO(requireContext())
                        .acumularEntradaTotal((int) novoProId, quantidade, valorTotalEntrada);

                Toast.makeText(getContext(), "Produto cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                limparCampos();
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).recarregarListaProdutos();
                }
            } else {
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

        // Validações (quantidade não é mais obrigatória)
        if (nome.isEmpty() || precoStr.isEmpty()) { // Removida verificação de quantidade
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
            int quantidadeAdicional = quantidadeStr.isEmpty() ? 0 : Integer.parseInt(quantidadeStr); //valor padrao 0
            double preco = Double.parseDouble(precoStr.replace(",", "."));

            // Busca quantidade ATUAL do produto
            int quantidadeAtual = 0;
            SQLiteDatabase leitura = db.getReadableDatabase();
            Cursor cursor = leitura.query(
                    "produtos",
                    new String[]{"proQtddeTotal"},
                    "proId = ?",
                    new String[]{String.valueOf(proId)},
                    null, null, null
            );

            if (cursor.moveToFirst()) {
                quantidadeAtual = cursor.getInt(cursor.getColumnIndexOrThrow("proQtddeTotal"));
            }
            cursor.close();
            leitura.close();

            // Calcula nova quantidade (mantém atual se não houver alteração)
            int novaQuantidade = quantidadeAtual;
            if (quantidadeAdicional != 0) {
                novaQuantidade = quantidadeAtual + quantidadeAdicional;
                if (novaQuantidade < 0) { // Evita valores negativos
                    Toast.makeText(getContext(), "Quantidade inválida", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            SQLiteDatabase escrita = db.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put("proNome", nome);
            valores.put("proPreco", preco);
            valores.put("catId", catId);

            // Só atualiza quantidade se houver mudança
            if (quantidadeAdicional != 0) {
                valores.put("proQtddeTotal", novaQuantidade);

                // Acumular apenas novas entradas (quantidades positivas)
                if (quantidadeAdicional > 0) {
                    double valorTotalEntrada = quantidadeAdicional * preco;
                    new ProdutoDAO(requireContext())
                            .acumularEntradaTotal(proId, quantidadeAdicional, valorTotalEntrada);
                }
            }

            if (proImagem.getDrawable() != null) {
                valores.put("proImg", ImageViewToByte(proImagem));
            }

            int linhasAfetadas = escrita.update(
                    "produtos",
                    valores,
                    "proId = ?",
                    new String[]{String.valueOf(proId)}
            );
            escrita.close();
            /// ////////

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
        proImagem.setImageResource(R.drawable.ic_image);
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