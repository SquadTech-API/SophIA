package br.com.fatec.sophia.agent.advisor;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;

/**
 * Advisor de SAÍDA (pós-processamento). Demonstra a lógica "em volta": primeiro
 * deixa o restante da corrente rodar (inclusive o agente), e só DEPOIS ajusta o
 * resultado — aqui, apenas apara espaços em branco da resposta final.
 *
 * <p>É o tipo de ponto onde, no futuro, dá para plugar filtros de segurança,
 * formatação ou limites de tamanho sem tocar na lógica do agente.</p>
 */
@Component
public class ResponsePolishAdvisor implements Advisor {

    @Override
    public int order() {
        return 90; // mais interno: fica logo "em volta" do agente
    }

    @Override
    public void handle(AgentContext ctx, AdvisorChain chain) {
        chain.next(ctx); // deixa o agente (e demais elos) rodarem primeiro

        // Pós-processamento: limpa a resposta devolvida.
        String resposta = ctx.getResponse();
        if (resposta != null) {
            ctx.setResponse(resposta.trim());
        }
    }
}
