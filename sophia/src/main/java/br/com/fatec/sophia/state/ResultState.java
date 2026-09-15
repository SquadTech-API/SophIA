package br.com.fatec.sophia.state;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import br.com.fatec.sophia.RAGContext;
import br.com.fatec.sophia.command.CommandExecutor;
import br.com.fatec.sophia.command.GenerateResponseCommand;

@Component
public class ResultState implements RAGState {

    private final ChatClient chatClient;
    private final CommandExecutor commandExecutor;

    public ResultState(ChatClient chatClient, CommandExecutor commandExecutor) {
        this.chatClient = chatClient;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public RAGState execute(RAGContext context) {
        System.out.println("Generating response based on the results...");

        String response = commandExecutor.run(
            new GenerateResponseCommand(chatClient, context.getResults(), context.getUserQuestion())
        );

        System.out.println("Response generated.");
        context.setResponse(response);
        return null;
    }
}