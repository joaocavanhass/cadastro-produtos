package br.com.senac.produtos.application.port.out;

import br.com.senac.produtos.domain.model.Produto;
import java.util.List;
import java.util.Optional;

public interface ProdutoRepositoryPort {
    void salvar(Produto produto);
    Optional<Produto> buscarPorCodigo(String codigo);
    List<Produto> listarTodos();
    boolean existePorCodigo(String codigo);
}