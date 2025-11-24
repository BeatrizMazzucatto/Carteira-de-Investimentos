package com.invest.controller;

import com.invest.dto.CarteiraRentabilidadeResponse;
import com.invest.dto.RentabilidadeResponse;
import com.invest.service.RentabilidadeService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para cálculos de rentabilidade
 */
@RestController
@RequestMapping("/api/rentabilidade")
@CrossOrigin(origins = "*")
public class RentabilidadeController {

    @Autowired
    private RentabilidadeService rentabilidadeService;

    @Operation(summary = "Calcula rentabilidade de um ativo",
               description = "Retorna a rentabilidade detalhada de um ativo específico pelo seu ID")
    @GetMapping("/ativo/{ativoId}")
    public ResponseEntity<RentabilidadeResponse> getRentabilidadeAtivo(@PathVariable Long ativoId) {
        try {
            RentabilidadeResponse rentabilidade = rentabilidadeService.calcularRentabilidadeAtivo(ativoId);
            return ResponseEntity.ok(rentabilidade);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Calcula rentabilidade completa da carteira",
               description = "Retorna todos os dados de rentabilidade de uma carteira")
    @GetMapping("/carteira/{carteiraId}")
    public ResponseEntity<CarteiraRentabilidadeResponse> getRentabilidadeCarteira(@PathVariable Long carteiraId) {
        try {
            CarteiraRentabilidadeResponse rentabilidade = rentabilidadeService.calcularRentabilidadeCarteira(carteiraId);
            return ResponseEntity.ok(rentabilidade);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Calcula resumo da rentabilidade da carteira",
               description = "Retorna um resumo consolidado da rentabilidade da carteira")
    @GetMapping("/carteira/{carteiraId}/resumo")
    public ResponseEntity<RentabilidadeResponse> getResumoRentabilidadeCarteira(@PathVariable Long carteiraId) {
        try {
            CarteiraRentabilidadeResponse carteiraRentabilidade = rentabilidadeService.calcularRentabilidadeCarteira(carteiraId);
            
            // Cria resumo da carteira como um "ativo"
            RentabilidadeResponse resumo = new RentabilidadeResponse();
            resumo.setCodigoAtivo("CARTEIRA");
            resumo.setNomeAtivo(carteiraRentabilidade.getCarteiraNome());
            resumo.setValorTotalInvestido(carteiraRentabilidade.getValorTotalInvestido());
            resumo.setValorAtualMercado(carteiraRentabilidade.getValorAtualMercado());
            resumo.setValorAtualComProventos(carteiraRentabilidade.getValorAtualComProventos());
            resumo.setRentabilidadeBruta(carteiraRentabilidade.getRentabilidadeBruta());
            resumo.setRentabilidadeLiquida(carteiraRentabilidade.getRentabilidadeLiquida());
            resumo.setRentabilidadePercentualBruta(carteiraRentabilidade.getRentabilidadePercentualBruta());
            resumo.setRentabilidadePercentualLiquida(carteiraRentabilidade.getRentabilidadePercentualLiquida());
            resumo.setRentabilidadePercentualAnual(carteiraRentabilidade.getRentabilidadePercentualAnual());
            resumo.setTotalTaxasCorretagem(carteiraRentabilidade.getTotalTaxasCorretagem());
            resumo.setTotalImpostos(carteiraRentabilidade.getTotalImpostos());
            resumo.setTotalCustos(carteiraRentabilidade.getTotalCustos());
            resumo.setDataAtualizacao(carteiraRentabilidade.getDataUltimaAtualizacao());
            
            return ResponseEntity.ok(resumo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Lista rentabilidade de todos os ativos da carteira",
               description = "Retorna a rentabilidade detalhada de todos os ativos de uma carteira")
    @GetMapping("/carteira/{carteiraId}/ativos")
    public ResponseEntity<List<RentabilidadeResponse>> getRentabilidadeAtivosCarteira(@PathVariable Long carteiraId) {
        try {
            CarteiraRentabilidadeResponse carteiraRentabilidade = rentabilidadeService.calcularRentabilidadeCarteira(carteiraId);
            return ResponseEntity.ok(carteiraRentabilidade.getAtivos());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Calcula rentabilidade por tipo de ativo",
               description = "Filtra os ativos da carteira por tipo e retorna suas rentabilidades")
    @GetMapping("/carteira/{carteiraId}/tipo/{tipoAtivo}")
    public ResponseEntity<List<RentabilidadeResponse>> getRentabilidadePorTipo(
            @PathVariable Long carteiraId, 
            @PathVariable String tipoAtivo) {
        try {
            CarteiraRentabilidadeResponse carteiraRentabilidade = rentabilidadeService.calcularRentabilidadeCarteira(carteiraId);
            
            List<RentabilidadeResponse> ativosFiltrados = carteiraRentabilidade.getAtivos().stream()
                    .filter(ativo -> ativo.getNomeAtivo().toUpperCase().contains(tipoAtivo.toUpperCase()))
                    .toList();
            
            return ResponseEntity.ok(ativosFiltrados);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Calcula ativos com rentabilidade positiva",
               description = "Retorna apenas os ativos que possuem rentabilidade líquida maior que zero")
    @GetMapping("/carteira/{carteiraId}/positivos")
    public ResponseEntity<List<RentabilidadeResponse>> getAtivosPositivos(@PathVariable Long carteiraId) {
        try {
            CarteiraRentabilidadeResponse carteiraRentabilidade = rentabilidadeService.calcularRentabilidadeCarteira(carteiraId);
            
            List<RentabilidadeResponse> ativosPositivos = carteiraRentabilidade.getAtivos().stream()
                    .filter(ativo -> ativo.getRentabilidadeLiquida() != null && 
                                   ativo.getRentabilidadeLiquida().compareTo(java.math.BigDecimal.ZERO) > 0)
                    .toList();
            
            return ResponseEntity.ok(ativosPositivos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Calcula ativos com rentabilidade negativa",
               description = "Retorna apenas os ativos que possuem rentabilidade líquida menor que zero")
    @GetMapping("/carteira/{carteiraId}/negativos")
    public ResponseEntity<List<RentabilidadeResponse>> getAtivosNegativos(@PathVariable Long carteiraId) {
        try {
            CarteiraRentabilidadeResponse carteiraRentabilidade = rentabilidadeService.calcularRentabilidadeCarteira(carteiraId);
            
            List<RentabilidadeResponse> ativosNegativos = carteiraRentabilidade.getAtivos().stream()
                    .filter(ativo -> ativo.getRentabilidadeLiquida() != null && 
                                   ativo.getRentabilidadeLiquida().compareTo(java.math.BigDecimal.ZERO) < 0)
                    .toList();
            
            return ResponseEntity.ok(ativosNegativos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Calcula top performers da carteira",
               description = "Retorna os N melhores ativos da carteira de acordo com rentabilidade líquida")
    @GetMapping("/carteira/{carteiraId}/top/{limit}")
    public ResponseEntity<List<RentabilidadeResponse>> getTopPerformers(
            @PathVariable Long carteiraId, 
            @PathVariable int limit) {
        try {
            CarteiraRentabilidadeResponse carteiraRentabilidade = rentabilidadeService.calcularRentabilidadeCarteira(carteiraId);
            
            List<RentabilidadeResponse> topPerformers = carteiraRentabilidade.getAtivos().stream()
                    .filter(ativo -> ativo.getRentabilidadePercentualLiquida() != null)
                    .sorted((a, b) -> b.getRentabilidadePercentualLiquida().compareTo(a.getRentabilidadePercentualLiquida()))
                    .limit(limit)
                    .toList();
            
            return ResponseEntity.ok(topPerformers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Calcula piores performers da carteira",
               description = "Retorna os N piores ativos da carteira de acordo com rentabilidade líquida")
    @GetMapping("/carteira/{carteiraId}/piores/{limit}")
    public ResponseEntity<List<RentabilidadeResponse>> getPioresPerformers(
            @PathVariable Long carteiraId, 
            @PathVariable int limit) {
        try {
            CarteiraRentabilidadeResponse carteiraRentabilidade = rentabilidadeService.calcularRentabilidadeCarteira(carteiraId);
            
            List<RentabilidadeResponse> pioresPerformers = carteiraRentabilidade.getAtivos().stream()
                    .filter(ativo -> ativo.getRentabilidadePercentualLiquida() != null)
                    .sorted((a, b) -> a.getRentabilidadePercentualLiquida().compareTo(b.getRentabilidadePercentualLiquida()))
                    .limit(limit)
                    .toList();
            
            return ResponseEntity.ok(pioresPerformers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Calcula distribuição por tipo de ativo",
               description = "Retorna a distribuição percentual da carteira por tipo de ativo")
    @GetMapping("/carteira/{carteiraId}/distribuicao")
    public ResponseEntity<CarteiraRentabilidadeResponse> getDistribuicaoPorTipo(@PathVariable Long carteiraId) {
        try {
            CarteiraRentabilidadeResponse rentabilidade = rentabilidadeService.calcularRentabilidadeCarteira(carteiraId);
            
            // Retorna apenas os dados de distribuição
            CarteiraRentabilidadeResponse distribuicao = new CarteiraRentabilidadeResponse();
            distribuicao.setCarteiraId(rentabilidade.getCarteiraId());
            distribuicao.setCarteiraNome(rentabilidade.getCarteiraNome());
            distribuicao.setPercentualAcoes(rentabilidade.getPercentualAcoes());
            distribuicao.setPercentualFIIs(rentabilidade.getPercentualFIIs());
            distribuicao.setPercentualETFs(rentabilidade.getPercentualETFs());
            distribuicao.setPercentualRendaFixa(rentabilidade.getPercentualRendaFixa());
            distribuicao.setPercentualCripto(rentabilidade.getPercentualCripto());
            distribuicao.setValorAtualMercado(rentabilidade.getValorAtualMercado());
            
            return ResponseEntity.ok(distribuicao);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Calcula métricas de risco da carteira",
               description = "Retorna apenas as métricas de risco da carteira, como volatilidade, Sharpe Ratio, Max Drawdown e VAR 95")
    @GetMapping("/carteira/{carteiraId}/risco")
    public ResponseEntity<CarteiraRentabilidadeResponse> getMetricasRisco(@PathVariable Long carteiraId) {
        try {
            CarteiraRentabilidadeResponse rentabilidade = rentabilidadeService.calcularRentabilidadeCarteira(carteiraId);
            
            // Retorna apenas as métricas de risco
            CarteiraRentabilidadeResponse risco = new CarteiraRentabilidadeResponse();
            risco.setCarteiraId(rentabilidade.getCarteiraId());
            risco.setCarteiraNome(rentabilidade.getCarteiraNome());
            risco.setVolatilidade(rentabilidade.getVolatilidade());
            risco.setSharpeRatio(rentabilidade.getSharpeRatio());
            risco.setMaxDrawdown(rentabilidade.getMaxDrawdown());
            risco.setVar95(rentabilidade.getVar95());
            risco.setRentabilidadePercentualLiquida(rentabilidade.getRentabilidadePercentualLiquida());
            
            return ResponseEntity.ok(risco);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
