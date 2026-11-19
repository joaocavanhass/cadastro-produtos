package br.com.senac.produtos.application.port.in;

import br.com.senac.produtos.domain.model.Produto;

public interface AplicarDescontoUseCase {
    Produto aplicar(String codigoProduto, double percentualDesconto);
}
