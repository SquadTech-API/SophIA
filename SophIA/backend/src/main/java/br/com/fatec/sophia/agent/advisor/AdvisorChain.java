package br.com.fatec.sophia.agent.advisor;

import java.util.Iterator;
import java.util.List;

import br.com.fatec.sophia.agent.Agent;
import br.com.fatec.sophia.agent.AgentContext;

/**
 * A corrente de advisors. Percorre os elos em ordem; quando acabam os advisors,
 * o elo final executa o agente propriamente dito ({@link Agent#run}).
 *
 * <p>É criada uma nova corrente por requisição (ela carrega um iterador com
 * posição), por isso não é um bean Spring — é instanciada pelo serviço.</p>
 */
public class AdvisorChain {

    private final Iterator<Advisor> iterator;
    private final Agent agent;

    public AdvisorChain(List<Advisor> advisors, Agent agent) {
        this.iterator = advisors.iterator();
        this.agent = agent;
    }

    /** Avança para o próximo elo; se não houver mais, executa o agente. */
    public void next(AgentContext ctx) {
        if (iterator.hasNext()) {
            iterator.next().handle(ctx, this);
        } else {
            agent.run(ctx);
        }
    }
}
