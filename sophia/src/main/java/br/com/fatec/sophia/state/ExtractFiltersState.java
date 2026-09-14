package br.com.fatec.sophia.state;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import br.com.fatec.sophia.RAGContext;
import br.com.fatec.sophia.model.BookFilters;

@Component
public class ExtractFiltersState implements RAGState {

    private final ChatClient chatClient;
    private final EmbeddingState embeddingState;

    public ExtractFiltersState(ChatClient chatClient, EmbeddingState embeddingState) {
        this.chatClient = chatClient;
        this.embeddingState = embeddingState;
    }
    
    @Override
    public RAGState execute(RAGContext context) {
        
        System.out.println("Extracting filters from user question...");
        BookFilters filters = chatClient.prompt()
            .system("""
                Extract the search filters from the user's question.
                Awnser ONLY with a valid JSON object, without any additional text or formatting.
                The JSON object should have the following fields:
                {
                    "title": null,
                    "genreName": null,
                    "authorName": null,
                    "minPages": null,
                    "maxPages": null,
                    "yearPublished": null,
                    "minRating": null
                }
                Fill only the fields that the question explicitly mentions. Leave the rest null.
                """)
            .user(context.getUserQuestion())
            .call()
            .entity(BookFilters.class);

        System.out.println("Extracted filters: " + filters);
        context.setFilters(filters);
        return embeddingState;
    }
}
