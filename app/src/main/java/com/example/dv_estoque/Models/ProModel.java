package com.example.dv_estoque.Models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

/**
 * Classe modelo que representa um Produto no sistema.
 * Armazena as informações essenciais de um produto como ID, imagem, nome, quantidade e preço.
 * Implementa Parcelable para permitir passagem entre activities/fragments.
 */
public class ProModel implements Parcelable {
    private int proId;
    private byte[] proImagem;
    private String proNome;
    private int proQuantidade;
    private Double proPreco;

    // Construtor principal
    public ProModel(int proId, byte[] proImagem, String proNome, int proQuantidade, Double proPreco) {
        setProId(proId);
        setProImagem(proImagem);
        setProNome(proNome);
        setProQuantidade(proQuantidade);
        setProPreco(proPreco);
    }

    // Construtor vazio (para frameworks que necessitam)
    public ProModel() {
        this.proImagem = new byte[0]; // Inicializa com array vazio
    }

    // Getters e Setters com validações
    public int getProId() {
        return proId;
    }

    public void setProId(int proId) {
        if (proId < 0) {
            throw new IllegalArgumentException("ID do produto não pode ser negativo");
        }
        this.proId = proId;
    }

    public byte[] getProImagem() {
        return proImagem != null ? proImagem : new byte[0];
    }

    public void setProImagem(byte[] proImagem) {
        this.proImagem = proImagem != null ? proImagem : new byte[0];
    }

    public String getProNome() {
        return proNome != null ? proNome : "";
    }

    public void setProNome(String proNome) {
        if (proNome == null || proNome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto não pode ser vazio");
        }
        this.proNome = proNome.trim();
    }

    public int getProQuantidade() {
        return proQuantidade;
    }

    public void setProQuantidade(int proQuantidade) {
        if (proQuantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }
        this.proQuantidade = proQuantidade;
    }

    public Double getProPreco() {
        return proPreco != null ? proPreco : 0.0;
    }

    public void setProPreco(Double proPreco) {
        if (proPreco == null || proPreco < 0) {
            throw new IllegalArgumentException("Preço deve ser um valor positivo");
        }
        this.proPreco = proPreco;
    }

    // Métodos auxiliares
    public boolean temImagem() {
        return proImagem != null && proImagem.length > 0;
    }

    // Implementação Parcelable
    protected ProModel(Parcel in) {
        proId = in.readInt();
        proImagem = new byte[in.readInt()];
        in.readByteArray(proImagem);
        proNome = in.readString();
        proQuantidade = in.readInt();
        proPreco = in.readDouble();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(proId);
        dest.writeInt(proImagem != null ? proImagem.length : 0);
        if (proImagem != null) {
            dest.writeByteArray(proImagem);
        }
        dest.writeString(proNome);
        dest.writeInt(proQuantidade);
        dest.writeDouble(proPreco != null ? proPreco : 0.0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProModel> CREATOR = new Creator<ProModel>() {
        @Override
        public ProModel createFromParcel(Parcel in) {
            return new ProModel(in);
        }

        @Override
        public ProModel[] newArray(int size) {
            return new ProModel[size];
        }
    };

    // Método toString para logging/debug
    @Override
    public String toString() {
        return "ProModel{" +
                "proId=" + proId +
                ", proNome='" + proNome + '\'' +
                ", proQuantidade=" + proQuantidade +
                ", proPreco=" + proPreco +
                '}';
    }
}