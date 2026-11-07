package br.com.fatec.sophia.state;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import br.com.fatec.sophia.RAGContext;
import br.com.fatec.sophia.model.BookFilters;
import br.com.fatec.sophia.command.CommandExecutor;
import br.com.fatec.sophia.command.ExtractBooksCommand;

@Component
public class ExtractFiltersState implements RAGState {

    private final ChatClient chatClient;
    private final EmbeddingState embeddingState;
    private final CommandExecutor commandExecutor;

    public ExtractFiltersState(ChatClient chatClient, EmbeddingState embeddingState, CommandExecutor commandExecutor) {
        this.chatClient = chatClient;
        this.embeddingState = embeddingState;
        this.commandExecutor = commandExecutor;
    }
    
    @Override
    public RAGState execute(RAGContext context) {
            
        System.out.println("Extracting filters from user question...");

        BookFilters filters = commandExecutor.run(new ExtractBooksCommand(chatClient, context.getUserQuestion()));
        
        System.out.println("Extracted filters: " + filters);
        context.setFilters(filters);
        return embeddingState;
    }
};
