package br.com.fatec.sophia.state;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.RAGContext;
import br.com.fatec.sophia.model.Book;
import br.com.fatec.sophia.repository.BookRepository;
import br.com.fatec.sophia.model.BookFilters;

@Component 
public class SearchState implements RAGState {
    
    private final BookRepository bookRepository;
    private final ResultState resultState;
    private final ErrorState errorState;

    public SearchState(BookRepository bookRepository, ResultState resultState, ErrorState errorState) {
        this.bookRepository = bookRepository;
        this.resultState = resultState;
        this.errorState = errorState;
    }

    @Override
    public RAGState execute(RAGContext context) {

        System.out.println("Searching for books...");

        BookFilters filters = context.getFilters();
        List<Book> results = bookRepository.hybridSearch(
            filters.title(),
            filters.genreName(),
            filters.authorName(),
            filters.minPages(),
            filters.maxPages(),
            filters.yearPublished(),
            filters.minRating(),
            context.getQueryEmbeddingLiteral(),
            5
        );

        if (results.isEmpty()) {

            context.setResponse(
            //O prompt está em inglês por conta dos dados da fonte usada, privando o modelo de ter o trabalho de tradução
                "No founded results with these criteria. Try to be more specific — "
                 + "provide genre, author, period or a minimum rating to search what you're looking for."
                );
            return errorState;
        }

        System.out.println("Search completed.");
        context.setResults(results);
        return resultState;
    }
    
}
