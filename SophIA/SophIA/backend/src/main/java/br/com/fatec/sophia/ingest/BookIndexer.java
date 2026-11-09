package br.com.fatec.sophia.ingest;

import java.util.List;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.fatec.sophia.common.VectorFormatter;
import br.com.fatec.sophia.model.Book;
import br.com.fatec.sophia.repository.BookRepository;

/**
 * Gera (ou atualiza) os embeddings de todos os livros do banco.
 *
 * <p>Para cada livro, monta um texto com título + descrição, pede o vetor ao
 * modelo de embedding e grava o resultado na coluna {@code embedding} via query
 * nativa. A conversão do vetor para texto fica a cargo do {@link VectorFormatter}
 * (antes essa lógica estava duplicada).</p>
 */
@Component
public class BookIndexer {

    private final BookRepository bookRepository;
    private final EmbeddingModel embeddingModel;
    private final VectorFormatter vectorFormatter;

    public BookIndexer(BookRepository bookRepository,
                       EmbeddingModel embeddingModel,
                       VectorFormatter vectorFormatter) {
        this.bookRepository = bookRepository;
        this.embeddingModel = embeddingModel;
        this.vectorFormatter = vectorFormatter;
    }

    @Transactional
    public int indexAllBooks() {
        List<Book> books = bookRepository.findAll();

        for (Book book : books) {
            String content = book.getTitle() + ": "
                    + (book.getDescription() != null ? book.getDescription() : "");

            float[] vector = embeddingModel.embed(content);
            bookRepository.updateEmbedding(book.getId(), vectorFormatter.toLiteral(vector));
        }

        return books.size();
    }
}
