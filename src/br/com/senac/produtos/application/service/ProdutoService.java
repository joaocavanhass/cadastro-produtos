package br.com.senac.produtos.application.service;

import br.com.senac.produtos.application.port.in.AplicarDescontoUseCase;
import br.com.senac.produtos.application.port.in.CadastrarProdutoUseCase;
import br.com.senac.produtos.application.port.in.ConsultarProdutoUseCase;
import br.com.senac.produtos.application.port.out.ProdutoRepositoryPort;
import br.com.senac.produtos.domain.exception.RegraNegocioException;
import br.com.senac.produtos.domain.model.Produto;

import java.util.List;
import java.util.Optional;

public class ProdutoService implements CadastrarProdutoUseCase, ConsultarProdutoUseCase, AplicarDescontoUseCase {

    private final ProdutoRepositoryPort repository;

    // Injeção de dependência via construtor: depende apenas da INTERFACE (Porta de Saída)
    public ProdutoService(ProdutoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Produto executar(String codigo, String nome, double preco, int quantidadeEstoque) {
        String codigoNormalizado = codigo != null ? codigo.trim().toUpperCase() : "";

        if (repository.existePorCodigo(codigoNormalizado)) {
            throw new RegraNegocioException("Já existe um produto cadastrado com o código: " + codigoNormalizado);
        }

        // A entidade Produto valida suas próprias regras de negócio no construtor
        Produto novoProduto = new Produto(codigoNormalizado, nome, preco, quantidadeEstoque);
        repository.salvar(novoProduto);
        return novoProduto;
    }

    @Override
    public List<Produto> listarTodos() {
        return repository.listarTodos();
    }

    @Override
    public Optional<Produto> buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return Optional.empty();
        }
        return repository.buscarPorCodigo(codigo.trim().toUpperCase());
    }

    @Override
    public Produto aplicar(String codigoProduto, double percentualDesconto) {
        Produto produto = buscarPorCodigo(codigoProduto)
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado para o código: " + codigoProduto));

        // Delega a regra de negócio de desconto para a entidade de domínio
        produto.aplicarDesconto(percentualDesconto);
        repository.salvar(produto);
        return produto;
    }
}