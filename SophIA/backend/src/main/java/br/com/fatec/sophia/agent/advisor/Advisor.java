package br.com.fatec.sophia.agent.advisor;

import br.com.fatec.sophia.agent.AgentContext;

/**
 * Padrão <b>Chain of Responsibility</b>: cada advisor é um elo de uma corrente
 * que processa a requisição. Um advisor pode:
 * <ul>
 *   <li>tratar a requisição e <b>passar adiante</b> (chamando {@code chain.next});</li>
 *   <li>processar <b>antes e depois</b> do restante da corrente (lógica "em volta");</li>
 *   <li>ou <b>interromper</b> a corrente (não chamar {@code chain.next}).</li>
 * </ul>
 *
 * <p>O último elo da corrente é a execução do próprio agente. Assim os advisors
 * funcionam como "camadas" em volta do núcleo (validar entrada, normalizar,
 * ajustar a saída), desacopladas da lógica do agente.</p>
 */
public interface Advisor {

    /** Ordem na corrente (menor = mais externo, roda primeiro). */
    int order();

    /** Processa o contexto e decide se chama o próximo elo ({@code chain.next}). */
    void handle(AgentContext ctx, AdvisorChain chain);
}
