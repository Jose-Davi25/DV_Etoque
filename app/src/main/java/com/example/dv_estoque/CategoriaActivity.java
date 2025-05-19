package com.example.dv_estoque;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dv_estoque.Adapters.CategoriaAdapter;
import com.example.dv_estoque.DataBase.DataBase;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

public class CategoriaActivity extends AppCompatActivity {

    private RecyclerView recyclerCategorias;
    private CategoriaAdapter adapter;
    private List<String> listaCategorias;
    private CategoriaAdapter.OnItemClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categoria);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerCategorias = findViewById(R.id.recyclerCategorias);
        recyclerCategorias.setLayoutManager(new LinearLayoutManager(this));

        listaCategorias = buscarCategorias();

        listener = new CategoriaAdapter.OnItemClickListener() {
            @Override
            public void onItemLongClick(String categoria) {
                new AlertDialog.Builder(CategoriaActivity.this)
                        .setTitle("Excluir categoria")
                        .setMessage("Tem certeza que deseja excluir a categoria \"" + categoria + "\"?")
                        .setPositiveButton("Sim", (dialog, which) -> {
                            excluirCategoria(categoria);
                            listaCategorias.clear();
                            listaCategorias.addAll(buscarCategorias());
                            adapter.notifyDataSetChanged();
                            Toast.makeText(CategoriaActivity.this, "Categoria excluída", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        };


        adapter = new CategoriaAdapter(listaCategorias, listener);
        recyclerCategorias.setAdapter(adapter);

        Button btnNovaCategoria = findViewById(R.id.btnNovaCategoria);
        btnNovaCategoria.setOnClickListener(v -> {
            Intent intent = new Intent(CategoriaActivity.this, CadastroCategoriaActivity.class);
            startActivity(intent);
        });
    }

    private List<String> buscarCategorias() {
        List<String> categorias = new ArrayList<>();
        SQLiteDatabase db = new DataBase(this).getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT catNome FROM categorias ORDER BY catNome ASC", null);
        if (cursor.moveToFirst()) {
            do {
                categorias.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categorias;
    }
    private void excluirCategoria(String nomeCategoria) {
        SQLiteDatabase db = new DataBase(this).getWritableDatabase();
        db.delete("categorias", "catNome = ?", new String[]{nomeCategoria});
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Atualizar lista caso tenha inserido categoria nova
        listaCategorias.clear();
        listaCategorias.addAll(buscarCategorias());
        adapter.notifyDataSetChanged();
    }
}
