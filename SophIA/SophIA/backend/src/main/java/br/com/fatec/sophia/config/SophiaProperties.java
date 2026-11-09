package br.com.fatec.sophia.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Parâmetros de configuração da aplicação, lidos do {@code application.properties}
 * sob o prefixo {@code sophia.*}. Externalizar isso evita "números mágicos"
 * espalhados pelo código.
 *
 * <p>Exemplo no application.properties:</p>
 * <pre>
 * sophia.planner=plan-then-execute
 * sophia.max-iterations=12
 * sophia.search-limit=5
 * sophia.import-limit=-1
 * </pre>
 */
@Component
@ConfigurationProperties(prefix = "sophia")
public class SophiaProperties {

    /** Planner (Strategy) padrão quando a requisição não especifica um. */
    private String planner = "plan-then-execute";

    /** Trava de segurança: máximo de ciclos PLANNING antes de encerrar à força. */
    private int maxIterations = 12;

    /** Quantos livros a busca retorna no máximo. */
    private int searchLimit = 5;

    /** Limite de linhas importadas do CSV (-1 = todas). */
    private int importLimit = -1;

    public String getPlanner() {
        return planner;
    }

    public void setPlanner(String planner) {
        this.planner = planner;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public int getSearchLimit() {
        return searchLimit;
    }

    public void setSearchLimit(int searchLimit) {
        this.searchLimit = searchLimit;
    }

    public int getImportLimit() {
        return importLimit;
    }

    public void setImportLimit(int importLimit) {
        this.importLimit = importLimit;
    }
}
