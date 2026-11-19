package br.com.senac.produtos.domain.model;

import br.com.senac.produtos.domain.exception.RegraNegocioException;

public class Produto {

    private final String codigo;
    private String nome;
    private double preco;
    private int quantidadeEstoque;

    public Produto(String codigo, String nome, double preco, int quantidadeEstoque) {
        validar(codigo, nome, preco, quantidadeEstoque);
        this.codigo = codigo.trim().toUpperCase();
        this.nome = nome.trim();
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    private void validar(String codigo, String nome, double preco, int quantidadeEstoque) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new RegraNegocioException("O código do produto é obrigatório.");
        }
        if (nome == null || nome.trim().length() < 2) {
            throw new RegraNegocioException("O nome do produto deve ter pelo menos 2 caracteres.");
        }
        if (preco <= 0) {
            throw new RegraNegocioException("O preço do produto deve ser maior que zero.");
        }
        if (quantidadeEstoque < 0) {
            throw new RegraNegocioException("A quantidade em estoque não pode ser negativa.");
        }
    }

    public void aplicarDesconto(double percentual) {
        if (percentual <= 0 || percentual > 50) {
            throw new RegraNegocioException("O desconto deve estar entre 1% e 50%.");
        }
        this.preco = this.preco * (1 - (percentual / 100.0));
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - R$ %.2f (Estoque: %d)", codigo, nome, preco, quantidadeEstoque);
    }
}