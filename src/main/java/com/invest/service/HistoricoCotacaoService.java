package com.invest.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invest.dto.HistoricoCotacaoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Serviço para gerenciar histórico de cotações
 * Salva e recupera histórico a cada atualização do cotacoes.json
 */
@Service
public class HistoricoCotacaoService {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String HISTORICO_DIR = "src/main/resources/data/historico";
    private static final int MAX_HISTORICO_ITENS = 1000; // Limita histórico para não ficar muito grande

    /**
     * Salva o histórico de cotações a partir do cotacoes.json atualizado
     */
    public void salvarHistoricoAtualizacao() {
        try {
            // Lê o cotacoes.json atual
            var resource = resourceLoader.getResource("classpath:data/cotacoes.json");
            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());

            LocalDateTime agora = LocalDateTime.now();

            // Processa cada ativo do JSON
            for (JsonNode ativo : rootNode) {
                String codigo = obterCodigoAtivo(ativo);
                if (codigo == null) continue;

                BigDecimal precoAtual = obterPrecoAtivo(ativo);
                if (precoAtual == null) continue;

                // Carrega histórico existente
                List<HistoricoCotacaoResponse.HistoricoItem> historico = carregarHistoricoAtivo(codigo);

                // Calcula variação
                BigDecimal variacao = BigDecimal.ZERO;
                BigDecimal variacaoPercentual = BigDecimal.ZERO;
                if (!historico.isEmpty()) {
                    BigDecimal precoAnterior = historico.get(historico.size() - 1).getPreco();
                    variacao = precoAtual.subtract(precoAnterior);
                    if (precoAnterior.compareTo(BigDecimal.ZERO) > 0) {
                        variacaoPercentual = variacao.divide(precoAnterior, 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal("100"));
                    }
                }

                // Adiciona novo item ao histórico
                HistoricoCotacaoResponse.HistoricoItem novoItem = 
                    new HistoricoCotacaoResponse.HistoricoItem(agora, precoAtual, variacao, variacaoPercentual);
                historico.add(novoItem);

                // Limita tamanho do histórico
                if (historico.size() > MAX_HISTORICO_ITENS) {
                    historico = historico.subList(historico.size() - MAX_HISTORICO_ITENS, historico.size());
                }

                // Salva histórico atualizado
                salvarHistoricoAtivo(codigo, historico);
            }

        } catch (Exception e) {
            System.err.println("Erro ao salvar histórico de cotações: " + e.getMessage());
        }
    }

    /**
     * Retorna o histórico de um ativo específico
     */
    public HistoricoCotacaoResponse getHistoricoAtivo(String codigo) {
        try {
            List<HistoricoCotacaoResponse.HistoricoItem> historico = carregarHistoricoAtivo(codigo);
            
            // Busca nome do ativo no cotacoes.json atual
            String nome = buscarNomeAtivo(codigo);
            
            HistoricoCotacaoResponse response = new HistoricoCotacaoResponse();
            response.setCodigo(codigo.toUpperCase());
            response.setNome(nome != null ? nome : codigo);
            response.setHistorico(historico.toArray(new HistoricoCotacaoResponse.HistoricoItem[0]));
            
            return response;
        } catch (Exception e) {
            System.err.println("Erro ao buscar histórico do ativo " + codigo + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Retorna histórico de todos os ativos
     */
    public List<HistoricoCotacaoResponse> getAllHistorico() {
        List<HistoricoCotacaoResponse> historicos = new ArrayList<>();
        
        try {
            Path historicoDir = Paths.get(HISTORICO_DIR);
            if (!Files.exists(historicoDir)) {
                return historicos;
            }

            Files.list(historicoDir)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".json"))
                .forEach(path -> {
                    try {
                        String codigo = path.getFileName().toString().replace(".json", "");
                        HistoricoCotacaoResponse historico = getHistoricoAtivo(codigo);
                        if (historico != null) {
                            historicos.add(historico);
                        }
                    } catch (Exception e) {
                        // Ignora erros
                    }
                });

        } catch (Exception e) {
            System.err.println("Erro ao buscar todos os históricos: " + e.getMessage());
        }

        return historicos;
    }

    /**
     * Carrega histórico de um ativo do arquivo
     */
    private List<HistoricoCotacaoResponse.HistoricoItem> carregarHistoricoAtivo(String codigo) {
        try {
            Path arquivo = Paths.get(HISTORICO_DIR, codigo.toUpperCase() + ".json");
            if (!Files.exists(arquivo)) {
                return new ArrayList<>();
            }

            JsonNode rootNode = objectMapper.readTree(arquivo.toFile());
            JsonNode historicoNode = rootNode.get("historico");
            
            if (historicoNode == null || !historicoNode.isArray()) {
                return new ArrayList<>();
            }

            List<HistoricoCotacaoResponse.HistoricoItem> historico = new ArrayList<>();
            for (JsonNode item : historicoNode) {
                LocalDateTime dataHora = LocalDateTime.parse(
                    item.get("dataHora").asText(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                );
                BigDecimal preco = new BigDecimal(item.get("preco").asText());
                BigDecimal variacao = item.has("variacao") ? 
                    new BigDecimal(item.get("variacao").asText()) : BigDecimal.ZERO;
                BigDecimal variacaoPercentual = item.has("variacaoPercentual") ? 
                    new BigDecimal(item.get("variacaoPercentual").asText()) : BigDecimal.ZERO;

                historico.add(new HistoricoCotacaoResponse.HistoricoItem(
                    dataHora, preco, variacao, variacaoPercentual
                ));
            }

            return historico;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Salva histórico de um ativo no arquivo
     */
    private void salvarHistoricoAtivo(String codigo, List<HistoricoCotacaoResponse.HistoricoItem> historico) {
        try {
            Path historicoDir = Paths.get(HISTORICO_DIR);
            Files.createDirectories(historicoDir);

            Path arquivo = historicoDir.resolve(codigo.toUpperCase() + ".json");

            Map<String, Object> dados = new HashMap<>();
            dados.put("codigo", codigo.toUpperCase());
            dados.put("ultimaAtualizacao", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            dados.put("totalItens", historico.size());
            
            List<Map<String, Object>> historicoList = new ArrayList<>();
            for (HistoricoCotacaoResponse.HistoricoItem item : historico) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("dataHora", item.getDataHora().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                itemMap.put("preco", item.getPreco());
                itemMap.put("variacao", item.getVariacao());
                itemMap.put("variacaoPercentual", item.getVariacaoPercentual());
                historicoList.add(itemMap);
            }
            dados.put("historico", historicoList);

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(arquivo.toFile(), dados);

        } catch (Exception e) {
            System.err.println("Erro ao salvar histórico do ativo " + codigo + ": " + e.getMessage());
        }
    }

    /**
     * Obtém código do ativo do JSON
     */
    private String obterCodigoAtivo(JsonNode ativo) {
        String[] campos = {"Código", "Ação", "Acao", "Codigo", "codigo", "acao"};
        for (String campo : campos) {
            if (ativo.has(campo)) {
                return ativo.get(campo).asText().trim().toUpperCase();
            }
        }
        return null;
    }

    /**
     * Obtém preço do ativo do JSON
     */
    private BigDecimal obterPrecoAtivo(JsonNode ativo) {
        String[] campos = {"Preço", "Preço Atual", "Preco Atual", "PreÃ§o Atual", "preco"};
        for (String campo : campos) {
            if (ativo.has(campo)) {
                try {
                    String precoStr = ativo.get(campo).asText().replace(",", ".").trim();
                    return new BigDecimal(precoStr).setScale(2, RoundingMode.HALF_UP);
                } catch (Exception e) {
                    // Tenta próximo campo
                }
            }
        }
        return null;
    }

    /**
     * Busca nome do ativo no cotacoes.json atual
     */
    private String buscarNomeAtivo(String codigo) {
        try {
            var resource = resourceLoader.getResource("classpath:data/cotacoes.json");
            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());

            for (JsonNode ativo : rootNode) {
                String codigoAtivo = obterCodigoAtivo(ativo);
                if (codigo.equalsIgnoreCase(codigoAtivo)) {
                    if (ativo.has("Nome")) {
                        return ativo.get("Nome").asText();
                    }
                }
            }
        } catch (Exception e) {
            // Ignora erro
        }
        return null;
    }
}

