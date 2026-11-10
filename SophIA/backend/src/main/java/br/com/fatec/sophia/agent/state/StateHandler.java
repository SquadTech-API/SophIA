package br.com.fatec.sophia.agent.state;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.agent.AgentState;

/**
 * Padrão <b>State</b>: cada estado da máquina é uma classe própria que sabe
 * processar o contexto e dizer qual é o próximo estado.
 *
 * <p>O orquestrador ({@code Agent}) não contém a lógica de cada estado — ele só
 * pergunta ao handler atual "o que faço agora e para onde vou?". Isso mantém
 * cada estado pequeno e isolado, e deixa o fluxo fácil de seguir.</p>
 */
public interface StateHandler {

    /** Qual estado esta classe trata. */
    AgentState state();

    /** Processa o contexto e retorna o PRÓXIMO estado. */
    AgentState handle(AgentContext ctx);
}
