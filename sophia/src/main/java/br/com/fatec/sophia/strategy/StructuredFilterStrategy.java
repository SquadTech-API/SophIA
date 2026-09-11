package br.com.fatec.sophia.strategy;

import br.com.fatec.sophia.model.Book;
import br.com.fatec.sophia.repository.BookRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("structuredFilter")
public class StructuredFilterStrategy implements BookSearchStrategy {

    private final BookRepository bookRepository;

    public StructuredFilterStrategy(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> search(String userPrompt) {
        // Em um cenário real de arquitetura de agentes, um componente extrator 
        // preencheria estes valores com base na intenção estruturada detectada.
        // Aqui simulamos a chamada passando os filtros estruturados e null no embedding.
        
        return bookRepository.hybridSearch(
                null, // title
                null, // genreName
                null, // authorName
                null, // minPages
                null, // maxPages
                null, // yearPublished
                null, // minRating
                null, // queryEmbedding (nulo pois não é busca semântica)
                5     // limit
        );
    }
}