package br.com.fatec.sophia.agent.planner;

import br.com.fatec.sophia.agent.AgentContext;

/**
 * Padrão <b>Strategy</b>: define COMO o agente decide quais comandos executar.
 *
 * <p>Cada planner é uma estratégia de raciocínio intercambiável. Trocar o
 * comportamento do agente é só trocar a implementação injetada — o resto da
 * máquina de estados não muda. As três estratégias exigidas são:</p>
 * <ul>
 *   <li>{@link ReActPlanner} – decide um passo por vez (raciocina, age, observa).</li>
 *   <li>{@link PlanThenExecutePlanner} – monta o plano inteiro de uma vez.</li>
 *   <li>{@link HumanInTheLoopPlanner} – pede ajuda ao usuário quando a pergunta é vaga.</li>
 * </ul>
 */
public interface Planner {

    /** Nome da estratégia (ex.: "react", "plan-then-execute", "human-in-the-loop"). */
    String name();

    /**
     * Inspeciona o contexto e decide o próximo passo, empurrando comando(s) para
     * a fila do plano ({@link AgentContext#pushStep}). Pode também sinalizar que
     * precisa de mais input do usuário ({@link AgentContext#setAwaitingUser}).
     */
    void plan(AgentContext ctx);
}
