package br.com.fatec.sophia.agent.state;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.agent.AgentState;

/**
 * Estado <b>INIT</b>: ponto de partida do ciclo. Prepara o contexto e entrega o
 * controle para o planejamento. Mantido simples de propósito — serve como
 * "porta de entrada" clara da máquina de estados.
 */
@Component
public class InitStateHandler implements StateHandler {

    @Override
    public AgentState state() {
        return AgentState.INIT;
    }

    @Override
    public AgentState handle(AgentContext ctx) {
        // Garante que começamos com a fila de passos limpa.
        ctx.clearSteps();
        return AgentState.PLANNING;
    }
}
