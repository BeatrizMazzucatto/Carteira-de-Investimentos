# 🧪 Guia de Testes - Sistema de Gestão de Carteiras

## 📋 Visão Geral

Este guia apresenta diferentes formas de testar o sistema de gestão de carteiras, desde testes unitários até testes de integração e scripts de API.

## 🚀 Como Executar o Sistema

### 1. **Pré-requisitos**
```bash
# Java 17+
java -version

# Maven 3.6+
mvn -version

# MariaDB rodando
mysql -u root -p
```

### 2. **Configuração do Banco**
```sql
-- Criar banco de dados
CREATE DATABASE investment_db;
USE investment_db;

-- Verificar se as tabelas foram criadas automaticamente
SHOW TABLES;
```

### 3. **Executar a Aplicação**
```bash
# Na pasta do projeto
cd /Users/ana/Downloads/Carteira

# Instalar dependências
mvn clean install

# Executar aplicação
mvn spring-boot:run
```

### 4. **Verificar se está funcionando**
```bash
# Aplicação deve estar rodando em:
http://localhost:8080

# Testar endpoint básico
curl http://localhost:8080/api/investidores
```

## 🧪 Tipos de Testes

### 1. **Testes Unitários**

#### **Testando FinancialCalculator**
```java
// src/test/java/com/invest/utils/FinancialCalculatorTest.java
package com.invest.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

class FinancialCalculatorTest {

    @Test
    @DisplayName("Deve calcular preço médio ponderado corretamente")
    void testCalcularPrecoMedioPonderado() {
        // Arrange
        BigDecimal quantidadeAtual = new BigDecimal("100");
        BigDecimal precoMedioAtual = new BigDecimal("25.00");
        BigDecimal quantidadeNova = new BigDecimal("100");
        BigDecimal precoNovaCompra = new BigDecimal("30.00");
        
        // Act
        BigDecimal resultado = FinancialCalculator.calcularPrecoMedioPonderado(
            quantidadeAtual, precoMedioAtual, quantidadeNova, precoNovaCompra);
        
        // Assert
        assertEquals(new BigDecimal("27.5000"), resultado);
    }

    @Test
    @DisplayName("Deve retornar zero quando valores são nulos")
    void testCalcularPrecoMedioPonderadoComValoresNulos() {
        // Act & Assert
        assertEquals(BigDecimal.ZERO, FinancialCalculator.calcularPrecoMedioPonderado(null, null, null, null));
    }

    @Test
    @DisplayName("Deve calcular rentabilidade percentual corretamente")
    void testCalcularRentabilidadePercentual() {
        // Arrange
        BigDecimal valorAtual = new BigDecimal("1200.00");
        BigDecimal valorInvestido = new BigDecimal("1000.00");
        
        // Act
        BigDecimal resultado = FinancialCalculator.calcularRentabilidadePercentual(valorAtual, valorInvestido);
        
        // Assert
        assertEquals(new BigDecimal("20.0000"), resultado);
    }

    @Test
    @DisplayName("Deve calcular dividend yield corretamente")
    void testCalcularDividendYield() {
        // Arrange
        BigDecimal proventosTotal = new BigDecimal("50.00");
        BigDecimal valorAtualMercado = new BigDecimal("1000.00");
        
        // Act
        BigDecimal resultado = FinancialCalculator.calcularDividendYield(proventosTotal, valorAtualMercado);
        
        // Assert
        assertEquals(new BigDecimal("5.0000"), resultado);
    }
}
```

