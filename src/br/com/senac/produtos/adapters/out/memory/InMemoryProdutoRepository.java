package br.com.senac.produtos.adapters.out.memory;

import br.com.senac.produtos.application.port.out.ProdutoRepositoryPort;
import br.com.senac.produtos.domain.model.Produto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryProdutoRepository implements ProdutoRepositoryPort {

    private final Map<String, Produto> bancoEmMemoria = new LinkedHashMap<>();

    @Override
    public void salvar(Produto produto) {
        bancoEmMemoria.put(produto.getCodigo(), produto);
    }

    @Override
    public Optional<Produto> buscarPorCodigo(String codigo) {
        return Optional.ofNullable(bancoEmMemoria.get(codigo.toUpperCase()));
    }

    @Override
    public List<Produto> listarTodos() {
        return new ArrayList<>(bancoEmMemoria.values());
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return bancoEmMemoria.containsKey(codigo.toUpperCase());
    }
}
