package com.example.dv_estoque;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dv_estoque.DataBase.DataBase;

public class CadastroCategoriaActivity extends AppCompatActivity {

    private EditText etNomeCategoria;
    private Button btnSalvarCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_categoria);

        etNomeCategoria = findViewById(R.id.etNomeCategoria);
        btnSalvarCategoria = findViewById(R.id.btnSalvarCategoria);

        btnSalvarCategoria.setOnClickListener(v -> {
            String nome = etNomeCategoria.getText().toString().trim();
            if (nome.isEmpty()) {
                Toast.makeText(this, "Informe o nome da categoria", Toast.LENGTH_SHORT).show();
                return;
            }
            salvarCategoria(nome);
        });
    }

    private void salvarCategoria(String nome) {
        SQLiteDatabase db = new DataBase(this).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("catNome", nome);
        long id = db.insert("categorias", null, cv);
        db.close();

        if (id > 0) {
            Toast.makeText(this, "Categoria cadastrada!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erro ao cadastrar categoria", Toast.LENGTH_SHORT).show();
        }
    }
}
