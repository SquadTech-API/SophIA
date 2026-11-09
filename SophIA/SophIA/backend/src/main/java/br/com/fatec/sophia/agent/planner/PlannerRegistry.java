package br.com.fatec.sophia.agent.planner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Registro dos planners (estratégias) disponíveis, indexados pelo nome.
 *
 * <p>Assim como o {@code CommandRegistry}, recebe via Injeção de Dependência a
 * lista de todos os beans {@link Planner}. Permite escolher a estratégia em
 * tempo de execução (por requisição ou por configuração).</p>
 */
@Component
public class PlannerRegistry {

    private final Map<String, Planner> plannersByName = new HashMap<>();

    public PlannerRegistry(List<Planner> planners) {
        for (Planner planner : planners) {
            plannersByName.put(planner.name(), planner);
        }
    }

    /** Retorna o planner pelo nome, ou {@code null} se não existir. */
    public Planner find(String name) {
        return plannersByName.get(name);
    }

    public boolean exists(String name) {
        return plannersByName.containsKey(name);
    }
}
