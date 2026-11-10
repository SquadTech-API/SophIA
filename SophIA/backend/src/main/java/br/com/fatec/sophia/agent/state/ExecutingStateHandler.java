package br.com.fatec.sophia.agent.state;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.agent.AgentState;
import br.com.fatec.sophia.agent.command.Command;
import br.com.fatec.sophia.agent.command.CommandRegistry;

/**
 * Estado <b>EXECUTING</b>: retira o próximo comando da fila do plano e o executa
 * via {@link CommandRegistry} (padrão Command). Qualquer erro vira transição
 * para {@code FAILED}, com a mensagem guardada no contexto.
 */
@Component
public class ExecutingStateHandler implements StateHandler {

    private final CommandRegistry commandRegistry;

    public ExecutingStateHandler(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Override
    public AgentState state() {
        return AgentState.EXECUTING;
    }

    @Override
    public AgentState handle(AgentContext ctx) {
        String step = ctx.pollStep();
        if (step == null) {
            // Nada para executar: volta a observar/decidir.
            return AgentState.OBSERVING;
        }

        try {
            Command command = commandRegistry.get(step);
            command.execute(ctx);
            ctx.setLastCommand(step);
            ctx.addTrace(step);
            return AgentState.OBSERVING;
        } catch (Exception e) {
            ctx.setErrorMessage("Falha ao executar '" + step + "': " + e.getMessage());
            return AgentState.FAILED;
        }
    }
}
