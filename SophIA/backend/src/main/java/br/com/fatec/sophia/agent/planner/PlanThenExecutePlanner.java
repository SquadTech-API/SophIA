package br.com.fatec.sophia.agent.planner;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;

/**
 * Estratégia <b>Plan-then-Execute</b>: monta o plano COMPLETO de uma só vez, no
 * primeiro ciclo de planejamento, e depois apenas executa os passos em sequência.
 *
 * <p>É o oposto do ReAct: aqui não há re-planejamento a cada passo. O plano fixo
 * da recomendação é: extrair filtros → gerar embedding → buscar → responder.</p>
 */
@Component
public class PlanThenExecutePlanner implements Planner {

    @Override
    public String name() {
        return "plan-then-execute";
    }

    @Override
    public void plan(AgentContext ctx) {
        // Só monta o plano uma vez. Nos ciclos seguintes não faz nada:
        // a fila já contém os passos restantes a executar.
        if (!ctx.isPlanned()) {
            ctx.pushStep("extract_filters");
            ctx.pushStep("embed_query");
            ctx.pushStep("search_books");
            ctx.pushStep("generate_answer");
            ctx.setPlanned(true);
        }
    }
}
