package com.example.dv_estoque;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

public class CategoriaActivity extends AppCompatActivity {

    private RecyclerView recyclerCategorias;
    private CategoriaAdapter adapter;
    private List<String> listaCategorias;

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
        adapter = new CategoriaAdapter(listaCategorias);
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

    @Override
    protected void onResume() {
        super.onResume();
        // Atualizar lista caso tenha inserido categoria nova
        listaCategorias.clear();
        listaCategorias.addAll(buscarCategorias());
        adapter.notifyDataSetChanged();
    }
}
