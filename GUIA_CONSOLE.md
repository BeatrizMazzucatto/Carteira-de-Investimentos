# Guia do Sistema de Console

## Interface de Console

O sistema possui uma interface de console simples e intuitiva, adequada para uso direto pelo usuário.

## Como Executar

### Windows

```cmd
run-console.bat
```

### macOS/Linux

```bash
./run-console.sh
```

### Manual

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

## Fluxo de Uso

### 1. Login ou Cadastro

* **Login:** Informe email e nome.
* **Nova Conta:** Crie um novo cadastro.
* **Sair:** Encerra o sistema.

### 2. Menu Principal

Após o login, o menu principal exibe as opções:

* Minhas Carteiras
* Nova Carteira
* Registrar Transação
* Relatórios
* Consultar Ativos
* Configurações
* Sair

## Funcionalidades Principais

### Gestão de Carteiras

* Criar carteiras com diferentes objetivos.
* Definir perfil de risco (Conservador, Moderado, Agressivo).
* Editar informações e visualizar resumo financeiro.

### Registro de Transações

* Registrar **compras**, **vendas** e **proventos** (dividendos, juros).
* Incluir detalhes como quantidade, preço e taxas.

### Relatórios de Rentabilidade

* Consultar valores investidos, rentabilidade bruta e líquida, custos e composição da carteira.

### Consulta de Ativos

* Visualizar todos os ativos da carteira, detalhes e histórico de transações.

## Dicas de Uso

### Primeira Utilização

1. Crie uma conta.
2. Adicione uma carteira de teste.
3. Registre uma transação.
4. Consulte os relatórios.

### Uso Diário

1. Faça login.
2. Visualize suas carteiras.
3. Registre novas transações.
4. Acompanhe relatórios de rentabilidade.

### Navegação

* Utilize números para selecionar opções.
* Pressione Enter para confirmar.
* Há sempre uma opção para voltar.
* Use “Sair” para encerrar a sessão.

## Configurações

### Banco de Dados

* **Padrão:** H2 em memória
* **Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
* **JDBC URL:** `jdbc:h2:mem:testdb`
* **Usuário:** `sa`
* **Senha:** (vazio)

### Perfis Disponíveis

* `h2` (padrão)
* `windows`
* `default` (MariaDB – requer instalação)

## Solução de Problemas

### Porta 8080 em uso

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# macOS/Linux
lsof -i :8080
kill -9 <PID>
```

### Dados não salvos

O banco H2 é temporário. Para manter os dados, configure MariaDB no arquivo `application.properties`.

---

# Sistema de Gestão de Carteiras de Investimentos

Sistema completo para gerenciamento de carteiras de investimento, cálculo de rentabilidade, registro de transações e integração com Google Sheets.

## Execução Rápida

### Interface de Console

```cmd
# Windows
run-console.bat

# macOS/Linux
./run-console.sh
```

### API REST

```cmd
# Windows
run-app.bat
test-api.bat

# macOS/Linux
./run-app.sh
./test-api.sh
```

### PowerShell

```powershell
.\run-app.ps1
```

## Pré-requisitos

* Java 17+
* Maven (opcional)
* IDE recomendada: IntelliJ IDEA, Eclipse ou VS Code

## Configuração do Banco

* **Banco padrão:** H2 (memória)
* **Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
* **Usuário:** `sa`
* **Senha:** (vazio)

Perfis disponíveis: `h2`, `windows`, `default` (MariaDB)

## Endpoints Principais

| Endpoint                                 | Método   | Descrição             |
| ---------------------------------------- | -------- | --------------------- |
| `/api/investidores`                      | GET/POST | Gerencia investidores |
| `/api/carteiras/investidor/{id}`         | POST     | Cria carteira         |
| `/api/transacoes/carteira/{id}/compra`   | POST     | Registra compra       |
| `/api/transacoes/carteira/{id}/venda`    | POST     | Registra venda        |
| `/api/transacoes/carteira/{id}/provento` | POST     | Registra provento     |
| `/api/rentabilidade/carteira/{id}`       | GET      | Calcula rentabilidade |

## Testes

### Verificar aplicação

```bash
curl http://localhost:8080/api/investidores
```

### Testes automáticos

* **Windows:** `test-api.bat`
* **Linux/macOS:** `./test-api.sh`

## Estrutura do Projeto

```
src/
├── main/java/com/invest/
│   ├── controller/     # Controllers REST
│   ├── service/        # Lógica de negócio
│   ├── repository/     # Acesso a dados
│   ├── model/          # Entidades
│   ├── dto/            # Data Transfer Objects
│   ├── config/         # Configurações
│   └── utils/          # Utilitários
└── resources/
    ├── application.properties
    ├── application-h2.properties
    └── application-windows.properties
```

## Exemplos de Uso

### Criar Investidor

```bash
curl -X POST http://localhost:8080/api/investidores \
  -H "Content-Type: application/json" \
  -d '{"nome": "João Silva", "email": "joao@email.com"}'
```

### Criar Carteira

```bash
curl -X POST http://localhost:8080/api/carteiras/investidor/1 \
  -H "Content-Type: application/json" \
  -d '{"nome": "Carteira Aposentadoria", "perfilRisco": "MODERADO"}'
```

### Registrar Compra

```bash
curl -X POST http://localhost:8080/api/transacoes/carteira/1/compra \
  -H "Content-Type: application/json" \
  -d '{"codigoAtivo": "PETR4", "quantidade": 100, "precoUnitario": 25.50}'
```

### Calcular Rentabilidade

```bash
curl http://localhost:8080/api/rentabilidade/carteira/1
```

## Erros Comuns

### Java não encontrado

Instale Java 17+ e verifique:

```bash
java -version
```

### Maven não encontrado

Instale Maven ou use uma IDE:

```bash
mvn -version
```

### Porta 8080 em uso

Verifique processos e encerre a porta ocupada (veja exemplo anterior).

### Conexão recusada

Confirme se a aplicação está em execução e teste o endpoint `/api/investidores`.

## Recomendações

1. Utilize uma IDE (IntelliJ IDEA recomendada).
2. Use o console H2 para visualizar dados.
3. Utilize os scripts `.bat` ou `.sh` conforme o sistema.
4. Monitore logs para identificar erros.
5. Execute testes automáticos com frequência.
