package br.com.fatec.sophia.command;

import java.util.List;

import br.com.fatec.sophia.model.Book;
import br.com.fatec.sophia.model.BookFilters;
import br.com.fatec.sophia.repository.BookRepository;

public class SearchBooksCommand implements AgentCommand<List<Book>> {

    private final BookRepository bookRepository;
    private final BookFilters filters;
    private final String queryEmbeddingLiteral;

    public SearchBooksCommand(BookRepository bookRepository, BookFilters filters, String queryEmbeddingLiteral) {
        this.bookRepository = bookRepository;
        this.filters = filters;
        this.queryEmbeddingLiteral = queryEmbeddingLiteral;
    }

    @Override
    public List<Book> execute() {
        return bookRepository.hybridSearch(
            filters.title(),
            filters.genreName(),
            filters.authorName(),
            filters.minPages(),
            filters.maxPages(),
            filters.yearPublished(),
            filters.minRating(),
            queryEmbeddingLiteral,
            5 // limite fixo de resultados por busca
        );
    }
}
