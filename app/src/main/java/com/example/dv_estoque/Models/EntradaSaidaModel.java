package com.example.dv_estoque.Models;

public class EntradaSaidaModel {
    // vaeiaveis
    private int ESbId;
    private String ESNome;
    private int ESQtddeEntrada;
    private int ESQtddeSaida;
    private Double ESPrecoSaida;

    public EntradaSaidaModel(int ESbId, String ESNome, int ESQtddeEntrada, int ESQtddeSaida, Double ESPrecoSaida) {
        this.ESbId = ESbId;
        this.ESNome = ESNome;
        this.ESQtddeEntrada = ESQtddeEntrada;
        this.ESQtddeSaida = ESQtddeSaida;
        this.ESPrecoSaida = ESPrecoSaida;
    }

    public int getESbId() {
        return ESbId;
    }

    public void setESbId(int ESbId) {
        this.ESbId = ESbId;
    }

    public String getESNome() {
        return ESNome;
    }

    public void setESNome(String ESNome) {
        this.ESNome = ESNome;
    }

    public int getESQtddeEntrada() {
        return ESQtddeEntrada;
    }

    public void setESQtddeEntrada(int ESQtddeEntrada) {
        this.ESQtddeEntrada = ESQtddeEntrada;
    }

    public int getESQtddeSaida() {
        return ESQtddeSaida;
    }

    public void setESQtddeSaida(int ESQtddeSaida) {
        this.ESQtddeSaida = ESQtddeSaida;
    }

    public Double getESPrecoSaida() {
        return ESPrecoSaida;
    }

    public void setESPrecoSaida(Double ESPrecoSaida) {
        this.ESPrecoSaida = ESPrecoSaida;
    }
}