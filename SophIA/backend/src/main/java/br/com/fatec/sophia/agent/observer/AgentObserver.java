package br.com.fatec.sophia.agent.observer;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.agent.AgentState;

/**
 * Padrão <b>Observer</b>: permite "escutar" a execução do agente sem acoplar o
 * monitoramento à lógica dele.
 *
 * <p>O agente (Subject) notifica todos os observers a cada transição de estado.
 * Observers podem logar, coletar métricas, etc. Adicionar um novo observer é só
 * criar um {@code @Component} que implemente esta interface — o Spring o injeta
 * automaticamente na lista do agente.</p>
 */
public interface AgentObserver {

    /**
     * Chamado a cada transição da máquina de estados.
     *
     * @param from  estado de origem ({@code null} na primeira transição, para INIT)
     * @param to    estado de destino
     * @param ctx   contexto atual da execução
     */
    void onTransition(AgentState from, AgentState to, AgentContext ctx);
}
