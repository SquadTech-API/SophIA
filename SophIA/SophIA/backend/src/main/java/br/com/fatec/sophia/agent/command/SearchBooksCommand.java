package br.com.fatec.sophia.agent.command;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.config.SophiaProperties;
import br.com.fatec.sophia.model.BookFilters;
import br.com.fatec.sophia.repository.BookRepository;

/**
 * Comando: busca livros no banco combinando filtros estruturados com
 * similaridade vetorial (busca híbrida).
 *
 * <p>Usa {@code filters} e {@code queryEmbedding} do contexto e escreve
 * {@code results}. A mesma consulta {@code hybridSearch} cobre três cenários,
 * dependendo do que o planner colocou no contexto:</p>
 * <ul>
 *   <li><b>Semântica</b>: embedding presente, filtros vazios.</li>
 *   <li><b>Estruturada</b>: embedding nulo, filtros preenchidos.</li>
 *   <li><b>Híbrida</b>: embedding + filtros juntos.</li>
 * </ul>
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
        // Se os filtros ainda não foram extraídos, usa um conjunto vazio
        // (todos os campos null) para fazer uma busca puramente semântica.
        BookFilters filters = ctx.getFilters();
        if (filters == null) {
            filters = new BookFilters(null, null, null, null, null, null, null);
        }

        ctx.setResults(bookRepository.hybridSearch(
                filters.title(),
                filters.genreName(),
                filters.authorName(),
                filters.minPages(),
                filters.maxPages(),
                filters.yearPublished(),
                filters.minRating(),
                ctx.getQueryEmbedding(),   // pode ser null → vira busca só estruturada
                properties.getSearchLimit()
        ));
    }
}
