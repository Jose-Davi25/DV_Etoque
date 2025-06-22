package com.example.dv_estoque;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dv_estoque.Adapters.EntradaTotalAdapter;
import com.example.dv_estoque.DataBase.ProdutoDAO;
import com.example.dv_estoque.Models.EntradaTotalModel;
import com.example.dv_estoque.Models.SaidaTotalModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class EntradaTotalActivity extends AppCompatActivity {

    private static final Logger log = LogManager.getLogger(EntradaTotalActivity.class);
    private RecyclerView recyclerView;
    private EntradaTotalAdapter entradaAdapter;
    private ProdutoDAO produtoDAO;

    private Button limparTudoEntr, gerarEcxelEntr;
    private TextView orderNameEntr, orderQtddEntr, orderPrecoEntr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrada_total);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> { Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;});
        // INICIALIZA OS COMPONENTES
        recyclerView = findViewById(R.id.recyclerEntradas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // INICIA O BANCO DE DADOS
        produtoDAO = new ProdutoDAO(this);
        // BOTÕES SUPERIORES
        limparTudoEntr = findViewById(R.id.btnLimparEntradasTotais);
        limparTudoEntr.setOnClickListener(v -> limparTodasEntradas());
        // BOTÃO PRA GERAR ARQUIVO ECXEL
        gerarEcxelEntr = findViewById(R.id.btnCriarAquivoEcxelEnt);
        gerarEcxelEntr.setOnClickListener(v -> gerarEcxelEntradas());
        // BOTÕES DE ORDENAÇÃO
        orderNameEntr = findViewById(R.id.listNomeEnt);
        orderQtddEntr = findViewById(R.id.listQtdTotalEnt);
        orderPrecoEntr = findViewById(R.id.listPriceTotalEnt);

        orderNameEntr.setOnClickListener( v -> ordenarPorNomeEntr());
        orderQtddEntr.setOnClickListener( v -> ordenarPorQtdEntr());
        orderPrecoEntr.setOnClickListener( v -> ordenarPorPrecoEntr());
        //
        carregarEntradasTotais();
    }

    private void gerarEcxelEntradas() {

        try {
            // Obter dados do banco de dados
            List<EntradaTotalModel> dados = produtoDAO.obterTodasEntradasTotais();

            // Criar workbook e planilha
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Entradas Totais");

            // Criar cabeçalhos
            String[] headers = {"ID", "ID Produto", "Nome", "Qtd Total", "Preço Total"};
            XSSFRow headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Preencher dados
            int rowNum = 1;
            for (EntradaTotalModel item : dados) {
                XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getEntrId());
                row.createCell(1).setCellValue(item.getEntrProId());
                row.createCell(2).setCellValue(item.getEntrNome());
                row.createCell(3).setCellValue(item.getEntrQtddeTotal());
                row.createCell(4).setCellValue(item.getEntrPrecoTotal());
            }

            // SOLUÇÃO: Definir larguras manuais (remova se não precisar)
            sheet.setColumnWidth(0, 5 * 256);   // 5 caracteres
            sheet.setColumnWidth(1, 10 * 256);  // 10 caracteres
            sheet.setColumnWidth(2, 20 * 256);  // 20 caracteres
            sheet.setColumnWidth(3, 12 * 256);  // 12 caracteres
            sheet.setColumnWidth(4, 15 * 256);  // 15 caracteres

            // Salvar arquivo
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, "entradas_totais.xlsx");

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
                workbook.close();
            }

            // Compartilhar arquivo
            compartilharArquivo(file);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao gerar Excel: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void compartilharArquivo(File file) {
        Uri uri = FileProvider.getUriForFile(
                this,
                getApplicationContext().getPackageName() + ".provider",
                file
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Compartilhar relatório"));

    }

    private void limparTodasEntradas() {
        new AlertDialog.Builder(this)
                .setTitle("Limpar Histórico")
                .setMessage("Deseja apagar todas as entradas?")
                .setPositiveButton("Sim", (d, w) -> {
                    if (produtoDAO.limparTodaEntradasTotais()) {
                        carregarEntradasTotais();
                        Toast.makeText(this, "Histórico limpo!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void carregarEntradasTotais() {
        List<EntradaTotalModel> entradas = produtoDAO.obterTodasEntradasTotais();
        entradaAdapter = new EntradaTotalAdapter(this, R.layout.item_entrada_total, entradas);
        recyclerView.setAdapter(entradaAdapter);
    }

    private void ordenarPorNomeEntr() {
        List<EntradaTotalModel> list1 = produtoDAO.obterTodasEntradasTotaisNome(); // Método alterado
        entradaAdapter.atualizar(list1);
    }

    private void ordenarPorQtdEntr() {
        List<EntradaTotalModel> list2 = produtoDAO.obterTodasEntradasTotaisQtdde(); // Método alterado
        entradaAdapter.atualizar(list2);
    }

    private void ordenarPorPrecoEntr() {
        List<EntradaTotalModel> list3 = produtoDAO.obterTodasEntradasTotaisPreco(); // Método alterado
        entradaAdapter.atualizar(list3);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (produtoDAO != null) {
            produtoDAO.close();
        }
    }
}