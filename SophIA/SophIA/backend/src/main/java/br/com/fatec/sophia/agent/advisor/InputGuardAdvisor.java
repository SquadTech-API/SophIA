package br.com.fatec.sophia.agent.advisor;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;

/**
 * Advisor de ENTRADA (pré-processamento). Valida e normaliza a pergunta antes de
 * ela chegar ao agente.
 *
 * <ul>
 *   <li>Pergunta vazia ou longa demais → <b>interrompe</b> a corrente (não chama
 *       {@code chain.next}) e devolve uma mensagem. O agente nem chega a rodar.</li>
 *   <li>Caso contrário → normaliza (tira espaços duplicados) e <b>passa adiante</b>.</li>
 * </ul>
 */
@Component
public class InputGuardAdvisor implements Advisor {

    private static final int LIMITE_CARACTERES = 500;

    @Override
    public int order() {
        return 10; // mais externo: roda primeiro
    }

    @Override
    public void handle(AgentContext ctx, AdvisorChain chain) {
        String pergunta = ctx.getUserQuestion();

        if (pergunta == null || pergunta.isBlank()) {
            ctx.setResponse("Envie uma pergunta para eu poder recomendar livros.");
            return; // interrompe a corrente
        }

        if (pergunta.length() > LIMITE_CARACTERES) {
            ctx.setResponse("Sua mensagem é muito longa. Resuma o pedido em até "
                    + LIMITE_CARACTERES + " caracteres, por favor.");
            return; // interrompe a corrente
        }

        // Normaliza: remove espaços nas pontas e colapsa espaços internos.
        ctx.setUserQuestion(pergunta.trim().replaceAll("\\s+", " "));

        chain.next(ctx); // passa adiante
    }
}
