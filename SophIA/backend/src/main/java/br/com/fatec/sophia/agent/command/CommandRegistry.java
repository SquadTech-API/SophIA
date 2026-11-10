package br.com.fatec.sophia.agent.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Registro de todos os {@link Command} disponíveis, indexados pelo seu nome
 * lógico ({@link Command#name()}).
 *
 * <p>O Spring injeta automaticamente a lista de TODOS os beans que implementam
 * {@link Command} (Injeção de Dependência). Assim, para adicionar uma nova tool
 * ao agente, basta criar uma classe {@code @Component} que implemente
 * {@link Command} — ela entra no registro sozinha, sem alterar este arquivo.</p>
 */
@Component
public class CommandRegistry {

    private final Map<String, Command> commandsByName = new HashMap<>();

    public CommandRegistry(List<Command> commands) {
        for (Command command : commands) {
            commandsByName.put(command.name(), command);
        }
    }

    /** Busca o comando pelo nome; lança erro claro se o nome não existir. */
    public Command get(String name) {
        Command command = commandsByName.get(name);
        if (command == null) {
            throw new IllegalArgumentException("Comando desconhecido: " + name);
        }
        return command;
    }
}
