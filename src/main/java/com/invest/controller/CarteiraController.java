package com.invest.controller;

import com.invest.dto.CarteiraRequest;
import com.invest.dto.CarteiraResponse;
import com.invest.dto.AtivoResponse;
import com.invest.exception.ResourceNotFoundException;
import com.invest.model.Carteira;
import com.invest.model.Investidor;
import com.invest.model.Ativo;
import com.invest.repository.CarteiraRepository;
import com.invest.repository.InvestidorRepository;
import com.invest.repository.AtivoRepository;
import com.invest.service.CarteiraService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST para gerenciar carteiras de investimentos
 * Permite que cada investidor gerencie múltiplas carteiras
 */
@RestController
@RequestMapping("/api/carteiras")
@CrossOrigin(origins = "*")
public class CarteiraController {

    @Autowired
    private CarteiraService carteiraService;

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private InvestidorRepository investidorRepository;

    @Autowired
    private AtivoRepository ativoRepository;

    @Operation(summary = "Lista todas as carteiras de um investidor",
               description = "Retorna uma página de carteiras de um investidor específico, com paginação e ordenação")
    @GetMapping("/investidor/{investidorId}")
    public Page<CarteiraResponse> getCarteirasByInvestidor(
            @PathVariable Long investidorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sort) {
        
        Investidor investidor = investidorRepository.findById(investidorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investidor não encontrado: " + investidorId));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Carteira> carteiras = carteiraRepository.findByInvestidor(investidor, pageable);
        
        return carteiras.map(this::convertToResponse);
    }

    @Operation(summary = "Busca uma carteira específica",
               description = "Retorna os detalhes de uma carteira pelo seu ID")
    @GetMapping("/{id}")
    public CarteiraResponse getCarteiraById(@PathVariable Long id) {
        Carteira carteira = carteiraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada: " + id));
        
        return convertToResponse(carteira);
    }

    @Operation(summary = "Lista carteiras por objetivo",
               description = "Retorna todas as carteiras de um investidor que correspondem a um objetivo específico")
    @GetMapping("/investidor/{investidorId}/objetivo/{objetivo}")
    public List<CarteiraResponse> getCarteirasByObjetivo(
            @PathVariable Long investidorId,
            @PathVariable String objetivo) {
        
        Investidor investidor = investidorRepository.findById(investidorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investidor não encontrado: " + investidorId));
        
        return carteiraService.getCarteirasByObjetivo(investidor, objetivo)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Lista carteiras por perfil de risco",
               description = "Retorna todas as carteiras de um investidor que correspondem a um perfil de risco específico")
    @GetMapping("/investidor/{investidorId}/perfil/{perfil}")
    public List<CarteiraResponse> getCarteirasByPerfil(
            @PathVariable Long investidorId,
            @PathVariable String perfil) {
        
        Investidor investidor = investidorRepository.findById(investidorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investidor não encontrado: " + investidorId));
        
        return carteiraService.getCarteirasByPerfil(investidor, perfil)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Cria uma nova carteira",
               description = "Cria uma carteira para um investidor específico usando os dados enviados no corpo da requisição")
    @PostMapping("/investidor/{investidorId}")
    public ResponseEntity<CarteiraResponse> createCarteira(
            @PathVariable Long investidorId,
            @RequestBody @Valid CarteiraRequest carteiraRequest) {
        
        Investidor investidor = investidorRepository.findById(investidorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investidor não encontrado: " + investidorId));
        
        Carteira carteira = carteiraService.createCarteira(investidor, carteiraRequest);
        CarteiraResponse response = convertToResponse(carteira);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Atualiza uma carteira inteira",
               description = "Atualiza todos os campos de uma carteira existente pelo ID")
    @PutMapping("/{id}")
    public CarteiraResponse updateCarteira(
            @PathVariable Long id,
            @RequestBody @Valid CarteiraRequest carteiraRequest) {
        
        Carteira carteira = carteiraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada: " + id));
        
        carteira = carteiraService.updateCarteira(carteira, carteiraRequest);
        return convertToResponse(carteira);
    }

    @Operation(summary = "Atualização parcial de uma carteira",
               description = "Atualiza apenas os campos enviados de uma carteira existente pelo ID")
    @PatchMapping("/{id}")
    public CarteiraResponse patchCarteira(
            @PathVariable Long id,
            @RequestBody CarteiraRequest carteiraRequest) {
        
        Carteira carteira = carteiraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada: " + id));
        
        carteira = carteiraService.patchCarteira(carteira, carteiraRequest);
        return convertToResponse(carteira);
    }

    @Operation(summary = "Deleta uma carteira",
               description = "Exclui uma carteira pelo seu ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarteira(@PathVariable Long id) {
        if (!carteiraRepository.existsById(id)) {
            throw new ResourceNotFoundException("Carteira não encontrada: " + id);
        }
        
        carteiraService.deleteCarteira(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualiza preços de uma carteira",
               description = "Atualiza os preços de uma carteira específica usando dados do JSON ou serviço externo")
    @PostMapping("/{id}/atualizar-precos")
    public ResponseEntity<String> atualizarPrecos(@PathVariable Long id) {
        try {
            carteiraService.atualizarPrecosCarteira(id);
            return ResponseEntity.ok("Preços da carteira atualizados com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar preços: " + e.getMessage());
        }
    }

    @Operation(summary = "Sincroniza carteira com Google Sheets",
               description = "Sincroniza uma carteira específica com os dados do Google Sheets")
    @PostMapping("/{id}/sincronizar-sheets")
    public ResponseEntity<String> sincronizarComSheets(@PathVariable Long id) {
        try {
            carteiraService.sincronizarComGoogleSheets(id);
            return ResponseEntity.ok("Carteira sincronizada com Google Sheets!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro na sincronização: " + e.getMessage());
        }
    }

    /**
     * Converte entidade Carteira para DTO de resposta
     */
    private CarteiraResponse convertToResponse(Carteira carteira) {
        CarteiraResponse response = new CarteiraResponse(
                carteira.getId(),
                carteira.getNome(),
                carteira.getDescricao(),
                carteira.getObjetivo(),
                carteira.getPerfilRisco(),
                carteira.getValorInicial(),
                carteira.getValorAtual(),
                carteira.getDataCriacao(),
                carteira.getDataAtualizacao(),
                carteira.getGoogleSheetsId(),
                carteira.getInvestidor().getId(),
                carteira.getInvestidor().getNome()
        );
        
        response.setPrazo(carteira.getPrazo());
        
        // Calcular variação percentual
        if (carteira.getValorInicial() != null && carteira.getValorInicial().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal variacao = carteira.getValorAtual()
                .subtract(carteira.getValorInicial())
                .divide(carteira.getValorInicial(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
            response.setVariacaoPercentual(variacao);
        }
        
        // Busca e popula ativos da carteira para evitar LazyInitializationException
        try {
            List<Ativo> ativos = ativoRepository.findByCarteira(carteira);
            List<AtivoResponse> ativosResponse = ativos.stream()
                    .map(this::convertToAtivoResponse)
                    .collect(Collectors.toList());
            response.setAtivos(ativosResponse);
            response.setTotalAtivos(ativosResponse.size());
        } catch (Exception e) {
            // Se houver erro ao buscar ativos, define como lista vazia
            response.setAtivos(new ArrayList<>());
            response.setTotalAtivos(0);
        }
        
        return response;
    }

    /**
     * Converte entidade Ativo para DTO de resposta
     */
    private AtivoResponse convertToAtivoResponse(Ativo ativo) {
        AtivoResponse ativoResponse = new AtivoResponse(
                ativo.getId(),
                ativo.getCodigo(),
                ativo.getNome(),
                ativo.getTipo(),
                ativo.getQuantidade(),
                ativo.getPrecoCompra(),
                ativo.getPrecoAtual(),
                ativo.getDataCompra(),
                ativo.getDataAtualizacao(),
                ativo.getCarteira().getId(),
                ativo.getCarteira().getNome()
        );
        
        // Calcula valores adicionais
        ativoResponse.setValorTotalCompra(ativo.getValorTotalCompra());
        ativoResponse.setValorTotalAtual(ativo.getValorTotalAtual());
        ativoResponse.setVariacaoPercentual(ativo.getVariacaoPercentual());
        
        return ativoResponse;
    }
}
