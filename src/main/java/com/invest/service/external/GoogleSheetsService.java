package com.invest.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço para leitura de cotações de ativos a partir de um arquivo JSON local.
 * 
 * O arquivo cotacoes.json é gerado por script Python a partir da planilha Google Sheets.
 */
@Service
public class GoogleSheetsService {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String JSON_PATH = "classpath:data/cotacoes.json";

    // Cache simples (para evitar leitura frequente do disco)
    private Map<String, BigDecimal> cotacoesCache = null;
    private long lastModified = 0;

    /**
     * Busca o preço atual de um ativo pelo código (ex: PETR4, MGLU3)
     * @param codigoAtivo Código do ativo (case-insensitive)
     * @return Preço do ativo ou null se não encontrado
     */
    public BigDecimal buscarPrecoAtivo(String codigoAtivo) {
        if (codigoAtivo == null || codigoAtivo.trim().isEmpty()) {
            return null;
        }

        try {
            // Recarrega o cache se necessário
            recarregarCotacoesSeNecessario();

            String codigoUpper = codigoAtivo.toUpperCase().trim();
            return cotacoesCache.get(codigoUpper);

        } catch (Exception e) {
            System.err.println("Erro ao buscar preço do ativo " + codigoAtivo + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Retorna todas as cotações disponíveis
     * @return Mapa com código do ativo e preço
     */
    public Map<String, BigDecimal> getAllCotacoes() {
        try {
            recarregarCotacoesSeNecessario();
            return new HashMap<>(cotacoesCache);
        } catch (Exception e) {
            System.err.println("Erro ao buscar todas as cotações: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Busca todos os dados de uma cotação pelo código (preço, variação, máximo, mínimo, etc)
     * @param codigoAtivo Código do ativo (case-insensitive)
     * @return Mapa com todos os dados da cotação ou null se não encontrado
     */
    public Map<String, Object> buscarCotacaoCompleta(String codigoAtivo) {
        if (codigoAtivo == null || codigoAtivo.trim().isEmpty()) {
            return null;
        }

        try {
            Resource resource = resourceLoader.getResource(JSON_PATH);
            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());
            String codigoUpper = codigoAtivo.toUpperCase().trim();

            for (JsonNode ativo : rootNode) {
                String codigo = null;
                if (ativo.has("Código")) {
                    codigo = ativo.get("Código").asText().trim().toUpperCase();
                }

                if (codigo != null && codigo.equals(codigoUpper)) {
                    Map<String, Object> cotacaoMap = new HashMap<>();
                    cotacaoMap.put("codigo", codigo);

                    // Preço
                    if (ativo.has("Preço")) {
                        String precoStr = ativo.get("Preço").asText().replace(",", ".").trim();
                        try {
                            cotacaoMap.put("precoAtual", new BigDecimal(precoStr).setScale(2, RoundingMode.HALF_UP));
                        } catch (NumberFormatException e) {
                            // Ignora se não conseguir converter
                        }
                    }

                    // Variação
                    if (ativo.has("Variação")) {
                        String variacaoStr = ativo.get("Variação").asText().replace(",", ".").trim();
                        try {
                            cotacaoMap.put("variacao", new BigDecimal(variacaoStr).setScale(2, RoundingMode.HALF_UP));
                        } catch (NumberFormatException e) {
                            // Ignora se não conseguir converter
                        }
                    }

                    // Máximo
                    if (ativo.has("Máximo")) {
                        String maxStr = ativo.get("Máximo").asText().replace(",", ".").trim();
                        try {
                            cotacaoMap.put("precoMaximo", new BigDecimal(maxStr).setScale(2, RoundingMode.HALF_UP));
                        } catch (NumberFormatException e) {
                            // Ignora se não conseguir converter
                        }
                    }

                    // Mínimo
                    if (ativo.has("Mínimo")) {
                        String minStr = ativo.get("Mínimo").asText().replace(",", ".").trim();
                        try {
                            cotacaoMap.put("precoMinimo", new BigDecimal(minStr).setScale(2, RoundingMode.HALF_UP));
                        } catch (NumberFormatException e) {
                            // Ignora se não conseguir converter
                        }
                    }

                    // Nome
                    if (ativo.has("Nome")) {
                        cotacaoMap.put("nome", ativo.get("Nome").asText());
                    }

                    // Data/Hora
                    if (ativo.has("Data/Hora")) {
                        cotacaoMap.put("dataHora", ativo.get("Data/Hora").asText());
                    }

                    return cotacaoMap;
                }
            }

            return null;
        } catch (Exception e) {
            System.err.println("Erro ao buscar cotação completa do ativo " + codigoAtivo + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Atualiza o cache de cotações se o arquivo foi modificado
     */
    private void recarregarCotacoesSeNecessario() throws IOException {
        Resource resource = resourceLoader.getResource(JSON_PATH);
        long currentLastModified = resource.lastModified(); // Só funciona se o recurso for um arquivo real

        if (cotacoesCache == null || currentLastModified > lastModified) {
            cotacoesCache = carregarCotacoes(resource);
            lastModified = currentLastModified;
        }
    }

    /**
     * Lê o arquivo JSON e retorna um mapa de código → preço
     */
    private Map<String, BigDecimal> carregarCotacoes(Resource resource) throws IOException {
        JsonNode rootNode = objectMapper.readTree(resource.getInputStream());
        Map<String, BigDecimal> cotacoes = new HashMap<>();

        for (JsonNode ativo : rootNode) {
            // Tenta encontrar código em diferentes campos possíveis
            String codigo = null;
            if (ativo.has("Código")) {
                codigo = ativo.get("Código").asText().trim();
            } else if (ativo.has("Ação")) {
                codigo = ativo.get("Ação").asText().trim();
            } else if (ativo.has("Codigo")) {
                codigo = ativo.get("Codigo").asText().trim();
            }

            // Tenta encontrar preço em diferentes campos possíveis
            String precoStr = null;
            if (ativo.has("Preço")) {
                precoStr = ativo.get("Preço").asText().replace("R$", "").replace(",", ".").trim();
            } else if (ativo.has("Preço Atual")) {
                precoStr = ativo.get("Preço Atual").asText().replace("R$", "").replace(",", ".").trim();
            } else if (ativo.has("Preco Atual")) {
                precoStr = ativo.get("Preco Atual").asText().replace("R$", "").replace(",", ".").trim();
            }

            if (codigo != null && precoStr != null && !precoStr.isEmpty()) {
                try {
                    BigDecimal preco = new BigDecimal(precoStr)
                            .setScale(2, RoundingMode.HALF_UP); // Arredonda para 2 casas
                    cotacoes.put(codigo.toUpperCase(), preco);
                } catch (NumberFormatException e) {
                    System.err.println("Erro ao converter preço para '" + codigo + "': " + precoStr);
                }
            }
        }

        return cotacoes;
    }

    public List<List<Object>> readFromSheet() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readFromSheet'");
    }
}