package com.invest.exception;

/**
 * exceção para recursos não encontrados
 * 
 * Exception para quando um recurso não é encontrado
 * serve pra quando os controllers usarem para retornar HTTP 404
 * adaptação do contacts
 */

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
