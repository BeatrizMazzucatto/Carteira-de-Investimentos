package com.invest.controller;

/**
 * Controller REST para gerenciar investidores
 * 
 * adaptação do contacts
 * CRUD
 * Usa DTOs para responses 
 */

import com.invest.dto.InvestidorResponse;
import com.invest.exception.ResourceNotFoundException;
import com.invest.model.Investidor;
import com.invest.repository.InvestidorRepository;

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

@RestController
@RequestMapping("/api/investidores")
@CrossOrigin(origins = "*")
public class InvestidorControllerAdaptado {

    @Autowired
    private InvestidorRepository investidorRepository;

    @Operation(summary = "Lista todos os investidores",
               description = "Retorna uma página de investidores com paginação e ordenação")
    @GetMapping
    public Page<InvestidorResponse> getAllInvestidores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sort) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Investidor> investidores = investidorRepository.findAll(pageable);
        
        return investidores.map(investidor -> new InvestidorResponse(
                investidor.getId(),
                investidor.getNome(),
                investidor.getEmail(),
                investidor.getDataCriacao()
        ));
    }

    @Operation(summary = "Busca um investidor pelo ID",
               description = "Retorna os detalhes de um investidor específico pelo seu ID")
    @GetMapping("/{id}")
    public InvestidorResponse getInvestidorById(@PathVariable Long id) {
        Investidor investidor = investidorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Investidor não encontrado: " + id));

        return new InvestidorResponse(
                investidor.getId(),
                investidor.getNome(),
                investidor.getEmail(),
                investidor.getDataCriacao()
        );
    }

    @Operation(summary = "Busca investidores pelo nome",
               description = "Retorna uma página de investidores cujo nome contém o valor informado")
    @GetMapping("/search")
    public Page<InvestidorResponse> searchInvestidoresByName(
            @RequestParam String nome, 
            Pageable pageable) {
        return investidorRepository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(investidor -> new InvestidorResponse(
                        investidor.getId(),
                        investidor.getNome(),
                        investidor.getEmail(),
                        investidor.getDataCriacao()
                ));
    }

     @Operation(summary = "Cria um novo investidor",
               description = "Cria um investidor usando os dados enviados e valida se o email já existe")
    @PostMapping
    public ResponseEntity<InvestidorResponse> createInvestidor(@RequestBody @Valid Investidor investidor) {
        // Validação adicional de email único
        if (investidorRepository.existsByEmail(investidor.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        
        Investidor saved = investidorRepository.save(investidor);
        InvestidorResponse response = new InvestidorResponse(
                saved.getId(),
                saved.getNome(),
                saved.getEmail(),
                saved.getDataCriacao()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Atualiza um investidor inteiro",
               description = "Atualiza todos os campos de um investidor existente pelo ID")
    @PutMapping("/{id}")
    public InvestidorResponse updateInvestidor(@PathVariable Long id, @RequestBody @Valid Investidor updatedInvestidor) {
        Investidor existingInvestidor = investidorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Investidor não encontrado: " + id));

        // Verificar se o email já existe em outro investidor
        if (!existingInvestidor.getEmail().equals(updatedInvestidor.getEmail()) 
            && investidorRepository.existsByEmail(updatedInvestidor.getEmail())) {
            throw new RuntimeException("Já existe um investidor com este email");
        }

        existingInvestidor.setNome(updatedInvestidor.getNome());
        existingInvestidor.setEmail(updatedInvestidor.getEmail());

        Investidor saved = investidorRepository.save(existingInvestidor);
        return new InvestidorResponse(
                saved.getId(),
                saved.getNome(),
                saved.getEmail(),
                saved.getDataCriacao()
        );
    }

    @Operation(summary = "Atualiza parcialmente um investidor",
               description = "Atualiza apenas os campos enviados de um investidor existente pelo ID")
    @PatchMapping("/{id}")
    public InvestidorResponse patchInvestidor(@PathVariable Long id, @RequestBody Investidor updatedInvestidor) {
        Investidor existingInvestidor = investidorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Investidor não encontrado: " + id));

        // Apenas atualizar campos não nulos (patch parcial)
        if (updatedInvestidor.getNome() != null && !updatedInvestidor.getNome().trim().isEmpty()) {
            existingInvestidor.setNome(updatedInvestidor.getNome());
        }
        if (updatedInvestidor.getEmail() != null && !updatedInvestidor.getEmail().trim().isEmpty()) {
            // Verificar se o email já existe
            if (!existingInvestidor.getEmail().equals(updatedInvestidor.getEmail()) 
                && investidorRepository.existsByEmail(updatedInvestidor.getEmail())) {
                throw new RuntimeException("Já existe um investidor com este email");
            }
            existingInvestidor.setEmail(updatedInvestidor.getEmail());
        }

        Investidor saved = investidorRepository.save(existingInvestidor);
        return new InvestidorResponse(
                saved.getId(),
                saved.getNome(),
                saved.getEmail(),
                saved.getDataCriacao()
        );
    }

    @Operation(summary = "Deleta um investidor",
               description = "Remove um investidor pelo seu ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvestidor(@PathVariable Long id) {
        if (!investidorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Investidor não encontrado: " + id);
        }
        
        investidorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
