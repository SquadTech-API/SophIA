package br.com.fatec.sophia.agent.command;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.model.Author;
import br.com.fatec.sophia.model.BookFilters;
import br.com.fatec.sophia.model.Genre;
import br.com.fatec.sophia.repository.AuthorRepository;
import br.com.fatec.sophia.repository.BookRepository;
import br.com.fatec.sophia.repository.GenreRepository;

/**
 * Comando: extrai filtros estruturados da pergunta de forma DETERMINÍSTICA,
 * casando o texto contra os valores que realmente existem no catálogo (títulos,
 * gêneros e autores do banco), mais alguns padrões simples (nota mínima e ano).
 *
 * <p>Por que não usar o modelo de linguagem aqui? Porque assim os filtros só
 * podem conter valores que de fato existem no banco — o que torna a busca por
 * gênero/autor/nome confiável — e porque remove uma chamada lenta ao modelo por
 * requisição (a extração fica instantânea). A IA continua no lugar certo: a
 * busca semântica (embeddings) cobre os temas livres, e o modelo de linguagem
 * escreve a recomendação final (comando {@code generate_answer}).</p>
 *
 * <p>Lê {@code userQuestion} e escreve {@code filters}.</p>
 */
@Component
public class ExtractFiltersCommand implements Command {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    // "nota 4", "acima de 4", "mais de 4.5"... (um único dígito, não seguido de outro)
    private static final Pattern RATING =
            Pattern.compile("(?:nota|avaliacao|acima de|mais de|pelo menos|a partir de)\\s*(\\d(?:[.,]\\d)?)(?!\\d)");
    // "4 estrelas", "4 de nota"
    private static final Pattern RATING_STARS =
            Pattern.compile("(\\d(?:[.,]\\d)?)\\s*(?:estrelas?|de nota)");
    // ano precedido de contexto ("de 1990", "em 2005", "ano 1999") para não confundir com título "1984"
    private static final Pattern YEAR =
            Pattern.compile("\\b(?:de|em|ano)\\s+(1\\d{3}|20\\d{2})\\b");

    public ExtractFiltersCommand(BookRepository bookRepository,
                                 AuthorRepository authorRepository,
                                 GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public String name() {
        return "extract_filters";
    }

    @Override
    public void execute(AgentContext ctx) {
        String q = normalize(ctx.getUserQuestion());

        String title = matchTitle(q);
        String genre = matchGenre(q);
        String author = matchAuthor(q);
        Double minRating = matchRating(q);
        Integer year = matchYear(q);

        ctx.setFilters(new BookFilters(title, genre, author, null, null, year, minRating));
    }

    /** Título: só casa quando o título completo aparece na pergunta (evita falso-positivo). */
    private String matchTitle(String q) {
        String best = null;
        for (String title : bookRepository.findAllTitles()) {
            String norm = normalize(title);
            if (norm.length() >= 4 && q.contains(norm)
                    && (best == null || norm.length() > normalize(best).length())) {
                best = title;
            }
        }
        return best;
    }

    /** Gênero: casa quando o nome do gênero aparece na pergunta (o mais longo vence). */
    private String matchGenre(String q) {
        String best = null;
        for (Genre g : genreRepository.findAll()) {
            String norm = normalize(g.getName());
            if (!norm.isBlank() && q.contains(norm)
                    && (best == null || norm.length() > normalize(best).length())) {
                best = g.getName();
            }
        }
        return best;
    }

    /** Autor: casa quando um token (>= 4 letras) do nome do autor aparece na pergunta. */
    private String matchAuthor(String q) {
        for (Author a : authorRepository.findAll()) {
            for (String token : normalize(a.getName()).split("\\s+")) {
                if (token.length() >= 4 && q.contains(token)) {
                    return a.getName();
                }
            }
        }
        return null;
    }

    private Double matchRating(String q) {
        Matcher m = RATING.matcher(q);
        if (m.find()) {
            return clampRating(parseDouble(m.group(1)));
        }
        m = RATING_STARS.matcher(q);
        if (m.find()) {
            return clampRating(parseDouble(m.group(1)));
        }
        return null;
    }

    private Double clampRating(Double v) {
        return (v != null && v >= 0 && v <= 5) ? v : null;
    }

    private Integer matchYear(String q) {
        Matcher m = YEAR.matcher(q);
        if (m.find()) {
            try {
                return Integer.valueOf(m.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Double parseDouble(String s) {
        try {
            return Double.valueOf(s.replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Minúsculas + remoção de acentos (para casar "ficcao" com "Ficção"). */
    private String normalize(String s) {
        if (s == null) {
            return "";
        }
        String lower = s.toLowerCase(Locale.ROOT);
        return Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim();
    }
}
