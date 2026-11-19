package br.com.senac.produtos.adapters.out.file;

import br.com.senac.produtos.application.port.out.ProdutoRepositoryPort;
import br.com.senac.produtos.domain.model.Produto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class ArquivoProdutoRepository implements ProdutoRepositoryPort {

    private final File arquivo;

    public ArquivoProdutoRepository(String caminhoArquivo) {
        this.arquivo = new File(caminhoArquivo);
        inicializarArquivo();
    }

    private void inicializarArquivo() {
        try {
            if (!arquivo.exists()) {
                arquivo.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar arquivo de persistência: " + arquivo.getName(), e);
        }
    }

    @Override
    public void salvar(Produto produto) {
        Map<String, Produto> produtos = carregarMapa();
        produtos.put(produto.getCodigo(), produto);
        gravarMapa(produtos);
    }

    @Override
    public Optional<Produto> buscarPorCodigo(String codigo) {
        return Optional.ofNullable(carregarMapa().get(codigo.toUpperCase()));
    }

    @Override
    public List<Produto> listarTodos() {
        return new ArrayList<>(carregarMapa().values());
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return carregarMapa().containsKey(codigo.toUpperCase());
    }

    private Map<String, Produto> carregarMapa() {
        Map<String, Produto> mapa = new LinkedHashMap<>();
        if (!arquivo.exists()) {
            return mapa;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] partes = linha.split(";");
                if (partes.length == 4) {
                    String codigo = partes[0];
                    String nome = partes[1];
                    double preco = Double.parseDouble(partes[2]);
                    int estoque = Integer.parseInt(partes[3]);
                    mapa.put(codigo, new Produto(codigo, nome, preco, estoque));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler produtos do arquivo.", e);
        }
        return mapa;
    }

    private void gravarMapa(Map<String, Produto> mapa) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo, false))) {
            for (Produto p : mapa.values()) {
                String linha = String.format(Locale.US, "%s;%s;%.2f;%d",
                        p.getCodigo(), p.getNome(), p.getPreco(), p.getQuantidadeEstoque());
                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar produtos no arquivo.", e);
        }
    }
}