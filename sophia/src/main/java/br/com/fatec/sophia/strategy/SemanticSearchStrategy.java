package br.com.fatec.sophia.strategy;

import br.com.fatec.sophia.model.Book;
import br.com.fatec.sophia.repository.BookRepository;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("semanticSearch")
public class SemanticSearchStrategy implements BookSearchStrategy {

    private final BookRepository bookRepository;
    private final EmbeddingModel embeddingModel;

    public SemanticSearchStrategy(BookRepository bookRepository, EmbeddingModel embeddingModel) {
        this.bookRepository = bookRepository;
        this.embeddingModel = embeddingModel;
    }

    @Override
    public List<Book> search(String userPrompt) {
        float[] embeddingArray = embeddingModel.embed(userPrompt);
        
        // Converte o array de floats para o formato String esperado pelo pgvector na query nativa
        String queryEmbedding = formatEmbeddingToString(embeddingArray);

        // 2. Chama o hybridSearch passando apenas o embedding (filtros estruturados ficam null)
        return bookRepository.hybridSearch(
                null, // title
                null, // genreName
                null, // authorName
                null, // minPages
                null, // maxPages
                null, // yearPublished
                null, // minRating
                queryEmbedding,
                5     // limit padrão
        );
    }

    private String formatEmbeddingToString(float[] embedding) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            sb.append(embedding[i]);
            if (i < embedding.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
