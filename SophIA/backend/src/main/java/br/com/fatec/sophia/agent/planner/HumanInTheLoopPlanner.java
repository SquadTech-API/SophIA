package br.com.fatec.sophia.agent.planner;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.model.BookFilters;

/**
 * Estratégia <b>Human-in-the-Loop</b>: coloca o usuário no meio do processo.
 *
 * <p>Primeiro extrai os filtros. Se a pergunta for vaga demais (sem filtros e
 * muito curta), em vez de "chutar" uma busca, o agente devolve uma pergunta de
 * esclarecimento e encerra, esperando que o usuário detalhe melhor o pedido.
 * Se a pergunta já tem substância, segue o fluxo normal de recomendação.</p>
 *
 * <p>Numa API de uma requisição por vez, "human-in-the-loop" se traduz em pedir
 * mais informação ao usuário quando a confiança é baixa — justamente o ponto em
 * que um humano deve decidir, não a máquina.</p>
 */
@Component
public class HumanInTheLoopPlanner implements Planner {

    @Override
    public String name() {
        return "human-in-the-loop";
    }

    @Override
    public void plan(AgentContext ctx) {
        // 1) Primeiro passo: extrair filtros da pergunta.
        if (ctx.getFilters() == null) {
            ctx.pushStep("extract_filters");
            return;
        }

        // 2) Com os filtros em mãos, decide uma vez: pergunta vaga → pede ajuda.
        if (!ctx.isPlanned()) {
            if (perguntaVaga(ctx)) {
                ctx.setAwaitingUser(true);
                ctx.setResponse("""
                        Para eu recomendar bons livros, me conte um pouco mais. Por exemplo:
                        • um gênero (fantasia, romance, suspense, ...);
                        • um tema ou assunto que te interessa;
                        • um autor, uma época ou uma nota mínima.
                        O que você está procurando?""");
                return;
            }

            // 3) Pergunta com substância: segue o fluxo completo.
            ctx.pushStep("embed_query");
            ctx.pushStep("search_books");
            ctx.pushStep("generate_answer");
            ctx.setPlanned(true);
        }
    }

    /** Vaga = sem nenhum filtro estrutural E pergunta muito curta. */
    private boolean perguntaVaga(AgentContext ctx) {
        BookFilters f = ctx.getFilters();
        boolean semEstrutura = f.title() == null && f.genreName() == null && f.authorName() == null
                && f.minPages() == null && f.maxPages() == null
                && f.yearPublished() == null && f.minRating() == null;

        int palavras = ctx.getUserQuestion().trim().split("\\s+").length;
        return semEstrutura && palavras < 4;
    }
}
