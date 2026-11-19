package br.com.senac.produtos.adapters.in.console;

import br.com.senac.produtos.application.port.in.AplicarDescontoUseCase;
import br.com.senac.produtos.application.port.in.CadastrarProdutoUseCase;
import br.com.senac.produtos.application.port.in.ConsultarProdutoUseCase;
import br.com.senac.produtos.domain.exception.RegraNegocioException;
import br.com.senac.produtos.domain.model.Produto;

import java.util.List;
import java.util.Scanner;

public class ConsoleProdutoAdapter {

    private final CadastrarProdutoUseCase cadastrarUseCase;
    private final ConsultarProdutoUseCase consultarUseCase;
    private final AplicarDescontoUseCase aplicarDescontoUseCase;
    private final Scanner scanner;

    public ConsoleProdutoAdapter(
            CadastrarProdutoUseCase cadastrarUseCase,
            ConsultarProdutoUseCase consultarUseCase,
            AplicarDescontoUseCase aplicarDescontoUseCase) {
        this.cadastrarUseCase = cadastrarUseCase;
        this.consultarUseCase = consultarUseCase;
        this.aplicarDescontoUseCase = aplicarDescontoUseCase;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcao = -1;
        while (opcao != 0) {
            exibirMenu();
            try {
                opcao = Integer.parseInt(scanner.nextLine().trim());
                processarOpcao(opcao);
            } catch (NumberFormatException e) {
                System.out.println("Erro: Digite um número válido.");
            } catch (RegraNegocioException e) {
                System.out.println("Regra de Negócio: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Erro inesperado: " + e.getMessage());
            }
        }
    }

    private void exibirMenu() {
        System.out.println("\n=== CADASTRO DE PRODUTOS (ARQUITETURA HEXAGONAL) ===");
        System.out.println("1 - Cadastrar novo produto");
        System.out.println("2 - Listar todos os produtos");
        System.out.println("3 - Buscar produto por código");
        System.out.println("4 - Aplicar desconto em produto");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    private void processarOpcao(int opcao) {
        switch (opcao) {
            case 1 -> cadastrar();
            case 2 -> listar();
            case 3 -> buscar();
            case 4 -> aplicarDesconto();
            case 0 -> System.out.println("Encerrando o sistema...");
            default -> System.out.println("Opção inválida!");
        }
    }

    private void cadastrar() {
        System.out.print("Código do produto (ex: P001): ");
        String codigo = scanner.nextLine();

        System.out.print("Nome do produto: ");
        String nome = scanner.nextLine();

        System.out.print("Preço (ex: 49.90): ");
        double preco = Double.parseDouble(scanner.nextLine().replace(",", "."));

        System.out.print("Quantidade em estoque: ");
        int estoque = Integer.parseInt(scanner.nextLine().trim());

        Produto salvo = cadastrarUseCase.executar(codigo, nome, preco, estoque);
        System.out.println("Sucesso! Produto cadastrado: " + salvo);
    }

    private void listar() {
        List<Produto> produtos = consultarUseCase.listarTodos();
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        System.out.println("\n--- Lista de Produtos ---");
        produtos.forEach(System.out::println);
    }

    private void buscar() {
        System.out.print("Informe o código do produto: ");
        String codigo = scanner.nextLine();
        consultarUseCase.buscarPorCodigo(codigo)
                .ifPresentOrElse(
                        p -> System.out.println("Encontrado: " + p),
                        () -> System.out.println("Produto não encontrado.")
                );
    }

    private void aplicarDesconto() {
        System.out.print("Informe o código do produto: ");
        String codigo = scanner.nextLine();

        System.out.print("Percentual de desconto (1 a 50%): ");
        double percentual = Double.parseDouble(scanner.nextLine().replace(",", "."));

        Produto atualizado = aplicarDescontoUseCase.aplicar(codigo, percentual);
        System.out.println("Desconto aplicado! Novo valor: " + atualizado);
    }
}