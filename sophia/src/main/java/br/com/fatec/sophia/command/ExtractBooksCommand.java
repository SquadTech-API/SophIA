package br.com.fatec.sophia.command;
import org.springframework.ai.chat.client.ChatClient;

import br.com.fatec.sophia.model.BookFilters;

public class ExtractBooksCommand implements AgentCommand<BookFilters> {

    private final ChatClient chatClient;
    private final String userQuestion;

    public ExtractBooksCommand(ChatClient chatClient, String userQuestion) {
        this.chatClient = chatClient;
        this.userQuestion = userQuestion;
    }

    @Override
    public BookFilters execute() {
        BookFilters filters = chatClient.prompt()
            .system("""
                Extract the search filters from the user's question.
                Answer ONLY with a valid JSON object, without any additional text or formatting.
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
            .user(userQuestion)
            .call()
            .entity(BookFilters.class);

        return filters;
    }
    
}
