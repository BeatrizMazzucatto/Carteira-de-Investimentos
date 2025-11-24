package com.invest.service;

import com.invest.dto.CarteiraRequest;
import com.invest.model.*;
import com.invest.repository.CarteiraRepository;
import com.invest.repository.AtivoRepository;
import com.invest.service.external.GoogleSheetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service para lógica de negócio das carteiras de investimentos
 * 
 * Agora integrado com cotacoes.json (leitura-only).
 * Atualizações do Google Sheets devem ser feitas externamente (ex: script Python).
 */
@Service
@Transactional
public class CarteiraService {

    @Autowired
    private CarteiraRepository carteiraRepository;
    
    @Autowired
    private GoogleSheetsService googleSheetsService;
    
    @Autowired
    private AtivoRepository ativoRepository;

    /**
     * Cria uma nova carteira para um investidor por ID
     */
    public Carteira createCarteira(Long investidorId, CarteiraRequest request) {
        Investidor investidor = new Investidor();
        investidor.setId(investidorId);
        return createCarteira(investidor, request);
    }

    /**
     * Cria uma nova carteira para um investidor
     */
    public Carteira createCarteira(Investidor investidor, CarteiraRequest request) {
        Carteira carteira = new Carteira();
        carteira.setNome(request.getNome());
        carteira.setDescricao(request.getDescricao());
        carteira.setObjetivo(request.getObjetivo());
        carteira.setPrazo(request.getPrazo());
        carteira.setPerfilRisco(request.getPerfilRisco());
        carteira.setValorInicial(request.getValorInicial());
        carteira.setGoogleSheetsId(request.getGoogleSheetsId());
        carteira.setInvestidor(investidor);
        
        return carteiraRepository.save(carteira);
    }

    /**
     * Atualiza uma carteira existente
     */
    public Carteira updateCarteira(Carteira carteira, CarteiraRequest request) {
        carteira.setNome(request.getNome());
        carteira.setDescricao(request.getDescricao());
        carteira.setObjetivo(request.getObjetivo());
        carteira.setPrazo(request.getPrazo());
        carteira.setPerfilRisco(request.getPerfilRisco());
        carteira.setValorInicial(request.getValorInicial());
        carteira.setGoogleSheetsId(request.getGoogleSheetsId());
        
        return carteiraRepository.save(carteira);
    }

    /**
     * Atualização parcial de uma carteira
     */
    public Carteira patchCarteira(Carteira carteira, CarteiraRequest request) {
        if (request.getNome() != null && !request.getNome().trim().isEmpty()) {
            carteira.setNome(request.getNome());
        }
        if (request.getDescricao() != null) {
            carteira.setDescricao(request.getDescricao());
        }
        if (request.getObjetivo() != null) {
            carteira.setObjetivo(request.getObjetivo());
        }
        if (request.getPrazo() != null) {
            carteira.setPrazo(request.getPrazo());
        }
        if (request.getPerfilRisco() != null) {
            carteira.setPerfilRisco(request.getPerfilRisco());
        }
        if (request.getValorInicial() != null) {
            carteira.setValorInicial(request.getValorInicial());
        }
        if (request.getGoogleSheetsId() != null) {
            carteira.setGoogleSheetsId(request.getGoogleSheetsId());
        }
        
        return carteiraRepository.save(carteira);
    }

    /**
     * Deleta uma carteira
     */
    public void deleteCarteira(Long id) {
        carteiraRepository.deleteById(id);
    }

    /**
     * Busca carteiras por investidor e recalcula o valor atual de cada uma
     */
    public List<Carteira> getCarteirasByInvestidor(Long investidorId) {
        Investidor investidor = new Investidor();
        investidor.setId(investidorId);
        List<Carteira> carteiras = carteiraRepository.findByInvestidor(investidor);
        // Recalcula o valor atual de cada carteira para garantir que está atualizado
        for (Carteira carteira : carteiras) {
            calcularValorAtualCarteira(carteira);
            carteiraRepository.save(carteira);
        }
        return carteiras;
    }

    /**
     * Busca carteira por ID
     */
    public Carteira getCarteiraById(Long id) {
        return carteiraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada: " + id));
    }

