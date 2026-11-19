package br.com.senac.produtos.application.port.in;

import br.com.senac.produtos.domain.model.Produto;

public interface CadastrarProdutoUseCase {
    Produto executar(String codigo, String nome, double preco, int quantidadeEstoque);
}