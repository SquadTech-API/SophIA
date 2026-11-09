package br.com.fatec.sophia.agent.observer;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.agent.AgentState;

/**
 * Observer que coleta métricas simples de uso do agente. Demonstra que vários
 * observers independentes escutam o MESMO evento sem um saber do outro — a
 * essência do padrão Observer.
 *
 * <p>Os contadores são globais (somados entre todas as requisições) e
 * thread-safe via {@link AtomicLong}.</p>
 */
@Component
public class MetricsObserver implements AgentObserver {

    private static final Logger log = LoggerFactory.getLogger(MetricsObserver.class);

    private final AtomicLong totalExecucoes = new AtomicLong();
    private final AtomicLong totalTransicoes = new AtomicLong();
    private final AtomicLong totalFalhas = new AtomicLong();

    @Override
    public void onTransition(AgentState from, AgentState to, AgentContext ctx) {
        totalTransicoes.incrementAndGet();

        if (to == AgentState.FAILED) {
            totalFalhas.incrementAndGet();
        }

        if (to == AgentState.COMPLETED || to == AgentState.FAILED) {
            long execucoes = totalExecucoes.incrementAndGet();
            log.info("[métricas] execuções={} | transições={} | falhas={} | passos desta execução={}",
                    execucoes, totalTransicoes.get(), totalFalhas.get(), ctx.getTrace().size());
        }
    }

    public long getTotalExecucoes() {
        return totalExecucoes.get();
    }

    public long getTotalTransicoes() {
        return totalTransicoes.get();
    }

    public long getTotalFalhas() {
        return totalFalhas.get();
    }
}
