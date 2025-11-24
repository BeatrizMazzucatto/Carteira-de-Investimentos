# Guia do Relatório da Empresa - API JSON

## 📋 Visão Geral

O endpoint `/api/relatorio/empresa` retorna um relatório consolidado em JSON com dados agregados de todos os investidores da plataforma. Este relatório foi desenvolvido especificamente para ser consumido no **Postman** e processado posteriormente no **front-end**.

## 🚀 Como Usar no Postman

### 1. Configuração da Requisição

**Método:** `GET`  
**URL:** `http://localhost:8080/api/relatorio/empresa`  
**Headers:** Não são necessários (opcional: `Accept: application/json`)

### 2. Exemplo de Requisição

```
GET http://localhost:8080/api/relatorio/empresa
```

### 3. Resposta Esperada

**Status Code:** `200 OK`  
**Content-Type:** `application/json`

## 📊 Estrutura do JSON Retornado

O JSON retornado possui a seguinte estrutura:

```json
{
  "dataGeracao": "2024-01-15T10:30:00",
  "versao": "1.0",
  
  "totalInvestidores": 5,
  "totalCarteiras": 12,
  "totalAtivos": 45,
  "totalTransacoes": 120,
  "valorTotalInvestido": 500000.00,
  "valorTotalAtual": 525000.00,
  "rentabilidadeTotal": 25000.00,
  "rentabilidadePercentual": 5.00,
  
  "investidores": [
    {
      "id": 1,
      "nome": "João Silva",
      "email": "joao@email.com",
      "totalCarteiras": 3,
      "totalAtivos": 10,
      "totalTransacoes": 25,
      "valorTotalInvestido": 100000.00,
      "valorTotalAtual": 105000.00,
      "rentabilidade": 5000.00,
      "rentabilidadePercentual": 5.00,
      "dataCriacao": "2024-01-01T08:00:00"
    }
  ],
  
  "estatisticasPorTipo": {
    "ACAO": {
      "tipo": "ACAO",
      "quantidade": 20,
      "valorTotalInvestido": 300000.00,
      "valorTotalAtual": 315000.00,
      "rentabilidade": 15000.00,
      "rentabilidadePercentual": 5.00
    },
    "FII": {
      "tipo": "FII",
      "quantidade": 15,
      "valorTotalInvestido": 150000.00,
      "valorTotalAtual": 157500.00,
      "rentabilidade": 7500.00,
      "rentabilidadePercentual": 5.00
    }
  },
  
  "transacoesRecentes": [
    {
      "id": 100,
      "tipoTransacao": "COMPRA",
      "codigoAtivo": "PETR4",
      "nomeAtivo": "Petrobras PN",
      "tipoAtivo": "ACAO",
      "quantidade": 100.00,
      "precoUnitario": 25.50,
      "valorTotal": 2550.00,
      "dataTransacao": "2024-01-15T09:00:00",
      "carteiraNome": "Carteira Principal",
      "carteiraId": 1,
      "investidorNome": "João Silva",
      "investidorId": 1
    }
  ]
}
```

## 📝 Campos do Relatório

### Metadados
- **dataGeracao**: Data e hora de geração do relatório
- **versao**: Versão do formato do relatório

### Estatísticas Gerais Consolidadas
- **totalInvestidores**: Número total de investidores cadastrados
- **totalCarteiras**: Número total de carteiras criadas
- **totalAtivos**: Número total de ativos em todas as carteiras
- **totalTransacoes**: Número total de transações realizadas
- **valorTotalInvestido**: Soma de todos os valores investidos
- **valorTotalAtual**: Soma de todos os valores atuais
- **rentabilidadeTotal**: Rentabilidade total em valor (R$)
- **rentabilidadePercentual**: Rentabilidade total em percentual (%)

### Lista de Investidores
Cada investidor contém:
- **id**: ID único do investidor
- **nome**: Nome completo
- **email**: Email de cadastro
- **totalCarteiras**: Quantidade de carteiras do investidor
- **totalAtivos**: Quantidade de ativos do investidor
- **totalTransacoes**: Quantidade de transações do investidor
- **valorTotalInvestido**: Valor total investido pelo investidor
- **valorTotalAtual**: Valor atual total do investidor
- **rentabilidade**: Rentabilidade em valor (R$)
- **rentabilidadePercentual**: Rentabilidade em percentual (%)
- **dataCriacao**: Data de cadastro do investidor

### Estatísticas por Tipo de Ativo
Agrupamento consolidado por tipo (ACAO, FII, TESOURO, etc.):
- **tipo**: Tipo do ativo
- **quantidade**: Quantidade de ativos deste tipo
- **valorTotalInvestido**: Valor total investido neste tipo
- **valorTotalAtual**: Valor atual total deste tipo
- **rentabilidade**: Rentabilidade em valor (R$)
- **rentabilidadePercentual**: Rentabilidade em percentual (%)

