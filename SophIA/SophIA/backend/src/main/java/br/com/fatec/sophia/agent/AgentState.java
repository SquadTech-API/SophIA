package br.com.fatec.sophia.agent;

/**
 * Os estados do ciclo de vida do agente (padrão <b>State Machine</b>).
 *
 * <p>Fluxo esperado:</p>
 * <pre>
 *   INIT → PLANNING → EXECUTING → OBSERVING → (volta para PLANNING)  ... → COMPLETED
 *                                         └→ FAILED (em caso de erro)
 * </pre>
 *
 * <ul>
 *   <li>{@code INIT}      – prepara o contexto da requisição.</li>
 *   <li>{@code PLANNING}  – o planner (Strategy) decide o(s) próximo(s) passo(s).</li>
 *   <li>{@code EXECUTING} – executa o próximo comando (Command) do plano.</li>
 *   <li>{@code OBSERVING} – avalia o resultado e decide se continua ou encerra.</li>
 *   <li>{@code COMPLETED} – estado final de sucesso (resposta pronta).</li>
 *   <li>{@code FAILED}    – estado final de erro.</li>
 * </ul>
 *
 * <p>{@code COMPLETED} e {@code FAILED} são terminais: não possuem handler e
 * encerram o laço da máquina de estados.</p>
 */
public enum AgentState {
    INIT,
    PLANNING,
    EXECUTING,
    OBSERVING,
    COMPLETED,
    FAILED
}
