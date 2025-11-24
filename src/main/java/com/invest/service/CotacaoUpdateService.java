package com.invest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invest.service.external.GoogleSheetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Serviço responsável por atualizar automaticamente o arquivo JSON de cotações
 * a partir do Google Sheets a cada hora
 */
@Service
public class CotacaoUpdateService {

    private static final String URL_CSV = "https://docs.google.com/spreadsheets/d/1Zyzbrjd7mAFDaEKaXURGzA0o0cDA4p35MCcDW-2mwo8/export?format=csv&gid=1706485275";
    
    @Autowired
    private GoogleSheetsService googleSheetsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HistoricoCotacaoService historicoCotacaoService;

    /**
     * Atualiza o arquivo JSON de cotações automaticamente a cada hora
     * Executa no minuto 0 de cada hora (ex: 10:00, 11:00, 12:00)
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void atualizarCotacoesAutomaticamente() {
        System.out.println("🔄 Iniciando atualização automática de cotações...");
        try {
            atualizarCotacoes();
            System.out.println("✅ Atualização automática concluída com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ Erro na atualização automática de cotações: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Atualiza o arquivo JSON de cotações manualmente
     */
    public void atualizarCotacoes() throws Exception {
        // Busca dados do Google Sheets
        List<Map<String, String>> dados = buscarDadosDoGoogleSheets();
        
        // Salva apenas em resources (onde o Spring Boot lê)
        salvarJson(dados, "src/main/resources/data/cotacoes.json");
        
        // Força recarregamento do cache
        googleSheetsService.forcarRecarregamento();
        
        // Salva histórico de cotações
        historicoCotacaoService.salvarHistoricoAtualizacao();
        
        System.out.println("✅ " + dados.size() + " cotações atualizadas com sucesso!");
    }

    /**
     * Busca dados do Google Sheets via CSV
     */
    private List<Map<String, String>> buscarDadosDoGoogleSheets() throws Exception {
        URI uri = new URI(URL_CSV);
        List<Map<String, String>> dados = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(uri.toURL().openStream(), StandardCharsets.UTF_8))) {
            
            String linha = reader.readLine(); // Lê cabeçalho
            if (linha == null) {
                throw new Exception("CSV vazio ou inválido");
            }
            
            String[] colunas = linha.split(",");
            
            while ((linha = reader.readLine()) != null) {
                Map<String, String> registro = new LinkedHashMap<>();
                String[] valores = parseCSVLine(linha);
                
                for (int i = 0; i < colunas.length && i < valores.length; i++) {
                    String coluna = colunas[i].trim().replace("\"", "");
                    String valor = valores[i].trim().replace("\"", "");
                    registro.put(coluna, valor);
                }
                
                // Adiciona timestamp de atualização
                registro.put("atualizado_em", LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                
                dados.add(registro);
            }
        }
        
        return dados;
    }

    /**
     * Faz parse de uma linha CSV considerando valores entre aspas
     * Usa uma abordagem mais robusta para lidar com vírgulas dentro de valores entre aspas
     */
    private String[] parseCSVLine(String linha) {
        List<String> valores = new ArrayList<>();
        boolean dentroAspas = false;
        StringBuilder valorAtual = new StringBuilder();
        
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            
            if (c == '"') {
                // Verifica se é uma aspa dupla escapada ("")
                if (i + 1 < linha.length() && linha.charAt(i + 1) == '"' && dentroAspas) {
                    valorAtual.append('"');
                    i++; // Pula a próxima aspa
                } else {
                    dentroAspas = !dentroAspas;
                }
            } else if (c == ',' && !dentroAspas) {
                valores.add(valorAtual.toString().trim());
                valorAtual = new StringBuilder();
            } else {
                valorAtual.append(c);
            }
        }
        // Adiciona o último valor
        valores.add(valorAtual.toString().trim());
        
        return valores.toArray(new String[0]);
    }

    /**
     * Salva os dados em formato JSON no caminho especificado
     */
    private void salvarJson(List<Map<String, String>> dados, String caminhoRelativo) throws Exception {
        // Obtém o diretório de trabalho atual (raiz do projeto)
        String diretorioAtual = System.getProperty("user.dir");
        Path caminhoCompleto = Paths.get(diretorioAtual, caminhoRelativo);
        
        // Cria diretórios se não existirem
        Files.createDirectories(caminhoCompleto.getParent());
        
        // Salva o JSON
        try (FileWriter writer = new FileWriter(caminhoCompleto.toFile(), StandardCharsets.UTF_8)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, dados);
        }
        
        System.out.println("📄 JSON salvo em: " + caminhoCompleto.toAbsolutePath());
    }
}

