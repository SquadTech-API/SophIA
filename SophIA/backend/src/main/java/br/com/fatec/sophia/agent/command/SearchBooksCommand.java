package br.com.fatec.sophia.agent.command;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.config.SophiaProperties;
import br.com.fatec.sophia.model.Book;
import br.com.fatec.sophia.model.BookFilters;
import br.com.fatec.sophia.repository.BookRepository;

/**
 * Comando: busca livros no banco combinando filtros estruturados com
 * similaridade vetorial (busca híbrida).
 *
 * <p>Usa {@code filters} e {@code queryEmbedding} do contexto e escreve
 * {@code results}. A mesma consulta {@code hybridSearch} cobre três cenários:
 * semântica (só embedding), estruturada (só filtros) e híbrida (os dois).</p>
 *
 * <p><b>Fallback semântico:</b> se os filtros estruturados não acharem nada mas
 * houver embedding, a busca é refeita só por similaridade (sem filtros). Assim o
 * usuário sempre recebe livros relevantes do catálogo em vez de um "não
 * encontrei" — mesmo que algum filtro tenha ficado específico demais.</p>
 */
@Component
public class SearchBooksCommand implements Command {

    private final BookRepository bookRepository;
    private final SophiaProperties properties;

    public SearchBooksCommand(BookRepository bookRepository, SophiaProperties properties) {
        this.bookRepository = bookRepository;
        this.properties = properties;
    }

    @Override
    public String name() {
        return "search_books";
    }

    @Override
    public void execute(AgentContext ctx) {
        BookFilters filters = ctx.getFilters();
        if (filters == null) {
            filters = new BookFilters(null, null, null, null, null, null, null);
        }

        int limit = properties.getSearchLimit();
        String embedding = ctx.getQueryEmbedding();

        List<Book> results = bookRepository.hybridSearch(
                filters.title(),
                filters.genreName(),
                filters.authorName(),
                filters.minPages(),
                filters.maxPages(),
                filters.yearPublished(),
                filters.minRating(),
                embedding,                 // pode ser null → vira busca só estruturada
                limit
        );

        // Fallback: filtros não acharam nada, mas temos embedding → busca só semântica.
        if (results.isEmpty() && embedding != null && hasStructuredFilters(filters)) {
            results = bookRepository.hybridSearch(
                    null, null, null, null, null, null, null, embedding, limit);
        }

        ctx.setResults(results);
    }

    private boolean hasStructuredFilters(BookFilters f) {
        return f.title() != null || f.genreName() != null || f.authorName() != null
                || f.minPages() != null || f.maxPages() != null
                || f.yearPublished() != null || f.minRating() != null;
    }
}
