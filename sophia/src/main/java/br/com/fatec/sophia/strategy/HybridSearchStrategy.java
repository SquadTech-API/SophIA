package br.com.fatec.sophia.strategy;

import br.com.fatec.sophia.model.Book;
import br.com.fatec.sophia.repository.BookRepository;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("hybridSearch")
public class HybridSearchStrategy implements BookSearchStrategy {

    private final BookRepository bookRepository;
    private final EmbeddingModel embeddingModel;

    public HybridSearchStrategy(BookRepository bookRepository, EmbeddingModel embeddingModel) {
        this.bookRepository = bookRepository;
        this.embeddingModel = embeddingModel;
    }

    @Override
    public List<Book> search(String userPrompt) {
        // 1. Gera o vetor para a busca semântica baseada na frase toda
        float[] embeddingArray = embeddingModel.embed(userPrompt);
        String queryEmbedding = formatEmbeddingToString(embeddingArray);

        // 2. Chamada correta passando apenas os valores (sem os rótulos de parâmetro)
        return bookRepository.hybridSearch(
                null,                    // title
                null,                    // genreName
                null,                    // authorName
                null,                    // minPages
                null,                    // maxPages
                null,                    // yearPublished
                null,                    // minRating
                queryEmbedding,          // queryEmbedding
                5                        // limit
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