#### **Testando ValidationUtils**
```java
// src/test/java/com/invest/utils/ValidationUtilsTest.java
package com.invest.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

class ValidationUtilsTest {

    @Test
    @DisplayName("Deve validar nome válido")
    void testValidName() {
        assertTrue(ValidationUtils.isValidName("João Silva"));
        assertTrue(ValidationUtils.isValidName("A"));
        assertFalse(ValidationUtils.isValidName(""));
        assertFalse(ValidationUtils.isValidName(null));
        assertFalse(ValidationUtils.isValidName("   "));
    }

    @Test
    @DisplayName("Deve validar email válido")
    void testValidEmail() {
        assertTrue(ValidationUtils.isValidEmail("joao@email.com"));
        assertTrue(ValidationUtils.isValidEmail("test.user+tag@domain.co.uk"));
        assertFalse(ValidationUtils.isValidEmail("email-invalido"));
        assertFalse(ValidationUtils.isValidEmail("@domain.com"));
        assertFalse(ValidationUtils.isValidEmail(""));
        assertFalse(ValidationUtils.isValidEmail(null));
    }

    @Test
    @DisplayName("Deve validar código de ativo")
    void testValidAssetCode() {
        assertTrue(ValidationUtils.isValidAssetCode("PETR4"));
        assertTrue(ValidationUtils.isValidAssetCode("VALE3"));
        assertTrue(ValidationUtils.isValidAssetCode("petr4")); // deve converter para maiúsculo
        assertFalse(ValidationUtils.isValidAssetCode(""));
        assertFalse(ValidationUtils.isValidAssetCode(null));
        assertFalse(ValidationUtils.isValidAssetCode("PETR4-"));
    }

    @Test
    @DisplayName("Deve validar quantidade de ativo")
    void testValidAssetQuantity() {
        assertTrue(ValidationUtils.isValidAssetQuantity(new BigDecimal("100")));
        assertTrue(ValidationUtils.isValidAssetQuantity(new BigDecimal("0.001")));
        assertFalse(ValidationUtils.isValidAssetQuantity(new BigDecimal("0")));
        assertFalse(ValidationUtils.isValidAssetQuantity(new BigDecimal("-10")));
        assertFalse(ValidationUtils.isValidAssetQuantity(null));
    }
}
```

### 2. **Testes de Integração**

#### **Testando Services**
```java
// src/test/java/com/invest/service/TransacaoServiceTest.java
package com.invest.service;

import com.invest.dto.TransacaoRequest;
import com.invest.model.*;
import com.invest.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransacaoServiceTest {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private InvestidorRepository investidorRepository;

    @Test
    void testCreateTransacaoCompra() {
        // Arrange
        Investidor investidor = new Investidor("João Silva", "joao@email.com");
        investidor = investidorRepository.save(investidor);

        Carteira carteira = new Carteira("Carteira Teste", ObjetivoCarteira.APOSENTADORIA, PerfilRisco.MODERADO);
        carteira.setInvestidor(investidor);
        carteira = carteiraRepository.save(carteira);

        TransacaoRequest request = new TransacaoRequest();
        request.setTipoTransacao(TipoTransacao.COMPRA);
        request.setCodigoAtivo("PETR4");
        request.setNomeAtivo("Petrobras");
        request.setTipoAtivo(TipoAtivo.ACAO);
        request.setQuantidade(new BigDecimal("100"));
        request.setPrecoUnitario(new BigDecimal("25.00"));
        request.setTaxasCorretagem(new BigDecimal("5.00"));

        // Act
        Transacao transacao = transacaoService.createTransacao(carteira.getId(), request);

        // Assert
        assertNotNull(transacao.getId());
        assertEquals(TipoTransacao.COMPRA, transacao.getTipoTransacao());
        assertEquals("PETR4", transacao.getCodigoAtivo());
        assertEquals(new BigDecimal("2500.00"), transacao.getValorTotal());
        assertEquals(new BigDecimal("2495.00"), transacao.getValorLiquido());
    }
}
```

### 3. **Testes de API com cURL**

