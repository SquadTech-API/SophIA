package br.com.fatec.sophia.agent;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.observer.AgentObserver;
import br.com.fatec.sophia.agent.state.StateHandler;

/**
 * Orquestrador da <b>máquina de estados</b> e <b>Subject</b> do padrão Observer.
 *
 * <p>Roda o ciclo INIT → PLANNING → EXECUTING → OBSERVING → ... → COMPLETED/FAILED.
 * A cada transição, consulta o handler do estado atual (que diz o próximo estado)
 * e notifica todos os observers. Não contém a lógica de cada estado — isso vive
 * nos {@link StateHandler}.</p>
 *
 * <p>A lista de handlers e a de observers chegam por Injeção de Dependência: o
 * Spring entrega todos os beans de cada tipo, e montamos o mapa estado→handler.</p>
 */
@Component
public class Agent {

    private final Map<AgentState, StateHandler> handlers = new EnumMap<>(AgentState.class);
    private final List<AgentObserver> observers;

    public Agent(List<StateHandler> stateHandlers, List<AgentObserver> observers) {
        for (StateHandler handler : stateHandlers) {
            handlers.put(handler.state(), handler);
        }
        this.observers = observers;
    }

    /**
     * Executa o ciclo completo do agente sobre o contexto informado. Ao final,
     * {@code ctx.getResponse()} contém a resposta ao usuário.
     */
    public void run(AgentContext ctx) {
        AgentState state = AgentState.INIT;
        notifyObservers(null, AgentState.INIT, ctx);

        while (state != AgentState.COMPLETED && state != AgentState.FAILED) {
            StateHandler handler = handlers.get(state);
            if (handler == null) {
                ctx.setErrorMessage("Nenhum handler registrado para o estado " + state);
                notifyObservers(state, AgentState.FAILED, ctx);
                state = AgentState.FAILED;
                break;
            }

            AgentState next = handler.handle(ctx);
            notifyObservers(state, next, ctx);
            state = next;
        }

        // Rede de segurança: nunca devolver resposta nula.
        if (ctx.getResponse() == null) {
            ctx.setResponse(state == AgentState.FAILED
                    ? "Ocorreu um erro ao processar sua solicitação."
                    : "Não consegui gerar uma resposta para o seu pedido.");
        }
    }

    private void notifyObservers(AgentState from, AgentState to, AgentContext ctx) {
        for (AgentObserver observer : observers) {
            observer.onTransition(from, to, ctx);
        }
    }
}