    /**
     * Atualiza carteira por ID
     */
    public Carteira updateCarteira(Long id, CarteiraRequest request) {
        Carteira carteira = getCarteiraById(id);
        return updateCarteira(carteira, request);
    }

    /**
     * Busca carteiras por objetivo
     */
    public List<Carteira> getCarteirasByObjetivo(Investidor investidor, String objetivo) {
        try {
            ObjetivoCarteira objetivoEnum = ObjetivoCarteira.valueOf(objetivo.toUpperCase());
            return carteiraRepository.findByInvestidorAndObjetivo(investidor, objetivoEnum);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Objetivo inválido: " + objetivo);
        }
    }

    /**
     * Busca carteiras por perfil de risco
     */
    public List<Carteira> getCarteirasByPerfil(Investidor investidor, String perfil) {
        try {
            PerfilRisco perfilEnum = PerfilRisco.valueOf(perfil.toUpperCase());
            return carteiraRepository.findByInvestidorAndPerfilRisco(investidor, perfilEnum);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Perfil inválido: " + perfil);
        }
    }

    /**
     * Atualiza preços de uma carteira com base no JSON local (cotacoes.json)
     */
    public void atualizarPrecosCarteira(Long carteiraId) {
        Carteira carteira = carteiraRepository.findById(carteiraId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada: " + carteiraId));

        try {
            // Força recarregamento do cache de cotações para garantir dados atualizados
            googleSheetsService.forcarRecarregamento();
            
            // Atualiza o preço atual de cada ativo com base no JSON
            for (Ativo ativo : carteira.getAtivos()) {
                String codigo = ativo.getCodigo();
                if (codigo != null) {
                    BigDecimal precoAtual = googleSheetsService.buscarPrecoAtivo(codigo);
                    if (precoAtual != null) {
                        ativo.setPrecoAtual(precoAtual);
                    } else {
                        System.out.println("⚠️ Preço não encontrado para o ativo: " + codigo);
                        // Opcional: manter preço anterior ou definir como null
                    }
                }
            }

            // Recalcula valor total da carteira
            calcularValorAtualCarteira(carteira);

            carteira.setDataAtualizacao(LocalDateTime.now());
            carteiraRepository.save(carteira);

            System.out.println("✅ Preços da carteira '" + carteira.getNome() + "' atualizados com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar preços da carteira: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar preços: " + e.getMessage());
        }
    }

    /**
     * Sincronização com Google Sheets foi desativada.
     * O Google Sheets é atualizado EXTERNAMENTE (ex: script Python).
     * Este método foi mantido apenas para compatibilidade, mas não faz nada.
     */
    public void sincronizarComGoogleSheets(Long carteiraId) {
        // Não há escrita no Google Sheets neste modelo
        System.out.println("ℹ️ Sincronização com Google Sheets desativada. " +
                "A planilha deve ser atualizada externamente.");
    }

    /**
     * Calcula o valor atual de uma carteira com base nos preços atuais dos ativos
     * Se o ativo não tiver precoAtual, usa o precoCompra
     */
    public void calcularValorAtualCarteira(Carteira carteira) {
        // Busca os ativos da carteira diretamente do banco para garantir dados atualizados
        List<Ativo> ativos = ativoRepository.findByCarteira(carteira);
        
        BigDecimal valorTotal = BigDecimal.ZERO;
        
        // Itera sobre os ativos da carteira
        for (Ativo ativo : ativos) {
            if (ativo.getQuantidade() != null && ativo.getQuantidade().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal valorAtivo = ativo.getValorTotalAtual(); // Usa precoAtual se disponível, senão precoCompra
                if (valorAtivo != null && valorAtivo.compareTo(BigDecimal.ZERO) > 0) {
                    valorTotal = valorTotal.add(valorAtivo);
                }
            }
        }
        
        carteira.setValorAtual(valorTotal);
    }

    /**
     * Busca carteiras que precisam de atualização (ex: não atualizadas nas últimas 24h)
     */
    public List<Carteira> getCarteirasParaAtualizacao() {
        LocalDateTime dataLimite = LocalDateTime.now().minusHours(24);
        return carteiraRepository.findCarteirasParaAtualizacao(dataLimite);
    }
}