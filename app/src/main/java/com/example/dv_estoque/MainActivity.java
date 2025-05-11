package com.example.dv_estoque;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * Classe principal da aplicação que controla a navegação entre os fragmentos usando o BottomNavigationView.
 */
public class MainActivity extends AppCompatActivity {

    // Área do layout onde os fragmentos serão exibidos
    private FrameLayout frameLayout;

    // Menu de navegação inferior
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Habilita o modo de tela cheia com suporte a bordas (para dispositivos mais novos)
        EdgeToEdge.enable(this);

        // Define o layout principal da atividade
        setContentView(R.layout.activity_main);

        // INICIALIZAÇÃO DAS VIEWS
        frameLayout = findViewById(R.id.FrameLayout); // Frame onde os fragmentos são carregados
        bottomNavigationView = findViewById(R.id.bottom_navigation); // Menu inferior

        // Verifica se a activity está sendo criada pela primeira vez (ou seja, não está restaurando um estado anterior)
        if (savedInstanceState == null) {
            // Exibe o fragmento Inicial por padrão ao abrir o app
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.FrameLayout, new Inicial())
                    .commit();
        }

        // Listener que responde aos cliques nos itens do menu inferior
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                // Pega o ID do item de menu selecionado
                int id = menuItem.getItemId();

                // Verifica qual item foi clicado e troca o fragmento mostrado
                if (id == R.id.btn_nav_initial) {
                    // Fragmento da tela inicial
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.FrameLayout, new Inicial())
                            .commit();

                } else if (id == R.id.btn_nav_list_pro) {
                    // Fragmento com a lista de produtos
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.FrameLayout, new ListaProdutos())
                            .commit();

                } else if (id == R.id.btn_nav_cad_prod) {
                    // Fragmento para cadastrar produtos
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.FrameLayout, new CadastrarProdutos())
                            .commit();

                } else if (id == R.id.btn_nav_table_pro) {
                    // Fragmento com a tabela de produtos (visualização em grade ou formato de tabela)
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.FrameLayout, new TabelaSaidaProdutos())
                            .commit();
                }

                // Retorna true para indicar que o clique foi tratado
                return true;
            }
        });
    }

    /**
     * Método público para recarregar a Lista de Produtos de forma programática.
     * Pode ser chamado de outros fragmentos ou classes para forçar a atualização da tela.
     */
    public void recarregarListaProdutos() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerPrincipal, new ListaProdutos())
                .commit();
    }
}
