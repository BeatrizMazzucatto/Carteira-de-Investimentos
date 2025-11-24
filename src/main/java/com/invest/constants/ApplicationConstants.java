package com.invest.constants;

/**
 * Constantes da aplicação para melhor organização e manutenibilidade
 */
public final class ApplicationConstants {
    
    // Construtor privado para evitar instanciação
    private ApplicationConstants() {
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada");
    }
    
    // Constantes de API
    public static final String API_BASE_PATH = "/api";
    public static final String CORS_ALLOWED_ORIGINS = "*";
    
    // Constantes de Carteira
    public static final String CARTEIRA_ENDPOINT = "/carteiras";
    public static final String TRANSACAO_ENDPOINT = "/transacoes";
    public static final String RENTABILIDADE_ENDPOINT = "/rentabilidade";
    public static final String GOOGLE_SHEETS_ENDPOINT = "/google-sheets";
    
    // Constantes de Validação
    public static final int NOME_MIN_LENGTH = 2;
    public static final int NOME_MAX_LENGTH = 100;
    public static final int DESCRICAO_MAX_LENGTH = 500;
    public static final int OBSERVACOES_MAX_LENGTH = 500;
    public static final int CODIGO_ATIVO_MAX_LENGTH = 20;
    
    // Constantes de Paginação
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int MAX_PAGE_SIZE = 100;
    
    // Constantes de Cálculo Financeiro
    public static final int PRECISION_SCALE = 4;
    public static final int MONETARY_SCALE = 2;
    public static final int QUANTITY_SCALE = 4;
    
    // Constantes de Percentual
    public static final int PERCENTAGE_MULTIPLIER = 100;
    public static final int ANNUAL_DAYS = 365;
    
    // Constantes de Preço Médio
    public static final double PRECO_TETO_MULTIPLIER = 1.1; // +10%
    public static final double PRECO_SUPORTE_MULTIPLIER = 0.9; // -10%
    
    // Constantes de Performance
    public static final int TOP_PERFORMERS_DEFAULT_LIMIT = 5;
    public static final int WORST_PERFORMERS_DEFAULT_LIMIT = 5;
    
    // Constantes de Mensagens de Erro
    public static final String ERRO_CARTEIRA_NAO_ENCONTRADA = "Carteira não encontrada";
    public static final String ERRO_ATIVO_NAO_ENCONTRADO = "Ativo não encontrado";
    public static final String ERRO_TRANSACAO_NAO_ENCONTRADA = "Transação não encontrada";
    public static final String ERRO_INVESTIDOR_NAO_ENCONTRADO = "Investidor não encontrado";
    
    // Constantes de Validação de Campos
    public static final String MSG_NOME_OBRIGATORIO = "Nome é obrigatório";
    public static final String MSG_EMAIL_OBRIGATORIO = "Email é obrigatório";
    public static final String MSG_EMAIL_INVALIDO = "Email deve ter formato válido";
    public static final String MSG_QUANTIDADE_POSITIVA = "Quantidade deve ser positiva";
    public static final String MSG_PRECO_POSITIVO = "Preço deve ser positivo";
    public static final String MSG_TIPO_OBRIGATORIO = "Tipo é obrigatório";
    
    // Constantes de Google Sheets
    public static final String GOOGLE_SHEETS_APPLICATION_NAME = "Investment Portfolio Manager";
    public static final String GOOGLE_SHEETS_TOKENS_PATH = "tokens";
    public static final String GOOGLE_SHEETS_CREDENTIALS_PATH = "/credentials.json";
    public static final int GOOGLE_SHEETS_PORT = 8888;
    
    // Constantes de Log
    public static final String LOG_PREFIX_SUCCESS = "✅";
    public static final String LOG_PREFIX_ERROR = "❌";
    public static final String LOG_PREFIX_INFO = "ℹ️";
    public static final String LOG_PREFIX_WARNING = "⚠️";
}
