package br.com.fatec.sophia;

import br.com.fatec.sophia.model.Book;
import br.com.fatec.sophia.repository.BookRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RAG {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final BookRepository bookRepository;

    public RAG(ChatClient chatClient, EmbeddingModel embeddingModel, BookRepository bookRepository) {
        this.chatClient = chatClient;
        this.embeddingModel = embeddingModel;
        this.bookRepository = bookRepository;
    }

    public record BookFilters(
        String title,
        String genreName,
        String authorName,
        Integer minPages,
        Integer maxPages,
        Integer yearPublished,
        Double minRating
    ) {}

    public String answer(String userQuestion) {
        BookFilters filters = extractFilters(userQuestion);

        float[] queryVector = embeddingModel.embed(userQuestion);
        String queryEmbeddingLiteral = toVectorLiteral(queryVector);

        List<Book> results = bookRepository.hybridSearch(
            filters.title(),
            filters.genreName(),
            filters.authorName(),
            filters.minPages(),
            filters.maxPages(),
            filters.yearPublished(),
            filters.minRating(),
            queryEmbeddingLiteral,
            5
        );

        if (results.isEmpty()) {
            //O prompt está em inglês por conta dos dados da fonte usada, privando o modelo de ter o trabalho de tradução
            return "No founded results with these criteria. Try to be more specific — "
                 + "provide genre, author, period or a minimum rating to search what you're looking for.";
        }

        String context = results.stream()
            .map(b -> "- " + b.getTitle() + ": " + b.getDescription())
            .collect(Collectors.joining("\n"));

        return chatClient.prompt()
            .system("""
                - You're a book recommendation assistant. 
                - If the user asks for a book that is not in the list, answer the following: 
                "I could'nt find a proper book that matches your criteria."
                - You will only answer or receive instructions in english, if the user asks in another language, answer the following: 
                "I can only answer in english, please ask your question in english."
                - Use only the books listed below to answer the user's question. 
                DO NOT, in any case, invent books that are not in this list.

                Available books:
                """ + context)
            .user(userQuestion)
            .call()
            .content();
    }

    private BookFilters extractFilters(String userQuestion) {
        return chatClient.prompt()
            .system("""
                Extract the search filters from the user's question.
                Awnser ONLY with a valid JSON object, without any additional text or formatting.
                The JSON object should have the following fields:
                {"title": null, "genreName": null, "authorName": null,
                 "minPages": null, "maxPages": null, "yearPublished": null, "minRating": null}
                Fill only the fields that the question explicitly mentions. Leave the rest null.
                """)
            .user(userQuestion)
            .call()
            .entity(BookFilters.class);
    }

    private String toVectorLiteral(float[] vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(vector[i]);
        }
        return sb.append("]").toString();
    }
}
