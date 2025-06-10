package com.example.dv_estoque.Models;

public class EntradaTotalModel {
    //variaveis
    private int entrId;
    private int entrProId;
    private String entrNome;
    private int entrQtddeTotal;
    private Double entrPrecoTotal;

    public EntradaTotalModel(int entrId, int entrProId, String entrNome, int entrQtddeTotal, Double entrPrecoTotal) {
        this.entrId = entrId;
        this.entrProId = entrProId;
        this.entrNome = entrNome;
        this.entrQtddeTotal = entrQtddeTotal;
        this.entrPrecoTotal = entrPrecoTotal;
    }

    public int getEntrId() {
        return entrId;
    }

    public void setEntrId(int entrId) {
        this.entrId = entrId;
    }

    public int getEntrProId() {
        return entrProId;
    }

    public void setEntrProId(int entrProId) {
        this.entrProId = entrProId;
    }

    public String getEntrNome() {
        return entrNome;
    }

    public void setEntrNome(String entrNome) {
        this.entrNome = entrNome;
    }

    public int getEntrQtddeTotal() {
        return entrQtddeTotal;
    }

    public void setEntrQtddeTotal(int entrQtddeTotal) {
        this.entrQtddeTotal = entrQtddeTotal;
    }

    public Double getEntrPrecoTotal() {
        return entrPrecoTotal;
    }

    public void setEntrPrecoTotal(Double entrPrecoTotal) {
        this.entrPrecoTotal = entrPrecoTotal;
    }
}
