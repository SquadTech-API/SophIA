package br.com.fatec.sophia.agent.planner;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.model.BookFilters;

/**
 * Estratégia <b>ReAct</b> (Reason + Act): decide UM passo por vez, olhando o que
 * já foi observado até agora. A cada ciclo PLANNING, raciocina sobre o estado
 * atual do contexto e escolhe a próxima única ação.
 *
 * <p>Diferencial desta estratégia: ela decide dinamicamente se precisa gerar o
 * embedding. Se a pergunta é curta e claramente estruturada (ex.: "livros do
 * autor Tolkien"), ela pula o embedding e faz busca estruturada; caso contrário,
 * gera o embedding para busca semântica.</p>
 */
@Component
public class ReActPlanner implements Planner {

    @Override
    public String name() {
        return "react";
    }

    @Override
    public void plan(AgentContext ctx) {
        // 1) Ainda não temos filtros? Primeiro passo: extrair filtros.
        if (ctx.getFilters() == null) {
            ctx.pushStep("extract_filters");
            return;
        }

        // 2) Já temos filtros mas ainda não buscamos?
        if (ctx.getResults() == null) {
            // Raciocínio: preciso de busca semântica? Se sim e ainda não embeddei, embeda.
            if (precisaEmbedding(ctx) && ctx.getQueryEmbedding() == null) {
                ctx.pushStep("embed_query");
                return;
            }
            ctx.pushStep("search_books");
            return;
        }

        // 3) Já temos resultados mas não geramos a resposta? Gera a resposta.
        if (ctx.getResponse() == null) {
            ctx.pushStep("generate_answer");
            return;
        }

        // 4) Nada a fazer: o ciclo termina (nenhum passo empurrado).
    }

    /**
     * Heurística simples: embeda (busca semântica) a menos que a pergunta seja
     * curta E tenha filtros estruturais claros — aí basta a busca estruturada.
     */
    private boolean precisaEmbedding(AgentContext ctx) {
        BookFilters f = ctx.getFilters();
        boolean temEstrutura = f.title() != null || f.genreName() != null || f.authorName() != null
                || f.minPages() != null || f.maxPages() != null
                || f.yearPublished() != null || f.minRating() != null;

        int palavras = ctx.getUserQuestion().trim().split("\\s+").length;
        boolean puramenteEstrutural = temEstrutura && palavras <= 5;

        return !puramenteEstrutural;
    }
}