### Transações Recentes
Últimas 20 transações de todos os investidores (ordenadas por data, mais recente primeiro):
- **id**: ID da transação
- **tipoTransacao**: COMPRA ou VENDA
- **codigoAtivo**: Código do ativo (ex: PETR4)
- **nomeAtivo**: Nome completo do ativo
- **tipoAtivo**: Tipo do ativo
- **quantidade**: Quantidade negociada
- **precoUnitario**: Preço unitário da transação
- **valorTotal**: Valor total da transação
- **dataTransacao**: Data e hora da transação
- **carteiraNome**: Nome da carteira
- **carteiraId**: ID da carteira
- **investidorNome**: Nome do investidor
- **investidorId**: ID do investidor

## 🔍 Exemplos de Uso no Postman

### 1. Requisição Básica

1. Abra o Postman
2. Crie uma nova requisição GET
3. Digite a URL: `http://localhost:8080/api/relatorio/empresa`
4. Clique em **Send**
5. O JSON será retornado no corpo da resposta

### 2. Salvar Resposta

1. Após receber a resposta, clique em **Save Response**
2. Escolha **Save as Example** para salvar como exemplo
3. Ou copie o JSON para processar no front-end

### 3. Testar com Diferentes Ambientes

Se você tiver ambientes configurados (dev, prod), ajuste a URL base:
- **Desenvolvimento:** `http://localhost:8080/api/relatorio/empresa`
- **Produção:** `https://api.seudominio.com/api/relatorio/empresa`

## 💻 Processamento no Front-End

### Exemplo em JavaScript/TypeScript

```javascript
// Função para buscar relatório da empresa
async function buscarRelatorioEmpresa() {
  try {
    const response = await fetch('http://localhost:8080/api/relatorio/empresa');
    const relatorio = await response.json();
    
    // Processar dados
    console.log('Total de investidores:', relatorio.totalInvestidores);
    console.log('Rentabilidade total:', relatorio.rentabilidadePercentual + '%');
    
    // Exibir investidores
    relatorio.investidores.forEach(investidor => {
      console.log(`${investidor.nome}: ${investidor.rentabilidadePercentual}%`);
    });
    
    // Processar estatísticas por tipo
    Object.entries(relatorio.estatisticasPorTipo).forEach(([tipo, estat]) => {
      console.log(`${tipo}: ${estat.quantidade} ativos, ${estat.rentabilidadePercentual}%`);
    });
    
    return relatorio;
  } catch (error) {
    console.error('Erro ao buscar relatório:', error);
  }
}
```

### Exemplo em React

```jsx
import { useState, useEffect } from 'react';

function RelatorioEmpresa() {
  const [relatorio, setRelatorio] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('http://localhost:8080/api/relatorio/empresa')
      .then(res => res.json())
      .then(data => {
        setRelatorio(data);
        setLoading(false);
      })
      .catch(err => {
        console.error(err);
        setLoading(false);
      });
  }, []);

  if (loading) return <div>Carregando...</div>;
  if (!relatorio) return <div>Erro ao carregar relatório</div>;

  return (
    <div>
      <h1>Relatório da Empresa</h1>
      <p>Total de Investidores: {relatorio.totalInvestidores}</p>
      <p>Rentabilidade Total: {relatorio.rentabilidadePercentual}%</p>
      
      <h2>Investidores</h2>
      {relatorio.investidores.map(inv => (
        <div key={inv.id}>
          <h3>{inv.nome}</h3>
          <p>Rentabilidade: {inv.rentabilidadePercentual}%</p>
        </div>
      ))}
    </div>
  );
}
```

## 📚 Documentação Swagger

O endpoint também está documentado no Swagger UI:

**URL do Swagger:** `http://localhost:8080/swagger-ui.html`

Navegue até a seção **Relatórios** → **GET /api/relatorio/empresa** para ver a documentação completa e testar diretamente no Swagger.

## ⚠️ Tratamento de Erros

### Erro 400 Bad Request
Retornado quando há algum problema na geração do relatório.

**Resposta:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Erro ao gerar relatório"
}
```

### Erro 500 Internal Server Error
Retornado quando há um erro interno no servidor.

## ✅ Checklist de Teste

- [ ] Endpoint responde com status 200 OK
- [ ] JSON retornado está bem formatado
- [ ] Todos os campos esperados estão presentes
- [ ] Valores numéricos estão corretos
- [ ] Datas estão no formato ISO 8601
- [ ] Lista de investidores contém todos os investidores
- [ ] Estatísticas por tipo estão agrupadas corretamente
- [ ] Transações recentes estão ordenadas (mais recente primeiro)
- [ ] Dados podem ser processados no front-end sem erros

## 🎯 Próximos Passos

1. **Testar no Postman**: Faça uma requisição GET para o endpoint
2. **Validar JSON**: Verifique se todos os campos estão presentes
3. **Integrar no Front-End**: Use o JSON retornado para popular dashboards
4. **Monitorar Performance**: Acompanhe o tempo de resposta do endpoint

---

**Desenvolvido para facilitar o processamento de dados no front-end e análise empresarial.**

