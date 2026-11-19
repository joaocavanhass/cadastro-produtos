package test.br.com.senac.produtos;

import br.com.senac.produtos.adapters.out.memory.InMemoryProdutoRepository;
import br.com.senac.produtos.application.service.ProdutoService;
import br.com.senac.produtos.domain.exception.RegraNegocioException;
import br.com.senac.produtos.domain.model.Produto;

public class ProdutoServiceTest {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO TESTES UNITÁRIOS DO NÚCLEO HEXAGONAL ===");

        deveCadastrarProdutoValido();
        naoDevePermitirCodigoDuplicado();
        naoDevePermitirPrecoZeroOuNegativo();
        deveAplicarDescontoValido();
        naoDevePermitirDescontoAcimaDe50PorCento();

        System.out.println("=== TODOS OS 5 TESTES PASSARAM COM SUCESSO! ===");
    }

    private static void deveCadastrarProdutoValido() {
        ProdutoService service = new ProdutoService(new InMemoryProdutoRepository());
        Produto p = service.executar("p001", "Teclado Mecânico", 250.00, 10);

        if (!"P001".equals(p.getCodigo()) || service.listarTodos().size() != 1) {
            throw new AssertionError("Falha ao cadastrar produto válido.");
        }
        System.out.println("[OK] deveCadastrarProdutoValido");
    }

    private static void naoDevePermitirCodigoDuplicado() {
        ProdutoService service = new ProdutoService(new InMemoryProdutoRepository());
        service.executar("P001", "Mouse Gamer", 120.00, 5);

        try {
            service.executar("p001", "Mouse Sem Fio", 150.00, 3);
            throw new AssertionError("Deveria ter lançado RegraNegocioException para código duplicado.");
        } catch (RegraNegocioException e) {
            System.out.println("[OK] naoDevePermitirCodigoDuplicado -> " + e.getMessage());
        }
    }

    private static void naoDevePermitirPrecoZeroOuNegativo() {
        ProdutoService service = new ProdutoService(new InMemoryProdutoRepository());

        try {
            service.executar("P002", "Monitor", -500.00, 2);
            throw new AssertionError("Deveria ter lançado RegraNegocioException para preço negativo.");
        } catch (RegraNegocioException e) {
            System.out.println("[OK] naoDevePermitirPrecoZeroOuNegativo -> " + e.getMessage());
        }
    }

    private static void deveAplicarDescontoValido() {
        ProdutoService service = new ProdutoService(new InMemoryProdutoRepository());
        service.executar("P003", "Headset", 200.00, 8);

        Produto atualizado = service.aplicar("P003", 10.0);
        if (Math.abs(atualizado.getPreco() - 180.00) > 0.001) {
            throw new AssertionError("Preço com desconto incorreto.");
        }
        System.out.println("[OK] deveAplicarDescontoValido -> Novo preço: R$ " + atualizado.getPreco());
    }

    private static void naoDevePermitirDescontoAcimaDe50PorCento() {
        ProdutoService service = new ProdutoService(new InMemoryProdutoRepository());
        service.executar("P004", "Webcam", 300.00, 4);

        try {
            service.aplicar("P004", 60.0);
            throw new AssertionError("Deveria ter bloqueado desconto acima de 50%.");
        } catch (RegraNegocioException e) {
            System.out.println("[OK] naoDevePermitirDescontoAcimaDe50PorCento -> " + e.getMessage());
        }
    }
}