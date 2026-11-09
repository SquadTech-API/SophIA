package br.com.fatec.sophia.ingest;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.fatec.sophia.config.SophiaProperties;
import br.com.fatec.sophia.model.Author;
import br.com.fatec.sophia.model.Book;
import br.com.fatec.sophia.model.Genre;
import br.com.fatec.sophia.repository.AuthorRepository;
import br.com.fatec.sophia.repository.BookRepository;
import br.com.fatec.sophia.repository.GenreRepository;

/**
 * Importa o catálogo de livros a partir de {@code src/main/resources/books.csv}.
 *
 * <p>Colunas esperadas no CSV (cabeçalho): {@code book_id, title, authors,
 * original_publication_year, average_rating, pages, genres, description}. Autores
 * e gêneros podem vir como lista separada por vírgula (entre aspas) ou no formato
 * {@code [a, b]}. Livros com título repetido são ignorados.</p>
 */
@Component
public class CSVBookImporter {

    /** Caminho do CSV dentro de resources/. */
    private static final String CSV_PATH = "books.csv";

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final SophiaProperties properties;

    public CSVBookImporter(BookRepository bookRepository,
                           AuthorRepository authorRepository,
                           GenreRepository genreRepository,
                           SophiaProperties properties) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.properties = properties;
    }

    @Transactional
    public int importFromCsv() throws Exception {
        int limit = properties.getImportLimit(); // -1 = importar tudo

        // Caches para não ir ao banco toda hora buscar autor/gênero já existente.
        Map<String, Author> authorCache = new HashMap<>();
        authorRepository.findAll().forEach(a -> authorCache.put(a.getName(), a));

        Map<String, Genre> genreCache = new HashMap<>();
        genreRepository.findAll().forEach(g -> genreCache.put(g.getName(), g));

        Set<String> existingTitles = new HashSet<>();
        bookRepository.findAll().forEach(b -> existingTitles.add(b.getTitle()));

        int imported = 0;

        try (Reader reader = new InputStreamReader(
                new ClassPathResource(CSV_PATH).getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build()
                     .parse(reader)) {

            for (CSVRecord record : parser) {
                if (limit != -1 && imported >= limit) {
                    break;
                }

                String title = safeGet(record, "title");
                if (title == null || existingTitles.contains(title)) {
                    continue;
                }

                Book book = new Book();
                book.setBookId(parseInt(safeGet(record, "book_id"))); // ID original do catálogo
                book.setTitle(title);
                book.setDescription(safeGet(record, "description"));
                book.setPageCount(parseInt(safeGet(record, "pages")));
                book.setYearPublished(parseInt(safeGet(record, "original_publication_year")));
                book.setRating(parseRating(safeGet(record, "average_rating")));

                Set<Author> authors = new HashSet<>();
                for (String name : parseList(safeGet(record, "authors"))) {
                    authors.add(authorCache.computeIfAbsent(name, n -> authorRepository.save(new Author(n))));
                }
                book.setAuthors(authors);

                Set<Genre> genres = new HashSet<>();
                for (String name : parseList(safeGet(record, "genres"))) {
                    genres.add(genreCache.computeIfAbsent(name, n -> genreRepository.save(new Genre(n))));
                }
                book.setGenres(genres);

                bookRepository.save(book);
                existingTitles.add(title);
                imported++;
            }
        }

        return imported;
    }

    /** Lê uma coluna com segurança: retorna null se ausente ou vazia. */
    private String safeGet(CSVRecord record, String column) {
        try {
            String value = record.get(column);
            return (value == null || value.isBlank()) ? null : value.trim();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Integer parseInt(String value) {
        if (value == null) {
            return null;
        }
        try {
            return (int) Double.parseDouble(value); // trata "374.0" como 374
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parseRating(String value) {
        if (value == null) {
            return null;
        }
        try {
            return BigDecimal.valueOf(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Converte "a, b, c" ou "[a, b, c]" em uma lista de nomes limpos. */
    private List<String> parseList(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return List.of();
        }
        String cleaned = rawValue.trim();
        if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        List<String> result = new ArrayList<>();
        for (String part : cleaned.split(",")) {
            String name = part.trim().replaceAll("^['\"]|['\"]$", "");
            if (!name.isBlank()) {
                result.add(name);
            }
        }
        return result;
    }
}
