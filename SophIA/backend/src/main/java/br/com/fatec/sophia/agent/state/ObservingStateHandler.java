package br.com.fatec.sophia.agent.state;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.agent.AgentState;

/**
 * Estado <b>OBSERVING</b>: avalia o resultado do último comando e decide o rumo.
 *
 * <ul>
 *   <li>Houve erro → {@code FAILED}.</li>
 *   <li>A busca não retornou nada → encerra com uma mensagem amigável.</li>
 *   <li>A resposta já foi gerada → {@code COMPLETED}.</li>
 *   <li>Caso contrário → volta para {@code PLANNING} (próximo passo).</li>
 * </ul>
 *
 * <p>É aqui que o tratamento de "nenhum resultado" fica centralizado — antes
 * estava espalhado entre a busca e a geração da resposta.</p>
 */
@Component
public class ObservingStateHandler implements StateHandler {

    @Override
    public AgentState state() {
        return AgentState.OBSERVING;
    }

    @Override
    public AgentState handle(AgentContext ctx) {
        if (ctx.getErrorMessage() != null) {
            return AgentState.FAILED;
        }

        // Acabou de buscar e não veio nada: encerra sem chamar o modelo à toa.
        boolean buscouAgora = "search_books".equals(ctx.getLastCommand());
        boolean semResultados = ctx.getResults() == null || ctx.getResults().isEmpty();
        if (buscouAgora && semResultados) {
            ctx.setResponse("""
                    Não encontrei livros com esses critérios. Tente ser mais específico \
                    informando um gênero, autor, período ou uma nota mínima.""");
            ctx.clearSteps();
            return AgentState.COMPLETED;
        }

        // Resposta pronta (gerada ou definida pelo planner): terminou.
        if (ctx.getResponse() != null) {
            return AgentState.COMPLETED;
        }

        // Ainda há trabalho: volta a planejar o próximo passo.
        return AgentState.PLANNING;
    }
}
