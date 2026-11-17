package br.com.fatec.sophia.agent.planner;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;

/**
 * Estratégia que demonstra o padrão <b>Composite</b> em ação.
 *
 * <p>Em vez de planejar os passos de recuperação um a um, este planner usa um
 * ÚNICO passo — o comando composto {@code "retrieve"} — que por dentro executa
 * extrair filtros + gerar embedding + buscar. Ou seja, o plano trata um grupo de
 * comandos como se fosse um comando só:</p>
 *
 * <pre>  plano = [ retrieve (composto)  →  generate_answer ]</pre>
 *
 * <p>Compare com o {@link PlanThenExecutePlanner}, que lista os mesmos passos de
 * forma "achatada". O resultado é o mesmo; o que muda é que aqui a recuperação é
 * tratada como uma peça única (Composite).</p>
 */
@Component
public class CompositePlanner implements Planner {

    @Override
    public String name() {
        return "composite";
    }

    @Override
    public void plan(AgentContext ctx) {
        if (!ctx.isPlanned()) {
            ctx.pushStep("retrieve");         // comando COMPOSTO (extrai + embeda + busca)
            ctx.pushStep("generate_answer");
            ctx.setPlanned(true);
        }
    }
}
