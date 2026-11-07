package br.com.fatec.sophia.state;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

import br.com.fatec.sophia.RAGContext;
import br.com.fatec.sophia.command.CommandExecutor;
import br.com.fatec.sophia.command.EmbedQueryCommand;

@Component
public class EmbeddingState implements RAGState {

    private final EmbeddingModel embeddingModel;
    private final SearchState searchState;
    private final CommandExecutor commandExecutor;

    public EmbeddingState(EmbeddingModel embeddingModel, SearchState searchState, CommandExecutor commandExecutor) {
        this.embeddingModel = embeddingModel;
        this.searchState = searchState;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public RAGState execute(RAGContext context) {
        System.out.println("Embedding the word-keys...");

        String queryEmbeddingLiteral = commandExecutor.run(
            new EmbedQueryCommand(embeddingModel, context.getUserQuestion())
        );

        context.setQueryEmbeddingLiteral(queryEmbeddingLiteral);
        System.out.println("Embedded");
        return searchState;
    }
}