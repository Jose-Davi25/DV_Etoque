package com.example.dv_estoque.Models;

public class SaidaTotalModel {
    private int ESbId;
    private int ESproId;
    private String ESNome;
    private int ESQtddeSaidaTotal;
    private Double ESPrecoTotalSaida;

    public SaidaTotalModel(int ESbId, int ESproId, String ESNome,
                           int ESQtddeSaidaTotal, Double ESPrecoTotalSaida) {
        this.ESbId = ESbId;
        this.ESproId = ESproId;
        this.ESNome = ESNome;
        this.ESQtddeSaidaTotal = ESQtddeSaidaTotal;
        this.ESPrecoTotalSaida = ESPrecoTotalSaida;
    }

    public int getESbId() {
        return ESbId;
    }

    public void setESbId(int ESbId) {
        this.ESbId = ESbId;
    }

    public int getESproId() {
        return ESproId;
    }

    public void setESproId(int ESproId) {
        this.ESproId = ESproId;
    }

    public String getESNome() {
        return ESNome;
    }

    public void setESNome(String ESNome) {
        this.ESNome = ESNome;
    }

    public int getESQtddeSaidaTotal() {
        return ESQtddeSaidaTotal;
    }

    public void setESQtddeSaidaTotal(int ESQtddeSaidaTotal) {
        this.ESQtddeSaidaTotal = ESQtddeSaidaTotal;
    }

    public Double getESPrecoTotalSaida() {
        return ESPrecoTotalSaida;
    }

    public void setESPrecoTotalSaida(Double ESPrecoTotalSaida) {
        this.ESPrecoTotalSaida = ESPrecoTotalSaida;
    }
}