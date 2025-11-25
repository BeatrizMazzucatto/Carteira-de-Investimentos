package com.invest.console;

import com.invest.model.Investidor;
import com.invest.model.Carteira;
import com.invest.service.InvestidorService;
import com.invest.service.CarteiraService;
import com.invest.service.TransacaoService;
import com.invest.service.RentabilidadeService;
import com.invest.service.external.GoogleSheetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Scanner;

/**
 * Aplicação de Console para Sistema de Carteiras
 * Interface amigável para o cliente
 */
@Component
public class ConsoleApplication implements CommandLineRunner {

    @Autowired
    private InvestidorService investidorService;

    @Autowired
    private CarteiraService carteiraService;

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private RentabilidadeService rentabilidadeService;

    @Autowired
    private GoogleSheetsService googleSheetsService;

    @Autowired
    private com.invest.repository.AtivoRepository ativoRepository;

    private Scanner scanner = new Scanner(System.in);
    private Investidor investidorLogado = null;

    @Override
    public void run(String... args) throws Exception {
        mostrarBanner();
        executarLogin();
    }

    /**
     * Mostra o banner inicial do sistema
     */
    private void mostrarBanner() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                  SISTEMA DE CARTEIRAS                       ║");
        System.out.println("║            Gestao de Investimentos Pessoais                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * Executa o processo de login
     */
    private void executarLogin() {
        System.out.println("LOGIN");
        System.out.println("════════");
        System.out.println();

        while (investidorLogado == null) {
            System.out.println("Escolha uma opção:");
            System.out.println("1. Fazer Login");
            System.out.println("2. Criar Nova Conta");
            System.out.println("3. Sair");
            System.out.print("Opção: ");

            int opcao = lerInteiro();
            System.out.println();

            switch (opcao) {
                case 1:
                    fazerLogin();
                    break;
                case 2:
                    criarNovaConta();
                    break;
                case 3:
                    System.out.println("Obrigado por usar o Sistema de Carteiras!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opcao invalida! Tente novamente.");
                    System.out.println();
            }
        }
        mostrarMenuPrincipal();
    }

    /**
     * Processo de login
     */
    private void fazerLogin() {
        System.out.println("LOGIN");
        System.out.println("════════");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();

        try {
            List<Investidor> investidores = investidorService.getAllInvestidores();
            investidorLogado = investidores.stream()
                    .filter(inv -> inv.getEmail().equalsIgnoreCase(email) && 
                                 inv.getSenha().equals(senha))
                    .findFirst()
                    .orElse(null);

            if (investidorLogado != null) {
                System.out.println();
                System.out.println("Login realizado com sucesso!");
                System.out.println("Bem-vindo(a), " + investidorLogado.getNome() + "!");
                System.out.println();
            } else {
                System.out.println();
                System.out.println("Email ou senha incorretos!");
                System.out.println("Verifique os dados ou crie uma nova conta.");
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println();
            System.out.println("Erro ao fazer login: " + e.getMessage());
            System.out.println();
        }
    }

    /**
     * Cria uma nova conta de investidor
     */
    private void criarNovaConta() {
        System.out.println("NOVA CONTA");
        System.out.println("═══════════");
        System.out.print("Nome completo: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Senha (minimo 4 caracteres): ");
        String senha = scanner.nextLine().trim();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            System.out.println();
            System.out.println("Nome, email e senha sao obrigatorios!");
            System.out.println();
            return;
        }

        if (senha.length() < 4) {
            System.out.println();
            System.out.println("Senha deve ter no minimo 4 caracteres!");
            System.out.println();
            return;
        }

        try {
            Investidor novoInvestidor = new Investidor(nome, email, senha);
            investidorLogado = investidorService.createInvestidor(novoInvestidor);

            System.out.println();
            System.out.println("Conta criada com sucesso!");
            System.out.println("Bem-vindo(a), " + investidorLogado.getNome() + "!");
            System.out.println();
        } catch (Exception e) {
            System.out.println();
            System.out.println("Erro ao criar conta: " + e.getMessage());
            System.out.println();
        }
    }

    /**
     * Mostra o menu principal do sistema
     */
    private void mostrarMenuPrincipal() {
        while (true) {
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                        MENU PRINCIPAL                       ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("Investidor: " + investidorLogado.getNome());
            System.out.println("Email: " + investidorLogado.getEmail());
            System.out.println();
            System.out.println("Escolha uma opção:");
            System.out.println("1. Minhas Carteiras");
            System.out.println("2. Nova Carteira");
            System.out.println("3. Registrar Transação");
            System.out.println("4. Relatórios de Rentabilidade");
            System.out.println("5. Consultar Ativos");
            System.out.println("6. Configurações");
            System.out.println("7. Sair");
            System.out.println();
            System.out.print("Opção: ");

            int opcao = lerInteiro();
            System.out.println();

            switch (opcao) {
                case 1:
                    mostrarMinhasCarteiras();
                    break;
                case 2:
                    criarNovaCarteira();
                    break;
                case 3:
                    registrarTransacao();
                    break;
                case 4:
                    mostrarRelatorios();
                    break;
                case 5:
                    consultarAtivos();
                    break;
                case 6:
                    mostrarConfiguracoes();
                    break;
                case 7:
                    System.out.println("Obrigado por usar o Sistema de Carteiras!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
                    System.out.println();
            }
        }
    }

    /**
     * Mostra as carteiras do investidor
     */
    private void mostrarMinhasCarteiras() {
        System.out.println("MINHAS CARTEIRAS");
        System.out.println("═══════════════════");
        System.out.println();

        try {
            List<Carteira> carteiras = carteiraService.getCarteirasByInvestidor(investidorLogado.getId());

            if (carteiras.isEmpty()) {
                System.out.println("Você ainda não possui carteiras.");
                System.out.println("Crie sua primeira carteira no menu principal!");
                System.out.println();
                return;
            }

            System.out.println("Suas carteiras:");
            System.out.println();

            for (int i = 0; i < carteiras.size(); i++) {
                Carteira carteira = carteiras.get(i);
                System.out.println((i + 1) + ". " + carteira.getNome());
                System.out.println("   Descrição: " + carteira.getDescricao());
                System.out.println("   Objetivo: " + (carteira.getObjetivo() != null ? carteira.getObjetivo().getDescricao() : "N/A"));
                System.out.println("   Prazo: " + (carteira.getPrazo() != null ? carteira.getPrazo().getDescricao() : "N/A"));
                System.out.println("   Risco: " + (carteira.getPerfilRisco() != null ? carteira.getPerfilRisco().getDescricao() : "N/A"));
                System.out.println("   Valor Atual: R$ " + formatarValor(carteira.getValorAtual()));
                System.out.println("   Criada em: " + formatarData(carteira.getDataCriacao()));
                System.out.println();
            }

            System.out.println("Escolha uma carteira para ver detalhes (0 para voltar):");
            System.out.print("Opção: ");
            int opcao = lerInteiro();

            if (opcao == 0) {
                return; // Volta para o menu principal
            }

            if (opcao > 0 && opcao <= carteiras.size()) {
                Carteira carteiraSelecionada = carteiras.get(opcao - 1);
                mostrarDetalhesCarteira(carteiraSelecionada);
            } else {
                System.out.println("Opção inválida! Por favor, escolha um número entre 0 e " + carteiras.size() + ".");
                System.out.println();
            }

        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("No enum constant")) {
                System.out.println("❌ Erro: Alguma carteira possui um valor inválido no banco de dados.");
                System.out.println("   Por favor, atualize os dados da carteira através da interface de edição.");
                System.out.println("   Detalhes: " + e.getMessage());
            } else {
                System.out.println("Erro ao carregar carteiras: " + e.getMessage());
            }
            System.out.println();
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Erro ao carregar carteiras: " + e.getMessage());
            System.out.println("Tipo do erro: " + e.getClass().getSimpleName());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * Mostra detalhes de uma carteira específica
     */
    private void mostrarDetalhesCarteira(Carteira carteira) {
        while (true) {
            System.out.println("DETALHES DA CARTEIRA: " + carteira.getNome());
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println();
            System.out.println("Descrição: " + (carteira.getDescricao() != null ? carteira.getDescricao() : "N/A"));
            System.out.println("Objetivo: " + (carteira.getObjetivo() != null ? carteira.getObjetivo().getDescricao() : "N/A"));
            System.out.println("Prazo: " + (carteira.getPrazo() != null ? carteira.getPrazo().getDescricao() : "N/A"));
            System.out.println("Perfil de Risco: " + (carteira.getPerfilRisco() != null ? carteira.getPerfilRisco().getDescricao() : "N/A"));
            System.out.println("Valor Inicial: R$ " + formatarValor(carteira.getValorInicial()));
            System.out.println("Valor Atual: R$ " + formatarValor(carteira.getValorAtual()));
            System.out.println("Criada em: " + formatarData(carteira.getDataCriacao()));
            System.out.println();

            System.out.println("Escolha uma opção:");
            System.out.println("1. Registrar Transação");
            System.out.println("2. Ver Rentabilidade");
            System.out.println("3. Ver Transações");
            System.out.println("4. Ver Ativos com Rentabilidade");
            System.out.println("5. Editar Carteira");
            System.out.println("6. Voltar");
            System.out.println();
            System.out.print("Opção: ");

            int opcao = lerInteiro();
            System.out.println();

            switch (opcao) {
                case 1:
                    registrarTransacaoCarteira(carteira);
                    break;
                case 2:
                    mostrarRentabilidadeCarteira(carteira);
                    break;
                case 3:
                    mostrarTransacoesCarteira(carteira);
                    break;
                case 4:
                    mostrarAtivosCarteira(carteira);
                    break;
                case 5:
                    editarCarteira(carteira);
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
                    System.out.println();
            }
        }
    }

    /**
     * Cria uma nova carteira
     */
    private void criarNovaCarteira() {
        System.out.println("NOVA CARTEIRA");
        System.out.println("═══════════════");
        System.out.println();

        System.out.print("Nome da carteira: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Descrição (opcional): ");
        String descricao = scanner.nextLine().trim();

        System.out.println();
        System.out.println("Escolha o perfil de risco:");
        System.out.println("1. Baixo Risco");
        System.out.println("2. Alto Risco");
        System.out.print("Opção: ");

        int riscoOpcao = lerInteiro();
        com.invest.model.PerfilRisco perfilRisco = obterPerfilRisco(riscoOpcao);

        System.out.println();
        System.out.println("Escolha o objetivo:");
        System.out.println("1. Aposentadoria");
        System.out.println("2. Reserva de Emergência");
        System.out.println("3. Valorização Rápida");
        System.out.println("4. Renda Passiva");
        System.out.println("5. Educação");
        System.out.println("6. Casa Própria");
        System.out.println("7. Viagem");
        System.out.println("8. Outros");
        System.out.print("Opção: ");

        int objetivoOpcao = lerInteiro();
        com.invest.model.ObjetivoCarteira objetivo = obterObjetivoCarteira(objetivoOpcao);

        System.out.println();
        System.out.println("Escolha o prazo:");
        System.out.println("1. Curto Prazo");
        System.out.println("2. Medio Prazo");
        System.out.println("3. Longo Prazo");
        System.out.print("Opção: ");

        int prazoOpcao = lerInteiro();
        com.invest.model.PrazoCarteira prazo = obterPrazoCarteira(prazoOpcao);

        System.out.print("Valor inicial (R$, opcional): ");
        BigDecimal valorInicial = lerDecimal();

        try {
            com.invest.dto.CarteiraRequest request = new com.invest.dto.CarteiraRequest();
            request.setNome(nome);
            request.setDescricao(descricao);
            request.setObjetivo(objetivo);
            request.setPerfilRisco(perfilRisco);
            request.setPrazo(prazo);
            request.setValorInicial(valorInicial);

            Carteira novaCarteira = carteiraService.createCarteira(investidorLogado.getId(), request);

            System.out.println();
            System.out.println("Carteira criada com sucesso!");
            System.out.println("ID: " + novaCarteira.getId());
            System.out.println("Nome: " + novaCarteira.getNome());
            System.out.println();

        } catch (Exception e) {
            System.out.println();
            System.out.println("Erro ao criar carteira: " + e.getMessage());
            System.out.println();
        }
    }

    /**
     * Registra uma nova transação
     */
    private void registrarTransacao() {
        System.out.println("REGISTRAR TRANSAÇÃO");
        System.out.println("══════════════════════");
        System.out.println();

        try {
            List<Carteira> carteiras = carteiraService.getCarteirasByInvestidor(investidorLogado.getId());

            if (carteiras.isEmpty()) {
                System.out.println("Você não possui carteiras.");
                System.out.println("Crie uma carteira primeiro!");
                System.out.println();
                return;
            }

            System.out.println("Escolha a carteira:");
            for (int i = 0; i < carteiras.size(); i++) {
                System.out.println((i + 1) + ". " + carteiras.get(i).getNome());
            }
            System.out.print("Opção: ");

            int carteiraOpcao = lerInteiro();
            if (carteiraOpcao < 1 || carteiraOpcao > carteiras.size()) {
                System.out.println("Carteira inválida!");
                return;
            }

            Carteira carteira = carteiras.get(carteiraOpcao - 1);
            registrarTransacaoCarteira(carteira);

        } catch (Exception e) {
            System.out.println("Erro ao registrar transação: " + e.getMessage());
            System.out.println();
        }
    }

    /**
     * Registra transação em uma carteira específica
     */
    private void registrarTransacaoCarteira(Carteira carteira) {
        System.out.println("REGISTRAR TRANSAÇÃO - " + carteira.getNome());
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();

        System.out.println("Tipo de transação:");
        System.out.println("1. Compra");
        System.out.println("2. Venda");
        System.out.println("3. Provento/Dividendo");
        System.out.print("Opção: ");

        int tipoOpcao = lerInteiro();
        com.invest.model.TipoTransacao tipoTransacao = obterTipoTransacao(tipoOpcao);

        // Se for compra, mostra lista de ações disponíveis do JSON
        if (tipoTransacao == com.invest.model.TipoTransacao.COMPRA) {
            mostrarListaAcoesEComprar(carteira);
            return;
        }

        System.out.print("Código do ativo (ex: PETR4): ");
        String codigoAtivo = scanner.nextLine().trim().toUpperCase();

        System.out.print("Nome do ativo (ex: Petrobras): ");
        String nomeAtivo = scanner.nextLine().trim();

        System.out.println();
        System.out.println("Tipo do ativo:");
        System.out.println("1. Ação");
        System.out.println("2. FII");
        System.out.println("3. ETF");
        System.out.println("4. CDB");
        System.out.println("5. LCI/LCA");
        System.out.println("6. Tesouro");
        System.out.println("7. Criptomoeda");
        System.out.print("Opção: ");

        int ativoOpcao = lerInteiro();
        com.invest.model.TipoAtivo tipoAtivo = obterTipoAtivo(ativoOpcao);

        System.out.print("Quantidade: ");
        BigDecimal quantidade = lerDecimal();

        System.out.print("Preço unitário (R$): ");
        BigDecimal precoUnitario = lerDecimal();

        System.out.print("Taxas/corretagem (R$, opcional): ");
        BigDecimal taxas = lerDecimal();

        System.out.print("Observações (opcional): ");
        String observacoes = scanner.nextLine().trim();

        try {
            com.invest.dto.TransacaoRequest request = new com.invest.dto.TransacaoRequest();
            request.setTipoTransacao(tipoTransacao);
            request.setCodigoAtivo(codigoAtivo);
            request.setNomeAtivo(nomeAtivo);
            request.setTipoAtivo(tipoAtivo);
            request.setQuantidade(quantidade);
            request.setPrecoUnitario(precoUnitario);
            request.setTaxasCorretagem(taxas);
            request.setObservacoes(observacoes);

            com.invest.model.Transacao transacao = transacaoService.createTransacao(carteira.getId(), request);

            System.out.println();
            System.out.println("Transação registrada com sucesso!");
            System.out.println("Valor total: R$ " + formatarValor(transacao.getValorTotal()));
            System.out.println("Valor líquido: R$ " + formatarValor(transacao.getValorLiquido()));
            System.out.println();

        } catch (Exception e) {
            System.out.println();
            System.out.println("Erro ao registrar transação: " + e.getMessage());
            System.out.println();
        }
    }

    /**
     * Mostra lista de ações disponíveis e permite comprar
     */
    private void mostrarListaAcoesEComprar(Carteira carteira) {
        System.out.println();
        System.out.println("AÇÕES DISPONÍVEIS PARA COMPRA");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();

        try {
            Map<String, BigDecimal> cotacoes = googleSheetsService.getAllCotacoes();

            if (cotacoes.isEmpty()) {
                System.out.println("Nenhuma cotação disponível no momento.");
                System.out.println();
                return;
            }

            // Ordenar por código
            List<String> codigos = new ArrayList<>(cotacoes.keySet());
            Collections.sort(codigos);

            System.out.println("┌─────┬──────────────┬──────────────────────┐");
            System.out.println("│ Op  │ Código       │ Preço               │");
            System.out.println("├─────┼──────────────┼──────────────────────┤");

            int index = 1;
            Map<Integer, String> mapaOpcoes = new HashMap<>();
            for (String codigo : codigos) {
                BigDecimal preco = cotacoes.get(codigo);
                mapaOpcoes.put(index, codigo);
                System.out.printf("│ %-3d │ %-12s │ R$ %-17s │\n", index, codigo, formatarValor(preco));
                index++;
            }
            System.out.println("└─────┴──────────────┴──────────────────────┘");
            System.out.println();

            System.out.println("Escolha a ação para comprar (0 para voltar):");
            System.out.print("Opção: ");
            int opcao = lerInteiro();

            if (opcao == 0) {
                return;
            }

            if (!mapaOpcoes.containsKey(opcao)) {
                System.out.println("Opção inválida!");
                System.out.println();
                return;
            }

            String codigoAtivo = mapaOpcoes.get(opcao);
            BigDecimal precoAtual = cotacoes.get(codigoAtivo);

            System.out.println();
            System.out.println("Você selecionou: " + codigoAtivo);
            System.out.println("Preço atual: R$ " + formatarValor(precoAtual));
            System.out.println();

            System.out.println("Escolha o tipo de compra:");
            System.out.println("1. Comprar por quantidade de ações");
            System.out.println("2. Comprar por valor total (R$)");
            System.out.print("Opção: ");

            int tipoCompraOpcao = lerInteiro();
            BigDecimal quantidade;
            BigDecimal precoUnitario = precoAtual;

            if (tipoCompraOpcao == 1) {
                // Compra por quantidade
                System.out.print("Quantidade de ações: ");
                quantidade = lerDecimal();
            } else if (tipoCompraOpcao == 2) {
                // Compra por valor total
                System.out.print("Valor total a investir (R$): ");
                BigDecimal valorTotal = lerDecimal();
                quantidade = valorTotal.divide(precoAtual, 4, java.math.RoundingMode.HALF_UP);
                System.out.println("Quantidade calculada: " + formatarQuantidade(quantidade));
            } else {
                System.out.println("Opção inválida!");
                return;
            }

            System.out.print("Taxas/corretagem (R$, opcional): ");
            BigDecimal taxas = lerDecimal();

            System.out.print("Observações (opcional): ");
            String observacoes = scanner.nextLine().trim();

            // Confirmar compra
            BigDecimal valorTotal = quantidade.multiply(precoAtual);
            System.out.println();
            System.out.println("RESUMO DA COMPRA:");
            System.out.println("Ação: " + codigoAtivo);
            System.out.println("Quantidade: " + formatarQuantidade(quantidade));
            System.out.println("Preço unitário: R$ " + formatarValor(precoAtual));
            System.out.println("Valor total: R$ " + formatarValor(valorTotal));
            if (taxas.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("Taxas: R$ " + formatarValor(taxas));
                System.out.println("Valor líquido: R$ " + formatarValor(valorTotal.add(taxas)));
            }

            System.out.println();
            System.out.print("Confirmar compra? (S/N): ");
            String confirmacao = scanner.nextLine().trim().toUpperCase();

            if (!confirmacao.equals("S")) {
                System.out.println("Compra cancelada.");
                System.out.println();
                return;
            }

            // Registrar compra
            com.invest.dto.TransacaoRequest request = new com.invest.dto.TransacaoRequest();
            request.setTipoTransacao(com.invest.model.TipoTransacao.COMPRA);
            request.setCodigoAtivo(codigoAtivo);
            request.setNomeAtivo(codigoAtivo);
            request.setTipoAtivo(com.invest.model.TipoAtivo.ACAO);
            request.setQuantidade(quantidade);
            request.setPrecoUnitario(precoUnitario);
            request.setTaxasCorretagem(taxas);
            request.setObservacoes(observacoes);

            com.invest.model.Transacao transacao = transacaoService.createTransacao(carteira.getId(), request);

            System.out.println();
            System.out.println("Compra registrada com sucesso!");
            System.out.println("Valor total: R$ " + formatarValor(transacao.getValorTotal()));
            System.out.println("Valor líquido: R$ " + formatarValor(transacao.getValorLiquido()));
            System.out.println();

        } catch (Exception e) {
            System.out.println();
            System.out.println("Erro ao processar compra: " + e.getMessage());
            System.out.println();
        }
    }

    /**
     * Mostra relatórios de rentabilidade
     */
    private void mostrarRelatorios() {
        System.out.println("RELATÓRIOS DE RENTABILIDADE");
        System.out.println("══════════════════════════════");
        System.out.println();

        try {
            List<Carteira> carteiras = carteiraService.getCarteirasByInvestidor(investidorLogado.getId());

            if (carteiras.isEmpty()) {
                System.out.println("Você não possui carteiras.");
                System.out.println();
                return;
            }

            System.out.println("Escolha a carteira para relatório:");
            for (int i = 0; i < carteiras.size(); i++) {
                System.out.println((i + 1) + ". " + carteiras.get(i).getNome());
            }
            System.out.print("Opção: ");

            int opcao = lerInteiro();
            if (opcao < 1 || opcao > carteiras.size()) {
                System.out.println("Carteira inválida!");
                return;
            }

            Carteira carteira = carteiras.get(opcao - 1);
            mostrarRentabilidadeCarteira(carteira);

        } catch (Exception e) {
            System.out.println("Erro ao gerar relatório: " + e.getMessage());
            System.out.println();
        }
    }

    /**
     * Mostra rentabilidade de uma carteira
     */
    private void mostrarRentabilidadeCarteira(Carteira carteira) {
        System.out.println("RENTABILIDADE - " + carteira.getNome());
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();

        try {
            com.invest.dto.CarteiraRentabilidadeResponse rentabilidade = 
                rentabilidadeService.calcularRentabilidadeCarteira(carteira.getId());

            System.out.println("RESUMO FINANCEIRO");
            System.out.println("────────────────────");
            System.out.println("Valor Total Investido: R$ " + formatarValor(rentabilidade.getValorTotalInvestido()));
            System.out.println("Valor Atual de Mercado: R$ " + formatarValor(rentabilidade.getValorAtualMercado()));
            System.out.println("Valor com Proventos: R$ " + formatarValor(rentabilidade.getValorAtualComProventos()));
            System.out.println();

            System.out.println("RENTABILIDADE");
            System.out.println("────────────────");
            System.out.println("Rentabilidade Bruta: R$ " + formatarValor(rentabilidade.getRentabilidadeBruta()));
            System.out.println("Rentabilidade Líquida: R$ " + formatarValor(rentabilidade.getRentabilidadeLiquida()));
            System.out.println("Rentabilidade % Bruta: " + formatarPercentual(rentabilidade.getRentabilidadePercentualBruta()) + "%");
            System.out.println("Rentabilidade % Líquida: " + formatarPercentual(rentabilidade.getRentabilidadePercentualLiquida()) + "%");
            System.out.println();

            System.out.println("CUSTOS");
            System.out.println("─────────");
            System.out.println("Total Taxas: R$ " + formatarValor(rentabilidade.getTotalTaxasCorretagem()));
            System.out.println("Total Impostos: R$ " + formatarValor(rentabilidade.getTotalImpostos()));
            System.out.println("Total Custos: R$ " + formatarValor(rentabilidade.getTotalCustos()));
            System.out.println();

            System.out.println("COMPOSIÇÃO");
            System.out.println("─────────────");
            System.out.println("Ações: " + formatarPercentual(rentabilidade.getPercentualAcoes()) + "%");
            System.out.println("FIIs: " + formatarPercentual(rentabilidade.getPercentualFIIs()) + "%");
            System.out.println("ETFs: " + formatarPercentual(rentabilidade.getPercentualETFs()) + "%");
            System.out.println("Renda Fixa: " + formatarPercentual(rentabilidade.getPercentualRendaFixa()) + "%");
            System.out.println();

            System.out.println("Pressione Enter para continuar...");
            scanner.nextLine();

        } catch (Exception e) {
            System.out.println("Erro ao calcular rentabilidade: " + e.getMessage());
            System.out.println();
        }
    }

    /**
     * Consulta ativos
     */
    private void consultarAtivos() {
        System.out.println("CONSULTAR ATIVOS");
        System.out.println("═══════════════════");
        System.out.println();

        try {
            List<Carteira> carteiras = carteiraService.getCarteirasByInvestidor(investidorLogado.getId());

            if (carteiras.isEmpty()) {
                System.out.println("Você não possui carteiras.");
                System.out.println();
                return;
            }

            System.out.println("Escolha a carteira:");
            for (int i = 0; i < carteiras.size(); i++) {
                System.out.println((i + 1) + ". " + carteiras.get(i).getNome());
            }
            System.out.print("Opção: ");

            int opcao = lerInteiro();
            if (opcao < 1 || opcao > carteiras.size()) {
                System.out.println("Carteira inválida!");
                return;
            }

            Carteira carteira = carteiras.get(opcao - 1);
            mostrarAtivosCarteira(carteira);

        } catch (Exception e) {
            System.out.println("Erro ao consultar ativos: " + e.getMessage());
            System.out.println();
        }
    }

    /**
     * Mostra ativos de uma carteira com rentabilidade individual
     */
    private void mostrarAtivosCarteira(Carteira carteira) {
        System.out.println("ATIVOS COM RENTABILIDADE - " + carteira.getNome());
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();

        try {
            // Atualiza preços dos ativos antes de exibir
            System.out.println("Atualizando preços dos ativos...");
            carteiraService.atualizarPrecosCarteira(carteira.getId());
            // Recarrega a carteira para ter os dados atualizados
            carteira = carteiraService.getCarteiraById(carteira.getId());
            System.out.println("Preços atualizados!");
            System.out.println();

            // Busca ativos diretamente do repository para evitar LazyInitializationException
            List<com.invest.model.Ativo> ativos = ativoRepository.findByCarteira(carteira);

            if (ativos.isEmpty()) {
                System.out.println("Esta carteira não possui ativos.");
                System.out.println("Registre uma transação ou compre ações do mercado para adicionar ativos!");
                System.out.println();
                return;
            }

            System.out.println("┌─────────────────────────────────────────────────────────────────────────────────────┐");
            System.out.println("│                           ATIVOS DA CARTEIRA                                         │");
            System.out.println("├─────┬──────────┬──────────────┬────────────┬────────────┬──────────────┬───────────┤");
            System.out.println("│ #   │ Código   │ Nome         │ Qtd        │ Preço Méd. │ Preço Atual  │ Rentab. % │");
            System.out.println("├─────┼──────────┼──────────────┼────────────┼────────────┼──────────────┼───────────┤");

            BigDecimal valorTotalInvestido = BigDecimal.ZERO;
            BigDecimal valorTotalAtual = BigDecimal.ZERO;
            BigDecimal rentabilidadeTotal = BigDecimal.ZERO;

            for (int i = 0; i < ativos.size(); i++) {
                com.invest.model.Ativo ativo = ativos.get(i);
                
                // Calcula rentabilidade do ativo
                com.invest.dto.RentabilidadeResponse rentabilidade = null;
                try {
                    rentabilidade = rentabilidadeService.calcularRentabilidadeAtivo(ativo.getId());
                } catch (Exception e) {
                    System.err.println("Erro ao calcular rentabilidade do ativo " + ativo.getCodigo() + ": " + e.getMessage());
                }

                String codigo = ativo.getCodigo() != null ? ativo.getCodigo() : "N/A";
                String nome = ativo.getNome() != null ? (ativo.getNome().length() > 12 ? ativo.getNome().substring(0, 9) + "..." : ativo.getNome()) : "N/A";
                String quantidade = formatarQuantidade(ativo.getQuantidade());
                String precoMedio = formatarValor(ativo.getPrecoCompra());
                String precoAtual = formatarValor(ativo.getPrecoAtual());
                
                String rentabilidadeStr = "N/A";
                if (rentabilidade != null && rentabilidade.getRentabilidadePercentualBruta() != null) {
                    BigDecimal rent = rentabilidade.getRentabilidadePercentualBruta();
                    rentabilidadeStr = formatarPercentual(rent);
                    if (rent.compareTo(BigDecimal.ZERO) > 0) {
                        rentabilidadeStr = "+" + rentabilidadeStr;
                    }
                }

                System.out.printf("│ %-3d │ %-8s │ %-12s │ %-10s │ %-10s │ %-12s │ %-9s │%n", 
                    (i + 1), codigo, nome, quantidade, precoMedio, precoAtual, rentabilidadeStr);
                
                // Acumula totais
                if (rentabilidade != null) {
                    if (rentabilidade.getValorTotalInvestido() != null) {
                        valorTotalInvestido = valorTotalInvestido.add(rentabilidade.getValorTotalInvestido());
                    }
                    if (rentabilidade.getValorAtualMercado() != null) {
                        valorTotalAtual = valorTotalAtual.add(rentabilidade.getValorAtualMercado());
                    }
                }
            }

            System.out.println("└─────┴──────────┴──────────────┴────────────┴────────────┴──────────────┴───────────┘");
            System.out.println();

            // Mostra resumo detalhado de cada ativo
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("DETALHES DE RENTABILIDADE POR ATIVO:");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println();

            for (int i = 0; i < ativos.size(); i++) {
                com.invest.model.Ativo ativo = ativos.get(i);
                
                com.invest.dto.RentabilidadeResponse rentabilidade = null;
                try {
                    rentabilidade = rentabilidadeService.calcularRentabilidadeAtivo(ativo.getId());
                } catch (Exception e) {
                    continue;
                }

                if (rentabilidade == null) continue;

                System.out.println((i + 1) + ". " + ativo.getCodigo() + " - " + ativo.getNome());
                System.out.println("   Tipo: " + ativo.getTipo().getDescricao());
                System.out.println("   Quantidade: " + formatarQuantidade(ativo.getQuantidade()));
                System.out.println("   Preço Médio de Compra: R$ " + formatarValor(ativo.getPrecoCompra()));
                System.out.println("   Preço Atual: R$ " + formatarValor(ativo.getPrecoAtual()));
                
                if (rentabilidade.getValorTotalInvestido() != null) {
                    System.out.println("   Valor Total Investido: R$ " + formatarValor(rentabilidade.getValorTotalInvestido()));
                }
                if (rentabilidade.getValorAtualMercado() != null) {
                    System.out.println("   Valor Atual de Mercado: R$ " + formatarValor(rentabilidade.getValorAtualMercado()));
                }
                if (rentabilidade.getRentabilidadeBruta() != null) {
                    String sinal = rentabilidade.getRentabilidadeBruta().compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
                    System.out.println("   Rentabilidade Bruta: R$ " + sinal + formatarValor(rentabilidade.getRentabilidadeBruta()));
                }
                if (rentabilidade.getRentabilidadePercentualBruta() != null) {
                    String sinal = rentabilidade.getRentabilidadePercentualBruta().compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
                    System.out.println("   Rentabilidade Percentual: " + sinal + formatarPercentual(rentabilidade.getRentabilidadePercentualBruta()) + "%");
                }
                if (rentabilidade.getVariacaoPercentual() != null) {
                    String sinal = rentabilidade.getVariacaoPercentual().compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
                    System.out.println("   Variação Preço (%): " + sinal + formatarPercentual(rentabilidade.getVariacaoPercentual()) + "%");
                }
                
                System.out.println("   Última Atualização: " + formatarData(ativo.getDataAtualizacao()));
                System.out.println();
            }

            // Mostra resumo geral
            rentabilidadeTotal = valorTotalAtual.subtract(valorTotalInvestido);
            BigDecimal rentabilidadePercentualTotal = BigDecimal.ZERO;
            if (valorTotalInvestido.compareTo(BigDecimal.ZERO) > 0) {
                rentabilidadePercentualTotal = rentabilidadeTotal
                    .divide(valorTotalInvestido, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            }

            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("RESUMO GERAL:");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("Total Investido: R$ " + formatarValor(valorTotalInvestido));
            System.out.println("Valor Atual Total: R$ " + formatarValor(valorTotalAtual));
            String sinalTotal = rentabilidadeTotal.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            System.out.println("Rentabilidade Total: R$ " + sinalTotal + formatarValor(rentabilidadeTotal));
            System.out.println("Rentabilidade % Total: " + sinalTotal + formatarPercentual(rentabilidadePercentualTotal) + "%");
            System.out.println();

            System.out.println("Pressione Enter para continuar...");
            scanner.nextLine();

        } catch (Exception e) {
            System.out.println("Erro ao carregar ativos: " + e.getMessage());
            e.printStackTrace();
            System.out.println();
            System.out.println("Pressione Enter para continuar...");
            scanner.nextLine();
        }
    }

    /**
     * Mostra transações de uma carteira
     */
    private void mostrarTransacoesCarteira(Carteira carteira) {
        System.out.println("TRANSAÇÕES - " + carteira.getNome());
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();

        try {
            List<com.invest.model.Transacao> transacoes = transacaoService.getTransacoesByCarteira(carteira.getId());

            if (transacoes.isEmpty()) {
                System.out.println("Esta carteira não possui transações.");
                System.out.println();
                return;
            }

            System.out.println("Transações da carteira:");
            System.out.println();

            for (int i = 0; i < transacoes.size(); i++) {
                com.invest.model.Transacao transacao = transacoes.get(i);
                System.out.println((i + 1) + ". " + transacao.getTipoTransacao().getDescricao() + " - " + transacao.getCodigoAtivo());
                System.out.println("   Ativo: " + transacao.getNomeAtivo());
                System.out.println("   Quantidade: " + formatarQuantidade(transacao.getQuantidade()));
                System.out.println("   Preço Unitário: R$ " + formatarValor(transacao.getPrecoUnitario()));
                System.out.println("   Valor Total: R$ " + formatarValor(transacao.getValorTotal()));
                System.out.println("   Data: " + formatarData(transacao.getDataTransacao()));
                if (transacao.getObservacoes() != null && !transacao.getObservacoes().isEmpty()) {
                    System.out.println("   Observações: " + transacao.getObservacoes());
                }
                System.out.println();
            }

            System.out.println("Pressione Enter para continuar...");
            scanner.nextLine();

        } catch (Exception e) {
            System.out.println("Erro ao carregar transações: " + e.getMessage());
            System.out.println();
        }
    }

    /**
     * Mostra configurações
     */
    private void mostrarConfiguracoes() {
        System.out.println(" CONFIGURAÇÕES");
        System.out.println("═════════════════");
        System.out.println();
        System.out.println("1. Alterar Email");
        System.out.println("2. Alterar Nome");
        System.out.println("3. Voltar");
        System.out.println();
        System.out.print("Opção: ");

        int opcao = lerInteiro();
        System.out.println();

        switch (opcao) {
            case 1:
                alterarEmail();
                break;
            case 2:
                alterarNome();
                break;
            case 3:
                return;
            default:
                System.out.println("Opção inválida!");
                System.out.println();
        }
    }

    /**
     * Altera email do investidor
     */
    private void alterarEmail() {
        System.out.print("Novo email: ");
        String novoEmail = scanner.nextLine().trim();

        if (novoEmail.isEmpty()) {
            System.out.println("Email não pode ser vazio!");
            return;
        }

        try {
            investidorLogado.setEmail(novoEmail);
            investidorService.updateInvestidor(investidorLogado.getId(), investidorLogado);
            System.out.println("Email alterado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao alterar email: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Altera nome do investidor
     */
    private void alterarNome() {
        System.out.print("Novo nome: ");
        String novoNome = scanner.nextLine().trim();

        if (novoNome.isEmpty()) {
            System.out.println("Nome não pode ser vazio!");
            return;
        }

        try {
            investidorLogado.setNome(novoNome);
            investidorService.updateInvestidor(investidorLogado.getId(), investidorLogado);
            System.out.println("Nome alterado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao alterar nome: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Edita uma carteira
     */
    private void editarCarteira(Carteira carteira) {
        System.out.println(" EDITAR CARTEIRA - " + carteira.getNome());
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("1. Alterar Nome");
        System.out.println("2. 📄 Alterar Descrição");
        System.out.println("3. Alterar Objetivo");
        System.out.println("4.  Alterar Perfil de Risco");
        System.out.println("5. Voltar");
        System.out.println();
        System.out.print("Opção: ");

        int opcao = lerInteiro();
        System.out.println();

        try {
            com.invest.dto.CarteiraRequest request = new com.invest.dto.CarteiraRequest();
            request.setNome(carteira.getNome());
            request.setDescricao(carteira.getDescricao());
            request.setObjetivo(carteira.getObjetivo());
            request.setPerfilRisco(carteira.getPerfilRisco());
            request.setValorInicial(carteira.getValorInicial());

            switch (opcao) {
                case 1:
                    System.out.print("Novo nome: ");
                    request.setNome(scanner.nextLine().trim());
                    break;
                case 2:
                    System.out.print("Nova descrição: ");
                    request.setDescricao(scanner.nextLine().trim());
                    break;
                case 3:
                    System.out.println("Novo objetivo:");
                    System.out.println("1. Aposentadoria 2. Reserva de Emergência 3. Valorização Rápida 4. Renda Passiva 5. Educação 6. Casa Própria 7. Viagem 8. Outros");
                    System.out.print("Opção: ");
                    request.setObjetivo(obterObjetivoCarteira(lerInteiro()));
                    break;
                case 4:
                    System.out.println("Novo perfil de risco:");
                    System.out.println("1. Baixo Risco 2. Alto Risco");
                    System.out.print("Opção: ");
                    request.setPerfilRisco(obterPerfilRisco(lerInteiro()));
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Opção inválida!");
                    return;
            }

            carteiraService.updateCarteira(carteira.getId(), request);
            System.out.println("Carteira atualizada com sucesso!");
            System.out.println();

        } catch (Exception e) {
            System.out.println("Erro ao atualizar carteira: " + e.getMessage());
            System.out.println();
        }
    }

    // Métodos auxiliares

    private int lerInteiro() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Digite um número válido: ");
            }
        }
    }

    private BigDecimal lerDecimal() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    return BigDecimal.ZERO;
                }
                return new BigDecimal(input.replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.print("Digite um valor válido: ");
            }
        }
    }

    private com.invest.model.ObjetivoCarteira obterObjetivoCarteira(int opcao) {
        switch (opcao) {
            case 1: return com.invest.model.ObjetivoCarteira.APOSENTADORIA;
            case 2: return com.invest.model.ObjetivoCarteira.RESERVA_EMERGENCIAL;
            case 3: return com.invest.model.ObjetivoCarteira.VALORIZACAO_RAPIDA;
            case 4: return com.invest.model.ObjetivoCarteira.RENDA_PASSIVA;
            case 5: return com.invest.model.ObjetivoCarteira.EDUCACAO;
            case 6: return com.invest.model.ObjetivoCarteira.CASA_PROPIA;
            case 7: return com.invest.model.ObjetivoCarteira.VIAGEM;
            case 8: return com.invest.model.ObjetivoCarteira.OUTROS;
            default: return com.invest.model.ObjetivoCarteira.APOSENTADORIA;
        }
    }

    private com.invest.model.PerfilRisco obterPerfilRisco(int opcao) {
        switch (opcao) {
            case 1: return com.invest.model.PerfilRisco.BAIXO_RISCO;
            case 2: return com.invest.model.PerfilRisco.ALTO_RISCO;
            default: return com.invest.model.PerfilRisco.BAIXO_RISCO;
        }
    }

    private com.invest.model.PrazoCarteira obterPrazoCarteira(int opcao) {
        switch (opcao) {
            case 1: return com.invest.model.PrazoCarteira.CURTO_PRAZO;
            case 2: return com.invest.model.PrazoCarteira.MEDIO_PRAZO;
            case 3: return com.invest.model.PrazoCarteira.LONGO_PRAZO;
            default: return com.invest.model.PrazoCarteira.MEDIO_PRAZO;
        }
    }

    private com.invest.model.TipoTransacao obterTipoTransacao(int opcao) {
        switch (opcao) {
            case 1: return com.invest.model.TipoTransacao.COMPRA;
            case 2: return com.invest.model.TipoTransacao.VENDA;
            case 3: return com.invest.model.TipoTransacao.PROVENTO;
            default: return com.invest.model.TipoTransacao.COMPRA;
        }
    }

    private com.invest.model.TipoAtivo obterTipoAtivo(int opcao) {
        switch (opcao) {
            case 1: return com.invest.model.TipoAtivo.ACAO;
            case 2: return com.invest.model.TipoAtivo.FII;
            case 3: return com.invest.model.TipoAtivo.ETF;
            case 4: return com.invest.model.TipoAtivo.CDB;
            case 5: return com.invest.model.TipoAtivo.LCI;
            case 6: return com.invest.model.TipoAtivo.TESOURO;
            case 7: return com.invest.model.TipoAtivo.CRIPTOMOEDA;
            default: return com.invest.model.TipoAtivo.ACAO;
        }
    }

    private String formatarValor(BigDecimal valor) {
        if (valor == null) return "0,00";
        return String.format("%.2f", valor).replace(".", ",");
    }

    private String formatarPercentual(BigDecimal percentual) {
        if (percentual == null) return "0,00";
        return String.format("%.2f", percentual).replace(".", ",");
    }

    private String formatarQuantidade(BigDecimal quantidade) {
        if (quantidade == null) return "0";
        return String.format("%.4f", quantidade).replace(".", ",");
    }

    private String formatarData(LocalDateTime data) {
        if (data == null) return "N/A";
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
