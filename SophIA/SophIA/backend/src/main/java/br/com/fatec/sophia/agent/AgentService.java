package br.com.fatec.sophia.agent;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fatec.sophia.agent.advisor.Advisor;
import br.com.fatec.sophia.agent.advisor.AdvisorChain;
import br.com.fatec.sophia.agent.planner.PlannerRegistry;
import br.com.fatec.sophia.config.SophiaProperties;

/**
 * Porta de entrada do agente. É quem a camada web chama.
 *
 * <p>Responsabilidades: escolher a estratégia (planner) desta requisição, montar
 * a corrente de advisors (Chain of Responsibility) e disparar o processamento.
 * A corrente termina executando a máquina de estados ({@link Agent}).</p>
 */
@Service
public class AgentService {

    private final List<Advisor> advisors;
    private final Agent agent;
    private final PlannerRegistry plannerRegistry;
    private final SophiaProperties properties;

    public AgentService(List<Advisor> advisors,
                        Agent agent,
                        PlannerRegistry plannerRegistry,
                        SophiaProperties properties) {
        // Ordena os advisors pela ordem declarada (menor = mais externo).
        this.advisors = advisors.stream()
                .sorted(Comparator.comparingInt(Advisor::order))
                .toList();
        this.agent = agent;
        this.plannerRegistry = plannerRegistry;
        this.properties = properties;
    }

    /**
     * Processa uma pergunta e devolve o contexto já preenchido (com a resposta,
     * o planner usado e o rastro de passos executados).
     *
     * @param question    pergunta do usuário
     * @param plannerName planner desejado (ou {@code null} para usar o padrão)
     */
    public AgentContext chat(String question, String plannerName) {
        String planner = (plannerName == null || plannerName.isBlank())
                ? properties.getPlanner()
                : plannerName;

        // Se pediram um planner inexistente, cai no padrão em vez de quebrar.
        if (!plannerRegistry.exists(planner)) {
            planner = properties.getPlanner();
        }

        AgentContext ctx = new AgentContext(question, planner, properties.getMaxIterations());
        new AdvisorChain(advisors, agent).next(ctx);
        return ctx;
    }
}
