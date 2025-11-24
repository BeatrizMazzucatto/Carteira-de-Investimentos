# 💼 Carteira de Investimentos

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=for-the-badge&logo=spring)
![Maven](https://img.shields.io/badge/Maven-3.8+-blue?style=for-the-badge&logo=apachemaven)

**Sistema completo de gestão de múltiplas carteiras de investimentos**

[Funcionalidades](#-funcionalidades) • [Tecnologias](#-tecnologias) • [Instalação](#-instalação) • [Documentação](#-documentação)

</div>

---

## 📋 Sobre o Projeto

O **Investment Portfolio Manager** é um sistema robusto desenvolvido em Java/Spring Boot para gerenciamento completo de carteiras de investimentos. Permite que investidores gerenciem múltiplas carteiras, registrem transações, acompanhem rentabilidade em tempo real, calculem valores deflacionados e gerem relatórios detalhados para análise de desempenho.

### 🎯 Objetivo

Fornecer uma solução profissional e intuitiva para gestão pessoal de investimentos, com suporte a:

- 📊 Múltiplas carteiras por investidor
- 💰 Registro detalhado de transações (compras/vendas)
- 📈 Cálculo automático de rentabilidade
- 💵 Análise de inflação e valores deflacionados
- 📄 Relatórios consolidados e históricos
- 🖥️ Interface de console interativa
- 🌐 API REST completa
- ⚡ Modo servidor com atualizações em tempo real

---

## ✨ Funcionalidades

<table>
<tr>
<td width="50%">

### 🏦 Gestão de Carteiras

- ✅ Criação de múltiplas carteiras
- ✅ Objetivos personalizados
- ✅ Perfis de risco (Baixo, Moderado, Alto)
- ✅ Prazos de investimento
- ✅ Histórico de alterações
- ✅ Atualização automática de valores

### 💰 Transações

- ✅ Compras e vendas
- ✅ Cálculo de preço médio
- ✅ Gestão de taxas e impostos
- ✅ Validação automática
- ✅ Transações rápidas
- ✅ Histórico completo

</td>
<td width="50%">

### 📊 Relatórios e Análises

- ✅ Rentabilidade detalhada
- ✅ Análise de inflação
- ✅ Valores deflacionados
- ✅ Ganho real e poder de compra
- ✅ Exportação em JSON
- ✅ Relatórios históricos

### 📈 Cotações

- ✅ Integração com Google Sheets
- ✅ Atualização automática
- ✅ Consulta em tempo real
- ✅ Suporte a múltiplos ativos
- ✅ Histórico de cotações
- ✅ Streaming via WebSocket

</td>
</tr>
</table>

### 🔐 Segurança

- ✅ Autenticação JWT
- ✅ Hash de senhas com BCrypt
- ✅ Recuperação de senha (console)
- ✅ Validação de dados
- ✅ Tratamento de exceções

---

## 🛠 Tecnologias

<div align="center">

| Categoria | Tecnologias |
|-----------|-------------|
| **Backend** | Java 21, Spring Boot 3.2.0, Spring Data JPA, Hibernate |
| **Segurança** | JWT (jjwt), BCrypt |
| **Tempo Real** | Spring WebSocket, Scheduled Tasks |
| **Banco de Dados** | MariaDB/MySQL, H2 Database |
| **Build** | Maven 3.8+, Maven Wrapper |
| **Documentação** | Swagger/OpenAPI |
| **Testes** | JUnit 5, Mockito, Spring Boot Test |
| **Outros** | Jackson (JSON), Python (Scripts) |

</div>

---

## 📦 Pré-requisitos

Antes de começar, você precisa ter instalado:

```bash
# Java 21 ou superior
java -version

# Maven 3.8+ (opcional - projeto inclui Maven Wrapper)
mvn -version

# MariaDB/MySQL (opcional - pode usar H2 em memória)
mysql --version

# Python 3 (opcional - para atualização de cotações)
python3 --version
```

---

## 🚀 Instalação

### 1️⃣ Clone o repositório

```bash
git clone https://github.com/seu-usuario/carteira.git
cd carteira/carteira
```

### 2️⃣ Configure o banco de dados

Edite `src/main/resources/application.properties`:

```properties
# Desenvolvimento (H2 em memória)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver

# Produção (MariaDB)
spring.datasource.url=jdbc:mariadb://localhost:3306/investment_db
spring.datasource.username=root
spring.datasource.password=sua_senha
```

### 3️⃣ Compile o projeto

```bash
# Com Maven instalado
mvn clean install

# Ou use o Maven Wrapper
./mvnw clean install      # Linux/Mac
mvnw.cmd clean install    # Windows
```

---

## 💻 Como Usar

### 🖥️ Interface de Console

Inicie a interface interativa:

```bash
# Windows
run-console.bat

# Linux/Mac
./run-console.sh

# Manual
mvn spring-boot:run
```

### 📱 Fluxo de Uso

1. **🔐 Autenticação**
   - Login com email e senha
   - Criar nova conta
   - Recuperar senha (disponível no console)

2. **📋 Menu Principal**
   - Gerenciar carteiras
   - Registrar transações
   - Visualizar relatórios
   - Consultar ativos
   - Análise de inflação

3. **💼 Gestão**
   - Criar carteiras com objetivos
   - Definir perfil de risco
   - Acompanhar rentabilidade

---

## 🌐 API REST

### Iniciar o Servidor

```bash
# Windows
run-app.bat

# Linux/Mac
./run-app.sh
```

**API disponível em:** `http://localhost:8080`  
**Documentação Swagger:** `http://localhost:8080/swagger-ui.html`

### 📍 Principais Endpoints

<details>
<summary><b>👤 Investidores</b></summary>

```http
POST   /api/investidores              # Criar investidor
GET    /api/investidores/{id}         # Buscar investidor
PUT    /api/investidores/{id}         # Atualizar investidor
GET    /api/investidores              # Listar investidores
GET    /api/investidores/search       # Buscar investidores
DELETE /api/investidores/{id}         # Deletar investidor
```

</details>

<details>
<summary><b>💼 Carteiras</b></summary>

```http
GET    /api/carteiras/investidor/{id} # Listar carteiras
POST   /api/carteiras                 # Criar carteira
GET    /api/carteiras/{id}            # Buscar carteira
PUT    /api/carteiras/{id}            # Atualizar carteira
DELETE /api/carteiras/{id}            # Deletar carteira
POST   /api/carteiras/{id}/atualizar-precos  # Atualizar preços
POST   /api/carteiras/{id}/sincronizar-sheets  # Sincronizar com Google Sheets
```

</details>

<details>
<summary><b>💰 Transações</b></summary>

```http
POST   /api/transacoes/carteira/{id}  # Criar transação
GET    /api/transacoes/carteira/{id}  # Listar transações
GET    /api/transacoes/{id}           # Buscar transação
PUT    /api/transacoes/{id}           # Atualizar transação
DELETE /api/transacoes/{id}           # Deletar transação
POST   /api/transacoes/quick/comprar  # Transação rápida - compra
POST   /api/transacoes/quick/vender  # Transação rápida - venda
GET    /api/transacoes/quick/cotacao/{codigo}  # Consultar cotação
```

</details>

<details>
<summary><b>📈 Cotações & Relatórios</b></summary>

```http
GET    /api/cotacoes                  # Listar cotações
GET    /api/cotacoes/{codigo}         # Buscar cotação
POST   /api/cotacoes/atualizar        # Atualizar cotações
POST   /api/cotacoes/recarregar      # Recarregar cache
POST   /api/cotacoes/atualizar-json  # Atualizar JSON
GET    /api/cotacoes/status           # Status das cotações

GET    /api/relatorio/investidor/{id} # Relatório completo
GET    /api/relatorio/empresa         # Relatório consolidado
GET    /api/historico/ativo/{codigo}  # Histórico de cotações
GET    /api/historico/ativos          # Listar todos os históricos
GET    /api/historico/carteira/{id}  # Histórico da carteira
```

</details>

<details>
<summary><b>📊 Rentabilidade</b></summary>

```http
GET    /api/rentabilidade/ativo/{id}           # Rentabilidade do ativo
GET    /api/rentabilidade/carteira/{id}       # Rentabilidade completa da carteira
GET    /api/rentabilidade/carteira/{id}/resumo  # Resumo da rentabilidade
GET    /api/rentabilidade/carteira/{id}/ativos  # Rentabilidade de todos os ativos
GET    /api/rentabilidade/carteira/{id}/tipo/{tipo}  # Filtrar por tipo
GET    /api/rentabilidade/carteira/{id}/positivos   # Ativos positivos
GET    /api/rentabilidade/carteira/{id}/negativos    # Ativos negativos
GET    /api/rentabilidade/carteira/{id}/top/{limit}  # Top performers
GET    /api/rentabilidade/carteira/{id}/piores/{limit}  # Piores performers
GET    /api/rentabilidade/carteira/{id}/distribuicao  # Distribuição por tipo
GET    /api/rentabilidade/carteira/{id}/risco  # Métricas de risco
```

</details>

<details>
<summary><b>⚡ Tempo Real (Modo Servidor)</b></summary>

```http
POST   /api/realtime/cotacoes/atualizar  # Atualizar cotações manualmente
POST   /api/realtime/relatorio/gerar     # Gerar relatório manualmente
GET    /api/realtime/status               # Status das atualizações
```

**WebSocket:** `/ws-cotacoes` (conexão STOMP via SockJS)

</details>

### 💡 Exemplo de Uso

```bash
# Criar investidor
curl -X POST http://localhost:8080/api/investidores \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@example.com",
    "senha": "senha123"
  }'

# Registrar compra
curl -X POST http://localhost:8080/api/transacoes/carteira/1 \
  -H "Content-Type: application/json" \
  -d '{
    "tipoTransacao": "COMPRA",
    "codigoAtivo": "PETR4",
    "quantidade": 100,
    "precoUnitario": 25.50
  }'
```

---

## 🧪 Testes

O projeto possui cobertura completa de testes:

```bash
# Executar todos os testes
./run-tests.sh        # Linux/Mac
run-tests.bat         # Windows

# Com Maven
mvn test                           # Todos
mvn test -Dtest=*Test             # Unitários
mvn test -Dtest=*IntegrationTest  # Integração

# Relatório de cobertura
mvn clean test jacoco:report
```

**Cobertura:** Unitários, Integração, Funcionais End-to-End

---

## 📁 Estrutura do Projeto

```
carteira/
├── 📂 src/main/java/com/invest/
│   ├── config/          # Configurações (WebSocket, Database)
│   ├── console/         # Interface de console
│   ├── controller/      # Controllers REST
│   ├── dto/             # Data Transfer Objects
│   ├── model/           # Entidades JPA
│   ├── service/         # Lógica de negócio
│   └── util/            # Utilitários (JWT, Calculadoras)
│
├── 📂 src/main/resources/
│   ├── application.properties          # Config principal
│   ├── application-server.properties   # Modo servidor
│   └── data/cotacoes.json             # Cotações
│
├── 📂 src/test/          # Testes (unitários, integração, funcionais)
├── 📄 pom.xml            # Configuração Maven
├── 📖 README.md          # Este arquivo
└── 🚀 run-*.sh/.bat      # Scripts de execução
```

---

## 📚 Documentação Adicional

| Documento | Descrição |
|-----------|-----------|
| [📖 Guia do Console](GUIA_CONSOLE.md) | Manual completo da interface de console |
| [📊 Relatório da Empresa](GUIA_RELATORIO_EMPRESA.md) | Documentação do relatório consolidado |
| [🧪 Guia de Testes](TESTING_GUIDE.md) | Documentação dos testes |
| [🌐 Swagger UI](http://localhost:8080/swagger-ui.html) | API interativa (requer servidor rodando) |

---

## 🔄 Funcionalidades Especiais

### ⚡ Atualização Automática de Cotações

```bash
python3 atualiza_cotacoes.py
```

### 📊 Modo Servidor

```bash
# Executar com perfil server
mvn spring-boot:run -Dspring-boot.run.profiles=server
```

**Recursos:**

- ✅ Atualização de cotações a cada 5 minutos
- ✅ Relatórios automáticos a cada 10 minutos
- ✅ WebSocket para streaming em tempo real
- ✅ Endpoint: `/ws-cotacoes`

---

## 🤝 Contribuindo

Contribuições são muito bem-vindas! 

1. 🍴 Fork o projeto
2. 🌿 Crie uma branch (`git checkout -b feature/NovaFuncionalidade`)
3. 💾 Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. 📤 Push para a branch (`git push origin feature/NovaFuncionalidade`)
5. 🔃 Abra um Pull Request

### 📏 Padrões de Código

- ✅ Siga as convenções Java
- ✅ Adicione testes para novas funcionalidades
- ✅ Documente código complexo
- ✅ Mantenha cobertura de testes > 70%
- ✅ Use commits descritivos

---

## 👥 Autores

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/analayslla">
        <img src="https://github.com/github.png" width="100px;" alt="Ana Layslla"/><br>
        <sub><b>Ana Layslla</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/annakitice">
        <img src="https://github.com/annakitice.png" width="100px;" alt="Anna Kitice"/><br>
        <sub><b>Anna Kitice</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/BeatrizMazzucatto">
        <img src="https://github.com/BeatrizMazzucatto.png" width="100px;" alt="Beatriz Mazzucatto"/><br>
        <sub><b>Beatriz Mazzucatto</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/juliagarciac">
        <img src="https://github.com/juliagarciac.png" width="100px;" alt="Julia Garcia"/><br>
        <sub><b>Julia Garcia</b></sub>
      </a>
    </td>
  </tr>
</table>

---

## 🙏 Agradecimentos

- 💚 Spring Boot Community
- 🌟 Todos os contribuidores do projeto
- 📚 Comunidade open source

---

<div align="center">

⭐ **Se este projeto foi útil, considere dar uma estrela!** ⭐

---

**Última atualização:** Novembro 2025

</div>
