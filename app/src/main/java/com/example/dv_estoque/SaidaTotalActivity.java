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

import com.example.dv_estoque.Adapters.SaidaTotalAdapter;
import com.example.dv_estoque.DataBase.ProdutoDAO;
import com.example.dv_estoque.Models.SaidaTotalModel;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SaidaTotalActivity extends AppCompatActivity {

    private RecyclerView recyclerSaidasEntradas;
    private SaidaTotalAdapter entradaSaidaAdapter;
    private ProdutoDAO produtoDAO;
    private Button limparTudoES, bntcriarEcxel;
    private TextView orderName, orderQtd, orderPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saida_total);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;});

        produtoDAO = new ProdutoDAO(this);
        recyclerSaidasEntradas = findViewById(R.id.recyclerSaidasEntradas);
        recyclerSaidasEntradas.setLayoutManager(new LinearLayoutManager(this)); // Adicione esta linha

        // Inicialize o adapter ANTES de carregar os dados
        entradaSaidaAdapter = new SaidaTotalAdapter(this, new ArrayList<>());
        recyclerSaidasEntradas.setAdapter(entradaSaidaAdapter);

        limparTudoES = findViewById(R.id.btnLimparSaidasTotais);
        limparTudoES.setOnClickListener(v -> limpartudo()); // Adicione esta linha

        // BOTÕES DE ORDENAÇÃO
        orderName = findViewById(R.id.listNome);
        orderQtd = findViewById(R.id.listQtdTotal);
        orderPrice = findViewById(R.id.listPriceTotal);

        orderName.setOnClickListener( v -> ordenarPorNome());
        orderQtd.setOnClickListener( v -> ordenarPorQtd());
        orderPrice.setOnClickListener( v -> ordenarPorPreco());

        // BOTÃO PRA GERAR ARQUIVO ECXEL
        bntcriarEcxel = findViewById(R.id.btnCriarAquivoEcxel);
        bntcriarEcxel.setOnClickListener(v -> gerarEcxel());

        carregarDados();
    }

    private void gerarEcxel() {
        try {
            // Obter dados do banco de dados
            List<SaidaTotalModel> dados = produtoDAO.obterTodasSaidasAcumuladas();

            // Criar workbook e planilha
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Saídas Totais");

            // Criar cabeçalhos
            String[] headers = {"ID", "ID Produto", "Nome", "Qtd Total", "Preço Total"};
            XSSFRow headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Preencher dados
            int rowNum = 1;
            for (SaidaTotalModel item : dados) {
                XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getESbId());
                row.createCell(1).setCellValue(item.getESproId());
                row.createCell(2).setCellValue(item.getESNome());
                row.createCell(3).setCellValue(item.getESQtddeSaidaTotal());
                row.createCell(4).setCellValue(item.getESPrecoTotalSaida());
            }

            // SOLUÇÃO: Definir larguras manuais (remova se não precisar)
            sheet.setColumnWidth(0, 5 * 256);   // 5 caracteres
            sheet.setColumnWidth(1, 10 * 256);  // 10 caracteres
            sheet.setColumnWidth(2, 20 * 256);  // 20 caracteres
            sheet.setColumnWidth(3, 12 * 256);  // 12 caracteres
            sheet.setColumnWidth(4, 15 * 256);  // 15 caracteres

            // Salvar arquivo
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, "saidas_totais.xlsx");

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


    private void ordenarPorNome() {
        List<SaidaTotalModel> lista2 = produtoDAO.obterTodasSaidasAcumuladas2(); // Método alterado
        entradaSaidaAdapter.atualizarLista(lista2);
    }

    private void ordenarPorQtd() {
        List<SaidaTotalModel> lista = produtoDAO.obterTodasSaidasAcumuladas(); // Método alterado
        entradaSaidaAdapter.atualizarLista(lista);
    }

    private void ordenarPorPreco() {
        List<SaidaTotalModel> lista3 = produtoDAO.obterTodasSaidasAcumuladas3(); // Método alterado
        entradaSaidaAdapter.atualizarLista(lista3);
    }

    private void carregarDados() {
        List<SaidaTotalModel> lista = produtoDAO.obterTodasSaidasAcumuladas(); // Método alterado
        entradaSaidaAdapter.atualizarLista(lista);
    }

    private void limpartudo() {
        new AlertDialog.Builder(this)
                .setTitle("Limpar Histórico")
                .setMessage("Deseja apagar todas as saídas?")
                .setPositiveButton("Sim", (d, w) -> {
                    if (produtoDAO.limparTodasSaidasTotais()) {
                        carregarDados();
                        Toast.makeText(this, "Histórico limpo!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    //
    @Override
    protected void onDestroy(){
        produtoDAO.close();
        super.onDestroy();
    }
}