#### **Script de Teste Completo**
```bash
#!/bin/bash
# test-api.sh

BASE_URL="http://localhost:8080/api"
echo "🧪 Testando API do Sistema de Carteiras"
echo "======================================"

# 1. Criar Investidor
echo "📝 1. Criando investidor..."
INVESTIDOR_RESPONSE=$(curl -s -X POST "$BASE_URL/investidores" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com"
  }')

echo "Investidor criado: $INVESTIDOR_RESPONSE"

# Extrair ID do investidor (assumindo que retorna JSON com campo 'id')
INVESTIDOR_ID=$(echo $INVESTIDOR_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "ID do Investidor: $INVESTIDOR_ID"

# 2. Criar Carteira
echo "📝 2. Criando carteira..."
CARTEIRA_RESPONSE=$(curl -s -X POST "$BASE_URL/carteiras/investidor/$INVESTIDOR_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Carteira Aposentadoria",
    "descricao": "Carteira para aposentadoria",
    "objetivo": "APOSENTADORIA",
    "perfilRisco": "MODERADO",
    "valorInicial": 10000.00
  }')

echo "Carteira criada: $CARTEIRA_RESPONSE"

# Extrair ID da carteira
CARTEIRA_ID=$(echo $CARTEIRA_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "ID da Carteira: $CARTEIRA_ID"

# 3. Registrar Compra
echo "📝 3. Registrando compra de PETR4..."
COMPRA_RESPONSE=$(curl -s -X POST "$BASE_URL/transacoes/carteira/$CARTEIRA_ID/compra" \
  -H "Content-Type: application/json" \
  -d '{
    "codigoAtivo": "PETR4",
    "nomeAtivo": "Petrobras",
    "tipoAtivo": "ACAO",
    "quantidade": 100,
    "precoUnitario": 25.50,
    "taxasCorretagem": 5.00,
    "observacoes": "Compra inicial"
  }')

echo "Compra registrada: $COMPRA_RESPONSE"

# 4. Registrar Venda
echo "📝 4. Registrando venda parcial..."
VENDA_RESPONSE=$(curl -s -X POST "$BASE_URL/transacoes/carteira/$CARTEIRA_ID/venda" \
  -H "Content-Type: application/json" \
  -d '{
    "codigoAtivo": "PETR4",
    "nomeAtivo": "Petrobras",
    "tipoAtivo": "ACAO",
    "quantidade": 50,
    "precoUnitario": 30.00,
    "taxasCorretagem": 3.00,
    "observacoes": "Venda parcial"
  }')

echo "Venda registrada: $VENDA_RESPONSE"

# 5. Registrar Provento
echo "📝 5. Registrando provento..."
PROVENTO_RESPONSE=$(curl -s -X POST "$BASE_URL/transacoes/carteira/$CARTEIRA_ID/provento" \
  -H "Content-Type: application/json" \
  -d '{
    "codigoAtivo": "PETR4",
    "nomeAtivo": "Petrobras",
    "tipoAtivo": "ACAO",
    "quantidade": 50,
    "precoUnitario": 0.50,
    "observacoes": "Dividendo trimestral"
  }')

echo "Provento registrado: $PROVENTO_RESPONSE"

# 6. Consultar Rentabilidade
echo "📊 6. Consultando rentabilidade..."
RENTABILIDADE_RESPONSE=$(curl -s -X GET "$BASE_URL/rentabilidade/carteira/$CARTEIRA_ID")

echo "Rentabilidade: $RENTABILIDADE_RESPONSE"

# 7. Listar Transações
echo "📋 7. Listando transações..."
TRANSACOES_RESPONSE=$(curl -s -X GET "$BASE_URL/transacoes/carteira/$CARTEIRA_ID")

echo "Transações: $TRANSACOES_RESPONSE"

echo "✅ Testes concluídos!"
```

### 4. **Testes com Postman**

#### **Coleção de Testes**
```json
{
  "info": {
    "name": "Sistema de Carteiras - Testes",
    "description": "Coleção de testes para o sistema de gestão de carteiras"
  },
  "item": [
    {
      "name": "1. Criar Investidor",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"nome\": \"João Silva\",\n  \"email\": \"joao@email.com\"\n}"
        },
        "url": {
          "raw": "{{base_url}}/api/investidores",
          "host": ["{{base_url}}"],
          "path": ["api", "investidores"]
        }
      }
    },
    {
      "name": "2. Criar Carteira",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"nome\": \"Carteira Aposentadoria\",\n  \"descricao\": \"Carteira para aposentadoria\",\n  \"objetivo\": \"APOSENTADORIA\",\n  \"perfilRisco\": \"MODERADO\",\n  \"valorInicial\": 10000.00\n}"
        },
        "url": {
          "raw": "{{base_url}}/api/carteiras/investidor/{{investidor_id}}",
          "host": ["{{base_url}}"],
          "path": ["api", "carteiras", "investidor", "{{investidor_id}}"]
        }
      }
    },
    {
      "name": "3. Registrar Compra",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"codigoAtivo\": \"PETR4\",\n  \"nomeAtivo\": \"Petrobras\",\n  \"tipoAtivo\": \"ACAO\",\n  \"quantidade\": 100,\n  \"precoUnitario\": 25.50,\n  \"taxasCorretagem\": 5.00,\n  \"observacoes\": \"Compra inicial\"\n}"
        },
        "url": {
          "raw": "{{base_url}}/api/transacoes/carteira/{{carteira_id}}/compra",
          "host": ["{{base_url}}"],
          "path": ["api", "transacoes", "carteira", "{{carteira_id}}", "compra"]
        }
      }
    },
    {
      "name": "4. Consultar Rentabilidade",
      "request": {
        "method": "GET",
        "url": {
          "raw": "{{base_url}}/api/rentabilidade/carteira/{{carteira_id}}",
          "host": ["{{base_url}}"],
          "path": ["api", "rentabilidade", "carteira", "{{carteira_id}}"]
        }
      }
    }
  ],
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:8080"
    },
    {
      "key": "investidor_id",
      "value": "1"
    },
    {
      "key": "carteira_id",
      "value": "1"
    }
  ]
}
```

