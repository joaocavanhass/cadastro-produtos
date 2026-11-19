# Cadastro de Produtos — Arquitetura Hexagonal (Portas e Adaptadores)

Projeto desenvolvido em **Java puro** para a disciplina de **Engenharia e Arquitetura de Software (Senac)**, demonstrando na prática os princípios e a organização da **Arquitetura Hexagonal (Ports & Adapters)**.

## Integrantes
* **João Cavanhas**
* **Nicolas Thomaz**

---

## Estrutura Arquitetural e Pacotes

O sistema é dividido em **Núcleo da Aplicação** (Domínio + Aplicação/Portas) e **Adaptadores Externos**, garantindo que as regras de negócio não dependam de frameworks, interface de usuário ou mecanismos de persistência.

```text
cadastro-produtos/
├── src/br/com/senac/produtos/
│   ├── domain/                              # NÚCLEO: Regras de negócio puras
│   │   ├── model/
│   │   │   └── Produto.java                 # Entidade com validações e regra de desconto
│   │   └── exception/
│   │       └── RegraNegocioException.java   # Exceção específica do domínio
│   │
│   ├── application/                         # NÚCLEO: Orquestração e Contratos
│   │   ├── port/
│   │   │   ├── in/                          # Portas de Entrada (o que o sistema oferece)
│   │   │   │   ├── CadastrarProdutoUseCase.java
│   │   │   │   ├── ConsultarProdutoUseCase.java
│   │   │   │   └── AplicarDescontoUseCase.java
│   │   │   └── out/                         # Porta de Saída (o que o sistema precisa)
│   │   │       └── ProdutoRepositoryPort.java
│   │   └── service/
│   │       └── ProdutoService.java          # Implementação dos Casos de Uso
│   │
│   ├── adapters/                            # EXTERNO: Infraestrutura e Interfaces
│   │   ├── in/
│   │   │   └── console/
│   │   │       └── ConsoleProdutoAdapter.java      # Adaptador de Entrada (CLI)
│   │   └── out/
│   │       ├── memory/
│   │       │   └── InMemoryProdutoRepository.java  # Adaptador de Saída 1 (Memória RAM)
│   │       └── file/
│   │           └── ArquivoProdutoRepository.java   # Adaptador de Saída 2 (Arquivo CSV)
│   │
│   └── Main.java                            # Composição / Injeção de Dependências
│
└── test/br/com/senac/produtos/
    └── ProdutoServiceTest.java              # Testes unitários isolados do núcleo
```

---

## Identificação dos Componentes Arquiteturais

| Componente | Camada | Classes / Interfaces | Responsabilidade |
| :--- | :--- | :--- | :--- |
| **Domínio** | Núcleo (Interno) | `Produto`, `RegraNegocioException` | Contém os dados e as regras de negócio puras (validação de preço, código obrigatório, limite de desconto). |
| **Portas de Entrada** | Núcleo (Fronteira) | `CadastrarProdutoUseCase`, `ConsultarProdutoUseCase`, `AplicarDescontoUseCase` | Interfaces que definem as operações disponíveis para o mundo externo. |
| **Casos de Uso** | Núcleo (Interno) | `ProdutoService` | Implementa as portas de entrada, orquestra a entidade `Produto` e aciona a porta de saída. |
| **Porta de Saída** | Núcleo (Fronteira) | `ProdutoRepositoryPort` | Interface que define o contrato de persistência exigido pelo núcleo. |
| **Adaptador de Entrada** | Externa | `ConsoleProdutoAdapter` | Captura comandos do usuário no terminal e converte em chamadas às portas de entrada. |
| **Adaptadores de Saída** | Externa | `InMemoryProdutoRepository`, `ArquivoProdutoRepository` | Implementam `ProdutoRepositoryPort` usando estruturas em memória ou arquivo `.csv`. |
| **Composição** | Externa | `Main` | Instancia o adaptador de saída escolhido e o injeta no núcleo (`ProdutoService`). |

---

## Fluxo de Execução de uma Operação (Cadastro de Produto)

1. **Entrada do Usuário:** `ConsoleProdutoAdapter` lê código, nome, preço e estoque digitados no console.
2. **Acionamento do Caso de Uso:** O adaptador invoca `cadastrarUseCase.executar(...)` através da porta de entrada.
3. **Execução da Regra de Negócio:** `ProdutoService` verifica se o código já existe e instancia `Produto`, que valida todas as regras de negócio.
4. **Acesso à Porta de Saída:** `ProdutoService` chama `repository.salvar(produto)` pela interface `ProdutoRepositoryPort`.
5. **Persistência e Retorno:** O adaptador ativo (`InMemory` ou `Arquivo`) grava o produto e retorna o objeto criado para exibição no terminal.

---

## Como Compilar e Executar

### 1. Compilar o código-fonte
Na raiz do repositório (`cadastro-produtos`), execute:

**Windows (PowerShell / CMD):**
```powershell
mkdir out -Force
dir /s /B src\*.java > fontes.txt
javac -encoding UTF-8 -d out @fontes.txt
del fontes.txt
```

**Linux / macOS:**
```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
```

### 2. Executar a aplicação principal
```bash
java -cp out br.com.senac.produtos.Main
```

### 3. Compilar e executar os testes unitários
```bash
javac -encoding UTF-8 -cp out -d out test/br/com/senac/produtos/ProdutoServiceTest.java
java -cp out test.br.com.senac.produtos.ProdutoServiceTest
```

---

## Procedimento para Alternar os Adaptadores de Saída

A substituição entre os mecanismos de persistência ocorre exclusivamente na classe `Main.java` (ponto de montagem das dependências), **sem alterar nenhuma linha** das regras de negócio (`Produto`), casos de uso (`ProdutoService`) ou portas:

1. **Via Menu Interativo na Execução:**
   * Ao iniciar `Main`, digite `1` para injetar `InMemoryProdutoRepository` (dados armazenados temporariamente em memória RAM).
   * Ou digite `2` para injetar `ArquivoProdutoRepository` (dados persistidos de forma permanente no arquivo local `produtos.csv`).

2. **Via Código-Fonte (`src/br/com/senac/produtos/Main.java`):**
   ```java
   // Opção 1: Persistência em Memória
   ProdutoRepositoryPort repositoryPort = new InMemoryProdutoRepository();

   // Opção 2: Persistência em Arquivo CSV (basta trocar a instância abaixo)
   // ProdutoRepositoryPort repositoryPort = new ArquivoProdutoRepository("produtos.csv");

   ProdutoService produtoService = new ProdutoService(repositoryPort);
   ```