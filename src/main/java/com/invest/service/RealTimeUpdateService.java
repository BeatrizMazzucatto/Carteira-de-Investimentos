package com.invest.service;

import com.invest.dto.RelatorioEmpresaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Serviço para atualizações em tempo real via WebSocket
 * Envia atualizações de cotações e relatórios para clientes conectados
 * 
 * Só executa quando app.realtime.enabled=true (padrão em modo servidor)
 */
@Service
@ConditionalOnProperty(name = "app.realtime.enabled", havingValue = "true", matchIfMissing = false)
public class RealTimeUpdateService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Value("${app.realtime.cotacoes.interval:300000}")
    private long cotacoesInterval;

    @Value("${app.realtime.relatorio.interval:600000}")
    private long relatorioInterval;

    @Autowired
    private CotacaoUpdateService cotacaoUpdateService;

    @Autowired
    private RelatorioEmpresaService relatorioEmpresaService;

    /**
     * Atualiza cotações e envia para clientes conectados
     * Intervalo configurável via app.realtime.cotacoes.interval (padrão: 5 minutos)
     */
    @Scheduled(fixedRateString = "${app.realtime.cotacoes.interval:300000}")
    public void atualizarCotacoesTempoReal() {
        try {
            // Atualiza cotações
            cotacaoUpdateService.atualizarCotacoes();
            
            // Envia notificação para clientes
            messagingTemplate.convertAndSend("/topic/cotacoes/atualizacao", 
                new AtualizacaoMensagem("Cotações atualizadas com sucesso", System.currentTimeMillis()));
            
        } catch (Exception e) {
            System.err.println("Erro ao atualizar cotações em tempo real: " + e.getMessage());
            messagingTemplate.convertAndSend("/topic/cotacoes/erro", 
                new ErroMensagem("Erro ao atualizar cotações: " + e.getMessage()));
        }
    }

    /**
     * Gera e envia relatório da empresa
     * Intervalo configurável via app.realtime.relatorio.interval (padrão: 10 minutos)
     */
    @Scheduled(fixedRateString = "${app.realtime.relatorio.interval:600000}")
    public void gerarRelatorioEmpresaTempoReal() {
        try {
            RelatorioEmpresaResponse relatorio = relatorioEmpresaService.gerarRelatorioEmpresa();
            
            // Envia relatório para clientes conectados
            messagingTemplate.convertAndSend("/topic/relatorio/empresa", relatorio);
            
        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório em tempo real: " + e.getMessage());
            messagingTemplate.convertAndSend("/topic/relatorio/erro", 
                new ErroMensagem("Erro ao gerar relatório: " + e.getMessage()));
        }
    }

    /**
     * Envia atualização manual de cotações
     */
    public void enviarAtualizacaoCotacoes() {
        try {
            cotacaoUpdateService.atualizarCotacoes();
            messagingTemplate.convertAndSend("/topic/cotacoes/atualizacao", 
                new AtualizacaoMensagem("Cotações atualizadas manualmente", System.currentTimeMillis()));
        } catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/cotacoes/erro", 
                new ErroMensagem("Erro ao atualizar: " + e.getMessage()));
        }
    }

    /**
     * Envia relatório manual da empresa
     */
    public void enviarRelatorioEmpresa() {
        try {
            RelatorioEmpresaResponse relatorio = relatorioEmpresaService.gerarRelatorioEmpresa();
            messagingTemplate.convertAndSend("/topic/relatorio/empresa", relatorio);
        } catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/relatorio/erro", 
                new ErroMensagem("Erro ao gerar relatório: " + e.getMessage()));
        }
    }

    /**
     * Classe para mensagens de atualização
     */
    public static class AtualizacaoMensagem {
        private String mensagem;
        private long timestamp;

        public AtualizacaoMensagem(String mensagem, long timestamp) {
            this.mensagem = mensagem;
            this.timestamp = timestamp;
        }

        public String getMensagem() {
            return mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    /**
     * Classe para mensagens de erro
     */
    public static class ErroMensagem {
        private String erro;
        private long timestamp;

        public ErroMensagem(String erro) {
            this.erro = erro;
            this.timestamp = System.currentTimeMillis();
        }

        public String getErro() {
            return erro;
        }

        public void setErro(String erro) {
            this.erro = erro;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}