## 🔧 Configuração de Ambiente de Teste

### 1. **application-test.properties**
```properties
# src/main/resources/application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Desabilitar logs desnecessários
logging.level.org.springframework.web=WARN
logging.level.org.hibernate=WARN
```

### 2. **Dependências de Teste no pom.xml**
```xml
<dependencies>
    <!-- Testes -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 📊 Cenários de Teste

### 1. **Cenário: Investidor Completo**
```bash
# Sequência de operações:
1. Criar investidor
2. Criar 3 carteiras (Aposentadoria, Emergência, Growth)
3. Adicionar ativos em cada carteira
4. Registrar transações (compras, vendas, proventos)
5. Calcular rentabilidade de cada carteira
6. Gerar relatório consolidado
```

### 2. **Cenário: Teste de Preço Médio**
```bash
# Sequência:
1. Compra 100 PETR4 a R$ 25,00
2. Compra 100 PETR4 a R$ 30,00
3. Compra 100 PETR4 a R$ 20,00
4. Verificar preço médio = R$ 25,00
5. Venda 150 PETR4 a R$ 28,00
6. Verificar posição restante
```

### 3. **Cenário: Teste de Rentabilidade**
```bash
# Sequência:
1. Investir R$ 10.000 em PETR4
2. Receber R$ 500 em proventos
3. Valor atual: R$ 12.000
4. Verificar rentabilidade líquida: +25%
```

## 🚨 Troubleshooting

### **Problemas Comuns:**

#### 1. **Erro de Conexão com Banco**
```bash
# Verificar se MariaDB está rodando
sudo systemctl status mariadb

# Verificar configurações
mysql -u root -p -e "SHOW DATABASES;"
```

#### 2. **Erro de Porta em Uso**
```bash
# Verificar processo na porta 8080
lsof -i :8080

# Matar processo se necessário
kill -9 <PID>
```

#### 3. **Erro de Dependências**
```bash
# Limpar cache do Maven
mvn clean

# Reinstalar dependências
mvn install -U
```

#### 4. **Erro de Compilação**
```bash
# Verificar versão do Java
java -version

# Compilar projeto
mvn compile

# Verificar erros específicos
mvn compile -X
```

## 📈 Métricas de Teste

### **Cobertura de Testes Esperada:**
- ✅ **Utilitários**: 100% (FinancialCalculator, ValidationUtils)
- ✅ **Services**: 80%+ (lógica de negócio principal)
- ✅ **Controllers**: 70%+ (endpoints principais)
- ✅ **Repositories**: 60%+ (queries customizadas)

### **Executar Cobertura:**
```bash
# Gerar relatório de cobertura
mvn test jacoco:report

# Visualizar relatório
open target/site/jacoco/index.html
```

## 🎯 Próximos Passos

1. **Implementar testes unitários** para todos os utilitários
2. **Criar testes de integração** para services críticos
3. **Implementar testes de API** automatizados
4. **Configurar CI/CD** com execução automática de testes
5. **Adicionar testes de performance** para operações críticas

## 📝 Conclusão

Este guia fornece uma base sólida para testar o sistema de gestão de carteiras. Comece com os testes unitários dos utilitários e evolua gradualmente para testes mais complexos. A chave é testar incrementalmente e manter os testes atualizados conforme o sistema evolui.
