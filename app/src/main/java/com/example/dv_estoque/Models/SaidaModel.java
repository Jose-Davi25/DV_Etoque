package com.example.dv_estoque.Models;

public class SaidaModel {
    private int saidaId;
    private String proNome;
    private int quantidadeVendida;
    private double precoTotal;
    private String dataSaida;

    public SaidaModel(int saidaId, String proNome, int quantidadeVendida, double precoTotal, String dataSaida) {
        this.saidaId = saidaId;
        this.proNome = proNome;
        this.quantidadeVendida = quantidadeVendida;
        this.precoTotal = precoTotal;
        this.dataSaida = dataSaida;
    }

    public int getSaidaId() {
        return saidaId;
    }

    public String getProNome() {
        return proNome;
    }

    public int getQuantidadeVendida() {
        return quantidadeVendida;
    }

    public double getPrecoTotal() {
        return precoTotal;
    }

    public String getDataSaida() {
        return dataSaida;
    }
}
