package br.com.fatec.sophia;

import br.com.fatec.sophia.model.Book;
import br.com.fatec.sophia.repository.BookRepository;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BookIndexer {

    private final BookRepository bookRepository;
    private final EmbeddingModel embeddingModel;

    public BookIndexer(BookRepository bookRepository, EmbeddingModel embeddingModel) {
        this.bookRepository = bookRepository;
        this.embeddingModel = embeddingModel;
    }

    @Transactional
    public int indexAllBooks() {
        List<Book> books = bookRepository.findAll();

        for (Book book : books) {
            String content = book.getTitle() + ": " +
                    (book.getDescription() != null ? book.getDescription() : "");

            float[] vector = embeddingModel.embed(content);
            String vectorLiteral = toVectorLiteral(vector);

            bookRepository.updateEmbedding(book.getId(), vectorLiteral);
        }

        return books.size();
    }

    private String toVectorLiteral(float[] vector) {
        return "[" + IntStream_join(vector) + "]";
    }

    private String IntStream_join(float[] vector) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(vector[i]);
        }
        return sb.toString();
    }
}