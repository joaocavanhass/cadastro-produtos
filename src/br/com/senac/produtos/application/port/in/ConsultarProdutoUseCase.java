package br.com.senac.produtos.application.port.in;

import br.com.senac.produtos.domain.model.Produto;
import java.util.List;
import java.util.Optional;

public interface ConsultarProdutoUseCase {
    List<Produto> listarTodos();
    Optional<Produto> buscarPorCodigo(String codigo);
}