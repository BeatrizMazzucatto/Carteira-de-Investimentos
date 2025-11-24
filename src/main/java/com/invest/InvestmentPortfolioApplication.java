package com.invest;

/**
 * Classe principal da aplicação Spring Boot
 * 
 * Coemço da aplicação de gestão de carteiras de investimentos
 * Base do Spring Boot com scheduling habilitado
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InvestmentPortfolioApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvestmentPortfolioApplication.class, args);
    }
}




