package br.com.fatec.sophia.agent.state;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.agent.AgentState;
import br.com.fatec.sophia.agent.planner.Planner;
import br.com.fatec.sophia.agent.planner.PlannerRegistry;

/**
 * Estado <b>PLANNING</b>: pede ao planner (Strategy escolhido para esta
 * requisição) que decida o próximo passo e o coloque na fila do plano.
 *
 * <p>Aqui os padrões se encontram: a máquina de estados delega a DECISÃO para a
 * estratégia de planejamento, mantendo a separação entre "quando decidir"
 * (máquina de estados) e "o que decidir" (planner).</p>
 */
@Component
public class PlanningStateHandler implements StateHandler {

    private final PlannerRegistry plannerRegistry;

    public PlanningStateHandler(PlannerRegistry plannerRegistry) {
        this.plannerRegistry = plannerRegistry;
    }

    @Override
    public AgentState state() {
        return AgentState.PLANNING;
    }

    @Override
    public AgentState handle(AgentContext ctx) {
        ctx.incIterations();

        // Trava de segurança contra laço infinito.
        if (ctx.getIterations() > ctx.getMaxIterations()) {
            if (ctx.getResponse() == null) {
                ctx.setResponse("Não consegui concluir a recomendação a tempo. Tente reformular o pedido.");
            }
            return AgentState.COMPLETED;
        }

        Planner planner = plannerRegistry.find(ctx.getPlannerName());
        planner.plan(ctx);

        // Human-in-the-loop pediu mais informação: encerra devolvendo a pergunta.
        if (ctx.isAwaitingUser()) {
            return AgentState.COMPLETED;
        }

        // Nada mais a executar: encerra.
        if (!ctx.hasSteps()) {
            return AgentState.COMPLETED;
        }

        return AgentState.EXECUTING;
    }
}
