package br.com.senac.produtos;

import br.com.senac.produtos.adapters.in.console.ConsoleProdutoAdapter;
import br.com.senac.produtos.adapters.out.file.ArquivoProdutoRepository;
import br.com.senac.produtos.adapters.out.memory.InMemoryProdutoRepository;
import br.com.senac.produtos.application.port.out.ProdutoRepositoryPort;
import br.com.senac.produtos.application.service.ProdutoService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Selecione o adaptador de persistência (Porta de Saída):");
        System.out.println("1 - Memória RAM (InMemoryProdutoRepository)");
        System.out.println("2 - Arquivo Local CSV (ArquivoProdutoRepository)");
        System.out.print("Opção [1 ou 2]: ");
        String escolha = scanner.nextLine().trim();

        // Montagem da dependência externa (Adaptador de Saída)
        ProdutoRepositoryPort repositoryPort;
        if ("2".equals(escolha)) {
            repositoryPort = new ArquivoProdutoRepository("produtos.csv");
            System.out.println(">> Adaptador ativo: Persistência em Arquivo (produtos.csv)");
        } else {
            repositoryPort = new InMemoryProdutoRepository();
            System.out.println(">> Adaptador ativo: Persistência em Memória");
        }

        // Núcleo da aplicação recebe a abstração (interface)
        ProdutoService produtoService = new ProdutoService(repositoryPort);

        // Adaptador de entrada conectado às portas de entrada do núcleo
        ConsoleProdutoAdapter consoleAdapter = new ConsoleProdutoAdapter(
                produtoService,
                produtoService,
                produtoService
        );

        consoleAdapter.iniciar();
    }
}