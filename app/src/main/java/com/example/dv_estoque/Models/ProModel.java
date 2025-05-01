package com.example.dv_estoque.Models;

/**
 * Classe modelo que representa um Produto no sistema.
 * Armazena as informações essenciais de um produto como ID, imagem, nome, quantidade e preço.
 */
public class ProModel {

    // Identificador único do produto (geralmente usado em banco de dados)
    private int proId;
    // Imagem do produto armazenada como um array de bytes (útil para salvar imagens em banco de dados SQLite)
    private byte[] proImagem;
    // Nome do produto
    private String proNome;
    // Quantidade total disponível do produto em estoque
    private int proQuantidade;
    // Preço do produto (pode ter casas decimais, por isso usamos Double)
    private Double proPreco;

    /**
     * Construtor da classe. Inicializa todos os atributos de um produto.
     *
     * @param proId         ID do produto
     * @param proImagem     Imagem do produto em bytes
     * @param proNome       Nome do produto
     * @param proQuantidade Quantidade disponível
     * @param proPreco      Preço do produto
     */
    public ProModel(int proId, byte[] proImagem, String proNome, int proQuantidade, Double proPreco) {
        this.proId = proId;
        this.proImagem = proImagem;
        this.proNome = proNome;
        this.proQuantidade = proQuantidade;
        this.proPreco = proPreco;
    }

    // Métodos getters e setters para acessar e modificar os atributos privados

    public int getProId() {
        return proId;
    }

    public void setProId(int proId) {
        this.proId = proId;
    }

    public byte[] getProImagem() {
        return proImagem;
    }

    public void setProImagem(byte[] proImagem) {
        this.proImagem = proImagem;
    }

    public String getProNome() {
        return proNome;
    }

    public void setProNome(String proNome) {
        this.proNome = proNome;
    }

    public int getProQuantidade() {
        return proQuantidade;
    }

    public void setProQuantidade(int proQuantidade) {
        this.proQuantidade = proQuantidade;
    }

    public Double getProPreco() {
        return proPreco;
    }

    public void setProPreco(Double proPreco) {
        this.proPreco = proPreco;
    }
}
