package br.com.fatec.sophia.command;

import org.springframework.stereotype.Component;

@Component
public class CommandExecutor {

    public <T> T run(AgentCommand<T> command) {
        return command.execute();
    }
}