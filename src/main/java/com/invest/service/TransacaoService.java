package com.invest.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invest.dto.TransacaoRequest;
import com.invest.model.Ativo;
import com.invest.model.Carteira;
import com.invest.model.TipoTransacao;
import com.invest.model.Transacao;
import com.invest.repository.AtivoRepository;
import com.invest.repository.CarteiraRepository;
import com.invest.repository.TransacaoRepository;

/**
 * Service para lógica de negócio das transações
 */
@Service
@Transactional
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private AtivoRepository ativoRepository;

    @Autowired
    @Lazy
    private CarteiraService carteiraService;

    /**
     * Cria uma nova transação
     */
    public Transacao createTransacao(Long carteiraId, TransacaoRequest request) {
        Carteira carteira = carteiraRepository.findById(carteiraId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada: " + carteiraId));

        Transacao transacao = new Transacao();
        transacao.setTipoTransacao(request.getTipoTransacao());
        transacao.setCodigoAtivo(request.getCodigoAtivo());
        transacao.setNomeAtivo(request.getNomeAtivo());
        transacao.setTipoAtivo(request.getTipoAtivo());
        transacao.setQuantidade(request.getQuantidade());
        transacao.setPrecoUnitario(request.getPrecoUnitario());
        transacao.setTaxasCorretagem(request.getTaxasCorretagem());
        transacao.setImpostos(request.getImpostos());
        transacao.setDataTransacao(request.getDataTransacao() != null ? request.getDataTransacao() : LocalDateTime.now());
        transacao.setDataLiquidacao(request.getDataLiquidacao());
        transacao.setObservacoes(request.getObservacoes());
        transacao.setCarteira(carteira);

        // Busca ou cria ativo relacionado
        Ativo ativo = buscarOuCriarAtivo(carteira, request);
        
        // Para novos ativos, atualiza a posição ANTES de salvar a transação
        // para garantir que o ativo tenha valores válidos (quantidade > 0)
        boolean isNovoAtivo = ativo.getId() == null;
        if (isNovoAtivo) {
            atualizarPosicaoAtivo(ativo, transacao);
        }
        
        transacao.setAtivo(ativo);
        Transacao savedTransacao = transacaoRepository.save(transacao);

        // Para ativos existentes, atualiza a posição após salvar a transação
        if (!isNovoAtivo) {
            atualizarPosicaoAtivo(ativo, transacao);
        }

        // Recalcula o valor atual da carteira após criar a transação
        carteiraService.calcularValorAtualCarteira(carteira);
        carteiraRepository.save(carteira);

        return savedTransacao;
    }

    /**
     * Atualiza uma transação existente
     */
    public Transacao updateTransacao(Long id, TransacaoRequest request) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada: " + id));

        // Reverte posição anterior
        reverterPosicaoAtivo(transacao);

        // Atualiza dados
        transacao.setTipoTransacao(request.getTipoTransacao());
        transacao.setCodigoAtivo(request.getCodigoAtivo());
        transacao.setNomeAtivo(request.getNomeAtivo());
        transacao.setTipoAtivo(request.getTipoAtivo());
        transacao.setQuantidade(request.getQuantidade());
        transacao.setPrecoUnitario(request.getPrecoUnitario());
        transacao.setTaxasCorretagem(request.getTaxasCorretagem());
        transacao.setImpostos(request.getImpostos());
        transacao.setDataTransacao(request.getDataTransacao() != null ? request.getDataTransacao() : transacao.getDataTransacao());
        transacao.setDataLiquidacao(request.getDataLiquidacao());
        transacao.setObservacoes(request.getObservacoes());

        Transacao savedTransacao = transacaoRepository.save(transacao);

        // Atualiza posição do ativo
        atualizarPosicaoAtivo(transacao.getAtivo(), transacao);

        // Recalcula o valor atual da carteira após atualizar a transação
        Carteira carteira = savedTransacao.getCarteira();
        carteiraService.calcularValorAtualCarteira(carteira);
        carteiraRepository.save(carteira);

        return savedTransacao;
    }

    /**
     * Deleta uma transação
     */
    public void deleteTransacao(Long id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada: " + id));

        Carteira carteira = transacao.getCarteira();

        // Reverte posição do ativo
        reverterPosicaoAtivo(transacao);

        transacaoRepository.deleteById(id);

        // Recalcula o valor atual da carteira após deletar a transação
        carteiraService.calcularValorAtualCarteira(carteira);
        carteiraRepository.save(carteira);
    }

    /**
     * Busca uma transação por ID com relações carregadas (carteira e ativo)
     */
    public Transacao getTransacaoById(Long id) {
        return transacaoRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada: " + id));
    }

    /**
     * Busca transações de uma carteira
     */
    public List<Transacao> getTransacoesByCarteira(Long carteiraId) {
        Carteira carteira = carteiraRepository.findById(carteiraId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada: " + carteiraId));

        return transacaoRepository.findByCarteira(carteira);
    }

    /**
     * Busca transações por tipo
     */
    public List<Transacao> getTransacoesByTipo(Long carteiraId, TipoTransacao tipoTransacao) {
        Carteira carteira = carteiraRepository.findById(carteiraId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada: " + carteiraId));

        return transacaoRepository.findByCarteiraAndTipoTransacao(carteira, tipoTransacao);
    }

    /**
     * Busca transações por ativo
     */
    public List<Transacao> getTransacoesByAtivo(Long carteiraId, String codigoAtivo) {
        Carteira carteira = carteiraRepository.findById(carteiraId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada: " + carteiraId));

        return transacaoRepository.findByCarteiraAndCodigoAtivo(carteira, codigoAtivo);
    }

    /**
     * Busca transações por período
     */
    public List<Transacao> getTransacoesByPeriodo(Long carteiraId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        Carteira carteira = carteiraRepository.findById(carteiraId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada: " + carteiraId));

        return transacaoRepository.findByCarteiraAndDataTransacaoBetween(carteira, dataInicio, dataFim);
    }

    /**
     * Calcula estatísticas de uma carteira
     */
    public CarteiraStats calcularEstatisticasCarteira(Long carteiraId) {
        Carteira carteira = carteiraRepository.findById(carteiraId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada: " + carteiraId));

        BigDecimal valorTotalCompras = transacaoRepository.calcularValorTotalCompras(carteira);
        BigDecimal valorTotalVendas = transacaoRepository.calcularValorTotalVendas(carteira);
        BigDecimal valorTotalProventos = transacaoRepository.calcularValorTotalProventos(carteira);

        return new CarteiraStats(valorTotalCompras, valorTotalVendas, valorTotalProventos);
    }

    /**
     * Busca ou cria ativo relacionado à transação
     */
    private Ativo buscarOuCriarAtivo(Carteira carteira, TransacaoRequest request) {
        Optional<Ativo> ativoExistente = ativoRepository.findByCodigoAndCarteira(request.getCodigoAtivo(), carteira);

        if (ativoExistente.isPresent()) {
            return ativoExistente.get();
        } else {
            // Cria novo ativo (não salva ainda - será salvo após atualizarPosicaoAtivo)
            Ativo novoAtivo = new Ativo();
            novoAtivo.setCodigo(request.getCodigoAtivo());
            novoAtivo.setNome(request.getNomeAtivo());
            novoAtivo.setTipo(request.getTipoAtivo());
            // Define valores iniciais para primeira compra
            // A quantidade será atualizada em atualizarPosicaoAtivo() antes de salvar
            novoAtivo.setQuantidade(BigDecimal.ZERO);
            // O preço de compra será calculado em atualizarPosicaoAtivo()
            // Por enquanto, define um valor positivo para passar na validação
            novoAtivo.setPrecoCompra(request.getPrecoUnitario());
            novoAtivo.setCarteira(carteira);
            // Não salva aqui - será salvo após atualizarPosicaoAtivo com valores válidos
            return novoAtivo;
        }
    }

    /**
     * Atualiza posição do ativo baseado na transação
     */
    private void atualizarPosicaoAtivo(Ativo ativo, Transacao transacao) {
        if (transacao.getTipoTransacao().isEntrada()) {
            // Aumenta quantidade
            BigDecimal quantidadeAnterior = ativo.getQuantidade();
            ativo.setQuantidade(quantidadeAnterior.add(transacao.getQuantidade()));
            
            // Atualiza preço médio se for compra
            if (transacao.getTipoTransacao() == TipoTransacao.COMPRA) {
                BigDecimal novoPrecoMedio = calcularPrecoMedio(ativo, transacao);
                ativo.setPrecoCompra(novoPrecoMedio);
            }
        } else if (transacao.getTipoTransacao().isSaida()) {
            // Diminui quantidade
            ativo.setQuantidade(ativo.getQuantidade().subtract(transacao.getQuantidade()));
        }
        // Proventos não alteram quantidade

        ativo.setDataAtualizacao(LocalDateTime.now());
        
        // Garante que o ativo tenha valores válidos antes de salvar
        // Se quantidade for zero ou negativa, não salva (mas isso não deveria acontecer)
        if (ativo.getQuantidade().compareTo(BigDecimal.ZERO) > 0 && 
            ativo.getPrecoCompra().compareTo(BigDecimal.ZERO) > 0) {
            ativoRepository.save(ativo);
        } else {
            throw new RuntimeException("Ativo com valores inválidos: quantidade=" + 
                ativo.getQuantidade() + ", precoCompra=" + ativo.getPrecoCompra());
        }
    }

    /**
     * Reverte posição do ativo (para atualizações/deleções)
     */
    private void reverterPosicaoAtivo(Transacao transacao) {
        if (transacao.getAtivo() != null) {
            Ativo ativo = transacao.getAtivo();
            
            if (transacao.getTipoTransacao().isEntrada()) {
                ativo.setQuantidade(ativo.getQuantidade().subtract(transacao.getQuantidade()));
            } else if (transacao.getTipoTransacao().isSaida()) {
                ativo.setQuantidade(ativo.getQuantidade().add(transacao.getQuantidade()));
            }
            
            ativo.setDataAtualizacao(LocalDateTime.now());
            ativoRepository.save(ativo);
        }
    }

    /**
     * Calcula preço médio ponderado
     * Para primeira compra, retorna o preço unitário da transação
     * Para compras subsequentes, calcula a média ponderada
     */
    private BigDecimal calcularPrecoMedio(Ativo ativo, Transacao transacao) {
        // Se é a primeira compra (quantidade atual é zero ou negativa após subtração)
        BigDecimal quantidadeAntesDaTransacao = ativo.getQuantidade().subtract(transacao.getQuantidade());
        
        if (quantidadeAntesDaTransacao.compareTo(BigDecimal.ZERO) <= 0) {
            // Primeira compra: preço médio é o preço da transação
            return transacao.getPrecoUnitario();
        }
        
        // Compra subsequente: calcula média ponderada
        BigDecimal valorAtual = quantidadeAntesDaTransacao.multiply(ativo.getPrecoCompra());
        BigDecimal valorNovaCompra = transacao.getQuantidade().multiply(transacao.getPrecoUnitario());
        BigDecimal quantidadeTotal = quantidadeAntesDaTransacao.add(transacao.getQuantidade());

        if (quantidadeTotal.compareTo(BigDecimal.ZERO) == 0) {
            return transacao.getPrecoUnitario(); // Fallback: retorna preço da transação
        }

        return valorAtual.add(valorNovaCompra)
                        .divide(quantidadeTotal, 4, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Classe para estatísticas da carteira
     */
    public static class CarteiraStats {
        private final BigDecimal valorTotalCompras;
        private final BigDecimal valorTotalVendas;
        private final BigDecimal valorTotalProventos;

        public CarteiraStats(BigDecimal valorTotalCompras, BigDecimal valorTotalVendas, BigDecimal valorTotalProventos) {
            this.valorTotalCompras = valorTotalCompras;
            this.valorTotalVendas = valorTotalVendas;
            this.valorTotalProventos = valorTotalProventos;
        }

        public BigDecimal getValorTotalCompras() {
            return valorTotalCompras;
        }

        public BigDecimal getValorTotalVendas() {
            return valorTotalVendas;
        }

        public BigDecimal getValorTotalProventos() {
            return valorTotalProventos;
        }

        public BigDecimal getValorLiquido() {
            return valorTotalVendas.add(valorTotalProventos).subtract(valorTotalCompras);
        }
    }
}
