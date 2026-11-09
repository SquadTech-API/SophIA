package br.com.fatec.sophia.agent.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.agent.AgentState;

/**
 * Observer que registra, via logger, cada transição de estado e o comando
 * executado. Substitui os antigos {@code System.out.println} espalhados pelo
 * código por um log estruturado e centralizado.
 */
@Component
public class LoggingObserver implements AgentObserver {

    private static final Logger log = LoggerFactory.getLogger(LoggingObserver.class);

    @Override
    public void onTransition(AgentState from, AgentState to, AgentContext ctx) {
        if (from == null) {
            log.info("[agente] início | planner='{}' | pergunta='{}'",
                    ctx.getPlannerName(), ctx.getUserQuestion());
            return;
        }

        log.info("[agente] {} → {}", from, to);

        // Ao entrar em OBSERVING, acabamos de executar um comando: registra qual.
        if (to == AgentState.OBSERVING && ctx.getLastCommand() != null) {
            log.info("[agente]   ↳ comando executado: {}", ctx.getLastCommand());
        }

        if (to == AgentState.COMPLETED) {
            log.info("[agente] concluído | passos={}", ctx.getTrace());
        }
        if (to == AgentState.FAILED) {
            log.warn("[agente] falhou | motivo: {}", ctx.getErrorMessage());
        }
    }
}
